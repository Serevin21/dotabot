package com.example.serevin.entity;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "players")
public class Player {
    @Id
    @Column(name = "player_id")
    private Long playerId;
    @Column(name = "name")
    private String name;
    @Column(name = "last_match_id")
    private Long lastMatchId;
}
