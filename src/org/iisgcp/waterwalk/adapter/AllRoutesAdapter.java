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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.iisgcp.waterwalk.R;
import org.iisgcp.waterwalk.utils.ImageResizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllRoutesAdapter extends BaseExpandableListAdapter {
    private ImageResizer mImageFetcher;
    private Context mContext;
    private List<RowItem> mListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<RowItem, List<RowItem>> mListDataChild;
 
    public AllRoutesAdapter(Context context, ImageResizer imageFetcher, HashMap<RowItem, List<RowItem>> items) {
    	mImageFetcher = imageFetcher;
        mContext = context;
        
        Set<RowItem> keys = items.keySet();
        
        mListDataHeader = new ArrayList<RowItem>(keys.size());
        
        for (RowItem key: keys) {
        	mListDataHeader.add(key);
        }
        
        mListDataChild = items;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mListDataChild.get(mListDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final RowItem rowItem = (RowItem) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_child, null);
        }
 
        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        title.setText(rowItem.getTitle());
        title.setContentDescription(rowItem.getTitle());
        
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        
        mImageFetcher.loadImage(String.valueOf(rowItem.getImageId()), imageView);
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        final RowItem rowItem = (RowItem) getGroup(groupPosition);
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_parent, null);
        }
 
        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        title.setText(rowItem.getTitle());
        title.setContentDescription(rowItem.getTitle());
 
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        
        mImageFetcher.loadImage(String.valueOf(rowItem.getImageId()), imageView);
        
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
