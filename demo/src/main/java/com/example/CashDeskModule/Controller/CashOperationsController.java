package com.example.CashDeskModule.Controller;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Service.CashBalanceServiceImpl;
import com.example.CashDeskModule.Service.CashOperationsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CashOperationsController {

    @Value("${API_KEY}")
    private String API_KEY;
    private static final String BALANCES_FILE = "balances.txt";


    private final CashOperationsServiceImpl cashOperationsServiceImpl;

    private final CashBalanceServiceImpl cashBalanceServiceImpl;

    public CashOperationsController(CashOperationsServiceImpl cashOperationsServiceImpl, CashBalanceServiceImpl cashBalanceServiceImpl) {
        this.cashOperationsServiceImpl = cashOperationsServiceImpl;
        this.cashBalanceServiceImpl = cashBalanceServiceImpl;
    }

    private boolean validateAPIKey(HttpServletRequest request) {
        String apiKey = request.getHeader("FIB-X-AUTH");
        return API_KEY.equals(apiKey);
    }

    @PostMapping("/cash-operation") //I would not want anyone to see the transactions operations in real cases, so they should be visible only in the DB (or transactions.txt file, in this case). Because of that I would return only the status of the response.
    public ResponseEntity<String> handleCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey,
                                                      @Valid @RequestBody CashOperationRequest request) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }

        Optional<CashOperationRequest> processedTransaction = cashOperationsServiceImpl.processTransaction(request);
        if (processedTransaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsuccessful: Invalid transaction details");
        }

        return ResponseEntity.ok("Transaction has been processed successfully!");
    }

    @GetMapping("/cash-balance")
    public ResponseEntity<String> getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey,
                                                 @RequestParam String currency) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
        }
        String cashBalance = cashBalanceServiceImpl.getBalance(currency);
        if (cashBalance.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsuccessful: Invalid balance details");
        }

        return ResponseEntity.ok("Balance has been processed successfully!");
    }

}
