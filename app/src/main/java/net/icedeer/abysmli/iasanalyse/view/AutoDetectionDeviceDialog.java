package net.icedeer.abysmli.iasanalyse.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import net.icedeer.abysmli.iasanalyse.R;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Li, Yuan on 18.06.15.
 * All Right reserved!
 */
public class AutoDetectionDeviceDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AutoDetectionDeviceDialogListener {
        void onItemClick(DialogFragment dialog, String ip);
    }

    // Use this instance of the interface to deliver action events
    AutoDetectionDeviceDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AutoDetectionDeviceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        //String ipAddress = Formatter.formatIpAddress(ip);
        //Log.i("AutoDialog", ipAddress);

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue; // Don't want to broadcast to the loopback interface

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();

                    InetAddress _ip = interfaceAddress.getAddress();
                    short prefix = interfaceAddress.getNetworkPrefixLength();

                    // Android seems smart enough to set to null broadcast to
                    //  the external mobile network. It makes sense since Android
                    //  silently drop UDP broadcasts involving external mobile network.
                    if (broadcast == null)
                        continue;

                    Log.i("AutoDialog ip", _ip.toString());
                    Log.i("AutoDialog prefix", String.valueOf(prefix));
                    Log.i("AutoDialog broadcast", broadcast.toString());

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //new DeviceDiscovery().start();

        builder.setTitle(R.string.auto_detect_dialog_title)
                .setItems(R.array.available_devices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onItemClick(AutoDetectionDeviceDialog.this, "192.168.1.2");
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}