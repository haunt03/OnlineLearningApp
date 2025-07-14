package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Adapter.LessonAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.ViewModel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnLoginRegister;
    private RecyclerView rvTopCourses;
    private RecyclerView rvLatestLessons;

    private CourseAdapter courseAdapter;
    private LessonAdapter lessonAdapter;
    private HomeViewModel homeViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    public static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName"; // Changed to public

    private int currentUserId = -1; // -1 indicates no user logged in
    private String currentUserName = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize views
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        btnLoginRegister = findViewById(R.id.btn_login_register);
        rvTopCourses = findViewById(R.id.rv_top_courses);
        rvLatestLessons = findViewById(R.id.rv_latest_lessons);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe LiveData from ViewModel
        observeViewModel();

        // Set click listeners for login/register/profile/logout button
        btnLoginRegister.setOnClickListener(v -> {
            if (currentUserId == -1) {
                // Not logged in, go to LoginActivity
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // Logged in, show options (e.g., via a dialog or navigate to profile)
                // For simplicity, let's navigate to UserProfileActivity for now,
                // and UserProfileActivity can have a logout button.
                Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
        currentUserName = sharedPreferences.getString(KEY_LOGGED_IN_USER_NAME, "Guest");

        if (currentUserId != -1) {
            tvWelcomeMessage.setText("Welcome, " + currentUserName + "!");
            btnLoginRegister.setText("Profile"); // Change button text to "Profile"
        } else {
            tvWelcomeMessage.setText("Welcome, Guest!");
            btnLoginRegister.setText("Login / Register");
        }
    }

    private void setupRecyclerViews() {
        // Top Courses RecyclerView
        rvTopCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        courseAdapter = new CourseAdapter(new ArrayList<>(), course -> {
            // Handle course click
            if (currentUserId == -1) {
                Toast.makeText(HomePageActivity.this, "Please login to view course details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomePageActivity.this, "Course: " + course.getTitle() + " clicked! (ID: " + course.getCourseId() + ")", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to CourseDetailsActivity, passing course.getCourseId()
            }
        });
        rvTopCourses.setAdapter(courseAdapter);

        // Latest Lessons RecyclerView
        rvLatestLessons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lessonAdapter = new LessonAdapter(new ArrayList<>(), lesson -> {
            // Handle lesson click
            if (currentUserId == -1) {
                Toast.makeText(HomePageActivity.this, "Please login to view lesson details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomePageActivity.this, "Lesson: " + lesson.getTitle() + " clicked! (ID: " + lesson.getLessonId() + ")", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to LessonDetailsActivity, passing lesson.getLessonId()
            }
        });
        rvLatestLessons.setAdapter(lessonAdapter);
    }

    private void observeViewModel() {
        homeViewModel.getTopCourses().observe(this, courses -> {
            if (courses != null) {
                courseAdapter.setCourses(courses);
            }
        });

        homeViewModel.getNewestLessons().observe(this, lessons -> {
            if (lessons != null) {
                lessonAdapter.setLessons(lessons);
            }
        });
    }

    // This method can be called from UserProfileActivity to log out
    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.remove(KEY_LOGGED_IN_USER_NAME);
        editor.apply();

        // Navigate back to HomePageActivity and clear back stack
        Intent intent = new Intent(context, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}