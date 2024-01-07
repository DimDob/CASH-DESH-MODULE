package com.example.CashDeskModule.Service;


import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Repository.CashOperationsServiceRepository;
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
        private Optional<CashOperationRequest> handleCashOperation(CashOperationRequest request) throws IOException {
            String transactionType = request.getType();
            double amount = request.getAmount();

            if (amount <= 0.0) {
                System.out.println("Transaction amount must be positive. Invalid transaction not processed.");
                return Optional.empty();
            }

            return switch (transactionType) {
                case "deposit", "withdrawal" -> {
                    double newAmount = (transactionType.equals("deposit")) ? depositAmount(amount) : withdrawalAmount(amount);

                    if (newAmount == getCurrentBalance()) {
                        System.out.println("No changes in the amount detected!");
                        yield Optional.empty();
                    }

                    request.setAmount(newAmount);
                    writeToTransactionsFile(request);
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


    private double depositAmount(double amount) {
        if (amount <= 0.0) {
            System.out.println("Please enter a positive amount to deposit!");
            return getCurrentBalance();
        }
        return getCurrentBalance() + amount;
    }

    private double getCurrentBalance() {
        return cashOperationsServiceRepository.findLastTransaction()
                .map(CashOperationRequest::getAmount)
                .orElse(0.0);
    }


    private double withdrawalAmount(double amount) {
        double currentBalance = getCurrentBalance();

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


    private void writeToTransactionsFile(CashOperationRequest request) throws IOException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.now();

        request.setDate(dtf.format(date));

        String transactionRecord = String.format("""
                    TRANSACTION REGISTERED:
                    Type: %s
                    Amount: %s
                    Made by: %s
                    On date & time: %s

                    """,
                request.getType(),
                request.getAmount(),
                request.getCashier(),
                dtf.format(date));

        Path path = Paths.get(TRANSACTIONS_FILE);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Files.write(path, transactionRecord.getBytes(), StandardOpenOption.APPEND);

    }
}



