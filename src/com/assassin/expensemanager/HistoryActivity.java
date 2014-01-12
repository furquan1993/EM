package com.assassin.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class HistoryActivity extends Activity {

	private ImageView btnHistoryAddExpense;
	private Spinner spinCategorySelector;
	private ListView lvHistoryDetails;
	private SQLiteDatabase expenses;
	private final String sql = "Select * from "
			+ ExpenseDBHelper.tableNameExpenses
			+ " ORDER BY _id DESC, Date DESC";
	private String[] categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// Initializing Views
		btnHistoryAddExpense = (ImageView) findViewById(R.id.btnHistoryAddExpense);
		lvHistoryDetails = (ListView) findViewById(R.id.lvHistoryDetails);
		spinCategorySelector = (Spinner) findViewById(R.id.spinCategorySelector);
		lvHistoryDetails.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long pid) {
				View v = view.findViewById(R.id.viewHistoryCategory);
				Integer id = (Integer) v.getTag();
				Log.d("HistoryActivity.ID", id.toString());
				Intent intent = new Intent(HistoryActivity.this,
						AddExpenseActivity.class);
				intent.putExtra(MainActivity.ID, id);
				startActivity(intent);
			}
		});

		btnHistoryAddExpense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryActivity.this,
						AddExpenseActivity.class);
				intent.putExtra(MainActivity.ID, "0");
				startActivity(intent);
			}
		});

		Cursor category = MainActivity.expensesDb.getReadableDatabase()
				.rawQuery(
						"Select * from " + ExpenseDBHelper.tableNameCategories,
						null);
		categories = new String[category.getCount() + 1];
		categories[0] = "All";
		try {
			category.moveToFirst();
			do {
				categories[category.getInt(0)] = category.getString(1);
			} while (category.moveToNext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.history_category, categories);

		spinCategorySelector.setAdapter(adapter);
		spinCategorySelector
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapter,
							View view, int position, long id) {
						TextView tv = (TextView) view
								.findViewById(R.id.txtHistoryCategory);
						String where = " where Category=\'"
								+ tv.getText().toString() + "\'";
						if (tv.getText().toString().equals("All")) {
							where = "";
						}
						Cursor cursor = MainActivity.expensesDb
								.getReadableDatabase()
								.rawQuery(
										"select * from "
												+ ExpenseDBHelper.tableNameExpenses
												+ where, null);
						showList(cursor);
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapter) {
						Cursor cursor = MainActivity.expensesDb
								.getReadableDatabase()
								.rawQuery(
										"select * from "
												+ ExpenseDBHelper.tableNameExpenses,
										null);
						showList(cursor);
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Initializing DBs and cursors
		expenses = MainActivity.expensesDb.getReadableDatabase();
		Cursor cursor = expenses.rawQuery(sql, null);
		showList(cursor);
		try {
			expenses.close();
		} catch (Exception e) {
		}

		lvHistoryDetails.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long pid) {
				Integer id = (Integer) view.findViewById(
						R.id.viewHistoryCategory).getTag();
				Log.d("HistoryActivity.id", id.toString());
				Intent intent = new Intent(HistoryActivity.this,
						AddExpenseActivity.class);
				intent.putExtra(MainActivity.ID, id.toString());
				startActivity(intent);
			}
		});

	}

	// Function to populate the list
	private void showList(Cursor cursor) {
		HistoryAdapter adapter = new HistoryAdapter(this, cursor, true);
		lvHistoryDetails.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings: {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		case R.id.clear: {
			MainActivity.expensesDb.onUpgrade(
					MainActivity.expensesDb.getWritableDatabase(), 1, 2);
			break;
		}
		}
		return super.onContextItemSelected(item);
	}

}

class HistoryAdapter extends CursorAdapter {
	private LayoutInflater inflater;

	public HistoryAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View viewHistoryCategory = (View) view
				.findViewById(R.id.viewHistoryCategory);
		TextView txtHistoryDate = (TextView) view
				.findViewById(R.id.txtHistoryDate);
		TextView txtHistoryDescription = (TextView) view
				.findViewById(R.id.txtHistoryDescription);
		TextView txtHistoryAmount = (TextView) view
				.findViewById(R.id.txtHistoryAmount);
		int color = getCategoryColor(cursor.getString(2));
		Integer id = cursor.getInt(0);
		txtHistoryDate.setText(cursor.getString(1));
		viewHistoryCategory.setBackgroundColor(color);
		viewHistoryCategory.setTag(id);
		txtHistoryDescription.setText(getDescription(cursor));
		txtHistoryAmount.setText("Rs. " + cursor.getDouble(4));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.history_row, parent, false);
	}

	private String getDescription(Cursor cursor) {
		if (cursor.getString(3).equals("")) {
			return cursor.getString(2);
		}
		return cursor.getString(3);
	}

	private int getCategoryColor(String cat) {
		int color = Color.parseColor("#000000");
		SQLiteDatabase db = MainActivity.expensesDb.getReadableDatabase();
		Cursor categoryCursor = db.rawQuery("Select * from "
				+ ExpenseDBHelper.tableNameCategories, null);
		categoryCursor.moveToFirst();
		try {
			do {
				if (categoryCursor.getString(1).equals(cat)) {
					color = categoryCursor.getInt(2);
					break;
				}
			} while (categoryCursor.moveToNext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return color;
	}
}
/*
 * class HistoryCategoryAdapter extends CursorAdapter {
 * 
 * LayoutInflater inflater;
 * 
 * public HistoryCategoryAdapter(Context context, Cursor c, boolean autoRequery)
 * { super(context, c, true); inflater = LayoutInflater.from(context); }
 * 
 * @Override public void bindView(View view, Context context, Cursor cursor) {
 * TextView tv = (TextView) view.findViewById(R.id.txtHistoryCategory);
 * tv.setText(cursor.getString(1)); tv.setTag(cursor.getInt(0)); }
 * 
 * @Override public View newView(Context context, Cursor cursor, ViewGroup
 * parent) { return inflater.inflate(R.layout.history_category, parent, false);
 * } }
 */