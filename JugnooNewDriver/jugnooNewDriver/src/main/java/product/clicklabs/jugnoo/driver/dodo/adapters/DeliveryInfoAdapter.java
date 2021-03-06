package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public class DeliveryInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HomeActivity activity;
    private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();
    private Callback callback;

	private static final int TYPE_HEADER = 3;
	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_ITEM = 1;

    public DeliveryInfoAdapter(HomeActivity activity, ArrayList<DeliveryInfo> deliveryInfos, Callback callback) {
        this.activity = activity;
        this.deliveryInfos = deliveryInfos;
        this.callback = callback;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == TYPE_FOOTER) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_list_end, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewFooterHolder(v, activity);
		} else if (viewType == TYPE_HEADER) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_list_start, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewHeaderHolder(v, activity);
		} else {

			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_info, parent, false);

			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);

			ASSL.DoMagic(v);
			return new ViewListHolder(v, activity);
		}
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        try {

			if (viewHolder instanceof ViewListHolder) {
				ViewListHolder holder = (ViewListHolder) viewHolder;

				final DeliveryInfo deliveryInfo = getItem(position);

				holder.textViewOrderIdValue.setText(String.valueOf(deliveryInfo.getIndex() +1));

				if (deliveryInfo.getCustomerName().equalsIgnoreCase("")) {
					holder.textViewCustomerNameValue.setText(activity.getResources().getString(R.string.return_to_merchant));
					holder.textViewOrderIdValue.setText("R");
				} else {
					holder.textViewCustomerNameValue.setText(deliveryInfo.getCustomerName());
				}

				holder.textViewCustomerDeliveryAddressValue.setText(deliveryInfo.getDeliveryAddress());

				holder.textViewOrderStatus.setVisibility(View.VISIBLE);
				if (deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
					holder.imageViewStatus.setBackgroundResource(R.drawable.delivery_pending_clock);

					if (activity.getDeliveryPos() == deliveryInfo.getIndex()) {
						holder.linearLayoutCard1.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
						holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.in_progress));
						holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.themeColor));
						holder.imageViewStatus.setBackgroundResource(R.drawable.delivery_in_progress);
					} else {
						holder.imageViewStatus.setBackgroundResource(R.drawable.delivery_pending_clock);
						holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.pending));
						holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
						holder.linearLayoutCard1.setBackgroundResource(R.drawable.background_white_rounded_grey_alpha_bordered);
					}

				} else {
					holder.linearLayoutCard1.setBackgroundResource(R.drawable.background_grey_deivery_list_rounded_bordered);
					holder.textViewCustomerNameValue.setTextColor(activity.getResources().getColor(R.color.black_text_v2_50op));
					holder.textViewCustomerDeliveryAddressValue.setTextColor(activity.getResources().getColor(R.color.black_text_v2_50op));
					if (deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()) {
						holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.delivered));
						holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.green_delivery));
						holder.imageViewStatus.setBackgroundResource(R.drawable.delivery_green_tick);
					} else if (deliveryInfo.getStatus() == DeliveryStatus.CANCELLED.getOrdinal()) {
						holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.failed));
						holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.red_status));
						holder.imageViewStatus.setBackgroundResource(R.drawable.delivery_red_cross);
					} else {
						holder.textViewOrderStatus.setVisibility(View.GONE);
						holder.imageViewStatus.setVisibility(View.GONE);
					}
				}

				holder.rootLinear.setTag(position);

				holder.rootLinear.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
