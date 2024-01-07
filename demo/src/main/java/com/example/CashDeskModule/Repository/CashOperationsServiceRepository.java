package com.example.CashDeskModule.Repository;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashOperationsServiceRepository extends JpaRepository<CashOperationRequest, UUID> {
    @Query(value = "SELECT * FROM cash_operations ORDER BY date DESC LIMIT 1", nativeQuery = true)
    Optional<CashOperationRequest> findLastTransaction();
}