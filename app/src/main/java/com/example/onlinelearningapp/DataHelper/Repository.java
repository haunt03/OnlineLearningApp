package com.example.onlinelearningapp.DataHelper;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.onlinelearningapp.Dao.AnswerDao;
import com.example.onlinelearningapp.Dao.CourseDao;
import com.example.onlinelearningapp.Dao.EnrollmentDao;
import com.example.onlinelearningapp.Dao.LessonDao;
import com.example.onlinelearningapp.Dao.OptionDao;
import com.example.onlinelearningapp.Dao.ProgressDao;
import com.example.onlinelearningapp.Dao.QuestionDao;
import com.example.onlinelearningapp.Dao.QuizDao;
import com.example.onlinelearningapp.Dao.QuizResultDao;
import com.example.onlinelearningapp.Dao.UserDao;
import com.example.onlinelearningapp.Entity.Answer;
import com.example.onlinelearningapp.Entity.Course;
import com.example.onlinelearningapp.Entity.Enrollment;
import com.example.onlinelearningapp.Entity.Lesson;
import com.example.onlinelearningapp.Entity.Option;
import com.example.onlinelearningapp.Entity.Progress;
import com.example.onlinelearningapp.Entity.Question;
import com.example.onlinelearningapp.Entity.Quiz;
import com.example.onlinelearningapp.Entity.QuizResult;
import com.example.onlinelearningapp.Entity.User;

import java.util.List;

public class Repository {
    private UserDao userDao;
    private CourseDao courseDao;
    private LessonDao lessonDao;
    private EnrollmentDao enrollmentDao;
    private QuizDao quizDao;
    private QuestionDao questionDao;
    private OptionDao optionDao;
    private AnswerDao answerDao;
    private QuizResultDao quizResultDao;
    private ProgressDao progressDao;

    public Repository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        courseDao = db.courseDao();
        lessonDao = db.lessonDao();
        enrollmentDao = db.enrollmentDao();
        quizDao = db.quizDao();
        questionDao = db.questionDao();
        optionDao = db.optionDao();
        answerDao = db.answerDao();
        quizResultDao = db.quizResultDao();
        progressDao = db.progressDao();
    }

    // --- User operations ---
    public LiveData<User> getUserByEmailAndPassword(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, password);
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public void insertUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.updateUser(user));
    }

    // --- Course operations ---
    public LiveData<List<Course>> getTop5CoursesByRecentCreation() {
        return courseDao.getTop5CoursesByRecentCreation();
    }

    public LiveData<Course> getCourseById(int courseId) {
        return courseDao.getCourseById(courseId);
    }

    // --- Lesson operations ---
    public LiveData<List<Lesson>> getTop5NewestLessons() {
        return lessonDao.getTop5NewestLessons();
    }

    public LiveData<Lesson> getLessonById(int lessonId) {
        return lessonDao.getLessonById(lessonId);
    }

    public LiveData<List<Lesson>> getLessonsByCourseId(int courseId) {
        return lessonDao.getLessonsByCourseId(courseId);
    }

    // --- Enrollment operations ---
    public LiveData<Integer> getEnrollmentCountForCourse(int courseId) {
        return enrollmentDao.getEnrollmentCountForCourse(courseId);
    }

    public LiveData<Enrollment> getEnrollment(int userId, int courseId) {
        return enrollmentDao.getEnrollment(userId, courseId);
    }

    public void insertEnrollment(Enrollment enrollment) {
        AppDatabase.databaseWriteExecutor.execute(() -> enrollmentDao.insertEnrollment(enrollment));
    }

    // ADD THIS MISSING METHOD
    public LiveData<List<Enrollment>> getEnrollmentsByUserId(int userId) {
        return enrollmentDao.getEnrollmentsByUserId(userId);
    }

    // --- Quiz operations ---
    public LiveData<List<Quiz>> getQuizzesByLessonId(int lessonId) {
        return quizDao.getQuizzesByLessonId(lessonId);
    }

    // --- Question operations ---
    public LiveData<List<Question>> getQuestionsByQuizId(int quizId) {
        return questionDao.getQuestionsByQuizId(quizId);
    }

    // --- Option operations ---
    public LiveData<List<Option>> getOptionsByQuestionId(int questionId) {
        return optionDao.getOptionsByQuestionId(questionId);
    }

    // --- Answer operations ---
    public void insertAnswer(Answer answer) {
        AppDatabase.databaseWriteExecutor.execute(() -> answerDao.insertAnswer(answer));
    }

    // --- QuizResult operations ---
    public void insertQuizResult(QuizResult quizResult) {
        AppDatabase.databaseWriteExecutor.execute(() -> quizResultDao.insertQuizResult(quizResult));
    }

    // --- Progress operations ---
    public LiveData<Progress> getUserProgressForLesson(int userId, int lessonId) {
        return progressDao.getUserProgressForLesson(userId, lessonId);
    }

    public void insertProgress(Progress progress) {
        AppDatabase.databaseWriteExecutor.execute(() -> progressDao.insertProgress(progress));
    }

    public void updateProgress(Progress progress) {
        AppDatabase.databaseWriteExecutor.execute(() -> progressDao.updateProgress(progress));
    }
}