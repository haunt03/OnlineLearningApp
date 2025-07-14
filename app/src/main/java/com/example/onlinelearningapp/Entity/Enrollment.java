package com.example.onlinelearningapp.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Enrollments",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "UserID",
                        childColumns = "UserID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Course.class,
                        parentColumns = "CourseID",
                        childColumns = "CourseID",
                        onDelete = ForeignKey.CASCADE)
        })
public class Enrollment {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "EnrollmentID")
    private int enrollmentId;

    @ColumnInfo(name = "UserID")
    private int userId;

    @ColumnInfo(name = "CourseID")
    private int courseId;

    @ColumnInfo(name = "EnrollDate", defaultValue = "CURRENT_TIMESTAMP")
    private String enrollDate;

    @ColumnInfo(name = "Status", defaultValue = "'enrolled'")
    private String status;

    // Constructor
    public Enrollment(int userId, int courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.status = "enrolled"; // Default value
    }

    // Getters and Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
