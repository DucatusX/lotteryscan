package io.lastwill.eventscan.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "transfer_ducatustransfer")
public class DucatusTransitionEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "tx_hash")
    protected String txHash;

    @Setter
    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;
    @ManyToOne(optional = true)
    private TokenInfo token;
    private BigInteger amount;

    public DucatusTransitionEntry(TokenInfo token, BigInteger amount, String txHash) {
        this.txHash = txHash;
        this.token = token;
        this.amount = amount;
    }

}
