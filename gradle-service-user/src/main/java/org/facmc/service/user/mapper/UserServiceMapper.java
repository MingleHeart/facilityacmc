package org.facmc.service.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.facmc.service.user.pojo.User;

import java.util.List;

@Mapper
public interface UserServiceMapper {
    public List<User> listForSuperUser();

    public List<User> listByBelond(long id);

    public void addUser(User user);

    public void addUserPermission(User user);

    public int countByUserNameInt(String userName);
}
