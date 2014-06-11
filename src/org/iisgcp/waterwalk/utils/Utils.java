/*
 * Copyright (C) 2014 The Illinois-Indiana Sea Grant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iisgcp.waterwalk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.iisgcp.waterwalk.activity.MainActivity;
import org.iisgcp.waterwalk.activity.PointOfInterestActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;

public class Utils {
	
	public static String getRawString(Context context, int id) {
		String line;
		StringBuffer html = new StringBuffer();

		try {
			InputStream is = context.getResources().openRawResource(id);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			while ((line = reader.readLine()) != null) {
				html = html.append(line);
			}

			is.close();
			reader.close();
		} catch (IOException e) {
		}

		return html.toString().replaceAll("<title.*.title>", "");
	}
	
	public static int getLargestGridDimension(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	return context.getResources().getDisplayMetrics().widthPixels / 2;
        } else {
        	return context.getResources().getDisplayMetrics().heightPixels / 2;
        }
	}
	
	public static int getTitleBarHeight(Context context) {
		int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		int height = 0;

		switch (densityDpi) {
			case DisplayMetrics.DENSITY_XHIGH:
				height = 50;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				height = 38;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				height = 25;
				break;
			case DisplayMetrics.DENSITY_LOW:
				height = 19;
				break;
			default:
				height = 50;
		}
		
		return height;
	}
	
    public static int calculateInSampleSize(
    		BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;

    	if (height > reqHeight || width > reqWidth) {

    		final int halfHeight = height / 2;
    		final int halfWidth = width / 2;

    		// Calculate the largest inSampleSize value that is a power of 2 and keeps both
    		// height and width larger than the requested height and width.
    		while ((halfHeight / inSampleSize) > reqHeight
    				&& (halfWidth / inSampleSize) > reqWidth) {
    			inSampleSize *= 2;
    		}
    	}

    	return inSampleSize;
    }
    
    public static Bitmap decodeSampledBitmapFromResource(Context context, int resId,
    		int reqWidth, int reqHeight) {

    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(context.getResources(), resId, options);

    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    	// Decode bitmap with inSampleSize set
    	options.inJustDecodeBounds = false;
    	return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
    
    @TargetApi(11)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(MainActivity.class, 1)
                        .setClassInstanceLimit(PointOfInterestActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
