package com.adriim1.esports_calendar;

import java.util.List;

public class Match {
    private int id;
    private String match_name;
    private int external_id;
    private String scheduled_at;
    private String videogame_name;
    private String league_name;
    private String tournament_name;
    private String serie_full_name;
    private List<Opponent> opponents;
    private String match_type;
    private int number_of_games;
    private String status;
    private List<Result> results;
    private Integer winner_id;
    private List<String> streams;
    private String end_at;
    private String created_at;
    private String updated_at;

    // Getters
    public int getId() { return id; }
    public String getMatch_name() { return match_name; }
    public int getExternal_id() { return external_id; }
    public String getScheduled_at() { return scheduled_at; }
    public String getVideogame_name() { return videogame_name; }
    public String getLeague_name() { return league_name; }
    public String getTournament_name() { return tournament_name; }
    public String getSerie_full_name() { return serie_full_name; }
    public List<Opponent> getOpponents() { return opponents; }
    public String getMatch_type() { return match_type; }
    public int getNumber_of_games() { return number_of_games; }
    public String getStatus() { return status; }
    public List<Result> getResults() { return results; }
    public Integer getWinner_id() { return winner_id; }
    public List<String> getStreams() { return streams; }
    public String getEnd_at() { return end_at; }
    public String getCreated_at() { return created_at; }
    public String getUpdated_at() { return updated_at; }

    // Clases anidadas
    public static class Opponent {
        private int id;
        private String name;
        private String image_url;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getImage_url() { return image_url; }
    }

    public static class Result {
        private int team_id;
        private int score;

        public int getTeam_id() { return team_id; }
        public int getScore() { return score; }
    }
}
