//package com.barszcz.server.dao;
//
//import com.barszcz.server.entity.UserModel;
//import com.barszcz.server.entity.UserSettingsRespondModel;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.Optional;
//
//
//@Repository
//public interface UserDao extends CrudRepository<UserModel, Long> {
//    public UserModel findUserModelByLoginLike(String login);
//
//    @Transactional
//    public void deleteUserModelByLoginLike(String login);
//}
