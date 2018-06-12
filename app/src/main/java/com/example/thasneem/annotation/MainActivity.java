package com.example.thasneem.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.annotationsample.UserLog;
import com.example.annotation.MyLogger;

@MyLogger
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserLog.startMainActivity(this);
    }
}
