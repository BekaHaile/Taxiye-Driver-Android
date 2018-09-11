package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.InvoiceDetailsActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.InvoiceInfo;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SlidngBarFragment extends Fragment implements FlurryEventNames {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;


	DriverRidesListAdapter driverRidesListAdapter;

	RelativeLayout main;

	ArrayList<InvoiceInfo> invoices = new ArrayList<InvoiceInfo>();

	UpdateDriverEarnings updateDriverEarnings;

	public SlidngBarFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		invoices.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Fonts.mavenRegular(getActivity()));
		listView = (ListView) rootView.findViewById(R.id.listView);


		driverRidesListAdapter = new DriverRidesListAdapter();
		listView.setAdapter(driverRidesListAdapter);

		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);


		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getInvoiceHistory(getActivity());
			}
		});

		getInvoiceHistory(getActivity());
		FlurryEventLogger.event(COMPLETE_RIDES_CHECKED);


		return rootView;
	}


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			invoices.clear();
			driverRidesListAdapter.notifyDataSetChanged();
		} else {
			if (invoices.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverRidesListAdapter.notifyDataSetChanged();
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
		TextView textViewHeadText, textView1Value, textView1, textView2Value,
				textView2, textViewValue, textViewValueSub;
		ImageView imageViewRequestType;
		ImageView statusImage;
		RelativeLayout relative;
		int id;
	}

	class DriverRidesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRides holder;

		public DriverRidesListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return invoices.size();
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
				convertView = mInflater.inflate(R.layout.list_item_slidebar_tab, null);

				holder.textViewHeadText = (TextView) convertView.findViewById(R.id.textViewHeadText);
				holder.textViewHeadText.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textView1Value = (TextView) convertView.findViewById(R.id.textView1Value);
				holder.textView1Value.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				holder.textView1.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textView2Value = (TextView) convertView.findViewById(R.id.textView2Value);
				holder.textView2Value.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
				holder.textView2.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textViewValue = (TextView) convertView.findViewById(R.id.textViewValue);
				holder.textViewValue.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.textViewValueSub = (TextView) convertView.findViewById(R.id.textViewValueSub);
				holder.textViewValueSub.setTypeface(Fonts.mavenRegular(getActivity()));

				holder.statusImage = (ImageView) convertView.findViewById(R.id.statusImage);
				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}


			final InvoiceInfo invoiceInfo;
			try {
				invoiceInfo = invoices.get(position);

				holder.id = position;


//				if(invoiceInfo.generatedTime.equalsIgnoreCase("0")){
//					holder.dateTimeValueGenerated.setText(" "+getResources().getString(R.string.NA) );
//				}else {
//					holder.dateTimeValueGenerated.setText(DateOperations.reverseDate(invoiceInfo.generatedTime));
//				}
//				if(invoiceInfo.id==0){
//					holder.textViewInvoiceId.setText(getResources().getString(R.string.Invoice) + ": " + getResources().getString(R.string.NA));
//				}else {
//					holder.textViewInvoiceId.setText(getResources().getString(R.string.Invoice)+": " + invoiceInfo.id);
//				}
//
//				holder.dateTimeValueTo.setText(DateOperations.reverseDate(invoiceInfo.toTime));
//				holder.dateTimeValueFrom.setText(DateOperations.reverseDate(invoiceInfo.fromTime));
//
//				if(invoiceInfo.statusString.equalsIgnoreCase("Success")) {
//					holder.textViewStatusString.setText(getResources().getString(R.string.success));
//					holder.statusImage.setImageResource(R.drawable.green_tick);
//					holder.textViewStatusString.setTextColor(getResources().getColor(R.color.green_delivery));
//				}else if(invoiceInfo.statusString.equalsIgnoreCase("Pending")){
//					holder.textViewStatusString.setText(getResources().getString(R.string.payment_sent));
//					holder.statusImage.setImageResource(R.drawable.exclamation_red);
//					holder.textViewStatusString.setTextColor(getResources().getColor(R.color.red_delivery));
//				}else if(invoiceInfo.statusString.equalsIgnoreCase("Outstanding Adjusted")){
//					holder.textViewStatusString.setText(getResources().getString(R.string.outstanding_amount1));
//					holder.statusImage.setImageResource(R.drawable.rupee_green);
//					holder.textViewStatusString.setTextColor(getResources().getColor(R.color.green_delivery));
//				}
//				holder.textViewInvoiceFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(invoiceInfo.fare));


				if(Prefs.with(getActivity()).getInt(Constants.SHOW_INVOICE_DETAILS,0)==1) {
					holder.relative.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							holder = (ViewHolderDriverRides) v.getTag();
							Intent intent = new Intent(getActivity(), InvoiceDetailsActivity.class);
							intent.putExtra("invoice_id", invoiceInfo.id);
							getActivity().startActivity(intent);
							getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
							try {
								JSONObject map = new JSONObject();
								map.put(Constants.KEY_INVOICE_ID, invoiceInfo.id);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}

	}


//	Retrofit

	private void getInvoiceHistory(final Activity activity) {
		progressBar.setVisibility(View.VISIBLE);
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put("current_mode", "1");
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().invoiceHistory(params, new Callback<InvoiceHistoryResponse>() {
                        @Override
                        public void success(InvoiceHistoryResponse invoiceHistoryResponse, Response response) {
                            try {
                                String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                JSONObject jObj;
                                jObj = new JSONObject(jsonString);
                                if (!jObj.isNull("error")) {
                                    String errorMessage = jObj.getString("error");
                                    if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                        HomeActivity.logoutUser(activity, null);
                                    } else {
                                        updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                                    }

                                } else {

                                    for (int i = 0; i < invoiceHistoryResponse.getData().size(); i++) {
                                        InvoiceHistoryResponse.Datum data = invoiceHistoryResponse.getData().get(i);
                                        InvoiceInfo invoiceInfo = new InvoiceInfo(data.getInvoiceId(),data.getAmountToBePaid(),
                                                data.getFromDate(), data.getToDate(), data.getInvoiceDate(), data.getInvoiceStatus()
										,data.getCurrencyUnit());
                                        invoices.add(invoiceInfo);
                                    }

                                    updateListData(activity.getResources().getString(R.string.no_rides_currently), false);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                            }
                            progressBar.setVisibility(View.GONE);
                        }


                        @Override
                        public void failure(RetrofitError error) {
                            progressBar.setVisibility(View.GONE);
                            updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
                        }
                    });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
