package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "coin_info_tokeninfo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Setter
    @Column(name = "ducatusx_address")
    private String ducatusxAddress;

    @Setter
    @Column(name = "ducatus_address")
    private String ducatuAddress;

    @Column(name = "token_type")
    private String tokenType;

    private Boolean isActive;

    public TokenInfo(String userId, TokenType type, Boolean isActive) {
        this.userId = userId;
        this.tokenType = type.getName();
        this.isActive = isActive;
    }
}
