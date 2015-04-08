package com.duckma.conference.listeners;

import com.estimote.sdk.BeaconManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************************************
 * <p/>
 * ******		***	    ***	 	******	***   ***   *****   *****     *******
 * ********	    ***	    ***	   *******	***  ***   *** *** *** ***   *********
 * ***	  ***	***	    ***	  ***		*** ***    ***   ***   ***  ***     ***
 * ***	   ***	***	    ***	 *** 		******     ***   ***   ***  ***     ***
 * ***	   ***	***	    ***	 *** 		*****      ***   ***   ***  ***********     ****         *
 * ***	   ***	***	    ***	 ***		******     ***   ***   ***  ***********    *      *  **  *
 * ***	  ***	***	    ***	  ***		*** ***    ***   ***   ***  ***     ***     ***   * *    *
 * ********	     ***   ***	   *******	***  ***   ***   ***   ***  ***     ***        *  *      *
 * ******		  *******	 	******	***   ***  ***   ***   ***  ***     ***    ****   *       **
 * <p/>
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * <p/>
 * EmptyListener:
 * <p/>
 * This class serves as an empty Ranging Listener to override any previous registered listener.
 * <p/>
 * ************************************************************************************************
 */

public class EmptyListener implements RangeNotifier, BeaconManager.RangingListener {
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        // Just do nothing
    }

    @Override
    public void onBeaconsDiscovered(com.estimote.sdk.Region region, List<com.estimote.sdk.Beacon> beacons) {
        // Just do nothing
    }
}
