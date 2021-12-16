package com.nttdata.retire.config;

import com.nttdata.retire.handler.RetireHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(RetireHandler handler){
        return route(GET("/retire/{id}"), handler::findById)
                .andRoute(POST("/retire"), handler::create)
                .andRoute(POST("/retire/create"), handler::retireCreate);
    }
}
