package com.example.serevin.discord;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {
    @Autowired
    private List<CommandHandler> commandHandlers;
    @Autowired
    private JDA jda;
    @PostConstruct
    public void init() {
        jda.addEventListener(this);
        commandHandlers.forEach(commandHandler -> {
            commandHandler.registerCommands(jda);
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandHandlers.forEach(commandHandler -> {
            commandHandler.handle(event);
        });
    }
}