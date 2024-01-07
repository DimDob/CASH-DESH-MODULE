package com.example.CashDeskModule.Service;


import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Entity.Cashier;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CashOperationsServiceImpl implements CashOperationsService {

    private static final String TRANSACTIONS_FILE = "transactions.txt";

    @Override
    public Optional<CashOperationRequest> processTransaction(CashOperationRequest request, Cashier cashier) throws IOException {

        return handleCashOperation(request, cashier);
    }
        //Software logic which handles sensitive data must always be processed in a private method and accessed from a public endpoint.
        private Optional<CashOperationRequest> handleCashOperation(CashOperationRequest request, Cashier cashier) throws IOException {

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); //I use this to format the date.
            LocalDateTime date = LocalDateTime.now();

            String transactionRecord = String.format("""
                            TRANSACTION REGISTERED:
                            Transaction id: %s
                            Type: %s
                            Amount: %s
                            Made by: %s
                            On date & time: %s

                            """,
                    request.getRequestId(),
                    request.getType(),
                    request.getAmount(),
                    cashier.getNAME(),
                    dtf.format(date));

            Path path = Paths.get(TRANSACTIONS_FILE);
            if (!(Files.exists(path))) {
                Files.createFile(path);
            }

        String transactionType = request.getType();
        return switch (transactionType) {
            case "deposit", "withdrawal" -> {
                Files.write(
                       path,
                       transactionRecord.getBytes(),
                       StandardOpenOption.APPEND);
                yield Optional.of(request);
            }
            default -> {
                request.setType("Unknown transaction type!");
                yield Optional.of(request);
            }
        };

    }
}
