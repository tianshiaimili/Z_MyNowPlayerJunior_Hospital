package com.hua.nowplayerjunior.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.hua.nowplayerjunior.utils.ErrorCodeString;
import com.pccw.nmal.util.http.AsyncHttpCallback;
import com.pccw.nmal.util.http.AsyncHttpGet;
import com.pccw.nmal.util.http.HttpRequestItem;

public class ProxyServerRequest implements AsyncHttpCallback {
    
    private static final String TAG = ProxyServerRequest.class.getSimpleName();
    
    public interface Callback {
        public void didProxyServiceLoaded(JSONObject jsonObj, ProxyServerRequestType requestType);
        public void proxyServiceError(String errorCode);
    }
    
    private Callback callback;
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void removeCallback() {
        setCallback(null);
    }

    private int timeout;
   
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public enum ProxyServerRequestType {
        ProxyAcquireToekn,
        ProxyAcquirePPVToekn,
        ProxyAcquireSVODToken,
        ProxyKeepAlive,
        ProxyCheckSubscription,
        ProxyCheckBinding,
        ProxyTermToken
    }
    
    private ProxyServerRequestType proxyServerRequestType;
    
    public ProxyServerRequest() {
        this.timeout = -1;
    }
    
    public void setProxyServerRequestType(
            ProxyServerRequestType proxyServerRequestType) {
        this.proxyServerRequestType = proxyServerRequestType;
    }

    public void executeAsyncGet(HttpRequestItem item) {
        AsyncHttpGet ahp = new AsyncHttpGet();
        if (this.timeout > 0) {
            ahp.setTimeout(this.timeout);
        }
        ahp.setCancelDelay();
        ahp.setCallback(this);
        ahp.execute(new Object[]{item});
        
    }
    
    @Override
    public void onSuccess(String result) {
        if (callback != null) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                callback.didProxyServiceLoaded(jsonObj, this.proxyServerRequestType);
            } catch (JSONException e) {
                if (this.proxyServerRequestType != ProxyServerRequestType.ProxyKeepAlive
                        && this.proxyServerRequestType != ProxyServerRequestType.ProxyTermToken) {
                    callback.proxyServiceError(ErrorCodeString.ParseJsonError);
                } else {
                    Log.i(TAG, "parse json in keep alive or term token, ingore it");
                }
            }
        }
    }

    @Override
    public void onFailure(Exception exception) {
        if (callback != null) {
            if (this.proxyServerRequestType != ProxyServerRequestType.ProxyKeepAlive
                    && this.proxyServerRequestType != ProxyServerRequestType.ProxyTermToken) {
                callback.proxyServiceError(ErrorCodeString.ProxyServerRequestTimeout);
            }
        }
    }
}
