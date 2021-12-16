package com.nttdata.retire.model.dto;

import lombok.Data;

@Data
public class RetireDto {
    private String cardNumber;
    private double amount;
    private double remaining;
    private double retireAmount;
}
