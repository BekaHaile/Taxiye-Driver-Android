package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.InfoTilesAdapter;
import product.clicklabs.jugnoo.driver.adapters.RideInfoTilesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RideDetailsNewActivity extends BaseFragmentActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	LinearLayout relative;

	Button backBtn;
	TextView title;

	TextView idValue, dateTimeValue, distanceValue, rideTimeValue, waitTimeValue,
			textViewRideFareValue, textViewConvayenceChargeValue, textViewLuggageChargeValue,
			textViewRateApplied, textViewRateAppliedValue,
			textViewAcceptSubsidyValue, textViewCancelSubsidyValue, textViewJugnooCutValue,
			textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText,
			textViewFromValue, textViewToValue;

	ImageView imageViewRequestType;
	public static final int MAP_PATH_COLOR = Color.TRANSPARENT;
	RelativeLayout relativeLayoutConvenienceCharges, relativeLayoutLuggageCharges,
			relativeLayoutCancelSubsidy, relativeLayoutJugnooCut;
	ArrayList<FareStructureInfo> fareStructureInfos = new ArrayList<>();
	RecyclerView recyclerViewRideInfo;
	RideInfoTilesAdapter rideInfoTilesAdapter;

	public static RideInfo openedRideInfo;
	public ASSL assl;

	GoogleMap mapLite;
	private GoogleApiClient mGoogleApiClient;
	private LatLng pickupLatLng;
	private SearchResult searchResultGlobal;

	CustomerInfo customerInfo;


	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_details_new);


		try {
			Intent intent = getIntent();
			String extra = intent.getStringExtra("extra");
			InfoTileResponse.Tile.Extras extras = new Gson().fromJson(extra, InfoTileResponse.Tile.Extras.class);


		} catch (Exception e) {
			e.printStackTrace();
		}


		try {
			double latitude = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, 0);
			double longitude = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, 0);
			pickupLatLng = new LatLng(latitude, longitude);
		} catch (Exception e) {
			e.printStackTrace();
			try {
//				pickupLatLng = Data.autoData.getPickupLatLng();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();


		relative = (LinearLayout) findViewById(R.id.relative);
		assl = new ASSL(RideDetailsNewActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		relativeLayoutConvenienceCharges = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharges);
		relativeLayoutLuggageCharges = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharges);
		relativeLayoutCancelSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutCancelSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);


		recyclerViewRideInfo = (RecyclerView) findViewById(R.id.recyclerViewRideInfo);
		recyclerViewRideInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewRideInfo.setLayoutManager(llm);
		recyclerViewRideInfo.setItemAnimator(new DefaultItemAnimator());

		fareStructureInfos = new ArrayList<>();
		rideInfoTilesAdapter = new RideInfoTilesAdapter(this, fareStructureInfos);
		recyclerViewRideInfo.setAdapter(rideInfoTilesAdapter);


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				mapLite = googleMap;
				if (mapLite != null) {
					mapLite.getUiSettings().setAllGesturesEnabled(false);
					mapLite.getUiSettings().setZoomGesturesEnabled(false);
					mapLite.getUiSettings().setZoomControlsEnabled(false);
					mapLite.setMyLocationEnabled(true);
					mapLite.getUiSettings().setTiltGesturesEnabled(false);
					mapLite.getUiSettings().setMyLocationButtonEnabled(false);
					mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});

				}
			}
		});


		idValue = (TextView) findViewById(R.id.idValue);
		idValue.setTypeface(Data.latoRegular(this));
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Data.latoRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		distanceValue.setTypeface(Data.latoRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue);
		rideTimeValue.setTypeface(Data.latoRegular(this));
		waitTimeValue = (TextView) findViewById(R.id.waitTimeValue);
		waitTimeValue.setTypeface(Data.latoRegular(this));

		textViewRideFareValue = (TextView) findViewById(R.id.textViewRideFareValue);
		textViewRideFareValue.setTypeface(Data.latoRegular(this));
		textViewConvayenceChargeValue = (TextView) findViewById(R.id.textViewConvayenceChargeValue);
		textViewConvayenceChargeValue.setTypeface(Data.latoRegular(this));
		textViewLuggageChargeValue = (TextView) findViewById(R.id.textViewLuggageChargeValue);
		textViewLuggageChargeValue.setTypeface(Data.latoRegular(this));
		textViewRateApplied = (TextView) findViewById(R.id.textViewRateApplied);
		textViewRateApplied.setTypeface(Data.latoRegular(this));

		textViewRateAppliedValue = (TextView) findViewById(R.id.textViewRateAppliedValue);
		textViewRateAppliedValue.setTypeface(Data.latoRegular(this));
		textViewAcceptSubsidyValue = (TextView) findViewById(R.id.textViewAcceptSubsidyValue);
		textViewAcceptSubsidyValue.setTypeface(Data.latoRegular(this));
		textViewCancelSubsidyValue = (TextView) findViewById(R.id.textViewCancelSubsidyValue);
		textViewCancelSubsidyValue.setTypeface(Data.latoRegular(this));
		textViewJugnooCutValue = (TextView) findViewById(R.id.textViewJugnooCutValue);
		textViewJugnooCutValue.setTypeface(Data.latoRegular(this), Typeface.BOLD);

		textViewActualFare = (TextView) findViewById(R.id.textViewActualFare);
		textViewActualFare.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance);
		textViewAccountBalance.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid);
		textViewCustomerPaid.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewFromValue = (TextView) findViewById(R.id.textViewFromValue);
		textViewFromValue.setTypeface(Data.latoRegular(this));
		textViewToValue = (TextView) findViewById(R.id.textViewToValue);
		textViewToValue.setTypeface(Data.latoRegular(this));
		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.distanceValue)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.rideTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.waitTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFareRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewConvayenceCharge)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewConvayenceChargeRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewLuggageCharge)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewLuggageChargeRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewJugnooCut)).setTypeface(Data.latoRegular(this));
