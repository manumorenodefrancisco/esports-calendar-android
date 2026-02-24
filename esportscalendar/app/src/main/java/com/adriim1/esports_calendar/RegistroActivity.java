package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private Button btnRegister;
    private TextView tvBackLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar API
        apiService = RetrofitClient.getApiService();

        // Bind views
        etName = findViewById(R.id.reg_name);
        etEmail = findViewById(R.id.reg_email);
        etPassword = findViewById(R.id.reg_password);
        etPassword2 = findViewById(R.id.reg_password2);
        btnRegister = findViewById(R.id.btn_registrar_final);
        tvBackLogin = findViewById(R.id.text_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password1 = etPassword.getText().toString().trim();
            String password2 = etPassword2.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password1.equals(password2)) {
                Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password1, password2);
        });

        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser(String email, String password1, String password2) {
        ApiService.RegisterRequest registerRequest = new ApiService.RegisterRequest(email, password1, password2);
        
        apiService.register(registerRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                        finish();
                        
                    } else {
                        // Registro fallido - mostrar errores
                        String errorMessage = "Error en el registro";
                        if (apiResponse.getErrors() != null && apiResponse.getErrors().length > 0) {
                            errorMessage = apiResponse.getErrors()[0];
                        }
                        Toast.makeText(RegistroActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegistroActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
