package com.example.serevin.service.impl;

import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.model.MatchResponse.MatchResponse;
import com.example.serevin.model.MatchResponse.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class PlayerMatchesServiceImpl {
    private final RestTemplate restTemplate;
    private final String apiKey;

    public PlayerMatchesServiceImpl(RestTemplate restTemplate, @Value("${steam.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public List<Match> getPlayerMatches(long accountId) {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/v1/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("account_id", accountId);

        MatchResponse response = null;
        try {
            response = restTemplate.getForObject(builder.toUriString(), MatchResponse.class);
        } catch (Exception e) {
            log.error("Error when getting player matches from the API by account ID: {}", accountId, e);
        }
        return response != null ? response.result().matches() : Collections.emptyList();
    }
    public List<Match> getPlayerRankedMatches(long accountId){
        List<Match> playerMatches = getPlayerMatches(accountId);
        List<Match> playerRankedMatches = new ArrayList<>();
        for (Match match : playerMatches) {
            if (isRankedMatch(match)) {
                playerRankedMatches.add(match);
            }
        }
        return playerRankedMatches;
    }

    public Result getStatusPageAPI(long accountId) {
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("account_id", accountId);
        try {
            MatchResponse response = restTemplate.getForObject(builder.toUriString(), MatchResponse.class);
            if(response != null){
                return response.result();
            } else {
                throw new NoSuchElementException("Response from API is null for matchId(pageCheck)");
            }
        } catch (Exception e) {
            log.error("Error test page check when getting player matches from the API by account ID: {}", accountId, e);
            throw new IllegalStateException("Error occurred while checking player matches (pageCheck)");
        }
    }
    private boolean isRankedMatch(Match match) {
        int lobby_type = match.lobby_type();
        if (lobby_type >= 7 && lobby_type <=9){
            return true;
        } else {
            return false;
        }
    }
}