package com.example.serevin.service;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
public interface ChannelService {
    TextChannel setupDotaMatchesChannel();
}
