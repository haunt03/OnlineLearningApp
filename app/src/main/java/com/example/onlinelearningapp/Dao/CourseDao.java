package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void deleteCourse(Course course);

    @Query("SELECT * FROM Courses WHERE CourseID = :courseId")
    LiveData<Course> getCourseById(int courseId);

    @Query("SELECT * FROM Courses ORDER BY CreatedAt DESC LIMIT 5")
    LiveData<List<Course>> getTop5CoursesByRecentCreation();

    // ADD THIS METHOD
    @Query("SELECT * FROM Courses")
    LiveData<List<Course>> getAllCourses();

    @Query("DELETE FROM Courses")
    void deleteAllCourses();

    @Query("SELECT COUNT(CourseID) FROM Courses")
    LiveData<Integer> getCourseCount();

    @Query("SELECT DISTINCT c.* FROM Courses c " +
            "INNER JOIN Lessons l ON c.CourseID = l.CourseID " +
            "INNER JOIN Progress p ON l.LessonID = p.LessonID " +
            "WHERE p.Status = 'in_progress' LIMIT 5")
    LiveData<List<Course>> getInProgressCourses();
}