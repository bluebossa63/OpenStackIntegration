package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.woorea.openstack.swift.model.Container;

public class ObjectListAdapter extends BaseAdapter {
	
	private List<com.woorea.openstack.swift.model.Object> objects = new ArrayList<com.woorea.openstack.swift.model.Object>();
	
	private Context context;

	public ObjectListAdapter(Context context) {
		this.context = context;
	}	

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int location) {
		return objects.get(location);
	}

	@Override
	public long getItemId(int location) {
		return objects.get(location).hashCode();
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
		textView.setText(((com.woorea.openstack.swift.model.Object)getItem(position)).getName());
		return textView;
	}

	public void setObjects(List<com.woorea.openstack.swift.model.Object> objects) {
		this.objects = objects;
	}

}
