package com.example.serevin.model.MatchDetailsResponse;

import java.util.List;
public record MatchDetailResponse(
        List<PlayerDetail> players,
        long start_time,
        boolean radiant_win,
        long match_id,
        long match_seq_num,
        int game_mode,
        int duration,
        int path,
        int region
) {
}

