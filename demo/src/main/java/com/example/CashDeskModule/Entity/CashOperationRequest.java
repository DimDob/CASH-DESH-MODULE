package com.example.CashDeskModule.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "cash_operations")
@NoArgsConstructor
public class CashOperationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "transaction_type")
    private String type;

    @Column(name="amount")
    private double amount;

    @Column(name = "Cashier")
    private String cashier = "Martina"; //I suppose the cashier's name won't change in the future.

    @Column(name = "Date")
    private String date;
}
