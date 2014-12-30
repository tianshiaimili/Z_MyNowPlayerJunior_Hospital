package com.hua.nowplayerjunior.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.hua.nowplayerjunior.service.PlayerControlProxy.VideoType;
import com.hua.nowplayerjunior.service.ProxyServerRequest.ProxyServerRequestType;
import com.hua.nowplayerjunior.utils.ErrorCodeString;
import com.pccw.nmal.util.PreferenceHelper;
import com.pccw.nmal.util.http.HttpRequestItem;


public class ProxyServerControl implements ProxyServerRequest.Callback {

	public static final String URL_PROXY_ACUQIRE_TOKEN = "https://ctlproxy.nowplayer.now.com/proxy/pool/acquireToken.action";
	public static final String URL_PROXY_ACUQIRE_PPV_TOKEN = "https://ctlproxy.nowplayer.now.com/proxy/pool/acquirePPVToken.action";
	public static final String URL_PROXY_ACUQIRE_SVOD_TOKEN = "https://ctlproxy.nowplayer.now.com/proxy/pool/acquireSVODToken.action";
	public static final String URL_PROXY_KEEPALIVE = "http://ctlproxy.nowplayer.now.com/proxy/pool/keepAlive.action";
	public static final String URL_PROXY_CHECK_SUB = "https://ctlproxy.nowplayer.now.com/proxy/pool/checkSubscription.action";
	public static final String URL_PROXY_CHECK_BINDING = "https://ctlproxy.nowplayer.now.com/proxy/pool/checkBinding.action";
	public static final String URL_PROXY_TERM_TOKEN = "https://ctlproxy.nowplayer.now.com/proxy/pool/termToken.action";
	public static final String PARAM_JSON = "json";
	public static final String SERVICE_ID_FTA = "FTA";
	public static final String SERVICE_ID_FSA = "FSA";
	public static final int ONE_SECOND = 1000;
	
	
	public static final String URL_VIPRION_ACQUIRE_TOKEN = "https://[ccDomain]/proxy/pool/acquireToken.action";
	public static final String URL_VIPRION_KEEPALIVE = "http://[ccDomain]/proxy/pool/keepAlive.action";
	public static final String URL_VIPRION_TERM_TOKEN = "https://[ccDomain]/proxy/pool/termToken.action";	
	
	public static String ccDomain = null;
	public static String ccPoolType = null;
	public static String ccCustId = null;
	
	
	public interface Callback {
        public void proxyServerControlError(String errorCode);
        public void tokenIsValid();
        public void didProxyTermToken(boolean success, String errorCode);
    }
    
    private Callback callback;
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    
    public void removeCallback() {
        setCallback(null);
    }
    
    private static ProxyServerControl instance = null;
    
    private static final String PARAM_FSA = "fsa";
    private static final String PARAM_CUSTID = "custid";
    private static final String PARAM_SERVICE_ID = "srvid";
    private static final String PARAM_NETPASS_ID = "npid";
    private static final String PARAM_VFMT = "vfmt";
    private static final String PARAM_PRODUCT_ID = "pid";
    private static final String PARAM_LIBRARY_ID = "libid";
    private static final String PARAM_SCHEDULE_ID = "schdid";
    private static final String PARAM_SERIES_ID = "seriesid";
    private static final String PARAM_FORMAT = "fmt";
    private static final String PARAM_TOKEN = "TOKEN";
    private static final String PARAM_KEEPALIVE_INTERVAL = "KEEPALIVEINTERVAL";
    private static final String PARAM_CHECK_SUB_INTERVAL = "CHECKSUBINTERVAL";
    private static final String PARAM_DURATION = "DURATION";
    private static final String PARAM_DATE = "DATE";
    private static final String PARAM_RESULT = "RESULT";
    private static final String PARAM_ERRCODE = "ERRCODE";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_CLIENT_ID = "clientid";
    
    private String requestFsa;
    private String requestServiceId;
    private String requestNetpassId;
    private String requestType;
    private String productId;
    private String libraryId;
    private String scheduleId;
    private String seriesId;
    private String videoFormat;
    private String token;
    private int keepaliveInterval;
    private long checkSubInterval;
    private long duration;
    private String date;
    private VideoType currentPlayerVideoType;
    
