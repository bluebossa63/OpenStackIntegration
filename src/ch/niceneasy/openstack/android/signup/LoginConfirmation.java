/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.signup;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.woorea.openstack.keystone.model.User;

/**
 * The Class LoginConfirmation.
 * 
 * @author Daniele
 */
@JsonRootName("loginConfirmation")
public class LoginConfirmation {

	/** The user. */
	private User user;

	/** The keystone auth url. */
	private String keystoneAuthUrl;

	/** The keystone admin auth url. */
	private String keystoneAdminAuthUrl;

	/** The keystone endpoint. */
	private String keystoneEndpoint;

	/** The tenant name. */
	private String tenantName;

	/** The nova endpoint. */
	private String novaEndpoint;

	/** The ceilometer endpoint. */
	private String ceilometerEndpoint;

	/** The swift url. */
	private String swiftUrl;

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the keystone auth url.
	 * 
	 * @return the keystone auth url
	 */
	public String getKeystoneAuthUrl() {
		return keystoneAuthUrl;
	}

	/**
	 * Sets the keystone auth url.
	 * 
	 * @param keystoneAuthUrl
	 *            the new keystone auth url
	 */
	public void setKeystoneAuthUrl(String keystoneAuthUrl) {
		this.keystoneAuthUrl = keystoneAuthUrl;
	}

	/**
	 * Gets the keystone admin auth url.
	 * 
	 * @return the keystone admin auth url
	 */
	public String getKeystoneAdminAuthUrl() {
		return keystoneAdminAuthUrl;
	}

	/**
	 * Sets the keystone admin auth url.
	 * 
	 * @param keystoneAdminAuthUrl
	 *            the new keystone admin auth url
	 */
	public void setKeystoneAdminAuthUrl(String keystoneAdminAuthUrl) {
		this.keystoneAdminAuthUrl = keystoneAdminAuthUrl;
	}

	/**
	 * Gets the keystone endpoint.
	 * 
	 * @return the keystone endpoint
	 */
	public String getKeystoneEndpoint() {
		return keystoneEndpoint;
	}

	/**
	 * Sets the keystone endpoint.
	 * 
	 * @param keystoneEndpoint
	 *            the new keystone endpoint
	 */
	public void setKeystoneEndpoint(String keystoneEndpoint) {
		this.keystoneEndpoint = keystoneEndpoint;
	}

	/**
	 * Gets the tenant name.
	 * 
	 * @return the tenant name
	 */
	public String getTenantName() {
		return tenantName;
	}

	/**
	 * Sets the tenant name.
	 * 
	 * @param tenantName
	 *            the new tenant name
	 */
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	/**
	 * Gets the nova endpoint.
	 * 
	 * @return the nova endpoint
	 */
	public String getNovaEndpoint() {
		return novaEndpoint;
	}

	/**
	 * Sets the nova endpoint.
	 * 
	 * @param novaEndpoint
	 *            the new nova endpoint
	 */
	public void setNovaEndpoint(String novaEndpoint) {
		this.novaEndpoint = novaEndpoint;
	}

	/**
	 * Gets the ceilometer endpoint.
	 * 
	 * @return the ceilometer endpoint
	 */
	public String getCeilometerEndpoint() {
		return ceilometerEndpoint;
	}

	/**
	 * Sets the ceilometer endpoint.
	 * 
	 * @param ceilometerEndpoint
	 *            the new ceilometer endpoint
	 */
	public void setCeilometerEndpoint(String ceilometerEndpoint) {
		this.ceilometerEndpoint = ceilometerEndpoint;
	}

	/**
	 * Gets the swift url.
	 * 
	 * @return the swift url
	 */
	public String getSwiftUrl() {
		return swiftUrl;
	}

	/**
	 * Sets the swift url.
	 * 
	 * @param swiftUrl
	 *            the new swift url
	 */
	public void setSwiftUrl(String swiftUrl) {
		this.swiftUrl = swiftUrl;
	}

}
