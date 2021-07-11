package com.liuzifan.alltogether.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.liuzifan.alltogether.R;
import com.liuzifan.alltogether.annotation.EasySet;
import com.liuzifan.alltogether.reflect.ReflectElements;

public class TestRelativeLayoutActivity extends AppCompatActivity {

    @EasySet("name")
    private String name;

    @EasySet("index")
    private Integer index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawable_fish);
        ReflectElements.setElements(this);
        Log.d("EasySet", "name:" + name);
        Log.d("EasySet", "index:" + index);
    }

}
