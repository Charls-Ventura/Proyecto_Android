package com.example.proyecto_android.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_android.R;
import com.example.proyecto_android.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText editRegisterName;
    private EditText editRegisterEmail;
    private EditText editRegisterPassword;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editRegisterName = findViewById(R.id.editRegisterName);
        editRegisterEmail = findViewById(R.id.editRegisterEmail);
        editRegisterPassword = findViewById(R.id.editRegisterPassword);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView txtGoLogin = findViewById(R.id.txtGoLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(v -> {

            String name =
                    editRegisterName.getText().toString();

            String email =
                    editRegisterEmail.getText().toString();

            String password =
                    editRegisterPassword.getText().toString();

            authViewModel.register(name, email, password);
        });

        authViewModel.getSuccessMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            authViewModel.logout();
            finish();
        });

        authViewModel.getErrorMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );

        txtGoLogin.setOnClickListener(v -> finish());
    }
}