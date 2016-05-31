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


public class DeliveryInfoAdapter extends RecyclerView.Adapter<DeliveryInfoAdapter.ViewHolder> {

    private Activity activity;
    private Callback callback;
    private ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();

    public DeliveryInfoAdapter(Activity activity, ArrayList<DeliveryInfo> deliveryInfos, Callback callback) {
        this.activity = activity;
        this.callback = callback;
        this.deliveryInfos = deliveryInfos;
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

            holder.textViewCustomerName.setText("");
            holder.textViewCustomerDeliveryAddress.setText("");
            holder.textViewShowDistance.setText("");

            holder.markDeliveryRl.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

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
        public TextView textViewCustomerName, textViewCustomerDeliveryAddress, textViewShowDistance;
        public RelativeLayout driverPassengerCallRl, markDeliveryRl;
        public LinearLayout rootLinear;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewCustomerName = (TextView) convertView.findViewById(R.id.textViewCustomerName);
            textViewCustomerName.setTypeface(Data.latoRegular(context));
            textViewCustomerDeliveryAddress = (TextView) convertView.findViewById(R.id.textViewCustomerDeliveryAddress);
            textViewCustomerDeliveryAddress.setTypeface(Data.latoRegular(context));
            textViewShowDistance = (TextView) convertView.findViewById(R.id.textViewShowDistance);
            textViewShowDistance.setTypeface(Data.latoRegular(context));
            driverPassengerCallRl = (RelativeLayout) convertView.findViewById(R.id.driverPassengerCallRl);
            markDeliveryRl = (RelativeLayout) convertView.findViewById(R.id.markDeliveryRl);
            rootLinear = (LinearLayout) convertView.findViewById(R.id.rootLinear);
        }
    }

    public interface Callback{
        void onClick(int position, DeliveryInfo deliveryInfo);
    }

}
