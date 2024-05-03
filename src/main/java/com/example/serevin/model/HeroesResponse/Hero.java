package com.example.serevin.model.HeroesResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Hero {
    private int id;
    private String name;
    @JsonProperty("localized_name")
    private String localizedName;
}