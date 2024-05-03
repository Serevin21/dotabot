package com.example.serevin.service;

import com.example.serevin.model.HeroesResponse.HeroesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Dota2HeroImageService {
    private final String apiKey;
    private final RestTemplate restTemplate;
    public Dota2HeroImageService(RestTemplate restTemplate, @Value("${steam.api.key}")String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String getHeroImageUrlById(int heroId) {
        String url = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("language", "en_us");

        HeroesResponse response = restTemplate.getForObject(builder.toUriString(), HeroesResponse.class);
        return response.getResult().getHeroes().stream()
                .filter(hero -> hero.getId() == heroId)
                .findFirst()
                .map(hero -> "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero.getName().substring(14) + "_lg.png")
                .orElse("No Image Available");
    }
}
