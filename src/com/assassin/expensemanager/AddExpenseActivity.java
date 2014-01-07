package com.assassin.expensemanager;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddExpenseActivity extends Activity {

	private Button btnAdd, btnCancel;
	private EditText etxtDescription, etxtAmount, etxtDate;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expense);

		Calendar currentDate = Calendar.getInstance();

		// Initializing Views
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		etxtAmount = (EditText) findViewById(R.id.etxtAmount);
		etxtDate = (EditText) findViewById(R.id.etxtDate);
		etxtDate.setText(currentDate.get(Calendar.DATE) + "/"
				+ (currentDate.get(Calendar.MONTH) + 1) + "/"
				+ currentDate.get(Calendar.YEAR));
		etxtDescription = (EditText) findViewById(R.id.etxtDescription);
		etxtDescription.setMaxWidth(etxtDescription.getWidth() + 5);

		

		// Settings Listeners
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Adding the contents to Database;
				try {
					Double.parseDouble(etxtAmount.getText().toString());
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(),
							"Invalid Amount", Toast.LENGTH_LONG).show();
					etxtAmount.requestFocus();
					return;
				}

				
				ContentValues cv = new ContentValues();
				String date = etxtDate.getText().toString();
				
				cv.put(ExpenseDBHelper.columns[0], date);
				cv.put(ExpenseDBHelper.columns[1], "Food");
				cv.put(ExpenseDBHelper.columns[2], etxtDescription.getText().toString());
				cv.put(ExpenseDBHelper.columns[3], etxtAmount.getText().toString());

				SQLiteDatabase expenses = MainActivity.expensesDb.getWritableDatabase();

				expenses.insert(ExpenseDBHelper.tableName, ExpenseDBHelper.columns[2], cv);

				Toast.makeText(getApplicationContext(),
						"Record successfully added", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "No record created",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
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
		return super.onOptionsItemSelected(item);
	}
	
	

}



class Calculator extends View {

	public Calculator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}
}
