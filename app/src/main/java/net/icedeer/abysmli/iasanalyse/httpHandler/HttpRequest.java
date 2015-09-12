package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Li, Yuan on 22.06.15.
 * All Right reserved!
 */
class HttpRequest {

    private final Context context;

    HttpRequest(Context context) {
        this.context = context;
    }

    void sendRequest(String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, (JSONObject) null, responseListener, errorListener);
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }

    void sendRequestString(final Map<String, String> params, String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, responseListener, errorListener)
        {
            @Override
            public String getBodyContentType()
            {
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }

    void sendRequestJSONArray(String url, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.GET, url, (JSONObject) null, responseListener, errorListener);
        // Add the request to the RequestQueue.
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }
}