//		((TextView) findViewById(R.id.textViewJugnooCutRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCancelSubsidy)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCancelSubsidyRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRateAppliedRupee)).setTypeface(Data.latoRegular(this));


		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (openedRideInfo != null) {

			customerInfo = Data.getCustomerInfo("");

			idValue.setText(getResources().getString(R.string.ride_id) + " " + openedRideInfo.id);
			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));

			distanceValue.setText(getResources().getString(R.string.distance) + ": "
					+ Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.distance))
					+ " " + getResources().getString(R.string.km));

			rideTimeValue.setText(getResources().getString(R.string.total_time) + ": "
					+ openedRideInfo.rideTime + " " + getResources().getString(R.string.min));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.waitTime), 0) == 0) {
				waitTimeValue.setVisibility(View.GONE);
			} else {
				waitTimeValue.setVisibility(View.VISIBLE);
				waitTimeValue.setText(getResources().getString(R.string.wait) + ": "
						+ openedRideInfo.waitTime + " " + getResources().getString(R.string.min));
			}

			textViewRideFareValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.driverRideFair)));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.convenienceCharges), 0) == 0) {
				relativeLayoutConvenienceCharges.setVisibility(View.GONE);
			} else {
				relativeLayoutConvenienceCharges.setVisibility(View.VISIBLE);
				textViewConvayenceChargeValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.convenienceCharges)));
			}

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.luggageCharges), 0) == 0) {
				relativeLayoutLuggageCharges.setVisibility(View.GONE);
			} else {
				relativeLayoutLuggageCharges.setVisibility(View.VISIBLE);
				textViewLuggageChargeValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.luggageCharges)));
			}

			textViewRateAppliedValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.fareFactorValue)));
			textViewRateApplied.setText(getResources().getString(R.string.rate_applied) + " "
					+ Utils.getDecimalFormat().format(Double.parseDouble(openedRideInfo.fareFactorApplied)) + "x");

			textViewAcceptSubsidyValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.acceptSubsidy)));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.cancelSubsidy), 0) == 0) {
				relativeLayoutCancelSubsidy.setVisibility(View.GONE);
			} else {
				relativeLayoutCancelSubsidy.setVisibility(View.VISIBLE);
				textViewCancelSubsidyValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.cancelSubsidy)));
			}

//			relativeLayoutJugnooCut.setVisibility(View.GONE);
			if ("0".equalsIgnoreCase(openedRideInfo.jugnooCut)) {
				relativeLayoutJugnooCut.setVisibility(View.GONE);
			} else {
				relativeLayoutJugnooCut.setVisibility(View.GONE);
				textViewJugnooCutValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.jugnooCut)));
			}

			textViewActualFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.actualFare)));
			textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.customerPaid)));

			if (Double.parseDouble(openedRideInfo.accountBalance) < 0) {
				textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(openedRideInfo.accountBalance)))));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.money_to));
			} else {
				textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.accountBalance)));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.account));
			}
			textViewFromValue.setText(openedRideInfo.fromLocation);
			textViewToValue.setText(openedRideInfo.toLocation);

			imageViewRequestType.setImageResource(R.drawable.request_autos);

		} else {
			performBackPressed();
		}


		try {
			if (customerInfo.getDropLatLng() != null) {
				getDirectionsAndComputeFare(customerInfo.getDropLatLng(), customerInfo.getDropLatLng());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	private void getDirectionsAndComputeFare(final LatLng sourceLatLng, final LatLng destLatLng) {
		try {
			RestClient.getGoogleApiServices().getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
					destLatLng.latitude + "," + destLatLng.longitude, false, "driving", false, new retrofit.Callback<SettleUserDebt>() {
						@Override
						public void success(SettleUserDebt settleUserDebt, Response response) {
							String result = new String(((TypedByteArray) response.getBody()).getBytes());
							List<LatLng> list = MapUtils.getLatLngListFromPath(result);
							if (list.size() > 0) {
								LatLngBounds.Builder builder = new LatLngBounds.Builder();

								PolylineOptions polylineOptions = new PolylineOptions();
								polylineOptions.width(ASSL.Xscale() * 5).color(MAP_PATH_COLOR).geodesic(true);
								for (int z = 0; z < list.size(); z++) {
									polylineOptions.add(list.get(z));
									builder.include(list.get(z));
								}

								final LatLngBounds latLngBounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 408);

								if (mapLite != null) {
									mapLite.clear();
									mapLite.addPolyline(polylineOptions);


									MarkerOptions markerOptionsS = new MarkerOptions();
									markerOptionsS.title("Start");
									markerOptionsS.position(sourceLatLng);
									markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(RideDetailsNewActivity.this, assl)));
									mapLite.addMarker(markerOptionsS);

									MarkerOptions markerOptionsE = new MarkerOptions();
									markerOptionsE.title("Start");
									markerOptionsE.position(destLatLng);
									markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
											.createCustomMarkerBitmap(RideDetailsNewActivity.this, assl, 50f, 69f, R.drawable.passenger)));
									mapLite.addMarker(markerOptionsE);


									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											try {
												float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
												mapLite.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,
														(int) (660f * minRatio), (int) (240f * minRatio),
														(int) (minRatio * 60)));
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}, 500);
								}
							}
						}

						@Override
						public void failure(RetrofitError error) {

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}
}
