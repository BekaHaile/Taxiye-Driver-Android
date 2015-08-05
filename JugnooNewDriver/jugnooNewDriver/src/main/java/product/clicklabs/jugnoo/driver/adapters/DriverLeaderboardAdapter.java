package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboard;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 7/24/15.
 */
public class DriverLeaderboardAdapter extends BaseAdapter  {

    private Context context;
    private LayoutInflater mInflater;
    private ViewHolderDriverLeaderboard holder;
    private ArrayList<DriverLeaderboard> driverLeaderboards;
    private int showCity;

    public DriverLeaderboardAdapter(Context context, ArrayList<DriverLeaderboard> driverLeaderboards, int showCity) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.driverLeaderboards = driverLeaderboards;
        this.showCity = showCity;
    }

    public synchronized void setResults(ArrayList<DriverLeaderboard> driverLeaderboards, int showCity){
        this.driverLeaderboards = driverLeaderboards;
        this.showCity = showCity;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return driverLeaderboards.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolderDriverLeaderboard();
            convertView = mInflater.inflate(R.layout.list_item_driver_leaderboard, null);

            holder.textViewSno = (TextView) convertView.findViewById(R.id.textViewSno); holder.textViewSno.setTypeface(Data.latoRegular(context));
            holder.textViewDriverName = (TextView) convertView.findViewById(R.id.textViewDriverName); holder.textViewDriverName.setTypeface(Data.latoLight(context));
            holder.textViewRidesTaken = (TextView) convertView.findViewById(R.id.textViewRidesTaken); holder.textViewRidesTaken.setTypeface(Data.latoRegular(context));

            holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderDriverLeaderboard) convertView.getTag();
        }

        DriverLeaderboard driverLeaderboard = driverLeaderboards.get(position);

        holder.id = position;

        holder.textViewSno.setText(""+(position+1)+".");
        if(1 == showCity && !"".equalsIgnoreCase(driverLeaderboard.cityName)){
            holder.textViewDriverName.setText(driverLeaderboard.userName+" ("+driverLeaderboard.cityName+")");
        }
        else{
            holder.textViewDriverName.setText(driverLeaderboard.userName);
        }
        holder.textViewRidesTaken.setText(""+driverLeaderboard.numRides);

        return convertView;
    }

    private class ViewHolderDriverLeaderboard {
        TextView textViewSno, textViewDriverName, textViewRidesTaken;
        LinearLayout relative;
        int id;
    }

}

