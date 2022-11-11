package org.facmc.service.user.service;

import org.facmc.service.user.pojo.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;

@Service
public interface UserService {
    /**
     * @return 获取除superUser外的用户
     */
    public List<User> listForSuperUser() throws Exception;

    /**
     * @param json 存在管理员id
     * @return 返回与id相同的belongid用户
     */
    public List<User> ListByBelond(String json) throws Exception;

    /**
     * 增加用户之后自动检查用户数量，确保添加成功
     *
     * @param json
     * @return
     */
    public boolean addUser(String json, Mono<Principal> principal) throws Exception;
}
