package product.clicklabs.jugnoo.driver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.RideData;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Handles database related work
 */
public class Database2 {																	// class for handling database related activities

	private static Database2 dbInstance;
	
	private static final String DATABASE_NAME = "jugnoo_database2";						// declaring database variables

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	private SQLiteDatabase database;

	public static final String YES = "yes", NO = "no", NEVER = "never";
	
	private static final String TABLE_DRIVER_SERVICE_FAST = "table_driver_service_fast";
	private static final String FAST = "fast";

	private static final String TABLE_TOTAL_DISTANCE = "table_total_distance";
	private static final String TOTAL_DISTANCE = "total_distance";
	
	private static final String TABLE_DRIVER_LOC_DATA = "table_driver_loc_data";
	private static final String DLD_ACCESS_TOKEN = "dld_access_token";
	private static final String DLD_DEVICE_TOKEN = "dld_device_token";
	private static final String DLD_SERVER_URL = "dld_server_url";
	
	private static final String TABLE_DRIVER_SCREEN_MODE = "table_driver_screen_mode";
	private static final String DRIVER_SCREEN_MODE = "driver_screen_mode";
	
	public static final String VULNERABLE = "vulnerable";
	public static final String NOT_VULNERABLE = "not_vulnerable";
	
	
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
	
	private static final String TABLE_PORT_NUMBER = "table_port_number";
	private static final String PORT_ID = "port_id";
	private static final String LIVE_PORT_NUMBER = "live_port_number";
	private static final String DEV_PORT_NUMBER = "dev_port_number";
	private static final String SALES_PORT_NUMBER = "sales_port_number";
	
	private static final String DEFAULT_LIVE_PORT = "4012";
	private static final String DEFAULT_DEV_PORT = "8012";
	private static final String DEFAULT_SALES_PORT = "8200";
	
	
	private static final String TABLE_RIDE_DATA = "table_ride_data";
	private static final String RIDE_DATA_I = "i";
	private static final String RIDE_DATA_LAT = "lat";
	private static final String RIDE_DATA_LNG = "lng";
	private static final String RIDE_DATA_T = "t";
	

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




	private static final String TABLE_GPS_STATE = "table_gps_state";
	private static final String GPS_STATE = "gps_state";


	private static final String TABLE_PUSHY_TOKEN = "table_pushy_token";
	private static final String PUSHY_TOKEN = "pushy_token";



