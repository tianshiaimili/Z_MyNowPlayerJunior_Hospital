package com.hua.nowplayerjunior.util.http;

public interface AsyncHttpCallback {
    void onSuccess(String result);
    void onFailure(Exception exception);
}

