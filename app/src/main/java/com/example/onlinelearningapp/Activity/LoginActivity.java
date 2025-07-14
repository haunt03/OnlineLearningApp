package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterPrompt, tvForgotPassword;
    private AuthViewModel authViewModel;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    // Using HomePageActivity's constant for user name to update it in SharedPreferences
    // private static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName"; // This is now public in HomePageActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterPrompt = findViewById(R.id.tv_register_prompt);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Set click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Observe login message from ViewModel - FIX APPLIED HERE
        authViewModel.loginMessage.observe(this, message -> { // Changed from getLoginMessage()
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show(); // Explicitly use LoginActivity.this for Context
        });

        // Observe logged in user from ViewModel - FIX APPLIED HERE
        authViewModel.loggedInUser.observe(this, user -> { // Changed from getLoggedInUser()
            if (user != null) {
                // Login successful, save user info to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // FIX APPLIED HERE: Access HomePageActivity.KEY_LOGGED_IN_USER_NAME directly
                editor.putInt(KEY_LOGGED_IN_USER_ID, user.getUserId()); // user.getUserId() is correct
                editor.putString(HomePageActivity.KEY_LOGGED_IN_USER_NAME, user.getName()); // user.getName() is correct
                editor.apply();

                // Navigate to HomePage and clear back stack
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Close LoginActivity
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return;
        }

        // Call ViewModel to perform login
        authViewModel.login(email, password);
    }
}
