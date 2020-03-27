package io.lastwill.eventscan.services.scheduler;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.services.ConditionChecker;
import io.lastwill.eventscan.utils.CurrencyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Component
public class TransitionScheduler {
    private static final BigInteger TRANSFER_FEE = BigInteger.valueOf(100000000);

    @Autowired
    private DucatusTransitionEntryRepository transitionRepository;

    @Autowired
    private BtcdClient dapsClient;

    @Autowired
    private ConditionChecker conditionChecker;

    @Value("${io.lastwill.eventscan.model.minimal.transition-amount}")
    private BigInteger minTransitionAmount;

    @Value("${io.lastwill.eventscan.model.maximum.send-attempt}")
    private int maxSendAttempt;

    @Scheduled(cron = "${transition.timer.cron}")
    public synchronized void sendToAllWaitingTransitions() throws InterruptedException {
        log.info("start transition function");

        List<DucatusTransitionEntry> waitingForSend = transitionRepository
                .findAllByTransferStatus(TransferStatus.WAIT_FOR_SEND);
        for (DucatusTransitionEntry transitionEntry : waitingForSend) {
            send(transitionEntry);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void send(DucatusTransitionEntry transitionEntry) throws InterruptedException {
        this.send(transitionEntry, 0);
    }

    private void send(DucatusTransitionEntry transitionEntry, int attempt) throws InterruptedException {
        if (!checkTransition(transitionEntry)) return;

        String address = transitionEntry.getConnectEntry().getDapsAddress();
        BigDecimal amount = new BigDecimal(CurrencyUtil.convertEthToDaps(transitionEntry.getAmount()),
                CryptoCurrency.DAPS.getDecimals());
        if (conditionChecker.checkDate()) {
            transitionEntry.setTransferStatus(TransferStatus.OK);
            return;
        }
        try {
            log.info("Trying to send {} DAPS to {}", amount, address);
            String txHash = dapsClient.sendToStealthAddress(address, amount);
            transitionEntry.setTransferStatus(TransferStatus.WAIT_FOR_CONFIRM);
            transitionEntry.setTxHash(txHash);
            log.info("DAPS coins transferred for {}: {}", transitionEntry.getEthTxHash(), txHash);
        } catch (BitcoindException e) {
            attempt++;
            log.warn("Bitcoind library exception when sending", e);
            if (attempt < maxSendAttempt) {
                Thread.sleep(1000);
                log.warn("The " + attempt + " attempt to sennd after failure");
                this.send(transitionEntry, attempt);
                return;
            } else {
                transitionEntry.setTransferStatus(TransferStatus.ERROR);
            }
        } catch (CommunicationException e) {
            log.warn("Communication exception", e);
        }

        transitionRepository.save(transitionEntry);
    }

    private boolean checkTransition(DucatusTransitionEntry transitionEntry) {
        if (transitionEntry.getConnectEntry() == null
                || !conditionChecker.checkAmount(transitionEntry.getAmount(), minTransitionAmount)) {
            return false;
        }

        BigInteger amount = CurrencyUtil.convertEthToDaps(transitionEntry.getAmount());

        if (amount.equals(BigInteger.ZERO)) {
            log.warn("Zero transition amount");
            return false;
        }

        BigInteger balance;
        try {
            balance = getBalance();
        } catch (BitcoindException | CommunicationException e) {
            log.warn("Bitcoind library exception when getting balance", e);
            return false;
        }

        BigInteger need = amount.add(TRANSFER_FEE);
        if (balance.compareTo(need) < 0) {
            log.warn("Insufficient balance: {}, but needed {}", balance, need);
            return false;
        }

        return true;
    }

    private BigInteger getBalance() throws BitcoindException, CommunicationException {
        return dapsClient.getBalances()
                .getSpendable()
                .multiply(BigDecimal.TEN.pow(8))
                .toBigInteger();
    }
}
