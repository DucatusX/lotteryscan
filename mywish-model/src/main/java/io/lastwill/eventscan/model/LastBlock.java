package io.lastwill.eventscan.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "last_block")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LastBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private NetworkType network;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    public LastBlock(NetworkType networkType, Long blockNumber) {
        this.network = networkType;
        this.blockNumber = blockNumber;
    }
}
