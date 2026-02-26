package com.adriim1.esports_calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventoList;
    private boolean useRecomendadoLayout;

    public EventoAdapter(List<Evento> eventoList) {
        this.eventoList = eventoList;
        this.useRecomendadoLayout = false;
    }

    public EventoAdapter(List<Evento> eventoList, boolean useRecomendadoLayout) {
        this.eventoList = eventoList;
        this.useRecomendadoLayout = useRecomendadoLayout;
    }

    public List<Evento> getEventoList() {
        return eventoList;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = useRecomendadoLayout ? R.layout.item_match_recomendado : R.layout.item_match;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
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
        ImageButton suscribirBtn;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            matchNameTV = itemView.findViewById(R.id.text_view_match_name);
            leagueNameTV = itemView.findViewById(R.id.text_view_league_name);
            timeTV = itemView.findViewById(R.id.text_view_time);
            statusTV = itemView.findViewById(R.id.text_view_status);
            teamsTV = itemView.findViewById(R.id.text_view_teams);
            suscribirBtn = itemView.findViewById(R.id.btn_suscribir);
        }

        public void bind(Evento evento) {
            String matchName = evento.getMatch_name() != null && !evento.getMatch_name().isEmpty() ? evento.getMatch_name() : evento.getVideogame_name() + " - " + evento.getTournament_name();
            matchNameTV.setText(matchName);

            leagueNameTV.setText(evento.getLeague_name() != null ? evento.getLeague_name() : "Liga por determinar");

            String time = "Sin hora";
            if (evento.getScheduled_at() != null && evento.getScheduled_at().length() >= 16) {
                time = evento.getScheduled_at().substring(11, 16); // HH:MM
            }
            timeTV.setText(time);

            statusTV.setText(getStatusText(evento.getStatus()));

            String teams = getTeamsText(evento);
            teamsTV.setText(teams);

            suscribirBtn.setOnClickListener(v -> {
                
                android.widget.Toast.makeText(itemView.getContext(), "Suscrito a: " + matchName, android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        private String getStatusText(String status) {
            if (status == null) {
                return "SIN ESTADO";
            }
            switch (status) {
                case "running": return "EN VIVO";
                case "finished": return "FINALIZADO";
                case "not_started": return "NO EMPEZADO";
                default: return status.toUpperCase();
            }
        }

        private String getTeamsText(Evento evento) {
            if (evento.getOpponents() != null && evento.getOpponents().size() >= 2) {
                String team1Name = evento.getOpponents().get(0).getName() != null ? 
                    evento.getOpponents().get(0).getName() : "Equipo 1";
                String team2Name = evento.getOpponents().get(1).getName() != null ? 
                    evento.getOpponents().get(1).getName() : "Equipo 2";
                return team1Name + " vs " + team2Name;
            }
            return "Equipos por determinar";
        }
    }
}
