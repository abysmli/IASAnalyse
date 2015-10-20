package net.icedeer.abysmli.iasanalyse.controller;

import android.util.Log;

/**
 * Created by Li, Yuan on 15.10.15.
 * All Right reserved!
 */
public class ProblemDiagnose {
    private int componentID;
    private String errorInfo;
    private double oldValue;

    public ProblemDiagnose(int componentID) {
        this.componentID = componentID;
    }

    public boolean diagnose(double newValue) {
        boolean result = true;
        Log.i("Value Log: ", String.valueOf(newValue));
        switch (componentID) {
            case 1:
                if (newValue > 12) {
                    result = false;
                    errorInfo = "Fuellstand ist zu hoch";
                } else if (newValue < 0) {
                    result = false;
                    errorInfo = "Fuellstand ist zu niedrig";
                } else if (Math.abs((newValue-oldValue) / AppSetting.ComponentDetailsRefreshRate) > 10) {
                    result = false;
                    errorInfo = "Fuellstandrate ist zu hoch";
                } else {
                    result = true;
                    errorInfo = "";
                }
                break;
            case 2:
                if (newValue > 100) {
                    result = false;
                    errorInfo = "Durchfluss ist zu schnell";
                } else if (newValue < 0) {
                    result = false;
                    errorInfo = "Durchfluss ist zu langsam";
                } else {
                    result = true;
                    errorInfo = "";
                }
                break;
            case 3:
                if (newValue > 6000) {
                    result = false;
                    errorInfo = "Druck ist zu hoch";
                } else if (newValue < 0) {
                    result = false;
                    errorInfo = "Druck ist zu niedrig";
                } else {
                    result = true;
                    errorInfo = "";
                }
                break;
            case 4:
                if (newValue > 35) {
                    result = false;
                    errorInfo = "Temperatur ist zu hoch";
                } else if (newValue < 20) {
                    result = false;
                    errorInfo = "Temperatur ist zu niedrig";
                } else {
                    result = true;
                    errorInfo = "";
                }
                break;
            default:
                if (newValue > 90) {
                    result = false;
                    errorInfo = "Component Value ist zu hoch";
                } else if (newValue < 0) {
                    result = false;
                    errorInfo = "Component Value ist zu niedrig";
                } else {
                    result = true;
                    errorInfo = "";
                }
                break;
        }
        oldValue = newValue;
        return result;
    }

    public String getErrorInfo() {
        return errorInfo;
    }
}
