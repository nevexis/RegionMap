package dev.nevah5.nevexis.regionmap.api;

import de.bluecolored.bluemap.api.BlueMapAPI;

public class BlueMapApiImpl implements BlueMapApi {
    private BlueMapAPI api;

    public BlueMapApiImpl() {
        BlueMapAPI.onEnable(api ->
                this.api = api
        );
    }
}
