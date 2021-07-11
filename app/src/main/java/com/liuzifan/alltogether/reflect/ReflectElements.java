package com.liuzifan.alltogether.reflect;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.liuzifan.alltogether.annotation.EasySet;

import java.lang.reflect.Field;

public class ReflectElements {

    public static void setElements(Activity activity) {
        Class<? extends Activity> cls = activity.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(EasySet.class)) {
                EasySet easySet = field.getAnnotation(EasySet.class);
                String value = easySet.value();
                Log.d("EasySet", "value" + value);
                Log.d("EasySet", "getGenericType" + field.getGenericType().toString());
                Intent intent = activity.getIntent();
                field.setAccessible(true);
                try {
                    if(field.getGenericType().toString().equals("class java.lang.String")) {
                        field.set(activity, intent.getStringExtra(value));
                    } else if(field.getGenericType().toString().equals("class java.lang.Integer")) {
                        field.set(activity, intent.getIntExtra(value, 0));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
