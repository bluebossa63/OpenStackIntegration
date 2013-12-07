package ch.niceneasy.openstack.android;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.ObjectForUpload;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		OpenStackClientService service = OpenStackClientService.getInstance();

		// Tenant tenant = new Tenant("test4", "unit test tenant3", true);
		// adminKeystone.tenants().create(tenant).execute();

		Tenants tenants = service.getKeystone().tenants().list().execute();
		List<Tenant> tenantList = tenants.getList();
		Tenant selectedTenant = null;
		for (Tenant tenant : tenantList) {
			if (tenant.getName().equals("demo")) {
				selectedTenant = tenant;
				break;
			}
		}
		Swift swift = service.getSwift(selectedTenant.getId());
		swift.containers().create("navidad9").execute();

		System.out.println(swift.containers().list());

		ObjectMapper mapper = service.getContext(selectedTenant.getClass());
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, selectedTenant);
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
		upload.setContainer("navidad9");
		upload.setName("example5");
		upload.setInputStream(new ByteArrayInputStream(writer.toString()
				.getBytes()));
		swift.containers().container("navidad9").upload(upload).execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
