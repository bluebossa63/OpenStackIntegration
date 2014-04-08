/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.sdk.connector;

import static ch.niceneasy.openstack.android.sdk.service.OpenStackClientService.copyStream;
import static ch.niceneasy.openstack.android.sdk.service.OpenStackClientService.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.swift.model.ObjectDownload;

/**
 * The Class AndroidOpenStackResponse.
 * 
 * @author Daniele
 */
public class AndroidOpenStackResponse implements OpenStackResponse {

	/** The tag. */
	public static String TAG = "AndroidOpenStackResponse";

	/** The url connection. */
	private HttpURLConnection urlConnection;

	/** The status phrase. */
	private String statusPhrase;

	/** The status code. */
	private int statusCode;

	/** The headers. */
	private Map<String, String> headers;

	/** The is. */
	private InputStream is;

	/**
	 * Instantiates a new android open stack response.
	 * 
	 * @param urlConnection
	 *            the url connection
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public AndroidOpenStackResponse(HttpURLConnection urlConnection)
			throws IOException {
		this.urlConnection = urlConnection;
		try {
			this.statusCode = urlConnection.getResponseCode();
			Log.i(TAG, this.statusCode + "");
		} catch (IOException ioe) {
			// intolerant implementation, see
			// http://stackoverflow.com/a/15893389
			if ("No authentication challenges found".equals(ioe.getMessage())) {
				this.statusCode = 401;
			}
		}
		try {
			this.statusPhrase = urlConnection.getResponseMessage();
			// Log.i(TAG, this.statusPhrase);
			headers = new HashMap<String, String>();
			for (Entry<String, List<String>> iterable_element : urlConnection
					.getHeaderFields().entrySet()) {
				for (String value : iterable_element.getValue()) {
					headers.put(iterable_element.getKey(), value);
					// Log.i(TAG, "response header " + iterable_element.getKey()
					// + ": " + value);
				}
			}
			is = copyStream(urlConnection.getInputStream());
		} catch (IOException e) {
			throw new OpenStackResponseException(getStatusPhrase(),
					getStatusCode());
		} finally {
			try {
				urlConnection.disconnect();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.woorea.openstack.base.client.OpenStackResponse#getEntity(java.lang
	 * .Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEntity(Class<T> returnType) {
		if (getStatusCode() >= 400) {
			throw new OpenStackResponseException(getStatusPhrase(),
					getStatusCode());
		}
		if (returnType.equals(ObjectDownload.class)) {
			ObjectDownload objectDownload = new ObjectDownload();
			objectDownload.setInputStream(getInputStream());
			return (T) objectDownload;
		} else {
			ObjectMapper mapper = mapper(returnType);
			try {
				return mapper.readValue(getInputStream(), returnType);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woorea.openstack.base.client.OpenStackResponse#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return is;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.woorea.openstack.base.client.OpenStackResponse#header(java.lang.String
	 * )
	 */
	@Override
	public String header(String name) {
		return headers.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woorea.openstack.base.client.OpenStackResponse#headers()
	 */
	@Override
	public Map<String, String> headers() {
		return headers;
	}

	/**
	 * Gets the url connection.
	 * 
	 * @return the url connection
	 */
	public HttpURLConnection getUrlConnection() {
		return urlConnection;
	}

	/**
	 * Sets the url connection.
	 * 
	 * @param urlConnection
	 *            the new url connection
	 */
	public void setUrlConnection(HttpURLConnection urlConnection) {
		this.urlConnection = urlConnection;
	}

	/**
	 * Gets the status code.
	 * 
	 * @return the status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 * 
	 * @param statusCode
	 *            the new status code
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the status phrase.
	 * 
	 * @return the status phrase
	 */
	public String getStatusPhrase() {
		return statusPhrase;
	}

}
