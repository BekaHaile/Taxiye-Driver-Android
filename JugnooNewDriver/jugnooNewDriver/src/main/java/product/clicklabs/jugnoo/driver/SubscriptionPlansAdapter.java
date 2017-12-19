package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.PlanDetails;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 14/12/17.
 */

class SubscriptionPlansAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private JugnooSubscriptionActivity activity;
    private ArrayList<PlanDetails> planDetails;
    private RecyclerView recyclerView;
    private int currentSelectedIndex;
    private LayoutInflater inflater;
    private boolean isActiveSubscriptionArray;

    public SubscriptionPlansAdapter(JugnooSubscriptionActivity activity, ArrayList<PlanDetails> planDetails,RecyclerView recyclerView,boolean isActiveSubscriptionArray) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.planDetails = planDetails;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isActiveSubscriptionArray = isActiveSubscriptionArray;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionPlansView(inflater.inflate(R.layout.list_item_subscription_offers,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubscriptionPlansView subscriptionPlansView = (SubscriptionPlansView) holder;
        subscriptionPlansView.tvValidity.setText(String.format("%s%s", activity.getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(planDetails.get(position).getAmount())));



        subscriptionPlansView.radioButton.setText(planDetails.get(position).getValidityDays()+ " "+  activity.getString(R.string.days));

       /* if(planDetails.get(position).getValidityDays()>1){
        }else{
            subscriptionPlansView.radioButton.setText(planDetails.get(position).getValidityDays()+ " " + activity.getString(R.string.day));
        }
*/
        if(!isActiveSubscriptionArray){
            if(planDetails.get(position).getIsSelected()){
                subscriptionPlansView.radioButton.setChecked(true);
                currentSelectedIndex = position;
            }else{
                subscriptionPlansView.radioButton.setChecked(false);
            }
        }else{
            subscriptionPlansView.radioButton.setChecked(true);
        }



    }

    @Override
    public int getItemCount() {
        return planDetails == null?0:planDetails.size();
    }

    private class SubscriptionPlansView extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private TextView tvValidity;
        private View itemView;
        private View.OnClickListener rootOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActiveSubscriptionArray) {
                    int pos = recyclerView.getChildAdapterPosition(itemView);
                    if(pos!=RecyclerView.NO_POSITION){
                        if(currentSelectedIndex>=0 && currentSelectedIndex<planDetails.size()){
                            planDetails.get(currentSelectedIndex).setIsSelected(0);

                        }
                        planDetails.get(pos).setIsSelected(1);
                        activity.setUpBreakUpData(planDetails.get(pos).getAmount());

                        notifyItemChanged(currentSelectedIndex);
                        notifyItemChanged(pos);
                    }
                }
            }
        };

        public SubscriptionPlansView(View itemView) {
            super(itemView);
            this.itemView = itemView;
            radioButton  = (RadioButton) itemView.findViewById(R.id.rb_plan_description);
            tvValidity  = (TextView) itemView.findViewById(R.id.tv_plan_validity);
            itemView.setClickable(!isActiveSubscriptionArray);
            itemView.setOnClickListener(isActiveSubscriptionArray?null:rootOnClickListener);
        }

    }

    public PlanDetails getCurrentSelectedPlan(){
        if(currentSelectedIndex>=0 && currentSelectedIndex<planDetails.size()){
            return planDetails.get(currentSelectedIndex);
        }else{
            return null;

        }
    }
}
