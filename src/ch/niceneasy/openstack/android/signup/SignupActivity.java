package ch.niceneasy.openstack.android.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.PromptDialogUtil;
import ch.niceneasy.openstack.android.base.TaskResult;

import com.woorea.openstack.keystone.model.User;

public class SignupActivity extends Activity {

	protected ProgressBar progressBar;

	EditText txtName;
	EditText txtUsername;
	EditText txtEmail;
	EditText txtPassword;
	TextView txtPleaseWait;

	SignupService signupService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
		progressBar.setVisibility(View.GONE);
		signupService = SignupService.getInstance();
		txtName = (EditText) findViewById(R.id.txtName);
		txtName.setText(signupService.getUser().getName());
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtUsername.setText(signupService.getUser().getUsername());
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtEmail.setText(signupService.getUser().getEmail());
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtPassword.setText(signupService.getUser().getPassword());
		txtPleaseWait = (TextView) findViewById(R.id.txtPleaseWait);
		Button btRegister = (Button) findViewById(R.id.btnRegister);
		btRegister.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				signupService.getUser().setName(
						txtUsername.getText().toString());
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
				Intent intent = new Intent(SignupActivity.this,
						LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.putExtra("registered", true);
				startActivity(intent);
			} else {
				PromptDialogUtil.showErrorDialog(SignupActivity.this,
						R.string.error_dlg, result.getException(), null);
			}
		}
	}
}
