package io.lastwill.eventscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String status;
    @JsonProperty("duc_address")
    private String ducAddress;
    @JsonProperty("ducx_address")
    private String ducxAddress;
    private String error;

    public RegisterResponse(String status, String ducAddress, String ducxAddress) {
        this.status = status;
        this.ducAddress = ducAddress;
        this.ducxAddress = ducxAddress;
    }

    public RegisterResponse(String error) {
        this.error = error;
    }
}
