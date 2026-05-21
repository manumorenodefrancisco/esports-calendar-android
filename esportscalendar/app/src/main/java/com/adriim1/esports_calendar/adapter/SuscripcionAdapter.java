package com.adriim1.esports_calendar.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adriim1.esports_calendar.api.ApiService;
import com.adriim1.esports_calendar.receptor.ReceptorNotis;
import com.adriim1.esports_calendar.R;
import com.adriim1.esports_calendar.api.RetrofitClient;
import com.adriim1.esports_calendar.model.ApiResponse;
import com.adriim1.esports_calendar.model.Evento;
import com.adriim1.esports_calendar.model.Suscripcion;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SuscripcionAdapter extends RecyclerView.Adapter<SuscripcionAdapter.SuscripcionViewHolder> {

    private static final String TAG = "SuscripcionAdapter";
    private List<Suscripcion> suscripcionList;
    private Context context;

    public SuscripcionAdapter(List<Suscripcion> suscripcionList, Context context) {
        this.suscripcionList = suscripcionList;
        this.context = context;
    }

    @NonNull
    @Override
    public SuscripcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suscripcion, parent, false);
        return new SuscripcionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuscripcionViewHolder holder, int position) {
        Suscripcion sus = suscripcionList.get(position);
        holder.bind(sus);
    }

    @Override
    public int getItemCount() {
        return suscripcionList != null ? suscripcionList.size() : 0;
    }

    class SuscripcionViewHolder extends RecyclerView.ViewHolder {
        TextView matchNameTV, leagueNameTV, timeTV, statusTV, teamsTV, tipoTV;
        ImageView btnEliminar;

        public SuscripcionViewHolder(@NonNull View itemView) {
            super(itemView);
            matchNameTV = itemView.findViewById(R.id.text_view_match_name);
            leagueNameTV = itemView.findViewById(R.id.text_view_league_name);
            timeTV = itemView.findViewById(R.id.text_view_time);
            statusTV = itemView.findViewById(R.id.text_view_status);
            teamsTV = itemView.findViewById(R.id.text_view_teams);
            tipoTV = itemView.findViewById(R.id.text_view_suscripcion_tipo);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_suscripcion);
        }

        public void bind(Suscripcion sus) {
            Evento evento = sus.getEvento();
            if (evento != null) {
                String matchName = evento.getMatch_name() != null && !evento.getMatch_name().isEmpty() ?
                        evento.getMatch_name() : (evento.getVideogame_name() != null ? evento.getVideogame_name() : "Evento") +
                        " - " + (evento.getTournament_name() != null ? evento.getTournament_name() : "");
                matchNameTV.setText(matchName);

                leagueNameTV.setText(evento.getLeague_name() != null ? evento.getLeague_name() : "Liga por determinar");

                String time = "Sin hora";
                if (evento.getScheduled_at() != null && evento.getScheduled_at().length() >= 16) {
                    //time = evento.getScheduled_at().substring(11, 16);
                    time = evento.getScheduled_at_espana().substring(11, 16);
                }
                timeTV.setText(time);

                statusTV.setText(getStatusText(evento.getStatus()));
                teamsTV.setText(getTeamsText(evento));

                String tipo = "";
                if (sus.isRecordatorio_1_hora()) {
                    tipo += "1h antes ";
                }
                if (sus.isRecordatorio_5_minutos()) {
                    tipo += "5m antes";
                }
                if (tipo.isEmpty()) {
                    tipo = "Sin alertas";
                }
                tipoTV.setText(tipo);

                btnEliminar.setOnClickListener(v -> eliminarSuscripcion(evento.getId(), matchName));
                itemView.setOnClickListener(v -> mostrarDetalleEvento(evento));

                // Programar alarmas locales al vincular el item
                programarAlarma(sus, matchName);
            }
        }

        private void programarAlarma(Suscripcion sus, String matchName) {
            Evento evento = sus.getEvento();
            if (evento == null || evento.getScheduled_at() == null) return;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date dateEvento = sdf.parse(evento.getScheduled_at());
                if (dateEvento == null) return;

                long timeEventoMillis = dateEvento.getTime();
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                long currentTime = System.currentTimeMillis();


                if (sus.isRecordatorio_1_hora()) {
                    long triggerTime = timeEventoMillis - (60 * 60 * 1000);
                    if (triggerTime > currentTime) setAlarm(alarmManager, triggerTime, evento.getId() * 10 + 1, matchName, "¡El partido empieza en 1 hora!");
                }

                if (sus.isRecordatorio_5_minutos()) {
                    long triggerTime = timeEventoMillis - (5 * 60 * 1000);
                    if (triggerTime > currentTime) setAlarm(alarmManager, triggerTime, evento.getId() * 10 + 2, matchName, "¡El partido está a punto de comenzar!");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error al programar alarma: " + e.getMessage());
            }
        }

        private void setAlarm(AlarmManager alarmManager, long triggerTime, int requestCode, String matchName, String message) {
            Intent intent = new Intent(context, ReceptorNotis.class);
            intent.putExtra("matchName", matchName);
            intent.putExtra("message", message);
            intent.putExtra("matchId", requestCode);

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // setAndAllowWhileIdle es el que permite que suene en reposo
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
            Log.d(TAG, "Alarma configurada para: " + new Date(triggerTime).toString() + " (ID: " + requestCode + ")");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_match_detail, null);
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
                String fullDate = evento.getScheduled_at_espana();
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
                Glide.with(context).load(t1.getImage_url()).placeholder(R.drawable.ic_launcher_foreground).into(team1LogoIV);
                Glide.with(context).load(t2.getImage_url()).placeholder(R.drawable.ic_launcher_foreground).into(team2LogoIV);
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
                    context.startActivity(i);
                });
            } else {
                container.setVisibility(View.GONE);
            }
        }

        private void eliminarSuscripcion(int eventoId, String matchName) {
            SharedPreferences prefs = context.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("accessToken", null);

            if (token == null) {
                Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService(token);
            apiService.eliminarSuscripcion(eventoId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(context, "Suscripción eliminada", Toast.LENGTH_SHORT).show();
                        // Cancelar alarmas al eliminar la suscripción
                        cancelarAlarmas(eventoId);

                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            suscripcionList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    } else {
                        Toast.makeText(context, "Error al eliminar suscripción", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error al eliminar: código " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error de red al eliminar", t);
                }
            });
        }

        private void cancelarAlarmas(int eventoId) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReceptorNotis.class);

            int[] ids = {eventoId * 10 + 1, eventoId * 10 + 2};
            for (int id : ids) {
                PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
                        PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0));
                if (pi != null) {
                    alarmManager.cancel(pi);
                    pi.cancel();
                }
            }
        }
    }
}
