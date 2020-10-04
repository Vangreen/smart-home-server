package com.barszcz.server.dao;

import com.barszcz.server.entity.UserModel;
import com.barszcz.server.entity.UserSettingsRespondModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserSettingsRespondDao extends CrudRepository<UserSettingsRespondModel, Long> {
    public UserSettingsRespondModel findUserModelByLoginLike(String login);
    public Optional<UserSettingsRespondModel> findUserSettingsRespondModelByLoginLike(String login);
}
