package io.lastwill.eventscan.service;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DucatusAddressValidator {
    @Autowired
    private BtcdClient ducatusClient;

    public boolean isAddressValid(String ducatusAddress) throws CommunicationException {
        try {
            ducatusClient.validateAddress(ducatusAddress);
            return true;
        } catch (BitcoindException e) {
            return false;
        }
    }
}
