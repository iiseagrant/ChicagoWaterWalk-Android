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
import org.iisgcp.waterwalk.fragment.PointOfInterestGridViewPagerFragment;
import org.iisgcp.waterwalk.fragment.PointOfInterestMapFragment;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.OnItemClickedListener;
import org.iisgcp.waterwalk.utils.OnPageSelectedListener;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PointOfInterestActivity extends NavigationDrawerActivity implements
		ActionBar.OnNavigationListener,
		OnItemClickedListener,
		OnPageSelectedListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String STATE_SELECTED_PAGE_POSITION = "selected_page_position";
	
	private String mTag;
	private int mPosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_point_of_interest);
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_dropdown_item_1line,
						android.R.id.text1, new String[] {
								getString(R.string.title_grid),
								getString(R.string.title_map)}), this);
		
		LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.actionbar, null);
		actionBar.setCustomView(view);
		
		setTitle(getIntent().getExtras().getString(Constants.INTENT_TITLE));
		
		mPosition = getIntent().getIntExtra(Constants.INTENT_POSITION, -1);
		
		if (savedInstanceState == null) {
			actionBar.setSelectedNavigationItem(getIntent().getIntExtra(Constants.INTENT_TAB, 0));
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		TextView view = (TextView) findViewById(R.id.title);
		view.setText(title);
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					Integer.valueOf(savedInstanceState.getString(STATE_SELECTED_NAVIGATION_ITEM)));
			
			mTag = savedInstanceState.getString(STATE_SELECTED_NAVIGATION_ITEM);
		}
		
		if (savedInstanceState.containsKey(STATE_SELECTED_PAGE_POSITION)) {
			mPosition = savedInstanceState.getInt(STATE_SELECTED_PAGE_POSITION);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Serialize the current dropdown position.
		outState.putString(STATE_SELECTED_NAVIGATION_ITEM, mTag);
		outState.putInt(STATE_SELECTED_PAGE_POSITION, mPosition);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    	case android.R.id.home:
	    		Intent intent = new Intent(this, MainActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(intent);
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTag);
    	
    	if (fragment != null) {
    		fragmentManager.beginTransaction().detach(fragment).commit();
    	}
    	
    	String tag = String.valueOf(position);
    	fragment = getSupportFragmentManager().findFragmentByTag(tag);
    	
    	if (fragment == null) {
            if (position == 0) {
        		fragment = PointOfInterestGridViewPagerFragment.newInstance(mPosition);
            } else if (position == 1) {
        		fragment = PointOfInterestMapFragment.newInstance(mPosition);
            }
    		
            getSupportFragmentManager().beginTransaction()
    				.add(R.id.content, fragment, tag).commit();
    	} else {
    		if (position == 0) {
        		fragment = PointOfInterestGridViewPagerFragment.newInstance(mPosition);
                getSupportFragmentManager().beginTransaction()
                		.replace(R.id.content, fragment, tag).commit();
        	} else if (position == 1) {
        		((PointOfInterestMapFragment) fragment).update(mPosition);
            	getSupportFragmentManager().beginTransaction()
        				.attach(fragment).commit();
        	}
    	}
    	
    	mTag = tag;
    	
    	return true;
    }
	
	@Override
	public void onItemClicked(Intent intent) {
		startActivity(intent);
	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
	}
}
