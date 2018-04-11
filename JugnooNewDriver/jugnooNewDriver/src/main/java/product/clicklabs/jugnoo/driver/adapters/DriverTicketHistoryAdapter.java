package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import product.clicklabs.jugnoo.driver.DriverTicketHistory;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class DriverTicketHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<TicketResponse.TicketDatum> ticketHistoryItems;
	private Context context;
	DriverTicketHistory activity;
	private Callback callback;
	private static final int TYPE_FOOTER = 2;
	private int totalRides;
	private static final int TYPE_ITEM = 1;

	public DriverTicketHistoryAdapter(DriverTicketHistory activity, ArrayList<TicketResponse.TicketDatum> items, int totalRides, Callback callback) {
		this.activity = activity;
		this.ticketHistoryItems = items;
		this.callback = callback;
		this.totalRides = totalRides;
	}

	public void setList(ArrayList<TicketResponse.TicketDatum> slots, int totalRides){
		this.totalRides = totalRides;
		this.ticketHistoryItems = slots;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		if (ticketHistoryItems == null || ticketHistoryItems.size() == 0) {
			return 0;
		} else {
			if (totalRides > ticketHistoryItems.size()) {
				return ticketHistoryItems.size() + 1;
			} else {
				return ticketHistoryItems.size();
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
		return position == ticketHistoryItems.size();
	}



	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		if (viewHolder instanceof RideInfoViewHolder) {
			RideInfoViewHolder holder = (RideInfoViewHolder) viewHolder;

			final TicketResponse.TicketDatum itr = ticketHistoryItems.get(i);

			holder.textViewComplainId.setText("#"+itr.getTicketId());
			holder.textViewCreatedOn.setText(itr.getOpeningDate());

			if (!itr.getIssueType().equalsIgnoreCase("")) {
				holder.textViewIssueCategory.setVisibility(View.VISIBLE);
				holder.textViewIssueCategory.setText(itr.getIssueType());
			} else {
				holder.textViewIssueCategory.setVisibility(View.GONE);
			}

			if (itr.getStatus().equalsIgnoreCase("settled")) {
				holder.textViewStatus.setVisibility(View.VISIBLE);
				holder.textViewStatus.setText(activity.getResources().getString(R.string.settled));
				holder.textViewStatus.setTextColor(activity.getResources().getColor(R.color.green_status));
				holder.imageViewStatus.setImageResource(R.drawable.ic_tick_green_20);

			} else if(itr.getStatus().equalsIgnoreCase("registered")){
				holder.textViewStatus.setVisibility(View.VISIBLE);
				holder.textViewStatus.setText(activity.getResources().getString(R.string.registered));
				holder.textViewStatus.setTextColor(activity.getResources().getColor(R.color.red_v2));
				holder.imageViewStatus.setImageResource(R.drawable.ic_tick_orange_20);

			} else if(itr.getStatus().equalsIgnoreCase("pending")){
				holder.textViewStatus.setVisibility(View.VISIBLE);
				holder.textViewStatus.setText(activity.getResources().getString(R.string.pending));
				holder.textViewStatus.setTextColor(activity.getResources().getColor(R.color.red_ticket_status));
				holder.imageViewStatus.setImageResource(R.drawable.in_progress_red_20);
			}

			if(itr.getManualAdjustment() != 0){
				holder.textViewManualAdj.setVisibility(View.VISIBLE);
				holder.textViewManualAdj.setText(Utils.getAbsWithDecimalAmount(activity, itr.getManualAdjustment(),itr.getCurrencyUnit()));
			} else {
				holder.textViewManualAdj.setVisibility(View.GONE);
			}

			holder.linearLayoutTicketItem.setTag(i);
			holder.linearLayoutTicketItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						int pos = (int) v.getTag();
						callback.onTicketClick(pos, ticketHistoryItems.get(pos));

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
		protected LinearLayout linearLayoutTicketItem;
		protected TextView textViewComplainIdText, textViewComplainId, textViewCreatedOnText, textViewCreatedOn,
				textViewIssueCategory, textViewStatus, textViewManualAdj;
		protected ImageView imageViewStatus;
		protected int id;

		public RideInfoViewHolder(View v) {
			super(v);
			linearLayoutTicketItem = (LinearLayout)v.findViewById(R.id.linearLayoutTicketItem);
			imageViewStatus = (ImageView)v.findViewById(R.id.imageViewStatus);
			textViewComplainIdText = (TextView) v.findViewById(R.id.textViewComplainIdText);
			textViewComplainIdText.setTypeface(Data.latoRegular(activity));
			textViewComplainId = (TextView) v.findViewById(R.id.textViewComplainId);
			textViewComplainId.setTypeface(Data.latoRegular(activity));

			textViewStatus = (TextView) v.findViewById(R.id.textViewStatus);
			textViewStatus.setTypeface(Fonts.mavenBold(activity));
			textViewCreatedOnText = (TextView) v.findViewById(R.id.textViewCreatedOnText);
			textViewCreatedOnText.setTypeface(Data.latoRegular(activity));
			textViewCreatedOn = (TextView) v.findViewById(R.id.textViewCreatedOn);
			textViewCreatedOn.setTypeface(Data.latoRegular(activity));

			textViewIssueCategory = (TextView) v.findViewById(R.id.textViewIssueCategory);
			textViewIssueCategory.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			textViewManualAdj = (TextView) v.findViewById(R.id.textViewManualAdj);
			textViewManualAdj.setTypeface(Data.latoHeavy(activity));

			linearLayoutTicketItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutTicketItem);
		}
	}

	public class ViewFooterHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relativeLayoutShowMore;
		public TextView textViewShowMore;

		public ViewFooterHolder(View convertView) {
			super(convertView);
			relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
			textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore);
			textViewShowMore.setTypeface(Data.latoRegular(activity));
			textViewShowMore.setText("show more");
		}
	}


	public interface Callback{
		void onTicketClick(int position, TicketResponse.TicketDatum extras);
		void onShowMoreClick();
	}

}