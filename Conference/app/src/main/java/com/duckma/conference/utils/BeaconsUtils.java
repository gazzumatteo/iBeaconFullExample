package com.duckma.conference.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duckma.conference.R;
import com.estimote.sdk.Utils;

import org.altbeacon.beacon.Beacon;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
 * BeaconsUtils:
 * <p/>
 * This class is a container for various enumerations used to handle beacons.
 * <p/>
 * 1. DuckmaBeacons enum: this enum identifies each beacon mapped for this demo. It offers
 * methods to wrap a standard Beacon inside the matching DuckmaBeacon (with
 * corresponding major-minor values) and accessing its methods (getDistance, getRssi...).
 * Note the wrapper enum item object checks the actual class of the wrapped beacon and
 * handles correctly its calls.
 * <p/>
 * 2. Sections enum: this enum identifies the various sections the room is devided by the demo
 * beacons. Each section has a set of 16 rules to check on when trying to spot the user.
 * When evaluating user's position, it iterates each enum item and calculates a fit value
 * for it based on the distances it sees form each beacon in the passed list. Finally it
 * sorts the sections by descending fit values.
 * <p/>
 * 3. Ranges enum: this enum identifies the various ranges at which a beacon can be seen. Here
 * three ranges are defined: entering, proximity and immediate. The enums offers a method to
 * retrieve the correct range from a DuckmaBeacon based on the seen distance.
 * <p/>
 * 4. Points enum: this enum identifies the various points along the way-finding route. Each
 * point refers to a specific DuckmaBeacon and has a dialog title and a dialog message as
 * well as a color which identifies the point. The method getPointsForRange returns the
 * points which can be seen in the specified range among those inside the given list.
 * <p/>
 * <p/>
 * ************************************************************************************************
 */

public class BeaconsUtils {

    // the collection of all DuckMa Beacons in the room along with their major, minor and coordinates
    public static enum DuckmaBeacons {

        None(0, 0, new Location(0, 0)),
        YBlue(50081, 23249, new Location(0, -3)),
        GBlue(49745, 19544, new Location(-1, 0)),
        YLightBlue(56662, 59039, new Location(0, 1)),
        GLightBlue(11677, 58258, new Location(1, 0)),
        YGreen(20171, 41546, new Location(0, 3)),
        GGreen(16411, 10819, new Location(0, -1));

        private int minor, major;
        private Location location;
        public Parcelable wrappedBeacon;

        DuckmaBeacons(int major, int minor, Location location) {
            this.major = major;
            this.minor = minor;
            this.location = location;
        }

        // gets a DuckmaBeacon (element of this enum) wrapping the passed beacon
        public static DuckmaBeacons getDuckmaBeacon(Parcelable beacon) {
            if (beacon instanceof com.estimote.sdk.Beacon) {
                for (DuckmaBeacons duckmaBeacon : values()) {
                    if (duckmaBeacon.major == ((com.estimote.sdk.Beacon) beacon).getMajor() &&
                            duckmaBeacon.minor == ((com.estimote.sdk.Beacon) beacon).getMinor()) {
                        duckmaBeacon.wrappedBeacon = beacon;
                        return duckmaBeacon;
                    }
                }
            } else if (beacon instanceof Beacon) {
                for (DuckmaBeacons duckmaBeacon : values()) {
                    if (duckmaBeacon.major == ((Beacon) beacon).getId2().toInt() &&
                            duckmaBeacon.minor == ((Beacon) beacon).getId3().toInt()) {
                        duckmaBeacon.wrappedBeacon = beacon;
                        return duckmaBeacon;
                    }
                }
            }
            return None;
        }

        public double getDistance() {
            if (wrappedBeacon != null) {
                if (wrappedBeacon instanceof Beacon) {
                    return ((Beacon) wrappedBeacon).getDistance();
                } else {
                    return Utils.computeAccuracy((com.estimote.sdk.Beacon) wrappedBeacon);
                }
            }
            return -1;
        }

        public int getRssi() {
            if (wrappedBeacon != null) {
                if (wrappedBeacon instanceof Beacon) {
                    return ((Beacon) wrappedBeacon).getRssi();
                } else {
                    return ((com.estimote.sdk.Beacon) wrappedBeacon).getRssi();
                }
            }
            return -1;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public Location getLocation() {
            return this.location;
        }

    }

    // a collections of the sections in which the room is divided by the positioned beacons along with a list of rules
    // concerning the relative positioning of the various beacons i.e (DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue)
    // means GBlue beacon should be seen closer to the user device than the GLightBlue one.
    // NB - each section must have the same amount of rules as the others
    public static enum Sections {

