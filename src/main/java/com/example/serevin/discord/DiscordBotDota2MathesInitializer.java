package com.example.serevin.discord;

import com.example.serevin.database.model.Player;
import com.example.serevin.database.repository.PlayerRepository;
import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import com.example.serevin.model.MatchDetailsResponse.PlayerDetail;
import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.service.*;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DiscordBotDota2MathesInitializer extends ListenerAdapter  {
    @Autowired private JDA jda;
    @Autowired private Dota2PlayerMatchesService matchesService;
    @Autowired private Dota2MatchDetailService detailService;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private Dota2HeroNameService heroNameService;
    @Autowired private Dota2HeroImageService heroImageService;
    @Autowired private Dota2ItemService dota2ItemService;
    @Autowired private ImageMergerService imageMergerService;
    @Autowired private ImgurService imgurService;
    @Autowired private PlayerRankService playerRankService;
    @Value("${discord.id.server}") private String GUILD_ID;
    private static final String CHANNEL_NAME = "dotamatches";
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
        TextChannel channel = setupDotaMatchesChannel();
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            List<Match> matches = matchesService.getPlayerMatches(player.getPlayerId());
            int status = matchesService.pageCheck(player.getPlayerId()).status();

            if(status == 15){
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Игрок " + player.getName() + " закрыл свою историю матчей или не существует.")
                                .setColor(Color.RED);
                channel.sendMessage("").setEmbeds(embedBuilder.build()).queue();
                playerRepository.delete(player);
            } else if(!matches.isEmpty() && status == 1 && matches.get(0).match_id() != player.getLastMatchId()){
                Match latestMatch = matches.get(0);

                MatchDetailResponse matchDetail = detailService.getMatchDetails(latestMatch.match_id());
                PlayerDetail playerDetail = detailService.getMatchDetails(latestMatch.match_id()).detailResult().players().stream()
                        .filter(p -> player.getPlayerId().equals(p.account_id()))
                        .findFirst()
                        .orElse(null);

                if(playerDetail != null) {
                    String heroNameById = heroNameService.getHeroNameById(playerDetail.hero_id());
                    String heroImageUrlById = heroImageService.getHeroImageUrlById(playerDetail.hero_id());
                    String matchResult = getPlayerMatchResult(matchDetail.detailResult().radiant_win(), playerDetail.team_number());
                    String rankName = playerRankService.getRankName(playerDetail.account_id());
                    String matchDuration = formatDuration(matchDetail.detailResult().duration());
                    String imageItemBuild = uploadItemBuild(getItemUrls(playerDetail));
                    String damageString = stringDamageColumn(playerDetail);
                    String economyString = stringEconomyColumn(playerDetail);
                    Color messageColor = matchResult.equals("Win") ? Color.GREEN : Color.RED;

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle(player.getName() + " - Rank: "+ rankName + "\n" + heroNameById + " - " + matchResult)
                            .setThumbnail(heroImageUrlById)
                            .setImage(imageItemBuild)
                            .setColor(messageColor)
                            .addField("Damage", damageString, true)
                            .addField("Economy", economyString, true)
                            .setDescription("Match Duration: "+ matchDuration + "\n" + "\n" + " [Dotabuff](https://www.dotabuff.com/matches/"
                                    + latestMatch.match_id() + "), [OpenDota](https://www.opendota.com/matches/"
                                    + latestMatch.match_id() + "), [STRATZ](https://www.stratz.com/match/" + latestMatch.match_id() + ")");

                    channel.sendMessage("").setEmbeds(embedBuilder.build()).queue();
                    player.setLastMatchId(latestMatch.match_id());
                    playerRepository.save(player);
                }
            }
        }
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

    private String stringDamageColumn(PlayerDetail playerDetail){
        return Stream.of(
                String.format("K/D/A: %d/%d/%d", playerDetail.kills(), playerDetail.deaths(), playerDetail.assists()),
                String.format("Hero Damage: %d", playerDetail.hero_damage()),
                String.format("Tower Damage: %d", playerDetail.tower_damage()),
                String.format("Hero Healing: %d", playerDetail.hero_healing())
        ).collect(Collectors.joining("\n"));
    }
    private String stringEconomyColumn(PlayerDetail playerDetail){
        return Stream.of(
                String.format("GPM/XPM: %d/%d", playerDetail.gold_per_min(), playerDetail.xp_per_min()),
                String.format("Net Worth: %d", playerDetail.net_worth()),
                String.format("Last Hits: %d", playerDetail.last_hits()),
                String.format("Denies: %d", playerDetail.denies())
        ).collect(Collectors.joining("\n"));
    }
    private List<String> getItemUrls(PlayerDetail playerDetail) {
        ArrayList<String> strings = new ArrayList<>();
        dota2ItemService.getItemById(playerDetail.item_0()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_1()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_2()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_3()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_4()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_5()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        dota2ItemService.getItemById(playerDetail.item_neutral()).ifPresent(item -> {
            strings.add(item.getImageUrl());
        });
        return strings;
    }
    private String uploadItemBuild(List<String> itemUrls){
        File file;
        try {
            file = imageMergerService.mergeImages(itemUrls);
            return imgurService.uploadImage(file);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public String getPlayerMatchResult(boolean radiantWin, int teamNumber) {
        return (radiantWin && teamNumber == 0) || (!radiantWin && teamNumber == 1) ? "Win" : "Lose";
    }

    public String formatDuration(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
