package com.example.serevin.service;

import com.example.serevin.model.HeroesResponse.Hero;
import com.example.serevin.model.HeroesResponse.HeroesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Dota2HeroNameService {
    private final String apiKey;
    private final RestTemplate restTemplate;

    public Dota2HeroNameService(RestTemplate restTemplate, @Value("${steam.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String getHeroNameById(int heroId) {
        String url = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("language", "en_us");

        HeroesResponse response = restTemplate.getForObject(builder.toUriString(), HeroesResponse.class);
        return response.getResult().getHeroes().stream()
                .filter(hero -> hero.getId() == heroId)
                .findFirst()
                .map(Hero::getLocalizedName)
                .orElse("Unknown Hero");
    }
}
