/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.base;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

/**
 * The Class OpenstackListActivity.
 * 
 * @author Daniele
 */
public abstract class OpenstackListActivity extends ListActivity {

	/** The progress bar. */
	protected ProgressBar progressBar;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	protected OpenStackClientService getService() {
		return OpenStackClientService.getInstance();
	}

	/**
	 * Gets the application state.
	 * 
	 * @return the application state
	 */
	protected OpenstackApplicationState getApplicationState() {
		return OpenstackApplicationState.getInstance();
	}

	/**
	 * Sets the content view.
	 */
	protected abstract void setContentView();

	/**
	 * Show error dialog.
	 * 
	 * @param title
	 *            the title
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	protected void showErrorDialog(String title, Exception e, final Intent onOK) {
		PromptDialogUtil.showErrorDialog(this, title, e, onOK);
	}

	/**
	 * Show error dialog.
	 * 
	 * @param ressource
	 *            the ressource
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	public void showErrorDialog(int ressource, Exception e, final Intent onOK) {
		PromptDialogUtil.showErrorDialog(this, ressource, e, onOK);
	}

	/**
	 * Show dialog.
	 * 
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param onOK
	 *            the on ok
	 */
	protected void showDialog(String title, String message, final Intent onOK) {
		PromptDialogUtil.showDialog(this, title, message, onOK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	
	protected void onStart() {
		super.onStart();
		if (getApplicationState().shouldReturnToCaller()) {
			finish();
		}
	}

}
