package com.liuzifan.alltogether.network;

import com.liuzifan.alltogether.entity.TextInfo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @GET("api/rand.qinghua")
    Call<TextInfo> getJsonData(@Query("format") String format);

    @FormUrlEncoded
    @POST("api/rand.qinghua")
    Observable<TextInfo> postJsonData(@Field("format") String format);

}
