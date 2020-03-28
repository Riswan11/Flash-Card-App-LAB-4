package com.example.flashcardlab2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String str1;
    String str2;
    TextView qView;
    TextView aView;
    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        qView = findViewById(R.id.flashCard_question);
        aView = findViewById(R.id.flashCard_answer);
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) {
            qView.setText(allFlashcards.get(0).getQuestion());
            aView.setText(allFlashcards.get(0).getAnswer());
        }

        qView.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // get the center for the clipping circle
                int cx = aView.getWidth() / 2;
                int cy = aView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(aView, cx, cy, 0f, finalRadius);

                qView.setVisibility(v.INVISIBLE);
                aView.setVisibility(v.VISIBLE);

                anim.setDuration(3000);
                anim.start();
            }
        });

        aView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aView.setVisibility(v.INVISIBLE);
                qView.setVisibility(v.VISIBLE);
            }
        });

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out_animation);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in_animation);

                qView.startAnimation(leftOutAnim);
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        qView.startAnimation(rightInAnim);
                        // advance our pointer index so we can show the next card
                        currentCardDisplayedIndex++;

                        // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
                        if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                            currentCardDisplayedIndex = 0;
                        }

                        // set the question and answer TextViews with data from the database
                        qView.setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                        aView.setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });


                aView.setVisibility(v.INVISIBLE);
                qView.setVisibility(v.VISIBLE);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                MainActivity.this.startActivityForResult(intent,100);
                overridePendingTransition(R.anim.right_in_animation, R.anim.left_out_animation);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            str1 = data.getExtras().getString("string1");
            str2 = data.getExtras().getString("string2");

            flashcardDatabase.insertCard(new Flashcard(str1, str2));
            allFlashcards = flashcardDatabase.getAllCards();

            qView.setText(str1);
            aView.setText(str2);

        }

    }
}
