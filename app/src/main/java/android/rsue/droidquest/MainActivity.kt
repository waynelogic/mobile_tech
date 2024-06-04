package android.rsue.droidquest

import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var mTrueButton: Button
    private lateinit var mFalseButton: Button
    private lateinit var mNextButton: ImageButton
    private lateinit var mPrevButton: ImageButton
    private lateinit var mDeceitButton: Button
    private lateinit var mQuestionTextView: TextView
    private lateinit var mResultImageView: ImageView

    companion object{
        private const val TAG = "QuestActivity"
        private const val KEY_INDEX = "index"
        private const val ANSWER_RESULT = "answer_result"
    }

    //    Вопросы
    private var mCurrentIndex = 0
    private var mAnswerResult = false
    private var mQuestionBank = listOf(
        Question(R.string.question_android, true),
        Question(R.string.question_linear, false),
        Question(R.string.question_service, false),
        Question(R.string.question_res, true),
        Question(R.string.question_manifest, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate(Bundle) вызван")
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswerResult = savedInstanceState.getBoolean(ANSWER_RESULT, false)
            checkImage()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mResultImageView = findViewById(R.id.result_image_view)
        mQuestionTextView = findViewById(R.id.question_text_view)

        mTrueButton = findViewById(R.id.true_button)
        mTrueButton.setOnClickListener {
            checkAnswer(true)
        }

        mFalseButton = findViewById(R.id.false_button)
        mFalseButton.setOnClickListener {
            checkAnswer(false)
        }


        mDeceitButton = findViewById(R.id.deceit_button)
        mDeceitButton.setOnClickListener {
            val answerIsTrue = mQuestionBank[mCurrentIndex].answerTrue
            val intent = DeceitActivity.newIntent(this, answerIsTrue)
            startActivity(intent)
            sound("click")
        }


        mNextButton = findViewById(R.id.next_button)
        mNextButton.setOnClickListener {
            mResultImageView.setImageResource(0)
            sound("click")
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
            updateQuestion()
        }

        mPrevButton = findViewById(R.id.previous_button)
        mPrevButton.setOnClickListener {
            mResultImageView.setImageResource(0)
            sound("click")
            if (mCurrentIndex > 0) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.size
                updateQuestion()
            }
        }

        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() вызван")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, mCurrentIndex)
        outState.putBoolean(ANSWER_RESULT, mAnswerResult)
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() вызван")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() вызван")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() вызван")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() вызван")
    }


    private fun updateQuestion() {
        val question = mQuestionBank[mCurrentIndex].textResId
        mQuestionTextView.setText(question)
    }

    // Проверка ответа
    private fun checkAnswer(userPressedTrue: Boolean) {
        val answerIsTrue = mQuestionBank[mCurrentIndex].answerTrue
        val messageResId = if (userPressedTrue == answerIsTrue) {
            mAnswerResult = true
            checkImage()
            vibrate()
            sound("correct")
            R.string.correct_toast
        } else {
            mAnswerResult = false
            checkImage()
            sound("incorrect")
            vibrate()
            R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun checkImage() {
        if (mAnswerResult) {
            mResultImageView.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            mResultImageView.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

   // Воспроизведение звука
    private fun sound(typeOfSound: String) {
        var sound = 0
        when (typeOfSound) {
            "click" -> sound = R.raw.mouse
            "correct" -> sound = R.raw.win
            "incorrect" -> sound = R.raw.fail
        }
        MediaPlayer.create(this, sound).start()
    }

    // Вибрация
    private fun vibrate() {
        val vibratorService = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(1000)
    }
}