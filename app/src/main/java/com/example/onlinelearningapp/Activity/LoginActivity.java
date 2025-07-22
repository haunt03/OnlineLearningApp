package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import this
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

    public static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName";
    private static final String KEY_LOGGED_IN_USER_ROLE = "loggedInUserRole";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- Toolbar Setup ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
            getSupportActionBar().setDisplayShowHomeEnabled(true); // Make it visible
            getSupportActionBar().setTitle("Online Learning App"); // Set the title here
        }
        // --- End Toolbar Setup ---

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
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });

        // Observe login messages
        authViewModel.loginMessage.observe(this, message -> {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Observe logged in user
        authViewModel.loggedInUser.observe(this, user -> {
            if (user != null) {
                // Save to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(KEY_LOGGED_IN_USER_ID, user.getUserId());
                editor.putString(HomePageActivity.KEY_LOGGED_IN_USER_NAME, user.getName());
                editor.putInt(KEY_LOGGED_IN_USER_ROLE, user.getRole());
                editor.apply();

                // Điều hướng theo role
                Intent intent;
                if (user.getRole() == 1) {
                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, HomePageActivity.class);
                }

                // Clear back stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

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

        authViewModel.login(email, password);
    }

}