/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.signup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

import com.woorea.openstack.keystone.model.User;

/**
 * The Class SignupService.
 * 
 * @author Daniele
 */
public class SignupService {

	/** The trust all certs. */
	static TrustManager[] trustAllCerts;

	/** The tag. */
	public static String TAG = "SignupService";

	/** The default signup url. */
	public static String DEFAULT_SIGNUP_URL = "https://openstack.niceneasy.ch:7443/account-management/rest/users/";

	/** The instance. */
	private static SignupService INSTANCE = new SignupService();

	/**
	 * Gets the single instance of SignupService.
	 * 
	 * @return single instance of SignupService
	 */
	public static SignupService getInstance() {
		return INSTANCE;
	}

	/** The signup url. */
	private String signupURL;

	/** The user. */
	private User user = new User();

	/** The Constant DO_NOT_VERIFY. */
	final static HostnameVerifier DO_NOT_VERIFY;

	static {

		DO_NOT_VERIFY = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] arg0, String arg1)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub

			}

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

	/**
	 * Register.
	 */
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
			OpenStackClientService.getInstance().getContext(user.getClass())
					.writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			user = OpenStackClientService.getInstance()
					.getContext(user.getClass()).readValue(br, User.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Login.
	 * 
	 * @param context
	 *            the context
	 * @return the login confirmation
	 */
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
			OpenStackClientService.getInstance().getContext(user.getClass())
					.writeValue(writer, user);

			urlConnection.getOutputStream().write(writer.toString().getBytes());

			if (urlConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ urlConnection.getResponseCode() + " "
						+ urlConnection.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			LoginConfirmation loginConfirmation = OpenStackClientService
					.getInstance().getContext(user.getClass())
					.readValue(br, LoginConfirmation.class);
			loginConfirmation.getUser().setPassword(getUser().getPassword());
			user = loginConfirmation.getUser();
			return loginConfirmation;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Gets the signup url.
	 * 
	 * @return the signup url
	 */
	public String getSignupURL() {
		return signupURL;
	}

	/**
	 * Sets the signup url.
	 * 
	 * @param signupURL
	 *            the new signup url
	 */
	public void setSignupURL(String signupURL) {
		this.signupURL = signupURL;
	}

}
