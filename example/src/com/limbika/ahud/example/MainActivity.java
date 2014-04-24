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

package com.limbika.ahud.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.limbika.ahud.AhudConfiguration;
import com.limbika.ahud.AhudServiceConnection;
import com.limbika.ahud.AhudServiceConnection.OnCrossListener;
import com.limbika.ahud.AhudServiceConnection.OnServiceConnectionListener;

/**
 * Example of the Accessibility HUD Service library usage (Not a {@link Android AccessibilityService}).
 * <p>
 * The library has a simple public API that is managed with {@link AhudServiceConnection}, the
 * configuration with {@link AhudConfiguration} and two callbacks:
 * <ul>
 * <li>{@link OnServiceConnectionListener} to connect/disconnect listener.</li>
 * <li>{@link OnCrossListener} to cross listener.</li>
 * </ul>
 */
public class MainActivity extends Activity implements OnCrossListener, OnServiceConnectionListener, OnClickListener {
	
	private static final String TAG = "Albert";
	private AhudServiceConnection mConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_start).setOnClickListener(this);
		findViewById(R.id.btn_stop).setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		AhudConfiguration conf = new AhudConfiguration();
		conf.setAxisWidth(16);
		conf.setHorizontalSpeed(5);
		conf.setVerticalSpeed(5);
		conf.setAxisInital(AhudConfiguration.AXIS_VERTICAL);
		conf.setAxisRestater(true);
		
		// mConnection = new AhudServiceConnection(this); => mConnection = new AhudServiceConnection(this, true); 
		// If use above constructor, is not neccessary to add the overlay view manually.
		mConnection = new AhudServiceConnection(this, false);
		mConnection.setOnServiceConnectionListener(this);
		mConnection.setOnCrossListener(this);
		mConnection.setConfiguration(conf);
		mConnection.doBindService();
	}

	@Override
	public void onPause() {
		super.onPause();
		// Stop Accessibility Service
		if (mConnection != null)
			mConnection.doUnbindService();
		mConnection = null;
	}

	@Override
	public void onServiceConnected() {
		Log.d(TAG, "Service connected");
		mConnection.addOverlayView();
	}

	@Override
	public void onServiceDisconnected() {
		Log.d(TAG, "Service disconnected");
		// No neccessary to call removeOverlayView()
	}
	
	@Override
	public boolean onCross(int x, int y) {
		Log.d(TAG, "Cross! x=" + x + ", y=" + y);
		
		// Custom manage
		Toast.makeText(this, "(" + x + ", " + y + ")", Toast.LENGTH_SHORT).show();
		mConnection.addOverlayView();
		return true;
		
		// Alternative:
		// Library generate a click event in (x,y) coordenates and
		// put the HUD overlay view again with the automanage mode.
		// =>
		//return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			// If the overlay view is yet added
			// then addOverlayView() do nothing
			// and no crash.
			mConnection.addOverlayView();
			break;
		
		case R.id.btn_stop:
			// If the overlay view is yet removed
			// then removeOverlayView() do nothing
			// and no crash.
			mConnection.removeOverlayView();
			break;
		}
		
	}
	
}
