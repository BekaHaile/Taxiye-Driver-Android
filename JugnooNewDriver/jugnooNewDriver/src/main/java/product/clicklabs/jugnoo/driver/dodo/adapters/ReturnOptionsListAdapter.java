package product.clicklabs.jugnoo.driver.dodo.adapters;

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

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CancelOption;
import product.clicklabs.jugnoo.driver.utils.ASSL;

/**
 * Created by aneeshbansal on 24/10/15.
 */
class ViewHolderReturnOption {
	TextView textViewReturnOption;
	ImageView imageViewReturnOptionCheck;
	RelativeLayout relative;
	int id;
}

public class ReturnOptionsListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	ViewHolderReturnOption holder;
	Context context;

	public ReturnOptionsListAdapter(Context context) {
		this.context = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Data.cancelOptionsList.size();
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
			holder = new ViewHolderReturnOption();
			convertView = mInflater.inflate(R.layout.list_item_return_reasons, null);

			holder.textViewReturnOption = (TextView) convertView.findViewById(R.id.textViewCancelOption);
			holder.textViewReturnOption.setTypeface(Data.latoRegular(context));
			holder.imageViewReturnOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewCancelOptionCheck);

			holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

			holder.relative.setTag(holder);

			holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderReturnOption) convertView.getTag();
		}

		holder.id = position;

		CancelOption cancelOption = Data.cancelOptionsList.get(position);

		holder.textViewReturnOption.setText(cancelOption.name);

		if(cancelOption.checked){
			holder.relative.setBackgroundColor(Color.WHITE);
			holder.imageViewReturnOptionCheck.setImageResource(R.drawable.option_checked_orange);
		}
		else{
			holder.relative.setBackgroundColor(Color.TRANSPARENT);
			holder.imageViewReturnOptionCheck.setImageResource(R.drawable.option_unchecked);
		}

		holder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					holder = (ViewHolderReturnOption) v.getTag();
					for(int i=0; i<Data.cancelOptionsList.size(); i++){
						if(holder.id == i){
							Data.cancelOptionsList.get(i).checked = true;
						}
						else{
							Data.cancelOptionsList.get(i).checked = false;
						}
					}
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return convertView;
	}
}