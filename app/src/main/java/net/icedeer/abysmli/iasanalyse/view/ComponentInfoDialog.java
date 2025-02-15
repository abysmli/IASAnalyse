package net.icedeer.abysmli.iasanalyse.view;

/**
 * Created by Li, Yuan on 16.09.15.
 * All Right reserved!
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.controller.SessionManager;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.httpHandler.PMSHttpRequest;
import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;
import net.icedeer.abysmli.iasanalyse.model.DatabaseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ComponentInfoDialog extends DialogFragment {

    private int mComponentID;
    private String mComponentStatus;
    private String statusButtonText;
    private PMSHttpRequest pmsHttpRequest;
    private DeviceHttpRequest deviceHttpRequest;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View ComponentInfoView = inflater.inflate(R.layout.dialog_component_info, null);
        showComponentInfo(ComponentInfoView);
        builder.setTitle(R.string.component_info_dialog_title)
                .setView(ComponentInfoView)
                .setNegativeButton(R.string.done_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        SessionManager sessionManager = new SessionManager(getActivity());
        pmsHttpRequest = new PMSHttpRequest(getActivity());
        deviceHttpRequest = new DeviceHttpRequest(getActivity(), AppSetting.DeviceAddress);
        if (sessionManager.getUserLevel().equals("admin") || sessionManager.getUserLevel().equals("maintainer")) {
            builder.setPositiveButton(statusButtonText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (mComponentStatus.equals("inactive")) {
                        deviceHttpRequest.activateComponentByID(String.valueOf(mComponentID), changeStatusHandler, errorListener);
                    } else {
                        reportError("Manual Deactivate!");
                    }
                }
            });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private final Response.Listener<JSONObject> changeStatusHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

        }
    };

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };


    public void setComponentInfo(int componentID, boolean statusFlag) {
        this.mComponentID = componentID;
        if (statusFlag) {
            mComponentStatus = "active";
            statusButtonText = "Deactivate";
        } else {
            mComponentStatus = "inactive";
            statusButtonText = "Activate";
        }
    }

    private void showComponentInfo(View view) {
        DatabaseHandler db = new DatabaseHandler(getActivity());
        ComponentDataStruct mComponent = db.getComponent(mComponentID);
        ((TextView) view.findViewById(R.id.compt_id)).setText((Html.fromHtml("<b>Component ID:</b> " + mComponent.get_component_id())));
        ((TextView) view.findViewById(R.id.compt_name)).setText(Html.fromHtml("<b>Component Name: </b> " + mComponent.get_component_name()));
        ((TextView) view.findViewById(R.id.compt_ser)).setText(Html.fromHtml("<b>Component Series: </b> " + mComponent.get_series()));
        ((TextView) view.findViewById(R.id.compt_typ)).setText(Html.fromHtml("<b>Component Type: </b> " + mComponent.get_type()));
        ((TextView) view.findViewById(R.id.compt_desc)).setText(Html.fromHtml("<b>Component Description: </b> " + mComponent.get_component_description()));
        ((TextView) view.findViewById(R.id.compt_status)).setText(Html.fromHtml("<b>Component Status: </b> " + mComponentStatus));
    }

    private void reportError(String errorString) {
        Map<String, String> params = new HashMap<>();
        params.put("component_id", String.valueOf(mComponentID));
        params.put("error_type", "Manual");
        params.put("error_desc", errorString);
        pmsHttpRequest.reportError(params, reportHandler, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private final Response.Listener<String> reportHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String _response) {
            try {
                JSONObject response = new JSONObject(_response);
                //Toast.makeText(getContext(), "Response from Server:\nComponent ID: " + response.getString("component_id") + "\nError Type: " + response.getString("error_type") + "\nError Description: " + response.getString("error_desc") + "\nExecute Command: " + response.getString("execute_command"), Toast.LENGTH_LONG).show();
                //LogRecorder.Log("Send reconfiguration strategy to Devices ...", getActivity());
                Map<String, String> params = new HashMap<>();
                params.put("executeStrategy", _response);
                deviceHttpRequest.executeCommand(params, executeHandler, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.Listener<String> executeHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //LogRecorder.Log("Reconfiguration strategy executed in device successfully!", getActivity());
            Map<String, String> params = new HashMap<>();
            params.put("updateMeta", response);
            pmsHttpRequest.updateStatus(params, updateStatusHandler, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    };

    private final Response.Listener<String> updateStatusHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                if ((new JSONObject(response)).getString("result").equals("success")) {
                    //Toast.makeText(getActivity(), "Components' status has been updated!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
