package org.facmc.gateway.service;

import org.facmc.gateway.pojo.User;


public interface UserService {
    public User searchUserByUsername(String username);
}
