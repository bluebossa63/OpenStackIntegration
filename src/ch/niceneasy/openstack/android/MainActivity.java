package ch.niceneasy.openstack.android;

import static ch.niceneasy.openstack.android.sdk.connector.AndroidOpenStackClientConnector.getContext;
import static ch.niceneasy.openstack.android.sdk.connector.ExamplesConfiguration.KEYSTONE_ADMIN_AUTH_URL;
import static ch.niceneasy.openstack.android.sdk.connector.ExamplesConfiguration.KEYSTONE_AUTH_URL;
import static ch.niceneasy.openstack.android.sdk.connector.ExamplesConfiguration.KEYSTONE_PASSWORD;
import static ch.niceneasy.openstack.android.sdk.connector.ExamplesConfiguration.KEYSTONE_USERNAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import ch.niceneasy.openstack.android.sdk.connector.AndroidOpenStackClientConnector;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.ObjectForUpload;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AndroidOpenStackClientConnector connector = new AndroidOpenStackClientConnector();
		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL, connector);
		Keystone adminKeystone = new Keystone(KEYSTONE_ADMIN_AUTH_URL,
				connector);
		// access with unscoped token
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(KEYSTONE_USERNAME,
								KEYSTONE_PASSWORD)).execute();

		Access adminAccess = keystone
				.tokens()
				.authenticate(
						new TokenAuthentication(access.getToken().getId()))
				.withTenantName("admin").execute();

		// use the token in the following requests
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));
		adminKeystone.token(adminAccess.getToken().getId());
		Tenant tenant = new Tenant("test4", "unit test tenant3", true);
		adminKeystone.tenants().create(tenant).execute();

		Tenants tenants = keystone.tenants().list().execute();

		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {

			access = keystone
					.tokens()
					.authenticate(
							new TokenAuthentication(access.getToken().getId()))
					.withTenantId(tenants.getList().get(0).getId()).execute();

			Swift swift = new Swift(
					KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
							"object-store", null, "public"), connector);
			swift.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));
			// OpenStack.CLIENT.register(ObjectDownloadMessageBodyReader.class);

			// swiftClient.execute(new DeleteContainer("navidad2"));

			swift.containers().create("navidad6").execute();

			System.out.println(swift.containers().list());

			ObjectMapper mapper = getContext(access.getClass());				
			StringWriter writer = new StringWriter();
			 try {
				mapper.writeValue(writer, access);
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 ObjectForUpload upload = new ObjectForUpload();
			 upload.setContainer("navidad6");
			 upload.setName("example2");
			 upload.setInputStream(new ByteArrayInputStream(writer.toString().getBytes()));
			 swift.containers().container("navidad6").upload(upload).execute();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
