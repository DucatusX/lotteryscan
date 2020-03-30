package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.ErcTransitionEntry;
import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TransferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;


public interface ErcTransitionEntryRepository extends CrudRepository<ErcTransitionEntry, Long> {
    @Transactional
    @Query("update ErcTransitionEntry e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    @Query("select e from ErcTransitionEntry e where lower(e.txHash) = lower(:txHash)")
    ErcTransitionEntry findByTxHash(@Param("txHash") String hash);
    List<ErcTransitionEntry> findAllByTransferStatus(@Param("transferStatus") TransferStatus status);

    List<ErcTransitionEntry> findByTransferStatusEquals(
            @Param("transferStatus") TransferStatus status);

    ErcTransitionEntry findFirstByTransferStatusEqualsAndTokenEqualsAndAmountLessThan(
            @Param("transferStatus") TransferStatus status,
            @Param("connectEntry") TokenInfo connectEntry,
            @Param("amount") BigInteger amount);

    ErcTransitionEntry findFirstByTransferStatus(@Param("transferStatus") TransferStatus status);

}
