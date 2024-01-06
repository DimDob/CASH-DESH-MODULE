package com.example.CashDeskModule.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

//@Data this sets getters and setters, seems like its deprecated
@Setter
@Getter
@Entity
@Table(name = "CashOperations")
public class CashOperationRequest {
//    The class attrs should always be private. Accessed only by getters
    @Id
    @GeneratedValue
    private UUID requestId;
    @Getter
    private String type; //The attr "type" param will store the type of the transaction
    private double amount; //This would store the amount of the operation

}
