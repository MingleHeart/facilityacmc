package org.facmc.gateway.config;

import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.pojo.LoginData;
import org.facmc.gateway.pojo.MyUserDetials;
import org.facmc.gateway.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Log4j2
@Component
@Primary
public class MyAuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        //转换自定义token
        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        log.debug("{}", authenticationToken.toString());
        if (authentication.getPrincipal() == null) {
            throw new AuthenticationServiceException("无登录参数");
        }
        //获取登录参数
        LoginData loginData = new LoginData();
        loginData.setUsername((String) authenticationToken.getPrincipal());
        loginData.setPassword((String) authenticationToken.getCredentials());

        MyUserDetials myUserDetials = userDetailsService.findByUsername(loginData.getUsername());
        if (!passwordEncoder.matches(loginData.getPassword(), myUserDetials.getPassword())) {
            return Mono.error(new BadCredentialsException("用户不存在或者密码错误"));
        }
        AuthenticationToken myAuthenticationToken = new AuthenticationToken(myUserDetials, authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //获取用户
        return Mono.just(myAuthenticationToken);
    }

}
