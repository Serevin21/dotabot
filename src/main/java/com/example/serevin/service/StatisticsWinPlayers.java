//package com.example.serevin.service;
//
//import com.example.serevin.database.model.Player;
//import com.example.serevin.database.service.PlayerService;
//import com.example.serevin.model.MatchStats;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Component
//public class StatisticsWinPlayers {
//    @Autowired
//    PlayerService playerService;
//    @Autowired
//    DotaStatsService dotaStatsService;
//
//    @Scheduled(fixedRate = 18600000) // Вызывается каждые 6 часов
//    public void collectAndStoreStats() {
//        collectAndStoreStatsForLastNDays(7); // 7 дней
//        collectAndStoreStatsForLastNDays(30); // 30 дней
//    }
//
//    public String collectAndStoreStatsForLastNDays(int days) {
//        List<Player> players = playerService.getAllPlayers();
//        StringBuilder statsResults = new StringBuilder();
//        LocalDate today = LocalDate.now(ZoneId.of("Europe/Moscow"));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String dateFrom = today.format(formatter);
//
//        for (Player player : players) {
//            MatchStats stats = dotaStatsService.getMatchStats(player.getPlayerId(), days, dateFrom);
//            int percentWin = calculateWinPercentage(stats.getWins(), stats.getWins() + stats.getLosses());
//            statsResults.append(percentWin + "%")
//                    .append("(")
//                    .append(stats.getWins())
//                    .append("/")
//                    .append(stats.getLosses())
//                    .append(")")
//                    .append("\n");
//        }
//        return statsResults.toString();
//    }
//
//    public static int calculateWinPercentage(int wins, int totalGames) {
//        if (totalGames == 0) {
//            return 0;
//        }
//        return Math.round((float) wins / totalGames * 100);
//    }
//}
