package org.facmc.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.pojo.LoginData;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
public class AuthenticationConverter extends ServerFormLoginAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        HttpMethod method = exchange.getRequest().getMethod();
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();

        if (MediaType.APPLICATION_JSON.equals(contentType) && Objects.equals(method, HttpMethod.POST)) {
            ObjectMapper mapper = new ObjectMapper();
            HttpMessageReader<Object> messageReader = new DecoderHttpMessageReader<>(new Jackson2JsonDecoder(mapper));
            List<HttpMessageReader<?>> readers = new ArrayList<>();
            readers.add(messageReader);
            ServerRequest serverRequest = ServerRequest.create(exchange, readers);
            return serverRequest.bodyToMono(LoginData.class).map(loginData -> new AuthenticationToken(loginData.getUsername(), loginData.getPassword()));
        } else {
            return super.convert(exchange);
        }
    }
}
