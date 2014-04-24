/*
 * Copyright (C) 2014 Limbika Assistive Technologies
 *
 * This library is dual-licensed: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as 
 * published by the Free Software Foundation. For the terms of this 
 * license, see licenses at
 * 
 * 		http://www.gnu.org/licenses/gpl-2.0.html
 *
 * You are free to use this library under the terms of the GNU General
 * Public License, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * Alternatively, you can license this library under a commercial
 * license, as set out in LICENSE.txt.
 */

package com.limbika.ahud;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.ViewGroup;

/**
 * View with the Accessibility HUD Axis.
 */
/* package */ class AhudView extends ViewGroup {
	
	//-------------------------------------------------------------------------
	// States
	private static final int	STATE_FIRST_LINE 	= 1;
	private static final int	STATE_SECOND_LINE 	= 2;
	private static final int	STATE_CROSS			= 3;
	private static final int	STATE_PAUSE			= 4;
	
	//-------------------------------------------------------------------------
	// Constants
	private static final float	RADIUS				= 4F;
	private static final int	COLOR 				= Color.parseColor("#0099cc");	// Holo Blue Dark
	private static final int 	STROKE_WIDTH 		= 2;
	private static final int 	STROKE_WIDTH_DART	= 4;
	private static final int	ALPHA_EMPTY			= 255;
	private static final int	ALPHA_HALF			= 128;
	private static final int	ALPHA_HALF_HALF		= 64;
	private static final int	DART_SIZE			= 8;
	
	//-------------------------------------------------------------------------
	// Members
	private int 				mState 				= STATE_FIRST_LINE;
	private int					mMovingAxis;
	private int 				mHorizontalDirectionFactor;
	private int 				mVerticalDirectionFactor;
	private float 				mHorizontalPosition;
	private float 				mVerticalPosition;
	
	private Paint				mPaintBox;
	private Paint				mPaintBoxInside;
	private Paint				mPaintDart;
	
	private AhudConfiguration	mConf;
	private Handler				mHandler 			= new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			invalidate();
		}
	};
	
	public AhudView(Context context, AhudConfiguration conf, PreState preState) {
		super(context);
		AHUD.debug("TRACE: HUDView.onCreate()");
		
        mPaintBox = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBox.setColor(COLOR);
        mPaintBox.setAlpha(ALPHA_EMPTY);
        mPaintBox.setStyle(Style.STROKE);
        mPaintBox.setStrokeWidth(STROKE_WIDTH);
        
        mPaintBoxInside = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBoxInside.setColor(COLOR);
        mPaintBoxInside.setAlpha(ALPHA_HALF_HALF);
        mPaintBoxInside.setStyle(Style.FILL);
        
        mPaintDart = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDart.setColor(COLOR);
        mPaintDart.setAlpha(ALPHA_EMPTY);
        mPaintDart.setStyle(Style.STROKE);
        mPaintDart.setStrokeWidth(STROKE_WIDTH_DART);

        /* Configuration */
		mConf = conf;
		mHorizontalDirectionFactor = 1;
		mVerticalDirectionFactor = 1;
		mMovingAxis = conf.getAxisInital();
		mVerticalPosition = conf.getVerticalStart() == AhudConfiguration.START_LEFT ? 0 : -1;
		mHorizontalPosition = conf.getHorizontalStart() == AhudConfiguration.START_TOP ? 0 : -1;
		if ( conf.isAxisRestater() && preState != null ) {
			mVerticalPosition = preState.positionX;
			mHorizontalPosition = preState.positionY;
			mVerticalDirectionFactor = preState.directionX;
			mHorizontalDirectionFactor = preState.directionY;
		}
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		AHUD.debug("TRACE: HUDView.onLayout(" + changed + ", " + left + ", " + top + ", " 
	                                  + right + ", " + bottom + ")");
		if (changed) {
	        int width = this.getWidth();
	        int height = this.getHeight();
	        AHUD.debug("View size: " + width + "x" + height);
		}
		
		mVerticalPosition = mVerticalPosition == -1 ? this.getWidth() : mVerticalPosition;
		mHorizontalPosition = mHorizontalPosition == -1 ? this.getHeight() : mHorizontalPosition;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (mState) {
		case STATE_FIRST_LINE:
			drawSingleAxis(canvas);
			break;
			
		case STATE_SECOND_LINE:
			drawDoubleAxis(canvas);
			break;
			
		case STATE_PAUSE:
		case STATE_CROSS:
		default:
			return;
		}
		checkLimits();
		updatePosition();
		invalidate();
	}
	
	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	/**
	 * Go to next state, usually after a click.
	 * @return True if the axis are crossed.
	 */
	public boolean click() {
		switch (mState) {
		case STATE_FIRST_LINE:
			mState = STATE_SECOND_LINE;
			switchMovingAxis();
			break;

		case STATE_SECOND_LINE:
			mState = STATE_CROSS;
			return true;
			
		case STATE_PAUSE:
			mState = STATE_FIRST_LINE;
			invalidate();
			return false;
		}
		return false;
	}
	
	/**
	 * Hack for the fucking softkeyboard.
	 */
	public void reset() {
		mMovingAxis = mConf.getAxisInital();
		mState = STATE_FIRST_LINE;
	}
	
	/**
	 * @return The X coordeante.
	 */
	public int getXCoordenate() {
		return (int) mVerticalPosition;
	}

	/**
	 * @return The Y coordenate.
	 */
	public int getYCoordenate() {
		return (int) mHorizontalPosition;
	}
	
	public int getXDirection() {
		return mVerticalDirectionFactor;
	}
	
	public int getYDirection() {
		return mHorizontalDirectionFactor;
	}
	
	/**
	 * Pause the HUD.
	 */
	public void pause() {
		mState = STATE_PAUSE;
	}
	
	public void setColor(int color) {
		mPaintBox.setColor(color);
		mPaintDart.setColor(color);
	}
	
	private void drawSingleAxis (Canvas canvas) {
		if (mMovingAxis == AhudConfiguration.AXIS_HORIZONTAL)
		{
			drawHorizontalAxis(canvas, true);
		}
		else if (mMovingAxis == AhudConfiguration.AXIS_VERTICAL)
		{
			drawVerticalAxis(canvas, true);
		}
	}
	
	private void drawDoubleAxis (Canvas canvas) {
		mPaintBox.setAlpha(ALPHA_HALF);
		boolean isFirstHAxis = mConf.getAxisInital() == AhudConfiguration.AXIS_HORIZONTAL;
		drawVerticalAxis(canvas, !isFirstHAxis);
		drawHorizontalAxis(canvas, isFirstHAxis);
	}
	
	private void updatePosition () {
		if (mMovingAxis == AhudConfiguration.AXIS_HORIZONTAL)
		{
			mHorizontalPosition =(mHorizontalPosition + mHorizontalDirectionFactor * mConf.getHorizontalSpeed());
		}
		else if (mMovingAxis == AhudConfiguration.AXIS_VERTICAL)
		{
			mVerticalPosition = (mVerticalPosition + mVerticalDirectionFactor * mConf.getVerticalSpeed());		
		}
	}
	
	private void checkLimits() {
		if (mVerticalPosition >= getWidth()) {
			mVerticalDirectionFactor = -1;
		}
		if (mHorizontalPosition >= getHeight()) {
			mHorizontalDirectionFactor = -1;
		}
		if (mVerticalPosition <= 0) {
			mVerticalDirectionFactor = 1;
		}
		if (mHorizontalPosition <= 0) {
			mHorizontalDirectionFactor = 1;
		}
	}
	
	/**
	 * Draw horizontal axis.
	 * @param canvas The canvas to draw.
	 * @param mode True for main, axis axis. False for dart axis.
	 */
	private void drawHorizontalAxis(Canvas canvas, boolean mode) {
		int width = mConf.getAxisWidth();
		if ( mode ) {
			Rect rect = new Rect(0, (int) mHorizontalPosition - width, getWidth(), (int) mHorizontalPosition + width);
			RectF rf = new RectF();
			rf.set(rect);
			canvas.drawRect(rf, mPaintBox);
			Rect r = new Rect(0, (int) mHorizontalPosition - width, getWidth(), (int) mHorizontalPosition + width);
			canvas.drawRect(r, mPaintBoxInside);
		}
		else {
			canvas.drawCircle(mVerticalPosition, mHorizontalPosition, width, mPaintDart);
			canvas.drawLine(mVerticalPosition-DART_SIZE, mHorizontalPosition, mVerticalPosition+DART_SIZE, mHorizontalPosition, mPaintDart);
			canvas.drawLine(mVerticalPosition, mHorizontalPosition-DART_SIZE, mVerticalPosition, mHorizontalPosition+DART_SIZE, mPaintDart);
		}
	}
	
	/**
	 * Draw vertical axis.
	 * @param canvas The canvas to draw.
	 * @param mode True for main, box axis. False for dart axis.
	 */
	private void drawVerticalAxis(Canvas canvas, boolean mode) {
		int width = mConf.getAxisWidth();
		if ( mode ) {
			Rect rect = new Rect((int) mVerticalPosition - width, 0, (int) mVerticalPosition + width, getHeight());
			RectF rf = new RectF();
			rf.set(rect);
			canvas.drawRoundRect(rf, RADIUS, RADIUS, mPaintBox);			
		}
		else {
			canvas.drawCircle(mVerticalPosition, mHorizontalPosition, width, mPaintDart);
			canvas.drawLine(mVerticalPosition-DART_SIZE, mHorizontalPosition, mVerticalPosition+DART_SIZE, mHorizontalPosition, mPaintDart);
			canvas.drawLine(mVerticalPosition, mHorizontalPosition-DART_SIZE, mVerticalPosition, mHorizontalPosition+DART_SIZE, mPaintDart);
		}
	}
	
	private void switchMovingAxis() {
		if (mMovingAxis == AhudConfiguration.AXIS_HORIZONTAL)
			mMovingAxis = AhudConfiguration.AXIS_VERTICAL;
		else
			mMovingAxis = AhudConfiguration.AXIS_HORIZONTAL;
	}
	
}