    private boolean isFirstTimeGetToken;
    
    public ProxyServerControl() {
        
    }
    
    public synchronized static ProxyServerControl getInstance() {
        if (instance == null)
            instance = new ProxyServerControl();
        return instance;
    }
    
//    private void executeAsyncGet(HttpRequestItem item) {
//        AsyncHttpGet ahp = new AsyncHttpGet();
//        ahp.setCallback(this);
//        ahp.execute(new Object[]{item});
//    }
    
    public void killProxyControlInstance() {
        this.removeAllHandler();
        this.clearAllProperties();
    }
    
    private void clearAllProperties() {
        this.requestFsa = null;
        this.requestServiceId = null;
        this.requestNetpassId = null;
        this.productId = null;
        this.seriesId = null;
        this.videoFormat = null;
        this.token = null;
        this.keepaliveInterval = -1;
        this.checkSubInterval = -1;
        this.duration = -1;
        this.date = null;
        this.currentPlayerVideoType = null;
    }
    
    public void acquireToken(final boolean isFirstTime) {
    	if (!(this.ccCustId == null && this.ccDomain == null && this.ccPoolType == null)) {
            this.isFirstTimeGetToken = isFirstTime;
            
            HttpRequestItem requestItem = new HttpRequestItem();
            requestItem.setUrl(URL_VIPRION_ACQUIRE_TOKEN.replaceAll("\\[ccDomain\\]", this.ccDomain));
            requestItem.addContent(PARAM_CUSTID, this.ccCustId);
            requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
            requestItem.addContent(PARAM_TYPE, this.ccPoolType);
            
//            this.executeAsyncGet(requestItem);
            ProxyServerRequest psr = new ProxyServerRequest();
            psr.setProxyServerRequestType(ProxyServerRequestType.ProxyAcquireToekn);
            psr.setCallback(this);
            psr.executeAsyncGet(requestItem);
        } else {
            Log.e("ProxyServerControl", "acquire token all values are null (fsa, npId and serviceId)");
            if (isFirstTime) {
                Log.e("ProxyServerControl", "first time acquire token with all params are null, return error");
                this.proxyServiceError(ErrorCodeString.GeneralError);
            } else {
                Log.i("ProxyServerControl", "during playback, ignore the acquire request");
            }
        }
    }
    
    public void acquirePPVToken(final boolean isFirstTime) {
        if (!(this.requestFsa == null && this.requestNetpassId == null && this.requestServiceId == null)) {
            this.isFirstTimeGetToken = isFirstTime;
            
            HttpRequestItem requestItem = new HttpRequestItem();
            requestItem.setUrl(URL_PROXY_ACUQIRE_PPV_TOKEN);
            requestItem.addContent(PARAM_CUSTID, this.requestFsa);
            requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
            requestItem.addContent(PARAM_PRODUCT_ID, this.productId);
            requestItem.addContent(PARAM_VFMT, this.videoFormat);
            requestItem.addContent(PARAM_TYPE, this.requestType);
            requestItem.addContent(PARAM_CLIENT_ID, PreferenceHelper.getGenerateUID());
            if (this.seriesId!=null&&!this.seriesId.equalsIgnoreCase("")){
            	requestItem.addContent(PARAM_SERIES_ID, this.seriesId);
            }
            
//            this.executeAsyncGet(requestItem);
            ProxyServerRequest psr = new ProxyServerRequest();
            psr.setProxyServerRequestType(ProxyServerRequestType.ProxyAcquirePPVToekn);
            psr.setCallback(this);
            psr.executeAsyncGet(requestItem);
        } else {
            Log.e("ProxyServerControl", "acquire token all values are null (fsa, npId and serviceId)");
            if (isFirstTime) {
                Log.e("ProxyServerControl", "first time acquire token with all params are null, return error");
                this.proxyServiceError(ErrorCodeString.GeneralError);
            } else {
                Log.i("ProxyServerControl", "during playback, ignore the acquire request");
            }
        }
    }

