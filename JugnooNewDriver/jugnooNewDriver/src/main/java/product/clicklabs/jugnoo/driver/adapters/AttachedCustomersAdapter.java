package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public class AttachedCustomersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private Callback callback;

    public AttachedCustomersAdapter(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public synchronized void notifyList(){
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customer_info, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, 206);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
        CustomerInfo customerInfo = getItem(position);
        ViewHolder holder = (ViewHolder) viewholder;
        holder.linearRoot.setTag(position);
        holder.relativeLayoutCall.setTag(position);

        holder.textViewName.setText(customerInfo.getName());
        holder.textViewAddress.setText(customerInfo.getAddress());
        holder.textViewRideStatus.setText(String.valueOf(customerInfo.getStatus()));

        holder.relativeLayoutCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = (int) v.getTag();
                    callback.onCallUs(position, getItem(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = (int) v.getTag();
                    callback.onClick(position, getItem(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
        public TextView textViewName, textViewAddress, textViewCall, textViewRideStatus;
        public LinearLayout linearRoot;
        public RelativeLayout relativeLayoutCall;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            textViewName = (TextView) convertView.findViewById(R.id.textViewName); textViewName.setTypeface(Fonts.mavenRegular(context));
            textViewAddress = (TextView) convertView.findViewById(R.id.textViewAddress); textViewAddress.setTypeface(Fonts.mavenRegular(context));
            textViewCall = (TextView) convertView.findViewById(R.id.textViewCall); textViewCall.setTypeface(Fonts.mavenRegular(context));
            textViewRideStatus = (TextView) convertView.findViewById(R.id.textViewRideStatus); textViewRideStatus.setTypeface(Fonts.mavenRegular(context));

            linearRoot = (LinearLayout) convertView.findViewById(R.id.linearRoot);
            relativeLayoutCall = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutCall);
        }
    }

    public interface Callback{
        void onClick(int position, CustomerInfo customerInfo);
        void onCallUs(int position, CustomerInfo customerInfo);
    }

}
