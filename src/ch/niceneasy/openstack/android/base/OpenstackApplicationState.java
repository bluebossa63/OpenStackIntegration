/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.base;

import android.content.Intent;
import ch.niceneasy.openstack.android.object.PseudoFileSystem;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.Objects;

/**
 * The Class OpenstackApplicationState.
 * 
 * @author Daniele
 */
public class OpenstackApplicationState {

	/** The instance. */
	private static OpenstackApplicationState INSTANCE = new OpenstackApplicationState();

	/**
	 * Gets the single instance of OpenstackApplicationState.
	 * 
	 * @return single instance of OpenstackApplicationState
	 */
	public static OpenstackApplicationState getInstance() {
		return INSTANCE;
	}

	/**
	 * Instantiates a new openstack application state.
	 */
	private OpenstackApplicationState() {
	}

	/** The selected tenant. */
	private Tenant selectedTenant;

	/** The selected container. */
	private Container selectedContainer;

	/** The selected object. */
	private Object selectedObject;

	/** The pseudo file system. */
	private PseudoFileSystem pseudoFileSystem;

	/** The selected directory. */
	private PseudoFileSystem selectedDirectory;

	/** The share intent. */
	private Intent shareIntent;

	/** The should return to caller. */
	private boolean shouldReturnToCaller;

	/**
	 * Gets the selected tenant.
	 * 
	 * @return the selected tenant
	 */
	public Tenant getSelectedTenant() {
		return selectedTenant;
	}

	/**
	 * Sets the selected tenant.
	 * 
	 * @param selectedTenant
	 *            the new selected tenant
	 */
	public void setSelectedTenant(Tenant selectedTenant) {
		this.selectedTenant = selectedTenant;
	}

	/**
	 * Gets the selected container.
	 * 
	 * @return the selected container
	 */
	public Container getSelectedContainer() {
		return selectedContainer;
	}

	/**
	 * Sets the selected container.
	 * 
	 * @param selectedContainer
	 *            the new selected container
	 */
	public void setSelectedContainer(Container selectedContainer) {
		this.selectedContainer = selectedContainer;
	}

	/**
	 * Parses the selected pseudo file system.
	 * 
	 * @param objects
	 *            the objects
	 */
	public void parseSelectedPseudoFileSystem(Objects objects) {
		this.setPseudoFileSystem(PseudoFileSystem.readFromObjects(objects));
	}

	/**
	 * Gets the pseudo file system.
	 * 
	 * @return the pseudo file system
	 */
	public PseudoFileSystem getPseudoFileSystem() {
		return pseudoFileSystem;
	}

	/**
	 * Sets the pseudo file system.
	 * 
	 * @param pseudoFileSystem
	 *            the new pseudo file system
	 */
	public void setPseudoFileSystem(PseudoFileSystem pseudoFileSystem) {
		this.pseudoFileSystem = pseudoFileSystem;
		setSelectedDirectory(pseudoFileSystem);
	}

	/**
	 * Sets the selected object.
	 * 
	 * @param item
	 *            the new selected object
	 */
	public void setSelectedObject(Object item) {
		this.selectedObject = item;
	}

	/**
	 * Gets the selected object.
	 * 
	 * @return the selected object
	 */
	public Object getSelectedObject() {
		return this.selectedObject;
	}

	/**
	 * Gets the selected directory.
	 * 
	 * @return the selected directory
	 */
	public PseudoFileSystem getSelectedDirectory() {
		return selectedDirectory;
	}

	/**
	 * Sets the selected directory.
	 * 
	 * @param selectedDirectory
	 *            the new selected directory
	 */
	public void setSelectedDirectory(PseudoFileSystem selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		selectedTenant = null;
		selectedContainer = null;
		selectedObject = null;
		pseudoFileSystem = null;
		selectedDirectory = null;
	}

	/**
	 * Gets the share intent.
	 * 
	 * @return the share intent
	 */
	public Intent getShareIntent() {
		return shareIntent;
	}

	/**
	 * Sets the share intent.
	 * 
	 * @param shareIntent
	 *            the new share intent
	 */
	public void setShareIntent(Intent shareIntent) {
		this.shareIntent = shareIntent;
	}

	/**
	 * Checks if is in sharing mode.
	 * 
	 * @return true, if is in sharing mode
	 */
	public boolean isInSharingMode() {
		return this.shareIntent != null;
	}

	/**
	 * Should return to caller.
	 * 
	 * @return true, if successful
	 */
	public boolean shouldReturnToCaller() {
		return shouldReturnToCaller;
	}

	/**
	 * Sets the should return to caller.
	 * 
	 * @param shouldReturnToCaller
	 *            the new should return to caller
	 */
	public void setShouldReturnToCaller(boolean shouldReturnToCaller) {
		this.shouldReturnToCaller = shouldReturnToCaller;
	}

}
