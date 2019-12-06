package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

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

		rideInfoViewHolder.textViewInfoText.setTypeface(Fonts.mavenRegular(context));
		rideInfoViewHolder.textViewInfoValue.setTypeface(Fonts.mavenRegular(context));


		if(itr.getInfo() != null){
			if(itr.getInfo()!=null && itr.getInfo().contains("Jugnoo")){
				itr.setInfo(itr.getInfo().replace("Jugnoo", context.getResources().getString(R.string.white_label_name)));
			}
			rideInfoViewHolder.textViewInfoText.setText(itr.getInfo());
		} else {
			rideInfoViewHolder.textViewInfoText.setVisibility(View.GONE);
		}

		rideInfoViewHolder.textViewInfoValue.setText(Utils.formatCurrencyValue(itr.getCurrencyUnit(), itr.getValue()));



	}

	@Override
	public rideInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_ride_param, viewGroup, false);

		return new rideInfoViewHolder(itemView);
	}

	public class rideInfoViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout relativeLayoutRideItem;
		protected TextView textViewInfoText, textViewInfoValue;
		protected int id;

		public rideInfoViewHolder(View v) {
			super(v);
			relativeLayoutRideItem = (RelativeLayout) v.findViewById(R.id.relativeLayoutRideItem);
			textViewInfoText = (TextView) v.findViewById(R.id.textViewInfoText);
			textViewInfoText.setTypeface(Fonts.mavenRegular(context));
			textViewInfoValue = (TextView) v.findViewById(R.id.textViewInfoValue);
			textViewInfoValue.setTypeface(Fonts.mavenRegular(context));


			/*relativeLayoutRideItem.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relativeLayoutRideItem);*/
		}
	}
}