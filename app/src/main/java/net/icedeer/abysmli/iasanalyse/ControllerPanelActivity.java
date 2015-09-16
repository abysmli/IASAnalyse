package net.icedeer.abysmli.iasanalyse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.controller.SessionManager;
import net.icedeer.abysmli.iasanalyse.view.AboutDialog;
import net.icedeer.abysmli.iasanalyse.view.ControllerFragmentsAdapter;
import net.icedeer.abysmli.iasanalyse.view.HelpDialog;
import net.icedeer.abysmli.iasanalyse.view.LoginDialog;
import net.icedeer.abysmli.iasanalyse.view.RunningLogFragment;

import org.json.JSONObject;


public class ControllerPanelActivity extends AppCompatActivity implements LoginDialog.LoginDialogListener {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_panel);

        sessionManager = new SessionManager(getBaseContext());
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem usernameItem = menu.findItem(R.id.action_username);
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        if (sessionManager.getUserName().isEmpty()) {
            usernameItem.setVisible(false);
            logoutItem.setVisible(false);
            loginItem.setVisible(true);
        } else {
            usernameItem.setTitle(sessionManager.getUserName() + " (" + sessionManager.getUserLevel() + ")");
            usernameItem.setVisible(true);
            logoutItem.setVisible(true);
            loginItem.setVisible(false);
        }
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
                DialogFragment loginDialog = new LoginDialog();
                loginDialog.setCancelable(false);
                loginDialog.show(getFragmentManager(), "Login");
                return true;
            case R.id.action_about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.setCancelable(true);
                aboutDialog.show(getFragmentManager(), "About");
                return true;
            case R.id.action_help:
                DialogFragment helpDialog = new HelpDialog();
                helpDialog.setCancelable(true);
                helpDialog.show(getFragmentManager(), "Help");
                return true;
            case R.id.action_logout:
                sessionManager.remove();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getFragmentTag() {
        return "android:switcher:" + R.id.pager + ":" + 2;
    }

    public void clear_log(View view) {
        LogRecorder.cleanLog(this);
        RunningLogFragment running_log = (RunningLogFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        running_log.clear_log();
    }

    @Override
    public void onLoginClick(Dialog dialog, JSONObject login) {
        dialog.dismiss();
        LogRecorder.Log(getString(R.string.login_success), getApplicationContext());
        sessionManager.setUser(login);
        invalidateOptionsMenu();
    }
}
