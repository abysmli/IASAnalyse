package net.icedeer.abysmli.iasanalyse;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;


public class ControllerPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        setContentView(R.layout.activity_controller_panel);

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

    private String getFragmentTag(int viewPagerId, int fragmentPosition)
    {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    public void clear_log(View view) {
        LogRecorder.cleanLog(this);
        RunningLogFragment running_log = (RunningLogFragment)getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.pager, 2));
        running_log.clear_log();
    }
}
