package com.example.serevin.service;

import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;

public interface MatchDetailService {
    MatchDetailResponse getMatchDetails(long matchId);
}
