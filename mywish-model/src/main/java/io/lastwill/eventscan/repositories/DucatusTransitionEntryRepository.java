package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;


public interface DucatusTransitionEntryRepository extends CrudRepository<DucatusTransitionEntry, Long> {
    @Transactional
    @Query("update DucatusTransitionEntry e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    @Query("select e from DucatusTransitionEntry e where lower(e.txHash) = lower(:txHash)")
    DucatusTransitionEntry findByTxHash(@Param("txHash") String hash);

    List<DucatusTransitionEntry> findAllByTransferStatus(@Param("transferStatus") TransferStatus status);

    List<DucatusTransitionEntry> findByTransferStatusEquals(
            @Param("transferStatus") TransferStatus status);

    DucatusTransitionEntry findFirstByTransferStatusEqualsAndTokenEqualsAndAmountLessThan(
            @Param("transferStatus") TransferStatus status,
            @Param("connectEntry") TokenInfo connectEntry,
            @Param("amount") BigInteger amount);

    DucatusTransitionEntry findFirstByTransferStatus(@Param("transferStatus") TransferStatus status);

}
