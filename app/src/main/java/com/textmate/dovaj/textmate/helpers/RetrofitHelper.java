package com.textmate.dovaj.textmate.helpers;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.textmate.dovaj.textmate.BuildConfig;
import com.textmate.dovaj.textmate.interfaces.TextMateAPIInterface;
import com.textmate.dovaj.textmate.models.RatingResult;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private Retrofit retrofit;

    public TextMateAPIInterface Ratings(){
        if(retrofit == null) {
            retrofit = createRetrofit();
        }
        return retrofit.create(TextMateAPIInterface.class);
    }

    /**
     * https://inthecheesefactory.com/blog/retrofit-2.0/en
     * https://code.tutsplus.com/tutorials/getting-started-with-retrofit-2--cms-27792
     * https://www.beginnersheap.com/android-retrofit-version-2-0-with-okhttp-gson-tutorial-beginners/
     * https://blog.mindorks.com/rxjava2-and-retrofit2-error-handling-on-a-single-place-8daf720d42d6
     */
    private OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();

                final HttpUrl url = originalHttpUrl.newBuilder().build();

                // Request customization: add request headers
                final Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }

    /**
     * Creates a pre configured Retrofit instance
     */
    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                //.baseUrl("http://192.168.0.53:8000/api/")
                .baseUrl("https://textmate-192704.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .client(createOkHttpClient())
                .build();
    }
}
