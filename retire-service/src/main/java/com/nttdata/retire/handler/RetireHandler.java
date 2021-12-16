package com.nttdata.retire.handler;

import com.nttdata.retire.model.dto.RetireDto;
import com.nttdata.retire.model.entity.*;
import com.nttdata.retire.service.*;
import com.nttdata.retire.util.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RetireHandler {

    private final IRetireService service;
    private final IAssociationService iAssociationService;
    private final IAccountService iAccountService;
    private final IMovementService iMovementService;
    private final ICreditCardService iCreditCardService;

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<Retire> retireMono = request.bodyToMono(Retire.class);
        return retireMono
                .flatMap(service::save)
                .flatMap(r -> ServerResponse.created(URI.create("/retire/".concat(r.getId())))
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(r)
                );
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id)
                .flatMap(r -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(r)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> retireCreate(ServerRequest request){
        Mono<RetireDto> retireDtoMono = request.bodyToMono(RetireDto.class);
        List<Account> listAccount = new ArrayList<>();
        Account accountPrincipal = new Account();
        CreditCard creditCard = new CreditCard();
        RetireDto retireDto = new RetireDto();
        Retire retire = new Retire();
        return retireDtoMono
                .map(dto -> {
                    retire.setAmount(dto.getAmount());
                    return dto;
                })
                .flatMap(dto -> iCreditCardService.findByCardNumber(dto.getCardNumber())
                        .map(c -> {
                            creditCard.setCardNumber(c.getCardNumber());
                            creditCard.setCardType(c.getCardType());
                            return dto;
                        })
                )
                .flatMap(dto -> iAssociationService.findByCardNumberAndStatus(dto.getCardNumber(), "ASSOCIATED")
                        .collectList()
                )
                .flatMap(list -> {
                    return Flux.fromIterable(list)
                            .filter(a -> a.isPrincipal())
                            .map(a -> {
                                if (a.isPrincipal()) accountPrincipal.setAccountNumber(a.getAccountNumber());
                                return a;
                            })
                            .then(Mono.just(list));
                })
                .flatMap(list -> {
                    return iAccountService.findByAccountNumber(accountPrincipal.getAccountNumber())
                                    .flatMap(a -> {
                                        double balanceNew = 0;
                                        if (a.getBalance() >= retire.getAmount()){
                                            retireDto.setRemaining(0);
                                            retireDto.setRetireAmount(retire.getAmount());
                                            balanceNew = a.getBalance() - retire.getAmount();
                                        } else {
                                            retireDto.setRemaining(retire.getAmount() - a.getBalance());
                                            retireDto.setRetireAmount(a.getBalance());
                                            balanceNew = 0;
                                        }

                                        Account ap = new Account();
                                        ap.setAccountNumber(a.getAccountNumber());
                                        ap.setBalance(balanceNew);
                                        return iAccountService.updateAmountAccount(ap)
                                                .flatMap(acc -> {
                                                    Retire r = new Retire();
                                                    r.setOperationNumber(Generator.generateOperationNumber());
                                                    r.setAmount(retireDto.getRetireAmount());
                                                    r.setAccount(a);
                                                    r.setStatus("PROCESSED");
                                                    return service.save(r)
                                                            .flatMap(re -> {
                                                                Movement m = new Movement();
                                                                m.setOperationNumber(re.getOperationNumber());
                                                                m.setAccountNumber(acc.getAccountNumber());
                                                                m.setCardNumber(creditCard.getCardNumber());
                                                                m.setMovementType("RETIRE");
                                                                m.setAccountType(acc.getAccountType());
                                                                m.setCardType(creditCard.getCardType());
                                                                m.setDocumentNumber(a.getCustomer().getDocumentNumber());
                                                                m.setAmount(retireDto.getRetireAmount()*-1);
                                                                m.setConcept("RETIRE DEBIT CARD");
                                                                m.setStatus("PROCESSED");
                                                                return iMovementService.save(m).flatMap(mo -> Mono.just(list));
                                                                        /*.flatMap(mo -> {
                                                                            list.forEach(account -> {
                                                                                if (!account.isPrincipal()){
                                                                                    iAccountService.findByAccountNumber(account.getAccountNumber())
                                                                                            .flatMap(accSecond -> {
                                                                                                double balanceNewSec = 0;
                                                                                                if (accSecond.getBalance() >= retireDto.getRemaining()) {
                                                                                                    retireDto.setRemaining(0);
                                                                                                    retireDto.setRetireAmount(retireDto.getRemaining());
                                                                                                    balanceNewSec = accSecond.getBalance() - retireDto.getRemaining();
                                                                                                } else {
                                                                                                    retireDto.setRemaining(retireDto.getAmount() - accSecond.getBalance());
                                                                                                    retireDto.setRetireAmount(accSecond.getBalance());
                                                                                                    balanceNewSec = 0;
                                                                                                }

                                                                                                Account aSecond = new Account();
                                                                                                aSecond.setAccountNumber(accSecond.getAccountNumber());
                                                                                                aSecond.setBalance(balanceNewSec);
                                                                                                return iAccountService.updateAmountAccount(aSecond)
                                                                                                        .flatMap(as -> {
                                                                                                            Retire rs = new Retire();
                                                                                                            rs.setOperationNumber(Generator.generateOperationNumber());
                                                                                                            rs.setAmount(retireDto.getRetireAmount());
                                                                                                            rs.setAccount(accSecond);
                                                                                                            rs.setStatus("PROCESSED");
                                                                                                            return service.save(r)
                                                                                                                    .flatMap(res -> {
                                                                                                                        Movement ms = new Movement();
                                                                                                                        ms.setOperationNumber(re.getOperationNumber());
                                                                                                                        ms.setAccountNumber(ap.getAccountNumber());
                                                                                                                        ms.setCardNumber(creditCard.getCardNumber());
                                                                                                                        ms.setMovementType("RETIRE");
                                                                                                                        ms.setAccountType(acc.getAccountType());
                                                                                                                        ms.setCardType(creditCard.getCardType());
                                                                                                                        ms.setDocumentNumber(ap.getCustomer().getDocumentNumber());
                                                                                                                        ms.setAmount(retireDto.getRetireAmount());
                                                                                                                        ms.setConcept("RETIRE DEBIT CARD");
                                                                                                                        ms.setStatus("PROCESSED");
                                                                                                                        return iMovementService.save(m);
                                                                                                                    });
                                                                                                        });
                                                                                            });
                                                                                }
                                                                            });
                                                                            return null;
                                                                        });*/
                                                            });
                                                });
                                    });

                    //return null;
                })
                .flatMapMany(list ->
                                Flux.fromIterable(list)
                                .filter(a -> !a.isPrincipal())
                                .sort(Comparator.comparing(Association::getCreationDate))
                                .flatMap(ass -> iAccountService.findByAccountNumber(ass.getAccountNumber()))
                                .flatMap(acc -> {
                                    if (retireDto.getRemaining() > 0){
                                        double balanceNewSec = 0;
                                        if (acc.getBalance() >= retireDto.getRemaining()) {
                                            balanceNewSec = acc.getBalance() - retireDto.getRemaining();
                                            retireDto.setRetireAmount(retireDto.getRemaining());
                                            retireDto.setRemaining(0);
                                        } else {
                                            balanceNewSec = 0;
                                            retireDto.setRemaining(retireDto.getRemaining() - acc.getBalance());
                                            retireDto.setRetireAmount(acc.getBalance());
                                        }

                                        Account aSecond = new Account();
                                        aSecond.setAccountNumber(acc.getAccountNumber());
                                        aSecond.setBalance(balanceNewSec);
                                        return iAccountService.updateAmountAccount(aSecond)
                                                .flatMap(as -> {
                                                    double ret = acc.getBalance() - as.getBalance();
                                                    Retire rs = new Retire();
                                                    rs.setOperationNumber(Generator.generateOperationNumber());
                                                    rs.setAmount(ret);
                                                    rs.setAccount(acc);
                                                    rs.setStatus("PROCESSED");
                                                    return service.save(rs)
                                                            .flatMap(res -> {
                                                                Movement ms = new Movement();
                                                                ms.setOperationNumber(res.getOperationNumber());
                                                                ms.setAccountNumber(acc.getAccountNumber());
                                                                ms.setCardNumber(creditCard.getCardNumber());
                                                                ms.setMovementType("RETIRE");
                                                                ms.setAccountType(acc.getAccountType());
                                                                ms.setCardType(creditCard.getCardType());
                                                                ms.setDocumentNumber(acc.getCustomer().getDocumentNumber());
                                                                ms.setAmount(res.getAmount()*-1);
                                                                ms.setConcept("RETIRE DEBIT CARD SECOND ACCOUNT");
                                                                ms.setStatus("PROCESSED");
                                                                return iMovementService.save(ms);
                                                            });
                                                });
                                    }
                                    return Mono.just(acc);
                                })
                )
                .collectList()
                .flatMap(c -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(c)
                );
    }
}
