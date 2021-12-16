package com.nttdata.retire.service;

import com.nttdata.retire.model.entity.Account;
import reactor.core.publisher.Mono;

public interface IAccountService {
    public Mono<Account> findByAccountNumber(String accountNumber);
    public Mono<Account> updateAmountAccount(Account account);
}
