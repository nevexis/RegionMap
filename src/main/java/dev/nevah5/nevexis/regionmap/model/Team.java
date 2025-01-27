package dev.nevah5.nevexis.regionmap.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Team {
    private UUID owner;
    private String name;
    private String displayName;
    private Color color;
}
