package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

@Getter
public class DucatusTransitionEvent extends BaseEvent {
    private final DucatusTransitionEntry transitionEntry;

    public DucatusTransitionEvent(DucatusTransitionEntry transitionEntry) {
        super(NetworkType.DUCATUS_MAINNET);
        this.transitionEntry = transitionEntry;
    }
}
