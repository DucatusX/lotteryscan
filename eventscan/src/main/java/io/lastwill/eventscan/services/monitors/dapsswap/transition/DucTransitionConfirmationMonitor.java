package io.lastwill.eventscan.services.monitors.dapsswap.transition;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DucTransitionConfirmationMonitor extends AbstractConfirmationMonitor {
    public DucTransitionConfirmationMonitor(
            @Autowired DucatusTransitionEntryRepository transitionEntryRepository) {
        super(transitionEntryRepository, NetworkType.DUCATUS_MAINNET);
    }
}
