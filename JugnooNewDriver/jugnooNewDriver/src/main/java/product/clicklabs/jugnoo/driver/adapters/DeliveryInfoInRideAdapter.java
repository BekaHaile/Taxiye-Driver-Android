package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneesh on 10/4/15.
 */
public class DeliveryInfoInRideAdapter extends RecyclerView.Adapter<DeliveryInfoInRideAdapter.rideInfoViewHolder> {

	private List<DeliveryInfoInRideDetails.DeliveryDatum> deliveryDetails;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public DeliveryInfoInRideAdapter(Context context, List<DeliveryInfoInRideDetails.DeliveryDatum> deliveryDetails) {
		this.deliveryDetails = deliveryDetails;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		return deliveryDetails.size();
	}

	@Override
	public void onBindViewHolder(rideInfoViewHolder rideInfoViewHolder, int i) {
		final DeliveryInfoInRideDetails.DeliveryDatum itr = deliveryDetails.get(i);

		rideInfoViewHolder.textViewCustomerNameValue.setTypeface(Fonts.mavenRegular(context));
		rideInfoViewHolder.textViewCustomerDeliveryAddressValue.setTypeface(Fonts.mavenRegular(context));


		if(itr.getName() != null){
			rideInfoViewHolder.textViewCustomerNameValue.setText(itr.getName());
		} else {
			rideInfoViewHolder.textViewCustomerNameValue.setVisibility(View.GONE);
		}
		rideInfoViewHolder.textViewCustomerDeliveryAddressValue.setText(itr.getAddress());

		if (i == 0) {
			rideInfoViewHolder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
			rideInfoViewHolder.imageViewVerticalLine.setVisibility(View.GONE);
			rideInfoViewHolder.imageViewHorizontalLineNew.setImageResource(R.drawable.radio_select);
		} else if (i == getItemCount() - 1) {
			rideInfoViewHolder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			rideInfoViewHolder.imageViewVerticalLine1.setVisibility(View.GONE);
		} else {
			rideInfoViewHolder.imageViewVerticalLine.setVisibility(View.VISIBLE);
			rideInfoViewHolder.imageViewVerticalLine1.setVisibility(View.VISIBLE);
		}

		if(getItemCount() == 1){
			rideInfoViewHolder.imageViewVerticalLine1.setVisibility(View.GONE);
		}
	}

	@Override
	public rideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_delivery_info_in_ride, viewGroup, false);

		return new rideInfoViewHolder(itemView);
	}

	public class rideInfoViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout rootLinear;
		protected TextView textViewCustomerNameValue, textViewCustomerDeliveryAddressValue;
		protected ImageView imageViewVerticalLine, imageViewVerticalLine1, imageViewHorizontalLineNew, imageViewFakeBottom;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			rootLinear = (RelativeLayout) v.findViewById(R.id.rootLinear);
			textViewCustomerNameValue = (TextView) v.findViewById(R.id.textViewCustomerNameValue);
			textViewCustomerNameValue.setTypeface(Fonts.mavenRegular(context));
			textViewCustomerDeliveryAddressValue = (TextView) v.findViewById(R.id.textViewCustomerDeliveryAddressValue);
			textViewCustomerDeliveryAddressValue.setTypeface(Fonts.mavenRegular(context));

			imageViewFakeBottom = (ImageView) v.findViewById(R.id.imageViewFakeBottom);
			imageViewVerticalLine = (ImageView) v.findViewById(R.id.imageViewVerticalLine);
			imageViewVerticalLine1 = (ImageView) v.findViewById(R.id.imageViewVerticalLine1);
			imageViewHorizontalLineNew = (ImageView) v.findViewById(R.id.imageViewHorizontalLineNew);

			rootLinear.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(rootLinear);
		}
	}
}