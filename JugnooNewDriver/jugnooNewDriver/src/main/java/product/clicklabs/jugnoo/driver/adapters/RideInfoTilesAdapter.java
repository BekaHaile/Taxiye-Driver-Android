package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;

/**
 * Created by aneesh on 10/4/15.
 */
public class RideInfoTilesAdapter extends RecyclerView.Adapter<RideInfoTilesAdapter.rideInfoViewHolder> {

	private ArrayList<FareStructureInfo> fareStructureInfos;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public RideInfoTilesAdapter(Context context, ArrayList<FareStructureInfo> fareStructureInfos) {
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

		rideInfoViewHolder.textViewInfoText.setTypeface(Data.latoRegular(context));
		rideInfoViewHolder.textViewInfoValue.setTypeface(Data.latoRegular(context));


		if(itr.getInfo() != null){
			rideInfoViewHolder.textViewInfoText.setText(itr.getInfo());
		} else {
			rideInfoViewHolder.textViewInfoText.setVisibility(View.GONE);
		}

		if(itr.getValue() != 0){
			rideInfoViewHolder.textViewInfoValue.setText(""+itr.getValue());
		} else {
			rideInfoViewHolder.textViewInfoValue.setVisibility(View.GONE);
		}


	}

	@Override
	public rideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_ride_param, viewGroup, false);

		return new rideInfoViewHolder(itemView);
	}

	public class rideInfoViewHolder extends RecyclerView.ViewHolder {
		protected LinearLayout linearLayoutRideItem;
		protected TextView textViewInfoText, textViewInfoValue;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			textViewInfoText = (TextView) v.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Data.latoRegular(context));
			textViewInfoValue = (TextView) v.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Data.latoRegular(context));


			linearLayoutRideItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutRideItem);
		}
	}
}