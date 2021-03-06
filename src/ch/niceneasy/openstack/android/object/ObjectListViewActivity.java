/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
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

/**
 * The Class ObjectListViewActivity.
 * 
 * @author Daniele
 */
public class ObjectListViewActivity extends OpenstackListActivity {

	/** The directory list view. */
	ListView directoryListView;

	/** The directory list adapter. */
	DirectoryListAdapter directoryListAdapter;

	/** The camera pic request. */
	final int CAMERA_PIC_REQUEST = 2;

	/** The gallery pic request. */
	final int GALLERY_PIC_REQUEST = 3;

	/** The dir. */
	File dir = null;

	/** The m image uri. */
	Uri mImageUri = null;

	/** The m current photo path. */
	String mCurrentPhotoPath;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.niceneasy.openstack.android.base.OpenstackListActivity#onCreate(android
	 * .os.Bundle)
	 */
	
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
						
						public void onClick(View v) {
							UploadObjectTask uploadObjectTask = new UploadObjectTask(
									getApplicationState().getShareIntent());
							uploadObjectTask.execute();
						}
					});
			customActionBarView.findViewById(R.id.actionbar_cancel)
					.setOnClickListener(new View.OnClickListener() {
						
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
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				getApplicationState().setSelectedDirectory(
						(PseudoFileSystem) directoryListAdapter.getItem(item));
				updateLists();
			}
		});

		directoryListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					
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
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int item,
					long arg3) {
				getApplicationState().setSelectedObject(
						(Object) getListAdapter().getItem(item));
				LoadObjectTask loadObjectTask = new LoadObjectTask();
				loadObjectTask.execute();
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			
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

	/**
	 * Creates the temp files.
	 * 
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sharemenu, menu);
		return true;
	}

	 protected void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
         if (mImageUri != null) {
            outState.putString("cameraImageUri", mImageUri.toString());
         }
	 }

	 protected void onRestoreInstanceState(Bundle savedInstanceState) {
         super.onRestoreInstanceState(savedInstanceState);
         if (savedInstanceState.containsKey("cameraImageUri")) {
             mImageUri = Uri.parse(savedInstanceState
             .getString("cameraImageUri"));
         }
	 }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			DirectoryNamePromptDialog dlg = new DirectoryNamePromptDialog();
			dlg.show(getFragmentManager(), "Container Name Prompter");
			return true;
		case R.id.camera:
			try {
                 Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 mImageUri = Uri.fromFile(createTempFiles());
                 cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                 startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			 } catch (IOException e) {
			    e.printStackTrace();
			 }
			return true;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentData) {
		super.onActivityResult(requestCode, resultCode, intentData);
		switch (requestCode) {
		case GALLERY_PIC_REQUEST:
		case CAMERA_PIC_REQUEST:
            if (resultCode == RESULT_OK) {
                getApplicationState().setShareIntent(null);
                String imagePath = null;
                Uri uri = null;
                if ( intentData == null) {
                    mCurrentPhotoPath = null;
                    uri = mImageUri;
                } else {
                    uri = intentData.getData();
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
                    intentData.setDataAndType(
                            Uri.fromFile(new File(imagePath)),
                            map.getMimeTypeFromExtension(MimeTypeMap
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

	/**
	 * The Class LoadObjectTask.
	 */
	private class LoadObjectTask extends
			AsyncTask<String, Object, TaskResult<File>> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@SuppressLint("SdCardPath")
		
		protected TaskResult<File> doInBackground(String... params) {
			File tempFile = null;
			try {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(ObjectListViewActivity.this);
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
				File outputDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath(),
						"openstack");
				if (!outputDir.exists()) {
					outputDir.mkdir();
				}
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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

	/**
	 * The Class GetObjectsTask.
	 */
	private class GetObjectsTask extends
			AsyncTask<String, Object, TaskResult<Objects>> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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

	/**
	 * Update lists.
	 */
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

	/**
	 * The Class UploadObjectTask.
	 */
	private class UploadObjectTask extends
			AsyncTask<String, Object, TaskResult<ObjectForUpload>> {

		/** The intent data. */
		Intent intentData;

		/**
		 * Instantiates a new upload object task.
		 * 
		 * @param intentData
		 *            the intent data
		 */
		private UploadObjectTask(Intent intentData) {
			this.intentData = intentData;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		
		protected TaskResult<ObjectForUpload> doInBackground(String... params) {
			try {
                String imagePath = null;
                if ( intentData != null) {
                    Uri imageUri = null;
                    if (getApplicationState().isInSharingMode()) {
                        if (intentData
                                .getParcelableExtra("android.intent.extra.STREAM") != null) {
                            imageUri = intentData
                                    .getParcelableExtra("android.intent.extra.STREAM");
                        }
                    }
                    imagePath = GraphicsUtil.getOriginalFilePath(
                            ObjectListViewActivity.this, imageUri);
                } else {

                    imagePath = GraphicsUtil.getOriginalFilePath(
                            ObjectListViewActivity.this, mImageUri);
                }

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
                if ( intentData != null) {
                    objectForUpload.getProperties().put("Content-Type",
                            intentData.getType());
                } else {
                    objectForUpload.getProperties().put("Content-Type",
                            "image/jpeg");
                }

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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

	/**
	 * The Class DeleteObjectTask.
	 */
	private class DeleteObjectTask extends
			AsyncTask<String, Object, TaskResult<List<Object>>> {

		/** The object. */
		private Object object;

		/**
		 * Instantiates a new delete object task.
		 * 
		 * @param object
		 *            the object
		 */
		private DeleteObjectTask(Object object) {
			this.object = object;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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

	/**
	 * The Class DeleteDirectoryTask.
	 */
	private class DeleteDirectoryTask extends
			AsyncTask<String, Object, TaskResult<PseudoFileSystem>> {

		/** The object. */
		private Object object;

		/** The pseudo file system. */
		private PseudoFileSystem pseudoFileSystem;

		/**
		 * Instantiates a new delete directory task.
		 * 
		 * @param object
		 *            the object
		 * @param pseudoFileSystem
		 *            the pseudo file system
		 */
		private DeleteDirectoryTask(Object object,
				PseudoFileSystem pseudoFileSystem) {
			this.pseudoFileSystem = pseudoFileSystem;
			this.object = object;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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

	/**
	 * Write.
	 * 
	 * @param stream
	 *            the stream
	 * @param is
	 *            the is
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.niceneasy.openstack.android.base.OpenstackListActivity#setContentView
	 * ()
	 */
	
	protected void setContentView() {
		setContentView(R.layout.list_objects);
	}

	/**
	 * Gets the directory list adapter.
	 * 
	 * @return the directory list adapter
	 */
	public DirectoryListAdapter getDirectoryListAdapter() {
		return directoryListAdapter;
	}

	/**
	 * Creates the folder.
	 * 
	 * @param name
	 *            the name
	 */
	public void createFolder(String name) {
		progressBar.setVisibility(View.VISIBLE);
		CreateDirectoryTask createDirectoryTask = new CreateDirectoryTask(name);
		createDirectoryTask.execute();
	}

	/**
	 * The Class CreateDirectoryTask.
	 */
	private class CreateDirectoryTask extends
			AsyncTask<String, Object, TaskResult<PseudoFileSystem>> {

		/** The folder name. */
		private String folderName;

		/**
		 * Instantiates a new creates the directory task.
		 * 
		 * @param folderName
		 *            the folder name
		 */
		private CreateDirectoryTask(String folderName) {
			this.folderName = folderName;
			if (!folderName.endsWith("/")) {
				this.folderName += "/";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		
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
