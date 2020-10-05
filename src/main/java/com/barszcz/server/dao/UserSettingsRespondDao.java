package com.barszcz.server.dao;

import com.barszcz.server.entity.UserSettingsRespondModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserSettingsRespondDao extends CrudRepository<UserSettingsRespondModel, Long> {
    UserSettingsRespondModel findUserModelByLoginLike(String login);

    Optional<UserSettingsRespondModel> findUserSettingsRespondModelByLoginLike(String login);
}
