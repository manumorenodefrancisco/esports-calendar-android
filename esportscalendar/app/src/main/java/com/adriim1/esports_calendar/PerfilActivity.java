package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        final TextView profileNameDisplay = findViewById(R.id.profile_name_display);
        final TextView profileEmailDisplay = findViewById(R.id.profile_email_display);
        final EditText editName = findViewById(R.id.edit_profile_name);
        final EditText editEmail = findViewById(R.id.edit_profile_email);
        final EditText editCountry = findViewById(R.id.edit_profile_country);
        final EditText editBirthdate = findViewById(R.id.edit_profile_birthdate);
        final EditText editPassword = findViewById(R.id.edit_profile_password);
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        Button btnLogout = findViewById(R.id.btn_logout);
        View navHome = findViewById(R.id.nav_home);
        View navNotifications = findViewById(R.id.nav_notifications);

        editBirthdate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(PerfilActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> editBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        btnEditProfile.setOnClickListener(v -> {
            String newName = editName.getText().toString();
            String newEmail = editEmail.getText().toString();
            String newCountry = editCountry.getText().toString();
            String newBirthdate = editBirthdate.getText().toString();
            String newPassword = editPassword.getText().toString();

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
