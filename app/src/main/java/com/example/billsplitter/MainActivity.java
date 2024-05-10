package com.example.billsplitter;

import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    RelativeLayout relativeLayout;
    Animation txtAnimation , layoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //animation-code
        txtAnimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fall_down);
        layoutAnimation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_in_top);

        textView= findViewById(R.id.textview);
        relativeLayout= findViewById(R.id.main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.setAnimation(layoutAnimation);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setVisibility(View.VISIBLE);
                        textView.setAnimation(txtAnimation);
                    }
                } , 900);
            }
        } , 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent(MainActivity.this , SigninActivity.class);
                startActivity(intent);
            }
        } , 3100);
    }
}