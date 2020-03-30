package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "token_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Setter
    @Column(name = "ducatux_address")
    private String ducatusxAddress;

    @Setter
    @Column(name = "ducatus_address")
    private String ducatuAddress;

    @Setter
    @Column(name = "register")
    private boolean register;

    public TokenInfo(String userId) {
        this.userId = userId;
    }
}