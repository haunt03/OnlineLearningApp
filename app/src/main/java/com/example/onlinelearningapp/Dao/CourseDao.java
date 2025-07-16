package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    long insertCourse(Course course);

    @Update
    void updateCourse(Course course);

    @Query("SELECT * FROM Courses WHERE CourseID = :courseId")
    LiveData<Course> getCourseById(int courseId);

    @Query("SELECT * FROM Courses ORDER BY CreatedAt DESC LIMIT 5")
    LiveData<List<Course>> getTop5CoursesByRecentCreation(); // Placeholder for "most participated"

    // ADD THIS METHOD
    @Query("SELECT * FROM Courses")
    LiveData<List<Course>> getAllCourses();
}