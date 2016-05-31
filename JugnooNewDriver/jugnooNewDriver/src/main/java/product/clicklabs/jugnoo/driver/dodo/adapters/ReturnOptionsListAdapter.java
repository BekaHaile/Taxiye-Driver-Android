package product.clicklabs.jugnoo.driver.dodo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.datastructure.ReturnOptions;
import product.clicklabs.jugnoo.driver.utils.ASSL;

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
		ReturnOptions returnOptions = Data.returnOptionsList.get(position);

		holder.textViewReturnOption.setText(returnOptions.name);

		if(returnOptions.checked){
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

					for(int i=0; i<Data.returnOptionsList.size(); i++){
						if(id == i){
							Data.returnOptionsList.get(i).checked = true;
						}
						else{
							Data.returnOptionsList.get(i).checked = false;
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return Data.returnOptionsList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textViewReturnOption;
		public ImageView imageViewReturnOptionCheck;
		public RelativeLayout relative;
		public ViewHolder(View convertView, Activity context) {
			super(convertView);
			textViewReturnOption = (TextView) convertView.findViewById(R.id.textViewReturnOption);
			textViewReturnOption.setTypeface(Data.latoRegular(context));
			imageViewReturnOptionCheck = (ImageView) convertView.findViewById(R.id.imageViewReturnOptionCheck);
			relative = (RelativeLayout) convertView.findViewById(R.id.relative);
		}
	}

}