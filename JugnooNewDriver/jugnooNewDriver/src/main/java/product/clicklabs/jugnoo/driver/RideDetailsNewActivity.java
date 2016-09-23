package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.RideInfoTilesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.LinearLayoutManagerForResizableRecyclerView;
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

	TextView dateTimeValue, distanceValue, rideTimeValue, waitTimeValue,
			textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText,
			textViewFromValue, textViewToValue, textViewActualFareValue;

	ImageView imageViewRequestType;
	public static final int MAP_PATH_COLOR = Color.RED;
	RelativeLayout relativeLayoutConvenienceCharges, relativeLayoutLuggageCharges,
			relativeLayoutCancelSubsidy, relativeLayoutJugnooCut, relativeWaitingTime;
	ArrayList<FareStructureInfo> fareStructureInfos = new ArrayList<>();
	RecyclerView recyclerViewRideInfo;
	RideInfoTilesAdapter rideInfoTilesAdapter;

	public static RideInfo openedRideInfo;
	public ASSL assl;
	Shader textShader;
	GoogleMap mapLite;
	private GoogleApiClient mGoogleApiClient;
	private LatLng pickupLatLng, dropLatLng;
	private SearchResult searchResultGlobal;
	InfoTileResponse.Tile.Extras extras;
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
			String extra = intent.getStringExtra("extras");
			extras = new Gson().fromJson(extra, InfoTileResponse.Tile.Extras.class);


		} catch (Exception e) {
			e.printStackTrace();
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
		textShader=new LinearGradient(0, 0, 0, 20,
				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
				new float[]{0, 1}, Shader.TileMode.CLAMP);
		title.getPaint().setShader(textShader);
		relativeLayoutConvenienceCharges = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharges);
		relativeLayoutLuggageCharges = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharges);
		relativeLayoutCancelSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutCancelSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);
		relativeWaitingTime = (RelativeLayout) findViewById(R.id.relativeWaitingTime);

		recyclerViewRideInfo = (RecyclerView) findViewById(R.id.recyclerViewRideInfo);
		recyclerViewRideInfo.setHasFixedSize(true);
		LinearLayoutManagerForResizableRecyclerView llm = new LinearLayoutManagerForResizableRecyclerView(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewRideInfo.setLayoutManager(llm);
		recyclerViewRideInfo.setItemAnimator(new DefaultItemAnimator());

		int viewHeight = (int)(65f * ASSL.Yscale()) * extras.getRideParam().size();
		recyclerViewRideInfo.getLayoutParams().height = viewHeight;

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
					mapLite.setMyLocationEnabled(false);
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


		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Data.latoRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		distanceValue.setTypeface(Data.latoRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue);
		rideTimeValue.setTypeface(Data.latoRegular(this));
		waitTimeValue = (TextView) findViewById(R.id.waitTimeValue);
		waitTimeValue.setTypeface(Data.latoRegular(this));
		textViewActualFareValue = (TextView) findViewById(R.id.textViewActualFareValue);
		textViewActualFareValue.setTypeface(Data.latoRegular(this));

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

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.waitTimeText)).setTypeface(Data.latoRegular(this));

		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (extras != null) {

			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(extras.getDate())));

			distanceValue.setText( Utils.getDecimalFormatForMoney().format(extras.getDistance())
					+ " " + getResources().getString(R.string.km));

			rideTimeValue.setText(extras.getRideTime() + " " + getResources().getString(R.string.min));

			if (extras.getWaitTime() == 0) {
				relativeWaitingTime.setVisibility(View.GONE);
			} else {
				relativeWaitingTime.setVisibility(View.VISIBLE);
				waitTimeValue.setText( extras.getWaitTime() + " " + getResources().getString(R.string.min));
			}


			textViewActualFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(extras.getEarning()));
			textViewActualFareValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(extras.getEarning()));
			textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(extras.getPaidUsingCash()));
			textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(extras.getAccount()))));

//			if (Double.parseDouble(openedRideInfo.accountBalance) < 0) {
//				textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(openedRideInfo.accountBalance)))));
//				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
//				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
//				textViewAccountBalanceText.setText(getResources().getString(R.string.money_to));
//			} else {
//				textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.accountBalance)));
//				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
//				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
//				textViewAccountBalanceText.setText(getResources().getString(R.string.account));
//			}
			textViewFromValue.setText(extras.getFrom());
			textViewToValue.setText(extras.getTo());
			fareStructureInfos.addAll(extras.getRideParam());
			rideInfoTilesAdapter.notifyDataSetChanged();
		} else {
			performBackPressed();
		}

		try {
			if(extras.getPickupLatitude() != null && extras.getPickupLongitude() != null){
				 pickupLatLng = new LatLng(extras.getPickupLatitude(), extras.getPickupLongitude());
			}
			if(extras.getDropLatitude() != null && extras.getDropLongitude() != null){
				 dropLatLng = new LatLng(extras.getDropLatitude(), extras.getDropLongitude());
			}

			if (pickupLatLng != null && dropLatLng != null) {
				getDirectionsAndComputeFare(pickupLatLng, dropLatLng);
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
									markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
											.createCustomMarkerBitmap(RideDetailsNewActivity.this, assl, 24f, 24f, R.drawable.start_marker_v2)));
									mapLite.addMarker(markerOptionsS);

									MarkerOptions markerOptionsE = new MarkerOptions();
									markerOptionsE.title("End");
									markerOptionsE.position(destLatLng);
									markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
											.createCustomMarkerBitmap(RideDetailsNewActivity.this, assl, 24f, 24f, R.drawable.end_marker_v2)));
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
