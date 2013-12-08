package ch.niceneasy.openstack.android.container;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woorea.openstack.swift.model.Container;

public class ContainerListAdapter extends BaseAdapter {
	
	private List<Container> containers = new ArrayList<Container>();
	
	private Context context;

	public ContainerListAdapter(Context context) {
		this.context = context;
	}	

	@Override
	public int getCount() {
		return containers.size();
	}

	@Override
	public Object getItem(int location) {
		return containers.get(location);
	}

	@Override
	public long getItemId(int location) {
		return containers.get(location).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		if (convertView == null) {
			textView = new TextView(context);
			//textView.setPadding(220, 10, 10, 10);
			//textView.setTextAppearance(context, R.style.menuText);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(((Container)getItem(position)).getName());
		return textView;
	}

	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}

}
