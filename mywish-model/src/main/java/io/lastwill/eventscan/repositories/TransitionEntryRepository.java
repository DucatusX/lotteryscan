package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TransferStatus;
import io.lastwill.eventscan.model.TransitionEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.math.BigInteger;
import java.util.List;

public interface TransitionEntryRepository<T extends TransitionEntry> extends CrudRepository<T, Long> {


    List<T> findAllByTransferStatus(@Param("transferStatus") TransferStatus status);

    List<T> findByTransferStatusEquals(
            @Param("transferStatus") TransferStatus status);

    T findFirstByTransferStatusEqualsAndTokenEqualsAndAmountLessThan(
            @Param("transferStatus") TransferStatus status,
            @Param("connectEntry") TokenInfo connectEntry,
            @Param("amount") BigInteger amount);

    T findFirstByTransferStatus(@Param("transferStatus") TransferStatus status);
}