        section0(
                R.id.sector0,
                // each of this rules represents a statement like THIS is closer than THAT
                // for example the rule below states: GBlue beacon is closer than GLightBlue beacon
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YGreen)
        ),
        section1(
                R.id.sector1,
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YGreen)
        ),
        section2(
                R.id.sector2,
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen)
        ),
        section3(
                R.id.sector3,
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YGreen),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YGreen)
        ),
        section4(
                R.id.sector4,
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GLightBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GBlue, DuckmaBeacons.YBlue)
        ),
        section5(
                R.id.sector5,
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YGreen, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.GGreen),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.YLightBlue, DuckmaBeacons.YBlue),
                //
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GGreen, DuckmaBeacons.YBlue),
                new AbstractMap.SimpleEntry<>(DuckmaBeacons.GLightBlue, DuckmaBeacons.YBlue)
        );

        private AbstractMap.SimpleEntry<DuckmaBeacons, DuckmaBeacons>[] rules;
        private int layoutId;
        private double fit;

        Sections(int layoutId, AbstractMap.SimpleEntry<DuckmaBeacons, DuckmaBeacons>... rules) {
            this.layoutId = layoutId;
            this.rules = rules;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public AbstractMap.SimpleEntry<DuckmaBeacons, DuckmaBeacons>[] getRules() {
            return rules;
        }

        // this method evaluates the position, returning a list of sections ordered by closeness likelihood
        // for each section it evaluates each section's rule and increases its fitness for each confirmed rule
        public static ArrayList<Sections> evaluatePosition(ArrayList<DuckmaBeacons> view) {

            ArrayList<Sections> fitSections = new ArrayList<>();
            // for each section
            for (Sections section : values()) {

                double valids = 0;
                // for each rule of this section
                for (AbstractMap.SimpleEntry<DuckmaBeacons, DuckmaBeacons> rule : section.getRules()) {
                    // check if rule is verified
                    int closeNeedle = view.indexOf(rule.getKey());
                    int farNeedle = view.indexOf(rule.getValue());

                    if (closeNeedle > -1 && farNeedle > -1 &&
                            (view.get(closeNeedle).getDistance() > view.get(farNeedle).getDistance())) {
                        // if is verified increment section fitness
                        valids++;
                    }

                }

                section.fit = valids;
                fitSections.add(section);

            }
            // sort sections by decreasing fitness
            Collections.sort(fitSections, new FitnessComparator());
            return fitSections;

        }

    }

    public static enum Ranges {

        None(-1),
        Entering(50),
        Proximity(10),
        Immediate(1.5);

        double meters;

        Ranges(double meters) {
            this.meters = meters;
        }

        public double getRadius() {
            return meters;
        }

        public static Ranges getRange(DuckmaBeacons beacon) {

            Ranges closestRange = None;

            for (Ranges range : values()) {
                if (closestRange == None ||
                        beacon.getDistance() < range.getRadius()) {
                    closestRange = range;
                }
            }

            return closestRange;

        }

    }

    public static enum Points {

        None(DuckmaBeacons.None, Color.GRAY, 0, 0),
        Yellow(DuckmaBeacons.YBlue, Color.YELLOW, R.string.title_home, R.string.message_home),
        Red(DuckmaBeacons.GLightBlue, Color.RED, R.string.title_questions, R.string.message_questions),
        Blue(DuckmaBeacons.YGreen, Color.BLUE, R.string.title_info, R.string.message_info),
        Green(DuckmaBeacons.GBlue, Color.GREEN, R.string.title_end, R.string.message_end);

        DuckmaBeacons beacon;
        int color, titleRes, descriptionRes;

        Points(DuckmaBeacons beacon, int color, int titleRes, int descriptionRes) {
            this.color = color;
            this.beacon = beacon;
            this.titleRes = titleRes;
            this.descriptionRes = descriptionRes;
        }

        public DuckmaBeacons getBeacon() {
            return beacon;
        }

        public int getColor() {
            return color;
        }

        public static Points getBeaconPoint(DuckmaBeacons beacon) {
            for (Points point : values()) {
                if (point.beacon == beacon) return point;
            }
            return None;
        }

        public int getTitleRes() {
            return titleRes;
        }

        public int getDescriptionRes() {
            return descriptionRes;
        }

        public static ArrayList<Points> getPointsForRange(Ranges range, ArrayList<DuckmaBeacons> beacons) {

            ArrayList<Points> rangedPoints = new ArrayList<>();

            for (DuckmaBeacons beacon : beacons) {
                Points foundPoint = getBeaconPoint(beacon);
                if (foundPoint != None && Ranges.getRange(beacon) == range) {
                    rangedPoints.add(foundPoint);
                }
            }

            return rangedPoints;

        }

    }

    public static class Location {

        public double x;
        public double y;

        public Location(double x, double y) {
            this.x = x;
            this.y = y;
        }

    }

    public static class DistanceComparator implements Comparator<DuckmaBeacons> {

        @Override
        public int compare(DuckmaBeacons lhs, DuckmaBeacons rhs) {
            if (lhs.wrappedBeacon != null &&
                    rhs.wrappedBeacon != null)
                return ((Double) lhs.getDistance()).compareTo(rhs.getDistance());
            return 0;
        }

    }

    public static class FitnessComparator implements Comparator<Sections> {

        @Override
        public int compare(Sections lhs, Sections rhs) {
            return ((Double) rhs.fit).compareTo(lhs.fit);
        }

    }

    public static ImageView createImageView(Context context) {
        //ImageView Setup
        ImageView imageView = new ImageView(context);
        //setting image resource
        imageView.setImageResource(R.drawable.location_circle_red);
        //setting image layout param
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, 60);

        ScaleAnimation pulse = new ScaleAnimation(
                1f, 1.3f, // Start and end values for the X axis scaling
                1f, 1.3f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setRepeatCount(0);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setDuration(1000);
        imageView.setAnimation(pulse);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }
}
