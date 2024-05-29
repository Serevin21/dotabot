//package com.example.serevin.service;
//
//import com.example.serevin.model.MatchStats;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//
//@Service
//@CacheConfig(cacheNames = "playerStats")
//public class DotaStatsService {
//    private String baseUrl = "https://api.opendota.com/api";
//    private final RestTemplate restTemplate;
//    @Autowired
//    public DotaStatsService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @Cacheable(key = "{#accountId, #dateFrom, #dateTo}", unless = "#result == null") // Кэшируем результаты метода
//    public MatchStats getMatchStats(long accountId, int dateFrom, String dateTo) {
//        String url = String.format("%s/players/%s/wl?date=%s&date=%s", baseUrl, accountId, dateFrom, dateTo);
//        return restTemplate.getForObject(url, MatchStats.class);
//    }
//}