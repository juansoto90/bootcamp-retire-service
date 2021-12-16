package com.nttdata.retire.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private String id;
    private double balance;
    private String accountNumber;
    private String accountType;
    private Customer customer;
    //Feature of saving, current and fixed-term accounts
    private boolean maintenanceCommission;
    private boolean maximumMovementLimit;
    private Integer movementAmount;
    private List<Customer> customerOwner;
    private List<Customer> customerAuthorizedSigner;
    //Feature credit
    private double amount;
    private double payment;
    //Feature credit card
    private double consumption;
    private double creditLine;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate = LocalDateTime.now();
    private String status;
}
