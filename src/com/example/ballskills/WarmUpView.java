package com.example.ballskills;

import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class WarmUpView extends View {
	// Bounds of screen
	private int xMin = 0;
	private int yMin = 0;
	private int xMax;
	private int yMax;
	// Ball attributes
	private float ballRadius = 30;
	private float ballX = ballRadius + 20;
	private float ballY = ballRadius + 40;
	private float ballSpeedX = 5;
	private float ballSpeedY = 3;
	private float maxSpeed = 20;
	private RectF ballBounds;
	private Paint ballColor;
	// For touch input
	private float previousX;
	private float previousY;
	private float scalor = 5.0f;
	// For scoring wall
	private int barX = 0;
	private int barY = 0;
	private int barThickness = 8;
	private String hotWall = "left";
	private RectF barBounds;
	private Paint barColor;
	// For game elements
	private Paint textColor;
	private int scoreX;
	private int scoreY;
	private int score = 0;
	private int maxScore = 100;

	public WarmUpView(Context context){
		super(context);
		ballBounds = new RectF();
		ballColor = new Paint();
		barBounds = new RectF();
		barColor = new Paint();
		textColor = new Paint();
		this.setFocusableInTouchMode(true);
	}

	// When view is first created (or changed) set ball's X & Y max and position. 
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH){
		xMax = w-1;
		yMax = h-1;
		Random rand = new Random();
		ballX = rand.nextInt(xMax);
		ballY = rand.nextInt(yMax);
		barBounds.set(barX, barY, barThickness, yMax+1);
		scoreX = w/3;
		scoreY = h/2;
	}

	public void onDraw(Canvas canvas){
		// Draw game text elements
		textColor.setColor(Color.BLACK);
		textColor.setAlpha(60);
		textColor.setTextSize(50);
		String score_str = "Score: " + Integer.toString(score) + "/" + maxScore;
		canvas.drawText(score_str, scoreX, scoreY, textColor);
		// Draw ball
		ballBounds.set(ballX-ballRadius, ballY-ballRadius, ballX+ballRadius, ballY+ballRadius);
		ballColor.setColor(Color.GREEN);
		canvas.drawOval(ballBounds, ballColor);
		// Draw scoring bar (set to left initially in onSizeChanged())
		barColor.setColor(Color.RED);
		canvas.drawRect(barBounds, barColor);


		// Perform position calculations
		update();

		// Delay for the old human eyes to catch up
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) { }

		invalidate();
	}

	public void update() {
		// ball x & y change based on ball speed
		ballX += ballSpeedX;
		ballY += ballSpeedY;

		// Detect Wall Collision on horizontal plane
		if (ballX + ballRadius > xMax) {
			ballSpeedX = -ballSpeedX;
			ballX = xMax-ballRadius;
			wallCollision(this, "right");
		} else if (ballX - ballRadius < xMin) {
			ballSpeedX = -ballSpeedX;
			ballX = xMin+ballRadius;
			wallCollision(this, "left");
		}
		// Detect Wall Collision on vertical plane
		if (ballY + ballRadius > yMax) {
			ballSpeedY = -ballSpeedY;
			ballY = yMax - ballRadius;
			wallCollision(this, "bottom");
		} else if (ballY - ballRadius < yMin) {
			ballSpeedY = -ballSpeedY;
			ballY = yMin + ballRadius;
			wallCollision(this, "top");
		}
	}

	private void wallCollision(View view, String wall){
		if (wall == hotWall){
			score += 1;
			boolean keepGoing = true;
			while(keepGoing) {
				Random rand = new Random();
				int newHotWall = rand.nextInt(4);
				if ((newHotWall==0) && (hotWall != "left")){
					hotWall = "left";
					barBounds.set(barX, barY, barThickness, yMax+1);
					keepGoing = false;
				} else if ((newHotWall==1) && (hotWall != "top")){
					hotWall = "top";
					barBounds.set(barX, barY, xMax+1, barThickness);
					keepGoing = false;
				} else if ((newHotWall==2) && (hotWall != "right")){
					hotWall = "right";
					barBounds.set(xMax-barThickness, yMin, xMax+1, yMax+1);
					keepGoing = false;
				} else if ((newHotWall==3) && (hotWall != "bottom")){
					hotWall = "bottom";
					barBounds.set(xMin, yMax-barThickness, xMax+1, yMax+1);
					keepGoing = false;
				}
			}
			if (score == 15){
				Toast.makeText(getContext(), "You're ready", Toast.LENGTH_SHORT).show();
			} else if ((score >= 16) && (score < maxScore)){
				ballRadius += 10;
			} else if (score > maxScore){
				ballRadius = 0;
				ballSpeedX = 0;
				ballSpeedY = 0;
				barThickness = 0;
				// Alert Dialog 
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
				alertDialog.setTitle("Warm Up Complete");
				alertDialog.setMessage("Nice work. The fun is just beginning.").setCancelable(false);
				alertDialog.setPositiveButton("Level 1", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Context context = getContext();
						Intent intent = new Intent(context, Level1.class);
						context.startActivity(intent);
					}
				});
				alertDialog.show();
			}
		}
	}

	// Touch-input handler
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float currentX = event.getX();
		float currentY = event.getY();
		float deltaX, deltaY;
		float scalingFactor = scalor / ((xMax > yMax) ? yMax : xMax);
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
			ballSpeedX += deltaX * scalingFactor;
			ballSpeedY += deltaY * scalingFactor;
		}
		// reset to max speed if over
		radarGun();

		// Save current x, y
		previousX = currentX;
		previousY = currentY;
		return true; 
	}

	public void radarGun(){
		if (ballSpeedX > 0){
			ballSpeedX = ((ballSpeedX > maxSpeed) ? maxSpeed : ballSpeedX);
		} else if (ballSpeedX < 0){
			ballSpeedX = ((ballSpeedX < -maxSpeed) ? -maxSpeed : ballSpeedX);
		}
		if (ballSpeedY > 0){
			ballSpeedY = ((ballSpeedY > maxSpeed) ? maxSpeed : ballSpeedY);
		} else if (ballSpeedX < 0){
			ballSpeedY = ((ballSpeedY < -maxSpeed) ? -maxSpeed : ballSpeedY);
		}
	}
}
