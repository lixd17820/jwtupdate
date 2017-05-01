package com.jwt.update.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SysDataOpenHelper extends SQLiteOpenHelper {

	public static final String SYS_TABLE_NAME = "sysdata";
	public static final String KEY_WORD = "key";
	public static final String KEY_DEFINITION = "value";
	
	private static final String SYS_TABLE_CREATE = "CREATE TABLE "
			+ SYS_TABLE_NAME + " (" + KEY_WORD + " TEXT primary key, "
			+ KEY_DEFINITION + " TEXT);";

	public SysDataOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("SysDataOpenHelper", SYS_TABLE_CREATE);
		db.execSQL(SYS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("SysDataOpenHelper", "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		// Kills the table and existing data
		db.execSQL("DROP TABLE IF EXISTS " + SYS_TABLE_NAME);

		// Recreates the database with a new version
		onCreate(db);
	}

//	public static synchronized SysDataOpenHelper getDBAdapterInstance(
//			Context context) {
//		if (mDBConnection == null) {
//			mDBConnection = new SysDataOpenHelper(context);
//		}
//		return mDBConnection;
//	}
//
//	public void openDataBase() throws SQLException {
//		myDataBase = SQLiteDatabase.openDatabase(context.getDatabasePath(
//				DATABASE_NAME).getAbsolutePath(), null,
//				SQLiteDatabase.OPEN_READWRITE);
//	}
//
//	/**
//	 * Close the database if exist
//	 */
//	@Override
//	public synchronized void close() {
//		if (myDataBase != null)
//			myDataBase.close();
//		super.close();
//	}
//
//	/**
//	 * 查询记录
//	 * 
//	 * @param tableName
//	 * @param tableColumns
//	 * @param whereClase
//	 * @param whereArgs
//	 * @param groupBy
//	 * @param having
//	 * @param orderBy
//	 * @return
//	 */
//	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
//			String whereClase, String whereArgs[], String groupBy,
//			String having, String orderBy) {
//		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
//				groupBy, having, orderBy);
//	}
//
//	/**
//	 * 加入或更新记录
//	 * 
//	 * @param tableName
//	 * @param initialValues
//	 * @return
//	 */
//	public long replaceRecordsInDB(String tableName, ContentValues initialValues) {
//		return myDataBase.replace(tableName, null, initialValues);
//	}
}
