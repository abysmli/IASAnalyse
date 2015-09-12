package net.icedeer.abysmli.iasanalyse.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Li, Yuan on 12.09.15.
 * All Right reserved!
 */
public class SessionManager {
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = this.sharedpreferences.edit();
    }

    public void setUser(JSONObject user) {
        try {
            editor.putInt("user_id", user.getInt("userid"));
            editor.putString("username", user.getString("username"));
            editor.putString("user_level", user.getString("level"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    public int getUserID() {
        return sharedpreferences.getInt("user_id", 0);
    }

    public void setUserID(int userID) {
        editor.putInt("user_id", userID);
        editor.apply();
    }

    public String getUserName() {
        return sharedpreferences.getString("username", "");
    }

    public void setUserName(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedpreferences.getString("email", "");
    }

    public void setUserEmail(String email) {
        editor.putString("email", email);
        editor.apply();
    }

    public String getUserLevel() {
        return sharedpreferences.getString("user_level", "");
    }

    public void setUserLevel(String userLevel) {
        editor.putString("user_level", userLevel);
        editor.apply();
    }

    public void remove() {
        editor.clear();
        editor.apply();
    }
}
