package com.assassin.expensemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnAddExpense, btnHistory, btnSummary, btnLimits, btnExit;
	static ExpenseDBHelper expensesDb;
	static final String ID = "com.assassin.expensemanager.id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAddExpense = (Button) findViewById(R.id.btnAddExpense);
		btnHistory = (Button) findViewById(R.id.btnHistory);
		btnSummary = (Button) findViewById(R.id.btnSummary);
		btnLimits = (Button) findViewById(R.id.btnLimits);
		btnExit = (Button) findViewById(R.id.btnExit);

		expensesDb = new ExpenseDBHelper(this);

		SelectActivity listener = new SelectActivity();
		btnAddExpense.setOnClickListener(listener);
		btnHistory.setOnClickListener(listener);
		btnSummary.setOnClickListener(listener);
		btnLimits.setOnClickListener(listener);
		btnExit.setOnClickListener(listener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * public void openSettings(MenuItem item) { Intent settings = new
	 * Intent(this, SettingsActivity.class); startActivity(settings); }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == (R.id.settings)) {
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			return true;
		} else if (item.getItemId() == (R.id.clear)) {
			expensesDb.onUpgrade(expensesDb.getWritableDatabase(), 1, 2);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			expensesDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			expensesDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class SelectActivity implements OnClickListener {

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnAddExpense) {
			Intent intent = new Intent(v.getContext(), AddExpenseActivity.class);
			intent.putExtra(MainActivity.ID, "0");
			v.getContext().startActivity(intent);
		} else if (id == R.id.btnSummary) {
			Intent intent = new Intent(v.getContext(), SummaryActivity.class);
			v.getContext().startActivity(intent);
		} else if (id == R.id.btnHistory) {
			Intent intent = new Intent(v.getContext(), HistoryActivity.class);
			v.getContext().startActivity(intent);
		} else if (id == R.id.btnLimits) {
			Intent intent = new Intent(v.getContext(), CategoryActivity.class);
			v.getContext().startActivity(intent);
		} else if (id == R.id.btnExit) {
			System.exit(0);
		}

	}

}
