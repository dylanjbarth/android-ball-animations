package com.example.ballskills;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class Level1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level1);
		// Alert Dialog 
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Warm Up");
		alertDialog.setMessage("Bounce around, have fun, prepare yo'self. Level 1 starts at 25.").setCancelable(false);
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				View level1View = new Level1View(Level1.this);
				setContentView(level1View);
			}
		});
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_level1, menu);
		return true;
	}

}
