package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.Ranklist;
import rmn.androidscreenlibrary.ASSL;


/**
 * Created by Ankit on 7/17/15.
 */
public class LeaderboardItemsAdapter extends RecyclerView.Adapter<LeaderboardItemsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    Ranklist leaderboardItem;
    private ArrayList<Ranklist> leaderboardItems = new ArrayList<>();

    public LeaderboardItemsAdapter(ArrayList<Ranklist> leaderboardItems, Activity activity, int rowLayout) {
        this.leaderboardItems = leaderboardItems;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, 110);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(LeaderboardItemsAdapter.ViewHolder holder, int position) {
        leaderboardItem = leaderboardItems.get(position);

        holder.textViewRank.setText(leaderboardItem.getRankStr());
        holder.textViewName.setText(leaderboardItem.getName());
        holder.textViewNoOfDownloads.setText(leaderboardItem.getDownloadsStr());

        switch(position){
            case 0:
                holder.textViewRank.setBackgroundResource(R.drawable.circle_white);
                break;
            case 1:
                holder.textViewRank.setBackgroundResource(R.drawable.circle_white_60);
                break;
            case 2:
                holder.textViewRank.setBackgroundResource(R.drawable.circle_white_40);
                break;
            case 3:
                holder.textViewRank.setBackgroundResource(R.drawable.circle_white_20);
                break;
            default:
                holder.textViewRank.setBackgroundResource(R.drawable.background_transparent);
                break;
        }

        if(leaderboardItem.getIsUser()){
            holder.textViewRank.setTextColor(activity.getResources().getColor(R.color.grey_black));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.grey_black));
            holder.textViewRank.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
            holder.textViewName.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
        }
        else{
            holder.textViewRank.setTextColor(activity.getResources().getColor(R.color.grey_dark_more));
            holder.textViewName.setTextColor(activity.getResources().getColor(R.color.grey_dark_more));
            holder.textViewRank.setTypeface(Data.latoRegular(activity));
            holder.textViewName.setTypeface(Data.latoRegular(activity));
        }

	}

    @Override
    public int getItemCount() {
        return leaderboardItems == null ? 0 : leaderboardItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relative;
        public TextView textViewRank, textViewName, textViewNoOfDownloads;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            textViewRank = (TextView)itemView.findViewById(R.id.textViewRank);
            textViewRank.setTypeface(Data.latoRegular(activity));
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewName.setTypeface(Data.latoRegular(activity));
            textViewNoOfDownloads = (TextView)itemView.findViewById(R.id.textViewNoOfDownloads);
            textViewNoOfDownloads.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
        }
    }
}
