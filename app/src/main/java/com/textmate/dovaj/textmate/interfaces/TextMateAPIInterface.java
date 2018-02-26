package com.textmate.dovaj.textmate.interfaces;

import com.textmate.dovaj.textmate.models.RatingRequest;
import com.textmate.dovaj.textmate.models.RatingResult;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface TextMateAPIInterface {

    @FormUrlEncoded
    @POST("sentiment")
    Observable<RatingResult> getRating(@Field("Input") String msg , @Field("Count") Integer msgCount);

    @POST("sentiment")
    Observable<RatingResult> getRatingFromList(@Body RatingRequest request);


}