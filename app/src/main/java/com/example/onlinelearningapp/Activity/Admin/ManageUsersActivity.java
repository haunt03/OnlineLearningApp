package com.example.onlinelearningapp.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Adapter.UserAdapter;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.ManageUsersViewModel;

public class ManageUsersActivity extends AppCompatActivity {

    private ManageUsersViewModel viewModel;
    private RecyclerView rvUsers;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Learner Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Log.d("ManageUsers", "Back button clicked, returning to AdminDashboardActivity");
            finish();
        });

        // Initialize RecyclerView
        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(user -> {
            Intent intent = new Intent(this, ViewUserDetailActivity.class);
            intent.putExtra("userId", user.getUserId()); // learnerId là int hoặc String
            startActivity(intent);
            Toast.makeText(this, "View Learner Details: " + user.getName(), Toast.LENGTH_SHORT).show();
            Log.d("ManageUsers", "View user: " + user.getName());
        }, user -> {
            // Delete icon click: Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm delete")
                    .setMessage("Are you sure you want to delete this learner: " + user.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.deleteUser(user);
                        Toast.makeText(this, "Learner has been deleted: " + user.getName(), Toast.LENGTH_SHORT).show();
                        Log.d("ManageUsers", "Delete user: " + user.getName());
                    })
                    .setNegativeButton("Exit", null)
                    .show();
        });
        rvUsers.setAdapter(userAdapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);

        // Observe user data
        viewModel.getAllUsersByRole().observe(this, users -> {
            userAdapter.setUserList(users);
            Log.d("ManageUsers", "Users updated: " + (users != null ? users.size() : 0));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_active_learners) {
            viewModel.showActiveUsers();
            Toast.makeText(this, "Hiển thị danh sách Active learners", Toast.LENGTH_SHORT).show();
            Log.d("ManageUsers", "Selected: Active learners");
            return true;
        } else if (id == R.id.action_inactive_learners) {
            viewModel.showInactiveUsers();
            Toast.makeText(this, "Hiển thị danh sách Inactive learners", Toast.LENGTH_SHORT).show();
            Log.d("ManageUsers", "Selected: Inactive learners");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}