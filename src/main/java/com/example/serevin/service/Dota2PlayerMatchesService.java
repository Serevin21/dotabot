package com.example.serevin.service;

import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.model.MatchResponse.MatchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class Dota2PlayerMatchesService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public Dota2PlayerMatchesService(RestTemplate restTemplate, @Value("${steam.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public List<Match> getPlayerMatches(long accountId) {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("account_id", accountId);

        MatchResponse response = restTemplate.getForObject(builder.toUriString(), MatchResponse.class);
        return response.result().matches();
    }
}