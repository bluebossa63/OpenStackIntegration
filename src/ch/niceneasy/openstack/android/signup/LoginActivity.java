package ch.niceneasy.openstack.android.signup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.TaskResult;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;
import ch.niceneasy.openstack.android.tenant.TenantListViewActivity;

public class LoginActivity extends Activity {

	protected ProgressBar progressBar;

	EditText txtUsername;
	EditText txtPassword;
	TextView txtPleaseWait;

	SignupService signupService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
		progressBar.setVisibility(View.GONE);
		signupService = SignupService.getInstance();
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtUsername.setText(signupService.getUser().getUsername());
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtPassword.setText(signupService.getUser().getPassword());
		txtPleaseWait = (TextView) findViewById(R.id.txtPleaseWait);
		Button btLogin = (Button) findViewById(R.id.btnLogin);
		btLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				signupService.getUser().setUsername(
						txtUsername.getText().toString());
				signupService.getUser().setPassword(
						txtPassword.getText().toString());
				progressBar.setVisibility(View.VISIBLE);
				LoginTask loginTask = new LoginTask();
				loginTask.execute();
			}
		});
		Button btCancel = (Button) findViewById(R.id.btnCancel);
		btCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private class LoginTask extends
			AsyncTask<String, Object, TaskResult<LoginConfirmation>> {

		@Override
		protected TaskResult<LoginConfirmation> doInBackground(String... params) {
			try {
				LoginConfirmation loginConfirmation = signupService
						.login(LoginActivity.this);
				return new TaskResult<LoginConfirmation>(loginConfirmation);
			} catch (Exception e) {
				return new TaskResult<LoginConfirmation>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<LoginConfirmation> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				OpenStackClientService service = OpenStackClientService.getInstance();
				service.setKeystoneAuthUrl(result.getResult().getKeystoneAuthUrl());
				service.setKeystoneAdminAuthUrl(result.getResult().getKeystoneAdminAuthUrl());
				service.setKeystoneEndpoint(result.getResult().getKeystoneEndpoint());
				service.setTenantName(result.getResult().getTenantName());
				service.setKeystonePassword(signupService.getUser().getPassword());
				service.setKeystoneUsername(signupService.getUser().getUsername());
				signupService.getUser().setId(result.getResult().getUser().getId());
				signupService.getUser().setTenantId(result.getResult().getUser().getTenantId());
				ServicePreferences.copyServiceValues(LoginActivity.this);
				Intent intent = new Intent(LoginActivity.this,
						TenantListViewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(), null);
			}
		}

	}

	protected void showErrorDialog(String title, Exception e, final Intent onOK) {
		showDialog(title, e.getMessage() != null ? e.getMessage() : e
				.getClass().getName(), onOK);
	}

	public void showErrorDialog(int ressource, Exception e, final Intent onOK) {
		// showErrorDialog("Fehlermeldung", e, onOK);
		Toast.makeText(LoginActivity.this,
				"Fehlermeldung: " + e.getLocalizedMessage(), Toast.LENGTH_LONG)
				.show();
	}

	protected void showDialog(String title, String message, final Intent onOK) {
		AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
				.create();
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
}