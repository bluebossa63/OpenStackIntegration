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

public class AndroidOpenStackResponse implements OpenStackResponse {

	public static String TAG = "AndroidOpenStackResponse";

	private HttpURLConnection urlConnection;

	private String statusPhrase;

	private int statusCode;

	private Map<String, String> headers;

	private InputStream is;

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
			//Log.i(TAG, this.statusPhrase);
			headers = new HashMap<String, String>();
			for (Entry<String, List<String>> iterable_element : urlConnection
					.getHeaderFields().entrySet()) {
				for (String value : iterable_element.getValue()) {
					headers.put(iterable_element.getKey(), value);
					//Log.i(TAG, "response header " + iterable_element.getKey()
					//		+ ": " + value);
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

	@Override
	public InputStream getInputStream() {
		return is;
	}

	@Override
	public String header(String name) {
		return headers.get(name);
	}

	@Override
	public Map<String, String> headers() {
		return headers;
	}

	public HttpURLConnection getUrlConnection() {
		return urlConnection;
	}

	public void setUrlConnection(HttpURLConnection urlConnection) {
		this.urlConnection = urlConnection;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusPhrase() {
		return statusPhrase;
	}

}
