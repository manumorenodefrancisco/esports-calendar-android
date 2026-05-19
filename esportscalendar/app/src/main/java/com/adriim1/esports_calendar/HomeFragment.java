package com.adriim1.esports_calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private int currentYear, currentMonth, currentDay;
    private RecyclerView matchesRecyclerView;
    private RecyclerView anotacionesRecyclerView;
    private RecyclerView recomendadosRecyclerView;
    private EventoAdapter matchAdapter;
    private AnotacionAdapter anotacionAdapter;
    private EventoAdapter recomendadosAdapter;
    private List<Evento> allMatches;
    private ApiService apiService;
    private LinearLayout anotacionesLL;

    private TextView monthTitle;
    private final String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        View dayContent = view.findViewById(R.id.dayContent);
        Button todayButton = view.findViewById(R.id.todayButton);
        EditText searchEditText = view.findViewById(R.id.edit_text_search);
        ImageButton searchButton = view.findViewById(R.id.btn_search);
        Button btnNuevaAnotacion = view.findViewById(R.id.btn_nueva_anotacion);

        monthTitle = view.findViewById(R.id.monthTitle);

        matchesRecyclerView = view.findViewById(R.id.recycler_view_matches);
        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        anotacionesRecyclerView = view.findViewById(R.id.recycler_view_anotaciones);
        anotacionesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        anotacionesLL = view.findViewById(R.id.anotacionesLL);

        recomendadosRecyclerView = view.findViewById(R.id.rv_matches_recomendados);
        recomendadosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        apiService = RetrofitClient.getApiService();
        allMatches = new ArrayList<>();
        matchAdapter = new EventoAdapter(new ArrayList<>());
        matchesRecyclerView.setAdapter(matchAdapter);

        anotacionAdapter = new AnotacionAdapter(new ArrayList<>());
        anotacionesRecyclerView.setAdapter(anotacionAdapter);

        recomendadosAdapter = new EventoAdapter(new ArrayList<>(), true);
        recomendadosRecyclerView.setAdapter(recomendadosAdapter);

        loadAllMatches();

        obtenerTokenFCM();

        calendarView.setOnDateChangeListener((@NonNull CalendarView cv, int year, int month, int dayOfMonth) -> {
            currentYear = year;
            currentMonth = month;
            currentDay = dayOfMonth;

            monthTitle.setText(meses[month] + " " + year);

            loadMatchesForDate(year, month, dayOfMonth);
            loadAnotacionesForDate(year, month, dayOfMonth);
        });

        loadAnotacionesForDate(currentYear, currentMonth, currentDay);

        todayButton.setOnClickListener(v -> {
            long ahora = System.currentTimeMillis();
            calendarView.setDate(ahora);

            java.util.Calendar calHoy = java.util.Calendar.getInstance();
            monthTitle.setText(meses[calHoy.get(java.util.Calendar.MONTH)] + " " + calHoy.get(java.util.Calendar.YEAR));
        });

        searchButton.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString().trim();
            Search(searchText);
        });

        btnNuevaAnotacion.setOnClickListener(v -> {
            crearAnotacion();
        });

        return view;
    }

    private void loadAllMatches() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        currentYear = calendar.get(java.util.Calendar.YEAR);
        currentMonth = calendar.get(java.util.Calendar.MONTH);
        currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        if (monthTitle != null) {
            monthTitle.setText(meses[currentMonth] + " " + currentYear);
        }

        loadMatchesForDate(currentYear, currentMonth, currentDay);
        loadRecommendedMatches();
    }

    private void loadMatchesForDate(int year, int month, int day) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();

        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);

        apiServiceConToken.getEventsByDate(date).enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Evento> dayMatches = response.body().getData();

                    if (matchAdapter != null && matchAdapter.getEventoList() != null) {
                        matchAdapter.getEventoList().clear();
                        if (dayMatches != null) {
                            matchAdapter.getEventoList().addAll(dayMatches);
                        }
                        matchAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "matchAdapter o getEventoList() es null");
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexion cargando matches para fecha: " + date, t);
            }
        });
    }

    private void loadRecommendedMatches() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();

        apiServiceConToken.getRecommendedEvents().enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            List<Evento> recommendedMatches = response.body().getData();

                            if (recomendadosAdapter != null && recomendadosAdapter.getEventoList() != null) {
                                recomendadosAdapter.getEventoList().clear();
                                if (recommendedMatches != null) {
                                    recomendadosAdapter.getEventoList().addAll(recommendedMatches);
                                }
                                recomendadosAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "recomendadosAdapter o getEventoList() es null");
                            }
                        } else {
                        }
                    } else {
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Excepción procesando respuesta", e);
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexion cargando partidos recomendados", t);
            }
        });
    }

    private void Search(String searchText) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();

        if (searchText.isEmpty()) {
            loadAllMatches();
            return;
        }

        apiServiceConToken.searchEvents(null, null, searchText, null, null).enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Evento> searchResults = response.body().getData();

                        if (matchAdapter != null && matchAdapter.getEventoList() != null) {
                        }
                    } else {
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Excepción procesando respuesta búsqueda", e);
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexion en búsqueda", t);
            }
        });
    }

    private void crearAnotacion() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Nueva Anotación");

        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_anotacion, null);
        builder.setView(dialogView);

        android.widget.EditText tituloEditText = dialogView.findViewById(R.id.edit_text_titulo);
        android.widget.EditText descripcionEditText = dialogView.findViewById(R.id.edit_text_descripcion);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = tituloEditText.getText().toString().trim();
            String descripcion = descripcionEditText.getText().toString().trim();

            if (titulo.isEmpty()) {
                Toast.makeText(getContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            java.util.Calendar ahora = java.util.Calendar.getInstance();
            int horaActual = ahora.get(java.util.Calendar.HOUR_OF_DAY);
            int minutoActual = ahora.get(java.util.Calendar.MINUTE);

            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:00",
                    currentYear, currentMonth + 1, currentDay, horaActual, minutoActual);

            ApiService.AnotacionRequest request = new ApiService.AnotacionRequest(titulo, descripcion, fecha);

            ApiService apiServiceConToken = RetrofitClient.getApiService(token);
            apiServiceConToken.createAnotacion(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            Toast.makeText(getContext(), "Anotación creada", Toast.LENGTH_SHORT).show();
                            loadAnotacionesForDate(currentYear, currentMonth, currentDay);
                        } else {
                            Toast.makeText(getContext(), "Error al crear anotación", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error en el servidor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void loadAnotacionesForDate(int year, int month, int day) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            anotacionesLL.setVisibility(View.GONE);
            return;
        }

        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);

        ApiService apiServiceConToken = RetrofitClient.getApiService(token);
        apiServiceConToken.getAnotaciones(fecha).enqueue(new Callback<ApiService.AnotacionesResponse>() {
            @Override
            public void onResponse(Call<ApiService.AnotacionesResponse> call, Response<ApiService.AnotacionesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<Anotacion> anotaciones = response.body().getData();

                        if (anotaciones != null && !anotaciones.isEmpty()) {
                            anotacionAdapter.getAnotacionList().clear();
                            anotacionAdapter.getAnotacionList().addAll(anotaciones);
                            anotacionAdapter.notifyDataSetChanged();
                            anotacionesLL.setVisibility(View.VISIBLE);
                        } else {
                            anotacionesLL.setVisibility(View.GONE);
                        }
                    } else {
                        anotacionesLL.setVisibility(View.GONE);
                    }
                } else {
                    anotacionesLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiService.AnotacionesResponse> call, Throwable t) {
                anotacionesLL.setVisibility(View.GONE);
            }
        });
    }

    private void obtenerTokenFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d(TAG, "Token FCM obtenido: " + token);

                        enviarTokenAlBackend(token);
                    } else {
                        Log.w(TAG, "No se pudo obtener el token FCM");
                    }
                });
    }

    private void enviarTokenAlBackend(String token) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("accessToken", null);

        ApiService apiServiceConToken = accessToken != null ? RetrofitClient.getApiService(accessToken) : RetrofitClient.getApiService();

        ApiService.TokenRequest tokenRequest = new ApiService.TokenRequest(token);

        apiServiceConToken.registerNotificationToken(tokenRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Token FCM registrado exitosamente");
                } else {
                    Log.e(TAG, "Error registrando token FCM: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error de red registrando token FCM", t);
            }
        });
    }
}