package io.lastwill.eventscan.services.monitors.dapsswap.transition;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.ErcTransitionEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DucXTransitionConfirmationMonitor extends AbstractConfirmationMonitor {
    public DucXTransitionConfirmationMonitor(
            @Autowired ErcTransitionEntryRepository transitionEntryRepository) {
        super(transitionEntryRepository, NetworkType.DUCATUSX_MAINNET);
    }
}
