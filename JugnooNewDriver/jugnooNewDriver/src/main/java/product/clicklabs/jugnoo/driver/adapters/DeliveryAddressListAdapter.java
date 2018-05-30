package product.clicklabs.jugnoo.driver.adapters;

/**
 * Created by aneeshbansal on 13/06/16.
 */

import android.content.Context;
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
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;


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
	ArrayList<String> deliveryAddressList;

	public DeliveryAddressListAdapter(Context context, ArrayList<String> deliveryAddressList) {
		this.context = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.deliveryAddressList = deliveryAddressList;
	}

	@Override
	public int getCount() {
		return deliveryAddressList.size();
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
			holder.textViewToValue.setTypeface(Fonts.mavenRegular(context));
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

		String address = deliveryAddressList.get(position);

		holder.textViewToValue.setText(address);


		return convertView;
	}
}
