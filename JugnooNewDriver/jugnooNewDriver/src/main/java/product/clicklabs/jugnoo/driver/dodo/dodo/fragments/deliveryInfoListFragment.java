package product.clicklabs.jugnoo.driver.dodo.dodo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.InvoiceDetailsActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.InvoiceInfo;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.dodo.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class deliveryInfoListFragment extends Fragment implements FlurryEventNames {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;

	DeliveryInfoListAdapter deliveryInfoListAdapter;

	RelativeLayout main;

	ArrayList<DeliveryInfo> deliveryList = new ArrayList<>();


	public deliveryInfoListFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		deliveryList.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(getActivity()));
		listView = (ListView) rootView.findViewById(R.id.listView);

		deliveryInfoListAdapter = new DeliveryInfoListAdapter();
		listView.setAdapter(deliveryInfoListAdapter);

		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				getDeliveryInfoList(getActivity());
			}
		});

//		getDeliveryInfoList(getActivity());
		FlurryEventLogger.event(COMPLETE_RIDES_CHECKED);


		return rootView;
	}


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			deliveryList.clear();
			deliveryInfoListAdapter.notifyDataSetChanged();
		} else {
			if (deliveryList.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			deliveryInfoListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}


	class ViewHolderDriverRides {
		TextView textViewCustomerName, textViewCustomerDeliveryAddress, textViewShowDistance;
		RelativeLayout  driverPassengerCallRl, markDeliveryRl;
		LinearLayout rootLinear;
		int id;
	}

	class DeliveryInfoListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRides holder;

		public DeliveryInfoListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return deliveryList.size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderDriverRides();
				convertView = mInflater.inflate(R.layout.list_item_invoice_history, null);

				holder.textViewCustomerName = (TextView) convertView.findViewById(R.id.textViewCustomerName);
				holder.textViewCustomerName.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewCustomerDeliveryAddress = (TextView) convertView.findViewById(R.id.textViewCustomerDeliveryAddress);
				holder.textViewCustomerDeliveryAddress.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewShowDistance = (TextView) convertView.findViewById(R.id.textViewShowDistance);
				holder.textViewShowDistance.setTypeface(Data.latoRegular(getActivity()));


				holder.driverPassengerCallRl = (RelativeLayout) convertView.findViewById(R.id.driverPassengerCallRl);
				holder.markDeliveryRl = (RelativeLayout) convertView.findViewById(R.id.markDeliveryRl);

				holder.rootLinear = (LinearLayout) convertView.findViewById(R.id.rootLinear);

				holder.rootLinear.setTag(holder);

				holder.rootLinear.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.rootLinear);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}


			final DeliveryInfo deliveryInfo;
			try {
				deliveryInfo = deliveryList.get(position);

				holder.id = position;


				holder.textViewCustomerName.setText("");
				holder.textViewCustomerDeliveryAddress.setText("");
				holder.textViewShowDistance.setText("");


			holder.markDeliveryRl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderDriverRides) v.getTag();

				}
			});
			} catch (Resources.NotFoundException e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}



}
