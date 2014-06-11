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

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class DetailListAdapter extends BaseAdapter {
    private Context context;
    private List<RowItem> rowItems;
 
    public DetailListAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }
 
    // private view holder class
    private class ViewHolder {
        TextView txtTitle;
        TextView txtDescription;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
 
        LayoutInflater mInflater = (LayoutInflater)
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_simple_2, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.text1);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.text2);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        RowItem rowItem = (RowItem) getItem(position);
 
        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtDescription.setText(Html.fromHtml(rowItem.getDescription()).toString());
 
        return convertView;
    }
 
    @Override
    public int getCount() {
        return rowItems.size();
    }
 
    @Override
    public RowItem getItem(int position) {
        return rowItems.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}
