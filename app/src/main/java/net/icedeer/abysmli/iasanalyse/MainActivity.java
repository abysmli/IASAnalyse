package net.icedeer.abysmli.iasanalyse;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.icedeer.abysmli.iasanalyse.model.ComponentDataStruct;
import net.icedeer.abysmli.iasanalyse.model.DatabaseHandler;
import net.icedeer.abysmli.iasanalyse.view.AutoDetectionDeviceDialog;
import net.icedeer.abysmli.iasanalyse.view.ManualConnectDialog;

import java.util.List;


public class MainActivity extends Activity implements AutoDetectionDeviceDialog.AutoDetectionDeviceDialogListener, ManualConnectDialog.ManualConnectDialogListener{

    static public String ip = "http://141.58.62.4";
    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatabase();
    }

    @Override
    public void onItemClick(DialogFragment dialog, String ip) {
        startActivity(new Intent(this, ControllerPanelActivity.class));
    }

    @Override
    public void onManualConnectOKClick(DialogFragment dialog, String ip) {
        startActivity(new Intent(this, ControllerPanelActivity.class));
    }

    public void auto_connect_device(View v) {
        DialogFragment dialog = new AutoDetectionDeviceDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "auto_connect_dialog");
    }

    public void manual_connect_device(View v) {
        DialogFragment dialog = new ManualConnectDialog();
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "manual_connect_dialog");
    }

    public void initDatabase() {
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

    public void testDatabase () {
        Log.i("database: ", "database show");
        List<ComponentDataStruct> components = db.getAllComponents();
        for (ComponentDataStruct component: components) {
            Log.i("components: ", component.get_component_id() + " & " + component.get_component_name());
        }
    }

}
