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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.HashGeneratorUtils;
import net.icedeer.abysmli.iasanalyse.httpHandler.PMSHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface LoginDialogListener {
        void onLoginClick(Dialog dialog, JSONObject login);
    }

    // Use this instance of the interface to deliver action events
    private LoginDialogListener mListener;
    private EditText usernameEditText;
    private EditText passwordEditText;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog dialog = (AlertDialog) getDialog();
        final PMSHttpRequest pms_http = new PMSHttpRequest(getActivity());
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> certificate = new HashMap<>();
                    certificate.put("username", usernameEditText.getText().toString());
                    certificate.put("password", HashGeneratorUtils.generateSHA256(passwordEditText.getText().toString()));
                    pms_http.login(certificate, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String _response) {
                            try {
                                JSONObject response = new JSONObject(_response);
                                if (response.getString("status").equals("success")) {
                                    Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                                    mListener.onLoginClick(dialog, response);
                                } else {
                                    Toast.makeText(getActivity(), R.string.verify_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), R.string.login_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.dialog_login, null);
        usernameEditText = (EditText) view.findViewById(R.id._username);
        passwordEditText = (EditText) view.findViewById(R.id._password);
        builder.setTitle(R.string.login_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
