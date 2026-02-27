package com.adriim1.esports_calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private static final String TAG = "EventoAdapter";
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
        ImageView suscribirBtn;
        View notificarContainer;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            matchNameTV = itemView.findViewById(R.id.text_view_match_name);
            leagueNameTV = itemView.findViewById(R.id.text_view_league_name);
            timeTV = itemView.findViewById(R.id.text_view_time);
            statusTV = itemView.findViewById(R.id.text_view_status);
            teamsTV = itemView.findViewById(R.id.text_view_teams);
            suscribirBtn = itemView.findViewById(R.id.btn_suscribir);
            notificarContainer = itemView.findViewById(R.id.btn_notificar_container);
        }

        public void bind(Evento evento) {
            String matchName = evento.getMatch_name() != null && !evento.getMatch_name().isEmpty() ? 
                evento.getMatch_name() : (evento.getVideogame_name() != null ? evento.getVideogame_name() : "Evento") + 
                " - " + (evento.getTournament_name() != null ? evento.getTournament_name() : "");
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

            if (suscribirBtn != null) {
                suscribirBtn.setOnClickListener(v -> {
                    mostrarDialogoSuscripcion(evento, matchName);
                });
            }
            
            if (notificarContainer != null) {
                notificarContainer.setOnClickListener(v -> {
                    mostrarDialogoSuscripcion(evento, matchName);
                });
            }
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
        
        private void mostrarDialogoSuscripcion(Evento evento, String matchName) {
            boolean[] checkedItems = {false, false}; // 1 día, 1 hora
            String[] options = {"1 día antes", "1 hora antes"};
            
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Configurar notificaciones para: " + matchName);
            builder.setMultiChoiceItems(options, checkedItems, (dialog, which, isChecked) -> {
                checkedItems[which] = isChecked;
            });
            
            builder.setPositiveButton("Suscribir", (dialog, which) -> {
                boolean recordatorio1Dia = checkedItems[0];
                boolean recordatorio1Hora = checkedItems[1];
                
                if (!recordatorio1Dia && !recordatorio1Hora) {
                    Toast.makeText(itemView.getContext(), "Selecciona al menos una opción", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                suscribirEvento(evento, recordatorio1Dia, recordatorio1Hora);
            });
            
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        }
        
        private void suscribirEvento(Evento evento, boolean recordatorio1Dia, boolean recordatorio1Hora) {
            SharedPreferences prefs = itemView.getContext().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
            String accessToken = prefs.getString("accessToken", null);
            
            if (accessToken == null) {
                Toast.makeText(itemView.getContext(), "Debes iniciar sesión para suscribirte", Toast.LENGTH_SHORT).show();
                return;
            }
            
            ApiService apiServiceConToken = RetrofitClient.getApiService(accessToken);

            int eventoId = evento.getId(); 
            ApiService.SuscripcionRequest request = new ApiService.SuscripcionRequest(eventoId, recordatorio1Dia, recordatorio1Hora);
            
            Log.d(TAG, "Enviando datos de suscripción: evento_id=" + eventoId + ", 1_dia=" + recordatorio1Dia + ", 1_hora=" + recordatorio1Hora);

            apiServiceConToken.suscribirEvento(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        String matchTitle = evento.getMatch_name() != null ? evento.getMatch_name() : "el evento";
                        Toast.makeText(itemView.getContext(), "Suscrito a: " + matchTitle, Toast.LENGTH_SHORT).show();
                        
                        Map<String, String> notiData = new HashMap<>();
                        notiData.put("titulo", "Nueva Suscripción");
                        notiData.put("mensaje", "Te has suscrito a " + matchTitle);
                        notiData.put("evento_nombre", matchTitle);
                        apiServiceConToken.addNotification(notiData).enqueue(new Callback<ApiResponse>() {
                            @Override public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {}
                            @Override public void onFailure(Call<ApiResponse> call, Throwable t) {}
                        });
                    } else {
                        Log.e(TAG, "Error en suscripción: " + response.code());
                        Toast.makeText(itemView.getContext(), "Error al suscribirse", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
