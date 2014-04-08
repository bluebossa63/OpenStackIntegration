/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

/**
 * The Class PromptDialogUtil.
 * 
 * @author Daniele
 */
public class PromptDialogUtil {

	/**
	 * Show error dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	public static void showErrorDialog(final Context context, String title,
			Exception e, final Intent onOK) {
		showDialog(context, title, e.getMessage() != null ? e.getMessage() : e
				.getClass().getName(), onOK);
	}

	/**
	 * Show error dialog.
	 * 
	 * @param context
	 *            the context
	 * @param ressource
	 *            the ressource
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	public static void showErrorDialog(final Context context, int ressource,
			Exception e, final Intent onOK) {
		// showErrorDialog("Fehlermeldung", e, onOK);
		Toast.makeText(context, "Fehlermeldung: " + e.getLocalizedMessage(),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Show dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param onOK
	 *            the on ok
	 */
	public static void showDialog(final Context context, String title,
			String message, final Intent onOK) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		if (onOK != null) {
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(onOK);
							return;
						}
					});
		}
		alertDialog.show();
	}

}
