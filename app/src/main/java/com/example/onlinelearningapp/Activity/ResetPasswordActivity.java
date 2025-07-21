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
import com.example.onlinelearningapp.utils.EmailThrottleManager;
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

        // Check throttle
        if (!EmailThrottleManager.canSend(this)) {
            Toast.makeText(this, "Please wait 2 minutes before requesting again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi ViewModel để gửi email, dùng callback để xử lý kết quả
        authViewModel.resetPassword(email, isSuccess -> runOnUiThread(() -> {
            if (isSuccess) {
                EmailThrottleManager.updateLastSentTime(this); // Chỉ cập nhật sau khi gửi thành công
                Toast.makeText(this, "New password sent to your email.", Toast.LENGTH_LONG).show();
                finish(); // Quay lại màn hình login
            } else {
                Toast.makeText(this, "Failed to send email. Please try again.", Toast.LENGTH_LONG).show();
            }
        }));
    }
}
