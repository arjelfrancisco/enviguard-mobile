package capstone.com.cybertracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.PatrolObservationImage;

/**
 * Created by Arjel on 7/26/2016.
 */

public class ObservationGalleryGridViewAdapter extends ArrayAdapter<PatrolObservationImage> {

    private int layoutResourceId;
    private Context context;
    private List<PatrolObservationImage> data;

    private int width = 500;
    private int height = 500;

    private ImageView imageView;

    @Override
    public int getCount() {
        return data.size();
    }

    public ObservationGalleryGridViewAdapter(Context context, int layoutResourceId, List<PatrolObservationImage> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            imageView = (ImageView) row.findViewById(R.id.imageObsGallery);
            holder.image = imageView;
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
//        decodeImageFile("/storage/emulated/0/Android/data/capstone.com.cybertracker/files/Pictures/JPEG_20160727_220644_-1684901587.jpg");
        holder.image.setImageBitmap(decodeImageFile(data.get(position).getImageLocation()));
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    private Bitmap decodeImageFile(String imagePath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/imageView.getLayoutParams().width, photoH/imageView.getLayoutParams().height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        return rotatedBitmap;
    }

}
