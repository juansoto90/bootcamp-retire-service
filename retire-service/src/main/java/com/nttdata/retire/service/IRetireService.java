package com.nttdata.retire.service;

import com.nttdata.retire.model.entity.Retire;
import reactor.core.publisher.Mono;

public interface IRetireService {
    public Mono<Retire> save(Retire retire);
    public Mono<Retire> findById(String id);
}
