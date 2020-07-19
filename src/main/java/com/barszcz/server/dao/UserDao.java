package com.barszcz.server.dao;

import com.barszcz.server.entity.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao extends CrudRepository<UserModel, Long> {
    public UserModel findUserModelByLoginLike(String login);
}
