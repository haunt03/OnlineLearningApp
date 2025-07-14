package com.example.onlinelearningapp.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Progress",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "UserID",
                        childColumns = "UserID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Lesson.class,
                        parentColumns = "LessonID",
                        childColumns = "LessonID",
                        onDelete = ForeignKey.CASCADE)
        })
public class Progress {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ProgressID")
    private int progressId;

    @ColumnInfo(name = "UserID")
    private int userId;

    @ColumnInfo(name = "LessonID")
    private int lessonId;

    @ColumnInfo(name = "Status", defaultValue = "'not_started'")
    private String status; // 'not_started', 'in_progress', 'completed'

    @ColumnInfo(name = "UpdatedAt", defaultValue = "CURRENT_TIMESTAMP")
    private String updatedAt;

    // Constructor
    public Progress(int userId, int lessonId, String status) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.status = status;
    }

    // Getters and Setters
    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
