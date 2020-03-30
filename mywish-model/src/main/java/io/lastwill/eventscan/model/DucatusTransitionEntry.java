package io.lastwill.eventscan.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "ducatus_transition")
public class DucatusTransitionEntry extends TransitionEntry {
    @ManyToOne(optional = true)
    private TokenInfo token;
    private BigInteger amount;

    public DucatusTransitionEntry(TokenInfo token, BigInteger amount, String txHash) {
        super(txHash);
        this.token = token;
        this.amount = amount;
    }

}
