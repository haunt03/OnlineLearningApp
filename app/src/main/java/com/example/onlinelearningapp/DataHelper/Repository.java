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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    private LiveData<List<User>> activeUsers;
    private LiveData<List<User>> inactiveUsers;

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

    public LiveData<Integer> getUserCount() {
        return userDao.getUserCount();
    }

    public LiveData<List<User>> getRecentUsers() {
        return userDao.getRecentUsers();
    }

    // --- Course operations ---
    public LiveData<List<Course>> getTop5CoursesByRecentCreation() {
        return courseDao.getTop5CoursesByRecentCreation();
    }

    public LiveData<Course> getCourseById(int courseId) {
        return courseDao.getCourseById(courseId);
    }

    public LiveData<Integer> getCourseCount() {
        return courseDao.getCourseCount();
    }

    public LiveData<List<Course>> getInProgressCourses() {
        return courseDao.getInProgressCourses();
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

    public LiveData<Integer> getLessonCount() {
        return lessonDao.getLessonCount();
    }

    public LiveData<Integer> getLessonCountByCourseId(int courseId) {
        return lessonDao.getLessonCountByCourseId(courseId);
    }

    public LiveData<Enrollment> getEnrollment(int userId, int courseId) {
        return enrollmentDao.getEnrollment(userId, courseId);
    }

    public void insertEnrollment(Enrollment enrollment) {
        AppDatabase.databaseWriteExecutor.execute(() -> enrollmentDao.insertEnrollment(enrollment));
    }

    // New method to delete enrollment
    public void deleteEnrollment(int userId, int courseId) {
        AppDatabase.databaseWriteExecutor.execute(() -> enrollmentDao.deleteEnrollment(userId, courseId));
    }

    public LiveData<List<Enrollment>> getEnrollmentsByUserId(int userId) {
        return enrollmentDao.getEnrollmentsByUserId(userId);
    }

    public LiveData<List<Course>> getEnrolledCoursesWithDetails(int userId) {
        return enrollmentDao.getEnrolledCoursesWithDetails(userId);
    }

    // --- Quiz operations ---
    public LiveData<List<Quiz>> getQuizzesByLessonId(int lessonId) {
        return quizDao.getQuizzesByLessonId(lessonId);
    }

    public LiveData<Quiz> getQuizById(int quizId) {
        return quizDao.getQuizById(quizId);
    }

    // --- Question operations ---
    public LiveData<List<Question>> getQuestionsByQuizId(int quizId) {
        return questionDao.getQuestionsByQuizId(quizId);
    }

    public LiveData<Integer> getQuizCount() {
        return quizDao.getQuizCount();
    }

    // --- Option operations ---
    public LiveData<List<Option>> getOptionsByQuestionId(int questionId) {
        return optionDao.getOptionsByQuestionId(questionId);
    }

    public List<Option> getOptionsByQuestionIdSync(int questionId) throws ExecutionException, InterruptedException {
        Callable<List<Option>> callable = () -> optionDao.getOptionsByQuestionIdSync(questionId);
        Future<List<Option>> future = AppDatabase.databaseWriteExecutor.submit(callable);
        return future.get();
    }

    // --- Answer operations ---
    public void insertAnswer(Answer answer) {
        AppDatabase.databaseWriteExecutor.execute(() -> answerDao.insertAnswer(answer));
    }

    public LiveData<List<Answer>> getAnswersByUserIdAndQuizId(int userId, int quizId) {
        return answerDao.getAnswersByUserIdAndQuizId(userId, quizId);
    }

    // --- QuizResult operations ---
    public void insertQuizResult(QuizResult quizResult) {
        AppDatabase.databaseWriteExecutor.execute(() -> quizResultDao.insertQuizResult(quizResult));
    }

    public User getUserByEmailSync(String email) {
        return userDao.getUserByEmailSync(email); // DAO cần thêm hàm tương ứng
    }

    public LiveData<List<Lesson>> getInProgressLessons() {
        return lessonDao.getInProgressLessons();
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void deleteUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> userDao.deleteUser(user));
    }

    public LiveData<List<User>> getAllUsersByRole() {
        return userDao.getAllUsersByRole();
    }

    public LiveData<List<User>> getActiveUsers() {
        return activeUsers;
    }

    public LiveData<List<User>> getInactiveUsers() {
        return inactiveUsers;
    }

    public LiveData<List<Course>> getAllCourses() {
        return courseDao.getAllCourses();
    }

    public void deleteCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> courseDao.deleteCourse(course));
    }

    public LiveData<Integer> getEnrollmentCountForCourse(int courseId) {
        return enrollmentDao.getEnrollmentCountForCourse(courseId);
    }

    public void updateCourse(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> courseDao.updateCourse(course));
    }

}

