package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Activity.Admin.AdminDashboardActivity;
import com.example.onlinelearningapp.Activity.Learner.CourseDetailsActivity;
import com.example.onlinelearningapp.Activity.Learner.CourseListActivity;
import com.example.onlinelearningapp.Activity.Learner.LessonDetailsActivity;
import com.example.onlinelearningapp.Activity.Learner.MyCoursesActivity;
import com.example.onlinelearningapp.Activity.Learner.UserProfileActivity;
import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.CourseAdapter;
import com.example.onlinelearningapp.Adapter.LessonAdapter;
import com.example.onlinelearningapp.ViewModel.HomeViewModel;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView rvTopCourses;
    private RecyclerView rvLatestLessons;
    private BottomNavigationView bottomNavigationView;

    private CourseAdapter courseAdapter;
    private LessonAdapter lessonAdapter;
    private HomeViewModel homeViewModel;
    private UserProfileViewModel userProfileViewModel;

    private SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "OnlineLearningAppPrefs";
    public static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    public static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName";

    public static final String KEY_LOGGED_IN_USER_ROLE = "loggedInUserRole";

    private int currentUserId = -1;
    private String currentUserName = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Online Learning App");
        }

        rvTopCourses = findViewById(R.id.rv_top_courses);
        rvLatestLessons = findViewById(R.id.rv_latest_lessons);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        setupRecyclerViews();
        setupBottomNavigationView(R.id.nav_home);
        observeViewModel();
        checkLoginStatus();

        if (currentUserId != -1) {
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            courseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();

        if (currentUserId != -1) {
            userProfileViewModel.loadEnrollments(currentUserId);
        } else {
            courseAdapter.setEnrolledCourseIds(new ArrayList<>());
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private int currentUserRole = 0; // Default to student

    private void checkLoginStatus() {
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
        currentUserName = sharedPreferences.getString(KEY_LOGGED_IN_USER_NAME, "Guest");
        currentUserRole = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ROLE, 0);

        // Nếu là admin (role = 1) => chuyển sang AdminDashboardActivity
        if (currentUserRole == 1) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    private void setupRecyclerViews() {
        rvTopCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
        rvTopCourses.setAdapter(courseAdapter);

        rvLatestLessons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lessonAdapter = new LessonAdapter(new ArrayList<>(), lesson -> {
            if (currentUserId == -1) {
                Toast.makeText(this, "Please login to view lesson details.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Intent intent = new Intent(this, LessonDetailsActivity.class);
                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_ID, lesson.getLessonId());
                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_TITLE, lesson.getTitle());
                startActivity(intent);
            }
        });
        rvLatestLessons.setAdapter(lessonAdapter);
    }

    private void observeViewModel() {
        homeViewModel.getTop5MostEnrolledCourses().observe(this, courses -> {
            if (courses != null) {
                courseAdapter.setCourses(courses);
            }
        });

        homeViewModel.getTop5NewestLessons().observe(this, lessons -> {
            if (lessons != null) {
                lessonAdapter.setLessons(lessons);
            }
        });

        userProfileViewModel.getEnrollments().observe(this, enrollments -> {
            if (enrollments != null) {
                courseAdapter.setEnrolledCourseIds(enrollments);
            }
        });
    }

    private void setupBottomNavigationView(int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_home) {
                if (!(this instanceof HomePageActivity)) {
                    intent = new Intent(this, HomePageActivity.class);
                }
            } else if (itemId == R.id.nav_courses) {
                intent = new Intent(this, CourseListActivity.class);
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

    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.remove(KEY_LOGGED_IN_USER_NAME);
        editor.remove(KEY_LOGGED_IN_USER_ROLE);
        editor.apply();

        Intent intent = new Intent(context, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
