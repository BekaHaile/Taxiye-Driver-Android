//package product.clicklabs.jugnoo.driver.tutorial;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.SystemClock;
//import android.util.Log;
//
//import com.conweigh.calibration.utils.AppConstants;
//import com.conweigh.calibration.utils.BluetoothLeConstants;
//
//
///**
// * Created by Parminder Singh on 18/04/16.
// */
//public final class ExtendingStopTimeAlarm extends BroadcastReceiver implements BluetoothLeConstants {
//
//    private static final String TAG = "Alarm Receiver";
//
//    public static void setAlarm(Context context) {
//        Log.i(TAG, "setAlarm: called at " + SystemClock.currentThreadTimeMillis());
//        Intent i = new Intent(context, ExtendingStopTimeAlarm.class);
//        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null);
//        if (!alarmUp) {
//            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
//            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DELAY_EXTEND_STOP_TIME, PERIOD_EXTEND_STOP_TIME, pi);
//
//        }
//
//    }
//
//    public static void cancelAlarm(Context context) {
//
//        try {
//            Log.d(TAG, "cancelAlarm() called with: " + "context = [" + context + "]");
//            Intent intent = new Intent(context, ExtendingStopTimeAlarm.class);
//            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            alarmManager.cancel(sender);
//            sender.cancel();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//
//        try {
//            Log.i(TAG, "onReceive: called of alarm");
//            if (BluetoothLeService.mGatt != null && BluetoothLeService.mHashMapCharacteristics.size() > 0) {
//                Log.i(TAG, "onReceive: successful i.e gatt is not null");
//                BluetoothLeService.mHashMapCharacteristics.get(UUID_DEVICE_POWER).setValue(BYTE_COMMAND_ON);
//                BluetoothLeService.mGatt.writeCharacteristic(BluetoothLeService.mHashMapCharacteristics.get(UUID_DEVICE_POWER));
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
//}