//                    int pos = (int) v.getTag();
//                    callback.onClick(pos, getItem(pos));

						for (int i = 0; i < deliveryInfos.size(); i++) {
							deliveryInfos.get(i).setSate(false);
						}
						if (deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
							deliveryInfo.setSate(true);
						}
						notifyDataSetChanged();
					}
				});

				if (deliveryInfo.isSate()) {
					holder.linearLayoutSelection1.setVisibility(View.VISIBLE);
				} else {
					holder.linearLayoutSelection1.setVisibility(View.GONE);
				}

				holder.buttonSelect.setTag(position);
				holder.buttonSelect.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int pos = (int) v.getTag();
						callback.onClick(pos-1);
					}
				});

				holder.buttonCancel.setTag(position);
				holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int pos = (int) v.getTag();
						callback.onClick(pos-1);
					}
				});

			} else if (viewHolder instanceof ViewFooterHolder) {
				ViewFooterHolder holder = (ViewFooterHolder) viewHolder;
			} else if (viewHolder instanceof ViewHeaderHolder) {
				ViewHeaderHolder holder = (ViewHeaderHolder) viewHolder;
			}

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(deliveryInfos == null){
            return 0;
        } else{
            return deliveryInfos.size()+2;
        }
    }

    public DeliveryInfo getItem(int position){

		if (isPositionFooter(position) || isPositionHeader(position)) {
			return null;
		}

        return deliveryInfos.get(position-1);
    }

	@Override
	public int getItemViewType(int position){
		if (position == deliveryInfos.size()+1) {
			return TYPE_FOOTER;
		} else if (position == 0) {
			return TYPE_HEADER;
		}
		return TYPE_ITEM;
	}

	private boolean isPositionFooter(int position) {
		return position == deliveryInfos.size()+1;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}

    public class ViewListHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrderIdValue, textViewCustomerNameValue, textViewCustomerDeliveryAddressValue, textViewOrderStatus;
        public RelativeLayout rootLinear;
        public LinearLayout linearLayoutSelection1, linearLayoutCard1;
		public Button buttonSelect, buttonCancel;
		public ImageView imageViewStatus, imageViewFakeBottom, imageViewVerticalLine, imageViewStatusCircle;
        public ViewListHolder(View convertView, Activity context) {
            super(convertView);
            textViewOrderIdValue = (TextView) convertView.findViewById(R.id.textViewOrderIdValue);
            textViewOrderIdValue.setTypeface(Fonts.mavenRegular(context));
            textViewCustomerNameValue = (TextView) convertView.findViewById(R.id.textViewCustomerNameValue);
            textViewCustomerNameValue.setTypeface(Fonts.mavenRegular(context));
            textViewCustomerDeliveryAddressValue = (TextView) convertView.findViewById(R.id.textViewCustomerDeliveryAddressValue);
            textViewCustomerDeliveryAddressValue.setTypeface(Fonts.mavenRegular(context));
            textViewOrderStatus = (TextView) convertView.findViewById(R.id.textViewOrderStatus);
            textViewOrderStatus.setTypeface(Fonts.mavenRegular(context));
            rootLinear = (RelativeLayout) convertView.findViewById(R.id.rootLinear);
			linearLayoutSelection1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutSelection1);
			linearLayoutCard1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutCard1);
			buttonSelect = (Button) convertView.findViewById(R.id.buttonSelect);
			buttonCancel = (Button) convertView.findViewById(R.id.buttonCancel);
			imageViewStatus = (ImageView) convertView.findViewById(R.id.imageViewStatus);
            imageViewFakeBottom = (ImageView) convertView.findViewById(R.id.imageViewFakeBottom);
//			imageViewVerticalLine = (ImageView) convertView.findViewById(R.id.imageViewVerticalLine);
            imageViewStatusCircle = (ImageView) convertView.findViewById(R.id.imageViewStatusCircle);

        }
    }

    public class ViewFooterHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutEnd;
        public TextView textViewDeliveryStop;
        public ImageView imageViewDeliverycircle2, imageViewDeliveryline2;

        public ViewFooterHolder(View convertView, Activity context) {
            super(convertView);
            relativeLayoutEnd = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutEnd);
            textViewDeliveryStop = (TextView) convertView.findViewById(R.id.textViewDeliveryStop);
            textViewDeliveryStop.setText(activity.getResources().getString(R.string.delivery_end));
            imageViewDeliverycircle2 = (ImageView) convertView.findViewById(R.id.imageViewDeliverycircle2);
            imageViewDeliveryline2 = (ImageView) convertView.findViewById(R.id.imageViewDeliveryline2);
        }
    }

    public class ViewHeaderHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutStart;
        public TextView textViewDeliveryStart;
        public ImageView imageViewDeliverycircle, imageViewDeliveryline1;

        public ViewHeaderHolder(View convertView, Activity context) {
            super(convertView);
            relativeLayoutStart = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutStart);
            textViewDeliveryStart = (TextView) convertView.findViewById(R.id.textViewDeliveryStart);
            textViewDeliveryStart.setText(activity.getResources().getString(R.string.delivery_start));
            imageViewDeliverycircle = (ImageView) convertView.findViewById(R.id.imageViewDeliverycircle);
            imageViewDeliveryline1 = (ImageView) convertView.findViewById(R.id.imageViewDeliveryline1);
        }
    }


    public interface Callback{
		void onClick(int position);

		void onCancelClick(int position);
    }

}
