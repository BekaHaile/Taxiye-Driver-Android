package product.clicklabs.jugnoo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppMode;
import product.clicklabs.jugnoo.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.DriverRideRequest;
import product.clicklabs.jugnoo.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.LatLngPair;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PaymentMode;
import product.clicklabs.jugnoo.datastructure.ScheduleOperationMode;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.PausableChronometer;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.RoundBorderTransform;

@SuppressLint("DefaultLocale")
public class HomeActivity extends FragmentActivity implements AppInterruptHandler, LocationUpdate {

	
	
	DrawerLayout drawerLayout;																		// views declaration
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	
	
	
	ImageView profileImg;
	TextView userName;
	
	RelativeLayout inviteFriendRl;
	TextView inviteFriendText;
	
	RelativeLayout couponsRl;
	TextView couponsText;
	
	RelativeLayout walletRl;
	TextView walletText, textViewJugnooBalance;
	
	RelativeLayout bookingsRl;
	TextView bookingsText;
	
	RelativeLayout fareDetailsRl;
	TextView fareDetailsText;
	
	RelativeLayout helpRl;
	TextView helpText;
	
	RelativeLayout logoutRl;
	TextView logoutText;
	
	
	
	
	
	
	
	
	
	
	
	
	//Top RL
	RelativeLayout topRl;
	Button menuBtn, backBtn; //, favBtn;
	TextView title;
	ImageView jugnooLogo;
	Button checkServerBtn, toggleDebugModeBtn;
	ImageView jugnooShopImageView, jugnooMealsImageView;
	
	
	
	//Passenger main layout
	RelativeLayout passengerMainLayout;
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	//Initial layout
	RelativeLayout initialLayout;
	TextView textViewAssigningInProgress;
	Button myLocationBtn, requestRideBtn, initialCancelRideBtn;
	RelativeLayout nearestDriverRl;
	TextView nearestDriverText;
	ProgressBar nearestDriverProgress;
	
	
	
	//Request Final Layout
	RelativeLayout requestFinalLayout;
	ImageView driverImage, driverCarImage;
	TextView driverName, driverTime, driverCarNumberText;
	Button callDriverBtn;
	TextView inRideRideInProgress;
	ImageView passengerFreeRideIcon;
	Button customerInRideMyLocationBtn;
	TextView minFareText, minFareValue, fareAfterText, fareAfterValue;
	Button fareInfoBtn;
	
	
	
	
	//Center Location Layout
	RelativeLayout centreLocationRl;
	ImageView centreLocationPin;
	
	
	//Feedback layout
	RelativeLayout feedbackLayout;
	TextView feedbackRatingText, feedbackHelpText;
	RatingBar feedbackRatingBar;
	EditText feedbackEditText;
	Button feedbackSubmitBtn, feedbackSkipBtn;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Driver main layout 
	RelativeLayout driverMainLayout;
	
	
	//Driver initial layout
	RelativeLayout driverInitialLayout;
	RelativeLayout driverNewRideRequestRl;
	ListView driverRideRequestsList;
	TextView driverNewRideRequestText;
	TextView driverNewRideRequestClickText;
	Button driverInitialMyLocationBtn;
	RelativeLayout jugnooOffLayout;
	TextView jugnooOffText;
	
	DriverRequestListAdapter driverRequestListAdapter;
	
	
	
	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn;
	
	
	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;
	
	TextView driverPassengerName;
	TextView driverPassengerRatingValue;
	RelativeLayout driverPassengerCallRl;
	TextView driverPassengerCallText;
	TextView driverScheduledRideText;
	
	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideMyLocationBtn, driverStartRideBtn;
	Button driverCancelRideBtn;
	
	
	//End ride layout
	RelativeLayout driverInRideMainRl;
	Button driverEndRideMyLocationBtn;
	TextView driverIRDistanceText, driverIRDistanceValue, driverIRDistanceKmText;
	TextView driverIRFareText, driverIRFareRsText, driverIRFareValue;
	TextView driverRideTimeText;
	PausableChronometer rideTimeChronometer;
	RelativeLayout driverWaitRl;
	TextView driverWaitText;
	PausableChronometer waitChronometer;
	TextView inrideMinFareText, inrideMinFareValue, inrideFareAfterText, inrideFareAfterValue;
	Button inrideFareInfoBtn;
	Button driverEndRideBtn;
	
	public static int waitStart = 2;
	double distanceAfterWaitStarted = 0;
	
	
	
	
	
	//Review layout
	RelativeLayout endRideReviewRl;
		
	ImageView reviewUserImgBlured, reviewUserImage;
	TextView reviewUserName, reviewReachedDestinationText, 
	reviewDistanceText, reviewDistanceValue, 
	reviewWaitText, reviewWaitValue, reviewRideTimeText, reviewRideTimeValue,
	reviewFareText, reviewFareValue, 
	reviewRatingText;
	LinearLayout reviewRatingBarRl, endRideInfoRl;
	TextView jugnooRideOverText, takeFareText;
	RelativeLayout relativeLayoutCoupon;
	TextView textViewCouponTitle, textViewCouponSubTitle, textViewCouponPayTakeText, textViewCouponDiscountedFare;
	RatingBar reviewRatingBar;
	Button reviewSubmitBtn;
	TextView reviewMinFareText, reviewMinFareValue, reviewFareAfterText, reviewFareAfterValue;
	Button reviewFareInfoBtn;
	
	
	
	
	
																								// data variables declaration
	
	
	
	
	
	
	
	
	Location lastLocation;
	
	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>(); 
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
	
	static double totalDistance = -1, totalFare = 0;
	public static ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	
	
	static long previousWaitTime = 0, previousRideTime = 0;
	
	static String waitTime = "", rideTime = "";
	
	
	static Location myLocation;
	
	
	

	static UserMode userMode;
	static PassengerScreenMode passengerScreenMode;
	static DriverScreenMode driverScreenMode;
	
	
	
	
	
	
	GetDistanceTimeAddress getDistanceTimeAddress;
	
	
	
	
	Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker;
	MarkerOptions markerOptionsCustomerPickupLocation;
	
	static AppInterruptHandler appInterruptHandler;
	
	static Activity activity;
	
	boolean loggedOut = false, 
			zoomedToMyLocation = false, 
			mapTouchedOnce = false;
	boolean dontCallRefreshDriver = false;
	
	
	AlertDialog gpsDialogAlert;
	Dialog noDriversDialog;
	
	LocationFetcher lowPowerLF, highAccuracyLF;
	
	
	
	
	
	
	//TODO check final variables
	public static AppMode appMode;
	
	public static final int MAP_PATH_COLOR = Color.TRANSPARENT;
	public static final int D_TO_C_MAP_PATH_COLOR = Color.RED;
	
	public static final long DRIVER_START_RIDE_CHECK_METERS = 600; //in meters
	
	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000; //in milliseconds
	public static final double MAX_DISPLACEMENT_THRESHOLD = 200; //in meters
	
	public static final long SERVICE_RESTART_TIMER = 12 * 60 * 60 * 1000; //in milliseconds
	
	
	public static final float LOW_POWER_ACCURACY_CHECK = 2000, HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
	public static final float WAIT_FOR_ACCURACY_UPPER_BOUND = 2000, WAIT_FOR_ACCURACY_LOWER_BOUND = 200;  //in meters
	
	public static final long AUTO_RATING_DELAY = 5 * 60 * 1000; //in milliseconds
	
	public static final long MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT = 10 * 60000; //in milliseconds
	
	public static final double MAX_WAIT_TIME_ALLOWED_DISTANCE = 200; //in meters
	
	public static final double MAP_PAN_DISTANCE_CHECK = 50; // in meters
	
	public CheckForGPSAccuracyTimer checkForGPSAccuracyTimer;
	
	
	public static final String REQUEST_RIDE_BTN_NORMAL_TEXT = "Get an auto", REQUEST_RIDE_BTN_ASSIGNING_DRIVER_TEXT = "Assigning driver...";
	
	public ASSL assl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		HomeActivity.appInterruptHandler = HomeActivity.this;
		
		activity = this;
		
		loggedOut = false;
		zoomedToMyLocation = false;
		dontCallRefreshDriver = false;
		mapTouchedOnce = false;
		
		appMode = AppMode.NORMAL;
		
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		
		assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);
		
		
		
		
		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		
		
		
		
		profileImg = (ImageView) findViewById(R.id.profileImg);
		userName = (TextView) findViewById(R.id.userName); userName.setTypeface(Data.latoRegular(getApplicationContext()));
		
		inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
		inviteFriendText = (TextView) findViewById(R.id.inviteFriendText); inviteFriendText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		couponsRl = (RelativeLayout) findViewById(R.id.couponsRl);
		couponsText = (TextView) findViewById(R.id.couponsText); couponsText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		walletRl = (RelativeLayout) findViewById(R.id.walletRl);
		walletText = (TextView) findViewById(R.id.walletText); walletText.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewJugnooBalance = (TextView) findViewById(R.id.textViewJugnooBalance); textViewJugnooBalance.setTypeface(Data.latoRegular(getApplicationContext()));
		
		bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
		bookingsText = (TextView) findViewById(R.id.bookingsText); bookingsText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		fareDetailsRl = (RelativeLayout) findViewById(R.id.fareDetailsRl);
		fareDetailsText = (TextView) findViewById(R.id.fareDetailsText); fareDetailsText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		helpRl = (RelativeLayout) findViewById(R.id.helpRl);
		helpText = (TextView) findViewById(R.id.helpText); helpText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
		logoutText = (TextView) findViewById(R.id.logoutText); logoutText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		
		
		
		
		//Top RL
		topRl = (RelativeLayout) findViewById(R.id.topRl);
		menuBtn = (Button) findViewById(R.id.menuBtn);
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		jugnooLogo = (ImageView) findViewById(R.id.jugnooLogo);
		checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
		toggleDebugModeBtn = (Button) findViewById(R.id.toggleDebugModeBtn);
//		favBtn = (Button) findViewById(R.id.favBtn);
		jugnooShopImageView = (ImageView) findViewById(R.id.jugnooShopImageView);
		jugnooMealsImageView = (ImageView) findViewById(R.id.jugnooMealsImageView);
		
		
		
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
		textViewAssigningInProgress = (TextView) findViewById(R.id.textViewAssigningInProgress); textViewAssigningInProgress.setTypeface(Data.latoRegular(getApplicationContext()));
		
		myLocationBtn = (Button) findViewById(R.id.myLocationBtn);
		requestRideBtn = (Button) findViewById(R.id.requestRideBtn); requestRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn); initialCancelRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		nearestDriverRl = (RelativeLayout) findViewById(R.id.nearestDriverRl);
		nearestDriverText = (TextView) findViewById(R.id.nearestDriverText); nearestDriverText.setTypeface(Data.latoRegular(getApplicationContext()));
		nearestDriverProgress = (ProgressBar) findViewById(R.id.nearestDriverProgress);
		
		
		
		
		
		
		
		
		
		//Request Final Layout
		requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);
		
		driverImage = (ImageView) findViewById(R.id.driverImage);
		driverCarImage = (ImageView) findViewById(R.id.driverCarImage);
		
		driverName = (TextView) findViewById(R.id.driverName); driverName.setTypeface(Data.latoRegular(getApplicationContext()));
		driverTime = (TextView) findViewById(R.id.driverTime); driverTime.setTypeface(Data.latoRegular(getApplicationContext()));
		driverCarNumberText = (TextView) findViewById(R.id.driverCarNumberText); driverCarNumberText.setTypeface(Data.latoRegular(getApplicationContext()));
		callDriverBtn = (Button) findViewById(R.id.callDriverBtn); callDriverBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		inRideRideInProgress = (TextView) findViewById(R.id.inRideRideInProgress); inRideRideInProgress.setTypeface(Data.latoRegular(getApplicationContext()));
		passengerFreeRideIcon = (ImageView) findViewById(R.id.passengerFreeRideIcon);
		customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);
		
		minFareText = (TextView) findViewById(R.id.minFareText); minFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		minFareValue = (TextView) findViewById(R.id.minFareValue); minFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		fareAfterText = (TextView) findViewById(R.id.fareAfterText); fareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
		fareAfterValue = (TextView) findViewById(R.id.fareAfterValue); fareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
		fareInfoBtn = (Button) findViewById(R.id.fareInfoBtn);
		
		
		
		
		
		

		
		
		//Center location layout
		centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
		centreLocationPin = (ImageView) findViewById(R.id.centreLocationPin);
		
		
		//Feedback layout
		feedbackLayout = (RelativeLayout) findViewById(R.id.feedbackLayout);
		feedbackRatingText = (TextView) findViewById(R.id.feedbackRatingText); feedbackRatingText.setTypeface(Data.latoRegular(getApplicationContext()));
		feedbackHelpText = (TextView) findViewById(R.id.feedbackHelpText); feedbackHelpText.setTypeface(Data.latoRegular(getApplicationContext()));
		feedbackRatingBar = (RatingBar) findViewById(R.id.feedbackRatingBar);
		feedbackEditText = (EditText) findViewById(R.id.feedbackEditText); feedbackEditText.setTypeface(Data.latoRegular(getApplicationContext()));
		feedbackSubmitBtn = (Button) findViewById(R.id.feedbackSubmitBtn); feedbackSubmitBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		feedbackSkipBtn = (Button) findViewById(R.id.feedbackSkipBtn); feedbackSkipBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Driver main layout 
		driverMainLayout = (RelativeLayout) findViewById(R.id.driverMainLayout);
		
		
		
		//Driver initial layout
		driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
		driverNewRideRequestRl = (RelativeLayout) findViewById(R.id.driverNewRideRequestRl);
		driverRideRequestsList = (ListView) findViewById(R.id.driverRideRequestsList);
		driverNewRideRequestText = (TextView) findViewById(R.id.driverNewRideRequestText); driverNewRideRequestText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverNewRideRequestClickText = (TextView) findViewById(R.id.driverNewRideRequestClickText); driverNewRideRequestClickText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
		jugnooOffLayout = (RelativeLayout) findViewById(R.id.jugnooOffLayout);
		jugnooOffText = (TextView) findViewById(R.id.jugnooOffText); jugnooOffText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		driverNewRideRequestRl.setVisibility(View.GONE);
		driverRideRequestsList.setVisibility(View.GONE);
		
		driverRequestListAdapter = new DriverRequestListAdapter();
		driverRideRequestsList.setAdapter(driverRequestListAdapter);
		
		
		// Driver Request Accept layout
		driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
		driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
		driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn); driverAcceptRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn); driverCancelRequestBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
		
		
		
		// Driver engaged layout
		driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
		

		driverPassengerName = (TextView) findViewById(R.id.driverPassengerName); driverPassengerName.setTypeface(Data.latoRegular(getApplicationContext()));
		driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue); driverPassengerRatingValue.setTypeface(Data.latoRegular(getApplicationContext()));
		driverPassengerCallRl = (RelativeLayout) findViewById(R.id.driverPassengerCallRl);
		driverPassengerCallText = (TextView) findViewById(R.id.driverPassengerCallText); driverPassengerCallText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverScheduledRideText = (TextView) findViewById(R.id.driverScheduledRideText); driverScheduledRideText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		driverPassengerRatingValue.setVisibility(View.GONE);
		
		//Start ride layout
		driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
		driverStartRideMyLocationBtn = (Button) findViewById(R.id.driverStartRideMyLocationBtn);
		driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn); driverStartRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn); driverCancelRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));

		
		
		//End ride layout
		driverInRideMainRl = (RelativeLayout) findViewById(R.id.driverInRideMainRl);
		
		driverEndRideMyLocationBtn = (Button) findViewById(R.id.driverEndRideMyLocationBtn);
		
		driverIRDistanceText = (TextView) findViewById(R.id.driverIRDistanceText); driverIRDistanceText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverIRDistanceValue = (TextView) findViewById(R.id.driverIRDistanceValue); driverIRDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()));
		driverIRDistanceKmText = (TextView) findViewById(R.id.driverIRDistanceKmText); driverIRDistanceKmText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		driverIRFareText = (TextView) findViewById(R.id.driverIRFareText); driverIRFareText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverIRFareRsText = (TextView) findViewById(R.id.driverIRFareRsText); driverIRFareRsText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverIRFareValue = (TextView) findViewById(R.id.driverIRFareValue); driverIRFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		
		driverRideTimeText = (TextView) findViewById(R.id.driverRideTimeText); driverRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()));
		rideTimeChronometer = (PausableChronometer) findViewById(R.id.rideTimeChronometer); rideTimeChronometer.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		driverWaitRl = (RelativeLayout) findViewById(R.id.driverWaitRl);
		driverWaitText = (TextView) findViewById(R.id.driverWaitText); driverWaitText.setTypeface(Data.latoRegular(getApplicationContext()));
		waitChronometer = (PausableChronometer) findViewById(R.id.waitChronometer); waitChronometer.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

		inrideMinFareText = (TextView) findViewById(R.id.inrideMinFareText); inrideMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		inrideMinFareValue = (TextView) findViewById(R.id.inrideMinFareValue); inrideMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareAfterText = (TextView) findViewById(R.id.inrideFareAfterText); inrideFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareAfterValue = (TextView) findViewById(R.id.inrideFareAfterValue); inrideFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareInfoBtn = (Button) findViewById(R.id.inrideFareInfoBtn);
		
		driverWaitRl.setVisibility(View.GONE);
		
		driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn); driverEndRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		waitStart = 2;

//		driverEndRideSlider.setThumb(createEndRideThumbDrawable());
//		driverEndRideSlider.setThumbOffset((int)(5.0f * ASSL.Xscale()));
		
		rideTimeChronometer.setText("00:00:00");
		waitChronometer.setText("00:00:00");
		
		
		//Review Layout
		endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);
		
		reviewUserImgBlured = (ImageView) findViewById(R.id.reviewUserImgBlured);
		reviewUserImage = (ImageView) findViewById(R.id.reviewUserImage);
		
		reviewUserName = (TextView) findViewById(R.id.reviewUserName); reviewUserName.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewReachedDestinationText = (TextView) findViewById(R.id.reviewReachedDestinationText); reviewReachedDestinationText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText); reviewDistanceText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue); reviewDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewWaitText = (TextView) findViewById(R.id.reviewWaitText); reviewWaitText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewWaitValue = (TextView) findViewById(R.id.reviewWaitValue); reviewWaitValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewRideTimeText = (TextView) findViewById(R.id.reviewRideTimeText); reviewRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewRideTimeValue = (TextView) findViewById(R.id.reviewRideTimeValue); reviewRideTimeValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareText = (TextView) findViewById(R.id.reviewFareText); reviewFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewFareValue = (TextView) findViewById(R.id.reviewFareValue); reviewFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewRatingText = (TextView) findViewById(R.id.reviewRatingText); reviewRatingText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		reviewRatingBarRl = (LinearLayout) findViewById(R.id.reviewRatingBarRl);
		endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
		jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText); 
		jugnooRideOverText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		takeFareText = (TextView) findViewById(R.id.takeFareText); 
		takeFareText.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
		reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn); reviewSubmitBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		relativeLayoutCoupon = (RelativeLayout) findViewById(R.id.relativeLayoutCoupon);
		textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle); textViewCouponTitle.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		textViewCouponSubTitle = (TextView) findViewById(R.id.textViewCouponSubTitle); textViewCouponSubTitle.setTypeface(Data.museoSlab(getApplicationContext()));
		textViewCouponPayTakeText = (TextView) findViewById(R.id.textViewCouponPayTakeText); textViewCouponPayTakeText.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		textViewCouponDiscountedFare = (TextView) findViewById(R.id.textViewCouponDiscountedFare); textViewCouponDiscountedFare.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		
		reviewMinFareText = (TextView) findViewById(R.id.reviewMinFareText); reviewMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewMinFareValue = (TextView) findViewById(R.id.reviewMinFareValue); reviewMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareAfterText = (TextView) findViewById(R.id.reviewFareAfterText); reviewFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareAfterValue = (TextView) findViewById(R.id.reviewFareAfterValue); reviewFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareInfoBtn = (Button) findViewById(R.id.reviewFareInfoBtn);
		

		reviewRatingBarRl.setVisibility(View.GONE);
		endRideInfoRl.setVisibility(View.VISIBLE);
		
		
		reviewRatingBar.setRating(0);
		
		
				 
		
		
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Top bar events
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
			}
		});
		
		
