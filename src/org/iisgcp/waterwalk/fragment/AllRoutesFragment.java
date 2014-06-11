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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.activity.PointOfInterestDetailActivity;
import org.iisgcp.waterwalk.adapter.AllRoutesAdapter;
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.ImageFetcher;
import org.iisgcp.waterwalk.utils.Utils;
import org.iisgcp.waterwalk.utils.ImageCache.ImageCacheParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class AllRoutesFragment extends Fragment {
	
    private static final String IMAGE_CACHE_DIR = "all_route_thumbs";
	
    private AllRoutesAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    
	private ExpandableListView mList;
	
    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Tell the framework to try to keep this fragment around
        // during a configuration change.
        setRetainInstance(true);
        setHasOptionsMenu(true);
        
        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

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
        View view = inflater.inflate(R.layout.fragment_all_routes, container, false);
        mList = (ExpandableListView) view.findViewById(android.R.id.list);
        return view;
    }
    
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        
      	mAdapter = new AllRoutesAdapter(getActivity(), mImageFetcher, getListHeaders());      	 
        
        mList.setAdapter(mAdapter);
        mList.setFastScrollEnabled(true);
        mList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				RowItem rowItem = (RowItem) mAdapter.getChild(groupPosition, childPosition);
				
    			Intent intent = new Intent(AllRoutesFragment.this.getActivity(), PointOfInterestDetailActivity.class);
    			intent.putExtra(Constants.INTENT_TITLE, rowItem.getTitle());
    			intent.putExtra(Constants.INTENT_RES_ID, rowItem.getResId());
    			intent.putExtra(Constants.INTENT_DRAWER_POSITION, 1);
    			AllRoutesFragment.this.startActivity(intent);
				return false;
			}
        	
        });
        
        ViewTreeObserver vto = mList.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	@SuppressLint("NewApi")
        	@Override
        	public void onGlobalLayout() {
        		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        			mList.setIndicatorBounds(mList.getRight()- 70, mList.getWidth());
        		} else {
        			mList.setIndicatorBoundsRelative(mList.getRight()- 70, mList.getWidth());
        		}
        	}
        });
    }

    @SuppressLint("NewApi")
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
    
    private HashMap<RowItem, List<RowItem>> getListHeaders() {
    	LinkedHashMap<RowItem, List<RowItem>> rowItems = null;
        
        TypedArray routes = getResources().obtainTypedArray(R.array.routes);
        
        List<Integer> imageIds = new ArrayList<Integer>();
    	
        rowItems = new LinkedHashMap<RowItem, List<RowItem>>();
        
        for (int i = 0; i < routes.length(); i++) {
        	TypedArray route = getResources().obtainTypedArray(routes.getResourceId(i, -1));
        	
        	String title = getString(route.getResourceId(0, -1));
        	int imageId = route.getResourceId(1, -1);

        	imageIds.add(imageId);
        	
        	rowItems.put(buildRow(imageId, title, routes.getResourceId(i, -1)), getListChildren(route.getResourceId(5, -1)));
        	
        	route.recycle();
        }
        
        routes.recycle();
        
        return rowItems;
    }
    
    private List<RowItem> getListChildren(int resId) {
    	List<RowItem> rowItems = null;

    	TypedArray pois = getResources().obtainTypedArray(resId);

        List<Integer> imageIds = new ArrayList<Integer>();
    	
    	rowItems = new ArrayList<RowItem>();

    	for (int i = 0; i < pois.length(); i++) {
    		TypedArray poi = getResources().obtainTypedArray(pois.getResourceId(i, -1));

    		String title = getString(poi.getResourceId(0, -1));
    		int imageId = poi.getResourceId(1, -1);

    		imageIds.add(imageId);
    		
    		rowItems.add(buildRow(imageId, title, pois.getResourceId(i, -1)));
    		poi.recycle();
    	}

    	pois.recycle();
    	
    	return rowItems;
    }
    
    private RowItem buildRow(int imageId, String title, int resId) {
    	RowItem rowItem = new RowItem();
    	rowItem.setImageId(imageId);
    	rowItem.setTitle(title);
    	rowItem.setResId(resId);
    	
    	return rowItem;
    }
}
