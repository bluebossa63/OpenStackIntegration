/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

/**
 * The Class ObjectListAdapter.
 * 
 * @author Daniele
 */
public class ObjectListAdapter extends BaseAdapter {

	/** The objects. */
	private List<com.woorea.openstack.swift.model.Object> objects = new ArrayList<com.woorea.openstack.swift.model.Object>();

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new object list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public ObjectListAdapter(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	
	public int getCount() {
		return objects.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	
	public Object getItem(int location) {
		return objects.get(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	
	public long getItemId(int location) {
		return objects.get(location).hashCode();
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
					R.layout.list_item_with_media_image, null);
			textView = ((TextView) convertView.findViewById(R.id.text1));
			textView.setPadding(20, 10, 10, 10);
			textView.setTextAppearance(context, R.style.menuText);
		}
		((TextView) convertView.findViewById(R.id.text1))
				.setText(cleanName(((com.woorea.openstack.swift.model.Object) getItem(position))
						.getName()));
		int resId = R.drawable.document_icon;
		String contentType = ((com.woorea.openstack.swift.model.Object) getItem(position))
				.getContentType();
		if (contentType.contains("image")) {
			resId = R.drawable.document_picture_png_icon;
		}

		((ImageView) convertView.findViewById(R.id.image1))
				.setImageResource(resId);
		return convertView;
	}

	/**
	 * Sets the objects.
	 * 
	 * @param objects
	 *            the new objects
	 */
	public void setObjects(List<com.woorea.openstack.swift.model.Object> objects) {
		this.objects = objects;
		this.notifyDataSetChanged();
	}

	/**
	 * Clean name.
	 * 
	 * @param objectName
	 *            the object name
	 * @return the string
	 */
	public static String cleanName(String objectName) {
		String[] parts = objectName.split("/");
		return parts[parts.length - 1];
	}

	/**
	 * Gets the objects.
	 * 
	 * @return the objects
	 */
	public List<com.woorea.openstack.swift.model.Object> getObjects() {
		return objects;
	}

}
