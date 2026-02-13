package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        final TextView profileNameDisplay = findViewById(R.id.profile_name_display);
        final EditText editName = findViewById(R.id.edit_profile_name);
        final EditText editEmail = findViewById(R.id.edit_profile_email);

        // Botones de Ajustes
        View btnAlerts = findViewById(R.id.btn_alerts);
        View btnDarkMode = findViewById(R.id.btn_dark_mode);
        View btnIncidences = findViewById(R.id.btn_incidences);

        // Navegación inferior
        View navHome = findViewById(R.id.nav_home);
        View navNotifications = findViewById(R.id.nav_notifications);

        // Lógica para cambiar nombre (Junior style: al perder el foco o pulsar Enter)
        editName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newName = editName.getText().toString();
                if (!newName.isEmpty()) {
                    profileNameDisplay.setText(newName);
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Funcionalidad de los botones de Ajustes
        btnAlerts.setOnClickListener(v -> Toast.makeText(this, "Alertas configuradas", Toast.LENGTH_SHORT).show());
        
        btnDarkMode.setOnClickListener(v -> Toast.makeText(this, "Modo oscuro activado/desactivado", Toast.LENGTH_SHORT).show());
        
        btnIncidences.setOnClickListener(v -> Toast.makeText(this, "Abriendo reporte de incidencias", Toast.LENGTH_SHORT).show());

        // Navegación
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> Toast.makeText(this, "No tienes notificaciones", Toast.LENGTH_SHORT).show());
    }
}
