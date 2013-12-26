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
import android.widget.Toast;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.TaskResult;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;
import ch.niceneasy.openstack.android.tenant.TenantListViewActivity;

import com.woorea.openstack.keystone.model.User;

public class LoginActivity extends Activity {

	protected ProgressBar progressBar;

	// user.setEmail("luca@niceneasy.ch");
	// user.setUsername("lucaskylander");
	// user.setPassword("Benzolieren1");
	// user.setName("Luca Ulrich");
	EditText txtName;
	EditText txtUsername;
	EditText txtEmail;
	EditText txtPassword;

	SignupService signupService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
		progressBar.setVisibility(View.GONE);
		signupService = new SignupService();
		txtName = (EditText) findViewById(R.id.txtName);
		txtName.setText(signupService.getUser().getName());
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtUsername.setText(signupService.getUser().getUsername());
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtEmail.setText(signupService.getUser().getEmail());
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtPassword.setText(signupService.getUser().getPassword());
		Button btSubmit = (Button) findViewById(R.id.btnLogin);
		btSubmit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				signupService.getUser().setName(txtName.getText().toString());
				signupService.getUser().setUsername(
						txtUsername.getText().toString());
				signupService.getUser().setEmail(txtEmail.getText().toString());
				signupService.getUser().setPassword(
						txtPassword.getText().toString());
				progressBar.setVisibility(View.VISIBLE);
				SubscribeTask subscribeTask = new SubscribeTask();
				subscribeTask.execute();
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

	private class SubscribeTask extends
			AsyncTask<String, Object, TaskResult<User>> {
		
		@Override
		protected TaskResult<User> doInBackground(String... params) {
			try {
				signupService.register();
				return new TaskResult<User>(signupService.getUser());
			} catch (Exception e) {
				return new TaskResult<User>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<User> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				ServicePreferences.copyServiceValues(LoginActivity.this);
				Intent intent = new Intent(LoginActivity.this,
						TenantListViewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				startActivity(intent);
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(), null);
			}
		}

		protected void showErrorDialog(String title, Exception e,
				final Intent onOK) {
			showDialog(title, e.getMessage() != null ? e.getMessage() : e
					.getClass().getName(), onOK);
		}

		public void showErrorDialog(int ressource, Exception e,
				final Intent onOK) {
			// showErrorDialog("Fehlermeldung", e, onOK);
			Toast.makeText(LoginActivity.this,
					"Fehlermeldung: " + e.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
		}

		protected void showDialog(String title, String message,
				final Intent onOK) {
			AlertDialog alertDialog = new AlertDialog.Builder(
					LoginActivity.this).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(message);
			if (onOK != null) {
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								startActivity(onOK);
								return;
							}
						});
			}
			alertDialog.show();
		}

	}

}
