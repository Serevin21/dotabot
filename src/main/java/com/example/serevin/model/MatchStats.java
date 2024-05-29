package com.example.serevin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchStats {
    @JsonProperty("win")
    private int wins;
    @JsonProperty("lose")
    private int losses;
}
