package com.adriim1.esports_calendar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        final EditText etName = findViewById(R.id.reg_name);
        final EditText etEmail = findViewById(R.id.reg_email);
        final EditText etPassword = findViewById(R.id.reg_password);
        Button btnRegister = findViewById(R.id.btn_registrar_final);
        TextView tvBackLogin = findViewById(R.id.text_back_to_login);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String pass = etPassword.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegistroActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
