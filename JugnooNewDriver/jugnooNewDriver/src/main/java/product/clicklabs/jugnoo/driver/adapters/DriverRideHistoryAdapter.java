package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.DailyRideDetailsActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverRideHistoryNew;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideHistoryItem;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneesh on 10/4/15.
 */
public class DriverRideHistoryAdapter extends RecyclerView.Adapter<DriverRideHistoryAdapter.rideInfoViewHolder> {

	private ArrayList<RideHistoryItem> rideHistoryItems;
	private Context context;
	DriverRideHistoryNew activity;
	private Callback callback;



	public DriverRideHistoryAdapter(DriverRideHistoryNew activity, ArrayList<RideHistoryItem> items, Callback callback) {
		this.activity = activity;
		this.rideHistoryItems = items;
		this.callback = callback;
	}

	public void setList(ArrayList<RideHistoryItem> slots){
		this.rideHistoryItems = slots;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return rideHistoryItems.size();
	}

	@Override
	public void onBindViewHolder(rideInfoViewHolder rideInfoViewHolder, int i) {
		final RideHistoryItem itr = rideHistoryItems.get(i);

		rideInfoViewHolder.textViewInfoText.setTypeface(Fonts.mavenRegular(context));
		rideInfoViewHolder.textViewInfoValue.setTypeface(Fonts.mavenRegular(context));


		rideInfoViewHolder.textViewInfoText.setText(itr.getTime());
		rideInfoViewHolder.textViewInfoDate.setText(DateOperations.convertDateToDay(itr.getDate()) +", "+
				DateOperations.convertMonthDayViaFormat(itr.getDate()));

		if(itr.getEarning() >= 0) {
			rideInfoViewHolder.textViewInfoValue.setText(activity.getResources().getString(R.string.rupee)+ itr.getEarning());
		} else{
			rideInfoViewHolder.textViewInfoValue.setText("-"+ activity.getResources().getString(R.string.rupee)+ Math.abs(itr.getEarning()));
		}


		if(itr.getStatus().equalsIgnoreCase("Ride Cancelled")){
			rideInfoViewHolder.textViewStatus.setVisibility(View.VISIBLE);
			rideInfoViewHolder.textViewStatus.setText(activity.getResources().getString(R.string.cancelled));
			rideInfoViewHolder.textViewStatus.setTextColor(activity.getResources().getColor(R.color.red_status_v2));

		} else {
			rideInfoViewHolder.textViewStatus.setVisibility(View.GONE);
			rideInfoViewHolder.textViewInfoText.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
			rideInfoViewHolder.textViewInfoValue.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
		}

		if(itr.getType() ==3){
			rideInfoViewHolder.textViewType.setVisibility(View.VISIBLE);
			rideInfoViewHolder.textViewType.setText(activity.getResources().getString(R.string.delivery_caps));
		} else if(itr.getType() ==2){
			rideInfoViewHolder.textViewType.setVisibility(View.VISIBLE);
			rideInfoViewHolder.textViewType.setText(activity.getResources().getString(R.string.pool));
		} else {
			rideInfoViewHolder.textViewType.setVisibility(View.GONE);
		}




		rideInfoViewHolder.linearLayoutRideItem.setTag(i);
		rideInfoViewHolder.linearLayoutRideItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					int pos = (int) v.getTag();
					callback.onRideClick(pos, rideHistoryItems.get(pos).getExtras());
					notifyDataSetChanged();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	@Override
	public rideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_ride_history_new, viewGroup, false);

		return new rideInfoViewHolder(itemView);
	}

	public class rideInfoViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout linearLayoutRideItem;
		protected TextView textViewInfoText, textViewInfoValue, textViewStatus, textViewType, textViewInfoDate;
		protected ImageView imageViewArrow;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			imageViewArrow = (ImageView)v.findViewById(R.id.imageViewArrow);
			textViewInfoText = (TextView) v.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			textViewInfoValue = (TextView) v.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(context));
			textViewStatus = (TextView) v.findViewById(R.id.textViewStatus);
			textViewStatus.setTypeface(Fonts.mavenRegular(context));
			textViewType = (TextView) v.findViewById(R.id.textViewType);
			textViewType.setTypeface(Fonts.mavenRegular(context));
			textViewInfoDate = (TextView) v.findViewById(R.id.textViewInfoDate);
			textViewInfoDate.setTypeface(Fonts.mavenRegular(context));

			linearLayoutRideItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutRideItem);
		}
	}

	public interface Callback{
		void onRideClick(int position, InfoTileResponse.Tile.Extras extras);
	}

}