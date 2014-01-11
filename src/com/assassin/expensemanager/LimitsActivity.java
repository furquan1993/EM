package com.assassin.expensemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class LimitsActivity extends Activity {

	private ListView lvLimits;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_limits);
		lvLimits = (ListView) findViewById(R.id.lvLimits);
		lvLimits.setAdapter(new LimitsAdapter(this, MainActivity.expensesDb
				.getReadableDatabase().rawQuery(
						"Select * from " + ExpenseDBHelper.tableNameLimits,
						null), true));
		lvLimits.setClickable(true);

		lvLimits.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				String limitName = ((TextView) view
						.findViewById(R.id.txtLimitName)).getText().toString();
				Log.d("LimitsActivity", limitName);
				new SetLimitDialog(LimitsActivity.this);
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

class LimitsAdapter extends CursorAdapter {

	private LayoutInflater inflater;

	public LimitsAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtLimitName = (TextView) view.findViewById(R.id.txtLimitName);
		TextView txtLimitAmount = (TextView) view
				.findViewById(R.id.txtLimitAmount);

		SeekBar seekLimit = (SeekBar) view.findViewById(R.id.seekLimit);
		txtLimitAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SetLimitDialog d = new SetLimitDialog(v.getContext());
				d.show();
				EditText etxt = (EditText) d.findViewById(R.id.etxtSetLimit);
				((TextView) v).setText(etxt.getText().toString());

			}
		});
		seekLimit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return true;
			}
		});
		seekLimit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				v.performClick();
			}
		});

		txtLimitName.setText(cursor.getString(1));
		txtLimitAmount.setText("Rs. " + cursor.getDouble(2));

		seekLimit.setProgress(getProgress(cursor));
	}

	private int getProgress(Cursor cursor) {
		int progress = 0;
		if (cursor.getDouble(2) == 0.0) {
			return progress;
		}
		double total = 0;
		try {
			Cursor expense = MainActivity.expensesDb.getReadableDatabase()
					.rawQuery(
							"Select * from "
									+ ExpenseDBHelper.tableNameExpenses, null);
			expense.moveToFirst();
			do {
				if (expense.getString(2).equals(cursor.getString(1))) {
					total += expense.getDouble(4);
				}
			} while (expense.moveToNext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		progress = (int) ((total / cursor.getDouble(2)) * 100);
		if (progress > 100)
			progress = 100;
		return progress;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.limits_row, parent, false);
	}

}

class SetLimitDialog extends Dialog {

	EditText etxtSetLimit;

	public SetLimitDialog(Context context) {
		super(context);
		this.setTitle(toString());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_limit_dialog);
		etxtSetLimit = (EditText) findViewById(R.id.etxtSetLimit);
	}

	public String toString() {
		return "Set Limit Dialog";
	}

	public String getLimit() {
		return etxtSetLimit.getText().toString();
	}
}
