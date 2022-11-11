package org.facmc.gateway.config;

import com.alibaba.fastjson.JSONObject;
import org.facmc.gateway.handle.LoginSuccessHandle;
import org.facmc.gateway.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
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
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    AuthenticationConverter authenticationConverter;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
//        List<ReactiveAuthenticationManager> managers = new LinkedList<>();
//        managers.add(authenticationManager);
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(getPW());
//        return new DelegatingReactiveAuthenticationManager(managers);
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
//        httpSecurity.formLogin(formLoginSpec -> formLoginSpec
//                        .loginPage("/api/login").authenticationFailureHandler((webFilterExchange, exception) -> { //验证失败处理器(可以单独创建类处理)
//                                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                                    Map<String, String> responseMap = new HashMap<>();
//                                    responseMap.put("code", "failure");
//                                    if (exception instanceof UsernameNotFoundException) {
//                                        responseMap.put("msg", "用户不存在 " + exception.getMessage());
//                                    } else if (exception instanceof BadCredentialsException) {
//                                        responseMap.put("msg", "密码错误 " + exception.getMessage());
//                                    } else if (exception instanceof LockedException) {
//                                        responseMap.put("msg", "用户锁定 " + exception.getMessage());
//                                    } else if (exception instanceof AccountExpiredException) {
//                                        responseMap.put("msg", "账户过期 " + exception.getMessage());
//                                    } else if (exception instanceof DisabledException) {
//                                        responseMap.put("msg", "账户不可用 " + exception.getMessage());
//                                    } else {
//                                        responseMap.put("msg", "系统错误 " + exception.getMessage());
//                                    }
//                                    //responseMap.put("msg", exception.getMessage());
//                                    return writeWith(webFilterExchange.getExchange(), responseMap);
//                                }
//                        )
////                .authenticationSuccessHandler((webFilterExchange, authentication) -> { //验证成功处理器(可以单独创建类处理)
////                    User user = (User) authentication.getPrincipal();
////                    userDetailsService.saveCacheUser(user);
////                    String token = new JwtTokenUtils().generateToken(user);
////                    Map<String, String> responseMap = new HashMap<>();
////                    responseMap.put("code", "success");
////                    responseMap.put("data", token);
////                    return writeWith(webFilterExchange.getExchange(), responseMap);
////                }
////                ).and().logout().logoutSuccessHandler((webFilterExchange, authentication) -> { //退出成功处理器(可以单独创建类处理)
////                    Map<String, String> responseMap = new HashMap<>();
////                    responseMap.put("code", "logout");
////                    responseMap.put("msg", "退出成功");
////                    return writeWith(webFilterExchange.getExchange(), responseMap);
////                }
////                ).and().exceptionHandling().accessDeniedHandler((exchange, denied) -> { // 无权限访问处理器(可以单独创建类处理)
////                    Map<String, String> responseMap = new HashMap<>();
////                    responseMap.put("code", "denied");
////                    responseMap.put("msg", "账户无权限访问");
////                    return writeWith(exchange, responseMap);
////                }
////                )
//        );
        httpSecurity.httpBasic().disable().formLogin().disable();
        httpSecurity.addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.FORM_LOGIN);
        httpSecurity
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
//                        .pathMatchers("/api/login").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/api/test").permitAll()
                        .pathMatchers("/api/user/service/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                        .and());
//                .pathMatchers("/**").permitAll()
        httpSecurity.csrf(csrfSpec -> csrfSpec
                .disable().headers().disable());

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


    @Bean
    public PasswordEncoder getPW() {
        return new BCryptPasswordEncoder();
//        {
//            /***
//             * 重写密码加密方式
//             * @param rawPassword 密码
//             * @return 使用MD5加盐加密
//             */
//            @Override
//            public String encode(CharSequence rawPassword) {
//                return Md5SaltUtils.generate(rawPassword.toString());
//            }
//
//            /***
//             * 密码验证
//             * @param rawPassword the raw password to encode and match
//             * @param encodedPassword the encoded password from storage to compare with
//             * @return
//             */
//            @Override
//            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                return Md5SaltUtils.verify(rawPassword.toString(), encodedPassword);
//            }
//        };
    }

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
//        filter.setSecurityContextRepository(securityContextRepository);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/login")
        );
        return filter;
    }
}
