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

import java.io.Serializable;

/** Accessibility HUD preferences */
public class AhudConfiguration implements Serializable {
	
	//-------------------------------------------------------------------------
	// Public constants
	/** Axis horizontal */
	public static final int 	AXIS_HORIZONTAL 	= 1;
	/** Axis vertical */
	public static final int		AXIS_VERTICAL 		= 2;
	/** Start from top */
	public static final int		START_TOP			= 1;
	/** Start from left */
	public static final int		START_LEFT			= 2;
	/** Start from rigth */
	public static final int		START_RIGTH			= 3;
	/** Start from bottom */
	public static final int		START_BOTTOM		= 4;

	/** Generated serial version */
	private static final long serialVersionUID = -3972796583383872919L;

	//-------------------------------------------------------------------------
	// Default preferences
	private static final int		DEFAULT_AXIS_INITIAL		= 1;		// Horizontal
	private static final boolean	DEFAULT_AXIS_RESTARTER		= false;
	private static final int		DEFAULT_SPEED 				= 10;
	private static final int		DEFAULT_WIDTH 				= 24;
	private static final int		DEFAULT_HORIZONTAL_START	= START_TOP;
	private static final int		DEFAULT_VERTICAL_START		= START_LEFT;

	//-------------------------------------------------------------------------
	// Members
	/* package */ long mId = -1L;
	/* package */ boolean mAxisRestater = DEFAULT_AXIS_RESTARTER;
	/* package */ int mAxisInitial = DEFAULT_AXIS_INITIAL;
	/* package */ int mAxisWidth = DEFAULT_WIDTH;
	/* package */ int mHorizontalSpeed = DEFAULT_SPEED;
	/* package */ int mVerticalSpeed = DEFAULT_SPEED;
	/* package */ int mHorizontalStart = DEFAULT_HORIZONTAL_START;
	/* package */ int mVerticalStart = DEFAULT_VERTICAL_START;

	/**
	 * Default configuration parameters.
	 */
	public AhudConfiguration() {}
	
	/**
	 * @return The id of the configuration.
	 */
	public long getId() {
		return mId;
	}
	
	/**
	 * @return The inital bar. 1 to horizontal, -1 to vertical.
	 */
	public int getAxisInital() {
		return mAxisInitial;
	}
	
	/**
	 * @return True if the axis restarts.
	 */
	public boolean isAxisRestater () {
		return mAxisRestater;
	}
	
	/**
	 * @return The width of the axis.
	 */
	public int getAxisWidth() {
		return mAxisWidth;
	}
	
	/**
	 * @return The speed of the horizontal axis.
	 */
	public int getHorizontalSpeed() {
		return mHorizontalSpeed;
	}

	/**
	 * @return The speed of the vertical axis.
	 */
	public int getVerticalSpeed() {
		return mVerticalSpeed;
	}
	
	/**
	 * @return Horizontal start, 1 to top, 4 to botton.
	 */
	public int getHorizontalStart() {
		return mHorizontalStart;
	}

	/**
	 * @return Vertical start, 2 to left, 3 to rigth.
	 */
	public int getVerticalStart() {
		return mVerticalStart;
	}

	/**
	 * Set the id of the configuration.
	 * @param id The id.
	 */
	public void setId(long id) {
		mId = id;
	}
	
	/**
	 * Set the inital axis.
	 * @param inital 1 to horizontal, 2 to vertical.
	 */
	public void setAxisInital(int inital) {
		mAxisInitial = inital;
	}
	
	/**
	 * Set if the axis restart where stop in the last cross.
	 * @param restater True to set restarter.
	 */
	public void setAxisRestater(boolean restater) {
		mAxisRestater = restater;
	}
	
	/**
	 * Set the axis width.
	 * @param width The width in pixels.
	 */
	public void setAxisWidth(int width) {
		mAxisWidth = width;
	}
	
	/**
	 * Set the speed to the horizontal axis.
	 * @param speed The speed.
	 */
	public void setHorizontalSpeed(int speed) {
		mHorizontalSpeed = speed;
	}

	/**
	 * Set the speed to the vertical axis.
	 * @param speed The speed.
	 */
	public void setVerticalSpeed(int speed) {
		mVerticalSpeed = speed;
	}
	
	/**
	 * Set the starter point, top or botton.
	 * @param start 1 to top, 4 to botton.
	 */
	public void setHorizontalStart(int start) {
		mHorizontalStart = start;
	}
	
	/**
	 * Set the starter point, left or rigth-
	 * @param start 2 to left, 3 to rigth.
	 */
	public void setVerticalStart(int start) {
		mVerticalStart = start;
	}

	@Override
	public String toString() {
		return "AhudConfiguration [mId=" + mId + ", mAxisRestater="
				+ mAxisRestater + ", mAxisInitial=" + mAxisInitial
				+ ", mAxisWidth=" + mAxisWidth + ", mHorizontalSpeed="
				+ mHorizontalSpeed + ", mVerticalSpeed=" + mVerticalSpeed
				+ ", mHorizontalStart=" + mHorizontalStart
				+ ", mVerticalStart=" + mVerticalStart + "]";
	}
	
}
