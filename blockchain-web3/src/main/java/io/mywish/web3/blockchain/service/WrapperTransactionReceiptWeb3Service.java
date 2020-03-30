package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

@Slf4j
@Component
public class WrapperTransactionReceiptWeb3Service {

    private boolean isSuccess(TransactionReceipt receipt) {
        BigInteger status;
        if (receipt.getStatus().startsWith("0x")) {
            status = Numeric.decodeQuantity(receipt.getStatus());
        }
        else {
            status = new BigInteger(receipt.getStatus());
        }
        return status.compareTo(BigInteger.ZERO) != 0;
    }

    public WrapperTransactionReceipt build(TransactionReceipt receipt) {
        String hash = receipt.getTransactionHash();


        return new WrapperTransactionReceipt(
                hash,
                isSuccess(receipt)
        );
    }
}
