package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem; // Add this import
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Add this import
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.EnrollmentAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.User;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Add this import

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

    // Declare BottomNavigationView
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
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
            // Redirect to LoginActivity instead of just finish() if user not logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Setup RecyclerView
        rvEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrollmentAdapter = new EnrollmentAdapter(new ArrayList<>(), course -> {
            // Handle enrolled course click
            Toast.makeText(UserProfileActivity.this, "Enrolled Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to CourseDetailsActivity for enrolled course
            // Example: Intent intent = new Intent(UserProfileActivity.this, CourseDetailsActivity.class);
            // intent.putExtra("COURSE_ID", course.getCourseId());
            // startActivity(intent);
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
                HomePageActivity.logout(UserProfileActivity.this);
            }
        });

        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrollmentAdapter.setEnrolledCourses(courses);
            }
        });

        // Set click listeners for existing buttons
        btnLogout.setOnClickListener(v -> {
            HomePageActivity.logout(UserProfileActivity.this);
            finish(); // Finish UserProfileActivity after logout
        });

        btnChangePassword.setOnClickListener(v -> {
            // Navigate to ChangeProfileActivity
            Intent intent = new Intent(UserProfileActivity.this, ChangeProfileActivity.class);
            startActivity(intent);
        });

        // --- Start of BottomNavigationView additions ---

        // 1. Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Set up the item selection listener for BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(UserProfileActivity.this, HomePageActivity.class);
                    // Use flags to manage the activity stack: Clear activities on top and bring HomePageActivity to front
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    Intent intent = new Intent(UserProfileActivity.this, CourseListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_my_courses) {
                    // Navigate to MyCoursesActivity (if not already there)
                    Intent intent = new Intent(UserProfileActivity.this, MyCoursesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // To avoid creating multiple MyCoursesActivity instances
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Already in UserProfileActivity, do nothing or show a Toast
                    Toast.makeText(UserProfileActivity.this, "You are already on your profile.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        // --- End of BottomNavigationView additions ---
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user profile data when returning to this activity
        // This ensures updated name/password changes are reflected
        if (currentUserId != -1) {
            userProfileViewModel.loadUserProfile(currentUserId);
        }
        // Ensure the "Profile" item is selected on BottomNavigationView when returning to UserProfileActivity
        // Only select if bottomNavigationView has been initialized
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
    }
}