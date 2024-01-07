package com.example.CashDeskModule.Service;


import com.example.CashDeskModule.Entity.CashOperationRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public interface CashOperationsService {
    Optional<CashOperationRequest> processTransaction(CashOperationRequest request) throws IOException;
}
