package com.example.onlinelearningapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
// import android.view.View; // Giữ lại nếu các view khác được sử dụng, nếu không thì xóa
// import android.widget.Button; // Xóa import này nếu không có Button nào khác được sử dụng
// import android.widget.TextView; // Xóa import này nếu không có TextView nào khác được sử dụng
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

    // Xóa các khai báo này
    // private TextView tvWelcomeMessage;
    // private Button btnLoginRegister;

    private RecyclerView rvTopCourses;
    private RecyclerView rvLatestLessons;
    private BottomNavigationView bottomNavigationView;

    private CourseAdapter courseAdapter;
    private LessonAdapter lessonAdapter;
    private HomeViewModel homeViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    public static final String KEY_LOGGED_IN_USER_NAME = "loggedInUserName";

    private int currentUserId = -1;
    private String currentUserName = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Xóa các khởi tạo này
        // tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        // btnLoginRegister = findViewById(R.id.btn_login_register);

        rvTopCourses = findViewById(R.id.rv_top_courses);
        rvLatestLessons = findViewById(R.id.rv_latest_lessons);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Thiết lập RecyclerViews
        setupRecyclerViews();

        // Khởi tạo ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Quan sát LiveData từ ViewModel
        observeViewModel();

        // Xóa lắng nghe sự kiện click cho btnLoginRegister vì nút đã bị xóa
        // btnLoginRegister.setOnClickListener(v -> {
        //     if (currentUserId == -1) {
        //         Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
        //         startActivity(intent);
        //     } else {
        //         Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
        //         startActivity(intent);
        //     }
        // });

        // Thiết lập lắng nghe BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Toast.makeText(HomePageActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    Toast.makeText(HomePageActivity.this, "Courses List", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePageActivity.this, CourseListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_my_courses) {
                    if (currentUserId == -1) {
                        Toast.makeText(HomePageActivity.this, "Vui lòng đăng nhập để xem các khóa học của bạn.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomePageActivity.this, "Khóa học của tôi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, MyCoursesActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(HomePageActivity.this, "Vui lòng đăng nhập để xem hồ sơ của bạn.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomePageActivity.this, "Hồ sơ", Toast.LENGTH_SHORT).show();
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
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void checkLoginStatus() {
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);
        currentUserName = sharedPreferences.getString(KEY_LOGGED_IN_USER_NAME, "Guest");

        // Xóa các dòng này vì chúng cập nhật TextView và Button đã bị xóa
        // if (currentUserId != -1) {
        //     tvWelcomeMessage.setText("Welcome, " + currentUserName + "!");
        //     btnLoginRegister.setText("Profile");
        // } else {
        //     tvWelcomeMessage.setText("Welcome, Guest!");
        //     btnLoginRegister.setText("Login / Register");
        // }
    }

    private void setupRecyclerViews() {
        // RecyclerView các khóa học hàng đầu
        rvTopCourses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        courseAdapter = new CourseAdapter(new ArrayList<>(), course -> {
            // Xử lý click vào khóa học
            if (currentUserId == -1) {
                Toast.makeText(HomePageActivity.this, "Vui lòng đăng nhập để xem chi tiết khóa học.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomePageActivity.this, "Khóa học: " + course.getTitle() + " đã click! (ID: " + course.getCourseId() + ")", Toast.LENGTH_SHORT).show();
                // TODO: Điều hướng đến CourseDetailsActivity, truyền course.getCourseId()
            }
        });
        rvTopCourses.setAdapter(courseAdapter);

        // RecyclerView các bài học mới nhất
        rvLatestLessons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lessonAdapter = new LessonAdapter(new ArrayList<>(), lesson -> {
            // Xử lý click vào bài học
            if (currentUserId == -1) {
                Toast.makeText(HomePageActivity.this, "Vui lòng đăng nhập để xem chi tiết bài học.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomePageActivity.this, "Bài học: " + lesson.getTitle() + " đã click! (ID: " + lesson.getLessonId() + ")", Toast.LENGTH_SHORT).show();
                // TODO: Điều hướng đến LessonDetailsActivity, truyền lesson.getLessonId()
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

    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.remove(KEY_LOGGED_IN_USER_NAME);
        editor.apply();

        Intent intent = new Intent(context, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}