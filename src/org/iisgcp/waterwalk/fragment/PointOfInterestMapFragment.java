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

import java.util.HashMap;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.activity.PointOfInterestActivity;
import org.iisgcp.waterwalk.activity.PointOfInterestDetailActivity;
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;
import org.iisgcp.waterwalk.utils.OnItemClickedListener;
import org.iisgcp.waterwalk.utils.OnPageSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PointOfInterestMapFragment extends SupportMapFragment
		implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
		OnMyLocationButtonClickListener {

	private static final String INTENT_POSITION = "position";	
	
	private HashMap<Marker, RowItem> mMarkerInfoMap = new HashMap<Marker, RowItem>();
	
	private OnItemClickedListener mItemClickedListener;
	private OnPageSelectedListener mPageSelectedListener;
	
	private GoogleMap mMap;
    private LocationClient mLocationClient;

    private int mZoom;
	private double mDefaultLatitude;
	private double mDefaultLongitude;
	
	private View mPrevious;
	private View mNext;
	
	private int [] mPois;
	
	private int mIndex = 0;
	
	/**
     * Create a new instance of PointOfInterestListFragment
     */
    public static PointOfInterestMapFragment newInstance(int position) {
    	PointOfInterestMapFragment f = new PointOfInterestMapFragment();
    	
        // Supply title and poi input as an argument.
        Bundle args = new Bundle();
        args.putInt(INTENT_POSITION, position);
        f.setArguments(args);

        return f;
    }
	
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
       
    	try {
    		mItemClickedListener = (OnItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    	
    	try {
    		mPageSelectedListener = (OnPageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPageSelectedListener");
        }
    }

    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
    }
	
	@SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        mPrevious = new ImageButton(getActivity());
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        mPrevious.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.previous));
        mPrevious.setLayoutParams(params);
        
        mNext = new ImageButton(getActivity());
        params = new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mNext.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.next));
        mNext.setLayoutParams(params);
        
        ((FrameLayout) view).addView(mPrevious);
        ((FrameLayout) view).addView(mNext); 
        
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		TypedArray routes = getResources().obtainTypedArray(R.array.routes);
		
		mPois = new int[routes.length()]; 
		
		for (int i = 0; i < routes.length(); i++) {
			mPois[i] = routes.getResourceId(i, -1);
		}
		
		routes.recycle();
		
		mPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIndex == 0) {
					mIndex = 3;
				} else {
					mIndex--;
				}
				
				updateMarkers(mIndex);
			}
		});
		
		mNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIndex == 3) {
					mIndex = 0;
				} else {
					mIndex++;
				}
				
				updateMarkers(mIndex);
			}
		});
		
	    if (savedInstanceState == null) {
	    	// First incarnation of this activity.
	        setRetainInstance(true);
	    } else {
	        // Reincarnated activity. The obtained map is the same map instance in the previous
	            // activity life cycle. There is no need to reinitialize it.
	        mMap = getMap();
	    }
	    setUpMapIfNeeded();
	}
	
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }
	
    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
    
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.routes_map_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_route_locations:
        		goToDefaultLocation();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
	
	private void goToDefaultLocation() {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mDefaultLatitude,
				mDefaultLongitude), mZoom);
	    mMap.animateCamera(cameraUpdate);
	}

	private void addMarkers(int resId) {
		TypedArray pois = getResources().obtainTypedArray(resId);
	        
	    for (int i = 0; i < pois.length(); i++) {
	        TypedArray poi = getResources().obtainTypedArray(pois.getResourceId(i, -1));
	        
	        String title = getString(poi.getResourceId(0, -1));
	        float lat = poi.getFloat(3, -1);
	        float lng = poi.getFloat(4, -1);
	        
			Marker marker = mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(lat, lng))
	        .title(title));
	        
			RowItem rowItem = new RowItem();
			rowItem.setResId(pois.getResourceId(i, -1));
	        mMarkerInfoMap.put(marker, rowItem);
			
	        poi.recycle();
	    }
	        
	    pois.recycle();
	}

	public void update(int position) {
		mIndex = position;
		updateMarkers(mIndex);
	}
	
	private void updateMarkers(int position) {
		mPageSelectedListener.onPageSelected(position);
		
		int resId = mPois[position];
		
		TypedArray route = getResources().obtainTypedArray(resId);
		
		((PointOfInterestActivity) getActivity()).setTitle(getString(route.getResourceId(0, -1)));
		
		TypedArray pois = getResources().obtainTypedArray(route.getResourceId(5, -1));
	    
		route.recycle();
		
		mMap.clear();
		mMarkerInfoMap.clear();
		
	    for (int i = 0; i < pois.length(); i++) {
	        TypedArray poi = getResources().obtainTypedArray(pois.getResourceId(i, -1));
	        
	        String title = getString(poi.getResourceId(0, -1));
	        float lat = poi.getFloat(3, -1);
	        float lng = poi.getFloat(4, -1);
	        
			Marker marker = mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(lat, lng))
	        .title(title));
	        
			RowItem rowItem = new RowItem();
			rowItem.setResId(pois.getResourceId(i, -1));
	        mMarkerInfoMap.put(marker, rowItem);
			
	        poi.recycle();
	    }
	        
	    pois.recycle();
	}
	
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }
	
    private void setUpMap() {
    	mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
  	    mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(PointOfInterestMapFragment.this.getActivity(), PointOfInterestDetailActivity.class);
				intent.putExtra(Constants.INTENT_TITLE, marker.getTitle());
				intent.putExtra(Constants.INTENT_RES_ID, mMarkerInfoMap.get(marker).getResId());
				mItemClickedListener.onItemClicked(intent);
			}
  	    	
  	    });
  	    
  		TypedArray routes = getResources().obtainTypedArray(R.array.routes);
  		int resId = routes.getResourceId(getArguments().getInt(INTENT_POSITION), -1);
  		routes.recycle();
    	TypedArray route = getResources().obtainTypedArray(resId);
    	
    	mZoom = route.getInteger(2, -1);
    	mDefaultLatitude = route.getFloat(3, -1);
    	mDefaultLongitude = route.getFloat(4, -1);
    	
        addMarkers(route.getResourceId(5, -1));
    	route.recycle();
        goToDefaultLocation();
    }
    
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
            		getActivity().getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    private boolean showMyLocation() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            Location location = mLocationClient.getLastLocation();
            
            if (location == null) {
            	final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            	if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            		startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            		return true;
            	} else {
                	Toast.makeText(getActivity().getApplicationContext(), R.string.gps_error, Toast.LENGTH_LONG).show();
                	return true;
            	}
            }
        }
        
        return false;
    }

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
		return showMyLocation();
	}
}
