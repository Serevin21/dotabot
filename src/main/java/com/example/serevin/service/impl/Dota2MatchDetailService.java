package com.example.serevin.service.impl;

import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class Dota2MatchDetailService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://api.opendota.com/api/matches/";
    public Dota2MatchDetailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public MatchDetailResponse getMatchDetails(long matchId) {
        String url = baseUrl + matchId;
        try {
            MatchDetailResponse matchDetail = restTemplate.getForObject(url, MatchDetailResponse.class);
            if (matchDetail != null) {
                return matchDetail;
            } else {
                throw new NoSuchElementException("Response from API is null for matchId: " + matchId);
            }
        } catch (Exception e){
            log.error("Error when receiving matchDetail from API for matchId: {}", matchId, e);
            throw new NoSuchElementException("Failed to get match detail for matchId: " + matchId);
        }
    }
}