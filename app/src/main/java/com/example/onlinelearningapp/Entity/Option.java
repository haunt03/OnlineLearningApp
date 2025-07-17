package com.example.onlinelearningapp.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// Assuming Question entity exists and Option is linked to it
@Entity(tableName = "Options",
        foreignKeys = @ForeignKey(entity = Question.class,
                parentColumns = "QuestionID",
                childColumns = "QuestionID",
                onDelete = ForeignKey.CASCADE))
public class Option {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OptionID")
    private int optionID; // Make sure this matches your column name

    @ColumnInfo(name = "QuestionID")
    private int questionID;

    @ColumnInfo(name = "OptionText")
    private String optionText;

    @ColumnInfo(name = "IsCorrect")
    private boolean isCorrect;

    // Constructor
    public Option(int questionID, String optionText, boolean isCorrect) {
        this.questionID = questionID;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getOptionID() { // Ensure this getter exists and matches case
        return optionID;
    }

    public void setOptionID(int optionID) {
        this.optionID = optionID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() { // Standard getter for boolean is 'is' or 'get'
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}