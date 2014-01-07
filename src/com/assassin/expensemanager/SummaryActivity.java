package com.assassin.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SummaryActivity extends Activity {

	private TextView txtTotalExpense, txtTotalSavings, txtMonthlySavings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		txtTotalExpense = (TextView) findViewById(R.id.txtTotalExpense);
		txtTotalSavings = (TextView) findViewById(R.id.txtTotalSavings);
		txtMonthlySavings = (TextView) findViewById(R.id.txtMonthlySavings);

		txtTotalSavings.setText("Rs. 1000");
		txtMonthlySavings.setText("Rs. 400");
		
		try {
			String sql = "Select * from " + ExpenseDBHelper.tableName;
			Cursor cursor = MainActivity.expensesDb.getReadableDatabase().rawQuery(sql, null);
			double expense = 0;
			while(cursor.moveToNext()){
				expense += cursor.getDouble(4);
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
		case R.id.settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