    public void acquireSVODToken(final boolean isFirstTime) {
        if (!(this.requestFsa == null && this.requestNetpassId == null && this.requestServiceId == null)) {
            this.isFirstTimeGetToken = isFirstTime;
            
            HttpRequestItem requestItem = new HttpRequestItem();
            requestItem.setUrl(URL_PROXY_ACUQIRE_SVOD_TOKEN);
            requestItem.addContent(PARAM_CUSTID, this.requestFsa);
            requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
            requestItem.addContent(PARAM_PRODUCT_ID, this.productId);
            requestItem.addContent(PARAM_LIBRARY_ID, this.libraryId);
            requestItem.addContent(PARAM_SCHEDULE_ID, this.scheduleId);
            requestItem.addContent(PARAM_TYPE, this.requestType);
            requestItem.addContent(PARAM_CLIENT_ID, PreferenceHelper.getGenerateUID());
            
//            this.executeAsyncGet(requestItem);
            ProxyServerRequest psr = new ProxyServerRequest();
            psr.setProxyServerRequestType(ProxyServerRequestType.ProxyAcquireSVODToken);
            psr.setCallback(this);
            psr.executeAsyncGet(requestItem);
        } else {
            Log.e("ProxyServerControl", "acquire token all values are null (fsa, npId and serviceId)");
            if (isFirstTime) {
                Log.e("ProxyServerControl", "first time acquire token with all params are null, return error");
                this.proxyServiceError(ErrorCodeString.GeneralError);
            } else {
                Log.i("ProxyServerControl", "during playback, ignore the acquire request");
            }
        }
    }

    private Handler handler = new Handler();
    
    private Runnable sendKeepAlive = new Runnable() {
        public void run() {
            if (!SERVICE_ID_FTA.equalsIgnoreCase(requestServiceId)) {
                Log.d("ProxyServerControl", "send keep alive");
                // !FTA
                HttpRequestItem requestItem = new HttpRequestItem();
                requestItem.setUrl(URL_VIPRION_KEEPALIVE.replaceAll("\\[ccDomain\\]", ccDomain));
                requestItem.addContent(PARAM_CUSTID, ccCustId);
                requestItem.addContent(PARAM_TOKEN, token);
                requestItem.addContent(PARAM_TYPE, ccPoolType);
                requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
                
                ProxyServerRequest psr = new ProxyServerRequest();
                psr.setProxyServerRequestType(ProxyServerRequestType.ProxyKeepAlive);
                psr.setCallback(ProxyServerControl.this);
                psr.setTimeout(keepaliveInterval*ONE_SECOND);
                psr.executeAsyncGet(requestItem);
                
                handler.postDelayed(sendKeepAlive, keepaliveInterval*ONE_SECOND);
            }
        }
    };
    
    private Runnable checkBindingStatus = new Runnable() {
        public void run() {
            if ((VideoType.VideoTypeLive == currentPlayerVideoType) 
                    && !(SERVICE_ID_FTA.equalsIgnoreCase(requestServiceId))) {
                Log.d("ProxyServerControl", "checkBindingStatus");
                // live and !FTA
                HttpRequestItem requestItem = new HttpRequestItem();
                requestItem.setUrl(URL_PROXY_CHECK_BINDING);
                requestItem.addContent(PARAM_CUSTID, requestFsa);
                requestItem.addContent(PARAM_SERVICE_ID, requestServiceId);
                requestItem.addContent(PARAM_NETPASS_ID, requestNetpassId);
                requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
                requestItem.addContent(PARAM_TYPE, requestType);

                ProxyServerRequest psr = new ProxyServerRequest();
                psr.setProxyServerRequestType(ProxyServerRequestType.ProxyCheckBinding);
                psr.setCallback(ProxyServerControl.this);
                psr.executeAsyncGet(requestItem);

                handler.postDelayed(checkBindingStatus, checkSubInterval * ONE_SECOND);
            }
        }
    };
    
