package product.clicklabs.jugnoo.driver.adapters;

/**
 * Created by aneeshbansal on 13/06/16.
 */

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
class ViewHolderDeliveryAddress {
	TextView textViewToValue;
	ImageView destination;
	RelativeLayout relative;
	int id;
}

public class DeliveryAddressListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	ViewHolderDeliveryAddress holder;
	Context context;

	public DeliveryAddressListAdapter(Context context) {
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
			holder = new ViewHolderDeliveryAddress();
			convertView = mInflater.inflate(R.layout.list_item_delivery_address, null);

			holder.textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue);
			holder.textViewToValue.setTypeface(Data.latoRegular(context));
			holder.destination = (ImageView) convertView.findViewById(R.id.destination);

			holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

			holder.relative.setTag(holder);

			holder.relative.setLayoutParams(new ListView.LayoutParams(720, ViewGroup.LayoutParams.WRAP_CONTENT));
			ASSL.DoMagic(holder.relative);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolderDeliveryAddress) convertView.getTag();
		}

		holder.id = position;

		CancelOption cancelOption = Data.cancelOptionsList.get(position);

		holder.textViewToValue.setText(cancelOption.name);


		return convertView;
	}
}