//		favBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			}
//		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		
		checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				Toast.makeText(getApplicationContext(), "url = "+Data.SERVER_URL, Toast.LENGTH_SHORT).show();
				FlurryEventLogger.checkServerPressed(Data.userData.accessToken);
				
				return false;
			}
		});
		
		toggleDebugModeBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(HomeActivity.this);
				FlurryEventLogger.debugPressed(Data.userData.accessToken);
				return false;
			}
		});
		
		jugnooShopImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Data.userData != null){
		    		if(Data.userData.nukkadEnable == 1){
		    			startActivity(new Intent(HomeActivity.this, ItemInfosListActivity.class));
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
						FlurryEventLogger.christmasNewScreenOpened(Data.userData.accessToken);
		    		}
				}
				
			}
		});
		
		jugnooMealsImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					if(Data.userData != null){
						if(Data.userData.enableJugnooMeals == 1){
							CustomAppLauncher.launchApp(HomeActivity.this, Data.userData.jugnooMealsPackageName);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// menu events\
		
		inviteFriendRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, ShareActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.shareScreenOpened(Data.userData.accessToken);
			}
		});
		
		couponsRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AccountActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.couponsScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		walletRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, WalletActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.walletScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		fareDetailsRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendToFareDetails();
			}
		});
		
		
		helpRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, HelpActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		
		
		bookingsRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(userMode == UserMode.PASSENGER){
					startActivity(new Intent(HomeActivity.this, RidesActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				FlurryEventLogger.rideScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		
		
		
		
		logoutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(((userMode == UserMode.DRIVER) && (driverScreenMode == DriverScreenMode.D_INITIAL)) 
						|| ((userMode == UserMode.PASSENGER) && (passengerScreenMode == PassengerScreenMode.P_INITIAL))){
					logoutPopup(HomeActivity.this);
					FlurryEventLogger.logoutPressed(Data.userData.accessToken);
				}
				else{
					new DialogPopup().alertPopup(activity, "", "Ride in progress. You can logout only after the ride ends.");
					FlurryEventLogger.logoutPressedBetweenRide(Data.userData.accessToken);
				}
			}
		});
		
		
		menuLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Customer initial layout events
		requestRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(requestRideBtn.getText().toString().equalsIgnoreCase(REQUEST_RIDE_BTN_NORMAL_TEXT)){
						if(checkWorkingTime()){
							if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
								if(myLocation != null){
									callAnAutoPopup(HomeActivity.this);
								}
								else{
									Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
								}
							}
							else{
								new DialogPopup().alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
							}
						}
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		
		initialCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if("".equalsIgnoreCase(Data.cSessionId)){
					if(checkForGPSAccuracyTimer != null){
						if(checkForGPSAccuracyTimer.isRunning){
							checkForGPSAccuracyTimer.stopTimer();
							customerUIBackToInitialAfterCancel();
						}
					}
				}
				else{
					cancelCustomerRequestAsync(HomeActivity.this);
				}
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer request final layout events
		callDriverBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+Data.assignedDriverInfo.phoneNumber));
		        startActivity(callIntent);
		        FlurryEventLogger.callDriverPressed(Data.userData.accessToken, Data.assignedDriverInfo.userId, 
		        		Data.assignedDriverInfo.phoneNumber);
			}
		});
		
		fareInfoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendToFareDetails();
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer center location info layout events
//		setFavoriteBtn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				saveToFavoritePopup(HomeActivity.this, fullAddress, map.getCameraPosition().target);
//			}
//		});
//		
//		centreInfoRl.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		
		
		
		
		
		// feedback events
		
		feedbackRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if(rating >= 5){
					feedbackEditText.setHint("Superb, Tell us more.");
				}
				else if(rating >= 4){
					feedbackEditText.setHint("Awesome, Do let us know where we can improve.");
				}
				else if(rating >= 3){
					feedbackEditText.setHint("Great, Do let us know where we can improve.");
				}
				else if(rating >= 2){
					feedbackEditText.setHint("Ok, Your feedback is valuable. Let us know our shortcomings.");
				}
				else if(rating >= 1){
					feedbackEditText.setHint("Oh, We strive to serve you in best possible way. Yet you can let us know our shortcomings.");
				}
				else{
					feedbackRatingBar.setRating(1);
				}
			}
		});
		
		feedbackSkipBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				skipFeedbackForCustomerAsync(HomeActivity.this, Data.cEngagementId);
			}
		});
		
		feedbackSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int rating = (int) feedbackRatingBar.getRating();
				String feedbackStr = feedbackEditText.getText().toString().trim();
				if(rating > 0){
					if(feedbackStr.length() > 300){
						feedbackEditText.requestFocus();
						feedbackEditText.setError("Review must be in 300 letters.");
					}
					else{
						submitFeedbackToDriverAsync(HomeActivity.this, Data.cEngagementId, Data.cDriverId, rating, feedbackStr);
						FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, Data.cEngagementId);
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Please rate the ride.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver initial layout events
		driverNewRideRequestRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverNewRideRequestRl.setVisibility(View.GONE);
				driverRideRequestsList.setVisibility(View.VISIBLE);
			}
		});
		
		jugnooOffLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver accept layout events
		
		driverRequestAcceptBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				driverScreenMode = DriverScreenMode.D_INITIAL;
				switchDriverScreen(driverScreenMode);
			}
		});
		
		driverAcceptRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getBatteryPercentage() >= 10){
					 GCMIntentService.clearNotifications(HomeActivity.this);
					driverAcceptRideAsync(HomeActivity.this);
				}
				else{
					new DialogPopup().alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to accept the ride. Plugin to a power source to continue.");
				}
			}
		});
		
		
		driverCancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				driverRejectRequestAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver start ride layout events
		driverPassengerCallRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+Data.assignedCustomerInfo.phoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		driverStartRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(getBatteryPercentage() >= 10){
					startRidePopup(HomeActivity.this);
				}
				else{
					new DialogPopup().alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to start the ride. Plugin to a power source to continue.");
				}
	        }
		});
		
		
		
		driverCancelRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelRidePopup(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver in ride layout events 
		driverWaitRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(waitStart == 2){ 
					startWait();
				}
				else if(waitStart == 1){
					stopWait();
				}
				else if(waitStart == 0){
					startWait();
				}
			}
		});
		
		inrideFareInfoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fareInfoBtn.performClick();
			}
		});
		
		driverEndRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				endRidePopup(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// End ride review layout events
		endRideReviewRl.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
						
			}
		});
				
				
		reviewSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				if(userMode == UserMode.PASSENGER){
					feedbackEditText.setText("");
					feedbackRatingBar.setRating(5);
					passengerScreenMode = PassengerScreenMode.P_FEEDBACK;
					switchPassengerScreen(passengerScreenMode);
				}
				else{
					int rating = (int) reviewRatingBar.getRating();
					if(rating > 0){
						submitReviewAsync(HomeActivity.this, Data.dEngagementId, "0", Data.dCustomerId, ""+rating, "");
						FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, Data.dEngagementId);
					}
					else{
						submitReviewAsync(HomeActivity.this, Data.dEngagementId, "0", Data.dCustomerId, "5", "");
						FlurryEventLogger.reviewSubmitted(Data.userData.accessToken, Data.dEngagementId);
					}
				}
			}
		});
		
		reviewFareInfoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fareInfoBtn.performClick();
			}
		});
		
		
		
		
		
		
		lastLocation = null;
		
																	// map object initialized
		if(map != null){
			map.getUiSettings().setZoomGesturesEnabled(false);
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setTiltGesturesEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			//30.7500, 76.7800
			
			if(0 == Data.latitude && 0 == Data.longitude){
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
			}
			else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
			}
			
			
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
			
			
			map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(Marker arg0) {
					
					if(arg0.getTitle().equalsIgnoreCase("pickup location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Your Pickup Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("customer_current_location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
						map.setInfoWindowAdapter(customIW);
						
						return true;
					}
					
					else if(arg0.getTitle().equalsIgnoreCase("start ride location")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("driver position")){
						
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Driver Location", "");
						map.setInfoWindowAdapter(customIW);
						
						return false;
					}
					else if(arg0.getTitle().equalsIgnoreCase("driver shown to customer")){
						if(appMode == AppMode.DEBUG){
							String driverId = arg0.getSnippet();
							try{
								DriverInfo driverInfo = Data.driverInfos.get(Data.driverInfos.indexOf(new DriverInfo(driverId)));
								debugDriverInfoPopup(HomeActivity.this, driverInfo);
							} catch(Exception e){
								e.printStackTrace();
							}
						}
						
						return true;
					}
					else{
						return true;
					}
				}
			});
			
			
			
			
			
			
			
			
			
			new MapStateListener(map, mapFragment, this) {
				  @Override
				  public void onMapTouched() {
				    // Map touched
				  }

				  @Override
				  public void onMapReleased() {
				    // Map released
				  }

				  @Override
				  public void onMapUnsettled() {
				    // Map unsettled
					  if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
						  if(getDistanceTimeAddress != null){
							  getDistanceTimeAddress.cancel(true);
							  getDistanceTimeAddress = null;
						  }
					  }
				  }
				  
				  @Override
				  public void onMapSettled() {
				    // Map settled
					  callMapTouchedRefreshDrivers();
				  }
				};
			
			
			
			
			
			myLocationBtn.setOnClickListener(mapMyLocationClick);
			driverInitialMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverRequestAcceptMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverStartRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverEndRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			customerInRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			
		}
		
		
		
		
		
		try {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
	
			
			if(userMode == null){
				userMode = UserMode.PASSENGER;
			}
			
			if(passengerScreenMode == null){
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
			}
			
			if(driverScreenMode == null){
				driverScreenMode = DriverScreenMode.D_INITIAL;
			}
			
			enableJugnooShopUI();
			enableJugnooMealsUI();
			
			switchUserScreen(userMode);
			
			startUIAfterGettingUserStatus();
			
			
			changeJugnooONUI(Data.userData.isAvailable);
			
			changeExceptionalDriverUI();
			
			Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Data.SERVER_URL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Database2.getInstance(HomeActivity.this).close();
		
		showManualPatchPushReceivedDialog();
	}
	
	
	
	public void enableJugnooShopUI(){
		if(UserMode.PASSENGER == userMode){
			if(Data.userData != null){
	    		if(Data.userData.nukkadEnable == 1){
	    			jugnooShopImageView.setVisibility(View.VISIBLE);
	    			try{
	    				if(!"".equalsIgnoreCase(Data.userData.nukkadIcon)){
	    					Picasso.with(HomeActivity.this).load(Data.userData.nukkadIcon).into(jugnooShopImageView);
	    				}
	    				else{
	    					jugnooShopImageView.setImageResource(R.drawable.gift_button_selector);
	    				}
	    			}catch(Exception e){}
	    		}
	    		else{
	    			jugnooShopImageView.setVisibility(View.GONE);
	    		}
			}
			else{
				jugnooShopImageView.setVisibility(View.GONE);
			}
		}
		else{
			jugnooShopImageView.setVisibility(View.GONE);
		}
	}
	
	
	public void enableJugnooMealsUI(){
		if(UserMode.PASSENGER == userMode){
			if(Data.userData != null){
	    		if(Data.userData.enableJugnooMeals == 1){
	    			jugnooMealsImageView.setVisibility(View.VISIBLE);
	    		}
	    		else{
	    			jugnooMealsImageView.setVisibility(View.GONE);
	    		}
			}
			else{
				jugnooMealsImageView.setVisibility(View.GONE);
			}
		}
		else{
			jugnooMealsImageView.setVisibility(View.GONE);
		}
	}
	
	
	public void callMapTouchedRefreshDrivers(){
		if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
			  if(Data.userData.canChangeLocation == 1){
				  Data.pickupLatLng = map.getCameraPosition().target;
				  if(!dontCallRefreshDriver){
					  getDistanceTimeAddress = new GetDistanceTimeAddress(Data.pickupLatLng, false);
					  getDistanceTimeAddress.execute();
				  }
			  }
			  else{
				  if(myLocation != null){
					  Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
					  if(!dontCallRefreshDriver){
						  getDistanceTimeAddress = new GetDistanceTimeAddress(Data.pickupLatLng, false);
						  getDistanceTimeAddress.execute();
					  }
				  }
			  }
		  }
	}
	
	
	public void startUIAfterGettingUserStatus(){
		if(userMode == UserMode.PASSENGER){
			if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
				initiateRequestRide(false);
			}
			else if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
				switchPassengerScreen(passengerScreenMode);
			}
			else if(passengerScreenMode == PassengerScreenMode.P_RIDE_END){
				displayCouponApplied(Data.couponJSON);
				clearSPData();
				switchPassengerScreen(passengerScreenMode);
			}
			else{
				switchPassengerScreen(passengerScreenMode);
			}
			
		}
		else if(userMode == UserMode.DRIVER){
			switchDriverScreen(driverScreenMode);
		}
	}
	
	
	public void sendToFareDetails(){
		HelpParticularActivity.helpSection = HelpSection.FARE_DETAILS;
		startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
		FlurryEventLogger.fareDetailsOpened(Data.userData.accessToken);
	}
	
	
	public void startWait(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitChronometer.start();
				rideTimeChronometer.stop();
				driverWaitRl.setBackgroundResource(R.drawable.red_btn_selector);
				driverWaitText.setText(getResources().getString(R.string.stop_wait));
				waitStart = 1;
				distanceAfterWaitStarted = 0;
				startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 1);
			}
		});
	}
	
	
	
	public void stopWait(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitChronometer.stop();
				rideTimeChronometer.start();
				driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
				driverWaitText.setText(getResources().getString(R.string.start_wait));
				waitStart = 0;
				startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 0);
			}
		});
	}
	
	
	public void initiateRequestRide(boolean newRequest){
		
		if(newRequest){
			Data.cSessionId = "";
			Data.cEngagementId = "";
			
			if(Data.userData.canChangeLocation == 1){
				if(Data.pickupLatLng == null){
					Data.pickupLatLng = map.getCameraPosition().target;
				}
				double distance = MapUtils.distance(Data.pickupLatLng, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				if(distance > MAP_PAN_DISTANCE_CHECK){
					switchRequestRideUI();
					startTimerRequestRide();
				}
				else{
					checkForGPSAccuracyTimer = new CheckForGPSAccuracyTimer(HomeActivity.this, 0, 5000, System.currentTimeMillis(), 60000);
				}
			}
			else{
				checkForGPSAccuracyTimer = new CheckForGPSAccuracyTimer(HomeActivity.this, 0, 5000, System.currentTimeMillis(), 60000);
			}
		}
		else{
			if(myLocation == null){
				Data.pickupLatLng = new LatLng(0, 0);
			}
			else{
				Data.pickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			}
			
			Data.cEngagementId = "";
			
			
			
			switchRequestRideUI();
			startTimerRequestRide();
		}
		
	}
	
	
	
	public void switchRequestRideUI(){
		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_C_SESSION_ID, Data.cSessionId);
		editor.putString(Data.SP_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_LAST_LATITUDE, ""+Data.pickupLatLng.latitude);
		editor.putString(Data.SP_LAST_LONGITUDE, ""+Data.pickupLatLng.longitude);
		editor.commit();

		cancelTimerUpdateDrivers();
		
		HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
		switchPassengerScreen(passengerScreenMode);
	}
	
	
	
	public void changeJugnooON(int mode){
		if(mode == 1){
			if(myLocation != null){
				switchJugnooOnThroughServer(1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
			}
			else{
				Toast.makeText(HomeActivity.this, "Waiting for location...", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			switchJugnooOnThroughServer(0, new LatLng(0, 0));
		}
	}
	
	
	
	
	public void switchJugnooOnThroughServer(final int jugnooOnFlag, final LatLng latLng){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
					nameValuePairs.add(new BasicNameValuePair("latitude", ""+latLng.latitude));
					nameValuePairs.add(new BasicNameValuePair("longitude", ""+latLng.longitude));
					nameValuePairs.add(new BasicNameValuePair("flag", ""+jugnooOnFlag));
					
					Log.e("nameValuePairs in sending loc on jugnoo toggle","="+nameValuePairs);
					
					HttpRequester simpleJSONParser = new HttpRequester();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL+"/change_availability", nameValuePairs);
					
					Log.e("result ","="+result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
					
					//{"log":"Updated"}
					JSONObject jObj = new JSONObject(result);
					
					if(jObj.has("flag")){
						int flag = jObj.getInt("flag");
						if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
							if(jugnooOnFlag == 1){
								Data.userData.isAvailable = 1;
								changeJugnooONUI(1);
							}
							else{
								Data.userData.isAvailable = 0;
								changeJugnooONUI(0);
							}
						}
					}
					if(jObj.has("message")){
						String message = jObj.getString("message");
						showDialogFromBackground(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	
	public void showDialogFromBackground(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				new DialogPopup().alertPopup(HomeActivity.this, "", message);
			}
		});
	}
	
	public void switchJugnooOn(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				jugnooOffLayout.setVisibility(View.GONE);
			}
		});
	}
	
	public void switchJugnooOff(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				jugnooOffLayout.setVisibility(View.VISIBLE);
			}
		});
	}
	
	
	
	public void changeExceptionalDriverUI(){
		if(Data.userData.exceptionalDriver == 1){
			logoutRl.setVisibility(View.GONE);
		}
		else{
			logoutRl.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	public void changeJugnooONUI(int mode){
		Log.e("homeac changeJugnooONUI ====", "="+mode);
		if(mode == 1){
			switchJugnooOn();
		}
		else{
			switchJugnooOff();
		}
	}
	
	
	
	OnClickListener mapMyLocationClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(myLocation != null){
				if(map.getCameraPosition().zoom < 12){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12));
				}
				else if(map.getCameraPosition().zoom < 17){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 17));
				}
				else{
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
				reconnectLocationFetchers();
			}
		}
	};
	
	
	Handler reconnectionHandler = null;;
	
	public void reconnectLocationFetchers(){
		if(reconnectionHandler == null){
			destroyFusedLocationFetchers();
			reconnectionHandler = new Handler();
			reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					initializeFusedLocationFetchers();
					reconnectionHandler.removeCallbacks(this);
					reconnectionHandler = null;
				}
			}, 2000);
		}
	}
	
	
	public void setUserData(){
		try{
			userName.setText(Data.userData.userName);
			
			textViewJugnooBalance.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDecimal.format(Data.userData.jugnooBalance));
			
			Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(profileImg);}catch(Exception e){}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	
	public void switchUserScreen(final UserMode mode){
		
		if(getDistanceTimeAddress != null){
			getDistanceTimeAddress.cancel(true);
			getDistanceTimeAddress = null;
		}
		
		
		switch(mode){
		
			case DRIVER:
				cancelTimerUpdateDrivers();
				
				Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_DRIVER);
				
				passengerMainLayout.setVisibility(View.GONE);
				driverMainLayout.setVisibility(View.VISIBLE);
				
				couponsRl.setVisibility(View.GONE);
				
//				favBtn.setVisibility(View.GONE);
				
				break;
			
				
				
				
			case PASSENGER:
				
				Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_PASSENGER);
				
				passengerMainLayout.setVisibility(View.VISIBLE);
				driverMainLayout.setVisibility(View.GONE);
				
				couponsRl.setVisibility(View.VISIBLE);
				
