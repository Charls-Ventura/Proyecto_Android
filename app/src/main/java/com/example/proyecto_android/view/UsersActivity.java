package com.example.proyecto_android.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_android.R;
import com.example.proyecto_android.adapter.UserAdapter;
import com.example.proyecto_android.viewmodel.AuthViewModel;
import com.example.proyecto_android.viewmodel.UserViewModel;

public class UsersActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;

    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        RecyclerView recyclerUsers = findViewById(R.id.recyclerUsers);
        Button btnLogout = findViewById(R.id.btnLogout);

        userAdapter = new UserAdapter();

        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setAdapter(userAdapter);

        authViewModel =
                new ViewModelProvider(this).get(AuthViewModel.class);

        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUsers().observe(this, users ->
                userAdapter.setUsers(users)
        );

        userViewModel.getErrorMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );

        userViewModel.loadUsers();

        btnLogout.setOnClickListener(v -> {

            authViewModel.logout();

            Intent intent =
                    new Intent(UsersActivity.this, LoginActivity.class);

            startActivity(intent);
            finish();
        });
    }
}