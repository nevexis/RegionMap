package dev.nevah5.nevexis.regionmap.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.List;

@Data
public class Color {
    private String name;
    private int red;
    private int green;
    private int blue;
    private float alpha;

    public de.bluecolored.bluemap.api.math.Color getColor() {
        return new de.bluecolored.bluemap.api.math.Color(red, green, blue, alpha);
    }

    public de.bluecolored.bluemap.api.math.Color getLineColor() {
        return new de.bluecolored.bluemap.api.math.Color(red, green, blue, alpha + 0.3f);
    }


    public static List<Color> getDefaultConfig() {
        String jsonString = "[{\"name\":\"red\",\"red\":255,\"green\":0,\"blue\":0,\"alpha\":0.1},{\"name\":\"green\",\"red\":0,\"green\":255,\"blue\":0,\"alpha\":0.1},{\"name\":\"blue\",\"red\":0,\"green\":0,\"blue\":255,\"alpha\":0.1},{\"name\":\"white\",\"red\":255,\"green\":255,\"blue\":255,\"alpha\":0.1},{\"name\":\"black\",\"red\":0,\"green\":0,\"blue\":0,\"alpha\":0.1},{\"name\":\"yellow\",\"red\":255,\"green\":255,\"blue\":0,\"alpha\":0.1},{\"name\":\"cyan\",\"red\":0,\"green\":255,\"blue\":255,\"alpha\":0.1},{\"name\":\"magenta\",\"red\":255,\"green\":0,\"blue\":255,\"alpha\":0.1}]";

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Color>>() {}.getType();
        return gson.fromJson(jsonString, listType);
    }
}
