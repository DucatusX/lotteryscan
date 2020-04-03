package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;


@Getter
@Setter
@Entity
@Table(name = "transfer_erctransfer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
