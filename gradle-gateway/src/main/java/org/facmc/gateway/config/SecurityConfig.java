package org.facmc.gateway.config;

import com.alibaba.fastjson.JSONObject;
import org.facmc.gateway.handle.LoginFailureHandle;
import org.facmc.gateway.handle.LoginSuccessHandle;
import org.facmc.gateway.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//                .loginProcessingUrl("/test")
//                .loginPage("http://localhost:39091/login");
//
//        //授权
//        http.authorizeHttpRequests()
//                .antMatchers("/api/test").permitAll()
//                .anyRequest().authenticated();
//    }

    @Resource
    LoginSuccessHandle loginSuccessHandle;
    //    @Resource
//    SecurityContextRepository securityContextRepository;
    @Resource
    MyAuthenticationManager authenticationManager1;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    AuthenticationConverter authenticationConverter;
    @Resource
    SecurityContextRepository securityContextRepository;
    @Resource
    MyAuthorizationManager myAuthorizationManager;
    @Resource
    LoginFailureHandle loginFailureHandle;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers("/public/**").permitAll()
                            .pathMatchers("/api/test").permitAll()
//                            .pathMatchers("/api/user/service/listUsersForSuperUser").hasRole("SUPER")
                            .pathMatchers(HttpMethod.OPTIONS).permitAll()
                            .anyExchange().access(myAuthorizationManager);
                });
        httpSecurity.addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.FORM_LOGIN);
//        httpSecurity.csrf(csrfSpec -> csrfSpec
//                .disable().headers().disable());

        httpSecurity.cors(cors -> cors.configurationSource( // 对跨域请求进行配置
                exchange -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setExposedHeaders(Collections.singletonList("Content-Disposition"));
                    config.setAllowCredentials(true);
                    config.applyPermitDefaultValues();
                    return config;
                }
        ));
        return httpSecurity.build();
    }


//    @Bean
//    public PasswordEncoder getPW() {
//        return new BCryptPasswordEncoder();
////
//    }

    /**
     * 输出响应信息
     *
     * @param exchange
     * @param responseMap
     * @return
     */
    public Mono<Void> writeWith(ServerWebExchange exchange, Map<String, String> responseMap) {
        ServerHttpResponse response = exchange.getResponse();
        String body = JSONObject.toJSONString(responseMap);
        DataBuffer buffer = null;
        buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));

    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(authenticationConverter);
        filter.setAuthenticationSuccessHandler(loginSuccessHandle);
        filter.setAuthenticationFailureHandler(loginFailureHandle);
        filter.setSecurityContextRepository(securityContextRepository);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/login")
        );
        return filter;
    }

    @Bean
    ReactiveAuthenticationManager authenticationManager() {
        List<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authenticationManager1);
//        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//        authenticationManager.setPasswordEncoder(getPW());
        return new DelegatingReactiveAuthenticationManager(managers);
//        return authenticationManager;
    }

    @Bean
    public PasswordEncoder getPassword() {
        return new BCryptPasswordEncoder();
    }
}
