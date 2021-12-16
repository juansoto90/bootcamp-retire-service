package com.nttdata.retire.service.Impl;

import com.nttdata.retire.model.entity.CreditCard;
import com.nttdata.retire.service.ICreditCardService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CreditCardServiceImpl implements ICreditCardService {

    private final WebClient.Builder webClientBuilder;
    private final String WEB_CLIENT_URL = "microservice.web.creditcard";
    private final String BASE;

    public CreditCardServiceImpl(WebClient.Builder webClientBuilder, Environment env) {
        this.webClientBuilder = webClientBuilder;
        BASE = env.getProperty(WEB_CLIENT_URL);
    }

    @Override
    public Mono<CreditCard> findByCardNumber(String cardNumber) {
        return webClientBuilder
                .baseUrl(BASE)
                .build()
                .get()
                .uri("/card-number/{cardNumber}", cardNumber)
                .retrieve()
                .bodyToMono(CreditCard.class);
    }
}
