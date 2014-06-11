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
import org.iisgcp.waterwalk.utils.ImageFetcher;
import org.iisgcp.waterwalk.utils.Utils;
import org.iisgcp.waterwalk.utils.ImageCache.ImageCacheParams;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FactViewPagerFragment extends Fragment {

	private static final String INTENT_POSITION = "position";	
	private static final String INTENT_RESID = "resId";	
    private static final String IMAGE_CACHE_DIR = "fact_thumbs";
	
    private ImageFetcher mImageFetcher;
    
	private ViewPager mPager;
	private FactPagerAdapter mAdapter;
	
    public static FactViewPagerFragment newInstance(int position, int resId) {
    	FactViewPagerFragment f = new FactViewPagerFragment();

        // Supply title and poi input as an argument.
        Bundle args = new Bundle();
        args.putInt(INTENT_POSITION, position);
        args.putInt(INTENT_RESID, resId);
        f.setArguments(args);

        return f;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       
    	setRetainInstance(true);
    	
    	ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), Utils.getLargestGridDimension(getActivity()));
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fact_view_pager, container, false);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        return view;
    }
    
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

        // Instantiate a ViewPager and a PagerAdapter.
    	mAdapter = new FactPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageSelected(int position) {
	        	String title = null;
	        	
	        	int count = 0;
	        	
	            int id = getArguments().getInt(INTENT_RESID);
	        	
	        	TypedArray details = FactViewPagerFragment.this.getResources().obtainTypedArray(id);
	        	
	            for (int i = 0; i < details.length(); i = i + 3) {
	            	title = getString(details.getResourceId(i, -1));
	            	if (position == count) {
	             		break;
	             	}
	             	count++;
	            }
	            
	            details.recycle();
	            
	            FactViewPagerFragment.this.getActivity().setTitle(title);
			}
        	
        });
        mPager.setCurrentItem(getArguments().getInt(INTENT_POSITION));
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }
    
    private class FactPagerAdapter extends FragmentStatePagerAdapter {
    	public FactPagerAdapter(FragmentManager fm) {
    		super(fm);
        }
    	
        @Override
        public Fragment getItem(int position) {
        	Fragment fragment = null;
        	
        	int count = 0;
        	
        	String description = null;
        	int img = -1;
        	
            int id = getArguments().getInt(INTENT_RESID);
        	
        	TypedArray details = FactViewPagerFragment.this.getResources().obtainTypedArray(id);
            
            for (int i = 0; i < details.length(); i = i + 3) {
                int j = i + 1;
             	description = Utils.getRawString(getActivity(), details.getResourceId(j, -1)).replaceAll("<title.*.title>", "");
             	j++;
             	img = details.getResourceId(j, -1);
             	if (position == count) {
             		break;
             	}
             	count++;
            }
             
            details.recycle();
        	
        	fragment = FactFragment.newInstance(description, img);

            return fragment;
        }

        @Override
        public int getCount() {
        	int count = 0;
        	
            int id = getArguments().getInt(INTENT_RESID);
        	
        	TypedArray details = FactViewPagerFragment.this.getResources().obtainTypedArray(id);
            
            for (int i = 0; i < details.length(); i = i + 3) {
             	count++;
            }
             
            details.recycle();
            
            return count;
        }
        
        @Override
        public Parcelable saveState() {
        	return null;
        }
    }
}
