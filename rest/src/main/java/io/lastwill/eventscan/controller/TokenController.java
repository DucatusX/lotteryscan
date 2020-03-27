package io.lastwill.eventscan.controller;

import com.neemre.btcdcli4j.core.CommunicationException;
import io.lastwill.eventscan.model.RegisterResponse;
import io.lastwill.eventscan.model.CoinRequest;
import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import io.lastwill.eventscan.service.DucatusAddressValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenController {
    private static final String NEW_USER_STATUS = "new";
    private static final String ALREADY_REGISTER_STATUS = "exist";

    @Autowired
    private TokenEntryRepository tokenEntryRepository;

    @Autowired
    private DucatusAddressValidator ducatusAddressValidator;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> validateRequestAndGetEthAddressOrGenerateNew(@RequestBody CoinRequest request) {
        if (!isRequestValid(request)) {
            return build400Response("Coin ID is missing in request");
        }

        String userId = request.getUserId().trim();

        return build200Response(new RegisterResponse(""));
    }

    private boolean isAddressValid(String ducatusAddress) throws CommunicationException {
        return ducatusAddressValidator.isAddressValid(ducatusAddress);
    }

    private boolean isRequestValid(CoinRequest request) {
        return request != null && request.getUserId() != null;
    }

    private ResponseEntity<RegisterResponse> build200Response(RegisterResponse registerResponse) {
        return ResponseEntity.ok(registerResponse);
    }

    private ResponseEntity<RegisterResponse> build400Response(String error) {
        return ResponseEntity
                .badRequest()
                .body(new RegisterResponse(error));
    }

    private TokenInfo getIfRegister(String userId) {
        return tokenEntryRepository.findFirstByUserId(userId);
    }

}