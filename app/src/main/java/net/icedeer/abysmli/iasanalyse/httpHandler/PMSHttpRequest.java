package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Li, Yuan on 24.06.15.
 * All Right reserved!
 */
public class PMSHttpRequest extends HttpRequest {
    private final String root_url;

    public PMSHttpRequest(Context context) {
        super(context);
        this.root_url = net.icedeer.abysmli.iasanalyse.controller.AppSetting.PMSIPAddress;
    }

    public void updateStatus(Map<String,String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(params, root_url + "/updateStatus", responseListener, errorListener);
    }

    public void reportError(Map<String,String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(params, root_url + "/reportError", responseListener, errorListener);
    }

    public void getPMSStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(root_url + "/status", responseListener, errorListener);
    }

    public void login(Map<String, String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(params, root_url + "/user/login", responseListener, errorListener);
    }
}
