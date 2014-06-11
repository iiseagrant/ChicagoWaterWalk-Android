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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.iisgcp.waterwalk.utils.InfoDialog;
import org.iisgcp.waterwalk.utils.LicenseDialog;
import org.iisgcp.waterwalk.utils.PictureInfoDialog;
import org.iisgcp.waterwalk.utils.Utils;

public class AboutActivity extends ActionBarActivity {

	private static final String PICTURE_INFO_DIALOG = "picture_info_dialog";
	private static final String INFO_DIALOG = "info_dialog";
	private static final String LICENSE_DIALOG = "license_dialog";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTitle(R.string.title_about);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        TextView versionText = (TextView) findViewById(R.id.version_text);
        versionText.setText(getString(R.string.version));

        Button whoAreWeButton = (Button) findViewById(R.id.who_are_we_button);
        whoAreWeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			 	String dialogTitle = getString(R.string.who_are_we);
			 	String dialogText = getString(R.string.who_are_we_text);
			 	String altDialogText = getString(R.string.alt_who_are_we_text);
			 	Fragment fragment = PictureInfoDialog.newInstance(AboutActivity.this, dialogTitle, dialogText, altDialogText);
    			showDialog(fragment, PICTURE_INFO_DIALOG);
			}
        	
        });
        
        Button creditsButton = (Button) findViewById(R.id.credits_button);
        creditsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String dialogTitle = getString(R.string.credits);
				String dialogText = Html.fromHtml(Utils.getRawString(AboutActivity.this, R.raw.photo_credits)).toString();
				String altDialogText = Html.fromHtml(Utils.getRawString(AboutActivity.this, R.raw.photo_credits_alt)).toString();
        		Fragment fragment = InfoDialog.newInstance(AboutActivity.this, dialogTitle, dialogText, altDialogText);
        		showDialog(fragment, INFO_DIALOG);
			}
        	
        });
        
        Button websiteButton = (Button) findViewById(R.id.website_button);
        websiteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.website_url)));
	            try {
	            	startActivity(intent);
	            } catch(ActivityNotFoundException ex) {
	            	
	            }
			}
        	
        });
        
        Button emailUsButton = (Button) findViewById(R.id.email_us_button);
        emailUsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_EMAIL, new String [] {getString(R.string.contact_email)});
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_subject));
				intent.setType("plain/text");
	            try {
	            	startActivity(intent);
	            } catch(ActivityNotFoundException ex) {
	            	
	            }
			}
        	
        });
        
        Button copyrightButton = (Button) findViewById(R.id.copyright_button);
        copyrightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String dialogTitle = getString(R.string.copyright);
				String dialogText = getString(R.string.university_copyright);
        		Fragment fragment = InfoDialog.newInstance(AboutActivity.this, dialogTitle, dialogText);
        		showDialog(fragment, INFO_DIALOG);
			}
        	
        });
        
        Button licenseButton = (Button) findViewById(R.id.license_button);
        licenseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String dialogTitle = getString(R.string.licenses);
				String dialogText = "This application uses code from the <a href=\"http://source.android.com\">Android Open Source Project</a> released under the <a href=\"http://www.apache.org/licenses/LICENSE-2.0.html\">Apache License, Version 2.0</a> " +
				                    "and <a href=\"https://github.com/nirhart/ParallaxScroll\">ParallaxScroll</a> released under the <a href=\"http://opensource.org/licenses/MIT\">MIT License</a>";
				Fragment fragment = LicenseDialog.newInstance(AboutActivity.this, dialogTitle, dialogText);
				showDialog(fragment, LICENSE_DIALOG);
			}
        	
        });
        
        Button facebookButton = (Button) findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.facebook_url)));
	            try {
	            	startActivity(intent);
	            } catch(ActivityNotFoundException ex) {
	            	
	            }
			}
        	
        });
        
        Button twitterButton = (Button) findViewById(R.id.twitter_button);
        twitterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.twitter_url)));
	            try {
	            	startActivity(intent);
	            } catch(ActivityNotFoundException ex) {
	            	
	            }
			}
        	
        });
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_activity_menu, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = null;
    	
        switch (item.getItemId()) {
        	case android.R.id.home:
        		intent = new Intent(this, MainActivity.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		startActivity(intent);
        		return true;
        	case R.id.menu_share:
        		intent = new Intent(Intent.ACTION_SEND);
        		intent.setType("text/plain");
        		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_url));
        		startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
    
    private void showDialog(Fragment fragment, String tag) {
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.add(0, fragment);
        ft.commit();
    }
}
