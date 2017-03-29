package fr.univ_lille1.iut_info.caronic.kreasport.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    private RequestQueue mRequestQueue;
    private static final String TAG = VolleySingleton.class.getSimpleName();

//    private VolleySingleton(Context context) {
//        mRequestQueue = Volley.newRequestQueue(context, new ProxyHurlStack());
//    }
    private VolleySingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        req.setTag(tag == null ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequestsNoTag() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    public void clearVolleyCache() {
        if (mRequestQueue != null) {
            mRequestQueue.getCache().clear();
        }
    }
}