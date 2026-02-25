package com.adriim1.esports_calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class PerfilFragment extends Fragment {

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

            if (!newName.isEmpty()) {
                profileNameDisplay.setText(newName);
            }
            if (!newEmail.isEmpty()) {
                profileEmailDisplay.setText(newEmail);
            }

            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
