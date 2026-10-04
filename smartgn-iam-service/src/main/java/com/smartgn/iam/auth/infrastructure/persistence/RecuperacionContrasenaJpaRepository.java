package com.smartgn.iam.auth.infrastructure.persistence;

import com.smartgn.iam.auth.domain.model.RecuperacionContrasena;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RecuperacionContrasenaJpaRepository extends JpaRepository<RecuperacionContrasena, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RecuperacionContrasena> findByTokenHash(String tokenHash);

    @Modifying(flushAutomatically = true)
    @Query("update RecuperacionContrasena r set r.utilizadoEn = :ahora "
            + "where r.idCuenta = :idCuenta and r.utilizadoEn is null")
    int invalidarPendientes(@Param("idCuenta") UUID idCuenta, @Param("ahora") Instant ahora);
}
