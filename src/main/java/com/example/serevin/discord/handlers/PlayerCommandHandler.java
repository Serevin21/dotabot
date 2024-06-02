package com.example.serevin.discord.handlers;

import com.example.serevin.discord.CommandHandler;
import com.example.serevin.entity.Player;
import com.example.serevin.service.impl.PlayerServiceImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerCommandHandler implements CommandHandler {
    @Autowired
    private PlayerServiceImpl playerService;
    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if ("addidplayers".equals(command)) {
            addPlayer(event);
        } else if ("removeidplayers".equals(command)) {
            removePlayer(event);
        } else if ("listidplayers".equals(command)) {
            listPlayers(event);
        }
    }
    @Override
    public void registerCommands(JDA jda) {
        jda.upsertCommand(Commands.slash("listidplayers", "Список ID игроков")
                        .setDescription("Показывает список ID игроков, статистику которых парсит бот.")
                ).queue();
        jda.upsertCommand(Commands.slash("addidplayers", "Добавить игрока")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "id", "ID игрока", true),
                        new OptionData(OptionType.STRING, "name", "Имя игрока", true),
                        new OptionData(OptionType.INTEGER, "last_match_id", "ID последнего матча", true))
                        .setDescription("Добавляет игрока в репозиторий бота.")
                ).queue();
        jda.upsertCommand(Commands.slash("removeidplayers", "Удаляет игрока из репозитория")
                .addOption(OptionType.STRING, "id", "id игрока", true)
                .setDescription("Удаляет игрока из репозитория бота.")
        ).queue();
    }

    private void addPlayer(SlashCommandInteractionEvent event) {
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
        } catch (Exception e) {
            event.reply("Произошла ошибка: " + e.getMessage()).setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

    private void removePlayer(SlashCommandInteractionEvent event) {
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

    private void listPlayers(SlashCommandInteractionEvent event) {
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
            embed.addField("name", namePlayers, true);
            embed.addField("id", idPlayers, true);
            embed.addField("lastMatch", lastMathId, true);
            embed.setColor(Color.GREEN);
        }
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
