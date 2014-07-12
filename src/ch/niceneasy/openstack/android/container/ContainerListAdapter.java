/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.container;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

import com.woorea.openstack.swift.model.Container;

/**
 * The Class ContainerListAdapter.
 * 
 * @author Daniele
 */
public class ContainerListAdapter extends BaseAdapter {

	/** The containers. */
	private List<Container> containers = new ArrayList<Container>();

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new container list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public ContainerListAdapter(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount() {
		return containers.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(int location) {
		return containers.get(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	
	public long getItemId(int location) {
		return containers.get(location).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView textView;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_with_folder_image, null);
			textView = ((TextView) convertView.findViewById(R.id.text1));
			textView.setPadding(20, 10, 10, 10);
			textView.setTextAppearance(context, R.style.menuText);
		}
		((TextView) convertView.findViewById(R.id.text1))
				.setText(((Container) getItem(position)).getName());
		return convertView;
	}

	/**
	 * Sets the containers.
	 * 
	 * @param containers
	 *            the new containers
	 */
	public void setContainers(List<Container> containers) {
		this.containers = containers;
		this.notifyDataSetChanged();
	}

	/**
	 * Gets the containers.
	 * 
	 * @return the containers
	 */
	public List<Container> getContainers() {
		return this.containers;
	}

}
