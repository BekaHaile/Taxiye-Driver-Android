package product.clicklabs.jugnoo.driver;

import java.util.Calendar;

import product.clicklabs.jugnoo.driver.utils.Log;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class DriverServiceOperations {

	private static int DRIVER_SERVICE_RESTART_PI_REQUEST_CODE = 123;
	private static final String START_SERVICE = "product.clicklabs.jugnoo.driver.START_SERVICE";
	
	
	public PendingIntent createPendingIntent(Context context){
		Intent receiverIntent = new Intent(context, DriverServiceRestartAlarmReceiver.class);
		receiverIntent.setAction(START_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DRIVER_SERVICE_RESTART_PI_REQUEST_CODE, 
				receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
	
	public void cancelAlarm(Context context){
		PendingIntent pendingIntent = createPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}
	
	public void setupAlarm(Context context, long milliseconds){
		cancelAlarm(context);
		PendingIntent pendingIntent = createPendingIntent(context);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
	}
	
	
	public Calendar getRestartSetCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR, 12);
		return calendar;
	}
	
	
	
	
	
	public void startDriverService(Context context){
		try{
			Database2.getInstance(context).updateDriverServiceRun(Database2.YES);
			Database2.getInstance(context).updateDriverServiceTimeToRestart(0);
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			context.startService(new Intent(context, DriverLocationUpdateService.class));
			
			Log.e("startDriverService ====", "="+Database2.getInstance(context).getDriverServiceRun());
			
			cancelAlarm(context);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}
	}
	
	
	
	
	
	
	
	
	public void stopAndScheduleDriverService(Context context){
		try{
			Database2.getInstance(context).updateDriverServiceRun(Database2.NO);
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			Calendar calendar = getRestartSetCalendar();
			setupAlarm(context, calendar.getTimeInMillis());
			Database2.getInstance(context).updateDriverServiceTimeToRestart(calendar.getTimeInMillis());
			Log.e("stopAndScheduleDriverService", "=current = "+ System.currentTimeMillis() + " adv = " + getRestartSetCalendar().getTimeInMillis());
			Log.e("startDriverService ====", "="+Database2.getInstance(context).getDriverServiceRun());
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}
	}
	
	
	
	
	
	
	
	
	
	public void rescheduleDriverService(Context context){
		try{
			long timeToRestartService = Database2.getInstance(context).getDriverServiceTimeToRestart();
			
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			
			setupAlarm(context, timeToRestartService);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}
	}
	
	
	
	
	
	public void stopService(Context context){
		try{
			cancelAlarm(context);
			context.stopService(new Intent(context, DriverLocationUpdateService.class));
			Database2.getInstance(context).updateDriverServiceRun(Database2.NEVER);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}
	}
	
	
	
	public void checkStartService(Context context){
		try{
			final String serviceRestartOnReboot = Database2.getInstance(context).getDriverServiceRun();
			if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
				context.stopService(new Intent(context, DriverLocationUpdateService.class));
				context.startService(new Intent(context, DriverLocationUpdateService.class));
				cancelAlarm(context);
	    	}
			Log.e("checkStartService", "=serviceRestartOnReboot = "+serviceRestartOnReboot);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}
	}
	
	
}
