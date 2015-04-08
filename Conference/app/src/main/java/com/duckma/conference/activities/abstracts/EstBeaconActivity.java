package com.duckma.conference.activities.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;

import com.duckma.conference.ConferenceApplication;
import com.duckma.conference.R;
import com.duckma.conference.listeners.EmptyListener;
import com.duckma.conference.utils.BeaconsUtils;
import com.estimote.sdk.BeaconManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************************************************************************
 * <p/>
 * ******       ***	    ***	    ******	***   ***   *****   *****     *******
 * ********     ***	    ***	   *******	***  ***   *** *** *** ***   *********
 * ***	  ***   ***	    ***	  ***       *** ***    ***   ***   ***  ***     ***
 * ***     ***  ***	    ***	 ***        ******     ***   ***   ***  ***     ***
 * ***     ***  ***	    ***	 *** 		*****      ***   ***   ***  ***********     ****         *
 * ***     ***  ***	    ***	 ***		******     ***   ***   ***  ***********    *      *  **  *
 * ***	  ***	***	    ***	  ***		*** ***    ***   ***   ***  ***     ***     ***   * *    *
 * ********	     ***   ***	   *******	***  ***   ***   ***   ***  ***     ***        *  *      *
 * ******         *******       ******	***   ***  ***   ***   ***  ***     ***    ****   *       **
 * <p/>
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * <p/>
 * EstBeaconActivity:
 * <p/>
 * This class extends the basic Activity class as well as implementing the Estimote interfaces for:
 * <p/>
 * 1. iBeacon service connection listener (BeaconManager.ServiceReadyCallback)
 * 2. iBeacon Ranging updates (BeaconManager.RangingListener)
 * <p/>
 * ************************************************************************************************
 */

public abstract class EstBeaconActivity extends Activity implements BeaconManager.ServiceReadyCallback, BeaconManager.RangingListener {

    // declare an estimote region
    protected com.estimote.sdk.Region mEstRegion;
    // declare and instantiate an (empty) list of DuckmaBeacons
    protected ArrayList<BeaconsUtils.DuckmaBeacons> mBeacons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // here we can instantiate a new estimote region with uuid matching that of our demo beacons
        mEstRegion = new com.estimote.sdk.Region(
                "Conference Estimote Region",
                getResources().getString(R.string.beacons_uuid),
                null,
                null
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        // when activity is started make sure to clear the beacons list
        mBeacons.clear();
        // then bind the global manager to this class which is a ServiceReadyCallback implementation
        ConferenceApplication.mEstimoteBeaconManager.connect(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // when activity stops
        try {
            // set range notifier to an empty one
            ConferenceApplication.mEstimoteBeaconManager.stopRanging(mEstRegion);
            // stop ranging in the previously defined region
            ConferenceApplication.mEstimoteBeaconManager.setRangingListener(new EmptyListener());
            // unbind this ready callback from the beacon service
            ConferenceApplication.mEstimoteBeaconManager.disconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceReady() {
        try {
            // when the service is ready set this as ranging listener
            ConferenceApplication.mEstimoteBeaconManager.setRangingListener(this);
            // and then start ranging on the previously defined region
            ConferenceApplication.mEstimoteBeaconManager.startRanging(mEstRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconsDiscovered(com.estimote.sdk.Region region, List<com.estimote.sdk.Beacon> beacons) {
        // when the list of beacons seen in region changes
        // clear the old beacon list
        mBeacons.clear();
        // iterate each currently seen beacon
        for (com.estimote.sdk.Beacon b : beacons) {
            // check if it matches a demo DuckmaBeacon
            BeaconsUtils.DuckmaBeacons beaconNeedle;
            if ((beaconNeedle = BeaconsUtils.DuckmaBeacons.getDuckmaBeacon(b))
                    != BeaconsUtils.DuckmaBeacons.None)
                // if so then add it to the new beacons list
                mBeacons.add(beaconNeedle);
        }
        // NB - you don't have to sort the beacons at this point, because estimote sdk already gives
        //      a sorted list of beacons
        // ...
        // finally invoke the UI update
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                }
        );
    }

    protected abstract void updateUI();

}
