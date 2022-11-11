package org.facmc.gateway.config;

import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.pojo.LoginData;
import org.facmc.gateway.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@Primary
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        //转换自定义token
        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        log.debug("{}", authenticationToken.toString());
        //获取登录参数
        LoginData loginData = authenticationToken.getLoginData();
        if (loginData == null) {
            throw new AuthenticationServiceException("无登录参数");
        }

        //获取用户
        Mono<UserDetails> byUsername = userDetailsService.findByUsername(loginData.getUsername());
        AuthenticationToken authenticationToken1 = new AuthenticationToken(byUsername.block(), authenticationToken);
        return Mono.just(authenticationToken1);
    }
}
