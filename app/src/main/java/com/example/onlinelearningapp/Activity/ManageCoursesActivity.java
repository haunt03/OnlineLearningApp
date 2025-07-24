package com.example.onlinelearningapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Adapter.CourseAdminAdapter;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.ManageCoursesViewModel;

public class ManageCoursesActivity extends AppCompatActivity {
    private static final String TAG = "ManageCoursesActivity";
    private ManageCoursesViewModel viewModel;
    private CourseAdminAdapter adapter;
    private RecyclerView rvCourses;
    private TextView tvTotalCourses;
    private Course selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Courses Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Log.d("ManageCourses", "Back button clicked, returning to AdminDashboardActivity");
            finish();
        });

        // Initialize RecyclerView
        rvCourses = findViewById(R.id.rv_courses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        // Initialize CourseAdminAdapter
        adapter = new CourseAdminAdapter(
                course -> {
                    // Handle item click: Start LessonOfCourseActivity with course ID
                    Intent intent = new Intent(ManageCoursesActivity.this, LessonOfCourseActivity.class);
                    intent.putExtra("courseId", course.getCourseId());
                    startActivity(intent);
                    Log.d(TAG, "Navigating to LessonOfCourseActivity with course ID: " + course.getCourseId());
                },
                course -> {
                    // Handle delete click
                    viewModel.deleteCourse(course);
                    Log.d(TAG, "Delete Course clicked for: " + course.getTitle());
                },
                (course, view) -> {
                    // Handle long click to show context menu
                    selectedCourse = course;
                    view.showContextMenu();

                }
        );
        rvCourses.setAdapter(adapter);

        // Register RecyclerView for context menu
        registerForContextMenu(rvCourses);

        // Initialize TextView for total courses
        tvTotalCourses = findViewById(R.id.tv_total_courses);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ManageCoursesViewModel.class);

        // Observe course list
        viewModel.getAllCourses().observe(this, courses -> {
            adapter.setCourses(courses);
        });

        // Observe course count
        viewModel.getCourseCount().observe(this, count -> {
            tvTotalCourses.setText("Total Number of Courses: " + count);
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.rv_courses && selectedCourse != null) {
            menu.setHeaderTitle(selectedCourse.getTitle());
            getMenuInflater().inflate(R.menu.course_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedCourse == null) {
            return false;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.action_view_course_details) {
            Log.d(TAG, "View Course Details selected for: " + selectedCourse.getTitle());
            Intent intent = new Intent(this, CourseDetailsAdminActivity.class);
            intent.putExtra("courseId", selectedCourse.getCourseId());
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_update_course) {
            Log.d(TAG, "View Course Details selected for: " + selectedCourse.getTitle());
            Intent intent = new Intent(this, UpdateInfoCourseActivity.class);
            intent.putExtra("courseId", selectedCourse.getCourseId());
            startActivity(intent);
            return true;
        // them code vao day
        } else if (itemId == R.id.action_delete_course) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete the course: " + selectedCourse.getTitle() + "?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewModel.deleteCourse(selectedCourse);
                            Log.d(TAG, "Delete Course selected for: " + selectedCourse.getTitle());
                            Toast.makeText(getApplicationContext(), "Course deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        }

        return super.onContextItemSelected(item);
    }
}