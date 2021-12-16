package com.nttdata.retire.service.Impl;

import com.nttdata.retire.model.entity.Association;
import com.nttdata.retire.service.IAssociationService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class AssociationServiceImpl implements IAssociationService {

    private final WebClient.Builder webClientBuilder;
    private final String WEB_CLIENT_URL = "microservice.web.association";
    private final String BASE;

    public AssociationServiceImpl(WebClient.Builder webClientBuilder, Environment env) {
        this.webClientBuilder = webClientBuilder;
        BASE = env.getProperty(WEB_CLIENT_URL);
    }

    @Override
    public Flux<Association> findByCardNumberAndStatus(String cardNumber, String status) {
        return webClientBuilder
                .baseUrl(BASE)
                .build()
                .get()
                .uri("/card/{cardNumber}/{status}", cardNumber, status)
                .retrieve()
                .bodyToFlux(Association.class);
    }
}
