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

package org.iisgcp.waterwalk.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView {
    
	private Context mContext;
	
	public CustomImageView(Context context) {
        super(context);
        mContext = context;
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int width = getResources().getDisplayMetrics().widthPixels - Constants.PICTURE_PADDING - (Utils.getTitleBarHeight(mContext) * 2);
        int height = getResources().getDisplayMetrics().heightPixels - Constants.PICTURE_PADDING - (Utils.getTitleBarHeight(mContext) * 2);
        
        setMeasuredDimension(width, height);
    }
}
