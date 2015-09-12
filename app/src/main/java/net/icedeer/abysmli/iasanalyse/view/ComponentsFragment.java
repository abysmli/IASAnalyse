package net.icedeer.abysmli.iasanalyse.view;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.icedeer.abysmli.iasanalyse.ComponentDetailsActivity;
import net.icedeer.abysmli.iasanalyse.ControllerPanelActivity;
import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;
import net.icedeer.abysmli.iasanalyse.model.DatabaseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ComponentsFragment extends ListFragment {


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> schedulerHandler;
    // ListView items list
    private List<ComponentsContainer> mItems;

    private DeviceHttpRequest device_http;
    private ComponentsViewAdapter components_adapter;
    private List<ComponentDataStruct> components;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the items list
        mItems = new ArrayList<>();
        Resources resources = getResources();
        DatabaseHandler db = new DatabaseHandler(getActivity());

        // Reading all contacts
        components = db.getAllComponents();

        for (ComponentDataStruct component : components) {
            mItems.add(new ComponentsContainer(resources.getDrawable(R.drawable.red_light, null), component.get_component_name() + " " + component.get_series(), component.get_component_description()));
        }

        components_adapter = new ComponentsViewAdapter(getActivity(), mItems);
        // initialize and set the list adapter
        setListAdapter(components_adapter);

        device_http = ((ControllerPanelActivity) getActivity()).getDeviceHttpRequester();


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        schedulerHandler.cancel(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        schedulerHandler = scheduler.scheduleAtFixedRate(getComponentsStatusThread, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        Intent intent = new Intent(getActivity(), ComponentDetailsActivity.class);
        intent.putExtra(AppSetting.COMPONENT_ID, position + 1);
        startActivity(intent);
    }

    private final Runnable getComponentsStatusThread = new Runnable() {
        public void run() {
            device_http.getComponentsStatus(componentsStatusSuccessHandler, componentsStatusErrorHandler);
        }
    };

    private final Response.Listener<JSONArray> componentsStatusSuccessHandler = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); i++) {
                    if (response.getJSONObject(i).getString("status").equals("active")) {
                        mItems.set(i, new ComponentsContainer(getResources().getDrawable(R.drawable.green_light, null), components.get(i).get_component_name() + " " + components.get(i).get_series(), components.get(i).get_component_description()));
                    } else {
                        mItems.set(i, new ComponentsContainer(getResources().getDrawable(R.drawable.red_light, null), components.get(i).get_component_name() + " " + components.get(i).get_series(), components.get(i).get_component_description()));
                    }
                }
                components_adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.ErrorListener componentsStatusErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogRecorder.Log("Get components states failed!", getActivity());
        }
    };
}