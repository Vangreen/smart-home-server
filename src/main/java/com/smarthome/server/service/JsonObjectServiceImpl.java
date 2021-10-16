package com.smarthome.server.service;

import com.smarthome.server.entity.DeviceConfigurationModel;
import com.smarthome.server.entity.Hsv;
import com.smarthome.server.exception.JsonObjectException;
import com.smarthome.server.parser.JsonObjectParser;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JsonObjectServiceImpl implements JsonObjectService {

    private static final String SERIAL_VALUE = "serial";
    private static final String DEVICE_NAME_VALUE = "deviceName";
    private static final String ROOM_ID_VALUE = "roomID";
    private static final String DEVICE_TYPE_VALUE = "deviceType";
    private static final String HUE = "hue";
    private static final String SATURATION = "saturation";
    private static final String BRIGHTNESS = "brightness";

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


    public JSONObject parse(String body) throws JsonObjectException {
        try {
            return jsonParser.parse(body);
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }
    }

    public DeviceConfigurationModel bodyToDevice(JSONObject body) throws JsonObjectException {
        try {
            DeviceConfigurationModel deviceConfigurationModel = new DeviceConfigurationModel();
            deviceConfigurationModel.setSerial(getInt(body, SERIAL_VALUE));
            deviceConfigurationModel.setDeviceName(getString(body, DEVICE_NAME_VALUE));
            deviceConfigurationModel.setRoomID(getInt(body, ROOM_ID_VALUE));
            deviceConfigurationModel.setDeviceType(getString(body, DEVICE_TYPE_VALUE));
            return deviceConfigurationModel;
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }

    }


    public Hsv bodyToHsv(JSONObject body) throws JsonObjectException {
        try {
            return Hsv.builder()
                    .hue(getInt(body, HUE))
                    .saturation(getInt(body, SATURATION))
                    .bright(getInt(body, BRIGHTNESS))
                    .build();
        } catch (Exception e) {
            throw new JsonObjectException(e.toString());
        }

    }
}
