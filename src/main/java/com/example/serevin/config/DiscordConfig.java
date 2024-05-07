package com.example.serevin.config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@Configuration
public class DiscordConfig {

    @Value("${discord.token}")
    private String botToken;

    @Bean
    public JDA jda() {
        try {
            JDA jda = JDABuilder.createDefault(botToken)
                    .build();
            return jda;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось инициализировать JDA", e);
        }
    }
}
