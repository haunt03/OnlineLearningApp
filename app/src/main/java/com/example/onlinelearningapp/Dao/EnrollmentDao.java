package com.example.onlinelearningapp.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM Enrollments WHERE UserID = :userId")
    LiveData<List<Enrollment>> getEnrollmentsByUserId(int userId);

    @Query("SELECT COUNT(EnrollmentID) FROM Enrollments WHERE CourseID = :courseId")
    LiveData<Integer> getEnrollmentCountForCourse(int courseId);
}

