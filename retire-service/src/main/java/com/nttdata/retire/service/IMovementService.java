package com.nttdata.retire.service;

import com.nttdata.retire.model.entity.Movement;
import reactor.core.publisher.Mono;

public interface IMovementService {
    public Mono<Movement> save(Movement movement);
}
