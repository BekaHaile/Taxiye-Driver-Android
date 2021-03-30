package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;


public class JugnooExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Thread.UncaughtExceptionHandler defaultUEH;
    private static Activity app;
    private static JugnooExceptionHandler jugnooExceptionHandler;

    private JugnooExceptionHandler(Activity app) {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        JugnooExceptionHandler.app = app;
    }

    public static JugnooExceptionHandler getInstance(Activity activity) {
        if (jugnooExceptionHandler == null)
            jugnooExceptionHandler = new JugnooExceptionHandler(activity);
        return jugnooExceptionHandler;
    }

    public void uncaughtException(Thread t, Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        StringBuilder report = new StringBuilder(e.toString() + "\n\n");
        report.append("--------- Stack trace ---------\n\n");
        for (StackTraceElement traceElement : arr) {
            report.append("    ").append(traceElement.toString()).append("\n");
        }
        report.append("-------------------------------\n\n");

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause

        report.append("--------- Cause ---------\n\n");
        Throwable cause = e.getCause();
        if (cause != null) {
            report.append(cause.toString()).append("\n\n");
            arr = cause.getStackTrace();
            for (StackTraceElement stackTraceElement : arr) {
                report.append("    ").append(stackTraceElement.toString()).append("\n");
            }
        }
        report.append("-------------------------------\n\n");
        if (app == null)
            return;
        HashMap<String, String> map = new HashMap<>();
        HomeUtil.putDefaultParams(map);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String data = Data.userData == null ? "" : " Customer ID:" + Data.userData.getUserId();
        String subject = "Crash Report" + data;
        String body = "Crash Logs: " + data + "\n" + Utils.prettyJson(map) + "\n" + "\n" + report + "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("text/plain");

        app.startActivity(Intent.createChooser(sendIntent, "Send Crash To:"));


        defaultUEH.uncaughtException(t, e);
    }
}

