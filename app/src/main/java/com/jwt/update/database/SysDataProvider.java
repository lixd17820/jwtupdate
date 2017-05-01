package com.jwt.update.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.content.ContentProvider;

public class SysDataProvider extends ContentProvider {

	public static final String AUTHORITY = "com.google.provider.SysData";

	private DatabaseHelper mOpenHelper;
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "sysdata.db";
	public static final String KEY_WORD = "key";
	public static final String KEY_DEFINITION = "value";
	private static final String SYS_TABLE_NAME = "sysdata";

	private static final String SYS_TABLE_CREATE = "CREATE TABLE "
			+ SYS_TABLE_NAME + " (" + KEY_WORD + " TEXT primary key, "
			+ KEY_DEFINITION + " TEXT);";
	private static final String TAG = "SysDataProvider";

	private static final String[] READ_SYS_PROJECTION = new String[] {
			KEY_WORD, KEY_DEFINITION };

	public static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "updateSys", 0);
		uriMatcher.addURI(AUTHORITY, "querySys", 1);
	}

	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SYS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			// Kills the table and existing data
			db.execSQL("DROP TABLE IF EXISTS notes");

			// Recreates the database with a new version
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());

		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = null;
		switch (uriMatcher.match(uri)) {
		case 1:
			c = db.query(SYS_TABLE_NAME, READ_SYS_PROJECTION, selection,
					selectionArgs, null, null, sortOrder);
			break;
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int row = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		// 更新字典表
		case 0:
			row = (int) db.replace(SYS_TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return row;
	}

}
