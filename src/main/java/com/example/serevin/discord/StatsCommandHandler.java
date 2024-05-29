//package com.example.serevin.discord;
//
//
//import com.example.serevin.database.model.Player;
//import com.example.serevin.database.service.PlayerService;
//import com.example.serevin.service.StatisticsWinPlayers;
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
//import net.dv8tion.jda.api.interactions.commands.build.Commands;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.awt.*;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class StatsCommandHandler implements CommandHandler {
//    @Autowired
//    private PlayerService playerService;
//    @Autowired
//    private StatisticsWinPlayers statisticsWinPlayers;
//
//    @Override
//    public void handle(SlashCommandInteractionEvent event) {
//        if ("listwinplayers".equals(event.getName())) {
//            listWinPlayers(event);
//        }
//    }
//    @Override
//    public void registerCommands(JDA jda) {
//        jda.upsertCommand(Commands.slash("listwinplayers", "Винрейты игроков")
//                        .setDescription("Показывает винрейты игроков из репозитория.")
//                ).queue();
//    }
//    private void listWinPlayers(SlashCommandInteractionEvent event) {
//        EmbedBuilder embed = new EmbedBuilder();
//        List<Player> players = playerService.getAllPlayers();
//
//        String namePlayers = players.stream()
//                .map(name -> String.format(
//                        name.getName()))
//                .collect(Collectors.joining("\n"));
//        if (players.isEmpty()) {
//            embed.setDescription("Список игроков пуст.");
//            embed.setColor(Color.RED);
//        } else {
//            embed.setTitle("Винрейты: ");
//            embed.addField("name:",namePlayers,true);
//            embed.addField("7 day:",statisticsWinPlayers.collectAndStoreStatsForLastNDays(7),true);
//            embed.addField("30 day:",statisticsWinPlayers.collectAndStoreStatsForLastNDays(30),true);
//            embed.setColor(Color.MAGENTA);
//        }
//        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
//    }
//}
