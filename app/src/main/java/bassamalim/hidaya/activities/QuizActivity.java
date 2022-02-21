package bassamalim.hidaya.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import bassamalim.hidaya.R;
import bassamalim.hidaya.database.AppDatabase;
import bassamalim.hidaya.database.dbs.QuizAnswersDB;
import bassamalim.hidaya.database.dbs.QuizQuestionsDB;
import bassamalim.hidaya.databinding.ActivityQuizBinding;
import bassamalim.hidaya.other.Util;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private AppDatabase db;
    private List<QuizQuestionsDB> questions;
    private int current = 0;
    private final int[] cAnswers = new int[10];
    private Button nextBtn;
    private Button prevBtn;
    private final Button[] answerBtns = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.onActivityCreateSetTheme(this);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        db = Room.databaseBuilder(this, AppDatabase.class, "HidayaDB")
                .createFromAsset("databases/HidayaDB.db").allowMainThreadQueries().build();

        selectQuestions(getQuestions());

        Arrays.fill(cAnswers, -1);

        initViews();

        setListeners();

        ask(current);
    }

    private void selectQuestions(List<QuizQuestionsDB> rawQuestions) {
        Collections.shuffle(rawQuestions);
        questions = new ArrayList<>(rawQuestions.subList(0, 10));
    }

    private void initViews() {
        nextBtn = binding.nextQuestion;
        prevBtn = binding.previousQuestion;

        answerBtns[0] = binding.answer1;
        answerBtns[1] = binding.answer2;
        answerBtns[2] = binding.answer3;
        answerBtns[3] = binding.answer4;
    }

    private void setListeners() {
        for (int i = 0; i < answerBtns.length; i++) {
            int finalI = i;
            answerBtns[i].setOnClickListener(v -> answered(finalI));
        }

        prevBtn.setOnClickListener(v -> previousQ());
        nextBtn.setOnClickListener(v -> nextQ());
    }

    private void ask(int num) {
        QuizQuestionsDB q = questions.get(num);

        String qNum = "سؤال " + (current+1);
        binding.questionNumber.setText(qNum);

        binding.questionScreen.setText(questions.get(current).getQuestion_text());

        List<QuizAnswersDB> answers = getAnswers(q.getQuestion_id());
        for (int i = 0; i < answerBtns.length; i++)
            answerBtns[i].setText(answers.get(i).getAnswer_text());

        adjustButtons();
    }

    private void adjustButtons() {
        TypedValue text = new TypedValue();
        getTheme().resolveAttribute(R.attr.myText, text, true);

        TypedValue accent = new TypedValue();
        getTheme().resolveAttribute(R.attr.myActiveBar, accent, true);

        for (Button answerBtn : answerBtns)
            answerBtn.setTextColor(text.data);

        if (cAnswers[current] != -1)
            answerBtns[cAnswers[current]].setTextColor(accent.data);

        if (current == 0) {
            prevBtn.setEnabled(false);
            prevBtn.setTextColor(getResources().getColor(R.color.grey));
        }
        else if (current == 9) {
            if (allAnswered()) {
                nextBtn.setText("إنهاء الإختبار");
                nextBtn.setEnabled(true);
                nextBtn.setTextColor(text.data);
            }
            else {
                nextBtn.setText("أجب على جميع الاسئلة");
                nextBtn.setEnabled(false);
                nextBtn.setTextColor(getResources().getColor(R.color.grey));
            }
        }
        else {
            prevBtn.setEnabled(true);
            prevBtn.setTextColor(text.data);

            nextBtn.setEnabled(true);
            nextBtn.setText("السؤال التالي");
            nextBtn.setTextColor(text.data);
        }
    }

    private void answered(int a) {
        cAnswers[current] = a;

        adjustButtons();

        if (current != 9)
            nextQ();
    }

    private void nextQ() {
        if (current == 9)
            endQuiz();
        else
            ask(++current);
    }

    private void previousQ() {
        if (current > 0)
            ask(--current);
    }

    private boolean allAnswered() {
        for (int cAnswer : cAnswers) {
            if (cAnswer == -1)
                return false;
        }
        return true;
    }

    private void endQuiz() {
        int score = 0;
        for (int i = 0; i < 10; i++) {
            if (cAnswers[i] == questions.get(i).getCorrect_answer_id())
                score++;
        }

        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("cAnswers", cAnswers);
        intent.putExtra("score", score);
        intent.putExtra("questions", (Serializable) questions);
        startActivity(intent);

        finish();
    }

    private List<QuizQuestionsDB> getQuestions() {
        return db.quizQuestionDao().getAll();
    }

    private List<QuizAnswersDB> getAnswers(int qId) {
        return db.quizAnswerDao().getAnswers(qId);
    }
}