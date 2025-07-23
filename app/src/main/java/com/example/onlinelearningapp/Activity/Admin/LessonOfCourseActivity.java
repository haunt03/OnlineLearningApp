package com.example.onlinelearningapp.Activity.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Adapter.LessonAdminAdapter;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.LessonOfCourseViewModel;

public class LessonOfCourseActivity extends AppCompatActivity {
    private static final String TAG = "LessonOfCourseActivity";
    private LessonOfCourseViewModel viewModel;
    private LessonAdminAdapter adapter;
    private RecyclerView rvLessons;
    private TextView tvNoLessons;
    private TextView tvTotalLessons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_of_course);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List of Lessons");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Log.d("ListofLessons", "Back button clicked, returning to Courses Management");
            finish();
        });

        // Initialize TextViews
        tvNoLessons = findViewById(R.id.tv_no_lessons);
        tvTotalLessons = findViewById(R.id.tv_total_lessons);
        if (tvNoLessons == null) {
            Log.e(TAG, "tv_no_lessons is null - check activity_lesson_of_course.xml");
        }
        if (tvTotalLessons == null) {
            Log.e(TAG, "tv_total_lessons is null - check activity_lesson_of_course.xml");
        }

        // Get course ID from Intent
        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId == -1) {
            Log.e(TAG, "No course ID provided");
            if (tvNoLessons != null) {
                tvNoLessons.setText("Error: No course ID provided");
                tvNoLessons.setVisibility(View.VISIBLE);
            }
            finish();
            return;
        }

        // Initialize RecyclerView
        rvLessons = findViewById(R.id.rv_lessons);
        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LessonAdminAdapter();
        rvLessons.setAdapter(adapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LessonOfCourseViewModel.class);

        // Observe lessons for the course
        viewModel.getLessonsByCourseId(courseId).observe(this, lessons -> {
            if (lessons != null && !lessons.isEmpty()) {
                adapter.setLessons(lessons);
                rvLessons.setVisibility(View.VISIBLE);
                if (tvNoLessons != null) {
                    tvNoLessons.setVisibility(View.GONE);
                }
                if (tvTotalLessons != null) {
                    tvTotalLessons.setText("Total Number of Lessons: " + lessons.size());
                }
                Log.d(TAG, "Loaded " + lessons.size() + " lessons for course ID: " + courseId);
            } else {
                rvLessons.setVisibility(View.GONE);
                if (tvNoLessons != null) {
                    tvNoLessons.setVisibility(View.VISIBLE);
                    tvNoLessons.setText("No lessons available for this course");
                }
                if (tvTotalLessons != null) {
                    tvTotalLessons.setText("Total Number of Lessons: 0");
                }
                Log.d(TAG, "No lessons found for course ID: " + courseId);
            }
        });
    }
}