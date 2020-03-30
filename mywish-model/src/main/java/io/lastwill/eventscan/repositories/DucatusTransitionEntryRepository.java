package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.DucatusTransitionEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface DucatusTransitionEntryRepository extends TransitionEntryRepository<DucatusTransitionEntry> {
    @Transactional
    @Query("update DucatusTransitionEntry e set e.txHash = :txHash")
    boolean setTxHash(@Param("txHash") String txHash);

    @Query("select e from DucatusTransitionEntry e where lower(e.txHash) = lower(:txHash)")
    DucatusTransitionEntry findByTxHash(@Param("ethTxHash") String hash);

}
