package com.example.onlinelearningapp.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Options",
        foreignKeys = @ForeignKey(entity = Question.class,
                parentColumns = "QuestionID",
                childColumns = "QuestionID",
                onDelete = ForeignKey.CASCADE))
public class Option {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OptionID")
    private int optionId;

    @ColumnInfo(name = "QuestionID")
    private int questionId;

    @ColumnInfo(name = "OptionText")
    private String optionText;

    @ColumnInfo(name = "IsCorrect")
    private boolean isCorrect; // SQLite stores BOOLEAN as INTEGER (0 or 1)

    // Constructor
    public Option(int questionId, String optionText, boolean isCorrect) {
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}

