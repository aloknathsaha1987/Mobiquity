package com.aloknath.mobiquity.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.aloknath.mobiquity.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ALOKNATH on 3/23/2015.
 */
public class ImageAdapter extends ArrayAdapter<File> {

    private Context context;
    private List<File> files;
    private  Bitmap bitmap = null;
    private HashMap<Integer, Bitmap> imageHashMap = new HashMap<>();

    public ImageAdapter(Context context, int resource, List<File> file) {
        super(context, resource, file);
        this.context = context;
        this.files = file;
    }

    static class ViewHolder {
        private ImageView imageView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        File file = files.get(position);


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_image_display, null);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
            ViewHolder holderItem = (ViewHolder) convertView.getTag();

            if(imageHashMap.get(position) == null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                holderItem.imageView.setImageBitmap(bitmap);
                imageHashMap.put(position, bitmap);
            }
            else{
                holderItem.imageView.setImageBitmap(imageHashMap.get(position));
            }

        }

        return convertView;
    }

}
