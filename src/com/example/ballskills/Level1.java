package com.example.ballskills;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Level1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level1);
		View Level1View = new Level1View(this);
		setContentView(Level1View);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_level1, menu);
		return true;
	}

}
