package com.barszcz.server.service;

import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.entity.Hsv;
import com.barszcz.server.exception.JsonObjectException;
import com.barszcz.server.exception.ParseException;
import org.json.JSONObject;

public interface JsonObjectService {

    String getString(JSONObject jsonObject, String value) throws JsonObjectException;

    int getInt(JSONObject jsonObject, String value) throws JsonObjectException;

    JSONObject parse(String body) throws ParseException, JsonObjectException;

    DeviceConfigurationModel bodyToDevice(JSONObject body) throws JsonObjectException;

    Hsv bodyToHsv(JSONObject body) throws JsonObjectException;
}
