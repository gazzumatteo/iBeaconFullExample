package com.duckma.conference.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duckma.conference.R;
import com.duckma.conference.activities.altbeacon.AltRangingActivity;
import com.duckma.conference.activities.altbeacon.AltRoomActivity;
import com.duckma.conference.activities.altbeacon.AltWayfindingActivity;
import com.duckma.conference.activities.estimote.EstRangingActivity;
import com.duckma.conference.activities.estimote.EstRoomActivity;
import com.duckma.conference.activities.estimote.EstWayfindingActivity;

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
 * MainActivity:
 * <p/>
 * This activity simply shows the list of demos available and gives the choice to run it with
 * Estimote SDK or Altbeacon SDK.
 * <p/>
 * ************************************************************************************************
 */

public class MainActivity extends Activity {

    Button btnAltRanging, btnEstRanging, btnAltIndoor, btnEstIndoor, btnAltWayfinding, btnEstWayfinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        notifyBluetoothStatus();

        btnAltRanging = (Button) findViewById(R.id.btnAltRanging);
        btnAltIndoor = (Button) findViewById(R.id.btnAltIndoor);
        btnAltWayfinding = (Button) findViewById(R.id.btnAltWayfinding);

        btnEstRanging = (Button) findViewById(R.id.btnEstRanging);
        btnEstIndoor = (Button) findViewById(R.id.btnEstIndoor);
        btnEstWayfinding = (Button) findViewById(R.id.btnEstWayfinding);

        btnAltRanging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRangingActivityIntent = new Intent(MainActivity.this, AltRangingActivity.class);
                startActivity(openRangingActivityIntent);
            }
        });

        btnAltIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRangingActivityIntent = new Intent(MainActivity.this, AltRoomActivity.class);
                startActivity(openRangingActivityIntent);
            }
        });

        btnAltWayfinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWayfindingActivityIntent = new Intent(MainActivity.this, AltWayfindingActivity.class);
                startActivity(openWayfindingActivityIntent);
            }
        });

        btnEstRanging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRangingActivityIntent = new Intent(MainActivity.this, EstRangingActivity.class);
                startActivity(openRangingActivityIntent);
            }
        });

        btnEstIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRoomActivityIntent = new Intent(MainActivity.this, EstRoomActivity.class);
                startActivity(openRoomActivityIntent);
            }
        });

        btnEstWayfinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWayfindingActivityIntent = new Intent(MainActivity.this, EstWayfindingActivity.class);
                startActivity(openWayfindingActivityIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void notifyBluetoothStatus() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        PackageManager pm = MainActivity.this.getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || mBluetoothAdapter == null) {
            builder.setTitle(MainActivity.this.getResources().getString(R.string.title_bluetooth_not_supported));
            builder.setMessage(MainActivity.this.getResources().getString(R.string.message_bluetooth_not_supported));
            builder.setPositiveButton(MainActivity.this.getResources().getString(R.string.button_bluetooth_not_supported), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                builder.setTitle(MainActivity.this.getResources().getString(R.string.title_bluetooth_off));
                builder.setMessage(MainActivity.this.getResources().getString(R.string.message_bluetooth_off));
                builder.setPositiveButton(MainActivity.this.getResources().getString(R.string.button_bluetooth_off), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        MainActivity.this.startActivity(intentOpenBluetoothSettings);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(MainActivity.this.getResources().getString(R.string.button_negative_bluetooth_off), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

}
