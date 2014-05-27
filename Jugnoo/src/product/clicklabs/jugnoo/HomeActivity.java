package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HomeActivity extends FragmentActivity implements DetectRideStart, RefreshDriverLocations, RequestRideInterrupt {

	
	
	DrawerLayout drawerLayout;
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	ProgressBar profileImgProgress;
	ImageView profileImg;
	TextView userName;
	
	RelativeLayout driverModeRl;
	TextView driverModeText;
	ImageView driverModeToggle;
	
	RelativeLayout inviteFriendRl;
	TextView inviteFriendText;
	
	RelativeLayout helpRl;
	TextView helpText;
	
	RelativeLayout aboutRl;
	TextView aboutText;
	
	
	
	//Favorite bar
	LinearLayout favoriteRl;
	ListView favoriteList;
	FavoriteListAdapter favoriteListAdapter;
	
	
	
	//Top RL
	RelativeLayout topRl;
	Button menuBtn, backBtn, favBtn;
	TextView title;
	ImageView jugnooLogo;
	
	
	
	//Passenger main layout
	RelativeLayout passengerMainLayout;
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	//Initial layout
	RelativeLayout initialLayout;
	EditText searchEt;
	Button search, myLocationBtn, requestRideBtn, initialCancelRideBtn;
	RelativeLayout nearestDriverRl;
	TextView nearestDriverText;
	ProgressBar nearestDriverProgress;
	
	
	//Before request final layout
	RelativeLayout beforeRequestFinalLayout;
	Button cancelRequestBtn;
	ProgressBar assignedDriverProgress;
	TextView assignedDriverText;
	
	
	//Request Final Layout
	RelativeLayout requestFinalLayout;
	ProgressBar driverImageProgress, driverCarProgress;
	ImageView driverImage, driverCarImage;
	TextView driverName, driverTime;
	Button callDriverBtn;
	
	
	
	
	//In Ride layout
	RelativeLayout inRideLayout;
	TextView rideInProgressText;
	
	
	
	//Search layout
	RelativeLayout searchLayout;
	ListView searchList;
	SearchListAdapter searchListAdapter;
	
	
	
	
	
	
	
	//Center Location Layout
	RelativeLayout centreLocationRl, centreInfoRl;
	ProgressBar centreInfoProgress;
	TextView centreLocationSnippet;
	
	
	
	
	
	//On Map
	TextView distance, fare, rate;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Driver main layout 
	RelativeLayout driverMainLayout;
	
	
	//Driver initial layout
	RelativeLayout driverInitialLayout;
	RelativeLayout driverNewRideRequestRl;
	TextView driverNewRideRequestText;
	TextView driverNewRideRequestClickText;
	Button driverInitialMyLocationBtn;
	
	
	
	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn;
	
	
	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;
	
	TextView driverPassengerName;
	TextView driverPassengerRatingValue;
	Button driverPassengerCallBtn;
	ProgressBar driverPassengerImageProgress;
	ImageView driverPassengerImage;
	
	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideMyLocationBtn;
	TextView driverStartRideText;
	SlideButton driverStartRideSlider;
	
	//End ride layout
	RelativeLayout driverInRideMainRl;
	PausableChronometer waitChronometer;
	Button driverWaitBtn;
	TextView driverEndRideText;
	SlideButtonInvert driverEndRideSlider;
	int waitStart = 2;
	
	
	
	
	//Review layout
	RelativeLayout endRideReviewRl;
		
	ImageView reviewUserImgBlured, reviewUserImage;
	ProgressBar reviewUserImgProgress;
	TextView reviewUserName, reviewUserRating, reviewReachedDestinationText, reviewDistanceText, reviewDistanceValue, reviewFareText, reviewFareValue, reviewRatingText;
	RatingBar reviewRatingBar;
	Button reviewSubmitBtn;
	
	
	
	
	PowerManager.WakeLock mWakeLock;
	
	
	
	
	ArrayList<Polyline> polyLinesAL = new ArrayList<Polyline>();
	
	ArrayList<Location> locations = new ArrayList<Location>();
	
	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>(); 
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	double totalDistance = 0;
	double unitFare = 5;
	double totalPrice = 0;
	
	
	
	static boolean startTracking = true;
	static Location myLocation;
	
	
	
	LocationManager locationManager;
	
	static PassengerScreenMode passengerScreenMode;
	
	static UserMode userMode;
	
	static DriverScreenMode driverScreenMode;
	
	
	BeforeCancelRequestAsync beforeCancelRequestAsync;
	
	Marker pickupLocationMarker;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		
		CStartRideService.detectRideStart = HomeActivity.this;
		CUpdateDriverLocationsService.refreshDriverLocations = HomeActivity.this;
		CRequestRideService.requestRideInterrupt = HomeActivity.this;
		
		
		startTracking = false;
		
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		
		
		
		
		new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);
		
		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		profileImgProgress = (ProgressBar) findViewById(R.id.profileImgProgress);
		profileImg = (ImageView) findViewById(R.id.profileImg);
		userName = (TextView) findViewById(R.id.userName);
		
		driverModeRl = (RelativeLayout) findViewById(R.id.driverModeRl);
		driverModeText = (TextView) findViewById(R.id.driverModeText);
		driverModeToggle = (ImageView) findViewById(R.id.driverModeToggle);
		
		inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
		inviteFriendText = (TextView) findViewById(R.id.inviteFriendText);
		
		helpRl = (RelativeLayout) findViewById(R.id.helpRl);
		helpText = (TextView) findViewById(R.id.helpText);
		
		aboutRl = (RelativeLayout) findViewById(R.id.aboutRl);
		aboutText = (TextView) findViewById(R.id.aboutText);
		
		
		
		
		//Favorite bar
		favoriteRl = (LinearLayout) findViewById(R.id.favoriteRl);
		favoriteList = (ListView) findViewById(R.id.favoriteList);
		favoriteListAdapter = new FavoriteListAdapter();
		favoriteList.setAdapter(favoriteListAdapter);
		
		
		
		//Top RL
		topRl = (RelativeLayout) findViewById(R.id.topRl);
		menuBtn = (Button) findViewById(R.id.menuBtn);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		jugnooLogo = (ImageView) findViewById(R.id.jugnooLogo);
		favBtn = (Button) findViewById(R.id.favBtn);
		
		
		menuBtn.setVisibility(View.VISIBLE);
		jugnooLogo.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		
		
		
		
		
		
		//Map Layout
		mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		
		
		
		
		
		
		
		//Passenger main layout
		passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);
		
		
		
		//Initial layout 
		initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
		
		searchEt = (EditText) findViewById(R.id.searchEt);
		search = (Button) findViewById(R.id.search);
		
		myLocationBtn = (Button) findViewById(R.id.myLocationBtn);
		requestRideBtn = (Button) findViewById(R.id.requestRideBtn);
		initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn);
		
		nearestDriverRl = (RelativeLayout) findViewById(R.id.nearestDriverRl);
		nearestDriverText = (TextView) findViewById(R.id.nearestDriverText);
		nearestDriverProgress = (ProgressBar) findViewById(R.id.nearestDriverProgress);
		
		
		
		
		
		//Before request final layout
		beforeRequestFinalLayout = (RelativeLayout) findViewById(R.id.beforeRequestFinalLayout);

		cancelRequestBtn = (Button) findViewById(R.id.cancelRequestBtn);

		assignedDriverText = (TextView) findViewById(R.id.assignedDriverText);
		assignedDriverProgress = (ProgressBar) findViewById(R.id.assignedDriverProgress);
		
		
		
		
		//Request Final Layout
		requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);
		
		driverImageProgress = (ProgressBar) findViewById(R.id.driverImageProgress);
		driverCarProgress = (ProgressBar) findViewById(R.id.driverCarProgress);
		driverImage = (ImageView) findViewById(R.id.driverImage);
		driverCarImage = (ImageView) findViewById(R.id.driverCarImage);
		
		driverName = (TextView) findViewById(R.id.driverName);
		driverTime = (TextView) findViewById(R.id.driverTime);
		callDriverBtn = (Button) findViewById(R.id.callDriverBtn);
		
		
		
		
		//In Ride layout
		inRideLayout = (RelativeLayout) findViewById(R.id.inRideLayout);
		rideInProgressText = (TextView) findViewById(R.id.rideInProgressText);
		distance = (TextView) findViewById(R.id.distance);
		fare = (TextView) findViewById(R.id.fare);
		rate = (TextView) findViewById(R.id.rate);
		
		
		

		
		
		//Center location layout
		centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
		centreInfoRl = (RelativeLayout) findViewById(R.id.centreInfoRl);
		centreInfoProgress = (ProgressBar) findViewById(R.id.centreInfoProgress);
		centreLocationSnippet = (TextView) findViewById(R.id.centreLocationSnippet);
		
		
		
		
		
		
		//Search Layout
		searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
		searchList = (ListView) findViewById(R.id.searchList);
		searchListAdapter = new SearchListAdapter();
		searchList.setAdapter(searchListAdapter);
		
		
		
		
		
		
		
		
		
		// Driver main layout 
		driverMainLayout = (RelativeLayout) findViewById(R.id.driverMainLayout);
		
		
		//Driver initial layout
		driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
		driverNewRideRequestRl = (RelativeLayout) findViewById(R.id.driverNewRideRequestRl);
		driverNewRideRequestText = (TextView) findViewById(R.id.driverNewRideRequestText);
		driverNewRideRequestClickText = (TextView) findViewById(R.id.driverNewRideRequestClickText);
		driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
		
		
		
		// Driver Request Accept layout
		driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
		driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn);
		driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn);
		driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
		
		
		
		// Driver engaged layout
		driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
		

		driverPassengerName = (TextView) findViewById(R.id.driverPassengerName);
		driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue);
		driverPassengerCallBtn = (Button) findViewById(R.id.driverPassengerCallBtn);
		driverPassengerImageProgress = (ProgressBar) findViewById(R.id.driverPassengerImageProgress);
		driverPassengerImage = (ImageView) findViewById(R.id.driverPassengerImage);
		
		//Start ride layout
		driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
		driverStartRideMyLocationBtn = (Button) findViewById(R.id.driverStartRideMyLocationBtn);
		driverStartRideText = (TextView) findViewById(R.id.driverStartRideText);
		driverStartRideSlider = (SlideButton) findViewById(R.id.driverStartRideSlider);
		
		
		//End ride layout
		driverInRideMainRl = (RelativeLayout) findViewById(R.id.driverInRideMainRl);
		waitChronometer = (PausableChronometer) findViewById(R.id.waitChronometer);
		driverWaitBtn = (Button) findViewById(R.id.driverWaitBtn);
		driverEndRideText = (TextView) findViewById(R.id.driverEndRideText);
		driverEndRideSlider = (SlideButtonInvert) findViewById(R.id.driverEndRideSlider);
		waitStart = 2;
		
		
		waitChronometer.setText("00:00:00");
		waitChronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer cArg) {
						long time = SystemClock.elapsedRealtime() - cArg.getBase();
						int h = (int) (time / 3600000);
						int m = (int) (time - h * 3600000) / 60000;
						int s = (int) (time - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						cArg.setText(hh + ":" + mm + ":" + ss);
					}
				});
		
		
		
		//Review Layout
		endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);
		
		reviewUserImgBlured = (ImageView) findViewById(R.id.reviewUserImgBlured);
		reviewUserImage = (ImageView) findViewById(R.id.reviewUserImage);
		reviewUserImgProgress = (ProgressBar) findViewById(R.id.reviewUserImgProgress);
		
		reviewUserName = (TextView) findViewById(R.id.reviewUserName);
		reviewUserRating = (TextView) findViewById(R.id.reviewUserRating);
		reviewReachedDestinationText = (TextView) findViewById(R.id.reviewReachedDestinationText);
		reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText);
		reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue);
		reviewFareText = (TextView) findViewById(R.id.reviewFareText);
		reviewFareValue = (TextView) findViewById(R.id.reviewFareValue);
		reviewRatingText = (TextView) findViewById(R.id.reviewRatingText);
		
		reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
		reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn);
		
		
		
		
		
		
				 
		
		
				
		
		
		
		
		
		
		
		
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
			}
		});
		
		
		favBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(favoriteRl);
			}
		});
		
		
		
		
		
		
		driverModeToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.e("userMode","="+userMode);
				
				if(userMode == UserMode.DRIVER){
					userMode = UserMode.PASSENGER;
					driverModeToggle.setImageResource(R.drawable.off);
					
					passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
				}
				else{
					userMode = UserMode.DRIVER;
					driverModeToggle.setImageResource(R.drawable.on);
					
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
				}
				
				drawerLayout.closeDrawer(menuLayout);
				
				switchUserScreen(userMode);
			}
		});
		
		
		
		inviteFriendRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(menuLayout);
				startActivity(new Intent(HomeActivity.this, InviteFacebookFriendsActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		
		
		
		
		search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String searchPlace = searchEt.getText().toString();
				
				if(searchPlace.length() > 0){
					searchGooglePlaces(searchPlace);
					hideSoftKeyboard();
				}
			}
		});
		
		
		searchEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						search.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		requestRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(requestRideBtn.getText().toString().equalsIgnoreCase("Request Ride")){
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						if(Data.driverInfos.size() > 0){
							requestRideBtn.setText("Assigning driver...");
							
							passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
							Data.engagementId = "";
							Data.mapTarget = map.getCameraPosition().target;
							
							switchPassengerScreen(passengerScreenMode);
							
							startService(new Intent(HomeActivity.this, CRequestRideService.class));
						}
						else{
							new DialogPopup().alertPopup(HomeActivity.this, "", "No driver available currently");
						}
					}
					else{
						new DialogPopup().alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
					}
				}
				
			}
		});
		
		
		initialCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelCustomerRequestAsync(HomeActivity.this, 0);
			}
		});
		
		
		cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelCustomerRequestAsync(HomeActivity.this, 1);
			}
		});
		
		
		callDriverBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		rideInProgressText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_RIDE_END;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		centreInfoRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		beforeRequestFinalLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		requestFinalLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		menuLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
