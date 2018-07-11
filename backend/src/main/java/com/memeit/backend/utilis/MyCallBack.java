package com.memeit.backend.utilis;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.memeit.backend.utilis.OnCompleteListener.Error.NETWORK_ERROR;
import static com.memeit.backend.utilis.OnCompleteListener.Error.OTHER_ERROR;
import static com.memeit.backend.utilis.Utils.checkAndFireError;
import static com.memeit.backend.utilis.Utils.checkAndFireSuccess;

public  class MyCallBack<T> implements Callback<T> {
    public OnCompleteListener<T> listener;

    public MyCallBack(OnCompleteListener<T> listener) {
        this.listener = listener;
    }


    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()){
            checkAndFireSuccess(listener,response.body());
        }else{
            try {
                checkAndFireError(listener,OTHER_ERROR.setMessage(response.message()+""+response.errorBody().string()));
            } catch (IOException e) {
                checkAndFireError(listener,OTHER_ERROR.setMessage("nooo"+e.getMessage()));
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        if (call.isCanceled())return;
        Utils.checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
    }
}
