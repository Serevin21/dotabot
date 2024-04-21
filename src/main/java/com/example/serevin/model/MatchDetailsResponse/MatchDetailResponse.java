package com.example.serevin.model.MatchDetailsResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchDetailResponse(@JsonProperty("result")DetailResult detailResult) {}

