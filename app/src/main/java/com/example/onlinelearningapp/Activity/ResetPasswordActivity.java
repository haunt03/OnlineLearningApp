package com.example.onlinelearningapp.Activity;

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

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Set click listeners
        btnResetPassword.setOnClickListener(v -> attemptResetPassword());

        tvBackToLogin.setOnClickListener(v -> finish()); // Go back to LoginActivity

        // Observe reset password message from ViewModel - FIX APPLIED HERE
        authViewModel.resetPasswordMessage.observe(this, message -> { // Changed from getResetPasswordMessage()
            Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show(); // Explicitly use ResetPasswordActivity.this for Context
            if (message != null && message.contains("sent")) { // Added null check for message
                finish(); // Go back to LoginActivity after simulated reset
            }
        });
    }

    private void attemptResetPassword() {
        String email = etEmail.getText().toString().trim();

        // Validate input
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

        // Call ViewModel to perform password reset
        authViewModel.resetPassword(email);
    }
}