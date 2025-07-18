package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.EnrollmentAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout, btnChangePassword;
    private RecyclerView rvEnrolledCourses;
    private EnrollmentAdapter enrollmentAdapter;
    private UserProfileViewModel userProfileViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";

    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views - UPDATED TO MATCH YOUR XML IDs
        tvUserName = findViewById(R.id.user_name);
        tvUserEmail = findViewById(R.id.user_email);
        btnLogout = findViewById(R.id.btnlogout);
        btnChangePassword = findViewById(R.id.btn_change_password);
        rvEnrolledCourses = findViewById(R.id.enrolled_courses);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        rvEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrollmentAdapter = new EnrollmentAdapter(new ArrayList<>(), course -> {
            // Handle enrolled course click
            Toast.makeText(UserProfileActivity.this, "Enrolled Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to CourseDetailsActivity for enrolled course
        });
        rvEnrolledCourses.setAdapter(enrollmentAdapter);

        // Initialize ViewModel
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.loadUserProfile(currentUserId);

        // Observe LiveData
        userProfileViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                tvUserName.setText("Name: " + user.getName());
                tvUserEmail.setText("Email: " + user.getEmail());
            } else {
                Toast.makeText(UserProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                // Optionally, log out if user data is unexpectedly missing
                HomePageActivity.logout(UserProfileActivity.this); // Gọi phương thức static
            }
        });

        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrollmentAdapter.setEnrolledCourses(courses);
            }
        });

        // Set click listeners
        btnLogout.setOnClickListener(v -> {
            HomePageActivity.logout(UserProfileActivity.this); // Gọi phương thức static
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            // Navigate to ChangeProfileActivity
            Intent intent = new Intent(UserProfileActivity.this, ChangeProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user profile data when returning to this activity
        // This ensures updated name/password changes are reflected
        if (currentUserId != -1) {
            userProfileViewModel.loadUserProfile(currentUserId);
        }
    }
}
