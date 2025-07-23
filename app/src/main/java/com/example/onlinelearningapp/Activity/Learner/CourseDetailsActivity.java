package com.example.onlinelearningapp.Activity.Learner;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelearningapp.Activity.LoginActivity;
import com.example.onlinelearningapp.Adapter.LessonAdapter;
import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.R;
import com.example.onlinelearningapp.ViewModel.CourseDetailsViewModel;
import com.example.onlinelearningapp.ViewModel.UserProfileViewModel;

import java.util.ArrayList;

public class CourseDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CourseDetailsActivity"; // Define TAG for logging

    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_COURSE_TITLE = "extra_course_title";

    private TextView tvCourseDetailsTitle;
    private RecyclerView rvLessonsInCourse;
    private Button btnEnrollCourseDetails;
    private LessonAdapter lessonAdapter;
    private CourseDetailsViewModel courseDetailsViewModel;
    private UserProfileViewModel userProfileViewModel;

    private int courseId;
    private int currentUserId = -1;
    private boolean isEnrolled = false; // Flag to track enrollment status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Log.d(TAG, "onCreate: CourseDetailsActivity started.");

        tvCourseDetailsTitle = findViewById(R.id.tv_course_details_title);
        rvLessonsInCourse = findViewById(R.id.rv_lessons_in_course);
        btnEnrollCourseDetails = findViewById(R.id.btn_enroll_course_details);

        SharedPreferences sharedPreferences = getSharedPreferences(HomePageActivity.PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(HomePageActivity.KEY_LOGGED_IN_USER_ID, -1);
        Log.d(TAG, "onCreate: currentUserId = " + currentUserId);


        if (getIntent().hasExtra(EXTRA_COURSE_ID)) {
            courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
            String courseTitle = getIntent().getStringExtra(EXTRA_COURSE_TITLE);
            tvCourseDetailsTitle.setText(courseTitle);
            Log.d(TAG, "onCreate: Received Course ID: " + courseId + ", Title: " + courseTitle);
        } else {
            Toast.makeText(this, "Course not found. Finishing activity.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate: EXTRA_COURSE_ID not found in Intent. Finishing.");
            finish();
            return;
        }

        rvLessonsInCourse.setLayoutManager(new LinearLayoutManager(this));
        lessonAdapter = new LessonAdapter(new ArrayList<>(), lesson -> {
            Log.d(TAG, "Lesson click: LessonID: " + lesson.getLessonId() + ", Title: " + lesson.getTitle());
            if (currentUserId == -1) {
                Toast.makeText(CourseDetailsActivity.this, "Please login to view lesson details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }

            if (isEnrolled) {
                Log.d(TAG, "Lesson click: User is enrolled. Navigating to LessonDetailsActivity.");
                Intent intent = new Intent(CourseDetailsActivity.this, LessonDetailsActivity.class);
                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_ID, lesson.getLessonId());
                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_TITLE, lesson.getTitle());
                startActivity(intent);
            } else {
                Log.d(TAG, "Lesson click: User is NOT enrolled. Showing enrollment confirmation dialog.");
                showEnrollConfirmationDialog(lesson);
            }
        });
        rvLessonsInCourse.setAdapter(lessonAdapter);

        courseDetailsViewModel = new ViewModelProvider(this).get(CourseDetailsViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        Log.d(TAG, "onCreate: ViewModels initialized.");

        // IMPORTANT: Call checkEnrollmentStatus BEFORE observeViewModels()
        // This ensures the LiveData in UserProfileViewModel is initialized by switchMap
        if (currentUserId != -1) {
            Log.d(TAG, "onCreate: User logged in. Calling checkEnrollmentStatus for user " + currentUserId + ", course " + courseId);
            userProfileViewModel.checkEnrollmentStatus(currentUserId, courseId);
        } else {
            isEnrolled = false;
            btnEnrollCourseDetails.setVisibility(View.VISIBLE);
            btnEnrollCourseDetails.setText("Login to Enroll");
            Log.d(TAG, "onCreate: User not logged in. Setting 'Login to Enroll' button state.");
        }

        observeViewModels(); // Now observeViewModels can safely observe the LiveData
        Log.d(TAG, "onCreate: observeViewModels() called.");


        btnEnrollCourseDetails.setOnClickListener(v -> {
            Log.d(TAG, "Enroll button clicked.");
            if (currentUserId != -1) {
                Enrollment newEnrollment = new Enrollment(currentUserId, courseId);
                userProfileViewModel.enrollCourse(newEnrollment);
                Toast.makeText(CourseDetailsActivity.this, "Enrolled in this course!", Toast.LENGTH_SHORT).show();
                isEnrolled = true;
                btnEnrollCourseDetails.setVisibility(View.GONE);
                userProfileViewModel.loadEnrollments(currentUserId);
                userProfileViewModel.checkEnrollmentStatus(currentUserId, courseId); // Re-trigger for UI update
                Log.d(TAG, "Enroll button clicked: User " + currentUserId + " enrolled in course " + courseId + ". UI updated.");
            } else {
                Toast.makeText(CourseDetailsActivity.this, "Please login to enroll in courses.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CourseDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
                Log.d(TAG, "Enroll button clicked: Not logged in, redirecting to LoginActivity.");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: CourseDetailsActivity resumed.");
        if (courseId != -1) {
            courseDetailsViewModel.loadLessonsForCourse(courseId);
            if (currentUserId != -1) {
                Log.d(TAG, "onResume: User logged in. Calling checkEnrollmentStatus for user " + currentUserId + ", course " + courseId);
                userProfileViewModel.checkEnrollmentStatus(currentUserId, courseId);
            } else {
                isEnrolled = false;
                btnEnrollCourseDetails.setVisibility(View.VISIBLE);
                btnEnrollCourseDetails.setText("Login to Enroll");
                Log.d(TAG, "onResume: User not logged in. Setting 'Login to Enroll' button state.");
            }
        }
    }

    private void observeViewModels() {
        Log.d(TAG, "observeViewModels() called.");

        // Observe lessons for the current course
        courseDetailsViewModel.getLessons(courseId).observe(this, lessons -> {
            if (lessons != null) {
                lessonAdapter.setLessons(lessons);
            }
        });


        // Observe enrollment status if a user is logged in
        if (currentUserId != -1) {
            Log.d(TAG, "observeViewModels: Attempting to observe enrollment status for current user.");
            LiveData<Enrollment> enrollmentStatusLiveData = userProfileViewModel.getEnrollmentStatus();

            if (enrollmentStatusLiveData == null) {
                // This block should ideally NOT be reached if UserProfileViewModel's constructor
                // properly initializes liveEnrollmentStatus using Transformations.switchMap.
                Log.e(TAG, "observeViewModels: ERROR! userProfileViewModel.getEnrollmentStatus() returned NULL LiveData object!");
                // Consider adding a Toast or UI feedback here if this happens in production
                return; // Prevent NullPointerException
            }
            Log.d(TAG, "observeViewModels: userProfileViewModel.getEnrollmentStatus() is NOT NULL. Proceeding to observe.");

            enrollmentStatusLiveData.observe(this, enrollment -> { // This is likely line 143
                Log.d(TAG, "observeViewModels: Enrollment status LiveData changed. Enrollment: " + (enrollment != null ? "ENROLLED" : "NOT ENROLLED"));
                if (enrollment == null) {
                    // User is NOT enrolled, show enroll button
                    isEnrolled = false;
                    btnEnrollCourseDetails.setVisibility(View.VISIBLE);
                    btnEnrollCourseDetails.setText("Enroll in this Course");
                } else {
                    // User IS enrolled, hide enroll button
                    isEnrolled = true;
                    btnEnrollCourseDetails.setVisibility(View.GONE);
                }
            });
        } else {
            Log.d(TAG, "observeViewModels: User not logged in, skipping enrollment status observation.");
        }
    }

    private void showEnrollConfirmationDialog(Lesson lessonToAccess) {
        Log.d(TAG, "showEnrollConfirmationDialog: Showing dialog for lesson: " + lessonToAccess.getTitle());
        new AlertDialog.Builder(this)
                .setTitle("Enroll in Course?")
                .setMessage("You need to enroll in this course to view the lesson. Do you want to enroll now?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Log.d(TAG, "Enrollment confirmation dialog: User chose YES.");
                    // User wants to enroll
                    Enrollment newEnrollment = new Enrollment(currentUserId, courseId);
                    userProfileViewModel.enrollCourse(newEnrollment);
                    Toast.makeText(CourseDetailsActivity.this, "Enrolled in this course!", Toast.LENGTH_SHORT).show();
                    isEnrolled = true;
                    btnEnrollCourseDetails.setVisibility(View.GONE);
                    userProfileViewModel.loadEnrollments(currentUserId);
                    userProfileViewModel.checkEnrollmentStatus(currentUserId, courseId); // Re-trigger for UI update

                    // Proceed to lesson details after enrolling
                    Intent intent = new Intent(CourseDetailsActivity.this, LessonDetailsActivity.class);
                    intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_ID, lessonToAccess.getLessonId());
                    intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_TITLE, lessonToAccess.getTitle());
                    startActivity(intent);
                    Log.d(TAG, "Enrollment confirmed. Navigating to LessonDetailsActivity.");
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Log.d(TAG, "Enrollment confirmation dialog: User chose NO.");
                    // User does not want to enroll, stay on CourseDetailsActivity
                    Toast.makeText(CourseDetailsActivity.this, "Enrollment cancelled.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
