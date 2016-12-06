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
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class CustomerInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private Callback callback;

    public CustomerInfoAdapter(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }


    public synchronized void notifyList(){
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

		if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
			text = activity.getResources().getString(R.string.please_drop_customer,
					customerInfo.getName());
		} else if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()) {
			text = activity.getResources().getString(R.string.please_reach_customer_location,
					customerInfo.getName());
		} else if (customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
			text = activity.getResources().getString(R.string.please_start_customer_ride,
					customerInfo.getName());
		}
		holder.textViewCustomer1Name.setText(text);

		holder.textViewCustomer1Address.setText(customerInfo.getAddress());

        holder.relative.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        if(Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(customerInfo.getEngagementId()))&& (getItemCount() >1)){
//            Utils.setTextColor(holder.textViewCustomer1Name, customerInfo.getColor(),
//                    activity.getResources().getColor(R.color.new_orange));
			holder.linearLayoutSelection1.setVisibility(View.GONE);
			holder.linearLayoutCard1.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
        } else {
			holder.linearLayoutSelection1.setVisibility(View.VISIBLE);
			holder.linearLayoutCard1.setBackgroundResource(R.drawable.background_grey_alpha_rounded_bordered);
        }

		holder.buttonSelect.setTag(position);
        holder.buttonSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int position = (int) v.getTag();
					if (!Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(getItem(position).getEngagementId()))) {
						callback.onClick(position, getItem(position));
					}
					notifyList();
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
					if (!Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(getItem(position).getEngagementId()))) {
						callback.onCancelClick(position, getItem(position));
					}
					notifyList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		if(position == 0){
			holder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine.setVisibility(View.GONE);
		} else if(position == getItemCount()-1){
			holder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine1.setVisibility(View.GONE);
			holder.imageViewDote.setBackgroundResource(R.drawable.red_dot_icon);
		} else {
			holder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			holder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
		}


    }

    @Override
    public int getItemCount() {
        if(Data.getAssignedCustomerInfosListForEngagedStatus() == null
                || Data.getAssignedCustomerInfosListForEngagedStatus().size() == 0){
            return 0;
        }
        else{
            return Data.getAssignedCustomerInfosListForEngagedStatus().size();
        }
    }

    public CustomerInfo getItem(int position){
        return Data.getAssignedCustomerInfosListForEngagedStatus().get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView textViewCustomer1Name, textViewCustomer1Address;
		public LinearLayout linearLayoutCard1, linearLayoutSelection1;
        public RelativeLayout relative;
        public ImageView imageViewDote, imageViewHorizontalLineNew, imageViewVerticalLine, imageViewFakeBottom, imageViewVerticalLine1;
		public Button buttonSelect, buttonCancel;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
			textViewCustomer1Name = (TextView) convertView.findViewById(R.id.textViewCustomer1Name);
			textViewCustomer1Name.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
			textViewCustomer1Address = (TextView) convertView.findViewById(R.id.textViewCustomer1Address);
			textViewCustomer1Address.setTypeface(Fonts.mavenRegular(context));

            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
			linearLayoutCard1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutCard1);
			linearLayoutSelection1 = (LinearLayout) convertView.findViewById(R.id.linearLayoutSelection1);
			imageViewDote = (ImageView) convertView.findViewById(R.id.imageViewDote);
			imageViewHorizontalLineNew = (ImageView) convertView.findViewById(R.id.imageViewHorizontalLineNew);

			imageViewVerticalLine = (ImageView) convertView.findViewById(R.id.imageViewVerticalLine);
			imageViewFakeBottom = (ImageView) convertView.findViewById(R.id.imageViewFakeBottom);
			imageViewVerticalLine1 = (ImageView) convertView.findViewById(R.id.imageViewVerticalLine1);

			buttonSelect = (Button) convertView.findViewById(R.id.buttonSelect);
			buttonCancel = (Button) convertView.findViewById(R.id.buttonCancel);
        }
    }

    public interface Callback{
        void onClick(int position, CustomerInfo customerInfo);

		void onCancelClick(int position, CustomerInfo customerInfo);
    }

}
