package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.InvoiceDetailsActivity;
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

	private ArrayList<InfoTileResponse.Tile> infoTileResponses;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public InfoTilesAdapter(Context context, ArrayList<InfoTileResponse.Tile> infoTileResponses, InfoTilesAdapterHandler infoTilesAdapterHandler) {
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
		final InfoTileResponse.Tile itr = infoTileResponses.get(i);

		infoTileViewHolder.textViewHeadText.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView1Value.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView1.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView2Value.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textView2.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textViewValue.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textViewValueSub.setTypeface(Data.latoRegular(context));
		infoTileViewHolder.textViewEmpty.setVisibility(View.GONE);

		if(i == 0){
			infoTileViewHolder.topRlOuterSlide.setVisibility(View.VISIBLE);
		} else {
			infoTileViewHolder.topRlOuterSlide.setVisibility(View.GONE);
		}

		if(itr.getDeepIndex() == 10){
			infoTileViewHolder.arrowSlider.setVisibility(View.GONE);
		} else {
			infoTileViewHolder.arrowSlider.setVisibility(View.VISIBLE);
		}

		if(itr.getTitle() != null){
			infoTileViewHolder.textViewHeadText.setText(itr.getTitle());
		} else {
			infoTileViewHolder.textViewHeadText.setVisibility(View.GONE);
		}

		if(itr.getTextValue1() != null){
			infoTileViewHolder.textView1Value.setText(itr.getTextValue1());
		} else {
			infoTileViewHolder.textView1Value.setVisibility(View.GONE);
			infoTileViewHolder.textViewEmpty.setVisibility(View.VISIBLE);
		}

		if(itr.getTextView1() != null){
			infoTileViewHolder.textView1.setText(itr.getTextView1());
		} else {
			infoTileViewHolder.textView1.setVisibility(View.GONE);
		}

		if(itr.getTextValue2() != null){
			infoTileViewHolder.textView2Value.setText(itr.getTextValue2());
		} else {
			infoTileViewHolder.textView2Value.setVisibility(View.GONE);
			infoTileViewHolder.textViewEmpty.setVisibility(View.VISIBLE);
		}

		if(itr.getTextView2() != null){
			infoTileViewHolder.textView2.setText(itr.getTextView2());
		} else {
			infoTileViewHolder.textView2.setVisibility(View.GONE);
		}

		if(itr.getValue() != null){
			infoTileViewHolder.textViewValue.setText(itr.getValue());
		} else {
			infoTileViewHolder.textViewValue.setVisibility(View.GONE);
		}

		if(itr.getTextViewSub() != null){
			infoTileViewHolder.textViewValueSub.setText(itr.getTextViewSub());
		} else {
			infoTileViewHolder.textViewValueSub.setVisibility(View.GONE);
		}

		infoTileViewHolder.relative.setTag(i);
		infoTileViewHolder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				infoTileResponses.get((int) v.getTag()).completed = 1;
				notifyDataSetChanged();
				infoTilesAdapterHandler.okClicked(infoTileResponses.get((Integer) v.getTag()));
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
				textView2, textViewValue, textViewValueSub, textViewEmpty;
		protected int id;
		protected ImageView topRlOuterSlide, arrowSlider;
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
			textViewEmpty = (TextView) v.findViewById(R.id.textViewEmpty);
			topRlOuterSlide = (ImageView) v.findViewById(R.id.topRlOuterSlide);
			arrowSlider = (ImageView) v.findViewById(R.id.arrowSlider);
			relative.setLayoutParams(new RecyclerView.LayoutParams(690, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}

}