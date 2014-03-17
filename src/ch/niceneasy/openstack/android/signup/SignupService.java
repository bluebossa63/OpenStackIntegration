package ch.niceneasy.openstack.android.signup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;






import android.content.Context;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.woorea.openstack.keystone.model.User;

public class SignupService {

	public static String TAG = "SignupService";

	public static String DEFAULT_SIGNUP_URL = "http://openstack.ne.local:9080/account-management/rest/users/";

	private static SignupService INSTANCE = new SignupService();

	public static SignupService getInstance() {
		return INSTANCE;
	}

	private String signupURL;
	private User user = new User();

	public void register() {

		try {
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(
					signupURL).openConnection();
			urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection
					.addRequestProperty("Content-Type", "application/json");
			urlConnection.getDoOutput();
			urlConnection.setRequestMethod("PUT");
			StringWriter writer = new StringWriter();
			OpenStackClientService.getInstance().getContext(user.getClass()).writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			user = OpenStackClientService.getInstance().getContext(user.getClass()).readValue(br, User.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public LoginConfirmation login(Context context) {

		try {
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(
					signupURL + "login").openConnection();
			urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection
					.addRequestProperty("Content-Type", "application/json");
			urlConnection.getDoOutput();
			urlConnection.setRequestMethod("POST");
			StringWriter writer = new StringWriter();
			OpenStackClientService.getInstance().getContext(user.getClass()).writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			LoginConfirmation loginConfirmation = OpenStackClientService.getInstance().getContext(user.getClass()).readValue(br,
					LoginConfirmation.class);
			loginConfirmation.getUser().setPassword(getUser().getPassword());
			user = loginConfirmation.getUser();
			return loginConfirmation;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public User getUser() {
		return user;
	}

	public String getSignupURL() {
		return signupURL;
	}

	public void setSignupURL(String signupURL) {
		this.signupURL = signupURL;
	}

}
