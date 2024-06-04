package com.example.serevin.discord;

import com.example.serevin.entity.Player;
import com.example.serevin.repository.PlayerRepository;
import com.example.serevin.service.impl.ChannelServiceImpl;
import com.example.serevin.service.impl.MatchUpdateServiceImpl;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Dota2MatchNotificationBot extends ListenerAdapter {
    @Value("${discord.id.server}") private String GUILD_ID;
    @Autowired
    private JDA jda;
    @Autowired private MatchUpdateServiceImpl matchUpdateService;
    @Autowired private ChannelServiceImpl channelService;
    @Autowired private PlayerRepository playerRepository;
    @PostConstruct
    public void init() {
        this.jda.addEventListener(this);
    }
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        updatePlayersMatches();
    }
    @Scheduled(fixedRate = 120000) //2 min
    private void scheduledUpdatePlayersMatches() {
        updatePlayersMatches();
    }

    private void updatePlayersMatches()  {
        TextChannel channel = channelService.setupDotaMatchesChannel();
        List<Player> players = playerRepository.findAll();
        matchUpdateService.updatePlayersMatches(channel, players);
    }
}

