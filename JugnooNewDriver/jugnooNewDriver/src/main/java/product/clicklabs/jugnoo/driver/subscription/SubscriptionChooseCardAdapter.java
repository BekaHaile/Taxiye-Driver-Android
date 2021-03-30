package product.clicklabs.jugnoo.driver.subscription;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.stripe.StripeUtils;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;

public class SubscriptionChooseCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DriverSplashActivity activity;
    private ArrayList<StripeCardData> subscriptionCardsData;
    private SubscriptionChooseCardFragment fragment;
    private SubscriptionCallback callback;

    public SubscriptionChooseCardAdapter(DriverSplashActivity activity, ArrayList<StripeCardData> subscriptionCardsData, SubscriptionChooseCardFragment frag,SubscriptionCallback callback) {
        this.activity = activity;
        this.subscriptionCardsData = subscriptionCardsData;
        this.fragment = frag;
        this.callback = callback;
    }

    SubscriptionChooseCardViewHolder subscriptionViewHolder = null;

    @Override
    public SubscriptionChooseCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.choose_cards_list_item, viewGroup, false);
        subscriptionViewHolder = new SubscriptionChooseCardViewHolder(view);
        return subscriptionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SubscriptionChooseCardViewHolder) holder).cardText.setText(StripeUtils.getStripeCardDisplayString(activity,subscriptionCardsData.get(position).getLast4()));
        ((SubscriptionChooseCardViewHolder) holder).cardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             callback.onCardSelectedCallback(subscriptionCardsData.get(position).getCardId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionCardsData.size();
    }


    public class SubscriptionChooseCardViewHolder extends RecyclerView.ViewHolder {
        private TextView cardText;


        public SubscriptionChooseCardViewHolder(View itemView) {
            super(itemView);
            cardText = itemView.findViewById(R.id.tv_choose_card);
        }
    }
}
