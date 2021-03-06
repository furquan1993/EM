package com.assassin.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryActivity extends Activity {

	private ListView lvCategory;
	private SQLiteDatabase expenseDb;
	static final String categoryColor = "CategoryActivity.Color";
	static final String categoryName = "CategoryActivity.Name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		lvCategory = (ListView) findViewById(R.id.lvCategory);

		expenseDb = MainActivity.expensesDb.getReadableDatabase();
		Cursor cursor = expenseDb.rawQuery("Select * from "
				+ ExpenseDBHelper.tableNameCategories, null);

		lvCategory.setAdapter(new CategoryAdaper(getApplicationContext(),
				cursor, true));
		lvCategory.setClickable(true);

		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				TextView tvColor = (TextView) view
						.findViewById(R.id.viewCategoryColor);
				TextView tvName = (TextView) view
						.findViewById(R.id.txtCategoryName);
				int color = (Integer) tvColor.getTag();
				String name = tvName.getText().toString();

				Intent intent = new Intent(CategoryActivity.this,
						EditCategoryActivity.class);
				intent.putExtra(categoryName, name);
				intent.putExtra(categoryColor, color);
				startActivity(intent);
			}
		});
	}

	public void CreateNew(View view) {
		Intent intent = new Intent(this, EditCategoryActivity.class);
		intent.putExtra(categoryColor, 0);
		intent.putExtra(categoryName, "");
		startActivity(intent);
	}

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

	@Override
	protected void onResume() {
		super.onResume();
		expenseDb = MainActivity.expensesDb.getReadableDatabase();
		Cursor cursor = expenseDb.rawQuery("Select * from "
				+ ExpenseDBHelper.tableNameCategories, null);
		lvCategory.setAdapter(new CategoryAdaper(getApplicationContext(),
				cursor, true));

	}

}

class CategoryAdaper extends CursorAdapter {

	LayoutInflater inflater;

	public CategoryAdaper(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtCategoryName = (TextView) view
				.findViewById(R.id.txtCategoryName);
		TextView viewCategoryColor = (TextView) view
				.findViewById(R.id.viewCategoryColor);

		txtCategoryName.setText(cursor.getString(1).toString());
		viewCategoryColor.setBackgroundColor(cursor.getInt(2));
		viewCategoryColor.setTag(cursor.getInt(2));

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.category_row, parent, false);
	}
}