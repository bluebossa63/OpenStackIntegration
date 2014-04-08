/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.tenant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

import com.woorea.openstack.keystone.model.Tenant;

/**
 * The Class TenantListAdapter.
 * 
 * @author Daniele
 */
public class TenantListAdapter extends BaseAdapter {

	/** The tenants. */
	private List<Tenant> tenants = new ArrayList<Tenant>();

	/** The context. */
	private Context context;

	/**
	 * Instantiates a new tenant list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public TenantListAdapter(Context context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return tenants.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int location) {
		return tenants.get(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int location) {
		return tenants.get(location).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView textView;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.list_item_with_container_image, null);
			textView = ((TextView) convertView.findViewById(R.id.text1));
			textView.setPadding(20, 10, 10, 10);
			textView.setTextAppearance(context, R.style.menuText);
			// } else {
			// textView = (TextView) convertView;
		}
		((TextView) convertView.findViewById(R.id.text1))
				.setText(((Tenant) getItem(position)).getName());
		// ((ImageView) convertView.findViewById(R.id.image1));
		return convertView;
	}

	/**
	 * Sets the tenants.
	 * 
	 * @param tenants
	 *            the new tenants
	 */
	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
		this.notifyDataSetChanged();
	}

}