    /**
	 * Creates and opens database for the application use 
	 * @author shankar
	 *
	 */
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
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

	
	private static void createAllTables(SQLiteDatabase database){
		/****************************************** CREATING ALL THE TABLES *****************************************************/
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SERVICE_FAST + " ("
				+ FAST + " TEXT" + ");");
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_TOTAL_DISTANCE + " ("
				+ TOTAL_DISTANCE + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_LOC_DATA + " ("
				+ DLD_ACCESS_TOKEN + " TEXT, " 
				+ DLD_DEVICE_TOKEN + " TEXT, " 
				+ DLD_SERVER_URL + " TEXT" 
				+ ");");
		
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
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PORT_NUMBER + " ("
				+ PORT_ID + " TEXT, " 
				+ LIVE_PORT_NUMBER + " TEXT, " 
				+ DEV_PORT_NUMBER + " TEXT, " 
				+ SALES_PORT_NUMBER + " TEXT" 
				+ ");");
		
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_RIDE_DATA + " ("
				+ RIDE_DATA_I + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ RIDE_DATA_LAT + " TEXT, " 
				+ RIDE_DATA_LNG + " TEXT, " 
				+ RIDE_DATA_T + " TEXT" 
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

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_GPS_STATE + " ("
				+ GPS_STATE + " INTEGER" + ");");

		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PUSHY_TOKEN + " ("
				+ PUSHY_TOKEN + " TEXT" + ");");

	}
	
	public static Database2 getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new Database2(context);
		} 
		else if (!dbInstance.database.isOpen()) {
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







	public double getTotalDistance() {
		double totaldistance = 0;
		try {
			String[] columns = new String[] { Database2.TOTAL_DISTANCE };
			Cursor cursor = database.query(Database2.TABLE_TOTAL_DISTANCE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				totaldistance = Double.parseDouble(cursor.getString(cursor.getColumnIndex(Database2.TOTAL_DISTANCE)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totaldistance;
	}


	public void updateTotalDistance(double totalDistance){
		try{
			deleteTotalDistance();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.TOTAL_DISTANCE, totalDistance);
			database.insert(Database2.TABLE_TOTAL_DISTANCE, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}



	public int deleteTotalDistance(){
		try{
			return database.delete(Database2.TABLE_TOTAL_DISTANCE, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	
	
	
	public String getDriverServiceFast() {
		String[] columns = new String[] { Database2.FAST };
		Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_FAST, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.FAST));
			return choice;
		} else {
			return NO;
		}
	}
	
	public void updateDriverServiceFast(String choice) {
		deleteDriverServiceFast();
		insertDriverServiceFast(choice);
	}
	
	public void insertDriverServiceFast(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.FAST, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE_FAST, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteDriverServiceFast(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_FAST, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public void insertDriverLocData(String accessToken, String deviceToken, String serverUrl){
		try{
			deleteDriverLocData();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DLD_ACCESS_TOKEN, accessToken);
			contentValues.put(Database2.DLD_DEVICE_TOKEN, deviceToken);
			contentValues.put(Database2.DLD_SERVER_URL, serverUrl);
			database.insert(Database2.TABLE_DRIVER_LOC_DATA, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteDriverLocData(){
		try{
			database.delete(Database2.TABLE_DRIVER_LOC_DATA, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public String getDLDAccessToken() {
		try {
			String[] columns = new String[] { Database2.DLD_ACCESS_TOKEN };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_ACCESS_TOKEN));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getDLDDeviceToken() {
		try {
			String[] columns = new String[] { Database2.DLD_DEVICE_TOKEN };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_DEVICE_TOKEN));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getDLDServerUrl() {
		try {
			String[] columns = new String[] { Database2.DLD_SERVER_URL };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LOC_DATA, columns, null, null, null, null, null);
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.DLD_SERVER_URL));
			return choice;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	
	
	
	

	
	
	
	
	
	public String getDriverScreenMode() {
		try {
			String[] columns = new String[] { Database2.DRIVER_SCREEN_MODE };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_SCREEN_MODE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String userMode = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_SCREEN_MODE));
				return userMode;
			} else {
				return Database2.NOT_VULNERABLE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Database2.NOT_VULNERABLE;
		}
	}
	
	
	
	public void updateDriverScreenMode(String userMode) {
		try {
			deleteDriverScreenMode();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_SCREEN_MODE, userMode);
			database.insert(Database2.TABLE_DRIVER_SCREEN_MODE, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteDriverScreenMode(){
		try{
			database.delete(Database2.TABLE_DRIVER_SCREEN_MODE, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public Location getDriverCurrentLocation(Context context) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		try {
			String[] columns = new String[] { Database2.DRIVER_CURRENT_LATITUDE, Database2.DRIVER_CURRENT_LONGITUDE,
					Database2.DRIVER_CURRENT_LOCATION_ACCURACY, Database2.DRIVER_CURRENT_LOCATION_TIME, Database2.DRIVER_CURRENT_LOCATION_BEARING };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_CURRENT_LOCATION, columns, null, null, null, null, null);
			
			int in0 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LATITUDE);
			int in1 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LONGITUDE);
			int in2 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_ACCURACY);
			int in3 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_TIME);
			int in4 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_BEARING);

			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				location.setLatitude(Double.parseDouble(cursor.getString(in0)));
				location.setLongitude(Double.parseDouble(cursor.getString(in1)));
				location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
				location.setTime(Long.parseLong(cursor.getString(in3)));
				location.setBearing(Float.parseFloat(cursor.getString(in4)));

			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dbInstance = null;
				Database2.getInstance(context);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			alterTableDriverCurrentLocation();

			String[] columns = new String[] { Database2.DRIVER_CURRENT_LATITUDE, Database2.DRIVER_CURRENT_LONGITUDE,
					Database2.DRIVER_CURRENT_LOCATION_ACCURACY, Database2.DRIVER_CURRENT_LOCATION_TIME, Database2.DRIVER_CURRENT_LOCATION_BEARING };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_CURRENT_LOCATION, columns, null, null, null, null, null);

			int in0 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LATITUDE);
			int in1 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LONGITUDE);
			int in2 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_ACCURACY);
			int in3 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_TIME);
			int in4 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LOCATION_BEARING);

			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				location.setLatitude(Double.parseDouble(cursor.getString(in0)));
				location.setLongitude(Double.parseDouble(cursor.getString(in1)));
				location.setAccuracy(Float.parseFloat(cursor.getString(in2)));
				location.setTime(Long.parseLong(cursor.getString(in3)));
				location.setBearing(Float.parseFloat(cursor.getString(in4)));
			}
		}
		return location;
	}
	
	
	public void updateDriverCurrentLocation(Context context, Location location){
		try{
			deleteDriverCurrentLocation();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_CURRENT_LATITUDE, ""+location.getLatitude());
			contentValues.put(Database2.DRIVER_CURRENT_LONGITUDE, ""+location.getLongitude());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_ACCURACY, ""+location.getAccuracy());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
			contentValues.put(Database2.DRIVER_CURRENT_LOCATION_BEARING, ""+location.getBearing());
			long rowId = database.insert(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);
			Log.e("insert successful", "= rowId =" + rowId);

		} catch(Exception e){
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
				contentValues.put(Database2.DRIVER_CURRENT_LATITUDE, ""+location.getLatitude());
				contentValues.put(Database2.DRIVER_CURRENT_LONGITUDE, ""+location.getLongitude());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_ACCURACY, ""+location.getAccuracy());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_TIME, "" + location.getTime());
				contentValues.put(Database2.DRIVER_CURRENT_LOCATION_BEARING, ""+location.getBearing());
				database.insert(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public void alterTableDriverCurrentLocation(){
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
	
	
	public int deleteDriverCurrentLocation(){
		try{
			return database.delete(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public long getDriverLastLocationTime() {
		long lastTimeInMillis = 0;
		try {
			String[] columns = new String[] { Database2.LAST_LOCATION_TIME };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, columns, null, null, null, null, null);
			
			int in0 = cursor.getColumnIndex(Database2.LAST_LOCATION_TIME);
			
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				lastTimeInMillis = Long.parseLong(cursor.getString(in0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastTimeInMillis;
	}
	
	
	public void updateDriverLastLocationTime(){
		try{
			long timeInMillis = System.currentTimeMillis();
			deleteDriverLastLocationTime();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.LAST_LOCATION_TIME, ""+timeInMillis);
			database.insert(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public int deleteDriverLastLocationTime(){
		try{
			return database.delete(Database2.TABLE_DRIVER_LAST_LOCATION_TIME, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	
	
	
	
	



	public String getDriverServiceRun() {
		try {
			String[] columns = new String[] { Database2.DRIVER_SERVICE_RUN };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String choice = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_SERVICE_RUN));
				return choice;
			} else {
				return YES;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return YES;
		}
	}

	public void updateDriverServiceRun(String choice) {
		try{
			deleteDriverServiceRun();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_SERVICE_RUN, choice);
			database.insert(Database2.TABLE_DRIVER_SERVICE, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void deleteDriverServiceRun(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}














	
	
	
	public long getDriverServiceTimeToRestart() {
		long timeToRestart = System.currentTimeMillis() - 1000;
		try {
			String[] columns = new String[] { Database2.TIME_TO_RESTART };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_SERVICE_TIME_TO_RESTART, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				timeToRestart = Long.parseLong(cursor.getString(cursor.getColumnIndex(Database2.TIME_TO_RESTART)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeToRestart;
	}
	
	
	
	public void updateDriverServiceTimeToRestart(long timeToRestart) {
		try {
			deleteDriverServiceTimeToRestart();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.TIME_TO_RESTART, ""+timeToRestart);
			database.insert(Database2.TABLE_DRIVER_SERVICE_TIME_TO_RESTART, null, contentValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void deleteDriverServiceTimeToRestart(){
		try{
			database.delete(Database2.TABLE_DRIVER_SERVICE_TIME_TO_RESTART, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public String getDriverManualPatchPushReceived() {
		try {
			String[] columns = new String[] { Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_MANUAL_PATCH, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String choice = cursor.getString(cursor.getColumnIndex(Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED));
				return choice;
			} else {
				return NO;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return NO;
		}
	}
	
	public void updateDriverManualPatchPushReceived(String choice) {
		try{
			deleteDriverManualPatchPushReceived();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_MANUAL_PATCH_PUSH_RECEIVED, choice);
			database.insert(Database2.TABLE_DRIVER_MANUAL_PATCH, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void deleteDriverManualPatchPushReceived(){
		try{
			database.delete(Database2.TABLE_DRIVER_MANUAL_PATCH, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int getDriverGcmIntent() {
		try {
			String[] columns = new String[] { Database2.DRIVER_GCM_INTENT };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_GCM_INTENT, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int choice = cursor.getInt(cursor.getColumnIndex(Database2.DRIVER_GCM_INTENT));
				return choice;
			} else {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public void updateDriverGcmIntent(int choice) {
		try{
			deleteDriverGcmIntent();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_GCM_INTENT, choice);
			database.insert(Database2.TABLE_DRIVER_GCM_INTENT, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void deleteDriverGcmIntent(){
		try{
			database.delete(Database2.TABLE_DRIVER_GCM_INTENT, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public ArrayList<PendingAPICall> getAllPendingAPICalls() {
		ArrayList<PendingAPICall> pendingAPICalls = new ArrayList<PendingAPICall>();
		try {
			String[] columns = new String[] { Database2.API_ID, Database2.API_URL, Database2.API_REQUEST_PARAMS };
			Cursor cursor = database.query(Database2.TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				int in0 = cursor.getColumnIndex(Database2.API_ID);
				int in1 = cursor.getColumnIndex(Database2.API_URL);
				int in2 = cursor.getColumnIndex(Database2.API_REQUEST_PARAMS);
				
				for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
					try {
						pendingAPICalls.add(new PendingAPICall(cursor.getInt(in0), cursor.getString(in1), Utils.convertQueryToNameValuePairArr(cursor.getString(in2))));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pendingAPICalls;
	}
	
	public int getAllPendingAPICallsCount() {
		try {
			String[] columns = new String[] { Database2.API_ID };
			Cursor cursor = database.query(Database2.TABLE_PENDING_API_CALLS, columns, null, null, null, null, null);
			return cursor.getCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void insertPendingAPICall(Context context, String url, HashMap<String, String> requestParams) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.API_URL, url);
			contentValues.put(Database2.API_REQUEST_PARAMS, requestParams.toString());
			database.insert(Database2.TABLE_PENDING_API_CALLS, null, contentValues);
			checkStartPendingApisService(context);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int deletePendingAPICall(int apiId){
		try{
			return database.delete(Database2.TABLE_PENDING_API_CALLS, Database2.API_ID + "=" + apiId, null);
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public void checkStartPendingApisService(Context context){
		if(!HomeActivity.isServiceRunning(context, PushPendingCallsService.class.getName())){
			context.startService(new Intent(context, PushPendingCallsService.class));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public int insertDefaultPorts(){
		deletePortNumbers();
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.PORT_ID, "1");
			contentValues.put(Database2.LIVE_PORT_NUMBER, DEFAULT_LIVE_PORT);
			contentValues.put(Database2.DEV_PORT_NUMBER, DEFAULT_DEV_PORT);
			contentValues.put(Database2.SALES_PORT_NUMBER, DEFAULT_SALES_PORT);
			database.insert(Database2.TABLE_PORT_NUMBER, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	public String getLivePortNumber() {
		try {
			String[] columns = new String[] { Database2.LIVE_PORT_NUMBER };
			Cursor cursor = database.query(Database2.TABLE_PORT_NUMBER, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String port = cursor.getString(cursor.getColumnIndex(Database2.LIVE_PORT_NUMBER));
				return port;
			} else {
				insertDefaultPorts();
				return DEFAULT_LIVE_PORT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			insertDefaultPorts();
			return DEFAULT_LIVE_PORT;
		}
	}
	
	public void updateLivePortNumber(String port) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.LIVE_PORT_NUMBER, port);
			database.update(Database2.TABLE_PORT_NUMBER, contentValues, Database2.PORT_ID + "=?", new String[]{"1"});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getDevPortNumber() {
		try {
			String[] columns = new String[] { Database2.DEV_PORT_NUMBER };
			Cursor cursor = database.query(Database2.TABLE_PORT_NUMBER, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String port = cursor.getString(cursor.getColumnIndex(Database2.DEV_PORT_NUMBER));
				return port;
			} else {
				insertDefaultPorts();
				return DEFAULT_DEV_PORT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			insertDefaultPorts();
			return DEFAULT_DEV_PORT;
		}
	}
	
	public void updateDevPortNumber(String port) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DEV_PORT_NUMBER, port);
			database.update(Database2.TABLE_PORT_NUMBER, contentValues, Database2.PORT_ID + "=?", new String[]{"1"});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getSalesPortNumber() {
		try {
			String[] columns = new String[] { Database2.SALES_PORT_NUMBER };
			Cursor cursor = database.query(Database2.TABLE_PORT_NUMBER, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String port = cursor.getString(cursor.getColumnIndex(Database2.SALES_PORT_NUMBER));
				return port;
			} else {
				insertDefaultPorts();
				return DEFAULT_SALES_PORT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			insertDefaultPorts();
			return DEFAULT_SALES_PORT;
		}
	}
	
	public void updateSalesPortNumber(String port) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.SALES_PORT_NUMBER, port);
			database.update(Database2.TABLE_PORT_NUMBER, contentValues, Database2.PORT_ID + "=?", new String[]{"1"});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void deletePortNumbers(){
		try{
			database.delete(Database2.TABLE_PORT_NUMBER, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getRideData() {
		String rideDataStr = "";
		String template = "i,lat,long,t";
		String newLine = "\n";
		boolean hasValues = false;
		try {
			String[] columns = new String[] { Database2.RIDE_DATA_I, Database2.RIDE_DATA_LAT, Database2.RIDE_DATA_LNG, Database2.RIDE_DATA_T };
			Cursor cursor = database.query(Database2.TABLE_RIDE_DATA, columns, null, null, null, null, null);
			
			int i0 = cursor.getColumnIndex(Database2.RIDE_DATA_I);
			int i1 = cursor.getColumnIndex(Database2.RIDE_DATA_LAT);
			int i2 = cursor.getColumnIndex(Database2.RIDE_DATA_LNG);
			int i3 = cursor.getColumnIndex(Database2.RIDE_DATA_T);
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
				try {
					RideData rideData = new RideData(cursor.getInt(i0), 
							Double.parseDouble(cursor.getString(i1)), 
							Double.parseDouble(cursor.getString(i2)), 
							Long.parseLong(cursor.getString(i3)));
					
					rideDataStr = rideDataStr + rideData.toString() + newLine;
					hasValues = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(hasValues){
				rideDataStr = template + newLine + rideDataStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rideDataStr;
	}
	
	public void insertRideData(String lat, String lng, String t) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.RIDE_DATA_LAT, lat);
			contentValues.put(Database2.RIDE_DATA_LNG, lng);
			contentValues.put(Database2.RIDE_DATA_T, t);
			database.insert(Database2.TABLE_RIDE_DATA, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void deleteRideData(){
		try{
			database.delete(Database2.TABLE_RIDE_DATA, null, null);
			database.execSQL("DROP TABLE " + Database2.TABLE_RIDE_DATA);
			createAllTables(database);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getMetringState() {
		String[] columns = new String[] { Database2.METERING_STATE };
		Cursor cursor = database.query(Database2.TABLE_METERING_STATE, columns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.METERING_STATE));
			return choice;
		} else {
			return OFF;
		}
	}
	
	
	public int updateMetringState(String choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.METERING_STATE, choice);
			int rowsAffected = database.update(Database2.TABLE_METERING_STATE, contentValues, null, null);
			if(rowsAffected == 0){
				database.insert(Database2.TABLE_METERING_STATE, null, contentValues);
				return 1;
			}
			else{
				return rowsAffected;
			}
		} catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}




	public void insertCustomAudioUrl(String url, String id) {
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.CUSTOM_AUDIO_URL, url);
			contentValues.put(Database2.CUSTOM_AUDIO_ID, id);
			database.insert(Database2.TABLE_CUSTOM_AUDIO, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public String getCustomAudioUrl(String id) {
		String[] columns = new String[] { Database2.CUSTOM_AUDIO_URL };
		Cursor cursor = database.query(Database2.TABLE_CUSTOM_AUDIO, columns, CUSTOM_AUDIO_ID+"=?",
				new String[]{id}, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String choice = cursor.getString(cursor.getColumnIndex(Database2.CUSTOM_AUDIO_URL));
			return choice;
		} else {
			return "";
		}
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
        try{
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
        } catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public int updateCurrentPathItemSectionIncomplete(long rowId, int sectionIncomplete){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(SECTION_INCOMPLETE, sectionIncomplete);
            int rowsAffected = database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
            return rowsAffected;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int updateCurrentPathItemSectionIncompleteAndGooglePath(long rowId, int sectionIncomplete, int googlePath){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(SECTION_INCOMPLETE, sectionIncomplete);
            contentValues.put(GOOGLE_PATH, googlePath);
            int rowsAffected = database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
            return rowsAffected;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int updateCurrentPathItemAcknowledged(long rowId, int acknowledged){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACKNOWLEDGED, acknowledged);
            int rowsAffected = database.update(TABLE_CURRENT_PATH, contentValues, ID + "=" + rowId, null);
            return rowsAffected;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    public int updateCurrentPathItemAcknowledgedForArray(ArrayList<Long> rowId, int acknowledged){
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(ACKNOWLEDGED, acknowledged);
            int rowsAffected = database.update(TABLE_CURRENT_PATH, contentValues, ID + " in(" + rowId.toString().substring(1, rowId.toString().length() - 1) + ")", null);
            Log.e("rowsAffected", "=" + rowsAffected);
            return rowsAffected;
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }



    public ArrayList<CurrentPathItem> getCurrentPathItemsSaved(){
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList<CurrentPathItem>();
        try {
            String[] columns = new String[] { ID, PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED };
            Cursor cursor = database.query(TABLE_CURRENT_PATH, columns, null, null, null, null, null);
            currentPathItems.addAll(getCurrentPathItemsFromCursor(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPathItems;
    }



    public ArrayList<CurrentPathItem> getCurrentPathItemsToUpload(){
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList<CurrentPathItem>();
        try {
            String[] columns = new String[] { ID, PARENT_ID, SLAT, SLNG, DLAT, DLNG, SECTION_INCOMPLETE, GOOGLE_PATH, ACKNOWLEDGED };
            Cursor cursor = database.query(TABLE_CURRENT_PATH, columns, ACKNOWLEDGED + "=0", null, null, null, null);
            currentPathItems.addAll(getCurrentPathItemsFromCursor(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentPathItems;
    }


    public ArrayList<CurrentPathItem> getCurrentPathItemsFromCursor(Cursor cursor){
        ArrayList<CurrentPathItem> currentPathItems = new ArrayList<CurrentPathItem>();
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


                int currentCursorPosition = -1;
                long parentId = 0;
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                    try {
                        if(0 == cursor.getInt(in6)) {
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
                                        if(0 == cursor.getInt(in6)) {
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
                                        }
                                        else{
                                            break;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                cursor.moveToPosition(currentCursorPosition);
                            }
                            else{
                            }
                        }
                        else{
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



    public void deleteAllCurrentPathItems(){
        try{
            database.delete(Database2.TABLE_CURRENT_PATH, null, null);
            database.execSQL("DROP TABLE " + Database2.TABLE_CURRENT_PATH);
            createAllTables(database);
        } catch(Exception e){
            e.printStackTrace();
        }
    }















	public int getGpsState() {
		try {
			String[] columns = new String[] { Database2.GPS_STATE };
			Cursor cursor = database.query(Database2.TABLE_GPS_STATE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int choice = cursor.getInt(cursor.getColumnIndex(Database2.GPS_STATE));
                return choice;
            } else {
                return GpsState.ZERO_TWO.getOrdinal();
            }
		} catch (Exception e) {
			return GpsState.ZERO_TWO.getOrdinal();
		}
	}


	public int updateGpsState(int choice){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.GPS_STATE, choice);
			int rowsAffected = database.update(Database2.TABLE_GPS_STATE, contentValues, null, null);
			if(rowsAffected == 0){
				database.insert(Database2.TABLE_GPS_STATE, null, contentValues);
				return 1;
			}
			else{
				return rowsAffected;
			}
		} catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}






	public String getPushyToken() {
		try {
			String[] columns = new String[] { Database2.PUSHY_TOKEN };
			Cursor cursor = database.query(Database2.TABLE_PUSHY_TOKEN, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String token = cursor.getString(cursor.getColumnIndex(Database2.PUSHY_TOKEN));
				return token;
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}


	public int updatePushyToken(String token){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.PUSHY_TOKEN, token);
			int rowsAffected = database.update(Database2.TABLE_PUSHY_TOKEN, contentValues, null, null);
			if(rowsAffected == 0){
				database.insert(Database2.TABLE_PUSHY_TOKEN, null, contentValues);
				return 1;
			}
			else{
				return rowsAffected;
			}
		} catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}


}