package com.example.onlinelearningapp.DataHelper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Course.class, Lesson.class, Enrollment.class,
        Quiz.class, Question.class, Option.class, Answer.class,
        QuizResult.class, Progress.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CourseDao courseDao();
    public abstract LessonDao lessonDao();
    public abstract EnrollmentDao enrollmentDao();
    public abstract QuizDao quizDao();
    public abstract QuestionDao questionDao();
    public abstract OptionDao optionDao();
    public abstract AnswerDao answerDao();
    public abstract QuizResultDao quizResultDao();
    public abstract ProgressDao progressDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "learning_system_db")
                            .addCallback(sRoomDatabaseCallback) // Add callback for pre-population
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback to pre-populate the database with sample data
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Get DAOs
                UserDao userDao = INSTANCE.userDao();
                CourseDao courseDao = INSTANCE.courseDao();
                LessonDao lessonDao = INSTANCE.lessonDao();
                QuizDao quizDao = INSTANCE.quizDao();
                QuestionDao questionDao = INSTANCE.questionDao();
                OptionDao optionDao = INSTANCE.optionDao();
                EnrollmentDao enrollmentDao = INSTANCE.enrollmentDao();
                ProgressDao progressDao = INSTANCE.progressDao();

                // Clear all data (for development/testing) - Note: This is for initial creation, not for every app start
                // Consider adding a check if data already exists before inserting
                // For now, we assume this runs only once when DB is created.

                // 👤 Sample Users
                long userId1 = userDao.insertUser(new User("HauNT", "haunt@gmail.com", "123456", 0)); // Learner
                long userId2 = userDao.insertUser(new User("ThaoPT", "thaopt@gmail.com", "123456", 0)); // Learner
                long userId3 = userDao.insertUser(new User("HungLV", "hunglv@gmail.com", "123456", 0)); // Learner
                long userId4 = userDao.insertUser(new User("Admin", "admin123@gmail.com", "admin123", 1)); // Admin

                // 📘 Sample Courses
                long courseId1 = courseDao.insertCourse(new Course("Tiếng Anh Cho Bé Mẫu Giáo", "Khóa học giúp bé làm quen từ vựng cơ bản", "colors.png"));
                long courseId2 = courseDao.insertCourse(new Course("Tiếng Anh Thiếu Nhi Cấp 1", "Tăng cường ngữ pháp và giao tiếp", "numbers.png"));

                // 📚 Sample Lessons
                long lessonId1 = lessonDao.insertLesson(new Lesson((int) courseId1, "Màu sắc cơ bản", "Học các màu như Red, Blue, Green...", "colors.png"));
                long lessonId2 = lessonDao.insertLesson(new Lesson((int) courseId1, "Số đếm 1-10", "Đếm từ One đến Ten", "numbers.png"));

                // 📝 Sample Quizzes
                long quizId1 = quizDao.insertQuiz(new Quiz((int) lessonId1, "Quiz: Màu sắc", 5));
                long quizId2 = quizDao.insertQuiz(new Quiz((int) lessonId2, "Quiz: Số đếm", 5));

                // ❓ Sample Questions
                long questionId1 = questionDao.insertQuestion(new Question((int) quizId1, "Which one is the color Red?"));
                long questionId2 = questionDao.insertQuestion(new Question((int) quizId2, "What comes after number 3?"));

                // ✅ Sample Options
                optionDao.insertOption(new Option((int) questionId1, "Red", true));
                optionDao.insertOption(new Option((int) questionId1, "Blue", false));
                optionDao.insertOption(new Option((int) questionId1, "Green", false));

                optionDao.insertOption(new Option((int) questionId2, "4", true));
                optionDao.insertOption(new Option((int) questionId2, "5", false));
                optionDao.insertOption(new Option((int) questionId2, "2", false));

                // 📖 Sample Enrollments
                enrollmentDao.insertEnrollment(new Enrollment((int) userId1, (int) courseId1));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId2, (int) courseId1));

                // 📈 Sample Progress
                progressDao.insertProgress(new Progress((int) userId1, (int) lessonId1, "in_progress"));
                progressDao.insertProgress(new Progress((int) userId2, (int) lessonId1, "not_started"));
            });
        }
    };
}