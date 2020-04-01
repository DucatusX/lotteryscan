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
    private BigInteger amount;

    public DucatusTransitionEntry(BigInteger amount, String txHash) {
        this.txHash = txHash;
        this.amount = amount;
    }

}
