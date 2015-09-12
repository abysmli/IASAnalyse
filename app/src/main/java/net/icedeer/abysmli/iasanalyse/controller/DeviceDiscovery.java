package net.icedeer.abysmli.iasanalyse.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;

import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.view.AutoDetectionDeviceDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Li, Yuan on 24.06.15.
 * All Right reserved!
 */
public class DeviceDiscovery extends AsyncTask<Void, String, Integer> {

    private final Context context;
    private final String[] mAddresses;
    private final AutoDetectionDeviceDialog dialog;

    public DeviceDiscovery(Context context, AutoDetectionDeviceDialog dialog, String[] mAddresses) {
        this.context = context;
        this.mAddresses = mAddresses;
        this.dialog = dialog;
    }

    protected Integer doInBackground(Void... voids) {
        DeviceHttpRequest deviceHttpRequest = new DeviceHttpRequest(context);
        int summer = 0;
        for (String mAddress : mAddresses) {
            String _add = "http://" + mAddress + ":" + AppSetting.DevicePort;
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            deviceHttpRequest.detectDevice(_add, future);
            try {
                if (future.get(AppSetting.AutoDetectionDeviceDelay, TimeUnit.MILLISECONDS).getString("status").equals("running")) {
                    summer++;
                    publishProgress(mAddress);
                }
            } catch (InterruptedException e) {
                Log.i("Interrupted", mAddress);
            } catch (ExecutionException e) {
                Log.i("Execution", mAddress);
            } catch (TimeoutException e) {
                Log.i("Timeout", mAddress);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isCancelled()) break;
        }
        return summer;
    }

    protected void onProgressUpdate(String... progress) {
        dialog.addDeviceAddress(progress[progress.length - 1]);
    }

    protected void onPostExecute(Integer result) {
        dialog.searchFinished();
        Log.i("Execute", "Find " + result + " Devices");
    }
}

