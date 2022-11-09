package org.facmc.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.facmc.gateway.pojo.User;

@Mapper
public interface UserDetailsMapper extends BaseMapper<User> {
    /***
     * 登录验证，查找用户信息方法
     * @param username 用户名
     * @return User MAP:userSearch
     */
    public User searchUserByUsername(String username);
}
