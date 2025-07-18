package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MyCoursesActivity extends AppCompatActivity {

    private RecyclerView rvMyEnrolledCourses;
    private CourseAdapter enrolledCourseAdapter;
    private UserProfileViewModel userProfileViewModel;
    private BottomNavigationView bottomNavigationView;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        rvMyEnrolledCourses = findViewById(R.id.rv_my_enrolled_courses);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem các khóa học của bạn.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        rvMyEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrolledCourseAdapter = new CourseAdapter(
                new ArrayList<>(),
                course -> {
                    Intent intent = new Intent(MyCoursesActivity.this, CourseDetailsActivity.class);
                    intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, course.getCourseId());
                    intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_TITLE, course.getTitle());
                    startActivity(intent);
                },
                (course, mode) -> {
                    if (mode == CourseAdapter.AdapterMode.MY_COURSES) {
                        showDropOutConfirmationDialog(course);
                    }
                },
                currentUserId,
                CourseAdapter.AdapterMode.MY_COURSES
        );
        rvMyEnrolledCourses.setAdapter(enrolledCourseAdapter);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.loadUserProfile(currentUserId);

        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrolledCourseAdapter.setCourses(courses);
            }
        });

        setupBottomNavigationView(R.id.nav_my_courses);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
        if (currentUserId != -1) {
            userProfileViewModel.loadUserProfile(currentUserId);
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            enrolledCourseAdapter.setCourses(new ArrayList<>());
            enrolledCourseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_my_courses);
        }
    }

    private void showDropOutConfirmationDialog(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đăng ký")
                .setMessage("Bạn có chắc chắn muốn hủy đăng ký khóa học '" + course.getTitle() + "' không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    userProfileViewModel.dropOutCourse(currentUserId, course.getCourseId());
                    Toast.makeText(MyCoursesActivity.this, "Đã hủy đăng ký khóa học " + course.getTitle(), Toast.LENGTH_SHORT).show();
                    userProfileViewModel.loadUserProfile(currentUserId);
                    userProfileViewModel.loadEnrollments(currentUserId);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void setupBottomNavigationView(int selectedItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Intent intent = null;

                if (itemId == R.id.nav_home) {
                    intent = new Intent(MyCoursesActivity.this, HomePageActivity.class);
                } else if (itemId == R.id.nav_courses) {
                    intent = new Intent(MyCoursesActivity.this, CourseListActivity.class);
                } else if (itemId == R.id.nav_my_courses) {
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(MyCoursesActivity.this, "Vui lòng đăng nhập để xem hồ sơ của bạn.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MyCoursesActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(MyCoursesActivity.this, UserProfileActivity.class);
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