//				favBtn.setVisibility(View.VISIBLE);
				
				break;
			
				
				
				
			default:
		
		}
		
		Database2.getInstance(HomeActivity.this).close();
		
	}
	
	
	
	public void updateDriverServiceFast(String choice){
		Database2.getInstance(HomeActivity.this).updateDriverServiceFast(choice);
		Database2.getInstance(HomeActivity.this).close();
	}
	
	
	public void switchDriverScreen(final DriverScreenMode mode){
		if(userMode == UserMode.DRIVER){
			
			initializeFusedLocationFetchers();
			
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
			
			saveDataOnPause(false);
			
		if(mode == DriverScreenMode.D_RIDE_END){
			startAutomaticReviewHandler();
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			
			
			
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			reviewWaitValue.setText(waitTime+" min");
			reviewRideTimeValue.setText(rideTime+" min");
			reviewFareValue.setText("Rs. "+decimalFormat.format(totalFare));
			
			reviewRatingText.setText("Customer Rating");
			reviewRatingBar.setRating(0);
			
			reviewUserName.setText(Data.assignedCustomerInfo.name);
			
			Data.assignedCustomerInfo.image = Data.assignedCustomerInfo.image.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.assignedCustomerInfo.image).skipMemoryCache().transform(new BlurTransform()).into(reviewUserImgBlured);}catch(Exception e){}
			try{Picasso.with(HomeActivity.this).load(Data.assignedCustomerInfo.image).skipMemoryCache().transform(new CircleTransform()).into(reviewUserImage);}catch(Exception e){}
			
			reviewSubmitBtn.setText("OK");
			
			setTextToFareInfoTextViews(reviewMinFareValue, reviewFareAfterValue, reviewFareAfterText);
			
			
			jugnooRideOverText.setText("The Jugnoo ride is over.");
			takeFareText.setText("Please take the fare as shown above from the customer.");
			
			
		}
		else{
			stopAutomaticReviewhandler();
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey));
		}
		
		
		switch(mode){
		
			case D_INITIAL:
				
				updateDriverServiceFast("no");
				
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				
				if(map != null){
					map.clear();
				}
				
				getAndShowAllDriverRequests(HomeActivity.this);
				cancelMapAnimateAndUpdateRideDataTimer();
				
				break;
				
				
			case D_REQUEST_ACCEPT:
				
				updateDriverServiceFast("no");
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.VISIBLE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				
			
				break;
				
				
				
			case D_START_RIDE:
				
				updateDriverServiceFast("yes");
				
				if(map != null){
					map.clear();
					
					markerOptionsCustomerPickupLocation = null;
					
					markerOptionsCustomerPickupLocation = new MarkerOptions();
					markerOptionsCustomerPickupLocation.title(Data.dEngagementId);
					markerOptionsCustomerPickupLocation.snippet("");
					markerOptionsCustomerPickupLocation.position(Data.dCustLatLng);
					markerOptionsCustomerPickupLocation.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPassengerMarkerBitmap(HomeActivity.this, assl)));
					
					map.addMarker(markerOptionsCustomerPickupLocation);
				}
				
				
				double rateingD = 4;
				try{
					rateingD = Double.parseDouble(Data.assignedCustomerInfo.rating);
				} catch(Exception e){
					e.printStackTrace();
				}
				
				driverPassengerName.setText(Data.assignedCustomerInfo.name);
				driverPassengerRatingValue.setText(decimalFormat.format(rateingD) + " "+getResources().getString(R.string.rating));
				
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				if("".equalsIgnoreCase(Data.assignedCustomerInfo.schedulePickupTime)){
					driverScheduledRideText.setVisibility(View.GONE);
				}
				else{
					String time = DateOperations.getTimeAMPM(DateOperations.utcToLocal(Data.assignedCustomerInfo.schedulePickupTime));
					if("".equalsIgnoreCase(time)){
						driverScheduledRideText.setVisibility(View.GONE);
					}
					else{
						driverScheduledRideText.setVisibility(View.VISIBLE);
						driverScheduledRideText.setText("Scheduled Ride Pickup: "+time);
					}
				}
				
				
				driverStartRideMainRl.setVisibility(View.VISIBLE);
				driverInRideMainRl.setVisibility(View.GONE);
				
				
				startCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				
				
				break;
				
				
				
				
			case D_IN_RIDE:
				
				updateDriverServiceFast("no");
				
				cancelCustomerPathUpdateTimer();
				
				startMapAnimateAndUpdateRideDataTimer();
				
				long timeR = (long)HomeActivity.previousRideTime;
				int hR = (int) (timeR / 3600000);
				int mR = (int) (timeR - hR * 3600000) / 60000;
				int sR = (int) (timeR - hR * 3600000 - mR * 60000) / 1000;
				String hhR = hR < 10 ? "0" + hR : hR + "";
				String mmR = mR < 10 ? "0" + mR : mR + "";
				String ssR = sR < 10 ? "0" + sR : sR + "";
				rideTimeChronometer.setText(hhR + ":" + mmR + ":" + ssR);
				
				rideTimeChronometer.eclipsedTime = (long)HomeActivity.previousRideTime;
				rideTimeChronometer.start();
				
				
				long time = (long)HomeActivity.previousWaitTime;
				int h = (int) (time / 3600000);
				int m = (int) (time - h * 3600000) / 60000;
				int s = (int) (time - h * 3600000 - m * 60000) / 1000;
				String hh = h < 10 ? "0" + h : h + "";
				String mm = m < 10 ? "0" + m : m + "";
				String ss = s < 10 ? "0" + s : s + "";
				waitChronometer.setText(hh + ":" + mm + ":" + ss);
				
				waitChronometer.eclipsedTime =  (long)HomeActivity.previousWaitTime;
				
				
				
				if(map != null){
					map.clear();
				}
				
				updateDistanceFareTexts();
				
				driverPassengerName.setText(Data.assignedCustomerInfo.name);
				
			
				setTextToFareInfoTextViews(inrideMinFareValue, inrideFareAfterValue, inrideFareAfterText);
				
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverScheduledRideText.setVisibility(View.GONE);
				
				driverStartRideMainRl.setVisibility(View.GONE);
				driverInRideMainRl.setVisibility(View.VISIBLE);
				
			
				break;
				
				
			case D_RIDE_END:
				
				updateDriverServiceFast("no");
				
				cancelMapAnimateAndUpdateRideDataTimer();

				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				
			
				break;
				
				
			
			default:
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
		
		}
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(DriverScreenMode.D_INITIAL == mode){
						Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.VULNERABLE);
					}
					else{
						Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.NOT_VULNERABLE);
					}
				} catch (Exception e) {
					Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.NOT_VULNERABLE);
					e.printStackTrace();
				}
				finally{
					Database2.getInstance(HomeActivity.this).close();
				}
			}
		}).start();
		
		
		}
		
		
		
		
	}
	
	
	
	public void switchPassengerScreen(PassengerScreenMode mode){
		if(userMode == UserMode.PASSENGER){
			initializeFusedLocationFetchers();
			
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
		
			saveDataOnPause(false);
			
		if(mode == PassengerScreenMode.P_RIDE_END){
			startAutomaticReviewHandler();
			mapLayout.setVisibility(View.GONE);
			endRideReviewRl.setVisibility(View.VISIBLE);
			topRl.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			double totalDistanceInKm = Data.totalDistance;
			String kmsStr = "";
			if(totalDistanceInKm > 1){
				kmsStr = "kms";
			}
			else{
				kmsStr = "km";
			}
			reviewDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			
			reviewWaitValue.setText(Data.waitTime+" min");
			
			reviewRideTimeValue.setText(Data.rideTime+" min");
			
			reviewFareValue.setText("Rs. "+decimalFormat.format(Data.totalFare));
			
			reviewRatingText.setText("Driver Rating");
			reviewRatingBar.setRating(0);
			
			reviewUserName.setText(Data.assignedDriverInfo.name);
			
			Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new BlurTransform()).into(reviewUserImgBlured);}catch(Exception e){}
			try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new CircleTransform()).into(reviewUserImage);}catch(Exception e){}
			
			reviewSubmitBtn.setText("OK");
			
			setTextToFareInfoTextViews(reviewMinFareValue, reviewFareAfterValue, reviewFareAfterText);
			
			jugnooRideOverText.setText("The Jugnoo ride is over.");
			takeFareText.setText("Please pay the fare as shown above to the driver.");
			
		}
		else{
			stopAutomaticReviewhandler();
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey));
		}
		
		
		
		
		
		switch(mode){
		
			case P_INITIAL:

				requestRideBtn.setText(REQUEST_RIDE_BTN_NORMAL_TEXT);
				requestRideBtn.setBackgroundResource(R.drawable.blue_btn_selector);
				
				cancelDriverLocationUpdateTimer();
				cancelTimerRequestRide();
				
				try{pickupLocationMarker.remove();} catch(Exception e){}
				try{driverLocationMarker.remove();} catch(Exception e){}
				
				
		        GCMIntentService.clearNotifications(getApplicationContext());
				
				if(getDistanceTimeAddress != null){
					getDistanceTimeAddress.cancel(true);
					getDistanceTimeAddress = null;
				}
				
				
				
				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				if (Data.userData.canChangeLocation == 1) {
					centreLocationRl.setVisibility(View.VISIBLE);
				} else {
					centreLocationRl.setVisibility(View.GONE);
				}
				feedbackLayout.setVisibility(View.GONE);
				
				nearestDriverRl.setVisibility(View.VISIBLE);
				initialCancelRideBtn.setVisibility(View.GONE);
				textViewAssigningInProgress.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				
				Log.e("Data.latitude", "="+Data.latitude);
				Log.e("myLocation", "="+myLocation);
				
				if (Data.latitude != 0 && Data.longitude != 0) {
					showDriverMarkersAndPanMap(new LatLng(Data.latitude, Data.longitude));
				} else if (myLocation != null) {
					showDriverMarkersAndPanMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				}
				
				startTimerUpdateDrivers();
				
				break;
				
				
			case P_ASSIGNING:
				
				requestRideBtn.setText(REQUEST_RIDE_BTN_ASSIGNING_DRIVER_TEXT);
				requestRideBtn.setBackgroundResource(R.drawable.blue_btn_normal);
				
				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				feedbackLayout.setVisibility(View.GONE);
				
				if(map != null){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.pickupLatLng);
					markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
					
					pickupLocationMarker = map.addMarker(markerOptions);
				}
				
				
				nearestDriverRl.setVisibility(View.GONE);
				initialCancelRideBtn.setVisibility(View.VISIBLE);
				textViewAssigningInProgress.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);

				cancelTimerUpdateDrivers();
				
				break;
				
				
				
			case P_REQUEST_FINAL:
				
				if(map != null){
					
					if(Data.pickupLatLng == null){
						Log.e("Data.mapTarget","=null");
						SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
						String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
						String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
						Data.pickupLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					}
					else{
						Log.e("Data.mapTarget","=not null");
					}
					
					Log.e("Data.mapTarget","="+Data.pickupLatLng);
					Log.e("Data.assignedDriverInfo.latLng","="+Data.assignedDriverInfo.latLng);
					
					map.clear();
					
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.pickupLatLng);
					markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
					
					pickupLocationMarker = map.addMarker(markerOptions);
					
					MarkerOptions markerOptions1 = new MarkerOptions();
					markerOptions1.title("driver position");
					markerOptions1.snippet("");
					markerOptions1.position(Data.assignedDriverInfo.latLng);
					markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
					markerOptions1.anchor(0.5f, 0.7f);
					
					driverLocationMarker = map.addMarker(markerOptions1);
					
					Log.i("marker added", "REQUEST_FINAL");
				}
				
				
				setAssignedDriverData();
				
				setTextToFareInfoTextViews(minFareValue, fareAfterValue, fareAfterText);
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				feedbackLayout.setVisibility(View.GONE);
				
				driverTime.setVisibility(View.VISIBLE);
				inRideRideInProgress.setText("Please wait while Jugnoo is coming...");
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.GONE);
				
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						driverTime.setVisibility(View.GONE);
					}
				}, 60000);

				startDriverLocationUpdateTimer();
				
				cancelTimerUpdateDrivers();
				
				break;
				
				
				
			case P_IN_RIDE:
				
				cancelTimerUpdateDrivers();
				
				cancelDriverLocationUpdateTimer();
				
				startMapAnimateAndUpdateRideDataTimer();
				
				if(map != null){
					map.clear();
				}
				
				setAssignedDriverData();
				
				setTextToFareInfoTextViews(minFareValue, fareAfterValue, fareAfterText);
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				feedbackLayout.setVisibility(View.GONE);
				
				driverTime.setVisibility(View.GONE);
				inRideRideInProgress.setText("Ride in progress...");
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
//				favBtn.setVisibility(View.GONE);

				
				break;
				
			case P_RIDE_END:
				
				cancelMapAnimateAndUpdateRideDataTimer();
				
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				feedbackLayout.setVisibility(View.GONE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);

				
				break;
				
			case P_FEEDBACK:
				mapLayout.setVisibility(View.GONE);
				initialLayout.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				feedbackLayout.setVisibility(View.VISIBLE);
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.GONE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.VISIBLE);
				title.setText("Feedback");
				
				break;
				
				
			default:

				initialLayout.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				
				
				menuBtn.setVisibility(View.VISIBLE);
				jugnooLogo.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				
		}
		
		
		}
		
	}
	
	
	
	public void setTextToFareInfoTextViews(TextView minFareValue, TextView fareAfterValue, TextView fareAfterText){
		
		minFareValue.setText("Rs " + decimalFormat.format(Data.fareStructure.fixedFare) + " for " 
				+ decimalFormat.format(Data.fareStructure.thresholdDistance) + " km");
		
		fareAfterValue.setText("Rs " + decimalFormat.format(Data.fareStructure.farePerKm) + " per km + Rs "
				+ decimalFormat.format(Data.fareStructure.farePerMin) + " per min");
		
		SpannableString sstr = new SpannableString("Fare");
		final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
		sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		fareAfterText.setText("");
		fareAfterText.append(sstr);
		fareAfterText.append(" (after " + decimalFormat.format(Data.fareStructure.thresholdDistance) + " km)");
	}
	
	
	public void setAssignedDriverData(){
		driverName.setText(Data.assignedDriverInfo.name);
		if(!"".equalsIgnoreCase(Data.assignedDriverInfo.carNumber)){
			driverCarNumberText.setText(Data.assignedDriverInfo.carNumber);
		}
		else{
			driverCarNumberText.setText("");
		}
		
		updateAssignedDriverETA();
		
		Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
		try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).skipMemoryCache().transform(new RoundBorderTransform()).into(driverImage);}catch(Exception e){}
		
		
		Data.assignedDriverInfo.carImage = Data.assignedDriverInfo.carImage.replace("http://graph.facebook", "https://graph.facebook");
		try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.carImage).skipMemoryCache().transform(new RoundBorderTransform()).into(driverCarImage);}catch(Exception e){}
		
		if(1 == Data.userData.freeRideIconDisable){
			passengerFreeRideIcon.setVisibility(View.GONE);
		}
		else{
			if(1 == Data.assignedDriverInfo.freeRide){
				passengerFreeRideIcon.setVisibility(View.VISIBLE);
			}
			else{
				passengerFreeRideIcon.setVisibility(View.GONE);
			}
		}
		
	}
	
	public void updateAssignedDriverETA(){
		if("".equalsIgnoreCase(Data.assignedDriverInfo.durationToReach) || "no".equalsIgnoreCase(Data.assignedDriverInfo.durationToReach)){
			driverTime.setText("");
		}
		else{
			if(Locale.getDefault().getLanguage().equalsIgnoreCase("hi")){
				driverTime.setText(Data.assignedDriverInfo.durationToReach + " "+ getResources().getString(R.string.will_arrive_in));
	 		}
	 		else{
	 			driverTime.setText(getResources().getString(R.string.will_arrive_in) +" "+ Data.assignedDriverInfo.durationToReach);
	 		}
		}
	}
	
	
	
	
	
	
	
	
	

	
	
	
	
	void buildAlertMessageNoGps() {
		if(!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(gpsDialogAlert != null && gpsDialogAlert.isShowing()){
		    }
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("The app needs active GPS connection. Enable it from Settings.")
			           .setCancelable(false)
			           .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
			               public void onClick(final DialogInterface dialog, final int id) {
			                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			                   dialog.dismiss();
			                   gpsDialogAlert = null;
			               }
			           })
			           ;
			    gpsDialogAlert = null;
			    gpsDialogAlert = builder.create();
			    gpsDialogAlert.show();
			}
		}
		else{
			if(gpsDialogAlert != null && gpsDialogAlert.isShowing()){
				gpsDialogAlert.dismiss();
		    }
		}
	}
	
	public Dialog timeDialogAlert;
	
    @SuppressWarnings("deprecation")
	public void buildTimeSettingsAlertDialog(final Activity activity) {
    	try {
    		int autoTime = android.provider.Settings.System.getInt(activity.getContentResolver(), android.provider.Settings.System.AUTO_TIME);
    		if(autoTime == 0){
				if(timeDialogAlert != null && timeDialogAlert.isShowing()){
			    }
				else{
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				    builder.setMessage("The app needs Network Provided Time to be enabled. Enable it from Settings.")
				           .setCancelable(false)
				           .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
				               public void onClick(final DialogInterface dialog, final int id) {
				            	   activity.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
				               }
				           })
				           ;
				    timeDialogAlert = null;
				    timeDialogAlert = builder.create();
				    timeDialogAlert.show();
				}
			}
			else{
				if(timeDialogAlert != null && timeDialogAlert.isShowing()){
					timeDialogAlert.dismiss();
			    }
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static boolean checkIfUserDataNull(Activity activity){
		Log.e("checkIfUserDataNull", "Data.userData = "+Data.userData);
		if(Data.userData == null){
			activity.startActivity(new Intent(activity, SplashNewActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		}
		else{
			return false;
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
		if(!checkIfUserDataNull(HomeActivity.this)){
				setUserData();
		//		SplashNewActivity.isLastLocationUpdateFine(HomeActivity.this);
				
				if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
					  startTimerUpdateDrivers();
				}
			    
			    initializeFusedLocationFetchers();
			    
			    if(UserMode.DRIVER == userMode){
					buildTimeSettingsAlertDialog(this);
				}
			    
			    sendToShareScreen();
			    
			    Data.autoShare = 0;
		}
		
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	
	
	public static void checkForAccessTokenChange(Activity activity){
		Pair<String, Integer> pair = AccessTokenGenerator.getAccessTokenPair(activity);
		if(!"".equalsIgnoreCase(pair.first)){
			if(Data.userData == null){
				logoutIntent(activity);
			}
			else{
				if(!pair.first.equalsIgnoreCase(Data.userData.accessToken)){
					logoutIntent(activity);
				}
			}
		}
		else{
			if(Data.userData == null){
				
			}
			else{
				logoutIntent(activity);
			}
		}
	}
	
	
	public static void logoutIntent(Activity cont){
		try {
			new FacebookLoginHelper().logoutFacebook();
			Data.userData = null;
			Intent intent = new Intent(cont, SplashNewActivity.class);
			cont.startActivity(intent);
			cont.finish();
			cont.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendToShareScreen(){
		if(Data.autoShare == 1){
	    	Data.autoShare = 0;
	    	startActivity(new Intent(HomeActivity.this, ShareActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
	    }
	}
	
	
	
	public void saveDataOnPause(final boolean stopWait){
		new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
					if(userMode == UserMode.DRIVER){
						
						SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
						Editor editor = pref.edit();
						
						if(driverScreenMode == DriverScreenMode.D_START_RIDE){
							
							editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_START_RIDE);
							
							editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
							editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
							
							editor.putString(Data.SP_D_LATITUDE, ""+Data.dCustLatLng.latitude);
							editor.putString(Data.SP_D_LONGITUDE, ""+Data.dCustLatLng.longitude);
							
							editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
							editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
							editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
							editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
							
						}
						else if(driverScreenMode == DriverScreenMode.D_IN_RIDE){
							
							if(stopWait){
								if(waitChronometer.isRunning){
									stopWait();
								}
								if(rideTimeChronometer.isRunning){
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											try{
												rideTimeChronometer.stop();
											} catch(Exception e){
							        			e.printStackTrace();
							        		}
										}
									});
								}
							}
							
							editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_IN_RIDE);
							
							editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
							editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
							
							editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
							editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
							editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
							editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
							
							long elapsedMillis = waitChronometer.eclipsedTime;
					    	
							editor.putString(Data.SP_TOTAL_DISTANCE, ""+totalDistance);
							editor.putString(Data.SP_WAIT_TIME, ""+elapsedMillis);
							
							long elapsedRideTime = rideTimeChronometer.eclipsedTime;
							editor.putString(Data.SP_RIDE_TIME, ""+elapsedRideTime);
							
							if(HomeActivity.this.lastLocation != null){
								editor.putString(Data.SP_LAST_LATITUDE, ""+HomeActivity.this.lastLocation.getLatitude());
					    		editor.putString(Data.SP_LAST_LONGITUDE, ""+HomeActivity.this.lastLocation.getLongitude());
							}
							
							Log.e("Data on app paused", "-----");
							Log.i("HomeActivity.totalDistance", "="+HomeActivity.totalDistance);
							Log.i("lastLocation", "="+lastLocation);
							Log.e("----------", "-----");
							
						}
						else{
							editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");
						}
						
						
						editor.commit();
						
					}
					else if(userMode == UserMode.PASSENGER){
					    
						
						SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
						Editor editor = pref.edit();
						
						if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
							
							editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_REQUEST_FINAL);
							
							editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
							editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
							editor.putString(Data.SP_C_LATITUDE, ""+Data.assignedDriverInfo.latLng.latitude);
							editor.putString(Data.SP_C_LONGITUDE, ""+Data.assignedDriverInfo.latLng.longitude);
							editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
							editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
							editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
							editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
							editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
							editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
							editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
							
							
						}
						else if(passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
							
							editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_IN_RIDE);
							
							editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
							editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
							editor.putString(Data.SP_C_LATITUDE, ""+Data.assignedDriverInfo.latLng.latitude);
							editor.putString(Data.SP_C_LONGITUDE, ""+Data.assignedDriverInfo.latLng.longitude);
							editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
							editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
							editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
							editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
							editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
							editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
							editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
							
							
							editor.putString(Data.SP_TOTAL_DISTANCE, ""+totalDistance);
							
							if(HomeActivity.this.lastLocation != null){
								editor.putString(Data.SP_LAST_LATITUDE, ""+HomeActivity.this.lastLocation.getLatitude());
					    		editor.putString(Data.SP_LAST_LONGITUDE, ""+HomeActivity.this.lastLocation.getLongitude());
							}
							
						
						}
						else{
							editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");
						}
						
						editor.commit();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	
	}
	
	
	
	
	@Override
	protected void onPause() {
		
		GCMIntentService.clearNotifications(getApplicationContext());
		saveDataOnPause(false);
		
		try{
			if(userMode == UserMode.PASSENGER){
				cancelTimerUpdateDrivers();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		destroyFusedLocationFetchers();
		
		super.onPause();
		
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		try{
			if(userMode == UserMode.DRIVER){
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE || driverScreenMode == DriverScreenMode.D_START_RIDE){
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				}
				else{
					ActivityCompat.finishAffinity(this);
//					super.onBackPressed();
				}
			}
			else{
				ActivityCompat.finishAffinity(this);
//				super.onBackPressed();
			}
		} catch(Exception e){
			e.printStackTrace();
			ActivityCompat.finishAffinity(this);
//			super.onBackPressed();
		}
	}
	
	    
	
	@Override
    public void onDestroy() {
        try{
        	if(createPathAsyncTasks != null){
        		createPathAsyncTasks.clear();
        	}
        	saveDataOnPause(true);
        	
    		GCMIntentService.clearNotifications(HomeActivity.this);
    		
    		destroyFusedLocationFetchers();
	        
	        ASSL.closeActivity(drawerLayout);
	        cancelTimerUpdateDrivers();
	        
	        appInterruptHandler = null;
	        
	        new FacebookLoginHelper().logoutFacebook();
	        
	        System.gc();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        super.onDestroy();
    }
	
	
	
	
	
	
	
	public synchronized void drawLocationChanged(Location location){
		try {
			if(map != null){
				HomeActivity.myLocation = location;
				zoomToCurrentLocationAtFirstLocationFix(location);
			
				updatePickupLocation(location);
			
				if(((userMode == UserMode.DRIVER) && (driverScreenMode == DriverScreenMode.D_IN_RIDE)) 
						|| ((userMode == UserMode.PASSENGER) && (passengerScreenMode == PassengerScreenMode.P_IN_RIDE))){
					
					final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
					
					if(Utils.compareDouble(totalDistance, -1.0) == 0){
						lastLocation = null;
						Log.i("lastLocation made null", "="+lastLocation);
					}
					
					Log.i("lastLocation", "="+lastLocation);
					Log.i("totalDistance", "="+totalDistance);
					
					writePathLogToFile("lastLocation = "+lastLocation);
					writePathLogToFile("totalDistance = "+totalDistance);
					
					if(lastLocation != null){
						final LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
						addLatLngPathToDistance(lastLatLng, currentLatLng);
					}
					else{
						if(Utils.compareDouble(totalDistance, -1.0) == 0){
							totalDistance = 0;
						}
						displayOldPath();
						writePathLogToFile("Data.startRidePreviousLatLng = "+Data.startRidePreviousLatLng);
						addLatLngPathToDistance(Data.startRidePreviousLatLng, currentLatLng);
					}
					
					lastLocation = location;
					
					saveDataOnPause(false);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	public void writePathLogToFile(String text){
		try{
			if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
				Log.writePathLogToFile(Data.dEngagementId, text);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public synchronized void addLatLngPathToDistance(final LatLng lastLatLng, final LatLng currentLatLng){
		try {
			double displacement = MapUtils.distance(lastLatLng, currentLatLng);
			Log.i("displacement", "="+displacement);
			
			writePathLogToFile("lastLatLng = "+lastLatLng+", currentLatLng = "+currentLatLng);
			writePathLogToFile("displacement = "+displacement);
			
			if(Utils.compareDouble(displacement, MAX_DISPLACEMENT_THRESHOLD) == -1){
				
				boolean validDistance = updateTotalDistance(lastLatLng, currentLatLng, displacement);
				if(validDistance){
					checkAndUpdateWaitTimeDistance(displacement);
					map.addPolyline(new PolylineOptions()
				    .add(lastLatLng, currentLatLng)
				    .width(5)
				    .color(MAP_PATH_COLOR).geodesic(true));
					logPathDataToFlurry(currentLatLng, totalDistance);
					new Thread(new Runnable() {
						@Override
						public void run() {
							Database.getInstance(HomeActivity.this).insertPolyLine(lastLatLng, currentLatLng);
							Database.getInstance(HomeActivity.this).close();
						}
					}).start();
				}
				
				updateDistanceFareTexts();
			}
			else{
				callGooglePathAPI(lastLatLng, currentLatLng, displacement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	ArrayList<CreatePathAsyncTask> createPathAsyncTasks = new ArrayList<HomeActivity.CreatePathAsyncTask>();
	
	public synchronized void callGooglePathAPI(LatLng lastLatLng, LatLng currentLatLng, double displacement){
		if(createPathAsyncTasks == null){
			createPathAsyncTasks = new ArrayList<HomeActivity.CreatePathAsyncTask>();
		}
		CreatePathAsyncTask createPathAsyncTask = new CreatePathAsyncTask(lastLatLng, currentLatLng, displacement);
		if(!createPathAsyncTasks.contains(createPathAsyncTask)){
			createPathAsyncTasks.add(createPathAsyncTask);
			createPathAsyncTask.execute();
		}
	}
	
	class CreatePathAsyncTask extends AsyncTask<Void, Void, String>{
	    String url;
	    double displacementToCompare;
	    LatLng source, destination;
	    CreatePathAsyncTask(LatLng source, LatLng destination, double displacementToCompare){
	    	this.source = source;
	    	this.destination = destination;
	        this.url = MapUtils.makeDirectionsURL(source, destination);
	        this.displacementToCompare = displacementToCompare;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	return new HttpRequester().getJSONFromUrl(url);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        if(result!=null){
	            drawPath(result, displacementToCompare, source, destination);
				updateDistanceFareTexts();
	        }
	        createPathAsyncTasks.remove(this);
	    }
	    
	    
	    @Override
	    public boolean equals(Object o) {
	    	try{
	    		if((((CreatePathAsyncTask)o).source == this.source) && (((CreatePathAsyncTask)o).destination == this.destination)){
	    			return true;
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return false;
	    }
	}
	
	
	
	public void logPathDataToFlurry(LatLng latLng, double totalDistance){
		try{
			if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
				FlurryEventLogger.logRideData(Data.userData.accessToken, Data.dEngagementId, latLng, totalDistance);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void logPathDataToFlurryGAPI(LatLng latLng1, LatLng latLng2, double totalDistance){
		try{
			if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
				FlurryEventLogger.logRideDataGAPI(Data.userData.accessToken, Data.dEngagementId, latLng1, latLng2, totalDistance);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void checkAndUpdateWaitTimeDistance(final double distance){
		try {
			if(waitStart == 1){
				distanceAfterWaitStarted = distanceAfterWaitStarted + distance;
				if(distanceAfterWaitStarted >= MAX_WAIT_TIME_ALLOWED_DISTANCE){
					stopWait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public synchronized void updateDistanceFareTexts(){
		double totalDistanceInKm = Math.abs(totalDistance/1000.0);
		
		int h = (int) (rideTimeChronometer.eclipsedTime / 3600000);
		int m = (int) (rideTimeChronometer.eclipsedTime - h * 3600000) / 60000;
		double totalTimeInMin = (double) m;
		Log.i("totalTimeInMin", "="+totalTimeInMin);
		
		driverIRDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm));
		driverIRFareValue.setText(""+decimalFormat.format(Data.fareStructure.calculateFare(totalDistanceInKm, totalTimeInMin)));
	}
	
	
	
	public synchronized void displayOldPath(){
		
		try {
			ArrayList<Pair<LatLng, LatLng>> path = Database.getInstance(HomeActivity.this).getSavedPath();
			Database.getInstance(HomeActivity.this).close();
			
			LatLng firstLatLng = null;
			
			PolylineOptions polylineOptions = new PolylineOptions();
			polylineOptions.width(5);
			polylineOptions.color(MAP_PATH_COLOR);
			polylineOptions.geodesic(true);
			
			for(Pair<LatLng, LatLng> pair : path){
				LatLng src = pair.first;
			    LatLng dest = pair.second;
			    
				if(firstLatLng == null){
					firstLatLng = src;
				}
				
				polylineOptions.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));
			}
			
			map.addPolyline(polylineOptions);
			
			if(firstLatLng == null){
				firstLatLng = Data.startRidePreviousLatLng;
			}
			
			if(firstLatLng != null){
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.snippet("");
				markerOptions.title("start ride location");
				markerOptions.position(firstLatLng);
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
				map.addMarker(markerOptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	

	
	public synchronized boolean updateTotalDistance(LatLng lastLatLng, LatLng currentLatLng, double deltaDistance){
		boolean validDistance = false;
		if(deltaDistance > 0.0){
			LatLngPair latLngPair = new LatLngPair(lastLatLng, currentLatLng, deltaDistance);
			
			Log.e("latLngPair to add", "="+latLngPair);
			
			if(HomeActivity.deltaLatLngPairs == null){
				HomeActivity.deltaLatLngPairs = new ArrayList<LatLngPair>();
			}
			
			if(!HomeActivity.deltaLatLngPairs.contains(latLngPair)){
				totalDistance = totalDistance + deltaDistance;
				HomeActivity.deltaLatLngPairs.add(latLngPair);
				validDistance = true;
			}
		}
		Log.e("HomeActivity.deltaLatLngPairs", "="+HomeActivity.deltaLatLngPairs);
		return validDistance;
	}
	
	
	
	public synchronized void drawPath(String result, double displacementToCompare, LatLng source, LatLng destination) {
	    try {
	        writePathLogToFile("GAPI source = "+source+", destination = "+destination);
	    	 final JSONObject json = new JSONObject(result);
	    	
	    	JSONObject leg0 = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
	    	double distanceOfPath = leg0.getJSONObject("distance").getDouble("value");
	    	
	    	 writePathLogToFile("GAPI distanceOfPath = "+distanceOfPath);
	    	
	    	if(Utils.compareDouble(distanceOfPath, (displacementToCompare*1.8)) <= 0){														// distance would be approximately correct

		           JSONArray routeArray = json.getJSONArray("routes");
		           JSONObject routes = routeArray.getJSONObject(0);
		           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		           String encodedString = overviewPolylines.getString("points");
		           List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
			    	
//			    	totalDistance = totalDistance + distanceOfPath;
		           boolean validDistance = updateTotalDistance(source, destination, distanceOfPath);
		           if(validDistance){
				    	checkAndUpdateWaitTimeDistance(distanceOfPath);
				    	 
			           for(int z = 0; z<list.size()-1;z++){
			                LatLng src= list.get(z);
			                LatLng dest= list.get(z+1);
			                map.addPolyline(new PolylineOptions()
			                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
			                .width(5)
					        .color(MAP_PATH_COLOR).geodesic(true));
			                Database.getInstance(this).insertPolyLine(src, dest);
			            }
			           Database.getInstance(this).close();
		           }
	    	}
	    	else{																									// displacement would be correct
//	    		totalDistance = totalDistance + displacementToCompare;
	    		boolean validDistance = updateTotalDistance(source, destination, displacementToCompare);
	    		if(validDistance){
		    		checkAndUpdateWaitTimeDistance(displacementToCompare);
		    		
		    		 map.addPolyline(new PolylineOptions()
		                .add(new LatLng(source.latitude, source.longitude), new LatLng(destination.latitude, destination.longitude))
		                .width(5)
				        .color(MAP_PATH_COLOR).geodesic(true));
		    		 Database.getInstance(this).insertPolyLine(source, destination);
		    		 Database.getInstance(this).close();
	    		}
	    		
	    	}
	    	writePathLogToFile("totalDistance after GAPI = "+totalDistance);
	    	logPathDataToFlurryGAPI(source, destination, totalDistance);
	           
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	} 
	
	
	
	
	
	
	
	
	class ViewHolderDriverRequest {
		TextView textViewRequestAddress, textViewRequestDistance, textViewRequestTime, textViewRequestNumber;
		RelativeLayout relative;
		int id;
	}

	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRequest holder;
		
		DateOperations dateOperations;
		
		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateOperations = new DateOperations();
		}
		
		public DateOperations getDateOperations(){
			if(dateOperations == null){
				dateOperations = new DateOperations();
			}
			return dateOperations;
		}

		@Override
		public int getCount() {
			return Data.driverRideRequests.size();
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
				
				holder = new ViewHolderDriverRequest();
				convertView = mInflater.inflate(R.layout.list_item_driver_request, null);
				
				holder.textViewRequestAddress = (TextView) convertView.findViewById(R.id.textViewRequestAddress); holder.textViewRequestAddress.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestDistance = (TextView) convertView.findViewById(R.id.textViewRequestDistance); holder.textViewRequestDistance.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestTime = (TextView) convertView.findViewById(R.id.textViewRequestTime); holder.textViewRequestTime.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestNumber = (TextView) convertView.findViewById(R.id.textViewRequestNumber); holder.textViewRequestNumber.setTypeface(Data.latoRegular(getApplicationContext()));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRequest) convertView.getTag();
			}
			
			
			DriverRideRequest driverRideRequest = Data.driverRideRequests.get(position);
			
			holder.id = position;
			
			holder.textViewRequestNumber.setText(""+(position+1));
			holder.textViewRequestAddress.setText(driverRideRequest.address);
			
			
			long timeDiff = getDateOperations().getTimeDifference(DateOperations.getCurrentTime(), driverRideRequest.startTime);
			long timeDiffInSec = timeDiff / 1000;
			holder.textViewRequestTime.setText(""+timeDiffInSec + " sec left");
			
			if(myLocation != null){
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				double distance = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), driverRideRequest.latLng);
				if(distance >= 1000){
					holder.textViewRequestDistance.setText(""+decimalFormat.format(distance/1000)+" km away");
				}
				else{
					holder.textViewRequestDistance.setText(""+distance+" m away");
				}
			}
			else{
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}
			
			
			
			
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						holder = (ViewHolderDriverRequest) v.getTag();
						
						DriverRideRequest driverRideRequest = Data.driverRideRequests.get(holder.id);
						
						Data.dEngagementId = driverRideRequest.engagementId;
						Data.dCustomerId = driverRideRequest.customerId;
						Data.dCustLatLng = driverRideRequest.latLng;
						
						
						driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
						switchDriverScreen(driverScreenMode);
						
						
						map.animateCamera(CameraUpdateFactory.newLatLng(driverRideRequest.latLng), 1000, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			
			return convertView;
		}

	}
	
	
	
	

	
	
	
	class GetDistanceTimeAddress extends AsyncTask<Void, Void, String>{
	    String url;
	    
	    String distance, duration;
	    
	    LatLng destination;
	    
	    boolean driverAcceptPushRecieved;
	    
	    public GetDistanceTimeAddress(LatLng destination, boolean driverAcceptPushRecieved){
	    	this.distance = "";
	    	this.duration = "";
	    	this.destination = destination;
	    	this.driverAcceptPushRecieved = driverAcceptPushRecieved;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(userMode == UserMode.PASSENGER){
		        runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						if(driverAcceptPushRecieved){
							passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
							switchPassengerScreen(passengerScreenMode);
				        }
						else{
							addCurrentLocationAddressMarker(destination);
						}
						
						nearestDriverProgress.setVisibility(View.VISIBLE);
				        nearestDriverText.setVisibility(View.GONE);
				        dontCallRefreshDriver = false;
				        
					}
				});
	        }
	    }
	    
	    
	    @Override
	    protected String doInBackground(Void... params) {
	    	if(userMode == UserMode.PASSENGER){
		    	try{
		    		
		    		if(!driverAcceptPushRecieved){
			    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
		    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+destination.latitude));
		    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+destination.longitude));
		    			
		    			Log.e("nameValuePairs in find_a_driver", "="+nameValuePairs);
		    			
		    			HttpRequester simpleJSONParser = new HttpRequester();
		    			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/find_a_driver", nameValuePairs);
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
		    			}
		    			else{
		    				try{
		    					JSONObject jObj = new JSONObject(result);
		    					new JSONParser().parseDriversToShow(jObj, "data");
		    				}
		    				catch(Exception e){
		    					e.printStackTrace();
		    				}
		    			}
		    		}
		    		
		    		LatLng source = null;
					
		    		if(!driverAcceptPushRecieved){
		    			double minDistance = 999999999;
			    		for(int i=0; i<Data.driverInfos.size(); i++){
			    			if(MapUtils.distance(destination, Data.driverInfos.get(i).latLng) < minDistance){
			    				minDistance = MapUtils.distance(destination, Data.driverInfos.get(i).latLng);
			    				source = Data.driverInfos.get(i).latLng;
			    			}
			    		}
			    		
			    		if(Data.driverInfos.size() > 0){
				    		double approxDistance = (minDistance * 1.5);
				    		if(approxDistance < 1000){
				    			distance = (int)approxDistance+" m";
				    		}
				    		else{
				    			approxDistance = approxDistance / 1000;
				    			distance = decimalFormat.format(approxDistance)+" km";
				    		}
			    		}
			    		else{
			    			distance = "error";
			    		}
			    		return distance;
		    		}
		    		else if(driverAcceptPushRecieved){
		    			source = Data.assignedDriverInfo.latLng;
		    			
		    			if(source == null){
			    			return "error";
			    		}
			    			
			    		this.url = MapUtils.makeDistanceMatrixURL(source, destination);
				    	HttpRequester jParser = new HttpRequester();
				    	String response = jParser.getJSONFromUrl(url);
				    	JSONObject jsonObject = new JSONObject(response);
				    	String status = jsonObject.getString("status");
				    	if("OK".equalsIgnoreCase(status)){
				    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
				    		distance = element0.getJSONObject("distance").getString("text") ;
				    		duration = element0.getJSONObject("duration").getString("text");
				    		if(driverAcceptPushRecieved){
				    			Data.assignedDriverInfo.distanceToReach = distance;
				    			Data.assignedDriverInfo.durationToReach = duration;
				    		}
				    		return "Distance: " + distance + "\n" + "Duration: " + duration;
				    	}
		    		}
		    	} catch(Exception e){
		    		e.printStackTrace();
		    	}
	    	}
	    	
	        return "error";
	    }
	    @Override
	    protected void onPostExecute(final String result) {
	        super.onPostExecute(result);  
	        if(userMode == UserMode.PASSENGER){
		        runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(!driverAcceptPushRecieved){
							showDriverMarkersAndPanMap(destination);
			    		}
				        dontCallRefreshDriver = true;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dontCallRefreshDriver = false;
							}
						}, 4000);
				        
				        nearestDriverProgress.setVisibility(View.GONE);
				        nearestDriverText.setVisibility(View.VISIBLE);
				        String distanceString = "";
				        if(!"error".equalsIgnoreCase(result)){
					        Log.i("passengerScreenMode","="+passengerScreenMode);
					        if(!driverAcceptPushRecieved){
					 	       	if(!"".equalsIgnoreCase(distance)){
					 	       		distanceString = getResources().getString(R.string.nearest_driver_is) + " " + distance + " " + getResources().getString(R.string.away);
						        }
						        else{
						        	distanceString = getResources().getString(R.string.could_not_find_nearest_driver_distance);
						        }
					 	       	nearestDriverText.setText(distanceString);
					        }
				        }
				        else{
				        	distanceString = getResources().getString(R.string.no_drivers_nearby);
				        	nearestDriverText.setText(distanceString);
				        }
				        
				        if (driverAcceptPushRecieved) {
							SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
							Editor editor = pref.edit();
							editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, Data.P_REQUEST_FINAL);
	
							editor.putString(Data.SP_C_ENGAGEMENT_ID, Data.cEngagementId);
							editor.putString(Data.SP_C_DRIVER_ID, Data.cDriverId);
							editor.putString(Data.SP_C_LATITUDE, "" + Data.assignedDriverInfo.latLng.latitude);
							editor.putString(Data.SP_C_LONGITUDE, "" + Data.assignedDriverInfo.latLng.longitude);
							editor.putString(Data.SP_C_DRIVER_NAME, Data.assignedDriverInfo.name);
							editor.putString(Data.SP_C_DRIVER_IMAGE, Data.assignedDriverInfo.image);
							editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, Data.assignedDriverInfo.carImage);
							editor.putString(Data.SP_C_DRIVER_PHONE, Data.assignedDriverInfo.phoneNumber);
							editor.putString(Data.SP_C_DRIVER_RATING, Data.assignedDriverInfo.rating);
							editor.putString(Data.SP_C_DRIVER_DISTANCE, Data.assignedDriverInfo.distanceToReach);
							editor.putString(Data.SP_C_DRIVER_DURATION, Data.assignedDriverInfo.durationToReach);
	
							editor.commit();
							if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
								updateAssignedDriverETA();
							}
						}
					}
				});
	        }
	        
	        
	    }
	    
	    
	    
	}
	
	
	
	
	public void addDriverMarkerForCustomer(DriverInfo driverInfo){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title("driver shown to customer");
		markerOptions.snippet(""+driverInfo.userId);
		markerOptions.position(driverInfo.latLng);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createCarMarkerBitmap(HomeActivity.this, assl)));
		markerOptions.anchor(0.5f, 0.7f);
		map.addMarker(markerOptions);
	}
	
	public void addCurrentLocationAddressMarker(LatLng latLng){
		try {
			if (Data.userData.canChangeLocation == 0) {
				if(currentLocationMarker != null){
					currentLocationMarker.remove();
				}
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.title("customer_current_location");
				markerOptions.snippet("");
				markerOptions.position(latLng);
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
				currentLocationMarker = map.addMarker(markerOptions);
			}
		} catch (Exception e) {
		}
	}
	
	
	
	public void showDriverMarkersAndPanMap(final LatLng userLatLng){
		if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_INITIAL){
				if(map != null){
					map.clear();
					addCurrentLocationAddressMarker(userLatLng);
					LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
					LatLng farthestLatLng = null;
					for(int i=0; i<Data.driverInfos.size(); i++){
						addDriverMarkerForCustomer(Data.driverInfos.get(i));
						if(i < 5){
							farthestLatLng = Data.driverInfos.get(i).latLng;
						}
					}
					if(!mapTouchedOnce){
						if(farthestLatLng != null){
							boundsBuilder.include(new LatLng(userLatLng.latitude, farthestLatLng.longitude));
							boundsBuilder.include(new LatLng(farthestLatLng.latitude, userLatLng.longitude));
							boundsBuilder.include(new LatLng(userLatLng.latitude, ((2*userLatLng.longitude) - farthestLatLng.longitude)));
							boundsBuilder.include(new LatLng(((2*userLatLng.latitude) - farthestLatLng.latitude), userLatLng.longitude));
							
							boundsBuilder.include(userLatLng);
							
							try {
								final LatLngBounds bounds = boundsBuilder.build();
								final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										try {
											map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int)(160*minScaleRatio)), 1000, null);
										} catch (Exception e) {
											e.printStackTrace();
										}
										mapTouchedOnce = true;
									}
								}, 1000);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else{
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									try {
										map.animateCamera(CameraUpdateFactory.newLatLng(userLatLng), 1000, null);
									} catch (Exception e) {
										e.printStackTrace();
									}
									mapTouchedOnce = true;
								}
							}, 1000);
						}
					}
					
				}
		}
	}
	
	
	
	
	
	public float getBatteryPercentage(){
		try {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = (level / (float)scale)*100;
			
			// Are we charging / charged?
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
			                     status == BatteryManager.BATTERY_STATUS_FULL;
			if(isCharging){
				return 70;
			}
			else{
				return batteryPct;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 70;
		}
	}
	
	

	
	
	
	
	
	
	/**
	 * ASync for cancelCustomerRequestAsync from server
	 */
	public void cancelCustomerRequestAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("session_id", Data.cSessionId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("session_id", "="+Data.cSessionId);
			
			
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/cancel_the_request", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
					
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							callAndHandleStateRestoreAPI();
						}

						
						@Override
						public void onSuccess(String response) {
							Log.e("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									customerUIBackToInitialAfterCancel();
									FlurryEventLogger.cancelRequestPressed(Data.userData.accessToken, Data.cSessionId);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void customerUIBackToInitialAfterCancel(){
		cancelTimerRequestRide();
		
		passengerScreenMode = PassengerScreenMode.P_INITIAL;
		switchPassengerScreen(passengerScreenMode);
		
		if(map != null && pickupLocationMarker != null){
			pickupLocationMarker.remove();
		}
			
		callMapTouchedRefreshDrivers();
	}
	
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void changeDriverModeAsync(final Activity activity, final int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("flag", "=" + flag);
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/switch_to_driver_mode", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(1 == flag){
										makeMeDriverPopup(activity, errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									if(flag == 1){
										try {
											int excepInt = jObj.getInt("exceptional_driver");
											Data.userData.exceptionalDriver = excepInt;
										} catch (Exception e) {
											Data.userData.exceptionalDriver = 0;
											e.printStackTrace();
										}
										
										changeExceptionalDriverUI();
										
										userMode = UserMode.DRIVER;
										
										switchUserScreen(userMode);
										
										driverScreenMode = DriverScreenMode.D_INITIAL;
										switchDriverScreen(driverScreenMode);
										
										getAndShowAllDriverRequests(activity);
										
										if(UserMode.DRIVER == userMode){
											buildTimeSettingsAlertDialog(activity);
										}
										
									}
									else{
										
										userMode = UserMode.PASSENGER;
										
										
										switchUserScreen(userMode);
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
									
									enableJugnooShopUI();
									enableJugnooMealsUI();
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void deleteAllDriverRequests(final Activity activity){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2.getInstance(activity).deleteAllDriverRequests();
				Database2.getInstance(activity).close();
			}
		}).start();
	}
	
	
	public void deleteParticularDriverRequest(final Activity activity, final String engagementId){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Database2.getInstance(activity).deleteDriverRequest(engagementId);
				Database2.getInstance(activity).close();
			}
		}).start();
	}
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverAcceptRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			 
			if(myLocation != null){
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}
			
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);

			Log.e("accept ride api", "=");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("user_id", "=" + Data.dCustomerId);
			Log.i("engage_id", "=" + Data.dEngagementId);
			Log.i("latitude", "="+Data.latitude);
			Log.i("longitude", "="+Data.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/accept_a_request", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							DialogPopup.dismissLoadingDialog();
							callAndHandleStateRestoreAPI();
						}

						@Override
						public void onSuccess(String response) {
							Log.v("accept ride api Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									Log.e("accept_a_request flag", "="+flag);
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									
//									response = {"error":"Request timed out","flag":10}
									DialogPopup.dismissLoadingDialog();
									
									reduceRideRequest(activity, Data.dEngagementId);
									
								}
								else{
									
									if(jObj.has("flag")){
										try{
											int flag = jObj.getInt("flag");
											Log.e("accept_a_request flag", "="+flag);
											String logMessage = jObj.getString("log");
											new DialogPopup().alertPopup(activity, "", ""+logMessage);
											
										} catch(Exception e){
											e.printStackTrace();
										}
										reduceRideRequest(activity, Data.dEngagementId);
									}
									else{
//										{
//											Òuser_dataÓ: {
//										            "user_name": result_customer[0].user_name,
//										            "phone_no": result_customer[0].phone_no,
//										            "user_image": result_customer[0].user_image,
//										            "user_rating": rating};
										
										JSONObject userData = jObj.getJSONObject("user_data");
										
										String userName = userData.getString("user_name");
										String userImage = userData.getString("user_image");
										String phoneNo = userData.getString("phone_no");
										String rating = "4";
										try{
											rating = userData.getString("user_rating");
										} catch(Exception e){
											e.printStackTrace();
										}
										if(userName == null){
											userName = "";
										}
										if(userImage == null){
											userImage = "http://jugnoo-images.s3.amazonaws.com/user_profile/user.png";
										}
										if(phoneNo == null){
											phoneNo = "";
										}
										
										
										int isScheduled = 0;
										String pickupTime = "";
										if(jObj.has("is_scheduled")){
											isScheduled = jObj.getInt("is_scheduled");
											if(isScheduled == 1 && jObj.has("pickup_time")){
												pickupTime = jObj.getString("pickup_time");
											}
										}
										
										Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, userName,
												userImage, phoneNo, rating);
										Data.assignedCustomerInfo.schedulePickupTime = pickupTime;
										
										Data.driverRideRequests.clear();
										
										SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
										Editor editor = pref.edit();
										editor.putString(Data.SP_DRIVER_SCREEN_MODE, Data.D_START_RIDE);
										
										editor.putString(Data.SP_D_ENGAGEMENT_ID, Data.dEngagementId);
										editor.putString(Data.SP_D_CUSTOMER_ID, Data.dCustomerId);
										
										editor.putString(Data.SP_D_LATITUDE, ""+Data.dCustLatLng.latitude);
										editor.putString(Data.SP_D_LONGITUDE, ""+Data.dCustLatLng.longitude);
										
										editor.putString(Data.SP_D_CUSTOMER_NAME, Data.assignedCustomerInfo.name);
										editor.putString(Data.SP_D_CUSTOMER_IMAGE, Data.assignedCustomerInfo.image);
										editor.putString(Data.SP_D_CUSTOMER_PHONE, Data.assignedCustomerInfo.phoneNumber);
										editor.putString(Data.SP_D_CUSTOMER_RATING, Data.assignedCustomerInfo.rating);
										
										editor.commit();
	
								        GCMIntentService.clearNotifications(getApplicationContext());
								        
										driverScreenMode = DriverScreenMode.D_START_RIDE;
										switchDriverScreen(driverScreenMode);
										
										deleteAllDriverRequests(HomeActivity.this);
										
									}
									
									
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
							
							DialogPopup.dismissLoadingDialog();
							
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	
	public void reduceRideRequest(Activity activity, String engagementId){
		deleteParticularDriverRequest(activity, engagementId);
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
	}
	
	
	public void getAndShowAllDriverRequests(final Activity activity){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Data.driverRideRequests.clear();
				Data.driverRideRequests.addAll(Database2.getInstance(activity).getAllDriverRequests());
				Database2.getInstance(activity).close();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showAllRideRequestsOnMap();
					}
				});
			}
		}).start();
	}
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverRejectRequestAsync(final Activity activity) {

			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
			
				
				params.put("access_token", Data.userData.accessToken);
				params.put("customer_id", Data.dCustomerId);
				params.put("engagement_id", Data.dEngagementId);

				Log.i("access_token", "=" + Data.userData.accessToken);
				Log.i("customer_id", "=" + Data.dCustomerId);
				Log.i("engagement_id", "=" + Data.dEngagementId);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/reject_a_request", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
//										{"log":"rejected successfully"}
//										{"log":"Rejected successfully","flag":110}
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												new DialogPopup().alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										
										if(map != null){
											map.clear();
										}
										
										reduceRideRequest(activity, Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	public void initializeStartRideVariables(){
		if(createPathAsyncTasks != null){
    		createPathAsyncTasks.clear();
    	}
		
		lastLocation = null;
		
		HomeActivity.previousWaitTime = 0;
		HomeActivity.previousRideTime = 0;
		HomeActivity.totalDistance = -1;
		
		if(HomeActivity.deltaLatLngPairs == null){
			HomeActivity.deltaLatLngPairs = new ArrayList<LatLngPair>();
		}
		HomeActivity.deltaLatLngPairs.clear();
		
		clearRideSPData();
		
		waitStart = 2;
	}
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverStartRideAsync(final Activity activity, final LatLng driverAtPickupLatLng) {
		initializeStartRideVariables();
		
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("pickup_latitude", ""+driverAtPickupLatLng.latitude);
			params.put("pickup_longitude", ""+driverAtPickupLatLng.longitude);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("pickup_latitude", "=" + driverAtPickupLatLng.latitude);
			Log.i("pickup_longitude", "=" + driverAtPickupLatLng.longitude);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/start_ride", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
//							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							callAndHandleStateRestoreAPI();
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
									
//									{"log":"ride_started"}

									if(map != null){
										map.clear();
									}
									
									initializeStartRideVariables();
									
									Data.startRidePreviousLatLng = driverAtPickupLatLng;
									SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
									Editor editor = pref.edit();
									editor.putString(Data.SP_LAST_LATITUDE, "" + driverAtPickupLatLng.latitude);
									editor.putString(Data.SP_LAST_LONGITUDE, "" + driverAtPickupLatLng.longitude);
									editor.commit();
									Log.e("driverAtPickupLatLng in start_ride", "=" + driverAtPickupLatLng);
									writePathLogToFile("on Start driverAtPickupLatLng"+driverAtPickupLatLng);
									
						        	driverScreenMode = DriverScreenMode.D_IN_RIDE;
									switchDriverScreen(driverScreenMode);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	
	/**
	 * ASync for change driver mode from server
	 */
	public void driverCancelRideAsync(final Activity activity) {

			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
			
				
				params.put("access_token", Data.userData.accessToken);
				params.put("customer_id", Data.dCustomerId);
				params.put("engagement_id", Data.dEngagementId);

				Log.e("access_token", "=" + Data.userData.accessToken);
				Log.e("customer_id", "=" + Data.dCustomerId);
				Log.e("engagement_id", "=" + Data.dEngagementId);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/cancel_the_ride", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
//								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
								callAndHandleStateRestoreAPI();
							}

							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
//										{"log":"rejected successfully"}
//										response = {"log":"Ride cancelled successfully","flag":109}
										
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												new DialogPopup().alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										if(map != null){
											map.clear();
										}
										
										reduceRideRequest(activity, Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, double dropLatitude, double dropLongitude, double waitMinutes, double rideMinutes) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			if(createPathAsyncTasks != null){
        		createPathAsyncTasks.clear();
        	}

			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			SharedPreferences pref = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis()));
			long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
			long rideTimeSeconds = timeDiffToAdd / 1000;
			double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
			Log.e("System.currentTimeMillis() - rideStartTime", "="+System.currentTimeMillis() + " - " +  rideStartTime);
			Log.e("timeDiffToAdd", "="+rideTimeMinutes);
			if(rideTimeMinutes > 0){
				rideMinutes = rideTimeMinutes;
			}
			
			rideTime = decimalFormatNoDecimal.format(rideMinutes);
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("latitude", ""+dropLatitude);
			params.put("longitude", ""+dropLongitude);
			params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
			params.put("wait_time", waitTime);
			params.put("ride_time", rideTime);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", "=" + Data.dEngagementId);
			Log.i("customer_id", "=" + Data.dCustomerId);
			Log.i("latitude", "="+dropLatitude);
			Log.i("longitude", "="+dropLongitude);
			Log.i("distance_travelled", "="+decimalFormat.format(totalDistanceInKm));
			Log.i("wait_time", "="+waitTime);
			Log.i("ride_time", "="+rideTime);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/end_ride", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							driverScreenMode = DriverScreenMode.D_IN_RIDE;
							DialogPopup.dismissLoadingDialog();
							rideTimeChronometer.start();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.e("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									driverScreenMode = DriverScreenMode.D_IN_RIDE;
									rideTimeChronometer.start();
								}
								else{
									
//									{"log":"ride_ended"}//result
									
									try{
										totalFare = jObj.getDouble("fare");
									} catch(Exception e){
										e.printStackTrace();
										totalFare = 0;
									}
									
									displayCouponApplied(jObj);
									

									lastLocation = null;
									
									if(map != null){
										map.clear();
									}
									
									waitStart = 2;
									waitChronometer.stop();
									rideTimeChronometer.stop();
									
									
									clearSPData();
									
						        	driverScreenMode = DriverScreenMode.D_RIDE_END;
									switchDriverScreen(driverScreenMode);
									
									driverUploadPathDataFileAsync(activity, Data.dEngagementId);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								rideTimeChronometer.start();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			driverScreenMode = DriverScreenMode.D_IN_RIDE;
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			rideTimeChronometer.start();
		}

	}
	
	
	
	/**
	 * ASync for uploading path data file to server
	 */
	public void driverUploadPathDataFileAsync(final Activity activity, final String engagementId) {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				File pathLogFile = null;
				try{
					pathLogFile = Log.getPathLogFile(engagementId);
					if(pathLogFile != null){
						RequestParams params = new RequestParams();
						params.put("access_token", Data.userData.accessToken);
						params.put("engagement_id", engagementId);
						params.put("ride_path", pathLogFile);
						params.put("file_type", "0");
						
						Log.e("access_token", "=" + Data.userData.accessToken);
						Log.e("engagement_id", "=" + engagementId);
						Log.e("pathLogFile", "=" + pathLogFile);
					
						AsyncHttpClient client = Data.getClient();
						client.post(Data.SERVER_URL + "/upload_file", params,
								new CustomAsyncHttpResponseHandler() {

									@Override
									public void onFailure(Throwable arg3) {
										Log.e("request fail", arg3.toString());
									}

									@Override
									public void onSuccess(String response) {
										Log.e("Server response on upload path file", "response = " + response);
									}
								});
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
	}
	
	
	public void displayCouponApplied(JSONObject jObj){
		
		try{
		
			int paymentMode = PaymentMode.CASH.getOrdinal();
			if(jObj.has("payment_mode")){
				paymentMode = jObj.getInt("payment_mode");
			}
			
			String moneyToPay = decimalFormat.format(jObj.getDouble("to_pay"));
			
			try {
				
				if(jObj.has("coupon")){
					endRideInfoRl.setVisibility(View.GONE);
					relativeLayoutCoupon.setVisibility(View.VISIBLE);
					
					JSONObject couponObject = jObj.getJSONObject("coupon");
					
					String couponTitle = couponObject.getString("title");
					String couponSubTitle = couponObject.getString("subtitle");
					
					if(PaymentMode.WALLET.getOrdinal() == paymentMode){					// wallet
						textViewCouponDiscountedFare.setText("Rs. "+moneyToPay);
						textViewCouponTitle.setText(couponTitle + "\n& Jugnoo Cash");
						textViewCouponSubTitle.setVisibility(View.GONE);
					}
					else{																			// no wallet
						textViewCouponDiscountedFare.setText("Rs. "+moneyToPay);
						textViewCouponTitle.setText(couponTitle);
						textViewCouponSubTitle.setText(couponSubTitle);
						textViewCouponSubTitle.setVisibility(View.VISIBLE);
					}
					
					if(UserMode.DRIVER == HomeActivity.userMode){
						textViewCouponPayTakeText.setText("Take");
					}
					else{
						textViewCouponPayTakeText.setText("Pay");
					}
				}
				else{
					if(PaymentMode.WALLET.getOrdinal() == paymentMode){								// wallet
						textViewCouponDiscountedFare.setText("Rs. "+moneyToPay);
						textViewCouponTitle.setText("Jugnoo Cash");
						textViewCouponSubTitle.setVisibility(View.GONE);
						if(UserMode.DRIVER == HomeActivity.userMode){
							textViewCouponPayTakeText.setText("Take");
						}
						else{
							textViewCouponPayTakeText.setText("Pay");
						}
							
						endRideInfoRl.setVisibility(View.GONE);
						relativeLayoutCoupon.setVisibility(View.VISIBLE);
					}
					else{																			// no wallet
						endRideInfoRl.setVisibility(View.VISIBLE);
						relativeLayoutCoupon.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(PaymentMode.WALLET.getOrdinal() == paymentMode){								// wallet
					textViewCouponDiscountedFare.setText("Rs. "+moneyToPay);
					textViewCouponTitle.setText("Jugnoo Cash");
					textViewCouponSubTitle.setVisibility(View.GONE);
					if(UserMode.DRIVER == HomeActivity.userMode){
						textViewCouponPayTakeText.setText("Take");
					}
					else{
						textViewCouponPayTakeText.setText("Pay");
					}
						
					endRideInfoRl.setVisibility(View.GONE);
					relativeLayoutCoupon.setVisibility(View.VISIBLE);
				}
				else{																			// no wallet
					endRideInfoRl.setVisibility(View.VISIBLE);
					relativeLayoutCoupon.setVisibility(View.GONE);
				}
			}
		
			
			if(jObj.has("jugnoo_balance")){
				Data.userData.jugnooBalance = jObj.getDouble("jugnoo_balance");
				setUserData();
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void submitReviewAsync(final Activity activity, String engagementId, final String flag, String ratingReceiverId, String givenRating, String feedbackText) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);
			params.put("flag", flag);
			params.put("rating_receiver_id", ratingReceiverId);
			params.put("given_rating", givenRating);
			params.put("feedback", feedbackText);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", engagementId);
			Log.i("flag", flag);
			Log.i("rating_receiver_id", ratingReceiverId);
			Log.i("given_rating", givenRating);
			Log.i("feedback", feedbackText);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/rating", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									
//									{"log":"Rated successfully"}
									

									if(flag.equalsIgnoreCase("0")){
										userMode = UserMode.DRIVER;
										
										switchUserScreen(userMode);
										
										driverScreenMode = DriverScreenMode.D_INITIAL;
										switchDriverScreen(driverScreenMode);
									}
									else{
										userMode = UserMode.PASSENGER;
										
										switchUserScreen(userMode);
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
									
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, String ratingReceiverId, final int givenRating, String feedbackText) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("given_rating", ""+givenRating);
			params.put("engagement_id", engagementId);
			params.put("driver_id", ratingReceiverId);
			params.put("feedback", feedbackText);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("given_rating", ""+givenRating);
			Log.i("engagement_id", engagementId);
			Log.i("driver_id", ratingReceiverId);
			Log.i("feedback", feedbackText);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/rate_the_driver", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
									HomeActivity.logoutUser(activity);
								}
								else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
									String errorMessage = jObj.getString("error");
									new DialogPopup().alertPopup(activity, "", errorMessage);
								}
								else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
									String message = jObj.getString("message");
									new DialogPopup().alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
									userMode = UserMode.PASSENGER;
									
									switchUserScreen(userMode);
									
									passengerScreenMode = PassengerScreenMode.P_INITIAL;
									switchPassengerScreen(passengerScreenMode);
									
									if(givenRating >= 4 && Data.customerRateAppFlag == 1){
										rateAppPopup(activity);
									}
								}
								else{
									new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	public void skipFeedbackForCustomerAsync(final Activity activity, String engagementId) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", engagementId);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/skip_rating_by_customer", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										userMode = UserMode.PASSENGER;
										
										switchUserScreen(userMode);
										
										passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
									else{
										new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	void saveToFavoritePopup(final Activity activity, String locationName, final LatLng favLatLng){

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.save_to_favorite_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(getApplicationContext()));
			final EditText favoriteNameEt = (EditText) dialog.findViewById(R.id.favoriteNameEt); favoriteNameEt.setTypeface(Data.latoRegular(getApplicationContext()));
			
			favoriteNameEt.setText(locationName);
			
			favoriteNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					favoriteNameEt.setError(null);
				}
			});
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(getApplicationContext()));
			Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.latoRegular(getApplicationContext()));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					String favName = favoriteNameEt.getText().toString().trim();
					
					if(!"".equalsIgnoreCase(favName)){
						dialog.dismiss();
						saveToFavoriteAsync(activity, favName, favLatLng);
					}
					else{
						favoriteNameEt.requestFocus();
						favoriteNameEt.setError("Favorite location name cannot be empty.");
					}
					
				}
				
			});
			
			
			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * ASync for saveToFavoriteAsync from server
	 */
	public void saveToFavoriteAsync(final Activity activity, String favName, LatLng favLatLng) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("fav_name", favName);
			params.put("fav_latitude", ""+favLatLng.latitude);
			params.put("fav_longitude", ""+favLatLng.longitude);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("fav_name", "="+favName);
			Log.i("fav_latitude", "="+favLatLng.latitude);
			Log.i("fav_longitude", "="+favLatLng.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/fav_locations", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(0 == flag){ // {"error": 'some parameter missing',"flag":0}//error
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	/**
	 * ASync for logout from server
	 */
	public void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("client_id", Data.CLIENT_ID);

			Log.i("access_token", "="+Data.userData.accessToken);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL+"/logout_user", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){

									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{

									PicassoTools.clearCache(Picasso.with(HomeActivity.this));
									
									new FacebookLoginHelper().logoutFacebook();
									
									GCMIntentService.clearNotifications(HomeActivity.this);
									
									Data.clearDataOnLogout(HomeActivity.this);
									
									userMode = UserMode.PASSENGER;
									passengerScreenMode = PassengerScreenMode.P_INITIAL;
									driverScreenMode = DriverScreenMode.D_INITIAL;
									
									loggedOut = true;
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}

						
						@Override
						public void onRetry(int retryNo) {
							Log.e("retryNo","="+retryNo);
							super.onRetry(retryNo);
						}
						
						
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	/**
	 * ASync for start or end wait from server
	 */
	public void startEndWaitAsync(final Activity activity, String customerId, int flag) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", customerId);
			params.put("flag", ""+flag);

			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("customer_id", "="+customerId);
			Log.i("flag", "="+flag);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/start_end_wait", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
								}
								else{
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
	
						}
					});
		}
		else {
		}

	}
	
	
	
	
	void logoutPopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.logout_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					logoutAsync(activity);
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	void noDriverAvailablePopup(final Activity activity, boolean zeroDriversNearby, String message){
		try {
			if(noDriversDialog != null && noDriversDialog.isShowing()){
				noDriversDialog.dismiss();
			}
			noDriversDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			noDriversDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			noDriversDialog.setContentView(R.layout.no_driver_dialog);

			FrameLayout frameLayout = (FrameLayout) noDriversDialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = noDriversDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			noDriversDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			noDriversDialog.setCancelable(false);
			noDriversDialog.setCanceledOnTouchOutside(false);

			TextView textHead = (TextView) noDriversDialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			textHead.setVisibility(View.GONE);
			TextView textMessage = (TextView) noDriversDialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			if(zeroDriversNearby){
				textMessage.setText("Sorry there are no drivers available nearby within 3 km. We will look into it");
			}
			else{
				if("".equalsIgnoreCase(message)){
					textMessage.setText("Sorry, All our drivers are currently busy. We are unable to offer you services right now. Please try again sometime later.");
				}
				else{
					textMessage.setText(message);
				}
				//"Sorry, All our drivers are currently busy. We are unable to offer you services right now. Please try again sometime later."
			}
			

			Button btnOk = (Button) noDriversDialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button crossbtn = (Button) noDriversDialog
					.findViewById(R.id.crossbtn);
			crossbtn.setTypeface(Data.latoRegular(activity));
			crossbtn.setVisibility(View.GONE);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					noDriversDialog.dismiss();
//					noDriverAsync(activity);
					noDriversDialog = null;
				}
			});

			crossbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					noDriversDialog.dismiss();
					noDriverAsync(activity);
					noDriversDialog = null;
				}

			});

			noDriversDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * ASync for no drivers from server
	 */
	public void noDriverAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);

			if(myLocation != null){
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}
			
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/request_now", params,
					new CustomAsyncHttpResponseHandler() {

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
						}
					});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	void makeMeDriverPopup(final Activity activity, String message){
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_two_btn_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText("Alert");
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					makeMeDriverAsync(activity);
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * ASync for make me driver server
	 */
	public void makeMeDriverAsync(final Activity activity) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/make_me_driver_request", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.v("Server response", "response = " + response);

								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										

										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											new DialogPopup().alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
//									{"log": "Thank you for signing up. We will reach out to you soon."}
										
										new DialogPopup().alertPopup(activity, "", jObj.getString("log"));

									
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
								}

								DialogPopup.dismissLoadingDialog();
								
							}
						});
			}
			else {
				new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void acceptAppRatingRequestAPI(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/accept_app_rating_request", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
							}

							@Override
							public void onSuccess(String response) {
								Log.i("Server response accept_app_rating_request", "response = " + response);
								try {
									jObj = new JSONObject(response);
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										String errorMessage = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										String message = jObj.getString("message");
										new DialogPopup().alertPopup(activity, "", message);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										
									}
									else{
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
								}
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	//Customer's timer
	Timer timerDriverLocationUpdater;
	TimerTask timerTaskDriverLocationUpdater;
	
	public void startDriverLocationUpdateTimer(){
		
		try{
			if(timerTaskDriverLocationUpdater != null){
				timerTaskDriverLocationUpdater.cancel();
				timerTaskDriverLocationUpdater = null;
			}
			
			if(timerDriverLocationUpdater != null){
				timerDriverLocationUpdater.cancel();
				timerDriverLocationUpdater.purge();
				timerDriverLocationUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			timerDriverLocationUpdater = new Timer();
			
			timerTaskDriverLocationUpdater = new TimerTask() {
				
				@Override
				public void run() {
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						
						if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
								
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
							nameValuePairs.add(new BasicNameValuePair("driver_id", Data.assignedDriverInfo.userId));
							
							
							
							HttpRequester simpleJSONParser = new HttpRequester();
							String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/get_driver_current_location", nameValuePairs);
							
							
							if(result.contains(HttpRequester.SERVER_TIMEOUT)){
							}
							else{
								try {
									JSONObject jObj = new JSONObject(result);
									
									if(!jObj.isNull("error")){
	
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											
										}
									}
									else{
										
	//								{
	//								    "data": [
	//								        {
	//								            "user_id": 192,
	//								            "current_location_latitude": 30.718788,
	//								            "current_location_longitude": 76.810109
	//								        }
	//								    ]
	//								}
										
										JSONArray data = jObj.getJSONArray("data");
										JSONObject data0 = data.getJSONObject(0);
										final LatLng driverCurrentLatLng = new LatLng(data0.getDouble("current_location_latitude"),
												data0.getDouble("current_location_longitude"));
										
										if(Data.assignedDriverInfo != null){
											Data.assignedDriverInfo.latLng = driverCurrentLatLng;
										}
										
										runOnUiThread(new Runnable() {
											
											@Override
											public void run() {
												try{
													if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
														driverLocationMarker.setPosition(driverCurrentLatLng);
													}
												} catch(Exception e){
													e.printStackTrace();
												}
											}
										});
										
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
					
				}
			};
			
			
			timerDriverLocationUpdater.scheduleAtFixedRate(timerTaskDriverLocationUpdater, 10, 15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelDriverLocationUpdateTimer(){
		try{
			if(timerTaskDriverLocationUpdater != null){
				timerTaskDriverLocationUpdater.cancel();
				timerTaskDriverLocationUpdater = null;
			}
			
			if(timerDriverLocationUpdater != null){
				timerDriverLocationUpdater.cancel();
				timerDriverLocationUpdater.purge();
				timerDriverLocationUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//Driver's timer
	Timer timerCustomerPathUpdater;
	TimerTask timerTaskCustomerPathUpdater;
	
	
	public void startCustomerPathUpdateTimer(){
		
		try{
			if(timerTaskCustomerPathUpdater != null){
				timerTaskCustomerPathUpdater.cancel();
				timerTaskCustomerPathUpdater = null;
			}
			
			if(timerCustomerPathUpdater != null){
				timerCustomerPathUpdater.cancel();
				timerCustomerPathUpdater.purge();
				timerCustomerPathUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			timerCustomerPathUpdater = new Timer();
			
			timerTaskCustomerPathUpdater = new TimerTask() {
				
				@Override
				public void run() {
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
							//customer path updater
							if(myLocation != null){
								if(Data.dCustLatLng != null){
									
									if(driverScreenMode == DriverScreenMode.D_START_RIDE){
										LatLng source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
										String url = MapUtils.makeDirectionsURL(source, Data.dCustLatLng);
										String result = new HttpRequester().getJSONFromUrl(url);
										
										if(result != null){
											final List<LatLng> list = getLatLngListFromPath(result);
											
											runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													try {
														if(driverScreenMode == DriverScreenMode.D_START_RIDE){
															map.clear();
															
															if(markerOptionsCustomerPickupLocation == null){
																markerOptionsCustomerPickupLocation = new MarkerOptions();
																markerOptionsCustomerPickupLocation.title(Data.dEngagementId);
																markerOptionsCustomerPickupLocation.snippet("");
																markerOptionsCustomerPickupLocation.position(Data.dCustLatLng);
																markerOptionsCustomerPickupLocation.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPassengerMarkerBitmap(HomeActivity.this, assl)));
															}
															
															map.addMarker(markerOptionsCustomerPickupLocation);
															
															for(int z = 0; z<list.size()-1;z++){
															    LatLng src= list.get(z);
															    LatLng dest= list.get(z+1);
															    map.addPolyline(new PolylineOptions()
															    .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
															    .width(5)
															    .color(D_TO_C_MAP_PATH_COLOR).geodesic(true));
															}
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											});
								        }
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			
			
			timerCustomerPathUpdater.scheduleAtFixedRate(timerTaskCustomerPathUpdater, 10, 15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelCustomerPathUpdateTimer(){
		try{
			if(timerTaskCustomerPathUpdater != null){
				timerTaskCustomerPathUpdater.cancel();
				timerTaskCustomerPathUpdater = null;
			}
			
			if(timerCustomerPathUpdater != null){
				timerCustomerPathUpdater.cancel();
				timerCustomerPathUpdater.purge();
				timerCustomerPathUpdater = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	

	
	//Customer's timer
    Timer timerUpdateDrivers;
	TimerTask timerTaskUpdateDrivers;
	
	public void startTimerUpdateDrivers(){
		cancelTimerUpdateDrivers();
		try {
			timerUpdateDrivers = new Timer();
			timerTaskUpdateDrivers = new TimerTask() {
				@Override
				public void run() {
					try {
						if(HomeActivity.appInterruptHandler != null){
							HomeActivity.appInterruptHandler.refreshDriverLocations();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			timerUpdateDrivers.scheduleAtFixedRate(timerTaskUpdateDrivers, 100, 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelTimerUpdateDrivers(){
		try{
			if(timerTaskUpdateDrivers != null){
				timerTaskUpdateDrivers.cancel();
				timerTaskUpdateDrivers = null;
			}
			if(timerUpdateDrivers != null){
				timerUpdateDrivers.cancel();
				timerUpdateDrivers.purge();
				timerUpdateDrivers = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	public void updateInRideData(){
		if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
			if(myLocation != null){
				double totalDistanceInKm = Math.abs(totalDistance/1000.0);
				
				long rideTimeSeconds = rideTimeChronometer.eclipsedTime / 1000;
				double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
				nameValuePairs.add(new BasicNameValuePair("engagement_id", Data.dEngagementId));
				nameValuePairs.add(new BasicNameValuePair("current_latitude", ""+myLocation.getLatitude()));
				nameValuePairs.add(new BasicNameValuePair("current_longitude", ""+myLocation.getLongitude()));
				nameValuePairs.add(new BasicNameValuePair("distance_travelled", decimalFormat.format(totalDistanceInKm)));
				nameValuePairs.add(new BasicNameValuePair("ride_time", decimalFormatNoDecimal.format(rideTimeMinutes)));
				nameValuePairs.add(new BasicNameValuePair("wait_time", "0"));
				
				Log.i("update_in_ride_data nameValuePairs", "="+nameValuePairs);
				
				HttpRequester simpleJSONParser = new HttpRequester();
				String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/update_in_ride_data", nameValuePairs);
				Log.i("update_in_ride_data result", "="+result);
			}
		}
	}
	
	
	
	//Both driver and customer
	Timer timerMapAnimateAndUpdateRideData;
	TimerTask timerTaskMapAnimateAndUpdateRideData;
	
	
	public void startMapAnimateAndUpdateRideDataTimer() {
		cancelMapAnimateAndUpdateRideDataTimer();
		try {
			timerMapAnimateAndUpdateRideData = new Timer();

			timerTaskMapAnimateAndUpdateRideData = new TimerTask() {

				@Override
				public void run() {
					try {
						updateInRideData();
						if (myLocation != null && map != null) {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			timerMapAnimateAndUpdateRideData.scheduleAtFixedRate(timerTaskMapAnimateAndUpdateRideData, 100, 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void cancelMapAnimateAndUpdateRideDataTimer(){
		try{
			if(timerTaskMapAnimateAndUpdateRideData != null){
				timerTaskMapAnimateAndUpdateRideData.cancel();
				timerTaskMapAnimateAndUpdateRideData = null;
			}
			
			if(timerMapAnimateAndUpdateRideData != null){
				timerMapAnimateAndUpdateRideData.cancel();
				timerMapAnimateAndUpdateRideData.purge();
				timerMapAnimateAndUpdateRideData = null;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public List<LatLng> getLatLngListFromPath(String result){
		try {
	    	 final JSONObject json = new JSONObject(result);
		     JSONArray routeArray = json.getJSONArray("routes");
		     JSONObject routes = routeArray.getJSONObject(0);
		     JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		     String encodedString = overviewPolylines.getString("points");
		     List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
		     return list;
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return new ArrayList<LatLng>();
	    }
	}
	
	
	
	
	
	
	
	
	
	
	void callAnAutoPopup(final Activity activity) {
		try {
			final String getAuto = "Do you want an auto to pick you up?";
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.call_an_auto_dialog);

			new ASSL(activity, (FrameLayout)dialog.findViewById(R.id.rv), 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			
			textHead.setVisibility(View.VISIBLE);
			textHead.setText("Chalo Jugnoo Se");
			
			if(Data.userData.canChangeLocation == 1){
				Data.pickupLatLng = map.getCameraPosition().target;
				double distance = MapUtils.distance(Data.pickupLatLng, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				if(distance > MAP_PAN_DISTANCE_CHECK){
					textMessage.setText("The pickup location you have set is different from your current location. Are you sure you want an auto at this pickup location?");
				}
				else{
					textMessage.setText(getAuto);
				}
			}
			else{
				textMessage.setText(getAuto);
			}
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
			
			btnOk.setText("Get Now");
			btnCancel.setText("Later");
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					initiateRequestRide(true);
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if(Data.userData != null){
						if(Data.userData.canSchedule == 1){
							switchToScheduleScreen(activity);
						}
					}
				}
			});
			
			
			dialog.findViewById(R.id.crossbtn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			
			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			
			
			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	void switchToScheduleScreen(Activity activity){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.add(Calendar.MINUTE, 5);
		ScheduleRideActivity.selectedScheduleCalendar = calendar;
		ScheduleRideActivity.selectedScheduleLatLng = null;
		ScheduleRideActivity.scheduleOperationMode = ScheduleOperationMode.INSERT;
		activity.startActivity(new Intent(activity, ScheduleRideActivity.class));
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	
	
	
	void startRidePopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.custom_two_btn_dialog);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			
			textMessage.setText("Are you sure you want to start ride?");
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(myLocation != null){
						dialog.dismiss();
						LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			        	double displacement = MapUtils.distance(driverAtPickupLatLng, Data.dCustLatLng);
			        	
			        	if(displacement <= DRIVER_START_RIDE_CHECK_METERS){
			        		buildAlertMessageNoGps();
				        	
				        	GCMIntentService.clearNotifications(activity);
				        	
				        	driverStartRideAsync(activity, driverAtPickupLatLng);
			        	}
			        	else{
			        		new DialogPopup().alertPopup(activity, "", "You must be present near the customer pickup location to start ride.");
			        	}
					}
					else{
						Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
					}
		        	
				}
				
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	void endRidePopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				
				textMessage.setText("Are you sure you want to end ride?");
				
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(myLocation != null){
							dialog.dismiss();
	
							GCMIntentService.clearNotifications(HomeActivity.this);
							waitChronometer.stop();
							rideTimeChronometer.stop();
							
							driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
							driverWaitText.setText(getResources().getString(R.string.start_wait));
							waitStart = 0;
							
//							long waitSeconds = waitChronometer.eclipsedTime / 1000;
//							double waitMinutes = Math.ceil(((double)waitSeconds) / 60.0);
							
							long rideTimeSeconds = rideTimeChronometer.eclipsedTime / 1000;
							double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
							
							driverScreenMode = DriverScreenMode.D_RIDE_END;
							
				        	driverEndRideAsync(activity, myLocation.getLatitude(), myLocation.getLongitude(), 0, rideTimeMinutes);
						}
						else{
							Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
						}
			        	
					}
					
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	
		void cancelRidePopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.custom_two_btn_dialog);

					FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
					
					textMessage.setText("Are you sure you want to cancel ride?");
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							
							GCMIntentService.clearNotifications(HomeActivity.this);
							driverCancelRideAsync(HomeActivity.this);
						}
						
						
					});
					
					btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
						}
						
					});

					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
		
		
		
		/**
		 * Displays popup to rate the app
		 */
		public void rateAppPopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog_with_title);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
				
				textHead.setVisibility(View.VISIBLE);
				textHead.setText("Rate Us");
				textMessage.setText("Liked our services!!! Please rate us on Play Store");
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity)); btnOk.setText("RATE NOW");
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity)); btnCancel.setText("LATER");
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn);
				crossbtn.setVisibility(View.GONE);
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						acceptAppRatingRequestAPI(activity);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=product.clicklabs.jugnoo"));
						activity.startActivity(intent);
					}
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				
				dialog.findViewById(R.id.innerRl).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					}
				});

				frameLayout.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		public void confirmDebugPasswordPopup(final Activity activity){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.edittext_confirm_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.latoRegular(activity));
				
				textHead.setText("Confirm Debug Password");
				textMessage.setText("Please enter password to continue.");
				
				textHead.setVisibility(View.GONE);
				textMessage.setVisibility(View.GONE);
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.latoRegular(activity));
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.latoRegular(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(Data.DEBUG_PASSWORD.equalsIgnoreCase(code)){
								dialog.dismiss();
								changeDebugModePopup(activity);
							}
							else{
								etCode.requestFocus();
								etCode.setError("Code not matched.");
							}
						}
					}
					
				});
				
				
				etCode.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
						int result = actionId & EditorInfo.IME_MASK_ACTION;
						switch (result) {
							case EditorInfo.IME_ACTION_DONE:
								btnConfirm.performClick();
							break;

							case EditorInfo.IME_ACTION_NEXT:
							break;

							default:
						}
						return true;
					}
				});
				
				crossbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		void changeDebugModePopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				
				
				if(appMode == AppMode.DEBUG){
					textMessage.setText("App is in DEBUG mode.\nChange to:");
				}
				else if(appMode == AppMode.NORMAL){
					textMessage.setText("App is in NORMAL mode.\nChange to:");
				}
				
				
				
				
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
				btnOk.setText("NORMAL");
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
				btnCancel.setText("DEBUG");
				
				Button crossbtn = (Button) dialog.findViewById(R.id.crossbtn); crossbtn.setTypeface(Data.latoRegular(activity));
				crossbtn.setVisibility(View.VISIBLE);
				
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						appMode = AppMode.NORMAL;
						dialog.dismiss();
					}
					
					
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						appMode = AppMode.DEBUG;
						dialog.dismiss();
					}
					
				});
				
				crossbtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});
				

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		void debugDriverInfoPopup(final Activity activity, final DriverInfo driverInfo){
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.custom_two_btn_dialog);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
				
				textHead.setText("Driver Info");
				textMessage.setText(driverInfo.toString());
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity)); btnOk.setText("Call");
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						Intent callIntent = new Intent(Intent.ACTION_VIEW);
				        callIntent.setData(Uri.parse("tel:"+driverInfo.phoneNumber));
				        startActivity(callIntent);
					}
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
					
				});

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		

		
		
		
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus){
			if(loggedOut){
				loggedOut = false;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_OFFLINE);
						Database2.getInstance(HomeActivity.this).close();
					}
				}).start();
				
				cancelTimerUpdateDrivers();
				
		        Intent intent = new Intent(HomeActivity.this, SplashNewActivity.class);
				intent.putExtra("no_anim", "yes");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
			else{
				buildAlertMessageNoGps();
			}
		}
	}
	
	
	
	
	
	

	@Override
	public void startRideForCustomer(final int flag) {
		try {
			if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
			Log.e("in ","herestartRideForCustomer");
			
			if(flag == 0){
			
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("in in herestartRideForCustomer  run class","=");
						initializeStartRideVariables();
						passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
						switchPassengerScreen(passengerScreenMode);
					}
				});
				
			}
			else{
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.i("in in herestartRideForCustomer  run class","=");
						passengerScreenMode = PassengerScreenMode.P_INITIAL;
						switchPassengerScreen(passengerScreenMode);
						new DialogPopup().alertPopup(HomeActivity.this, "", "Driver has canceled the ride.");
					}
				});
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}



	@Override
	public void refreshDriverLocations() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					callMapTouchedRefreshDrivers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	

	// 0 = not found   1 = accept
	@Override
	public void rideRequestAcceptedInterrupt(JSONObject jObj) {
		try {
			if(userMode == UserMode.PASSENGER){
				if(jObj.getString("session_id").equalsIgnoreCase(Data.cSessionId)){
					cancelTimerUpdateDrivers();
					cancelTimerRequestRide();
					fetchAcceptedDriverInfoAndChangeState(jObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void fetchAcceptedDriverInfoAndChangeState(JSONObject jObj){
		try {
			cancelTimerRequestRide();
			passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
//			{
//	            "user_name": "Shankar Bhagwati",
//	            "session_id": 798,
//	            "driver_car_image": "",
//	            "flag": 5,
//	            "engagement_id": "2704",
//	            "phone_no": "+919780298413",
//	            "driver_id": 207,
//	            "rating": 4.888888888888889,
//	            "user_image": "http:\/\/graph.facebook.com\/717496164959213\/picture?width=160&height=160"
//	        }
			
			Data.cSessionId = jObj.getString("session_id");
			Data.cEngagementId = jObj.getString("engagement_id");
			
			Data.cDriverId = jObj.getString("driver_id");
			String userName = jObj.getString("user_name");
			String driverPhone = jObj.getString("phone_no");
			String driverImage = jObj.getString("user_image");
			String driverCarImage = jObj.getString("driver_car_image");
			double latitude = jObj.getDouble("current_location_latitude");
			double longitude = jObj.getDouble("current_location_longitude");
			double pickupLatitude, pickupLongitude;
			if(jObj.has("pickup_latitude")){
				pickupLatitude = jObj.getDouble("pickup_latitude");
				pickupLongitude = jObj.getDouble("pickup_longitude");
			}
			else{
				if(myLocation != null){
					pickupLatitude = myLocation.getLatitude();
					pickupLongitude = myLocation.getLongitude();
				}
				else{
					pickupLatitude = map.getCameraPosition().target.latitude;
					pickupLongitude = map.getCameraPosition().target.longitude;
				}
			}
			String carNumber = "";
			if(jObj.has("driver_car_no")){
				carNumber = jObj.getString("driver_car_no");
			}
			
			int freeRide = 0;
			if(jObj.has("free_ride")){
				freeRide = jObj.getInt("free_ride");
			}
			
			String driverRating = jObj.getString("rating");
			
			Data.pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName, 
					driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide);
			
			
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.e("assignedDriverInfo", "="+Data.assignedDriverInfo);
					Log.e("myLocation", "="+myLocation);
					if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
					}
					if(myLocation != null){
						getDistanceTimeAddress = new GetDistanceTimeAddress(Data.pickupLatLng, true);
						getDistanceTimeAddress.execute();
					}
				}
			});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void fetchAcceptedDriverStartRideInfoAndChangeState(JSONObject jObj){
		try {
			cancelTimerRequestRide();
			passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
			
			// response = {
			// "flag": 114,
			// "driver_id": 1,
			// "user_name": "Name",
			// "phone_no": "0172",
			// "user_image": "abcd.png",
			// "driver_car_image": "wxyz.png",
			// "driver_car_number": "1234",
			// "rating": 5,
			// "current_location_latitude": 11,
			// "current_location_longitude": 11,
			// "engagement_id": 1,
			// "session_id": 1};};
			
			Data.cSessionId = jObj.getString("session_id");
			Data.cEngagementId = jObj.getString("engagement_id");
			
			Data.cDriverId = jObj.getString("driver_id");
			
			String userName = jObj.getString("user_name");
			String driverPhone = jObj.getString("phone_no");
			String driverImage = jObj.getString("user_image");
			String driverCarImage = jObj.getString("driver_car_image");
			double latitude = jObj.getDouble("current_location_latitude");
			double longitude = jObj.getDouble("current_location_longitude");
			String driverRating = jObj.getString("rating");
			
			String carNumber = "";
			if(jObj.has("driver_car_no")){
				carNumber = jObj.getString("driver_car_no");
			}
			
			int freeRide = 0;
			if(jObj.has("free_ride")){
				freeRide = jObj.getInt("free_ride");
			}
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName, 
					driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide);
			
			Data.startRidePreviousLatLng = Data.pickupLatLng;
			initializeStartRideVariables();
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					switchPassengerScreen(passengerScreenMode);
				}
			});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	


	public void showAllRideRequestsOnMap(){
		
		try {
			if(userMode == UserMode.DRIVER){
				
				map.clear();
				
			
				if(Data.driverRideRequests.size() > 0){
					
					driverRequestListAdapter.notifyDataSetChanged();
					
					if(driverNewRideRequestRl.getVisibility() == View.GONE && driverRideRequestsList.getVisibility() == View.GONE){
						driverNewRideRequestRl.setVisibility(View.VISIBLE);
						driverRideRequestsList.setVisibility(View.GONE);
					}
					
					LatLng last = map.getCameraPosition().target;
					
					for(int i=0; i<Data.driverRideRequests.size(); i++){
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.title(Data.driverRideRequests.get(i).engagementId);
						markerOptions.snippet("");
						markerOptions.position(Data.driverRideRequests.get(i).latLng);
						markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPassengerMarkerBitmap(HomeActivity.this, assl)));
						
						map.addMarker(markerOptions);
						
					}
					
					driverNewRideRequestText.setText(getResources().getString(R.string.you_have)+" "+Data.driverRideRequests.size()+" "+getResources().getString(R.string.ride_request));
					
					map.animateCamera(CameraUpdateFactory.newLatLng(last), 1000, null);
					
				}
				else{
					driverNewRideRequestRl.setVisibility(View.GONE);
					driverRideRequestsList.setVisibility(View.GONE);
				}
			
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onNewRideRequest() {
		if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getAndShowAllDriverRequests(HomeActivity.this);
				}
			});
		}
	}

	@Override
	public void onCancelRideRequest(final String engagementId, final boolean acceptedByOtherDriver) {
		try {
				if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getAndShowAllDriverRequests(HomeActivity.this);
						}
					});
				}
				else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
								DialogPopup.dismissLoadingDialog();
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								if(acceptedByOtherDriver){
									new DialogPopup().alertPopup(HomeActivity.this, "", "This request has been accepted by other driver");
								}
								else{
									new DialogPopup().alertPopup(HomeActivity.this, "", "User has canceled the request");
								}
							}
						}
					});
				}
				else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_START_RIDE){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								new DialogPopup().alertPopup(HomeActivity.this, "", "User has canceled the request");
							}
						}
					});
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onRideRequestTimeout(final String engagementId) {
		if(userMode == UserMode.DRIVER ){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
						if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
							driverScreenMode = DriverScreenMode.D_INITIAL;
							switchDriverScreen(driverScreenMode);
						}
					}
					else if(driverScreenMode == DriverScreenMode.D_INITIAL){
						getAndShowAllDriverRequests(HomeActivity.this);
					}
				}
			});
		}
	}
	
	
	@Override
	public void onManualDispatchPushReceived() {
		try {
			if(userMode == UserMode.DRIVER ){
				callStateRestoreAPIOnManualPatchPushReceived();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	public void clearRideSPData(){

		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");

		editor.commit();

		Database.getInstance(this).deleteSavedPath();
		Database.getInstance(this).close();
	
	}
	
	public void clearSPData() {

		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");

		editor.putString(Data.SP_D_ENGAGEMENT_ID, "");
		editor.putString(Data.SP_D_CUSTOMER_ID, "");
		editor.putString(Data.SP_D_LATITUDE, "0");
		editor.putString(Data.SP_D_LONGITUDE, "0");
		editor.putString(Data.SP_D_CUSTOMER_NAME, "");
		editor.putString(Data.SP_D_CUSTOMER_IMAGE, "");
		editor.putString(Data.SP_D_CUSTOMER_PHONE, "");
		editor.putString(Data.SP_D_CUSTOMER_RATING, "");

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");

		editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");

		editor.putString(Data.SP_C_SESSION_ID, "");
		editor.putString(Data.SP_C_ENGAGEMENT_ID, "");
		editor.putString(Data.SP_C_DRIVER_ID, "");
		editor.putString(Data.SP_C_LATITUDE, "0");
		editor.putString(Data.SP_C_LONGITUDE, "0");
		editor.putString(Data.SP_C_DRIVER_NAME, "");
		editor.putString(Data.SP_C_DRIVER_IMAGE, "");
		editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, "");
		editor.putString(Data.SP_C_DRIVER_PHONE, "");
		editor.putString(Data.SP_C_DRIVER_RATING, "");
		editor.putString(Data.SP_C_DRIVER_DISTANCE, "0");
		editor.putString(Data.SP_C_DRIVER_DURATION, "");

		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_C_TOTAL_FARE, "0");
		editor.putString(Data.SP_C_WAIT_TIME, "0");
		editor.putString(Data.SP_C_RIDE_TIME, "0");

		editor.commit();

		Database.getInstance(this).deleteSavedPath();
		Database.getInstance(this).close();

	}
	


	@Override
	public void customerEndRideInterrupt(final JSONObject jObj) {
		try{
			if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						displayCouponApplied(jObj);
						lastLocation = null;
						clearSPData();
						map.clear();
						passengerScreenMode = PassengerScreenMode.P_RIDE_END;
						switchPassengerScreen(passengerScreenMode);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onChangeStatePushReceived() {
		try{
			callAndHandleStateRestoreAPI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void logoutUser(final Activity cont){
		try{
			
			new FacebookLoginHelper().logoutFacebook();
			
			SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
			Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			Data.clearDataOnLogout(cont);
			
			PicassoTools.clearCache(Picasso.with(cont));
			
			cont.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(cont);
					builder.setMessage(cont.getResources().getString(R.string.your_login_session_expired)).setTitle(cont.getResources().getString(R.string.alert));
					builder.setCancelable(false);
			        builder.setPositiveButton(cont.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			                    @Override
			                    public void onClick(DialogInterface dialog, int which) {
			                    	try {
			                			dialog.dismiss();
			                			Intent intent = new Intent(cont, SplashNewActivity.class);
			                			intent.putExtra("no_anim", "yes");
			                			cont.startActivity(intent);
			                			cont.finish();
			                			cont.overridePendingTransition(
			                					R.anim.left_in,
			                					R.anim.left_out);
			                		} catch (Exception e) {
			                			Log.i("excption logout",
			                					e.toString());
			                		}
			                    }
			                });
			        
			        AlertDialog alertDialog = builder.create();
			        alertDialog.show();
				}
			});
        
		} catch(Exception e){e.printStackTrace();}
		
	}
	
	
	
	
	public Handler automaticReviewHandler;
	public Runnable automaticReviewRunnable;
	
	public void startAutomaticReviewHandler(){
		try {
			stopAutomaticReviewhandler();
			automaticReviewHandler = new Handler();
			automaticReviewRunnable = new Runnable() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_RIDE_END){
								GCMIntentService.clearNotifications(HomeActivity.this);
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
							}
							else if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_RIDE_END){
								GCMIntentService.clearNotifications(HomeActivity.this);
								passengerScreenMode = PassengerScreenMode.P_INITIAL;
								switchPassengerScreen(passengerScreenMode);
							}
						}
					});
				}
			};
			
			automaticReviewHandler.postDelayed(automaticReviewRunnable, AUTO_RATING_DELAY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopAutomaticReviewhandler(){
		try {
			if(automaticReviewHandler != null && automaticReviewRunnable != null){
				automaticReviewHandler.removeCallbacks(automaticReviewRunnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		automaticReviewHandler = null;
		automaticReviewRunnable = null;
	}
	
	
	
	
	
	
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("HomeActivity started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	
	
	
	public void initializeFusedLocationFetchers() {
		destroyFusedLocationFetchers();
		if (myLocation == null) {
			if (lowPowerLF == null) {
				lowPowerLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 0);
			}
			if (highAccuracyLF == null) {
				highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
			}
		} 
		else {
			if (highAccuracyLF == null) {
				highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
			}
		}
	}
	
	
	public void destroyFusedLocationFetchers(){
		destroyLowPowerFusedLocationFetcher();
		destroyHighAccuracyFusedLocationFetcher();
	}
	
	public void destroyLowPowerFusedLocationFetcher(){
		try{
			if(lowPowerLF != null){
				lowPowerLF.destroy();
				lowPowerLF = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void destroyHighAccuracyFusedLocationFetcher(){
		try{
			if(highAccuracyLF != null){
				highAccuracyLF.destroy();
				highAccuracyLF = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public synchronized void onLocationChanged(Location location, int priority) {
		if(priority == 0){
			if(location.getAccuracy() <= LOW_POWER_ACCURACY_CHECK){
				HomeActivity.myLocation = location;
				zoomToCurrentLocationAtFirstLocationFix(location);
				updatePickupLocation(location);
			}
		}
		else if(priority == 2){
			destroyLowPowerFusedLocationFetcher();
			if(location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK){
				HomeActivity.myLocation = location;
				zoomToCurrentLocationAtFirstLocationFix(location);
				updatePickupLocation(location);
				drawLocationChanged(location);
			}
		}
	}
	
	
	public void zoomToCurrentLocationAtFirstLocationFix(Location location){
		try {
			if(map != null){
				if(!zoomedToMyLocation){
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
					zoomedToMyLocation = true;
					callMapTouchedRefreshDrivers();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 *  Updates PickupLocation on first location fix, when passenger state is P_ASSIGNING_DRIVERS 
	 * 	and saving it in shared preferences.
	 * @param location location changed in listeners
	 */
	public void updatePickupLocation(Location location){
		if(userMode == UserMode.PASSENGER){
			if(passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
				if(Data.pickupLatLng.latitude == 0 && Data.pickupLatLng.longitude == 0){
					Data.pickupLatLng = new LatLng(location.getLatitude(), location.getLongitude());
					SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
					Editor editor = pref.edit();
					editor.putString(Data.SP_C_SESSION_ID, Data.cSessionId);
					editor.putString(Data.SP_TOTAL_DISTANCE, "0");
					editor.putString(Data.SP_LAST_LATITUDE, ""+Data.pickupLatLng.latitude);
		    		editor.putString(Data.SP_LAST_LONGITUDE, ""+Data.pickupLatLng.longitude);
		    		editor.commit();
		    		
		    		if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
						getDistanceTimeAddress = null;
					}
					if(myLocation != null){
						getDistanceTimeAddress = new GetDistanceTimeAddress(Data.pickupLatLng, false);
						getDistanceTimeAddress.execute();
					}
				}
			}
			else if(passengerScreenMode == PassengerScreenMode.P_REQUEST_FINAL){
				if("".equalsIgnoreCase(Data.assignedDriverInfo.durationToReach)){
					if(getDistanceTimeAddress != null){
						getDistanceTimeAddress.cancel(true);
					}
					if(myLocation != null){
						Data.assignedDriverInfo.durationToReach = "no";
						getDistanceTimeAddress = new GetDistanceTimeAddress(Data.pickupLatLng, true);
						getDistanceTimeAddress.execute();
					}
				}
			}
		}
	}
	
	
	
	
	public boolean checkWorkingTime(){
		return true;
//		try {
//			Calendar calendar = Calendar.getInstance();
//			int hour = calendar.get(Calendar.HOUR_OF_DAY);
//			
//			if(hour>=7 && hour<=21){
//				return true;
//			}
//			else{
//				new DialogPopup().alertPopup(HomeActivity.this, "", "We are currently operational between 7 am - 9 pm. " +
//						"We are increasing our operations hour to 24 hours soon");
//				return false;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return true;
//		}
	}
	
//	SELECT * FROM `tb_updated_user_location` WHERE `user_id`=231 and `location_updated_timestamp` >= '2014-10-18' and `location_updated_timestamp` < '2014-10-20'
	
	
	//Customer's timer
    Timer timerRequestRide;
	TimerTask timerTaskRequestRide;
	long requestRideLifeTime;
	long serverRequestStartTime = 0;
	long serverRequestEndTime = 0;
	long executionTime = -1;
	long requestPeriod = 20000;
	
	public void startTimerRequestRide(){
		cancelTimerRequestRide();
		try {
			
			requestRideLifeTime = ((2 * 60 * 1000) + (1 * 60 * 1000));
			serverRequestStartTime = 0;
			serverRequestEndTime = 0;
			executionTime = -1;
			requestPeriod = 20000;
			
			timerRequestRide = new Timer();
			timerTaskRequestRide = new TimerTask() {
				@Override
				public void run() {
					
					long startTime = System.currentTimeMillis();
					
					try {
						Log.e("request_ride execution", "="+(serverRequestEndTime - executionTime));
						if(executionTime >= serverRequestEndTime){
							cancelTimerRequestRide();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
										noDriverAvailablePopup(HomeActivity.this, false, "");
										HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
										switchPassengerScreen(passengerScreenMode);
									}
								}
							});
						}
						else{
							if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if("".equalsIgnoreCase(Data.cSessionId)){
											initialCancelRideBtn.setBackgroundResource(R.drawable.red_btn_pressed);
										}
										else{
											initialCancelRideBtn.setBackgroundResource(R.drawable.red_btn_selector);
										}
									}
								});
								
								ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
								nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
								nameValuePairs.add(new BasicNameValuePair("latitude", ""+Data.pickupLatLng.latitude));
								nameValuePairs.add(new BasicNameValuePair("longitude", "" + Data.pickupLatLng.longitude));
								if(myLocation != null){
									nameValuePairs.add(new BasicNameValuePair("current_latitude", ""+myLocation.getLatitude()));
									nameValuePairs.add(new BasicNameValuePair("current_longitude", "" + myLocation.getLongitude()));
								}
								else{
									nameValuePairs.add(new BasicNameValuePair("current_latitude", ""+Data.pickupLatLng.latitude));
									nameValuePairs.add(new BasicNameValuePair("current_longitude", "" + Data.pickupLatLng.longitude));
								}
								
								if("".equalsIgnoreCase(Data.cSessionId)){
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "0"));
									if(myLocation != null && myLocation.hasAccuracy()){
										nameValuePairs.add(new BasicNameValuePair("location_accuracy", ""+myLocation.getAccuracy()));
									}
								}
								else{
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "1"));
								}
								
								Log.i("nameValuePairs of request_ride", "="+nameValuePairs);
								String response = new HttpRequester().getJSONFromUrlParams(Data.SERVER_URL+"/request_ride", nameValuePairs);
								
								Log.e("response of request_ride", "="+response);
								
								if(response.contains(HttpRequester.SERVER_TIMEOUT)){
									Log.e("timeout","=");
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
//											new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
										}
									});
								}
								else{
									try{
										JSONObject jObj = new JSONObject(response);
										if(!jObj.isNull("error")){
											final String errorMessage = jObj.getString("error");
											final int flag = jObj.getInt("flag");
											if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
												cancelTimerRequestRide();
												HomeActivity.logoutUser(activity);
											}
											else{
												if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
													cancelTimerRequestRide();
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
																new DialogPopup().alertPopup(HomeActivity.this, "", errorMessage);
																HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
																switchPassengerScreen(passengerScreenMode);
															}
														}
													});
												}
											}
										}
										else{
											int flag = jObj.getInt("flag");
											
											if(ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
												final String log = jObj.getString("log");
												Log.e("ASSIGNING_DRIVERS log", "="+log);
												final String start_time = jObj.getString("start_time");
												if(executionTime == -1){
													serverRequestStartTime = new DateOperations().getMilliseconds(start_time);
													serverRequestEndTime = serverRequestStartTime + requestRideLifeTime;
													long stopTime = System.currentTimeMillis();
												    long elapsedTime = stopTime - startTime;
													executionTime = serverRequestStartTime + elapsedTime;
												}
												
												Data.cSessionId = jObj.getString("session_id");
											}
											else if(ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag){
												if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
													cancelTimerRequestRide();
													fetchAcceptedDriverInfoAndChangeState(jObj);
												}
											}
											else if(ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag){
												if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
													cancelTimerRequestRide();
													fetchAcceptedDriverStartRideInfoAndChangeState(jObj);
												}
											}
											else if(ApiResponseFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag){
												final String log = jObj.getString("log");
												Log.e("NO_DRIVERS_AVAILABLE log", "="+log);
												cancelTimerRequestRide();
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
															noDriverAvailablePopup(HomeActivity.this, false, log);
															HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
															switchPassengerScreen(passengerScreenMode);
														}
													}
												});
											}
											
										}
									} catch(Exception e){
										e.printStackTrace();
									}
								}
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if("".equalsIgnoreCase(Data.cSessionId)){
											initialCancelRideBtn.setBackgroundResource(R.drawable.red_btn_pressed);
										}
										else{
											initialCancelRideBtn.setBackgroundResource(R.drawable.red_btn_selector);
										}
									}
								});
								
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					long stopTime = System.currentTimeMillis();
				    long elapsedTime = stopTime - startTime;
				    
				    if(executionTime != -1){
				    	if(elapsedTime >= requestPeriod){
				    		executionTime = executionTime + elapsedTime;
				    	}
				    	else{
				    		executionTime = executionTime + requestPeriod;
				    	}
				    }
				    Log.i("request_ride execution", "="+(serverRequestEndTime - executionTime));
					
				}
			};
			
			timerRequestRide.scheduleAtFixedRate(timerTaskRequestRide, 0, requestPeriod);
			textViewAssigningInProgress.setText("Assigning driver could take up to 3 minutes...");
			FlurryEventLogger.requestRidePressed(Data.userData.accessToken, Data.pickupLatLng);
			startAssigningDriversAnimation();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelTimerRequestRide(){
		stopAssigningDriversAnimation();
		try{
			if(timerTaskRequestRide != null){
				timerTaskRequestRide.cancel();
				timerTaskRequestRide = null;
			}
			if(timerRequestRide != null){
				timerRequestRide.cancel();
				timerRequestRide.purge();
				timerRequestRide = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void startAssigningDriversAnimation(){
		try{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Animation myFadeInAnimation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.tween);
					requestRideBtn.startAnimation(myFadeInAnimation);
				}
			});
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void stopAssigningDriversAnimation(){
		try{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					requestRideBtn.clearAnimation();
				}
			});
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	


	@Override
	public void onNoDriversAvailablePushRecieved(final String logMessage) {
		cancelTimerRequestRide();
		if(HomeActivity.passengerScreenMode == PassengerScreenMode.P_ASSIGNING){
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					noDriverAvailablePopup(HomeActivity.this, false, logMessage);
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
					switchPassengerScreen(passengerScreenMode);
				}
			});
		}
	}

	
	
	
	
	
	

	
	public void callAndHandleStateRestoreAPI() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Data.userData != null){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
						}
					});
					int currentUserStatus = 0;
					if(UserMode.DRIVER == userMode){
						currentUserStatus = 1;
					}
					else if(UserMode.PASSENGER == userMode){
						currentUserStatus = 2;
					}
					if(currentUserStatus != 0){
						String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
						if(resp.contains(HttpRequester.SERVER_TIMEOUT)){
							String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(resp1.contains(HttpRequester.SERVER_TIMEOUT)){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new DialogPopup().alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
									}
								});
							}
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
							startUIAfterGettingUserStatus();
						}
					});
				}
			}
		}).start();
	}
	
	
	public String manualPatchPushStateRestoreResponse = "";
	public void callStateRestoreAPIOnManualPatchPushReceived() {
		manualPatchPushStateRestoreResponse = "";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(Data.userData != null){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
							}
						});
						int currentUserStatus = 0;
						if(UserMode.DRIVER == userMode){
							currentUserStatus = 1;
						}
						else if(UserMode.PASSENGER == userMode){
							currentUserStatus = 2;
						}
						if(currentUserStatus != 0){
							manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(manualPatchPushStateRestoreResponse.contains(HttpRequester.SERVER_TIMEOUT)){
								manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
								if(manualPatchPushStateRestoreResponse.contains(HttpRequester.SERVER_TIMEOUT)){
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											new DialogPopup().alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
										}
									});
								}
							}
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogPopup.dismissLoadingDialog();
								startUIAfterGettingUserStatus();
								
								if(!manualPatchPushStateRestoreResponse.contains(HttpRequester.SERVER_TIMEOUT)){
									showManualPatchPushReceivedDialog();
								}
								
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	public void showManualPatchPushReceivedDialog(){
		try {
			if(UserMode.DRIVER == userMode && DriverScreenMode.D_START_RIDE == driverScreenMode){
				if(Data.assignedCustomerInfo != null){
					String manualPatchPushReceived = Database2.getInstance(HomeActivity.this).getDriverManualPatchPushReceived();
					if(Database2.YES.equalsIgnoreCase(manualPatchPushReceived)){
						new DialogPopup().alertPopupWithListener(HomeActivity.this, "", "A pickup has been assigned to you. Please pick the customer.", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Database2.getInstance(HomeActivity.this).updateDriverManualPatchPushReceived(Database2.NO);
								Database2.getInstance(HomeActivity.this).close();
								manualPatchPushAckAPI(HomeActivity.this);
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Database2.getInstance(HomeActivity.this).close();
	}
	
	
	/**
	 * ASync for acknowledging the server about manual patch push received
	 */
	public void manualPatchPushAckAPI(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.assignedCustomerInfo.userId);

			Log.i("server call", "=" + Data.SERVER_URL + "/acknowledge_manual_engagement");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", Data.dEngagementId);
			Log.i("customer_id", Data.assignedCustomerInfo.userId);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/acknowledge_manual_engagement", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							manualPatchPushAckAPI(activity);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response acknowledge_manual_engagement", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									int flag = jObj.getInt("flag");	
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
								}
								else{
									if(jObj.has("flag")){
										int flag = jObj.getInt("flag");	
										if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
											
										}
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					});
		}

	}
	
	
	
	
	
	
	
	class CheckForGPSAccuracyTimer{
    	
    	public Timer timer;
    	public TimerTask timerTask;
    	
    	public long startTime, lifeTime, endTime, period, executionTime;
    	public boolean isRunning = false;
    	
    	public CheckForGPSAccuracyTimer(Context context, long delay, long period, long startTime, long lifeTime){
    		Log.i("CheckForGPSAccuracyTimer before start myLocation = ", "="+myLocation);
    		isRunning = false;
    		if(myLocation != null){
	    		if(myLocation.hasAccuracy()){
	    			float accuracy = myLocation.getAccuracy();
	    			if(accuracy > HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND){
	    				displayLessAccurateToast(context);
	    			}
	    			else if(accuracy <= HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND 
	    					&& accuracy > HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND){
	    				startTimer(context, delay, period, startTime, lifeTime);
	    				HomeActivity.this.switchRequestRideUI();
	    			}
	    			else if(accuracy <= HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND){
	    				HomeActivity.this.switchRequestRideUI();
	    				HomeActivity.this.startTimerRequestRide();
	    			}
	    			else{
	    				displayLessAccurateToast(context);
	    			}
	    		}
	    		else{
	    			displayLessAccurateToast(context);
	    		}
    		}
    		else{
    			displayLessAccurateToast(context);
    		}
    	}
    	
    	
    	
    	public void displayLessAccurateToast(Context context){
    		Toast.makeText(context, "Please wait for sometime. We need to get your more accurate location.", Toast.LENGTH_LONG).show();
    	}
    	
    	public void initRequestRideUi(){
    		stopTimer();
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
    				HomeActivity.this.startTimerRequestRide();
				}
			});
    	}
    	
    	public void stopRequest(Context context){
    		displayLessAccurateToast(context);
			stopTimer();
			HomeActivity.this.customerUIBackToInitialAfterCancel();
    	}
    	
    	public void startTimer(final Context context, long delay, long period, long startTime, long lifeTime){
    		stopTimer();
    		isRunning = true;
    		
    		this.startTime = startTime;
    		this.lifeTime = lifeTime;
    		this.endTime = startTime + lifeTime;
    		this.period = period;
    		this.executionTime = -1;
    		
    		timer = new Timer();
    		timerTask = new TimerTask() {
    			@Override
    			public void run() {
    				HomeActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								long start = System.currentTimeMillis();
								if(executionTime == -1){
									executionTime = CheckForGPSAccuracyTimer.this.startTime;
								}
								
								if(executionTime >= CheckForGPSAccuracyTimer.this.endTime){
									//Timer finished
									if(myLocation != null){
										if(myLocation.hasAccuracy()){
											float accuracy = myLocation.getAccuracy();
											if(accuracy > HomeActivity.WAIT_FOR_ACCURACY_UPPER_BOUND){
												stopRequest(context);
							    			}
											else{
												initRequestRideUi();
											}
										}
										else{
											stopRequest(context);
										}
									}
									else{
										stopRequest(context);
									}
								}
								else{
									//Check for location accuracy
									Log.i("CheckForGPSAccuracyTimer myLocation = ", "="+myLocation);
									if(myLocation != null){
										if(myLocation.hasAccuracy()){
											float accuracy = myLocation.getAccuracy();
											if(accuracy <= HomeActivity.WAIT_FOR_ACCURACY_LOWER_BOUND){
												initRequestRideUi();
											}
										}
									}
								}
								long stop = System.currentTimeMillis();
								long elapsedTime = stop - start;
								if(executionTime != -1){
									if(elapsedTime >= CheckForGPSAccuracyTimer.this.period){
										executionTime = executionTime + elapsedTime;
									}
									else{
										executionTime = executionTime + CheckForGPSAccuracyTimer.this.period;
									}
								}
								Log.i("WaitForGPSAccuracyTimerTask execution", "="+(CheckForGPSAccuracyTimer.this.endTime - executionTime));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
    			}
    		};
    		timer.scheduleAtFixedRate(timerTask, delay, period);
    		isRunning = true;
    		//textViewAssigningInProgress
    		textViewAssigningInProgress.setText("Assigning driver could take up to 4 minutes...");
    	}
    	
    	public void stopTimer(){
    		try{
    			isRunning = false;
    			Log.e("WaitForGPSAccuracyTimerTask","stopTimer");
    			startTime = 0;
    			lifeTime = 0;
    			if(timerTask != null){
    				timerTask.cancel();
    				timerTask = null;
    			}
    			if(timer != null){
    				timer.cancel();
    				timer.purge();
    				timer = null;
    			}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	
    	
    }
    
	
	
}