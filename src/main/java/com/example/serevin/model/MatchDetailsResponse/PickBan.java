package com.example.serevin.model.MatchDetailsResponse;

public record PickBan(
        boolean is_pick,
        int hero_id,
        int team,
        int order
) {
}
