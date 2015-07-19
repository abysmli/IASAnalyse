package net.icedeer.abysmli.iasanalyse;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity implements AutoDetectionDeviceDialog.AutoDetectionDeviceDialogListener, ManualConnectDialog.ManualConnectDialogListener{

    static public String ip = "http://141.58.42.66";
    final Context context = this;
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
        DatabaseHandler db = new DatabaseHandler(this);

        if (db.getComponentsCount() == 0 ) {
            // Inserting Contacts
            Resources resources = getResources();
            String[] componentName = resources.getStringArray(R.array.component_name);
            String[] series = resources.getStringArray(R.array.series);
            String[] type = resources.getStringArray(R.array.type);
            String[] componentDesc = resources.getStringArray(R.array.component_desc);

            for (int i=0; i<14; i++) {
                db.addComponent(new ComponentDataStruct(componentName[i], series[i], type[i], componentDesc[i]));
            }
        }
    }

}
