package com.assassin.expensemanager;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private TextView txtReminderTime;
	//private Time currentTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_settings);
		// this.setContentView(R.xml.settings);
		setContentView(R.layout.activity_settings);
		
		//String time = currentTime.toString();
		try {
			txtReminderTime = (TextView) findViewById(R.id.txtReminderTime);	
			txtReminderTime.setText(new Date().toString());
		} catch (Exception e) {
		}
	}

}
