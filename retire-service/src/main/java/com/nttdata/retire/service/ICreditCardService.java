package com.nttdata.retire.service;

import com.nttdata.retire.model.entity.CreditCard;
import reactor.core.publisher.Mono;

public interface ICreditCardService {
    public Mono<CreditCard> findByCardNumber(String cardNumber);
}
