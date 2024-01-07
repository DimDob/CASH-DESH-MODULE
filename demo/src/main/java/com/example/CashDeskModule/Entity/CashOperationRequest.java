package com.example.CashDeskModule.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "CashOperations")
public class CashOperationRequest {
    //The class attrs should always be private. Accessed only by getters
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) //I believe this will work only if i save the entity in a DB.
    private UUID requestId = UUID.randomUUID();

    private String type; //The attr "type" param will store the type of the transaction

    private double amount; //This would store the amount of the operation

}
