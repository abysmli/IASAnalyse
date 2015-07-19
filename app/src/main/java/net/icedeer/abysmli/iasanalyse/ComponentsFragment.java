package net.icedeer.abysmli.iasanalyse;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ComponentsFragment extends ListFragment {

    private Handler updateComponentsHandler = new Handler();
    private List<ComponentsContainer> mItems;        // ListView items list

    public final static String COMPONENT_ID = "net.icedeer.IASAnalyse.ComponentId";
    private DeviceHttpRequest device_http;
    private ComponentsViewAdpter components_adapter;
    private List<ComponentDataStruct> components;
    private boolean runnable_flag = true;

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
            mItems.add(new ComponentsContainer(resources.getDrawable(R.drawable.green_light, null), component.get_component_name() + " " + component.get_series(), component.get_component_description()));
        }

        components_adapter = new ComponentsViewAdpter(getActivity(), mItems);
        //components_adapter.notifyDataSetChanged();
        // initialize and set the list adapter
        setListAdapter(components_adapter);

        device_http = new DeviceHttpRequest(getActivity(), MainActivity.ip+":3000");
        updateComponentsHandler.post(getComponentsStatusThread);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        Intent intent = new Intent(getActivity(), ComponentDetailsActivity.class);
        intent.putExtra(COMPONENT_ID, position + 1);
        startActivity(intent);
    }

    Response.Listener<JSONArray> componentsStatusSuccessHandler = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                LogRecorder.Log("Get every component running states...", getActivity());
                for (int i = 0; i < response.length(); i++) {
                    if (response.getString(i).equals("running")) {
                        mItems.set(i, new ComponentsContainer(getResources().getDrawable(R.drawable.green_light, null), components.get(i).get_component_name() + " " + components.get(i).get_series(), components.get(i).get_component_description()));
                    } else {
                        LogRecorder.Log("Component "+String.valueOf(i+1)+" is now suspend!", getActivity());
                        mItems.set(i, new ComponentsContainer(getResources().getDrawable(R.drawable.red_light, null), components.get(i).get_component_name() + " " + components.get(i).get_series(), components.get(i).get_component_description()));
                    }
                }
                components_adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener componentsStatusErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogRecorder.Log("Get components states failed!", getActivity());
        }
    };

    private Runnable getComponentsStatusThread = new Runnable() {
        @Override
        public void run() {
            if(isAdded()&&runnable_flag) {
                device_http.getComponentsStatus(componentsStatusSuccessHandler, componentsStatusErrorHandler);
                updateComponentsHandler.postDelayed(this, 10000);
            }
        }
    };
}