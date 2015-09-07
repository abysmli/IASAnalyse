package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Li, Yuan on 22.06.15.
 * All Right reserved!
 */
public class HttpRequest {

    private Context context;

    public HttpRequest(Context context) {
        this.context = context;
    }

    public void sendRequest(int method, JSONObject params, String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(method, url, params, responseListener, errorListener);
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }

    public void sendRequestString(int method, final Map<String,String> params, String url, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        StringRequest request = new StringRequest(method, url, responseListener, errorListener)
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

    public void sendRequestJSONArray(int method, JSONObject params, String url, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(method, url, params, responseListener, errorListener);
        // Add the request to the RequestQueue.
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }
}
