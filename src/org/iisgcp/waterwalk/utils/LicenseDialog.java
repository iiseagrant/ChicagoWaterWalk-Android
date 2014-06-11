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

import org.iisgcp.waterwalk.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicenseDialog extends DialogFragment {
	
	private AlertDialog mDialog = null;
	
	private String mDialogTitle = null;
	private String mDialogText = null;
	
    /* Call this to instantiate a new InfoDialog.
     * @param activity  The activity hosting the dialog
     * @returns A new instance of InfoDialog.
     */
    public static LicenseDialog newInstance(Activity activity, String dialogTitle, String dialogText) {
    	LicenseDialog frag = new LicenseDialog();
        
   	 	// Supply dialog text as an argument.
        Bundle args = new Bundle();
        args.putString("dialog_title", dialogTitle);
        args.putString("dialog_text", dialogText);
        frag.setArguments(args);
        
        return frag;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogTitle = getArguments().getString("dialog_title");
        mDialogText = getArguments().getString("dialog_text");
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(mDialogTitle);
    	builder.setMessage(Html.fromHtml(mDialogText));
        builder.setCancelable(true);
        builder.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.dismiss();
    		}
		});
        // Create the AlertDialog object and return it
        mDialog = builder.create();
        return mDialog;
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	   // Make the textview clickable. Must be called after show()
        TextView message = (TextView) mDialog.findViewById(android.R.id.message);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setContentDescription(Html.fromHtml(mDialogText).toString());
    }
}
