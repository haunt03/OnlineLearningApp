package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.CourseViewModel; // We'll create this ViewModel
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView rvAllCourses;
    private CourseAdapter courseAdapter;
    private CourseViewModel courseViewModel;
    private UserProfileViewModel userProfileViewModel;

    private BottomNavigationView bottomNavigationView;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        rvAllCourses = findViewById(R.id.rv_all_courses);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        // Initialize ViewModels
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        rvAllCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(new ArrayList<>(),
                course -> {
                    // Handle course click -> navigate to CourseDetailsActivity
                    if (currentUserId == -1) {
                        Toast.makeText(CourseListActivity.this, "Please login to view course details.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CourseListActivity.this, CourseDetailsActivity.class);
                        intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, course.getCourseId());
                        intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_TITLE, course.getTitle());
                        startActivity(intent);
                    }
                },
                (course, mode) -> {
                    // Handle Enroll button click from ALL_COURSES mode
                    if (mode == CourseAdapter.AdapterMode.ALL_COURSES) {
                        if (currentUserId != -1) {
                            Enrollment newEnrollment = new Enrollment(currentUserId, course.getCourseId());
                            userProfileViewModel.enrollCourse(newEnrollment);
                            Toast.makeText(CourseListActivity.this, "Enrolled in " + course.getTitle(), Toast.LENGTH_SHORT).show();
                            userProfileViewModel.loadEnrollments(currentUserId); // Refresh enrollments to update UI
                        } else {
                            Toast.makeText(CourseListActivity.this, "Please login to enroll in courses.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CourseListActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                },
                currentUserId,
                CourseAdapter.AdapterMode.ALL_COURSES // Set mode for this adapter
        );
        rvAllCourses.setAdapter(courseAdapter);

        // Load enrollments before observing
        if (currentUserId != -1) {
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            courseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }
        observeViewModel();

        setupBottomNavigationView(R.id.nav_courses);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
        if (currentUserId != -1) {
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            courseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_courses);
        }
    }

    private void observeViewModel() {
        // Đảm bảo courseViewModel.getAllCourses() không null
        courseViewModel.getAllCourses().observe(this, courses -> {
            if (courses != null) {
                courseAdapter.setCourses(courses);
            }
        });

        userProfileViewModel.getEnrollments().observe(this, enrollments -> {
            if (enrollments != null) {
                courseAdapter.setEnrolledCourseIds(enrollments);
            }
        });
    }

    private void setupBottomNavigationView(int selectedItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Intent intent = null;

                if (itemId == R.id.nav_home) {
                    intent = new Intent(CourseListActivity.this, HomePageActivity.class);
                } else if (itemId == R.id.nav_courses) {
                    if (!(this instanceof CourseListActivity)) {
                        intent = new Intent(CourseListActivity.this, CourseListActivity.class);
                    }
                } else if (itemId == R.id.nav_my_courses) {
                    if (currentUserId == -1) {
                        Toast.makeText(CourseListActivity.this, "Please login to view your courses.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(CourseListActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(CourseListActivity.this, MyCoursesActivity.class);
                    }
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(CourseListActivity.this, "Please login to view your profile.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(CourseListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(CourseListActivity.this, UserProfileActivity.class);
                    }
                }

                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                return true;
            });
        }
    }
}
