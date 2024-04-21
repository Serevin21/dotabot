package com.example.serevin.service;


import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Dota2MatchDetailService {
    private final RestTemplate restTemplate;
    private final String apiKey;

    public Dota2MatchDetailService(RestTemplate restTemplate, @Value("${steam.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public MatchDetailResponse getMatchDetails(long matchId) {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("match_id", matchId)
                .queryParam("key", apiKey)
                .build();
        return restTemplate.getForObject(uri.toUriString(), MatchDetailResponse.class);
    }
}