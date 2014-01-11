package com.assassin.expensemanager;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddExpenseActivity extends Activity {

	private Button btnAdd, btnCancel;
	ImageView btnDelete;
	private EditText etxtDescription, etxtAmount, etxtDate;
	private Spinner spinCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expense);

		// Initializing Views
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnDelete = (ImageView) findViewById(R.id.btnDelete);

		etxtAmount = (EditText) findViewById(R.id.etxtAmount);
		etxtDate = (EditText) findViewById(R.id.etxtDate);
		etxtDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					etxtDate.performClick();
				}

			}
		});
		etxtDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(
						AddExpenseActivity.this, new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								etxtDate.setText(dayOfMonth + "/"
										+ (monthOfYear + 1) + "/" + year);

							}
						}, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
						date.get(Calendar.DAY_OF_MONTH));
				dialog.show();
				etxtAmount.requestFocus();
			}
		});

		etxtDescription = (EditText) findViewById(R.id.etxtDescription);
		etxtDescription.setMaxWidth(etxtDescription.getWidth() + 5);
		spinCategory = (Spinner) findViewById(R.id.spinCategory);
		spinCategory.setAdapter(new AddCategoryAdapter(this,
				MainActivity.expensesDb.getReadableDatabase().rawQuery(
						"Select * from " + ExpenseDBHelper.tableNameCategories,
						null), true));

		// Settings Listeners
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String category;
				// Adding the contents to Database;
				if (etxtDate.getText().equals("")
						|| etxtDate.getText().equals(null)) {
					Toast.makeText(getApplicationContext(), "Select Date",
							Toast.LENGTH_LONG).show();
					etxtAmount.requestFocus();
					return;
				}
				try {
					Double.parseDouble(etxtAmount.getText().toString());
					category = ((TextView) ((View) (spinCategory
							.getSelectedView()))
							.findViewById(R.id.txtAddCategory)).getText()
							.toString();
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), "Invalid Amount",
							Toast.LENGTH_LONG).show();
					etxtAmount.requestFocus();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					category = "Food";
				}

				ContentValues cv = new ContentValues();

				cv.put(ExpenseDBHelper.columnsExpenses[0], etxtDate.getText()
						.toString());
				cv.put(ExpenseDBHelper.columnsExpenses[1], category);
				cv.put(ExpenseDBHelper.columnsExpenses[2], etxtDescription
						.getText().toString());
				cv.put(ExpenseDBHelper.columnsExpenses[3], etxtAmount.getText()
						.toString());

				SQLiteDatabase expenses = MainActivity.expensesDb
						.getWritableDatabase();

				int id = Integer.parseInt((String) getIntent().getStringExtra(
						MainActivity.ID));

				Cursor c = expenses.rawQuery("Select * from "
						+ ExpenseDBHelper.tableNameExpenses + " where _id = "
						+ id, null);

				try {
					c.moveToFirst();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (c.getCount() != 0) {
					expenses.update(ExpenseDBHelper.tableNameExpenses, cv,
							"_id=" + id, null);
					Toast.makeText(getApplicationContext(), "Updated",
							Toast.LENGTH_SHORT).show();
				} else {
					expenses.insert(ExpenseDBHelper.tableNameExpenses,
							ExpenseDBHelper.columnsExpenses[2], cv);
					Toast.makeText(getApplicationContext(),
							"Record successfully added", Toast.LENGTH_SHORT)
							.show();
				}

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

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = Integer.parseInt((String) getIntent().getStringExtra(
						MainActivity.ID));

				Cursor c = MainActivity.expensesDb.getWritableDatabase()
						.rawQuery(
								"Select * from "
										+ ExpenseDBHelper.tableNameExpenses
										+ " where _id = " + id, null);

				try {
					c.moveToFirst();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (c.getCount() != 0) {
					MainActivity.expensesDb.getWritableDatabase().delete(
							ExpenseDBHelper.tableNameExpenses, "_id=" + id,
							null);
					Toast.makeText(getApplicationContext(), "Record Deleted",
							Toast.LENGTH_SHORT).show();
				}
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		String rid = "";
		btnDelete.setClickable(false);
		try {
			Intent received = getIntent();
			rid = received.getStringExtra(MainActivity.ID);

			if (!rid.equals("0")) {
				btnDelete.setClickable(true);
				Cursor c = MainActivity.expensesDb.getReadableDatabase()
						.rawQuery(
								"Select * from "
										+ ExpenseDBHelper.tableNameExpenses
										+ " where _id = "
										+ (Integer.parseInt(rid)), null);
				c.moveToFirst();
				etxtAmount.requestFocus();
				etxtAmount.setText(c.getDouble(4) + "");
				etxtDescription.setText(c.getString(3));
				etxtDate.setText(c.getString(1));
			}
		} catch (Exception e) {
			rid = "" + 0;
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
		return super.onOptionsItemSelected(item);
	}

}

class AddCategoryAdapter extends CursorAdapter {
	LayoutInflater inflater;

	public AddCategoryAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtAddCategory = (TextView) view
				.findViewById(R.id.txtAddCategory);
		View viewAddCategoryColor = view
				.findViewById(R.id.viewAddCategoryColor);

		txtAddCategory.setText(cursor.getString(1));
		viewAddCategoryColor.setBackgroundColor(cursor.getInt(2));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.add_catergory, parent, false);
	}

}