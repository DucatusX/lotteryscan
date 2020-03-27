package io.lastwill.eventscan.services.monitors.dapsswap.confirmation;

import io.lastwill.eventscan.model.EthBurnEntry;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.EthBurnEntryRepository;
import org.springframework.stereotype.Component;

@Component
public class BurnConfirmation extends AbstractTransactionConfirmationMonitor<EthBurnEntry> {
    public BurnConfirmation(EthBurnEntryRepository burnRepository) {
        super(NetworkType.ETHEREUM_MAINNET, burnRepository);
    }
}
