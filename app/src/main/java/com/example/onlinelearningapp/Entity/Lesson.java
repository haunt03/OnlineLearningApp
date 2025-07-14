package com.example.onlinelearningapp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Lessons",
        foreignKeys = @ForeignKey(entity = Course.class,
                parentColumns = "CourseID",
                childColumns = "CourseID",
                onDelete = ForeignKey.CASCADE))
public class Lesson {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LessonID")
    private int lessonId;

    @ColumnInfo(name = "CourseID")
    private int courseId;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Content")
    private String content;

    @ColumnInfo(name = "Img")
    private String img;

    @ColumnInfo(name = "Status", defaultValue = "'active'")
    private String status;

    // Constructor
    public Lesson(int courseId, String title, String content, String img) {
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.img = img;
        this.status = "active"; // Default value
    }

    // Getters and Setters
    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

