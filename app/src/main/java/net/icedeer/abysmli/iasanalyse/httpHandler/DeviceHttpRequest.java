package net.icedeer.abysmli.iasanalyse.httpHandler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import net.icedeer.abysmli.iasanalyse.httpHandler.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Li, Yuan on 22.06.15.
 * All Right reserved!
 */
public class DeviceHttpRequest extends HttpRequest {
    private String root_url;

    public DeviceHttpRequest(Context context, String root_url) {
        super(context);
        this.root_url = root_url;
    }

    public void getComponentValuebyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.GET, null, root_url+"/component?action=getvalue&component_id="+component_id, responseListener, errorListener);
    }

    public void executeCommand(JSONObject command, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.POST, command, root_url + "/execute_strategy", responseListener, errorListener);
    }

    public void closeComponentbyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.GET, null, root_url+"/component?action=deactivate&component_id="+component_id, responseListener, errorListener);
    }

    public void activateComponentbyID(String component_id, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.GET, null, root_url+"/component?action=activate&component_id="+component_id, responseListener, errorListener);
    }

    public void getComponentsStatus(Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        super.sendRequestJSONArray(Request.Method.GET, null, root_url + "/get_components_status", responseListener, errorListener);
    }

    public void getDeviceStatus(Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super.sendRequest(Request.Method.GET, null, root_url+"/status", responseListener, errorListener);
    }
}
