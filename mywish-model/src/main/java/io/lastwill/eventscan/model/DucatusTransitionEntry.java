package io.lastwill.eventscan.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "ducatus_transition")
public class DucatusTransitionEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "tx_hash")
    private String txHash;

    @ManyToOne(optional = false)
    private TokenInfo token;

    private BigInteger amount;

    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    public DucatusTransitionEntry(TokenInfo token, BigInteger amount, String txHash) {
        this.token = token;
        this.amount = amount;
        this.txHash = txHash;
    }

}
