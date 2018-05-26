package com.memeit.backend;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.memeit.backend.OnCompleteListener.Error.NETWORK_ERROR;

public abstract class MyCallBack<T,U> implements Callback<T> {
    OnCompleteListener<U> listener;

    public MyCallBack(OnCompleteListener<U> listener) {
        this.listener = listener;
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        Utils.checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
    }
}
