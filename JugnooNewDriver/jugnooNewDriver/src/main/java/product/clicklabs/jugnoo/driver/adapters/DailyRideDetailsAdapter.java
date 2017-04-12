package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.DailyRideDetailsActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponseNew;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by gurmail on 19/05/16.
 */
public class DailyRideDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DailyRideDetailsActivity activity;
    private ArrayList<DailyEarningItem> items;
    private Callback callback;
    protected String editTextStr = "";
    DailyEarningResponse dailyEarningResponse;
	InvoiceDetailResponseNew invoiceDetailResponseNew;

    public DailyRideDetailsAdapter(DailyRideDetailsActivity activity, ArrayList<DailyEarningItem> items, Callback callback) {
        this.activity = activity;
        this.items = items;
        this.callback = callback;
    }

    public void setList(ArrayList<DailyEarningItem> slots, DailyEarningResponse dailyEarningResponse, InvoiceDetailResponseNew invoiceDetailResponseNew){
        this.dailyEarningResponse = dailyEarningResponse;
        this.items = slots;
		this.invoiceDetailResponseNew = invoiceDetailResponseNew;
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ride_history_new, parent, false);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            v.setLayoutParams(layoutParams);
            ASSL.DoMagic(v);
            return new ViewHolderRide(v, activity);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        try {
			final DailyEarningItem item = items.get(position);
            if(holder instanceof ViewHolderRide){

				if (item.getTime() != null) {
					((ViewHolderRide) holder).textViewInfoText.setText(item.getTime());
				}else {
					((ViewHolderRide) holder).textViewInfoText.setVisibility(View.GONE);
				}
				((ViewHolderRide)holder).textViewInfoDate.setText(DateOperations.convertDateToDay(item.getDate()) +", "+
						DateOperations.convertMonthDayViaFormat(item.getDate()));
				((ViewHolderRide)holder).textViewInfoValue.setText(Utils.getAbsAmount(activity, item.getEarning()));


				if(item.getStatus()!= null && (item.getStatus().equalsIgnoreCase("Ride Cancelled")
						|| item.getStatus().equalsIgnoreCase("Delivery Cancelled"))){
					((ViewHolderRide)holder).textViewStatus.setVisibility(View.VISIBLE);
					((ViewHolderRide)holder).textViewStatus.setText(activity.getResources().getString(R.string.cancelled));
					((ViewHolderRide)holder).textViewStatus.setTextColor(activity.getResources().getColor(R.color.red_status_v2));

				} else {
					((ViewHolderRide)holder).textViewStatus.setVisibility(View.GONE);
					((ViewHolderRide)holder).textViewInfoText.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
					((ViewHolderRide)holder).textViewInfoValue.setTextColor(activity.getResources().getColor(R.color.black_text_v2));
				}

				if(item.getType() ==3 || item.getType() == 4){
					((ViewHolderRide)holder).textViewType.setVisibility(View.VISIBLE);
					((ViewHolderRide)holder).textViewType.setText(activity.getResources().getString(R.string.delivery));
				} else if(item.getType() ==2){
					((ViewHolderRide)holder).textViewType.setVisibility(View.VISIBLE);
					((ViewHolderRide)holder).textViewType.setText(activity.getResources().getString(R.string.pool));
				} else {
					((ViewHolderRide)holder).textViewType.setVisibility(View.GONE);
				}



				if(invoiceDetailResponseNew != null){
					((ViewHolderRide)holder).relativeBelow.setVisibility(View.GONE);
				}

//				if(item.getEarning() > 0 || invoiceDetailResponseNew != null) {
//					((ViewHolderRide)holder).imageViewArrow.setVisibility(View.VISIBLE);
//				} else {
//					((ViewHolderRide)holder).imageViewArrow.setVisibility(View.GONE);
//				}


                ((ViewHolderRide)holder).linearLayoutRideItem.setTag(position);
				((ViewHolderRide)holder).linearLayoutRideItem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							int pos = (int) v.getTag();
							MyApplication.getInstance().logEvent(FirebaseEvents.DAILY_EARNING+"_"+FirebaseEvents.TRIP,null);
							callback.onRideClick(pos, items.get(pos).getExtras(), items.get(pos).getDate());
							notifyDataSetChanged();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

            } else if(holder instanceof ViewHolderHeader) {
				int totalRides = 0;
				if(dailyEarningResponse != null) {
					((ViewHolderHeader) holder).textViewActualFareValue.setText(Utils.getAbsAmount(activity, dailyEarningResponse.getEarnings()));
					((ViewHolderHeader) holder).textViewCustomerPaid.setText(Utils.getAbsAmount(activity, dailyEarningResponse.getPaidByCustomer()));
					((ViewHolderHeader) holder).onlineTimeValue.setText(""+dailyEarningResponse.getTimeOnline()
							+" "+ activity.getResources().getString(R.string.km));
					((ViewHolderHeader) holder).textViewBankDepositeValue.setText(Utils.getAbsAmount(activity, dailyEarningResponse.getAccount()));

					totalRides = dailyEarningResponse.getTotalTrips() - dailyEarningResponse.getTotalDelivery();
					((ViewHolderHeader) holder).textViewTripCount.setText(""+totalRides+" "+ activity.getResources().getString(R.string.rides));
					((ViewHolderHeader) holder).textViewDeliveryCount.setText(""+dailyEarningResponse.getTotalDelivery()+" "+ activity.getResources().getString(R.string.deliveries));
					((ViewHolderHeader) holder).textViewTripsText.setText(activity.getResources().getString(R.string.trips));
					if(dailyEarningResponse.getTrips().size() <= 0){
						((ViewHolderHeader) holder).textViewNoData.setText(activity.getResources().getString(R.string.no_rides_currently));
					}else {
						((ViewHolderHeader) holder).textViewNoData.setVisibility(View.GONE);
					}
				} else if(invoiceDetailResponseNew != null) {
					((ViewHolderHeader) holder).textViewActualFareValue.setText(Utils.getAbsAmount(activity, invoiceDetailResponseNew.getEarnings()));
					((ViewHolderHeader) holder).textViewCustomerPaid.setText(Utils.getAbsAmount(activity, invoiceDetailResponseNew.getPaidUsingCash()));
					((ViewHolderHeader) holder).onlineTimeValue.setText(""+invoiceDetailResponseNew.getTotalDistanceTravelled()
							+" "+ activity.getResources().getString(R.string.km));
					((ViewHolderHeader) holder).textViewBankDepositeValue.setText(Utils.getAbsAmount(activity, invoiceDetailResponseNew.getAccount()));
					((ViewHolderHeader) holder).textViewTripCount.setText(""+invoiceDetailResponseNew.getTotalTrips());

					totalRides = invoiceDetailResponseNew.getTotalTrips() - invoiceDetailResponseNew.getTotalDelivery();
					((ViewHolderHeader) holder).textViewTripCount.setText(""+totalRides+" "+ activity.getResources().getString(R.string.rides));
					((ViewHolderHeader) holder).textViewDeliveryCount.setText(""+invoiceDetailResponseNew.getTotalDelivery()+" "+ activity.getResources().getString(R.string.deliveries));

					((ViewHolderHeader) holder).textViewTripsText.setText(activity.getResources().getString(R.string.daily_breakup));
					if(invoiceDetailResponseNew.getDailyBreakup().size() <= 0){
						((ViewHolderHeader) holder).textViewNoData.setText(activity.getResources().getString(R.string.no_invoice_currently));
					}else {
						((ViewHolderHeader) holder).textViewNoData.setVisibility(View.GONE);
					}
				}

            } else if(holder instanceof ViewHolderTotalAmount) {
				if(dailyEarningResponse != null) {
					((ViewHolderTotalAmount) holder).dateTimeValue.setText("" + dailyEarningResponse.getDay() + ", " + DateOperations.convertMonthDayViaFormat(dailyEarningResponse.getDate()));
					((ViewHolderTotalAmount) holder).textViewEarningsValue.setText(Utils.getAbsAmount(activity, dailyEarningResponse.getEarnings()));

				} else if(invoiceDetailResponseNew != null) {
					((ViewHolderTotalAmount) holder).dateTimeValue.setText(invoiceDetailResponseNew.getPeriod());
					((ViewHolderTotalAmount) holder).textViewEarningsValue.setText(Utils.getAbsAmount(activity, invoiceDetailResponseNew.getEarnings()));
					((ViewHolderTotalAmount) holder).textViewInvoiceStatus.setText(invoiceDetailResponseNew.getInvoiceStatus());
				}

			} else if(holder instanceof ViewHolderRideParam){
                final DailyEarningItem param = items.get(position);
                ((ViewHolderRideParam)holder).textViewInfoText.setText(param.getText());
                ((ViewHolderRideParam)holder).textViewInfoValue.setText(Utils.getAbsWithDecimalAmount(activity, param.getValue()));

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
		protected LinearLayout linearLayoutRideItem;
		protected TextView textViewInfoText, textViewInfoValue, textViewStatus, textViewType, textViewInfoDate;
		protected ImageView imageViewArrow;
		protected RelativeLayout relativeBelow;
        public ViewHolderRide(View v, Context context) {
            super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			relativeBelow = (RelativeLayout)v.findViewById(R.id.relativeBelow);
			imageViewArrow = (ImageView)v.findViewById(R.id.imageViewArrow);
			textViewInfoText = (TextView) v.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			textViewInfoValue = (TextView) v.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(context));
			textViewStatus = (TextView) v.findViewById(R.id.textViewStatus);
			textViewStatus.setTypeface(Fonts.mavenRegular(context));
			textViewType = (TextView) v.findViewById(R.id.textViewType);
			textViewType.setTypeface(Fonts.mavenRegular(context));
			textViewInfoDate = (TextView) v.findViewById(R.id.textViewInfoDate);
			textViewInfoDate.setTypeface(Fonts.mavenRegular(context));
        }
    }

    static class ViewHolderTotalAmount extends RecyclerView.ViewHolder {
		public LinearLayout linearLayout;
		public TextView dateTimeValue, textViewEarningsValue, textViewEarningsText, textViewInvoiceStatus;
        public ViewHolderTotalAmount(View itemView, Context context) {
            super(itemView);
			linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
			dateTimeValue = (TextView) itemView.findViewById(R.id.dateTimeValue);
			dateTimeValue.setTypeface(Fonts.mavenRegular(context));
			textViewEarningsValue = (TextView)itemView.findViewById(R.id.textViewEarningsValue);
			textViewEarningsValue.setTypeface(Fonts.mavenRegular(context));
			textViewEarningsText = (TextView)itemView.findViewById(R.id.textViewEarningsText);
			textViewEarningsText.setTypeface(Fonts.mavenRegular(context));
			textViewInvoiceStatus = (TextView)itemView.findViewById(R.id.textViewInvoiceStatus);
			textViewInvoiceStatus.setTypeface(Fonts.mavenBold(context));
        }
    }

    static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public LinearLayout linear;
        public TextView textViewActualFareText, textViewActualFareValue, textViewTripCount,
				textViewCustomerPaidText, textViewCustomerPaid, rideTimeValueText, textViewDeliveryCount,
				textViewBankDeposite, textViewBankDepositeValue, onlineTimeValue, textViewTripsText, textViewNoData;


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
			textViewBankDeposite.setTypeface(Data.latoRegular(context), Typeface.BOLD);
			textViewBankDepositeValue = (TextView)itemView.findViewById(R.id.textViewBankDepositeValue);
			textViewBankDepositeValue.setTypeface(Fonts.mavenBold(context));

			onlineTimeValue = (TextView)itemView.findViewById(R.id.onlineTimeValue);
			onlineTimeValue.setTypeface(Fonts.mavenRegular(context));
			rideTimeValueText = (TextView)itemView.findViewById(R.id.rideTimeValueText);
			rideTimeValueText.setTypeface(Fonts.mavenRegular(context));

			textViewTripCount = (TextView)itemView.findViewById(R.id.textViewTripCount);
			textViewTripCount.setTypeface(Fonts.mavenRegular(context));
			textViewDeliveryCount = (TextView)itemView.findViewById(R.id.textViewDeliveryCount);
			textViewDeliveryCount.setTypeface(Fonts.mavenRegular(context));

			textViewTripsText = (TextView)itemView.findViewById(R.id.textViewTripsText);
			textViewTripsText.setTypeface(Fonts.mavenRegular(context));
			textViewNoData = (TextView)itemView.findViewById(R.id.textViewNoData);
			textViewNoData.setTypeface(Fonts.mavenRegular(context));

        }
    }

    public interface Callback{
        void onRideClick(int position, InfoTileResponse.Tile.Extras extras, String date);
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
