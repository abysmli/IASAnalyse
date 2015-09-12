package net.icedeer.abysmli.iasanalyse.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.DeviceDiscovery;

import org.apache.commons.net.util.SubnetUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Li, Yuan on 18.06.15.
 * All Right reserved!
 */
public class AutoDetectionDeviceDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AutoDetectionDeviceDialogListener {
        void onItemClick(String ip);
    }

    // Use this instance of the interface to deliver action events
    private AutoDetectionDeviceDialogListener mListener;
    private DeviceDiscovery discoveryDevice;
    private ProgressBar progressBar;
    private TextView notFoundLabel;
    private List<String> deviceAddresses;

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
        DeviceDetection(getSubnetAddresses());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout itemsView = new LinearLayout(getActivity());
        itemsView.setOrientation(LinearLayout.VERTICAL);
        itemsView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        deviceAddresses = new ArrayList<>();

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyle);
        progressBar.setPadding(0, 50, 0, 0);

        notFoundLabel = new TextView(getActivity());
        notFoundLabel.setText("Can not find any available Devices!");
        notFoundLabel.setPadding(50, 50, 0, 0);
        notFoundLabel.setVisibility(View.GONE);

        ListView deviceLists = new ListView(getActivity());
        deviceLists.setPadding(0, 20, 0, 0);
        deviceLists.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, deviceAddresses);
        deviceLists.setAdapter(adapter);
        deviceLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onItemClick(adapterView.getItemAtPosition(position).toString());
            }
        });

        itemsView.addView(progressBar, 0);
        itemsView.addView(notFoundLabel, 1);
        itemsView.addView(deviceLists, 2);

        builder.setTitle(R.string.auto_detect_dialog_title)
                .setView(itemsView)
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        discoveryDevice.cancel(true);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void DeviceDetection(String[] mAddresses) {
        discoveryDevice = new DeviceDiscovery(getActivity(), this, mAddresses);
        discoveryDevice.execute();
    }

    private String[] getSubnetAddresses() {
        String[] mAddresses = new String[0];
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
                    String IP = _ip.toString().substring(1);
                    String subnet = IP + "/" + prefix;
                    SubnetUtils utils = new SubnetUtils(subnet);
                    utils.setInclusiveHostCount(true);
                    mAddresses = utils.getInfo().getAllAddresses();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mAddresses;
    }

    public void addDeviceAddress(String mAddress) {
        deviceAddresses.add(mAddress);
    }

    public void searchFinished() {
        if (deviceAddresses.size() == 0) {
            notFoundLabel.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
}