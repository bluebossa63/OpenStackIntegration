package ch.niceneasy.openstack.android.container;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.object.ObjectListViewActivity;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.tenant.TenantListViewActivity;

import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;

public class ContainerListViewActivity extends ListActivity {

	ContainerListAdapter containerListAdapter;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_containers);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				Intent showObjects = new Intent(ContainerListViewActivity.this,
						ObjectListViewActivity.class);
				OpenStackClientService.getInstance().setSelectedContainer(
						(Container) containerListAdapter.getItem(item));
				startActivity(showObjects);
			}
		});
		containerListAdapter = new ContainerListAdapter(this);
		setListAdapter(containerListAdapter);
		GetContainerTask getContainerTask = new GetContainerTask();
		getContainerTask.execute();
	}

	private class GetContainerTask extends
			AsyncTask<String, Object, List<Container>> {

		@Override
		protected List<Container> doInBackground(String... params) {
			try {
				Swift swift = OpenStackClientService.getInstance().getSwift(
						OpenStackClientService.getInstance()
								.getSelectedTenant().getId());
				return swift.containers().list().execute().getList();
			} catch (Exception e) {
				Looper.prepare();
				showErrorDialog(R.string.error_dlg, e);
				Looper.loop();
			}
			return new ArrayList<Container>();
		}

		@Override
		protected void onPostExecute(List<Container> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(ProgressBar.GONE);			
			containerListAdapter.setContainers(result);
			getListView().setAdapter(containerListAdapter);
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
						startActivity(new Intent(ContainerListViewActivity.this,
								TenantListViewActivity.class));
						return;
					}
				});
		alertDialog.show();
	}		

}
