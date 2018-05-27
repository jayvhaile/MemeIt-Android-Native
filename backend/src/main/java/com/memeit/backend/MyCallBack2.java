package com.memeit.backend;

import retrofit2.Call;
import retrofit2.Callback;

import static com.memeit.backend.OnCompleteListener.Error.NETWORK_ERROR;

public  abstract class MyCallBack2<T,U> implements Callback<T> {
    OnCompleteListener<U> listener;

    public MyCallBack2(OnCompleteListener<U> listener) {
        this.listener = listener;
    }




    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        Utils.checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
    }
}
