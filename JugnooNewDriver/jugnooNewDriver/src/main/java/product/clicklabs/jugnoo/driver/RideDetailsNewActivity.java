package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import product.clicklabs.jugnoo.driver.adapters.DeliveryAddressListAdapter;
import product.clicklabs.jugnoo.driver.adapters.RideInfoTilesAdapter;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleDirectionWaypoints;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import product.clicklabs.jugnoo.driver.fragments.RideIssueFragment;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class RideDetailsNewActivity extends BaseFragmentActivity {
	private static final String TAG = RideDetailsNewActivity.class.getSimpleName();

	LinearLayout linearLayoutTo;

	RelativeLayout relative, relativeContainer, relativeLayoutCreateTicket;

	ImageView backBtn;
	Button buttonReportIssue, buttonGetSupport;
	TextView title;

	TextView dateTimeValue, distanceValue, rideTimeValue, waitTimeValue, textViewTicketDate,
			textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText,
			textViewFromValue, textViewActualFareValue, textViewStatus, textViewEngID;

	NonScrollListView listViewDeliveryAddresses;
	DeliveryAddressListAdapter deliveryAddressListAdapter;
	RideIssueFragment rideIssueFragment;

	ImageView imageViewRequestType, imageViewSeprator;
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
	private ArrayList<LatLng> latLngs = new ArrayList<>();
	private SearchResult searchResultGlobal;
	Tile.Extras extras;
	CustomerInfo customerInfo;
	String accessToken;


	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

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
			extras = new Gson().fromJson(extra, Tile.Extras.class);


		} catch (Exception e) {
			e.printStackTrace();
		}


		relative = (RelativeLayout) findViewById(R.id.relative);
		assl = new ASSL(RideDetailsNewActivity.this, relative, 1134, 720, false);

		relativeContainer = (RelativeLayout) findViewById(R.id.relativeContainer);
		backBtn = findViewById(R.id.backBtn);
		relativeLayoutCreateTicket = (RelativeLayout) findViewById(R.id.relativeLayoutCreateTicket);
		buttonReportIssue = (Button) findViewById(R.id.buttonReportIssue);
		buttonGetSupport = (Button) findViewById(R.id.buttonGetSupport);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
		title.setText(R.string.trip_details);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		textViewStatus.setTypeface(Fonts.mavenRegular(this));
		textViewEngID = (TextView) findViewById(R.id.textViewEngID);
		textViewEngID.setTypeface(Fonts.mavenRegular(this));
		textViewTicketDate = (TextView) findViewById(R.id.textViewTicketDate);
		textViewTicketDate.setTypeface(Fonts.mavenRegular(this));

