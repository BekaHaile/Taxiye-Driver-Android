package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CancelOption;

/**
 * Created by aneeshbansal on 24/10/15.
 */
class ViewHolderCancelOption {
	TextView textViewCancelOption;
	ImageView imageViewCancelOptionCheck;
	RelativeLayout relative;
	int id;
}

public class CancelOptionsListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	ViewHolderCancelOption holder;
	Context context;

	public CancelOptionsListAdapter(Context context) {
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
			holder = new ViewHolderCancelOption();
			convertView = mInflater.inflate(R.layout.list_item_cancel_option, null);

			holder.textViewCancelOption = (TextView) convertView.findViewById(R.id.textViewCancelOption);
			holder.imageViewCancelOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewCancelOptionCheck);

			holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

			holder.relative.setTag(holder);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderCancelOption) convertView.getTag();
		}

		holder.id = position;

		CancelOption cancelOption = Data.cancelOptionsList.get(position);

		holder.textViewCancelOption.setText(cancelOption.name);

		if(cancelOption.checked){
			holder.relative.setBackgroundColor(Color.WHITE);
			holder.imageViewCancelOptionCheck.setImageResource(R.drawable.option_checked_orange);
			holder.imageViewCancelOptionCheck.setColorFilter(ContextCompat.getColor(holder.imageViewCancelOptionCheck.getContext(), R.color.themeColor),
					android.graphics.PorterDuff.Mode.MULTIPLY);

		}
		else{
			holder.relative.setBackgroundColor(Color.TRANSPARENT);
			holder.imageViewCancelOptionCheck.setImageResource(R.drawable.option_unchecked);
			holder.imageViewCancelOptionCheck.setColorFilter(null);
		}

		holder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					holder = (ViewHolderCancelOption) v.getTag();
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