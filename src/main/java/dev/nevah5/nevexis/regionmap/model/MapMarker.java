package dev.nevah5.nevexis.regionmap.model;

import com.flowpowered.math.vector.Vector2d;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@AllArgsConstructor
@Builder
public class MapMarker {
    private Vector2d position;
    private String label;
    private String icon;
    private UUID owner;
    private String ownerName;
    @Builder.Default
    private int createdTimestamp = (int) (System.currentTimeMillis() / 1000);
}
