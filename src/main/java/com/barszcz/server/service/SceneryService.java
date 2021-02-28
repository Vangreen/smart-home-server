package com.barszcz.server.service;
import com.barszcz.server.entity.SceneriesGetRequest;
import com.barszcz.server.entity.SceneryConfigurationModel;
import com.barszcz.server.entity.SceneryCreation;

import java.util.List;


public interface SceneryService {

    void addScenery(SceneryCreation sceneryCreation);

    List<SceneryConfigurationModel> getSceneries(SceneriesGetRequest sceneriesGetRequest);

    void changeSceneryStatus(int sceneryID, SceneryConfigurationModel sceneryConfigurationModel) throws Exception;
}
