package ch.niceneasy.openstack.android.base;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

public abstract class OpenstackListActivity extends ListActivity {

	protected ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
	}

	protected OpenStackClientService getService() {
		return OpenStackClientService.getInstance();
	}

	protected OpenstackApplicationState getApplicationState() {
		return OpenstackApplicationState.getInstance();
	}

	protected abstract void setContentView();

	protected void showErrorDialog(String title, Exception e, final Intent onOK) {
		PromptDialogUtil.showErrorDialog(this, title, e, onOK);
	}

	public void showErrorDialog(int ressource, Exception e, final Intent onOK) {
		PromptDialogUtil.showErrorDialog(this, ressource, e, onOK);
	}

	protected void showDialog(String title, String message, final Intent onOK) {
		PromptDialogUtil.showDialog(this, title, message, onOK);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getApplicationState().shouldReturnToCaller()) {
			finish();
		}
	}

}
