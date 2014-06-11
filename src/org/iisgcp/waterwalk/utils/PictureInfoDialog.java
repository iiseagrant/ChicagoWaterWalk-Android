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
import android.view.View;
import android.widget.TextView;

public class PictureInfoDialog extends GenericDialog {
	
    /* Call this to instantiate a new PictureInfoDialog.
     * @param activity  The activity hosting the dialog
     * @returns A new instance of PictureInfoDialog.
     */
    public static PictureInfoDialog newInstance(Activity activity, String dialogTitle, String dialogText, String altDialogText) {
    	PictureInfoDialog frag = new PictureInfoDialog();
        
   	 	// Supply dialog text as an argument.
        Bundle args = new Bundle();
        args.putString("dialog_title", dialogTitle);
        args.putString("dialog_text", dialogText);
        args.putString("alt_dialog_text", altDialogText);
        frag.setArguments(args);
        
        return frag;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_picture_info, null);
	    TextView messageText = (TextView) view.findViewById(R.id.message_text);
	    messageText.setText(mDialogText);
	    if (mAltDialogText != null) {
	    	messageText.setContentDescription(mAltDialogText);
	    }
	    builder.setView(view);
    	builder.setTitle(mDialogTitle);
        builder.setCancelable(true);
        builder.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.dismiss();
    		}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
