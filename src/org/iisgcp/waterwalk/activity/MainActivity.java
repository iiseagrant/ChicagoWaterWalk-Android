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
import org.iisgcp.waterwalk.fragment.AllRoutesFragment;
import org.iisgcp.waterwalk.fragment.RoutesGridFragment;
import org.iisgcp.waterwalk.fragment.RoutesMapFragment;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.HelpDialog;
import org.iisgcp.waterwalk.utils.HelpDialog.HelpDialogListener;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		ActionBar.OnNavigationListener,
		HelpDialogListener {

	private static final String HELP_DIALOG = "help_dialog";
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private String mTag;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	
    private String [] mDrawerTitles;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		mDrawerTitles = getResources().getStringArray(R.array.drawer_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
		
		setTitle(getString(R.string.title_routes));
		
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
        	
            public void onDrawerClosed(View view) {
            	if (mDrawerList.getCheckedItemPosition() == 0) {
                    setTitle(mDrawerTitles[0]);
            		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            	} else {
                    setTitle(mDrawerTitles[1]);
            		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            	}
            }

            public void onDrawerOpened(View drawerView) {
                setTitle(getString(R.string.app_name));
            	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        
        if (!prefs.contains(Constants.INTENT_DRAWER_POSITION)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        	mDrawerLayout.openDrawer(Gravity.LEFT);
        	showDialog(HELP_DIALOG);
        }
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (intent == null) {
			return;
		}
		
		int position = intent.getIntExtra(Constants.INTENT_DRAWER_POSITION, -1);
		
		if (position != -1) {
            selectItem(position);
		}
		
		setIntent(null);
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
			int position = Integer.valueOf(savedInstanceState.getString(STATE_SELECTED_NAVIGATION_ITEM));
			
			if (position == 0 || position == 1) {
				getSupportActionBar().setSelectedNavigationItem(
						Integer.valueOf(savedInstanceState.getString(STATE_SELECTED_NAVIGATION_ITEM)));
			} else {
	    		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
			mTag = savedInstanceState.getString(STATE_SELECTED_NAVIGATION_ITEM);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Serialize the current dropdown position.
		outState.putString(STATE_SELECTED_NAVIGATION_ITEM, mTag);
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
            	fragment = new RoutesGridFragment();
            } else if (position == 1) {
            	fragment = new RoutesMapFragment();
            }
    		
            getSupportFragmentManager().beginTransaction()
    				.add(R.id.content, fragment, tag).commit();
    	} else {
    		fragmentManager.beginTransaction().attach(fragment).commit();
    	}
    	
    	mTag = tag;
    	
    	return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
    	FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(mTag);
		if (fragment != null) {
			fragmentManager.beginTransaction().detach(fragment).commit();
		}
    	
    	if (position == 0) {
    		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    		// Set up the dropdown list navigation in the action bar.
    		getSupportActionBar().setListNavigationCallbacks(
    		// Specify a SpinnerAdapter to populate the dropdown list.
    				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
    						android.R.layout.simple_dropdown_item_1line,
    						android.R.id.text1, new String[] {
    								getString(R.string.title_grid),
    								getString(R.string.title_map)}), this);
    		
    		setTitle(getString(R.string.title_routes));
    	} else {
    		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    		
    	 	String tag = String.valueOf(2);
        	fragment = getSupportFragmentManager().findFragmentByTag(tag);
        	
        	if (fragment == null) {
                fragment = new AllRoutesFragment();
        		
                getSupportFragmentManager().beginTransaction()
        				.add(R.id.content, fragment, tag).commit();
        	} else {
        		fragmentManager.beginTransaction().attach(fragment).commit();
        	}
    		
    		setTitle(getString(R.string.title_all_stops));
    		mTag = tag;
    	}
    	
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    private void showDialog(String tag) {
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        Fragment fragment = HelpDialog.newInstance(this, getString(R.string.welcome), getString(R.string.help_text));
        
        ft.add(0, fragment, tag);
        ft.commit();
    }

	@Override
	public void onHelpDialogClosed(DialogFragment dialog) {
        mDrawerLayout.closeDrawer(mDrawerList);
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putString(Constants.INTENT_DRAWER_POSITION, Constants.INTENT_DRAWER_POSITION);
    	editor.commit();
	}
}
