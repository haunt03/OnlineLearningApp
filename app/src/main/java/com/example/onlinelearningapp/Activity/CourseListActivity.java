package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.CourseViewModel;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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

    private EditText etSearchCourse;
    private List<Course> allCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        rvAllCourses = findViewById(R.id.rv_all_courses);
        etSearchCourse = findViewById(R.id.et_search_course);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        rvAllCourses.setLayoutManager(new LinearLayoutManager(this));

        courseAdapter = new CourseAdapter(new ArrayList<>(),
                course -> {
                    if (currentUserId == -1) {
                        Toast.makeText(this, "Please login to view course details.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        Intent intent = new Intent(this, CourseDetailsActivity.class);
                        intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_ID, course.getCourseId());
                        intent.putExtra(CourseDetailsActivity.EXTRA_COURSE_TITLE, course.getTitle());
                        startActivity(intent);
                    }
                },
                (course, mode) -> {
                    if (mode == CourseAdapter.AdapterMode.ALL_COURSES) {
                        if (currentUserId != -1) {
                            Enrollment newEnrollment = new Enrollment(currentUserId, course.getCourseId());
                            userProfileViewModel.enrollCourse(newEnrollment);
                            Toast.makeText(this, "Enrolled in " + course.getTitle(), Toast.LENGTH_SHORT).show();
                            userProfileViewModel.loadEnrollments(currentUserId);
                        } else {
                            Toast.makeText(this, "Please login to enroll in courses.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                        }
                    }
                },
                currentUserId,
                CourseAdapter.AdapterMode.ALL_COURSES
        );
        rvAllCourses.setAdapter(courseAdapter);

        if (currentUserId != -1) {
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            courseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }

        observeViewModel();
        setupBottomNavigationView(R.id.nav_courses);

        etSearchCourse.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterCourses(s.toString());
            }
        });
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
        courseViewModel.getAllCourses().observe(this, courses -> {
            if (courses != null) {
                allCourses.clear();
                allCourses.addAll(courses);
                courseAdapter.setCourses(courses);
            }
        });

        userProfileViewModel.getEnrollments().observe(this, enrollments -> {
            if (enrollments != null) {
                courseAdapter.setEnrolledCourseIds(enrollments);
            }
        });
    }

    private void filterCourses(String query) {
        List<Course> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(allCourses);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Course course : allCourses) {
                if (course.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                    course.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(course);
                }
            }
        }
        courseAdapter.setCourses(filteredList);
    }

    private void setupBottomNavigationView(int selectedItemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                Intent intent = null;

                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, HomePageActivity.class);
                } else if (itemId == R.id.nav_courses) {
                    Toast.makeText(this, "You are already on the course list.", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_my_courses) {
                    if (currentUserId == -1) {
                        Toast.makeText(this, "Please login to view your courses.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(this, LoginActivity.class);
                    } else {
                        intent = new Intent(this, MyCoursesActivity.class);
                    }
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(this, "Please login to view your profile.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(this, LoginActivity.class);
                    } else {
                        intent = new Intent(this, UserProfileActivity.class);
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
