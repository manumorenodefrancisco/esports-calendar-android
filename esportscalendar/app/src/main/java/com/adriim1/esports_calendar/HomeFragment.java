package com.adriim1.esports_calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private int currentYear, currentMonth, currentDay;
    private RecyclerView matchesRecyclerView;
    private RecyclerView anotacionesRecyclerView;
    private RecyclerView recomendadosRecyclerView;
    private EventoAdapter matchAdapter;
    private AnotacionAdapter anotacionAdapter;
    private EventoAdapter recomendadosAdapter;
    private ApiService apiService;
    private LinearLayout anotacionesLL;

    private TextView monthTitle;
    private final String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    // Filtros
    private String selectedVideogame = null;
    private String selectedStatus = null;
    private String lastSearchText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        Button todayButton = view.findViewById(R.id.todayButton);
        EditText searchEditText = view.findViewById(R.id.edit_text_search);
        ImageButton searchButton = view.findViewById(R.id.btn_search);
        Button btnNuevaAnotacion = view.findViewById(R.id.btn_nueva_anotacion);
        View btnFilters = view.findViewById(R.id.btn_open_filters);

        monthTitle = view.findViewById(R.id.monthTitle);

        matchesRecyclerView = view.findViewById(R.id.recycler_view_matches);
        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        anotacionesRecyclerView = view.findViewById(R.id.recycler_view_anotaciones);
        anotacionesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        anotacionesLL = view.findViewById(R.id.anotacionesLL);

        recomendadosRecyclerView = view.findViewById(R.id.rv_matches_recomendados);
        recomendadosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        View btnReloadContainer = view.findViewById(R.id.btn_reload_recomendados_container);
        ImageButton btnReload = view.findViewById(R.id.btn_reload_recomendados);

        View.OnClickListener reloadAction = v -> generateAndLoadRecommendations();
        if (btnReloadContainer != null) btnReloadContainer.setOnClickListener(reloadAction);
        if (btnReload != null) btnReload.setOnClickListener(reloadAction);

        if (btnFilters != null) {
            btnFilters.setOnClickListener(v -> mostrarDialogoFiltros());
        }

        apiService = RetrofitClient.getApiService();
        matchAdapter = new EventoAdapter(new ArrayList<>());
        matchesRecyclerView.setAdapter(matchAdapter);

        anotacionAdapter = new AnotacionAdapter(new ArrayList<>());
        anotacionesRecyclerView.setAdapter(anotacionAdapter);

        recomendadosAdapter = new EventoAdapter(new ArrayList<>(), true);
        recomendadosRecyclerView.setAdapter(recomendadosAdapter);

        loadAllMatches();

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
            lastSearchText = searchEditText.getText().toString().trim();
            Search(lastSearchText);
        });

        btnNuevaAnotacion.setOnClickListener(v -> {
            crearAnotacion();
        });

        return view;
    }

    private void mostrarDialogoFiltros() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filtros de Eventos");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        // Spinner para Videojuego
        TextView tvGame = new TextView(getContext());
        tvGame.setText("Videojuego:");
        tvGame.setPadding(0, 10, 0, 10);
        layout.addView(tvGame);

        Spinner spinnerGame = new Spinner(getContext());
        String[] games = {"Todos", "League of Legends", "Valorant", "CS:GO", "Dota 2", "Overwatch", "Rocket League"};
        ArrayAdapter<String> gameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, games);
        spinnerGame.setAdapter(gameAdapter);
        
        if (selectedVideogame != null) {
            for (int i = 0; i < games.length; i++) {
                if (games[i].equalsIgnoreCase(selectedVideogame)) {
                    spinnerGame.setSelection(i);
                    break;
                }
            }
        }
        layout.addView(spinnerGame);

        // Spinner para Status
        TextView tvStatus = new TextView(getContext());
        tvStatus.setText("\nEstado:");
        tvStatus.setPadding(0, 10, 0, 10);
        layout.addView(tvStatus);

        Spinner spinnerStatus = new Spinner(getContext());
        String[] statuses = {"Todos", "not_started", "running", "finished"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statusAdapter);

        if (selectedStatus != null) {
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(selectedStatus)) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }
        layout.addView(spinnerStatus);

        builder.setView(layout);

        builder.setPositiveButton("Aplicar", (dialog, which) -> {
            String game = spinnerGame.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            selectedVideogame = game.equals("Todos") ? null : game;
            selectedStatus = status.equals("Todos") ? null : status;

            Search(lastSearchText);
        });

        builder.setNegativeButton("Limpiar", (dialog, which) -> {
            selectedVideogame = null;
            selectedStatus = null;
            Search(lastSearchText);
        });

        builder.show();
    }

    private void Search(String searchText) {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        ApiService apiServiceConToken = token != null ? RetrofitClient.getApiService(token) : RetrofitClient.getApiService();

        if ((searchText == null || searchText.isEmpty()) && selectedVideogame == null && selectedStatus == null) {
            loadMatchesForDate(currentYear, currentMonth, currentDay);
            return;
        }

        apiServiceConToken.searchEvents(null, null, searchText, selectedVideogame, selectedStatus).enqueue(new Callback<ApiService.EventsResponse>() {
            @Override
            public void onResponse(Call<ApiService.EventsResponse> call, Response<ApiService.EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Evento> results = response.body().getData();
                    if (matchAdapter != null) {
                        matchAdapter.getEventoList().clear();
                        if (results != null) matchAdapter.getEventoList().addAll(results);
                        matchAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error en búsqueda", t);
            }
        });
    }

    private void generateAndLoadRecommendations() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(getContext(), "Inicia sesión para generar recomendaciones", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiServiceConToken = RetrofitClient.getApiService(token);
        Toast.makeText(getContext(), "Actualizando preferencias...", Toast.LENGTH_SHORT).show();

        apiServiceConToken.generatePreferences().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Preferencias generadas con éxito, recargando lista...");
                    loadRecommendedMatches();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        String displayMsg = "Error al actualizar preferencias";
                        if (response.code() == 400) {
                            try {
                                JsonObject jsonObject = JsonParser.parseString(errorMsg).getAsJsonObject();
                                displayMsg = jsonObject.has("error") ? jsonObject.get("error").getAsString() : errorMsg;
                            } catch (Exception e) {
                                displayMsg = errorMsg;
                            }
                        }
                        Toast.makeText(getContext(), displayMsg, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error procesando respuesta", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error de red generando preferencias", t);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
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
                    if (matchAdapter != null) {
                        matchAdapter.getEventoList().clear();
                        if (dayMatches != null) matchAdapter.getEventoList().addAll(dayMatches);
                        matchAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error cargando matches por fecha", t);
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
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Evento> recommendedMatches = response.body().getData();
                    if (recomendadosAdapter != null) {
                        recomendadosAdapter.getEventoList().clear();
                        if (recommendedMatches != null) recomendadosAdapter.getEventoList().addAll(recommendedMatches);
                        recomendadosAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.EventsResponse> call, Throwable t) {
                Log.e(TAG, "Error cargando recomendados", t);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nueva Anotación");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_anotacion, null);
        builder.setView(dialogView);

        EditText tituloEditText = dialogView.findViewById(R.id.edit_text_titulo);
        EditText descripcionEditText = dialogView.findViewById(R.id.edit_text_descripcion);

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
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getContext(), "Anotación creada", Toast.LENGTH_SHORT).show();
                        loadAnotacionesForDate(currentYear, currentMonth, currentDay);
                    } else {
                        Toast.makeText(getContext(), "Error al crear anotación", Toast.LENGTH_SHORT).show();
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
            if (anotacionesLL != null) anotacionesLL.setVisibility(View.GONE);
            return;
        }

        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
        ApiService apiServiceConToken = RetrofitClient.getApiService(token);
        apiServiceConToken.getAnotaciones(fecha).enqueue(new Callback<ApiService.AnotacionesResponse>() {
            @Override
            public void onResponse(Call<ApiService.AnotacionesResponse> call, Response<ApiService.AnotacionesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Anotacion> anotaciones = response.body().getData();
                    if (anotaciones != null && !anotaciones.isEmpty()) {
                        anotacionAdapter.getAnotacionList().clear();
                        anotacionAdapter.getAnotacionList().addAll(anotaciones);
                        anotacionAdapter.notifyDataSetChanged();
                        if (anotacionesLL != null) anotacionesLL.setVisibility(View.VISIBLE);
                    } else {
                        if (anotacionesLL != null) anotacionesLL.setVisibility(View.GONE);
                    }
                } else {
                    if (anotacionesLL != null) anotacionesLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiService.AnotacionesResponse> call, Throwable t) {
                if (anotacionesLL != null) anotacionesLL.setVisibility(View.GONE);
            }
        });
    }

}
