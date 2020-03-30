package io.lastwill.eventscan.services.monitors.dapsswap.transition;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.model.TransitionEntry;
import io.lastwill.eventscan.repositories.TransitionEntryRepository;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractConfirmationMonitor {
    private final TransitionEntryRepository transitionEntryRepository;
    private final NetworkType networkType;

    public AbstractConfirmationMonitor(
            TransitionEntryRepository transitionEntryRepository,
            NetworkType networkType) {
        this.transitionEntryRepository = transitionEntryRepository;
        this.networkType = networkType;
    }

    @EventListener(NewBlockEvent.class)
    public void doScan(NewBlockEvent event) {
        if (event.getNetworkType() != networkType) {
            return;
        }
        List<String> txHashes = getTxHashes(event);
        if (txHashes.isEmpty()) {
            return;
        }
        transitionEntryRepository
                .findAllByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM)
                .stream()
                .filter(entry -> txHashes.contains(((TransitionEntry) entry).getTxHash()))
                .forEach(entry -> {
                    ((TransitionEntry) entry).setTransferStatus(TransferStatus.OK);
                    transitionEntryRepository.save(entry);
                    log.debug("{}: Transaction {} confirmed", this.getClass().getSimpleName(), ((TransitionEntry) entry).getTxHash());
                });
    }

    protected List<String> getTxHashes(NewBlockEvent newBlockEvent) {
        return newBlockEvent
                .getTransactionsByAddress()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(WrapperTransaction::getHash)
                .distinct()
                .collect(Collectors.toList());
    }
}
