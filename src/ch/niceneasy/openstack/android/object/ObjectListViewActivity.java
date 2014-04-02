package ch.niceneasy.openstack.android.object;

import static ch.niceneasy.openstack.android.object.ObjectListAdapter.cleanName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ch.niceneasy.openstack.android.R;
import ch.niceneasy.openstack.android.base.GraphicsUtil;
import ch.niceneasy.openstack.android.base.OpenstackListActivity;
import ch.niceneasy.openstack.android.base.TaskResult;
import ch.niceneasy.openstack.android.tenant.TenantListViewActivity;

import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.woorea.openstack.swift.model.ObjectForUpload;
import com.woorea.openstack.swift.model.Objects;

public class ObjectListViewActivity extends OpenstackListActivity {

	ListView directoryListView;
	DirectoryListAdapter directoryListAdapter;

	final int CAMERA_PIC_REQUEST = 2;
	final int GALLERY_PIC_REQUEST = 3;

	File dir = null;
	Uri mImageUri = null;
	String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getApplicationState().isInSharingMode()) {

			// BEGIN_INCLUDE (inflate_set_custom_view)
			// Inflate a "Done/Cancel" custom action bar view.
			final LayoutInflater inflater = (LayoutInflater) getActionBar()
					.getThemedContext().getSystemService(
							LAYOUT_INFLATER_SERVICE);
			final View customActionBarView = inflater.inflate(
					R.layout.actionbar_custom_view_done_cancel, null);
			customActionBarView.findViewById(R.id.actionbar_done)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							UploadObjectTask uploadObjectTask = new UploadObjectTask(
									getApplicationState().getShareIntent());
							uploadObjectTask.execute();
						}
					});
			customActionBarView.findViewById(R.id.actionbar_cancel)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getApplicationState().setSelectedTenant(null);
							finish();
						}
					});

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

		Button button = (Button) findViewById(R.id.back);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getApplicationState().getSelectedDirectory().getParent() == null) {
					// startActivity(new Intent(ObjectListViewActivity.this,
					// TenantListViewActivity.class));
					getApplicationState().setSelectedDirectory(null);
					finish();
				} else {
					getApplicationState().setSelectedDirectory(
							getApplicationState().getSelectedDirectory()
									.getParent());
					updateLists();
				}
			}
		});

		directoryListView = (ListView) findViewById(R.id.folders);
		directoryListAdapter = new DirectoryListAdapter(this);
		directoryListView.setAdapter(directoryListAdapter);
		directoryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				getApplicationState().setSelectedDirectory(
						(PseudoFileSystem) directoryListAdapter.getItem(item));
				updateLists();
			}
		});

		directoryListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int item, long arg3) {
						PseudoFileSystem fs = (PseudoFileSystem) ObjectListViewActivity.this
								.getDirectoryListAdapter().getItem(item);
						final Object selectedObject = fs.getMetaData();
						AlertDialog alertDialog = new AlertDialog.Builder(
								ObjectListViewActivity.this).create();
						alertDialog.setTitle("delecte top folder");
						alertDialog
								.setMessage("are you sure that you want to delete this folder?");
						alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
								"OK", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										DeleteDirectoryTask deleteDirectoryTask = new DeleteDirectoryTask(
												selectedObject,
												getApplicationState()
														.getPseudoFileSystem());
										deleteDirectoryTask.execute();
										return;
									}
								});
						alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
								"Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								});
						alertDialog.show();
						return true;
					}
				});

		setListAdapter(new ObjectListAdapter(this));
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				getApplicationState().setSelectedObject(
						(Object) getListAdapter().getItem(item));
				LoadObjectTask loadObjectTask = new LoadObjectTask();
				loadObjectTask.execute();
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int item, long arg3) {
				final Object selectedObject = (Object) getListAdapter()
						.getItem(item);
				AlertDialog alertDialog = new AlertDialog.Builder(
						ObjectListViewActivity.this).create();
				alertDialog.setTitle("delecte top folder");
				alertDialog
						.setMessage("are you sure that you want to delete this folder?");
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DeleteObjectTask deleteObjectTask = new DeleteObjectTask(
										selectedObject);
								deleteObjectTask.execute();
								return;
							}
						});
				alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						"Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				alertDialog.show();
				return true;
			}
		});

		GetObjectsTask getObjectsTask = new GetObjectsTask();
		getObjectsTask.execute();
	}

	public File createTempFiles() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sharemenu, menu);
		return true;
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (mImageUri != null) {
//			outState.putString("cameraImageUri", mImageUri.toString());
//		}
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		if (savedInstanceState.containsKey("cameraImageUri")) {
//			mImageUri = Uri.parse(savedInstanceState
//					.getString("cameraImageUri"));
//		}
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			DirectoryNamePromptDialog dlg = new DirectoryNamePromptDialog();
			dlg.show(getFragmentManager(), "Container Name Prompter");
			return true;
		case R.id.camera:
			
			Intent cameraIntent = new
			Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); return
			true;
			

//			try {
//				Intent cameraIntent = new Intent(
//						MediaStore.ACTION_IMAGE_CAPTURE);
//				mImageUri = Uri.fromFile(createTempFiles());
//				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//				startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return true;

		case R.id.gallery:
			Intent photoPickerIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, GALLERY_PIC_REQUEST);
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentData) {
		super.onActivityResult(requestCode, resultCode, intentData);
		switch (requestCode) {
		case GALLERY_PIC_REQUEST:
		case CAMERA_PIC_REQUEST:
			if (resultCode == RESULT_OK) {
				getApplicationState().setShareIntent(null);
				String imagePath = null;
				if (mCurrentPhotoPath != null) {
					imagePath = mCurrentPhotoPath;
					mCurrentPhotoPath = null;
				} else {
					Uri uri = intentData.getData();
					if (uri == null) {
						if (intentData
								.getParcelableExtra("android.intent.extra.STREAM") != null) {
							uri = intentData
									.getParcelableExtra("android.intent.extra.STREAM");
						} 
					}
					imagePath = GraphicsUtil.getOriginalFilePath(
							ObjectListViewActivity.this, uri);
				}
				MimeTypeMap map = MimeTypeMap.getSingleton();
				if (map.hasExtension(MimeTypeMap
						.getFileExtensionFromUrl(imagePath))) {
					intentData.setDataAndType(Uri.fromFile(new File(imagePath)), map
							.getMimeTypeFromExtension(MimeTypeMap
									.getFileExtensionFromUrl(imagePath)));
				}
				UploadObjectTask uploadObjectTask = new UploadObjectTask(
						intentData);
				uploadObjectTask.execute();
				break;
			}
		default:
			Toast.makeText(this, "Picture NOt taken", Toast.LENGTH_LONG).show();
		}

	}

	private class LoadObjectTask extends
			AsyncTask<String, Object, TaskResult<File>> {

		@SuppressLint("SdCardPath")
		@Override
		protected TaskResult<File> doInBackground(String... params) {
			File tempFile = null;
			try {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(ObjectListViewActivity.this);
				String localStorage = prefs.getString(
						ObjectListViewActivity.this
								.getString(R.string.LOCAL_STORAGE),
						"/sdcard/Download/");
				ObjectDownload download = getService()
						.getSwift(
								getApplicationState().getSelectedTenant()
										.getId())
						.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName())
						.download(
								getApplicationState().getSelectedObject()
										.getName()).execute();
				File outputDir = new File(localStorage, "openstack");
				if (!outputDir.exists()) {
					outputDir.mkdir();
				}
				String cleanedName = cleanName(getApplicationState()
						.getSelectedObject().getName());
				if (!cleanedName.contains(".")) {
					String extension = MimeTypeMap.getSingleton()
							.getExtensionFromMimeType(
									getApplicationState().getSelectedObject()
											.getContentType());
					if (extension != null)
						cleanedName += "." + extension;
				}
				tempFile = new File(outputDir, cleanedName);
				FileOutputStream fos = new FileOutputStream(tempFile);
				write(fos, download.getInputStream());
				fos.close();
			} catch (Exception e) {
				return new TaskResult<File>(e);
			}
			return new TaskResult<File>(tempFile);
		}

		@Override
		protected void onPostExecute(TaskResult<File> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				File file = result.getResult();
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				Object object = getApplicationState().getSelectedObject();
				if ("application/octet-stream".equals(object.getContentType())) {
					String extension = MimeTypeMap.getFileExtensionFromUrl(file
							.getName());
					if (MimeTypeMap.getSingleton().hasExtension(extension)) {
						object.setContentType(MimeTypeMap.getSingleton()
								.getMimeTypeFromExtension(extension));
					} else {
						try {
							FileInputStream fis = new FileInputStream(file);
							String guessed = URLConnection
									.guessContentTypeFromStream(fis);
							if (guessed != null) {
								object.setContentType(guessed);
							}
							intent.setType(object.getContentType());
						} catch (Exception e) {
							// ignore
						}
					}
				}
				intent.setDataAndType(Uri.fromFile(file),
						object.getContentType());
				startActivity(intent);
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
			}

		}
	}

	private class GetObjectsTask extends
			AsyncTask<String, Object, TaskResult<Objects>> {

		@Override
		protected TaskResult<Objects> doInBackground(String... params) {
			try {
				Swift swift = getService().getSwift(
						getApplicationState().getSelectedTenant().getId());
				return new TaskResult<Objects>(swift
						.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName()).list().execute());
			} catch (Exception e) {
				return new TaskResult<Objects>(e);

			}
		}

		@Override
		protected void onPostExecute(TaskResult<Objects> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				PseudoFileSystem selectedDirectory = getApplicationState()
						.getSelectedDirectory();
				getApplicationState().parseSelectedPseudoFileSystem(
						result.getResult());
				if (selectedDirectory != null
						&& selectedDirectory.getMetaData() != null
						&& selectedDirectory.getMetaData().getName() != null) {
					getApplicationState().setSelectedDirectory(
							PseudoFileSystem.findChild(getApplicationState()
									.getSelectedDirectory().getRoot(),
									selectedDirectory.getMetaData().getName()));
				} else {
					getApplicationState().setSelectedDirectory(
							getApplicationState().getPseudoFileSystem());
				}
				updateLists();
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
			}
		}
	}

	private void updateLists() {
		((ObjectListAdapter) getListAdapter())
				.setObjects(new ArrayList<Object>(getApplicationState()
						.getSelectedDirectory().getFiles().values()));
		directoryListAdapter.setDirectories(new ArrayList<PseudoFileSystem>(
				getApplicationState().getSelectedDirectory().getDirectories()
						.values()));
		ListView directoryListView = (ListView) findViewById(R.id.folders);
		directoryListView.setAdapter(directoryListAdapter);
	}

	private class UploadObjectTask extends
			AsyncTask<String, Object, TaskResult<ObjectForUpload>> {

		Intent intentData;

		private UploadObjectTask(Intent intentData) {
			this.intentData = intentData;
		}

		@Override
		protected TaskResult<ObjectForUpload> doInBackground(String... params) {
			try {
				Uri imageUri = intentData.getData();
				if (getApplicationState().isInSharingMode()) {
					if (intentData
							.getParcelableExtra("android.intent.extra.STREAM") != null) {
						imageUri = intentData
								.getParcelableExtra("android.intent.extra.STREAM");
					}
				}
				String imagePath = GraphicsUtil.getOriginalFilePath(
						ObjectListViewActivity.this, imageUri);
				ObjectForUpload objectForUpload = new ObjectForUpload();
				objectForUpload.setContainer(getApplicationState()
						.getSelectedContainer().getName());
				String directory = "";
				if (getApplicationState().getSelectedDirectory().getMetaData() != null) {
					Log.i("test", getApplicationState().getSelectedDirectory()
							.getMetaData().getName());
					directory = getApplicationState().getSelectedDirectory()
							.getMetaData().getName();
				}
				String[] parts = imagePath.split("/");
				objectForUpload.setName(directory + parts[parts.length - 1]);
				objectForUpload.getProperties().put("Content-Type",
						intentData.getType());
				objectForUpload.setInputStream(new FileInputStream(imagePath));
				getService()
						.getSwift(
								getApplicationState().getSelectedTenant()
										.getId())
						.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName()).upload(objectForUpload)
						.execute();
				return new TaskResult<ObjectForUpload>(objectForUpload);
			} catch (Exception e) {
				return new TaskResult<ObjectForUpload>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<ObjectForUpload> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				if (getApplicationState().isInSharingMode()) {
					Intent tempIntent = getApplicationState().getShareIntent();
					getApplicationState().setShareIntent(null);
					setResult(Activity.RESULT_OK, tempIntent);
					getApplicationState().setShouldReturnToCaller(true);
					finish();
				} else {
					GetObjectsTask getObjectsTask = new GetObjectsTask();
					getObjectsTask.execute();
				}
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
			}
		}
	}

	private class DeleteObjectTask extends
			AsyncTask<String, Object, TaskResult<List<Object>>> {

		private Object object;

		private DeleteObjectTask(Object object) {
			this.object = object;
		}

		@Override
		protected TaskResult<List<Object>> doInBackground(String... params) {
			try {
				Swift swift = getService().getSwift(
						getApplicationState().getSelectedTenant().getId());
				swift.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName()).delete(object.getName())
						.execute();
				List<Object> tempList = new ArrayList<Object>(
						((ObjectListAdapter) getListAdapter()).getObjects());
				tempList.remove(object);
				Comparator<Object> comparator = new Comparator<Object>() {

					@Override
					public int compare(Object lhs, Object rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}

				};
				Collections.sort(tempList, comparator);
				return new TaskResult<List<Object>>(tempList);
			} catch (Exception e) {
				return new TaskResult<List<Object>>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<List<Object>> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				((ObjectListAdapter) getListAdapter()).setObjects(result
						.getResult());
				setListAdapter(getListAdapter());
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
			}
		}
	}

	private class DeleteDirectoryTask extends
			AsyncTask<String, Object, TaskResult<PseudoFileSystem>> {

		private Object object;
		private PseudoFileSystem pseudoFileSystem;

		private DeleteDirectoryTask(Object object,
				PseudoFileSystem pseudoFileSystem) {
			this.pseudoFileSystem = pseudoFileSystem;
			this.object = object;
		}

		@Override
		protected TaskResult<PseudoFileSystem> doInBackground(String... params) {
			try {
				Swift swift = getService().getSwift(
						getApplicationState().getSelectedTenant().getId());
				swift.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName()).delete(object.getName())
						.execute();
				PseudoFileSystem pseudoFileSystem = PseudoFileSystem.findChild(
						this.pseudoFileSystem.getRoot(), object.getName());
				pseudoFileSystem.getParent().getDirectories()
						.remove(pseudoFileSystem);
				return new TaskResult<PseudoFileSystem>(pseudoFileSystem);
			} catch (Exception e) {
				return new TaskResult<PseudoFileSystem>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<PseudoFileSystem> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				directoryListView.setAdapter(getDirectoryListAdapter());
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(),
						new Intent(ObjectListViewActivity.this,
								TenantListViewActivity.class));
			}
		}
	}

	private static void write(OutputStream stream, InputStream is) {
		try {
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				stream.write(buffer, 0, len);
			}
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.list_objects);
	}

	public DirectoryListAdapter getDirectoryListAdapter() {
		return directoryListAdapter;
	}

	public void createFolder(String name) {
		progressBar.setVisibility(View.VISIBLE);
		CreateDirectoryTask createDirectoryTask = new CreateDirectoryTask(name);
		createDirectoryTask.execute();
	}

	private class CreateDirectoryTask extends
			AsyncTask<String, Object, TaskResult<PseudoFileSystem>> {

		private String folderName;

		private CreateDirectoryTask(String folderName) {
			this.folderName = folderName;
			if (!folderName.endsWith("/")) {
				this.folderName += "/";
			}
		}

		@Override
		protected TaskResult<PseudoFileSystem> doInBackground(String... params) {
			try {
				Container container = new Container();
				container.setName(folderName);
				String path = "";
				if (getApplicationState().getSelectedDirectory().getMetaData() != null
						&& getApplicationState().getSelectedDirectory()
								.getParent() != null) {
					path += getApplicationState().getSelectedDirectory()
							.getMetaData().getName();
				}
				path += folderName;

				Swift swift = getService().getSwift(
						getApplicationState().getSelectedTenant().getId());
				swift.containers()
						.container(
								getApplicationState().getSelectedContainer()
										.getName()).createDirectory(path)
						.execute();

				PseudoFileSystem.findOrCreateChild(getApplicationState()
						.getSelectedDirectory().getRoot(), folderName);
				return new TaskResult<PseudoFileSystem>(getApplicationState()
						.getSelectedDirectory());
			} catch (Exception e) {
				return new TaskResult<PseudoFileSystem>(e);
			}
		}

		@Override
		protected void onPostExecute(TaskResult<PseudoFileSystem> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			if (result.isValid()) {
				directoryListAdapter
						.setDirectories(new ArrayList<PseudoFileSystem>(result
								.getResult().getDirectories().values()));
				ListView directoryListView = (ListView) findViewById(R.id.folders);
				directoryListView.setAdapter(directoryListAdapter);
			} else {
				showErrorDialog(R.string.error_dlg, result.getException(), null);
			}
		}
	}

}
