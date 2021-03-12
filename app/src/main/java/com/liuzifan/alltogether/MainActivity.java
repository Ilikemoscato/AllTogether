package com.liuzifan.alltogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.liuzifan.alltogether.activity.TestRelativeLayoutActivity;
import com.liuzifan.alltogether.util.NoDoubleClickUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.layout_test)
    RelativeLayout layout_test;
    @BindView(R.id.network_test)
    RelativeLayout network_test;
    @BindView(R.id.test1)
    RelativeLayout test1;
    @BindView(R.id.test2)
    RelativeLayout test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.layout_test, R.id.network_test, R.id.test1, R.id.test2})
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.layout_test:
                if(!NoDoubleClickUtils.isDoubleClick())
                    startActivity(new Intent(this, TestRelativeLayoutActivity.class));
                break;
        }
    }

}