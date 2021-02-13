package com.barszcz.server.service;

import com.barszcz.server.entity.DeviceConfigurationModel;
import com.barszcz.server.exception.JsonObjectException;
import com.barszcz.server.exception.ParseException;
import com.barszcz.server.parser.JsonObjectParser;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JsonObjectServiceImpl implements JsonObjectService {

    private static final String SERIAL_VALUE = "serial";
    private static final String DEVICE_NAME_VALUE = "serial";
    private static final String ROOM_ID_VALUE = "serial";
    private static final String DEVICE_TYPE_VALUE = "serial";

    private JsonObjectParser jsonParser = new JsonObjectParser();

    public String getString(JSONObject jsonObject, String value) throws JsonObjectException {
        try {
            return jsonObject.getString(value);
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }
    }

    public int getInt(JSONObject jsonObject, String value) throws JsonObjectException {
        try {
            return jsonObject.getInt(value);
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }
    }


    public JSONObject parse(String body) throws ParseException {
        return jsonParser.parse(body);
    }

    public DeviceConfigurationModel bodyToDevice(JSONObject body) throws JsonObjectException {
        DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
        deviceConfigurationModel.setSerial(getInt(body, SERIAL_VALUE));
        deviceConfigurationModel.setDeviceName(getString(body, DEVICE_NAME_VALUE));
        deviceConfigurationModel.setRoomID(getInt(body, ROOM_ID_VALUE));
        deviceConfigurationModel.setDeviceType(getString(body, DEVICE_TYPE_VALUE));
        return deviceConfigurationModel;

    }
}
