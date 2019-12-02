package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.app.Activity;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryReturnOption;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;

/**
 * Created by aneeshbansal on 24/10/15.
 */

public class ReturnOptionsListAdapter extends  RecyclerView.Adapter<ReturnOptionsListAdapter.ViewHolder> {

	private Activity activity;

	public ReturnOptionsListAdapter(Activity activity) {
		this.activity = activity;
	}


	@Override
	public ReturnOptionsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_return_reasons, parent, false);

		RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);

		ASSL.DoMagic(v);
		return new ViewHolder(v, activity);
	}

	@Override
	public void onBindViewHolder(ReturnOptionsListAdapter.ViewHolder holder, int position) {
		DeliveryReturnOption deliveryReturnOption = Data.deliveryReturnOptionList.get(position);

		holder.textViewReturnOption.setText(deliveryReturnOption.getName());

		if(deliveryReturnOption.isChecked()){
			holder.relative.setBackgroundColor(Color.WHITE);
			holder.imageViewReturnOptionCheck.setImageResource(R.drawable.radio_select);
		}
		else{
			holder.relative.setBackgroundColor(Color.TRANSPARENT);
			holder.imageViewReturnOptionCheck.setImageResource(R.drawable.radio_unslelcet);
		}

		holder.relative.setTag(position);
		holder.relative.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int id = (int) v.getTag();
					for(int i=0; i<Data.deliveryReturnOptionList.size(); i++){
						if(id == i){
							Data.deliveryReturnOptionList.get(i).setChecked(true);
						}
						else{
							Data.deliveryReturnOptionList.get(i).setChecked(false);
						}
					}
					notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		if(Data.deliveryReturnOptionList == null){
			return 0;
		}
		return Data.deliveryReturnOptionList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textViewReturnOption;
		public ImageView imageViewReturnOptionCheck;
		public RelativeLayout relative;
		public ViewHolder(View convertView, Activity context) {
			super(convertView);
			textViewReturnOption = (TextView) convertView.findViewById(R.id.textViewReturnOption);
			textViewReturnOption.setTypeface(Fonts.mavenRegular(context));
			imageViewReturnOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewReturnOptionCheck);
			relative = (RelativeLayout) convertView.findViewById(R.id.relative);
		}
	}

}