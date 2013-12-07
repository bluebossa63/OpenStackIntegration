package ch.niceneasy.openstack.android.sdk.connector;


import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Tenant;

public class ExamplesConfiguration {

	public static final String KEYSTONE_AUTH_URL = "http://192.168.0.20:5000/v2.0";
	
	public static final String KEYSTONE_ADMIN_AUTH_URL = "http://192.168.0.20:35357/v2.0";
	
	public static final String KEYSTONE_USERNAME = "admin";
	
	public static final String KEYSTONE_PASSWORD = "*************";
	
	public static final String KEYSTONE_ENDPOINT = "http://192.168.0.20:8776/v2.0";
	
	public static final String TENANT_NAME = "demo";

	public static final String NOVA_ENDPOINT = "http://192.168.0.20:8774/v2";
	
	public static final String CEILOMETER_ENDPOINT = "";
	
	public static void main(String[] args) {
		Keystone client = new Keystone(KEYSTONE_ENDPOINT);
		client.setTokenProvider(new OpenStackSimpleTokenProvider("secret0"));
		client.tenants().delete("36c481aec1d54fc49190c92c3ef6840a").execute();
		Tenant tenant = client.tenants().create(new Tenant("new_api")).execute();
		System.out.println(tenant);
		System.out.println(client.tenants().list().execute());
		client.tenants().delete(tenant.getId()).execute();
	}
	
}
