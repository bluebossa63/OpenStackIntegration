package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.woorea.openstack.swift.model.Container;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.niceneasy.openstack.android.R;

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
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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

	public void setObjects(List<com.woorea.openstack.swift.model.Object> objects) {
		this.objects = objects;
	}

	public static String cleanName(String objectName) {
		String[] parts = objectName.split("/");
		return parts[parts.length - 1];
	}

	public List<com.woorea.openstack.swift.model.Object> getObjects() {
		return objects;
	}

}
