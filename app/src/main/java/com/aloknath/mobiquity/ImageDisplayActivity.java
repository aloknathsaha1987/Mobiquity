package com.aloknath.mobiquity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by ALOKNATH on 3/23/2015.
 */
public class ImageDisplayActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images_display);

        ImageView imageView = (ImageView)findViewById(R.id.imageView2);

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        imageView.setImageBitmap(bmp);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
