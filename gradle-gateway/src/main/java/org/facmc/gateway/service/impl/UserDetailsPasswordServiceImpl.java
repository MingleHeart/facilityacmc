package org.facmc.gateway.service.impl;

import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDetailsPasswordServiceImpl implements ReactiveUserDetailsPasswordService {
    private final Map<String, UserDetails> users;

    public UserDetailsPasswordServiceImpl(Map<String, UserDetails> users) {
        this.users = users;
    }

    public UserDetailsPasswordServiceImpl(UserDetails... users) {
        this(Arrays.asList(users));
    }

    public UserDetailsPasswordServiceImpl(Collection<UserDetails> users) {
        Assert.notEmpty(users, "users cannot be null or empty");
        this.users = new ConcurrentHashMap<>();
        for (UserDetails user : users) {
            this.users.put(getKey(user.getUsername()), user);
        }
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return Mono.just(user)
                .map(u ->
                        User.withUserDetails(u)
                                .password(newPassword)
                                .build()
                )
                .doOnNext(u -> {
                    String key = getKey(user.getUsername());
                    this.users.put(key, u);
                });
    }

    private String getKey(String username) {
        return username.toLowerCase();
    }

}