//		inRideLayout.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		
		endRideReviewRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		driverNewRideRequestRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
				switchDriverScreen(driverScreenMode);
				
			}
		});
		
		
		driverAcceptRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverScreenMode = DriverScreenMode.D_START_RIDE;
				switchDriverScreen(driverScreenMode);
			}
		});
		
		
		
		
		driverStartRideSlider.setSlideButtonListener(new SlideButtonListener() {  
	        @Override
	        public void handleSlide() {
	            
	        	driverStartRideText.setVisibility(View.GONE);
	        	
	        	driverScreenMode = DriverScreenMode.D_IN_RIDE;
				switchDriverScreen(driverScreenMode);
				
	        }
	    });
		
		
		driverWaitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(waitStart == 2){
					waitChronometer.setBase(SystemClock.elapsedRealtime());
					waitChronometer.start();
					driverWaitBtn.setBackgroundResource(R.drawable.red_btn_selector);
					driverWaitBtn.setText("Stop wait");
					waitStart = 1;
				}
				else{
					if(waitStart == 1){
						waitChronometer.stop();
						driverWaitBtn.setBackgroundResource(R.drawable.blue_btn_selector);
						driverWaitBtn.setText("Start wait");
						waitStart = 0;
					}
					else if(waitStart == 0){
						waitChronometer.start();
						driverWaitBtn.setBackgroundResource(R.drawable.red_btn_selector);
						driverWaitBtn.setText("Stop wait");
						waitStart = 1;
					}
				}
				
			}
		});
		
		
		
		driverEndRideSlider.setSlideButtonListener(new SlideButtonInvertListener() {
			
			@Override
			public void handleSlide() {
				
				driverEndRideText.setVisibility(View.GONE);
				waitChronometer.stop();
				
				long elapsedMillis = SystemClock.elapsedRealtime() - waitChronometer.getBase();
				long seconds = elapsedMillis / 1000;
				long minutes = seconds / 60;
				long leftSeconds = seconds % 60;
				String min = (minutes > 9)? ""+minutes : "0"+minutes;
				String sec = (leftSeconds > 9)? ""+leftSeconds : "0"+leftSeconds;
				
				Toast.makeText(getApplicationContext(), "wait = "+min + ":" + sec, Toast.LENGTH_SHORT).show();
	        	
	        	driverScreenMode = DriverScreenMode.D_RIDE_END;
				switchDriverScreen(driverScreenMode);
				
			}
		});
		
		fare.setText("Fare = "+unitFare);
		
		polyLinesAL = new ArrayList<Polyline>();
		
		locations = new ArrayList<Location>();
		
		
																	// map object initialized
		if(map != null){
			map.getUiSettings().setZoomControlsEnabled(true);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.getUiSettings().setAllGesturesEnabled(true);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Data.getChandigarhLatLng(), 12));
			
			
			map.setOnMyLocationChangeListener(myLocationChangeListener);
			
			// Find ZoomControl view
			View zoomControls = mapFragment.getView().findViewById(0x1);

			if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			    // ZoomControl is inside of RelativeLayout
			    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

			    // Align it to - parent top|left
			    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			    // Update margins, set to 10dp
			    final int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 250*ASSL.Yscale(),
			            getResources().getDisplayMetrics());
			    final int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20*ASSL.Yscale(),
			            getResources().getDisplayMetrics());
			    
			    params.setMargins(0, marginTop, marginRight, 0);
			}
			
			
			
			
			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					
				}
			});
			
			
			map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng arg0) {
					
					Log.e("Map Long click arg0","=="+arg0);
					
				}
			});
			
			map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker arg0) {
					
					if(arg0.getTitle().equalsIgnoreCase("pickup location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Your Pickup Location", "Start");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else{
						return true;
					}
					
					
				}
			});
			
			
			
			
			
			
			
			
			
			
			Data.favoriteLocations.add(new FavoriteLocation("1", "1", new LatLng(Data.chandigarhLatLng.latitude+0.2,
					Data.chandigarhLatLng.longitude)));

			Data.favoriteLocations.add(new FavoriteLocation("2", "2", new LatLng(Data.chandigarhLatLng.latitude-0.2,
					Data.chandigarhLatLng.longitude)));
			
			Data.favoriteLocations.add(new FavoriteLocation("3", "3", new LatLng(Data.chandigarhLatLng.latitude,
					Data.chandigarhLatLng.longitude+0.2)));
			
			Data.favoriteLocations.add(new FavoriteLocation("4", "4", new LatLng(Data.chandigarhLatLng.latitude,
					Data.chandigarhLatLng.longitude-0.2)));
			
			favoriteListAdapter.notifyDataSetChanged();
			
			
			new MapStateListener(map, mapFragment, this) {
				  @Override
				  public void onMapTouched() {
				    // Map touched
					  Log.e("onMapTouched","=onMapTouched");
				  }

				  @Override
				  public void onMapReleased() {
				    // Map released
					  Log.e("onMapReleased","=onMapReleased");
				  }

				  @Override
				  public void onMapUnsettled() {
				    // Map unsettled
					  Log.e("onMapUnsettled","=onMapUnsettled");
					  if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  centreInfoRl.setVisibility(View.INVISIBLE);
						  centreInfoProgress.setVisibility(View.GONE);
					  }
				  }

				  @Override
				  public void onMapSettled() {
				    // Map settled
					  Log.e("onMapSettled","=onMapSettled");
					  if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
						  new GetDistanceTimeAddress(map.getCameraPosition().target).execute();
					  }
				  }
				};
			
			
			
			
			
			myLocationBtn.setOnClickListener(mapMyLocationClick);
			
		}
		
		
		
		
		
		
		
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		
		
		
		

		userMode = UserMode.PASSENGER;
		passengerScreenMode = PassengerScreenMode.P_INITIAL;
		driverScreenMode = DriverScreenMode.D_INITIAL;
		
		
		if(userMode == UserMode.DRIVER){
			driverModeToggle.setImageResource(R.drawable.on);
		}
		else{
			driverModeToggle.setImageResource(R.drawable.off);
		}
		
		switchUserScreen(userMode);
		switchPassengerScreen(passengerScreenMode);
		switchDriverScreen(driverScreenMode);
	  
		
		setUserData();
		
	}
	
	
	OnClickListener mapMyLocationClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(myLocation != null){
				map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
			}
			else{
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	public void setUserData(){
		
		userName.setText(Data.userData.userName);
		
		AQuery aq = new AQuery(profileImg);
		aq.id(profileImg).progress(profileImgProgress).image(Data.userData.userImage, Data.imageOptionsFullRound());
		
		
	}
	
	
	
	public void switchUserScreen(UserMode mode){
		
		switch(mode){
		
			case DRIVER:
				passengerMainLayout.setVisibility(View.GONE);
				driverMainLayout.setVisibility(View.VISIBLE);
				
				favBtn.setVisibility(View.GONE);
				
				break;
			
				
				
				
			case PASSENGER:
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
				favBtn.setVisibility(View.VISIBLE);
				
				break;
			
				
				
				
			default:
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
				favBtn.setVisibility(View.VISIBLE);
		
		
		}
		
	}
	
	
	
	public void switchDriverScreen(DriverScreenMode mode){
		
		if(mode == DriverScreenMode.D_RIDE_END){
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey_opaque));
		}
		
		switch(mode){
		
			case D_INITIAL:
				
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
			
				break;
				
				
			case D_REQUEST_ACCEPT:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.VISIBLE);
				driverEngagedLayout.setVisibility(View.GONE);
			
				break;
				
				
				
			case D_START_RIDE:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverStartRideSlider.setProgress(0);
				driverStartRideText.setVisibility(View.VISIBLE);
				
				driverStartRideMainRl.setVisibility(View.VISIBLE);
				driverInRideMainRl.setVisibility(View.GONE);
				
			
				break;
				
				
			case D_IN_RIDE:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverEndRideSlider.setProgress(100);
				driverEndRideText.setVisibility(View.VISIBLE);
				
				driverStartRideMainRl.setVisibility(View.GONE);
				driverInRideMainRl.setVisibility(View.VISIBLE);
				
			
				break;
				
				
			case D_RIDE_END:
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				
			
				break;
				
				
			
			default:
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
		
		}
		
	}
	
	
	
	public void switchPassengerScreen(PassengerScreenMode mode){
		
		
		if(mode == PassengerScreenMode.P_RIDE_END){
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey_opaque));
		}
		
		
		switch(mode){
		
			case P_INITIAL:

				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				nearestDriverRl.setVisibility(View.VISIBLE);
				initialCancelRideBtn.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				startService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
				
				break;
				
				
			case P_ASSIGNING:
				
				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				if(map != null){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.mapTarget);
					markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_ball1));
					
					pickupLocationMarker = map.addMarker(markerOptions);
				}
				
				
				nearestDriverRl.setVisibility(View.GONE);
				initialCancelRideBtn.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
				
				break;
				
		
			case P_SEARCH:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.VISIBLE);
				
				
				menuBtn.setVisibility(View.GONE);
				jugnooLogo.setVisibility(View.GONE);
				backBtn.setVisibility(View.VISIBLE);
				title.setVisibility(View.VISIBLE);
				favBtn.setVisibility(View.GONE);
				
				title.setText("Search results");
				
				searchListAdapter.notifyDataSetChanged();

				
				break;
				
				
				
			case P_BEFORE_REQUEST_FINAL:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				
				break;
				
				
				
			case P_REQUEST_FINAL:

				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
				
				
			case P_IN_RIDE:
				
				startTracking = true;
				
				
				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
			case P_RIDE_END:
				
				startTracking = false;
				
				initialLayout.setVisibility(View.GONE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				searchLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.GONE);

				
				break;
				
				
			default:

				initialLayout.setVisibility(View.VISIBLE);
				beforeRequestFinalLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				inRideLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.VISIBLE);
				searchLayout.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				favBtn.setVisibility(View.VISIBLE);

				
		}
		
		
		
		
	}
	
	
	/**
	 * Hides keyboard
	 * @param context
	 */
	public void hideSoftKeyboard() {
		try{
			InputMethodManager mgr = (InputMethodManager) HomeActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
		} catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			
			Log.e("event","=="+event);
			
//			switch(event){
//				case GpsStatus.GPS_EVENT_FIRST_FIX:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_FIRST_FIX "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_SATELLITE_STATUS "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_STARTED:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_STARTED "+event, Toast.LENGTH_SHORT).show();
//					break;
//					
//				case GpsStatus.GPS_EVENT_STOPPED:
//					Toast.makeText(getApplicationContext(), "GPS_EVENT_STOPPED "+event, Toast.LENGTH_SHORT).show();
//					break;
//			
//				default:
//					Toast.makeText(getApplicationContext(), " "+event, Toast.LENGTH_SHORT).show();
//			
//			}
			
			
			
			
		}
	};
	
	
	
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	
	void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("The app needs active GPS connection. Do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	
	@Override
	protected void onResume() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
		super.onResume();
		
		

		if(locationManager == null)
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        buildAlertMessageNoGps();
	    }
	    else{
	    	locationManager.addGpsStatusListener(gpsStatusListener);
	    }

		
	    
	}
	
	
	@Override
	protected void onPause() {
		this.mWakeLock.release();
		
		locationManager.removeGpsStatusListener(gpsStatusListener);
		
		super.onPause();
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		if(passengerScreenMode == PassengerScreenMode.P_SEARCH){
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
			switchPassengerScreen(passengerScreenMode);
		}
	}
	
	    
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        
        Data.locationFetcher.destroy();
        
        ASSL.closeActivity(drawerLayout);
        stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
        stopService(new Intent(HomeActivity.this, CStartRideService.class));
        stopService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
        
        System.gc();
        
    }
	
	
	OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {
		
		@Override
		public void onMyLocationChange(Location location) {

//			Log.e("location","=="+location);
			
			HomeActivity.myLocation = location;
			
			
			if(HomeActivity.startTracking){
			
			LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			Log.e("locations.size()", "="+locations.size());
			if(locations.size() > 0){
				
				LatLng lastLatLng = new LatLng(locations.get(locations.size()-1).getLatitude(), locations.get(locations.size()-1).getLongitude());
				
				double displacement = distance(lastLatLng, currentLatLng);
				Log.e("displacement", "="+displacement);
				
				totalDistance = totalDistance + displacement;
				totalPrice = totalPrice + (displacement * unitFare);
				
				Polyline line = map.addPolyline(new PolylineOptions()
		        .add(lastLatLng, currentLatLng)
		        .width(5)
		        .color(Color.RED).geodesic(true));

		        polyLinesAL.add(line);
				
				
			}
			else if(locations.size() == 0){
				totalDistance = 0;
				totalPrice = 0;
				
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.snippet("Start");
				markerOptions.title("Start");
				markerOptions.position(currentLatLng);
				CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start", "Start");
				map.addMarker(markerOptions);
				map.setInfoWindowAdapter(customIW);
			}
			
			map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
			
			
			distance.setText("Distance = "+decimalFormat.format(totalDistance));
			rate.setText("Rate = "+decimalFormat.format(totalPrice));
			

			locations.add(location);
			
			}
		}
	};
	
	
	double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

	

	
	
	class ViewHolderSearch {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class SearchListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderSearch holder;

		public SearchListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return searchResults.size();
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
				
				holder = new ViewHolderSearch();
				convertView = mInflater.inflate(R.layout.search_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); 
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderSearch) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+searchResults.get(position).name + "\n" + searchResults.get(position).address);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderSearch) v.getTag();
					
					passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
					
					SearchResult searchResult = searchResults.get(holder.id);
					
					
					Log.e("searchResult.latLng ==",">"+searchResult.latLng);
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, 12), 2000, null);
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	class ViewHolderFavorite {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class FavoriteListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderFavorite holder;

		public FavoriteListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return Data.favoriteLocations.size();
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
				
				holder = new ViewHolderFavorite();
				convertView = mInflater.inflate(R.layout.search_list_item, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); 
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderFavorite) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+Data.favoriteLocations.get(position).name + "\n" + Data.favoriteLocations.get(position).address);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderFavorite) v.getTag();
					
					FavoriteLocation favoriteLocation = Data.favoriteLocations.get(holder.id);
					
					Log.e("searchResult.latLng ==",">"+favoriteLocation.latLng);
					
					drawerLayout.closeDrawer(favoriteRl);
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(favoriteLocation.latLng, 12), 2000, null);
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	

	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=30.75,76.78&destinations=30.78,76.79&language=EN&sensor=false
	
	public String makeURL(LatLng source, LatLng destination){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(source.longitude));
        urlString.append("&destinations=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&language=EN&sensor=false&alternatives=false");
        return urlString.toString();
	}
	
	
	
	class GetDistanceTimeAddress extends AsyncTask<Void, Void, String>{
	    String url;
	    
	    String distance, duration, fullAddress;
	    
	    LatLng destination;
	    
	    public GetDistanceTimeAddress(LatLng destination){
	    	this.distance = "";
	    	this.duration = "";
	    	this.destination = destination;
	    }
	    

	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	centreInfoRl.setVisibility(View.INVISIBLE);
				centreInfoProgress.setVisibility(View.VISIBLE);
	        	nearestDriverProgress.setVisibility(View.VISIBLE);
	 	        nearestDriverText.setVisibility(View.GONE);
	        }
	        else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	        	centreInfoRl.setVisibility(View.VISIBLE);
				centreInfoProgress.setVisibility(View.INVISIBLE);
	        	assignedDriverProgress.setVisibility(View.VISIBLE);
	        	assignedDriverText.setVisibility(View.GONE);
	        }
	        
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	try{
	    		
	    		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	    			fullAddress = getAddress(destination.latitude, destination.longitude);
	    			Log.e("fullAddress",">"+fullAddress);
	    		}
	    		
	    		LatLng source = null;
				
	    		if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	    			double minDistance = 999999999;
		    		for(int i=0; i<Data.driverInfos.size(); i++){
		    			if(distance(destination, Data.driverInfos.get(i).latLng) < minDistance){
		    				minDistance = distance(destination, Data.driverInfos.get(i).latLng);
		    				source = Data.driverInfos.get(i).latLng;
		    			}
		    		}
	    		}
	    		else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	    			source = Data.assignedDriverInfo.latLng;
	    		}
	    		
	    		
	    		if(source == null){
	    			return "error";
	    		}
	    			
	    		this.url = makeURL(source, destination);
	    		
		    	SimpleJSONParser jParser = new SimpleJSONParser();
		    	
		    	String response = jParser.getJSONFromUrl(url);
		    	
		    	JSONObject jsonObject = new JSONObject(response);
		    	
		    	
		    	String status = jsonObject.getString("status");
		    	
		    	if("OK".equalsIgnoreCase(status)){
		    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
		    		
		    		distance = element0.getJSONObject("distance").getString("text") ;
		    		
		    		duration = element0.getJSONObject("duration").getString("text");
		    		
		    		
		    		if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
		    			Data.assignedDriverInfo.distanceToReach = distance;
		    			Data.assignedDriverInfo.durationToReach = duration;
		    		}

		    		return "Distance: " + distance + "\n" + "Duration: " + duration;
		    		
		    	}
	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	
	        return "error";
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	 if(fullAddress != null && !"".equalsIgnoreCase(fullAddress)){
	 				centreLocationSnippet.setText(fullAddress);
	 				centreInfoRl.setVisibility(View.VISIBLE);
	 			}
	 			else{
	 				centreInfoRl.setVisibility(View.INVISIBLE);
	 			}
	 			centreInfoProgress.setVisibility(View.GONE);
    		}
	        
	        
	       
			
	        
	        if(!"error".equalsIgnoreCase(result)){
			        
		        String distanceString = "";
		        
		        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
		        	nearestDriverProgress.setVisibility(View.GONE);
		 	        nearestDriverText.setVisibility(View.VISIBLE);
		 	        
		 	       if(!"".equalsIgnoreCase(duration) && !"".equalsIgnoreCase(distance)){
	       	 		distanceString = "Nearest driver is " + distance + " " + duration + " away.";
			        }
			        else if(!"".equalsIgnoreCase(duration)){
			        	distanceString = "Nearest driver is " + duration + " away.";
			        }
			        else if(!"".equalsIgnoreCase(distance)){
			        	distanceString = "Nearest driver is " + distance + " away.";
			        }
			        else{
			        	distanceString = "Could not find nearest driver's distance.";
			        }
		 	        
		 	       	nearestDriverText.setText(distanceString);
		 	       
		        }
		        else if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
		        	assignedDriverProgress.setVisibility(View.GONE);
		        	assignedDriverText.setVisibility(View.VISIBLE);
		        	
		        	if(!"".equalsIgnoreCase(duration) && !"".equalsIgnoreCase(distance)){
	        	 		distanceString = "Your ride is " + distance + " and will arrive \n in approximately " + duration + ".";
			        }
			        else{
			        	distanceString = "Could not find nearest driver's distance.";
			        }
		        	assignedDriverText.setText(distanceString);
		        	
		        }
		        
	        }
	        else{
		        nearestDriverText.setVisibility(View.GONE);
		        assignedDriverText.setVisibility(View.GONE);
	        }
	        
	        if(passengerScreenMode == PassengerScreenMode.P_INITIAL){
	        	startService(new Intent(HomeActivity.this, CUpdateDriverLocationsService.class));
	        }
	        
	        if(passengerScreenMode == PassengerScreenMode.P_BEFORE_REQUEST_FINAL){
	        	beforeCancelRequestAsync = new BeforeCancelRequestAsync();
	        	beforeCancelRequestAsync.execute();
	        }
	        
	    }
	    
	    
	    
	}
	
	

	
	
	
	
	
	/**
	 * To search addresses related to particular address available on google
	 */
	public void searchGooglePlaces(String searchPlace) {
		
		final ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "","Searching... ", true);
		
		RequestParams params = new RequestParams();

		searchResults.clear();

		String ignr2 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="
				+ ""
				+ ","
				+ ""
				+ "&radius=50000"
				+ "&query="
				+ searchPlace
				+ "&sensor=true&key="+Data.MAPS_BROWSER_KEY;
		// "https://maps.googleapis.com/maps/api/place/textsearch/json?location=%f,%f&radius=2bb0000&query=%@&sensor=true&key=%@";

		ignr2 = ignr2.replaceAll(" ", "%20");

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(ignr2, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.e("request result", response);
				new SimpleJSONParser().writeJSONToFile(response, "googlePlaceJson");
				try {
					JSONArray info = null;
					JSONObject jj = new JSONObject(response);
					info = jj.getJSONArray("results");
					Log.v("converted", info + "");
					for (int a = 0; a < info.length(); a++) {
						JSONObject first = info.getJSONObject(a);
						Log.e("first" + a, "" + first);
					}
					Log.v("info.len....", "" + info.length());
					for (int i = 0; i < info.length(); i++) {
						// printing the values to the logcat
						try {
							SearchResult searchResult = new SearchResult(info.getJSONObject(i).getString("name"),
									info.getJSONObject(i).getString("formatted_address"), 
									new LatLng(
									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
									info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")
									));
							
							searchResults.add(searchResult);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < searchResults.size(); i++) {
						Log.i("Results name : ....", "" + searchResults.get(i).name);
					}
					passengerScreenMode = PassengerScreenMode.P_SEARCH;
					switchPassengerScreen(passengerScreenMode);
					
					Toast.makeText(getApplicationContext(), ""+searchResults.size() + " results found.", Toast.LENGTH_LONG).show();
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("errorr at response", "" + e.toString());
				}
				
				dialog.dismiss();
			}

			@Override
			public void onFailure(Throwable arg0) {
				try {
					Log.e("request fail", arg0.getMessage().toString());
				} catch (Exception e) {
					Log.e("moving from", e.toString());
				}
				dialog.dismiss();
			}
		});
	}
	
	
	
	
	String getAddress(double curLatitude, double curLongitude) {
    	String fullAddress = "";

        try {

        	
        	Log.i("curLatitude ",">"+curLatitude);
        	Log.i("curLongitude ",">"+curLongitude);
        	
            JSONObject jsonObj = new JSONObject(new SimpleJSONParser().getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/json?" +
            		"latlng=" + curLatitude + "," + curLongitude 
            		+ "&sensor=true"));
            
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                fullAddress = zero.getString("formatted_address");

                Log.e("Results.length() ==","="+Results.length());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fullAddress;
    }

	
	
	
	
	/**
	 * ASync for login from server
	 */
	public void getAssignedDriverInfoAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("driver_id", Data.driverId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("driver_id", "=" + Data.driverId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/get_driver_info", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									
									JSONObject driverData = jObj.getJSONObject("driver_data");
									
									Data.assignedDriverInfo = new DriverInfo(Data.driverId, driverData.getDouble("latitude"), driverData.getDouble("longitude"), 
											driverData.getString("name"), driverData.getString("image"), driverData.getString("car_image"), 
											driverData.getString("phone_number"));
									
									
									
									passengerScreenMode = PassengerScreenMode.P_BEFORE_REQUEST_FINAL;
									switchPassengerScreen(passengerScreenMode);
									
									new GetDistanceTimeAddress(Data.mapTarget).execute();
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	class BeforeCancelRequestAsync extends AsyncTask<String, Integer, String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			try{
				Thread.sleep(10000);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
			switchPassengerScreen(passengerScreenMode);
			
			
			driverName.setText(Data.assignedDriverInfo.name);
			driverTime.setText("Will arrive in "+Data.assignedDriverInfo.durationToReach);
			
			AQuery aq = new AQuery(driverImage);
			aq.id(driverImage).progress(driverImageProgress).image(Data.assignedDriverInfo.image, Data.imageOptionsRound());
			
			AQuery aq1 = new AQuery(driverCarImage);
			aq1.id(driverCarImage).progress(driverCarProgress).image(Data.assignedDriverInfo.carImage, Data.imageOptionsRound());
			
		}
		
	}
	
	
	
	
	/**
	 * ASync for cancelCustomerRequestAsync from server
	 */
	public void cancelCustomerRequestAsync(final Activity activity, final int switchCase) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("driver_id", Data.driverId);
			params.put("engage_id", Data.engagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("driver_id", "=" + Data.driverId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/cancel_the_req", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"cancelled sucessfully"}//result

									if(switchCase == 0){
										stopService(new Intent(HomeActivity.this, CRequestRideService.class));
										requestRideBtn.setText("Request Ride");
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
										
										
										if(map != null && pickupLocationMarker != null){
											pickupLocationMarker.remove();
										}
										
										new GetDistanceTimeAddress(map.getCameraPosition().target).execute();
									}
									else{
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
										
										if(beforeCancelRequestAsync != null){
											beforeCancelRequestAsync.cancel(true);
											beforeCancelRequestAsync = null;
										}
										
										if(map != null && pickupLocationMarker != null){
											pickupLocationMarker.remove();
										}
										
										new GetDistanceTimeAddress(map.getCameraPosition().target).execute();
									}
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	/**
	 * ASync for login from server
	 */
	public void changeDriverModeAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("driver_id", Data.driverId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("driver_id", "=" + Data.driverId);
			
		
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(Data.SERVER_TIMEOUT);
			client.post(Data.SERVER_URL + "/switch_to_driver_mode", params,
					new AsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									
									JSONObject driverData = jObj.getJSONObject("driver_data");
									
									Data.assignedDriverInfo = new DriverInfo(Data.driverId, driverData.getDouble("latitude"), driverData.getDouble("longitude"), 
											driverData.getString("name"), driverData.getString("image"), driverData.getString("car_image"), 
											driverData.getString("phone_number"));
									
									
									
									passengerScreenMode = PassengerScreenMode.P_BEFORE_REQUEST_FINAL;
									switchPassengerScreen(passengerScreenMode);
									
									new GetDistanceTimeAddress(Data.mapTarget).execute();
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
	
						@Override
						public void onFailure(Throwable arg0) {
							Log.e("request fail", arg0.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	

	@Override
	public void sendIntent() {
		Log.e("in ","here");
		passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
		switchPassengerScreen(passengerScreenMode);
		
	}



	@Override
	public void refreshDriverLocations() {
		
		if(map != null){
			
			map.clear();
			
			for(int i=0; i<Data.driverInfos.size(); i++){
				DriverInfo driverInfo = Data.driverInfos.get(i);
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.title(""+driverInfo.userId);
				markerOptions.snippet(""+driverInfo.userId);
				markerOptions.position(driverInfo.latLng);
				markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_android));
				
				map.addMarker(markerOptions);
			}
			
		}
		
		
		
	}



	// 0 = not found   1 = accept
	@Override
	public void requestRideInterrupt(int switchCase) {
		
		stopService(new Intent(HomeActivity.this, CRequestRideService.class));
		
		if(switchCase == 0){
			
			new DialogPopup().alertPopup(this, "", "No Driver available right now.");
			requestRideBtn.setText("Request Ride");
			passengerScreenMode = PassengerScreenMode.P_INITIAL;
			switchPassengerScreen(passengerScreenMode);
			
		}
		else if(switchCase == 1){
			getAssignedDriverInfoAsync(this);
		}
		
	}
	
	
	
	
	
}