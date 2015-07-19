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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;

import org.json.JSONException;
import org.json.JSONObject;


public class ComponentDetailsActivity extends AppCompatActivity {

    private Handler updateComponentHandler = new Handler();
    private String component_api;
    private DeviceHttpRequest device_http;
    private PMSHttpRequest pms_http;
    private boolean runnable_flag = true;
    private LineChartView value_chart;
    private LineSet dataset;
    private float _data[] = new float[100];
    private String _labels[] = new String[100];
    private ComponentDataStruct component;
    private String status_flag = "";
    private String error_string = "";
    private Button status_button;

    TextView comp_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_component_details);

        Intent intent = getIntent();
        long component_id = intent.getIntExtra(ComponentsFragment.COMPONENT_ID, -1);
        DatabaseHandler db = new DatabaseHandler(this);
        component = db.getComponent((int) component_id);
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
        dataset = new LineSet();
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
        component_api = getResources().getStringArray(R.array.component_apis)[(int) component_id - 1];
        device_http = new DeviceHttpRequest(this, MainActivity.ip+":3000");
        pms_http = new PMSHttpRequest(Request.Method.POST, this, MainActivity.ip+":3001");
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
            if (runnable_flag && !status_flag.equals("stop")) {
                device_http.getComponentInfobyID(component_api, componentHandler, componentErrorHandler);
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
        JSONObject param = new JSONObject();
        try {
            param.put("component_name", component.get_component_name());
            param.put("component_id", component.get_component_id());
            param.put("error_info", error_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogRecorder.Log("Report Error informations...", this);
        pms_http.reportError(param, reportHandler, reportErrorHandler);
    }

    public void status_change(View view) {
        if (status_flag.equals("running")) {
            status_flag = "stop";
            status_button.setText("Activate");
            device_http.closeComponentbyID(component_api, closeHandler, closeErrorHandler);
            LogRecorder.Log("User suspend component "+component.get_component_id()+" manual!", this);
        } else {
            status_flag = "running";
            status_button.setText("Suspend");
            updateComponentHandler.post(getComponentDetailsThread);
            device_http.activateComponentbyID(component_api, closeHandler, closeErrorHandler);
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
                    error_string = "Value is now over the Threshold! Error!";
                    error_info.setText(Html.fromHtml("<b>Error: </b>"+error_string));
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

    Response.Listener<JSONObject> reportHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                if ((response.getString("exec")).equals("stop")) {
                    device_http.closeComponentbyID(component_api, closeHandler, closeErrorHandler);
                }
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

}
