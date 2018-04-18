package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.MissedRideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverMissedRidesFragment extends Fragment implements FlurryEventNames {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;

	DriverMissedRidesListAdapter driverMissedRidesListAdapter;

	RelativeLayout main;

	ArrayList<MissedRideInfo> missedRideInfos = new ArrayList<MissedRideInfo>();

	public DriverMissedRidesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		missedRideInfos.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(getActivity()));
		listView = (ListView) rootView.findViewById(R.id.listView);

		driverMissedRidesListAdapter = new DriverMissedRidesListAdapter();
		listView.setAdapter(driverMissedRidesListAdapter);

		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getMissedRidesAsync(getActivity());
			}
		});

		getMissedRidesAsync(getActivity());
		FlurryEventLogger.event(MISSED_RIDES_CHECKED);

		return rootView;
	}

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			missedRideInfos.clear();
			driverMissedRidesListAdapter.notifyDataSetChanged();
		} else {
			if (missedRideInfos.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverMissedRidesListAdapter.notifyDataSetChanged();
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

	class ViewHolderDriverMissedRides {
		TextView textViewMissedRideFrom, textViewMissedRideFromValue, textViewMissedRideTime, textViewMissedRideCustomerName, textViewMissedRideCustomerNameValue,
				textViewMissedRideCustomerDistance, textViewMissedRideCustomerDistanceValue;
		LinearLayout relative;
		int id;
	}

	class DriverMissedRidesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverMissedRides holder;

		public DriverMissedRidesListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return missedRideInfos.size();
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
				holder = new ViewHolderDriverMissedRides();
				convertView = mInflater.inflate(R.layout.list_item_missed_rides, null);

				holder.textViewMissedRideFrom = (TextView) convertView.findViewById(R.id.textViewMissedRideFrom);
				holder.textViewMissedRideFrom.setTypeface(Data.latoRegular(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideFromValue = (TextView) convertView.findViewById(R.id.textViewMissedRideFromValue);
				holder.textViewMissedRideFromValue.setTypeface(Data.latoRegular(getActivity()));

				holder.textViewMissedRideTime = (TextView) convertView.findViewById(R.id.textViewMissedRideTime);
				holder.textViewMissedRideTime.setTypeface(Data.latoRegular(getActivity()));

				holder.textViewMissedRideCustomerName = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerName);
				holder.textViewMissedRideCustomerName.setTypeface(Data.latoRegular(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideCustomerNameValue = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerNameValue);
				holder.textViewMissedRideCustomerNameValue.setTypeface(Data.latoRegular(getActivity()));

				holder.textViewMissedRideCustomerDistance = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerDistance);
				holder.textViewMissedRideCustomerDistance.setTypeface(Data.latoRegular(getActivity()), Typeface.BOLD);
				holder.textViewMissedRideCustomerDistanceValue = (TextView) convertView.findViewById(R.id.textViewMissedRideCustomerDistanceValue);
				holder.textViewMissedRideCustomerDistanceValue.setTypeface(Data.latoRegular(getActivity()));


				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverMissedRides) convertView.getTag();
			}


			MissedRideInfo missedRideInfo = missedRideInfos.get(position);

			holder.id = position;

			holder.textViewMissedRideFromValue.setText(missedRideInfo.pickupLocationAddress);
			holder.textViewMissedRideTime.setText(DateOperations.convertDate(DateOperations.utcToLocal(missedRideInfo.timestamp)));

			holder.textViewMissedRideCustomerNameValue.setText(missedRideInfo.customerName);
			holder.textViewMissedRideCustomerDistanceValue.setText(missedRideInfo.customerDistance + " km");

			return convertView;
		}
	}


	/**
	 * ASync for get rides from server
	 */
	public void getMissedRidesAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			progressBar.setVisibility(View.VISIBLE);
			textViewInfoDisplay.setVisibility(View.GONE);
			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().getMissedRides(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());

					try {
						JSONObject jObj = new JSONObject(responseStr);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							}
						} else {

							//									{
							//									    "missed_rides": [
							//									         {
//							            			"engagement_id": 250,
//							            			"pickup_location_address": "1604, Sector 18D, Chandigarh 160018, India",
//							            			"timestamp": "2014-07-30 05:51:42",
//							            			"user_name": "Shankar Bhagwati",
//							            			"distance": 0.42
//							        			}
							//									    ]
							//									}

							JSONArray missedRidesData = jObj.getJSONArray("missed_rides");
							missedRideInfos.clear();
							DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
							if (missedRidesData.length() > 0) {
								for (int i = missedRidesData.length() - 1; i >= 0; i--) {
									JSONObject rideData = missedRidesData.getJSONObject(i);
									Log.e("rideData" + i, "=" + rideData);
									if (rideData.has("user_name")) {
										missedRideInfos.add(new MissedRideInfo(rideData.getString("engagement_id"),
												rideData.getString("pickup_location_address"),
												rideData.getString("timestamp"),
												rideData.getString("user_name"),
												decimalFormat.format(rideData.getDouble("distance"))));
									} else {
										missedRideInfos.add(new MissedRideInfo(rideData.getString("engagement_id"),
												rideData.getString("pickup_location_address"),
												rideData.getString("timestamp"),
												"",
												decimalFormat.format(rideData.getDouble("distance"))));
									}
								}
							}
							updateListData(getResources().getString(R.string.no_missed_rides_currently), false);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
					}
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					progressBar.setVisibility(View.GONE);
					updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
				}
			});

		} else {
			updateListData(getResources().getString(R.string.no_internet_tap_to_retry), true);
		}
	}
}