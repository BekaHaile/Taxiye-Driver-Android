package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.DailyRideDetailsActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by gurmail on 19/05/16.
 */
public class DailyRideDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DailyRideDetailsActivity activity;
    private ArrayList<DailyEarningItem> items;
    private Callback callback;
    protected String editTextStr = "";
    DailyEarningResponse dailyEarningResponse;

    public DailyRideDetailsAdapter(DailyRideDetailsActivity activity, ArrayList<DailyEarningItem> items, Callback callback) {
        this.activity = activity;
        this.items = items;
        this.callback = callback;
    }

    public void setList(ArrayList<DailyEarningItem> slots, DailyEarningResponse dailyEarningResponse){
        this.dailyEarningResponse = dailyEarningResponse;
        this.items = slots;
        notifyDataSetChanged();
    }

	

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(viewType == ViewType.TOTAL_AMNT.getOrdinal()) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_total_amnt_header, parent, false);
			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);
			ASSL.DoMagic(v);
			return new ViewHolderTotalAmount(v, activity);

        } else if(viewType == ViewType.EARNING_PARAM.getOrdinal()){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ride_param, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderRideParam(v, activity);

        } else if(viewType == ViewType.TOTAL_VALUES.getOrdinal()){
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_total_vlaues, parent, false);
			RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(layoutParams);
			ASSL.DoMagic(v);
			return new ViewHolderHeader(v, activity);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ride_info, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderRide(v, activity);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        try {
			DailyEarningItem item = items.get(position);
            if(holder instanceof ViewHolderRide){

				((ViewHolderRide)holder).textViewInfoText.setText(item.getTime());
				((ViewHolderRide)holder).textViewInfoValue.setText(""+item.getEarning());
				((ViewHolderRide)holder).linear.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						try {
							int pos = (int) v.getTag();
							callback.onRideClick(pos, items.get(pos).getExtras());
							notifyDataSetChanged();

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

            } else if(holder instanceof ViewHolderHeader) {

				if(dailyEarningResponse != null) {
					((ViewHolderHeader) holder).textViewActualFareValue.setText(""+dailyEarningResponse.getEarnings());
					((ViewHolderHeader) holder).textViewCustomerPaid.setText(""+dailyEarningResponse.getPaidByCustomer());
					((ViewHolderHeader) holder).onlineTimeValue.setText(""+dailyEarningResponse.getTimeOnline());
					((ViewHolderHeader) holder).textViewBankDepositeValue.setText(""+dailyEarningResponse.getAccount());
					((ViewHolderHeader) holder).textViewTripCount.setText(""+dailyEarningResponse.getTotalTrips());
				}

            } else if(holder instanceof ViewHolderTotalAmount) {
				if(dailyEarningResponse != null) {
					((ViewHolderTotalAmount) holder).dateTimeValue.setText("" + dailyEarningResponse.getDay() + ", " + dailyEarningResponse.getDate());
					((ViewHolderTotalAmount) holder).textViewEarningsValue.setText("" + dailyEarningResponse.getEarnings());
				}

			} else if(holder instanceof ViewHolderRideParam){
                final DailyEarningItem param = items.get(position);
                ((ViewHolderRideParam)holder).textViewInfoText.setText(param.getText());
                ((ViewHolderRideParam)holder).textViewInfoValue.setText(""+param.getValue());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType().getOrdinal();
    }

    static class ViewHolderRideParam extends RecyclerView.ViewHolder {
        public LinearLayout linearLayoutRideItem;
        public TextView textViewInfoText, textViewInfoValue;
        public ViewHolderRideParam(View itemView, Context context) {
            super(itemView);
			linearLayoutRideItem = (LinearLayout) itemView.findViewById(R.id.linearLayoutRideItem);
			textViewInfoText = (TextView) itemView.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			textViewInfoValue = (TextView)itemView.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderRide extends RecyclerView.ViewHolder {
		public LinearLayout linear;
		public TextView textViewInfoText, textViewInfoValue;
        public ViewHolderRide(View itemView, Context context) {
            super(itemView);
			linear = (LinearLayout)itemView.findViewById(R.id.linear);
			textViewInfoText = (TextView) itemView.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			textViewInfoValue = (TextView)itemView.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderTotalAmount extends RecyclerView.ViewHolder {
		public LinearLayout linearLayout;
		public TextView dateTimeValue, textViewEarningsValue;
        public ViewHolderTotalAmount(View itemView, Context context) {
            super(itemView);
			linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
			dateTimeValue = (TextView) itemView.findViewById(R.id.dateTimeValue);
			dateTimeValue.setTypeface(Fonts.mavenRegular(context));
			textViewEarningsValue = (TextView)itemView.findViewById(R.id.textViewEarningsValue);
			textViewEarningsValue.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        public TextView textViewActualFareText, textViewActualFareValue, textViewTripCount,
				textViewCustomerPaidText, textViewCustomerPaid, rideTimeValueText, textViewTrips,
				textViewBankDeposite, textViewBankDepositeValue, onlineTimeValue, textViewTripsText;


        public ViewHolderHeader(View itemView, Context context) {
            super(itemView);
			linear = (LinearLayout) itemView.findViewById(R.id.linear);

			textViewActualFareText = (TextView)itemView.findViewById(R.id.textViewActualFareText);
			textViewActualFareText.setTypeface(Fonts.mavenRegular(context));
			textViewActualFareValue = (TextView)itemView.findViewById(R.id.textViewActualFareValue);
			textViewActualFareValue.setTypeface(Fonts.mavenRegular(context));

			textViewCustomerPaidText = (TextView)itemView.findViewById(R.id.textViewCustomerPaidText);
			textViewCustomerPaidText.setTypeface(Fonts.mavenRegular(context));
			textViewCustomerPaid = (TextView)itemView.findViewById(R.id.textViewCustomerPaid);
			textViewCustomerPaid.setTypeface(Fonts.mavenRegular(context));

			textViewBankDeposite = (TextView)itemView.findViewById(R.id.textViewBankDeposite);
			textViewBankDeposite.setTypeface(Fonts.mavenRegular(context));
			textViewBankDepositeValue = (TextView)itemView.findViewById(R.id.textViewBankDepositeValue);
			textViewBankDepositeValue.setTypeface(Fonts.mavenRegular(context));

			onlineTimeValue = (TextView)itemView.findViewById(R.id.onlineTimeValue);
			onlineTimeValue.setTypeface(Fonts.mavenRegular(context));
			rideTimeValueText = (TextView)itemView.findViewById(R.id.rideTimeValueText);
			rideTimeValueText.setTypeface(Fonts.mavenRegular(context));

			textViewTripCount = (TextView)itemView.findViewById(R.id.textViewTripCount);
			textViewTripCount.setTypeface(Fonts.mavenRegular(context));
			textViewTrips = (TextView)itemView.findViewById(R.id.textViewTrips);
			textViewTrips.setTypeface(Fonts.mavenRegular(context));

			textViewTripsText = (TextView)itemView.findViewById(R.id.textViewTripsText);
			textViewTripsText.setTypeface(Fonts.mavenRegular(context));

        }
    }

    public interface Callback{
        void onRideClick(int position, Object extras);
    }

    public enum ViewType {
        TOTAL_AMNT(0),
        EARNING_PARAM(1),
        TOTAL_VALUES(2),
        RIDE_INFO(3)
        ;

        private int ordinal;

        ViewType(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

}
