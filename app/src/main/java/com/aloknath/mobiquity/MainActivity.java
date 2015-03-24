package com.aloknath.mobiquity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.aloknath.mobiquity.Adapters.ImageAdapter;
import com.aloknath.mobiquity.AsyncTasks.UploadFileToDropbox;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class MainActivity extends ListActivity implements OnClickListener {
    private DropboxAPI<AndroidAuthSession> dropbox;
    public final static String FILE_DIR = "/Images_Mobiquity/";
    private final static String DROPBOX_NAME = "Alok_Dropbox";
    private final static String ACCESS_KEY = "f2ngv7yav8k1vru";
    private final static String ACCESS_SECRET = "wf71qfmldgjy76k";
    private boolean isLoggedIn;
    private Button logIn;
    private Button uploadFile;
    private Button listFiles;
    private File file;
    private static final int TAKE_PHOTO = 1001;
    private ArrayList<File> images_files;
    private ImageAdapter adapter;
    private ProgressDialog mDialog;
    private List<Bitmap> imagesBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);

        logIn = (Button) findViewById(R.id.dropbox_login);
        logIn.setOnClickListener(this);
        uploadFile = (Button) findViewById(R.id.upload_image);
        uploadFile.setOnClickListener(this);
        listFiles = (Button) findViewById(R.id.list_images);
        listFiles.setOnClickListener(this);

        loggedIn(false);
        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER, token);
        } else {
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER);
        }
        dropbox = new DropboxAPI<>(session);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropbox.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();
                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
        uploadFile.setEnabled(isLogged);
        listFiles.setEnabled(isLogged);
        logIn.setText(isLogged ? "Log out" : "Log in");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dropbox_login:
                if (isLoggedIn) {
                    dropbox.getSession().unlink();
                    loggedIn(false);
                } else {
                    dropbox.getSession().startAuthentication(MainActivity.this);
                }

                break;
            case R.id.list_images:
                ListDropboxFiles list = new ListDropboxFiles(dropbox, FILE_DIR);
                list.execute();
                break;
            case R.id.upload_image:
                createDir();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getPath(),new Date().getTime()+".jpg");
                Log.i("The file path:" , getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, TAKE_PHOTO);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {
                if (isOnline(MainActivity.this)) {

                    AndroidAuthSession session = dropbox.getSession();

                    if (session.authenticationSuccessful()) {
                        try {
                            session.finishAuthentication();

                            UploadFileToDropbox upload = new UploadFileToDropbox(this, dropbox,
                                    FILE_DIR, file);
                            upload.execute();

                        } catch (IllegalStateException e) {
                            Log.i("Couldn't authenticate with Dropbox:",
                                     e.getLocalizedMessage());
                        }
                    }


                } else {
                    Toast.makeText(MainActivity.this, "Network is unavailable !!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void createDir() {
        File dir = new File(getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String getPath() {
        String path;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else if ((new File("/mnt/emmc")).exists()) {
            path = "/mnt/emmc";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path ;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//
//        if(imagesBitmap.size()>0) {
//            Bitmap bitmap = imagesBitmap.get(position);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//            byte[] b = baos.toByteArray();
//
//            Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
//            intent.putExtra("picture", b);
//            startActivity(intent);
//        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = null;
        File file1 = images_files.get(position);
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file1), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
        intent.putExtra("picture", b);
        startActivity(intent);


    }



    private class ListDropboxFiles extends AsyncTask<Void, Void, ArrayList<File>> {

        private DropboxAPI<?> dropbox;
        private String path;


        public ListDropboxFiles(DropboxAPI<?> dropbox, String path) {
            this.dropbox = dropbox;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Fetching Images From DropBox");
            mDialog.setIndeterminate(true);
            mDialog.show();

        }

        @Override
        protected ArrayList<File> doInBackground(Void... params) {
            ArrayList<String> files = new ArrayList<>();
            ArrayList<File> image_files = new ArrayList<>();
            try {
                DropboxAPI.Entry directory = dropbox.metadata(path, 1000, null, true, null);

                for (DropboxAPI.Entry entry : directory.contents) {
                    files.add(entry.fileName());

                    File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() , entry.fileName());

                    FileOutputStream outputStream = new FileOutputStream(file);
                    dropbox.getFile("/Images_Mobiquity/" + entry.fileName(), null, outputStream, null);
                    image_files.add(file);
                }

            } catch (DropboxException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            return image_files;
        }

        @Override
        protected void onPostExecute(ArrayList<File> result) {

            images_files = result;
//            if(images_files.size()>0) {
//                for (File file1 : images_files) {
//
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    try {
//                        imagesBitmap.add(BitmapFactory.decodeStream(new FileInputStream(file1), null, options));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            mDialog.hide();
            refreshDisplay();

        }

    }

    private void refreshDisplay() {

        if (images_files.size() > 0) {

            adapter = new ImageAdapter(this, R.layout.list_image_display, images_files);
            setListAdapter(adapter);
        }

    }
}