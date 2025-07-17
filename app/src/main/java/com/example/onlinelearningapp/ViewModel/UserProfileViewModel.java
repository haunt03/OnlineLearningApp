package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.Entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserProfileViewModel extends AndroidViewModel {
    private static final String TAG = "UserProfileViewModel"; // Define TAG for logging

    private Repository repository;
    private LiveData<User> currentUser;
    private MutableLiveData<List<Enrollment>> enrollments = new MutableLiveData<>(new ArrayList<>());
    private MediatorLiveData<List<Course>> enrolledCoursesWithDetails = new MediatorLiveData<>();

    private MutableLiveData<Pair<Integer, Integer>> enrollmentStatusTrigger = new MutableLiveData<>();
    private LiveData<Enrollment> liveEnrollmentStatus;


    public UserProfileViewModel(Application application) {
        super(application);
        Log.d(TAG, "Constructor: UserProfileViewModel is being created.");
        repository = new Repository(application);

        // Initialize liveEnrollmentStatus using Transformations.switchMap
        // This LiveData will react to changes in enrollmentStatusTrigger
        liveEnrollmentStatus = Transformations.switchMap(enrollmentStatusTrigger, input -> {
            Log.d(TAG, "SwitchMap: Triggered with input: " + input);
            if (input != null && input.first != null && input.second != null) {
                Log.d(TAG, "SwitchMap: Fetching enrollment for userId: " + input.first + ", courseId: " + input.second);
                return repository.getEnrollment(input.first, input.second);
            }
            // Ensure a non-null LiveData is always returned, even if its value is null
            MutableLiveData<Enrollment> emptyLiveData = new MutableLiveData<>(null);
            Log.d(TAG, "SwitchMap: Returning empty LiveData (input was null/invalid).");
            return emptyLiveData;
        });
        Log.d(TAG, "Constructor: liveEnrollmentStatus initialized. Is it null? " + (liveEnrollmentStatus == null));
    }

    public void loadUserProfile(int userId) {
        Log.d(TAG, "loadUserProfile: Loading profile for userId: " + userId);
        currentUser = repository.getUserById(userId);
        loadEnrolledCoursesWithDetails(userId);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void loadEnrollments(int userId) {
        Log.d(TAG, "loadEnrollments: Loading enrollments for userId: " + userId);
        LiveData<List<Enrollment>> newSource = repository.getEnrollmentsByUserId(userId);
        newSource.observeForever(newEnrollments -> {
            Log.d(TAG, "loadEnrollments: Observed new enrollments. Count: " + (newEnrollments != null ? newEnrollments.size() : "null"));
            enrollments.postValue(newEnrollments);
        });
    }

    public LiveData<List<Enrollment>> getEnrollments() {
        return enrollments;
    }

    public void checkEnrollmentStatus(int userId, int courseId) {
        Log.d(TAG, "checkEnrollmentStatus: Setting trigger for userId: " + userId + ", courseId: " + courseId);
        enrollmentStatusTrigger.setValue(new Pair<>(userId, courseId));
    }

    public LiveData<Enrollment> getEnrollmentStatus() {
        Log.d(TAG, "getEnrollmentStatus: Called. Is liveEnrollmentStatus null? " + (liveEnrollmentStatus == null));
        return liveEnrollmentStatus;
    }

    public void enrollCourse(Enrollment enrollment) {
        Log.d(TAG, "enrollCourse: Enrolling userId: " + enrollment.getUserId() + ", courseId: " + enrollment.getCourseId());
        repository.insertEnrollment(enrollment);
    }

    public void dropOutCourse(int userId, int courseId) {
        Log.d(TAG, "dropOutCourse: Dropping out userId: " + userId + ", courseId: " + courseId);
        repository.deleteEnrollment(userId, courseId);
    }

    private void loadEnrolledCoursesWithDetails(int userId) {
        Log.d(TAG, "loadEnrolledCoursesWithDetails: Loading enrolled courses with details for userId: " + userId);
        LiveData<List<Course>> source = repository.getEnrolledCoursesWithDetails(userId);
        enrolledCoursesWithDetails.addSource(source, courses -> {
            Log.d(TAG, "loadEnrolledCoursesWithDetails: Observed new course details. Count: " + (courses != null ? courses.size() : "null"));
            enrolledCoursesWithDetails.setValue(courses);
        });
    }

    public LiveData<List<Course>> getEnrolledCoursesWithDetails() {
        return enrolledCoursesWithDetails;
    }
}
