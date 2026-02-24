package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("EsportsCalendarPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        apiService = RetrofitClient.getApiService();

        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login_final);
        tvGoRegister = findViewById(R.id.text_go_to_register);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        ApiService.LoginRequest loginRequest = new ApiService.LoginRequest(email, password);
        
        apiService.login(loginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        ApiResponse.LoginData data = apiResponse.getData();
                        
                        editor.putString("accessToken", data.getToken());
                        editor.putString("refreshToken", data.getRefreshToken());
                        editor.putString("email", email);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        
                    } else {
                        String errorMessage = "Error en el login";
                        if (apiResponse.getErrors() != null && apiResponse.getErrors().length > 0) {
                            errorMessage = apiResponse.getErrors()[0];
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
