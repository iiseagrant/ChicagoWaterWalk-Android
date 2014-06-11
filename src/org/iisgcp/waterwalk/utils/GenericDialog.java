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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class GenericDialog extends DialogFragment {
	
	protected String mDialogTitle = null;
	protected String mDialogText = null;
	protected String mAltDialogText = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogTitle = getArguments().getString("dialog_title");
        mDialogText = getArguments().getString("dialog_text");
        mAltDialogText = getArguments().getString("alt_dialog_text");
    }
}
