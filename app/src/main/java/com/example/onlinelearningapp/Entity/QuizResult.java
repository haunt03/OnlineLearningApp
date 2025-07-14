package com.example.onlinelearningapp.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "QuizResults",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "UserID",
                        childColumns = "UserID",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Quiz.class,
                        parentColumns = "QuizID",
                        childColumns = "QuizID",
                        onDelete = ForeignKey.CASCADE)
        })
public class QuizResult {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ResultID")
    private int resultId;

    @ColumnInfo(name = "UserID")
    private int userId;

    @ColumnInfo(name = "QuizID")
    private int quizId;

    @ColumnInfo(name = "Score")
    private Integer score; // Can be null if not yet scored

    @ColumnInfo(name = "TakenAt", defaultValue = "CURRENT_TIMESTAMP")
    private String takenAt;

    // Constructor
    public QuizResult(int userId, int quizId, Integer score) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    // Getters and Setters
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
    }
}
