# Usage

Add to the manifest:
	
	<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
	<application>
		<service android:name="com.limbika.ahud.AhudService" />
		...
	</application>
	

In the Activity:


	@Override
	public void onResume() {
		super.onResume();
		
		AhudConfiguration conf = new AhudConfiguration();
		conf.setAxisWidth(16);
		conf.setHorizontalSpeed(5);
		conf.setVerticalSpeed(5);
		conf.setAxisInital(AhudConfiguration.AXIS_VERTICAL);
		conf.setAxisRestater(true);
		
		mConnection = new AhudServiceConnection(this);
		mConnection.setOnCrossListener(this);
		mConnection.setConfiguration(conf);
		mConnection.doBindService();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mConnection != null) {
			mConnection.doUnbindService();
			mConnection = null;
		}
	}

	@Override
	public boolean onCross(int x, int y) {
		Log.d(TAG, "Cross! x=" + x + ", y=" + y);
		return false;
	}
	
	
	
# License

Copyright (C) 2014 Limbika Assistive Technologies

This library is dual-licensed: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2 as 
published by the Free Software Foundation. For the terms of this 
license, see licenses at

		http://www.gnu.org/licenses/gpl-2.0.html

You are free to use this library under the terms of the GNU General
Public License, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

Alternatively, you can license this library under a commercial
license, as set out in LICENSE.txt.

