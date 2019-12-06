package product.clicklabs.jugnoo.driver.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.DriverRideHistoryNew;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.RideHistoryItem;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class DriverRideHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<RideHistoryItem> rideHistoryItems;
	DriverRideHistoryNew activity;
	private Callback callback;
	private static final int TYPE_FOOTER = 2;
	private int totalRides;
	private static final int TYPE_ITEM = 1;

	public DriverRideHistoryAdapter(DriverRideHistoryNew activity, ArrayList<RideHistoryItem> items, int totalRides, Callback callback) {
		this.activity = activity;
		this.rideHistoryItems = items;
		this.callback = callback;
		this.totalRides = totalRides;
	}

	public void setList(ArrayList<RideHistoryItem> slots, int totalRides){
		this.totalRides = totalRides;
		this.rideHistoryItems = slots;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		if (rideHistoryItems == null || rideHistoryItems.size() == 0) {
			return 0;
		} else {
			if (totalRides > rideHistoryItems.size()) {
				return rideHistoryItems.size() + 1;
			} else {
				return rideHistoryItems.size();
			}
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionFooter(position)) {
			return TYPE_FOOTER;
		}
		return TYPE_ITEM;
	}

	private boolean isPositionFooter(int position) {
		return position == rideHistoryItems.size();
	}

	private RideHistoryItem getItem(int position) {
		if (isPositionFooter(position)) {
			return null;
		}
		return rideHistoryItems.get(position);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		if (viewHolder instanceof RideInfoViewHolder) {
			RideInfoViewHolder holder = (RideInfoViewHolder) viewHolder;

			final RideHistoryItem itr = rideHistoryItems.get(i);

			holder.textViewInfoText.setTypeface(Fonts.mavenRegular(activity));
			holder.textViewInfoValue.setTypeface(Fonts.mavenRegular(activity));


			holder.textViewInfoText.setText(itr.getTime());
			holder.textViewInfoDate.setText(DateOperations.convertDateToDay(itr.getDate()) + ", " +
					DateOperations.convertMonthDayViaFormat(itr.getDate()));

			if (itr.getEarning() >= 0) {
				holder.textViewInfoValue.setText(Utils.formatCurrencyValue(itr.getCurrencyUnit(),itr.getEarning()));
			} else {
				holder.textViewInfoValue.setText("-" +Utils.formatCurrencyValue(itr.getCurrencyUnit(),Math.abs(itr.getEarning())));
			}


			if (itr.getStatus().equalsIgnoreCase("Ride Cancelled") || itr.getStatus().equalsIgnoreCase("Delivery Cancelled")) {
				holder.textViewStatus.setVisibility(View.VISIBLE);
				holder.textViewStatus.setText(activity.getResources().getString(R.string.cancelled));
				holder.textViewStatus.setTextColor(activity.getResources().getColor(R.color.red_status_v2));

			} else {
				holder.textViewStatus.setVisibility(View.GONE);
				holder.textViewInfoText.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
				holder.textViewInfoValue.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
			}

			if (itr.getType() == 3 || itr.getType() == 4) {
				holder.textViewType.setVisibility(View.VISIBLE);
				holder.textViewType.setText(activity.getResources().getString(R.string.delivery));
			} else if (itr.getType() == 2) {
				holder.textViewType.setVisibility(View.VISIBLE);
				holder.textViewType.setText(activity.getResources().getString(R.string.pool));
			} else {
				holder.textViewType.setVisibility(View.GONE);
			}


			holder.linearLayoutRideItem.setTag(i);
			holder.linearLayoutRideItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						int pos = (int) v.getTag();
						callback.onRideClick(pos, rideHistoryItems.get(pos).getExtras());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}  else if (viewHolder instanceof ViewFooterHolder) {
			ViewFooterHolder holder = (ViewFooterHolder) viewHolder;
			holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onShowMoreClick();
				}
			});
		}


	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		if (viewType == TYPE_FOOTER) {
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_show_more, viewGroup, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewFooterHolder(v);
		} else {
			View itemView = LayoutInflater.
					from(viewGroup.getContext()).
					inflate(R.layout.list_item_ride_history_new, viewGroup, false);

			return new RideInfoViewHolder(itemView);
		}
	}

	public class RideInfoViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout linearLayoutRideItem;
		protected TextView textViewInfoText, textViewInfoValue, textViewStatus, textViewType, textViewInfoDate;
		protected ImageView imageViewArrow;
		protected int id;

		public RideInfoViewHolder(View v) {
			super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			imageViewArrow = (ImageView)v.findViewById(R.id.imageViewArrow);
			textViewInfoText = (TextView) v.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(activity));
			textViewInfoValue = (TextView) v.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(activity));
			textViewStatus = (TextView) v.findViewById(R.id.textViewStatus);
			textViewStatus.setTypeface(Fonts.mavenRegular(activity));
			textViewType = (TextView) v.findViewById(R.id.textViewType);
			textViewType.setTypeface(Fonts.mavenRegular(activity));
			textViewInfoDate = (TextView) v.findViewById(R.id.textViewInfoDate);
			textViewInfoDate.setTypeface(Fonts.mavenRegular(activity));

			linearLayoutRideItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutRideItem);
		}
	}

	public class ViewFooterHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relativeLayoutShowMore;
		public TextView textViewShowMore;

		public ViewFooterHolder(View convertView) {
			super(convertView);
			relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
			textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore);
			textViewShowMore.setTypeface(Fonts.mavenRegular(activity));
			textViewShowMore.setText(R.string.show_more);
		}
	}


	public interface Callback{
		void onRideClick(int position, Tile.Extras extras);
		void onShowMoreClick();
	}

}