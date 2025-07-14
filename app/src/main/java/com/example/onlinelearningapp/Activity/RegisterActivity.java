package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginPrompt;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginPrompt = findViewById(R.id.tv_login_prompt);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Set click listeners
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLoginPrompt.setOnClickListener(v -> finish()); // Go back to LoginActivity

        // Observe registration message from ViewModel - FIX APPLIED HERE
        authViewModel.registerMessage.observe(this, message -> { // Changed from getRegisterMessage()
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            if (message.contains("successful")) {
                finish(); // Go back to LoginActivity on successful registration
            }
        });
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString(); // Get raw string
        String confirmPassword = etConfirmPassword.getText().toString(); // Get raw string

        // Validate input
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required.");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format.");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return;
        }
        // Check if password is just whitespace
        if (password.trim().isEmpty()) {
            etPassword.setError("Password cannot be empty or just spaces.");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) { // Check length on the original string
            etPassword.setError("Password must be at least 6 characters long.");
            etPassword.requestFocus();
            return;
        }

        // Confirm password validation
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required.");
            etConfirmPassword.requestFocus();
            return;
        }
        // Check if confirm password is just whitespace
        if (confirmPassword.trim().isEmpty()) {
            etConfirmPassword.setError("Confirm password cannot be empty or just spaces.");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) { // Compare original strings
            etConfirmPassword.setError("Passwords do not match.");
            etConfirmPassword.requestFocus();
            return;
        }

        // If all validations pass, use trimmed password for registration
        authViewModel.register(name, email, password.trim());
    }
}