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

public class RowItem {
    
	private int mImageId = -1;
    private String mTitle;
    private String mDescription;
    private int mResId = -1;
    private int mPosition = -1;
    
    /**
     * Default constructor
     */
    public RowItem() {
    	
    }
    
    @Override
    public String toString() {
    	return mTitle;
    }
    
	/**
	 * @return the mImageId
	 */
	public int getImageId() {
		return mImageId;
	}

	/**
	 * @param mImageId the mImageId to set
	 */
	public void setImageId(int mImageId) {
		this.mImageId = mImageId;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param mTitle the mTitle to set
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @return the mDescription
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @param mDescription the mDescription to set
	 */
	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	
	/**
	 * @return the mResId
	 */
	public int getResId() {
		return mResId;
	}

	/**
	 * @param mResId the mResId to set
	 */
	public void setResId(int mResId) {
		this.mResId = mResId;
	}

	/**
	 * @return the mPosition
	 */
	public int getPosition() {
		return mPosition;
	}

	/**
	 * @param mPosition the mPosition to set
	 */
	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}
}
