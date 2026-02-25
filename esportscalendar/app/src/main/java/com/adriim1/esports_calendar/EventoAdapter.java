package com.adriim1.esports_calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventoList;

    public EventoAdapter(List<Evento> eventoList) {
        this.eventoList = eventoList;
    }

    public List<Evento> getEventoList() {
        return eventoList;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new EventoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventoList.get(position);
        holder.bind(evento);
    }

    @Override
    public int getItemCount() {
        return eventoList.size();
    }

    class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView matchNameTV;
        TextView leagueNameTV;
        TextView timeTV;
        TextView statusTV;
        TextView teamsTV;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            matchNameTV = itemView.findViewById(R.id.text_view_match_name);
            leagueNameTV = itemView.findViewById(R.id.text_view_league_name);
            timeTV = itemView.findViewById(R.id.text_view_time);
            statusTV = itemView.findViewById(R.id.text_view_status);
            teamsTV = itemView.findViewById(R.id.text_view_teams);
        }

        public void bind(Evento evento) {
            String matchName = evento.getMatch_name() != null && !evento.getMatch_name().isEmpty()
                ? evento.getMatch_name() 
                : evento.getVideogame_name() + " - " + evento.getTournament_name();
            matchNameTV.setText(matchName);

            leagueNameTV.setText(evento.getLeague_name());

            String time = evento.getScheduled_at().substring(11, 16); // HH:MM
            timeTV.setText(time);

            statusTV.setText(getStatusText(evento.getStatus()));

            String teams = getTeamsText(evento);
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

        private String getTeamsText(Evento evento) {
            if (evento.getOpponents() != null && evento.getOpponents().size() >= 2) {
                return evento.getOpponents().get(0).getName() + " vs " + evento.getOpponents().get(1).getName();
            }
            return "Equipos por determinar";
        }
    }
}