//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);
		linearLayoutTo = (LinearLayout) findViewById(R.id.linearLayoutTo);
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
		int viewHeight = 0;
		if (extras.getRideParam().size() < 3) {
			viewHeight = (int) (80f * ASSL.Yscale()) * extras.getRideParam().size();
		} else {
			viewHeight = (int) (72f * ASSL.Yscale()) * extras.getRideParam().size();
		}
		recyclerViewRideInfo.getLayoutParams().height = viewHeight;

		if (Data.isCaptive()) {
			recyclerViewRideInfo.setVisibility(View.GONE);
		} else {
			fareStructureInfos = new ArrayList<>();
			rideInfoTilesAdapter = new RideInfoTilesAdapter(this, fareStructureInfos);
			recyclerViewRideInfo.setAdapter(rideInfoTilesAdapter);
		}


		ArrayList<String> deliveryAddressList = new ArrayList<>();
		listViewDeliveryAddresses = (NonScrollListView) findViewById(R.id.listViewDeliveryAddresses);
		deliveryAddressListAdapter = new DeliveryAddressListAdapter(RideDetailsNewActivity.this, deliveryAddressList);
		listViewDeliveryAddresses.setAdapter(deliveryAddressListAdapter);

		try {
			rideIssueFragment = new RideIssueFragment();
			Bundle bundle = new Bundle();
			accessToken = Data.userData.accessToken;
			bundle.putString("access_token", accessToken);
			bundle.putString("engagement_id", String.valueOf(extras.getEngagementId()));
			rideIssueFragment.setArguments(bundle);
		} catch (Exception e) {
			e.printStackTrace();
		}


		((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLite)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				mapLite = googleMap;
				if (mapLite != null) {
					mapLite.getUiSettings().setAllGesturesEnabled(false);
					mapLite.getUiSettings().setZoomGesturesEnabled(false);
					mapLite.getUiSettings().setZoomControlsEnabled(false);
					if (ActivityCompat.checkSelfPermission(RideDetailsNewActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RideDetailsNewActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {
						mapLite.setMyLocationEnabled(false);
					} else { Log.d(TAG, "ACCESS_FINE_LOCATION NOT GRANTED"); }

					mapLite.getUiSettings().setTiltGesturesEnabled(false);
					mapLite.getUiSettings().setMyLocationButtonEnabled(false);
					mapLite.setMapType(GoogleMap.MAP_TYPE_NORMAL);

					mapLite.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
						@Override
						public boolean onMarkerClick(Marker marker) {
							return true;
						}
					});
					final LatLngBounds.Builder builder = new LatLngBounds.Builder();
					try {
						latLngs.clear();

						if(extras.getPickupLatitude() != null && extras.getPickupLongitude() != null){
							latLngs.add(new LatLng(extras.getPickupLatitude(), extras.getPickupLongitude()));
							MarkerOptions markerOptionsS = new MarkerOptions();
							markerOptionsS.title("Start");
							markerOptionsS.position(new LatLng(extras.getPickupLatitude(), extras.getPickupLongitude()));
							markerOptionsS.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
									.createCustomMarkerBitmap(RideDetailsNewActivity.this, assl, 24f, 24f, R.drawable.start_marker_v2)));
							mapLite.addMarker(markerOptionsS);
							builder.include(new LatLng(extras.getPickupLatitude(), extras.getPickupLongitude()));
						}
						if(extras.getDropCoordinates() != null){
							for(Tile.Extras.DropCoordinate dropCoordinate : extras.getDropCoordinates()){
								if(dropCoordinate.getLatitude() != 0 && dropCoordinate.getLongitude()!= 0) {
									latLngs.add(new LatLng(dropCoordinate.getLatitude(), dropCoordinate.getLongitude()));
									MarkerOptions markerOptionsE = new MarkerOptions();
									markerOptionsE.title("End");
									markerOptionsE.position(new LatLng(dropCoordinate.getLatitude(), dropCoordinate.getLongitude()));
									markerOptionsE.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
											.createCustomMarkerBitmap(RideDetailsNewActivity.this, assl, 24f, 24f, R.drawable.end_marker_v2)));
									mapLite.addMarker(markerOptionsE);
									builder.include(new LatLng(dropCoordinate.getLatitude(), dropCoordinate.getLongitude()));
								}
							}
						}
						if(latLngs.size() > 1){
							new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.themeColorLight), true,
									new ApiGoogleDirectionWaypoints.Callback() {
										@Override
										public void onPre() {

										}

										@Override
										public boolean showPath() {
											return true;
										}

										@Override
										public void polylineOptionGenerated(PolylineOptions polylineOptions) {
											mapLite.addPolyline(polylineOptions);

											if(mapLite != null){
												mapLite.addPolyline(polylineOptions);
											}
										}

										@Override
										public void onFinish() {
										}
									}).execute();
						}

						final LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
									mapLite.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
											(int) (660f * minRatio), (int) (240f * minRatio),
											(int) (minRatio * 60)));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 500);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});


		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Fonts.mavenRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		distanceValue.setTypeface(Fonts.mavenRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue);
		rideTimeValue.setTypeface(Fonts.mavenRegular(this));
		waitTimeValue = (TextView) findViewById(R.id.waitTimeValue);
		waitTimeValue.setTypeface(Fonts.mavenRegular(this));
		textViewActualFareValue = (TextView) findViewById(R.id.textViewActualFareValue);
		textViewActualFareValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		relativeContainer.setVisibility(View.GONE);
		textViewActualFare = (TextView) findViewById(R.id.textViewActualFare);
		textViewActualFare.setTypeface(Fonts.mavenRegular(this));
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance);
		textViewAccountBalance.setTypeface(Fonts.mavenRegular(this));
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid);
		textViewCustomerPaid.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewFromValue = (TextView) findViewById(R.id.textViewFromValue);
		textViewFromValue.setTypeface(Fonts.mavenRegular(this));
		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.distanceValue)).setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.rideTimeValue)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.waitTimeValue)).setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.waitTimeText)).setTypeface(Fonts.mavenRegular(this));

		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);
		imageViewSeprator = (ImageView) findViewById(R.id.imageViewSeprator);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.TRIP_DETAIL+"_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});

		buttonReportIssue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.TRIP_DETAILS_REPORT_FARE_ISSUE);
				relativeContainer.setVisibility(View.VISIBLE);
				MyApplication.getInstance().logEvent(FirebaseEvents.TRIP_DETAIL + "_report_" + FirebaseEvents.FARE_ISSUE, null);
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.right_in, R.anim.right_out,R.anim.right_in, R.anim.left_out)
						.add(R.id.relativeContainer, rideIssueFragment, RideIssueFragment.class.getName())
						.addToBackStack(RideIssueFragment.class.getName())
						.commit();


			}
		});

		buttonGetSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeUtil.scheduleCallDriver(RideDetailsNewActivity.this);
			}
		});


		if (extras != null) {

			textViewEngID.setText(getResources().getString(R.string.id)+": "+extras.getEngagementId());
			dateTimeValue.setText(DateOperations.convertMonthDayViaFormat(extras.getDate())+", "+extras.getTime());

			distanceValue.setText(Utils.getKilometers(extras.getDistance(),this, extras.getDistanceUnit()));

			rideTimeValue.setText(extras.getRideTime() + " " + getResources().getString(R.string.min));

			if (extras.getWaitTime() == 0) {
				relativeWaitingTime.setVisibility(View.GONE);
			} else {
				relativeWaitingTime.setVisibility(View.VISIBLE);
				waitTimeValue.setText( extras.getWaitTime() + " " + getResources().getString(R.string.min));
			}




			if(getResources().getBoolean(R.bool.show_support_ride_detail)){
				if(extras.getTicketStatus() == 0){
					relativeLayoutCreateTicket.setVisibility(View.GONE);
				} else if(extras.getTicketStatus() == 1){
					buttonReportIssue.setVisibility(View.VISIBLE);
					textViewStatus.setVisibility(View.GONE);
					textViewTicketDate.setVisibility(View.GONE);

				} else if(extras.getTicketStatus() == 2){
					buttonReportIssue.setVisibility(View.GONE);
					textViewStatus.setVisibility(View.VISIBLE);
					textViewTicketDate.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.fair_complaint)+":"+getResources().getString(R.string.in_review));
					textViewTicketDate.setText(extras.getTicketDate());
					textViewStatus.setTextColor(getResources().getColor(R.color.status_pending));
				} else if(extras.getTicketStatus() == 3){
					buttonReportIssue.setVisibility(View.GONE);
					textViewStatus.setVisibility(View.VISIBLE);
					textViewTicketDate.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.fair_complaint)+":"+getResources().getString(R.string.success));
					textViewStatus.setTextColor(getResources().getColor(R.color.green_status));
					textViewTicketDate.setText(extras.getTicketDate());
				} else if(extras.getTicketStatus() == 4){
					buttonReportIssue.setVisibility(View.GONE);
					textViewStatus.setVisibility(View.VISIBLE);
					textViewTicketDate.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.fair_complaint)+":"+getResources().getString(R.string.rejected));
					textViewStatus.setTextColor(getResources().getColor(R.color.themeColor));
					textViewTicketDate.setText(extras.getTicketDate());
				}

				buttonGetSupport.setVisibility(Prefs.with(RideDetailsNewActivity.this).getInt(Constants.SHOW_IN_APP_CALL_US,0) == 1 ? View.VISIBLE : View.GONE);
			}else{
				buttonReportIssue.setVisibility(View.GONE);
				relativeLayoutCreateTicket.setVisibility(View.GONE);
			}


			textViewFromValue.setText(extras.getFrom());


			if(extras.getTo().size() == 0){
				linearLayoutTo.setVisibility(View.GONE);
			}
			deliveryAddressList.addAll(extras.getTo());
			deliveryAddressListAdapter.notifyDataSetChanged();

			if(Data.isCaptive()){
				textViewCustomerPaid.setText(Utils.getAbsAmount(this, extras.getPaidUsingCash(),extras.getCurrencyUnit()));
				imageViewSeprator.setVisibility(View.GONE);
				findViewById(R.id.rl_bank_deposit).setVisibility(View.GONE);
				findViewById(R.id.rlIncome).setVisibility(View.GONE);
				findViewById(R.id.iv_below_rl_income).setVisibility(View.GONE);
				textViewActualFare.setText(Utils.getKilometers(extras.getDistance(),this, extras.getDistanceUnit()));
			}else{
				if (extras.getEarning() <= 0) {
					imageViewSeprator.setVisibility(View.GONE);
				}else{
					imageViewSeprator.setVisibility(View.VISIBLE);

				}

				textViewActualFare.setText(Utils.getAbsAmount(this, extras.getEarning(),extras.getCurrencyUnit()));
				textViewActualFareValue.setText(Utils.getAbsAmount(this, extras.getEarning(),extras.getCurrencyUnit()));
				textViewCustomerPaid.setText(Utils.getAbsAmount(this, extras.getPaidUsingCash(),extras.getCurrencyUnit()));
				textViewAccountBalance.setText(Utils.getAbsAmount(this, extras.getAccount(),extras.getCurrencyUnit()));
				fareStructureInfos.addAll(extras.getRideParam());
				rideInfoTilesAdapter.notifyDataSetChanged();

			}

		} else {
			performBackPressed();
		}

	}

	public void setTicketState(final String date){
		buttonReportIssue.setVisibility(View.GONE);
		textViewStatus.setVisibility(View.VISIBLE);
		textViewTicketDate.setVisibility(View.VISIBLE);
		textViewStatus.setText(getResources().getString(R.string.fair_complaint) + ":" + getResources().getString(R.string.in_review));
		textViewTicketDate.setText(date);
		textViewStatus.setTextColor(getResources().getColor(R.color.status_pending));

	}

	public void performBackPressed() {
		if(getSupportFragmentManager().getBackStackEntryCount() == 1){
			super.onBackPressed();
		} else {
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}

	}

	@Override
	public void onBackPressed() {
		performBackPressed();

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

}
