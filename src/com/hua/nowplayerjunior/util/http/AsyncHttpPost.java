package com.hua.nowplayerjunior.util.http;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


public class AsyncHttpPost extends AsyncTask<Object, Void, String> {
    AsyncHttpCallback callback = null;
    private int timeout; 
    private Handler handler = new Handler();

    
    private Runnable cancelRequest = new Runnable() {
        public void run() {
            cancel(true);
            if (callback != null) {
                Log.w(AsyncHttpPost.class.getSimpleName(), "cancel request");
                callback.onFailure(null);
            }
        }
    };
    
    public AsyncHttpPost() {
        // default timeout 30s
        this.timeout = 30000;
    }
    
	public void setCallback(AsyncHttpCallback callback) {
        this.callback = callback;
    }
	
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public void setCancelDelay() {
        this.handler.postDelayed(cancelRequest, this.timeout);
    }

    @Override
	protected String doInBackground(Object... params) {
	    
	    String response = "";
	    HttpPost httppost = (HttpPost)params[0];

        Log.d(this.getClass().getSimpleName(), "request item: " + httppost.getRequestLine());
        
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used. 
        int timeoutConnection = this.timeout;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT) 
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = this.timeout;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
//        HttpClient httpClient2 = new DefaultHttpClient(httpParameters);
        
        try{
         
            HttpResponse execute = httpclient.execute(httppost);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                Log.e(AsyncHttpPost.class.getSimpleName(), "exception here" + e.toString());
                return null;
            }
        }

        return response;   
	    
	}

	@Override
	protected void onPostExecute(String result) {
	    handler.removeCallbacks(cancelRequest);
	    if (!isCancelled()) {
    	    if (callback != null) {
    	        if (result != null) {
    	            callback.onSuccess(result);
    	        } else {
    	            Log.w(AsyncHttpPost.class.getSimpleName(), "post execute, return null");
    	            callback.onFailure(null);
    	        }
    	    }
	    } else {

	    }
//		textView.setText(result);
//		Log.d("DownloadFile", "result = " + result);
	}
}

