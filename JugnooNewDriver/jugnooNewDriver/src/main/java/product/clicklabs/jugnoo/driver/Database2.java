package product.clicklabs.jugnoo.driver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.util.Pair;
import product.clicklabs.jugnoo.driver.datastructure.AllNotificationData;
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.datastructure.LogUSL;
import product.clicklabs.jugnoo.driver.datastructure.NotificationData;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.RideData;
import product.clicklabs.jugnoo.driver.datastructure.RingData;
import product.clicklabs.jugnoo.driver.datastructure.UsageData;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Handles database related work
 */
public class Database2 {                                                                    // class for handling database related activities

	private static Database2 dbInstance;

	private static final String DATABASE_NAME = BuildConfig.APP_DB_ID +"_database2";                        // declaring database variables

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	private SQLiteDatabase database;

	public static final String YES = "yes", NO = "no", NEVER = "never";

	private static final String TABLE_DRIVER_SERVICE_FAST = "table_driver_service_fast";
	private static final String FAST = "fast";

	private static final String TABLE_TOTAL_DISTANCE = "table_total_distance";
	private static final String TOTAL_DISTANCE = "total_distance";


	private static final String TABLE_DRIVER_SCREEN_MODE = "table_driver_screen_mode";
	private static final String DRIVER_SCREEN_MODE = "driver_screen_mode";


	private static final String TABLE_DRIVER_CURRENT_LOCATION = "table_driver_current_location";
	private static final String DRIVER_CURRENT_LATITUDE = "driver_current_latitude";
	private static final String DRIVER_CURRENT_LONGITUDE = "driver_current_longitude";
	private static final String DRIVER_CURRENT_LOCATION_ACCURACY = "driver_current_location_accuracy";
	private static final String DRIVER_CURRENT_LOCATION_TIME = "driver_current_location_time";
	private static final String DRIVER_CURRENT_LOCATION_BEARING = "driver_current_location_bearing";


	private static final String TABLE_DRIVER_LAST_LOCATION_TIME = "table_driver_last_location_time";
	private static final String LAST_LOCATION_TIME = "last_location_time";

	private static final String TABLE_DRIVER_SERVICE = "table_driver_service";
	private static final String DRIVER_SERVICE_RUN = "driver_service_run";

	private static final String TABLE_DRIVER_SERVICE_TIME_TO_RESTART = "table_driver_service_time_to_restart";
	private static final String TIME_TO_RESTART = "time_to_restart";

	private static final String TABLE_DRIVER_MANUAL_PATCH = "table_driver_manual_patch";
	private static final String DRIVER_MANUAL_PATCH_PUSH_RECEIVED = "driver_manual_patch_push_received";

	private static final String TABLE_DRIVER_GCM_INTENT = "table_driver_gcm_intent";
	private static final String DRIVER_GCM_INTENT = "driver_gcm_intent";


	private static final String TABLE_PENDING_API_CALLS = "table_pending_api_calls";
	private static final String API_ID = "api_id";
	private static final String API_URL = "api_url";
	private static final String API_REQUEST_PARAMS = "api_request_params";


	private static final String TABLE_RIDE_DATA = "table_ride_data_4";
	private static final String RIDE_DATA_I = "i";
	private static final String RIDE_DATA_LAT = "lat";
	private static final String RIDE_DATA_LNG = "lng";
	private static final String RIDE_DATA_T = "t";
	private static final String RIDE_DATA_ENGAGEMENT_ID = "engagementId";
	private static final String RIDE_DATA_ACC_DISTANCE = "accDistance";

	private static final String TABLE_RIDE_LOGS = "table_ride_logs";
	private static final String RIDE_LOGS_I = "i";
	private static final String RIDE_LOGS_ENG_ID = "engagementId";
	private static final String RIDE_LOGS_LOG = "log";


	private static final String TABLE_RING_DATA = "table_ring_data";
	private static final String RING_DATA_ENGAGEMENT = "ring_data_engagement";
	private static final String RING_DATA_TIME = "ring_data_time";


	private static final String TABLE_PENALITY_COUNT = "table_penality_count";
	private static final String PENALITY_ID = "penality_id";
	private static final String PENALITY_TIME = "penality_time";
	private static final String PENALITY_FACTOR = "penality_factor";


	public static final String ON = "on", OFF = "off";

	private static final String TABLE_METERING_STATE = "table_metering_state";
	private static final String METERING_STATE = "metering_state";


	private static final String TABLE_CUSTOM_AUDIO = "table_custom_audio";
	private static final String CUSTOM_AUDIO_URL = "custom_audio_url";
	private static final String CUSTOM_AUDIO_ID = "custom_audio_id";


	// ***************** // table_current_path table columns  // **********************//
	private static final String TABLE_CURRENT_PATH = "table_current_path";
	private static final String ID = "id";
	private static final String PARENT_ID = "parent_id";
	private static final String SLAT = "slat";
	private static final String SLNG = "slng";
	private static final String DLAT = "dlat";
	private static final String DLNG = "dlng";
	private static final String SECTION_INCOMPLETE = "section_incomplete";
	private static final String GOOGLE_PATH = "google_path";
	private static final String ACKNOWLEDGED = "acknowledged";


	// Notification center table name and row names...
	private static final String TABLE_NOTIFICATION_CENTER = "table_notification_center";
	private static final String NOTIFICATION_ID = "notification_id";
	private static final String TIME_PUSH_ARRIVED = "time_push_arrived";
	private static final String MESSAGE = "message";
	private static final String TIME_TO_DISPLAY = "time_to_display";
	private static final String TIME_TILL_DISPLAY = "time_till_display";
	private static final String NOTIFICATION_IMAGE = "notification_image";


	private static final String TABLE_NOTIFICATION_DB = "table_notification_db";
	private static final String DB_NOTIFICATION_ID = "db_notification_id";
	private static final String DB_NOTIFICATION_TITLE = "db_notification_title";
	private static final String DB_NOTIFICATION_MESSAGE = "db_notification_message";
	private static final String DB_NOTIFICATION_PACKAGE = "db_notification_package";


	private static final String TABLE_GPS_STATE = "table_gps_state";
	private static final String GPS_STATE = "gps_state";




	private static final String TABLE_DATA_USAGE = "table_data_usage";
	private static final String LOG_TIME = "log_time";
	private static final String BYTES_RECEIVED = "bytes_received";
	private static final String BYTES_SENT = "bytes_sent";

	private static final String TABLE_POOL_DISCOUNT_FLAG = "table_pool_discount_flag";
	private static final String POOL_RIDE_ENGAGEMENT_ID = "pool_ride_engagement_id";
	private static final String POOL_RIDE_DISCOUNT_FLAG = "pool_ride_discount_flag";

	private static final String TABLE_USL_LOG = "table_usl_log";
	private static final String LOG_TIMESTAMP = "log_timestamp";
	private static final String LOG_EVENT = "log_event";

	private static final String TABLE_WAIT_TIME = "table_wait_time";
	private static final String WAIT_TIME = "wait_time";

