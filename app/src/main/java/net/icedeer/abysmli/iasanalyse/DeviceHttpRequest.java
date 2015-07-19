package net.icedeer.abysmli.iasanalyse;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by abysmli on 22.06.15.
 */
public class DeviceHttpRequest extends HttpRequest {
    private String root_url;

    public DeviceHttpRequest(Context context, String root_url) {
        super(context, Request.Method.GET);
        this.root_url = root_url;
    }

    public void getComponentInfobyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(null, root_url+component_id, responseListener, errorListener);
    }

    public void closeComponentbyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(null, root_url+component_id+"/close", responseListener, errorListener);
    }

    public void activateComponentbyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(null, root_url+component_id+"/activate", responseListener, errorListener);
    }

    public void getComponentsStatus(Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestJSONArray(null, root_url + "/get_components_status", responseListener, errorListener);
    }

    public void getDeviceStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(null, root_url+"/status", responseListener, errorListener);
    }
}
