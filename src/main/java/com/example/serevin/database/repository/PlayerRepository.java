package com.example.serevin.database.repository;

import com.example.serevin.database.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
