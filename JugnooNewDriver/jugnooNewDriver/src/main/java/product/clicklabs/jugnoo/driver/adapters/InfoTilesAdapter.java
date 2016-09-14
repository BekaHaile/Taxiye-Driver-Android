package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneesh on 10/4/15.
 */
public class InfoTilesAdapter extends RecyclerView.Adapter<InfoTilesAdapter.infoTileViewHolder> {

	private ArrayList<InfoTileResponse> infoTileResponses;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public InfoTilesAdapter(Context context, ArrayList<InfoTileResponse> infoTileResponses, InfoTilesAdapterHandler infoTilesAdapterHandler) {
		this.infoTileResponses = infoTileResponses;
		this.context = context;
		this.infoTilesAdapterHandler = infoTilesAdapterHandler;
	}

	@Override
	public int getItemCount() {
		return infoTileResponses.size();
	}

	@Override
	public void onBindViewHolder(infoTileViewHolder infoTileViewHolder, int i) {
		InfoTileResponse srd = infoTileResponses.get(i);

		infoTileViewHolder.textViewHeadText.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView1Value.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView1.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView2Value.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		infoTileViewHolder.textView2.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		infoTileViewHolder.textViewValue.setTypeface(Data.latoRegular(context), Typeface.BOLD);
		infoTileViewHolder.textViewValueSub.setTypeface(Data.latoRegular(context));


		infoTileViewHolder.relative.setTag(i);
		infoTileViewHolder.relative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				infoTileResponses.get((int) v.getTag()).completed = 1;
				notifyDataSetChanged();
				infoTilesAdapterHandler.okClicked(infoTileResponses.get((int) v.getTag()));
			}
		});

	}

	@Override
	public infoTileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.
				from(viewGroup.getContext()).
				inflate(R.layout.list_item_slidebar_tab, viewGroup, false);

		return new infoTileViewHolder(itemView);
	}

	public class infoTileViewHolder extends RecyclerView.ViewHolder {
		protected RelativeLayout relative;
		protected TextView textViewHeadText, textView1Value, textView1, textView2Value,
				textView2, textViewValue, textViewValueSub;
		protected int id;

		public infoTileViewHolder(View v) {
			super(v);
			relative = (RelativeLayout)v.findViewById(R.id.relative);
			textViewHeadText = (TextView) v.findViewById(R.id.textViewHeadText);
			textViewHeadText.setTypeface(Data.latoRegular(context));
			textView1Value = (TextView) v.findViewById(R.id.textView1Value);
			textView1Value.setTypeface(Data.latoRegular(context));
			textView1 = (TextView) v.findViewById(R.id.textView1);
			textView1.setTypeface(Data.latoRegular(context));
			textView2Value = (TextView) v.findViewById(R.id.textView2Value);
			textView2Value.setTypeface(Data.latoRegular(context));
			textView2 = (TextView) v.findViewById(R.id.textView2);
			textView2.setTypeface(Data.latoRegular(context));
			textViewValue = (TextView) v.findViewById(R.id.textViewValue);
			textViewValue.setTypeface(Data.latoRegular(context));
			textViewValueSub = (TextView) v.findViewById(R.id.textViewValueSub);
			textViewValueSub.setTypeface(Data.latoRegular(context));


			relative.setLayoutParams(new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}
}