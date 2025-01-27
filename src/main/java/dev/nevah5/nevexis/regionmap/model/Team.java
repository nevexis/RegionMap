package dev.nevah5.nevexis.regionmap.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class Team {
    private UUID teamId;
    private UUID owner;
    private String name;
    private String displayName;
    private Color color;

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Team team;

        public Builder(Team team) {
            this.team = team;
        }

        public Builder() {
            this.team = new Team();
            this.team.teamId = UUID.randomUUID();
        }

        public Builder teamId(UUID teamId) {
            this.team.teamId = teamId;
            return this;
        }

        public Builder owner(UUID owner) {
            this.team.owner = owner;
            return this;
        }

        public Builder name(String name) {
            this.team.name = name;
            return this;
        }

        public Builder displayName(String displayName) {
            this.team.displayName = displayName;
            return this;
        }

        public Builder color(Color color) {
            this.team.color = color;
            return this;
        }

        public Team build() {
            if (team.color == null)
                throw new IllegalArgumentException("Invalid color provided!");
            if (team.owner == null || team.name == null || team.displayName == null)
                throw new IllegalArgumentException("Team is missing required fields!");
            if (team.displayName.length() > 24)
                throw new IllegalArgumentException("Display name cannot exceed 24 characters!");
            if (!team.displayName.matches("[a-zA-Z0-9-_!?;]+"))
                throw new IllegalArgumentException("Display name  contains invalid characters!");
            if (team.displayName.length() < 5)
                throw new IllegalArgumentException("Display name must be at least 5 characters long!");
            if (!team.displayName.matches(".*[a-zA-Z].*"))
                throw new IllegalArgumentException("String must contain at least one letter.");

            return team;
        }
    }
}
