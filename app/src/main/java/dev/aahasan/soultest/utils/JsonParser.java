package dev.aahasan.soultest.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.aahasan.soultest.models.Pin;

public class JsonParser {


    public List<Pin> parsePins(String json)
    {
        List<Pin> pins = null;

        try
        {
            JSONArray array = new JSONArray(json);

            if(array.length() > 0)
            {
                pins = new ArrayList<>();

                for(int i = 0; i < array.length(); i++)
                {
                    JSONObject object = array.getJSONObject(i);
                    JSONObject urlsObject = object.getJSONObject("urls");

                    Pin pin = new Pin();
                    pin.setUrl(urlsObject.getString("small"));

                    pins.add(pin);
                }
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }


        return pins;
    }
}