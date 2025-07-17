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
                            .fallbackToDestructiveMigration() // Allows database to be rebuilt on schema changes
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

                // Clear all data (for development/testing) - Important for fresh data on each run
                userDao.deleteAllUsers();
                courseDao.deleteAllCourses();
                lessonDao.deleteAllLessons();
                enrollmentDao.deleteAllEnrollments();
                quizDao.deleteAllQuizzes();
                questionDao.deleteAllQuestions();
                optionDao.deleteAllOptions();
                // We don't delete answers/quiz results/progress here, as they are user-specific and linked

                // 👤 Sample Users
                long userId1 = userDao.insertUser(new User("HauNT", "haunt@gmail.com", "123456", 0)); // Learner
                long userId2 = userDao.insertUser(new User("ThaoPT", "thaopt@gmail.com", "123456", 0)); // Learner
                long userId3 = userDao.insertUser(new User("HungLV", "hunglv@gmail.com", "123456", 0)); // Learner
                long userId4 = userDao.insertUser(new User("Admin", "admin123@gmail.com", "admin123", 1)); // Admin

                // 📘 Sample Courses (10 courses for children's English)
                long courseId1 = courseDao.insertCourse(new Course("English for Kindergarten", "Fun activities and basic words for young learners.", "course_kindergarten.png"));
                long courseId2 = courseDao.insertCourse(new Course("English for Primary School", "Builds grammar, vocabulary, and communication skills for elementary students.", "course_primary.png"));
                long courseId3 = courseDao.insertCourse(new Course("Fun English Phonics", "Learn letter sounds and how to read simple words.", "course_phonics.png"));
                long courseId4 = courseDao.insertCourse(new Course("My First English Words", "Discover essential English words through engaging visuals.", "course_first_words.png"));
                long courseId5 = courseDao.insertCourse(new Course("English Songs & Rhymes", "Sing along and learn English with catchy tunes and rhymes.", "course_songs.png"));
                long courseId6 = courseDao.insertCourse(new Course("Daily English for Kids", "Practice everyday English phrases and conversations.", "course_daily_english.png"));
                long courseId7 = courseDao.insertCourse(new Course("English Story Time", "Enjoy classic children's stories in simple English.", "course_story_time.png"));
                long courseId8 = courseDao.insertCourse(new Course("Basic English Grammar for Kids", "Understand simple grammar rules with fun examples.", "course_grammar.png"));
                long courseId9 = courseDao.insertCourse(new Course("English for Young Travelers", "Learn useful phrases for traveling and exploring.", "course_travel.png"));
                long courseId10 = courseDao.insertCourse(new Course("English for Little Scientists", "Explore science concepts and vocabulary in English.", "course_science.png"));


                // 📚 Sample Lessons (5 lessons per course, 1 quiz per lesson, 5 questions per quiz, 5 mins duration)

                // Course 1: English for Kindergarten
                long l1_c1 = lessonDao.insertLesson(new Lesson((int) courseId1, "My ABCs", "Learn the English alphabet from A to Z with fun pictures and sounds. Practice recognizing and saying each letter.", "lesson_abc.png"));
                long q1_l1 = quizDao.insertQuiz(new Quiz((int) l1_c1, "Quiz: My ABCs", 5)); // 5 minutes
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1,
                        "Which letter comes after A?", "B", true, "C", false, "D", false, "Z", false,
                        "What letter does 'apple' start with?", "B", false, "A", true, "C", false, "D", false,
                        "Which letter is shaped like a circle?", "A", false, "O", true, "S", false, "L", false,
                        "What letter does 'cat' start with?", "D", false, "C", true, "B", false, "A", false,
                        "Which letter is the last letter of the alphabet?", "X", false, "Y", false, "Z", true, "A", false
                );

                long l2_c1 = lessonDao.insertLesson(new Lesson((int) courseId1, "Numbers 1-10", "Count from One to Ten. Practice recognizing numbers and their English names. Let's count together!", "lesson_numbers_1_10.png"));
                long q2_l2 = quizDao.insertQuiz(new Quiz((int) l2_c1, "Quiz: Numbers 1-10", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2,
                        "What number comes after three?", "Two", false, "Four", true, "Five", false, "One", false,
                        "How many eyes do you have?", "One", false, "Two", true, "Three", false, "Four", false,
                        "What is two plus two?", "Three", false, "Four", true, "Five", false, "Six", false,
                        "Which number is bigger: five or two?", "Two", false, "Five", true, "They are the same", false, "Neither", false,
                        "How many fingers are on one hand?", "Three", false, "Four", false, "Five", true, "Ten", false
                );

                long l3_c1 = lessonDao.insertLesson(new Lesson((int) courseId1, "Colors Everywhere", "Learn basic colors: Red, Blue, Green, Yellow. Identify colors in everyday objects. What's your favorite color?", "lesson_colors_basic.png"));
                long q3_l3 = quizDao.insertQuiz(new Quiz((int) l3_c1, "Quiz: Colors", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3,
                        "What color is an apple?", "Blue", false, "Red", true, "Yellow", false, "Green", false,
                        "What color is the sky?", "Green", false, "Yellow", false, "Blue", true, "Red", false,
                        "What color is grass?", "Red", false, "Blue", false, "Yellow", false, "Green", true,
                        "What color is a banana?", "Red", false, "Blue", false, "Yellow", true, "Green", false,
                        "What color is a stop sign?", "Blue", false, "Green", false, "Red", true, "Yellow", false
                );

                long l4_c1 = lessonDao.insertLesson(new Lesson((int) courseId1, "Farm Animals", "Meet friendly farm animals: cow, pig, duck, chicken. Learn their names and sounds. Moo, oink, quack, cluck!", "lesson_farm_animals.png"));
                long q4_l4 = quizDao.insertQuiz(new Quiz((int) l4_c1, "Quiz: Farm Animals", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4,
                        "What animal says 'Moo'?", "Pig", false, "Cow", true, "Duck", false, "Chicken", false,
                        "What animal says 'Oink'?", "Cow", false, "Pig", true, "Duck", false, "Chicken", false,
                        "What animal says 'Quack'?", "Pig", false, "Cow", false, "Duck", true, "Chicken", false,
                        "Which animal lays eggs?", "Cow", false, "Pig", false, "Duck", false, "Chicken", true,
                        "Which animal gives us milk?", "Pig", false, "Cow", true, "Duck", false, "Chicken", false
                );

                long l5_c1 = lessonDao.insertLesson(new Lesson((int) courseId1, "My Body Parts", "Learn to name your body parts: head, shoulders, knees, toes. Let's sing and point!", "lesson_body_parts.png"));
                long q5_l5 = quizDao.insertQuiz(new Quiz((int) l5_c1, "Quiz: Body Parts", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5,
                        "What do you use to see?", "Ears", false, "Eyes", true, "Nose", false, "Mouth", false,
                        "What do you use to hear?", "Eyes", false, "Nose", false, "Ears", true, "Hands", false,
                        "What do you use to smell?", "Mouth", false, "Hands", false, "Nose", true, "Feet", false,
                        "What do you use to walk?", "Hands", false, "Feet", true, "Head", false, "Arms", false,
                        "What is on top of your body?", "Feet", false, "Hands", false, "Head", true, "Knees", false
                );


                // Course 2: English for Primary School
                long l1_c2 = lessonDao.insertLesson(new Lesson((int) courseId2, "Greetings & Introductions", "Learn how to say hello, goodbye, and introduce yourself and others. Practice common phrases for daily interactions.", "lesson_greetings_intro.png"));
                long q1_l1_c2 = quizDao.insertQuiz(new Quiz((int) l1_c2, "Quiz: Greetings", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c2,
                        "What do you say when you meet someone in the morning?", "Good afternoon", false, "Good morning", true, "Good night", false, "Good evening", false,
                        "How do you introduce yourself?", "You are John.", false, "My name is John.", true, "He is John.", false, "She is John.", false,
                        "What do you say when you leave?", "Hello", false, "Goodbye", true, "How are you?", false, "What's your name?", false,
                        "Which is a polite way to ask someone's name?", "Your name?", false, "What is your name?", true, "Tell name.", false, "Name?", false,
                        "What do you say after someone says 'Nice to meet you'?", "You too", false, "Nice to meet you too", true, "Hello", false, "Goodbye", false
                );

                long l2_c2 = lessonDao.insertLesson(new Lesson((int) courseId2, "My Family", "Learn names of family members: mother, father, brother, sister, grandmother, grandfather. Draw your family tree!", "lesson_my_family.png"));
                long q2_l2_c2 = quizDao.insertQuiz(new Quiz((int) l2_c2, "Quiz: Family", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c2,
                        "Who is your mother's husband?", "Brother", false, "Father", true, "Sister", false, "Grandfather", false,
                        "Who is your father's daughter?", "Brother", false, "Sister", true, "Mother", false, "Son", false,
                        "Who is your mother's mother?", "Father", false, "Grandfather", false, "Grandmother", true, "Uncle", false,
                        "What do you call your mother's brother?", "Sister", false, "Aunt", false, "Uncle", true, "Cousin", false,
                        "Who is usually the youngest in a family?", "Grandfather", false, "Baby", true, "Mother", false, "Father", false
                );

                long l3_c2 = lessonDao.insertLesson(new Lesson((int) courseId2, "School Objects", "Identify common school objects: book, pencil, ruler, backpack. What do you bring to school?", "lesson_school_objects.png"));
                long q3_l3_c2 = quizDao.insertQuiz(new Quiz((int) l3_c2, "Quiz: School Objects", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c2,
                        "You write with a ___.", "book", false, "pencil", true, "desk", false, "chair", false,
                        "You read a ___.", "pencil", false, "book", true, "ruler", false, "eraser", false,
                        "You carry your books in a ___.", "desk", false, "backpack", true, "pencil", false, "ruler", false,
                        "What do you use to draw straight lines?", "Eraser", false, "Pencil", false, "Ruler", true, "Book", false,
                        "Where do you sit in a classroom?", "Board", false, "Desk", true, "Window", false, "Door", false
                );

                long l4_c2 = lessonDao.insertLesson(new Lesson((int) courseId2, "Daily Routines", "Learn verbs and phrases for daily activities: wake up, eat breakfast, go to school. What do you do every day?", "lesson_daily_routines.png"));
                long q4_l4_c2 = quizDao.insertQuiz(new Quiz((int) l4_c2, "Quiz: Daily Routines", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c2,
                        "What do you do in the morning after sleeping?", "Eat dinner", false, "Wake up", true, "Go to bed", false, "Watch TV", false,
                        "What meal do you eat in the morning?", "Lunch", false, "Breakfast", true, "Dinner", false, "Snack", false,
                        "Where do you go to learn?", "Park", false, "School", true, "Supermarket", false, "Zoo", false,
                        "What do you do before you go to sleep?", "Wake up", false, "Brush your teeth", true, "Eat lunch", false, "Play outside", false,
                        "When do you usually eat dinner?", "Morning", false, "Afternoon", false, "Evening", true, "Night", false
                );

                long l5_c2 = lessonDao.insertLesson(new Lesson((int) courseId2, "Simple Present Tense", "Understand how to use simple present tense for habits and facts. Practice making sentences about what you do.", "lesson_simple_present.png"));
                long q5_l5_c2 = quizDao.insertQuiz(new Quiz((int) l5_c2, "Quiz: Simple Present", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c2,
                        "I ___ to school every day.", "go", true, "goes", false, "going", false, "went", false,
                        "She ___ apples.", "like", false, "likes", true, "liking", false, "liked", false,
                        "They ___ football on Saturdays.", "play", true, "plays", false, "playing", false, "played", false,
                        "The sun ___ in the east.", "rise", false, "rises", true, "rising", false, "rose", false,
                        "We ___ English.", "study", true, "studies", false, "studying", false, "studied", false
                );

                // Course 3: Fun English Phonics
                long l1_c3 = lessonDao.insertLesson(new Lesson((int) courseId3, "Short A Sound", "Learn the short 'a' sound as in 'cat', 'apple', 'bat'. Practice words and identify the sound.", "lesson_phonics_short_a.png"));
                long q1_l1_c3 = quizDao.insertQuiz(new Quiz((int) l1_c3, "Quiz: Short A", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c3,
                        "Which word has a short 'a' sound?", "Cake", false, "Car", false, "Cat", true, "Tree", false,
                        "The word 'apple' starts with a short ___ sound.", "e", false, "a", true, "i", false, "o", false,
                        "Which word rhymes with 'bat'?", "Boat", false, "Hat", true, "Bit", false, "But", false,
                        "Is the 'a' in 'game' a short 'a' sound?", "Yes", false, "No", true, "Sometimes", false, "Always", false,
                        "What sound does 'ant' start with?", "Long A", false, "Short A", true, "Short E", false, "Short I", false
                );

                long l2_c3 = lessonDao.insertLesson(new Lesson((int) courseId3, "Short E Sound", "Learn the short 'e' sound as in 'bed', 'egg', 'pen'. Practice words and identify the sound.", "lesson_phonics_short_e.png"));
                long q2_l2_c3 = quizDao.insertQuiz(new Quiz((int) l2_c3, "Quiz: Short E", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c3,
                        "Which word has a short 'e' sound?", "Eat", false, "Bed", true, "Bike", false, "Boat", false,
                        "The word 'egg' starts with a short ___ sound.", "a", false, "e", true, "i", false, "o", false,
                        "Which word rhymes with 'hen'?", "Hand", false, "Pen", true, "Pin", false, "Pan", false,
                        "Is the 'e' in 'me' a short 'e' sound?", "Yes", false, "No", true, "Sometimes", false, "Always", false,
                        "What sound does 'red' have?", "Short A", false, "Short E", true, "Short I", false, "Short O", false
                );

                long l3_c3 = lessonDao.insertLesson(new Lesson((int) courseId3, "Short I Sound", "Learn the short 'i' sound as in 'pig', 'fish', 'sit'. Practice words and identify the sound.", "lesson_phonics_short_i.png"));
                long q3_l3_c3 = quizDao.insertQuiz(new Quiz((int) l3_c3, "Quiz: Short I", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c3,
                        "Which word has a short 'i' sound?", "Ice", false, "Bike", false, "Pig", true, "Pie", false,
                        "The word 'fish' has a short ___ sound.", "a", false, "e", false, "i", true, "o", false,
                        "Which word rhymes with 'sit'?", "Set", false, "Sat", false, "Kit", true, "Cot", false,
                        "Is the 'i' in 'light' a short 'i' sound?", "Yes", false, "No", true, "Sometimes", false, "Always", false,
                        "What sound does 'big' have?", "Short A", false, "Short E", false, "Short I", true, "Short O", false
                );

                long l4_c3 = lessonDao.insertLesson(new Lesson((int) courseId3, "Short O Sound", "Learn the short 'o' sound as in 'dog', 'hot', 'fox'. Practice words and identify the sound.", "lesson_phonics_short_o.png"));
                long q4_l4_c3 = quizDao.insertQuiz(new Quiz((int) l4_c3, "Quiz: Short O", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c3,
                        "Which word has a short 'o' sound?", "Go", false, "Boat", false, "Dog", true, "Home", false,
                        "The word 'hot' has a short ___ sound.", "a", false, "e", false, "i", false, "o", true,
                        "Which word rhymes with 'fox'?", "Box", true, "Fix", false, "Fan", false, "Fun", false,
                        "Is the 'o' in 'nose' a short 'o' sound?", "Yes", false, "No", true, "Sometimes", false, "Always", false,
                        "What sound does 'top' have?", "Short A", false, "Short E", false, "Short I", false, "Short O", true
                );

                long l5_c3 = lessonDao.insertLesson(new Lesson((int) courseId3, "Short U Sound", "Learn the short 'u' sound as in 'sun', 'cup', 'run'. Practice words and identify the sound.", "lesson_phonics_short_u.png"));
                long q5_l5_c3 = quizDao.insertQuiz(new Quiz((int) l5_c3, "Quiz: Short U", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c3,
                        "Which word has a short 'u' sound?", "Blue", false, "Cute", false, "Sun", true, "Tube", false,
                        "The word 'cup' has a short ___ sound.", "a", false, "e", false, "i", false, "u", true,
                        "Which word rhymes with 'run'?", "Ran", false, "Fun", true, "Rain", false, "Bone", false,
                        "Is the 'u' in 'flute' a short 'u' sound?", "Yes", false, "No", true, "Sometimes", false, "Always", false,
                        "What sound does 'bug' have?", "Short A", false, "Short E", false, "Short I", false, "Short U", true
                );

                // Course 4: My First English Words
                long l1_c4 = lessonDao.insertLesson(new Lesson((int) courseId4, "Food I Like", "Learn names of fruits, vegetables, and other yummy foods. What's your favorite snack?", "lesson_food.png"));
                long q1_l1_c4 = quizDao.insertQuiz(new Quiz((int) l1_c4, "Quiz: Food", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c4,
                        "Which is a fruit?", "Carrot", false, "Apple", true, "Bread", false, "Milk", false,
                        "Which is a vegetable?", "Banana", false, "Tomato", true, "Cookie", false, "Juice", false,
                        "What do you drink?", "Pizza", false, "Water", true, "Rice", false, "Cheese", false,
                        "Which is a sweet food?", "Broccoli", false, "Candy", true, "Fish", false, "Egg", false,
                        "What do you eat for breakfast?", "Soup", false, "Cereal", true, "Salad", false, "Steak", false
                );

                long l2_c4 = lessonDao.insertLesson(new Lesson((int) courseId4, "Clothes I Wear", "Identify clothes like shirt, pants, dress, shoes. What do you wear to play?", "lesson_clothes.png"));
                long q2_l2_c4 = quizDao.insertQuiz(new Quiz((int) l2_c4, "Quiz: Clothes", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c4,
                        "You wear this on your feet.", "Hat", false, "Shoes", true, "Gloves", false, "Scarf", false,
                        "You wear this on your head.", "Socks", false, "Pants", false, "Hat", true, "Shirt", false,
                        "Which is a top for your body?", "Pants", false, "Shirt", true, "Socks", false, "Shoes", false,
                        "Which do you wear on your legs?", "Dress", false, "Skirt", false, "Pants", true, "Jacket", false,
                        "What do you wear when it's cold?", "T-shirt", false, "Shorts", false, "Jacket", true, "Sandals", false
                );

                long l3_c4 = lessonDao.insertLesson(new Lesson((int) courseId4, "Toys & Games", "Learn names of toys: ball, doll, car, blocks. What's your favorite toy?", "lesson_toys.png"));
                long q3_l3_c4 = quizDao.insertQuiz(new Quiz((int) l3_c4, "Quiz: Toys", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c4,
                        "You can bounce and throw this toy.", "Doll", false, "Ball", true, "Car", false, "Blocks", false,
                        "This toy looks like a small person.", "Ball", false, "Doll", true, "Car", false, "Robot", false,
                        "You can build tall towers with these.", "Doll", false, "Car", false, "Blocks", true, "Ball", false,
                        "Which toy has wheels and drives?", "Doll", false, "Ball", false, "Car", true, "Blocks", false,
                        "What do you use to play outside?", "Blocks", false, "Ball", true, "Doll", false, "Puzzle", false
                );

                long l4_c4 = lessonDao.insertLesson(new Lesson((int) courseId4, "Places in Town", "Identify places like park, school, store, hospital. Where do you like to go?", "lesson_places.png"));
                long q4_l4_c4 = quizDao.insertQuiz(new Quiz((int) l4_c4, "Quiz: Places", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c4,
                        "Where do you go to play on swings?", "School", false, "Park", true, "Store", false, "Hospital", false,
                        "Where do you go to learn?", "Park", false, "School", true, "Store", false, "Zoo", false,
                        "Where do you buy groceries?", "School", false, "Park", false, "Store", true, "Hospital", false,
                        "Where do you go when you are sick?", "Park", false, "School", false, "Store", false, "Hospital", true,
                        "Where can you see many animals?", "School", false, "Park", false, "Zoo", true, "Store", false
                );

                long l5_c4 = lessonDao.insertLesson(new Lesson((int) courseId4, "Action Verbs", "Learn verbs like run, jump, eat, sleep. What actions can you do?", "lesson_action_verbs.png"));
                long q5_l5_c4 = quizDao.insertQuiz(new Quiz((int) l5_c4, "Quiz: Actions", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c4,
                        "What do you do with your legs?", "Eat", false, "Run", true, "Sleep", false, "Read", false,
                        "What do you do with your mouth?", "Run", false, "Jump", false, "Eat", true, "Play", false,
                        "What do you do with a ball?", "Sleep", false, "Play", true, "Drink", false, "Read", false,
                        "What do you do when you are tired?", "Run", false, "Sleep", true, "Jump", false, "Eat", false,
                        "What do you do with your eyes?", "Listen", false, "See", true, "Talk", false, "Walk", false
                );

                // Course 5: English Songs & Rhymes
                long l1_c5 = lessonDao.insertLesson(new Lesson((int) courseId5, "Old MacDonald Had a Farm", "Sing along to this classic song and learn about farm animals and their sounds. E-I-E-I-O!", "lesson_song_macdonald.png"));
                long q1_l1_c5 = quizDao.insertQuiz(new Quiz((int) l1_c5, "Quiz: MacDonald", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c5,
                        "What animal is in the song 'Old MacDonald'?", "Lion", false, "Cow", true, "Fish", false, "Bird", false,
                        "What sound does a cow make?", "Woof", false, "Moo", true, "Meow", false, "Quack", false,
                        "What is the farmer's name in the song?", "Young MacDonald", false, "Old MacDonald", true, "New MacDonald", false, "Big MacDonald", false,
                        "What do the letters E-I-E-I-O represent?", "A secret code", false, "Sounds in the song", true, "A type of animal", false, "A type of farm", false,
                        "Which animal is NOT usually mentioned in the song?", "Duck", false, "Pig", false, "Elephant", true, "Chicken", false
                );

                long l2_c5 = lessonDao.insertLesson(new Lesson((int) courseId5, "Twinkle, Twinkle, Little Star", "Learn this gentle lullaby about a shining star. Sing softly and look at the stars.", "lesson_song_star.png"));
                long q2_l2_c5 = quizDao.insertQuiz(new Quiz((int) l2_c5, "Quiz: Little Star", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c5,
                        "What is 'twinkle, twinkle' about?", "The sun", false, "A little star", true, "The moon", false, "A cloud", false,
                        "Where is the little star?", "On the ground", false, "Up above the world so high", true, "In the house", false, "In the water", false,
                        "What does the star do?", "Sleeps", false, "Shines", true, "Eats", false, "Plays", false,
                        "What shape is a star?", "Circle", false, "Square", false, "Pointy", true, "Round", false,
                        "When do you usually see stars?", "Day", false, "Night", true, "Morning", false, "Afternoon", false
                );

                long l3_c5 = lessonDao.insertLesson(new Lesson((int) courseId5, "The Wheels on the Bus", "Sing about the parts of a bus and the people on it. Round and round we go!", "lesson_song_bus.png"));
                long q3_l3_c5 = quizDao.insertQuiz(new Quiz((int) l3_c5, "Quiz: The Bus", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c3,
                        "What goes 'round and round' on the bus?", "The doors", false, "The wheels", true, "The wipers", false, "The people", false,
                        "What do the wipers on the bus do?", "Go up and down", true, "Go round and round", false, "Open and shut", false, "Swish, swish, swish", false,
                        "What do the doors on the bus do?", "Go round and round", false, "Open and shut", true, "Go swish, swish, swish", false, "Go up and down", false,
                        "What do the people on the bus do?", "Go round and round", false, "Open and shut", false, "Go up and down", false, "Talk, talk, talk", true,
                        "What sound do the babies on the bus make?", "Wah, wah, wah", true, "Shush, shush, shush", false, "Beep, beep, beep", false, "Round and round", false
                );

                long l4_c5 = lessonDao.insertLesson(new Lesson((int) courseId5, "Head, Shoulders, Knees, and Toes", "Learn and sing about your body parts with actions. Let's move!", "lesson_song_body.png"));
                long q4_l4_c5 = quizDao.insertQuiz(new Quiz((int) l4_c5, "Quiz: Body Song", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c5,
                        "What are the first two body parts mentioned in the song?", "Eyes, ears", false, "Head, shoulders", true, "Knees, toes", false, "Nose, mouth", false,
                        "What do you touch after your knees?", "Head", false, "Shoulders", false, "Toes", true, "Eyes", false,
                        "Which body part is NOT in the song title?", "Head", false, "Shoulders", false, "Elbows", true, "Knees", false,
                        "What do you point to after your nose?", "Mouth", true, "Eyes", false, "Ears", false, "Head", false,
                        "What part of your body helps you stand up?", "Hands", false, "Legs", true, "Arms", false, "Head", false
                );

                long l5_c5 = lessonDao.insertLesson(new Lesson((int) courseId5, "If You're Happy and You Know It", "Sing about different emotions and actions. Clap your hands, stomp your feet!", "lesson_song_happy.png"));
                long q5_l5_c5 = quizDao.insertQuiz(new Quiz((int) l5_c5, "Quiz: Happy Song", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c5,
                        "What do you do if you're happy and you know it?", "Shout loud", false, "Clap your hands", true, "Cry", false, "Sleep", false,
                        "What emotion is the song about?", "Sadness", false, "Happiness", true, "Anger", false, "Fear", false,
                        "What other action is mentioned in the song?", "Jump up and down", false, "Stomp your feet", true, "Run fast", false, "Sing loud", false,
                        "What do you say if you're happy and you know it?", "Hello", false, "Hooray", true, "Goodbye", false, "Thank you", false,
                        "What is the main message of the song?", "To hide your feelings", false, "To express your happiness", true, "To be quiet", false, "To be sad", false
                );


                // Course 6: Daily English for Kids
                long l1_c6 = lessonDao.insertLesson(new Lesson((int) courseId6, "Asking & Answering Questions", "Learn how to ask 'What is...?' 'Where is...?' and give simple answers. Practice with friends!", "lesson_qa.png"));
                long q1_l1_c6 = quizDao.insertQuiz(new Quiz((int) l1_c6, "Quiz: Q&A", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c6,
                        "What do you say to ask about a thing?", "Who is this?", false, "What is this?", true, "Where is this?", false, "How is this?", false,
                        "What do you say to ask about a person?", "What is this?", false, "Who is this?", true, "Where is this?", false, "How is this?", false,
                        "If someone asks 'What is your name?', you say 'My name is ___'.", "Yes", false, "John", true, "No", false, "Thank you", false,
                        "If someone asks 'Where is the ball?', you say 'It's ___ the box'.", "on", false, "in", true, "under", false, "next to", false,
                        "What do you say if you don't know the answer?", "Yes", false, "I don't know", true, "Thank you", false, "Hello", false
                );

                long l2_c6 = lessonDao.insertLesson(new Lesson((int) courseId6, "Describing Things", "Use adjectives like big, small, red, happy to describe objects and people. Practice making descriptive sentences.", "lesson_describing.png"));
                long q2_l2_c6 = quizDao.insertQuiz(new Quiz((int) l2_c6, "Quiz: Describing", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c6,
                        "If a car is not small, it is ___.", "red", false, "big", true, "fast", false, "slow", false,
                        "A banana is usually ___.", "blue", false, "red", false, "yellow", true, "green", false,
                        "If a person is smiling, they are ___.", "sad", false, "angry", false, "happy", true, "tired", false,
                        "What word describes something that is not old?", "Big", false, "New", true, "Small", false, "Fast", false,
                        "Which word describes a loud sound?", "Quiet", false, "Soft", false, "Noisy", true, "Gentle", false
                );

                long l3_c6 = lessonDao.insertLesson(new Lesson((int) courseId6, "Expressing Feelings", "Learn words for emotions: happy, sad, angry, excited. How do you feel today?", "lesson_feelings.png"));
                long q3_l3_c6 = quizDao.insertQuiz(new Quiz((int) l3_c6, "Quiz: Feelings", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c6,
                        "If you are laughing, you are ___.", "sad", false, "happy", true, "angry", false, "tired", false,
                        "If you are crying, you are ___.", "happy", false, "sad", true, "excited", false, "hungry", false,
                        "What do you feel when you get a new toy?", "Angry", false, "Excited", true, "Sad", false, "Bored", false,
                        "What do you feel when someone takes your toy?", "Happy", false, "Excited", false, "Angry", true, "Sleepy", false,
                        "How do you ask someone about their feelings?", "What is your name?", false, "How are you feeling?", true, "What do you like?", false, "Where are you from?", false
                );

                long l4_c6 = lessonDao.insertLesson(new Lesson((int) courseId6, "Making Requests", "Learn how to ask for things politely using 'Can I have...?' or 'May I...?'", "lesson_requests.png"));
                long q4_l4_c6 = quizDao.insertQuiz(new Quiz((int) l4_c6, "Quiz: Requests", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c6,
                        "What do you say to ask for a cookie politely?", "Cookie!", false, "Give me cookie!", false, "Can I have a cookie, please?", true, "I want cookie.", false,
                        "If you want to go to the bathroom, you say 'May I go to the ___?'", "kitchen", false, "bathroom", true, "bedroom", false, "garden", false,
                        "What do you say if someone helps you?", "No", false, "Thank you", true, "Hello", false, "Goodbye", false,
                        "If you want to borrow a pencil, you say 'Can I borrow your ___?'", "book", false, "pencil", true, "ruler", false, "bag", false,
                        "What do you say if you want to play?", "I am hungry.", false, "Can I play?", true, "I am sleepy.", false, "I want to eat.", false
                );

                long l5_c6 = lessonDao.insertLesson(new Lesson((int) courseId6, "Giving Directions", "Learn how to tell someone where to go: go straight, turn left, turn right. Practice with a map!", "lesson_directions_kids.png"));
                long q5_l5_c6 = quizDao.insertQuiz(new Quiz((int) l5_c6, "Quiz: Directions", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c6,
                        "If you walk straight, you ___ ahead.", "turn", false, "go", true, "stop", false, "run", false,
                        "What do you do if you want to go left?", "Turn right", false, "Turn left", true, "Go straight", false, "Stop", false,
                        "Which way is opposite of left?", "Up", false, "Right", true, "Down", false, "Straight", false,
                        "If you are lost, what should you ask?", "What time is it?", false, "Where am I?", true, "What is your name?", false, "How old are you?", false,
                        "What do you say if you want someone to follow you?", "Stop", false, "Come with me", true, "Go away", false, "Sit down", false
                );

                // Course 7: English Story Time
                long l1_c7 = lessonDao.insertLesson(new Lesson((int) courseId7, "The Three Little Pigs", "Read about three little pigs building their houses and facing a big bad wolf. Which house is the strongest?", "lesson_pigs.png"));
                long q1_l1_c7 = quizDao.insertQuiz(new Quiz((int) l1_c7, "Quiz: Three Pigs", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c7,
                        "How many little pigs are there?", "Two", false, "Three", true, "Four", false, "Five", false,
                        "What did the first pig build his house with?", "Bricks", false, "Straw", true, "Wood", false, "Stones", false,
                        "What did the big bad wolf do to the houses?", "He helped build them", false, "He blew them down", true, "He painted them", false, "He ate them", false,
                        "Which house was the strongest?", "Straw house", false, "Wood house", false, "Brick house", true, "Mud house", false,
                        "What did the wolf try to do to the third pig?", "Eat him", true, "Play with him", false, "Sing to him", false, "Give him a gift", false
                );

                long l2_c7 = lessonDao.insertLesson(new Lesson((int) courseId7, "Little Red Riding Hood", "Follow Little Red Riding Hood on her journey to Grandma's house and meet a tricky wolf. Be careful!", "lesson_red_riding_hood.png"));
                long q2_l2_c7 = quizDao.insertQuiz(new Quiz((int) l2_c7, "Quiz: Red Riding Hood", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c7,
                        "What color is Little Red Riding Hood's cloak?", "Blue", false, "Red", true, "Green", false, "Yellow", false,
                        "Where was Little Red Riding Hood going?", "To the park", false, "To Grandma's house", true, "To school", false, "To the store", false,
                        "Who did Little Red Riding Hood meet in the forest?", "A bear", false, "A wolf", true, "A rabbit", false, "A bird", false,
                        "What did the wolf pretend to be?", "A squirrel", false, "Grandma", true, "A hunter", false, "A tree", false,
                        "Who saved Little Red Riding Hood and Grandma?", "The wolf", false, "The hunter", true, "The pigs", false, "The birds", false
                );

                long l3_c7 = lessonDao.insertLesson(new Lesson((int) courseId7, "The Gingerbread Man", "A little gingerbread man runs away from everyone! Can anyone catch him?", "lesson_gingerbread_man.png"));
                long q3_l3_c7 = quizDao.insertQuiz(new Quiz((int) l3_c7, "Quiz: Gingerbread Man", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c7,
                        "What kind of man is in the story?", "Chocolate man", false, "Gingerbread man", true, "Cookie man", false, "Bread man", false,
                        "What does the gingerbread man say when he runs?", "Stop, stop!", false, "Run, run, as fast as you can!", true, "Hello, hello!", false, "Eat me!", false,
                        "Who could NOT catch the gingerbread man?", "The old woman", false, "The old man", false, "The cow", false, "All of them at first", true,
                        "Who finally caught the gingerbread man?", "A dog", false, "A fox", true, "A cat", false, "A bird", false,
                        "What happened to the gingerbread man at the end?", "He lived happily ever after", false, "He was eaten by the fox", true, "He ran away forever", false, "He became a real boy", false
                );

                long l4_c7 = lessonDao.insertLesson(new Lesson((int) courseId7, "Goldilocks and the Three Bears", "A curious girl named Goldilocks visits the house of three bears. What happens when they come home?", "lesson_goldilocks.png"));
                long q4_l4_c7 = quizDao.insertQuiz(new Quiz((int) l4_c7, "Quiz: Goldilocks", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c7,
                        "How many bears are there in the story?", "Two", false, "Three", true, "Four", false, "Five", false,
                        "What did Goldilocks eat?", "Soup", false, "Porridge", true, "Cake", false, "Bread", false,
                        "Whose bed did Goldilocks sleep in that was 'just right'?", "Papa Bear's", false, "Mama Bear's", false, "Baby Bear's", true, "Her own", false,
                        "What did the bears find when they came home?", "Goldilocks sleeping", true, "A new toy", false, "A lot of food", false, "A clean house", false,
                        "What did Goldilocks do when she saw the bears?", "She played with them", false, "She ran away", true, "She hid", false, "She talked to them", false
                );

                long l5_c7 = lessonDao.insertLesson(new Lesson((int) courseId7, "The Ugly Duckling", "A little duckling feels different and sad, but he grows up to be something beautiful. What is it?", "lesson_ugly_duckling.png"));
                long q5_l5_c7 = quizDao.insertQuiz(new Quiz((int) l5_c7, "Quiz: Ugly Duckling", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c7,
                        "What kind of animal was the 'ugly duckling' at first?", "A chick", false, "A duckling", true, "A swan", false, "A goose", false,
                        "How did the other animals treat the ugly duckling?", "They loved him", false, "They were mean to him", true, "They ignored him", false, "They played with him", false,
                        "What did the ugly duckling grow up to be?", "A chicken", false, "A beautiful swan", true, "A big duck", false, "A bird", false,
                        "What is the main message of the story?", "It's good to be ugly", false, "Don't judge a book by its cover (or an animal by its looks)", true, "Ducks are always ugly", false, "Swans are always beautiful", false,
                        "Where did the ugly duckling spend the winter?", "In a warm house", false, "In a frozen pond", true, "In a nest", false, "In a barn", false
                );

                // Course 8: Basic English Grammar for Kids
                long l1_c8 = lessonDao.insertLesson(new Lesson((int) courseId8, "Nouns", "Learn about nouns: names of people, places, animals, or things. Find nouns in sentences!", "lesson_grammar_nouns.png"));
                long q1_l1_c8 = quizDao.insertQuiz(new Quiz((int) l1_c8, "Quiz: Nouns", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c8,
                        "Which word is a noun?", "Run", false, "Happy", false, "Cat", true, "Quickly", false,
                        "Is 'school' a noun?", "No", false, "Yes", true, "Maybe", false, "Sometimes", false,
                        "Which is a noun for a person?", "Sing", false, "Teacher", true, "Blue", false, "Jump", false,
                        "Which is a noun for a thing?", "Eat", false, "Book", true, "Sleepy", false, "Fast", false,
                        "Is 'park' a noun?", "No", false, "Yes", true, "Maybe", false, "Sometimes", false
                );

                long l2_c8 = lessonDao.insertLesson(new Lesson((int) courseId8, "Adjectives", "Discover adjectives: words that describe nouns. Make your sentences more colorful!", "lesson_grammar_adjectives.png"));
                long q2_l2_c8 = quizDao.insertQuiz(new Quiz((int) l2_c8, "Quiz: Adjectives", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c8,
                        "Which word is an adjective?", "Run", false, "Big", true, "Table", false, "Eat", false,
                        "What does an adjective do?", "Tells an action", false, "Describes a noun", true, "Names a person", false, "Connects words", false,
                        "Which word describes the color of an apple?", "Eat", false, "Red", true, "Run", false, "Book", false,
                        "Is 'happy' an adjective?", "No", false, "Yes", true, "Maybe", false, "Sometimes", false,
                        "Which word describes a tall tree?", "Short", false, "Tall", true, "Small", false, "Green", false
                );

                long l3_c8 = lessonDao.insertLesson(new Lesson((int) courseId8, "Verbs", "Learn about verbs: action words. What are you doing right now?", "lesson_grammar_verbs.png"));
                long q3_l3_c8 = quizDao.insertQuiz(new Quiz((int) l3_c8, "Quiz: Verbs", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c8,
                        "Which word is a verb?", "Cat", false, "Run", true, "Blue", false, "Happy", false,
                        "What does a verb tell you?", "A person's name", false, "An action", true, "A description", false, "A place", false,
                        "Which word is an action you can do with your mouth?", "See", false, "Eat", true, "Hear", false, "Walk", false,
                        "Is 'sleep' a verb?", "No", false, "Yes", true, "Maybe", false, "Sometimes", false,
                        "Which word is an action you do with your feet?", "Sing", false, "Walk", true, "Read", false, "Draw", false
                );

                long l4_c8 = lessonDao.insertLesson(new Lesson((int) courseId8, "Pronouns", "Understand pronouns: words that replace nouns (he, she, it, they). Make your sentences smoother!", "lesson_grammar_pronouns.png"));
                long q4_l4_c8 = quizDao.insertQuiz(new Quiz((int) l4_c8, "Quiz: Pronouns", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c8,
                        "Which word is a pronoun?", "Book", false, "He", true, "Run", false, "Big", false,
                        "What does a pronoun do?", "Describes a verb", false, "Replaces a noun", true, "Names a place", false, "Shows an action", false,
                        "If you are talking about a girl, you can use ___.", "he", false, "she", true, "it", false, "they", false,
                        "If you are talking about a boy, you can use ___.", "she", false, "he", true, "it", false, "they", false,
                        "Which pronoun refers to a thing?", "He", false, "She", false, "It", true, "They", false
                );

                long l5_c8 = lessonDao.insertLesson(new Lesson((int) courseId8, "Prepositions of Place", "Learn prepositions like in, on, under, next to. Describe where things are!", "lesson_grammar_prepositions.png"));
                long q5_l5_c8 = quizDao.insertQuiz(new Quiz((int) l5_c8, "Quiz: Prepositions", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c8,
                        "The apple is ___ the table.", "run", false, "on", true, "big", false, "eat", false,
                        "The cat is hiding ___ the bed.", "on", false, "under", true, "in", false, "next to", false,
                        "The bird is ___ the tree.", "under", false, "in", true, "on", false, "next to", false,
                        "The ball is ___ the box (inside).", "on", false, "under", false, "in", true, "next to", false,
                        "The dog is ___ the house (outside, close by).", "in", false, "on", false, "under", false, "next to", true
                );

                // Course 9: English for Young Travelers
                long l1_c9 = lessonDao.insertLesson(new Lesson((int) courseId9, "At the Airport", "Learn words and phrases for checking in, boarding, and finding your gate at the airport. Ready for adventure!", "lesson_travel_airport.png"));
                long q1_l1_c9 = quizDao.insertQuiz(new Quiz((int) l1_c9, "Quiz: Airport", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c9,
                        "Where do you go to catch a plane?", "Train station", false, "Airport", true, "Bus stop", false, "Port", false,
                        "What do you need to show before you get on the plane?", "Toy", false, "Ticket", true, "Book", false, "Food", false,
                        "What is a 'suitcase' used for?", "To sit on", false, "To carry clothes and things for travel", true, "To play with", false, "To eat from", false,
                        "What do pilots do?", "Drive a train", false, "Fly an airplane", true, "Drive a bus", false, "Sail a boat", false,
                        "What is the 'gate' at an airport?", "A place to buy food", false, "The door where you get on the plane", true, "A place to sleep", false, "A place to play", false
                );

                long l2_c9 = lessonDao.insertLesson(new Lesson((int) courseId9, "On the Plane", "Learn about what happens on an airplane: seats, windows, food. Enjoy your flight!", "lesson_travel_plane.png"));
                long q2_l2_c9 = quizDao.insertQuiz(new Quiz((int) l2_c9, "Quiz: On the Plane", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c9,
                        "What do you sit on in a plane?", "Floor", false, "Seat", true, "Table", false, "Window", false,
                        "What do you look out of to see clouds?", "Door", false, "Window", true, "Wall", false, "Ceiling", false,
                        "What do flight attendants bring you?", "Toys", false, "Food and drinks", true, "Books", false, "Pillows", false,
                        "What is the 'seatbelt' for?", "To keep you warm", false, "To keep you safe in your seat", true, "To play with", false, "To hold your food", false,
                        "What do you hear when the plane takes off?", "A car horn", false, "A loud engine sound", true, "A soft melody", false, "Silence", false
                );

                long l3_c9 = lessonDao.insertLesson(new Lesson((int) courseId9, "At the Hotel", "Learn phrases for checking in, asking for a room, and using hotel facilities. Have a comfortable stay!", "lesson_travel_hotel.png"));
                long q3_l3_c9 = quizDao.insertQuiz(new Quiz((int) l3_c9, "Quiz: Hotel", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c9,
                        "Where do you sleep when you are traveling?", "School", false, "Hotel", true, "Park", false, "Store", false,
                        "What do you get when you check in at a hotel?", "Food", false, "A room key", true, "A toy", false, "A book", false,
                        "What do you call the person who helps you at the front desk?", "Cook", false, "Receptionist", true, "Driver", false, "Teacher", false,
                        "What is a 'lobby' in a hotel?", "The bedroom", false, "The main entrance area", true, "The kitchen", false, "The swimming pool", false,
                        "If you need more towels, who do you ask?", "The pilot", false, "The receptionist", true, "The driver", false, "The chef", false
                );

                long l4_c9 = lessonDao.insertLesson(new Lesson((int) courseId9, "Asking for Food", "Learn how to order food and ask for drinks in a restaurant. What would you like to eat?", "lesson_travel_food.png"));
                long q4_l4_c9 = quizDao.insertQuiz(new Quiz((int) l4_c9, "Quiz: Food Orders", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c9,
                        "What do you say to ask for a menu?", "I want food.", false, "Can I have the menu, please?", true, "Food, please.", false, "Menu!", false,
                        "If you want water, you ask for a ___ of water.", "plate", false, "glass", true, "bowl", false, "cup", false,
                        "What do you say if you like the food?", "It's bad.", false, "It's delicious!", true, "It's cold.", false, "It's too much.", false,
                        "Who serves you food in a restaurant?", "Cook", false, "Waiter/Waitress", true, "Driver", false, "Cleaner", false,
                        "What do you say to ask for the bill?", "Food is good.", false, "Can I have the bill, please?", true, "I want to pay.", false, "Goodbye.", false
                );

                long l5_c9 = lessonDao.insertLesson(new Lesson((int) courseId9, "Shopping for Souvenirs", "Learn phrases for buying gifts and souvenirs. What will you bring home?", "lesson_travel_souvenirs.png"));
                long q5_l5_c9 = quizDao.insertQuiz(new Quiz((int) l5_c9, "Quiz: Souvenirs", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c9,
                        "What do you buy to remember your trip?", "Food", false, "Souvenirs", true, "Clothes", false, "Books", false,
                        "What do you say to ask the price?", "What is this?", false, "How much is this?", true, "I like this.", false, "Give me this.", false,
                        "Which is a small gift you can buy?", "Car", false, "Key chain", true, "House", false, "Boat", false,
                        "Where do you usually buy souvenirs?", "School", false, "Gift shop", true, "Hospital", false, "Park", false,
                        "What do you say if you want to buy something?", "I don't like it.", false, "I'll take it.", true, "It's too expensive.", false, "Thank you.", false
                );

                // Course 10: English for Little Scientists
                long l1_c10 = lessonDao.insertLesson(new Lesson((int) courseId10, "The Solar System", "Explore planets, stars, and the sun. Learn about our amazing solar system! Which planet is your favorite?", "lesson_science_solar_system.png"));
                long q1_l1_c10 = quizDao.insertQuiz(new Quiz((int) l1_c10, "Quiz: Solar System", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q1_l1_c10,
                        "Which is the biggest planet in our solar system?", "Earth", false, "Jupiter", true, "Mars", false, "Venus", false,
                        "What is the star at the center of our solar system?", "Moon", false, "Sun", true, "Mars", false, "Earth", false,
                        "Which planet is known as the 'Red Planet'?", "Earth", false, "Mars", true, "Jupiter", false, "Venus", false,
                        "How many planets are in our solar system?", "7", false, "8", true, "9", false, "10", false,
                        "What do astronauts travel in?", "Car", false, "Spaceship", true, "Boat", false, "Train", false
                );

                long l2_c10 = lessonDao.insertLesson(new Lesson((int) courseId10, "Animal Habitats", "Discover where different animals live: forest, ocean, desert. What's your favorite animal habitat?", "lesson_science_habitats.png"));
                long q2_l2_c10 = quizDao.insertQuiz(new Quiz((int) l2_c10, "Quiz: Habitats", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q2_l2_c10,
                        "Where do fish live?", "Forest", false, "Ocean", true, "Desert", false, "Mountain", false,
                        "Where do monkeys live?", "Ocean", false, "Forest", true, "Desert", false, "Ice", false,
                        "Which animal lives in the desert?", "Polar bear", false, "Camel", true, "Fish", false, "Monkey", false,
                        "Where do polar bears live?", "Forest", false, "Ocean", false, "Ice/Arctic", true, "Desert", false,
                        "What is a home for animals called?", "House", false, "Habitat", true, "School", false, "Store", false
                );

                long l3_c10 = lessonDao.insertLesson(new Lesson((int) courseId10, "Weather & Seasons", "Learn about different types of weather (sunny, rainy, snowy) and seasons (spring, summer, fall, winter).", "lesson_science_weather.png"));
                long q3_l3_c10 = quizDao.insertQuiz(new Quiz((int) l3_c10, "Quiz: Weather & Seasons", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q3_l3_c10,
                        "What kind of weather has water falling from the sky?", "Sunny", false, "Rainy", true, "Cloudy", false, "Windy", false,
                        "Which season is hot and good for swimming?", "Winter", false, "Spring", false, "Summer", true, "Fall", false,
                        "What do you see in the sky on a sunny day?", "Stars", false, "Sun", true, "Moon", false, "Clouds", false,
                        "Which season has leaves falling from trees?", "Spring", false, "Summer", false, "Fall (Autumn)", true, "Winter", false,
                        "What do you wear when it's snowy?", "T-shirt", false, "Shorts", false, "Warm coat", true, "Sandals", false
                );

                long l4_c10 = lessonDao.insertLesson(new Lesson((int) courseId10, "Plants & Growth", "Discover how plants grow from seeds to big trees. What do plants need to grow?", "lesson_science_plants.png"));
                long q4_l4_c10 = quizDao.insertQuiz(new Quiz((int) l4_c10, "Quiz: Plants", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q4_l4_c10,
                        "What does a plant grow from?", "Rock", false, "Seed", true, "Toy", false, "Water", false,
                        "What color are most leaves?", "Red", false, "Blue", false, "Green", true, "Yellow", false,
                        "What do plants need to grow?", "Candy", false, "Sunlight and water", true, "Clothes", false, "Cars", false,
                        "Which part of a plant grows under the ground?", "Leaf", false, "Flower", false, "Root", true, "Stem", false,
                        "What do plants give us to breathe?", "Water", false, "Oxygen", true, "Food", false, "Light", false
                );

                long l5_c10 = lessonDao.insertLesson(new Lesson((int) courseId10, "Simple Machines", "Learn about simple machines like levers, wheels, and ramps. How do they help us?", "lesson_science_simple_machines.png"));
                long q5_l5_c10 = quizDao.insertQuiz(new Quiz((int) l5_c10, "Quiz: Simple Machines", 5));
                addQuestionsAndOptions(questionDao, optionDao, (int) q5_l5_c10,
                        "Which is a simple machine that helps you lift heavy things?", "Wheel", false, "Lever", true, "Ramp", false, "Screw", false,
                        "What helps a car move easily?", "Lever", false, "Wheel", true, "Ramp", false, "Pulley", false,
                        "What is a ramp?", "A flat surface that helps you push things up or down easily", true, "A tool to cut things", false, "A device to lift things", false, "A way to make fire", false,
                        "Which simple machine helps you pull water from a well?", "Lever", false, "Wheel", false, "Pulley", true, "Ramp", false,
                        "What is a 'screw'?", "A simple machine that holds things together or lifts things", true, "A type of wheel", false, "A type of lever", false, "A type of ramp", false
                );


                // 📖 Sample Enrollments
                enrollmentDao.insertEnrollment(new Enrollment((int) userId1, (int) courseId1));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId1, (int) courseId3));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId2, (int) courseId2));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId2, (int) courseId5));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId3, (int) courseId4));
                enrollmentDao.insertEnrollment(new Enrollment((int) userId3, (int) courseId6));

                // 📈 Sample Progress (Initial progress for some lessons)
                progressDao.insertProgress(new Progress((int) userId1, (int) l1_c1, "completed"));
                progressDao.insertProgress(new Progress((int) userId1, (int) l2_c1, "in_progress"));
                progressDao.insertProgress(new Progress((int) userId2, (int) l1_c2, "not_started"));
                progressDao.insertProgress(new Progress((int) userId2, (int) l2_c2, "completed"));
                progressDao.insertProgress(new Progress((int) userId3, (int) l1_c4, "in_progress"));
                progressDao.insertProgress(new Progress((int) userId3, (int) l2_c4, "not_started"));
            });
        }
    };

    // Helper method to add questions and options for 4 options
    // This method simplifies adding multiple questions and their options
    private static void addQuestionsAndOptions(QuestionDao questionDao, OptionDao optionDao, int quizId,
                                               String qText1, String opt1_1, boolean opt1_1_c, String opt1_2, boolean opt1_2_c, String opt1_3, boolean opt1_3_c, String opt1_4, boolean opt1_4_c,
                                               String qText2, String opt2_1, boolean opt2_1_c, String opt2_2, boolean opt2_2_c, String opt2_3, boolean opt2_3_c, String opt2_4, boolean opt2_4_c,
                                               String qText3, String opt3_1, boolean opt3_1_c, String opt3_2, boolean opt3_2_c, String opt3_3, boolean opt3_3_c, String opt3_4, boolean opt3_4_c,
                                               String qText4, String opt4_1, boolean opt4_1_c, String opt4_2, boolean opt4_2_c, String opt4_3, boolean opt4_3_c, String opt4_4, boolean opt4_4_c,
                                               String qText5, String opt5_1, boolean opt5_1_c, String opt5_2, boolean opt5_2_c, String opt5_3, boolean opt5_3_c, String opt5_4, boolean opt5_4_c) {

        long qId1 = questionDao.insertQuestion(new Question(quizId, qText1));
        optionDao.insertOption(new Option((int) qId1, opt1_1, opt1_1_c));
        optionDao.insertOption(new Option((int) qId1, opt1_2, opt1_2_c));
        optionDao.insertOption(new Option((int) qId1, opt1_3, opt1_3_c));
        optionDao.insertOption(new Option((int) qId1, opt1_4, opt1_4_c));

        long qId2 = questionDao.insertQuestion(new Question(quizId, qText2));
        optionDao.insertOption(new Option((int) qId2, opt2_1, opt2_1_c));
        optionDao.insertOption(new Option((int) qId2, opt2_2, opt2_2_c));
        optionDao.insertOption(new Option((int) qId2, opt2_3, opt2_3_c));
        optionDao.insertOption(new Option((int) qId2, opt2_4, opt2_4_c));

        long qId3 = questionDao.insertQuestion(new Question(quizId, qText3));
        optionDao.insertOption(new Option((int) qId3, opt3_1, opt3_1_c));
        optionDao.insertOption(new Option((int) qId3, opt3_2, opt3_2_c));
        optionDao.insertOption(new Option((int) qId3, opt3_3, opt3_3_c));
        optionDao.insertOption(new Option((int) qId3, opt3_4, opt3_4_c));

        long qId4 = questionDao.insertQuestion(new Question(quizId, qText4));
        optionDao.insertOption(new Option((int) qId4, opt4_1, opt4_1_c));
        optionDao.insertOption(new Option((int) qId4, opt4_2, opt4_2_c));
        optionDao.insertOption(new Option((int) qId4, opt4_3, opt4_3_c));
        optionDao.insertOption(new Option((int) qId4, opt4_4, opt4_4_c));

        long qId5 = questionDao.insertQuestion(new Question(quizId, qText5));
        optionDao.insertOption(new Option((int) qId5, opt5_1, opt5_1_c));
        optionDao.insertOption(new Option((int) qId5, opt5_2, opt5_2_c));
        optionDao.insertOption(new Option((int) qId5, opt5_3, opt5_3_c));
        optionDao.insertOption(new Option((int) qId5, opt5_4, opt5_4_c));
    }

    // Add deleteAll methods to DAOs for development/testing convenience
    // You will need to add these methods to your DAO interfaces
    // e.g., in UserDao: @Query("DELETE FROM Users") void deleteAllUsers();
}
