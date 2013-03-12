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

public class Level1View extends View {
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
	// For game elements
	private Paint textColor;
	private int scoreX;
	private int scoreY;
	private int score = 0;
	// Game specific variables
	private int wallHits = 0;
	// Enemy Ball
	private float enemyRadius = 20; 
	private float enemyX = enemyRadius + 100; 
	private float enemyY = enemyRadius + 100;
	private float enemySpeedX;
	private float enemySpeedY;
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
		Random rand = new Random();
		ballX = rand.nextInt(xMax);
		ballY = rand.nextInt(yMax);
		scoreX = w/3;
		scoreY = h/2;
	}

	public void onDraw(Canvas canvas){
		// Draw game text elements
		textColor.setColor(Color.BLACK);
		textColor.setAlpha(60);
		textColor.setTextSize(50);
		String score_str = "Score: " + Integer.toString(score);
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
			wallCollision(this);
		} else if (ballX - ballRadius < xMin) {
			ballSpeedX = -ballSpeedX;
			ballX = xMin+ballRadius;
			wallCollision(this);
		}
		// Detect Wall Collision on vertical plane
		if (ballY + ballRadius > yMax) {
			ballSpeedY = -ballSpeedY;
			ballY = yMax - ballRadius;
			wallCollision(this);
		} else if (ballY - ballRadius < yMin) {
			ballSpeedY = -ballSpeedY;
			ballY = yMin + ballRadius;
			wallCollision(this);
		}
	}

	public void wallCollision(View view){
		wallHits += 1;
	}

	public void updateEnemy(){
		if (wallHits == 1){
			enemySpeedX = 5;
			enemySpeedY = 3;
		} else if (wallHits == 25){
			enemySpeedX = 8;
			enemySpeedY = 5;
		} else if (wallHits == 25){
			enemySpeedX = 10;
			enemySpeedY = 8;
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
		ballSpeedX = 0;
		ballSpeedY = 0;
		ballRadius = 0;
		enemySpeedX = 0;
		enemySpeedY = 0;
		enemyRadius = 0;

	}
	
	public void enemyWallCollision(){
		score += 1;
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
