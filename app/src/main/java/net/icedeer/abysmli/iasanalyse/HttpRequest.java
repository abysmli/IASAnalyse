package net.icedeer.abysmli.iasanalyse;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by abysmli on 22.06.15.
 */
public class HttpRequest {

    private Context context;
    private int method;

    public HttpRequest(Context context, int method) {
        this.context = context;
        this.method = method;
    }

    public void sendRequest(JSONObject params, String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(this.method, url, params, responseListener, errorListener);
        // Add the request to the RequestQueue.
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }

    public void sendRequestJSONArray(JSONObject params, String url, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(this.method, url, params, responseListener, errorListener);
        // Add the request to the RequestQueue.
        SingletonQueue.getInstance(this.context).addToRequestQueue(request);
    }
}
