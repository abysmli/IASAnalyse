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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import net.icedeer.abysmli.iasanalyse.R;


public class HelpDialog extends DialogFragment {

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
        final View HelpView = inflater.inflate(R.layout.dialog_help, null);
        builder.setTitle(R.string.help_dialog_title)
                .setView(HelpView)
                .setPositiveButton(R.string.done_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
