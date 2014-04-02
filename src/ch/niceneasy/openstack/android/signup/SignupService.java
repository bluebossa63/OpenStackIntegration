package ch.niceneasy.openstack.android.signup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import android.content.Context;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

import com.woorea.openstack.keystone.model.User;

public class SignupService {

	static TrustManager[] trustAllCerts;
	
	public static String TAG = "SignupService";

	public static String DEFAULT_SIGNUP_URL = "https://openstack.niceneasy.ch:7443/account-management/rest/users/";

	private static SignupService INSTANCE = new SignupService();

	public static SignupService getInstance() {
		return INSTANCE;
	}

	private String signupURL;
	private User user = new User();
	final static HostnameVerifier DO_NOT_VERIFY;
	
	static {
		
		DO_NOT_VERIFY = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	
		trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}


		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}
	} };
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	public void register() {

		try {
			HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(
					signupURL).openConnection();
			urlConnection.setHostnameVerifier(DO_NOT_VERIFY);
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
			HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(
					signupURL + "login").openConnection();
			urlConnection.setHostnameVerifier(DO_NOT_VERIFY);
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
