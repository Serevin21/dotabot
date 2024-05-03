package com.example.serevin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerRank {
    private Integer rank_tier;
    private Integer leaderboard_rank;
}
