package net.icedeer.abysmli.iasanalyse;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;
import net.icedeer.abysmli.iasanalyse.httpHandler.PMSHttpRequest;
import net.icedeer.abysmli.iasanalyse.view.ControllerFragmentsAdapter;
import net.icedeer.abysmli.iasanalyse.view.RunningLogFragment;


public class ControllerPanelActivity extends AppCompatActivity {

    private DeviceHttpRequest deviceHttpRequester;
    private PMSHttpRequest pmsHttpRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_controller_panel);

        deviceHttpRequester = new DeviceHttpRequest(this, AppSetting.DeviceAddress);
        pmsHttpRequester = new PMSHttpRequest(this);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ControllerFragmentsAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller_panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_login:
                Log.i("Actionbar", "login button clicked");
                return true;
            case R.id.action_about:
                Log.i("Actionbar", "about button clicked");
                return true;
            case R.id.action_help:
                Log.i("Actionbar", "help button clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getFragmentTag() {
        return "android:switcher:" + R.id.pager + ":" + 2;
    }

    public void clear_log(@SuppressWarnings("UnusedParameters") View view) {
        LogRecorder.cleanLog(this);
        RunningLogFragment running_log = (RunningLogFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        running_log.clear_log();
    }

    public DeviceHttpRequest getDeviceHttpRequester() {
        return deviceHttpRequester;
    }

    public PMSHttpRequest getPMSHttpRequester() {
        return pmsHttpRequester;
    }
}
