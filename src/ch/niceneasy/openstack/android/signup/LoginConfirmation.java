package ch.niceneasy.openstack.android.signup;

import org.codehaus.jackson.map.annotate.JsonRootName;

import com.woorea.openstack.keystone.model.User;

@JsonRootName("loginConfirmation")
public class LoginConfirmation {

	private User user;
	private String keystoneAuthUrl;
	private String keystoneAdminAuthUrl;
	private String keystoneEndpoint;
	private String tenantName;
	private String novaEndpoint;
	private String ceilometerEndpoint;
	private String swiftUrl;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getKeystoneEndpoint() {
		return keystoneEndpoint;
	}

	public void setKeystoneEndpoint(String keystoneEndpoint) {
		this.keystoneEndpoint = keystoneEndpoint;
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

	public String getSwiftUrl() {
		return swiftUrl;
	}

	public void setSwiftUrl(String swiftUrl) {
		this.swiftUrl = swiftUrl;
	}

}
