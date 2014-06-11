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
import org.iisgcp.waterwalk.fragment.FactViewPagerFragment;
import org.iisgcp.waterwalk.utils.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.WindowManager;

public class FactActivity extends NavigationDrawerActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(getIntent().getStringExtra(Constants.INTENT_TITLE));
        setContentView(R.layout.activity_fact);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
		setTitle(getIntent().getExtras().getString(Constants.INTENT_TITLE));
        
        int position = getIntent().getIntExtra(Constants.INTENT_POSITION, -1);
        
		if (savedInstanceState == null) {
			Fragment fragment = FactViewPagerFragment.newInstance(position, getIntent().getIntExtra(Constants.INTENT_RES_ID, -1));
		
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content, fragment).commit();
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
    	
        switch (item.getItemId()) {
    		case android.R.id.home:
    			intent = new Intent(this, PointOfInterestDetailActivity.class);
    			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
        }
    }
}
