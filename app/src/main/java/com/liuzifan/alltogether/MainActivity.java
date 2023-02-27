package com.liuzifan.alltogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuzifan.alltogether.activity.TestNetWorkActivity;
import com.liuzifan.alltogether.activity.TestRelativeLayoutActivity;
import com.liuzifan.alltogether.entity.TextInfo;
import com.liuzifan.alltogether.lifecycle.MyObserve;
import com.liuzifan.alltogether.util.NoDoubleClickUtils;
import com.liuzifan.alltogether.viewmodel.MyViewModel;

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

    @BindView(R.id.text_test1)
    TextView text_test1;
    @BindView(R.id.test2)
    RelativeLayout test2;

    MyViewModel myViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getLifecycle().addObserver(new MyObserve());

        initViewModel();
    }

    private void initViewModel() {
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getTextInfo().observe(this, new Observer<TextInfo>() {

            @Override
            public void onChanged(TextInfo textInfo) {
                text_test1.setText(textInfo.content);
            }
        });
    }

    @OnClick({R.id.layout_test, R.id.network_test, R.id.test1, R.id.test2})
    public void onClick(View view) {
        if(NoDoubleClickUtils.isDoubleClick()) {
            return;
        }
        int id = view.getId();
        switch(id) {
            case R.id.layout_test:
                Intent intent = new Intent(this, TestRelativeLayoutActivity.class);
                intent.putExtra("name", "MainActivity");
                intent.putExtra("index", 10);
                startActivity(intent);
                break;
            case R.id.network_test:
                Intent intent2 = new Intent(this, TestNetWorkActivity.class);
                startActivity(intent2);
                break;
            case R.id.test1:
                myViewModel.initTextInfo();
                break;
            case R.id.test2:

                break;
        }

    }

}