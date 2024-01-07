package com.example.CashDeskModule.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

@Data
@Entity
@Table(name = "cash_operations")
@NoArgsConstructor
public class CashOperationRequest {

    @Transient
    private String denominations;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "transaction_type")
    @Pattern(regexp = "deposit|withdrawal")
    private String type;

    @Column(name = "amount")
    private double amount;

    @Column(name = "Cashier")
    private String cashier = "Martina";

    @Column(name = "Currency")
    @Pattern(regexp = "EUR|BGN")
    private String currency;

    @Column(name = "Date")
    private String date;
}