    private Runnable checkSubscriptionStatus = new Runnable() {
        public void run() {
            if ((VideoType.VideoTypeLive == currentPlayerVideoType) 
                    && !(SERVICE_ID_FTA.equalsIgnoreCase(requestServiceId)) 
                    && !(SERVICE_ID_FSA.equalsIgnoreCase(requestServiceId))) {
                Log.d("ProxyServerControl", "checkSubscriptionStatus");
                // live and !FTA and !FSA
                HttpRequestItem requestItem = new HttpRequestItem();
                requestItem.setUrl(URL_PROXY_CHECK_SUB);
                requestItem.addContent(PARAM_CUSTID, requestFsa);
                requestItem.addContent(PARAM_SERVICE_ID, requestServiceId);
                requestItem.addContent(PARAM_NETPASS_ID, requestNetpassId);
                requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
                requestItem.addContent(PARAM_TYPE, requestType);

                ProxyServerRequest psr = new ProxyServerRequest();
                psr.setProxyServerRequestType(ProxyServerRequestType.ProxyCheckSubscription);
                psr.setCallback(ProxyServerControl.this);
                psr.executeAsyncGet(requestItem);

                handler.postDelayed(checkSubscriptionStatus, checkSubInterval * ONE_SECOND);
            }
        }
    };
    
    public void terminateToken() {
        if (!(SERVICE_ID_FTA.equalsIgnoreCase(this.requestServiceId)) && (this.token != null)) {
            // !FTA
        	HttpRequestItem requestItem = new HttpRequestItem();
            requestItem.setUrl(URL_VIPRION_TERM_TOKEN.replaceAll("\\[ccDomain\\]", ccDomain));
            requestItem.addContent(PARAM_CUSTID, this.ccCustId);
            requestItem.addContent(PARAM_TOKEN, this.token);
            requestItem.addContent(PARAM_FORMAT, PARAM_JSON);
            requestItem.addContent(PARAM_TYPE, this.ccPoolType);
            
            ProxyServerRequest psr = new ProxyServerRequest();
            psr.setProxyServerRequestType(ProxyServerRequestType.ProxyTermToken);
            psr.setCallback(this);
            psr.executeAsyncGet(requestItem);
            
            this.removeAllHandler();
            this.clearAllProperties();
        }
    }
    
    public void startConcurrentControl() {
        this.acquireToken(true);
    }
    
    
    public void startConcurrentControl(final String fsa, final String serviceId, final String netpassId, final VideoType videoType) {
        this.requestFsa = fsa;
        this.requestServiceId = serviceId;
        this.requestNetpassId = netpassId;
        this.currentPlayerVideoType = videoType;
        this.requestType = "NOWJR";
        this.acquireToken(true);
    }
    
    public void startConcurrentControl(final String fsa, final String productId, final String seriesId, final String format, final VideoType videoType) {
        this.requestFsa = fsa;
        this.productId = productId;
        this.seriesId = seriesId;
        this.videoFormat = format;
        this.currentPlayerVideoType = videoType;
        this.requestType = "VEPPV";
        this.acquirePPVToken(true);
    }

    public void startSVODConcurrentControl(final String fsa, final String productId, final String libraryId, final String scheduleId, final VideoType videoType) {
        this.requestFsa = fsa;
        this.productId = productId;
        this.libraryId = libraryId;
        this.scheduleId = scheduleId;
        this.currentPlayerVideoType = videoType;
        this.requestType = "VEPPV";
        this.acquireSVODToken(true);
    }
    
    private void removeAllHandler() {
        // reset all 3 timer handlers keepalive checkbinging checksub
        handler.removeCallbacks(sendKeepAlive);
        handler.removeCallbacks(checkBindingStatus);
        handler.removeCallbacks(checkSubscriptionStatus);
    }
    
