package com.aloknath.mobiquity;

import android.test.ActivityInstrumentationTestCase2;

import com.aloknath.mobiquity.Activities.MapDisplayActivity;

/**
 * Created by ALOKNATH on 3/24/2015.
 */
public class MapDisplayActivityTest extends ActivityInstrumentationTestCase2<MapDisplayActivity> {

    private MapDisplayActivity mapDisplayActivity;

    public MapDisplayActivityTest(Class<MapDisplayActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapDisplayActivity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull(" imageDisplayActivity is null", mapDisplayActivity);
    }
}
