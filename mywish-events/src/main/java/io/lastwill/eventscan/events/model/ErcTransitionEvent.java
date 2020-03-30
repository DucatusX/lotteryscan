package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.ErcTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;

public class ErcTransitionEvent extends BaseEvent {
    private final ErcTransitionEntry transitionEntry;

    public ErcTransitionEvent(ErcTransitionEntry transitionEntry) {
        super(NetworkType.DUCATUS_MAINNET);
        this.transitionEntry = transitionEntry;
    }
}
