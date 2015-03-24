package com.aloknath.mobiquity;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import com.aloknath.mobiquity.Activities.MainActivity;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by ALOKNATH on 3/24/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private  MainActivity mainActivity;
    private ListView listViewTest;
    private Button log;
    private Button upload;
    private Button listImages;
    private Intent mLaunchIntent;

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), MainActivity.class);
        startActivity(null, mLaunchIntent, null);

        mainActivity = getActivity();
        listViewTest = (ListView) mainActivity.findViewById(android.R.id.list);
        log = (Button) mainActivity.findViewById(R.id.dropbox_login);
        upload = (Button) mainActivity.findViewById(R.id.upload_image);
        listImages = (Button) mainActivity.findViewById(R.id.list_images);
    }


    @MediumTest
    public void testNextActivityWasLaunchedWithIntent() {
        startActivity(null,mLaunchIntent, null);
        final Button launchNextButton =
                (Button) getActivity()
                        .findViewById(R.id.upload_image);
        launchNextButton.performClick();

    }

    @MediumTest
    public void testLogButton_layout() {
        final View decorView = mainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, log);

        final ViewGroup.LayoutParams layoutParams =
                log.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @MediumTest
    public void testUploadButton_layout() {
        final View decorView = mainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, upload);

        final ViewGroup.LayoutParams layoutParams =
                upload.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @MediumTest
    public void testListImagesButton_layout() {
        final View decorView = mainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, listImages);

        final ViewGroup.LayoutParams layoutParams =
                listImages.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testPreconditions() {
        assertNotNull(" MainActivity is null", mainActivity);
        assertNotNull(" listViewTest is null", listViewTest);
    }
}
