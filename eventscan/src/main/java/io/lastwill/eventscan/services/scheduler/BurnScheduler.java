package io.lastwill.eventscan.services.scheduler;

import io.lastwill.eventscan.model.EthBurnEntry;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.EthBurnEntryRepository;
import io.lastwill.eventscan.repositories.EthRefillEntryRepository;
import io.lastwill.eventscan.service.EthCredentialsGenerator;
import io.lastwill.eventscan.services.EthTransactionSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Component
public class BurnScheduler {
    @Autowired
    private EthBurnEntryRepository burnEntryRepository;

    @Autowired
    private EthCredentialsGenerator generator;

    @Autowired
    private EthTransactionSender transactionSender;

    @Autowired
    private EthRefillEntryRepository refillEntryRepository;

    @Value("${io.lastwill.eventscan.daps-transition.token-address}")
    private String tokenContractAddress;

    @Value("${io.lastwill.eventscan.daps-transition.burner-address}")
    private String burnerAddress;

    @Scheduled(cron = "${burn.timer.cron}")
    public synchronized void burnForRefilled() {
        log.info("start burn function");
        burnEntryRepository
                .findAllByTransferStatus(TransferStatus.REFILLED)
                .forEach(this::burn);
    }

    private void burn(EthBurnEntry burnEntry) {
        BigInteger amount = burnEntry.getAmount();
        Long childId = burnEntry.getTransitionEntry().getConnectEntry().getId();
        String privateKey = generator.generatePrivateKey(childId);
        Credentials credentials = Credentials.create(privateKey);
        try {
            String txHash = transactionSender.sendERC20(
                    tokenContractAddress,
                    credentials,
                    burnerAddress,
                    amount,
                    refillEntryRepository.getFirstByBurnEntry(burnEntry).getAmount().divide(transactionSender.getGasPrice()));
            if (txHash == null || txHash.isEmpty()) throw new IllegalStateException("txHash is empty");
            burnEntry.setTxHash(txHash);
            burnEntry.setTransferStatus(TransferStatus.WAIT_FOR_CONFIRM);
            log.info("Burned {} DAPSToken", amount);
        } catch (IllegalStateException e) {
            log.info("exception when sending token, maybe balance on {} address is too low", burnEntry.getEthAddress(), e);
            burnEntry.setTransferStatus(TransferStatus.ERROR);
        } catch (IOException e) {
            burnEntry.setTransferStatus(TransferStatus.ERROR);
            log.warn("Error when burning {} DAPSToken", amount, e);
        }
        burnEntryRepository.save(burnEntry);
    }
}
