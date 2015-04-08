package com.duckma.conference.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckma.conference.R;
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
 * BeaconsAdapter:
 * <p/>
 * This class extends the BaseAdapter to handle the display of a list of DuckmaBeacons.
 * Each item displays a square with the estimote beacon color plus a stripe with the same color as
 * the label on it.
 * <p/>
 * The list should be sorted by distance from the user and the adapter shows its distance in meters
 * together with the rssi strenght.
 * <p/>
 * ************************************************************************************************
 */

public class BeaconsAdapter extends BaseAdapter {

    ArrayList<BeaconsUtils.DuckmaBeacons> items = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;

    public BeaconsAdapter(Context context, ArrayList<BeaconsUtils.DuckmaBeacons> items) {
        super();
        this.items = items;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getMajor();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        BeaconsUtils.DuckmaBeacons item = (BeaconsUtils.DuckmaBeacons) getItem(position);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.beacon_list_item, parent, false);
            holder = new ViewHolder();
            holder.imgBeaconColor = (ImageView) convertView.findViewById(R.id.imgBeaconColor);
            holder.imgLabelColor = (ImageView) convertView.findViewById(R.id.imgLabelColor);
            holder.tvBeaconData = (TextView) convertView.findViewById(R.id.tvBeaconData);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        switch (item) {

            case GBlue:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                break;

            case YBlue:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                break;

            case GLightBlue:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue));
                break;

            case YLightBlue:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.light_blue));
                break;

            case GGreen:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.green));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
                break;

            case YGreen:
                holder.imgLabelColor.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                holder.imgBeaconColor.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
                break;

        }

        holder.tvBeaconData.setText(item.name() + " : " + (String.valueOf(item.getDistance())).substring(0, 3) + "m - rssi = " + item.getRssi());

        return convertView;
    }

    private class ViewHolder {

        ImageView imgBeaconColor;
        ImageView imgLabelColor;
        TextView tvBeaconData;

    }

}
