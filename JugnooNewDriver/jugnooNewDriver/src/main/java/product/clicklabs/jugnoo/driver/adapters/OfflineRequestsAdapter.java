package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Utils;


/**
 * Created by Ankit on 7/17/15.
 */
public class OfflineRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_ITEM = 1;
	private Activity activity;
	private int rowLayout;
	CustomerInfo customerInfo;
	private ArrayList<CustomerInfo> requestsList = new ArrayList<>();
	private Callback callback;
	private int totalRequests;
	HomeActivity homeActivity;

	public OfflineRequestsAdapter(ArrayList<CustomerInfo> requestsList, Activity activity, int rowLayout, int totalRequests, Callback callback,HomeActivity homeActivity) {
		this.requestsList = requestsList;
		this.activity = activity;
		this.rowLayout = rowLayout;
		this.totalRequests = totalRequests;
		this.callback = callback;
		this.homeActivity = homeActivity;
	}

	public void notifyList(int totalRequests, ArrayList<CustomerInfo> requestsList, boolean refresh) {
		this.totalRequests = totalRequests;
		if (refresh) {
			this.requestsList.clear();
		}
		this.requestsList.addAll(requestsList);
		this.notifyDataSetChanged();
	}

	public int getListSize() {
		return requestsList.size();
	}
	public void removeItem(int position) {
		requestsList.remove(position);
		notifyItemRemoved(position);
	}

	public void restoreItem(CustomerInfo item, int position) {
		requestsList.add(position, item);
		notifyItemInserted(position);
	}

	public ArrayList<CustomerInfo> getData() {
		return requestsList;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_FOOTER) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_show_more, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewFooterHolder(v, activity);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

//			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
//			v.setLayoutParams(layoutParams);
//
//			ASSL.DoMagic(v);
			return new ViewHolder(v, activity);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
		if (viewholder instanceof ViewHolder) {
			ViewHolder holder = (ViewHolder) viewholder;
			CustomerInfo customerInfo = requestsList.get(position);

			holder.tvName.setText(customerInfo.getName());
			holder.tvPickup.setText(customerInfo.getPickupAddressEng());
			holder.tvDrop.setText(customerInfo.getDropAddress());
			holder.tvTime.setText(customerInfo.getPickupTime());
			holder.tvPrice.setText(customerInfo.getEstimatedDriverFare());

			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.relative.getLayoutParams();
			if(position==0){
				params.setMargins(0, 0, 0, 0);
				holder.relative.setLayoutParams(params);
			}
			else {
				params.setMargins(0, 10, 0, 0);
				holder.relative.setLayoutParams(params);
			}

			double distance = 0;
			if (customerInfo.getDryDistance() > 0) {
				holder.tvDistance.setVisibility(View.VISIBLE);
				distance = customerInfo.getDryDistance();
			} else if (homeActivity.myLocation != null) {
				holder.tvDistance.setVisibility(View.VISIBLE);
				distance = MapUtils.distance(new LatLng(homeActivity.myLocation.getLatitude(),homeActivity.myLocation.getLongitude()), customerInfo.getRequestlLatLng());
				distance = distance * 1.5;
			} else {
				holder.tvDistance.setVisibility(View.GONE);
			}
			holder.tvDistance.setText("" + homeActivity.decimalFormat.format(distance * UserData.getDistanceUnitFactor(homeActivity))
					+ " " + Utils.getDistanceUnit(UserData.getDistanceUnit(homeActivity)));

			holder.relative.setTag(position);

			try {
				if (customerInfo.getImage().equalsIgnoreCase("")) {
					holder.ivImage.setVisibility(View.GONE);

				} else {
					holder.ivImage.setVisibility(View.VISIBLE);
					//Picasso.with(activity).load(customerInfo.getNotificationImage()).into(holder.notificationImage);
					//Picasso.with(activity).load(customerInfo.getNotificationImage()).transform(new CircleTransform()).into(holder.notificationImage);

					Picasso.with(activity).load(customerInfo.getImage()).transform(new CircleTransform())
							.placeholder(R.drawable.ic_profile_img_placeholder)
							.error(R.drawable.ic_profile_img_placeholder)
//                        .transform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.TOP))
							.into(holder.ivImage);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			holder.relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						int position = (int) v.getTag();
						notifyItemChanged(position);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else if (viewholder instanceof ViewFooterHolder) {
			ViewFooterHolder holder = (ViewFooterHolder) viewholder;
			holder.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callback.onShowMoreClick();
				}
			});
		}

	}


	@Override
	public int getItemCount() {
		if (requestsList == null || requestsList.size() == 0) {
			return 0;
		} else {
			if (totalRequests > requestsList.size()) {
				return requestsList.size() + 1;
			} else {
				return requestsList.size();
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
		return position == requestsList.size();
	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relative;
		public ImageView ivImage;
		public TextView tvName, tvPickup, tvDrop,tvTime,tvDistance,tvPrice;

		public ViewHolder(View itemView, Activity activity) {
			super(itemView);
			ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
			relative = (RelativeLayout) itemView.findViewById(R.id.relative);

			tvName = (TextView) itemView.findViewById(R.id.tvName);
			tvName.setTypeface(Fonts.mavenMedium(activity));
			tvName.setSingleLine(true);

			tvPickup = (TextView) itemView.findViewById(R.id.tvPickup);
			tvPickup.setTypeface(Fonts.mavenMedium(activity));
			tvPickup.setSingleLine(true);

			tvDrop = (TextView) itemView.findViewById(R.id.tvDrop);
			tvDrop.setTypeface(Fonts.mavenRegular(activity));
			tvDrop.setSingleLine(true);

			tvTime = (TextView) itemView.findViewById(R.id.tvTime);
			tvTime.setTypeface(Fonts.mavenRegular(activity));

			tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
			tvDistance.setTypeface(Fonts.mavenRegular(activity));

			tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
			tvPrice.setTypeface(Fonts.mavenBold(activity));
		}
	}

	public class ViewFooterHolder extends RecyclerView.ViewHolder {
		public RelativeLayout relativeLayoutShowMore;
		public TextView textViewShowMore;

		public ViewFooterHolder(View convertView, Activity context) {
			super(convertView);
			relativeLayoutShowMore = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutShowMore);
			textViewShowMore = (TextView) convertView.findViewById(R.id.textViewShowMore);
			textViewShowMore.setTypeface(Fonts.mavenRegular(context));
			textViewShowMore.setText(context.getResources().getString(R.string.show_more));
		}
	}

	public interface Callback {
		void onShowMoreClick();
	}
}
