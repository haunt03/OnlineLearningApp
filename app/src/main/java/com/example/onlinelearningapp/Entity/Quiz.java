package com.example.onlinelearningapp.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Quizzes",
        foreignKeys = @ForeignKey(entity = Lesson.class,
                parentColumns = "LessonID",
                childColumns = "LessonID",
                onDelete = ForeignKey.CASCADE))
public class Quiz {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "QuizID")
    private int quizId;

    @ColumnInfo(name = "LessonID")
    private int lessonId;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Duration", defaultValue = "10")
    private int duration;

    // Constructor
    public Quiz(int lessonId, String title, int duration) {
        this.lessonId = lessonId;
        this.title = title;
        this.duration = duration;
    }

    // Getters and Setters
    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
