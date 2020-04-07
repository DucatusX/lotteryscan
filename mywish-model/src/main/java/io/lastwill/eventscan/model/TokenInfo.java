package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "coin_info_tokeninfo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "secret_code", unique = true)
    private String secretCode;

    @Column(name = "public_code", unique = true)
    private String publicCode;

    @Setter
    @Column(name = "ducatusx_address")
    private String ducatusxAddress;

    @Setter
    @Column(name = "ducatus_address")
    private String ducatuAddress;

    @Column(name = "token_type")
    private Integer tokenType;

    @Column(name = "certified_assayer")
    private String certifiedAssayer;

    private String country;

    @Column(name = "duc_value")
    private BigDecimal ducValue;

    @Column(name = "gold_price")
    private BigDecimal goldPrice;

    @Column(name = "purchase_date")
    private String purchaseDate;

    private Boolean isActive;

    public TokenInfo(String secretCode,
                     String publicCode,
                     Integer tokenType,
                     Boolean isActive,
                     String certifiedAssayer,
                     String country,
                     BigDecimal ducValue,
                     BigDecimal goldPrice,
                     String purchaseDate) {
        this.secretCode = secretCode;
        this.publicCode = publicCode;
        this.tokenType = tokenType;
        this.isActive = isActive;
        this.certifiedAssayer = certifiedAssayer;
        this.country = country;
        this.ducValue = ducValue;
        this.goldPrice = goldPrice;
        this.purchaseDate = purchaseDate;
    }
}
