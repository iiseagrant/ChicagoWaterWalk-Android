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

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment
		implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
		OnMyLocationButtonClickListener {

	private GoogleMap mMap;
    private LocationClient mLocationClient;

    private static int ZOOM = 13;
	private static double DEFAULT_LATITUDE = -1;
	private static double DEFAULT_LONGITUDE = -1;
	
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
        	String title = getActivity().getIntent().getStringExtra(Constants.INTENT_TITLE);
        	getActivity().setTitle(title);
        	DEFAULT_LATITUDE = getActivity().getIntent().getFloatExtra(Constants.INTENT_LATITUDE, -1);
        	DEFAULT_LONGITUDE = getActivity().getIntent().getFloatExtra(Constants.INTENT_LONGITUDE, -1);
	        
	        mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
	        .title(title));
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
