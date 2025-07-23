package com.example.onlinelearningapp.Activity.Admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.CourseDetailsAdminViewModel;

public class CourseDetailsAdminActivity extends AppCompatActivity {
    private static final String TAG = "CourseDetailsAdminActivity";
    private CourseDetailsAdminViewModel viewModel;
    private TextView tvCourseTitle, tvCourseDescription, tvCourseStatus, tvCourseCreatedAt, tvTotalLessons, tvTotalEnrollments;
    private ImageView ivCourseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details_admin);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Details of the Course");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "Back button clicked, returning to CoursesManagement");
            finish();
        });

        // Initialize Views
        tvCourseTitle = findViewById(R.id.tv_course_title);
        tvCourseDescription = findViewById(R.id.tv_course_description);
        tvCourseStatus = findViewById(R.id.tv_course_status);
        tvCourseCreatedAt = findViewById(R.id.tv_course_created_at);
        tvTotalLessons = findViewById(R.id.tv_total_lessons);
        tvTotalEnrollments = findViewById(R.id.tv_total_enrollments);
        ivCourseImage = findViewById(R.id.iv_course_image);

        // Get course ID from Intent
        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId == -1) {
            Log.e(TAG, "No course ID provided");
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CourseDetailsAdminViewModel.class);

        // Observe course details
        viewModel.getCourseById(courseId).observe(this, course -> {
            if (course != null) {
                tvCourseTitle.setText(course.getTitle());
                tvCourseDescription.setText(course.getDescription());
                tvCourseStatus.setText("Status: " + course.getStatus());
                tvCourseCreatedAt.setText("Created At: " + (course.getCreatedAt() != null ? course.getCreatedAt() : "N/A"));

                // Load image from database's img field using Glide
                String imageName = course.getImg();
                String resourceName = imageName.endsWith(".png") ? imageName.substring(0, imageName.length() - 4) : imageName;
                int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                Glide.with(this)
                        .load(resourceId)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.stat_notify_error)
                        .into(ivCourseImage);

                Log.d(TAG, "Loaded course: " + course.getTitle());
            } else {
                Log.e(TAG, "Course not found for ID: " + courseId);
                finish();
            }
        });

        // Observe lesson count
        viewModel.getLessonCountByCourseId(courseId).observe(this, count -> {
            if (count != null) {
                tvTotalLessons.setText("Total Lessons: " + count);
                Log.d(TAG, "Lesson count for course ID " + courseId + ": " + count);
            } else {
                tvTotalLessons.setText("Total Lessons: 0");
                Log.d(TAG, "No lessons found for course ID: " + courseId);
            }
        });

        // Observe enrollment count
        viewModel.getEnrollmentCountForCourse(courseId).observe(this, count -> {
            if (count != null) {
                tvTotalEnrollments.setText("Total Enrollments: " + count);
                Log.d(TAG, "Enrollment count for course ID " + courseId + ": " + count);
            } else {
                tvTotalEnrollments.setText("Total Enrollments: 0");
                Log.d(TAG, "No enrollments found for course ID: " + courseId);
            }
        });
    }
}