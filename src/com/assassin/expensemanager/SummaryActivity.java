package com.assassin.expensemanager;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SummaryActivity extends Activity {

	private Calendar currentDate;
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
		currentDate = Calendar.getInstance();
		txtTotalExpense = (TextView) findViewById(R.id.txtTotalExpense);
		txtMonthSummary = (TextView) findViewById(R.id.txtMonthSummary);
		txtMonthSummary.setText(months[currentDate.get(Calendar.MONTH)]);
		lvSummaryDetails = (ListView) findViewById(R.id.lvSummaryDetails);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Declaring array to perform Summary Calculation
		String[] categories;
		double[] amounts;
		int[] percentages;
		int[] colors;

		try {

			// String sqlr = "select * from " +
			// ExpenseDBHelper.tableNameExpenses;
			String sql = "Select * from " + ExpenseDBHelper.tableNameExpenses
					+ " where (Date >= \'" + currentDate.get(Calendar.YEAR)
					+ "-" + (currentDate.get(Calendar.MONTH) + 1) + "-"
					+ "1\' and Date <= \'" + currentDate.get(Calendar.YEAR)
					+ "-" + (currentDate.get(Calendar.MONTH) + 1) + "-"
					+ "31\')";
			Cursor cursor = MainActivity.expensesDb.getReadableDatabase()
					.rawQuery(sql, null);

			// Calculating total monthly expense and setting its value to the
			// textView
			double expense = 0;
			while (cursor.moveToNext())
				expense += cursor.getDouble(4);
			txtTotalExpense.setText("Rs. " + expense);

			// Setting up a cursor to get categories from the database
			Cursor category = MainActivity.expensesDb
					.getReadableDatabase()
					.rawQuery(
							"Select * from "
									+ ExpenseDBHelper.tableNameCategories, null);

			// Setting up the memory for the arrays defined for calculations
			int size = category.getCount();
			Log.d("Size: ", cursor.getCount() + "");
			categories = new String[size];
			amounts = new double[size];
			colors = new int[size];
			percentages = new int[size];

			// Initializing Amount array
			for (int j = 0; j < size; j++) {
				amounts[j] = 0;
			}

			// Initializing Percentage array.
			for (int j = 0; j < size; j++) {
				percentages[j] = 0;
			}

			// Performing required Calculations
			// Setting the strings and colors
			int i = 0;
			while (category.moveToNext()) {
				categories[i] = category.getString(1);
				colors[i] = category.getInt(2);
				i++;
			}

			cursor.moveToPosition(-1);

			// Calculating amount for each category
			while (cursor.moveToNext()) {
				i = 0;
				while (i < size && !(cursor.getString(2).equals(categories[i]))){
					i++;
				}
				amounts[i] += cursor.getDouble(4);
				Log.d("SummaryActivity amount: ", amounts[i] + "");
			}

			for (int j = 0; j < size; j++)
				Log.d("SummaryActivityAmounts " + categories[j], amounts[j]
						+ "");

			// Calculating percentages for each category
			double max = amounts[0];
			for (int j = 1; j < amounts.length; j++) {
				if (max < amounts[j])
					max = amounts[j];
			}
			for (int j = 0; j < percentages.length; j++)
				percentages[j] = (int) ((amounts[j] / max) * 100);

			for (int j = 0; j < amounts.length; j++)
				Log.d("SummaryActivityAmounts" + categories[j], percentages[j]
						+ "");

			// Creating set of objects to feed the lvSummaryDetails
			int count = 0;
			for (int j = 0; j < amounts.length; j++)
				if (amounts[j] > 0)
					count++;
			SummarySet[] sets = new SummarySet[count];
			count = 0;
			for (int j = 0; j < percentages.length; j++) {
				if (amounts[j] > 0)
					sets[count++] = new SummarySet(percentages[j], amounts[j],
							categories[j], colors[j]);
			}

			lvSummaryDetails.setAdapter(new SummaryDetailsAdapter(
					getApplicationContext(), R.layout.summary_list, sets));

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

class SummarySet {
	private int percentage, color;
	private double amount;
	private String category;

	public SummarySet(int p, double a, String c, int color) {
		percentage = p;
		amount = a;
		category = c;
		this.color = color;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}

class SummaryDetailsAdapter extends ArrayAdapter<SummarySet> {

	SummarySet[] set;

	public SummaryDetailsAdapter(Context context, int resource,
			SummarySet[] objects) {
		super(context, resource, objects);
		set = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.summary_list, parent, false);
		}

		TextView txtCategoryNameSummary = (TextView) row
				.findViewById(R.id.txtCategoryNameSummary);
		TextView txtCategoryAmountSummary = (TextView) row
				.findViewById(R.id.txtCategoryAmountSummary);
		TextView viewBar = (TextView) row.findViewById(R.id.viewBar);
		TextView viewBarBorder = (TextView) row
				.findViewById(R.id.viewBarBorder);
		TextView viewMarginBar = (TextView) row
				.findViewById(R.id.viewMarginBar);

		int percentage = set[position].getPercentage();
		double width = (int) (((parent.getWidth()) * (percentage)) / 100);
		double amount = set[position].getAmount();
		int color = set[position].getColor();
		if (width > 40)
			width -= 40;

		if (amount > 0) {

			txtCategoryNameSummary.setText(set[position].getCategory() + "");
			txtCategoryAmountSummary
					.setText("Rs. " + set[position].getAmount());
			txtCategoryAmountSummary.setTextColor(color);
			txtCategoryNameSummary.setTextColor(color);
			viewBar.setWidth((int) (width - 2));
			viewBar.setBackgroundColor(color);
			viewBarBorder.setWidth((int) width);

		} else {
			viewBar.setHeight(0);
			viewBar.setWidth(0);
			viewBarBorder.setWidth(0);
			viewBarBorder.setHeight(0);
			viewMarginBar.setHeight(0);
			viewMarginBar.setWidth(0);
		}

		return row;
	}

}

/*
 * class SummaryDetailsAdapter extends CursorAdapter {
 * 
 * LayoutInflater inflater;
 * 
 * public SummaryDetailsAdapter(Context context, Cursor c, boolean autoRequery)
 * { super(context, c, autoRequery); inflater = LayoutInflater.from(context); }
 * 
 * @Override public void bindView(View view, Context context, Cursor cursor) {
 * TextView txtCategoryNameSummary = (TextView) view
 * .findViewById(R.id.txtCategoryNameSummary); TextView txtCategoryAmountSummary
 * = (TextView) view .findViewById(R.id.txtCategoryAmountSummary);
 * 
 * }
 * 
 * @Override public View newView(Context context, Cursor cursor, ViewGroup
 * parent) {
 * 
 * return inflater.inflate(R.layout.summary_list, parent, false); } }
 */

