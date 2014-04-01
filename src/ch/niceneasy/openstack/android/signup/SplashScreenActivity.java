package ch.niceneasy.openstack.android.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ch.niceneasy.openstack.android.R;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_splash);
		Button btRegister = (Button) findViewById(R.id.btnRegister);
		btRegister.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashScreenActivity.this,
						SignupActivity.class));
			}
		});
		Button btLogin = (Button) findViewById(R.id.btnLogin);
		btLogin.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashScreenActivity.this,
						LoginActivity.class));
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}
}