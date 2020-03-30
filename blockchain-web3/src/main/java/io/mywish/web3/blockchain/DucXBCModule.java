package io.mywish.web3.blockchain;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.web3.blockchain.service.Web3Network;
import io.mywish.web3.blockchain.service.Web3Scanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.web3j.protocol.websocket.WebSocketClient;

import java.net.ConnectException;
import java.net.URI;

@Component
@ComponentScan
public class DucXBCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.ducatusx.mainnet")
    @Bean(name = NetworkType.DUCATUSX_MAINNET_VALUE)
    public Web3Network ducXNetMain(
            @Value("${io.lastwill.eventscan.ducatusx.mainnet}") URI web3Url,
            @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.DUCATUSX_MAINNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @Configuration
    public class DucXDbPersisterConfiguration {
        @Bean
        public LastBlockPersister ducXMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.DUCATUSX_MAINNET, lastBlockRepository, null);
        }
    }


    @ConditionalOnBean(name = NetworkType.DUCATUSX_MAINNET_VALUE)
    @Bean
    public Web3Scanner ducXScannerMain(
            final @Qualifier(NetworkType.DUCATUSX_MAINNET_VALUE) Web3Network network,
            final @Qualifier("ducXMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
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
