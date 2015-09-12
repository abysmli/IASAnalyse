package net.icedeer.abysmli.iasanalyse;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;
import net.icedeer.abysmli.iasanalyse.model.DatabaseHandler;
import net.icedeer.abysmli.iasanalyse.view.AutoDetectionDeviceDialog;
import net.icedeer.abysmli.iasanalyse.view.ManualConnectDialog;


public class MainActivity extends Activity implements AutoDetectionDeviceDialog.AutoDetectionDeviceDialogListener, ManualConnectDialog.ManualConnectDialogListener{
    private final DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatabase();
    }

    @Override
    public void onItemClick(String ip) {
        AppSetting.DeviceAddress = "http://" + ip + ":" + AppSetting.DevicePort;
        startActivity(new Intent(this, ControllerPanelActivity.class));
    }

    @Override
    public void onManualConnectOKClick(String ip) {
        if (!ip.isEmpty()) {
            AppSetting.DeviceAddress = "http://" + ip + ":" + AppSetting.DevicePort;
        }
        startActivity(new Intent(this, ControllerPanelActivity.class));
    }

    public void AutoConnectDevice(@SuppressWarnings("UnusedParameters") View v) {
        DialogFragment dialog = new AutoDetectionDeviceDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "AutoConnectDevice");
    }

    public void ManualConnectDevice(@SuppressWarnings("UnusedParameters") View v) {
        DialogFragment dialog = new ManualConnectDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "ManualConnectDevice");
    }

    private void initDatabase() {
        if (db.getComponentsCount() == 0 ) {
            // Inserting Contacts
            Resources resources = getResources();
            String[] componentName = resources.getStringArray(R.array.component_name);
            String[] series = resources.getStringArray(R.array.series);
            String[] type = resources.getStringArray(R.array.type);
            String[] componentDesc = resources.getStringArray(R.array.component_desc);

            for (int i=0; i<24; i++) {
                db.addComponent(new ComponentDataStruct(componentName[i], series[i], type[i], componentDesc[i]));
            }
        }
    }
}
