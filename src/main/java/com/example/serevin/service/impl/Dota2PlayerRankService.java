package com.example.serevin.service.impl;

import com.example.serevin.model.PlayerRank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Service
public class Dota2PlayerRankService {
    private final RestTemplate restTemplate;

    @Autowired
    public Dota2PlayerRankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRankName(long accountId) {
        String url = "https://api.opendota.com/api/players/" + accountId;
        try {
            PlayerRank profile = restTemplate.getForObject(url, PlayerRank.class);
            return mapRankTierToName(profile.getRank_tier(), profile.getLeaderboard_rank());
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Player profile not found for accountId: {}", accountId, e);
            throw new RuntimeException("Player profile not found");
        } catch (HttpServerErrorException e) {
            log.error("Server error occurred while fetching player profile for accountId: {}", accountId, e);
            throw new RuntimeException("Error occurred while fetching player profile");
        } catch (Exception e) {
            log.error("Error occurred while fetching player profile for accountId: {}", accountId, e);
            throw new RuntimeException("Unexpected error occurred while fetching player profile");
        }
    }

    private String mapRankTierToName(Integer rankTier, Integer leaderboardRank) {
        if (rankTier == null) return "Unknown Rank";
        int rank = rankTier / 10;
        int stars = rankTier % 10;
        switch (rank) {
            case 1: return "Herald " + stars;
            case 2: return "Guardian " + stars;
            case 3: return "Crusader " + stars;
            case 4: return "Archon " + stars;
            case 5: return "Legend " + stars;
            case 6: return "Ancient " + stars;
            case 7: return "Divine " + stars;
            case 8:
                return leaderboardRank != null ? "Immortal (Rank " + leaderboardRank + ")" : "Immortal";
            default: return "Unknown Rank";
        }
    }
}
