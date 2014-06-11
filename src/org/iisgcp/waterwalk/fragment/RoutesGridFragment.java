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
import java.util.List;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.activity.AboutActivity;
import org.iisgcp.waterwalk.activity.PointOfInterestActivity;
import org.iisgcp.waterwalk.adapter.RouteGridAdapter;
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.ImageFetcher;
import org.iisgcp.waterwalk.utils.Utils;
import org.iisgcp.waterwalk.utils.ImageCache.ImageCacheParams;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class RoutesGridFragment extends Fragment {
	
    private static final String IMAGE_CACHE_DIR = "route_thumbs";
	
    private RouteGridAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    
	private GridView mList;
	
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
        View view = inflater.inflate(R.layout.fragment_routes_grid, container, false);
        mList = (GridView) view.findViewById(R.id.gridview);
        return view;
    }
    
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        
        List<RowItem> rowItems = null;
        
        TypedArray routes = getResources().obtainTypedArray(R.array.routes);
        
        List<Integer> imageIds = new ArrayList<Integer>();
    	
        rowItems = new ArrayList<RowItem>();
        
        for (int i = 0; i < routes.length(); i++) {
        	TypedArray route = getResources().obtainTypedArray(routes.getResourceId(i, -1));
        	
        	String title = getString(route.getResourceId(0, -1));
        	int imageId = route.getResourceId(1, -1);

        	imageIds.add(imageId);
        	
        	rowItems.add(buildRow(imageId, title, routes.getResourceId(i, -1)));
        	route.recycle();
        }
        
        routes.recycle();
        
      	mAdapter = new RouteGridAdapter(getActivity(), mImageFetcher, rowItems);
        
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				RowItem rowItem = (RowItem) parent.getAdapter().getItem(position);
				
				Intent intent = new Intent(RoutesGridFragment.this.getActivity(), PointOfInterestActivity.class);
        		intent.putExtra(Constants.INTENT_TITLE, rowItem.getTitle());
        		intent.putExtra(Constants.INTENT_RES_ID, rowItem.getResId());
    			intent.putExtra(Constants.INTENT_TAB, 0);
    			intent.putExtra(Constants.INTENT_POSITION, position);
    			intent.putExtra(Constants.INTENT_DRAWER_POSITION, 0);
				RoutesGridFragment.this.startActivity(intent);
			}
        	
        });
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
    
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.routes_grid_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_about:
        		getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
    
    private RowItem buildRow(int imageId, String title, int resId) {
    	RowItem rowItem = new RowItem();
    	rowItem.setImageId(imageId);
    	rowItem.setTitle(title);
    	rowItem.setResId(resId);
    	
    	return rowItem;
    }
}
