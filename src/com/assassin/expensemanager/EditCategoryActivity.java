package com.assassin.expensemanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditCategoryActivity extends Activity {

	private Spinner spinCategoryColor;
	private Button btnAdd, btnCancel, btnDelete;
	private EditText etxtCategoryName;
	private SQLiteDatabase expenseDb;
	static final Integer colors[] = { Color.parseColor("#FFF700"),
			Color.parseColor("#FF3700"), Color.parseColor("#FF8000") };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_category);

		// Initializing Views
		spinCategoryColor = (Spinner) findViewById(R.id.spinCategoryColor);
		btnAdd = (Button) findViewById(R.id.btnAddEditCategory);
		btnCancel = (Button) findViewById(R.id.btnCancelEditCategory);
		btnDelete = (Button) findViewById(R.id.btnDeleteCategory);
		etxtCategoryName = (EditText) findViewById(R.id.etxtCategoryName);
		expenseDb = MainActivity.expensesDb.getWritableDatabase();
		spinCategoryColor.setPrompt("Select Color");

		// Setting the adapter to select color from
		CategoryColorAdapter adapter = new CategoryColorAdapter(
				getApplicationContext(), R.layout.category_color, colors);
		spinCategoryColor.setAdapter(adapter);

		final String name = getIntent().getStringExtra(
				CategoryActivity.categoryName);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int color = (int) spinCategoryColor.getSelectedItemId();

				if (etxtCategoryName.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Enter a name",
							Toast.LENGTH_LONG).show();
					return;
				}

				ContentValues cv = new ContentValues();
				cv.put(ExpenseDBHelper.columnsCategories[1], etxtCategoryName.getText().toString());
				cv.put(ExpenseDBHelper.columnsCategories[2], color);

				Cursor cursor = expenseDb.rawQuery("Select * from "
						+ ExpenseDBHelper.tableNameCategories + " where "
						+ ExpenseDBHelper.columnsCategories[1] + "=\'" + name
						+ "\'", null);
				if (cursor.getCount() > 0) {
					Toast.makeText(getApplicationContext(), "Updated",
							Toast.LENGTH_SHORT).show();
					expenseDb.update(ExpenseDBHelper.tableNameCategories, cv,
							ExpenseDBHelper.columnsCategories[1] + "=" + "\'"
									+ name + "\'", null);
				} else {
					Toast.makeText(getApplicationContext(), "Category Created",
							Toast.LENGTH_SHORT).show();
					expenseDb.insert(ExpenseDBHelper.tableNameCategories, null,
							cv);
				}
				finish();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Cancelled",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Delete Successful",
						Toast.LENGTH_SHORT).show();
				expenseDb
						.delete(ExpenseDBHelper.tableNameCategories,
								ExpenseDBHelper.columnsCategories[1] + "="
									+ "\'"	+ name + "\'", null);
				finish();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		expenseDb = MainActivity.expensesDb.getWritableDatabase();
		String name = getIntent().getStringExtra(CategoryActivity.categoryName);
		int color = getIntent().getIntExtra(CategoryActivity.categoryColor, 0);

		if (color != 0) {
			Cursor cursor = expenseDb.rawQuery("Select * from "
					+ ExpenseDBHelper.tableNameCategories, null);
			etxtCategoryName.setText(name);
			etxtCategoryName.requestFocus();
			btnAdd.setText("Update");
			btnDelete.setClickable(true);
			while (cursor.moveToNext()) {
				int count = 0;
				while (color != colors[count]) {
					count++;
				}
				spinCategoryColor.setSelection(count);
				break;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			expenseDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

}

class CategoryColorAdapter extends ArrayAdapter<Integer> {

	Integer colors[];

	public CategoryColorAdapter(Context context, int resource, Integer[] objects) {
		super(context, resource, objects);
		colors = objects;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.category_color, parent, false);
		}

		TextView viewColor = (TextView) row.findViewById(R.id.viewColor);
		int color = colors[position];
		viewColor.setBackgroundColor(color);

		return row;
	}

	@Override
	public long getItemId(int position) {
		return colors[position];
	}

	@Override
	public int getPosition(Integer item) {
		return super.getPosition(item);
	}

}