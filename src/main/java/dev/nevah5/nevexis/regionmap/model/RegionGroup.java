package dev.nevah5.nevexis.regionmap.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class RegionGroup {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private final List<UUID> regions = new ArrayList<>();

    public List<UUID> getRegions() {
        return regions;
    }

    public void addRegion(UUID region) {
        regions.add(region);
    }

    public void removeRegion(UUID region) {
        regions.remove(region);
    }
}
