package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Li, Yuan on 25.06.15.
 * All Right reserved!
 */
public class SingletonQueue {
    private static SingletonQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private SingletonQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SingletonQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonQueue(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
