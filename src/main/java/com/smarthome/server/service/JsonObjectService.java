package com.smarthome.server.service;

import com.smarthome.server.entity.DeviceConfigurationModel;
import com.smarthome.server.entity.Hsv;
import com.smarthome.server.exception.JsonObjectException;
import com.smarthome.server.exception.ParseException;
import org.json.JSONObject;

public interface JsonObjectService {

    String getString(JSONObject jsonObject, String value) throws JsonObjectException;

    int getInt(JSONObject jsonObject, String value) throws JsonObjectException;

    JSONObject parse(String body) throws ParseException, JsonObjectException;

    DeviceConfigurationModel bodyToDevice(JSONObject body) throws JsonObjectException;

    Hsv bodyToHsv(JSONObject body) throws JsonObjectException;
}
