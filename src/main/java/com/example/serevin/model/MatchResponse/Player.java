package com.example.serevin.model.MatchResponse;

public record Player(
        long account_id,
        int player_slot,
        int team_number,
        int team_slot,
        int hero_id
) {
}
