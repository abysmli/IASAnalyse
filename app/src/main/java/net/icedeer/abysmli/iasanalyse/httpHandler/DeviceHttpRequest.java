package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Li, Yuan on 22.06.15.
 * All Right reserved!
 */
public class DeviceHttpRequest extends HttpRequest {
    private final String root_url;
    private Context context;

    public DeviceHttpRequest(Context context) {
        super(context);
        this.context = context;
        this.root_url = "";
    }

    public DeviceHttpRequest(Context context, String root_url) {
        super(context);
        this.root_url = root_url;
    }

    public void getComponentValueByID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(root_url+"/component?action=getvalue&component_id="+component_id, responseListener, errorListener);
    }

    public void executeCommand(Map<String,String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestString(params, root_url + "/execute_strategy", responseListener, errorListener);
    }

    public void deactivateComponentByID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(root_url+"/component?action=deactivate&component_id="+component_id, responseListener, errorListener);
    }

    public void activateComponentByID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(root_url+"/component?action=activate&component_id="+component_id, responseListener, errorListener);
    }

    public void getComponentsStatus(Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestJSONArray(root_url + "/get_components_status", responseListener, errorListener);
    }

    public void getDeviceStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(root_url + "/status", responseListener, errorListener);
    }

    public void detectDevice(String url, RequestFuture<JSONObject> future) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + "/status", future, future);
        request.setRetryPolicy(new DefaultRetryPolicy(net.icedeer.abysmli.iasanalyse.controller.AppSetting.AutoDetectionDeviceDelay /2, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingletonQueue.getInstance(context).addToRequestQueue(request);
    }
}
