package com.example.sport_app_client.retrofit.deserializers;

import com.example.sport_app_client.model.group.BasketballGroup;
import com.example.sport_app_client.model.member.BasketballMember;
import com.example.sport_app_client.model.member.FootballMember;
import com.example.sport_app_client.model.member.Member;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MemberDeserializer implements JsonDeserializer<Member> {
    @Override
    public Member deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement sport = jsonObject.get("sport");

        if (sport != null) {
            switch (sport.getAsString()) {
                case "FOOTBALL":
                    return context.deserialize(jsonObject, FootballMember.class);
                case "BASKETBALL":
                    return context.deserialize(jsonObject, BasketballMember.class);
            }
        }

        return null;
    }
}
