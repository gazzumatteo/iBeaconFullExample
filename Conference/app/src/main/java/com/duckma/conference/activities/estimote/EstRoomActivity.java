package com.duckma.conference.activities.estimote;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duckma.conference.R;
import com.duckma.conference.activities.abstracts.EstBeaconActivity;
import com.duckma.conference.utils.BeaconsUtils;

import java.util.ArrayList;

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
 * EstRoomActivity:
 * <p/>
 * This class extends the EstBeaconActivity class, so it will have its updateUI() method called
 * when a new list of DuckmaBeacons is found around you.
 * <p/>
 * At this point this activity will evaluate the position of the user among the defined sections in
 * which the room is divided by the beacons.
 * <p/>
 * After evaluating the user's position in the sections, this activity will color the relative
 * squares with a brighter color the most confidence on the user being in it.
 * <p/>
 * ************************************************************************************************
 */

public class EstRoomActivity extends EstBeaconActivity {

    // a list of area colors to represent the likely areas
    private int[] colors = {
            R.color.white,
            R.color.low_prob,
            R.color.low_prob_2,
            R.color.some_prob,
            R.color.good_prob,
            R.color.most_prob
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_layout);
    }

    @Override
    protected void updateUI() {
        // get a list of sections ordered in descending fitness order
        ArrayList<BeaconsUtils.Sections> mySections = BeaconsUtils.Sections.evaluatePosition(mBeacons);
        ImageView imageView = BeaconsUtils.createImageView(this);
        // for all available sections, colour its beackground according to its likehood to be the current position
        for (BeaconsUtils.Sections section : mySections) {
            RelativeLayout rl = (RelativeLayout) findViewById(section.getLayoutId());

            if (colors[mySections.indexOf(section)] == R.color.most_prob) {
                rl.removeAllViews();
                if (rl.getChildCount() == 0) {
                    rl.addView(imageView);
                }
            } else {
                rl.removeAllViews();
            }
            rl.setBackgroundColor(getResources().getColor(colors[mySections.indexOf(section)]));
        }
    }
}
