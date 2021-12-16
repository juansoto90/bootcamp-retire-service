package com.nttdata.retire.service.Impl;

import com.nttdata.retire.model.entity.Retire;
import com.nttdata.retire.repository.IRetireRepository;
import com.nttdata.retire.service.IRetireService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RetireServiceImpl implements IRetireService {

    private final IRetireRepository repository;

    @Override
    public Mono<Retire> save(Retire retire) {
        return repository.save(retire);
    }

    @Override
    public Mono<Retire> findById(String id) {
        return repository.findById(id);
    }
}
