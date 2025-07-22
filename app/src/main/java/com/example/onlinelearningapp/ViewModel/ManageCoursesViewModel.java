package com.example.onlinelearningapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;

import java.util.List;

public class ManageCoursesViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Course>> allCourses;
    private LiveData<Integer> courseCount;

    public ManageCoursesViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        allCourses = repository.getAllCourses();
        courseCount = repository.getCourseCount();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<Integer> getCourseCount() {
        return courseCount;
    }

    public void deleteCourse(Course course) {
        repository.deleteCourse(course);
    }
}
