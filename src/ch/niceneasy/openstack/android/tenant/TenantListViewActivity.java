package ch.niceneasy.openstack.android.tenant;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.container.ContainerListViewActivity;
import ch.niceneasy.openstack.android.object.ObjectListViewActivity;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;

import com.woorea.openstack.keystone.model.Tenant;

@SuppressLint("NewApi")
public class TenantListViewActivity extends ListActivity {

	TenantListAdapter tenantListAdapter;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_tenants);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				Intent showTenant = new Intent(TenantListViewActivity.this,
						ContainerListViewActivity.class);
				OpenStackClientService.getInstance().setSelectedTenant((Tenant)tenantListAdapter.getItem(item));
				startActivity(showTenant);
			}
		});
		tenantListAdapter = new TenantListAdapter(this);
		setListAdapter(tenantListAdapter);
		
		GetTenantsTask getTenantsTask = new GetTenantsTask();
		getTenantsTask.execute();
	}

	private class GetTenantsTask extends
			AsyncTask<String, Object, List<Tenant>> {

		@Override
		protected List<Tenant> doInBackground(String... params) {
			try {
				return OpenStackClientService.getInstance().getKeystone()
						.tenants().list().execute().getList();
			} catch (Exception e) {
				Looper.prepare();
				showErrorDialog(R.string.error_dlg, e);
				Looper.loop();
			}
			return new ArrayList<Tenant>();
		}

		@Override
		protected void onPostExecute(List<Tenant> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(ProgressBar.GONE);
			tenantListAdapter.setTenants(result);
			getListView().setAdapter(tenantListAdapter);
		}

	}
	
	protected void showErrorDialog(String title, Exception e) {
		showDialog(title, e.getMessage() != null ? e.getMessage() : e
				.getClass().getName());
	}

	protected void showErrorDialog(int ressource, Exception e) {
		showErrorDialog("Fehlermeldung", e);
	}

	protected void showDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(TenantListViewActivity.this,
								TenantListViewActivity.class));
						return;
					}
				});
		alertDialog.show();
	}	

}
