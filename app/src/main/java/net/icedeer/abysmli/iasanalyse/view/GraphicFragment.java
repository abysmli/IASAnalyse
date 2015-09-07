package net.icedeer.abysmli.iasanalyse.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.icedeer.abysmli.iasanalyse.MainActivity;
import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.httpHandler.PMSHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphicFragment extends Fragment {


    public GraphicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graphic, container, false);
        final ImageView pms_status_light = (ImageView) view.findViewById(R.id.pms_status_light);
        final ImageView abf_status_light = (ImageView) view.findViewById(R.id.abf_status_light);

        DeviceHttpRequest device_http = new DeviceHttpRequest(getActivity(), MainActivity.ip+":3000");
        LogRecorder.Log("Try to connect Abfuellanlage...", getActivity());
        device_http.getDeviceStatus(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("running")) {
                        abf_status_light.setImageDrawable(getResources().getDrawable(R.drawable.red_light, null));
                        LogRecorder.Log("Connect to Abfuellanlage failed!", getActivity());
                    } else {
                        abf_status_light.setImageDrawable(getResources().getDrawable(R.drawable.green_light, null));
                        LogRecorder.Log("Connect to Abfuellanlage successed!", getActivity());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                abf_status_light.setImageDrawable(getResources().getDrawable(R.drawable.red_light, null));
                LogRecorder.Log("Connect to Abfuellanlage failed!", getActivity());
            }
        });

        PMSHttpRequest pms_http = new PMSHttpRequest(getActivity(), MainActivity.ip+":8080");
        LogRecorder.Log("Try to connect Problemmanagementsystem...", getActivity());
        pms_http.getPMSStatus(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("running")) {
                        pms_status_light.setImageDrawable(getResources().getDrawable(R.drawable.red_light, null));
                        LogRecorder.Log("Connect to Problemmanagementsystem failed!", getActivity());
                    } else {
                        pms_status_light.setImageDrawable(getResources().getDrawable(R.drawable.green_light, null));
                        LogRecorder.Log("Connect to Problemmanagementsystem successed!", getActivity());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pms_status_light.setImageDrawable(getResources().getDrawable(R.drawable.red_light, null));
                LogRecorder.Log("Connect to Problemmanagementsystem failed!", getActivity());
            }
        });

        return view;
    }


}
