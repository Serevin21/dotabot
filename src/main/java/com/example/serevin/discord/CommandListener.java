package com.example.serevin.discord;

import com.example.serevin.database.model.Player;
import com.example.serevin.database.service.PlayerService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class CommandListener extends ListenerAdapter {
    @Autowired
    PlayerService playerService;
    @Autowired
    private JDA jda;
    private static boolean commandsRegistered = false;
    @PostConstruct
    public void init() {
        this.jda.addEventListener(this);
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("addidplayers")) {
            Long playerId = event.getOption("id").getAsLong();
            String name = event.getOption("name").getAsString();
            Long lastMatchId = event.getOption("last_match_id").getAsLong();

            try {
                boolean isAdded = playerService.addPlayer(playerId, name, lastMatchId);
                EmbedBuilder embed = new EmbedBuilder();

                if (isAdded) {
                    embed.setDescription("Игрок с ID " + playerId + " успешно добавлен!");
                    embed.setColor(Color.GREEN);
                } else {
                    embed.setDescription("Игрок с ID " + playerId + " уже существует!");
                    embed.setColor((Color.RED));
                }
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            } catch (Exception e){
                event.reply("Произошла ошибка: " + e.getMessage()).setEphemeral(true).queue();
                e.printStackTrace();
            }
        }
        //------------------------------------------------------------
        if (command.equals("removeidplayers")) {
            String playerIdString = Optional.ofNullable(event.getOption("id"))
                    .map(option -> option.getAsString())
                    .orElse(null);

            if (playerIdString != null) {
                try {
                    Long playerId = Long.parseLong(playerIdString);
                    boolean isRemoved = playerService.deletePlayer(playerId);
                    EmbedBuilder embed = new EmbedBuilder();

                    if (isRemoved) {
                        embed.setDescription("Id " + playerId + " успешно удален!");
                        embed.setColor(Color.GREEN);
                    } else {
                        embed.setDescription("Id " + playerId + " не найден в списке.");
                        embed.setColor(Color.RED);
                    }
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                } catch (NumberFormatException e) {
                    event.reply("Пожалуйста, укажите действительный числовой ID.").setEphemeral(true).queue();
                }
            } else {
                event.reply("Вы должны указать ID для удаления.").setEphemeral(true).queue();
            }
        }
        //--------------------------------------------------------------------
        if (command.equals("listidplayers")) {
            EmbedBuilder embed = new EmbedBuilder();
            List<Player> players = playerService.getAllPlayers();

            String idPlayers = players.stream()
                    .map(id -> String.format(
                            String.valueOf(id.getPlayerId())))
                    .collect(Collectors.joining("\n"));
            String namePlayers = players.stream()
                    .map(name -> String.format(
                            name.getName()))
                    .collect(Collectors.joining("\n"));
            String lastMathId = players.stream()
                    .map(mathId -> String.format(
                            String.valueOf(mathId.getLastMatchId())))
                    .collect(Collectors.joining("\n"));

            if (players.isEmpty()) {
                embed.setDescription("Список игроков пуст.");
                embed.setColor(Color.RED);
            } else {
                embed.setTitle("Список: ");
                embed.addField("name", namePlayers ,true);
                embed.addField("id", idPlayers ,true);
                embed.addField("lastMatch", lastMathId ,true);
                embed.setColor(Color.GREEN);
            }
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if (!commandsRegistered) {
            Guild guild = event.getGuild();
            List<CommandData> commandData = new ArrayList<>();
            commandData.add(Commands.slash("listidplayers", "Список id"));

            commandData.add(Commands.slash("addidplayers", "Добавляет игрока в репозиторий")
                    .addOptions(new OptionData(OptionType.INTEGER, "id", "ID игрока", true),
                            new OptionData(OptionType.STRING, "name", "Имя игрока", true),
                            new OptionData(OptionType.INTEGER, "last_match_id", "ID последнего матча", true)));
            commandData.add(Commands.slash("removeidplayers", "Удаляет игрока из репозитория при помощи ID")
                    .addOption(OptionType.STRING, "id", "id игрока", true));
            guild.updateCommands().addCommands(commandData).queue(
                    success -> {
                        System.out.println("Commands registered successfully");
                        commandsRegistered = true;
                    },
                    error -> System.err.println("Error occurred while registering commands: " + error.getMessage())
            );
        }
    }
}
