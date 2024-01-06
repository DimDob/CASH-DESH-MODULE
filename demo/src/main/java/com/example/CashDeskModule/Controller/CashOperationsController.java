package com.example.CashDeskModule.Controller;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Entity.Cashier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api/v1")
public class CashOperationsController {

    private static final Cashier cashier = new Cashier();
    private static final String API_KEY = "f9Uie8nNf112hx8s";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String BALANCES_FILE = "balances.txt";

    private boolean validateAPIKey(HttpServletRequest request) {
        String apiKey = request.getHeader("FIB-X-AUTH");
        return API_KEY.equals(apiKey);
    }

    @PostMapping("/cash-operation")
    public String handleCashOperation(@RequestHeader("FIB-X-AUTH") String apiKey,
                                      @RequestBody CashOperationRequest request) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
        }

        String transactionRecord = String.format("%s %s \n",
                request.getType(),
                request.getAmount());

        Path path = Paths.get(TRANSACTIONS_FILE);
        if (!(Files.exists(path))) {
            Files.createFile(path);
        }

        Files.write(path, transactionRecord.getBytes(), StandardOpenOption.APPEND);

        return String.format("Transaction recorded from cashier %s %s",
                cashier.getNAME(),
                transactionRecord);
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

