package io.lastwill.eventscan.test;

import io.lastwill.eventscan.Application;
import io.lastwill.eventscan.services.EthTransactionSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = Application.class)
public class TestClasss {
    @Autowired
    private EthTransactionSender transactionSender;

    @Value("${io.lastwill.eventscan.daps-transition.token-address}")
    private String tokenContractAddress;

    @Test
    public void estimateGasLimitTest() throws IOException {
        String from = "0xa23F26b14447f44373Ef9B2c7902a6F1F27c1057";
        String to = "0x3dea9963bf4c1a3716025de8ae05a5cac66db46e";
        BigInteger amount = new BigInteger("100");
        BigInteger gasLimit = transactionSender.estimateERC20TransferCallGasLimit(tokenContractAddress, from, to, amount);
        System.out.println(gasLimit);
    }
}
