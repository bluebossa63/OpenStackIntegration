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

import org.codehaus.jackson.map.ObjectMapper;

import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class AndroidOpenStackResponse implements OpenStackResponse {

	private HttpURLConnection urlConnection;

	private String statusPhrase;

	private int statusCode;

	private Map<String, String> headers;

	private InputStream is;

	public AndroidOpenStackResponse(HttpURLConnection urlConnection)
			throws IOException {
		this.urlConnection = urlConnection;
		this.statusCode = urlConnection.getResponseCode();
		this.statusPhrase = urlConnection.getResponseMessage();
		headers = new HashMap<String, String>();
		try {
			for (Entry<String, List<String>> iterable_element : urlConnection
					.getHeaderFields().entrySet()) {
				for (String value : iterable_element.getValue()) {
					headers.put(iterable_element.getKey(), value);
				}
			}
			is = copyStream(urlConnection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				urlConnection.disconnect();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	@Override
	public <T> T getEntity(Class<T> returnType) {
		if (getStatusCode() >= 400) {
			throw new OpenStackResponseException(getStatusPhrase(),
					getStatusCode());
		}
		ObjectMapper mapper = mapper(returnType);
		try {
			return mapper.readValue(is, returnType);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
