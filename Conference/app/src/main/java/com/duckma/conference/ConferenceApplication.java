package com.duckma.conference;

import android.app.Application;
import android.content.Intent;

import com.duckma.conference.services.MonitoringService;
import com.estimote.sdk.BeaconManager;

import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

/**
 * ************************************************************************************************
 * <p/>
 * ******       ***	    ***	    ******	***   ***   *****   *****     *******
 * ********	    ***	    ***	   *******	***  ***   *** *** *** ***   *********
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
 * ConferenceApplication:
 * <p/>
 * Here we extend the basic Application object to execute the fallowing tasks:
 * <p/>
 * 1. declare and hold reference to the two BeaconManagers we will be using through the demo
 * 2. instantiate the BackgroundPowerSaver to help balance battery usage
 * 3. initialize the BeaconManagers
 * 4. start the MonitoringService
 * <p/>
 * ************************************************************************************************
 */

public class ConferenceApplication extends Application {

    // The global BeaconManager from Estimote SDK
    public static BeaconManager mEstimoteBeaconManager;
    // The global BeaconManager from Altbeacon SDK
    public static org.altbeacon.beacon.BeaconManager mAltBeaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // Show debugging logs
        com.estimote.sdk.utils.L.enableDebugLogging(true);
        // Setup the Altbeacon Beacon Manager
        mAltBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        // Add BeaconParser with correct BeaconLayout
        mAltBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        // Just instantiate a new BackgroundPowerSaver to make it work
        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);
        // Setup the Estimote Beacon Manager
        mEstimoteBeaconManager = new BeaconManager(this);
        // Start the MonitoringService
        startService(new Intent(ConferenceApplication.this, MonitoringService.class));

    }

}
