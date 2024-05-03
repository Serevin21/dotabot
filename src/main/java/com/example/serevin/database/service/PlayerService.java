package com.example.serevin.database.service;

import com.example.serevin.database.model.Player;
import com.example.serevin.database.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
    @Transactional
    public boolean deletePlayer(Long playerId) {
        if (playerRepository.existsById(playerId)) {
            playerRepository.deleteById(playerId);
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public boolean addPlayer(Long playerId, String name, Long lastMatchId) {
        if (!playerRepository.existsById(playerId)) {
            Player player = new Player();
            player.setPlayerId(playerId);
            player.setName(name);
            player.setLastMatchId(lastMatchId);
            playerRepository.save(player);
            return true;
        } else {
            return false;
        }
    }
}
