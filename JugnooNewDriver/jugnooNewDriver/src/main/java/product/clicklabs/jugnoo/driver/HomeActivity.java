package product.clicklabs.jugnoo.driver;

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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
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
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.AppMode;
import product.clicklabs.jugnoo.driver.datastructure.AutoCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EndRideData;
import product.clicklabs.jugnoo.driver.datastructure.FatafatCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatDeliveryInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatOrderInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.datastructure.LatLngPair;
import product.clicklabs.jugnoo.driver.datastructure.MealRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.StationData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapStateListener;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.PausableChronometer;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.driver.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

@SuppressLint("DefaultLocale")
public class HomeActivity extends FragmentActivity implements AppInterruptHandler, LocationUpdate, GPSLocationUpdate {

	
	
	DrawerLayout drawerLayout;																		// views declaration
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	
	
	
	ImageView profileImg;
	TextView userName;
	
	RelativeLayout relativeLayoutAutosOn, relativeLayoutMealsOn, relativeLayoutFatafatOn;
	ImageView imageViewAutosOnToggle, imageViewMealsOnToggle, imageViewFatafatOnToggle;
	
	RelativeLayout inviteFriendRl;
	TextView inviteFriendText;
	
	RelativeLayout bookingsRl;
	TextView bookingsText;
	
	RelativeLayout fareDetailsRl;
	TextView fareDetailsText;
	
	RelativeLayout helpRl;
	TextView helpText;
	
	RelativeLayout languagePrefrencesRl;
	TextView languagePrefrencesText;
	
	RelativeLayout logoutRl;
	TextView logoutText;
	
	
	
	
	
	
	
	
	
	
	
	
	//Top RL
	RelativeLayout topRl;
	Button menuBtn, backBtn;
	TextView title;
	ImageView jugnooLogo;
	Button checkServerBtn, toggleDebugModeBtn;
	
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Driver main layout 
	RelativeLayout driverMainLayout;
	
	
	//Driver initial layout
	RelativeLayout driverInitialLayout;
	TextView textViewDriverInfo;
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
	TextView textViewBeforeAcceptRequestInfo;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn;
	
	
	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;
	
	TextView driverPassengerName, textViewCustomerPickupAddress, textViewAfterAcceptRequestInfo, textViewAfterAcceptAmount;
	TextView driverPassengerRatingValue;
	RelativeLayout driverPassengerCallRl;
	TextView driverPassengerCallText;
	TextView driverScheduledRideText;
	ImageView driverFreeRideIcon;
	
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
    RelativeLayout inrideFareInfoRl;
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
	reviewFareText, reviewFareValue;
	
	LinearLayout endRideInfoRl;
	TextView jugnooRideOverText, takeFareText;
	
	RelativeLayout relativeLayoutCoupon;
	TextView textViewCouponTitle, textViewCouponSubTitle, textViewCouponPayTakeText, textViewCouponDiscountedFare;
	
	RelativeLayout relativeLayoutFatafatCustomerAmount;
	LinearLayout linearLayoutFatafatBill;
	TextView textViewFatafatBillAmountValue, textViewFatafatBillDiscountValue, 
		textViewFatafatBillFinalAmountValue, textViewFatafatBillJugnooCashValue, textViewFatafatBillToPay;
	
	Button reviewSubmitBtn;
	TextView reviewMinFareText, reviewMinFareValue, reviewFareAfterText, reviewFareAfterValue;
	Button reviewFareInfoBtn;
    RelativeLayout reviewFareInfoInnerRl;
	
	
	
	
	
																								// data variables declaration
	
	
	
	
	
	
	
	
	Location lastLocation;
	long lastLocationTime;
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
	
	static double totalDistance = -1, totalFare = 0;
	public static ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	
	
	static long previousWaitTime = 0, previousRideTime = 0;
	
	static String waitTime = "", rideTime = "";
	
	
	static Location myLocation;
	public Location lastGPSLocation, lastFusedLocation;
	public boolean distanceUpdateFromService = false;
	
	
	

	static UserMode userMode;
	static DriverScreenMode driverScreenMode;
	
	
	
	
	
	
	
	
	Marker rideStartPositionMarker, customerLocationMarker = null;
	Polyline pathToCustomerPolyline = null;
	
	static AppInterruptHandler appInterruptHandler;
	
	static Activity activity;
	
	boolean loggedOut = false, 
			zoomedToMyLocation = false, 
			mapTouchedOnce = false;
	boolean dontCallRefreshDriver = false;
	
	
	AlertDialog gpsDialogAlert;

	LocationFetcher highAccuracyLF;
	
	
	
	
	
	
	
	//TODO check final variables
	public static AppMode appMode;
	
	public static final int MAP_PATH_COLOR = Color.TRANSPARENT;
	public static final int D_TO_C_MAP_PATH_COLOR = Color.RED;
	public static final int DRIVER_TO_STATION_MAP_PATH_COLOR = Color.BLUE;
	
	public static final long DRIVER_START_RIDE_CHECK_METERS = 600; //in meters
	
	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000; //in milliseconds

	
	public static final float HIGH_ACCURACY_ACCURACY_CHECK = 1000;  //in meters


	public static final long MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT = 10 * 60000; //in milliseconds
	
//	public static final double MAX_WAIT_TIME_ALLOWED_DISTANCE = 200; //in meters


