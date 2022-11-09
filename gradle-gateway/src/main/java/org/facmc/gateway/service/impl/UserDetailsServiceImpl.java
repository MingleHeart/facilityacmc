package org.facmc.gateway.service.impl;


import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.mapper.UserDetailsMapper;
import org.facmc.gateway.pojo.CustomUserDetails;
import org.facmc.gateway.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService, org.facmc.gateway.service.UserDetailsService {

    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDetailsMapper.searchUserByUsername(username);
        if (user.getUserName() == null) {
            throw new UsernameNotFoundException("未找到用户");
        }
        if (user.getUserPermission() == 1) {
            return new CustomUserDetails(user.getId(), user.getUserName(), user.getIsEnable(), user.getEncryption(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        } else if (user.getUserPermission() == 2) {
            return new CustomUserDetails(user.getId(), user.getUserName(), user.getIsEnable(), user.getEncryption(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("super-admin"));
        }
        return new CustomUserDetails(user.getId(), user.getUserName(), user.getIsEnable(), user.getEncryption(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("normal"));
    }

    @Override
    public User searchUserByUsername(String username) {
        User user = null;
        try {
            user = userDetailsMapper.searchUserByUsername(username);
        } catch (Exception e) {
            log.error(e.toString(), username);
        }
        return user;
    }
}
