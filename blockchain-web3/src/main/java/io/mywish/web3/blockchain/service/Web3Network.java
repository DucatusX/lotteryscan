package io.mywish.web3.blockchain.service;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import rx.Subscription;

import javax.annotation.PreDestroy;
import java.math.BigInteger;

@Slf4j
public class Web3Network extends WrapperNetwork {
    @Getter
    private final Web3j web3j;

    @Autowired
    private WrapperBlockWeb3Service blockBuilder;

    @Autowired
    private WrapperTransactionReceiptWeb3Service transactionReceiptBuilder;

    private Subscription subscription;

    public Web3Network(NetworkType type, Web3j web3j) {
        super(type);
        this.web3j = web3j;
    }

    @PreDestroy
    private void close() {
        if (subscription == null) {
            return;
        }
        if (subscription.isUnsubscribed()) {
            return;
        }
        subscription.unsubscribe();
        subscription = null;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByHash(hash, false).send().getBlock());
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(number), true).send().getBlock());
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return web3j
                .ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .send()
                .getBalance();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(
                web3j
                        .ethGetTransactionReceipt(transaction.getHash())
                        .send()
                        .getResult()
        );
    }
}
