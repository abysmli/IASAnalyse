package net.icedeer.abysmli.iasanalyse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;

import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.httpHandler.PMSHttpRequest;
import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;
import net.icedeer.abysmli.iasanalyse.model.DatabaseHandler;
import net.icedeer.abysmli.iasanalyse.view.ComponentsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ComponentDetailsActivity extends AppCompatActivity {

    private Handler updateComponentHandler = new Handler();
    private DeviceHttpRequest device_http;
    private PMSHttpRequest pms_http;
    private boolean runnable_flag = true;
    private LineChartView value_chart;
    private float _data[] = new float[100];
    private String _labels[] = new String[100];
    private ComponentDataStruct component;
    private String status_flag = "";
    private String error_string = "";
    private Button status_button;
    String component_id = "";

    TextView comp_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_component_details);

        Intent intent = getIntent();
        component_id = String.valueOf(intent.getIntExtra(ComponentsFragment.COMPONENT_ID, -1));
        DatabaseHandler db = new DatabaseHandler(this);
        component = db.getComponent(Integer.parseInt(component_id));
        TextView comp_id = (TextView) findViewById(R.id.compt_id);
        TextView comp_name = (TextView) findViewById(R.id.compt_name);
        TextView comp_ser = (TextView) findViewById(R.id.compt_ser);
        TextView comp_typ = (TextView) findViewById(R.id.compt_typ);
        TextView comp_desc = (TextView) findViewById(R.id.compt_desc);
        status_button = (Button) findViewById(R.id.status_button);
        comp_status = (TextView) findViewById(R.id.compt_status);
        comp_id.setText((Html.fromHtml("<b>Component ID:</b> " + component.get_component_id())));
        comp_name.setText(Html.fromHtml("<b>Component Name: </b> " + component.get_component_name()));
        comp_ser.setText(Html.fromHtml("<b>Component Series: </b> " + component.get_series()));
        comp_typ.setText(Html.fromHtml("<b>Component Type: </b> " + component.get_type()));
        comp_desc.setText(Html.fromHtml("<b>Component Description: </b> " + component.get_component_description()));

        value_chart = (LineChartView) findViewById(R.id.value_chart);
        LineSet dataset = new LineSet();
        initDataSet();

        dataset.addPoints(_labels, _data);
        dataset.setLineColor(Color.parseColor("#FE2E2E"));
        dataset.setLineThickness((float) 1);
        value_chart.addData(dataset);

        value_chart.setXLabels(AxisController.LabelPosition.NONE);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#F2F2F2"));
        value_chart.setGrid(ChartView.GridType.FULL, paint);
        Paint paint_threshold_up = new Paint();
        paint_threshold_up.setColor(Color.parseColor("#2E2EFE"));
        value_chart.setThresholdLine(90, paint_threshold_up);
        value_chart.setAxisBorderValues(-10, 110, 10);
        value_chart.show();
        device_http = new DeviceHttpRequest(this, MainActivity.ip + ":3000");
        pms_http = new PMSHttpRequest(this, MainActivity.ip + ":8080");
        updateComponentHandler.post(getComponentDetailsThread);
    }

    @Override
    public void onPause() {
        super.onPause();
        runnable_flag = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        runnable_flag = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_component_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable getComponentDetailsThread = new Runnable() {
        @Override
        public void run() {
            if (runnable_flag && !status_flag.equals("inactive")) {
                device_http.getComponentValuebyID(component_id, componentHandler, componentErrorHandler);
                updateComponentHandler.postDelayed(this, 1000);
            }
        }
    };

    private void updateDataSet(float _new) {
        for (int i = 0; i < 99; i++) {
            _data[i] = _data[i + 1];
        }
        _data[99] = _new;
    }

    private void initDataSet() {
        Log.i("graph", "get");
        for (int i = 0; i < 100; i++) {
            _data[i] = 1;
            _labels[i] = "";
        }
    }

    private void reportError() {
        LogRecorder.Log("Report Error informations...", this);
        Map<String, String> params = new HashMap<>();
        params.put("component_id", String.valueOf(component.get_component_id()));
        params.put("error_type", "drift");
        params.put("error_desc", error_string);
        pms_http.reportError(params, reportHandler, reportErrorHandler);
    }

    public void status_change(View view) {
        if (status_flag.equals("active")) {
            status_flag = "inactive";
            status_button.setText("Activate");
            device_http.closeComponentbyID(component_id, closeHandler, closeErrorHandler);
            LogRecorder.Log("User suspend component " + component.get_component_id() + " manual!", this);
        } else {
            status_flag = "active";
            status_button.setText("Suspend");
            updateComponentHandler.post(getComponentDetailsThread);
            device_http.activateComponentbyID(component_id, closeHandler, closeErrorHandler);
        }
    }

    Response.Listener<JSONObject> componentHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                float value = Float.parseFloat(response.getString("value"));
                if (value > 90) {
                    Toast.makeText(getApplicationContext(), "Value is now over the Threshold! Error!", Toast.LENGTH_LONG).show();
                    TextView error_info = (TextView) findViewById(R.id.err_info);
                    error_string = "Value is over the Limit of Sensor! Error!";
                    error_info.setText(Html.fromHtml("<b>Error: </b>" + error_string));
                    reportError();
                }
                updateDataSet(value);
                status_flag = response.getString("status");
                comp_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
                if (status_flag.equals("running")) {
                    status_button.setText("Suspend");
                } else {
                    status_button.setText("Activate");
                }
                value_chart.updateValues(0, _data);
                value_chart.notifyDataUpdate();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener componentErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    Response.Listener<JSONObject> closeHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                status_flag = response.getString("status");
                status_button.setText("Activate");
                comp_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener closeErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    Response.Listener<String> reportHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String _response) {
            try {
                JSONObject response = new JSONObject(_response);
                Toast.makeText(getApplicationContext(), "Response from Server:\nError ID: " + response.getInt("error_id") + "\nComponent ID: " + response.getString("component_id") + "\nError Type: " + response.getString("error_type") + "\nError Description: " + response.getString("error_desc") + "\nExecute Command: " + response.getString("execute_command"), Toast.LENGTH_LONG).show();
                //LogRecorder.Log("Response from Server:\nError ID: " + response.getInt("error_id") + "\nComponent ID: " + response.getString("component_id") + "\nError Type: " + response.getString("error_type") + "\nError Description: " + response.getString("error_desc") + "\nExecute Command: " + response.getString("execute_command"), getApplication());
                device_http.executeCommand(response, executeHandler, executeErrorHandler);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener reportErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    Response.Listener<JSONObject> executeHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.i("executeHandler", response.toString());
            Map<String, String> params = new HashMap<>();
            params.put("updateMeta", response.toString());
            pms_http.updateStatus(params, updateStatusHandler, updateStatusErrorHandler);
            status_flag = "inactive";
            status_button.setText("Activate");
            comp_status.setText((Html.fromHtml("<b>Component Status:</b> " + status_flag)));
        }
    };


    Response.ErrorListener executeErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    Response.Listener<String> updateStatusHandler = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                if ((new JSONObject(response)).getString("result").equals("success")) {
                    Toast.makeText(getApplicationContext(), "Update Status Successed!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener updateStatusErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

}
