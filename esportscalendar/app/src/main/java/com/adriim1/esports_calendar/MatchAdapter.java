package com.adriim1.esports_calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;

    public MatchAdapter(List<Match> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView matchNameTV;
        TextView leagueNameTV;
        TextView timeTV;
        TextView statusTV;
        TextView teamsTV;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchNameTV = itemView.findViewById(R.id.text_view_match_name);
            leagueNameTV = itemView.findViewById(R.id.text_view_league_name);
            timeTV = itemView.findViewById(R.id.text_view_time);
            statusTV = itemView.findViewById(R.id.text_view_status);
            teamsTV = itemView.findViewById(R.id.text_view_teams);
        }

        public void bind(Match match) {
            // Nombre del match
            String matchName = match.getMatch_name() != null && !match.getMatch_name().isEmpty() 
                ? match.getMatch_name() 
                : match.getVideogame_name() + " - " + match.getTournament_name();
            matchNameTV.setText(matchName);

            // Liga
            leagueNameTV.setText(match.getLeague_name());

            // Hora (formatear fecha)
            String time = match.getScheduled_at().substring(11, 16); // HH:MM
            timeTV.setText(time);

            // Estado
            statusTV.setText(getStatusText(match.getStatus()));

            // Equipos
            String teams = getTeamsText(match);
            teamsTV.setText(teams);
        }

        private String getStatusText(String status) {
            switch (status) {
                case "running": return "EN VIVO";
                case "finished": return "FINALIZADO";
                case "not_started": return "NO EMPEZADO";
                default: return status.toUpperCase();
            }
        }

        private String getTeamsText(Match match) {
            if (match.getOpponents() != null && match.getOpponents().size() >= 2) {
                return match.getOpponents().get(0).getName() + " vs " + match.getOpponents().get(1).getName();
            }
            return "Equipos por determinar";
        }
    }
}
