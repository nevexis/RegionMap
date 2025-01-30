package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Chunk;
import dev.nevah5.nevexis.regionmap.model.ClaimedRegion;
import dev.nevah5.nevexis.regionmap.model.RegionGroup;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegionMapApiImpl implements RegionMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    private final WorldGuardApi worldGuardApi = new WorldGuardApiImpl();
    private final BlueMapApi blueMapApi = new BlueMapApiImpl();

    @Override
    public int claim(final Entity player, final String teamName, final ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendFeedback(() -> Text.literal("Only players can claim regions!"), false);
            return 0;
        }

        Chunk chunk = Chunk.fromPlayerPos(player.getPos());
        Optional<ClaimedRegion> region = RegionMapConfig.regions.stream()
                .filter(claimedRegion -> claimedRegion.toChunk().equals(chunk))
                .findFirst();

        if (region.isPresent()) {
            source.sendFeedback(() -> Text.literal("This chunk is already claimed!"), false);
            return 0;
        }

        Team team = RegionMapConfig.teams.stream().filter(t -> t.getName().equalsIgnoreCase(teamName)).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Team " + teamName + " doesn't exist!"), false);
            return 0;
        }
        if (!team.getMembers().contains(Objects.requireNonNull(source.getEntity()).getUuid()) && !team.getOwner().equals(source.getEntity().getUuid()) && !source.hasPermissionLevel(2)) {
            source.sendFeedback(() -> Text.literal("You are not a member of team " + team.getName()), false);
            return 0;
        }

        try {
            blueMapApi.addRegion(player, team, source);
        } catch (Exception e) {
            source.sendFeedback(() -> Text.literal("Failed to claim region: " + e.getMessage()), false);
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Region claimed!"), false);
        return 1;
    }

    @Override
    public int merge(Entity player, String name, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendFeedback(() -> Text.literal("Only players can merge claimed regions!"), false);
            return 0;
        }

        Chunk chunk = Chunk.fromPlayerPos(player.getPos());
        Optional<ClaimedRegion> region = RegionMapConfig.regions.stream()
                .filter(claimedRegion -> claimedRegion.toChunk().equals(chunk))
                .findFirst();

        if (region.isEmpty()) {
            source.sendFeedback(() -> Text.literal("You have to be in a claimed chunk."), false);
            return 0;
        }

        Team team = RegionMapConfig.teams.stream().filter(t -> t.getTeamId().equals(region.get().getTeam())).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Failed to load team for region: " + region.get().getTeam()), false);
            return 0;
        }

        if (!team.getOwner().equals(source.getEntity().getUuid()) && team.getMembers().contains(source.getEntity().getUuid()) && !source.hasPermissionLevel(2)){
            source.sendFeedback(() -> Text.literal("You have to be a member of the team " + team.getName() + " to merge!"), false);
            return 0;
        }

        if (region.get().getRegionGroup() != null) {
            source.sendFeedback(() -> Text.literal("You cannot merge already merged regions!"), false);
            return 0;
        }

        // find all adjacent regions
        List<ClaimedRegion> teamRegions = RegionMapConfig.regions.stream()
                .filter(r -> r.getTeam().equals(team.getTeamId()))
                .toList();
        List<ClaimedRegion> adjacentRegions = new ArrayList<>();
        adjacentRegions.add(region.get());
        addAdjacentRegionsRecursive(region.get(), adjacentRegions, teamRegions);

        // merge regions
        RegionGroup regionGroup = RegionGroup.builder()
                .name(name) // TODO: implement name validation
                .build();
        adjacentRegions.forEach(r -> regionGroup.addRegion(r.getId())); // add all region ids to region group first
        for (ClaimedRegion adjacentRegion : adjacentRegions) {
            adjacentRegion.setRegionGroup(regionGroup);
            RegionMapConfig.writeConfigFile(BlueMapApiImpl.REGION_DIRECTORY + adjacentRegion.getRegionId() + ".json", adjacentRegion);
        }

        BlueMapApiImpl.reloadTeamMarkers(team);
        source.sendFeedback(() -> Text.literal("Regions merged!"), false);
        return 1;
    }

    private void addAdjacentRegionsRecursive(final ClaimedRegion region, final List<ClaimedRegion> regions, final List<ClaimedRegion> regionsToCheck) {
        List<ClaimedRegion> adjacentRegions = new ArrayList<>();
        // Check north
        regionsToCheck.stream()
                .filter(r -> r.toChunk().getChunkX() == region.toChunk().getChunkX() && r.toChunk().getChunkZ() == region.toChunk().getChunkZ() - 1)
                .findFirst().ifPresent(adjacentRegions::add);
        // Check east
        regionsToCheck.stream()
                .filter(r -> r.toChunk().getChunkX() == region.toChunk().getChunkX() + 1 && r.toChunk().getChunkZ() == region.toChunk().getChunkZ())
                .findFirst().ifPresent(adjacentRegions::add);
        // Check south
        regionsToCheck.stream()
                .filter(r -> r.toChunk().getChunkX() == region.toChunk().getChunkX() && r.toChunk().getChunkZ() == region.toChunk().getChunkZ() + 1)
                .findFirst().ifPresent(adjacentRegions::add);
        // Check west
        regionsToCheck.stream()
                .filter(r -> r.toChunk().getChunkX() == region.toChunk().getChunkX() - 1 && r.toChunk().getChunkZ() == region.toChunk().getChunkZ())
                .findFirst().ifPresent(adjacentRegions::add);

        for (ClaimedRegion adjacentRegion : adjacentRegions) {
            if(regions.contains(adjacentRegion)) {
                return;
            }
            regions.add(adjacentRegion);
            addAdjacentRegionsRecursive(adjacentRegion, regions, regionsToCheck);
        }
    }

    @Override
    public int remove(Entity player, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendFeedback(() -> Text.literal("Only players can remove claimed regions!"), false);
            return 0;
        }

        Chunk chunk = Chunk.fromPlayerPos(player.getPos());
        Optional<ClaimedRegion> region = RegionMapConfig.regions.stream()
                .filter(claimedRegion -> claimedRegion.toChunk().equals(chunk))
                .findFirst();

        if (region.isEmpty()) {
            source.sendFeedback(() -> Text.literal("You have to be in a claimed chunk."), false);
            return 0;
        }
        Team team = RegionMapConfig.teams.stream().filter(t -> t.getTeamId().equals(region.get().getTeam())).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Failed to load team for region: " + region.get().getTeam()), false);
            return 0;
        }
        if (!team.getOwner().equals(source.getEntity().getUuid()) && !source.hasPermissionLevel(2)) {
            source.sendFeedback(() -> Text.literal("You are not the owner of this region!"), false);
            return 0;
        }
        region.ifPresent(r -> {
            RegionMapConfig.regions.remove(r);
            RegionMapConfig.deleteConfigFile(BlueMapApiImpl.REGION_DIRECTORY + r.getRegionId() + ".json");
            source.sendFeedback(() -> Text.literal("Region removed!"), false);
            BlueMapApiImpl.reloadTeamMarkers(team);
        });
        return 1;
    }
}
