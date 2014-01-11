package com.assassin.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

public class ExpenseDBHelper extends SQLiteOpenHelper {

	private static final String name = "ExpenseDB";
	static final String tableNameExpenses = "Expenses";
	static final String tableNameCategories = "Categories";
	static final String tableNameLimits = "Limits";
	private static final int version = 1;
	static final String columnsExpenses[] = { "Date", "Category",
			"Description", "Amount" };
	static final String columnsCategories[] = { "_id", "Name", "Color" };
	static final String columnsLimits[] = { "_id", "Limit_category",
			"Limit_amount" };

	public ExpenseDBHelper(Context context) {
		super(context, name, null, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("Create table "
				+ tableNameExpenses
				+ "(_id integer primary key autoincrement, Date date, Category TEXT, Description text, Amount real)");
		db.execSQL("Create table "
				+ tableNameCategories
				+ "(_id integer primary key autoincrement, Name TEXT, Color integer)");
		db.execSQL("Create table "
				+ tableNameLimits
				+ "(_id integer primary key autoincrement, Limit_category TEXT, Limit_amount real)");

		ContentValues cv = new ContentValues();

		// Inserting Food category
		cv.put(columnsCategories[1], "Food");
		cv.put(columnsCategories[2], Color.parseColor("#FFF700"));
		db.insert(tableNameCategories, null, cv);
		// Inserting Health category.
		cv.put(columnsCategories[1], "Health");
		cv.put(columnsCategories[2], Color.parseColor("#FF3700"));
		db.insert(tableNameCategories, null, cv);
		// Inserting Leisure Category
		cv.put(columnsCategories[1], "Leisure");
		cv.put(columnsCategories[2], Color.parseColor("#FF8000"));
		db.insert(tableNameCategories, null, cv);
		cv.clear();
		// Inserting values in Limits Table
		Cursor cursor = db.rawQuery("Select * from " + tableNameCategories,
				null);
		try {
			cursor.moveToFirst();
			do {
				cv.put(columnsLimits[1], cursor.getString(1));
				cv.put(columnsLimits[2], 0.0);
				db.insert(tableNameLimits, null, cv);
			} while (cursor.moveToNext());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			try {
				db.execSQL("Drop table " + tableNameExpenses);
				db.execSQL("drop table " + tableNameCategories);
				db.execSQL("Drop table " + tableNameLimits);
			} catch (Exception e) {
				e.printStackTrace();
			}
			onCreate(db);
		}
	}
}
