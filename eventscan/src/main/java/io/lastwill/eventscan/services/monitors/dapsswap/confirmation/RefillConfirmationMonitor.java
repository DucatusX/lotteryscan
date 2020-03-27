package io.lastwill.eventscan.services.monitors.dapsswap.confirmation;

import io.lastwill.eventscan.model.EthBurnEntry;
import io.lastwill.eventscan.model.EthRefillEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.repositories.EthBurnEntryRepository;
import io.lastwill.eventscan.repositories.EthRefillEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefillConfirmationMonitor extends AbstractTransactionConfirmationMonitor<EthRefillEntry> {
    @Autowired
    private EthBurnEntryRepository burnEntryRepository;

    public RefillConfirmationMonitor(EthRefillEntryRepository refillEntryRepository) {
        super(NetworkType.ETHEREUM_MAINNET, refillEntryRepository);
    }

    @Override
    protected void processEntryAfterConfirm(EthRefillEntry entry) {
        EthBurnEntry burnEntry = entry.getBurnEntry();
        burnEntry.setTransferStatus(TransferStatus.REFILLED);
        burnEntryRepository.save(burnEntry);
    }
}

