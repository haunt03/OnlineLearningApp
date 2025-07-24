package com.example.onlinelearningapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.ManageUsersViewModel;

public class ViewUserDetailActivity extends AppCompatActivity {

    private static final String TAG = "ViewUserDetail";
    private ManageUsersViewModel viewModel;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("User Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "Back button clicked, returning to ManageUsersActivity");
            finish();
        });

        // Get userId from Intent
        int userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Log.e(TAG, "Invalid userId received");
            Toast.makeText(this, "Error: Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);

        // Initialize UI elements
        TextView tvUserId = findViewById(R.id.tv_user_id);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvRole = findViewById(R.id.tv_role);
        TextView tvCreatedAt = findViewById(R.id.tv_created_at);
        Button btnToggleStatus = findViewById(R.id.BtnDeactive);

        // Observe user data
        viewModel.getUserById(userId).observe(this, user -> {
            if (user != null) {
                currentUser = user;
                // Populate UI
                tvUserId.setText(String.valueOf(user.getUserId()));
                tvName.setText(user.getName() != null ? user.getName() : "N/A");
                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                tvStatus.setText(user.getStatus() != null ? user.getStatus() : "N/A");
                tvRole.setText(user.getRole() == 0 ? "Learner" : "Admin");
                tvCreatedAt.setText(user.getCreatedAt() != null ? user.getCreatedAt() : "N/A");

                // Set text color for status: red for inactive, accent_color for active
                if (user.getStatus() != null && user.getStatus().equals("inactive")) {
                    tvStatus.setTextColor(Color.RED);
                } else {
                    tvStatus.setTextColor(getResources().getColor(R.color.accent_color));
                }

                // Set button text: "Active" for inactive users, "Deactivate" for active users
                btnToggleStatus.setText(user.getStatus().equals("active") ? "Deactivate" : "Active");

                Log.d(TAG, "Displaying user: ID=" + user.getUserId() + ", Name=" + user.getName());
            } else {
                Log.e(TAG, "User not found for userId: " + userId);
                Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Handle Active/Deactive button click with confirmation dialog
        btnToggleStatus.setOnClickListener(v -> {
            if (currentUser != null) {
                String currentStatus = currentUser.getStatus();
                String newStatus = currentStatus.equals("active") ? "inactive" : "active";
                String action = currentStatus.equals("active") ? "deactivate" : "activate";

                // Show confirmation dialog
                new AlertDialog.Builder(ViewUserDetailActivity.this)
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to " + action + " this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // User confirmed, update status
                            currentUser.setStatus(newStatus);
                            viewModel.updateUser(currentUser);
                            Log.d(TAG, "Updated user status: ID=" + currentUser.getUserId() + ", New Status=" + newStatus);
                            Toast.makeText(ViewUserDetailActivity.this, "User status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // User canceled, dismiss dialog
                            Log.d(TAG, "User canceled status update for ID=" + currentUser.getUserId());
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                Log.e(TAG, "Cannot update status: currentUser is null");
                Toast.makeText(this, "Error: Cannot update user status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}