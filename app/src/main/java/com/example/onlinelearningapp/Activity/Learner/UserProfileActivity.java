package com.example.onlinelearningapp.Activity.Learner;

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

import com.example.onlinelearningapp.Activity.HomePageActivity;
import com.example.onlinelearningapp.Activity.LoginActivity;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.EnrollmentAdapter;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Setup RecyclerView
        rvEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrollmentAdapter = new EnrollmentAdapter(new ArrayList<>(), course -> {
            Toast.makeText(UserProfileActivity.this, "Enrolled Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            // Optional: navigate to CourseDetailsActivity
        });
        rvEnrolledCourses.setAdapter(enrollmentAdapter);

        // ViewModel
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.loadUserProfile(currentUserId);

        userProfileViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                tvUserName.setText("Name: " + user.getName());
                tvUserEmail.setText("Email: " + user.getEmail());
            } else {
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                HomePageActivity.logout(this);
            }
        });

        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrollmentAdapter.setEnrolledCourses(courses);
            }
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            HomePageActivity.logout(UserProfileActivity.this);
            finish(); // Đóng activity sau khi logout
        });

        // Change password
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ChangeProfileActivity.class);
            startActivity(intent);
        });

        // Bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_home) {
                intent = new Intent(UserProfileActivity.this, HomePageActivity.class);
            } else if (itemId == R.id.nav_courses) {
                intent = new Intent(UserProfileActivity.this, CourseListActivity.class);
            } else if (itemId == R.id.nav_my_courses) {
                intent = new Intent(UserProfileActivity.this, MyCoursesActivity.class);
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(UserProfileActivity.this, "You are already on your profile.", Toast.LENGTH_SHORT).show();
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1) {
            userProfileViewModel.loadUserProfile(currentUserId);
        }
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
    }
}
