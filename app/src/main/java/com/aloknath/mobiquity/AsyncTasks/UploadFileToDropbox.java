package com.aloknath.mobiquity.AsyncTasks;

/**
 * Created by ALOKNATH on 3/23/2015.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;

public class UploadFileToDropbox extends AsyncTask<Void, Void, Boolean> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private File file;
    private DropboxAPI.UploadRequest mRequest;
    private ProgressDialog mDialog;
    private long mFileLen;

    public UploadFileToDropbox(Context context, DropboxAPI<?> dropbox,
                               String path, File file) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.file = file;

        mFileLen = file.length();
        mDialog = new ProgressDialog(context);
        mDialog.setMax(100);
        mDialog.setMessage("Uploading " + file.getName());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mRequest.abort();
            }
        });
        mDialog.show();
    }


    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            String pathFinal;
            pathFinal = path + file.getName();
            Log.i("The Final Path123: ", pathFinal);
            FileInputStream fileInputStream = new FileInputStream(file);
            mRequest = dropbox.putFileOverwriteRequest(pathFinal, fileInputStream, file.length(), new ProgressListener() {
                @Override
                public long progressInterval() {
                    return 100;
                }

                @Override
                public void onProgress(long bytes, long total) {

                    int percent = (int) (100.0 * (double) bytes / total + 0.5);
                    mDialog.setProgress(percent);

                }
            });

            if (mRequest != null) {
                mRequest.upload();
                return true;
            }else{
                Log.i("The Final Path request is false ", pathFinal + ": " + file.length());
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
        if (result) {
            Toast.makeText(context, "File Uploaded Sucesfully!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to upload file", Toast.LENGTH_LONG)
                    .show();
        }
    }

}
