package com.liuzifan.alltogether.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.liuzifan.alltogether.entity.TextInfo;

public class MyViewModel extends ViewModel {

    public MutableLiveData<TextInfo> textInfo;

    public MyViewModel() {
        this.textInfo = new MutableLiveData<>();
    }

    public void initTextInfo() {
        TextInfo textInfo1 = new TextInfo();
        textInfo1.content = "MyViewModel";
        textInfo.postValue(textInfo1);
    }

    public LiveData<TextInfo> getTextInfo() {
        return textInfo;
    }
}
