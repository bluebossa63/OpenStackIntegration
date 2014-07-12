/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ch.niceneasy.openstack.android.R;

/**
 * The Class SplashScreenActivity.
 * 
 * @author Daniele
 */
public class SplashScreenActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_splash);
		Button btRegister = (Button) findViewById(R.id.btnRegister);
		btRegister.setOnClickListener(new Button.OnClickListener() {

			
			public void onClick(View v) {
				startActivity(new Intent(SplashScreenActivity.this,
						SignupActivity.class));
			}
		});
		Button btLogin = (Button) findViewById(R.id.btnLogin);
		btLogin.setOnClickListener(new Button.OnClickListener() {

			
			public void onClick(View v) {
				startActivity(new Intent(SplashScreenActivity.this,
						LoginActivity.class));
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}
}