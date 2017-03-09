package product.clicklabs.jugnoo.driver.home.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class CustomerInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private HomeActivity activity;
	private Callback callback;

	public CustomerInfoAdapter(HomeActivity activity, Callback callback) {
		this.activity = activity;
		this.callback = callback;
	}


	public synchronized void notifyList() {
		this.notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_detail, parent, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
				RecyclerView.LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);

		ASSL.DoMagic(v);
		return new ViewHolder(v, activity);
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		CustomerInfo customerInfo = getItem(position);
		ViewHolder holder = (ViewHolder) viewHolder;
		holder.relative.setTag(position);
		String text = "";

		holder.textViewDeliveryName.setVisibility(View.GONE);

		if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
			if (customerInfo.getIsDeliveryPool() == 1) {
				text = activity.getResources().getString(R.string.please_drop_customer_delivery,
						customerInfo.getName());
				holder.textViewDeliveryName.setVisibility(View.VISIBLE);
				holder.textViewDeliveryName.setText(customerInfo.getDeliveryInfos().get(0).getCustomerName());
				setView(holder, 3);
			} else {
				text = activity.getResources().getString(R.string.please_drop_customer,
						customerInfo.getName());
			}
		} else if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()) {
			if (customerInfo.getIsDeliveryPool() == 1) {
				text = activity.getResources().getString(R.string.please_reach_customer_delivery,
						customerInfo.getName());
				setView(holder, 1);
			} else {
				text = activity.getResources().getString(R.string.please_reach_customer_location,
						customerInfo.getName());
			}
		} else if (customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
			if (customerInfo.getIsDeliveryPool() == 1) {
				text = activity.getResources().getString(R.string.please_start_customer_delivery,
						customerInfo.getName());
				setView(holder, 2);
			} else {
				text = activity.getResources().getString(R.string.please_start_customer_ride,
						customerInfo.getName());
			}
		}
		holder.textViewCustomer1Name.setText(text);

		if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
			holder.buttonCancel.setVisibility(View.GONE);
			for (int i = 0; i < customerInfo.getDeliveryInfos().size(); i++) {
				if (customerInfo.getDeliveryInfos().get(i).getStatus() == DeliveryStatus.COMPLETED.getOrdinal() ||
						customerInfo.getDeliveryInfos().get(i).getStatus() == DeliveryStatus.CANCELLED.getOrdinal()) {
					ImageView ivLine = new ImageView(activity);
					ivLine.setImageResource(R.color.red_v2);
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
					params1.weight = 1f;
					holder.linearLayoutProgress.addView(ivLine, params1);

					ImageView ivCircle = new ImageView(activity);
					ivCircle.setImageResource(R.drawable.circle_orange);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(14, 14);
//					params.weight = 1f;
					holder.linearLayoutProgress.addView(ivCircle, params);
				} else {
					ImageView ivLine = new ImageView(activity);
					ivLine.setImageResource(R.color.white_grey_v2);
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
					params1.weight = 1f;
					holder.linearLayoutProgress.addView(ivLine, params1);

						ImageView ivCircle = new ImageView(activity);
					ivCircle.setImageResource(R.drawable.circle_grey);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(14, 14);
//					params.weight = 1f;
					holder.linearLayoutProgress.addView(ivCircle, params);
				}
			}

		} else {
			for (int i = 0; i < customerInfo.getTotalDeliveries(); i++) {

				ImageView ivLine = new ImageView(activity);
				ivLine.setImageResource(R.color.white_grey_v2);
				LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2);
				params1.weight = 1f;
				holder.linearLayoutProgress.addView(ivLine, params1);

				ImageView ivCircle = new ImageView(activity);
				ivCircle.setImageResource(R.drawable.circle_grey);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(14, 14);
//				params.weight = 1f;
				holder.linearLayoutProgress.addView(ivCircle, params);
			}
		}
		if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal() && customerInfo.getIsDeliveryPool() == 1) {
			holder.textViewCustomer1Address.setText(customerInfo.getDeliveryInfos().get(0).getDeliveryAddress());
		} else {
			holder.textViewCustomer1Address.setText(customerInfo.getAddress());
		}

		if(customerInfo.getIsDeliveryPool() == 1 && customerInfo.getIsDelivery() == 1){
			holder.textViewOrderId.setVisibility(View.VISIBLE);
			holder.textViewOrderId.setText(""+Math.abs(customerInfo.getOrderId()));
		} else {
			holder.textViewOrderId.setVisibility(View.GONE);
		}

		holder.relative.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
		if (Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(customerInfo.getEngagementId())) && (getItemCount() > 1)) {
//            Utils.setTextColor(holder.textViewCustomer1Name, customerInfo.getColor(),
//                    activity.getResources().getColor(R.color.new_orange));
			holder.linearLayoutSelection1.setVisibility(View.GONE);
		} else {
			holder.linearLayoutSelection1.setVisibility(View.VISIBLE);
		}

		holder.buttonSelect.setTag(position);
		holder.buttonSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final int position = (int) v.getTag();
					DialogPopup.alertPopupTwoButtonsWithListeners(activity, "",
							activity.getResources().getString(R.string.switch_confiramtion),
							activity.getResources().getString(R.string.ok),
							activity.getResources().getString(R.string.cancel),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (!Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(getItem(position).getEngagementId()))) {
										callback.onClick(position, getItem(position));
									}
									notifyList();
								}
							}, new View.OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}, false, false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		holder.buttonCancel.setTag(position);
		holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int position = (int) v.getTag();
					activity.cancelRideRemotely(String.valueOf(getItem(position).getEngagementId()));
					if (!Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(getItem(position).getEngagementId()))) {
						callback.onCancelClick(position, getItem(position));
					}
					notifyList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		if (position == 0) {
			holder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine.setVisibility(View.GONE);
			holder.imageViewHorizontalLineNew.setImageResource(R.drawable.radio_select);
		} else if (position == getItemCount() - 1) {
			holder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine1.setVisibility(View.GONE);
		} else {
			holder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
		}

	}

	public void setView(ViewHolder holder, int state) {
		ImageView ivCircle = new ImageView(activity);
		ImageView ivCircle1 = new ImageView(activity);
		ImageView ivCircle2 = new ImageView(activity);
		ImageView ivLine = new ImageView(activity);
		ImageView ivLine1 = new ImageView(activity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(14, 14);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(14, 2);

		if (state == 3) {
			ivCircle.setImageResource(R.drawable.circle_orange);
			ivCircle1.setImageResource(R.drawable.circle_orange);
			ivCircle2.setImageResource(R.drawable.circle_orange);
			ivLine.setBackgroundResource(R.color.red_v2);
			ivLine1.setBackgroundResource(R.color.red_v2);
			holder.linearLayoutProgress.addView(ivCircle, params);
			holder.linearLayoutProgress.addView(ivLine, params1);
			holder.linearLayoutProgress.addView(ivCircle1, params);
			holder.linearLayoutProgress.addView(ivLine1, params1);
			holder.linearLayoutProgress.addView(ivCircle2, params);

		} else if (state == 2) {
			ivCircle.setImageResource(R.drawable.circle_orange);
			ivCircle1.setImageResource(R.drawable.circle_orange);
			ivCircle2.setImageResource(R.drawable.circle_grey);
			ivLine.setBackgroundResource(R.color.red_v2);
			ivLine1.setBackgroundResource(R.color.white_grey_v2);
			holder.linearLayoutProgress.addView(ivCircle, params);
			holder.linearLayoutProgress.addView(ivLine, params1);
			holder.linearLayoutProgress.addView(ivCircle1, params);
			holder.linearLayoutProgress.addView(ivLine1, params1);
			holder.linearLayoutProgress.addView(ivCircle2, params);

		} else {
			ivCircle.setImageResource(R.drawable.circle_orange);
			ivCircle1.setImageResource(R.drawable.circle_grey);
			ivCircle2.setImageResource(R.drawable.circle_grey);
			ivLine.setBackgroundResource(R.color.white_grey_v2);
			ivLine1.setBackgroundResource(R.color.white_grey_v2);
			holder.linearLayoutProgress.addView(ivCircle, params);
			holder.linearLayoutProgress.addView(ivLine, params1);
			holder.linearLayoutProgress.addView(ivCircle1, params);
			holder.linearLayoutProgress.addView(ivLine1, params1);
			holder.linearLayoutProgress.addView(ivCircle2, params);
		}
	}

	@Override
	public int getItemCount() {
		if (Data.getAssignedCustomerInfosListForEngagedStatus() == null
				|| Data.getAssignedCustomerInfosListForEngagedStatus().size() == 0) {
			return 0;
		} else {
			return Data.getAssignedCustomerInfosListForEngagedStatus().size();
		}
	}

	public CustomerInfo getItem(int position) {
		return Data.getAssignedCustomerInfosListForEngagedStatus().get(position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {


		public TextView textViewCustomer1Name, textViewCustomer1Address, textViewDeliveryName, textViewOrderId;
		public LinearLayout linearLayoutCard1, linearLayoutSelection1, linearLayoutProgress;
		public RelativeLayout relative;
		public ImageView imageViewHorizontalLineNew, imageViewVerticalLine, imageViewFakeBottom, imageViewVerticalLine1, bottomLine;
		public Button buttonSelect, buttonCancel;

		public ViewHolder(View convertView, Activity context) {
			super(convertView);
			textViewCustomer1Name = (TextView) convertView.findViewById(R.id.textViewCustomer1Name);
			textViewCustomer1Name.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);

			textViewDeliveryName = (TextView) convertView.findViewById(R.id.textViewDeliveryName);
			textViewDeliveryName.setTypeface(Fonts.mavenRegular(context));

			textViewCustomer1Address = (TextView) convertView.findViewById(R.id.textViewCustomer1Address);
			textViewCustomer1Address.setTypeface(Fonts.mavenRegular(context));

			textViewOrderId = (TextView) convertView.findViewById(R.id.textViewOrderId);
			textViewOrderId.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);

			relative = (RelativeLayout) convertView.findViewById(R.id.relative);
			linearLayoutCard1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutCard1);
			linearLayoutSelection1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutSelection1);
			linearLayoutProgress = (LinearLayout) convertView.findViewById(R.id.linearLayoutProgress);

			imageViewHorizontalLineNew = (ImageView) convertView.findViewById(R.id.imageViewHorizontalLineNew);
			bottomLine = (ImageView) convertView.findViewById(R.id.bottomLine);

			imageViewVerticalLine = (ImageView) convertView.findViewById(R.id.imageViewVerticalLine);
			imageViewFakeBottom = (ImageView) convertView.findViewById(R.id.imageViewFakeBottom);
			imageViewVerticalLine1 = (ImageView) convertView.findViewById(R.id.imageViewVerticalLine1);

			buttonSelect = (Button) convertView.findViewById(R.id.buttonSelect);
			buttonCancel = (Button) convertView.findViewById(R.id.buttonCancel);
		}
	}

	public interface Callback {
		void onClick(int position, CustomerInfo customerInfo);

		void onCancelClick(int position, CustomerInfo customerInfo);
	}

}
