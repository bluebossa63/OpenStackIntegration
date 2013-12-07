package ch.niceneasy.openstack.android.sdk.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import android.util.Log;

import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class AndroidOpenStackClientConnector implements
		OpenStackClientConnector {
	
	public static ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
	public static ObjectMapper WRAPPED_MAPPER = new ObjectMapper();
	
	static {
	
	DEFAULT_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
	DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	DEFAULT_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		
	WRAPPED_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
	WRAPPED_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	WRAPPED_MAPPER.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
	WRAPPED_MAPPER.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
	WRAPPED_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	WRAPPED_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	
	}
	
	
	public static ObjectMapper getContext(Class<?> type) {
		return type.getAnnotation(JsonRootName.class) == null ? DEFAULT_MAPPER : WRAPPED_MAPPER;
	}
	

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

		String sUrl = request.endpoint() + "/" + request.path();
		if (queryParameters.length() > 0) {
			sUrl += queryParameters;
		}

		AndroidOpenStackResponse resp = null;

		try {

			HttpURLConnection urlConnection = (HttpURLConnection) new URL(sUrl).openConnection();
			// WebTarget target =
			// client.target(request.endpoint()).path(request.path());
			//
			//
			// target.register(logger);
			// Invocation.Builder invocation = target.request();

			for (Map.Entry<String, List<Object>> h : request.headers()
					.entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Object v : h.getValue()) {
					sb.append(String.valueOf(v));
				}
				urlConnection.addRequestProperty(h.getKey(), sb.toString());
			}

			// Entity<?> entity = (request.entity() == null) ? null :
			// Entity.entity(request.entity().getEntity(),
			// request.entity().getContentType());

			urlConnection.setRequestMethod(request.method().name());

			if (request.entity() != null) {

				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Content-Type", request
						.entity().getContentType());
				if (request.entity().getContentType()
						.equals("application/json")) {
					ObjectMapper mapper = getContext(request
							.entity().getEntity().getClass());				
					StringWriter writer = new StringWriter();
					mapper.writeValue(writer, request
							.entity().getEntity());
					Log.i("ClientConnector",writer.toString());
					urlConnection.getOutputStream().write(writer.toString().getBytes());
				} else {
					copyStream((InputStream) request.entity().getEntity(),
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

	private void copyStream(InputStream stream, OutputStream os)
			throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		while ((entitySize = stream.read(entity)) != -1) {
			os.write(entity, 0, entitySize);
		}
	}

}
