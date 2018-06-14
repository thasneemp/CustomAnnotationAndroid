package com.example.thasneem.annotation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.annotationsample.ActivityRunner;
import com.example.annotation.ActivityHelper;

@ActivityHelper
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * test method one
         */
        ActivityRunner.startMainActivity(this);


        /**
         * Test method 2
         */

        Class classMainActivity = ActivityRunner.getClassMainActivity();

        startActivity(new Intent(this, classMainActivity));
    }
}
