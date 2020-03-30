package io.mywish.blockchain;

import lombok.Getter;

import java.util.List;

@Getter
public class WrapperTransactionReceipt {
    private final String transactionHash;
    private final boolean success;

    public WrapperTransactionReceipt(String txHash, boolean success) {
        this.transactionHash = txHash;
        this.success = success;
    }
}
