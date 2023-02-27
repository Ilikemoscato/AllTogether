package com.liuzifan.alltogether.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liuzifan.alltogether.R;
import com.liuzifan.alltogether.entity.TextInfo;
import com.liuzifan.alltogether.network.API;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestNetWorkActivity extends AppCompatActivity {

    Retrofit mRetrofit;

    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_net_work);

        mTextView = findViewById(R.id.text_view);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.uomg.com/") // 设置网络请求BaseUrl地址 baseUrl地址必须以/结尾，否则会报错
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    public void onButtonClick(View view) {
        // 步骤5：创建网络请求接口的实例
        API api = mRetrofit.create(API.class);
        // 步骤6：采用Observable<...>形式 对 网络请求 进行封装
        Observable<TextInfo> observable = api.postJsonData("json");

        // 步骤7：发送网络请求
        observable.subscribeOn(Schedulers.io())               // 在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 回到主线程 处理请求结果
                .subscribe(new Observer<TextInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Toast.makeText(TestNetWorkActivity.this, "开始采用subscribe连接", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(TextInfo info) {
                        // 步骤8：对返回的数据进行处理
                        Toast.makeText(TestNetWorkActivity.this, "回调成功:异步执行", Toast.LENGTH_SHORT).show();
                        if (info == null) {
                            return;
                        }
                        mTextView.setText("返回的数据：" + "\n\n" + info.content);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(TestNetWorkActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(TestNetWorkActivity.this, "get回调成功:请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onButtonClick2(View view) {
        //创建网络请求接口对象实例
        API api = mRetrofit.create(API.class);
        //对发送请求进行封装
        Call<TextInfo> dataCall = api.getJsonData("json");
        //异步请求
        dataCall.enqueue(new Callback<TextInfo>() {
            //请求成功回调
            @Override
            public void onResponse(Call<TextInfo> call, Response<TextInfo> response) {
                Toast.makeText(TestNetWorkActivity.this, "get回调成功:异步执行", Toast.LENGTH_SHORT).show();
                TextInfo info = response.body();
                if (info == null) {
                    return;
                }

                mTextView.setText("返回的数据：" + "\n\n" + info.content);
            }
            //请求失败回调
            @Override
            public void onFailure(Call<TextInfo> call, Throwable t) {
                Toast.makeText(TestNetWorkActivity.this, "get回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void test() {
        String str = "";
        TestKt.revert(str);
//        val just: Single<Int> = Single.just(1)
//        just.subscribe(object : SingleObserver<Int> {
//            override fun onSubscribe(d: Disposable?) {
//            }
//
//            override fun onSuccess(t: Int) {
//            }
//
//            override fun onError(e: Throwable?) {
//            }
//        })
        Single<Integer> just = Single.just(1);

        just.map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Throwable {
                return null;
            }
        }).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });

    }
}