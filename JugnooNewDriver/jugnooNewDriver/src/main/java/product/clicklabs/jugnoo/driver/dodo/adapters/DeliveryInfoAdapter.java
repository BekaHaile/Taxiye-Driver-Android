package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public class DeliveryInfoAdapter extends RecyclerView.Adapter<DeliveryInfoAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();
    private Callback callback;

    public DeliveryInfoAdapter(Activity activity, ArrayList<DeliveryInfo> deliveryInfos, Callback callback) {
        this.activity = activity;
        this.deliveryInfos = deliveryInfos;
        this.callback = callback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_delivery_info, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            DeliveryInfo deliveryInfo = getItem(position);

            holder.textViewOrderIdValue.setText(String.valueOf(deliveryInfo.getId()));
            holder.textViewCustomerNameValue.setText(deliveryInfo.getCustomerName());
            holder.textViewCustomerDeliveryAddressValue.setText(deliveryInfo.getDeliveryAddress());

            holder.textViewOrderStatus.setVisibility(View.VISIBLE);
            if(deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()){
                holder.textViewOrderStatus.setVisibility(View.GONE);
            } else {
                if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
                    holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.delivered));
                    holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.green_delivery));
                } else if(deliveryInfo.getStatus() == DeliveryStatus.CANCELLED.getOrdinal()){
                    holder.textViewOrderStatus.setText(activity.getResources().getString(R.string.returned));
                    holder.textViewOrderStatus.setTextColor(activity.getResources().getColor(R.color.red_status));

                } else{
                    holder.textViewOrderStatus.setVisibility(View.GONE);
                }
            }

            holder.rootLinear.setTag(position);

            holder.rootLinear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if(getItem(pos).getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
                        callback.onClick(pos, getItem(pos));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(deliveryInfos == null){
            return 0;
        } else{
            return deliveryInfos.size();
        }
    }

    public DeliveryInfo getItem(int position){
        return deliveryInfos.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrderIdValue, textViewCustomerNameValue, textViewCustomerDeliveryAddressValue, textViewOrderStatus;
        public RelativeLayout rootLinear;
        public ViewHolder(View convertView, Activity context) {
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
            ((TextView) convertView.findViewById(R.id.textViewOrderId)).setTypeface(Fonts.mavenRegular(context));
            ((TextView) convertView.findViewById(R.id.textViewCustomerName)).setTypeface(Fonts.mavenRegular(context));
            ((TextView) convertView.findViewById(R.id.textViewCustomerDeliveryAddress)).setTypeface(Fonts.mavenRegular(context));
        }
    }

    public interface Callback{
        void onClick(int position, DeliveryInfo deliveryInfo);
    }

}
