package net.icedeer.abysmli.iasanalyse.controller;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Li, Yuan on 22.06.15.
 * All Right reserved!
 */
public class LogRecorder {

    public static void Log(String logs, Context context) {
        try {
            final String filename = "runninglog";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            logs = "<p>"+logs+"&nbsp&nbsp&nbsp<i>" + formattedDate + "</i></p>";

            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(logs.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StringBuffer readLog(Context context) {
        final String filename = "runninglog";
        StringBuffer storedString = new StringBuffer();
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;
            if ((strLine = reader.readLine()) != null) {
                storedString.append(strLine);
            }
            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return storedString;
    }

    public static void cleanLog(Context context) {
        final String filename = "runninglog";
        context.deleteFile(filename);
    }
}
