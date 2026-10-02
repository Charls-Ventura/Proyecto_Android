package com.example.proyecto_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_android.R;
import com.example.proyecto_android.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private TextView txtGoRegister;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtGoRegister = findViewById(R.id.txtGoRegister);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Si ya existe una sesión iniciada, no mostramos el Login otra vez
        if (authViewModel.isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            authViewModel.login(email, password);
        });

        txtGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        authViewModel.getSuccessMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
            startActivity(intent);
            finish();
        });

        authViewModel.getErrorMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }
}