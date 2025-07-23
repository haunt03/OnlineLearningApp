package com.example.onlinelearningapp.Activity.Admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.ManageUsersViewModel;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;

public class ViewUserDetailActivity extends AppCompatActivity {

    private static final String TAG = "ViewUserDetail";
    private ManageUsersViewModel viewModel;
    private UserProfileViewModel userDetailsModel;
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
//        Button btnChangeInfo = findViewById(R.id.btn_changeinfo);
//        Button btnChangeStatus = findViewById(R.id.btn_changestatus);

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

                // Update status button text
//                btnChangeStatus.setText(user.getStatus().equals("active") ? "Deactivate" : "Activate");

                Log.d(TAG, "Displaying user: ID=" + user.getUserId() + ", Name=" + user.getName());
            } else {
                Log.e(TAG, "User not found for userId: " + userId);
                Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}