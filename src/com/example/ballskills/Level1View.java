package com.example.ballskills;

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

public class Level1View extends View {
	// Bounds of screen
	private int xMin = 0;
	private int yMin = 0;
	private int xMax;
	private int yMax;
	// Ball attributes
	private float ballRadius = 30;
	private float ballX = 50;
	private float ballY = 50;
	private float ballSpeedX = 5;
	private float ballSpeedY = 3;
	private float maxSpeed = 20;
	private RectF ballBounds;
	private Paint ballColor;
	// For touch input
	private float previousX;
	private float previousY;
	private float scalor = 5.0f;
	// For game elements
	private Paint textColor;
	private int scoreX;
	private int scoreY;
	private int score = 0;
	private int maxScore = 30;
	// Enemy Ball
	private float enemyRadius = 30; 
	private float enemyX = 200;
	private float enemyY = 300;
	private float enemySpeedX = 3;
	private float enemySpeedY = 5;
	private RectF enemyBallBounds; 
	private Paint enemyColor;

	public Level1View(Context context){
		super(context);
		// Initialize game elements
		ballBounds = new RectF();
		enemyBallBounds = new RectF();
		ballColor = new Paint();
		enemyColor = new Paint();
		textColor = new Paint();
		this.setFocusableInTouchMode(true);
	}

	// When view is first created (or changed) set ball's X & Y max and position. 
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH){
		xMax = w-1;
		yMax = h-1;
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
		// Draw enemy
		enemyBallBounds.set(enemyX - enemyRadius, enemyY - enemyRadius, enemyX + enemyRadius, enemyY + enemyRadius);
		enemyColor.setColor(Color.RED);
		canvas.drawOval(enemyBallBounds, enemyColor);
		// Perform position calculations
		updateBall();
		updateEnemy();
		// Delay for the old human eyes to catch up
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) { }
		invalidate();
	}

	public void updateBall(){
		// ball x & y change based on ball speed
		ballX += ballSpeedX;
		ballY += ballSpeedY;

		// Detect Wall Collision on horizontal plane
		if (ballX + ballRadius > xMax) {
			ballSpeedX = -ballSpeedX;
			ballX = xMax-ballRadius;
			enemyWallCollision();
		} else if (ballX - ballRadius < xMin) {
			ballSpeedX = -ballSpeedX;
			ballX = xMin+ballRadius;
			enemyWallCollision();
		}
		// Detect Wall Collision on vertical plane
		if (ballY + ballRadius > yMax) {
			ballSpeedY = -ballSpeedY;
			ballY = yMax - ballRadius;
			enemyWallCollision();
		} else if (ballY - ballRadius < yMin) {
			ballSpeedY = -ballSpeedY;
			ballY = yMin + ballRadius;
			enemyWallCollision();
		}
	}


	public void updateEnemy(){
		if (score == maxScore+1){
			pauseBalls();
			// Alert Dialog 
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
			alertDialog.setTitle("Level 1 Complete!");
			alertDialog.setMessage("Congratulations! Now lets see how you like the blue balls...").setCancelable(false);
			alertDialog.setPositiveButton("Level 2", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Context context = getContext();
					Intent intent = new Intent(context, Level2.class);
					context.startActivity(intent);
				}
			});
			alertDialog.show();
		}
		// Update position
		enemyX += enemySpeedX;
		enemyY += enemySpeedY;
		// Detect wall collision and react
		if (enemyX + enemyRadius > xMax) {
			enemySpeedX = -enemySpeedX;
			enemyX = xMax - enemyRadius;
			enemyWallCollision();
		} else if (enemyX - enemyRadius < xMin) {
			enemySpeedX = -enemySpeedX;
			enemyX = xMin + enemyRadius;
			enemyWallCollision();
		}
		if (enemyY + enemyRadius > yMax) {
			enemySpeedY = -enemySpeedY;
			enemyY = yMax - enemyRadius;
			enemyWallCollision();
		} else if (enemyY - enemyRadius < yMin) {
			enemySpeedY = -enemySpeedY;
			enemyY = yMin + enemyRadius;
			enemyWallCollision();
		}
		// Check for ball collisions: help ==> http://devmag.org.za/2009/04/13/basic-collision-detection-in-2d-part-1/
		// Calculate how far centers of circle are from one another
		float diffX = ballX - enemyX;
		float diffY = ballY - enemyY;
		// Square root each difference squared
		float diff = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
		if (diff <= (ballRadius + enemyRadius)) {
			ballCollision(this);
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

	public void ballCollision(View view){
		pauseBalls();
		// Alert Dialog 
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
		alertDialog.setTitle("Crash!");
		alertDialog.setMessage("Pick yourself up grasshopper.").setCancelable(false);
		alertDialog.setPositiveButton("Restart...", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Context context = getContext();
				Intent intent = new Intent(context, Level1.class);
				context.startActivity(intent);
			}
		});
		alertDialog.show();
	}

	public void enemyWallCollision(){
		score += 1;
		scalor += .4;
		float increment = .2f;
		if (enemySpeedX >= 0){
			enemySpeedX += increment;
		} else {
			enemySpeedX -= increment;
		}
		if (enemySpeedY >= 0){
			enemySpeedX += increment;
		} else {
			enemySpeedY -= increment;
		}
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

	public void resetBalls(){
		ballRadius = 30;
		ballX = ballRadius;
		ballY = ballRadius;
		ballSpeedX = 3;
		ballSpeedY = 5;
		enemyRadius = ballRadius;
		enemyX = xMax/3;
		enemyY = yMax/3;
		enemySpeedX = 5;
		enemySpeedY = 3;
	}

	public void pauseBalls() {
		ballRadius = 0;
		ballX = ballRadius;
		ballY = ballRadius;
		ballSpeedX = 0;
		ballSpeedY = 0;
		enemyRadius = ballRadius;
		enemyX = 500;
		enemyY = 500;
		enemySpeedX = 0;
		enemySpeedY = 0;
	}
}
