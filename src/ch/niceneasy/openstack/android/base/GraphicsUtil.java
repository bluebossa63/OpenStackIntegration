package ch.niceneasy.openstack.android.base;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;

public class GraphicsUtil {

	public static int getCameraPhotoOrientation(Context context, Uri imageUri,
			String imagePath) throws IOException {
		int rotate = 0;
		context.getContentResolver().notifyChange(imageUri, null);
		File imageFile = new File(imagePath);

		ExifInterface exif = new android.media.ExifInterface(
				imageFile.getAbsolutePath());
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		}
		return rotate;
	}

	public static int getOrientation(Context context, Uri imageUri) throws IOException {
		String[] filePathColumn = { MediaColumns.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return getCameraPhotoOrientation(context, imageUri, filePath);
	}

	public static String getOriginalFilePath(Context context, Uri imageUri) {
		if (imageUri.getScheme().equals("file")) {
			return imageUri.getPath();
		}
		String[] filePathColumn = { MediaColumns.DATA, MediaColumns.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		return cursor.getString(columnIndex);
	}

}
