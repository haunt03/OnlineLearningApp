package com.example.onlinelearningapp.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Activity.HomePageActivity;
import com.example.onlinelearningapp.Adapter.InProgressCourseAdapter;
import com.example.onlinelearningapp.Adapter.InProgressLessonAdapter;
import com.example.onlinelearningapp.Adapter.RecentUserAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.AdminDashboardViewModel;

public class AdminDashboardActivity extends AppCompatActivity {

    private AdminDashboardViewModel viewModel;
    private TextView tvUserCount, tvCourseCount, tvLessonCount, tvQuizCount;
    private RecyclerView rvRecentUsers, rvInProgressCourses, rvInProgressLessons;
    private RecentUserAdapter recentUserAdapter;
    private InProgressCourseAdapter inProgressCourseAdapter;
    private InProgressLessonAdapter inProgressLessonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize TextViews
        tvUserCount = findViewById(R.id.tv_user_count);
        tvCourseCount = findViewById(R.id.tv_course_count);
        tvLessonCount = findViewById(R.id.tv_lesson_count);
        tvQuizCount = findViewById(R.id.tv_quiz_count);

        // Initialize RecyclerViews
        rvRecentUsers = findViewById(R.id.rv_recent_users);
        rvRecentUsers.setLayoutManager(new LinearLayoutManager(this));
        recentUserAdapter = new RecentUserAdapter(user -> {
            // Handle user click
            Intent intent = new Intent(this, ViewUserDetailActivity.class);
            intent.putExtra("userId", user.getUserId());
            startActivity(intent);
            Toast.makeText(this, "Viewing details for: " + user.getName(), Toast.LENGTH_SHORT).show();
            Log.d("AdminDashboard", "Clicked user: " + user.getName() + ", ID: " + user.getUserId());
        });
        rvRecentUsers.setAdapter(recentUserAdapter);

        rvInProgressCourses = findViewById(R.id.rv_in_progress_courses);
        rvInProgressCourses.setLayoutManager(new LinearLayoutManager(this));
        inProgressCourseAdapter = new InProgressCourseAdapter();
        rvInProgressCourses.setAdapter(inProgressCourseAdapter);

        rvInProgressLessons = findViewById(R.id.rv_in_progress_lessons);
        rvInProgressLessons.setLayoutManager(new LinearLayoutManager(this));
        inProgressLessonAdapter = new InProgressLessonAdapter();
        rvInProgressLessons.setAdapter(inProgressLessonAdapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);

        // Observe LiveData and update UI
        viewModel.getUserCount().observe(this, count -> {
            tvUserCount.setText(String.valueOf(count != null ? count : 0));
            Log.d("AdminDashboard", "User count updated: " + count);
        });

        viewModel.getCourseCount().observe(this, count -> {
            tvCourseCount.setText(String.valueOf(count != null ? count : 0));
            Log.d("AdminDashboard", "Course count updated: " + count);
        });

        viewModel.getLessonCount().observe(this, count -> {
            tvLessonCount.setText(String.valueOf(count != null ? count : 0));
            Log.d("AdminDashboard", "Lesson count updated: " + count);
        });

        viewModel.getQuizCount().observe(this, count -> {
            tvQuizCount.setText(String.valueOf(count != null ? count : 0));
            Log.d("AdminDashboard", "Quiz count updated: " + count);
        });

        viewModel.getRecentUsers().observe(this, users -> {
            recentUserAdapter.setUserList(users);
            Log.d("AdminDashboard", "Recent users updated: " + (users != null ? users.size() : 0));
        });

        viewModel.getInProgressCourses().observe(this, courses -> {
            Log.d("AdminDashboard", "In-progress courses updated: " + (courses != null ? courses.size() : 0));
            if (courses != null) {
                for (Course course : courses) {
                    Log.d("AdminDashboard", "Course: " + course.getTitle());
                }
            }
            inProgressCourseAdapter.setCourseList(courses);
        });

        viewModel.getInProgressLessons().observe(this, lessons -> {
            Log.d("AdminDashboard", "In-progress lessons updated: " + (lessons != null ? lessons.size() : 0));
            if (lessons != null) {
                for (Lesson lesson : lessons) {
                    Log.d("AdminDashboard", "Lesson: " + lesson.getTitle());
                }
            }
            inProgressLessonAdapter.setLessonList(lessons);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("AdminDashboard", "Menu item selected: " + item.getTitle());
        if (id == R.id.menu_manage_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
            return true;
        } else if (id == R.id.menu_manage_courses) {
            startActivity(new Intent(this, ManageCoursesActivity.class));
            return true;
        } else if (id == R.id.menu_manage_lessons) {
           // startActivity(new Intent(this, ManageLessonsActivity.class));
            return true;
        } else if (id == R.id.menu_manage_progress) {
           // startActivity(new Intent(this, ManageProgressActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            HomePageActivity.logout(AdminDashboardActivity.this);
            finish(); // Đóng activity sau khi logout
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}