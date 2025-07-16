package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnLoginRegister;
    private RecyclerView rvTopCourses;
    private RecyclerView rvLatestLessons;
    private BottomNavigationView bottomNavigationView; // Declare BottomNavigationView

    private CourseAdapter courseAdapter;
    private LessonAdapter lessonAdapter;
    private HomeViewModel homeViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    public static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName";

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
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Initialize BottomNavigationView

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe LiveData from ViewModel
        observeViewModel();

        // Set click listeners for login/register/profile/logout button
        // This button will now primarily handle the "Profile" navigation from the top right
        btnLoginRegister.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set up BottomNavigationView listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Already on Home, do nothing or refresh
                    Toast.makeText(HomePageActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    // Navigate to CourseListActivity
                    Toast.makeText(HomePageActivity.this, "Courses List", Toast.LENGTH_SHORT).show();
                    // TODO: Create CourseListActivity and navigate
                        Intent intent = new Intent(HomePageActivity.this, CourseListActivity.class);
                     startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_my_courses) {
                    // Navigate to MyCoursesActivity (Enrolled Courses)
                    if (currentUserId == -1) {
                        Toast.makeText(HomePageActivity.this, "Please login to view your courses.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomePageActivity.this, "My Courses", Toast.LENGTH_SHORT).show();
                        // TODO: Create MyCoursesActivity and navigate
                        Intent intent = new Intent(HomePageActivity.this, MyCoursesActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Navigate to UserProfileActivity
                    if (currentUserId == -1) {
                        Toast.makeText(HomePageActivity.this, "Please login to view your profile.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomePageActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
        // Ensure the correct item is selected on BottomNavigationView when returning to HomePage
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
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