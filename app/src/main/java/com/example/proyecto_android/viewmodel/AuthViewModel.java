package com.example.proyecto_android.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_android.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public void register(String name, String email, String password) {

        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("El nombre es obligatorio");
            return;
        }

        if (!validateCredentials(email, password)) {
            return;
        }

        authRepository.register(
                name.trim(),
                email.trim(),
                password,
                new AuthRepository.AuthCallback() {

                    @Override
                    public void onSuccess() {
                        successMessage.setValue(
                                "Usuario registrado correctamente"
                        );
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                }
        );
    }

    public void login(String email, String password) {

        if (!validateCredentials(email, password)) {
            return;
        }

        authRepository.login(email.trim(), password,
                new AuthRepository.AuthCallback() {

                    @Override
                    public void onSuccess() {
                        successMessage.setValue("Inicio de sesión exitoso");
                    }

                    @Override
                    public void onError(String message) {
                        errorMessage.setValue(message);
                    }
                });
    }

    private boolean validateCredentials(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("El correo electrónico es obligatorio");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage.setValue("Ingresa un correo electrónico válido");
            return false;
        }

        if (password == null || password.isEmpty()) {
            errorMessage.setValue("La contraseña es obligatoria");
            return false;
        }

        if (password.length() < 6) {
            errorMessage.setValue("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        return true;
    }

    public void logout() {
        authRepository.logout();
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}