package com.barszcz.server.dao;

import com.barszcz.server.entity.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface UserDao extends CrudRepository<UserModel, Long> {
    UserModel findUserModelByLoginLike(String login);

    @Transactional
    void deleteUserModelByLoginLike(String login);
}
