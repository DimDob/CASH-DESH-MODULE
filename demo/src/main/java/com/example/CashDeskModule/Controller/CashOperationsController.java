package com.example.CashDeskModule.Controller;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Entity.Cashier;
import com.example.CashDeskModule.Service.CashOperationsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CashOperationsController {

    private static final Cashier cashier = new Cashier();
    @Value("${API_KEY}")
    private String API_KEY;
    private static final String BALANCES_FILE = "balances.txt";


    private final CashOperationsServiceImpl cashOperationsServiceImpl;

    public CashOperationsController(CashOperationsServiceImpl cashOperationsServiceImpl) {
        this.cashOperationsServiceImpl = cashOperationsServiceImpl;
    }

    private boolean validateAPIKey(HttpServletRequest request) {
        String apiKey = request.getHeader("FIB-X-AUTH");
        return API_KEY.equals(apiKey);
    }

    @PostMapping("/cash-operation")
    public Optional<CashOperationRequest> handleCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey,
                                                              @RequestBody CashOperationRequest request) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
        }

        return cashOperationsServiceImpl.processTransaction(request, cashier);

    }

    @GetMapping("/cash-balance")
    public String getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
        }

        Path path = Paths.get(BALANCES_FILE);
        if (!(Files.exists(path))) {
            Files.createFile(path);
        }
        String balanceInfo = new String(Files.readAllBytes(path));

        return String.format("Current balance and denominations for cashier %s %s",
                cashier.getNAME(),
                balanceInfo);
    }
}
