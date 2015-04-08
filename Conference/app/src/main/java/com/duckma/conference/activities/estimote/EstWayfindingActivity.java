package com.duckma.conference.activities.estimote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duckma.conference.R;
import com.duckma.conference.activities.abstracts.EstBeaconActivity;
import com.duckma.conference.utils.BeaconsUtils;

import java.util.ArrayList;

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
 * EstWayfindingActivity:
 * <p/>
 * This class extends the EstBeaconActivity class, so it will have its updateUI() method called
 * when a new list of DuckmaBeacons is found around you.
 * <p/>
 * At this point this activity will check the progress of user along the designed path and, if the
 * user stands close enough to the next way-point, it will show the next point and display an
 * informative dialog.
 * <p/>
 * ************************************************************************************************
 */

public class EstWayfindingActivity extends EstBeaconActivity {

    // the various image views representing the way-points
    ImageView imgYellowPoint, imgRedPoint, imgBluePoint, imgGreenPoint;
    // a list of points which will represent the walked path
    ArrayList<BeaconsUtils.Points> path = new ArrayList<>();
    // a single point representing the nex step in the designed path (starting from the yellow star)
    BeaconsUtils.Points nextPoint = BeaconsUtils.Points.Yellow;
    // some fancy animation for the points
    Animation pulse, fadePulse;
    // the container relative layout used to access the views by their tags
    RelativeLayout rlContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wayfinding_layout);
        // retreive the views references
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        imgYellowPoint = (ImageView) findViewById(R.id.imgYellowPoint);
        imgYellowPoint.setTag(BeaconsUtils.Points.Yellow.name());
        imgRedPoint = (ImageView) findViewById(R.id.imgRedPoint);
        imgRedPoint.setTag(BeaconsUtils.Points.Red.name());
        imgBluePoint = (ImageView) findViewById(R.id.imgBluePoint);
        imgBluePoint.setTag(BeaconsUtils.Points.Blue.name());
        imgGreenPoint = (ImageView) findViewById(R.id.imgGreenPoint);
        imgGreenPoint.setTag(BeaconsUtils.Points.Green.name());
        // initialize the animations
        // pulse animation
        pulse = new ScaleAnimation(
                1f, 1.3f, // Start and end values for the X axis scaling
                1f, 1.3f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setDuration(1000);
        // fade animation
        fadePulse = new AlphaAnimation(1.0f, 0.8f);
        fadePulse.setRepeatCount(Animation.INFINITE);
        fadePulse.setRepeatMode(Animation.REVERSE);
        fadePulse.setDuration(1000);
        // animation set pulse + fade
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(pulse);
        set.addAnimation(fadePulse);
        // start animation on first way-point (the yellow star)
        imgYellowPoint.startAnimation(set);

    }

    @Override
    protected void updateUI() {
        // when a fresh list of DuckmaBeacons is found
        // iterate all the points seen in Immediate range
        for (BeaconsUtils.Points point : BeaconsUtils.Points.getPointsForRange(BeaconsUtils.Ranges.Immediate, mBeacons)) {
            // if a point is found matching the prescribed next step point
            if (point == nextPoint) {
                // add this step to the walked path
                path.add(point);
                // depending on the path size, we can decide which point should be the next step
                switch (path.size()) {
                    case 1:
                        // when first point is walked (the yellow star), set red point as next
                        imgYellowPoint.setBackgroundResource(R.drawable.location_circle_yellow);
                        nextPoint = BeaconsUtils.Points.Red;
                        break;
                    case 2:
                        // when second point is walked (the red question mark), set blue point as next
                        imgRedPoint.setBackgroundResource(R.drawable.location_circle_red);
                        nextPoint = BeaconsUtils.Points.Blue;
                        break;
                    case 3:
                        // when third point is walked (the blue info point), set the green point as next
                        imgBluePoint.setBackgroundResource(R.drawable.location_circle_blue);
                        nextPoint = BeaconsUtils.Points.Green;
                        break;
                    default:
                        // when last point is walked (the green check mark), stop the walk by
                        // setting next step to None (no beacon will match that step)
                        imgGreenPoint.setBackgroundResource(R.drawable.location_circle_green);
                        nextPoint = BeaconsUtils.Points.None;
                }
                // when the next step is decided, show the Dialog with info about the current point
                new AlertDialog.Builder(this)
                        .setTitle(point.getTitleRes())
                        .setMessage(point.getDescriptionRes())
                        .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // when user clicks continue... show the next point in the path
                                View nextPointView = rlContainer.findViewWithTag(nextPoint.name());
                                if (nextPointView != null) {
                                    nextPointView.setVisibility(View.VISIBLE);
                                    nextPointView.startAnimation(pulse);
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        }
    }

}
