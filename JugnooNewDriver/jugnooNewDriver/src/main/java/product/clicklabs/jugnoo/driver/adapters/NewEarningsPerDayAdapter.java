package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.Item;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneesh on 10/4/15.
 */
public class NewEarningsPerDayAdapter extends RecyclerView.Adapter<NewEarningsPerDayAdapter.rideInfoViewHolder> implements ItemListener{

	private  List<DriverEarningsResponse.Earning> deliveryDetails;
	private Context context;
	private RecyclerView recyclerView;
	private  NewEarningsCallback newEarningsCallback;

	public NewEarningsPerDayAdapter(Context context, List<DriverEarningsResponse.Earning> deliveryDetails,RecyclerView recyclerView,NewEarningsCallback newEarningsCallback) {
		this.deliveryDetails = deliveryDetails;
		this.context = context;
		this.recyclerView = recyclerView;
		this.newEarningsCallback = newEarningsCallback;
	}

	@Override
	public int getItemCount() {
		return deliveryDetails==null?0:deliveryDetails.size();
	}

	@Override
	public void onBindViewHolder(rideInfoViewHolder rideInfoViewHolder, int i) {
		rideInfoViewHolder.relativeLayout.setVisibility(View.VISIBLE);
		rideInfoViewHolder.textViewDayDateVal.setText(deliveryDetails.get(i).getDay()
				+", "+ DateOperations.convertMonthDayViaFormat(deliveryDetails.get(i).getDate()));
		rideInfoViewHolder.textViewDailyValue.setText(context.getResources().getString(R.string.rupee)+deliveryDetails.get(i).getEarnings());

	}

	@Override
	public rideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.layout_item_earning_per_day, viewGroup, false);

		return new rideInfoViewHolder(itemView,this);
	}

	@Override
	public void onClickItem(View parentView, View childView) {
		int position = recyclerView.getChildAdapterPosition(childView);
		rideInfoViewHolder rideInfoViewHolder = (NewEarningsPerDayAdapter.rideInfoViewHolder) recyclerView.getChildViewHolder(parentView);
		if(position!=RecyclerView.NO_POSITION){
			switch (childView.getId()){
				case R.id.relative:
					if(deliveryDetails.get(position).getEarnings() != 0) {
						rideInfoViewHolder.arrow.setVisibility(View.VISIBLE);
						newEarningsCallback.onDailyDetailsClick(deliveryDetails.get(position));
					}
					break;
				default:
						break;
			}


		}
	}

	public class rideInfoViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout relativeLayout;
		protected TextView textViewDayDateVal, textViewDailyValue;
		protected ImageView arrow;
		protected int id;

		public rideInfoViewHolder(View v, final ItemListener itemListener) {
			super(v);
			arrow = (ImageView) v.findViewById(R.id.arrow);
			relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
			textViewDayDateVal = (TextView) v.findViewById(R.id.textViewDayDateVal);
			textViewDailyValue = (TextView) v.findViewById(R.id.textViewDailyValue);
			textViewDayDateVal.setTypeface(Fonts.mavenRegular(context));
			relativeLayout.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			relativeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					itemListener.onClickItem(v,relativeLayout);
				}
			});
			ASSL.DoMagic(relativeLayout);
		}
	}

	public interface NewEarningsCallback{
		void onDailyDetailsClick(DriverEarningsResponse.Earning earning);
	}


}