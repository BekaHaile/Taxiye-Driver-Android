package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


class ViewHolderDestinationOption {
	TextView textViewDestinationOption;
	ImageView imageViewDestinationOptionCheck;
	RelativeLayout relative;
	int id;
}


public class DestinationOptionsListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	ViewHolderDestinationOption holder;
	Context context;
	ArrayList<DestinationDataResponse.Region> destinationOptionList;

	public DestinationOptionsListAdapter(Context context, ArrayList<DestinationDataResponse.Region> destinationOptionList) {
		this.context = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.destinationOptionList = destinationOptionList;
	}

	@Override
	public int getCount() {
		return destinationOptionList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolderDestinationOption();
			convertView = mInflater.inflate(R.layout.list_item_destination_option, null);

			holder.textViewDestinationOption = (TextView) convertView.findViewById(R.id.textViewDestinationOption);
			holder.textViewDestinationOption.setTypeface(Fonts.mavenRegular(context));
			holder.imageViewDestinationOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewDestinationOptionCheck);

			holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

			holder.relative.setTag(holder);

			holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderDestinationOption) convertView.getTag();
		}

		holder.id = position;

		DestinationDataResponse.Region region = destinationOptionList.get(position);

		holder.textViewDestinationOption.setText(region.getRegion());

		if(region.getIsSelected() ==1){
			holder.relative.setBackgroundColor(Color.WHITE);
			holder.imageViewDestinationOptionCheck.setImageResource(R.drawable.option_checked_orange);
		}
		else{
			holder.relative.setBackgroundColor(Color.TRANSPARENT);
			holder.imageViewDestinationOptionCheck.setImageResource(R.drawable.option_unchecked);
		}

		holder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					holder = (ViewHolderDestinationOption) v.getTag();
					destinationOptionList.get(holder.id).setIsSelected(destinationOptionList.get(holder.id).getIsSelected() == 0 ? 1 : 0);
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return convertView;
	}
}