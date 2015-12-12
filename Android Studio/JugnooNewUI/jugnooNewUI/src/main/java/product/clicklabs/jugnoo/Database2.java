package product.clicklabs.jugnoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.utils.Log;

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
	
	
	private static final String TABLE_DRIVER_LOC_DATA = "table_driver_loc_data";
	private static final String DLD_ACCESS_TOKEN = "dld_access_token";
	private static final String DLD_DEVICE_TOKEN = "dld_device_token";
	private static final String DLD_SERVER_URL = "dld_server_url";
	
	private static final String TABLE_USER_MODE = "table_user_mode";
	private static final String USER_MODE = "user_mode";
	
	public static final String UM_DRIVER = "driver";
	public static final String UM_PASSENGER = "passenger";
	public static final String UM_OFFLINE = "offline";
	
	private static final String TABLE_DRIVER_SCREEN_MODE = "table_driver_screen_mode";
	private static final String DRIVER_SCREEN_MODE = "driver_screen_mode";
	
	public static final String VULNERABLE = "vulnerable";
	public static final String NOT_VULNERABLE = "not_vulnerable";
	
	
	private static final String TABLE_DRIVER_CURRENT_LOCATION = "table_driver_current_location";
	private static final String DRIVER_CURRENT_LATITUDE = "driver_current_latitude";
	private static final String DRIVER_CURRENT_LONGITUDE = "driver_current_longitude";
	
	
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
	
	private static final String TABLE_PORT_NUMBER = "table_port_number";
	private static final String PORT_ID = "port_id";
	private static final String LIVE_PORT_NUMBER = "live_port_number";
	private static final String DEV_PORT_NUMBER = "dev_port_number";
	private static final String SALES_PORT_NUMBER = "sales_port_number";
	
	private static final String DEFAULT_LIVE_PORT = "4012";
	private static final String DEFAULT_DEV_PORT = "8012";
	private static final String DEFAULT_SALES_PORT = "8200";
	
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
		
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_LOC_DATA + " ("
				+ DLD_ACCESS_TOKEN + " TEXT, " 
				+ DLD_DEVICE_TOKEN + " TEXT, " 
				+ DLD_SERVER_URL + " TEXT" 
				+ ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_USER_MODE + " ("
				+ USER_MODE + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_SCREEN_MODE + " ("
				+ DRIVER_SCREEN_MODE + " TEXT" + ");");
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER_CURRENT_LOCATION + " ("
				+ DRIVER_CURRENT_LATITUDE + " TEXT, " 
				+ DRIVER_CURRENT_LONGITUDE + " TEXT" 
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
		
		database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_PORT_NUMBER + " ("
				+ PORT_ID + " TEXT, " 
				+ LIVE_PORT_NUMBER + " TEXT, " 
				+ DEV_PORT_NUMBER + " TEXT, " 
				+ SALES_PORT_NUMBER + " TEXT" 
				+ ");");
		
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
	
	
	
	
	
	
	
	public String getUserMode() {
		try {
			String[] columns = new String[] { Database2.USER_MODE };
			Cursor cursor = database.query(Database2.TABLE_USER_MODE, columns, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String userMode = cursor.getString(cursor.getColumnIndex(Database2.USER_MODE));
				Log.e("getuserMode", "="+userMode);
				return userMode;
			} else {
				return Database2.UM_OFFLINE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Database2.UM_OFFLINE;
		}
	}
	
	
	
	public void updateUserMode(String userMode) {
		try {
			deleteUserMode();
			insertUserMode(userMode);
			Log.e("updateUserMode", "="+userMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertUserMode(String userMode){
		try{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.USER_MODE, userMode);
			database.insert(Database2.TABLE_USER_MODE, null, contentValues);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteUserMode(){
		try{
			database.delete(Database2.TABLE_USER_MODE, null, null);
		} catch(Exception e){
			e.printStackTrace();
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public LatLng getDriverCurrentLocation() {
		LatLng latLng = new LatLng(0, 0);
		try {
			String[] columns = new String[] { Database2.DRIVER_CURRENT_LATITUDE, Database2.DRIVER_CURRENT_LONGITUDE };
			Cursor cursor = database.query(Database2.TABLE_DRIVER_CURRENT_LOCATION, columns, null, null, null, null, null);
			
			int in0 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LATITUDE);
			int in1 = cursor.getColumnIndex(Database2.DRIVER_CURRENT_LONGITUDE);
			
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				latLng = new LatLng(Double.parseDouble(cursor.getString(in0)), Double.parseDouble(cursor.getString(in1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latLng;
	}
	
	
	public void updateDriverCurrentLocation(LatLng latLng){
		try{
			deleteDriverCurrentLocation();
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database2.DRIVER_CURRENT_LATITUDE, ""+latLng.latitude);
			contentValues.put(Database2.DRIVER_CURRENT_LONGITUDE, ""+latLng.longitude);
			database.insert(Database2.TABLE_DRIVER_CURRENT_LOCATION, null, contentValues);
		} catch(Exception e){
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
	
}