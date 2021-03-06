package product.clicklabs.jugnoo.driver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Handles database related work
 */
public class Database {																	// class for handling database related activities

	private static Database dbInstance;
	
	private static final String DATABASE_NAME = BuildConfig.APP_DB_ID + "_database";						// declaring database variables

	private static final int DATABASE_VERSION = 2;

	private DbHelper dbHelper;

	private SQLiteDatabase database;

	// ***************** table_email_suggestions table columns
	// **********************//
	private static final String TABLE_EMAILS = "table_email_suggestions";
	private static final String EMAIL = "email";


	
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
			/****************************************** CREATING ALL THE TABLES *****************************************************/
			createAllTables(database);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			onCreate(database);
		}

	}
	
	private static void createAllTables(SQLiteDatabase database){
		// table_email_suggestions
					database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_EMAILS + " ("
							+ EMAIL + " TEXT NOT NULL" + ");");

	}
	

	public static Database getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new Database(context);
		} 
		else if (!dbInstance.database.isOpen()) {
			dbInstance = null;
			dbInstance = new Database(context);
		}
		return dbInstance;
	}
	
	private Database(Context context) {
		dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
		createAllTables(database);
	}

	public void close() {
		database.close();
		dbHelper.close();
		System.gc();
	}


	/**
	 * Inserting login Email
	 * @param email String 
	 * @return
	 */
	public long insertEmail(String email) {																// insert login Email
		String[] columns = new String[] { Database.EMAIL };
		int count = 0;
		
		Cursor cursor1 = database
				.query(Database.TABLE_EMAILS, columns,
						Database.EMAIL + "=?", new String[] { email }, null,
						null, null);																	// checks if email is already added or not
		count = cursor1.getCount();
		if (count <= 0) {
		
		Cursor cursor = database.query(Database.TABLE_EMAILS, columns, null,
				null, null, null, null);
		count = cursor.getCount();
		int columnIndex = cursor.getColumnIndex(Database.EMAIL);
		if (count >= 20) {																							// else first deleting an email entry
			String emailToDelete = "";
			cursor.moveToFirst();
			emailToDelete = cursor.getString(columnIndex);
			deleteEmail(emailToDelete);
		}
		cursor.close();
		cursor1.close();
		
			ContentValues contentValues = new ContentValues();
			contentValues.put(Database.EMAIL, email);

			return database.insert(Database.TABLE_EMAILS, null, contentValues);
		} else {																						// if already added email will not be added
			return 1;
		}

	}

	/**
	 * Function to get login email array
	 * @return string array containing email
	 */
	public String[] getEmails() {																	// get login Email array
		String[] columns = new String[] { Database.EMAIL };

		Cursor cursor = database.query(Database.TABLE_EMAILS, columns, null,
				null, null, null, null);

		String result = "";
		
		if (cursor.getCount() > 0) {																		// if there are more than one emails
			
			int emailColumnIndex = cursor.getColumnIndex(Database.EMAIL);
	
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				result = result + cursor.getString(emailColumnIndex) + "\n";
			}
	
			String[] emails = result.split("\n");
			cursor.close();

			return emails;
		} else {
			cursor.close();
			return null;
		}

	}

	/**
	 * delete an email entry 
	 * @param email email string to delete
	 */
	public void deleteEmail(String email) {															// delete an email entry
		database.delete(Database.TABLE_EMAILS, Database.EMAIL + "=?",
				new String[] { email });
	}
	
}