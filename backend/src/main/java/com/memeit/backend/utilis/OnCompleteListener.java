package com.memeit.backend.utilis;

public interface OnCompleteListener<T> {
    public enum Error {
        NETWORK_ERROR("Connection Failed"),
        OTHER_ERROR("Some Error");
        private String defaultMessage;
        private String message;

        Error(String message) {
            this.defaultMessage = message;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }

        public String getMessage() {
            return message;
        }

        public Error setMessage(String message) {
            this.message = message;
            return this;
        }
    }


    public void onSuccess(T t);

    public void onFailure(Error error);
}
