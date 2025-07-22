package com.example.onlinelearningapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;

public class CourseDetailsAdminViewModel extends AndroidViewModel {
    private Repository repository;

    public CourseDetailsAdminViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public LiveData<Course> getCourseById(int courseId) {
        return repository.getCourseById(courseId);
    }

    public LiveData<Integer> getLessonCountByCourseId(int courseId) {
        return repository.getLessonCountByCourseId(courseId);
    }

    public LiveData<Integer> getEnrollmentCountForCourse(int courseId) {
        return repository.getEnrollmentCountForCourse(courseId);
    }
}