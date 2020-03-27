package io.lastwill.eventscan.services.scheduler;

import io.lastwill.eventscan.model.EthBurnEntry;
import io.lastwill.eventscan.model.EthRefillEntry;
import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.EthBurnEntryRepository;
import io.lastwill.eventscan.repositories.EthRefillEntryRepository;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import io.lastwill.eventscan.service.EthCredentialsGenerator;
import io.lastwill.eventscan.services.ConditionChecker;
import io.lastwill.eventscan.services.EthTransactionSender;
import io.lastwill.eventscan.utils.CurrencyUtil;
import io.lastwill.eventscan.services.saver.DbSaver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Component
public class RefillScheduler {

    @Autowired
    private DucatusTransitionEntryRepository transitionEntryRepository;

    @Autowired
    private EthBurnEntryRepository burnEntryRepository;

    @Autowired
    private EthRefillEntryRepository refillEntryRepository;

    @Autowired
    private EthCredentialsGenerator generator;

    @Autowired
    private EthTransactionSender transactionSender;

    @Autowired
    private ConditionChecker conditionChecker;

    @Autowired
    private DbSaver<EthBurnEntry> burnSaver;

    @Value("${io.lastwill.eventscan.daps-transition.token-address}")
    private String tokenContractAddress;

    @Value("${io.lastwill.eventscan.daps-transition.burner-address}")
    private String burnerAddress;

    @Value("${io.lastwill.eventscan.model.minimal.burn-amount}")
    private BigInteger minBurnAmount;

    private Credentials masterCredentials;

    @PostConstruct
    public void initMasterCredentials() {
        String masterPrivateKey = generator.generateMasterPrivateKey();
        masterCredentials = Credentials.create(masterPrivateKey);
        log.info("master address: {}", generator.getAddressFromPrivate(masterPrivateKey));
    }

    @Scheduled(cron = "${refill.timer.cron}")
    public synchronized void refillEthWalletsForBurn() {
        log.info("start refill function");
        getTransitionsForRefill()
                .stream()
                .map(EthBurnEntry::new)
                .forEach(burnSaver::save);
        burnEntryRepository
                .findAllByTransferStatus(TransferStatus.WAIT_FOR_REFILL)
                .stream()
                .filter(e -> conditionChecker.checkAllConditions(e.getAmount(), minBurnAmount))
                .forEach(this::refillBurnEntry);
    }

    private List<DucatusTransitionEntry> getTransitionsForRefill() {
        List<DucatusTransitionEntry> transactionEntryOk = transitionEntryRepository.findByTransferStatusEquals(TransferStatus.OK);
        transactionEntryOk.removeAll(burnEntryRepository.selectAllTransitionInBurn());
        return transactionEntryOk;
    }

    private void refillBurnEntry(EthBurnEntry burnEntry) {
        if (refillEntryRepository.existsByBurnEntry(burnEntry))
            return;
        TransferStatus status = TransferStatus.WAIT_FOR_CONFIRM;
        String ethAddress = burnEntry.getEthAddress();
        BigInteger ethAmount = null;
        String txHash = null;
        try {
            ethAmount = estimateEthAmountToSend(ethAddress, burnEntry.getAmount());
        } catch (IOException e) {
            log.info("exception when estimate gas limit", e);
            status = TransferStatus.ERROR;
        }
        try {
            txHash = sendEther(ethAddress, ethAmount);
            if (txHash == null || txHash.isEmpty()) throw new IllegalStateException("txHash is empty");
        } catch (IllegalStateException e) {
            log.info("exception when sending eth, maybe balance on master address is too low", e);
            status = TransferStatus.ERROR;
        } catch (IOException e) {
            log.info("exception when sending eth", e);
            status = TransferStatus.ERROR;
        }
        if (status.equals(TransferStatus.WAIT_FOR_CONFIRM)) {
            burnEntry.setTransferStatus(TransferStatus.WAIT_REFILL_CONFIRM);
            burnEntryRepository.save(burnEntry);
        }
        EthRefillEntry refillEntry = new EthRefillEntry(burnEntry, ethAmount, txHash, status);
        refillEntryRepository.save(refillEntry);
        if (txHash != null && !txHash.isEmpty()) {
            log.info("{} refilled for {} ETH: {}", ethAddress, CurrencyUtil.toString(ethAmount), txHash);
        }
    }

    private BigInteger estimateEthAmountToSend(String ethAddress, BigInteger tokensAmount) throws IOException {
        BigInteger gasLimit = transactionSender.estimateERC20TransferCallGasLimit(
                tokenContractAddress,
                ethAddress,
                burnerAddress,
                tokensAmount
        );
        BigInteger gasPrice = transactionSender.getGasPrice();
        return gasLimit.multiply(gasPrice);
    }

    private String sendEther(String ethAddress, BigInteger amount) throws IOException {
        return transactionSender.sendEther(masterCredentials, ethAddress, amount);
    }
}
