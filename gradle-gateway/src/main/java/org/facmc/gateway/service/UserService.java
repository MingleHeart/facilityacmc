package org.facmc.gateway.service;

import org.facmc.gateway.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserDetailsService {
    public User searchUserByUsername(String username);
}
