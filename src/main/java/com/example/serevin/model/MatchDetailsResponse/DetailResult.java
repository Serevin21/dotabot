package com.example.serevin.model.MatchDetailsResponse;

import java.util.List;

public record DetailResult(
        List<PlayerDetail> players,
        boolean radiant_win,
        int duration,
        long start_time,
        long match_id,
        long match_seq_num,
        int tower_status_radiant,
        int tower_status_dire,
        int barracks_status_radiant,
        int barracks_status_dire,
        List<PickBan> picksBans
) {
}
