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

/**
 * Accessibility HUD library - Limbika Assistive Technologies
 * <br>
 * Sample for an Activity:
 * 
 * <pre class="prettyprint">
 * 
 * 	public void onStart() {
 *		// Start Accessibility Service
 *		AhudConfiguration conf = new AhudConfiguration();
 *		conf.setAxisWidth(16);
 *		conf.setHorizontalSpeed(5);
 *		conf.setVerticalSpeed(5);
 *		conf.setAxisInital(AhudConfiguration.AXIS_VERTICAL);
 *		conf.setAxisRestater(true);
 *		
 *		// mConnection = new AhudServiceConnection(this); => mConnection = new AhudServiceConnection(this, true); 
 *		// If use up constructor, is not neccessary to add the overlay view manually.
 *		mConnection = new AhudServiceConnection(this, false);
 *		mConnection.setOnServiceConnectionListener(this);
 *		mConnection.setOnCrossListener(this);
 *		mConnection.setConfiguration(conf);
 *		mConnection.doBindService();
 *	}
 *
 *	public void onStop() {
 *		// Stop Accessibility Service
 *		if (mConnection != null) {
 *			mConnection.doUnbindService();
 *			mConnection = null;
 *		}
 *	}
 *
 *	public void onServiceConnected() {
 *		Log.d(TAG, "Service connected");
 *		mConnection.addOverlayView();
 *	}
 *
 *	public void onServiceDisconnected() {
 *		Log.d(TAG, "Service disconnected");
 *		// No neccessary to call removeOverlayView()
 *	}
 *	
 *	public boolean onCross(int x, int y) {
 *		Log.d(TAG, "Cross! x=" + x + ", y=" + y);
 *		
 *		// Custom manage
 *		Toast.makeText(this, "(" + x + ", " + y + ")", Toast.LENGTH_SHORT).show();
 *		mConnection.addOverlayView();
 *		return true;
 *		
 *		// Alternative:
 *		// Library generate a click event in (x,y) coordenates and
 *		// put the HUD overlay view again with the automanage mode.
 *		// =>
 *		//return false;
 *	}
 * }
 * </pre>
 */
package com.limbika.ahud;