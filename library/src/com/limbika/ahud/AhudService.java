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

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.limbika.ahud.AhudServiceConnection.OnCrossListener;

/** 
 * Limbika Accessibility HUD Service.
 * <p>
 * Not use directly. Use {@link AhudServiceConnection} instead.
 */
public class AhudService extends Service implements OnClickListener {
	
	class AHUDBinder extends Binder {
		public AhudService getService() {
			return AhudService.this;
		}
	}
	
	//-------------------------------------------------------------------------
	// Members
	private boolean mIsFinishing = false;
	private AhudConfiguration mConfiguration;
	private AhudView mAhudView;
	private PreState mPreState;
	private ListenerView mListenerView;
	private OnCrossListener mCrossListener;
	private OnLongClickListener mLongClickListener;
	private IBinder	mBinder = new AHUDBinder();
	
	@Override
	public void onCreate() {
		AHUD.debug("TRACE: onCreate()");
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		AHUD.debug("TRACE: onUnbind()");
		mIsFinishing = true;
		destroyOverlayView();
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		AHUD.debug("TRACE: onStartCommand(" + intent + ", " + flags + ", " + startId + ")");
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		AHUD.debug("TRACE: onDestroy()");
		super.onDestroy();
		destroyOverlayView();
	}
	
	@Override
	public void onClick(View v) {
		if ( mAhudView.click() ) {
			int x = mAhudView.getXCoordenate();
			int y = mAhudView.getYCoordenate();
			
			// Prestate
			mPreState = new PreState();
			mPreState.positionX = x;
			mPreState.positionY = y;
			mPreState.directionX = mAhudView.getXDirection();
			mPreState.directionY = mAhudView.getYDirection();
			
			destroyOverlayView();
			if ( mCrossListener == null || !mCrossListener.onCross(x, y) ) 	
				click(x, y);
		}
	}
	
	/* package */ void setOnLongClickListener(OnLongClickListener listener) {
		mLongClickListener = listener;
	}
	
	/* package */ void setConfiguration(AhudConfiguration conf) {
		mConfiguration = conf;
	}
	
	/* package */ void setOnCrossListener(OnCrossListener listener) {
		mCrossListener = listener;
	}
	
	//-------------------------------------------------------------------------
	// Overlay view 

	/**
	 * Create the overlay view with the axis.
	 */
	/* package */ void createOverlayView() {
		AHUD.debug("TRACE: createOverlayView()");
		
        // ////////////////////////////////////////////////////////////////////////////////
        // AHUDView
		// This LayoutParams fill all the screen except the buttons bar.
		// But the view is not clickable!
        LayoutParams ahudParams = new LayoutParams();
        ahudParams.setTitle("Limbika Accessibility HUD");

        // Set a transparent background
        ahudParams.format = PixelFormat.TRANSLUCENT;

        // Create an always on top type of window:
        //   TYPE_SYSTEM_ALERT   = touch events are intercepted
        ahudParams.type   	= LayoutParams.TYPE_SYSTEM_ALERT;
        ahudParams.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
        
        // The whole screen is covered (including status bar)
        ahudParams.flags  = LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // ////////////////////////////////////////////////////////////////////////////////
        
        // ////////////////////////////////////////////////////////////////////////////////
        // ListenerView
		// This LayoutParams fill all the screen except the title bar and the buttons bar.
		// The view is clickable.
        LayoutParams listenerParams = new LayoutParams();
        listenerParams.setTitle("Limbika Accesibility HUD - Listener");

        // Set a transparent background
        listenerParams.format = PixelFormat.TRANSLUCENT;

        // Create an always on top type of window:
        //   TYPE_SYSTEM_ALERT   = touch events are intercepted
        //   TYPE_SYSTEM_OVERLAY = touch event pass through
        listenerParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        
        // The whole screen is covered (including status bar)
        listenerParams.flags = LayoutParams.FLAG_LAYOUT_INSET_DECOR | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // ////////////////////////////////////////////////////////////////////////////////
        
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if ( mAhudView == null ) {
        	mConfiguration = mConfiguration == null ? new AhudConfiguration() : mConfiguration;
        	mAhudView = new AhudView(this, mConfiguration, mPreState);
        	wm.addView(mAhudView, ahudParams);
        }
        if ( mListenerView == null ) {
            mListenerView = new ListenerView(this);
            mListenerView.setClickable(true);
            mListenerView.setOnClickListener(this);
            mListenerView.setOnLongClickListener(mLongClickListener);
            
        	wm.addView(mListenerView, listenerParams);
        }
	}

	/**
	 * Destroy the overlay view.
	 * @param all True to remove all the view, false to keep {@link ButtonsBar} and {@link ListenerView}.
	 */
	/* package */ void destroyOverlayView() {
    	AHUD.debug("TRACE: destroyOverlayView()");
    	WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	    if (mAhudView != null )
	    {
	    	wm.removeViewImmediate(mAhudView);
	    	mAhudView = null;
	    }
	    if (mListenerView != null )
	    {
	    	wm.removeViewImmediate(mListenerView);
	    	mListenerView = null;
	    }
	}
	
	//-------------------------------------------------------------------------
	// Click
	
	/**
	 * Handle the end of click events to create overlay view.
	 */
	private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage (Message msg) {
    		if ( !mIsFinishing )
    			createOverlayView();
    	}
	};
	
    /**
     * Generate click event.
     * @param x The abscissa coordinate.
     * @param y The ordinatte coordiante.
     */
    private void click(final float x, final float y) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i(AHUD.TAG, "Generate click event in: (" + x + ", " + y + ")");
				Instrumentation i = new Instrumentation();
				MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,x, y, 0);
				MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
				i.sendPointerSync(down);
				i.sendPointerSync(up);
				up.recycle();
				down.recycle();
				mHandler.sendEmptyMessage(0);
			}
    	}).start();
    }


}
