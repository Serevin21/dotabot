package com.example.serevin.model.HeroesResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class HeroesResponse {
    private Result result;
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Result {
        private List<Hero> heroes;
    }
}
