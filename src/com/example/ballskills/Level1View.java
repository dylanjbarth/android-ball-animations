package com.example.ballskills;

import java.util.Random;

import android.content.Context;
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
	private float maxSpeed = 10;
	private RectF ballBounds;
	private Paint ballColor;
	// For touch input
	private float previousX;
	private float previousY;

	public Level1View(Context context){
		super(context);
		ballBounds = new RectF();
		ballColor = new Paint();
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
	}

	public void onDraw(Canvas canvas){
		// Draw ball
		ballBounds.set(ballX-ballRadius, ballY-ballRadius, ballX+ballRadius, ballY+ballRadius);
		ballColor.setColor(Color.GREEN);
		canvas.drawOval(ballBounds, ballColor);

		// Perform position calculations
		update();
		System.out.print("ballSpeedX: ");
		System.out.println(ballSpeedX);
		System.out.print("ballSpeedY: ");
		System.out.println(ballSpeedY);
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
		} else if (ballX - ballRadius < xMin) {
			ballSpeedX = -ballSpeedX;
			ballX = xMin+ballRadius;
		}
		// Detect Wall Collision on vertical plane
		if (ballY + ballRadius > yMax) {
			ballSpeedY = -ballSpeedY;
			ballY = yMax - ballRadius;
		} else if (ballY - ballRadius < yMin) {
			ballSpeedY = -ballSpeedY;
			ballY = yMin + ballRadius;
		}
	}

	// Touch-input handler
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float currentX = event.getX();
		float currentY = event.getY();
		float deltaX, deltaY;
		float scalingFactor = 5.0f / ((xMax > yMax) ? yMax : xMax);
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
