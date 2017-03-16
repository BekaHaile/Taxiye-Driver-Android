package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverRideHistoryNew;
import product.clicklabs.jugnoo.driver.DriverTicketHistory;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.RideHistoryItem;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneesh on 10/4/15.
 */
public class DriverTicketHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<TicketResponse.TicketDatum> rideHistoryItems;
	private Context context;
	DriverTicketHistory activity;
	private Callback callback;
	private static final int TYPE_FOOTER = 2;
	private int totalRides;
	private static final int TYPE_ITEM = 1;

	public DriverTicketHistoryAdapter(DriverTicketHistory activity, ArrayList<TicketResponse.TicketDatum> items, int totalRides, Callback callback) {
		this.activity = activity;
		this.rideHistoryItems = items;
		this.callback = callback;
		this.totalRides = totalRides;
	}

	public void setList(ArrayList<TicketResponse.TicketDatum> slots, int totalRides){
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

//	private RideHistoryItem getItem(int position) {
//		if (isPositionFooter(position)) {
//			return null;
//		}
//		return rideHistoryItems.get(position);
//	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		if (viewHolder instanceof RideInfoViewHolder) {
			RideInfoViewHolder holder = (RideInfoViewHolder) viewHolder;

			final TicketResponse.TicketDatum itr = rideHistoryItems.get(i);

			holder.textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			holder.textViewInfoValue.setTypeface(Fonts.mavenRegular(context));


			holder.textViewInfoText.setText(itr.getTime());
			holder.textViewInfoDate.setText(DateOperations.convertDateToDay(itr.getDate()) + ", " +
					DateOperations.convertMonthDayViaFormat(itr.getDate()));

			if (itr.getEarning() >= 0) {
				holder.textViewInfoValue.setText(activity.getResources().getString(R.string.rupee) + itr.getEarning());
			} else {
				holder.textViewInfoValue.setText("-" + activity.getResources().getString(R.string.rupee) + Math.abs(itr.getEarning()));
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
					inflate(R.layout.list_item_ticket_history, viewGroup, false);

			return new RideInfoViewHolder(itemView);
		}
	}

	public class RideInfoViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout linearLayoutRideItem;
		protected TextView textViewComplainIdText, textViewComplainId, textViewCreatedOnText, textViewCreatedOn, textViewIssueCategory, textViewStatus, textViewManualAdj;
		protected ImageView imageViewArrow;
		protected int id;

		public RideInfoViewHolder(View v) {
			super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			imageViewArrow = (ImageView)v.findViewById(R.id.imageViewArrow);
			textViewComplainIdText = (TextView) v.findViewById(R.id.textViewComplainIdText);
			textViewComplainIdText.setTypeface(Fonts.mavenRegular(context));
			textViewComplainId = (TextView) v.findViewById(R.id.textViewComplainId);
			textViewComplainId.setTypeface(Fonts.mavenRegular(context));
			textViewStatus = (TextView) v.findViewById(R.id.textViewStatus);
			textViewStatus.setTypeface(Fonts.mavenRegular(context));
			textViewCreatedOnText = (TextView) v.findViewById(R.id.textViewCreatedOnText);
			textViewCreatedOnText.setTypeface(Data.latoRegular(context));
			textViewCreatedOn = (TextView) v.findViewById(R.id.textViewCreatedOn);
			textViewCreatedOn.setTypeface(Data.latoRegular(context));

			textViewIssueCategory = (TextView) v.findViewById(R.id.textViewIssueCategory);
			textViewIssueCategory.setTypeface(Data.latoSemiBold(context));
			textViewManualAdj = (TextView) v.findViewById(R.id.textViewManualAdj);
			textViewManualAdj.setTypeface(Data.latoHeavy(context));

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
			textViewShowMore.setTypeface(Data.latoRegular(context));
			textViewShowMore.setText("show more");
		}
	}


	public interface Callback{
		void onRideClick(int position, InfoTileResponse.Tile.Extras extras);
		void onShowMoreClick();
	}

}