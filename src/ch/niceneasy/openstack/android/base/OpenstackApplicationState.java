package ch.niceneasy.openstack.android.base;

import ch.niceneasy.openstack.android.object.PseudoFileSystem;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.Objects;
import android.content.Intent;

public class OpenstackApplicationState {

	private static OpenstackApplicationState INSTANCE = new OpenstackApplicationState();

	public static OpenstackApplicationState getInstance() {
		return INSTANCE;
	}

	private OpenstackApplicationState() {
	}

	private Tenant selectedTenant;
	private Container selectedContainer;
	private Object selectedObject;
	private PseudoFileSystem pseudoFileSystem;
	private PseudoFileSystem selectedDirectory;
	private Intent shareIntent;
	private boolean shouldReturnToCaller;

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
		setSelectedDirectory(pseudoFileSystem);
	}

	public void setSelectedObject(Object item) {
		this.selectedObject = item;
	}

	public Object getSelectedObject() {
		return this.selectedObject;
	}

	public PseudoFileSystem getSelectedDirectory() {
		return selectedDirectory;
	}

	public void setSelectedDirectory(PseudoFileSystem selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
	}

	public void clear() {
		selectedTenant = null;
		selectedContainer = null;
		selectedObject = null;
		pseudoFileSystem = null;
		selectedDirectory = null;
	}

	public Intent getShareIntent() {
		return shareIntent;
	}

	public void setShareIntent(Intent shareIntent) {
		this.shareIntent = shareIntent;
	}

	public boolean isInSharingMode() {
		return this.shareIntent != null;
	}

	public boolean shouldReturnToCaller() {
		return shouldReturnToCaller;
	}

	public void setShouldReturnToCaller(boolean shouldReturnToCaller) {
		this.shouldReturnToCaller = shouldReturnToCaller;
	}

}