	public ASSL assl;
	
//	public GPSForegroundLocationFetcher gpsForegroundLocationFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        String languageToLoad = "hi_IN";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
            getBaseContext().getResources().getDisplayMetrics());

		setContentView(R.layout.activity_home);


		
		initializeGPSForegroundLocationFetcher();
		
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
		
		
		Log.e("home", "oncreate");
		
		
		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		
		
		
		
		profileImg = (ImageView) findViewById(R.id.profileImg);
		userName = (TextView) findViewById(R.id.userName); userName.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		
		relativeLayoutAutosOn = (RelativeLayout) findViewById(R.id.relativeLayoutAutosOn);
		((TextView) findViewById(R.id.textViewAutosOn)).setTypeface(Data.latoRegular(getApplicationContext()));
		imageViewAutosOnToggle = (ImageView) findViewById(R.id.imageViewAutosOnToggle);
		
		relativeLayoutMealsOn = (RelativeLayout) findViewById(R.id.relativeLayoutMealsOn);
		((TextView) findViewById(R.id.textViewMealsOn)).setTypeface(Data.latoRegular(getApplicationContext()));
		imageViewMealsOnToggle = (ImageView) findViewById(R.id.imageViewMealsOnToggle);
		
		relativeLayoutFatafatOn = (RelativeLayout) findViewById(R.id.relativeLayoutFatafatOn);
		((TextView) findViewById(R.id.textViewFatafatOn)).setTypeface(Data.latoRegular(getApplicationContext()));
		imageViewFatafatOnToggle = (ImageView) findViewById(R.id.imageViewFatafatOnToggle);
		
		
		
		
		inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
		inviteFriendText = (TextView) findViewById(R.id.inviteFriendText); inviteFriendText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
		bookingsText = (TextView) findViewById(R.id.bookingsText); bookingsText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		fareDetailsRl = (RelativeLayout) findViewById(R.id.fareDetailsRl);
		fareDetailsText = (TextView) findViewById(R.id.fareDetailsText); fareDetailsText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		helpRl = (RelativeLayout) findViewById(R.id.helpRl);
		helpText = (TextView) findViewById(R.id.helpText); helpText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		languagePrefrencesRl = (RelativeLayout) findViewById(R.id.languagePrefrencesRl);
		languagePrefrencesText = (TextView) findViewById(R.id.languagePrefrencesText); languagePrefrencesText.setTypeface(Data.latoRegular(getApplicationContext()));
		
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
		
		
		
		menuBtn.setVisibility(View.VISIBLE);
		jugnooLogo.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.GONE);
		title.setVisibility(View.GONE);
		
		
		
		
		
		
		//Map Layout
		mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		
		
		
		
		
		

		
		
		
		
		
		
		
		
		// Driver main layout 
		driverMainLayout = (RelativeLayout) findViewById(R.id.driverMainLayout);
		
		
		
		//Driver initial layout
		driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
		textViewDriverInfo = (TextView) findViewById(R.id.textViewDriverInfo); textViewDriverInfo.setTypeface(Data.latoRegular(getApplicationContext()));
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
		textViewBeforeAcceptRequestInfo = (TextView) findViewById(R.id.textViewBeforeAcceptRequestInfo); textViewBeforeAcceptRequestInfo.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
		driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn); driverAcceptRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn); driverCancelRequestBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
		
		
		
		// Driver engaged layout
		driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
		

		driverPassengerName = (TextView) findViewById(R.id.driverPassengerName); driverPassengerName.setTypeface(Data.latoRegular(getApplicationContext()));
        textViewCustomerPickupAddress = (TextView) findViewById(R.id.textViewCustomerPickupAddress); textViewCustomerPickupAddress.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewAfterAcceptRequestInfo = (TextView) findViewById(R.id.textViewAfterAcceptRequestInfo); textViewAfterAcceptRequestInfo.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewAfterAcceptAmount = (TextView) findViewById(R.id.textViewAfterAcceptAmount); textViewAfterAcceptAmount.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue); driverPassengerRatingValue.setTypeface(Data.latoRegular(getApplicationContext()));
		driverPassengerCallRl = (RelativeLayout) findViewById(R.id.driverPassengerCallRl);
		driverPassengerCallText = (TextView) findViewById(R.id.driverPassengerCallText); driverPassengerCallText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverScheduledRideText = (TextView) findViewById(R.id.driverScheduledRideText); driverScheduledRideText.setTypeface(Data.latoRegular(getApplicationContext()));
		driverFreeRideIcon = (ImageView) findViewById(R.id.driverFreeRideIcon);
		
		driverPassengerRatingValue.setVisibility(View.GONE);
		
		//Start ride layout
		driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
		driverStartRideMyLocationBtn = (Button) findViewById(R.id.driverStartRideMyLocationBtn);
		driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn); driverStartRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn); driverCancelRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));

		
		
		//In ride layout
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

        inrideFareInfoRl = (RelativeLayout) findViewById(R.id.inrideFareInfoRl);
		inrideMinFareText = (TextView) findViewById(R.id.inrideMinFareText); inrideMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		inrideMinFareValue = (TextView) findViewById(R.id.inrideMinFareValue); inrideMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareAfterText = (TextView) findViewById(R.id.inrideFareAfterText); inrideFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareAfterValue = (TextView) findViewById(R.id.inrideFareAfterValue); inrideFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
		inrideFareInfoBtn = (Button) findViewById(R.id.inrideFareInfoBtn);
		
		driverWaitRl.setVisibility(View.GONE);
		
		driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn); driverEndRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		waitStart = 2;

		
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
		
		endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
		jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText); 
		jugnooRideOverText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		takeFareText = (TextView) findViewById(R.id.takeFareText); 
		takeFareText.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn); reviewSubmitBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		relativeLayoutCoupon = (RelativeLayout) findViewById(R.id.relativeLayoutCoupon);
		textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle); textViewCouponTitle.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		textViewCouponSubTitle = (TextView) findViewById(R.id.textViewCouponSubTitle); textViewCouponSubTitle.setTypeface(Data.museoSlab(getApplicationContext()));
		textViewCouponPayTakeText = (TextView) findViewById(R.id.textViewCouponPayTakeText); textViewCouponPayTakeText.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		textViewCouponDiscountedFare = (TextView) findViewById(R.id.textViewCouponDiscountedFare); textViewCouponDiscountedFare.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
		
		relativeLayoutFatafatCustomerAmount = (RelativeLayout) findViewById(R.id.relativeLayoutFatafatCustomerAmount);
		linearLayoutFatafatBill = (LinearLayout) findViewById(R.id.linearLayoutFatafatBill);
		textViewFatafatBillAmountValue = (TextView) findViewById(R.id.textViewFatafatBillAmountValue); textViewFatafatBillAmountValue.setTypeface(Data.latoRegular(this));
		textViewFatafatBillDiscountValue = (TextView) findViewById(R.id.textViewFatafatBillDiscountValue); textViewFatafatBillDiscountValue.setTypeface(Data.latoRegular(this));
		textViewFatafatBillFinalAmountValue = (TextView) findViewById(R.id.textViewFatafatBillFinalAmountValue); textViewFatafatBillFinalAmountValue.setTypeface(Data.latoRegular(this));
		textViewFatafatBillJugnooCashValue = (TextView) findViewById(R.id.textViewFatafatBillJugnooCashValue); textViewFatafatBillJugnooCashValue.setTypeface(Data.latoRegular(this));
		textViewFatafatBillToPay = (TextView) findViewById(R.id.textViewFatafatBillToPay); textViewFatafatBillToPay.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		
		((TextView) findViewById(R.id.textViewFatafatBillAmount)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewFatafatBillDiscount)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewFatafatBillFinalAmount)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewFatafatBillJugnooCash)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewFatafatBillTake)).setTypeface(Data.latoRegular(this));
		
		
		reviewMinFareText = (TextView) findViewById(R.id.reviewMinFareText); reviewMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		reviewMinFareValue = (TextView) findViewById(R.id.reviewMinFareValue); reviewMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareAfterText = (TextView) findViewById(R.id.reviewFareAfterText); reviewFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareAfterValue = (TextView) findViewById(R.id.reviewFareAfterValue); reviewFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
		reviewFareInfoBtn = (Button) findViewById(R.id.reviewFareInfoBtn);
        reviewFareInfoInnerRl = (RelativeLayout) findViewById(R.id.reviewFareInfoInnerRl);
		

		
		
				 
		
		
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Top bar events
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// menu events
		imageViewAutosOnToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
					if(Data.userData.autosAvailable == 1){
						changeJugnooON(BusinessType.AUTOS, 0);
					}
					else{
						changeJugnooON(BusinessType.AUTOS, 1);
					}
				}
			}
		});

        imageViewFatafatOnToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
                    if(Data.userData.fatafatAvailable == 1){
                        changeJugnooON(BusinessType.FATAFAT, 0);
                    }
                    else{
                        changeJugnooON(BusinessType.FATAFAT, 1);
                    }
                }
            }
        });

        imageViewMealsOnToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL){
                    if(Data.userData.mealsAvailable == 1){
                        changeJugnooON(BusinessType.MEALS, 0);
                    }
                    else{
                        changeJugnooON(BusinessType.MEALS, 1);
                    }
                }
            }
        });

		
		
		inviteFriendRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, ShareActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.shareScreenOpened(Data.userData.accessToken);
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
		
		
		
		languagePrefrencesRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, LanguagePrefrencesActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		bookingsRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, DriverHistoryActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.rideScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		
		
		
		
		logoutRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(((userMode == UserMode.DRIVER) && (driverScreenMode == DriverScreenMode.D_INITIAL))){
					logoutPopup(HomeActivity.this);
					FlurryEventLogger.logoutPressed(Data.userData.accessToken);
				}
				else{
					DialogPopup.alertPopup(activity, "", "Ride in progress. You can logout only after the ride ends.");
					FlurryEventLogger.logoutPressedBetweenRide(Data.userData.accessToken);
				}
			}
		});
		
		
		menuLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
				if(getBatteryPercentage() >= 20){
					GCMIntentService.clearNotifications(HomeActivity.this);
					GCMIntentService.stopRing();
					driverAcceptRideAsync(HomeActivity.this);
				}
				else{
					DialogPopup.alertPopup(HomeActivity.this, "", "Battery Level must be greater than 20% to accept the ride. Plugin to a power source to continue.");
				}
			}
		});
		
		
		driverCancelRequestBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				GCMIntentService.stopRing();
				driverRejectRequestAsync(HomeActivity.this);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// driver start ride layout events
		driverPassengerCallRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String callPhoneNumber = "";
				
				if(Data.assignedCustomerInfo != null){
					if(BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType){
						if(DriverScreenMode.D_START_RIDE == driverScreenMode){
							callPhoneNumber = Data.assignedCustomerInfo.phoneNumber;
						}
						else if(DriverScreenMode.D_IN_RIDE == driverScreenMode){
							if(((FatafatOrderInfo)Data.assignedCustomerInfo).customerInfo != null){
								callPhoneNumber = ((FatafatOrderInfo)Data.assignedCustomerInfo).customerInfo.phoneNo;
							}
						}
					}
					else{
						callPhoneNumber = Data.assignedCustomerInfo.phoneNumber;
					}
				}
				
				if(!"".equalsIgnoreCase(callPhoneNumber)){
					Utils.openCallIntent(HomeActivity.this, callPhoneNumber);
				}
				else{
					Toast.makeText(HomeActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		driverStartRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getBatteryPercentage() >= 10){
					startRidePopup(HomeActivity.this);
				}
				else{
					DialogPopup.alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to start the ride. Plugin to a power source to continue.");
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
				sendToFareDetails();
			}
		});
		
		driverEndRideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Data.assignedCustomerInfo != null){
					if(BusinessType.AUTOS == Data.assignedCustomerInfo.businessType || BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType){
						endRidePopup(HomeActivity.this, Data.assignedCustomerInfo.businessType);
					}
					else{
						//Meals case of end ride
						endRidePopup(HomeActivity.this, Data.assignedCustomerInfo.businessType);
					}
				}
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
				MeteringService.clearNotifications(HomeActivity.this);
				driverScreenMode = DriverScreenMode.D_INITIAL;
				switchDriverScreen(driverScreenMode);
			}
		});
		
		reviewFareInfoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendToFareDetails();
			}
		});
		
		
		
		
		
		
		lastLocation = null;
		lastLocationTime = System.currentTimeMillis();
		
																	// map object initialized
		if(map != null){
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setTiltGesturesEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			//30.75, 76.78
			
			if(0 == Data.latitude && 0 == Data.longitude){
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
			}
			else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
			}
			
			
			
			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					Log.e("arg0", "="+arg0);
				}
			});
			
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
					else if(arg0.getTitle().equalsIgnoreCase("station_marker")){
						CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
						map.setInfoWindowAdapter(customIW);
						return false;
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
                }

                @Override
                public void onMapSettled() {
                    // Map settled
                }
            };

			
			driverInitialMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverRequestAcceptMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverStartRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			driverEndRideMyLocationBtn.setOnClickListener(mapMyLocationClick);
			
		}
		
		
		
		try {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			Log.i("userMode", "="+userMode);
			Log.i("driverScreenMode", "="+driverScreenMode);
			
			if(userMode == null){
				userMode = UserMode.DRIVER;
			}
			
			if(driverScreenMode == null){
				driverScreenMode = DriverScreenMode.D_INITIAL;
			}
			
			
			switchUserScreen(userMode);

            switchDriverScreen(driverScreenMode);
		



			changeDriverOptionsUI();

            changeJugnooONUIAndInitService();

			
			Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Data.SERVER_URL);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showManualPatchPushReceivedDialog();
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
	
	
	
	
	
	public void changeJugnooON(BusinessType businessType, int mode){
		if(mode == 1){
			if(myLocation != null){
                switchJugnooOnThroughServer(businessType, 1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
			}
			else{
				Toast.makeText(HomeActivity.this, "Waiting for location...", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			switchJugnooOnThroughServer(businessType, 0, new LatLng(0, 0));
		}
	}
	
	
	
	
	public void switchJugnooOnThroughServer(final BusinessType businessType, final int jugnooOnFlag, final LatLng latLng){
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

                    nameValuePairs.add(new BasicNameValuePair("business_id", ""+businessType.getOrdinal()));

					Log.e("nameValuePairs in sending loc on jugnoo toggle","="+nameValuePairs);
					
					HttpRequester simpleJSONParser = new HttpRequester();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL+"/change_availability", nameValuePairs);
					
					Log.e("result ","="+result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
					
					JSONObject jObj = new JSONObject(result);
					
					if(jObj.has("flag")){
						int flag = jObj.getInt("flag");
						if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                            if(BusinessType.AUTOS == businessType){
                                Data.userData.autosAvailable = jugnooOnFlag;
                            }
                            else if(BusinessType.FATAFAT == businessType){
                                Data.userData.fatafatAvailable = jugnooOnFlag;
                            }
                            else if(BusinessType.MEALS == businessType){
                                Data.userData.mealsAvailable = jugnooOnFlag;
                            }
                            changeJugnooONUIAndInitService();
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
				DialogPopup.alertPopup(HomeActivity.this, "", message);
			}
		});
	}
	

	
	public void changeDriverOptionsUI(){
		if(Data.userData != null){
			if(1 == Data.userData.autosEnabled){
				relativeLayoutAutosOn.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutAutosOn.setVisibility(View.GONE);
                Data.userData.autosAvailable = 0;
			}
			
			if(1 == Data.userData.mealsEnabled){
				relativeLayoutMealsOn.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutMealsOn.setVisibility(View.GONE);
                Data.userData.mealsAvailable = 0;
			}
			
			if(1 == Data.userData.fatafatEnabled){
				relativeLayoutFatafatOn.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutFatafatOn.setVisibility(View.GONE);
                Data.userData.fatafatAvailable = 0;
			}
		}
		
		logoutRl.setVisibility(View.VISIBLE);
	}
	
	
	
	public void changeJugnooONUIAndInitService(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogPopup.dismissLoadingDialog();
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(Data.userData != null){
                        if(1 == Data.userData.autosAvailable){
                            imageViewAutosOnToggle.setImageResource(R.drawable.on);
                        }
                        else{
                            imageViewAutosOnToggle.setImageResource(R.drawable.off);
                        }

                        if(1 == Data.userData.mealsAvailable){
                            imageViewMealsOnToggle.setImageResource(R.drawable.on);
                        }
                        else{
                            imageViewMealsOnToggle.setImageResource(R.drawable.off);
                        }

                        if(1 == Data.userData.fatafatAvailable){
                            imageViewFatafatOnToggle.setImageResource(R.drawable.on);
                        }
                        else{
                            imageViewFatafatOnToggle.setImageResource(R.drawable.off);
                        }

                        if(0 == Data.userData.autosAvailable && 0 == Data.userData.mealsAvailable && 0 == Data.userData.fatafatAvailable){
                            if(isDriverStateFree()){
                                jugnooOffLayout.setVisibility(View.VISIBLE);

                                new DriverServiceOperations().stopAndScheduleDriverService(HomeActivity.this);

                                GCMIntentService.clearNotifications(HomeActivity.this);
                                GCMIntentService.stopRing();

                                if(map != null){
                                    map.clear();
                                }
                                dismissStationDataPopup();
                                cancelStationPathUpdateTimer();
                            }

                        }
                        else{
                            if(isDriverStateFree()) {
                                jugnooOffLayout.setVisibility(View.GONE);

                                new DriverServiceOperations().startDriverService(HomeActivity.this);
                                initializeStationDataProcedure();
                            }


                        }

                        updateReceiveRequestsFlag();

                        updateDriverRequestsAccAvailaviblity();

                        if(Data.driverRideRequests.size() == 0){
                            GCMIntentService.clearNotifications(HomeActivity.this);
                            GCMIntentService.stopRing();
                        }

                        showAllRideRequestsOnMap();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

	}
	
	

    public void updateDriverRequestsAccAvailaviblity(){
        try{
            if(Data.userData != null) {
                ArrayList<DriverRideRequest> tempDriverRideRequests = new ArrayList<DriverRideRequest>();
                tempDriverRideRequests.addAll(Data.driverRideRequests);
                for (int i = 0; i < tempDriverRideRequests.size(); i++) {
                    if ((BusinessType.AUTOS == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.autosAvailable)) {
                        Data.driverRideRequests.remove(i);
                    }
                    else if ((BusinessType.FATAFAT == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.fatafatAvailable)) {
                        Data.driverRideRequests.remove(i);
                    }
                    else if ((BusinessType.MEALS == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.mealsAvailable)) {
                        Data.driverRideRequests.remove(i);
                    }
                }
                tempDriverRideRequests.clear();
                tempDriverRideRequests = null;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    public boolean isDriverStateFree(){
        return (UserMode.DRIVER == userMode
            && DriverScreenMode.D_INITIAL == driverScreenMode);
    }

    public boolean isDriverEngaged(){
        return (UserMode.DRIVER == userMode
            && (DriverScreenMode.D_START_RIDE == driverScreenMode
            ||  DriverScreenMode.D_IN_RIDE == driverScreenMode));
    }



    public void updateReceiveRequestsFlag(){
        if(Data.userData != null){
            if(0 == Data.userData.autosAvailable && 0 == Data.userData.mealsAvailable && 0 == Data.userData.fatafatAvailable){
                Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 0);
            }
            else{
                if(isDriverEngaged()){
                    Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 0);
                }
                else{
                    Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 1);
                }
            }
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
	








	Handler reconnectionHandler = null;
	public void reconnectLocationFetchers(){
		if(reconnectionHandler == null){
			disconnectGPSListener();
			destroyFusedLocationFetchers();
			reconnectionHandler = new Handler();
			reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					connectGPSListener();
					initializeFusedLocationFetchers();
					try {
						reconnectionHandler.removeCallbacks(this);
						reconnectionHandler = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 2000);
		}
	}









	
	public void setUserData(){
		try{
			userName.setText(Data.userData.userName);
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
            	Log.e("service already running", "="+service.service.getClassName());
                return true;
            }
        }
        return false;
    }



	public void switchUserScreen(final UserMode mode){
		switch(mode){
			case DRIVER:
				Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_DRIVER);
				driverMainLayout.setVisibility(View.VISIBLE);
				break;

			default:
				Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_DRIVER);
				driverMainLayout.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	public void updateDriverServiceFast(String choice){
		Database2.getInstance(HomeActivity.this).updateDriverServiceFast(choice);
	}
	
	
	public void switchDriverScreen(final DriverScreenMode mode){
		if(userMode == UserMode.DRIVER){
			
			 initializeFusedLocationFetchers();
			
			
		if(mode == DriverScreenMode.D_RIDE_END){
			if(Data.endRideData != null){
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
				
				reviewUserName.setText(Data.assignedCustomerInfo.name);
				
				try{
					Picasso.with(HomeActivity.this).load(((AutoCustomerInfo)Data.assignedCustomerInfo).image).skipMemoryCache().transform(new BlurTransform()).into(reviewUserImgBlured);
				}catch(Exception e){}
				try{
					Picasso.with(HomeActivity.this).load(((AutoCustomerInfo)Data.assignedCustomerInfo).image).skipMemoryCache().transform(new CircleTransform()).into(reviewUserImage);
				}catch(Exception e){}
				
				
				setTextToFareInfoTextViews(reviewMinFareValue, reviewFareAfterValue, reviewFareAfterText);
				
				
				jugnooRideOverText.setText("The Jugnoo ride is over.");
				takeFareText.setText("Please take the fare as shown above from the customer.");
				
				displayCouponApplied();
				
				reviewSubmitBtn.setText("OK");
				
			}
			else{
				driverScreenMode = DriverScreenMode.D_INITIAL;
				switchDriverScreen(driverScreenMode);
			}
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
			topRl.setBackgroundColor(getResources().getColor(R.color.bg_grey));
		}
		
		
		switch(mode){
		
			case D_INITIAL:
				
				updateDriverServiceFast("no");
				
				textViewDriverInfo.setVisibility(View.GONE);
				
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				new DriverServiceOperations().checkStartService(HomeActivity.this);
				
				cancelCustomerPathUpdateTimer();
				
				if(map != null){
					map.clear();
				}
				
				showAllRideRequestsOnMap();
				
				cancelCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				
				initializeStationDataProcedure();
				
				break;
				
				
			case D_REQUEST_ACCEPT:
				
				updateDriverServiceFast("no");
				
				if(!isServiceRunning(HomeActivity.this, DriverLocationUpdateService.class.getName())){
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				}
				
				if(map != null){
					map.clear();
					customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.dCustLatLng));
				}
				
				if(Data.openedDriverRideRequest != null){
					if(BusinessType.MEALS == Data.openedDriverRideRequest.businessType){
						textViewBeforeAcceptRequestInfo.setVisibility(View.VISIBLE);
						textViewBeforeAcceptRequestInfo.setText("Ride Time: "
								+((MealRideRequest)Data.openedDriverRideRequest).rideTime);
					}
					else if(BusinessType.FATAFAT == Data.openedDriverRideRequest.businessType){
						textViewBeforeAcceptRequestInfo.setVisibility(View.VISIBLE);
						textViewBeforeAcceptRequestInfo.setText("Cash Needed: Rs. "
							+((FatafatRideRequest)Data.openedDriverRideRequest).orderAmount);
					}
					else{
						textViewBeforeAcceptRequestInfo.setVisibility(View.GONE);
					}
				}
				else{
					textViewBeforeAcceptRequestInfo.setVisibility(View.GONE);
				}
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.VISIBLE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				
				cancelStationPathUpdateTimer();
			
				break;
				
				
				
			case D_START_RIDE:
				
				updateDriverServiceFast("yes");

				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				if(map != null){
					map.clear();
					customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.assignedCustomerInfo.requestlLatLng));
				}
				
				
				setAssignedCustomerInfoToViews(mode);
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverStartRideMainRl.setVisibility(View.VISIBLE);
				driverInRideMainRl.setVisibility(View.GONE);
				
				
				startCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				
				cancelStationPathUpdateTimer();
				
				
				break;
				
				
				
				
			case D_IN_RIDE:
				
				updateDriverServiceFast("no");
				
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
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
					if(((Data.assignedCustomerInfo != null) && (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType))){
						if (((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
							customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryLatLng));
						}
					}
				}
				
				updateDistanceFareTexts(totalDistance, rideTimeChronometer.eclipsedTime);
				
				setAssignedCustomerInfoToViews(mode);
			
				setTextToFareInfoTextViews(inrideMinFareValue, inrideFareAfterValue, inrideFareAfterText);
				
				
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.VISIBLE);
				
				driverScheduledRideText.setVisibility(View.GONE);
				
				driverStartRideMainRl.setVisibility(View.GONE);
				driverInRideMainRl.setVisibility(View.VISIBLE);
				
				if(BusinessType.FATAFAT.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()){
					startCustomerPathUpdateTimer();
					driverEndRideBtn.setText("Mark Delivered");
                    inrideFareInfoRl.setVisibility(View.GONE);
				}
				else if(BusinessType.MEALS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()){
					cancelCustomerPathUpdateTimer();
					driverEndRideBtn.setText("Mark Delivered");
                    inrideFareInfoRl.setVisibility(View.GONE);
				}
				else{
					cancelCustomerPathUpdateTimer();
					driverEndRideBtn.setText("End Ride");
                    inrideFareInfoRl.setVisibility(View.VISIBLE);
				}
				startMapAnimateAndUpdateRideDataTimer();
				cancelStationPathUpdateTimer();
				
			
				break;
				
				
			case D_RIDE_END:
				
				updateDriverServiceFast("no");
				
				stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
				driverInitialLayout.setVisibility(View.GONE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				cancelStationPathUpdateTimer();
				
				
			
				break;
				
				
			
			default:
				driverInitialLayout.setVisibility(View.VISIBLE);
				driverRequestAcceptLayout.setVisibility(View.GONE);
				driverEngagedLayout.setVisibility(View.GONE);
				
				cancelCustomerPathUpdateTimer();
				cancelMapAnimateAndUpdateRideDataTimer();
				cancelStationPathUpdateTimer();
		
		}

            updateReceiveRequestsFlag();

		startMeteringService();
		
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
				}
			}
		}).start();
		
		}
	}
	
	
	
	public void startMeteringService(){
		
		lastGPSLocation = null;
		lastFusedLocation = null;
		distanceUpdateFromService = false;
		
		if(rideStartPositionMarker != null){
			rideStartPositionMarker.remove();
			rideStartPositionMarker = null;
		}
		
		if(DriverScreenMode.D_IN_RIDE == driverScreenMode){
			
			String meteringState = Database2.getInstance(this).getMetringState();
			if(Database2.ON.equalsIgnoreCase(meteringState)){
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						displayOldPath();
					}
				}, 1000);
			}
			
			int rowsAffected = Database2.getInstance(this).updateMetringState(Database2.ON);
			if(rowsAffected > 0){
				startService(new Intent(this, MeteringService.class));
			}
			else{
				Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			int rowsAffected = Database2.getInstance(this).updateMetringState(Database2.OFF);
			if(rowsAffected > 0){
				stopService(new Intent(this, MeteringService.class));
			}
			else{
				Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
	public void displayCouponApplied(){
			try {
				if(Data.assignedCustomerInfo != null){
					if(BusinessType.AUTOS == Data.assignedCustomerInfo.businessType){
                        reviewFareInfoInnerRl.setVisibility(View.VISIBLE);
						AutoCustomerInfo autoCustomerInfo = (AutoCustomerInfo)Data.assignedCustomerInfo;
						if(autoCustomerInfo.couponInfo != null){
							endRideInfoRl.setVisibility(View.GONE);
							relativeLayoutCoupon.setVisibility(View.VISIBLE);
							relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
							
							if(PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode){					// wallet
								textViewCouponDiscountedFare.setText("Rs. "+decimalFormatNoDecimal.format(Data.endRideData.toPay));
								textViewCouponTitle.setText(autoCustomerInfo.couponInfo.title + "\n& Jugnoo Cash");
								textViewCouponSubTitle.setVisibility(View.GONE);
							}
							else{																			// no wallet
								textViewCouponDiscountedFare.setText("Rs. "+decimalFormatNoDecimal.format(Data.endRideData.toPay));
								textViewCouponTitle.setText(autoCustomerInfo.couponInfo.title);
								textViewCouponSubTitle.setText(autoCustomerInfo.couponInfo.subtitle);
								textViewCouponSubTitle.setVisibility(View.VISIBLE);
							}

                            textViewCouponPayTakeText.setText("Take");
						}
						else if(autoCustomerInfo.promoInfo != null){
							endRideInfoRl.setVisibility(View.GONE);
							relativeLayoutCoupon.setVisibility(View.VISIBLE);
							relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
							
							if(PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode){					// wallet
								textViewCouponDiscountedFare.setText("Rs. "+decimalFormatNoDecimal.format(Data.endRideData.toPay));
								textViewCouponTitle.setText(autoCustomerInfo.promoInfo.title + "\n& Jugnoo Cash");
								textViewCouponSubTitle.setVisibility(View.GONE);
							}
							else{																			// no wallet
								textViewCouponDiscountedFare.setText("Rs. "+decimalFormatNoDecimal.format(Data.endRideData.toPay));
								textViewCouponTitle.setText(autoCustomerInfo.promoInfo.title);
								textViewCouponSubTitle.setVisibility(View.GONE);
							}

                            textViewCouponPayTakeText.setText("Take");
						}
						else{
							throw new Exception();
						}
					}
					else if(BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType){
                        reviewFareInfoInnerRl.setVisibility(View.GONE);
						FatafatOrderInfo fatafatOrderInfo = (FatafatOrderInfo)Data.assignedCustomerInfo;
						
						endRideInfoRl.setVisibility(View.GONE);
						relativeLayoutCoupon.setVisibility(View.GONE);
						relativeLayoutFatafatCustomerAmount.setVisibility(View.VISIBLE);
						
						double finalDiscountedPrice = fatafatOrderInfo.deliveryInfo.finalPrice - fatafatOrderInfo.deliveryInfo.discount;
						if(finalDiscountedPrice < 0){
							finalDiscountedPrice = 0;
						}
						
						textViewFatafatBillAmountValue.setText("Rs. "+decimalFormatNoDecimal.format(fatafatOrderInfo.deliveryInfo.finalPrice));
						textViewFatafatBillDiscountValue.setText("Rs. "+decimalFormatNoDecimal.format(fatafatOrderInfo.deliveryInfo.discount));
						textViewFatafatBillFinalAmountValue.setText("Rs. "+decimalFormatNoDecimal.format(finalDiscountedPrice));
						textViewFatafatBillJugnooCashValue.setText("Rs. "+decimalFormatNoDecimal.format(fatafatOrderInfo.deliveryInfo.paidFromWallet));
						textViewFatafatBillToPay.setText("Rs. "+decimalFormatNoDecimal.format(fatafatOrderInfo.deliveryInfo.customerToPay));
					}
					else{
						throw new Exception();
					}
				}
				else{
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(BusinessType.AUTOS == Data.assignedCustomerInfo.businessType){
					if(PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode){								// wallet
						textViewCouponDiscountedFare.setText("Rs. "+decimalFormatNoDecimal.format(Data.endRideData.toPay));
						textViewCouponTitle.setText("Jugnoo Cash");
						textViewCouponSubTitle.setVisibility(View.GONE);

                        textViewCouponPayTakeText.setText("Take");

						endRideInfoRl.setVisibility(View.GONE);
						relativeLayoutCoupon.setVisibility(View.VISIBLE);
						relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
					}
					else{																			// no wallet
						endRideInfoRl.setVisibility(View.VISIBLE);
						relativeLayoutCoupon.setVisibility(View.GONE);
						relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
					}
				}
			}
		}



	
	
	public void setAssignedCustomerInfoToViews(DriverScreenMode mode){
		try {
			if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType) {
				driverPassengerName.setText(Data.assignedCustomerInfo.name);

				try {
					double rateingD = 4;
					try {
						rateingD = Double.parseDouble(((AutoCustomerInfo) Data.assignedCustomerInfo).rating);
					} catch (Exception e) {
						Log.w("rateingD", "e" + e.getLocalizedMessage());
					}
					driverPassengerRatingValue.setText(decimalFormat.format(rateingD) + " " + getResources().getString(R.string.rating));

					if ("".equalsIgnoreCase(((AutoCustomerInfo) Data.assignedCustomerInfo).schedulePickupTime)) {
						driverScheduledRideText.setVisibility(View.GONE);
					} else {
						String time = DateOperations.getTimeAMPM(DateOperations.utcToLocal(((AutoCustomerInfo) Data.assignedCustomerInfo).schedulePickupTime));
						if ("".equalsIgnoreCase(time)) {
							driverScheduledRideText.setVisibility(View.GONE);
						} else {
							driverScheduledRideText.setVisibility(View.VISIBLE);
							driverScheduledRideText.setText("Scheduled Ride Pickup: " + time);
						}
					}

					if (1 == Data.userData.freeRideIconDisable) {
						driverFreeRideIcon.setVisibility(View.GONE);
					} else {
						if (1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).freeRide) {
							driverFreeRideIcon.setVisibility(View.VISIBLE);
						} else {
							driverFreeRideIcon.setVisibility(View.GONE);
						}
					}

                    if(DriverScreenMode.D_START_RIDE == mode){
                        textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
                        updateCustomerPickupAddress(Data.assignedCustomerInfo.requestlLatLng);
                    }
                    else{
                        textViewCustomerPickupAddress.setVisibility(View.GONE);
                    }

					textViewAfterAcceptRequestInfo.setVisibility(View.GONE);
					textViewAfterAcceptAmount.setVisibility(View.GONE);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            else if (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType) {
				try {
					driverScheduledRideText.setVisibility(View.GONE);
					driverFreeRideIcon.setVisibility(View.GONE);
					textViewAfterAcceptRequestInfo.setVisibility(View.VISIBLE);
					textViewAfterAcceptAmount.setVisibility(View.VISIBLE);

					if (DriverScreenMode.D_START_RIDE == mode) {
                        textViewCustomerPickupAddress.setVisibility(View.GONE);
						driverPassengerName.setText(Data.assignedCustomerInfo.name);
						textViewAfterAcceptRequestInfo.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).address);
						textViewAfterAcceptAmount.setText("Money to pay: Rs. " + decimalFormatNoDecimal.format(((FatafatOrderInfo) Data.assignedCustomerInfo).orderAmount));
					}
                    else if (DriverScreenMode.D_IN_RIDE == mode) {
						if (((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo != null && ((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
							driverPassengerName.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo.name);
							textViewAfterAcceptRequestInfo.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryAddress);
							textViewAfterAcceptAmount.setText("Money to take: Rs. " + decimalFormatNoDecimal.format(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.customerToPay));
                            textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
                            textViewCustomerPickupAddress.setText("Order ID: "+((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.orderId);
						}
                        else{
                            textViewCustomerPickupAddress.setVisibility(View.GONE);
                        }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void updateCustomerPickupAddress(final LatLng latLng){
        textViewCustomerPickupAddress.setText("Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final String address = MapUtils.getGAPIAddress(latLng);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(DriverScreenMode.D_START_RIDE == driverScreenMode){
                                textViewCustomerPickupAddress.setText(address);
                            }
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public synchronized void onGPSLocationChanged(Location location) {
        if(Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0){
            HomeActivity.myLocation = location;
            zoomToCurrentLocationAtFirstLocationFix(location);
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
	
	
	public void initializeGPSForegroundLocationFetcher(){
//		if(gpsForegroundLocationFetcher == null){
//			gpsForegroundLocationFetcher = new GPSForegroundLocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD);
//		}
	}
	
	public void connectGPSListener(){
		disconnectGPSListener();
		try {
			initializeGPSForegroundLocationFetcher();
//			gpsForegroundLocationFetcher.connect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectGPSListener(){
		try {
//			if(gpsForegroundLocationFetcher != null){
//				gpsForegroundLocationFetcher.destroy();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(!checkIfUserDataNull(HomeActivity.this)){
			setUserData();
		    
		    connectGPSListener();
		    
		    initializeFusedLocationFetchers();
		    
		    if(UserMode.DRIVER == userMode){
				buildTimeSettingsAlertDialog(this);
			}
		    
		}
	}
	
	
	
	@Override
	protected void onPause() {
		
		GCMIntentService.clearNotifications(getApplicationContext());
		saveDataOnPause(false);
		
		try{
			if(userMode == UserMode.DRIVER){
	    		if(driverScreenMode != DriverScreenMode.D_IN_RIDE){
	    			disconnectGPSListener();
	    			destroyFusedLocationFetchers();
	    		}
	    	}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		super.onPause();
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		try{
			if(userMode == UserMode.DRIVER){
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE || driverScreenMode == DriverScreenMode.D_START_RIDE){
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				}
				else{
					ActivityCompat.finishAffinity(this);
				}
			}
			else{
				ActivityCompat.finishAffinity(this);
			}
		} catch(Exception e){
			e.printStackTrace();
			ActivityCompat.finishAffinity(this);
		}
	}
	
	    
	
	@Override
    public void onDestroy() {
        try{
        	saveDataOnPause(true);
        	
    		GCMIntentService.clearNotifications(HomeActivity.this);
    		GCMIntentService.stopRing();
    		
    		MeteringService.clearNotifications(HomeActivity.this);
    		
    		disconnectGPSListener();
    		
    		destroyFusedLocationFetchers();
	        
	        ASSL.closeActivity(drawerLayout);
	        
	        appInterruptHandler = null;
	        
	        Database2.getInstance(this).close();
	        
	        System.gc();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        super.onDestroy();
    }
	
	
	public void saveDataOnPause(final boolean stopWait){
		try {
			if(userMode == UserMode.DRIVER){
				
				SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				Editor editor = pref.edit();
				
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE){
					
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
					
					long elapsedMillis = waitChronometer.eclipsedTime;
			    	
					editor.putString(Data.SP_TOTAL_DISTANCE, ""+totalDistance);
					editor.putString(Data.SP_WAIT_TIME, ""+elapsedMillis);
					
					long elapsedRideTime = rideTimeChronometer.eclipsedTime;
					editor.putString(Data.SP_RIDE_TIME, ""+elapsedRideTime);
					
					editor.putString(Data.SP_LAST_LOCATION_TIME, ""+HomeActivity.this.lastLocationTime);
					
					if(HomeActivity.this.lastLocation != null){
						if(Utils.compareDouble(HomeActivity.this.lastLocation.getLatitude(), 0) != 0 && Utils.compareDouble(HomeActivity.this.lastLocation.getLongitude(), 0) != 0){
							editor.putString(Data.SP_LAST_LATITUDE, ""+HomeActivity.this.lastLocation.getLatitude());
							editor.putString(Data.SP_LAST_LONGITUDE, ""+HomeActivity.this.lastLocation.getLongitude());
						}
					}
					
				}
				
				editor.commit();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	

	
	
	
	
	
	
//	public synchronized void checkAndUpdateWaitTimeDistance(final double distance){
//		try {
//			if(waitStart == 1){
//				distanceAfterWaitStarted = distanceAfterWaitStarted + distance;
//				if(distanceAfterWaitStarted >= MAX_WAIT_TIME_ALLOWED_DISTANCE){
//					stopWait();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	//totalDistance, rideTimeChronometer.eclipsedTime
	public synchronized void updateDistanceFareTexts(double distance, long elapsedTime){
		try {
			double totalDistanceInKm = Math.abs(distance/1000.0);
			
			long rideTimeSeconds = elapsedTime / 1000;
			double totalTimeInMin = Math.ceil(((double)rideTimeSeconds) / 60.0);
			
			long waitTimeSeconds = waitChronometer.eclipsedTime / 1000;
			double totalWaitTimeInMin = Math.ceil(((double)waitTimeSeconds) / 60.0);
			
			
			driverIRDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm));
			if(Data.fareStructure != null){
				driverIRFareValue.setText(""+decimalFormat.format(Data.fareStructure.calculateFare(totalDistanceInKm, totalTimeInMin, totalWaitTimeInMin)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			
			if(Color.TRANSPARENT != MAP_PATH_COLOR){
				map.addPolyline(polylineOptions);
			}
			
			
			
			if(firstLatLng == null){
				firstLatLng = Data.startRidePreviousLatLng;
			}
			
			if(firstLatLng != null){
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.snippet("");
				markerOptions.title("start ride location");
				markerOptions.position(firstLatLng);
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
				rideStartPositionMarker = map.addMarker(markerOptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	class ViewHolderDriverRequest {
		TextView textViewRequestAddress, textViewRequestDistance, textViewRequestTime, textViewRequestNumber, 
			textViewOtherRequestDetails;
        Button buttonAcceptRide;
        ImageView imageViewRequestType;
		RelativeLayout relative;
		int id;
	}

	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRequest holder;
		
		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				holder.textViewOtherRequestDetails = (TextView) convertView.findViewById(R.id.textViewOtherRequestDetails); holder.textViewOtherRequestDetails.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.buttonAcceptRide = (Button) convertView.findViewById(R.id.buttonAcceptRide); holder.buttonAcceptRide.setTypeface(Data.latoRegular(getApplicationContext()));

				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
                holder.buttonAcceptRide.setTag(holder);
				
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
			
			
			long timeDiff = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), driverRideRequest.startTime);
			long timeDiffInSec = timeDiff / 1000;
			holder.textViewRequestTime.setText(""+timeDiffInSec + " sec left");
			
			if(myLocation != null){
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				double distance = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), driverRideRequest.latLng);
				distance = distance * 1.5;
				if(distance >= 1000){
					holder.textViewRequestDistance.setText(""+decimalFormat.format(distance/1000)+" km away");
				}
				else{
					holder.textViewRequestDistance.setText(""+decimalFormat.format(distance)+" m away");
				}
			}
			else{
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}
			
			if(BusinessType.AUTOS == driverRideRequest.businessType){
				holder.imageViewRequestType.setImageResource(R.drawable.request_autos);
				holder.textViewOtherRequestDetails.setVisibility(View.GONE);
			}
			else if(BusinessType.MEALS == driverRideRequest.businessType){
                holder.imageViewRequestType.setImageResource(R.drawable.request_meals);
				holder.textViewOtherRequestDetails.setVisibility(View.VISIBLE);
				holder.textViewOtherRequestDetails.setText("Ride Time: "+((MealRideRequest)driverRideRequest).rideTime);
			}
			else if(BusinessType.FATAFAT == driverRideRequest.businessType){
                holder.imageViewRequestType.setImageResource(R.drawable.request_fatafat);
				holder.textViewOtherRequestDetails.setVisibility(View.VISIBLE);
				holder.textViewOtherRequestDetails.setText("Cash Needed: Rs. "+((FatafatRideRequest)driverRideRequest).orderAmount);
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
						Data.openedDriverRideRequest = driverRideRequest;
						
						driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
						switchDriverScreen(driverScreenMode);
						
						map.animateCamera(CameraUpdateFactory.newLatLng(driverRideRequest.latLng), 1000, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

            holder.buttonAcceptRide.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        holder = (ViewHolderDriverRequest) v.getTag();

                        DriverRideRequest driverRideRequest = Data.driverRideRequests.get(holder.id);

                        Data.dEngagementId = driverRideRequest.engagementId;
                        Data.dCustomerId = driverRideRequest.customerId;
                        Data.dCustLatLng = driverRideRequest.latLng;
                        Data.openedDriverRideRequest = driverRideRequest;

                        driverAcceptRideBtn.performClick();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
			
			
			return convertView;
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
	 * ASync for change driver mode from server
	 */
	public void driverAcceptRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			final RequestParams params = new RequestParams();
			 
			if(myLocation != null){
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}
			
			
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			
			if(Data.openedDriverRideRequest != null){
				params.put("reference_id", ""+Data.openedDriverRideRequest.referenceId);
			}

			Log.i("accept_a_request  api", "=params="+params);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/accept_a_request", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							callAndHandleStateRestoreAPI();
						}
						
						@Override
						public void onSuccess(String response) {
							Log.i("accept ride api Server response", "response = " + response);
	
							
							try{
								
								Log.writePathLogToFile(Data.dEngagementId + "accept", params.toString());
								Log.writePathLogToFile(Data.dEngagementId + "accept", response);
							} catch(Exception e){
								e.printStackTrace();
							}
							
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
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									
									DialogPopup.dismissLoadingDialog();
									
									reduceRideRequest(Data.dEngagementId);
									
								}
								else{
									
									int flag = ApiResponseFlags.RIDE_ACCEPTED.getOrdinal();
									
									if(jObj.has("flag")){
										flag = jObj.getInt("flag");
									}
									
									if(ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag){
										if(jObj.has("fare_details")){
											try{
												Data.fareStructure = JSONParser.parseFareObject(jObj.getJSONObject("fare_details"));
											} catch(Exception e){
												Log.w("fareStructure", "e="+e.toString());
											}
										}
										if(jObj.has("fare_factor")){
											try{
												Data.fareStructure.fareFactor = jObj.getDouble("fare_factor");
											} catch(Exception e){
												Log.w("fareFactor", "e="+e.toString());
											}
										}
										
										//"http://jugnoo-images.s3.amazonaws.com/user_profile/user.png";

										int referenceId = jObj.getInt("reference_id");
										int businessId = jObj.getInt("business_id");
										
										if(BusinessType.AUTOS.getOrdinal() == businessId){
											
//										{
//										    "business_id": 1,
//										    "reference_id": 0,
//										    "user_data": {
//										        "user_id": 207,
//										        "user_name": "Shankar Bhagwati",
//										        "phone_no": "+919000111001",
//										        "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//										        "user_rating": 4.989637305699482,
//										        "jugnoo_balance": 1723
//										    },
//										    "is_scheduled": 0,
//										    "pickup_latitude": 30.7191,
//										    "pickup_longitude": 76.8103,
//										    "pickup_time": "",
//										    "free_ride": 0,
//										    "fare_factor": 1.2,
//										    "fare_details": {
//										        "id": 1,
//										        "fare_fixed": 20,
//										        "fare_per_km": 5,
//										        "fare_threshold_distance": 0,
//										        "fare_per_min": 1,
//										        "fare_threshold_time": 0,
//										        "fare_per_waiting_min": 0,
//										        "fare_threshold_waiting_time": 0,
//										        "type": 0,
//										        "per_ride_driver_subsidy": 0,
//										        "accept_subsidy_per_km": 3
//										    },
//										    "coupon": {},
//										    "promotion": {}
//										}
											
											double jugnooBalance = 0;
											
											JSONObject userData = jObj.getJSONObject("user_data");
											
											String userName = userData.getString("user_name");
											String userImage = userData.getString("user_image");
											String phoneNo = userData.getString("phone_no");
											String rating = "4";
											try{rating = userData.getString("user_rating");}catch(Exception e){}
											if(userData.has("jugnoo_balance")){
												jugnooBalance = userData.getDouble("jugnoo_balance");
											}
											
											double pickupLatitude = jObj.getDouble("pickup_latitude");
											double pickupLongitude = jObj.getDouble("pickup_longitude");
											
											LatLng pickuplLatLng = new LatLng(pickupLatitude, pickupLongitude);
											
											int isScheduled = 0;
											String pickupTime = "";
											if(jObj.has("is_scheduled")){
												isScheduled = jObj.getInt("is_scheduled");
												if(isScheduled == 1 && jObj.has("pickup_time")){
													pickupTime = jObj.getString("pickup_time");
												}
											}
											
											int freeRide = 0;
											if(jObj.has("free_ride")){
												freeRide = jObj.getInt("free_ride");
											}
											
											CouponInfo couponInfo = null;
											if(jObj.has("coupon")){
												try{
													couponInfo = JSONParser.parseCouponInfo(jObj.getJSONObject("coupon"));
												} catch(Exception e){
													Log.w("couponInfo", "e="+e.toString());
												}
											}
											
											PromoInfo promoInfo = null;
											if(jObj.has("promotion")){
												try{
													promoInfo = JSONParser.parsePromoInfo(jObj.getJSONObject("promotion"));
												} catch(Exception e){
													Log.w("promoInfo", "e="+e.toString());
												}
											}
											
											Data.assignedCustomerInfo = new AutoCustomerInfo(Integer.parseInt(Data.dEngagementId), 
													Integer.parseInt(Data.dCustomerId), referenceId,
													userName, phoneNo, pickuplLatLng, 
													userImage, rating, pickupTime, freeRide, couponInfo, promoInfo, jugnooBalance);
											
											Data.driverRideRequests.clear();
		
									        GCMIntentService.clearNotifications(getApplicationContext());
									        
											driverScreenMode = DriverScreenMode.D_START_RIDE;
											switchDriverScreen(driverScreenMode);
											
										}
										else if(BusinessType.MEALS.getOrdinal() == businessId){
											
										}
										else if(BusinessType.FATAFAT.getOrdinal() == businessId){
											
//											{
//												   flag    : constants.responseFlags.RIDE_ACCEPTED,
//												   store_data    : {
//												       store_id  : <store_id>,
//												       name      : <store_name>,
//												       latitude  : <store_latitude>,
//												       longitude : <store_longitude>,
//												       address   : <store_address>,
//												       phone_no  : <store_phone_no>
//												   },
//												   fare_factor  : <driver_fare_factor>,
//												   fare_details : <driver_fare_details>
//											}

											int orderAmount = jObj.getInt("order_amount");
											
											JSONObject storeData = jObj.getJSONObject("store_data");
											int storeId = storeData.getInt("store_id");
											String storeName = storeData.getString("name");
											double storeLatitude = storeData.getDouble("latitude");
											double storeLongitude = storeData.getDouble("longitude");
											String storeAddress = storeData.getString("address");
											String storePhoneNumber = storeData.getString("phone_no");
											
											Data.assignedCustomerInfo = new FatafatOrderInfo(Integer.parseInt(Data.dEngagementId), 
													storeId, referenceId, 
													storeName, storePhoneNumber, new LatLng(storeLatitude, storeLongitude), 
													storeAddress, orderAmount);
											
											Data.driverRideRequests.clear();
											
									        GCMIntentService.clearNotifications(getApplicationContext());
									        
											driverScreenMode = DriverScreenMode.D_START_RIDE;
											switchDriverScreen(driverScreenMode);
											
										}
										else{
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
										
									
									}
									else{
										try{
											Log.e("accept_a_request flag", "="+flag);
											String logMessage = jObj.getString("log");
											DialogPopup.alertPopup(activity, "", ""+logMessage);
											reduceRideRequest(Data.dEngagementId);
										} catch(Exception e){
											e.printStackTrace();
										}
									}
									
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
							
							DialogPopup.dismissLoadingDialog();
							
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	
	public void reduceRideRequest(String engagementId){
		Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
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
				
				if(Data.openedDriverRideRequest != null){
					params.put("reference_id", ""+Data.openedDriverRideRequest.referenceId);
				}

				Log.i("reject_a_request params", "=" + params);
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/reject_a_request", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.v("Server response of reject_a_request", "=" + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											DialogPopup.alertPopup(activity, "", errorMessage);
										}
									}
									else{
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												DialogPopup.alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										
										if(map != null){
											map.clear();
										}
										stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
										
										reduceRideRequest(Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	public void initializeStartRideVariables(){
		
		lastLocation = null;
		lastLocationTime = System.currentTimeMillis();
		
		Database2.getInstance(this).deleteRideData();
		
		Database.getInstance(this).deleteSavedPath();
		
		HomeActivity.previousWaitTime = 0;
		HomeActivity.previousRideTime = 0;
		HomeActivity.totalDistance = -1;
		
		if(HomeActivity.deltaLatLngPairs == null){
			HomeActivity.deltaLatLngPairs = new ArrayList<LatLngPair>();
		}
		HomeActivity.deltaLatLngPairs.clear();
		
		clearSPData();

		MeteringService.gpsInstance(this).saveEngagementIdToSP(this, Data.dEngagementId);
		MeteringService.gpsInstance(this).stop();
		
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
			
			if(Data.assignedCustomerInfo != null){
				params.put("reference_id", ""+Data.assignedCustomerInfo.referenceId);
			}

			Log.i("params", "=" + params);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/start_ride", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
//							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
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
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
								}
								else{

									int flag = ApiResponseFlags.RIDE_STARTED.getOrdinal();
									
									if(jObj.has("flag")){
										flag = jObj.getInt("flag");
									}
									
//									for fatafat request
//									{
//									    "flag": 114,
//									    "delivery_info": {
//									        "order_id": 230,
//									        "delivery_address": "Edited Address\nLine 2",
//									        "delivery_latitude": 31,
//									        "delivery_longitude": 71,
//									        "final_price": 620,
//									        "paid_from_wallet": 0,
//									        "customer_to_pay": 620
//									    },
//									    "customer_info": {
//									        "user_id": 287,
//									        "name": "User Name Change",
//									        "phone_no": "9779016609"
//									    }
//									}
									
									if(ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag){
										if((Data.assignedCustomerInfo != null) && (BusinessType.FATAFAT.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal())){
											
											JSONObject jDeliveryInfo = jObj.getJSONObject("delivery_info");
											FatafatDeliveryInfo deliveryInfo = new FatafatDeliveryInfo(jDeliveryInfo.getInt("order_id"), 
													jDeliveryInfo.getString("delivery_address"), 
													new LatLng(jDeliveryInfo.getDouble("delivery_latitude"), jDeliveryInfo.getDouble("delivery_longitude")), 
													jDeliveryInfo.getDouble("final_price"), 
													jDeliveryInfo.getDouble("discount"),
													jDeliveryInfo.getDouble("paid_from_wallet"), 
													jDeliveryInfo.getDouble("customer_to_pay"));
											
											JSONObject jCustomerInfo = jObj.getJSONObject("customer_info");
											FatafatCustomerInfo customerInfo = new FatafatCustomerInfo(jCustomerInfo.getInt("user_id"), 
													jCustomerInfo.getString("name"), 
													jCustomerInfo.getString("phone_no"));
											
											((FatafatOrderInfo)Data.assignedCustomerInfo).setCustomerDeliveryInfo(customerInfo, deliveryInfo);
										}
									}
									
									
									if(map != null){
										map.clear();
									}
									
									initializeStartRideVariables();
									
									Data.startRidePreviousLatLng = driverAtPickupLatLng;
									Data.startRidePreviousLocationTime = System.currentTimeMillis();
									SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
									Editor editor = pref.edit();
									editor.putString(Data.SP_LAST_LATITUDE, "" + driverAtPickupLatLng.latitude);
									editor.putString(Data.SP_LAST_LONGITUDE, "" + driverAtPickupLatLng.longitude);
									editor.putString(Data.SP_LAST_LOCATION_TIME, "" + Data.startRidePreviousLocationTime);
									editor.commit();
									
						        	driverScreenMode = DriverScreenMode.D_IN_RIDE;
									switchDriverScreen(driverScreenMode);
									
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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

				if(Data.assignedCustomerInfo != null){
					params.put("reference_id", ""+Data.assignedCustomerInfo.referenceId);
				}
				
				Log.i("cancel_the_ride params", "=" + params);
				
			
				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/cancel_the_ride", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								callAndHandleStateRestoreAPI();
							}

							@Override
							public void onSuccess(String response) {
								Log.v("Server response of cancel_the_ride", "response = " + response);
		
								try {
									jObj = new JSONObject(response);
									
									if(!jObj.isNull("error")){
										
										String errorMessage = jObj.getString("error");
										
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											DialogPopup.alertPopup(activity, "", errorMessage);
										}
									}
									else{
										
										try {
											int flag = jObj.getInt("flag");
											if(ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
												String log = jObj.getString("log");
												DialogPopup.alertPopup(activity, "", ""+log);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
										
										if(map != null){
											map.clear();
										}
										stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
										
										reduceRideRequest(Data.dEngagementId);
										
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
		
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}

		
	}
	
	
	
	
	
	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude, 
			double waitMinutes, double rideMinutes, 
			int flagDistanceTravelled, final BusinessType businessType) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			if(BusinessType.AUTOS == businessType){
				autoEndRideAPI(activity, lastAccurateLatLng, dropLatitude, dropLongitude, waitMinutes, rideMinutes, flagDistanceTravelled, businessType);
			}
			else if(BusinessType.FATAFAT == businessType){
				fatafatEndRideAPI(activity, lastAccurateLatLng, dropLatitude, dropLongitude, waitMinutes, rideMinutes, flagDistanceTravelled, businessType);
			}
		}
		else {
			driverScreenMode = DriverScreenMode.D_IN_RIDE;
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			rideTimeChronometer.start();
		}

	}
	
	
	public void autoEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude, 
			double waitMinutes, double rideMinutes, 
			int flagDistanceTravelled, final BusinessType businessType){
		DialogPopup.showLoadingDialog(activity, "Loading...");
		
		final RequestParams params = new RequestParams();
	
		SharedPreferences pref = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis()));
		long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
		long rideTimeSeconds = timeDiffToAdd / 1000;
		double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
		Log.e("timeDiffToAdd", "="+rideTimeMinutes);
		if(rideTimeMinutes > 0){
			rideMinutes = rideTimeMinutes;
		}
		
		rideTime = decimalFormatNoDecimal.format(rideMinutes);
		waitTime = decimalFormatNoDecimal.format(waitMinutes);
		
		final double eoRideMinutes = rideMinutes;
		final double eoWaitMinutes = waitMinutes;
		
		double totalDistanceInKm = Math.abs(totalDistance/1000.0);
		
		params.put("access_token", Data.userData.accessToken);
		params.put("engagement_id", Data.dEngagementId);
		params.put("customer_id", Data.dCustomerId);
		params.put("latitude", ""+dropLatitude);
		params.put("longitude", ""+dropLongitude);
		params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
		params.put("wait_time", waitTime);
		params.put("ride_time", rideTime);
		params.put("is_cached", "0");
		params.put("flag_distance_travelled", ""+flagDistanceTravelled);
		params.put("last_accurate_latitude", ""+lastAccurateLatLng.latitude);
		params.put("last_accurate_longitude", ""+lastAccurateLatLng.longitude);
		
		if(Data.assignedCustomerInfo != null){
			params.put("reference_id", ""+Data.assignedCustomerInfo.referenceId);
		}
		
		params.put("business_id", ""+businessType.getOrdinal());

		Log.i("end_ride params =", "="+params);
		
		final String url = Data.SERVER_URL + "/end_ride";
	
		AsyncHttpClient client = Data.getClient();
		client.post(url, params,
				new CustomAsyncHttpResponseHandler() {
				private JSONObject jObj;

					@Override
					public void onFailure(Throwable arg3) {
						Log.e("request fail", arg3.toString());
						
						endRideOffline(activity, url, params, eoRideMinutes, eoWaitMinutes, (AutoCustomerInfo) Data.assignedCustomerInfo);
						
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
									DialogPopup.alertPopup(activity, "", errorMessage);
								}
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								rideTimeChronometer.start();
							}
							else{
								
								try{
									totalFare = jObj.getDouble("fare");
								} catch(Exception e){
									e.printStackTrace();
									totalFare = 0;
								}
								
								JSONParser.parseEndRideData(jObj, Data.dEngagementId, totalFare);
								
								
								
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
								
								initializeStartRideVariables();
								
							}
						}  catch (Exception exception) {
							exception.printStackTrace();
							driverScreenMode = DriverScreenMode.D_IN_RIDE;
							rideTimeChronometer.start();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}

						DialogPopup.dismissLoadingDialog();
					}
				});
	}
	
	
	
	
	
	
	//TODO end ride offline
	public void endRideOffline(Activity activity, String url, RequestParams params, double rideTime, double waitTime, 
			AutoCustomerInfo assignedCustomerInfo){
		try{
			
			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay;
			int paymentMode = PaymentMode.CASH.getOrdinal();
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			Log.e("offline =============", "============");
			Log.i("rideTime", "="+rideTime);
			
			try{
				totalFare = Data.fareStructure.calculateFare(totalDistanceInKm, rideTime, waitTime);
			} catch(Exception e){
				e.printStackTrace();
				totalFare = 0;
			}
			
			
			try{
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.fareStructure = "+Data.fareStructure);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "rideTime = "+rideTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "waitTime = "+waitTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalDistanceInKm = "+totalDistanceInKm);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalFare = "+totalFare);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "assignedCustomerInfo = "+assignedCustomerInfo);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
			
			actualFare = totalFare;
			
			finalToPay = totalFare;
			
			Log.i("totalFare == endride offline ", "="+totalFare);
			Log.i("endRideOffline assignedCustomerInfo.couponInfo=", "="+assignedCustomerInfo.couponInfo);
			Log.i("endRideOffline assignedCustomerInfo.promoInfo=", "="+assignedCustomerInfo.promoInfo);
			
			
			
			if(assignedCustomerInfo.couponInfo != null){		// coupon
					if(assignedCustomerInfo.couponInfo.discountPrecent > 0){		//coupon discount
						finalDiscount = ((totalFare * assignedCustomerInfo.couponInfo.discountPrecent) / 100) < assignedCustomerInfo.couponInfo.maximumDiscountValue ?
				            Math.ceil(((totalFare * assignedCustomerInfo.couponInfo.discountPrecent) / 100)) : assignedCustomerInfo.couponInfo.maximumDiscountValue;
				            
				            Log.i("coupon case discount", "((totalFare * assignedCustomerInfo.couponInfo.discountPrecent) / 100) = "
								    +((totalFare * assignedCustomerInfo.couponInfo.discountPrecent) / 100));
						Log.i("coupon case discount", "assignedCustomerInfo.couponInfo.maximumDiscountValue = "+assignedCustomerInfo.couponInfo.maximumDiscountValue);
								            
					}
				    else if(assignedCustomerInfo.couponInfo.cappedFare > 0){		// coupon capped fare
				    	Log.i("coupon case capped", "assignedCustomerInfo.couponInfo.cappedFare = "+assignedCustomerInfo.couponInfo.cappedFare);
				        Log.i("coupon case capped", "assignedCustomerInfo.couponInfo.cappedFareMaximum = "+assignedCustomerInfo.couponInfo.cappedFareMaximum);
				        if(totalFare < assignedCustomerInfo.couponInfo.cappedFare){		// fare less than capped fare
				        	finalDiscount = 0;
				        }
				        else{																// fare greater than capped fare
				            double maxDiscount = assignedCustomerInfo.couponInfo.cappedFareMaximum - assignedCustomerInfo.couponInfo.cappedFare;
				            finalDiscount = totalFare - assignedCustomerInfo.couponInfo.cappedFare;
				            finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
				            Log.i("coupon case capped", "maxDiscount = "+maxDiscount);
				        }
				    }
				    else{
				    	finalDiscount = 0;
				    }
			}
			else if(assignedCustomerInfo.promoInfo != null){		// promotion
				
				if(assignedCustomerInfo.promoInfo.discountPercentage > 0){		//promotion discount
					finalDiscount = ((totalFare * assignedCustomerInfo.promoInfo.discountPercentage) / 100) < assignedCustomerInfo.promoInfo.discountMaximum ?
			            Math.ceil(((totalFare * assignedCustomerInfo.promoInfo.discountPercentage) / 100)) : assignedCustomerInfo.promoInfo.discountMaximum;
			            
			            Log.i("promo case discount", "((totalFare * assignedCustomerInfo.promoInfo.discountPercentage) / 100) = "
							    +((totalFare * assignedCustomerInfo.promoInfo.discountPercentage) / 100));
					Log.i("promo case discount", "assignedCustomerInfo.promoInfo.discountMaximum = "+assignedCustomerInfo.promoInfo.discountMaximum);
							            
				}
			    else if(assignedCustomerInfo.promoInfo.cappedFare > 0){		// promotion capped fare
			    	Log.i("promo case capped", "assignedCustomerInfo.promoInfo.cappedFare = "+assignedCustomerInfo.promoInfo.cappedFare);
			        Log.i("promo case capped", "assignedCustomerInfo.promoInfo.cappedFareMaximum = "+assignedCustomerInfo.promoInfo.cappedFareMaximum);
			        if(totalFare < assignedCustomerInfo.promoInfo.cappedFare){		// fare less than capped fare
			        	finalDiscount = 0;
			        }
			        else{																// fare greater than capped fare
			            double maxDiscount = assignedCustomerInfo.promoInfo.cappedFareMaximum - assignedCustomerInfo.promoInfo.cappedFare;
			            finalDiscount = totalFare - assignedCustomerInfo.promoInfo.cappedFare;
			            finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
			            Log.i("promo case capped", "maxDiscount = "+maxDiscount);
			        }
			    }
			    else{
			    	finalDiscount = 0;
			    }
				
				Log.i("finalDiscount == endride offline ", "="+finalDiscount);
			}
			else{
				finalDiscount = 0;
			}
			
			
			if(totalFare > finalDiscount){									// final toPay (totalFare - discount)
				finalToPay = totalFare - finalDiscount;
			}
			else{
				finalToPay = 0;
				finalDiscount = totalFare;
			}
			
			Log.i("finalDiscount == endride offline ", "="+finalDiscount);
			Log.i("finalToPay == endride offline ", "="+finalToPay);
			
			
																			// wallet application (no split fare)
			if(assignedCustomerInfo.jugnooBalance > 0 && finalToPay > 0 && assignedCustomerInfo.jugnooBalance >= finalToPay){	// wallet
				finalPaidUsingWallet = finalToPay;
				finalToPay = 0;
					
				paymentMode = PaymentMode.WALLET.getOrdinal();
				
				params.put("payment_mode", ""+PaymentMode.WALLET.getOrdinal());
			}
			else{																			// no wallet
				finalPaidUsingWallet = 0;
				
				paymentMode = PaymentMode.CASH.getOrdinal();
				
				params.put("payment_mode", ""+PaymentMode.CASH.getOrdinal());
			}
			
			
			Data.endRideData = new EndRideData(Data.dEngagementId, actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode);
			
			try{
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.endRideData = "+Data.endRideData);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
			
			
			
			if(map != null){
				map.clear();
			}
			
			waitStart = 2;
			waitChronometer.stop();
			rideTimeChronometer.stop();
			
			
			clearSPData();
			
        	driverScreenMode = DriverScreenMode.D_RIDE_END;
			switchDriverScreen(driverScreenMode);
			

			params.put("is_cached", "1");
			
			DialogPopup.dismissLoadingDialog();
			Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			try{
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "url = "+url+" params = "+params);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			driverUploadPathDataFileAsync(activity, Data.dEngagementId);
			
			initializeStartRideVariables();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void driverUploadPathDataFileAsync(final Activity activity, String engagementId) {
		String rideDataStr = Database2.getInstance(activity).getRideData();
		if(!"".equalsIgnoreCase(rideDataStr)){
			final RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);
			params.put("ride_path_data", rideDataStr);
		
			final String url = Data.SERVER_URL + "/upload_ride_data";
			
			AsyncHttpClient client = Data.getClient();
			client.post(url, params,
					new CustomAsyncHttpResponseHandler() {
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
//							Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
						}
					});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * API for Fatafat end ride (Mark Delivered)
	 * @param activity
	 * @param lastAccurateLatLng
	 * @param dropLatitude
	 * @param dropLongitude
	 * @param waitMinutes
	 * @param rideMinutes
	 * @param flagDistanceTravelled
     * @param businessType
	 */
	public void fatafatEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude, 
			double waitMinutes, double rideMinutes, 
			int flagDistanceTravelled, final BusinessType businessType){
		
		final RequestParams params = new RequestParams();
	
		SharedPreferences pref = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis()));
		long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
		long rideTimeSeconds = timeDiffToAdd / 1000;
		double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
		Log.e("timeDiffToAdd", "="+rideTimeMinutes);
		if(rideTimeMinutes > 0){
			rideMinutes = rideTimeMinutes;
		}
		
		rideTime = decimalFormatNoDecimal.format(rideMinutes);
		waitTime = decimalFormatNoDecimal.format(waitMinutes);
		
		final double eoRideMinutes = rideMinutes;
		final double eoWaitMinutes = waitMinutes;
		
		double totalDistanceInKm = Math.abs(totalDistance/1000.0);
		
		params.put("access_token", Data.userData.accessToken);
		
		JSONObject rideDataJSON = new JSONObject();
		try {
			rideDataJSON.put("latitude", dropLatitude);
			rideDataJSON.put("longitude", dropLongitude);
			rideDataJSON.put("distance_travelled", Double.parseDouble(decimalFormat.format(totalDistanceInKm)));
			rideDataJSON.put("ride_time", Integer.parseInt(rideTime));
			rideDataJSON.put("wait_time", Integer.parseInt(waitTime));
			rideDataJSON.put("flag_distance_travelled", flagDistanceTravelled);
			rideDataJSON.put("last_accurate_latitude", lastAccurateLatLng.latitude);
			rideDataJSON.put("last_accurate_longitude", lastAccurateLatLng.longitude);
			rideDataJSON.put("engagement_id", Integer.parseInt(Data.dEngagementId));
			rideDataJSON.put("is_cached", 0);
			
			params.put("ride_data", rideDataJSON.toString());
			params.put("business_id", ""+businessType.getOrdinal());
			
			params.put("reference_id", ""+Data.assignedCustomerInfo.referenceId);
			
			Log.i("mark_delivered params =", "="+params);
			
			final String url = Data.SERVER_URL + "/mark_delivered";
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			AsyncHttpClient client = Data.getClient();
			client.post(url, params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
	
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							fatafatEndRideOffline(activity, url, params, eoRideMinutes, eoWaitMinutes, (FatafatOrderInfo) Data.assignedCustomerInfo);
						}
	
						@Override
						public void onSuccess(String response) {
							Log.e("Server response of mark_delivered", "response= " + response);
	
//							{
//							    "flag": 115,
//							    "fare": 44,
//							    "to_pay": 0,
//							    "discount": 0,
//							    "paid_using_wallet": 44,
//							    "distance_travelled": 0.039,
//							    "wait_time": 0,
//							    "ride_time": 1,
//							    "coupon": {},
//							    "payment_mode": 2,
//							    "delivery_info": {
//							        "final_price": 620,
//							        "discount": 0,
//							        "paid_using_wallet": 0,
//							        "customer_to_pay": 620
//							    }
//							}
							
							try {
								jObj = new JSONObject(response);
								
								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
									if(ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag){
										
										try{
											totalFare = jObj.getDouble("fare");
										} catch(Exception e){
											e.printStackTrace();
											totalFare = 0;
										}
										
										JSONParser.parseEndRideData(jObj, Data.dEngagementId, totalFare);
										
										JSONObject jDeliveryInfo = jObj.getJSONObject("delivery_info");
										double finalPrice = jDeliveryInfo.getDouble("final_price"); 
										double discount = jDeliveryInfo.getDouble("discount"); 
										double paidFromWallet = jDeliveryInfo.getDouble("paid_from_wallet");
										double customerToPay = jDeliveryInfo.getDouble("customer_to_pay");
										
										((FatafatOrderInfo)Data.assignedCustomerInfo).deliveryInfo.updatePrices(finalPrice, discount, paidFromWallet, customerToPay);
										
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
										
										initializeStartRideVariables();
										
									}
									else{
										driverScreenMode = DriverScreenMode.D_IN_RIDE;
										rideTimeChronometer.start();
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
								else{
									driverScreenMode = DriverScreenMode.D_IN_RIDE;
									rideTimeChronometer.start();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								rideTimeChronometer.start();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
					});
			
		} catch (Exception e1) {
			e1.printStackTrace();
			driverScreenMode = DriverScreenMode.D_IN_RIDE;
			rideTimeChronometer.start();
		}

	}
	
	
	//TODO fatafat offline mark delivered
	public void fatafatEndRideOffline(Activity activity, String url, RequestParams params, double rideTime, double waitTime, 
			FatafatOrderInfo fatafatOrderInfo){
		try{
			
			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay;
			int paymentMode = PaymentMode.CASH.getOrdinal();
			
			double totalDistanceInKm = Math.abs(totalDistance/1000.0);
			
			Log.e("offline fatafatOrderInfo =============", "============");
			Log.i("rideTime", "="+rideTime);
			
			try{
				totalFare = Data.fareStructure.calculateFare(totalDistanceInKm, rideTime, waitTime);
			} catch(Exception e){
				e.printStackTrace();
				totalFare = 0;
			}
			
			
			try{
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.fareStructure = "+Data.fareStructure);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "rideTime = "+rideTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "waitTime = "+waitTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalDistanceInKm = "+totalDistanceInKm);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalFare = "+totalFare);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "fatafatOrderInfo = "+fatafatOrderInfo);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
			actualFare = totalFare;
			
			finalToPay = totalFare;
			
			Log.i("totalFare == endride offline ", "="+totalFare);
			Log.i("endRideOffline assignedCustomerInfo.couponInfo=", "="+fatafatOrderInfo);
			
			
			finalDiscount = 0;
			finalDiscount = totalFare;
			finalPaidUsingWallet = 0;
			
			
			Data.endRideData = new EndRideData(Data.dEngagementId, actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode);
			
			
			if(map != null){
				map.clear();
			}
			
			waitStart = 2;
			waitChronometer.stop();
			rideTimeChronometer.stop();
			
			
			clearSPData();
			
        	driverScreenMode = DriverScreenMode.D_RIDE_END;
			switchDriverScreen(driverScreenMode);
			

			params.put("is_cached", "1");
			
			DialogPopup.dismissLoadingDialog();
			Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			try{
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "url = "+url+" params = "+params);
			} catch(Exception e){
				e.printStackTrace();
			}
			
			driverUploadPathDataFileAsync(activity, Data.dEngagementId);
			
			initializeStartRideVariables();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * ASync for logout from server
	 */
	public void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");

			Log.i("params", "="+params);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL+"/logout_driver", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
									HomeActivity.logoutUser(activity);
								}
								else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
									String errorMessage = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", errorMessage);
								}
								else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
									String message = jObj.getString("message");
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.LOGOUT_FAILURE.getOrdinal() == flag){
									String errorMessage = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", errorMessage);
								}
								else if(ApiResponseFlags.LOGOUT_SUCCESSFUL.getOrdinal() == flag){
									PicassoTools.clearCache(Picasso.with(activity));
									
									try {
										Session.getActiveSession().closeAndClearTokenInformation();	
									}
									catch(Exception e) {
										Log.v("Logout", "Error"+e);	
									}
									
									GCMIntentService.clearNotifications(activity);
									
									Data.clearDataOnLogout(activity);
									
									userMode = UserMode.DRIVER;
									driverScreenMode = DriverScreenMode.D_INITIAL;
									
									new DriverServiceOperations().stopService(activity);
									
									loggedOut = true;
								}
								else{
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
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
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
	
	
	/**
	 * to call only in background
	 */
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
	
	
	
	
	void logoutPopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			
			textHead.setText("Alert");
			textMessage.setText("Are you sure you want to logout?");
			
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
	
	
	
	
	
	
	
	
	
	
	//Driver's timer
	Timer timerCustomerPathUpdater;
	TimerTask timerTaskCustomerPathUpdater;
	
	
	public void startCustomerPathUpdateTimer(){
		
		cancelCustomerPathUpdateTimer();
		
		try {
			timerCustomerPathUpdater = new Timer();
			
			timerTaskCustomerPathUpdater = new TimerTask() {
				
				@Override
				public void run() {
					if((driverScreenMode == DriverScreenMode.D_START_RIDE) && (Data.assignedCustomerInfo != null)){
						getCustomerPathAndDisplay(Data.assignedCustomerInfo.requestlLatLng);
					}
					else if (((Data.assignedCustomerInfo != null) && (driverScreenMode == DriverScreenMode.D_IN_RIDE) && (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType))) {
						if (((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
							getCustomerPathAndDisplay(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryLatLng);
						}
					}
				}
			};
			timerCustomerPathUpdater.scheduleAtFixedRate(timerTaskCustomerPathUpdater, 1000, 15000);
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
	
	public boolean toShowPathToCustomer(){
		return ((driverScreenMode == DriverScreenMode.D_START_RIDE) || 
		((Data.assignedCustomerInfo != null) && (driverScreenMode == DriverScreenMode.D_IN_RIDE) && (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType)));
	}
	
	public void getCustomerPathAndDisplay(final LatLng customerLatLng) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) && myLocation != null && customerLatLng != null) {
				LatLng source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				String url = MapUtils.makeDirectionsURL(source, customerLatLng);
				Log.i("getCustomerPathAndDisplay url", "="+url);
				String result = new HttpRequester().getJSONFromUrl(url);

				if (result != null) {
					final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
					if(list.size() > 0){
						runOnUiThread(new Runnable() {
	
							@Override
							public void run() {
								try {
									if (toShowPathToCustomer()) {
										if(customerLocationMarker != null){
											customerLocationMarker.remove();
										}
										if(pathToCustomerPolyline != null){
											pathToCustomerPolyline.remove();
										}
										
										customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(customerLatLng));
										
										PolylineOptions polylineOptions = new PolylineOptions();
										polylineOptions.width(5).color(D_TO_C_MAP_PATH_COLOR).geodesic(true);
										for (int z = 0; z < list.size(); z++) {
											polylineOptions.add(list.get(z));
										}
										pathToCustomerPolyline = map.addPolyline(polylineOptions);
										
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public MarkerOptions getCustomerLocationMarkerOptions(LatLng customerLatLng){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(Data.dEngagementId);
		markerOptions.snippet("");
		markerOptions.position(customerLatLng);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPassengerMarkerBitmap(HomeActivity.this, assl)));
		return markerOptions;
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
						runOnUiThread(new Runnable() {
								
							@Override
							public void run() {
								if (myLocation != null && map != null) {
									map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			timerMapAnimateAndUpdateRideData.scheduleAtFixedRate(timerTaskMapAnimateAndUpdateRideData, 1000, 60000);
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
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	void startRidePopup(final Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

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
			        		DialogPopup.alertPopup(activity, "", "You must be present near the customer pickup location to start ride.");
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
	
	
	
	
	
	void endRidePopup(final Activity activity, final BusinessType businessType) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_two_buttons);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				
				
				if(BusinessType.AUTOS == businessType){
					textMessage.setText("Are you sure you want to end ride?");
				}
				else if(BusinessType.FATAFAT == businessType){
					textMessage.setText("Are you sure you want to mark this item delivered?\nPlease take Rs. "
                        +decimalFormatNoDecimal.format(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.customerToPay)
                        +" from customer.");
				}
				
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@SuppressWarnings("unused")
					@Override
					public void onClick(View view) {
						//TODO end ride location check 
						
						if(distanceUpdateFromService){
							Location locationToUse;
							boolean fusedLocationUsed = false;
							
							long currentTime = System.currentTimeMillis();
							long threeMinuteMillis = 3 * 60 * 1000;
							
							Log.e("lastGPSLocation on end ride", "======="+lastGPSLocation);
							Log.e("lastFusedLocation on end ride", "======="+lastFusedLocation);
							
							Log.writePathLogToFile(Data.dEngagementId+"m", "lastGPSLocation on end ride = "+lastGPSLocation);
							Log.writePathLogToFile(Data.dEngagementId+"m", "lastFusedLocation on end ride = "+lastFusedLocation);
							
							LatLng oldGPSLatLng = MeteringService.gpsInstance(HomeActivity.this).getSavedLatLngFromSP(HomeActivity.this);
							
							Log.e("oldGPSLatLng on end ride", "======="+oldGPSLatLng);
							Log.writePathLogToFile(Data.dEngagementId+"m", "oldGPSLatLng on end ride = "+oldGPSLatLng);
							
							if(lastGPSLocation != null && lastFusedLocation != null){
								long gpsLocTimeDiff = currentTime - lastGPSLocation.getTime();
								long fusedLocTimeDiff = currentTime - lastFusedLocation.getTime();
	
								Log.e("gpsLocTimeDiff on end ride", "======="+gpsLocTimeDiff);
								Log.e("fusedLocTimeDiff on end ride", "======="+fusedLocTimeDiff);
								
								Log.writePathLogToFile(Data.dEngagementId+"m", "gpsLocTimeDiff="+gpsLocTimeDiff+" and fusedLocTimeDiff="+fusedLocTimeDiff);
								
								if(gpsLocTimeDiff <= threeMinuteMillis){								// gps location is fine
									locationToUse = lastGPSLocation;
									fusedLocationUsed = false;
								}
								else{
									if(fusedLocTimeDiff <= threeMinuteMillis){							//�fused to use
										locationToUse = lastFusedLocation;
										fusedLocationUsed = true;
									}
									else{
										locationToUse = lastGPSLocation;
										fusedLocationUsed = false;
									}
								}
							}
							else if(lastGPSLocation != null && lastFusedLocation == null){
								locationToUse = lastGPSLocation;
								fusedLocationUsed = false;
							}
							else{
								locationToUse = myLocation;
								fusedLocationUsed = true;
								Log.e("locationToUse on end ride from myLocation", "======="+locationToUse);
								Log.writePathLogToFile(Data.dEngagementId+"m", "locationToUse on end ride from myLocation="+locationToUse);
							}
							
							
							if((Utils.compareDouble(oldGPSLatLng.latitude, 0.0) == 0) && (Utils.compareDouble(oldGPSLatLng.longitude, 0.0) == 0)){
								oldGPSLatLng = new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude());
							}
							Log.writePathLogToFile(Data.dEngagementId+"m", "oldGPSLatLng after on end ride = "+oldGPSLatLng);
							
							
							Log.e("locationToUse on end ride", "======="+locationToUse);
							Log.e("fusedLocationUsed on end ride", "======="+fusedLocationUsed);
							
							Log.writePathLogToFile(Data.dEngagementId+"m", "locationToUse on end ride="+locationToUse);
							Log.writePathLogToFile(Data.dEngagementId+"m", "fusedLocationUsed on end ride="+fusedLocationUsed);
							
							if(locationToUse != null){
								
								dialog.dismiss();
								
								GCMIntentService.clearNotifications(activity);
								
								driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
								driverWaitText.setText(getResources().getString(R.string.start_wait));
								waitStart = 0;
								
								long waitSeconds = waitChronometer.eclipsedTime / 1000;
								double waitMinutes = Math.ceil(((double)waitSeconds) / 60.0);
								
								long rideTimeSeconds = rideTimeChronometer.eclipsedTime / 1000;
								double rideTimeMinutes = Math.ceil(((double)rideTimeSeconds) / 60.0);
								
								if(BusinessType.AUTOS == businessType || BusinessType.FATAFAT == businessType){
									
									waitChronometer.stop();
									rideTimeChronometer.stop();
									
									driverScreenMode = DriverScreenMode.D_RIDE_END;
									
									if(fusedLocationUsed){
										calculateFusedLocationDistance(activity, oldGPSLatLng, 
												new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude()), rideTimeMinutes, businessType);
									}
									else{								
										driverEndRideAsync(activity, oldGPSLatLng, locationToUse.getLatitude(), locationToUse.getLongitude(), 0, rideTimeMinutes, 0, businessType);
									}
								}
								else{
									
								}

							}
							else{
								Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
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
	
	
	int flagDistanceTravelled = 1;
	public void calculateFusedLocationDistance(final Activity activity, final LatLng source, final LatLng destination, final double rideTimeMinutes, 
			final BusinessType businessType){
		DialogPopup.showLoadingDialog(activity, "Loading...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				double displacement = MapUtils.distance(source, destination);
				HttpRequester.TIMEOUT_CONNECTION = 10000;
				HttpRequester.TIMEOUT_SOCKET = 10000;
				String response = new HttpRequester().getJSONFromUrl(MapUtils.makeDistanceMatrixURL(source, destination));
				HttpRequester.TIMEOUT_CONNECTION = 30000;
				HttpRequester.TIMEOUT_SOCKET = 30000;
				
				flagDistanceTravelled = 1;
				
				  try {
				    	double distanceOfPath = -1;
				    	JSONObject jsonObject = new JSONObject(response);
				    	String status = jsonObject.getString("status");
				    	if("OK".equalsIgnoreCase(status)){
				    		JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
				    		distanceOfPath = element0.getJSONObject("distance").getDouble("value") ;
				    	}
				    	Log.e("calculateFusedLocationDistance distanceOfPath ", "="+distanceOfPath);
				    	if(distanceOfPath > 0.0001){
				    		totalDistance = totalDistance + distanceOfPath;
				    		Log.writePathLogToFile(Data.dEngagementId+"m", "GAPI distanceOfPath="+distanceOfPath+" and totalDistance="+totalDistance);
				    	}
				    	else{
				    		throw new Exception();
				    	}
				    } 
				    catch (Exception e) {
				    	e.printStackTrace();
				    	totalDistance = totalDistance + displacement;
				    	flagDistanceTravelled = 2;
				    	Log.writePathLogToFile(Data.dEngagementId+"m", "GAPI excep displacement="+displacement+" and totalDistance="+totalDistance);
				    }
				  Log.e("calculateFusedLocationDistance totalDistance ", "="+totalDistance);
				  
				  activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						DialogPopup.dismissLoadingDialog();
						driverEndRideAsync(activity, source, destination.latitude, destination.longitude, 0, rideTimeMinutes, flagDistanceTravelled, businessType);
					}
				});
			}
		}).start();
	}
	
	
	
		void cancelRidePopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.dialog_custom_two_buttons);

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
					}
				}).start();
				
		        stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
				
		        Intent intent = new Intent(HomeActivity.this, SplashNewActivity.class);
				intent.putExtra("no_anim", "yes");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		}
		if(hasFocus){
			buildAlertMessageNoGps();
		}
	}
	
	
	
	
	

	
	
	
	
	


	public void showAllRideRequestsOnMap(){
		
		try {
			if(userMode == UserMode.DRIVER){
				
				Log.e("driverRideRequests", "="+Data.driverRideRequests);
				
				map.clear();
			
				if(Data.driverRideRequests.size() > 0){
					
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
					
					Collections.sort(Data.driverRideRequests, new Comparator<DriverRideRequest>() {

						@Override
						public int compare(DriverRideRequest lhs, DriverRideRequest rhs) {
							if(myLocation != null){
								double distanceLhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), lhs.latLng);
								double distanceRhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), rhs.latLng);
								return (int) (distanceLhs - distanceRhs);
							}
							return 0;
						}
					});
					
					if(Data.driverRideRequests.size() > 1){
						driverNewRideRequestText.setText("You have "+Data.driverRideRequests.size()+" requests");
					}
					else{
						driverNewRideRequestText.setText("You have "+Data.driverRideRequests.size()+" request");
					}
					
					driverRequestListAdapter.notifyDataSetChanged();
					
					map.animateCamera(CameraUpdateFactory.newLatLng(last), 1000, null);
					
				}
				else{
					driverNewRideRequestRl.setVisibility(View.GONE);
					driverRideRequestsList.setVisibility(View.GONE);
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onNewRideRequest() {
		if(userMode == UserMode.DRIVER && (driverScreenMode == DriverScreenMode.D_INITIAL || driverScreenMode == DriverScreenMode.D_RIDE_END)){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dismissStationDataPopup();
					cancelStationPathUpdateTimer();
					showAllRideRequestsOnMap();
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
							showAllRideRequestsOnMap();
							initializeStationDataProcedure();
						}
					});
				}
				else if(userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(engagementId.equalsIgnoreCase(Data.dEngagementId)){
								DialogPopup.dismissLoadingDialog();
								dismissStationDataPopup();
								cancelStationPathUpdateTimer();
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								if(acceptedByOtherDriver){
									DialogPopup.alertPopup(HomeActivity.this, "", "This request has been accepted by other driver");
								}
								else{
									DialogPopup.alertPopup(HomeActivity.this, "", "User has canceled the request");
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
								dismissStationDataPopup();
								cancelStationPathUpdateTimer();
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								DialogPopup.alertPopup(HomeActivity.this, "", "User has canceled the request");
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
						showAllRideRequestsOnMap();
					}
				}
			});
		}
	}
	
	
	@Override
	public void onManualDispatchPushReceived() {
		try {
			if(userMode == UserMode.DRIVER ){
				dismissStationDataPopup();
				cancelStationPathUpdateTimer();
				callStateRestoreAPIOnManualPatchPushReceived();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	
	public void clearSPData() {

		SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");
		editor.putString(Data.SP_LAST_LOCATION_TIME, ""+System.currentTimeMillis());

		editor.commit();

		Database.getInstance(this).deleteSavedPath();
	}
	



	@Override
	public void onChangeStatePushReceived() {
		try{
			callAndHandleStateRestoreAPI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	static class FBLogoutNoIntent extends AsyncTask<Void, Void, String> {
		
		Activity act;
		
		public FBLogoutNoIntent(Activity act){
			this.act = act;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		protected String doInBackground(Void... urls) {
			try {
				try {	
					Session.getActiveSession().closeAndClearTokenInformation();		
				}
				catch(Exception e) {
					Log.v("Logout", "Error"+e);	
				}
			
				SharedPreferences pref = act.getSharedPreferences("myPref", 0);
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			} catch (Exception e) {
				Log.v("e", e.toString());

			}
			
			return "";
		}

		@Override
		protected void onPostExecute(String text) {
			
		}
	}

	public static void logoutUser(final Activity cont){
		try{
			
			new FBLogoutNoIntent(cont).execute();
			
			PicassoTools.clearCache(Picasso.with(cont));
			
			cont.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
					Editor editor = pref.edit();
					editor.clear();
					editor.commit();
					Data.clearDataOnLogout(cont);
					
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
	
	
	
	
	
	
	
	
	
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
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
		try {
//			if(((UserMode.PASSENGER == userMode) && (PassengerScreenMode.P_IN_RIDE != passengerScreenMode)) ||
//					((UserMode.DRIVER == userMode) && (DriverScreenMode.D_IN_RIDE != driverScreenMode))){
				if(highAccuracyLF == null){
					highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
				}
				highAccuracyLF.connect();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void destroyFusedLocationFetchers(){
		destroyHighAccuracyFusedLocationFetcher();
	}
	
	public void destroyHighAccuracyFusedLocationFetcher(){
		try{
			if(highAccuracyLF != null){
				highAccuracyLF.destroy();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public synchronized void onLocationChanged(Location location, int priority) {
//		if(((UserMode.PASSENGER == userMode) && (PassengerScreenMode.P_IN_RIDE != passengerScreenMode)) ||
//				((UserMode.DRIVER == userMode) && (DriverScreenMode.D_IN_RIDE != driverScreenMode) && (DriverScreenMode.D_START_RIDE != driverScreenMode))){
			if((Utils.compareDouble(location.getLatitude(), 0.0) != 0) && (Utils.compareDouble(location.getLongitude(), 0.0) != 0)){
				if(location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK){
					HomeActivity.myLocation = location;
					zoomToCurrentLocationAtFirstLocationFix(location);
				}
			}
//		}
	}
	
	
	public void zoomToCurrentLocationAtFirstLocationFix(Location location){
		try {
			if(map != null){
				if(!zoomedToMyLocation){
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
					zoomedToMyLocation = true;
					initializeStationDataProcedure();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					if(currentUserStatus != 0){
						String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
						if(resp.contains(HttpRequester.SERVER_TIMEOUT)){
							String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(resp1.contains(HttpRequester.SERVER_TIMEOUT)){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										DialogPopup.alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
									}
								});
							}
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
                            switchDriverScreen(driverScreenMode);
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
						if(currentUserStatus != 0){
							manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(manualPatchPushStateRestoreResponse.contains(HttpRequester.SERVER_TIMEOUT)){
								manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
								if(manualPatchPushStateRestoreResponse.contains(HttpRequester.SERVER_TIMEOUT)){
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											DialogPopup.alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
										}
									});
								}
							}
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogPopup.dismissLoadingDialog();
                                switchDriverScreen(driverScreenMode);
								
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
						DialogPopup.alertPopupWithListener(HomeActivity.this, "", "A pickup has been assigned to you. Please pick the customer.", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								GCMIntentService.stopRing();
								Database2.getInstance(HomeActivity.this).updateDriverManualPatchPushReceived(Database2.NO);
								manualPatchPushAckAPI(HomeActivity.this);
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ASync for acknowledging the server about manual patch push received
	 */
	public void manualPatchPushAckAPI(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", ""+Data.assignedCustomerInfo.userId);

			Log.i("server call", "=" + Data.SERVER_URL + "/acknowledge_manual_engagement");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("engagement_id", Data.dEngagementId);
			Log.i("customer_id", ""+Data.assignedCustomerInfo.userId);
		
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
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
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
	
	
	
	



    public void initializeStationDataProcedure(){
    	dismissStationDataPopup();
    	cancelStationPathUpdateTimer();
    	if(myLocation != null){
			if(checkDriverFree()){
				fetchStationDataAPI(HomeActivity.this);
			}
    	}
    }
    
	public void fetchStationDataAPI(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", ""+myLocation.getLatitude());
			params.put("longitude", ""+myLocation.getLongitude());
			
			Log.e("get_nearest_station", "=");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("latitude", ""+myLocation.getLatitude());
			Log.i("longitude", ""+myLocation.getLongitude());

			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/get_nearest_station", params,
					new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
									HomeActivity.logoutUser(activity);
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}
								else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
									String errorMessage = jObj.getString("error");
									DialogPopup.alertPopup(activity, "", errorMessage);
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}
								else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
									String message = jObj.getString("message");
									DialogPopup.alertPopup(activity, "", message);
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}
								else if(ApiResponseFlags.STATION_ASSIGNED.getOrdinal() == flag){
									if(checkDriverFree()){
										assignedStationData = new StationData(jObj.getString("station_id"), jObj.getDouble("latitude"), jObj.getDouble("longitude"), 
												DateOperations.utcToLocal(jObj.getString("arrival_time")), jObj.getString("address"), jObj.getString("message"), jObj.getDouble("radius"));
										displayStationDataPopup(activity);
										startStationPathUpdateTimer();
										SoundMediaPlayer.startSound(activity, R.raw.ring_new, 3, false, false);
									}
									else{
										SoundMediaPlayer.stopSound();
										GCMIntentService.clearNotifications(activity);
									}
								}
								else if(ApiResponseFlags.NO_STATION_ASSIGNED.getOrdinal() == flag){
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}
								else if(ApiResponseFlags.NO_STATION_AVAILABLE.getOrdinal() == flag){
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}
								else{
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									SoundMediaPlayer.stopSound();
									GCMIntentService.clearNotifications(activity);
								}

							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					});
		}
	}
    
	public boolean checkDriverFree(){
		return (UserMode.DRIVER == userMode && driverScreenMode == DriverScreenMode.D_INITIAL && Data.userData.autosAvailable == 1 && Data.driverRideRequests.size() == 0);
	}
	
	StationData assignedStationData;
	
	Dialog stationDataDialog;
	
	void displayStationDataPopup(final Activity activity) {
		try {
			dismissStationDataPopup();
			stationDataDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			stationDataDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			stationDataDialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) stationDataDialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = stationDataDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			stationDataDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			stationDataDialog.setCancelable(false);
			stationDataDialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) stationDataDialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) stationDataDialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			
			textMessage.setText(assignedStationData.message);
			
			Button btnOk = (Button) stationDataDialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					stationDataDialog.dismiss();
					if(!assignedStationData.acknowledgeDone){
						acknowledgeStationDataReadAPI(activity, assignedStationData.stationId);
					}
					SoundMediaPlayer.stopSound();
				}
			});

			stationDataDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dismissStationDataPopup(){
		try {
			if(stationDataDialog != null){
				stationDataDialog.dismiss();
				stationDataDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void acknowledgeStationDataReadAPI(final Activity activity, final String stationId) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			params.put("access_token", Data.userData.accessToken);
			params.put("station_id", stationId);
			
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("station_id", "=" + stationId);

			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/acknowledge_stationing", params,
					new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
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
									DialogPopup.alertPopup(activity, "", errorMessage);
								}
								else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
									String message = jObj.getString("message");
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
									if(assignedStationData != null){
										assignedStationData.acknowledgeDone = true;
									}
								}
								else{
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}

							} catch (Exception exception) {
								exception.printStackTrace();
							}
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
	}
	
	
	
	int stationPathUpdaterRunCount;
	Timer timerStationPathUpdater;
	TimerTask timerTaskStationPathUpdater;
	
	public void startStationPathUpdateTimer(){
		cancelStationPathUpdateTimer();
		try {
			timerStationPathUpdater = new Timer();
			
			timerTaskStationPathUpdater = new TimerTask() {
				
				@Override
				public void run() {
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
							if(myLocation != null){
								if(assignedStationData != null){
									if(checkDriverFree()){
										
										Calendar currentCalendar = Calendar.getInstance();
										if(currentCalendar.getTimeInMillis() > assignedStationData.timeCalendar.getTimeInMillis() && stationPathUpdaterRunCount == 1){ // start showing alerts
											stationPathUpdaterRunCount = 10;
										}
										
										Log.e("stationPathUpdaterRunCount", "="+stationPathUpdaterRunCount);
										
										final LatLng source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
										double distanceToReachToStation = MapUtils.distance(source, assignedStationData.latLng);
										Log.e("distanceToReachToStation", "="+distanceToReachToStation);
										if(distanceToReachToStation > assignedStationData.toleranceDistance){
											if(stationPathUpdaterRunCount % 10 == 0){
												runOnUiThread(new Runnable() {
													
													@Override
													public void run() {
														if(checkDriverFree()){
															SoundMediaPlayer.startSound(HomeActivity.this, R.raw.ring_new, 3, false, false);
															displayStationDataPopup(HomeActivity.this);
														}
													}
												});
											}
											Log.e("currentCalendar", "="+currentCalendar.getTime());
											Log.i("assignedStationData.timeCalendar", "="+assignedStationData.timeCalendar.getTime());
											if(currentCalendar.getTimeInMillis() > assignedStationData.timeCalendar.getTimeInMillis()){ // start showing alerts
												stationPathUpdaterRunCount++;
											}
											
											runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													if(checkDriverFree()){
														textViewDriverInfo.setVisibility(View.VISIBLE);
														textViewDriverInfo.setText("Please reach " + assignedStationData.address + " by " + DateOperations.getTimeAMPM(assignedStationData.time));
													}
												}
											});
											
											String url = MapUtils.makeDirectionsURL(source, assignedStationData.latLng);
											String result = new HttpRequester().getJSONFromUrl(url);
											
											if(checkDriverFree()){
												if(result != null && !result.contains(HttpRequester.SERVER_TIMEOUT)){
													
													final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
													
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															try {
																if(checkDriverFree()){
																	if(assignedStationData != null){
																		map.clear();
																		MarkerOptions markerOptionsStationLocation = new MarkerOptions();
																		markerOptionsStationLocation.title("station_marker");
																		markerOptionsStationLocation.snippet(assignedStationData.address);
																		markerOptionsStationLocation.position(assignedStationData.latLng);
																		markerOptionsStationLocation.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
																		
																		Marker stationMarker = map.addMarker(markerOptionsStationLocation);
																		CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, stationMarker.getSnippet(), "");
																		map.setInfoWindowAdapter(customIW);
																		stationMarker.showInfoWindow();
																		
																		try {
																			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
																			boundsBuilder.include(source).include(assignedStationData.latLng);
																			LatLngBounds bounds = boundsBuilder.build();
																			map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 140));
																		} catch (Exception e) {
																			e.printStackTrace();
																		}
																		
																		CircleOptions circleOptionsStationLocation = new CircleOptions();
																		circleOptionsStationLocation.center(assignedStationData.latLng);
																		circleOptionsStationLocation.fillColor(Color.parseColor("#330000FF"));
																		circleOptionsStationLocation.radius(assignedStationData.toleranceDistance);
																		circleOptionsStationLocation.strokeWidth(0);
																		circleOptionsStationLocation.strokeColor(Color.parseColor("#330000FF"));
																		map.addCircle(circleOptionsStationLocation);
																		
																		
																		for(int z = 0; z<list.size()-1; z++){
																		    LatLng src= list.get(z);
																		    LatLng dest= list.get(z+1);
																		    map.addPolyline(new PolylineOptions()
																		    .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
																		    .width(5)
																		    .color(DRIVER_TO_STATION_MAP_PATH_COLOR).geodesic(true));
																		}
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
										else{
											runOnUiThread(new Runnable() {
												
												@Override
												public void run() {
													if(checkDriverFree()){
														stationPathUpdaterRunCount = 1;
														map.clear();
														MarkerOptions markerOptionsStationLocation = new MarkerOptions();
														markerOptionsStationLocation.title("station_marker");
														markerOptionsStationLocation.snippet(assignedStationData.address);
														markerOptionsStationLocation.position(assignedStationData.latLng);
														markerOptionsStationLocation.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
														
														Marker stationMarker = map.addMarker(markerOptionsStationLocation);
														CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, stationMarker.getSnippet(), "");
														map.setInfoWindowAdapter(customIW);
														stationMarker.showInfoWindow();
														
														CircleOptions circleOptionsStationLocation = new CircleOptions();
														circleOptionsStationLocation.center(assignedStationData.latLng);
														circleOptionsStationLocation.fillColor(Color.parseColor("#330000FF"));
														circleOptionsStationLocation.radius(assignedStationData.toleranceDistance);
														circleOptionsStationLocation.strokeWidth(0);
														circleOptionsStationLocation.strokeColor(Color.parseColor("#330000FF"));
														map.addCircle(circleOptionsStationLocation);
														
														textViewDriverInfo.setVisibility(View.VISIBLE);
														textViewDriverInfo.setText("Wait here...");
													}
													SoundMediaPlayer.stopSound();
													dismissStationDataPopup();
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
			timerStationPathUpdater.scheduleAtFixedRate(timerTaskStationPathUpdater, 10, 60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelStationPathUpdateTimer(){
		try{
			if(timerTaskStationPathUpdater != null){
				timerTaskStationPathUpdater.cancel();
			}
			if(timerStationPathUpdater != null){
				timerStationPathUpdater.cancel();
				timerStationPathUpdater.purge();
				
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			timerTaskStationPathUpdater = null;
			timerStationPathUpdater = null;
			stationPathUpdaterRunCount = 1;
		}
		textViewDriverInfo.setVisibility(View.GONE);
		SoundMediaPlayer.stopSound();
	}
	
	
	@Override
	public void onStationChangedPushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initializeStationDataProcedure();
			}
		});
		
	}
	
	@Override
	public void onCustomerCashDone() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				driverScreenMode = DriverScreenMode.D_INITIAL;
				switchDriverScreen(driverScreenMode);
			}
		});
	}
	
	
	@Override
	public void onCashAddedToWalletByCustomer(final int userId, final double balance) {
		if(Data.assignedCustomerInfo != null){
			if(Data.assignedCustomerInfo.userId == userId){
				if(BusinessType.AUTOS == Data.assignedCustomerInfo.businessType){
					((AutoCustomerInfo)Data.assignedCustomerInfo).jugnooBalance = balance;
				}
			}
		}
	}
	
	
	@Override
	public void updateMeteringUI(final double distance, final long elapsedTime, final Location lastGPSLocation, final Location lastFusedLocation) {
		if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
			totalDistance = distance;
			HomeActivity.this.lastGPSLocation = lastGPSLocation;
			HomeActivity.this.lastFusedLocation = lastFusedLocation;
			HomeActivity.this.distanceUpdateFromService = true;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					updateDistanceFareTexts(distance, elapsedTime);
					if(rideStartPositionMarker == null){
						displayOldPath();
					}
				}
			});
		}
	}
	
	@Override
	public void drawOldPath() {
		runOnUiThread(new Runnable() {
				
			@Override
			public void run() {
				if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
					displayOldPath();
				}
			}
		});
	}
	
	@Override
	public void addPathToMap(final PolylineOptions polylineOptions) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode){
					if(Color.TRANSPARENT != MAP_PATH_COLOR){
						map.addPolyline(polylineOptions.width(5).color(MAP_PATH_COLOR).geodesic(true));
					}
				}
			}
		});
	}
	
}