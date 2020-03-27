package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.TokenInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenEntryRepository extends CrudRepository<TokenInfo, Long> {

    List<TokenInfo> findAllByDucatuAddressOrderByIdDesc(@Param("ducatusAddress") String ducatusAddress);

    List<TokenInfo> findAllByDucatusxAddressOrderByIdDesc(@Param("ducatusxAddress") String ducatusxAddress);

    List<TokenInfo> findAll();

    TokenInfo findFirstByUserId(@Param("userId") String userId);
}
