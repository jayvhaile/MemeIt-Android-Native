package com.memeit.backend;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jv on 7/18/2018.
 */

public class Controller<T> {
    private boolean isRespondedFromNetwork;
    private boolean retryOnConnection;
    private boolean isConnected;
    private OnCompleteListener<T> onCompleteListener;
    private Call<T> netCall;
    public void makeCall(Call<T> cacheCall, Call<T> netCall) {
        cacheCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (isRespondedFromNetwork) return;

                if (response.isSuccessful())
                    fireResponceFromCache(response.body());


            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                if (isRespondedFromNetwork) return;

            }
        });
        if (!isConnected()) {
            //listener.onFailed("not connected", false);
            return;
        }
        netCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {

            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {

            }
        });
    }

    public void makeCacheCall(Call<T> cacheCall) {
        if (isConnected())
            cacheCall.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    if (isRespondedFromNetwork) return;

                    if (response.isSuccessful())
                        onCompleteListener.onResponceFromCache(response.body());
                    else
                        onCompleteListener.onFailed("", true);
                }

                @Override
                public void onFailure(Call<T> call, Throwable throwable) {
                    if (isRespondedFromNetwork) return;

                }
            });
    }



    public void makeNetCall(Call<T> netCall) {
        if (isConnected())
            netCall.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {


                    if (response.isSuccessful())
                        onCompleteListener.onResponceFromCache(response.body());
                    else
                        onCompleteListener.onFailed("", true);
                }

                @Override
                public void onFailure(Call<T> call, Throwable throwable) {
                    if (isRespondedFromNetwork) return;

                }
            });
    }

    private boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
        if (!isRespondedFromNetwork&&retryOnConnection)
            makeNetCall(netCall);
    }

    public void setOnCompleteListener(OnCompleteListener<T> OnCompleteListener) {
        this.onCompleteListener = OnCompleteListener;
    }


    public void fireResponceFromCache(T t) {
        if(this.onCompleteListener !=null){
            this.onCompleteListener.onResponceFromCache(t);
        }
    }

    ;

    public void fireResponceFromNetwork(T t) {
        if(this.onCompleteListener !=null){
            this.onCompleteListener.onResponceFromNetwork(t);
        }
    }


    public static class OnCompleteListener<T> {
        public void onResponceFromCache(T t) {
        }

        ;

        public void onResponceFromNetwork(T t) {
        }

        ;

        public void onFailed(String error, boolean fromCache) {

        }

        ;
    }

    public static class ResponseError {
        public enum ErrorType {
            NETWORK_ERROR,
            NOT_FOUND_ERROR,
            SERVER_ERROR,
            INVALID_URL_ERROR,
            BAD_REQUEST;

            int errorCode;

            public ErrorType ofCode(int code) {
                return NETWORK_ERROR;
            }
        }

        ErrorType errorType;
        String message;
    }
}



/*









W/"61-wB96BXBVJ4bE05MNVQS+nwnM9pM"
W/"61-wB96BXBVJ4bE05MNVQS+nwnM9pM"
W/"61-wB96BXBVJ4bE05MNVQS+nwnM9pM"
W/"61-4474EK9TYIiHRs6lbTPSbtj70xQ
W/"1815-hnINcay08WNhC5DTWRqGbP8he3M



*/
