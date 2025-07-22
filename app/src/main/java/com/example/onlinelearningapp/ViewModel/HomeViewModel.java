package com.example.onlinelearningapp.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Lesson;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Course>> topCourses;
    private LiveData<List<Lesson>> newestLessons;

    public HomeViewModel(Application application) {
        super(application);
        repository = new Repository(application);
        topCourses = repository.getTop5CoursesByRecentCreation();
        newestLessons = repository.getTop5NewestLessons();
    }

    public LiveData<List<Course>> getTop5CoursesByRecentCreation() {
        return topCourses;
    }

    public LiveData<List<Lesson>> getTop5NewestLessons() {
        return newestLessons;
    }
}
