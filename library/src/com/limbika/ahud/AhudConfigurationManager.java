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

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Accessibility HUD configuration manager.
 */
public class AhudConfigurationManager {

	private SharedPreferences             	mPrefs;
	private ArrayList<AhudConfiguration>	mConfigurations;
	private static AhudConfigurationManager	sInstance = null;

	@SuppressWarnings("unchecked")
	private AhudConfigurationManager(Context context) {
		mPrefs = context.getSharedPreferences(AHUD.PKG, Context.MODE_PRIVATE);
		try {
			String s = mPrefs.getString(AHUD.PKG, Serializer.toString(new ArrayList<AhudConfiguration>()));
			mConfigurations = (ArrayList<AhudConfiguration>) Serializer.fromString(s);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param context The application context.
	 * @return The unique instace of {@link AhudConfigurationManager}.
	 */
	public static AhudConfigurationManager get(Context context) {
		if ( sInstance == null )
			sInstance = new AhudConfigurationManager(context);
		return sInstance;
	}

	/**
	 * @return List the all ids of the saved configurations.
	 */
	public long[] getAllIds() {
		long[] out = new long[mConfigurations.size()];
		for (int i=0;i<out.length;i++) {
			out[i] = mConfigurations.get(i).getId();
		}
		return out;
	}
	
	/**
	 * @param id The id for the configuration.
	 * @return The configuration with given id.
	 */
	public AhudConfiguration findConfById(long id) {
		for (AhudConfiguration conf : mConfigurations) {
			if (conf.getId() == id) {
				return conf;
			}
		}
		return null;
	}

	/**
	 * Add configuration.
	 * @param configuration The configuration.
	 * @throws AhudConfigurationException 
	 */
	public void addConfiguration(AhudConfiguration configuration) throws AhudConfigurationException {
		if ( configuration.getId() == -1 ) {
			throw new AhudConfigurationException();
		}
		for ( int i=0;i<mConfigurations.size();i++ ) {
			if ( mConfigurations.get(i).getId() == configuration.getId() )
				mConfigurations.remove(configuration);
		}
		mConfigurations.add(configuration);
	}

	/**
	 * Remove configuration.
	 * @param configuration The configuration.
	 */
	public void removeConfiguration(AhudConfiguration configuration) {
		mConfigurations.remove(configuration);
	}
	
	/**
	 * Remove all configurations with the given id.
	 * @param id The id.
	 */
	public void removeConfiguration(long id)  {
		ArrayList<AhudConfiguration> toRemove = new ArrayList<AhudConfiguration>();
		for (AhudConfiguration conf : mConfigurations) {
			if (conf.getId() == id)
				toRemove.add(conf);
		}
		mConfigurations.removeAll(toRemove);
	}

	/**
	 * Save the configuration list.
	 */
	public void save() {
		try {
			mPrefs.edit().putString(AHUD.PKG, Serializer.toString(mConfigurations)).commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Invoked at an illegal configuration.
	 */
	public class AhudConfigurationException extends Exception {

		private static final long serialVersionUID = -7780289304171832132L;
		
		public AhudConfigurationException() {
			super("Illegal assigment with id=-1");
		}
	}
	
}
