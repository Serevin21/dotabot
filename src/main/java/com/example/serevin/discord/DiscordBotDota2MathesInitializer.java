package com.example.serevin.discord;

import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.service.Dota2MatchDetailService;
import com.example.serevin.service.Dota2PlayerMatchesService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscordBotDota2MathesInitializer extends ListenerAdapter {
    @Autowired
    private JDA jda;
    @Autowired
    private Dota2PlayerMatchesService matchesService;
    @Autowired
    private Dota2MatchDetailService detailService;
    @Value("${discord.id.server}")
    private String GUILD_ID;
    private static final String CHANNEL_NAME = "dotamatches";
    @PostConstruct
    public void init() {
        this.jda.addEventListener(this);
    }
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        messageWithPlayerStatistics();
    }

    private void messageWithPlayerStatistics(){
        TextChannel channel = setupDotaMatchesChannel();
        List<Match> playerMatches = matchesService.getPlayerMatches(925203100);
        long idMatch = playerMatches.get(0).match_id();
        MatchDetailResponse matchDetails = detailService.getMatchDetails(idMatch);

    }
    private TextChannel setupDotaMatchesChannel() {
        Guild guild = jda.getGuildById(GUILD_ID);
        if (guild == null) {
            throw new IllegalStateException("Server not found. Make sure the bot has been added to the server.");
        }

        return guild.getTextChannelsByName(CHANNEL_NAME, true)
                .stream()
                .findFirst()
                .orElseGet(() -> guild.createTextChannel(CHANNEL_NAME).complete());
    }
}
