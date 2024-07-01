package com.example.serevin.service;

import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;

import java.util.Optional;

public interface MatchDetailService {
    Optional<MatchDetailResponse> getMatchDetails(long matchId);
}
