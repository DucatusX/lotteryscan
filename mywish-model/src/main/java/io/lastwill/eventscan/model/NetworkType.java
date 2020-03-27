package io.lastwill.eventscan.model;

import lombok.Getter;

@Getter
public enum NetworkType {
    DUCATUS_MAINNET(NetworkProviderType.DUC),
    DUCATUSX_MAINNET(NetworkProviderType.DUCX);

    public final static String DUCATUSX_MAINNET_VALUE = "DUCATUSX_MAINNET";
    public final static String DUCATUS_MAINNET_VALUE = "DUCATUS_MAINNET";


    private final NetworkProviderType networkProviderType;

    NetworkType(NetworkProviderType networkProviderType) {
        this.networkProviderType = networkProviderType;
    }

}
