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

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnLongClickListener;

/**
 * Accessibility HUD Service Connection. 
 */
public final class AhudServiceConnection implements ServiceConnection {
	
	private Context								mContext;
	private AhudService 						mBoundService;
	private AhudConfiguration					mConfiguration;
	private boolean								mIsBound;
	private boolean								mIsStartOnBind;
	private OnServiceConnectionListener			mConnectionListener;
	private OnCrossListener						mCrossListener;

	/**
	 * Interface definition for a callback to be invoked when the
	 * service is connected or disconnected.
	 */
	public interface OnServiceConnectionListener {
		/**
		 * Called when the service is connected.
		 */
		public void onServiceConnected();
		/**
		 * Called when the service is disconnected.
		 */
		public void onServiceDisconnected ();
	}
	
	/**
	 * Interface definition for a callback to be invoked when the
	 * bars are crossed.
	 */
	public interface OnCrossListener {
		/**
		 * Called when the bars are crossed.
		 * @param x The x coordenate.
		 * @param y The y coordenate.
		 * @return 
		 * Return true to custom manage of the HUD.
		 * Yo must call to <code>addOverlayView()</code> to put HUD view again.
		 * <p>
		 * False to manage automatically the HUD.
		 * Library inject click event and add overlay view after it.
		 */
		public boolean onCross(int x, int y);
	}
	
	
	/**
	 * Creates an Accessible HUD Service connection object.
	 * @param context The application context.
	 */
	public AhudServiceConnection(Context context) {
		mContext = context;
		mIsStartOnBind = true;
	}
	
	/**
	 * Creates an Accessible HUD Service connection object.
	 * @param context The application context.
	 * @param mode  True to set automatically add the HUD like <code>
	 * 				AhudServiceConnection(Context)</code>, false to add manually.
	 */
	public AhudServiceConnection(Context context, boolean mode) {
		mContext = context;
		mIsStartOnBind = false;
	}
	
	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// This is called when the connection with the service has been
		// established, giving us the service object we can use to
		// interact with the service. Because we have bound to a explicit
		// service that we know is running in our own process, we can
		// cast its IBinder to a concrete class and directly access it.
		mBoundService = ((AhudService.AHUDBinder) service).getService();
		mBoundService.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				removeOverlayView();
				onServiceDisconnected(null);
				return true;
			}
		});
		

		if (mConfiguration != null)
			mBoundService.setConfiguration(mConfiguration);
		if(mCrossListener != null)
			mBoundService.setOnCrossListener(mCrossListener);
		if (mConnectionListener != null)
			mConnectionListener.onServiceConnected();
		if ( mIsStartOnBind )
			mBoundService.createOverlayView();
	}
	
	@Override
    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        // Because it is running in our same process, we should never
        // see this happen.
		if (mConnectionListener != null)
			mConnectionListener.onServiceDisconnected();
        mBoundService = null;
    }
	
	/**
	 * Register a callback to be invoked when the service is connected or disconnected.
	 * @param listener The callback that will run.
	 */
	public void setOnServiceConnectionListener (OnServiceConnectionListener listener) {
		mConnectionListener = listener;
	}
	
	/**
	 * Register a callback to be invoked when the bars are crossed.
	 * @param listener The callback that will run.
	 */
	public void setOnCrossListener(OnCrossListener listener) {
		mCrossListener = listener;
		if ( mIsBound )
			mBoundService.setOnCrossListener(listener);
	}
	
	/**
	 * Set the configuration of the HUD.
	 * @param conf The configuration.
	 */
	public void setConfiguration(AhudConfiguration conf) {
		mConfiguration = conf;
		if ( mIsBound )
			mBoundService.setConfiguration(conf);
	}
	
	/**
	 * Add the HUD overlay view.
	 */
	public void addOverlayView() {
		if ( mIsBound )
			mBoundService.createOverlayView();
	}
	
	/**
	 * Remove the HUD overlay view.
	 */
	public void removeOverlayView() {
		if ( mIsBound )
			mBoundService.destroyOverlayView();
	}
	
    /**
     * @return The bounded service.
     */
    public Service getBoundService() {
		return mBoundService;
	}
    
    /**
     * Bind the Accessibility HUD service.
     */
    public boolean doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
    	Intent intent = new Intent(mContext, AhudService.class);
    	mIsBound = mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    	return mIsBound;
    }
    
    /**
     * @return True if the service is bounded.
     */
    public boolean isBounded() {
    	return mIsBound;
    }
    
    /**
     * Unbind the Accessibility HUD service.
     */
    public void doUnbindService() {
        if ( mIsBound ) {
            // Detach our existing connection.
        	mContext.unbindService(this);
        	mIsBound = false;
        }
    }
    
}
