package ch.niceneasy.openstack.android.tenant;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.OpenstackListActivity;
import ch.niceneasy.openstack.android.base.TaskResult;
import ch.niceneasy.openstack.android.container.ContainerListViewActivity;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;

import com.woorea.openstack.keystone.model.Tenant;

public class TenantListViewActivity extends OpenstackListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new TenantListAdapter(this));
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				Intent showTenant = new Intent(TenantListViewActivity.this,
						ContainerListViewActivity.class);
				getApplicationState().setSelectedTenant(
						(Tenant) getListAdapter().getItem(item));
				startActivity(showTenant);
			}
		});
		loadData();
		
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	    	getApplicationState().setShareIntent(intent);
	    	Toast.makeText(this, "select folder to updload and confirm in action bar", Toast.LENGTH_LONG).show();
	    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
	    	getApplicationState().setShareIntent(intent);
	    	Toast.makeText(this, "select folder to updload and confirm in action bar", Toast.LENGTH_LONG).show();
	    }		
	    
		// BEGIN_INCLUDE (inflate_set_custom_view)
		// Inflate a "Done/Cancel" custom action bar view.
		final LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(
						LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.ctionbar_custom_view_settings_cancel, null);
		customActionBarView.findViewById(R.id.menu)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu menu = new PopupMenu(TenantListViewActivity.this,v);
						menu.getMenuInflater().inflate(R.menu.homemenu, menu.getMenu());
						menu.show();
					}
				});
//		customActionBarView.findViewById(R.id.actionbar_cancel)
//				.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//					}
//				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
		// END_INCLUDE (inflate_set_custom_view)
	}

	private class GetTenantsTask extends
			AsyncTask<String, Object, TaskResult<List<Tenant>>> {

		boolean editSettings = false;

		@Override
		protected TaskResult<List<Tenant>> doInBackground(String... params) {
			try {
				return new TaskResult<List<Tenant>>(OpenStackClientService.getInstance().getKeystone()
						.tenants().list().execute().getList());
			} catch (Exception e) {
				getService().resetConnection();
				return new TaskResult<List<Tenant>>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<List<Tenant>> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(ProgressBar.GONE);
			if (result.isValid()) {
				((TenantListAdapter) getListAdapter()).setTenants(result.getResult());
				if (editSettings) {
					startActivity(new Intent(TenantListViewActivity.this,
							ServicePreferences.class));
				}
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(), new Intent(
						TenantListViewActivity.this,
						TenantListViewActivity.class));				
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sharemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, ServicePreferences.class));
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (!OpenStackClientService.getInstance().isLoggedIn()) {
			loadData();
		}
	}

	private void loadData() {
		progressBar.bringToFront();
		GetTenantsTask getTenantsTask = new GetTenantsTask();
		getTenantsTask.execute();
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.list_tenants);
	}

	@Override
	public void finish() {
		getApplicationState().setShouldReturnToCaller(false);
		super.finish();
	}

	
		
}
