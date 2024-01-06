package com.example.CashDeskModule.Controller;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api/v1")
public class CashOperationsController {

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

        String transactionRecord = request.getType() + " " + request.getAmount() + "\n";
        Files.write(Paths.get(TRANSACTIONS_FILE), transactionRecord.getBytes(), StandardOpenOption.APPEND);


        return "Transaction recorded: " + transactionRecord;
    }

    @GetMapping("/cash-balance")
    public String getCashBalance(@RequestHeader("FIB-X-AUTH") String apiKey) throws IOException {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API Key");
        }

        String balanceInfo = new String(Files.readAllBytes(Paths.get(BALANCES_FILE)));

        return "Current balance and denominations: " + balanceInfo;
    }
}

