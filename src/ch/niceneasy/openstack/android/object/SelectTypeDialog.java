package ch.niceneasy.openstack.android.object;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ch.niceneasy.openstack.android.R;

public class SelectTypeDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.select_type_dialog, null))
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SelectTypeDialog.this.getDialog().cancel();
							}
						});
		Dialog dialog = builder.create();
		
		//inflater.inflate(resource, root)
		//dialog.findViewById(id)
		return dialog;
	}
	
	
	private class MediaType {
		int ressourceId;
		String name;
	}
	
	private class TypeAdapter extends BaseAdapter {
		
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		
		TypeAdapter() {
			MediaType mediaType = new MediaType();
			mediaType.ressourceId = R.drawable.folder_media_icon;
			mediaType.name = getActivity().getString(R.string.folder);
			mediaTypes.add(mediaType);
			mediaType = new MediaType();
			mediaType.ressourceId = R.drawable.folder_media_icon;
			mediaType.name = getActivity().getString(R.string.image);
			mediaTypes.add(mediaType);
			mediaType = new MediaType();
			mediaType.ressourceId = R.drawable.camera_icon;
			mediaType.name = getActivity().getString(R.string.camera);
			mediaTypes.add(mediaType);
			
		}
		
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
