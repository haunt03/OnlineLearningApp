package com.example.onlinelearningapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.CourseDetailsAdminViewModel;

public class UpdateInfoCourseActivity extends AppCompatActivity {

    private static final String TAG = "UpdateInfoCourse";
    private CourseDetailsAdminViewModel viewModel;
    private Course currentCourse;
    private Spinner spinnerStatus;
    private TextView tvStatusDisplay; // TextView phụ để hiển thị trạng thái với màu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info_course);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Course Information");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "Back button clicked, returning to previous activity");
            finish();
        });

        // Get courseId from Intent
        int courseId = getIntent().getIntExtra("courseId", -1);
        if (courseId == -1) {
            Log.e(TAG, "Invalid courseId received");
            Toast.makeText(this, "Error: Invalid course ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CourseDetailsAdminViewModel.class);

        // Initialize UI elements
        TextView tvCourseId = findViewById(R.id.tv_course_id);
        EditText etTitle = findViewById(R.id.et_title);
        EditText etDescription = findViewById(R.id.et_description);
        EditText etImage = findViewById(R.id.et_image);
        spinnerStatus = findViewById(R.id.spinner_status);
        TextView tvCreatedAt = findViewById(R.id.tv_created_at);
        Button btnSave = findViewById(R.id.btn_save);

        // Set up Spinner for status
        String[] statusOptions = {"active", "inactive"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Observe course data
        viewModel.getCourseById(courseId).observe(this, course -> {
            if (course != null) {
                currentCourse = course;
                // Populate UI
                tvCourseId.setText(String.valueOf(course.getCourseId()));
                etTitle.setText(course.getTitle() != null ? course.getTitle() : "N/A");
                etDescription.setText(course.getDescription() != null ? course.getDescription() : "N/A");
                etImage.setText(course.getImg() != null ? course.getImg() : "N/A");
                tvCreatedAt.setText(course.getCreatedAt() != null ? course.getCreatedAt() : "N/A");

                // Set Spinner selection
                if (course.getStatus() != null) {
                    spinnerStatus.setSelection(course.getStatus().equals("active") ? 0 : 1);
                }

                Log.d(TAG, "Displaying course: ID=" + course.getCourseId() + ", Title=" + course.getTitle());
            } else {
                Log.e(TAG, "Course not found for courseId: " + courseId);
                Toast.makeText(this, "Error: Course not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Handle Save button click
        btnSave.setOnClickListener(v -> {
            if (currentCourse != null) {
                // Update course with new values
                String newTitle = etTitle.getText().toString().trim();
                String newDescription = etDescription.getText().toString().trim();
                String newImage = etImage.getText().toString().trim();
                String newStatus = spinnerStatus.getSelectedItem().toString();

                // Basic validation
                if (newTitle.isEmpty() || newDescription.isEmpty() || newImage.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentCourse.setTitle(newTitle);
                currentCourse.setDescription(newDescription);
                currentCourse.setImg(newImage);
                currentCourse.setStatus(newStatus);

                // Update course in database
                viewModel.getRepository().updateCourse(currentCourse);
                Log.d(TAG, "Updated course info: ID=" + currentCourse.getCourseId() + ", Title=" + newTitle + ", Status=" + newStatus);
                Toast.makeText(this, "Course information updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "Cannot save: currentCourse is null");
                Toast.makeText(this, "Error: Cannot save course information", Toast.LENGTH_SHORT).show();
            }
        });
    }
}