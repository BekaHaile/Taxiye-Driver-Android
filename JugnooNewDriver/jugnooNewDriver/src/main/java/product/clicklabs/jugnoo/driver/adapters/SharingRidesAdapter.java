package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class SharingRidesAdapter extends RecyclerView.Adapter<SharingRidesAdapter.SharingRideViewHolder> {

	private ArrayList<SharingRideData> sharingRideDatas;
	private Context context;
	private SharingRidesAdapterHandler adapterHandler;

	public SharingRidesAdapter(Context context, ArrayList<SharingRideData> sharingRideDatas, SharingRidesAdapterHandler adapterHandler) {
		this.sharingRideDatas = sharingRideDatas;
		this.context = context;
		this.adapterHandler = adapterHandler;
	}

	@Override
	public int getItemCount() {
		return sharingRideDatas.size();
	}

	@Override
	public void onBindViewHolder(SharingRideViewHolder sharingRideViewHolder, int i) {
		SharingRideData srd = sharingRideDatas.get(i);

		sharingRideViewHolder.textViewDateValue.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewTimeValue.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewPhoneValue.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewActualFare.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		sharingRideViewHolder.textViewCustomerPaid.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		sharingRideViewHolder.textViewAccountBalance.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		sharingRideViewHolder.textRideStatus.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewCustomerPaidText.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewAccountBalanceText.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.textViewRideID.setTypeface(Data.latoRegular(context));
		sharingRideViewHolder.buttonRideComplete.setTypeface(Data.latoRegular(context));


			if(srd.completed == 1){
			sharingRideViewHolder.buttonRideComplete.setVisibility(View.GONE);
			sharingRideViewHolder.textRideStatus.setVisibility(View.VISIBLE);
			sharingRideViewHolder.relative.setBackgroundResource(R.drawable.background_white_lightdark);
		}
		else{
			sharingRideViewHolder.buttonRideComplete.setVisibility(View.VISIBLE);
			sharingRideViewHolder.textRideStatus.setVisibility(View.GONE);
			sharingRideViewHolder.relative.setBackgroundResource(R.drawable.bg_white);
		}

		sharingRideViewHolder.textViewRideID.setText(context.getResources().getString(R.string.ride_id)+": "+srd.sharingEngagementId);
		sharingRideViewHolder.textViewDateValue.setText(DateOperations.getDate(DateOperations.utcToLocalTZ(srd.transactionDateTime)));
		sharingRideViewHolder.textViewTimeValue.setText(DateOperations.getTimeAMPM(DateOperations.utcToLocalTZ(srd.transactionDateTime)));

		sharingRideViewHolder.textViewPhoneValue.setText(Utils.hidePhoneNoString(srd.customerPhoneNumber));
		sharingRideViewHolder.textViewActualFare.setText(Utils.formatCurrencyValue(srd.getCurrency(),srd.actualFare));

		if(srd.customerPaid > 0){
			sharingRideViewHolder.textViewCustomerPaid.setText(Utils.formatCurrencyValue(srd.getCurrency(),srd.customerPaid));
			sharingRideViewHolder.textViewCustomerPaidText.setText(context.getResources().getString(R.string.take_cash));
			sharingRideViewHolder.textViewCustomerPaid.setTextColor(context.getResources().getColor(R.color.red_status));
			sharingRideViewHolder.textViewCustomerPaidText.setTextColor(context.getResources().getColor(R.color.red_status));
		}
		else {
			sharingRideViewHolder.textViewCustomerPaid.setText(Utils.formatCurrencyValue(srd.getCurrency(),Math.abs(srd.customerPaid)));
			sharingRideViewHolder.textViewCustomerPaidText.setText(context.getResources().getString(R.string.take_cash));
			sharingRideViewHolder.textViewCustomerPaid.setTextColor(context.getResources().getColor(R.color.black));
			sharingRideViewHolder.textViewCustomerPaidText.setTextColor(context.getResources().getColor(R.color.black));
		}

		if(srd.accountBalance > 0){
			sharingRideViewHolder.textViewAccountBalance.setText(Utils.formatCurrencyValue(srd.getCurrency(),Math.abs(srd.accountBalance)));
			sharingRideViewHolder.textViewAccountBalanceText.setText(context.getResources().getString(R.string.account));
			sharingRideViewHolder.textViewAccountBalanceText.setTextColor(context.getResources().getColor(R.color.green_status));
			sharingRideViewHolder.textViewAccountBalance.setTextColor(context.getResources().getColor(R.color.green_status));
		}
		else {
			if(srd.accountBalance == 0){
				sharingRideViewHolder.textViewAccountBalance.setText(Utils.formatCurrencyValue(srd.getCurrency(),srd.accountBalance));
				sharingRideViewHolder.textViewAccountBalanceText.setText(context.getResources().getString(R.string.account));
				sharingRideViewHolder.textViewAccountBalanceText.setTextColor(context.getResources().getColor(R.color.black));
				sharingRideViewHolder.textViewAccountBalance.setTextColor(context.getResources().getColor(R.color.black));
			}
			else{
				sharingRideViewHolder.textViewAccountBalance.setText(Utils.formatCurrencyValue(srd.getCurrency(),srd.accountBalance));
				sharingRideViewHolder.textViewAccountBalanceText.setText("(-)"+context.getResources().getString(R.string.account));
				sharingRideViewHolder.textViewAccountBalanceText.setTextColor(context.getResources().getColor(R.color.red_status));
				sharingRideViewHolder.textViewAccountBalance.setTextColor(context.getResources().getColor(R.color.red_status));
			}

		}



		sharingRideViewHolder.buttonRideComplete.setTag(i);
		sharingRideViewHolder.buttonRideComplete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sharingRideDatas.get((int) v.getTag()).completed = 1;
				notifyDataSetChanged();
				adapterHandler.okClicked(sharingRideDatas.get((int) v.getTag()));
			}
		});

	}

	@Override
	public SharingRideViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_jugnoo_share, viewGroup, false);

		return new SharingRideViewHolder(itemView);
	}

	public class SharingRideViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout relative;
		protected TextView textViewDateValue, textViewTimeValue, textViewPhoneValue,
				textViewActualFare, textViewCustomerPaid, textViewAccountBalance,
				textRideStatus, textViewCustomerPaidText, textViewAccountBalanceText, textViewRideID;
		protected Button buttonRideComplete;

		public SharingRideViewHolder(View v) {
			super(v);
			relative = (RelativeLayout)v.findViewById(R.id.relative);
			textViewDateValue = (TextView) v.findViewById(R.id.textViewDateValue);
			textViewTimeValue = (TextView) v.findViewById(R.id.textViewTimeValue);
			textViewPhoneValue = (TextView) v.findViewById(R.id.textViewPhoneValue);
			textViewActualFare = (TextView) v.findViewById(R.id.textViewActualFare);
			textViewCustomerPaid = (TextView) v.findViewById(R.id.textViewCustomerPaid);
			textViewAccountBalance = (TextView) v.findViewById(R.id.textViewAccountBalance);
			textRideStatus = (TextView) v.findViewById(R.id.textRideStatus);
			textViewCustomerPaidText = (TextView) v.findViewById(R.id.textViewCustomerPaidtext);
			textViewAccountBalanceText = (TextView) v.findViewById(R.id.textViewBalanceText);
			textViewRideID = (TextView) v.findViewById(R.id.textViewRideID);
			buttonRideComplete = (Button) v.findViewById(R.id.buttonRideComplete);

			relative.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}
}