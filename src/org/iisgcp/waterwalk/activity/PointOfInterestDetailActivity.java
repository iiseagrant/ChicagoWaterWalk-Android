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

import java.util.ArrayList;
import java.util.List;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.adapter.DetailListAdapter;
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.ImageCache;
import org.iisgcp.waterwalk.utils.ImageResizer;
import org.iisgcp.waterwalk.utils.ImageWorker;
import org.iisgcp.waterwalk.utils.Utils;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.nirhart.parallaxscroll.views.ParallaxListView;

public class PointOfInterestDetailActivity extends NavigationDrawerActivity {
	
    private static final String IMAGE_CACHE_DIR = "detail_images";

    private int mImageId;
    private ImageView mImageView;
    
    private ImageResizer mImageFetcher;
	
	private String mTitle;
	private float mLat;
	private float mLng;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(getIntent().getStringExtra(Constants.INTENT_TITLE));
        setContentView(R.layout.activity_point_of_interest_detail);
        
        List<RowItem> rowItems = null;
        
        TypedArray poi = getResources().obtainTypedArray(getIntent().getIntExtra(Constants.INTENT_RES_ID, -1));
        mImageId = poi.getResourceId(2, -1);
        mTitle = getIntent().getStringExtra(Constants.INTENT_TITLE);
        mLat = poi.getFloat(3, -1);
        mLng = poi.getFloat(4, -1);
        TypedArray details = getResources().obtainTypedArray(poi.getResourceId(5, -1));
        final int resId = poi.getResourceId(5, -1);
        poi.recycle();
        
        rowItems = new ArrayList<RowItem>();
        
        for (int i = 0; i < details.length(); i = i + 3) {
        	String title = getString(details.getResourceId(i, -1));
        	int j = i + 1;
        	String description = Utils.getRawString(this, details.getResourceId(j, -1)).replaceAll("<title.*.title>", "");
        	j++;
        	int img = details.getResourceId(j, -1);
        	
        	rowItems.add(buildRow(title, img, description));
        }
        
        details.recycle();
        
        final ListView list = (ListView) findViewById(android.R.id.list);
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	mImageView = (ImageView) getLayoutInflater().inflate(R.layout.header, null);
            mImageView.setImageDrawable(null);
            ((ParallaxListView) list).addParallaxedHeaderView(mImageView, null, false);
        } else {
        	mImageView = (ImageView) findViewById(R.id.header);
            mImageView.setImageDrawable(null);
        }
        
        DetailListAdapter adapter = new DetailListAdapter(this, rowItems);
	
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				RowItem rowItem = (RowItem) parent.getItemAtPosition(position);
				
				position = position - list.getHeaderViewsCount();
				
				Intent intent = new Intent(PointOfInterestDetailActivity.this, FactActivity.class);
		        intent.putExtra(Constants.INTENT_POSITION, position);
		        intent.putExtra(Constants.INTENT_TITLE, rowItem.getTitle());
				intent.putExtra(Constants.INTENT_RES_ID, resId);
				PointOfInterestDetailActivity.this.startActivity(intent);
			}
        	
        });
        
        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        final int longest = (height > width ? height : width) / 2;
        
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageResizer(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);
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
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.point_of_interest_detail_activity_menu, menu);
	    return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
    	
        switch (item.getItemId()) {
    		case android.R.id.home:
    			finish();
    			return true;
    		case R.id.menu_map:
    			intent = new Intent(this, MapActivity.class);
    			intent.putExtra(Constants.INTENT_TITLE, mTitle);
    			intent.putExtra(Constants.INTENT_LATITUDE, mLat);
    			intent.putExtra(Constants.INTENT_LONGITUDE, mLng);
    			startActivity(intent);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
        }
    }
    
    private RowItem buildRow(String title, int imageId, String description) {
    	RowItem rowItem = new RowItem();
    	rowItem.setImageId(imageId);
    	rowItem.setTitle(title);
    	rowItem.setDescription(description);
    	
    	return rowItem;
    }
}
