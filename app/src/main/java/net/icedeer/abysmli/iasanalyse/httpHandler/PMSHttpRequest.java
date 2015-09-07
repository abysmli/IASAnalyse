package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Li, Yuan on 24.06.15.
 * All Right reserved!
 */
public class PMSHttpRequest extends HttpRequest {
    private String root_url;
    public Context context;

    public PMSHttpRequest(Context context, String root_url) {
        super(context);

        this.context = context;
        this.root_url = root_url+"/PMS";
    }

    public void updateStatus(Map<String,String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(Request.Method.POST, params, root_url + "/updateStatus", responseListener, errorListener);
    }

    public void reportError(Map<String,String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(Request.Method.POST, params, root_url + "/reportError", responseListener, errorListener);
    }

    public void getPMSStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.GET, null, root_url+"/status", responseListener, errorListener);
    }
}
