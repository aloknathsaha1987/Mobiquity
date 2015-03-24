package com.aloknath.mobiquity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

import com.aloknath.mobiquity.Activities.ImageDisplayActivity;

/**
 * Created by ALOKNATH on 3/24/2015.
 */
public class ImageDisplayActivityTest extends ActivityInstrumentationTestCase2<ImageDisplayActivity> {

    private ImageDisplayActivity imageDisplayActivity;
    private ImageView imageViewTest;

    public ImageDisplayActivityTest(Class<ImageDisplayActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        imageDisplayActivity = getActivity();
        imageViewTest = (ImageView) imageDisplayActivity.findViewById(R.id.imageView2);
    }

    public void testPreconditions() {
        assertNotNull(" ImageDisplayActivity is null", imageDisplayActivity);
        assertNotNull(" imageViewTest is null", imageViewTest);
    }

}
