package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.ErcTransitionEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ErcTransitionEntryRepository extends TransitionEntryRepository<ErcTransitionEntry> {
    @Transactional
    @Query("update ErcTransitionEntry e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    @Query("select e from ErcTransitionEntry e where lower(e.ethTxHash) = lower(:ethTxHash)")
    ErcTransitionEntry findByEthTxHash(@Param("ethTxHash") String hash);

}
