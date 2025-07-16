package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.CourseViewModel; // We'll create this ViewModel

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView rvAllCourses;
    private CourseAdapter courseAdapter;
    private CourseViewModel courseViewModel; // New ViewModel for courses

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        rvAllCourses = findViewById(R.id.rv_all_courses);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        rvAllCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(new ArrayList<>(), course -> {
            // Handle course click
            if (currentUserId == -1) {
                Toast.makeText(CourseListActivity.this, "Please login to view course details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CourseListActivity.this, "Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to CourseDetailsActivity
            }
        });
        rvAllCourses.setAdapter(courseAdapter);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        courseViewModel.getAllCourses().observe(this, courses -> {
            if (courses != null) {
                courseAdapter.setCourses(courses);
            }
        });
    }
}
