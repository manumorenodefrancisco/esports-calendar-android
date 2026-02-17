package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        final EditText etName = findViewById(R.id.reg_name);
        final EditText etEmail = findViewById(R.id.reg_email);
        final EditText etCountry = findViewById(R.id.reg_country);
        final EditText etBirthdate = findViewById(R.id.reg_birthdate);
        final EditText etPassword = findViewById(R.id.reg_password);
        final EditText etPasswordRepeat = findViewById(R.id.reg_password_repeat);
        Button btnRegister = findViewById(R.id.btn_registrar_final);
        TextView tvBackLogin = findViewById(R.id.text_back_to_login);

        etBirthdate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegistroActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> etBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                    year, month, day);
            datePickerDialog.show();
        });

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String country = etCountry.getText().toString();
            String birthdate = etBirthdate.getText().toString();
            String pass = etPassword.getText().toString();
            String passRepeat = etPasswordRepeat.getText().toString();

            if (name.isEmpty() || email.isEmpty() || country.isEmpty() || birthdate.isEmpty() || pass.isEmpty() || passRepeat.isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(passRepeat)) {
                Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
