package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class DeliveryInfoInRideAdapter extends RecyclerView.Adapter<DeliveryInfoInRideAdapter.rideInfoViewHolder> {

	private ArrayList<FareStructureInfo> fareStructureInfos;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public DeliveryInfoInRideAdapter(Context context, ArrayList<FareStructureInfo> fareStructureInfos) {
		this.fareStructureInfos = fareStructureInfos;
		this.context = context;
	}

	@Override
	public int getItemCount() {
		return fareStructureInfos.size();
	}

	@Override
	public void onBindViewHolder(rideInfoViewHolder rideInfoViewHolder, int i) {
		final FareStructureInfo itr = fareStructureInfos.get(i);

		rideInfoViewHolder.textViewCustomerNameValue.setTypeface(Fonts.mavenRegular(context));
		rideInfoViewHolder.textViewCustomerDeliveryAddressValue.setTypeface(Fonts.mavenRegular(context));


		if(itr.getInfo() != null){
			rideInfoViewHolder.textViewCustomerNameValue.setText(itr.getInfo());
		} else {
			rideInfoViewHolder.textViewCustomerNameValue.setVisibility(View.GONE);
		}

		rideInfoViewHolder.textViewCustomerDeliveryAddressValue.setText(Utils.getAbsWithDecimalAmount(context, itr.getValue()));



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
		protected ImageView imageViewVerticalLine, imageViewStatusCircle, imageViewVerticalLineBottom;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			rootLinear = (RelativeLayout) v.findViewById(R.id.rootLinear);
			textViewCustomerNameValue = (TextView) v.findViewById(R.id.textViewCustomerNameValue);
			textViewCustomerNameValue.setTypeface(Fonts.mavenRegular(context));
			textViewCustomerDeliveryAddressValue = (TextView) v.findViewById(R.id.textViewCustomerDeliveryAddressValue);
			textViewCustomerDeliveryAddressValue.setTypeface(Fonts.mavenRegular(context));

			imageViewVerticalLine = (ImageView) v.findViewById(R.id.imageViewVerticalLine);
			imageViewStatusCircle = (ImageView) v.findViewById(R.id.imageViewStatusCircle);
			imageViewVerticalLineBottom = (ImageView) v.findViewById(R.id.imageViewVerticalLineBottom);


			rootLinear.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(rootLinear);
		}
	}
}