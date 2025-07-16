package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable; // Add this import
import android.text.TextWatcher; // Add this import
import android.view.MenuItem;
import android.widget.EditText; // Add this import
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.CourseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List; // Add this import

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView rvAllCourses;
    private CourseAdapter courseAdapter;
    private CourseViewModel courseViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    private BottomNavigationView bottomNavigationView;

    // New: EditText for search
    private EditText etSearchCourse;
    // New: List to hold all courses, so we can filter from it
    private List<Course> allCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        rvAllCourses = findViewById(R.id.rv_all_courses);
        etSearchCourse = findViewById(R.id.et_search_course); // Initialize the new search EditText

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        rvAllCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(new ArrayList<>(), course -> {
            // Handle course click
            if (currentUserId == -1) {
                Toast.makeText(CourseListActivity.this, "Please log in to view course details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CourseListActivity.this, "Course: " + course.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to CourseDetailsActivity, passing course ID if needed
                // Example: Intent intent = new Intent(CourseListActivity.this, CourseDetailsActivity.class);
                // intent.putExtra("COURSE_ID", course.getCourseId());
                // startActivity(intent);
            }
        });
        rvAllCourses.setAdapter(courseAdapter);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        // Observe LiveData from ViewModel
        courseViewModel.getAllCourses().observe(this, courses -> {
            if (courses != null) {
                // Store the full list of courses
                allCourses.clear();
                allCourses.addAll(courses);
                // Display all courses initially
                courseAdapter.setCourses(courses);
            }
        });

        // --- New: Setup TextWatcher for search bar ---
        etSearchCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCourses(s.toString());
            }
        });
        // --- End of new search bar setup ---

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(CourseListActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    Toast.makeText(CourseListActivity.this, "You are already on the course list.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_my_courses) {
                    if (currentUserId == -1) {
                        Toast.makeText(CourseListActivity.this, "Please log in to view your courses.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CourseListActivity.this, MyCoursesActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(CourseListActivity.this, "Please log in to view your profile.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CourseListActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CourseListActivity.this, UserProfileActivity.class);
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
        bottomNavigationView.setSelectedItemId(R.id.nav_courses);
    }

    // New: Method to filter courses based on search query
    private void filterCourses(String query) {
        List<Course> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(allCourses); // If query is empty, show all courses
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Course course : allCourses) {
                // Filter by course title or description (you can add more fields)
                if (course.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        course.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(course);
                }
            }
        }
        courseAdapter.setCourses(filteredList); // Update RecyclerView with filtered list
    }
}