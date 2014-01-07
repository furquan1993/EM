package com.assassin.expensemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDBHelper extends SQLiteOpenHelper {

	private static final String name = "ExpenseDB";
	static final String tableName = "Expenses";
	private static final int version = 1;
	static final String columns [] = { "Date","Category","Description","Amount"}; 
	
	public ExpenseDBHelper(Context context) {
		super(context, name, null, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("Create table "
				+ tableName
				+ "(id integer primary key autoincrement, Date date, Category integer, Description text, Amount real)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("Drop table " + tableName);
			onCreate(db);
		}
	}

}
