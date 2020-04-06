package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.TokenInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface TokenEntryRepository extends CrudRepository<TokenInfo, Long> {

    List<TokenInfo> findAllByDucatuAddressOrderByIdDesc(@Param("ducatusAddress") String ducatusAddress);

    List<TokenInfo> findAllByDucatusxAddressOrderByIdDesc(@Param("ducatusxAddress") String ducatusxAddress);

    @Query("select t.userId from TokenInfo t where  t.tokenType = :tokenType")
    List<String> findAllIdByTokenType(@Param("tokenType") Integer tokenType);

    @Query("select t from TokenInfo t where t.userId in :userId")
    List<TokenInfo> findAllBySecretCode(@Param("userId") Collection<String> userId);

    List<TokenInfo> findAll();

    TokenInfo findFirstByUserId(@Param("userId") String userId);
}
