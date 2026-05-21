package com.adriim1.esports_calendar.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adriim1.esports_calendar.api.ApiService;
import com.adriim1.esports_calendar.R;
import com.adriim1.esports_calendar.api.RetrofitClient;
import com.adriim1.esports_calendar.activity.HomeActivity;
import com.adriim1.esports_calendar.model.ApiResponse;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private TextView profileNameDisplay, profileEmailDisplay;
    private EditText editName, editEmail, editCountry, editBirthdate, editPassword, editPhone;
    private Button btnSaveProfile, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        profileNameDisplay = view.findViewById(R.id.profile_name_display);
        profileEmailDisplay = view.findViewById(R.id.profile_email_display);
        editName = view.findViewById(R.id.edit_profile_name);
        editEmail = view.findViewById(R.id.edit_profile_email);
        editCountry = view.findViewById(R.id.edit_profile_country);
        editBirthdate = view.findViewById(R.id.edit_profile_birthdate);
        editPassword = view.findViewById(R.id.edit_profile_password);
        editPhone = view.findViewById(R.id.edit_profile_phone);
        btnSaveProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        cargarDatosPerfil();

        editBirthdate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String fecha = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                        editBirthdate.setText(fecha);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        btnSaveProfile.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newCountry = editCountry.getText().toString().trim();
            String newBirthdate = editBirthdate.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();

            actualizarPerfilBackend(newName, newEmail, newCountry, newBirthdate, newPassword, newPhone);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
            prefs.edit().remove("accessToken").apply();
            
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void cargarDatosPerfil() {
        Context ctx = getContext();
        if (ctx == null) return;

        SharedPreferences prefs = ctx.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        if (token == null) return;

        ApiService api = RetrofitClient.getApiService(token);
        api.obtenerPerfil().enqueue(new Callback<ApiService.PerfilResponse>() {
            @Override
            public void onResponse(Call<ApiService.PerfilResponse> call, Response<ApiService.PerfilResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.PerfilResponse data = response.body();
                    
                    profileNameDisplay.setText(data.getUsername());
                    profileEmailDisplay.setText(data.getEmail());
                    editName.setText(data.getUsername());
                    editEmail.setText(data.getEmail());
                    editCountry.setText(data.getCountry());
                    if (data.getPhone() != null) {
                        editPhone.setText(data.getPhone());
                    }
                    
                    if (data.getBirthday() != null) {
                        editBirthdate.setText(convertirFechaADisplay(data.getBirthday()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.PerfilResponse> call, Throwable t) {
                Log.e(TAG, "Fallo al cargar datos de perfil", t);
            }
        });
    }

    private void actualizarPerfilBackend(String username, String email, String country, String cumple, String password, String phone) {
        Context ctx = getContext();
        if (ctx == null) return;

        SharedPreferences prefs = ctx.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(ctx, "Sesión expirada", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getApiService(token);
        String birthday = convertirFechaABackend(cumple);
        
        // Sino escribe password, se envia null para que el Serializer no la cambie
        String finalPassword = (password != null && !password.isEmpty()) ? password : null;
        
        // Si los campos estan vacios, enviar null para que el Serializer no los cambie
        String finalPhone = (phone != null && !phone.isEmpty()) ? phone : null;
        String finalCountry = (country != null && !country.isEmpty()) ? country : null;
        String finalBirthday = (birthday != null && !birthday.isEmpty()) ? birthday : null;

        ApiService.PerfilRequest req = new ApiService.PerfilRequest(
                email,
                username,
                finalPassword,
                finalBirthday,
                finalPhone,
                finalCountry
        );

        api.actualizarPerfil(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ctx, "¡Cambios guardados con éxito!", Toast.LENGTH_SHORT).show();
                    profileNameDisplay.setText(username);
                    profileEmailDisplay.setText(email);
                    editPassword.setText("");
                } else {
                    String errorMsg = "Error al guardar";
                    if (response.body() != null && response.body().getErrors() != null) {
                        String[] errors = response.body().getErrors();
                        if (errors.length > 0) {
                            errorMsg = String.join(", ", errors);
                        }
                    } else if (response.code() == 404) {
                        errorMsg = "Error: Perfil no encontrado (404)";
                    } else if (response.code() == 400) {
                        errorMsg = "Error: Datos inválidos";
                    } else {
                        errorMsg = "Error: Código " + response.code();
                    }
                    Log.e(TAG, "Error respuesta: " + response.code() + " - " + errorMsg);
                    Toast.makeText(ctx, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión", t);
                Toast.makeText(ctx, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertirFechaABackend(String txt) {
        if (txt == null || txt.isEmpty()) return null;
        try {
            String[] p = txt.split("/");
            if (p.length != 3) return null;
            return p[2] + "-" + p[1] + "-" + p[0]; // YYYY-MM-DD
        } catch (Exception e) {
            return null;
        }
    }

    private String convertirFechaADisplay(String backendDate) {
        if (backendDate == null || backendDate.isEmpty()) return "";
        try {
            String[] p = backendDate.split("-"); // Viene como YYYY-MM-DD
            if (p.length != 3) return backendDate;
            return p[2] + "/" + p[1] + "/" + p[0]; // Pasamos a DD/MM/YYYY
        } catch (Exception e) {
            return backendDate;
        }
    }
}
