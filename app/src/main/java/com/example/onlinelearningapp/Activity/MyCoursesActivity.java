package com.example.onlinelearningapp.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.EnrollmentAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel; // Reusing UserProfileViewModel for enrolled courses

import java.util.ArrayList;

public class MyCoursesActivity extends AppCompatActivity {

    private RecyclerView rvMyEnrolledCourses;
    private EnrollmentAdapter enrollmentAdapter;
    private UserProfileViewModel userProfileViewModel; // Reusing this ViewModel

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        rvMyEnrolledCourses = findViewById(R.id.rv_my_enrolled_courses);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Please login to view your courses.", Toast.LENGTH_SHORT).show();
            finish(); // Close this activity if not logged in
            return;
        }

        rvMyEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrollmentAdapter = new EnrollmentAdapter(new ArrayList<>(), course -> {
            // Handle enrolled course click
            Toast.makeText(MyCoursesActivity.this, "My Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to CourseDetailsActivity for enrolled course
        });
        rvMyEnrolledCourses.setAdapter(enrollmentAdapter);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.loadUserProfile(currentUserId); // Load user's enrollments
        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrollmentAdapter.setEnrolledCourses(courses);
            }
        });
    }
}
