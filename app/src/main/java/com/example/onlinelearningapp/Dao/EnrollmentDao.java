package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Enrollment;

import java.util.List;

@Dao
public interface EnrollmentDao {
    @Insert
    long insertEnrollment(Enrollment enrollment);

    @Update
    void updateEnrollment(Enrollment enrollment);

    @Query("SELECT * FROM Enrollments WHERE UserID = :userId AND CourseID = :courseId")
    LiveData<Enrollment> getEnrollment(int userId, int courseId);

    @Query("SELECT COUNT(*) FROM Enrollments WHERE CourseID = :courseId")
    LiveData<Integer> getEnrollmentCountForCourse(int courseId);

    @Query("SELECT * FROM Enrollments WHERE UserID = :userId")
    LiveData<List<Enrollment>> getEnrollmentsByUserId(int userId);

    @Transaction
    @Query("SELECT C.* FROM Courses C INNER JOIN Enrollments E ON C.CourseID = E.CourseID WHERE E.UserID = :userId")
    LiveData<List<Course>> getEnrolledCoursesWithDetails(int userId);

    @Query("DELETE FROM Enrollments WHERE UserID = :userId AND CourseID = :courseId") // New method to delete specific enrollment
    void deleteEnrollment(int userId, int courseId);

    @Query("DELETE FROM Enrollments")
    void deleteAllEnrollments();
}
