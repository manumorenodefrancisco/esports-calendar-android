package com.adriim1.esports_calendar;

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
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        final TextView profileNameDisplay = view.findViewById(R.id.profile_name_display);
        final TextView profileEmailDisplay = view.findViewById(R.id.profile_email_display);
        final EditText editName = view.findViewById(R.id.edit_profile_name);
        final EditText editEmail = view.findViewById(R.id.edit_profile_email);
        final EditText editCountry = view.findViewById(R.id.edit_profile_country);
        final EditText editBirthdate = view.findViewById(R.id.edit_profile_birthdate);
        final EditText editPassword = view.findViewById(R.id.edit_profile_password);
        Button btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        editBirthdate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> editBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        btnEditProfile.setOnClickListener(v -> {
            String newName = editName.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newCountry = editCountry.getText().toString();
            String newBirthdate = editBirthdate.getText().toString();

            if (!newName.isEmpty()) {
                profileNameDisplay.setText(newName);
            }
            if (!newEmail.isEmpty()) {
                profileEmailDisplay.setText(newEmail);
            }

            actualizarPerfilBackend(newName, newEmail, newCountry, newBirthdate);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void actualizarPerfilBackend(String username, String email, String country, String cumple) {
        Context ctx = getContext();
        if (ctx == null) return;

        SharedPreferences prefs = ctx.getSharedPreferences("EsportsCalendarPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(ctx, "No hay sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getApiService(token);
        String birthday = convertirFecha(cumple);

        ApiService.PerfilRequest req = new ApiService.PerfilRequest(
                email,
                username,
                birthday,
                null,
                country
        );

        api.actualizarPerfil(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ctx, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error actualizar perfil. code=" + response.code());
                    Toast.makeText(ctx, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Fallo red actualizar perfil", t);
                Toast.makeText(ctx, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertirFecha(String txt) {
        if (txt == null) return null;
        txt = txt.trim();
        if (txt.isEmpty()) return null;

        try {
            String[] p = txt.split("/");
            if (p.length != 3) return null;
            String dd = p[0];
            String mm = p[1];
            String yy = p[2];
            if (dd.length() == 1) dd = "0" + dd;
            if (mm.length() == 1) mm = "0" + mm;
            return yy + "-" + mm + "-" + dd;
        } catch (Exception e) {
            return null;
        }
    }
}
