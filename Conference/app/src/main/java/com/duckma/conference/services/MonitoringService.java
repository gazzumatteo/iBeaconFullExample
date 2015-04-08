package com.duckma.conference.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.duckma.conference.R;
import com.duckma.conference.activities.MainActivity;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Date;
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
 * MonitoringService:
 * <p/>
 * This class extends the Service class as well as implementing the interfaces for Estimote SDK
 * beacon service connection (BeaconManager.ServiceReadyCallback) and monitoring listening
 * (BeaconManager.MonitoringListener).
 * <p/>
 * When the service is connected with the service's own BeaconManager, it starts monitoring.
 * When entering a region the service sends a welcome notification to the user.
 * When exiting a region the service sends a goodbye notification to the user.
 * <p/>
 * ************************************************************************************************
 */

public class MonitoringService extends Service implements BeaconManager.ServiceReadyCallback, BeaconManager.MonitoringListener {

    protected com.estimote.sdk.Region mEstRegion;
    public static BeaconManager mEstimoteBeaconManager;
    private long lastInNotification = 0;
    private long lastOutNotification = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mEstimoteBeaconManager = new BeaconManager(this);
        mEstimoteBeaconManager.setMonitoringListener(this);
        mEstimoteBeaconManager.connect(this);

        mEstRegion = new com.estimote.sdk.Region(
                getString(R.string.region_id),
                getResources().getString(R.string.beacons_uuid),
                null,
                null
        );

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onServiceReady() {
        try {
            mEstimoteBeaconManager.startMonitoring(mEstRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnteredRegion(Region region, List<Beacon> beacons) {
        // can trigger notification if not triggered 15 min before
        if (new Date().getTime() > lastInNotification + 15 * 60 * 1000) {
            sendNotificationWear(MonitoringService.this, getResources().getString(R.string.title_welcome_notification),
                    getResources().getString(R.string.message_welcome_notification));
            lastInNotification = new Date().getTime();
        }
    }

    @Override
    public void onExitedRegion(Region region) {
        // can trigger notification if not triggered 15 min before
        if (new Date().getTime() > lastOutNotification + 15 * 60 * 1000) {
            sendNotificationWear(MonitoringService.this, getResources().getString(R.string.title_goodbye_notification),
                    getResources().getString(R.string.message_goodbye_notification));
            lastOutNotification = new Date().getTime();
        }
    }

    public void sendNotificationWear(Context context, String title, String message) {
        int notificationId = 202;
        // Create a WearableExtender to add functionality for wearables
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender();

        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
//        viewIntent.putExtra(BEACON_EXTRA_KEY, beacon);

        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(viewPendingIntent) // Add a pending intent to open an activity when notification tapped
                        .setSound(alarmSound)
                        .extend(wearableExtender);


        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
