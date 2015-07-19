package net.icedeer.abysmli.iasanalyse;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by abysmli on 24.06.15.
 */
public class PMSHttpRequest extends HttpRequest {
    private String root_url;

    public PMSHttpRequest(int method, Context context, String root_url) {
        super(context, method);
        this.root_url = root_url;
    }

    public void reportError(JSONObject params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(params, root_url+"/reportError", responseListener, errorListener);
    }

    public void getPMSStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(null, root_url+"/status", responseListener, errorListener);
    }
}
