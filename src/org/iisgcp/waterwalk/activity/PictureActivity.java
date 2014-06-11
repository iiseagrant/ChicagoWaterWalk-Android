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

package org.iisgcp.waterwalk.activity;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.utils.ImageFetcher;
import org.iisgcp.waterwalk.utils.ImageWorker;
import org.iisgcp.waterwalk.utils.ImageCache.ImageCacheParams;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class PictureActivity extends FragmentActivity {

    private static final String IMAGE_CACHE_DIR = "historic_photos";
	
    private int mImageId;
    private ImageView mImageView;
    
    private ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    
        View view = getLayoutInflater().inflate(R.layout.activity_picture, null);
        setContentView(view);
	    
        view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
		// check to see if we were called by clicking on a link
	    if (getIntent().getData() != null) {
	    	Uri data = getIntent().getData();
	    	int resId = Integer.valueOf(data.getPath().replace("/", ""));
	    	
	        TypedArray historicalPhotos = getResources().obtainTypedArray(R.array.historical_photos);
	        mImageId = historicalPhotos.getResourceId(resId, -1);
	        historicalPhotos.recycle();
	    } 
	    
    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(getResources(), mImageId, options);
    	int outWidth = options.outWidth;
    	int outHeight = options.outHeight;
	    
    	int maxWidth;
    	int targetWidth;
    	int targetHeight;
    		
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	maxWidth = getResources().getDisplayMetrics().widthPixels;
        } else {
        	maxWidth = getResources().getDisplayMetrics().heightPixels;
        }
	    
        if (outWidth < maxWidth) {
        	targetWidth = maxWidth;
        	int scale = maxWidth / outWidth;
        	targetHeight = outHeight * scale;
        } else {
        	targetWidth = maxWidth;
        	int scale = outWidth / maxWidth;
        	targetHeight = outHeight * scale;
        }
        
	    mImageView = (ImageView) findViewById(R.id.picture);
	    
	    ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, targetWidth, targetHeight);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
		mImageFetcher.loadImage(String.valueOf(mImageId), mImageView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();

		if (mImageView != null) {
			// Cancel any pending image work
			ImageWorker.cancelWork(mImageView);
			mImageView.setImageDrawable(null);
		}
	}
}
