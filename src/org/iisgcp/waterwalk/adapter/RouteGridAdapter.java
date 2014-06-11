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

package org.iisgcp.waterwalk.adapter;

import java.util.List;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.utils.ImageResizer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class RouteGridAdapter extends BaseAdapter {
    private ImageResizer mImageFetcher;
	private Context mContext;
    private List<RowItem> mRowItems;
 
    public RouteGridAdapter(Context context, ImageResizer imageFetcher, List<RowItem> items) {
    	super();
    	mImageFetcher = imageFetcher;
        mContext = context;
        mRowItems = items;
    }
 
    @Override
    public int getCount() {
        return mRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    // private view holder class
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder = null;
    	LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    	
        // Now handle the main ImageView thumbnails
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_complex, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.list_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        RowItem rowItem = (RowItem) getItem(position);
        
        holder.txtTitle.setText(rowItem.getTitle());
        
        // Finally load the image asynchronously into the ImageView, this also takes care of
        // setting a placeholder image while the background thread runs
        mImageFetcher.loadImage(String.valueOf(mRowItems.get(position).getImageId()), holder.imageView);
        return convertView;
    }
}
