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
import org.iisgcp.waterwalk.adapter.RowItem;
import org.iisgcp.waterwalk.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class RoutesMapFragment extends SupportMapFragment
		implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
		OnMyLocationButtonClickListener {

	private HashMap<Marker, RowItem> mMarkerInfoMap = new HashMap<Marker, RowItem>();
	
	private GoogleMap mMap;
    private LocationClient mLocationClient;

    private static int ZOOM = 13;
	private static double DEFAULT_LATITUDE = 41.888078;
	private static double DEFAULT_LONGITUDE = -87.612087;
	
    /**
     * Fragment initialization.  We way we want to be retained and
     * start our thread.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
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
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(DEFAULT_LATITUDE,
				DEFAULT_LONGITUDE), ZOOM);
	    mMap.animateCamera(cameraUpdate);
	}

	private void addDefaultMarkers() {
		TypedArray routes = getResources().obtainTypedArray(R.array.routes);
	        
	    for (int i = 0; i < routes.length(); i++) {
	        TypedArray route = getResources().obtainTypedArray(routes.getResourceId(i, -1));
	        	
        	String title = getString(route.getResourceId(0, -1));
        	float lat = route.getFloat(3, -1);
        	float lng = route.getFloat(4, -1);
	        
	        Marker marker = mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(lat, lng))
	        .title(title));
	        
			RowItem rowItem = new RowItem();
			rowItem.setResId(routes.getResourceId(i, -1));
			rowItem.setPosition(i);
	        mMarkerInfoMap.put(marker, rowItem);
			
	        route.recycle();
	    }
	        
	    routes.recycle();
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
				String title = marker.getTitle();
  				int resId = mMarkerInfoMap.get(marker).getResId();
				
				Intent intent = new Intent(RoutesMapFragment.this.getActivity(), PointOfInterestActivity.class);
          		intent.putExtra(Constants.INTENT_TITLE, title);
          		intent.putExtra(Constants.INTENT_RES_ID, resId);
      			intent.putExtra(Constants.INTENT_TAB, 1);
      			intent.putExtra(Constants.INTENT_POSITION, mMarkerInfoMap.get(marker).getPosition());
    			intent.putExtra(Constants.INTENT_DRAWER_POSITION, 0);
      			RoutesMapFragment.this.getActivity().startActivity(intent);
			}
  	    	
  	    });
  		
  		addDefaultMarkers();
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
