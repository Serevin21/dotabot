package com.example.serevin.service.impl;

import com.example.serevin.entity.Player;
import com.example.serevin.repository.PlayerRepository;
import com.example.serevin.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional
    @Override
    public boolean deletePlayer(Long playerId) {
        if (playerRepository.existsById(playerId)) {
            playerRepository.deleteById(playerId);
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    @Override
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
