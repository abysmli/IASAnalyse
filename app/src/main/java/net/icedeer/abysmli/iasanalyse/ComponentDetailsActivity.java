package net.icedeer.abysmli.iasanalyse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.db.chart.view.LineChartView;

import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.controller.ComponentValueChart;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ComponentDetailsActivity extends AppCompatActivity {

    private Button status_button;
    private TextView component_status;

    private String component_id = "";

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> schedulerHandler;

    private DeviceHttpRequest device_http;
    private PMSHttpRequest pms_http;
    private ComponentValueChart chart;


    private String status_flag = "";

    @Override
    public void onPause() {
        super.onPause();
        schedulerHandler.cancel(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        schedulerHandler = scheduler.scheduleAtFixedRate(getComponentDetailsThread, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_component_details, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_details);
        Intent intent = getIntent();
        component_id = String.valueOf(intent.getIntExtra(AppSetting.COMPONENT_ID, -1));

        showComponentInfo();
        chart = new ComponentValueChart((LineChartView) findViewById(R.id.value_chart));
        chart.generateChart();

        status_button = (Button) findViewById(R.id.status_button);
        SessionManager sessionManager = new SessionManager(getBaseContext());
        if (!(sessionManager.getUserLevel().equals("admin") || sessionManager.getUserLevel().equals("maintainer"))) {
            status_button.setVisibility(View.GONE);
        }
        component_status = (TextView) findViewById(R.id.compt_status);

        device_http = new DeviceHttpRequest(this, AppSetting.DeviceAddress);
        pms_http = new PMSHttpRequest(this);
    }

    private void showComponentInfo() {
        DatabaseHandler db = new DatabaseHandler(this);
        ComponentDataStruct component = db.getComponent(Integer.parseInt(component_id));
        ((TextView) findViewById(R.id.compt_id)).setText((Html.fromHtml("<b>Component ID:</b> " + component.get_component_id())));
        ((TextView) findViewById(R.id.compt_name)).setText(Html.fromHtml("<b>Component Name: </b> " + component.get_component_name()));
        ((TextView) findViewById(R.id.compt_ser)).setText(Html.fromHtml("<b>Component Series: </b> " + component.get_series()));
        ((TextView) findViewById(R.id.compt_typ)).setText(Html.fromHtml("<b>Component Type: </b> " + component.get_type()));
        ((TextView) findViewById(R.id.compt_desc)).setText(Html.fromHtml("<b>Component Description: </b> " + component.get_component_description()));
    }

    private final Runnable getComponentDetailsThread = new Runnable() {
        public void run() {
            if (status_flag.equals("inactive")) {

                schedulerHandler.cancel(false);
            }
            device_http.getComponentValueByID(component_id, componentHandler, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        }
    };

    private void reportError(String errorString) {
        Map<String, String> params = new HashMap<>();
        params.put("component_id", component_id);
        params.put("error_type", "drift");
        params.put("error_desc", errorString);
        pms_http.reportError(params, reportHandler, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private final Response.Listener<JSONObject> componentHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                float value = Float.parseFloat(response.getString("value"));
                chart.update(value);
                if (value > 90) {
                    String errorString = "Value of component is over the Limit!";
                    Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
                    LogRecorder.Log("By component " + component_id + ", " + errorString + " Error has been reported to PMS!", getApplicationContext());
                    ((TextView) findViewById(R.id.err_info)).setText(Html.fromHtml("<b>Error: </b>" + errorString));
                    reportError(errorString);
                }

                status_flag = response.getString("status");
                component_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
                if (status_flag.equals("active")) {
                    status_button.setText("Deactivate");
                } else {
                    status_button.setText("Activate");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.Listener<String> reportHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String _response) {
            try {
                JSONObject response = new JSONObject(_response);
                Toast.makeText(getApplicationContext(), "Response from Server:\nComponent ID: " + response.getString("component_id") + "\nError Type: " + response.getString("error_type") + "\nError Description: " + response.getString("error_desc") + "\nExecute Command: " + response.getString("execute_command"), Toast.LENGTH_LONG).show();
                LogRecorder.Log("Send reconfiguration strategy to Devices ...", getApplicationContext());
                Map<String, String> params = new HashMap<>();
                params.put("executeStrategy", _response);
                device_http.executeCommand(params, executeHandler, new Response.ErrorListener() {
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
            LogRecorder.Log("Reconfiguration strategy executed in device successfully!", getApplicationContext());
            Map<String, String> params = new HashMap<>();
            params.put("updateMeta", response);
            pms_http.updateStatus(params, updateStatusHandler, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            status_flag = "inactive";
            status_button.setText("Activate");
            component_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
        }
    };

    private final Response.Listener<String> updateStatusHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                if ((new JSONObject(response)).getString("result").equals("success")) {
                    Toast.makeText(getApplicationContext(), "Components' status has been updated!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * Manual handles components
     */

    public void status_change(@SuppressWarnings("UnusedParameters") View view) {
        if (status_flag.equals("active")) {
            schedulerHandler.cancel(false);
            status_flag = "inactive";
            status_button.setText("Activate");
            device_http.deactivateComponentByID(component_id, manualSwitchHandler, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            LogRecorder.Log("User deactivates component " + component_id + " manually!", this);
        } else {
            schedulerHandler = scheduler.scheduleAtFixedRate(getComponentDetailsThread, 0, 1, TimeUnit.SECONDS);
            status_flag = "active";
            status_button.setText("Deactivate");
            device_http.activateComponentByID(component_id, manualSwitchHandler, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            LogRecorder.Log("User activates component " + component_id + " manually!", this);
        }
    }

    private final Response.Listener<JSONObject> manualSwitchHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                status_flag = response.getString("status");
                component_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
