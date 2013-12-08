package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import com.woorea.openstack.keystone.model.Tenant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DirectoryListAdapter extends BaseAdapter {
	
	private List<PseudoFileSystem> directories = new ArrayList<PseudoFileSystem>();
	
	private Context context;

	public DirectoryListAdapter(Context context) {
		this.context = context;
	}		

	@Override
	public int getCount() {
		return directories.size();
	}

	@Override
	public Object getItem(int position) {
		return directories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return directories.get(position).hashCode();
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
		textView.setText(((PseudoFileSystem)getItem(position)).getMetaData().getName());
		return textView;
	}

	public List<PseudoFileSystem> getDirectories() {
		return directories;
	}

	public void setDirectories(List<PseudoFileSystem> directories) {
		this.directories = directories;
	}

}
