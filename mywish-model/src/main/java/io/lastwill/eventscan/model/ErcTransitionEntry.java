package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "erc_transition")
public class ErcTransitionEntry extends TransitionEntry {
    @ManyToOne(optional = true)
    private TokenInfo token;
    private BigInteger amount;

    public ErcTransitionEntry(TokenInfo token, BigInteger amount, String txHash) {
        super(txHash);
        this.token = token;
        this.amount = amount;
    }
}
