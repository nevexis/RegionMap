package dev.nevah5.nevexis.regionmap.api;

public class RegionMapApiImpl implements RegionMapApi {
    private final WorldGuardApi worldGuardApi = new WorldGuardApiImpl();
    private final BlueMapApi blueMapApi = new BlueMapApiImpl();
}
