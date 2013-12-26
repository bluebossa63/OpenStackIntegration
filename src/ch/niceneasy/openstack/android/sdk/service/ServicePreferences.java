package ch.niceneasy.openstack.android.sdk.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.signup.SignupService;

import com.woorea.openstack.keystone.model.User;

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
		updateService(getBaseContext());
	}

	public static void copyServiceValues(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		OpenStackClientService service = OpenStackClientService.getInstance();
		Editor edit = prefs.edit();
		edit.putString(context.getString(R.string.SIGNUP_ENDPOINT),
				SignupService.getInstance().getSignupURL());
		User user = SignupService.getInstance().getUser();
		edit.putString(context.getString(R.string.FULLNAME), user.getName());
		edit.putString(context.getString(R.string.USERNAME), user.getUsername());
		edit.putString(context.getString(R.string.EMAIL), user.getEmail());
		edit.putString(context.getString(R.string.PASSWORD), user.getPassword());
		edit.putString(context.getString(R.string.TENANT_ID), user.getTenantId());		
		edit.putString(context.getString(R.string.USERID), user.getId());		
		service.setKeystoneUsername(user.getUsername());
		service.setKeystonePassword(user.getPassword());
//		edit.putString(context.getString(R.string.USERNAME),
//				service.getKeystoneUsername());
//		edit.putString(context.getString(R.string.PASSWORD),
//				service.getKeystonePassword());
		edit.putString(context.getString(R.string.TENANT_NAME),
				service.getTenantName());
		edit.putString(context.getString(R.string.KEYSTONE_AUTH_URL),
				service.getKeystoneAuthUrl());
		edit.putString(context.getString(R.string.KEYSTONE_ADMIN_AUTH_URL),
				service.getKeystoneAdminAuthUrl());
		edit.putString(context.getString(R.string.KEYSTONE_ENDPOINT),
				service.getKeystoneEndpoint());
		edit.putString(context.getString(R.string.NOVA_ENDPOINT),
				service.getNovaEndpoint());
		edit.putString(context.getString(R.string.CEILOMETER_ENDPOINT),
				service.getCeilometerEndpoint());

		edit.commit();
		dump(context);
	}

	public static void updateService(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		OpenStackClientService service = OpenStackClientService.getInstance();
//		service.setKeystoneUsername(prefs.getString(
//				context.getString(R.string.USERNAME), "admin"));
//		service.setKeystonePassword(prefs.getString(
//				context.getString(R.string.PASSWORD), "adminPassword"));
		service.setTenantName(prefs.getString(
				context.getString(R.string.TENANT_NAME), "demo"));
		service.setKeystoneAuthUrl(prefs.getString(
				context.getString(R.string.KEYSTONE_AUTH_URL),
				"http://192.168.0.20:5000/v2.0/"));
		service.setKeystoneAdminAuthUrl(prefs.getString(
				context.getString(R.string.KEYSTONE_ADMIN_AUTH_URL),
				"http://192.168.0.20:35357/v2.0/"));
		service.setKeystoneEndpoint(prefs.getString(
				context.getString(R.string.KEYSTONE_ENDPOINT),
				"http://192.168.0.20:8776/v2.0/"));
		service.setNovaEndpoint(prefs.getString(
				context.getString(R.string.NOVA_ENDPOINT),
				"http://192.168.0.20:8774/v2/"));
		service.setCeilometerEndpoint(prefs.getString(
				context.getString(R.string.CEILOMETER_ENDPOINT), ""));
		SignupService signupService = SignupService.getInstance();
		signupService.setSignupURL(prefs.getString(
				context.getString(R.string.SIGNUP_ENDPOINT), SignupService.DEFAULT_SIGNUP_URL));
		User user = signupService.getUser();
		user.setName(prefs.getString(context.getString(R.string.FULLNAME), ""));
		user.setUsername(prefs.getString(context.getString(R.string.USERNAME),
				""));
		user.setEmail(prefs.getString(context.getString(R.string.EMAIL), ""));
		user.setPassword(prefs.getString(context.getString(R.string.PASSWORD),
				""));
		user.setTenantId(prefs.getString(context.getString(R.string.TENANT_ID), ""));
		user.setId(prefs.getString(context.getString(R.string.USERID), ""));
		service.setKeystoneUsername(user.getUsername());
		service.setKeystonePassword(user.getPassword());		
		dump(context);
	}

	public static void dump(Context context) {
		String TAG = "ServicePreferences";
		OpenStackClientService service = OpenStackClientService.getInstance();
		Log.i(TAG,
				context.getString(R.string.USERNAME) + "="
						+ service.getKeystoneUsername());
		Log.i(TAG,
				context.getString(R.string.PASSWORD) + "="
						+ service.getKeystonePassword());
		Log.i(TAG,
				context.getString(R.string.TENANT_NAME) + "="
						+ service.getTenantName());
		Log.i(TAG, context.getString(R.string.KEYSTONE_AUTH_URL) + "="
				+ service.getKeystoneAuthUrl());
		Log.i(TAG, context.getString(R.string.KEYSTONE_ADMIN_AUTH_URL) + "="
				+ service.getKeystoneAdminAuthUrl());
		Log.i(TAG, context.getString(R.string.KEYSTONE_ENDPOINT) + "="
				+ service.getKeystoneEndpoint());
		Log.i(TAG,
				context.getString(R.string.NOVA_ENDPOINT) + "="
						+ service.getNovaEndpoint());
		Log.i(TAG, context.getString(R.string.CEILOMETER_ENDPOINT) + "="
				+ service.getCeilometerEndpoint());
		Log.i(TAG, context.getString(R.string.SIGNUP_ENDPOINT) + "="
				+ SignupService.getInstance().getSignupURL());
		User user = SignupService.getInstance().getUser();
		Log.i(TAG, context.getString(R.string.FULLNAME) + "=" + user.getName());
		Log.i(TAG,
				context.getString(R.string.USERNAME) + "=" + user.getUsername());
		Log.i(TAG, context.getString(R.string.EMAIL) + "=" + user.getEmail());
		Log.i(TAG,
				context.getString(R.string.PASSWORD) + "=" + user.getPassword());

	}

}
