/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.object;

import java.util.LinkedHashMap;
import java.util.Map;

import com.woorea.openstack.swift.model.Objects;

/**
 * The Class PseudoFileSystem.
 * 
 * @author Daniele
 */
public class PseudoFileSystem {

	/** The directories. */
	private Map<String, PseudoFileSystem> directories = new LinkedHashMap<String, PseudoFileSystem>();

	/** The files. */
	private Map<String, com.woorea.openstack.swift.model.Object> files = new LinkedHashMap<String, com.woorea.openstack.swift.model.Object>();

	/** The meta data. */
	private com.woorea.openstack.swift.model.Object metaData;

	/** The parent. */
	private PseudoFileSystem parent;

	/**
	 * Instantiates a new pseudo file system.
	 * 
	 * @param parent
	 *            the parent
	 * @param childPath
	 *            the child path
	 */
	public PseudoFileSystem(PseudoFileSystem parent, String childPath) {
		this.parent = parent;
		com.woorea.openstack.swift.model.Object object = new com.woorea.openstack.swift.model.Object();
		object.setName(childPath);
		this.setMetaData(object);
	}

	/**
	 * Gets the directories.
	 * 
	 * @return the directories
	 */
	public Map<String, PseudoFileSystem> getDirectories() {
		return directories;
	}

	/**
	 * Gets the files.
	 * 
	 * @return the files
	 */
	public Map<String, com.woorea.openstack.swift.model.Object> getFiles() {
		return files;
	}

	/**
	 * Read from objects.
	 * 
	 * @param objects
	 *            the objects
	 * @return the pseudo file system
	 */
	public static PseudoFileSystem readFromObjects(Objects objects) {
		PseudoFileSystem fs = new PseudoFileSystem(null, "");
		for (com.woorea.openstack.swift.model.Object object : objects) {
			String name = object.getName();
			if (!name.contains("/")) {
				fs.getFiles().put(name, object);
			} else {
				if (name.endsWith("/")) {
					PseudoFileSystem targetDirectory = findOrCreateChild(fs,
							name);
					targetDirectory.setMetaData(object);
				} else {
					String[] path = name.split("/");
					String directory = "";
					for (int i = 0; i < path.length - 1; i++) {
						directory += path[i] + "/";
					}
					PseudoFileSystem targetDirectory = findChild(fs, directory);
					targetDirectory.files.put(path[path.length - 1], object);
				}
			}
		}
		return fs;
	}

	/**
	 * Find or create child.
	 * 
	 * @param root
	 *            the root
	 * @param childPath
	 *            the child path
	 * @return the pseudo file system
	 */
	public static PseudoFileSystem findOrCreateChild(PseudoFileSystem root,
			String childPath) {
		PseudoFileSystem currentLevel = root;
		String[] path = childPath.split("/");
		for (int i = 0; i < path.length; i++) {
			if (!currentLevel.directories.containsKey(path[i])) {
				currentLevel.directories.put(path[i], new PseudoFileSystem(
						currentLevel, childPath));
			}
			currentLevel = currentLevel.directories.get(path[i]);
		}
		return currentLevel;
	}

	/**
	 * Find child.
	 * 
	 * @param root
	 *            the root
	 * @param childPath
	 *            the child path
	 * @return the pseudo file system
	 */
	public static PseudoFileSystem findChild(PseudoFileSystem root,
			String childPath) {
		PseudoFileSystem currentLevel = root;
		if (childPath.trim().length() == 0) {
			return root;
		}
		String[] path = childPath.split("/");
		for (int i = 0; i < path.length; i++) {
			if (!currentLevel.directories.containsKey(path[i])) {
				return null;
			}
			currentLevel = currentLevel.directories.get(path[i]);
		}
		return currentLevel;
	}

	/**
	 * Gets the meta data.
	 * 
	 * @return the meta data
	 */
	public com.woorea.openstack.swift.model.Object getMetaData() {
		return metaData;
	}

	/**
	 * Sets the meta data.
	 * 
	 * @param metaData
	 *            the new meta data
	 */
	public void setMetaData(com.woorea.openstack.swift.model.Object metaData) {
		this.metaData = metaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	
	public String toString() {
		return toString("  ");
	}

	/**
	 * To string.
	 * 
	 * @param ident
	 *            the ident
	 * @return the string
	 */
	public String toString(String ident) {
		StringBuilder builder = new StringBuilder();
		builder.append(getMetaData()).append("\n");
		for (Map.Entry<String, com.woorea.openstack.swift.model.Object> entry : getFiles()
				.entrySet()) {
			builder.append(ident).append("file ").append(entry.getValue())
					.append("\n");
		}
		for (Map.Entry<String, PseudoFileSystem> children : getDirectories()
				.entrySet()) {
			builder.append(ident).append("dir ")
					.append(children.getValue().toString(ident + "   "));
		}
		return builder.toString();
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public PseudoFileSystem getParent() {
		return parent;
	}

	/**
	 * Gets the root.
	 * 
	 * @return the root
	 */
	public PseudoFileSystem getRoot() {
		PseudoFileSystem p = this;
		while (p.getParent() != null) {
			p = p.getParent();
		}
		return p;
	}

}
