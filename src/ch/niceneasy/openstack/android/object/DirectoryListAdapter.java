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
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

/**
 * The Class DirectoryListAdapter.
 * 
 * @author Daniele
 */
public class DirectoryListAdapter extends BaseAdapter {

	/** The directories. */
	private List<PseudoFileSystem> directories = new ArrayList<PseudoFileSystem>();

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new directory list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public DirectoryListAdapter(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return directories.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return directories.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return directories.get(position).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
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
				.setText(((PseudoFileSystem) getItem(position)).getMetaData()
						.getName());
		return convertView;
	}

	/**
	 * Gets the directories.
	 * 
	 * @return the directories
	 */
	public List<PseudoFileSystem> getDirectories() {
		return directories;
	}

	/**
	 * Sets the directories.
	 * 
	 * @param directories
	 *            the new directories
	 */
	public void setDirectories(List<PseudoFileSystem> directories) {
		this.directories = directories;
		this.notifyDataSetChanged();
	}

}
