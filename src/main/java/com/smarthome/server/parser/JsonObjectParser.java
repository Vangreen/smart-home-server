package com.smarthome.server.parser;

import com.smarthome.server.exception.ParseException;
import org.json.JSONObject;


public class JsonObjectParser {

    JSONObject jsonObject = new JSONObject();

    public JSONObject parse(String body) throws ParseException {
        try {
            jsonObject = new JSONObject(body);
        } catch (Exception err) {
            throw new ParseException(err.toString());
        }

        if (jsonObject != null) {
            return jsonObject;
        } else {
            throw new ParseException("Empty body");
        }
    }
}
