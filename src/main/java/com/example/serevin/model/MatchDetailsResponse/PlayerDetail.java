package com.example.serevin.model.MatchDetailsResponse;

import java.util.List;

public record PlayerDetail(
        long account_id,
        int hero_id,
        int item_0,
        int item_1,
        int item_2,
        int item_3,
        int item_4,
        int item_5,
        int item_neutral,
        int kills,
        int deaths,
        int assists,
        int last_hits,
        int denies,
        int gold_per_min,
        int xp_per_min,
        int level,
        int net_worth,
        int hero_damage,
        int tower_damage,
        int hero_healing,
        List<AbilityUpgrade> abilityUpgrades
) {
}
