package dev.nevah5.nevexis.regionmap.model;

import com.flowpowered.math.vector.Vector2d;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class MapMarker {
    private Vector2d position;
    private String label;
    private String icon;
}
