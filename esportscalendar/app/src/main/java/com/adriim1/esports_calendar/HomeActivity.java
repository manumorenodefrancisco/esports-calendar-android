package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnLogin = findViewById(R.id.btn_iniciar_sesion);
        Button btnRegister = findViewById(R.id.btn_registrarse);
        Button btnContact = findViewById(R.id.btn_contacto);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        btnContact.setOnClickListener(v -> {
            // Lógica de contacto opcional
        });
    }
}
