package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "transfer_erctransfer")
public class ErcTransitionEntry {
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

    public ErcTransitionEntry(BigInteger amount, String txHash) {
        this.txHash = txHash;
        this.amount = amount;
    }
}
