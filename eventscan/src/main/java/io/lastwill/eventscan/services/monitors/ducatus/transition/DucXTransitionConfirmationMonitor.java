package io.lastwill.eventscan.services.monitors.ducatus.transition;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.ErcTransitionEntryRepository;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Component
public class DucXTransitionConfirmationMonitor {
    private final ErcTransitionEntryRepository transitionEntryRepository;
    private final NetworkType networkType = NetworkType.DUCATUSX_MAINNET;

    public DucXTransitionConfirmationMonitor(
            @Autowired ErcTransitionEntryRepository transitionEntryRepository) {
        this.transitionEntryRepository = transitionEntryRepository;
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
                .filter(entry -> txHashes.contains(entry.getTxHash()))
                .forEach(entry -> {
                    entry.setTransferStatus(TransferStatus.OK);
                    transitionEntryRepository.save(entry);
                    log.debug("{}: Transaction {} confirmed", this.getClass().getSimpleName(), entry.getTxHash());
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
