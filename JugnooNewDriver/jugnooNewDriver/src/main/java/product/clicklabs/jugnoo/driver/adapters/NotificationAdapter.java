package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.NotificationData;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;


/**
 * Created by Ankit on 7/17/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Activity activity;
    private int rowLayout;
    NotificationData notification;
    private ArrayList<NotificationData> notificationList = new ArrayList<>();

    public NotificationAdapter(ArrayList<NotificationData> notificationList, Activity activity, int rowLayout) {
        this.notificationList = notificationList;
        this.activity = activity;
        this.rowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(layoutParams);

        ASSL.DoMagic(v);
        return new ViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        notification = notificationList.get(position);

        holder.descriptionTxt.setText(notification.getMessage());
        holder.textViewTime.setText(DateOperations
                .convertDateViaFormat(DateOperations.utcToLocal(notification.getTimePushArrived())));
        holder.linearRoot.setTag(position);

        try {
            if(notification.getNotificationImage().equalsIgnoreCase("")){
                holder.linearLayoutNotificationImage.setVisibility(View.GONE);
            }
            else{
                holder.linearLayoutNotificationImage.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(notification.getNotificationImage())
                        .placeholder(R.drawable.ic_notification_placeholder)
                        .error(R.drawable.ic_notification_placeholder)
                        .into(holder.imageViewNotification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    int position = (int) v.getTag();
//                    notificationList.get(position).setExpanded(!notificationList.get(position).isExpanded());
//                    notifyItemChanged(position);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

	}

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearRoot, linearLayoutNotificationImage;
        public ImageView imageViewNotification;
        public TextView descriptionTxt, textViewTime;
        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            linearRoot = (LinearLayout) itemView.findViewById(R.id.linearRoot);
            linearLayoutNotificationImage = (LinearLayout) itemView.findViewById(R.id.linearLayoutNotificationImage);
            imageViewNotification = (ImageView)itemView.findViewById(R.id.imageViewNotification);
            descriptionTxt = (TextView) itemView.findViewById(R.id.description);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);

            descriptionTxt.setTypeface(Data.latoRegular(activity));
            textViewTime.setTypeface(Data.latoRegular(activity));
        }
    }
}
