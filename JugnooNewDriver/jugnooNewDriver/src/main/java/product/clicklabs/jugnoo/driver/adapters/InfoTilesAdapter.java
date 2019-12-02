package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneesh on 10/4/15.
 */
public class InfoTilesAdapter extends RecyclerView.Adapter<InfoTilesAdapter.infoTileViewHolder> {

	private ArrayList<Tile> infoTileResponses;
	private Context context;
	private InfoTilesAdapterHandler infoTilesAdapterHandler;

	public InfoTilesAdapter(Context context, ArrayList<Tile> infoTileResponses, InfoTilesAdapterHandler infoTilesAdapterHandler) {
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
		final Tile itr = infoTileResponses.get(i);

		infoTileViewHolder.textViewEmpty.setVisibility(View.GONE);

		if(i == 0){
			infoTileViewHolder.topRlOuterSlide.setVisibility(View.VISIBLE);
		} else {
			infoTileViewHolder.topRlOuterSlide.setVisibility(View.GONE);
		}

//		if(itr.getDeepIndex() == 10){
//			infoTileViewHolder.arrowSlider.setVisibility(View.GONE);
//		} else {
//			infoTileViewHolder.arrowSlider.setVisibility(View.VISIBLE);
//		}

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
				try {
					int pos = (Integer) v.getTag();
					notifyDataSetChanged();
					infoTilesAdapterHandler.okClicked(infoTileResponses.get((Integer) v.getTag()), pos);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
			textViewHeadText.setTypeface(Fonts.mavenMedium(context), Typeface.BOLD);
			textView1Value = (TextView) v.findViewById(R.id.textView1Value);
			textView1Value.setTypeface(Fonts.mavenRegular(context));
			textView1 = (TextView) v.findViewById(R.id.textView1);
			textView1.setTypeface(Fonts.mavenRegular(context));
			textView2Value = (TextView) v.findViewById(R.id.textView2Value);
			textView2Value.setTypeface(Fonts.mavenRegular(context));
			textView2 = (TextView) v.findViewById(R.id.textView2);
			textView2.setTypeface(Fonts.mavenRegular(context));
			textViewValue = (TextView) v.findViewById(R.id.textViewValue);
			textViewValue.setTypeface(Fonts.mavenRegular(context));
			textViewValueSub = (TextView) v.findViewById(R.id.textViewValueSub);
			textViewValueSub.setTypeface(Fonts.mavenRegular(context));
			textViewEmpty = (TextView) v.findViewById(R.id.textViewEmpty);
			topRlOuterSlide = (ImageView) v.findViewById(R.id.topRlOuterSlide);
			arrowSlider = (ImageView) v.findViewById(R.id.arrowSlider);
			relative.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(relative);
		}
	}

}