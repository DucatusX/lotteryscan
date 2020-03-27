package io.lastwill.eventscan.services.monitors.dapsswap;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.services.TransactionProvider;
import io.lastwill.eventscan.services.saver.DbSaver;
import io.lastwill.eventscan.utils.CurrencyUtil;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Objects;

@Slf4j
@Component
public class TransitionMonitor extends SingleNetworkMonitor {
    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private TokenEntryRepository connectRepository;

    @Autowired
    private DucatusTransitionEntryRepository transitionRepository;

    @Autowired
    private DbSaver<DucatusTransitionEntry> transitionSaver;

    @Value("${io.lastwill.eventscan.daps-transition.token-address}")
    private String ethTokenAddress;

    public TransitionMonitor() {
        super(NetworkType.ETHEREUM_MAINNET);
    }

    @Override
    protected void processBlockEvent(NewBlockEvent newBlockEvent) {
        filterTransactionsByAddress(newBlockEvent, ethTokenAddress)
                .forEach(transaction -> {
                    log.info("Getting receipt for {}", transaction.getHash());
                    transactionProvider.getTransactionReceiptAsync(newBlockEvent.getNetworkType(), transaction)
                            .thenAccept(receipt -> {
                                log.info("Got receipt for {}", receipt.getTransactionHash());
                                receipt.getLogs()
                                        .stream()
                                        .filter(event -> event instanceof TransferEvent)
                                        .map(event -> (TransferEvent) event)
                                        .map(e -> new TransferEvent(
                                                e.getDefinition(),
                                                e.getFrom(),
                                                e.getTo().toLowerCase(),
                                                e.getTokens(),
                                                e.getAddress()
                                        ))
                                        .filter(event -> connectRepository.getAllEthAddresses().contains(event.getTo()))
                                        .map(event -> getTransitionEntry(event, transaction))
                                        .filter(Objects::nonNull)
                                        .map(transitionSaver::save)
                                        .forEach(transitionEntry -> log.info(
                                                "{} transferred {} DAPSToken",
                                                transitionEntry.getConnectEntry().getEthAddress(),
                                                CurrencyUtil.toString(CurrencyUtil.convertEthToDaps(transitionEntry.getAmount()))
                                        ));
                            });
                });
    }

    private DucatusTransitionEntry getTransitionEntry(TransferEvent transferEvent, WrapperTransaction transaction) {
        String ethAddress = transferEvent.getTo();
        TokenInfo connectEntry = connectRepository.findFirstByEthAddressOrderByIdDesc(ethAddress);
        if (connectEntry == null) {
            log.warn("\"{}\" not connected", ethAddress);
            return null;
        }

        String txHash = transaction.getHash();
        DucatusTransitionEntry transitionEntry = transitionRepository.findByEthTxHash(txHash);
        if (transitionEntry != null) {
            log.warn("Transition entry already in DB: {}", txHash);
            return null;
        }

        BigInteger amount = transferEvent.getTokens();
        return new DucatusTransitionEntry(
                connectEntry,
                amount,
                txHash
        );
    }
}
