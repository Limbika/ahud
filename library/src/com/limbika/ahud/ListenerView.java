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
import android.view.ViewGroup;

/**
 * Dummy view to listen the touch events.
 * @author xabi
 */
/* package */ class ListenerView extends ViewGroup {
	
	public ListenerView(Context contex) {
		super(contex);
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		AHUD.debug("TRACE: ListenerView.onLayout(" + changed + ", " + left + ", " + top + ", " 
	                                  + right + ", " + bottom + ")");
	}
	
}
