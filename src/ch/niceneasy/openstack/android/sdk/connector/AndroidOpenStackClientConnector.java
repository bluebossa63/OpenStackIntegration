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

import com.fasterxml.jackson.databind.ObjectMapper;

import android.util.Log;

import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class AndroidOpenStackClientConnector implements
		OpenStackClientConnector {

	public static String TAG = "AndroidOpenStackClientConnector";

	@Override
	public <T> OpenStackResponse request(OpenStackRequest<T> request) {

		StringBuilder queryParameters = new StringBuilder();
		for (Map.Entry<String, List<Object>> entry : request.queryParams()
				.entrySet()) {
			for (Object o : entry.getValue()) {
				if (queryParameters.length() == 0) {
					queryParameters.append("?");
				} else {
					queryParameters.append("&");
				}
				queryParameters.append(encode(entry.getKey())).append("=").append(encode(o.toString()));
			}
		}

		String sUrl = null;
		if (!request.endpoint().endsWith("/")
				&& !request.path().startsWith("/")) {
			sUrl = request.endpoint() + "/" + request.path();
		} else {
			sUrl = request.endpoint() + request.path();
		}
		if (queryParameters.length() > 0) {
			sUrl += queryParameters;
		}

		sUrl = sUrl.replaceAll(" ", "%20");

		AndroidOpenStackResponse resp = null;

		try {

			//Log.i(TAG, request.method().name() + " " + sUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) new URL(sUrl)
					.openConnection();
			for (Map.Entry<String, List<Object>> h : request.headers()
					.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Object v : h.getValue()) {
					sb.append(String.valueOf(v));
				}
				urlConnection.addRequestProperty(h.getKey(), sb.toString());
				//Log.i(TAG, "add header " + h.getKey() + ": " + sb.toString());
			}
			urlConnection.setRequestMethod(request.method().name());
			if (request.entity() != null) {
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Content-Type", request
						.entity().getContentType());
//				Log.i(TAG, "add header " + "Content-Type" + ": "
//						+ request.entity().getContentType());
				if (request.entity().getContentType()
						.equals("application/json")) {
					ObjectMapper mapper = mapper(request.entity().getEntity()
							.getClass());
					StringWriter writer = new StringWriter();
					mapper.writeValue(writer, request.entity().getEntity());
//					Log.i("ClientConnector", writer.toString());
					urlConnection.getOutputStream().write(
							writer.toString().getBytes());
				} else {
					copy((InputStream) request.entity().getEntity(),
							urlConnection.getOutputStream());
				}
			}
			resp = new AndroidOpenStackResponse(urlConnection);
			return resp;
		} catch (OpenStackResponseException osre) {
			throw osre;
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
