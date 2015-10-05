package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.R;
import product.clicklabs.jugnoo.datastructure.NearbyDriver;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by socomo20 on 10/4/15.
 */
public class SharingRidesAdapter extends RecyclerView.Adapter<SharingRidesAdapter.SharingRideViewHolder> {

	private ArrayList<SharingRideData> sharingRideDatas;
	private Context context;
	private SharingRideDataAdapterHandler adapterHandler;

	public SharingRidesAdapter(Context context, ArrayList<SharingRideData> sharingRideDatas, SharingRideDataAdapterHandler adapterHandler) {
		this.sharingRideDatas = sharingRideDatas;
		this.context = context;
		this.adapterHandler = adapterHandler;
	}

	@Override
	public int getItemCount() {
		return sharingRideDatas.size();
	}

	@Override
	public void onBindViewHolder(SharingRideViewHolder nearbyDriverViewHolder, int i) {
		SharingRideData srd = sharingRideDatas.get(i);
		if(srd.completed == 1){
			nearbyDriverViewHolder.buttonRideComplete.setVisibility(View.GONE);
			nearbyDriverViewHolder.textRideStatus.setVisibility(View.VISIBLE);
		}
		else{
			nearbyDriverViewHolder.buttonRideComplete.setVisibility(View.VISIBLE);
			nearbyDriverViewHolder.textRideStatus.setVisibility(View.GONE);
		}


		nearbyDriverViewHolder.textViewDateValue.setText(srd.transactionDateTime);
		nearbyDriverViewHolder.linearLayoutNearby.setTag(i);
		nearbyDriverViewHolder.buttonRideComplete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0; i< sharingRideDatas.size(); i++){
					sharingRideDatas.get(i).ticked = false;
				}
				sharingRideDatas.get((int) v.getTag()).ticked = true;
				notifyDataSetChanged();
				adapterHandler.itemClicked(sharingRideDatas.get((int) v.getTag()));
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
				textRideStatus, textViewCustomerPaidText, textViewAccountBalanceText;
		protected Button buttonRideComplete;

		public SharingRideViewHolder(View v) {
			super(v);
			textViewDateValue = (TextView) v.findViewById(R.id.textViewDateValue);
			textViewTimeValue = (TextView) v.findViewById(R.id.textViewTimeValue);
			textViewPhoneValue = (TextView) v.findViewById(R.id.textViewPhoneValue);
			textViewActualFare = (TextView) v.findViewById(R.id.textViewActualFare);
			textViewCustomerPaid = (TextView) v.findViewById(R.id.textViewCustomerPaid);
			textViewAccountBalance = (TextView) v.findViewById(R.id.textViewAccountBalance);
			textRideStatus = (TextView) v.findViewById(R.id.textRideStatus);
			textViewCustomerPaidText = (TextView) v.findViewById(R.id.textViewCustomerPaidtext);
			textViewAccountBalanceText = (TextView) v.findViewById(R.id.textViewBalanceText);
			buttonRideComplete = (Button) v.findViewById(R.id.buttonRideComplete);

			relative.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}
}