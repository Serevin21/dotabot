package com.example.serevin.service.impl;

import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import com.example.serevin.service.MatchDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class MatchDetailServiceImpl implements MatchDetailService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://api.opendota.com/api/matches/";
    public MatchDetailServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public Optional<MatchDetailResponse> getMatchDetails(long matchId) {
        String url = baseUrl + matchId;
        try {
            MatchDetailResponse matchDetail = restTemplate.getForObject(url, MatchDetailResponse.class);
            if (matchDetail != null) {
                return Optional.of(matchDetail);
            } else {
                log.warn("Response from API is null for matchId: {}", matchId);
                return Optional.empty();
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Обработка случая, когда матч не найден (404 Not Found)
            log.warn("Match not found for matchId: {}. Returning empty response.", matchId);
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            // Обработка других HTTP-ошибок
            log.error("HTTP error when receiving matchDetail from API for matchId: {}", matchId, e);
            throw new RuntimeException("HTTP error for matchId: " + matchId + " - " + e.getMessage());
        } catch (ResourceAccessException e) {
            // Обработка сетевых проблем
            log.error("Resource access error when receiving matchDetail from API for matchId: {}", matchId, e);
            throw new RuntimeException("Failed to access resource for matchId: " + matchId, e);
        } catch (Exception e) {
            // Обработка других ошибок
            log.error("Unexpected error when receiving matchDetail from API for matchId: {}", matchId, e);
            throw new RuntimeException("Unexpected error for matchId: " + matchId, e);
        }
    }
}