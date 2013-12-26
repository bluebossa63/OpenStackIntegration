package ch.niceneasy.openstack.android.base;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
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
		showDialog(title, e.getMessage() != null ? e.getMessage() : e
				.getClass().getName(), onOK);
	}

	public void showErrorDialog(int ressource, Exception e, final Intent onOK) {
		// showErrorDialog("Fehlermeldung", e, onOK);
		Toast.makeText(this, "Fehlermeldung: " + e.getLocalizedMessage(),
				Toast.LENGTH_LONG).show();
	}

	protected void showDialog(String title, String message, final Intent onOK) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		if (onOK != null) {
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(onOK);
							return;
						}
					});
		}
		alertDialog.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getApplicationState().shouldReturnToCaller()) {
			finish();
		}
	}

}
