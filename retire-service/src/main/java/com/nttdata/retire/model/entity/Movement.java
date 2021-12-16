package com.nttdata.retire.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movement {
    @Id
    private String id;
    private String operationNumber;
    private String accountNumber;
    private String cardNumber;
    private String movementType;
    private String accountType;
    private String cardType;
    private String documentNumber;
    private double amount;
    private String concept;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime movementDate = LocalDateTime.now();
    private String status;
}
