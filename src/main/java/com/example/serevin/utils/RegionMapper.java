package com.example.serevin.utils;

import java.util.HashMap;
import java.util.Map;


public class RegionMapper {
    private static final Map<Integer, String> regionMap = new HashMap<>();

    static {
        regionMap.put(0, "UNSPECIFIED");
        regionMap.put(1, "US WEST");
        regionMap.put(2, "US EAST");
        regionMap.put(3, "EUROPE");
        regionMap.put(5, "SINGAPORE");
        regionMap.put(6, "DUBAI");
        regionMap.put(7, "AUSTRALIA");
        regionMap.put(8, "STOCKHOLM");
        regionMap.put(9, "AUSTRIA");
        regionMap.put(10, "BRAZIL");
        regionMap.put(11, "SOUTH AFRICA");
        regionMap.put(12, "PW TELECOM SHANGHAI");
        regionMap.put(13, "PW UNICOM");
        regionMap.put(14, "CHILE");
        regionMap.put(15, "PERU");
        regionMap.put(16, "INDIA");
        regionMap.put(17, "PW TELECOM GUANGZHOU");
        regionMap.put(18, "PW TELECOM ZHEJIANG 2");
        regionMap.put(19, "JAPAN");
        regionMap.put(20, "PW TELECOM WUHAN");
        regionMap.put(25, "PW UNICOM TIANJIN");
        regionMap.put(37, "ARGENTINA");
    }

    public static String getRegion(int regionCode) {
        return regionMap.getOrDefault(regionCode, "UNKNOWN REGION");
    }
}
