package com.example.serevin.service;

import com.example.serevin.model.PlayerRank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class PlayerRankService {
    private final RestTemplate restTemplate;

    @Autowired
    public PlayerRankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRankName(long accountId) {
        String url = "https://api.opendota.com/api/players/" + accountId;
        PlayerRank profile = restTemplate.getForObject(url, PlayerRank.class);
        return mapRankTierToName(profile.getRank_tier(), profile.getLeaderboard_rank());
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
