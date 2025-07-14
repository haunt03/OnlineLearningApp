package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
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
    private Repository repository;
    private LiveData<User> currentUser;
    private LiveData<List<Enrollment>> userEnrollments;

    // MediatorLiveData to combine user enrollments with course details
    private MediatorLiveData<List<Course>> enrolledCoursesWithDetails = new MediatorLiveData<>();

    public UserProfileViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public void loadUserProfile(int userId) {
        currentUser = repository.getUserById(userId);
        userEnrollments = repository.getEnrollmentsByUserId(userId);

        // Observe userEnrollments and fetch course details for each enrollment
        enrolledCoursesWithDetails.addSource(userEnrollments, enrollments -> {
            if (enrollments != null && !enrollments.isEmpty()) {
                List<LiveData<Course>> courseLiveDataList = new ArrayList<>();
                for (Enrollment enrollment : enrollments) {
                    courseLiveDataList.add(repository.getCourseById(enrollment.getCourseId()));
                }
                // Combine all LiveData<Course> into a single LiveData<List<Course>>
                // This is a simplified approach. For a more robust solution,
                // you might need a custom LiveData or combine multiple sources.
                // For now, we'll iterate and update.
                combineCourseDetails(courseLiveDataList);
            } else {
                enrolledCoursesWithDetails.postValue(new ArrayList<>()); // No enrollments
            }
        });
    }

    private void combineCourseDetails(List<LiveData<Course>> courseLiveDataList) {
        List<Course> courses = new ArrayList<>();
        final int[] loadedCount = {0}; // To track how many courses have been loaded

        if (courseLiveDataList.isEmpty()) {
            enrolledCoursesWithDetails.postValue(courses);
            return;
        }

        for (LiveData<Course> courseLiveData : courseLiveDataList) {
            enrolledCoursesWithDetails.addSource(courseLiveData, course -> {
                if (course != null) {
                    courses.add(course);
                }
                loadedCount[0]++;
                if (loadedCount[0] == courseLiveDataList.size()) {
                    enrolledCoursesWithDetails.postValue(courses);
                    // Remove sources to prevent redundant updates if not needed
                    for (LiveData<Course> ld : courseLiveDataList) {
                        enrolledCoursesWithDetails.removeSource(ld);
                    }
                }
            });
        }
    }


    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<List<Course>> getEnrolledCoursesWithDetails() {
        return enrolledCoursesWithDetails;
    }
}

