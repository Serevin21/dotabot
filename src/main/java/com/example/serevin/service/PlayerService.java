package com.example.serevin.service;


import com.example.serevin.entity.Player;

import java.util.List;

public interface PlayerService {
    List<Player> getAllPlayers();
    boolean deletePlayer(Long playerId);
    boolean addPlayer(Long playerId, String name, Long lastMatchId);
}
