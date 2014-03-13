package ch.niceneasy.openstack.android.signup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import android.content.Context;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;

import com.woorea.openstack.keystone.model.User;

public class SignupService {

	public static String TAG = "SignupService";

	public static String DEFAULT_SIGNUP_URL = "http://openstack.ne.local:9080/account-management/rest/users/";

	static ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

	static {
		DEFAULT_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
		DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		DEFAULT_MAPPER
				.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		DEFAULT_MAPPER
				.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

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
			DEFAULT_MAPPER.writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			user = DEFAULT_MAPPER.readValue(br, User.class);
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
			DEFAULT_MAPPER.writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			LoginConfirmation loginConfirmation = DEFAULT_MAPPER.readValue(br,
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
