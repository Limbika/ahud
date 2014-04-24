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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

/**
 * Serialize serializable objects into string.
 */
/* package */ class Serializer {
    
	/**
	 * Read the object from Base64 string.
	 * @param s The object in a String.
	 * @return The object.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
    public static Object fromString(String s) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream( new Base64InputStream(new ByteArrayInputStream(s.getBytes()), Base64.DEFAULT));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
    
    /**
     * Write the object to a Base64 string.
     * @param o The object.
     * @return The object in a String.
     * @throws IOException
     */
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new Base64OutputStream(baos, Base64.DEFAULT));
        oos.writeObject(o);
        oos.close();
        return new String(baos.toByteArray());
    }
}
