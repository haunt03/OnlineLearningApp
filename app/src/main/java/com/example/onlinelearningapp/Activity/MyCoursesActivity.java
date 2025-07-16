package com.example.onlinelearningapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem; // Import này cần thiết cho MenuItem
import android.widget.Toast;

import androidx.annotation.NonNull; // Import này cần thiết cho @NonNull
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.Adapter.EnrollmentAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Import này cần thiết cho BottomNavigationView

import java.util.ArrayList;

public class MyCoursesActivity extends AppCompatActivity {

    private RecyclerView rvMyEnrolledCourses;
    private EnrollmentAdapter enrollmentAdapter;
    private UserProfileViewModel userProfileViewModel;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "OnlineLearningAppPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";
    private int currentUserId = -1;

    // Khai báo BottomNavigationView
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        rvMyEnrolledCourses = findViewById(R.id.rv_my_enrolled_courses);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_LOGGED_IN_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem các khóa học của bạn.", Toast.LENGTH_SHORT).show();
            // Điều hướng đến LoginActivity thay vì chỉ finish()
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng MyCoursesActivity hiện tại
            return;
        }

        rvMyEnrolledCourses.setLayoutManager(new LinearLayoutManager(this));
        enrollmentAdapter = new EnrollmentAdapter(new ArrayList<>(), course -> {
            // Xử lý click vào khóa học đã đăng ký
            Toast.makeText(MyCoursesActivity.this, "Khóa học của tôi: " + course.getTitle() + " đã click!", Toast.LENGTH_SHORT).show();
            // TODO: Điều hướng đến CourseDetailsActivity cho khóa học đã đăng ký
            // Ví dụ: Intent intent = new Intent(MyCoursesActivity.this, CourseDetailsActivity.class);
            // intent.putExtra("COURSE_ID", course.getCourseId());
            // startActivity(intent);
        });
        rvMyEnrolledCourses.setAdapter(enrollmentAdapter);

        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.loadUserProfile(currentUserId); // Tải các khóa học đã đăng ký của người dùng
        userProfileViewModel.getEnrolledCoursesWithDetails().observe(this, courses -> {
            if (courses != null) {
                enrollmentAdapter.setEnrolledCourses(courses);
            }
        });

        // --- Bắt đầu phần thêm vào cho BottomNavigationView ---

        // 1. Khởi tạo BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Thiết lập lắng nghe sự kiện chọn item cho BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(MyCoursesActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_courses) {
                    Intent intent = new Intent(MyCoursesActivity.this, CourseListActivity.class);
                    // Để tránh tạo nhiều instance của CourseListActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_my_courses) {
                    // Đã ở MyCoursesActivity, không làm gì hoặc chỉ hiển thị Toast
                    Toast.makeText(MyCoursesActivity.this, "Bạn đang ở khóa học của tôi.", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (currentUserId == -1) {
                        Toast.makeText(MyCoursesActivity.this, "Vui lòng đăng nhập để xem hồ sơ của bạn.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyCoursesActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MyCoursesActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });

        // --- Kết thúc phần thêm vào cho BottomNavigationView ---
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo item "My Courses" được chọn trên BottomNavigationView khi quay lại MyCoursesActivity
        // Chỉ chọn nếu bottomNavigationView đã được khởi tạo
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_my_courses);
        }
    }
}