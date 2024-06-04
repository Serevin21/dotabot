package com.example.serevin.service.impl;

import com.example.serevin.entity.Player;
import com.example.serevin.model.Item;
import com.example.serevin.model.MatchDetailsResponse.MatchDetailResponse;
import com.example.serevin.model.MatchDetailsResponse.PlayerDetail;
import com.example.serevin.model.MatchResponse.Match;
import com.example.serevin.repository.PlayerRepository;
import com.example.serevin.utils.RegionMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MatchUpdateServiceImpl {
    @Autowired
    private PlayerMatchesServiceImpl matchesService;
    @Autowired private MatchDetailServiceImpl detailService;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private HeroServiceImpl dota2HeroService;
    @Autowired private ItemServiceImpl dota2ItemService;
    @Autowired private ImageMergerServiceImpl imageMergerService;
    @Autowired private ImgurServiceImpl imgurService;
    @Autowired private PlayerRankServiceImpl playerRankService;
    @Autowired private RegionMapper regionMapper;
    public void updatePlayersMatches(TextChannel channel, List<Player> players) {
        for (Player player : players) {
            List<Match> matches = matchesService.getPlayerRankedMatches(player.getPlayerId());
            int status = matchesService.getStatusPageAPI(player.getPlayerId()).status();

            if(status == 15){
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Игрок " + player.getName() + " закрыл свою историю матчей или не существует.")
                        .setColor(Color.RED);
                channel.sendMessage("").setEmbeds(embedBuilder.build()).queue();
                playerRepository.delete(player);
            } else if(!matches.isEmpty() && status == 1 && matches.get(0).match_id() != player.getLastMatchId()){
                processMatch(player, matches.get(0), channel);
            }
        }
    }

    private void processMatch(Player player, Match latestMatch, TextChannel channel) {
        MatchDetailResponse matchDetail = detailService.getMatchDetails(latestMatch.match_id());
        PlayerDetail playerDetail = matchDetail.players().stream()
                .filter(p -> player.getPlayerId().equals(p.account_id()))
                .findFirst()
                .orElse(null);

        if (playerDetail != null) {
            MessageEmbed message = createMatchMessage(player, playerDetail, matchDetail, latestMatch);
            channel.sendMessage("").setEmbeds(message).queue();
            player.setLastMatchId(latestMatch.match_id());
            playerRepository.save(player);
        }
    }
    private MessageEmbed createMatchMessage(Player player, PlayerDetail playerDetail, MatchDetailResponse matchDetail, Match latestMatch) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String matchid = String.valueOf(matchDetail.match_id());
        String heroNameById = dota2HeroService.getHeroNameById(playerDetail.hero_id());
        String heroImageUrlById = dota2HeroService.getHeroImageUrlById(playerDetail.hero_id());
        String matchResult = getPlayerMatchResult(matchDetail.radiant_win(), playerDetail.team_number());
        String rankName = playerRankService.getRankName(playerDetail.account_id());
        String matchDuration = formatDuration(matchDetail.duration());
        String imageItemBuild = uploadItemBuild(getItemUrls(playerDetail), getItemNeutralUrl(playerDetail));
        String damageString = stringDamageColumn(playerDetail);
        String economyString = stringEconomyColumn(playerDetail);
        Color messageColor = matchResult.equals("Win") ? Color.GREEN : Color.RED;
        String region = regionMapper.getRegion(matchDetail.region());

        return new EmbedBuilder()
                .setTitle(player.getName() + " - Rank: " + rankName + "\n" + heroNameById + " - " + matchResult)
                .setThumbnail(heroImageUrlById)
                .setImage(imageItemBuild)
                .setColor(messageColor)
                .setFooter(" * time:" + formattedDateTime + " * id:" + matchid + " * server:" + region)
                .addField("Damage", damageString, true)
                .addField("Economy", economyString, true)
                .setDescription("Match Duration: " + matchDuration + "\n\n[Dotabuff](https://www.dotabuff.com/matches/"
                        + latestMatch.match_id() + "), [OpenDota](https://www.opendota.com/matches/"
                        + latestMatch.match_id() + "), [STRATZ](https://www.stratz.com/match/" + latestMatch.match_id() + ")")
                .build();
    }
    private String uploadItemBuild(List<String> itemUrls, String neutralItemUrl){
        File file;
        try {
            file = imageMergerService.mergeImages(itemUrls, neutralItemUrl);
            return imgurService.uploadImage(file);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
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
        return strings;
    }
    private String getItemNeutralUrl(PlayerDetail playerDetail) {
        Optional<Item> neutralItem = dota2ItemService.getItemById(playerDetail.item_neutral());
        return neutralItem.map(item -> item.getImageUrl().toString()).orElse(null);
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
    private String getPlayerMatchResult(boolean radiantWin, int teamNumber) {
        return (radiantWin && teamNumber == 0) || (!radiantWin && teamNumber == 1) ? "Win" : "Lose";
    }

    private String formatDuration(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
