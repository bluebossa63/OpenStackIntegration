/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
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
import android.widget.Toast;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.OpenstackListActivity;
import ch.niceneasy.openstack.android.base.TaskResult;
import ch.niceneasy.openstack.android.container.ContainerListViewActivity;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.sdk.service.ServicePreferences;
import ch.niceneasy.openstack.android.signup.SignupService;
import ch.niceneasy.openstack.android.signup.SplashScreenActivity;

import com.woorea.openstack.keystone.model.Tenant;

/**
 * The Class TenantListViewActivity.
 * 
 * @author Daniele
 */
public class TenantListViewActivity extends OpenstackListActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.niceneasy.openstack.android.base.OpenstackListActivity#onCreate(android
	 * .os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ServicePreferences.updateService(this);
		if (SignupService.getInstance().getUser().getId() == null
				|| SignupService.getInstance().getUser().getId().trim()
						.length() == 0) {
			Intent intent = new Intent(this, SplashScreenActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			return;
		}
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
			Toast.makeText(this,
					"select folder to updload and confirm in action bar",
					Toast.LENGTH_LONG).show();
		} else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
			getApplicationState().setShareIntent(intent);
			Toast.makeText(this,
					"select folder to updload and confirm in action bar",
					Toast.LENGTH_LONG).show();
		}

		// BEGIN_INCLUDE (inflate_set_custom_view)
		// Inflate a "Done/Cancel" custom action bar view.
		final LayoutInflater inflater = (LayoutInflater) getActionBar()
				.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
				R.layout.ctionbar_custom_view_settings_cancel, null);
		customActionBarView.findViewById(R.id.menu).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu menu = new PopupMenu(
								TenantListViewActivity.this, v);
						menu.getMenuInflater().inflate(R.menu.homemenu,
								menu.getMenu());
						menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								return onOptionsItemSelected(item);
							}
						});
						menu.show();
					}
				});
		// customActionBarView.findViewById(R.id.actionbar_cancel)
		// .setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// }
		// });

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
		// END_INCLUDE (inflate_set_custom_view)
	}

	/**
	 * The Class GetTenantsTask.
	 */
	private class GetTenantsTask extends
			AsyncTask<String, Object, TaskResult<List<Tenant>>> {

		/** The edit settings. */
		boolean editSettings = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected TaskResult<List<Tenant>> doInBackground(String... params) {
			try {
				return new TaskResult<List<Tenant>>(OpenStackClientService
						.getInstance().getKeystone().tenants().list().execute()
						.getList());
			} catch (Exception e) {
				getService().resetConnection();
				return new TaskResult<List<Tenant>>(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(TaskResult<List<Tenant>> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				((TenantListAdapter) getListAdapter()).setTenants(result
						.getResult());
				if (editSettings) {
					startActivity(new Intent(TenantListViewActivity.this,
							ServicePreferences.class));
				}
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(TenantListViewActivity.this,
								TenantListViewActivity.class));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.addcontainer, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		if (!OpenStackClientService.getInstance().isLoggedIn()) {
			loadData();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	/**
	 * Load data.
	 */
	private void loadData() {
		progressBar.bringToFront();
		GetTenantsTask getTenantsTask = new GetTenantsTask();
		getTenantsTask.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.niceneasy.openstack.android.base.OpenstackListActivity#setContentView
	 * ()
	 */
	@Override
	protected void setContentView() {
		setContentView(R.layout.list_tenants);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		getApplicationState().setShouldReturnToCaller(false);
		super.finish();
	}

}
