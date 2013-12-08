package ch.niceneasy.openstack.android.sdk.connector;

import static ch.niceneasy.openstack.android.sdk.service.OpenStackClientService.copy;
import static ch.niceneasy.openstack.android.sdk.service.OpenStackClientService.mapper;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class AndroidOpenStackClientConnector implements
		OpenStackClientConnector {

	@Override
	public <T> OpenStackResponse request(OpenStackRequest<T> request) {

		String queryParameters = "";
		for (Map.Entry<String, List<Object>> entry : request.queryParams()
				.entrySet()) {
			for (Object o : entry.getValue()) {
				if (queryParameters.length() == 0) {
					queryParameters += "?";
				} else {
					queryParameters += "&";
				}
				queryParameters += encode(entry.getKey()) + "="
						+ encode(o.toString());
			}
		}

		String sUrl = request.endpoint() + request.path();
		if (queryParameters.length() > 0) {
			sUrl += queryParameters;
		}

		AndroidOpenStackResponse resp = null;

		try {

			HttpURLConnection urlConnection = (HttpURLConnection) new URL(sUrl)
					.openConnection();
			for (Map.Entry<String, List<Object>> h : request.headers()
					.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Object v : h.getValue()) {
					sb.append(String.valueOf(v));
				}
				urlConnection.addRequestProperty(h.getKey(), sb.toString());
			}
			urlConnection.setRequestMethod(request.method().name());
			if (request.entity() != null) {
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Content-Type", request
						.entity().getContentType());
				if (request.entity().getContentType()
						.equals("application/json")) {
					ObjectMapper mapper = mapper(request.entity().getEntity()
							.getClass());
					StringWriter writer = new StringWriter();
					mapper.writeValue(writer, request.entity().getEntity());
					Log.i("ClientConnector", writer.toString());
					urlConnection.getOutputStream().write(
							writer.toString().getBytes());
				} else {
					copy((InputStream) request.entity().getEntity(),
							urlConnection.getOutputStream());
				}
			}
			resp = new AndroidOpenStackResponse(urlConnection);
			return resp;
		} catch (Exception e) {
			throw new OpenStackResponseException(e.getLocalizedMessage(),
					resp != null ? resp.getStatusCode() : -1);
		}
	}

	private String encode(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
