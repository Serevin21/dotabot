package com.example.serevin.service;


import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.model.MatchResponse.Result;

import java.util.List;

public interface PlayerMatchesService {
    List<Match> getPlayerMatches(long accountId);
    List<Match> getPlayerRankedMatches(long accountId);
    Result pageCheck(long accountId);
}
