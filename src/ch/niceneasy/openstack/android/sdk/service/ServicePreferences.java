package ch.niceneasy.openstack.android.sdk.service;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import ch.niceneasy.openstack.android.R;

public class ServicePreferences extends PreferenceActivity {
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void finish() {
		super.finish();
		OpenStackClientService.getInstance().resetConnection();
		OpenStackClientService.getInstance().updateService(getBaseContext());
	}	



}
