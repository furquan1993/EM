package com.assassin.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity {

	private TextView txtHistory;
	private ListView lvHistoryDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// Initializing Views
		txtHistory = (TextView) findViewById(R.id.txtHistory);
		lvHistoryDetails = (ListView) findViewById(R.id.lvHistoryDetails);

		// Initializing DBs and cursors
		SQLiteDatabase expenses = MainActivity.expensesDb.getReadableDatabase();
		String sql = "Select Date, category, description, amount from " + ExpenseDBHelper.tableName;
		Cursor cursor = expenses.rawQuery(sql, null);

		// Setting Views
		try {
			showText(cursor);
			showList(cursor);
		} catch (Exception e) {
			Log.d("HistoryActivity", e.getStackTrace().toString());
			txtHistory.setText(e.getStackTrace().toString());
		}

	}

	private void showText(Cursor cursor) {
		StringBuilder txt = new StringBuilder("");
		cursor.moveToFirst();
		do {
			int id = cursor.getInt(0);
			String date = cursor.getString(1);
			String category = cursor.getString(2);
			String desc = cursor.getString(3);
			double amount = cursor.getDouble(4);
			txt.append(id + " " + date + " " + category + " " + desc + " "
					+ amount + "\n");
			txtHistory.setText(txt);

		} while (cursor.moveToNext());

	}

	private void showList(Cursor cursor) {

		String[] fromColumns = new String[4];
		for (int i = 0; i < fromColumns.length; i++) {
			fromColumns[i] = ExpenseDBHelper.columns[i];
		}
		int[] toViews = { R.id.txtHistoryDate, R.id.txtHistoryCategory,
				R.id.txtHistoryDescription, R.id.txtHistoryAmount };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.history_row, cursor, fromColumns, toViews, 0);
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
		case R.id.settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
