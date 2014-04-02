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

public class TenantListAdapter extends BaseAdapter {

	private List<Tenant> tenants = new ArrayList<Tenant>();

	private Context context;

	public TenantListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return tenants.size();
	}

	@Override
	public Object getItem(int location) {
		return tenants.get(location);
	}

	@Override
	public long getItemId(int location) {
		return tenants.get(location).hashCode();
	}

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

	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
		this.notifyDataSetChanged();
	}

}
