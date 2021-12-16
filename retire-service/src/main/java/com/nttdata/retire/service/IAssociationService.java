package com.nttdata.retire.service;

import com.nttdata.retire.model.entity.Association;
import reactor.core.publisher.Flux;

public interface IAssociationService {
    public Flux<Association> findByCardNumberAndStatus(String cardNumber, String status);
}
