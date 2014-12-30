package com.hua.nowplayerjunior.util.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


public class AsyncHttpGet extends AsyncTask<Object, Void, String> {
    AsyncHttpCallback callback = null;
    private int timeout; 
    private Handler handler = new Handler();
    
    private Runnable cancelRequest = new Runnable() {
        public void run() {
            cancel(true);
            if (callback != null) {
                Log.w(AsyncHttpGet.class.getSimpleName(), "cancel request");
                callback.onFailure(null);
            }
        }
    };
    
    public AsyncHttpGet() {
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
        HttpRequestItem httpRequestItem = (HttpRequestItem)params[0];

        Log.d(this.getClass().getSimpleName(), "request item: " + httpRequestItem.toString());
        
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used. 
        HttpConnectionParams.setConnectionTimeout(httpParameters, this.timeout);
        // Set the default socket timeout (SO_TIMEOUT) 
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, this.timeout);
        
        // Create a new HttpClient and Get Header
        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        
        String url = httpRequestItem.getUrl() + "?" + URLEncodedUtils.format(httpRequestItem.getContents(), "UTF-8");
        Log.d(this.getClass().getSimpleName(), "request url: " + url);
        HttpGet httpget = new HttpGet(url);
        try{
//            httppost.setEntity(new UrlEncodedFormEntity(httpRequestItem.getContents()));
            
            
            for (NameValuePair pair : httpRequestItem.getHttpHeaderList()) {
                httpget.setHeader(pair.getName(), pair.getValue());
            }
            
            HttpResponse execute = httpclient.execute(httpget);
            if (execute.getStatusLine().getStatusCode()>=400) {
                Log.d("AsyncHttpGet", "http status code: " + execute.getStatusLine().getStatusCode());
                return null;
            }
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                Log.e(AsyncHttpGet.class.getSimpleName(), "exception here" + e.toString());
//                callback.onFailure(e);
                return null;
            }
        }

        return response;   
	    
	}

	@Override
	protected void onPostExecute(String result) {
	    handler.removeCallbacks(cancelRequest);
	    if (!this.isCancelled()) {
            if (callback != null) {
                if (result != null) {
	                callback.onSuccess(result);
	            } else {
	                Log.w(AsyncHttpGet.class.getSimpleName(), "post execute, return null");
	                callback.onFailure(null);
	            }
	        }
	    } 
//		textView.setText(result);
//		Log.d("DownloadFile", "result = " + result);
	}
}

