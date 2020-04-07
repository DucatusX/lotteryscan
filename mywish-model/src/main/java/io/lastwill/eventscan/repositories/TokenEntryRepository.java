package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.TokenInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

import java.util.List;

public interface TokenEntryRepository extends CrudRepository<TokenInfo, Long> {

    List<TokenInfo> findAllByDucatuAddressOrderByIdDesc(@Param("ducatusAddress") String ducatusAddress);

    List<TokenInfo> findAllByDucatusxAddressOrderByIdDesc(@Param("ducatusxAddress") String ducatusxAddress);

    @Query("select t.secretCode from TokenInfo t where  t.tokenType = :tokenType")
    List<String> findAllIdByTokenType(@Param("tokenType") Integer tokenType);

    @Query("select t from TokenInfo t where t.secretCode in :secretCode")
    List<TokenInfo> findAllBySecretCode(@Param("secretCode") Collection<String> secretCode);

    @Query("select t from TokenInfo t where t.publicCode in :publicCode")
    List<TokenInfo> findAllByPublicCode(@Param("publicCode") Collection<String> publicCode);

    List<TokenInfo> findAll();

}