	private static final String TABLE_KEY_VALUE = "table_key_value";
	private static final String KEY = "skey";
	private static final String VALUE = "value";


	/**
	 * Creates and opens database for the application use
	 *
	 * @author shankar
	 */
	private static class DbHelper extends SQLiteOpenHelper {

		DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			Database2.createAllTables(database);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}

	}


	/****************************************** CREATING ALL THE TABLES *****************************************************/
	private static void createAllTables(SQLiteDatabase database) {

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE_FAST + " ("
				+ FAST + " TEXT" + ");");
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_TOTAL_DISTANCE + " ("
				+ TOTAL_DISTANCE + " TEXT" + ");");


		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SCREEN_MODE + " ("
				+ DRIVER_SCREEN_MODE + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_CURRENT_LOCATION + " ("
				+ DRIVER_CURRENT_LATITUDE + " TEXT, "
				+ DRIVER_CURRENT_LONGITUDE + " TEXT, "
				+ DRIVER_CURRENT_LOCATION_ACCURACY + " TEXT, "
				+ DRIVER_CURRENT_LOCATION_TIME + " TEXT, "
				+ DRIVER_CURRENT_LOCATION_BEARING + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_LAST_LOCATION_TIME + " ("
				+ LAST_LOCATION_TIME + " TEXT"
				+ ");");


		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE + " ("
				+ DRIVER_SERVICE_RUN + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE_TIME_TO_RESTART + " ("
				+ TIME_TO_RESTART + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_MANUAL_PATCH + " ("
				+ DRIVER_MANUAL_PATCH_PUSH_RECEIVED + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_GCM_INTENT + " ("
				+ DRIVER_GCM_INTENT + " INTEGER" + ");");


		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PENDING_API_CALLS + " ("
				+ API_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ API_URL + " TEXT, "
				+ API_REQUEST_PARAMS + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_RING_DATA + " ("
				+ RING_DATA_ENGAGEMENT + " INTEGER, "
				+ RING_DATA_TIME + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_RIDE_DATA + " ("
				+ RIDE_DATA_I + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RIDE_DATA_LAT + " TEXT, "
				+ RIDE_DATA_LNG + " TEXT, "
				+ RIDE_DATA_T + " TEXT, "
				+ RIDE_DATA_ENGAGEMENT_ID + " INTEGER, "
				+ RIDE_DATA_ACC_DISTANCE + " REAL "
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_RIDE_LOGS + " ("
				+ RIDE_LOGS_I + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ RIDE_LOGS_LOG + " TEXT, "
				+ RIDE_LOGS_ENG_ID + " INTEGER "
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PENALITY_COUNT + " ("
				+ PENALITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PENALITY_TIME + " TEXT, "
				+ PENALITY_FACTOR + " REAL "
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_METERING_STATE + " ("
				+ METERING_STATE + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOM_AUDIO + " ("
				+ CUSTOM_AUDIO_URL + " TEXT, "
				+ CUSTOM_AUDIO_ID + " TEXT" + ");");


		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT_PATH + " ("
				+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ PARENT_ID + " INTEGER, "
				+ SLAT + " REAL, "
				+ SLNG + " REAL, "
				+ DLAT + " REAL, "
				+ DLNG + " REAL, "
				+ SECTION_INCOMPLETE + " INTEGER, "
				+ GOOGLE_PATH + " INTEGER, "
				+ ACKNOWLEDGED + " INTEGER"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_CENTER + " ("
				+ NOTIFICATION_ID + " INTEGER, "
				+ TIME_PUSH_ARRIVED + " TEXT, "
				+ MESSAGE + " TEXT, "
				+ TIME_TO_DISPLAY + " TEXT, "
				+ TIME_TILL_DISPLAY + " TEXT, "
				+ NOTIFICATION_IMAGE + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_DB + " ("
				+ DB_NOTIFICATION_ID + " INTEGER, "
				+ DB_NOTIFICATION_PACKAGE + " TEXT, "
				+ DB_NOTIFICATION_TITLE + " TEXT, "
				+ DB_NOTIFICATION_MESSAGE + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_GPS_STATE + " ("
				+ GPS_STATE + " INTEGER" + ");");


		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DATA_USAGE + " ("
				+ LOG_TIME + " TEXT, "
				+ BYTES_RECEIVED + " TEXT, "
				+ BYTES_SENT + " TEXT"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_POOL_DISCOUNT_FLAG + " ("
				+ POOL_RIDE_ENGAGEMENT_ID + " INTEGER, "
				+ POOL_RIDE_DISCOUNT_FLAG + " INTEGER"
				+ ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_USL_LOG + " ("
				+ LOG_TIMESTAMP + " TEXT, "
				+ LOG_EVENT + " TEXT"
				+ ");");



		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_WAIT_TIME + " ("
				+ WAIT_TIME + " TEXT" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_KEY_VALUE + " ("
				+ KEY + " TEXT, "
				+ VALUE + " TEXT"
				+ ");");

	}

	public static Database2 getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new Database2(context);
		} else if (!dbInstance.database.isOpen()) {
			dbInstance = null;
			dbInstance = new Database2(context);
		}
		return dbInstance;
	}


	private Database2(Context context) {
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
		createAllTables(database);
	}

	public void close() {
		database.close();
		dbHelper.close();
		System.gc();
	}


	private void dropAndCreateNotificationTable(SQLiteDatabase database, Context context) {
		if (Prefs.with(context).getInt(Constants.FIRST_TIME_DB, 1) == 1) {
			ArrayList<NotificationData> notifications = getAllNotification();
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_CENTER);
			database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION_CENTER + " ("
					+ NOTIFICATION_ID + " INTEGER, "
					+ TIME_PUSH_ARRIVED + " TEXT, "
					+ MESSAGE + " TEXT, "
					+ TIME_TO_DISPLAY + " TEXT, "
					+ TIME_TILL_DISPLAY + " TEXT, "
					+ NOTIFICATION_IMAGE + " TEXT"
					+ ");");

			for (NotificationData data : notifications) {
				insertNotification(context, data.getNotificationId(),
						data.getTimePushArrived(),
						data.getMessage(),
						data.getTimeToDisplay(),
						data.getTimeTillDisplay(),
						data.getNotificationImage());
			}
			Prefs.with(context).save(Constants.FIRST_TIME_DB, 0);
		}
	}


	public double getTotalDistance() {
		double totaldistance = 0;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.TOTAL_DISTANCE};
			cursor = database.query(Database2.TABLE_TOTAL_DISTANCE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				totaldistance = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Database2.TOTAL_DISTANCE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return totaldistance;
	}


	void updateTotalDistance(double totalDistance) {
		try {
			deleteTotalDistance();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.TOTAL_DISTANCE, totalDistance);
			database.insert(Database2.TABLE_TOTAL_DISTANCE, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteTotalDistance() {
		try {
			database.delete(Database2.TABLE_TOTAL_DISTANCE, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	String getDriverServiceFast() {
		String[] columns = new String[]{Database2.FAST};
		Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_FAST, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.FAST));
			cursor.close();
			return choice;
		} else {
			cursor.close();
			return NO;
		}
	}

	public void updateDriverServiceFast(String choice) {
		deleteDriverServiceFast();
		insertDriverServiceFast(choice);
	}

	private void insertDriverServiceFast(String choice) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.FAST, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_FAST, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteDriverServiceFast() {
		try {
			database.delete(Database2.TABLE_DRIVER_SERVICE_FAST, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	public Location getDriverCurrentLocation(Context context) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		try {
			String[] columns = new String[]{Database2.DRIVER_CURRENT_LATITUDE, Database2.DRIVER_CURRENT_LONGITUDE,
					Database2.DRIVER_CURRENT_LOCATION_ACCURACY, Database2.DRIVER_CURRENT_LOCATION_TIME, Database2.DRIVER_CURRENT_LOCATION_BEARING};
			Cursor cursor = database.query(Database2.TABLE_DRIVER_CURRENT_LOCATION, columns, null, null, null, null, null);

			int in0 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LATITUDE);
			int in1 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LONGITUDE);
			int in2 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_ACCURACY);
			int in3 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_TIME);
			int in4 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_BEARING);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				location.setLatitude(Double.parseDouble(cursor.getString(in0)));
				location.setLongitude(Double.parseDouble(cursor.getString(in1)));
				location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
				location.setTime(Long.parseLong(cursor.getString(in3)));
				location.setBearing(Float.parseFloat(cursor.getString(in4)));

			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dbInstance = null;
				Database2.getInstance(context);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			alterTableDriverCurrentLocation();

			String[] columns = new String[]{Database2.DRIVER_CURRENT_LATITUDE, Database2.DRIVER_CURRENT_LONGITUDE,
					Database2.DRIVER_CURRENT_LOCATION_ACCURACY, Database2.DRIVER_CURRENT_LOCATION_TIME, Database2.DRIVER_CURRENT_LOCATION_BEARING};
			Cursor cursor = database.query(Database2.TABLE_DRIVER_CURRENT_LOCATION, columns, null, null, null, null, null);

			int in0 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LATITUDE);
			int in1 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LONGITUDE);
			int in2 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_ACCURACY);
			int in3 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_TIME);
			int in4 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_BEARING);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				location.setLatitude(Double.parseDouble(cursor.getString(in0)));
				location.setLongitude(Double.parseDouble(cursor.getString(in1)));
				location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
				location.setTime(Long.parseLong(cursor.getString(in3)));
				location.setBearing(Float.parseFloat(cursor.getString(in4)));
			}
			cursor.close();
		}
		return location;
	}


	public void updateDriverCurrentLocation(Context context, Location location) {
		try {
			deleteDriverCurrentLocation();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_CURRENT_LATITUDE, "" + location.getLatitude());
			contentValues.put(Database2.DRIVER_CURRENT_LONGITUDE, "" + location.getLongitude());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_ACCURACY, "" + location.getAccuracy());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_BEARING, "" + location.getBearing());
			database.insert(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);

		} catch (Exception e) {
			try {
				e.printStackTrace();
				Log.e("e", "=" + e);
				try {
					dbInstance = null;
					Database2.getInstance(context);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				alterTableDriverCurrentLocation();
				deleteDriverCurrentLocation();
				ContentValues contentValues = new ContentValues();
				contentValues.put(Database2.DRIVER_CURRENT_LATITUDE, "" + location.getLatitude());
				contentValues.put(Database2.DRIVER_CURRENT_LONGITUDE, "" + location.getLongitude());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_ACCURACY, "" + location.getAccuracy());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_BEARING, "" + location.getBearing());
				database.insert(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void alterTableDriverCurrentLocation() {
		try {
			database.execSQL("DROP TABLE " + TABLE_DRIVER_CURRENT_LOCATION);
			database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_CURRENT_LOCATION + " ("
					+ DRIVER_CURRENT_LATITUDE + " TEXT, "
					+ DRIVER_CURRENT_LONGITUDE + " TEXT, "
					+ DRIVER_CURRENT_LOCATION_ACCURACY + " TEXT, "
					+ DRIVER_CURRENT_LOCATION_TIME + " TEXT, "
					+ DRIVER_CURRENT_LOCATION_BEARING + " TEXT"
					+ ");");
			Log.e("drop query", "done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public int deleteDriverCurrentLocation() {
		try {
			return database.delete(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	long getDriverLastLocationTime() {
		long lastTimeInMillis = 0;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.LAST_LOCATION_TIME};
			cursor = database.query(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, columns, null, null, null, null, null);

			int in0 = cursor.getColumnIndex(Database2.LAST_LOCATION_TIME);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				lastTimeInMillis = Long.parseLong(cursor.getString(in0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return lastTimeInMillis;
	}


	void updateDriverLastLocationTime() {
		try {
			long timeInMillis = System.currentTimeMillis();
			deleteDriverLastLocationTime();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.LAST_LOCATION_TIME, "" + timeInMillis);
			database.insert(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteDriverLastLocationTime() {
		try {
			database.delete(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	String getDriverServiceRun() {
		String choice = YES;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.DRIVER_SERVICE_RUN};
			cursor = database.query(Database2.TABLE_DRIVER_SERVICE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				choice = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_SERVICE_RUN));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return choice;
	}

	public void updateDriverServiceRun(String choice) {
		try {
			deleteDriverServiceRun();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_SERVICE_RUN, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteDriverServiceRun() {
		try {
			database.delete(Database2.TABLE_DRIVER_SERVICE, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getDriverManualPatchPushReceived() {
		Cursor cursor = null;
		String choice = NO;
		try {
			String[] columns = new String[]{Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED};
			cursor = database.query(Database2.TABLE_DRIVER_MANUAL_PATCH, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				choice = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return choice;
	}

	public void updateDriverManualPatchPushReceived(String choice) {
		try {
			deleteDriverManualPatchPushReceived();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED, choice);
			database.insert(Database2.TABLE_DRIVER_MANUAL_PATCH, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteDriverManualPatchPushReceived() {
		try {
			database.delete(Database2.TABLE_DRIVER_MANUAL_PATCH, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	int getDriverGcmIntent() {
		Cursor cursor = null;
		int choice = 1;
		try {
			String[] columns = new String[]{Database2.DRIVER_GCM_INTENT};
			cursor = database.query(Database2.TABLE_DRIVER_GCM_INTENT, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				choice = cursor.getInt(cursor.getColumnIndex(Database2.DRIVER_GCM_INTENT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return choice;
	}

	void updateDriverGcmIntent(int choice) {
		try {
			deleteDriverGcmIntent();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_GCM_INTENT, choice);
			database.insert(Database2.TABLE_DRIVER_GCM_INTENT, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteDriverGcmIntent() {
		try {
			database.delete(Database2.TABLE_DRIVER_GCM_INTENT, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<PendingAPICall> getAllPendingAPICalls() {
		ArrayList<PendingAPICall> pendingAPICalls = new ArrayList<>();
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.API_ID, Database2.API_URL, Database2.API_REQUEST_PARAMS};
			cursor = database.query(Database2.TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				int in0 = cursor.getColumnIndex(Database2.API_ID);
				int in1 = cursor.getColumnIndex(Database2.API_URL);
				int in2 = cursor.getColumnIndex(Database2.API_REQUEST_PARAMS);

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					try {
						pendingAPICalls.add(new PendingAPICall(cursor.getInt(in0), cursor.getString(in1), Utils.convertQueryToNameValuePairArr(cursor.getString(in2))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return pendingAPICalls;
	}

	public int getAllPendingAPICallsCount() {
		int count = 0;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.API_ID};
			cursor = database.query(Database2.TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return count;
	}


	public void insertPendingAPICall(Context context, String url, HashMap<String, String> requestParams) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.API_URL, url);
			contentValues.put(Database2.API_REQUEST_PARAMS, requestParams.toString());
			database.insert(Database2.TABLE_PENDING_API_CALLS, null, contentValues);
			checkStartPendingApisService(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deletePendingAPICall(int apiId) {
		try {
			database.delete(Database2.TABLE_PENDING_API_CALLS, Database2.API_ID + "=" + apiId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void checkStartPendingApisService(Context context) {
		if (!Utils.isServiceRunning(context, PushPendingCallsService.class)) {
			context.startService(new Intent(context, PushPendingCallsService.class));
		}
	}


	public String getRideData(int engagementId) {
		StringBuilder rideDataStr = new StringBuilder();
		String template = "i,lat,long,t,accDist";
		String newLine = "\n";
		boolean hasValues = false;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.RIDE_DATA_I, Database2.RIDE_DATA_LAT, Database2.RIDE_DATA_LNG, Database2.RIDE_DATA_T, Database2.RIDE_DATA_ACC_DISTANCE};
			cursor = database.query(Database2.TABLE_RIDE_DATA, columns,
					RIDE_DATA_ENGAGEMENT_ID+"="+engagementId, null, null, null, null);

			int i0 = cursor.getColumnIndex(Database2.RIDE_DATA_I);
			int i1 = cursor.getColumnIndex(Database2.RIDE_DATA_LAT);
			int i2 = cursor.getColumnIndex(Database2.RIDE_DATA_LNG);
			int i3 = cursor.getColumnIndex(Database2.RIDE_DATA_T);
			int i4 = cursor.getColumnIndex(Database2.RIDE_DATA_ACC_DISTANCE);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				try {
					RideData rideData = new RideData(cursor.getInt(i0),
							Double.parseDouble(cursor.getString(i1)),
							Double.parseDouble(cursor.getString(i2)),
							cursor.getLong(i3),
							cursor.getDouble(i4));

					rideDataStr.append(rideData.toString()).append(newLine);
					hasValues = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (hasValues) {
				rideDataStr.insert(0, template + newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}

		return rideDataStr.toString();
	}

	public void insertRideData(Context context, String lat, String lng, String t, int engagementId) {
		try {
			double accDistance = 0;
			try {
				double latitude = Double.parseDouble(lat);
				double longitude = Double.parseDouble(lng);
				if(Utils.compareDouble(latitude, 0) == 0 && Utils.compareDouble(longitude, 0) == 0) {
					return;
				}
				if(Utils.compareDouble(latitude, 0) != 0
						&& Utils.compareDouble(longitude, 0) != 0) {
					RideData rideDataLast = getLastRideData(engagementId, 1);
					if (rideDataLast != null) {
						if (Utils.compareDouble(rideDataLast.lat, 0) != 0
								&& Utils.compareDouble(rideDataLast.lng, 0) != 0) {
							double currentDistance = MapUtils.distance(new LatLng(rideDataLast.lat, rideDataLast.lng),
									new LatLng(latitude, longitude));
							double timeDiff = (Double.parseDouble(t) - (double)rideDataLast.t)/1000d;
							double speed = currentDistance/timeDiff;
							if(speed < (double)(Prefs.with(context).getFloat(Constants.KEY_MAX_SPEED_THRESHOLD,
									(float) GpsDistanceCalculator.MAX_SPEED_THRESHOLD))){
								accDistance = rideDataLast.accDistance + currentDistance;
							} else{
								return;
							}
						} else if(Utils.compareDouble(rideDataLast.lat, 0) == 0
								&& Utils.compareDouble(rideDataLast.lng, 0) == 0){
							rideDataLast = getLastRideData(engagementId, 2);
							if(rideDataLast != null) {
								if (Utils.compareDouble(rideDataLast.lat, 0) != 0
										&& Utils.compareDouble(rideDataLast.lng, 0) != 0) {
									accDistance = MapUtils.distance(new LatLng(rideDataLast.lat, rideDataLast.lng),
											new LatLng(latitude, longitude));
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.RIDE_DATA_LAT, lat);
			contentValues.put(Database2.RIDE_DATA_LNG, lng);
			contentValues.put(Database2.RIDE_DATA_T, Long.parseLong(t));
			contentValues.put(Database2.RIDE_DATA_ENGAGEMENT_ID, engagementId);
			contentValues.put(Database2.RIDE_DATA_ACC_DISTANCE, accDistance);
			database.insert(Database2.TABLE_RIDE_DATA, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertRideDataWOChecks(String lat, String lng, String t, int engagementId, double accDistance) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(Database2.RIDE_DATA_LAT, lat);
		contentValues.put(Database2.RIDE_DATA_LNG, lng);
		contentValues.put(Database2.RIDE_DATA_T, Long.parseLong(t));
		contentValues.put(Database2.RIDE_DATA_ENGAGEMENT_ID, engagementId);
		contentValues.put(Database2.RIDE_DATA_ACC_DISTANCE, accDistance);
		database.insert(Database2.TABLE_RIDE_DATA, null, contentValues);
	}

	public void deleteRideData() {
		try {
			database.delete(Database2.TABLE_RIDE_DATA, null, null);
			database.execSQL("DROP TABLE " + Database2.TABLE_RIDE_DATA);
			createAllTables(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RideData getLastRideData(int engagementId, int limit){
		RideData rideData = null;
		Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_RIDE_DATA
				+" WHERE "+RIDE_DATA_ENGAGEMENT_ID+"="+engagementId
				+" ORDER BY "+RIDE_DATA_I+" DESC LIMIT "+limit, null);
		int i0 = cursor.getColumnIndex(Database2.RIDE_DATA_I);
		int i1 = cursor.getColumnIndex(Database2.RIDE_DATA_LAT);
		int i2 = cursor.getColumnIndex(Database2.RIDE_DATA_LNG);
		int i3 = cursor.getColumnIndex(Database2.RIDE_DATA_T);
		int i4 = cursor.getColumnIndex(Database2.RIDE_DATA_ACC_DISTANCE);
		if(cursor.moveToFirst()){
			if(limit == 2){
				cursor.moveToLast();
			}
			rideData = new RideData(cursor.getInt(i0),
					Double.parseDouble(cursor.getString(i1)),
					Double.parseDouble(cursor.getString(i2)),
					cursor.getLong(i3),
					cursor.getDouble(i4));
		}
		cursor.close();
		return rideData;
	}



	public double checkRideData(int engagementId, long starTime) {
		boolean containsZero = false;
		double startDistance = 0;
		double acctualDistance = 0;
		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RIDE_DATA
				+ " WHERE " + RIDE_DATA_ENGAGEMENT_ID + "=" + engagementId
				+ " ORDER BY " + RIDE_DATA_I, null);
		int i1 = cursor.getColumnIndex(Database2.RIDE_DATA_LAT);
		int i2 = cursor.getColumnIndex(Database2.RIDE_DATA_LNG);
		int i3 = cursor.getColumnIndex(Database2.RIDE_DATA_T);
		int i4 = cursor.getColumnIndex(Database2.RIDE_DATA_ACC_DISTANCE);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			if (Double.parseDouble(cursor.getString(i1)) == 0 && Double.parseDouble(cursor.getString(i2)) == 0) {
				containsZero = true;
				break;
			}
			if (starTime >= cursor.getLong(i3)) {
				startDistance = cursor.getDouble(i4);
			}
		}
		if (!containsZero) {
			cursor.moveToLast();
			acctualDistance = cursor.getDouble(i4) - startDistance;
		} else {
			acctualDistance = getLastRideData(engagementId, 1).accDistance;
		}
		cursor.close();
		return acctualDistance;
	}

	public long getFirstRideDataTime(int engagementId){
		long time = 0;
		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RIDE_DATA
				+ " WHERE " + RIDE_DATA_ENGAGEMENT_ID + "=" + engagementId
				+ " AND " + RIDE_DATA_LAT + "='0.0' AND " + RIDE_DATA_LNG + "='0.0'", null);
		int i0 = cursor.getColumnIndex(Database2.RIDE_DATA_I);
		int i3 = cursor.getColumnIndex(Database2.RIDE_DATA_T);
		if(cursor.moveToFirst()){
			Cursor cursor1 = database.rawQuery("SELECT * FROM " + TABLE_RIDE_DATA
					+ " WHERE "
					+ RIDE_DATA_I + ">" + cursor.getInt(i0)
					+ " AND "
					+ RIDE_DATA_ENGAGEMENT_ID + "=" + engagementId
					, null);
			int i31 = cursor1.getColumnIndex(Database2.RIDE_DATA_T);
			if(cursor1.moveToFirst()){
				time = System.currentTimeMillis() - Long.parseLong(cursor1.getString(i31));
			} else{
				time = System.currentTimeMillis() - cursor.getLong(i3);
			}
			cursor1.close();
		}
		cursor.close();
		return time;
	}




	public void insertRideLog(String log, int engagementId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Database2.RIDE_LOGS_LOG, log);
		contentValues.put(Database2.RIDE_LOGS_ENG_ID, engagementId);
		database.insert(Database2.TABLE_RIDE_LOGS, null, contentValues);
		Log.i("Database2 TABLE_RIDE_LOGS", "log="+log+", engagementId="+engagementId);
	}

	public String getRideLogs(int engagementId){
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.RIDE_LOGS_LOG};
			cursor = database.query(Database2.TABLE_RIDE_LOGS, columns,
					RIDE_LOGS_ENG_ID+"="+engagementId, null, null, null, null);

			int i0 = cursor.getColumnIndex(Database2.RIDE_LOGS_LOG);
			StringBuilder sb = new StringBuilder();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				sb.append(cursor.getString(i0)).append("\n");
			}
			if(cursor != null){cursor.close();}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}  finally {
			if(cursor != null){cursor.close();}
		}
	}

	public void deleteRideLogs(int engagementId) {
		database.delete(TABLE_RIDE_LOGS, RIDE_LOGS_ENG_ID + "=" + engagementId, null);
	}

	public void deleteRideLogsAll() {
		database.delete(TABLE_RIDE_LOGS, null, null);
	}



	public RingData getRingData(String limit) {
		RingData ringData = null;
		Cursor cursor = null;
		try {

			cursor = database.rawQuery("SELECT * FROM " + TABLE_RING_DATA
					+ " ORDER BY " + RING_DATA_ENGAGEMENT + " DESC LIMIT " + limit, null);

			int i0 = cursor.getColumnIndex(Database2.RING_DATA_ENGAGEMENT);
			int i1 = cursor.getColumnIndex(Database2.RING_DATA_TIME);

			if (cursor.moveToFirst()) {
				try {
					ringData = new RingData(cursor.getInt(i0),cursor.getLong(i1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}

		return ringData;
	}



	public String getRingCompleteData() {
		StringBuilder ringDataStr = new StringBuilder();
		RingData ringData;
		String template = "engagement,time";
		String newLine = "\n";
		boolean hasValues = false;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.RING_DATA_ENGAGEMENT, Database2.RING_DATA_TIME};
			cursor = database.query(Database2.TABLE_RING_DATA, columns, null, null, null, null, null);

//			Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RING_DATA
//					+ " ORDER BY " + RING_DATA_ENGAGEMENT + " DESC" , null);

			int i0 = cursor.getColumnIndex(Database2.RING_DATA_ENGAGEMENT);
			int i1 = cursor.getColumnIndex(Database2.RING_DATA_TIME);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				try {
					ringData = new RingData(cursor.getInt(i0),cursor.getLong(i1));
					if(ringData.time > 130000){
						ringData = new RingData(cursor.getInt(i0),1);
						ringDataStr.append(ringData.toString()).append(newLine);
					} else{
						ringDataStr.append(ringData.toString()).append(newLine);
					}

					hasValues = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (hasValues) {
				ringDataStr.insert(0, template + newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}

		return ringDataStr.toString();
	}



	void insertRingData(int engagementId, String time) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.RING_DATA_ENGAGEMENT, engagementId);
			contentValues.put(Database2.RING_DATA_TIME, time);
			database.insert(Database2.TABLE_RING_DATA, null, contentValues);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}


	public void updateRingData(int engagementId, String time) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.RING_DATA_ENGAGEMENT, engagementId);
			contentValues.put(Database2.RING_DATA_TIME, time);
			database.update(Database2.TABLE_RING_DATA, contentValues, RING_DATA_ENGAGEMENT + "=" + engagementId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void deleteRingData() {
		try {
			database.delete(Database2.TABLE_RING_DATA, null, null);
			database.execSQL("DROP TABLE " + Database2.TABLE_RING_DATA);
			createAllTables(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	int getPenalityData(String timediff) {
		Cursor cursor = null;
		int count = 0;
		try {
			cursor = database.rawQuery("select sum(" + Database2.PENALITY_FACTOR + ") as sum_penalty from " + Database2.TABLE_PENALITY_COUNT + " where " + Database2.PENALITY_TIME + " >" + timediff, null);
			if (cursor.moveToFirst()) {
				count = cursor.getInt(cursor.getColumnIndex("sum_penalty"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return count;
	}

	void insertPenalityData(String penalityTime, double penalityFactor) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.PENALITY_TIME, penalityTime);
			contentValues.put(Database2.PENALITY_FACTOR, penalityFactor);
			database.insert(Database2.TABLE_PENALITY_COUNT, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void deletePenalityData() {
		try {
			database.delete(Database2.TABLE_PENALITY_COUNT, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getMetringState() {
		String choice = OFF;
		String[] columns = new String[]{Database2.METERING_STATE};
		Cursor cursor = database.query(Database2.TABLE_METERING_STATE, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			choice = cursor.getString(cursor.getColumnIndex(Database2.METERING_STATE));
		}
		cursor.close();
		return choice;
	}


	public int updateMetringState(String choice) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.METERING_STATE, choice);
			int rowsAffected = database.update(Database2.TABLE_METERING_STATE, contentValues, null, null);
			if (rowsAffected == 0) {
				database.insert(Database2.TABLE_METERING_STATE, null, contentValues);
				return 1;
			} else {
				return rowsAffected;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}


	public void insertCustomAudioUrl(String url, String id) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.CUSTOM_AUDIO_URL, url);
			contentValues.put(Database2.CUSTOM_AUDIO_ID, id);
			database.insert(Database2.TABLE_CUSTOM_AUDIO, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getCustomAudioUrl(String id) {
		String choice = "";
		String[] columns = new String[]{Database2.CUSTOM_AUDIO_URL};
		Cursor cursor = database.query(Database2.TABLE_CUSTOM_AUDIO, columns, CUSTOM_AUDIO_ID + "=?",
				new String[]{id}, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			choice = cursor.getString(cursor.getColumnIndex(Database2.CUSTOM_AUDIO_URL));
		}
		cursor.close();
		return choice;
	}


//        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//        + PARENT_ID + " INTEGER, "
//        + SLAT + " REAL, "
//        + SLNG + " REAL, "
//        + DLAT + " REAL, "
//        + DLNG + " REAL, "
//        + SECTION_INCOMPLETE + " INTEGER, "
//        + GOOGLE_PATH + " INTEGER, "
//        + ACKNOWLEDGED + " INTEGER"


	//------- Current path table

	public long insertCurrentPathItem(long parentId, double slat, double slng, double dlat, double dlng, int sectionIncomplete, int googlePath) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(PARENT_ID, parentId);
			contentValues.put(SLAT, slat);
			contentValues.put(SLNG, slng);
			contentValues.put(DLAT, dlat);
			contentValues.put(DLNG, dlng);
			contentValues.put(SECTION_INCOMPLETE, sectionIncomplete);
			contentValues.put(GOOGLE_PATH, googlePath);
			contentValues.put(ACKNOWLEDGED, 0);

			return database.insert(TABLE_CURRENT_PATH, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updateCurrentPathItemSectionIncomplete(long rowId, int sectionIncomplete) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(SECTION_INCOMPLETE, sectionIncomplete);
			return database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	void updateCurrentPathItemSectionIncompleteAndGooglePath(long rowId) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(SECTION_INCOMPLETE, 0);
			contentValues.put(GOOGLE_PATH, 0);
			database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int updateCurrentPathItemAcknowledged(long rowId, int acknowledged) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(ACKNOWLEDGED, acknowledged);
			return database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	void updateCurrentPathItemAcknowledgedForArray(ArrayList<Long> rowId) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(ACKNOWLEDGED, 1);
			int rowsAffected = database.update(TABLE_CURRENT_PATH, contentValues, ID + " in(" + rowId.toString().substring(1, rowId.toString().length() - 1) + ")", null);
			Log.e("rowsAffected", "=" + rowsAffected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public ArrayList<CurrentPathItem> getCurrentPathItemsSaved() {
		ArrayList<CurrentPathItem> currentPathItems = new ArrayList<>();
		Cursor cursor = null;
		try {
			String[] columns = new String[]{ID, PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED};
			cursor = database.query(TABLE_CURRENT_PATH, columns, null, null, null, null, null);
			currentPathItems.addAll(getCurrentPathItemsFromCursor(cursor));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return currentPathItems;
	}


	ArrayList<CurrentPathItem> getCurrentPathItemsToUpload() {
		ArrayList<CurrentPathItem> currentPathItems = new ArrayList<>();
		Cursor cursor = null;
		try {
			String[] columns = new String[]{ID, PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED};
			cursor = database.query(TABLE_CURRENT_PATH, columns, ACKNOWLEDGED + "=0", null, null, null, null);
			currentPathItems.addAll(getCurrentPathItemsFromCursor(cursor));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return currentPathItems;
	}

	public Pair<Double, CurrentPathItem> getCurrentPathItemsAllComplete() {
		ArrayList<CurrentPathItem> currentPathItems = new ArrayList<>();
		Pair<Double, CurrentPathItem> pathItemPair = null;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{ID, PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED};
			cursor = database.query(TABLE_CURRENT_PATH, columns, SECTION_INCOMPLETE + "=0", null, null, null, null);
			currentPathItems.addAll(getCurrentPathItemsFromCursor(cursor));
			if(currentPathItems.size() > 0) {
				double totalDistance = 0;
				CurrentPathItem lastCPI = currentPathItems.get(currentPathItems.size() - 1);
				for (CurrentPathItem cpi : currentPathItems) {
					totalDistance += cpi.distance();
				}
				pathItemPair = new Pair<>(totalDistance,lastCPI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return pathItemPair;
	}


	private ArrayList<CurrentPathItem> getCurrentPathItemsFromCursor(Cursor cursor) {
		ArrayList<CurrentPathItem> currentPathItems = new ArrayList<>();
		try {
			if (cursor.getCount() > 0) {
				int in0 = cursor.getColumnIndex(ID);
				int in1 = cursor.getColumnIndex(PARENT_ID);
				int in2 = cursor.getColumnIndex(SLAT);
				int in3 = cursor.getColumnIndex(SLNG);
				int in4 = cursor.getColumnIndex(DLAT);
				int in5 = cursor.getColumnIndex(DLNG);
				int in6 = cursor.getColumnIndex(SECTION_INCOMPLETE);
				int in7 = cursor.getColumnIndex(GOOGLE_PATH);
				int in8 = cursor.getColumnIndex(ACKNOWLEDGED);


				int currentCursorPosition;
				long parentId;
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					try {
						if (0 == cursor.getInt(in6)) {
							CurrentPathItem currentPathItem = new CurrentPathItem(cursor.getLong(in0),
									cursor.getLong(in1),
									cursor.getDouble(in2),
									cursor.getDouble(in3),
									cursor.getDouble(in4),
									cursor.getDouble(in5),
									cursor.getInt(in6),
									cursor.getInt(in7),
									cursor.getInt(in8));

							if (-1 == currentPathItem.parentId) {
								currentPathItems.add(currentPathItem);
							}

							if (1 == currentPathItem.googlePath) {
								parentId = currentPathItem.id;
								currentCursorPosition = cursor.getPosition();
								for (cursor.moveToPosition(currentCursorPosition); !cursor.isAfterLast(); cursor.moveToNext()) {
									try {
										if (0 == cursor.getInt(in6)) {
											CurrentPathItem currentPathItemChild = new CurrentPathItem(cursor.getLong(in0),
													cursor.getLong(in1),
													cursor.getDouble(in2),
													cursor.getDouble(in3),
													cursor.getDouble(in4),
													cursor.getDouble(in5),
													cursor.getInt(in6),
													cursor.getInt(in7),
													cursor.getInt(in8));
											if (parentId == currentPathItemChild.parentId) {
												currentPathItems.add(currentPathItemChild);
											}
										} else {
											break;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								cursor.moveToPosition(currentCursorPosition);
							}
						} else {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentPathItems;
	}


	public void deleteAllCurrentPathItems() {
		try {
			database.delete(Database2.TABLE_CURRENT_PATH, null, null);
			database.execSQL("DROP TABLE " + Database2.TABLE_CURRENT_PATH);
			createAllTables(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}







	int getGpsState() {
		Cursor cursor = null;
		int gpsState = GpsState.ZERO_TWO.getOrdinal();
		try {
			String[] columns = new String[]{Database2.GPS_STATE};
			cursor = database.query(Database2.TABLE_GPS_STATE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				gpsState = cursor.getInt(cursor.getColumnIndex(Database2.GPS_STATE));
			}
		} catch (Exception ignored) {
		} finally {
			if(cursor != null){cursor.close();}
		}
		return gpsState;
	}


	void updateGpsState(int choice) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.GPS_STATE, choice);
			int rowsAffected = database.update(Database2.TABLE_GPS_STATE, contentValues, null, null);
			if (rowsAffected == 0) {
				database.insert(Database2.TABLE_GPS_STATE, null, contentValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	private ArrayList<NotificationData> getAllNotification() {
		ArrayList<NotificationData> allNotification = new ArrayList<>();
		Cursor cursor = null;
		try {
			String[] columns = new String[]{NOTIFICATION_ID, TIME_PUSH_ARRIVED, MESSAGE, TIME_TO_DISPLAY, TIME_TILL_DISPLAY, NOTIFICATION_IMAGE};
			cursor = database.query(TABLE_NOTIFICATION_CENTER, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				int in0 = cursor.getColumnIndex(NOTIFICATION_ID);
				int in1 = cursor.getColumnIndex(TIME_PUSH_ARRIVED);
				int in2 = cursor.getColumnIndex(MESSAGE);
				int in4 = cursor.getColumnIndex(TIME_TO_DISPLAY);
				int in5 = cursor.getColumnIndex(TIME_TILL_DISPLAY);
				int in6 = cursor.getColumnIndex(NOTIFICATION_IMAGE);

				long currentTimeLong = DateOperations.getMilliseconds(DateOperations.getCurrentTimeInUTC());
				Log.i("current time is ", "---->" + currentTimeLong);

				for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
					try {
						long savedIn4 = 600000;
						try {
							savedIn4 = Long.parseLong(cursor.getString(in4));
						} catch (Exception e) {
							e.printStackTrace();
						}
						long pushArrAndTimeToDisVal = (savedIn4 + DateOperations.getMilliseconds(cursor.getString(in1)));

						if ((!"0".equalsIgnoreCase(cursor.getString(in4))) && (!"".equalsIgnoreCase(cursor.getString(in5)))) { //if both values
							if ((currentTimeLong < pushArrAndTimeToDisVal) &&
									(currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
								allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2), cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
							}
						} else if ((!"0".equalsIgnoreCase(cursor.getString(in4))) && ("".equalsIgnoreCase(cursor.getString(in5)))) { // only timeToDisplay
							if ((currentTimeLong < pushArrAndTimeToDisVal)) {
								allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
										cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
							}
						} else if ((!"".equalsIgnoreCase(cursor.getString(in5))) && ("0".equalsIgnoreCase(cursor.getString(in4)))) { //only timeTillDisplay
							if ((currentTimeLong < DateOperations.getMilliseconds(cursor.getString(in5)))) {
								allNotification.add(new NotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
										cursor.getString(in4), cursor.getString(in5), cursor.getString(in6)));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return allNotification;
	}

	private int getAllNotificationCount() {
		Cursor cursor = null;
		int count = 0;
		try {
			String[] columns = new String[]{NOTIFICATION_ID};
			cursor = database.query(TABLE_NOTIFICATION_CENTER, columns, null, null, null, null, null);
			count =cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return count;
	}

	private void insertNotification(Context context, int id, String timePushArrived, String message, String timeToDisplay,
									String timeTillDisplay, String notificationImage) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(NOTIFICATION_ID, id);
			contentValues.put(TIME_PUSH_ARRIVED, timePushArrived);
			contentValues.put(MESSAGE, message);
			contentValues.put(TIME_TO_DISPLAY, timeToDisplay);
			contentValues.put(TIME_TILL_DISPLAY, timeTillDisplay);
			contentValues.put(NOTIFICATION_IMAGE, notificationImage);
			database.insert(TABLE_NOTIFICATION_CENTER, null, contentValues);
			getAllNotificationCount();
		} catch (Exception e) {
			e.printStackTrace();
			dropAndCreateNotificationTable(database, context);
			insertNotification(context, id, timePushArrived, message, timeToDisplay, timeTillDisplay, notificationImage);
		}
	}


	public ArrayList<AllNotificationData> getAllDBNotification() {
		ArrayList<AllNotificationData> allDBNotification = new ArrayList<>();
		Cursor cursor = null;
		try {
			String[] columns = new String[]{DB_NOTIFICATION_ID, DB_NOTIFICATION_PACKAGE, DB_NOTIFICATION_MESSAGE, DB_NOTIFICATION_TITLE};
			cursor = database.query(TABLE_NOTIFICATION_DB, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				int in0 = cursor.getColumnIndex(DB_NOTIFICATION_ID);
				int in1 = cursor.getColumnIndex(DB_NOTIFICATION_PACKAGE);
				int in2 = cursor.getColumnIndex(DB_NOTIFICATION_MESSAGE);
				int in4 = cursor.getColumnIndex(DB_NOTIFICATION_TITLE);

				long currentTimeLong = DateOperations.getMilliseconds(DateOperations.getCurrentTimeInUTC());
				Log.i("current time is ", "---->" + currentTimeLong);

				for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
					try {
						allDBNotification.add(new AllNotificationData(cursor.getInt(in0), cursor.getString(in1), cursor.getString(in2),
								cursor.getString(in4)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return allDBNotification;
	}


	public int getAllDbNotificationCount() {
		int count = 0;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{DB_NOTIFICATION_ID};
			cursor = database.query(TABLE_NOTIFICATION_DB, columns, null, null, null, null, null);
			count = cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}
		return count;
	}


	public void insertNotificationdb(int id, String packageText, String message, String title) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DB_NOTIFICATION_ID, id);
			contentValues.put(DB_NOTIFICATION_PACKAGE, packageText);
			contentValues.put(DB_NOTIFICATION_MESSAGE, message);
			contentValues.put(DB_NOTIFICATION_TITLE, title);
			database.insert(TABLE_NOTIFICATION_DB, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int deleteNotificationdb(int notificationId) {
		try {
			return database.delete(TABLE_NOTIFICATION_DB, NOTIFICATION_ID + "=" + notificationId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void deleteNotificationTabledb() {
		try {
			database.execSQL("delete from " + TABLE_NOTIFICATION_DB);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}





	public void insertPoolDiscountFlag(int engagementId, int flag) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.POOL_RIDE_ENGAGEMENT_ID, engagementId);
			contentValues.put(Database2.POOL_RIDE_DISCOUNT_FLAG, flag);
			database.insert(Database2.TABLE_POOL_DISCOUNT_FLAG, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public int getPoolDiscountFlag(int engagementId){
		int poolDiscount = 0;
		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_POOL_DISCOUNT_FLAG
				+ " WHERE " + POOL_RIDE_ENGAGEMENT_ID + "=" + engagementId, null);
		int i0 = cursor.getColumnIndex(Database2.POOL_RIDE_DISCOUNT_FLAG);
		if(cursor.moveToFirst()){
			poolDiscount = Integer.parseInt(cursor.getString(i0));
		}
		cursor.close();
		return poolDiscount;
	}


	public void updatePoolDiscountFlag(int engagementId, int flag) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(POOL_RIDE_DISCOUNT_FLAG, flag);
			database.update(TABLE_POOL_DISCOUNT_FLAG, contentValues, POOL_RIDE_ENGAGEMENT_ID + "=" + engagementId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void deletePoolDiscountFlag(int engagementId) {
		try {
			database.delete(Database2.TABLE_POOL_DISCOUNT_FLAG, POOL_RIDE_ENGAGEMENT_ID + "=" + engagementId, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	String getWaitTimeFromDB(){
		String wait = "0";
		Cursor cursor = null;
		try {
			String [] colums = new String[] {Database2.WAIT_TIME};
			cursor = database.query(Database2.TABLE_WAIT_TIME, colums, null, null, null, null, null);
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				wait = cursor.getString(cursor.getColumnIndex(Database2.WAIT_TIME));
			}
		} catch (Exception ignored) {
		} finally {
			if(cursor != null){cursor.close();}
		}
		return wait;
	}


	void updateWaitTime(String time){

		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.WAIT_TIME, time);
			int rowsAffected = database.update(Database2.TABLE_WAIT_TIME, contentValues, null, null);
			if(rowsAffected == 0){
				database.insert(Database2.TABLE_WAIT_TIME, null, contentValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteWaitTimeData() {
		try {
			database.delete(Database2.TABLE_WAIT_TIME, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void insertDataUsage(String time, String recived, String sent) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(LOG_TIME, time);
			contentValues.put(BYTES_RECEIVED, recived);
			contentValues.put(BYTES_SENT, sent);

			database.insert(TABLE_DATA_USAGE, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDataUsage() {
		StringBuilder usageDataStr = new StringBuilder();
		String template = "time,receive,sent";
		String newLine = "\n";
		boolean hasValues = false;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.LOG_TIME, Database2.BYTES_RECEIVED, Database2.BYTES_SENT};
			cursor = database.query(Database2.TABLE_DATA_USAGE, columns, null, null, null, null, null);

			int i0 = cursor.getColumnIndex(Database2.LOG_TIME);
			int i1 = cursor.getColumnIndex(Database2.BYTES_RECEIVED);
			int i2 = cursor.getColumnIndex(Database2.BYTES_SENT);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				try {
					UsageData usageData = new UsageData(cursor.getString(i0),
							cursor.getString(i1),
							cursor.getString(i2));

					usageDataStr.append(usageData.toString()).append(newLine);
					hasValues = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (hasValues) {
				usageDataStr.insert(0, template + newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}

		return usageDataStr.toString();
	}

	public void deleteUsageData() {
		try {
			database.delete(Database2.TABLE_DATA_USAGE, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void insertUSLLog(String event) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(LOG_TIMESTAMP, DateOperations.getCurrentTime());
			contentValues.put(LOG_EVENT, event);

			database.insert(TABLE_USL_LOG, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUSLLog() {
		StringBuilder usageDataStr = new StringBuilder();
		String template = "time,event";
		String newLine = "\n";
		boolean hasValues = false;
		Cursor cursor = null;
		try {
			String[] columns = new String[]{Database2.LOG_TIMESTAMP, Database2.LOG_EVENT};
			cursor = database.query(Database2.TABLE_USL_LOG, columns, null, null, null, null, null);

			int i0 = cursor.getColumnIndex(Database2.LOG_TIMESTAMP);
			int i1 = cursor.getColumnIndex(Database2.LOG_EVENT);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				try {
					LogUSL logUSL = new LogUSL(cursor.getString(i0),
							cursor.getString(i1));

					usageDataStr.append(logUSL.toString()).append(newLine);
					hasValues = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (hasValues) {
				usageDataStr.insert(0, template + newLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null){cursor.close();}
		}

		return usageDataStr.toString();
	}

	public void deleteUSLLog() {
		try {
			database.delete(Database2.TABLE_USL_LOG, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void setKeyValue(String key, String value) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(VALUE, value);
		int rowsAffected = database.update(TABLE_KEY_VALUE, contentValues, KEY + "=?", new String[]{key});
		if (rowsAffected < 1) {
			contentValues.put(KEY, key);
			database.insert(TABLE_KEY_VALUE, null, contentValues);
		}
	}


	public String getKeyValue(String key, String defaultVal){
		String[] columns = new String[]{VALUE};
		Cursor cursor = database.query(TABLE_KEY_VALUE, columns, KEY+"=?", new String[]{key}, null, null, null);

		int i0 = cursor.getColumnIndex(Database2.VALUE);
		String value = defaultVal;
		if(cursor.moveToFirst()){
			value = cursor.getString(i0);
		}
		cursor.close();
		return value;
	}



	public ArrayList<String> getAllKeys(){
		String[] columns = new String[]{KEY};
		Cursor cursor = database.query(TABLE_KEY_VALUE, columns, null, null, null, null, null);

		int i0 = cursor.getColumnIndex(Database2.KEY);
		ArrayList<String> keys = new ArrayList<>();
		if(cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				keys.add(cursor.getString(i0));
			}
		}

		cursor.close();
		return keys;
	}


	public void removeKey(String key) {
		database.delete(TABLE_KEY_VALUE, KEY + "= ?", new String[]{key});
	}

	public void removeKeyLike(String key) {
		database.delete(TABLE_KEY_VALUE, KEY + " LIKE ?", new String[]{key+"%"});
	}


}
