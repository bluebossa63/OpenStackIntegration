package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

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
	    LayoutInflater mInflater = (LayoutInflater)
	            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		TextView textView;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_with_folder_image, null);
			textView = ((TextView) convertView.findViewById(R.id.text1));
			textView.setPadding(20, 10, 10, 10);
			textView.setTextAppearance(context, R.style.menuText);
		}
		((TextView) convertView.findViewById(R.id.text1)).setText(((PseudoFileSystem)getItem(position)).getMetaData().getName());
		return convertView;		
	}	
	
	public List<PseudoFileSystem> getDirectories() {
		return directories;
	}

	public void setDirectories(List<PseudoFileSystem> directories) {
		this.directories = directories;
	}

}
