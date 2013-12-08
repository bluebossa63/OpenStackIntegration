package ch.niceneasy.openstack.android.tenant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
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
		TextView textView;
		if (convertView == null) {
			textView = new TextView(context);
			//textView.setPadding(220, 10, 10, 10);
			//textView.setTextAppearance(context, R.style.menuText);
		} else {
			textView = (TextView) convertView;
		}
		textView.setText(((Tenant)getItem(position)).getName());
		return textView;
	}

	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
	}

}
