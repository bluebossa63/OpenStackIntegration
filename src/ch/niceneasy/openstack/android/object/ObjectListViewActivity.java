package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.sdk.service.OpenStackClientService;
import ch.niceneasy.openstack.android.tenant.TenantListViewActivity;

import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.Objects;

public class ObjectListViewActivity extends ListActivity {

	DirectoryListAdapter directoryListAdapter;
	ObjectListAdapter objectListAdapter;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_objects);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				Intent showTenant = new Intent(ObjectListViewActivity.this,
						ObjectListViewActivity.class);
				showTenant.putExtra("selectedService", item);
				startActivity(showTenant);
			}
		});
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		objectListAdapter = new ObjectListAdapter(this);
		directoryListAdapter = new DirectoryListAdapter(this);
		setListAdapter(objectListAdapter);
		ListView directoryListView = (ListView) findViewById(R.id.folders);
		directoryListView.setAdapter(directoryListAdapter);
		findViewById(android.R.id.content);
		progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
		progressBar.setIndeterminate(true);
		progressBar.bringToFront();
		GetObjectTask getObjectTask = new GetObjectTask();
		getObjectTask.execute();
	}

	private class GetObjectTask extends AsyncTask<String, Object, Objects> {

		@Override
		protected Objects doInBackground(String... params) {
			try {
				Swift swift = OpenStackClientService.getInstance().getSwift(
						OpenStackClientService.getInstance()
								.getSelectedTenant().getId());
				return swift
						.containers()
						.container(
								OpenStackClientService.getInstance()
										.getSelectedContainer().getName())
						.list().execute();
			} catch (Exception e) {
				Looper.prepare();
				showErrorDialog(R.string.error_dlg, e);
				Looper.loop();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Objects objects) {
			super.onPostExecute(objects);
			progressBar.setVisibility(ProgressBar.GONE);			
			OpenStackClientService.getInstance().parseSelectedPseudoFileSystem(
					objects);
			objectListAdapter.setObjects(new ArrayList<Object>(
					OpenStackClientService.getInstance().getPseudoFileSystem()
							.getFiles().values()));
			directoryListAdapter
					.setDirectories(new ArrayList<PseudoFileSystem>(
							OpenStackClientService.getInstance()
									.getPseudoFileSystem().getDirectories()
									.values()));
			getListView().setAdapter(objectListAdapter);
			ListView directoryListView = (ListView) findViewById(R.id.folders);
			directoryListView.setAdapter(directoryListAdapter);
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
						startActivity(new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
						return;
					}
				});
		alertDialog.show();
	}

}
