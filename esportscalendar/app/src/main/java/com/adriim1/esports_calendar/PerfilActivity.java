package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        final TextView profileNameDisplay = findViewById(R.id.profile_name_display);
        final TextView profileEmailDisplay = findViewById(R.id.profile_email_display);
        final EditText editName = findViewById(R.id.edit_profile_name);
        final EditText editEmail = findViewById(R.id.edit_profile_email);
        final EditText editAddress = findViewById(R.id.edit_profile_address);
        final EditText editPhone = findViewById(R.id.edit_profile_phone);
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        Button btnLogout = findViewById(R.id.btn_logout);
        View navHome = findViewById(R.id.nav_home);
        View navNotifications = findViewById(R.id.nav_notifications);

        btnEditProfile.setOnClickListener(v -> {
            String newName = editName.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newAddress = editAddress.getText().toString();
            String newPhone = editPhone.getText().toString();

            if (!newName.isEmpty()) {
                profileNameDisplay.setText(newName);
            }
            if (!newEmail.isEmpty()) {
                profileEmailDisplay.setText(newEmail);
            }

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> Toast.makeText(this, "No tienes notificaciones", Toast.LENGTH_SHORT).show());
    }
}
