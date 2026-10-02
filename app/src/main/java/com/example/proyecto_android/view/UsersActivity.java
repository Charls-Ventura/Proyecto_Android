package com.example.proyecto_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_android.R;
import com.example.proyecto_android.viewmodel.AuthViewModel;

public class UsersActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Button btnLogout = findViewById(R.id.btnLogout);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();

            Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}