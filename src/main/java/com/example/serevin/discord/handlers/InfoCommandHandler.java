package com.example.serevin.discord.handlers;

import com.example.serevin.discord.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class InfoCommandHandler implements CommandHandler {

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if ("info".equals(event.getName())) {
            info(event);
        }
    }
    @Override
    public void registerCommands(JDA jda) {
        jda.upsertCommand(Commands.slash("info", "Информация о боте")
                        .setDescription("Информация о боте.")
                ).queue();
    }

    private void info(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Наш бот предназначен для парсинга статистики матчей игроков в дискорд канал сервера.");
        embedBuilder.setColor(Color.BLUE);

        embedBuilder.setDescription("**/commands нашего бота:**");

        embedBuilder.addField("__/addidplayers__",
                "C помощью данной команды мы можете добавить себя в список игроков, статистику которых парсит наш бот. Для этого вам потребуется dotaid, его можно посмотреть, зайдя в саму игру Dota 2 и нажав на поле **Добавить в друзья**.",
                false);

        embedBuilder.addField("__/removeidplayers__",
                "C помощью данной команды вы можете удалить себя из списка игроков, статистику которых показывает бот, указав свой dotaid.",
                false);

        embedBuilder.addField("__/listidplayers__",
                "C помощью данной команды можно посмотреть наш список игроков, статистику которых парсит бот.",
                false);

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }
}