    @Override
    public void didProxyServiceLoaded(JSONObject jsonObj,
            ProxyServerRequestType requestType) {
        Log.d("ProxyServerControl", "proxy response: " + jsonObj.toString());
        try {
            boolean result = jsonObj.getBoolean(PARAM_RESULT);
            String errorCode = jsonObj.optString(PARAM_ERRCODE, "");
            
            switch (requestType) {
            case ProxyAcquireToekn:
                if (result) {
                    this.token = jsonObj.getString(PARAM_TOKEN);
                    this.checkSubInterval = jsonObj.getLong(PARAM_CHECK_SUB_INTERVAL);
                    this.keepaliveInterval = jsonObj.getInt(PARAM_KEEPALIVE_INTERVAL);
                    // keep alive checking for both live / vod
                    handler.postDelayed(sendKeepAlive, keepaliveInterval*ONE_SECOND);
//                    if (this.isFirstTimeGetToken) {
                        if (this.currentPlayerVideoType == VideoType.VideoTypeLive) {
                            // if it is playing live video
                            if (!this.requestServiceId.equals(SERVICE_ID_FTA)) {
                                // live and !FTA
                                handler.postDelayed(checkBindingStatus, checkSubInterval*ONE_SECOND);
                                if (!this.requestServiceId.equals(SERVICE_ID_FSA)) {
                                    // live and !FTA and !FSA
                                    handler.postDelayed(checkSubscriptionStatus, checkSubInterval*ONE_SECOND);
                                }
                            }
                        }
//                    }
                    if (callback!=null && this.isFirstTimeGetToken) { 
                        callback.tokenIsValid();
                    }
                } else {
                    this.proxyServiceError(errorCode);
                }
                break;
            case ProxyAcquirePPVToekn:
            case ProxyAcquireSVODToken:
                if (result) {
                    this.token = jsonObj.getString(PARAM_TOKEN);
                    this.keepaliveInterval = jsonObj.getInt(PARAM_KEEPALIVE_INTERVAL);
                    if (jsonObj.has(PARAM_DURATION)) {
                    	this.duration = jsonObj.getLong(PARAM_DURATION);
                    }
                    if (jsonObj.has(PARAM_DATE)) {
                    	this.date = jsonObj.getString(PARAM_DATE);
                    }
                    handler.postDelayed(sendKeepAlive, keepaliveInterval*ONE_SECOND);
//                    
                    if (callback!=null && this.isFirstTimeGetToken) { 
                        callback.tokenIsValid();
                    }
                } else {
                    this.proxyServiceError(errorCode);
                }
                break;
            case ProxyKeepAlive: 
                if (!result) {
                    Log.d("ProxyServerControl", "keep alive return false, get token again");
                    this.removeAllHandler();
//                    handler.removeCallbacks(sendKeepAlive);
                    if (this.currentPlayerVideoType.equals(VideoType.VideoTypeVE)) {
                    	this.acquirePPVToken(false);
                    } else if (this.currentPlayerVideoType.equals(VideoType.VideoTypeVESVOD)) {
                    	this.acquireSVODToken(false);
                    } else {
                    	this.acquireToken(false);
                    }
                }
                break;
            case ProxyCheckBinding:
                if (!result) {
                    // temporary ignore the check binding error
//                    this.proxyServiceError(errorCode);
                }
                break;
            case ProxyCheckSubscription:
                if (!result) {
                    this.proxyServiceError(errorCode);
                }
                break;
            case ProxyTermToken:
                if (callback!=null) {
                    callback.didProxyTermToken(result, errorCode);
                }
                break;
            default:
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.proxyServiceError(ErrorCodeString.ParseJsonError);
        }
        
        
    }

    @Override
    public void proxyServiceError(String errorCode) {
        Log.d("ProxyServerControl", "error code: " + errorCode);
        this.removeAllHandler();
        if (callback != null) {
            callback.proxyServerControlError(errorCode);
        }
        
    }
    
    public void stopAllTimer() {
        this.removeAllHandler();
    }
    
    public void resumeConcurrentControl() {
        if (!SERVICE_ID_FTA.equalsIgnoreCase(requestServiceId) && (requestServiceId != null)) {
            handler.removeCallbacks(sendKeepAlive);
            handler.postDelayed(sendKeepAlive, ONE_SECOND);
        }
    }
    
    /**
     * Sharp Cut in seconds
     * @return
     */
	public long getDuration() {
		return duration;
	}
	
	public String getDate() {
		return date;
	}


	public static void setCcDomain(String ccDomain) {
		ProxyServerControl.ccDomain = ccDomain;
	}

	public static void setCcPoolType(String ccPoolType) {
		ProxyServerControl.ccPoolType = ccPoolType;
	}

	public static void setCcCustId(String ccCustId) {
		ProxyServerControl.ccCustId = ccCustId;
	}
	
	
	
}
