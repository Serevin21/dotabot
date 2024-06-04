package com.example.serevin.service.impl;

import com.example.serevin.service.ChannelService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private JDA jda;
    @Value("${discord.id.server}") private String GUILD_ID;
    private static final String CHANNEL_NAME = "dotamatches";

    public TextChannel setupDotaMatchesChannel() {
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
