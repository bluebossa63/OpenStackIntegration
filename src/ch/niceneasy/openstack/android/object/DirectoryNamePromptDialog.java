/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.object;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import ch.niceneasy.openstack.android.R;

/**
 * The Class DirectoryNamePromptDialog.
 * 
 * @author Daniele
 */
public class DirectoryNamePromptDialog extends DialogFragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.prompt_folder_name, null))
				.setPositiveButton(R.string.done,
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int id) {
								EditText editText = (EditText) DirectoryNamePromptDialog.this
										.getDialog().findViewById(
												R.id.folder_name);
								((ObjectListViewActivity) getActivity())
										.createFolder(editText.getText()
												.toString());
								DirectoryNamePromptDialog.this.dismiss();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int id) {
								DirectoryNamePromptDialog.this.getDialog()
										.cancel();
							}
						});
		return builder.create();
	}

}
