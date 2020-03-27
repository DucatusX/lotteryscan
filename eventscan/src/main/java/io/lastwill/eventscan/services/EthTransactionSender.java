package io.lastwill.eventscan.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Component
public class EthTransactionSender {
    private static final BigInteger TRANSFER_GAS_LIMIT = BigInteger.valueOf(21_000L);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(10_000_000_000L);
    private static final String EMPTY_DATA = "";

    @Autowired
    private Web3j web3j;

    public String sendEther(Credentials from, String to, BigInteger amount) throws IOException {
        return sendTransaction(from, to, EMPTY_DATA, amount, TRANSFER_GAS_LIMIT);
    }

    public String sendERC20(String tokenContractAddress, Credentials from, String to, BigInteger amount, BigInteger gasLimit)
            throws IOException {
        String data = createERC20TransferData(to, amount);
        return sendTransaction(from, tokenContractAddress, data, BigInteger.ZERO, gasLimit);
    }

    public BigInteger estimateERC20TransferCallGasLimit(
            String tokenContractAddress, String from, String to, BigInteger amount) throws IOException {
        String data = createERC20TransferData(to, amount);
        return estimateContractCallGasLimit(from, tokenContractAddress, data);
    }

    public BigInteger getGasPrice() {
        return GAS_PRICE;
    }

    private String sendTransaction(Credentials from, String to, String data, BigInteger amount, BigInteger gasLimit)
            throws IOException {
        TransactionManager transactionManager = new RawTransactionManager(web3j, from);
        EthSendTransaction response = transactionManager.sendTransaction(
                GAS_PRICE,
                gasLimit,
                to,
                data,
                amount
        );
        return response.getTransactionHash();
    }

    private String createERC20TransferData(String to, BigInteger value) {
        return FunctionEncoder.encode(createERC20TransferFunction(to, value));
    }

    private Function createERC20TransferFunction(String to, BigInteger value) {
        return new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(value)),
                Collections.emptyList()
        );
    }

    private BigInteger estimateContractCallGasLimit(String from, String contractAddress, String data)
            throws IOException {
        Transaction transaction = Transaction.createFunctionCallTransaction(
                from, null, GAS_PRICE, null, contractAddress, data);
        Request<?, EthEstimateGas> request = web3j.ethEstimateGas(transaction);
        EthEstimateGas response = request.send();
        return response.getAmountUsed();
    }
}
