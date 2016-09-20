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

		rideInfoViewHolder.textViewJugnooCut.setTypeface(Data.latoRegular(context));
		rideInfoViewHolder.textViewJugnooCutValue.setTypeface(Data.latoRegular(context));


		if(itr.getInfo() != null){
			rideInfoViewHolder.textViewJugnooCut.setText(itr.getInfo());
		} else {
			rideInfoViewHolder.textViewJugnooCut.setVisibility(View.GONE);
		}

		if(itr.getValue() != null){
			rideInfoViewHolder.textViewJugnooCutValue.setText(itr.getValue());
		} else {
			rideInfoViewHolder.textViewJugnooCutValue.setVisibility(View.GONE);
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
		protected TextView textViewJugnooCut, textViewJugnooCutValue;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			linearLayoutRideItem = (LinearLayout)v.findViewById(R.id.linearLayoutRideItem);
			textViewJugnooCut = (TextView) v.findViewById(R.id.textViewJugnooCut);
			textViewJugnooCut.setTypeface(Data.latoRegular(context));
			textViewJugnooCutValue = (TextView) v.findViewById(R.id.textViewJugnooCutValue);
			textViewJugnooCutValue.setTypeface(Data.latoRegular(context));


			linearLayoutRideItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(linearLayoutRideItem);
		}
	}
}