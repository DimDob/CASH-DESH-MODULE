package com.example.CashDeskModule.Service;


import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Repository.CashOperationsServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Service
public class CashOperationsServiceImpl implements CashOperationsService {

    @Autowired
    private final CashOperationsServiceRepository cashOperationsServiceRepository;

    private static final String TRANSACTIONS_FILE = "transactions.txt";

    public CashOperationsServiceImpl(CashOperationsServiceRepository cashOperationsServiceRepository) {
        this.cashOperationsServiceRepository = cashOperationsServiceRepository;
    }

    @Override
    public Optional<CashOperationRequest> processTransaction(CashOperationRequest request) throws IOException {

        return handleCashOperation(request);
    }

        //Software logic which handles sensitive data must always be processed in a private method and accessed from a public endpoint.
        @Transactional
        private Optional<CashOperationRequest> handleCashOperation(CashOperationRequest request) throws IOException {
            String transactionType = request.getType();
            double amount = request.getAmount();
            String currency = request.getCurrency();
            if (amount <= 0.0) {
                System.out.println("Transaction amount must be positive. Invalid transaction not processed.");
                return Optional.empty();
            }

            return switch (transactionType) {
                case "deposit", "withdrawal" -> {
                    double newAmount = (transactionType.equals("deposit")) ? depositAmount(amount, currency) : withdrawalAmount(amount, currency);

                    if (newAmount == getCurrentBalance(currency)) {
                        System.out.println("No changes in the amount detected!");
                        yield Optional.empty();
                    }

                    request.setAmount(newAmount);
                    writeToTransactionsFile(request, TRANSACTIONS_FILE);
                    cashOperationsServiceRepository.save(request);
                    System.out.printf("%s transaction %s saved in the database!%n", transactionType, request.getId());
                    yield Optional.of(request);
                }
                default -> {
                    System.out.println("Unknown transaction type!");
                    yield Optional.empty();
                }
            };
        }


    private double depositAmount(double amount, String currency) {
        if (amount <= 0.0) {
            System.out.println("Please enter a positive amount to deposit!");
            return getCurrentBalance(currency);
        }
        return getCurrentBalance(currency) + amount;
    }

    private double getCurrentBalance(String currency) {
        return cashOperationsServiceRepository.findLastTransaction(currency)
                .map(CashOperationRequest::getAmount)
                .orElse(0.0);
    }


    private double withdrawalAmount(double amount, String currency) {
        double currentBalance = getCurrentBalance(currency);

        if (amount <= 0.0) {
            System.out.println("Please enter a positive amount to withdraw!");
            return currentBalance;
        }

        if (currentBalance < amount) {
            System.out.println("Insufficient funds for the withdrawal.");
            return currentBalance;
        }

        return currentBalance - amount;
    }


    public void writeToTransactionsFile(CashOperationRequest request, String file) throws IOException {
        Path path = Paths.get(file);
        String transactionRecord;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.now();

        request.setDate(dtf.format(date));
        if (file.equals("transactions.txt")) {


            transactionRecord = String.format("""
                    TRANSACTION REGISTERED:
                    Type: %s
                    Amount: %s
                    Made by: %s
                    Currency: %s
                    On date & time: %s
                    
                    """,
                    request.getType(),
                    request.getAmount(),
                    request.getCashier(),
                    request.getCurrency(),
                    dtf.format(date));


        } else {
            transactionRecord = String.format("""
                    Balance for %s account as of date %s:
                    BALANCE: %s
                    Account Holder: %s
                    DENOMINATIONS: %s
                    
                    """,
                    request.getCurrency(),
                    request.getDate(),
                    request.getAmount(),
                    request.getCashier(),
                    request.getDenominations()
                    );
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Files.write(path, transactionRecord.getBytes(), StandardOpenOption.APPEND);
    }
}



