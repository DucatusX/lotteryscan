package io.mywish.web3.blockchain.parity;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;

public interface Web3jEx extends Web3j {
    /**
     * Construct a new Web3j instance.
     *
     * @param web3jService web3j service instance - i.e. HTTP or IPC
     * @return new Web3j instance
     */
    static Web3jEx build(Web3jService web3jService) {
        return new ParityJsonRpc(web3jService);
    }
}
