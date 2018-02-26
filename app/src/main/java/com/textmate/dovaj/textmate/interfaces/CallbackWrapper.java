package com.textmate.dovaj.textmate.interfaces;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;


public abstract class CallbackWrapper<T extends BaseResponse> extends DisposableObserver<T>{
    //BaseView is just a reference of a View in MVP
    private WeakReference<BaseView> weakReference;
    private static final String TAG = "RatingResponse";
    public CallbackWrapper() {
        //this.weakReference = new WeakReference<>(view);
    }

    protected abstract void onSuccess(T t);

    @Override
    public void onNext(T t) {
        //You can return StatusCodes of different cases from your API and handle it here. I usually include these cases on BaseResponse and iherit it from every Response
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
       // BaseView view = weakReference.get();
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException)e).response().errorBody();
            Log.e(TAG, responseBody.toString());
            //view.onUnknownError(getErrorMessage(responseBody));
        } else if (e instanceof SocketTimeoutException) {
            //view.onTimeout();
            Log.e(TAG, e.toString());
        } else if (e instanceof IOException) {
            //view.onNetworkError();
            Log.e(TAG, e.toString());
        } else {
            //view.onUnknownError(e.getMessage());
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onComplete() {

    }


    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
