package com.example.serevin.service.impl;

import com.example.serevin.model.HeroesResponse.Hero;
import com.example.serevin.model.HeroesResponse.HeroesResponse;
import com.example.serevin.service.HeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class HeroServiceImpl implements HeroService {
    private final String apiKey;
    private final RestTemplate restTemplate;

    public HeroServiceImpl(RestTemplate restTemplate, @Value("${steam.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    private HeroesResponse getHeroesResponse() {
        String url = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("language", "en_us");

        try {
            return restTemplate.getForObject(builder.toUriString(), HeroesResponse.class);
        } catch (Exception e) {
            log.error("Error fetching hero data from API", e);
            return null;
        }
    }
    @Override
    public String getHeroNameById(int heroId) {
        HeroesResponse response = getHeroesResponse();
        if (response == null) {
            return "No Hero Name Available";
        }
        return response.getResult().getHeroes().stream()
                .filter(hero -> hero.getId() == heroId)
                .findFirst()
                .map(Hero::getLocalizedName)
                .orElse("Unknown Hero");
    }

    @Override
    public String getHeroImageUrlById(int heroId) {
        HeroesResponse response = getHeroesResponse();
        if (response == null) {
            return "No Image Available";
        }
        return response.getResult().getHeroes().stream()
                .filter(hero -> hero.getId() == heroId)
                .findFirst()
                .map(hero -> "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero.getName().substring(14) + "_lg.png")
                .orElse("No Image Available");
    }
}