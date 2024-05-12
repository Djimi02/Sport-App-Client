package com.example.sport_app_client.retrofit.deserializers;

import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.group.FootballGroup;
import com.example.sport_app_client.model.group.Group;
import com.example.sport_app_client.model.member.FootballMember;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GroupDeserializer implements JsonDeserializer<Group> {
    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement sport = jsonObject.get("sport");

        if (sport != null) {
            switch (sport.getAsString()) {
                case "FOOTBALL":
                    return context.deserialize(jsonObject, FootballGroup.class);
                case "BASKETBALL":
                    return context.deserialize(jsonObject, BasketballGroup.class);
            }
        }

        return null;
    }
}
