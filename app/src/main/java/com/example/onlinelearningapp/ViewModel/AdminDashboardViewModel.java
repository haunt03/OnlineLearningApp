package com.example.onlinelearningapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.DataHelper.Repository;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.Entity.User;

import java.util.List;

public class AdminDashboardViewModel extends AndroidViewModel {
    private Repository repository;

    private LiveData<Integer> userCount;
    private LiveData<Integer> courseCount;
    private LiveData<Integer> lessonCount;
    private LiveData<Integer> quizCount;
    private LiveData<List<User>> recentUsers;
    private LiveData<List<Course>> inProgressCourses;
    private LiveData<List<Lesson>> inProgressLessons;

    public AdminDashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        userCount = repository.getUserCount();
        courseCount = repository.getCourseCount();
        lessonCount = repository.getLessonCount();
        quizCount = repository.getQuizCount();
        recentUsers = repository.getRecentUsers();
        inProgressCourses = repository.getInProgressCourses();
        inProgressLessons = repository.getInProgressLessons();
    }

    public LiveData<Integer> getUserCount() {
        return userCount;
    }

    public LiveData<Integer> getCourseCount() {
        return courseCount;
    }

    public LiveData<Integer> getLessonCount() {
        return lessonCount;
    }

    public LiveData<Integer> getQuizCount() {
        return quizCount;
    }

    public LiveData<List<User>> getRecentUsers() {
        return recentUsers;
    }

    public LiveData<List<Course>> getInProgressCourses() {
        return inProgressCourses;
    }

    public LiveData<List<Lesson>> getInProgressLessons() {
        return inProgressLessons;
    }
}