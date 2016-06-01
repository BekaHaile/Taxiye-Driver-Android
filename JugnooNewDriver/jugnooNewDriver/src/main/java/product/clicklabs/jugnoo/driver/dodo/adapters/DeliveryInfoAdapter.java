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
            holder.textViewCustomerName.setText(deliveryInfo.getCustomerName());
            holder.textViewCustomerDeliveryAddress.setText(deliveryInfo.getDeliveryAddress());
            holder.relativeLayoutCall.setTag(position);
            holder.rootLinear.setTag(position);

            holder.relativeLayoutCall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    callback.onCallClick(pos, getItem(pos));
                }
            });

            holder.rootLinear.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    callback.onClick(pos, getItem(pos));
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
        public TextView textViewOrderId, textViewOrderIdValue, textViewCustomerName, textViewCustomerDeliveryAddress, textViewCall;
        public RelativeLayout relativeLayoutCall;
        public LinearLayout rootLinear;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewOrderId = (TextView) convertView.findViewById(R.id.textViewOrderId);
            textViewOrderId.setTypeface(Fonts.mavenRegular(context));
            textViewOrderIdValue = (TextView) convertView.findViewById(R.id.textViewOrderIdValue);
            textViewOrderIdValue.setTypeface(Fonts.mavenRegular(context));
            textViewCustomerName = (TextView) convertView.findViewById(R.id.textViewCustomerName);
            textViewCustomerName.setTypeface(Fonts.mavenRegular(context));
            textViewCustomerDeliveryAddress = (TextView) convertView.findViewById(R.id.textViewCustomerDeliveryAddress);
            textViewCustomerDeliveryAddress.setTypeface(Data.latoRegular(context));
            textViewCall = (TextView) convertView.findViewById(R.id.textViewCall);
            textViewCall.setTypeface(Data.latoRegular(context));
            relativeLayoutCall = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutCall);
            rootLinear = (LinearLayout) convertView.findViewById(R.id.rootLinear);
        }
    }

    public interface Callback{
        void onClick(int position, DeliveryInfo deliveryInfo);
        void onCallClick(int position, DeliveryInfo deliveryInfo);
    }

}
