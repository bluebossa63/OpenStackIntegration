package ch.niceneasy.openstack.android.sdk.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import ch.niceneasy.openstack.android.object.PseudoFileSystem;
import ch.niceneasy.openstack.android.sdk.connector.AndroidOpenStackClientConnector;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Objects;

public class OpenStackClientService {

	private static OpenStackClientService INSTANCE;

	public static OpenStackClientService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OpenStackClientService();
		}
		return INSTANCE;
	}

	private AndroidOpenStackClientConnector connector = new AndroidOpenStackClientConnector();
	private Keystone keystone;
	private Keystone adminKeystone;
	private Access access;
	private Access adminAccess;
	private Map<String, Swift> swiftMap = new HashMap<String, Swift>();
	private ObjectMapper defaultMapper = new ObjectMapper();
	private ObjectMapper wrappedMapper = new ObjectMapper();
	private String keystoneAuthUrl = "http://192.168.0.20:5000/v2.0/";
	private String keystoneAdminAuthUrl = "http://192.168.0.20:35357/v2.0/";
	private String keystoneUsername = "admin";
	private String keystonePassword = "admin";
	private String keystoneEndpoint = "http://192.168.0.20:8776/v2.0/";
	private String tenantName = "demo";
	private String novaEndpoint = "http://192.168.0.20:8774/v2/";
	private String ceilometerEndpoint = "";
	
	private Tenant selectedTenant;
	private Container selectedContainer;
	private PseudoFileSystem pseudoFileSystem;

	private OpenStackClientService() {
		defaultMapper.setSerializationInclusion(Inclusion.NON_NULL);
		defaultMapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		defaultMapper
				.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		defaultMapper
				.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		wrappedMapper.setSerializationInclusion(Inclusion.NON_NULL);
		wrappedMapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		wrappedMapper.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
		wrappedMapper.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
		wrappedMapper
				.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		wrappedMapper
				.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public ObjectMapper getContext(Class<?> type) {
		return type.getAnnotation(JsonRootName.class) == null ? defaultMapper
				: wrappedMapper;
	}

	public String getKeystoneAuthUrl() {
		return keystoneAuthUrl;
	}

	public void setKeystoneAuthUrl(String keystoneAuthUrl) {
		this.keystoneAuthUrl = keystoneAuthUrl;
	}

	public String getKeystoneAdminAuthUrl() {
		return keystoneAdminAuthUrl;
	}

	public void setKeystoneAdminAuthUrl(String keystoneAdminAuthUrl) {
		this.keystoneAdminAuthUrl = keystoneAdminAuthUrl;
	}

	public String getKeystoneUsername() {
		return keystoneUsername;
	}

	public void setKeystoneUsername(String keystoneUsername) {
		this.keystoneUsername = keystoneUsername;
	}

	public String getKeystonePassword() {
		return keystonePassword;
	}

	public void setKeystonePassword(String keystonePassword) {
		this.keystonePassword = keystonePassword;
	}

	public String getKeystoneEendpoint() {
		return keystoneEndpoint;
	}

	public void setKeystoneEendpoint(String keystoneEendpoint) {
		this.keystoneEndpoint = keystoneEendpoint;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getNovaEndpoint() {
		return novaEndpoint;
	}

	public void setNovaEndpoint(String novaEndpoint) {
		this.novaEndpoint = novaEndpoint;
	}

	public String getCeilometerEndpoint() {
		return ceilometerEndpoint;
	}

	public void setCeilometerEndpoint(String ceilometerEndpoint) {
		this.ceilometerEndpoint = ceilometerEndpoint;
	}

	public Keystone getKeystone() {
		if (keystone == null) {
			keystone = new Keystone(getKeystoneAuthUrl(), connector);
			keystone.setTokenProvider(new OpenStackSimpleTokenProvider(
					getAccess().getToken().getId()));
		}
		return keystone;
	}

	public Keystone getAdminKeystone() {
		if (adminKeystone == null) {
			adminKeystone = new Keystone(getKeystoneAdminAuthUrl(), connector);
			adminKeystone.token(getAdminAccess().getToken().getId());
		}
		return adminKeystone;
	}

	public Access getAccess() {
		if (access == null) {
			access = getKeystone()
					.tokens()
					.authenticate(
							new UsernamePassword(getKeystoneUsername(),
									getKeystonePassword())).execute();
		}
		return access;
	}

	public Access getAdminAccess() {
		if (adminAccess == null) {
			adminAccess = getKeystone()
					.tokens()
					.authenticate(
							new TokenAuthentication(getAccess().getToken()
									.getId())).withTenantName("admin")
					.execute();

		}
		return adminAccess;
	}

	public Swift getSwift(String tenantId) {
		Swift swift = swiftMap.get(tenantId);
		if (swift == null) {
			Access access = getKeystone()
					.tokens()
					.authenticate(
							new TokenAuthentication(getAccess().getToken()
									.getId())).withTenantId(tenantId).execute();
			swift = new Swift(
					KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
							"object-store", null, "public"), connector);
			swift.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));
			swiftMap.put(tenantId, swift);
		}
		return swift;
	}

	public static ObjectMapper mapper(Class<?> type) {
		return OpenStackClientService.getInstance().getContext(type);
	}

	public static InputStream copyStream(InputStream stream) throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((entitySize = stream.read(entity)) != -1) {
			baos.write(entity, 0, entitySize);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public static void copy(InputStream stream, OutputStream os)
			throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		while ((entitySize = stream.read(entity)) != -1) {
			os.write(entity, 0, entitySize);
		}
	}

	public Tenant getSelectedTenant() {
		return selectedTenant;
	}

	public void setSelectedTenant(Tenant selectedTenant) {
		this.selectedTenant = selectedTenant;
	}

	public Container getSelectedContainer() {
		return selectedContainer;
	}

	public void setSelectedContainer(Container selectedContainer) {
		this.selectedContainer = selectedContainer;
	}

	public void parseSelectedPseudoFileSystem(Objects objects) {
		this.setPseudoFileSystem(PseudoFileSystem.readFromObjects(objects));
	}

	public PseudoFileSystem getPseudoFileSystem() {
		return pseudoFileSystem;
	}

	public void setPseudoFileSystem(PseudoFileSystem pseudoFileSystem) {
		this.pseudoFileSystem = pseudoFileSystem;
	}

}
