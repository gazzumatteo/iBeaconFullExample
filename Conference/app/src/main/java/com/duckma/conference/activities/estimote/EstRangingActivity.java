package com.duckma.conference.activities.estimote;

import android.os.Bundle;
import android.widget.ListView;

import com.duckma.conference.R;
import com.duckma.conference.activities.abstracts.EstBeaconActivity;
import com.duckma.conference.adapters.BeaconsAdapter;

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
 * EstRangingActivity:
 * <p/>
 * This class extends the EstBeaconActivity class, so it will have its updateUI() method called
 * when a new list of DuckmaBeacons is found around you.
 * <p/>
 * At this point this activity will simply create a new BeaconsAdapter out of that list and feed it
 * to the ListView.
 * <p/>
 * ************************************************************************************************
 */

public class EstRangingActivity extends EstBeaconActivity {

    // the list view where we will display the found beacons list
    private ListView mListView;
    // the adapter for the list view
    private BeaconsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranging_layout);
        // find the view
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void updateUI() {
        // when the beacons list is filled, instantiate a new adapter feeding it the list and give
        // it to the list view
        mAdapter = new BeaconsAdapter(EstRangingActivity.this, mBeacons);
        mListView.setAdapter(mAdapter);
    }

}
