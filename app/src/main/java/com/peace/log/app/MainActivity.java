package com.peace.log.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.peace.log.assist.LogAssist;
import com.peace.log.assist.LogListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogAssist.getInstance().setLogListener(new LogListener() {
            @Override
            public boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName, String className, String methodName, int line) {
                Log.e(tag, msg);
                return false;
            }
        });

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