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
import org.iisgcp.waterwalk.activity.PointOfInterestDetailActivity;
import org.iisgcp.waterwalk.adapter.RouteGridAdapter;
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.OnItemClickedListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class PointOfInterestGridFragment extends Fragment {

    private RouteGridAdapter mAdapter;
	
	private GridView mList;
    
	private OnItemClickedListener mListener;
	
	/**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static PointOfInterestGridFragment newInstance(String title, int resid) {
    	PointOfInterestGridFragment f = new PointOfInterestGridFragment();

        // Supply title and poi input as an argument.
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_TITLE, title);
        args.putInt(Constants.INTENT_RES_ID, resid);
        f.setArguments(args);

        return f;
    }
	
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
       
    	try {
            mListener = (OnItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
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
        View view = inflater.inflate(R.layout.fragment_point_of_interest_grid, container, false);
        mList = (GridView) view.findViewById(R.id.gridview);
        return view;
    }
    
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);

    	List<RowItem> rowItems = null;

    	TypedArray route = getResources().obtainTypedArray(getArguments().getInt(Constants.INTENT_RES_ID));
    	TypedArray pois = getResources().obtainTypedArray(route.getResourceId(5, -1));
    	route.recycle();

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

      	mAdapter = new RouteGridAdapter(getActivity(), ((PointOfInterestGridViewPagerFragment) getParentFragment()).getImageFetcher(), rowItems);

    	mList.setAdapter(mAdapter);
    	mList.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,
    				long id) {
    			RowItem rowItem = (RowItem) parent.getAdapter().getItem(position);
    			
    			Intent intent = new Intent(PointOfInterestGridFragment.this.getActivity(), PointOfInterestDetailActivity.class);
    			intent.putExtra(Constants.INTENT_TITLE, rowItem.getTitle());
    			intent.putExtra(Constants.INTENT_RES_ID, rowItem.getResId());
    			mListener.onItemClicked(intent);
    		}

    	});
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private RowItem buildRow(int imageId, String title, int resId) {
    	RowItem rowItem = new RowItem();
    	rowItem.setImageId(imageId);
    	rowItem.setTitle(title);
    	rowItem.setResId(resId);
    	
    	return rowItem;
    }
}
