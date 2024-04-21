package com.example.serevin.database.model;

import javax.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @Column(name = "player_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playerId;
    @Column(name = "name")
    private String name;
    @Column(name = "last_match_id")
    private Long lastMatchId;
    public Player() {}

    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getLastMatchId() {
        return lastMatchId;
    }
    public void setLastMatchId(Long lastMatchId) {
        this.lastMatchId = lastMatchId;
    }
}
