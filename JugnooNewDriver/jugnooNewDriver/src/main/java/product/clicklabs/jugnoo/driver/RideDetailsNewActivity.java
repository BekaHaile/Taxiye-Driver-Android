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

import product.clicklabs.jugnoo.driver.adapters.DeliveryAddressListAdapter;
import product.clicklabs.jugnoo.driver.adapters.RideInfoTilesAdapter;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleDirectionWaypoints;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.FareStructureInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import product.clicklabs.jugnoo.driver.fragments.RideIssueFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.LinearLayoutManagerForResizableRecyclerView;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RideDetailsNewActivity extends BaseFragmentActivity {

	LinearLayout linearLayoutTo;

	RelativeLayout relative, relativeContainer, relativeLayoutCreateTicket;

	Button backBtn, buttonReportIssue;
	TextView title;

	TextView dateTimeValue, distanceValue, rideTimeValue, waitTimeValue,textViewTicketDate,
			textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText,
			textViewFromValue, textViewActualFareValue, textViewStatus, textViewEngID;

	NonScrollListView listViewDeliveryAddresses;
	DeliveryAddressListAdapter deliveryAddressListAdapter;
	RideIssueFragment rideIssueFragment;

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
	private ArrayList<LatLng> latLngs = new ArrayList<>();
	private SearchResult searchResultGlobal;
	InfoTileResponse.Tile.Extras extras;
	CustomerInfo customerInfo;
	String accessToken;


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



		relative = (RelativeLayout) findViewById(R.id.relative);
		assl = new ASSL(RideDetailsNewActivity.this, relative, 1134, 720, false);

		relativeContainer = (RelativeLayout) findViewById(R.id.relativeContainer);
		backBtn = (Button) findViewById(R.id.backBtn);
		relativeLayoutCreateTicket = (RelativeLayout) findViewById(R.id.relativeLayoutCreateTicket);
		buttonReportIssue = (Button) findViewById(R.id.buttonReportIssue);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
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

		int viewHeight = (int)(65f * ASSL.Yscale()) * extras.getRideParam().size();
		recyclerViewRideInfo.getLayoutParams().height = viewHeight;

		fareStructureInfos = new ArrayList<>();
		rideInfoTilesAdapter = new RideInfoTilesAdapter(this, fareStructureInfos);
		recyclerViewRideInfo.setAdapter(rideInfoTilesAdapter);


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
							for(InfoTileResponse.Tile.Extras.DropCoordinate dropCoordinate : extras.getDropCoordinates()){
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
							new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.new_orange_path),
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
		textViewActualFareValue.setTypeface(Fonts.mavenRegular(this));
		relativeContainer.setVisibility(View.GONE);
		textViewActualFare = (TextView) findViewById(R.id.textViewActualFare);
		textViewActualFare.setTypeface(Fonts.mavenRegular(this));
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance);
		textViewAccountBalance.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid);
		textViewCustomerPaid.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		textViewFromValue = (TextView) findViewById(R.id.textViewFromValue);
		textViewFromValue.setTypeface(Fonts.mavenRegular(this));
		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Fonts.mavenBold(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.distanceValue)).setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.rideTimeValue)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.waitTimeValue)).setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.waitTimeText)).setTypeface(Fonts.mavenRegular(this));

		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonReportIssue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				relativeContainer.setVisibility(View.VISIBLE);
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.right_in, R.anim.right_out,R.anim.right_in, R.anim.left_out)
						.add(R.id.relativeContainer, rideIssueFragment, RideIssueFragment.class.getName())
						.addToBackStack(RideIssueFragment.class.getName())
						.commit();


			}
		});


		if (extras != null) {

			textViewEngID.setText(getResources().getString(R.string.id)+": "+extras.getEngagementId());
			dateTimeValue.setText(DateOperations.convertMonthDayViaFormat(extras.getDate())+", "+extras.getTime());

			distanceValue.setText( Utils.getDecimalFormatForMoney().format(extras.getDistance())
					+ " " + getResources().getString(R.string.km));

			rideTimeValue.setText(extras.getRideTime() + " " + getResources().getString(R.string.min));

			if (extras.getWaitTime() == 0) {
				relativeWaitingTime.setVisibility(View.GONE);
			} else {
				relativeWaitingTime.setVisibility(View.VISIBLE);
				waitTimeValue.setText( extras.getWaitTime() + " " + getResources().getString(R.string.min));
			}


			textViewActualFare.setText(Utils.getAbsAmount(this, extras.getEarning()));
			textViewActualFareValue.setText(Utils.getAbsAmount(this, extras.getEarning()));
			textViewCustomerPaid.setText(Utils.getAbsAmount(this, extras.getPaidUsingCash()));
			textViewAccountBalance.setText(Utils.getAbsAmount(this, extras.getAccount()));

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
				textViewStatus.setTextColor(getResources().getColor(R.color.red_v2));
				textViewTicketDate.setText(extras.getTicketDate());
			}

			textViewFromValue.setText(extras.getFrom());
			fareStructureInfos.addAll(extras.getRideParam());

			if(extras.getTo().size() == 0){
				linearLayoutTo.setVisibility(View.GONE);
			}
			deliveryAddressList.addAll(extras.getTo());
			deliveryAddressListAdapter.notifyDataSetChanged();
			rideInfoTilesAdapter.notifyDataSetChanged();
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
