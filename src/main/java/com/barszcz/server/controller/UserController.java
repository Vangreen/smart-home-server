package com.barszcz.server.controller;

import com.barszcz.server.dao.UserDao;
import com.barszcz.server.dao.UserSettingsRespondDao;
import com.barszcz.server.entity.Respond;
import com.barszcz.server.entity.UserModel;
import com.barszcz.server.entity.UserSettingsRespondModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserDao userDao;
    private final UserSettingsRespondDao userSettingsRespondDao;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping(path = "/login")
    public Respond login(@RequestParam String login, String password) {
        Respond respond = new Respond();
        UserModel userModel = userDao.findUserModelByLoginLike(login);
        if (userModel != null) {
            if (userModel.getPassword().equals(password)) {
                respond.setRespond("logged");
                respond.setAdmin(userModel.getAdmin());
                return respond;
            } else {
                respond.setRespond("wrong password");
                return respond;
            }
        } else {
            respond.setRespond("user doesnt exist");
            return respond;
        }
    }

    @GetMapping(path = "/register")
    public Respond register(@RequestParam String login, String password) {
        Respond respond = new Respond();
        if (userDao.findUserModelByLoginLike(login) == null) {
            UserModel userModel = new UserModel();
            userModel.setLogin(login);
            userModel.setPassword(password);
            userModel.setAdmin(false);
            userDao.save(userModel);
            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
            respond.setRespond("registered");
            respond.setAdmin(false);
            return respond;
        } else {
            respond.setRespond("user exists");
            return respond;
        }
    }

    @GetMapping(path = "/usersList")
    public List<UserModel> listUsers() {
        return (List<UserModel>) userDao.findAll();
    }

    @GetMapping(path = "/getOnlyUsers")
    public List<UserSettingsRespondModel> listOnlyUsers() {
        return (List<UserSettingsRespondModel>) userSettingsRespondDao.findAll();
    }

    @MessageMapping("/deleteUser/{userName}")
    public void deleteUser(@DestinationVariable("userName") String username) {
        userDao.deleteUserModelByLoginLike(username);
        simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
    }

    @MessageMapping("/changeUser/{userName}/{admin}")
    public void changeUserAdminState(@DestinationVariable("userName") String username, @DestinationVariable("admin") Boolean admin) throws Exception {
        userSettingsRespondDao.findUserSettingsRespondModelByLoginLike(username).map(userSettingsRespondModel -> {
            userSettingsRespondModel.setAdmin(admin);
            userSettingsRespondDao.save(userSettingsRespondModel);
            //simpMessagingTemplate.convertAndSend("/user/changeUser/"+username, userSettingsRespondModel);
            simpMessagingTemplate.convertAndSend("/user/changeUser/change", userSettingsRespondDao.findAll());
            return true;
        }).orElseThrow(
                Exception::new
        );
    }

}
