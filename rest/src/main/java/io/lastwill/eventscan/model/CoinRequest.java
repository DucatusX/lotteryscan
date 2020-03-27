package io.lastwill.eventscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CoinRequest {
    @JsonProperty("user_id")
    private String userId;
}
