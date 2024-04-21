package com.example.serevin.model.MatchResponse;

import java.util.List;

public record Match(
        long match_id,
        long match_seq_num,
        int start_time,
        int lobby_type,
        int radiant_team_id,
        int dire_team_id,
        List<Player> players
) {
}
