package io.mywish.web3.blockchain;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.web3.blockchain.parity.Web3jEx;
import io.mywish.web3.blockchain.service.Web3Network;
import io.mywish.web3.blockchain.service.Web3Scanner;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@ComponentScan
public class Web3BCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    @Bean(name = NetworkType.DUCATUSX_MAINNET_VALUE)
    public Web3Network ethNetMain(Web3j web3j) {
        return new Web3Network(NetworkType.DUCATUSX_MAINNET, web3j);
    }

    @Bean
    public Web3j web3j(
            OkHttpClient client,
            @Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url) {
        return Web3jEx.build(new HttpService(web3Url, client, false));
    }

    @Bean
    public LastBlockPersister ethMainnetLastBlockPersister(
            LastBlockRepository lastBlockRepository
    ) {
        return new LastBlockDbPersister(NetworkType.DUCATUSX_MAINNET, lastBlockRepository, null);
    }

    @ConditionalOnBean(name = NetworkType.DUCATUSX_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScannerMain(
            final @Qualifier(NetworkType.DUCATUSX_MAINNET_VALUE) Web3Network network,
            final @Qualifier("ethMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }
}
