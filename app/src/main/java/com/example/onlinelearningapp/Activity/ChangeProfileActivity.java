package com.example.onlinelearningapp.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.ViewModel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern; // Import Pattern for regex

public class ChangeProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSaveProfile;
    private AuthViewModel authViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    // Using HomePageActivity's constant for user name to update it in SharedPreferences
    // private static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName"; // Already in HomePageActivity

    private int currentUserId = -1;
    private User currentUser; // To hold the current user's data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        // Initialize views
        etName = findViewById(R.id.et_name);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Load current user data to pre-fill fields and for password validation
        authViewModel.getUserById(currentUserId).observe(this, user -> {
            if (user != null) {
                currentUser = user;
                etName.setText(user.getName());
                // Do NOT pre-fill password fields for security reasons
            } else {
                Toast.makeText(ChangeProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Set click listener
        btnSaveProfile.setOnClickListener(v -> attemptSaveChanges());
    }

    private void attemptSaveChanges() {
        if (currentUser == null) {
            Toast.makeText(this, "User data not loaded yet. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etName.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString(); // Don't trim password for comparison
        String newPassword = etNewPassword.getText().toString();
        String confirmNewPassword = etConfirmNewPassword.getText().toString();

        boolean changesMade = false;
        boolean passwordChangeAttempted = false; // Flag to check if password fields were touched

        // Check if any of the new password fields have content
        if (!TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmNewPassword) || !TextUtils.isEmpty(currentPassword)) {
            passwordChangeAttempted = true;
        }


        // 1. Update Name
        // Only update if name is not empty AND it's different from current name
        if (!newName.isEmpty() && !newName.equals(currentUser.getName())) {
            currentUser.setName(newName);
            // We'll call updateUser once at the end if any changes are made to optimize
            changesMade = true;
        }

        // 2. Update Password (only if new password fields are filled or current password field has input)
        if (passwordChangeAttempted) {
            // Validate current password
            if (TextUtils.isEmpty(currentPassword) || !currentPassword.equals(currentUser.getPassword())) {
                etCurrentPassword.setError("Incorrect current password.");
                etCurrentPassword.requestFocus();
                return;
            }

            // New password validation - applying RegisterActivity rules + "no all-whitespace"
            if (TextUtils.isEmpty(newPassword)) {
                etNewPassword.setError("New password is required.");
                etNewPassword.requestFocus();
                return;
            }
            if (newPassword.trim().isEmpty()) {
                etNewPassword.setError("New password cannot be empty or just spaces.");
                etNewPassword.requestFocus();
                return;
            }
            if (newPassword.length() < 6) {
                etNewPassword.setError("New password must be at least 6 characters long.");
                etNewPassword.requestFocus();
                return;
            }

            // --- NEW PASSWORD VALIDATIONS (from RegisterActivity) ---
            // Check for at least one uppercase letter
            Pattern uppercasePattern = Pattern.compile(".*[A-Z].*");
            if (!uppercasePattern.matcher(newPassword).matches()) {
                etNewPassword.setError("New password must contain at least one uppercase letter.");
                etNewPassword.requestFocus();
                return;
            }

            // Check for at least one special character
            // Note: This regex includes commonly used special characters. Adjust if your definition differs.
            Pattern specialCharPattern = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
            if (!specialCharPattern.matcher(newPassword).matches()) {
                etNewPassword.setError("New password must contain at least one special character (e.g., !@#$%^&*).");
                etNewPassword.requestFocus();
                return;
            }
            // --- END NEW PASSWORD VALIDATIONS ---

            if (TextUtils.isEmpty(confirmNewPassword)) {
                etConfirmNewPassword.setError("Confirm new password is required.");
                etConfirmNewPassword.requestFocus();
                return;
            }
            if (!newPassword.equals(confirmNewPassword)) {
                etConfirmNewPassword.setError("New passwords do not match.");
                etConfirmNewPassword.requestFocus();
                return;
            }

            // If all password validations pass, update password
            currentUser.setPassword(newPassword.trim()); // Trim new password before setting
            changesMade = true;
        }

        // Save changes if any were made
        if (changesMade) {
            authViewModel.updateUser(currentUser); // Update user in DB
            // Update SharedPreferences only if name was changed and it's defined
            if (!newName.isEmpty() && !newName.equals(sharedPreferences.getString(HomePageActivity.KEY_LOGGED_IN_USER_NAME, ""))) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(HomePageActivity.KEY_LOGGED_IN_USER_NAME, newName);
                editor.apply();
            }
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to UserProfileActivity
        } else {
            Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
        }
    }
}