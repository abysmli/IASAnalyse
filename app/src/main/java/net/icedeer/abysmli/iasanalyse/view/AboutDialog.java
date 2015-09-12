package net.icedeer.abysmli.iasanalyse.view;

/**
 * Created by Li, Yuan on 12.09.15.
 * All Right reserved!
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.icedeer.abysmli.iasanalyse.R;


public class AboutDialog extends DialogFragment {

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View aboutView = inflater.inflate(R.layout.dialog_about, null);
        TextView versionText = (TextView) aboutView.findViewById(R.id.version_text);
        versionText.setText(getVersionInfo());
        builder.setTitle(R.string.about_dialog_title)
                .setView(aboutView)
                .setPositiveButton(R.string.done_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private String getVersionInfo() {
        String strVersion = "Version:";
        PackageInfo packageInfo;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            strVersion += packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            strVersion += "Unknown";
        }
        return strVersion;
    }
}
