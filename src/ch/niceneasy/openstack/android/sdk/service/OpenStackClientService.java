/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.sdk.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import ch.niceneasy.openstack.android.base.OpenstackApplicationState;
import ch.niceneasy.openstack.android.sdk.connector.AndroidOpenStackClientConnector;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.swift.Swift;

/**
 * The Class OpenStackClientService.
 * 
 * @author Daniele
 */
public class OpenStackClientService {

	/** The instance. */
	private static OpenStackClientService INSTANCE;

	/**
	 * Gets the single instance of OpenStackClientService.
	 * 
	 * @return single instance of OpenStackClientService
	 */
	public static OpenStackClientService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OpenStackClientService();
		}
		return INSTANCE;
	}

	/** The connector. */
	private AndroidOpenStackClientConnector connector = new AndroidOpenStackClientConnector();

	/** The keystone. */
	private Keystone keystone;

	/** The admin keystone. */
	private Keystone adminKeystone;

	/** The access. */
	private Access access;

	/** The admin access. */
	private Access adminAccess;

	/** The swift map. */
	private Map<String, Swift> swiftMap = new HashMap<String, Swift>();

	/** The default mapper. */
	private ObjectMapper defaultMapper = new ObjectMapper();

	/** The wrapped mapper. */
	private ObjectMapper wrappedMapper = new ObjectMapper();

	/** The keystone auth url. */
	private String keystoneAuthUrl = "http://192.168.0.20:5000/v2.0/";

	/** The keystone admin auth url. */
	private String keystoneAdminAuthUrl = "http://192.168.0.20:35357/v2.0/";

	/** The keystone username. */
	private String keystoneUsername = "admin";

	/** The keystone password. */
	private String keystonePassword = "adminPassword";

	/** The keystone endpoint. */
	private String keystoneEndpoint = "http://192.168.0.20:8776/v2.0/";

	/** The tenant name. */
	private String tenantName = "demo";

	/** The nova endpoint. */
	private String novaEndpoint = "http://192.168.0.20:8774/v2/";

	/** The ceilometer endpoint. */
	private String ceilometerEndpoint = "";

	/**
	 * Instantiates a new open stack client service.
	 */
	private OpenStackClientService() {
		defaultMapper.setSerializationInclusion(Include.NON_NULL);
		defaultMapper.enable(SerializationFeature.INDENT_OUTPUT);
		defaultMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		defaultMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		wrappedMapper.setSerializationInclusion(Include.NON_NULL);
		wrappedMapper.enable(SerializationFeature.INDENT_OUTPUT);
		wrappedMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		wrappedMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		wrappedMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		wrappedMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	/**
	 * Gets the context.
	 * 
	 * @param type
	 *            the type
	 * @return the context
	 */
	public ObjectMapper getContext(Class<?> type) {
		return type.getAnnotation(JsonRootName.class) == null ? defaultMapper
				: wrappedMapper;
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
	 * Gets the keystone username.
	 * 
	 * @return the keystone username
	 */
	public String getKeystoneUsername() {
		return keystoneUsername;
	}

	/**
	 * Sets the keystone username.
	 * 
	 * @param keystoneUsername
	 *            the new keystone username
	 */
	public void setKeystoneUsername(String keystoneUsername) {
		this.keystoneUsername = keystoneUsername;
	}

	/**
	 * Gets the keystone password.
	 * 
	 * @return the keystone password
	 */
	public String getKeystonePassword() {
		return keystonePassword;
	}

	/**
	 * Sets the keystone password.
	 * 
	 * @param keystonePassword
	 *            the new keystone password
	 */
	public void setKeystonePassword(String keystonePassword) {
		this.keystonePassword = keystonePassword;
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
	 * Gets the keystone.
	 * 
	 * @return the keystone
	 */
	public Keystone getKeystone() {
		if (keystone == null) {
			keystone = new Keystone(getKeystoneAuthUrl(), connector);
			try {
				keystone.setTokenProvider(new OpenStackSimpleTokenProvider(
						getAccess().getToken().getId()));
			} catch (RuntimeException e) {
				keystone = null;
				throw e;
			}
		}
		return keystone;
	}

	/**
	 * Gets the admin keystone.
	 * 
	 * @return the admin keystone
	 */
	public Keystone getAdminKeystone() {
		if (adminKeystone == null) {
			adminKeystone = new Keystone(getKeystoneAdminAuthUrl(), connector);
			adminKeystone.token(getAdminAccess().getToken().getId());
		}
		return adminKeystone;
	}

	/**
	 * Gets the access.
	 * 
	 * @return the access
	 */
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

	/**
	 * Gets the admin access.
	 * 
	 * @return the admin access
	 */
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

	/**
	 * Gets the swift.
	 * 
	 * @param tenantId
	 *            the tenant id
	 * @return the swift
	 */
	public Swift getSwift(String tenantId) {
		Swift swift = swiftMap.get(tenantId);
		if (swift == null) {
			Access access = getKeystone()
					.tokens()
					.authenticate(
							new TokenAuthentication(getAccess().getToken()
									.getId())).withTenantId(tenantId).execute();
			// swift = new Swift(
			// KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
			// "object-store", null, "public"), connector);
			String url = KeystoneUtils.findEndpointURL(
					access.getServiceCatalog(), "object-store", null, "public");
			// TODO: replace with configuration on server!
			url = url.replace("http://192.168.0.7:8080",
					"https://openstack.niceneasy.ch:7443/swift");
			swift = new Swift(url, connector);
			swift.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));
			swiftMap.put(tenantId, swift);
		}
		return swift;
	}

	/**
	 * Mapper.
	 * 
	 * @param type
	 *            the type
	 * @return the object mapper
	 */
	public static ObjectMapper mapper(Class<?> type) {
		return OpenStackClientService.getInstance().getContext(type);
	}

	/**
	 * Copy stream.
	 * 
	 * @param stream
	 *            the stream
	 * @return the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static InputStream copyStream(InputStream stream) throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		ByteArrayBuilder baos = new ByteArrayBuilder();
		while ((entitySize = stream.read(entity)) != -1) {
			baos.write(entity, 0, entitySize);
		}
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		baos.close();
		return is;
	}

	/**
	 * Copy.
	 * 
	 * @param stream
	 *            the stream
	 * @param os
	 *            the os
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copy(InputStream stream, OutputStream os)
			throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		while ((entitySize = stream.read(entity)) != -1) {
			os.write(entity, 0, entitySize);
		}
	}

	/**
	 * Checks if is logged in.
	 * 
	 * @return true, if is logged in
	 */
	public boolean isLoggedIn() {
		return access != null;
	}

	/**
	 * Reset connection.
	 */
	public void resetConnection() {
		this.access = null;
		this.adminAccess = null;
		this.keystone = null;
		this.adminKeystone = null;
		this.swiftMap.clear();
		OpenstackApplicationState.getInstance().clear();
	}

}
