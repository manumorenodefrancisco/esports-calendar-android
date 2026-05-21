package com.adriim1.esports_calendar.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.adriim1.esports_calendar.receptor.ReceptorNotisReinicio;
import com.adriim1.esports_calendar.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnLogin = findViewById(R.id.btn_iniciar_sesion);
        Button btnRegister = findViewById(R.id.btn_registrarse);        Button btnContact = findViewById(R.id.btn_contacto);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        btnContact.setOnClickListener(v -> {
            // contacto
        });

        new ReceptorNotisReinicio().onReceive(this, new Intent(Intent.ACTION_BOOT_COMPLETED));    }
}
