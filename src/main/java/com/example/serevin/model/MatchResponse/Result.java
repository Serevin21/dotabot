package com.example.serevin.model.MatchResponse;

import java.util.List;

public record Result(
        int status,
        int num_results,
        int total_results,
        int results_remaining,
        List<Match> matches
) {
}
