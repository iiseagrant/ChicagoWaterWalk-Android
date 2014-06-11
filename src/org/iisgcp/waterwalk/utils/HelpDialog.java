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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class HelpDialog extends GenericDialog implements DialogInterface.OnCancelListener {
	
	public interface HelpDialogListener {
		public void onHelpDialogClosed(DialogFragment dialog);
	}
	
    // Use this instance of the interface to deliver action events
    static HelpDialogListener mListener;
	
    /* Call this to instantiate a new HelpDialog.
     * @param activity  The activity hosting the dialog
     * @returns A new instance of HelpDialog.
     */
    public static HelpDialog newInstance(Activity activity, String dialogTitle, String dialogText) {
    	// Verify that the host activity implements the callback interface
        try {
            // Instantiate the HelpDialogListener so we can send events with it
            mListener = (HelpDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement HelpDialogListener");
        }
    	
    	HelpDialog frag = new HelpDialog();
        
   	 	// Supply dialog text as an argument.
        Bundle args = new Bundle();
        args.putString("dialog_title", dialogTitle);
        args.putString("dialog_text", dialogText);
        frag.setArguments(args);
        
        return frag;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(mDialogTitle);
    	builder.setMessage(mDialogText);
        builder.setCancelable(false);
        builder.setOnCancelListener(this);
        builder.setNeutralButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.dismiss();
    			mListener.onHelpDialogClosed(HelpDialog.this);
    		}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    @Override
    public void onCancel(DialogInterface dialog) {
 		mListener.onHelpDialogClosed(HelpDialog.this);
     }
}
