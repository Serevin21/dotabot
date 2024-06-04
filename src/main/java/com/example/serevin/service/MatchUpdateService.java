package com.example.serevin.service;

import com.example.serevin.entity.Player;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public interface MatchUpdateService {
    void updatePlayersMatches(TextChannel channel, List<Player> players);
}
