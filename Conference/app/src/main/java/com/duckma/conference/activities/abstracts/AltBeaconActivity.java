package com.duckma.conference.activities.abstracts;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;

import com.duckma.conference.ConferenceApplication;
import com.duckma.conference.R;
import com.duckma.conference.listeners.EmptyListener;
import com.duckma.conference.utils.BeaconsUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
 * AltBeaconActivity:
 * <p/>
 * This class extends the basic Activity class as well as implementing the Altbeacon interfaces for:
 * <p/>
 * 1. iBeacon service connection listener (BeaconConsumer)
 * 2. iBeacon Ranging updates (RangeNotifier)
 * <p/>
 * ************************************************************************************************
 */

public abstract class AltBeaconActivity extends Activity implements BeaconConsumer, RangeNotifier {

    // declare an altbeacon region
    protected Region mAltRegion;
    // declare and instantiate an (empty) list of DuckmaBeacons
    protected ArrayList<BeaconsUtils.DuckmaBeacons> mBeacons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // here we can instantiate a new altbeacon region with uuid matching that of our demo beacons
        mAltRegion = new Region(
                "Conference Altbeacon Region",
                Identifier.parse(getResources().getString(R.string.beacons_uuid)),
                null,
                null
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // when activity is started make sure to clear the beacons list
        mBeacons.clear();
        // then bind the global manager to this class which is a BeaconConsumer implementation
        ConferenceApplication.mAltBeaconManager.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // when activity stops
        try {
            // set range notifier to an empty one
            ConferenceApplication.mAltBeaconManager.setRangeNotifier(new EmptyListener());
            // stop ranging in the previously defined region
            ConferenceApplication.mAltBeaconManager.stopRangingBeaconsInRegion(mAltRegion);
            // unbind this consumer from the beacon service
            ConferenceApplication.mAltBeaconManager.unbind(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            // when the service connects set this as range notifier
            ConferenceApplication.mAltBeaconManager.setRangeNotifier(this);
            // and then start ranging on the previously defined region
            ConferenceApplication.mAltBeaconManager.startRangingBeaconsInRegion(mAltRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        // when the list of beacons seen in region changes
        // clear the old beacon list
        mBeacons.clear();
        // iterate each currently seen beacon
        for (Beacon b : beacons) {
            // check if it matches a demo DuckmaBeacon
            BeaconsUtils.DuckmaBeacons beaconNeedle;
            if ((beaconNeedle = BeaconsUtils.DuckmaBeacons.getDuckmaBeacon(b))
                    != BeaconsUtils.DuckmaBeacons.None)
                // if so then add it to the new beacons list
                mBeacons.add(beaconNeedle);
        }
        // when done sort the list by distance
        Collections.sort(mBeacons, new BeaconsUtils.DistanceComparator());
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

    // extender class must implement this abstract method which will be invoked
    // when a new list of DuckmaBeacons is found
    protected abstract void updateUI();

}
