package com.adriim1.esports_calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
    private boolean mostrarBtnNotificar;

    public EventoAdapter(List<Evento> eventoList) {
        this.eventoList = eventoList;
        this.useRecomendadoLayout = false;
        this.mostrarBtnNotificar = true;
    }

    public EventoAdapter(List<Evento> eventoList, boolean useRecomendadoLayout) {
        this.eventoList = eventoList;
        this.useRecomendadoLayout = useRecomendadoLayout;
        this.mostrarBtnNotificar = true;
    }

    public EventoAdapter(List<Evento> eventoList, boolean useRecomendadoLayout, boolean mostrarBtnNotificar) {
        this.eventoList = eventoList;
        this.useRecomendadoLayout = useRecomendadoLayout;
        this.mostrarBtnNotificar = mostrarBtnNotificar;
    }

    public List<Evento> getEventoList() {
        return eventoList;
    }

    public void setEventoList(List<Evento> newList) {
        this.eventoList = newList;
        notifyDataSetChanged();
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
        return eventoList != null ? eventoList.size() : 0;
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
                time = evento.getScheduled_at().substring(11, 16);
            }
            timeTV.setText(time);

            statusTV.setText(getStatusText(evento.getStatus()));

            String teams = getTeamsText(evento);
            teamsTV.setText(teams);

            itemView.setOnClickListener(v -> mostrarDetalleEvento(evento));

            if (!EventoAdapter.this.mostrarBtnNotificar) {
                if (suscribirBtn != null) suscribirBtn.setVisibility(View.GONE);
                if (notificarContainer != null) notificarContainer.setVisibility(View.GONE);
            } else {
                if (suscribirBtn != null) suscribirBtn.setVisibility(View.VISIBLE);
                if (notificarContainer != null) notificarContainer.setVisibility(View.VISIBLE);
                
                View.OnClickListener subscribeClick = v -> mostrarDialogoSuscripcion(evento, matchName);
                if (suscribirBtn != null) suscribirBtn.setOnClickListener(subscribeClick);
                if (notificarContainer != null) notificarContainer.setOnClickListener(subscribeClick);
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "SIN ESTADO";
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

        private void mostrarDetalleEvento(Evento evento) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_match_detail, null);
            builder.setView(dialogView);

            TextView leagueTV = dialogView.findViewById(R.id.detail_league_name);
            TextView serieTV = dialogView.findViewById(R.id.detail_serie_name);
            TextView tournamentTV = dialogView.findViewById(R.id.detail_tournament_name);
            TextView matchNameTV = dialogView.findViewById(R.id.detail_match_name);
            TextView dateTV = dialogView.findViewById(R.id.detail_date);
            TextView timeTV = dialogView.findViewById(R.id.detail_time);
            TextView team1NameTV = dialogView.findViewById(R.id.detail_team1_name);
            TextView team2NameTV = dialogView.findViewById(R.id.detail_team2_name);
            ImageView team1LogoIV = dialogView.findViewById(R.id.detail_team1_logo);
            ImageView team2LogoIV = dialogView.findViewById(R.id.detail_team2_logo);
            TextView team1ScoreTV = dialogView.findViewById(R.id.detail_team1_score);
            TextView team2ScoreTV = dialogView.findViewById(R.id.detail_team2_score);
            TextView matchTypeTV = dialogView.findViewById(R.id.detail_match_type);
            TextView statusTextTV = dialogView.findViewById(R.id.detail_status);
            View indicatorDot = dialogView.findViewById(R.id.indicator_dot);
            TextView streamUrlTV = dialogView.findViewById(R.id.detail_stream_url);
            TextView endAtTV = dialogView.findViewById(R.id.detail_end_at);
            View streamContainer = dialogView.findViewById(R.id.stream_container);

            leagueTV.setText(evento.getLeague_name() != null ? evento.getLeague_name() : "");
            serieTV.setText(evento.getSerie_full_name() != null ? evento.getSerie_full_name() : "");
            tournamentTV.setText(evento.getTournament_name() != null ? evento.getTournament_name() : "");
            matchNameTV.setText(evento.getMatch_name() != null ? evento.getMatch_name() : "");

            if (evento.getScheduled_at() != null && evento.getScheduled_at().length() >= 16) {
                String fullDate = evento.getScheduled_at();
                String dateStr = fullDate.substring(8, 10) + "/" + fullDate.substring(5, 7);
                String timeStr = fullDate.substring(11, 16);
                dateTV.setText(dateStr);
                timeTV.setText(timeStr);
            }

            if (evento.getOpponents() != null && evento.getOpponents().size() >= 2) {
                Evento.Opponent t1 = evento.getOpponents().get(0);
                Evento.Opponent t2 = evento.getOpponents().get(1);
                team1NameTV.setText(t1.getName());
                team2NameTV.setText(t2.getName());
                Glide.with(itemView.getContext()).load(t1.getImage_url()).placeholder(R.drawable.ic_launcher_foreground).into(team1LogoIV);
                Glide.with(itemView.getContext()).load(t2.getImage_url()).placeholder(R.drawable.ic_launcher_foreground).into(team2LogoIV);
            }

            String mType = (evento.getMatch_type() != null ? evento.getMatch_type().toUpperCase() : "BO") + 
                           (evento.getNumber_of_games() != null ? evento.getNumber_of_games() : "");
            matchTypeTV.setText(mType);

            String status = evento.getStatus();
            if ("running".equals(status)) {
                statusTextTV.setText("LIVE");
                statusTextTV.setTextColor(Color.parseColor("#FF5252"));
                indicatorDot.setVisibility(View.VISIBLE);
                indicatorDot.setBackgroundResource(R.drawable.button_gradient_red);
                setScores(evento, team1ScoreTV, team2ScoreTV);
                setStream(evento, streamUrlTV, streamContainer);
            } else if ("finished".equals(status)) {
                statusTextTV.setText("FINALIZADO");
                statusTextTV.setTextColor(Color.GRAY);
                indicatorDot.setVisibility(View.GONE);
                setScores(evento, team1ScoreTV, team2ScoreTV);
                highlightWinner(evento, team1NameTV, team2NameTV);
                if (evento.getEnd_at() != null) {
                    endAtTV.setVisibility(View.VISIBLE);
                    String end = evento.getEnd_at().length() >= 16 ? "Finalizado a las " + evento.getEnd_at().substring(11, 16) : "Finalizado";
                    endAtTV.setText(end);
                }
                streamContainer.setVisibility(View.GONE);
            } else {
                statusTextTV.setText("PRÓXIMAMENTE");
                statusTextTV.setTextColor(Color.parseColor("#0997B1"));
                indicatorDot.setVisibility(View.GONE);
                team1ScoreTV.setText("0");
                team2ScoreTV.setText("0");
                setStream(evento, streamUrlTV, streamContainer);
            }

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.show();
        }

        private void setScores(Evento evento, TextView s1, TextView s2) {
            if (evento.getResults() != null && evento.getResults().size() >= 2) {
                s1.setText(String.valueOf(evento.getResults().get(0).getScore()));
                s2.setText(String.valueOf(evento.getResults().get(1).getScore()));
            } else {
                s1.setText("0");
                s2.setText("0");
            }
        }

        private void highlightWinner(Evento evento, TextView n1, TextView n2) {
            if (evento.getWinner_id() != null && evento.getOpponents() != null && evento.getOpponents().size() >= 2) {
                if (evento.getWinner_id().equals(evento.getOpponents().get(0).getId())) {
                    n1.setTextColor(Color.parseColor("#4CAF50"));
                } else if (evento.getWinner_id().equals(evento.getOpponents().get(1).getId())) {
                    n2.setTextColor(Color.parseColor("#4CAF50"));
                }
            }
        }

        private void setStream(Evento evento, TextView urlTV, View container) {
            if (evento.getStreams() != null && !evento.getStreams().isEmpty()) {
                String url = evento.getStreams().get(0);
                urlTV.setText(url);
                container.setVisibility(View.VISIBLE);
                container.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    itemView.getContext().startActivity(i);
                });
            } else {
                container.setVisibility(View.GONE);
            }
        }
        
        private void mostrarDialogoSuscripcion(Evento evento, String matchName) {
            Context context = itemView.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_notificacion, null);

            TextView tvPartidoNombre = dialogView.findViewById(R.id.tv_partido_nombre);
            CheckBox checkUnaHora = dialogView.findViewById(R.id.check_una_hora);
            CheckBox checkCincoMinutos = dialogView.findViewById(R.id.check_cinco_minutos);

            if (tvPartidoNombre != null) {
                tvPartidoNombre.setText(matchName);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);

            builder.setPositiveButton("Suscribir", (dialog, which) -> {
                boolean recordatorio1Hora = checkUnaHora.isChecked();
                boolean recordatorio5Minutos = checkCincoMinutos.isChecked();

                if (!recordatorio1Hora && !recordatorio5Minutos) {
                    Toast.makeText(context, "Selecciona al menos una opción", Toast.LENGTH_SHORT).show();
                    return;
                }

                suscribirEvento(evento, recordatorio1Hora, recordatorio5Minutos);
            });

            builder.setNegativeButton("Cancelar", null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.parseColor("#0D1B6D"));
            }
            if (alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.parseColor("#0D1B6D"));
            }
        }

        private void suscribirEvento(Evento evento, boolean recordatorio1Hora, boolean recordatorio5Minutos) {
            SharedPreferences prefs = itemView.getContext().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
            String accessToken = prefs.getString("accessToken", null);

            if (accessToken == null) {
                Toast.makeText(itemView.getContext(), "Debes iniciar sesión para suscribirte", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiServiceConToken = RetrofitClient.getApiService(accessToken);

            int eventoId = evento.getId();
            
            apiServiceConToken.getSubscriptions().enqueue(new Callback<ApiService.SubscriptionsResponse>() {
                @Override
                public void onResponse(Call<ApiService.SubscriptionsResponse> call, Response<ApiService.SubscriptionsResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Suscripcion> suscripciones = response.body().getData();
                        
                        if (suscripciones != null) {
                            for (Suscripcion sus : suscripciones) {
                                if (sus.getEvento() != null && sus.getEvento().getId() == eventoId) {
                                    String matchTitle = evento.getMatch_name() != null ? evento.getMatch_name() : "el evento";
                                    Toast.makeText(itemView.getContext(), "Ya estás suscrito a: " + matchTitle, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        
                        ApiService.SuscripcionRequest request = new ApiService.SuscripcionRequest(eventoId, recordatorio1Hora, recordatorio5Minutos);
                        Log.d(TAG, "Enviando datos de suscripción: evento_id=" + eventoId + ", 1_hora=" + recordatorio1Hora + ", 5_min=" + recordatorio5Minutos);

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
                                    Toast.makeText(itemView.getContext(), "Error al suscribirse. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error al suscribirse: código " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(itemView.getContext(), "Error de red al suscribirse", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error de red al suscribirse", t);
                            }
                        });
                    } else {
                        Toast.makeText(itemView.getContext(), "Error al verificar suscripciones", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al obtener suscripciones: código " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiService.SubscriptionsResponse> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), "Error de red al verificar suscripciones", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error de red al obtener suscripciones", t);
                }
            });
        }
    }
}