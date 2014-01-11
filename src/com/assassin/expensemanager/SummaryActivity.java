package com.assassin.expensemanager;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SummaryActivity extends Activity {

	private TextView txtTotalExpense, txtMonthSummary;
	private ListView lvSummaryDetails;
	private String[] months = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
			"December" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		Calendar currentDate = Calendar.getInstance();
		txtTotalExpense = (TextView) findViewById(R.id.txtTotalExpense);
		txtMonthSummary = (TextView) findViewById(R.id.txtMonthSummary);
		txtMonthSummary.setText(months[currentDate.get(Calendar.MONTH)]);
		lvSummaryDetails = (ListView) findViewById(R.id.lvSummaryDetails);

		try {
			String sql = "Select Date, Amount from "
					+ ExpenseDBHelper.tableNameExpenses + " where Date >= \'1/"
					+ currentDate.get(Calendar.MONTH) + "/"
					+ currentDate.get(Calendar.YEAR) + "\' and Date <= \'31"
					+ currentDate.get(Calendar.MONTH) + "/"
					+ currentDate.get(Calendar.YEAR);
			Cursor cursor = MainActivity.expensesDb.getReadableDatabase()
					.rawQuery(sql, null);

			double expense = 0;
			while (cursor.moveToNext()) {
				expense += cursor.getDouble(1);
			}
			txtTotalExpense.setText("Rs. " + expense);

		} catch (Exception e) {
			e.printStackTrace();
		}
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

class SummaryDetailsAdapter extends CursorAdapter {

	LayoutInflater inflater;

	public SummaryDetailsAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtCategoryNameSummary = (TextView) view
				.findViewById(R.id.txtCategoryNameSummary);
		TextView txtCategoryAmountSummary = (TextView) view
				.findViewById(R.id.txtCategoryAmountSummary);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		return inflater.inflate(R.layout.summary_list, parent, false);
	}
}