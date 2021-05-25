package com.peace.log.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("tag", "msg");
        Log.d("tag", "msg", new RuntimeException("d"));

        Log.v("tag", "msg");
        Log.v("tag", "msg", new RuntimeException("v"));

        Log.i("tag", "msg");
        Log.i("tag", "msg", new RuntimeException("i"));

        Log.w("tag", "msg");
        Log.w("tag", "msg", new RuntimeException("w"));
        Log.w("tag", new RuntimeException("w"));

        Log.e("tag", "msg");
        Log.e("tag", "msg", new RuntimeException("e"));
    }
}