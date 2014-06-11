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

package org.iisgcp.waterwalk.fragment;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.ImageResizer;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FactFragment extends Fragment {
	
    private TextView mFactText;
    private LinearLayout mLayout;
    private ImageView mImageView;
    
    private int mImageId;
    
    private ImageResizer mImageFetcher;
	
    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static FactFragment newInstance(String description, int imageId) {
    	FactFragment f = new FactFragment();

        // Supply title and poi input as an argument.
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_DESCRIPTION, description);
        args.putInt(Constants.INTENT_IMAGE_ID, imageId);
        f.setArguments(args);

        return f;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       
    	setRetainInstance(false);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fact, container, false);
        mFactText = (TextView) view.findViewById(R.id.fact_text);
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    	    mLayout = (LinearLayout) view.findViewById(R.id.picture_layout);
        } else {
            mImageView = (ImageView) view.findViewById(R.id.picture);
        }
        
        return view;
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

        mImageId = getArguments().getInt(Constants.INTENT_IMAGE_ID);
        
        mFactText.setText(Html.fromHtml(getArguments().getString(Constants.INTENT_DESCRIPTION)));
        mFactText.setMovementMethod(LinkMovementMethod.getInstance());

        // First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(getResources(), mImageId, options);
	    
    	int maxWidth;
    		
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        	maxWidth = getResources().getDisplayMetrics().widthPixels;
        } else {
        	maxWidth = getResources().getDisplayMetrics().heightPixels;
        }
	    
        final int targetWidth = maxWidth;
        double scale = (double) maxWidth / (double) options.outWidth;
        final int targetHeight = (int) (options.outHeight * scale);
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mImageView = new ImageView(getActivity());
    	    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(targetWidth, targetHeight);
    	    mImageView.setLayoutParams(layoutParams);
    	    mLayout.addView(mImageView);
        }

        mImageView.setImageDrawable(null);
        mImageView.setBackgroundColor(getResources().getColor(R.color.gray));
        
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = ((FactViewPagerFragment) getParentFragment()).getImageFetcher();
        mImageFetcher.setImageSize(targetWidth, targetHeight);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.loadImage(String.valueOf(mImageId), mImageView);
    }
}
