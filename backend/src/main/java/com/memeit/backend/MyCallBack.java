package com.memeit.backend;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.memeit.backend.OnCompleteListener.Error.NETWORK_ERROR;
import static com.memeit.backend.OnCompleteListener.Error.OTHER_ERROR;
import static com.memeit.backend.Utils.checkAndFireError;
import static com.memeit.backend.Utils.checkAndFireSuccess;

public  class MyCallBack<T> implements Callback<T> {
    OnCompleteListener<T> listener;

    public MyCallBack(OnCompleteListener<T> listener) {
        this.listener = listener;
    }


    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()){
            checkAndFireSuccess(listener,response.body());
        }else{
            checkAndFireError(listener,OTHER_ERROR.setMessage(response.message()));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        Utils.checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
    }
}
