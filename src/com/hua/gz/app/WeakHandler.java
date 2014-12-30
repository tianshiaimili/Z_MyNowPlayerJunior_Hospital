package com.hua.gz.app;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * If Handler class is not static, it will have a reference to your Activity/Service/... object.
 * Handler objects for the same thread all share a common Looper object, which they post messages to and read from.
 * As messages contain target Handler, as long as there are messages with target handler in the message queue, the handler cannot be garbage collected. 
 * If handler is not static, your Service or Activity cannot be garbage collected, even after being destroyed.
 * This may lead to memory leaks, for some time at least - as long as the messages stay int the queue. 
 * This is not much of an issue unless you post long delayed messages.
 * 
 * If you want to use a nested class, it has to be static. 
 * Otherwise, WeakReference doesn't change anything. 
 * Inner (nested but not static) class always holds strong reference to outer class. 
 * There is no need for any static variables though.
 * 
 * T can be Activity, Service, Fragment and so on.
 * 
 * @author AlfredZhong
 * @version 2013-06-04
 */
public abstract class WeakHandler<T> extends Handler {
	
	private static final String TAG = WeakHandler.class.getSimpleName();
	private final WeakReference<T> mContextObject;
	private boolean mstopped;
	
	public WeakHandler(T contextObject) {
		mContextObject = new WeakReference<T>(contextObject);
	}
	
	public void stopHandlingMessage() {
		mstopped = true;
	}
	
	public void restartHandlingMessage() {
		mstopped = false;
	}
	
	// Use final, no sub-class can override this method. Sub-class should use handleWeakHandlerMessage instead.
    @Override
	public final void handleMessage(Message msg) {
		if (mstopped) {
			Log.v(TAG, "Handler stop handling message.");
			return;
		}
		T obj = mContextObject.get();
		if (obj != null) {
			if (obj instanceof Fragment) {
				Fragment f = (Fragment) obj;
				// You may receive Handler messages or callbacks after the Activity or Fragment or View is destroyed.
				// Therefore, you should check context, findViewById() and so on inside callbacks before updating UI.
				// So we will NOT send message to the Fragment which getActivity() is null since the fragment can NOT be re-attach again.
				if (f.getActivity() == null) {
					// the fragment is after onDetach(), that means FragmentTransation.remove() has been called.
					Log.e(TAG, f.getClass().getSimpleName() + ".getActivity()=null.");
					return;
				}
				if (f.getView() == null) {
					// only not null between "onActivityCreated() -- onDestroyView()"
					Log.e(TAG, f.getClass().getSimpleName() + ".getView()=null.");
					// When you reach here, that means the fragment was detached but not removed, it can be re-attach.
					// Some fragment may hold the fragment view or sub views directly or they just use handler to refresh data,
					// in order to keep the UI(or data) up-to-date, still allow to send message to update UI even getView() null.
				}
			}
			// If you use a nested class, you can directly call obj.xxx() here.
			handleWeakHandlerMessage(obj, msg);
		}
	}
    
    public abstract void handleWeakHandlerMessage(T contextObject, Message msg);
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////// 		   How to use Handler correctly				    //////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    /*
    
    // This class is for you to copy to use. Just replace the T to your class.
    // Here is CheckoutSupportBaseFragment. It is OK to new WeakHandler instance like:     
    
    private final InnerStaticHandler mHandler = new InnerStaticHandler(this);
	private static class InnerStaticHandler extends WeakHandler<CheckoutSupportBaseFragment> {

		public InnerStaticHandler(CheckoutSupportBaseFragment contextObject) {
			super(contextObject);
		}

		@Override
		public void handleWeakHandlerMessage(CheckoutSupportBaseFragment contextObject, Message msg) {			
		}
		
	};
    
	// extends WeakHandler. Should be static !!!!!!
	private static class InnerStaticHandler extends WeakHandler<BaseSplashActivity> {
		
		InnerStaticHandler(BaseSplashActivity act) {
			super(act);
		}

		@Override
		public void handleWeakHandlerMessage(BaseSplashActivity contextObject, Message msg) {
			switch (msg.what) {
			case HANDLER_NETWORK_UNAVAILABLE:
				// call BaseSplashActivity methods directly.
				contextObject.removeAllHandlerMessage();
				if(!contextObject.isFinishing()) {
					contextObject.showDialog(HANDLER_NETWORK_UNAVAILABLE);
				}
				break;
			}
		}
		
	}
	
	// extends Handler directly. Should be static !!!!!!
	private static class InnerStaticHandler extends Handler {
		
		// You should refer to the object by WeakReference to avoid memory leak.
		private final WeakReference<BaseSplashActivity> mContextObject;
		
		InnerStaticHandler(BaseSplashActivity object) {
			mContextObject = new WeakReference<BaseSplashActivity>(object);
		}
		
	    @Override
	    public final void handleMessage(Message msg){
	    	BaseSplashActivity act = mContextObject.get();
	    	// You should check whether the referent is null.
			if (act != null) {
				switch (msg.what) {
				case HANDLER_NETWORK_UNAVAILABLE:
					act.removeAllHandlerMessage();
					if(!act.isFinishing()) {
						act.showDialog(HANDLER_NETWORK_UNAVAILABLE);
					}
					break;
				}
			}
	    }

	}
	
	*/
	
}
