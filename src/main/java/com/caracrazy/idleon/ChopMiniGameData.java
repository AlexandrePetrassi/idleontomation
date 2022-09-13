package com.caracrazy.idleon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChopMiniGameData {

    private String appName;
    private String frameReference;
    private String cursorReference;
    private int forceExitKey;
    private List<String> colors;

    public String getAppName() {
        return appName;
    }

    public String getFrameReference() {
        return frameReference;
    }

    public String getCursorReference() {
        return cursorReference;
    }

    public int getForceExitKey() {
        return forceExitKey;
    }

    @JsonProperty("targetColors")
    private List<String> getColors() {
        return colors;
    }

    private List<Color> targetColors = new ArrayList<>();

    @JsonIgnore
    public List<Color> getTargetColors() {
        if (targetColors.isEmpty() && !getColors().isEmpty()) {
            targetColors = getColors().stream()
                    .map(it -> it.split(","))
                    .map(it -> new Color(Integer.parseInt(it[0].trim()), Integer.parseInt(it[1].trim()), Integer.parseInt(it[2].trim())))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return targetColors;
    }
}
