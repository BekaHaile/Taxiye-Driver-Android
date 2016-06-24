package product.clicklabs.jugnoo.driver.home.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customer_brief_info, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(360, 80);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CustomerInfo customerInfo = getItem(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.relative.setTag(position);
        holder.driverPassengerName.setText(customerInfo.getName());

        holder.relative.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        if(Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(customerInfo.getEngagementId()))&& (getItemCount() >1)){
            Utils.setTextColor(holder.driverPassengerName, customerInfo.getColor(),
                    activity.getResources().getColor(R.color.new_orange));
            Utils.setDrawableColor(holder.imageViewCustomerImage, customerInfo.getColor(),
                    activity.getResources().getColor(R.color.new_orange));
            holder.imageViewDisable.setVisibility(View.GONE);
        } else {
            holder.relative.setBackgroundColor(activity.getResources().getColor(R.color.black_grey));
            holder.driverPassengerName.setTextColor(activity.getResources().getColor(R.color.white));
            holder.imageViewCustomerImage.setBackgroundResource(R.drawable.superhappy_face);
            holder.imageViewDisable.setVisibility(View.VISIBLE);
        }

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = (int) v.getTag();
                    callback.onClick(position, getItem(position));
                    notifyList();
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
        public TextView driverPassengerName;
        public RelativeLayout relative;
        public ImageView imageViewDisable, imageViewCustomerImage;
        public ViewHolder(View convertView, Activity context) {
            super(convertView);
            driverPassengerName = (TextView) convertView.findViewById(R.id.driverPassengerName);
            driverPassengerName.setTypeface(Fonts.mavenRegular(context), Typeface.BOLD);
            relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            imageViewDisable = (ImageView) convertView.findViewById(R.id.imageViewDisable);
            imageViewCustomerImage = (ImageView) convertView.findViewById(R.id.imageViewCustomerImage);
        }
    }

    public interface Callback{
        void onClick(int position, CustomerInfo customerInfo);
    }

}
