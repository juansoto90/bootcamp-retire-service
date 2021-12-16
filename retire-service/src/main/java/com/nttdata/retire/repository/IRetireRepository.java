package com.nttdata.retire.repository;

import com.nttdata.retire.model.entity.Retire;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IRetireRepository extends ReactiveMongoRepository<Retire, String> {
}
