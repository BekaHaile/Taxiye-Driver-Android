package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.Item;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


/**
 * Created by Ankit on 7/17/15.
 */
public class LeaderboardItemsAdapter extends RecyclerView.Adapter<LeaderboardItemsAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
	Item leaderboardItem;
    private ArrayList<Item> leaderboardItems = new ArrayList<>();

    public LeaderboardItemsAdapter(ArrayList<Item> leaderboardItems, Activity activity, int rowLayout) {
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

        holder.textViewRank.setText(String.valueOf(leaderboardItem.getCityRank()));
        holder.textViewName.setText(leaderboardItem.getDriverName());
        holder.textViewNoOfDownloads.setText(String.valueOf(leaderboardItem.getCustomColumnValue()));
        holder.textViewRank.setBackgroundResource(R.drawable.circle_white);

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
            textViewRank.setTypeface(Fonts.mavenRegular(activity));
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewName.setTypeface(Fonts.mavenRegular(activity));
            textViewNoOfDownloads = (TextView)itemView.findViewById(R.id.textViewNoOfDownloads);
            textViewNoOfDownloads.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
        }
    }
}
