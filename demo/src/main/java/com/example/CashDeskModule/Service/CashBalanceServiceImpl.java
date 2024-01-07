package com.example.CashDeskModule.Service;

import com.example.CashDeskModule.Entity.CashOperationRequest;
import com.example.CashDeskModule.Repository.CashOperationsServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CashBalanceServiceImpl {

    @Autowired
    private final CashOperationsServiceRepository cashOperationsServiceRepository;

    @Autowired
    private final CashOperationsServiceImpl cashOperationsServiceImpl;

    private static final String BALANCE_AND_DENOMINATIONS_FILE = "balance_and_denominations.txt";


    public CashBalanceServiceImpl(CashOperationsServiceRepository cashOperationsServiceRepository, CashOperationsServiceImpl cashOperationsServiceImpl) {
        this.cashOperationsServiceRepository = cashOperationsServiceRepository;
        this.cashOperationsServiceImpl = cashOperationsServiceImpl;
    }

    public String getBalance(String currency) throws IOException {
        Optional<CashOperationRequest> latestTransaction = cashOperationsServiceRepository.findLastTransaction(currency);
        if (latestTransaction.isEmpty()) {
            return String.format("No %s transactions stored in the database!", currency);
        }
        double currentAmount = latestTransaction.get().getAmount();

        Map<Integer, Integer> denominations = calculateDenominations(currentAmount, currency);
        latestTransaction.get().setDenominations(denominations.toString());
        cashOperationsServiceImpl.writeToTransactionsFile(latestTransaction.get(), BALANCE_AND_DENOMINATIONS_FILE);
        return "Balance and denominations saved in balance_and_denominations.txt file!";
    }

    private Map<Integer, Integer> calculateDenominations(double amount, String currency) {
        int[] BGNDenominations = {50, 10};
        int[] EURDenominations = {50, 20, 10};

        Map<Integer, Integer> denominationCount = new LinkedHashMap<>();
        int[] denominations = currency.equals("BGN") ? BGNDenominations : EURDenominations;

        for (int denomination : denominations) {
            int count = (int) (amount / denomination);
            amount -= (count * denomination);
            denominationCount.put(denomination, count);
        }

        return denominationCount;
    }
}
