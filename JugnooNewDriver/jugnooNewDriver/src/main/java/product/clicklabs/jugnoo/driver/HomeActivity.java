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
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.AppMode;
import product.clicklabs.jugnoo.driver.datastructure.AutoCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.BenefitType;
import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.CouponType;
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.datastructure.DriverRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EndRideData;
import product.clicklabs.jugnoo.driver.datastructure.FatafatCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatDeliveryInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatOrderInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.FlagRideStatus;
import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.datastructure.MealRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.PendingCall;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.PromotionType;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.StationData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.AGPSRefresh;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.PausableChronometer;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("DefaultLocale")
public class HomeActivity extends FragmentActivity implements AppInterruptHandler, LocationUpdate, GPSLocationUpdate, FlurryEventNames, OnMapReadyCallback, Constants {


	private final String TAG = HomeActivity.class.getSimpleName();

	DrawerLayout drawerLayout;                                                                        // views declaration


	//menu bar
	LinearLayout menuLayout;


	ImageView profileImg, seprator;
	TextView userName, ratingValue;
	LinearLayout linearLayoutDEI, driverImageRL, linearLayout_DEI;

	RelativeLayout relativeLayoutAutosOn, relativeLayoutMealsOn, relativeLayoutFatafatOn, relativeLayoutSharingOn;
	ImageView imageViewAutosOnToggle, imageViewMealsOnToggle, imageViewFatafatOnToggle, imageViewSharingOnToggle;

	RelativeLayout inviteFriendRl, driverRatingRl, notificationCenterRl;
	TextView inviteFriendText, notificationCenterText;

	RelativeLayout bookingsRl, RelativeLayoutNotificationCenter;
	TextView bookingsText, etaTimerText;

	RelativeLayout relativeLayoutSharingRides;

	RelativeLayout fareDetailsRl;
	TextView fareDetailsText;
	RelativeLayout relativeLayoutSuperDrivers;

	RelativeLayout callUsRl;
	TextView callUsText;

	RelativeLayout paytmRecharge;
	TextView paytmRechargeText;

	RelativeLayout languagePrefrencesRl;
	TextView languagePrefrencesText;

	RelativeLayout logoutRl;
	TextView logoutText;
	HeatMapResponse heatMapResponseGlobal;


	//Top RL
	RelativeLayout topRl;
	Button menuBtn;
	Button checkServerBtn;
	ImageView imageViewTitleBarDEI;
	TextView textViewTitleBarDEI;
//	ProgressBar progressBarDriverOnlineHours;


	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;


	// Driver main layout
	RelativeLayout driverMainLayout;


	//Driver initial layout
	RelativeLayout driverInitialLayout;
	TextView textViewDriverInfo;
	ListView driverRideRequestsList;
	Button driverInitialMyLocationBtn;
	RelativeLayout jugnooOffLayout;
	TextView jugnooOffText;

	DriverRequestListAdapter driverRequestListAdapter;


	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	TextView textViewBeforeAcceptRequestInfo;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn, buttonDriverNavigation;


	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;

	TextView driverPassengerName, textViewCustomerPickupAddress, textViewAfterAcceptRequestInfo, textViewAfterAcceptAmount, textViewInRideFareFactor;
	TextView driverPassengerRatingValue;
	RelativeLayout driverPassengerCallRl;
	TextView driverPassengerCallText;
	TextView driverScheduledRideText;
	ImageView driverFreeRideIcon;
	Button driverEngagedMyLocationBtn;

	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideBtn, buttonMarkArrived;
	Button driverCancelRideBtn;


	//In ride layout
	LinearLayout driverInRideMainRl;
	TextView driverIRDistanceText, driverIRDistanceValue, driverIRFareText, driverIRFareValue,
			driverRideTimeText, driverWaitText, driverWaitValue;
	PausableChronometer rideTimeChronometer;
	RelativeLayout driverWaitRl;
	ImageView imageViewIRWaitSep;
	RelativeLayout inrideFareInfoRl;
	TextView inrideMinFareText, inrideMinFareValue, inrideFareAfterText, inrideFareAfterValue, textViewInRideConvenienceCharges;
	Button inrideFareInfoBtn;
	Button driverEndRideBtn;

	public static int waitStart = 2;
	double distanceAfterWaitStarted = 0;
	int lastLogId = 0;


	//Review layout
	RelativeLayout endRideReviewRl;

	LinearLayout reviewReachedDistanceRl;
	LinearLayout linearLayoutMeterFare;
	TextView reviewReachedDestinationText,
			reviewDistanceText, reviewDistanceValue,
			reviewWaitText, reviewWaitValue, reviewRideTimeText, reviewRideTimeValue,
			reviewFareText, reviewFareValue;
	RelativeLayout reviewWaitTimeRl;
	ImageView imageViewEndRideWaitSep;

	LinearLayout linearLayoutMeterFareEditText;
	TextView textViewMeterFareRupee;
	EditText editTextEnterMeterFare;

	RelativeLayout relativeLayoutEndRideLuggageCount;
	ImageView imageViewEndRideLuggageCountPlus, imageViewEndRideLuggageCountMinus;
	TextView textViewEndRideLuggageCount;

	RelativeLayout relativeLayoutUseJugnooFare;
	RelativeLayout relativeLayoutJugnooCalculatedFare;
	TextView textViewCalculatedDistance, textViewCalculatedTime, textViewCalculatedFare;

	RelativeLayout relativeLayoutEndRideCustomerAmount;

	LinearLayout endRideInfoRl;
	TextView jugnooRideOverText, takeFareText;

	RelativeLayout relativeLayoutCoupon;
	TextView textViewCouponTitle, textViewCouponSubTitle, textViewCouponPayTakeText, textViewCouponDiscountedFare;

	RelativeLayout relativeLayoutFatafatCustomerAmount;
	LinearLayout linearLayoutFatafatBill;
	TextView textViewFatafatBillAmountValue, textViewFatafatBillDiscountValue,
			textViewFatafatBillFinalAmountValue, textViewFatafatBillJugnooCashValue, textViewFatafatBillToPay;

	Button reviewSubmitBtn;
	RelativeLayout relativeLayoutRateCustomer;
	RatingBar ratingBarFeedback, ratingBarFeedbackSide;
	Button reviewSkipBtn;
	RelativeLayout reviewFareInfoInnerRl;
	TextView reviewMinFareText, reviewMinFareValue, reviewFareAfterText, reviewFareAfterValue, textViewReviewConvenienceCharges;
	Button reviewFareInfoBtn;

	ScrollView scrollViewEndRide;
	LinearLayout linearLayoutEndRideMain;
	TextView textViewScroll, textViewNotificationValue;


	// data variables declaration


	Location lastLocation;
	long lastLocationTime;

	public String language = "";


	DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));

	static double totalDistance = -1, totalFare = 0, totalHaversineDistance = -1;
	static long totalWaitTime = 0;
	long fetchHeatMapTime = 0;

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
	boolean dontCallRefreshDriver = false, resumed = false;
	int fareFetchedFromJugnoo = 0;
	int luggageCountAdded = 0;


	AlertDialog gpsDialogAlert;

	LocationFetcher highAccuracyLF;


	//TODO check final variables
	public static AppMode appMode;

	public static final int MAP_PATH_COLOR = Color.TRANSPARENT;
	public static final int D_TO_C_MAP_PATH_COLOR = Color.BLUE;
	public static final int DRIVER_TO_STATION_MAP_PATH_COLOR = Color.BLUE;

	public static final long DRIVER_START_RIDE_CHECK_METERS = 600; //in meters

	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000; //in milliseconds

	public static final float HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
	public CountDownTimer timer = null;


	public static final long MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT = 10 * 60000; //in milliseconds


	public ASSL assl;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_home);

//        String languageToLoad = "hi";
//        Locale locale = new Locale(languageToLoad);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//            getBaseContext().getResources().getDisplayMetrics());


			decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
			decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));


			initializeGPSForegroundLocationFetcher();

			HomeActivity.appInterruptHandler = HomeActivity.this;

			activity = this;

			loggedOut = false;
			zoomedToMyLocation = false;
			dontCallRefreshDriver = false;
			mapTouchedOnce = false;
			resumed = false;

			appMode = AppMode.NORMAL;

			language = Locale.getDefault().getLanguage();

//        String language = Locale.getDefault().getLanguage()       ;//---> en
//        String iso3Language = Locale.getDefault().getISO3Language()   ;//---> eng
//        String country = Locale.getDefault().getCountry()        ;//---> US
//        String iso3Country = Locale.getDefault().getISO3Country()    ;//---> USA
//        String displayCountry = Locale.getDefault().getDisplayCountry(); //---> United States
//        String displayName = Locale.getDefault().getDisplayName() ;   //---> English (United States)
//        String toString = Locale.getDefault().toString()    ;      //---> en_US
//        String displayLanguage = Locale.getDefault().getDisplayLanguage();//---> English
//
//        String localeInfo = language + " " + iso3Language + " " + country + " " + iso3Country + " " + displayCountry + " " + displayName + " " + toString + " " + displayLanguage;
//
//        Log.e("Locale info", "="+localeInfo);


			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
			drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


			assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);


			Log.e("home", "oncreate");


			//Swipe menu
			menuLayout = (LinearLayout) findViewById(R.id.menuLayout);


			profileImg = (ImageView) findViewById(R.id.profileImg);
			seprator = (ImageView) findViewById(R.id.seprator);
			userName = (TextView) findViewById(R.id.userName);
			ratingValue = (TextView) findViewById(R.id.ratingValue);
			userName.setTypeface(Data.latoRegular(getApplicationContext()));
			ratingValue.setTypeface(Data.latoRegular(getApplicationContext()));

			linearLayoutDEI = (LinearLayout) findViewById(R.id.linearLayoutDEI);
			linearLayout_DEI = (LinearLayout) findViewById(R.id.linearLayout_DEI);
//			RelativeLayoutNotificationCenter = (RelativeLayout) findViewById(R.id.RelativeLayoutNotificationCenter);
//			textViewDEI = (TextView) findViewById(R.id.textViewDEI);
//			textViewDEI.setTypeface(Data.latoRegular(this));

			relativeLayoutAutosOn = (RelativeLayout) findViewById(R.id.relativeLayoutAutosOn);
			((TextView) findViewById(R.id.textViewAutosOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewAutosOnToggle = (ImageView) findViewById(R.id.imageViewAutosOnToggle);

			relativeLayoutMealsOn = (RelativeLayout) findViewById(R.id.relativeLayoutMealsOn);
			((TextView) findViewById(R.id.textViewMealsOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewMealsOnToggle = (ImageView) findViewById(R.id.imageViewMealsOnToggle);

			relativeLayoutFatafatOn = (RelativeLayout) findViewById(R.id.relativeLayoutFatafatOn);
			((TextView) findViewById(R.id.textViewFatafatOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewFatafatOnToggle = (ImageView) findViewById(R.id.imageViewFatafatOnToggle);

			relativeLayoutSharingOn = (RelativeLayout) findViewById(R.id.relativeLayoutSharingOn);
			((TextView) findViewById(R.id.textViewSharingOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewSharingOnToggle = (ImageView) findViewById(R.id.imageViewSharingOnToggle);


			inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
			driverRatingRl = (RelativeLayout) findViewById(R.id.driverRatingRl);
			inviteFriendText = (TextView) findViewById(R.id.inviteFriendText);
			inviteFriendText.setTypeface(Data.latoRegular(getApplicationContext()));

			notificationCenterRl = (RelativeLayout) findViewById(R.id.notificationCenterRl);
			notificationCenterText = (TextView) findViewById(R.id.notificationCenterText);
			notificationCenterText.setTypeface(Data.latoRegular(getApplicationContext()));

			bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
			RelativeLayoutNotificationCenter = (RelativeLayout) findViewById(R.id.RelativeLayoutNotificationCenter);
			bookingsText = (TextView) findViewById(R.id.bookingsText);
			bookingsText.setTypeface(Data.latoRegular(getApplicationContext()));

			etaTimerText = (TextView) findViewById(R.id.ETATimerText);
			etaTimerText.setTypeface(Data.digitalRegular(getApplicationContext()));

			relativeLayoutSharingRides = (RelativeLayout) findViewById(R.id.relativeLayoutSharingRides);
			((TextView) findViewById(R.id.textViewSharingRides)).setTypeface(Data.latoRegular(this));

			fareDetailsRl = (RelativeLayout) findViewById(R.id.fareDetailsRl);
			driverImageRL = (LinearLayout) findViewById(R.id.driverImageRL);
			fareDetailsText = (TextView) findViewById(R.id.fareDetailsText);
			fareDetailsText.setTypeface(Data.latoRegular(getApplicationContext()));

			relativeLayoutSuperDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutSuperDrivers);
			((TextView) findViewById(R.id.textViewSuperDrivers)).setTypeface(Data.latoRegular(this));

			callUsRl = (RelativeLayout) findViewById(R.id.callUsRl);
			callUsText = (TextView) findViewById(R.id.callUsText);
			callUsText.setTypeface(Data.latoRegular(getApplicationContext()));

			paytmRecharge = (RelativeLayout) findViewById(R.id.paytmRecharge);
			paytmRechargeText = (TextView) findViewById(R.id.paytmRechargeText);
			paytmRechargeText.setTypeface(Data.latoRegular(getApplicationContext()));

			languagePrefrencesRl = (RelativeLayout) findViewById(R.id.languagePrefrencesRl);
			languagePrefrencesText = (TextView) findViewById(R.id.languagePrefrencesText);
			languagePrefrencesText.setTypeface(Data.latoRegular(getApplicationContext()));

			logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
			logoutText = (TextView) findViewById(R.id.logoutText);
			logoutText.setTypeface(Data.latoRegular(getApplicationContext()));


//		driverProfileText = (TextView) findViewById(R.id.driverProfileText); driverProfileText.setTypeface(Data.latoRegular(getApplicationContext()));


			//Top RL
			topRl = (RelativeLayout) findViewById(R.id.topRl);
			menuBtn = (Button) findViewById(R.id.menuBtn);
			checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
			imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);
//			progressBarDriverOnlineHours = (ProgressBar) findViewById(R.id.progressBarDriverOnlineHours);
			textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
			textViewTitleBarDEI.setTypeface(Data.latoRegular(this));
//			textViewTitleBarOvalText = (TextView) findViewById(R.id.textViewTitleBarOvalText);
//			textViewTitleBarOvalText.setTypeface(Data.latoRegular(this));
			textViewNotificationValue = (TextView) findViewById(R.id.textViewNotificationValue);
			textViewNotificationValue.setTypeface(Data.latoRegular(this));
			textViewNotificationValue.setVisibility(View.GONE);



			menuBtn.setVisibility(View.VISIBLE);


			//Map Layout
			mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);


			// Driver main layout
			driverMainLayout = (RelativeLayout) findViewById(R.id.driverMainLayout);


			//Driver initial layout
			driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
			textViewDriverInfo = (TextView) findViewById(R.id.textViewDriverInfo);
			textViewDriverInfo.setTypeface(Data.latoRegular(getApplicationContext()));
			driverRideRequestsList = (ListView) findViewById(R.id.driverRideRequestsList);
			driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
			jugnooOffLayout = (RelativeLayout) findViewById(R.id.jugnooOffLayout);
			jugnooOffText = (TextView) findViewById(R.id.jugnooOffText);
			jugnooOffText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverRideRequestsList.setVisibility(View.GONE);

			driverRequestListAdapter = new DriverRequestListAdapter();
			driverRideRequestsList.setAdapter(driverRequestListAdapter);


			// Driver Request Accept layout
			driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
			textViewBeforeAcceptRequestInfo = (TextView) findViewById(R.id.textViewBeforeAcceptRequestInfo);
			textViewBeforeAcceptRequestInfo.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
			driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn);
			driverAcceptRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn);
			driverCancelRequestBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
			buttonDriverNavigation = (Button) findViewById(R.id.buttonDriverNavigation);


			// Driver engaged layout
			driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);


			driverPassengerName = (TextView) findViewById(R.id.driverPassengerName);
			driverPassengerName.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewCustomerPickupAddress = (TextView) findViewById(R.id.textViewCustomerPickupAddress);
			textViewCustomerPickupAddress.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewAfterAcceptRequestInfo = (TextView) findViewById(R.id.textViewAfterAcceptRequestInfo);
			textViewAfterAcceptRequestInfo.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewAfterAcceptAmount = (TextView) findViewById(R.id.textViewAfterAcceptAmount);
			textViewAfterAcceptAmount.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor);
			textViewInRideFareFactor.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);


			driverPassengerRatingValue = (TextView) findViewById(R.id.driverPassengerRatingValue);
			driverPassengerRatingValue.setTypeface(Data.latoRegular(getApplicationContext()));
			driverPassengerCallRl = (RelativeLayout) findViewById(R.id.driverPassengerCallRl);
			driverPassengerCallText = (TextView) findViewById(R.id.driverPassengerCallText);
			driverPassengerCallText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverScheduledRideText = (TextView) findViewById(R.id.driverScheduledRideText);
			driverScheduledRideText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverFreeRideIcon = (ImageView) findViewById(R.id.driverFreeRideIcon);
			driverEngagedMyLocationBtn = (Button) findViewById(R.id.driverEngagedMyLocationBtn);

			driverPassengerRatingValue.setVisibility(View.GONE);

			//Start ride layout
			driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
			driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn);
			driverStartRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			buttonMarkArrived = (Button) findViewById(R.id.buttonMarkArrived);
			buttonMarkArrived.setTypeface(Data.latoRegular(this));
			driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn);
			driverCancelRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));


			//In ride layout
			driverInRideMainRl = (LinearLayout) findViewById(R.id.driverInRideMainRl);

			driverIRDistanceText = (TextView) findViewById(R.id.driverIRDistanceText);
			driverIRDistanceText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverIRDistanceValue = (TextView) findViewById(R.id.driverIRDistanceValue);
			driverIRDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverIRFareText = (TextView) findViewById(R.id.driverIRFareText);
			driverIRFareText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverIRFareValue = (TextView) findViewById(R.id.driverIRFareValue);
			driverIRFareValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverRideTimeText = (TextView) findViewById(R.id.driverRideTimeText);
			driverRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()));
			rideTimeChronometer = (PausableChronometer) findViewById(R.id.rideTimeChronometer);
			rideTimeChronometer.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverWaitRl = (RelativeLayout) findViewById(R.id.driverWaitRl);
			driverWaitText = (TextView) findViewById(R.id.driverWaitText);
			driverWaitText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverWaitValue = (TextView) findViewById(R.id.driverWaitValue);
			driverWaitValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			imageViewIRWaitSep = (ImageView) findViewById(R.id.imageViewIRWaitSep);

			inrideFareInfoRl = (RelativeLayout) findViewById(R.id.inrideFareInfoRl);
			inrideMinFareText = (TextView) findViewById(R.id.inrideMinFareText);
			inrideMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			inrideMinFareValue = (TextView) findViewById(R.id.inrideMinFareValue);
			inrideMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
			inrideFareAfterText = (TextView) findViewById(R.id.inrideFareAfterText);
			inrideFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
			inrideFareAfterValue = (TextView) findViewById(R.id.inrideFareAfterValue);
			inrideFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewInRideConvenienceCharges = (TextView) findViewById(R.id.textViewInRideConvenienceCharges);
			textViewInRideConvenienceCharges.setTypeface(Data.latoRegular(this));
			textViewInRideConvenienceCharges.setVisibility(View.GONE);
			inrideFareInfoBtn = (Button) findViewById(R.id.inrideFareInfoBtn);

			driverWaitRl.setVisibility(View.GONE);
			imageViewIRWaitSep.setVisibility(View.GONE);

			driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn);
			driverEndRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			waitStart = 2;


			rideTimeChronometer.setText("00:00:00");
			driverWaitValue.setText("00:00:00");


			//Review Layout
			endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);

			reviewReachedDestinationText = (TextView) findViewById(R.id.reviewReachedDestinationText);
			reviewReachedDestinationText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText);
			reviewDistanceText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue);
			reviewDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewWaitText = (TextView) findViewById(R.id.reviewWaitText);
			reviewWaitText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewWaitValue = (TextView) findViewById(R.id.reviewWaitValue);
			reviewWaitValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewRideTimeText = (TextView) findViewById(R.id.reviewRideTimeText);
			reviewRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewRideTimeValue = (TextView) findViewById(R.id.reviewRideTimeValue);
			reviewRideTimeValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewFareText = (TextView) findViewById(R.id.reviewFareText);
			reviewFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewFareValue = (TextView) findViewById(R.id.reviewFareValue);
			reviewFareValue.setTypeface(Data.latoRegular(getApplicationContext()));

			reviewWaitTimeRl = (RelativeLayout) findViewById(R.id.reviewWaitTimeRl);
			imageViewEndRideWaitSep = (ImageView) findViewById(R.id.imageViewEndRideWaitSep);
			reviewWaitTimeRl.setVisibility(View.GONE);
			imageViewEndRideWaitSep.setVisibility(View.GONE);


			reviewReachedDistanceRl = (LinearLayout) findViewById(R.id.reviewReachedDistanceRl);

			linearLayoutMeterFare = (LinearLayout) findViewById(R.id.linearLayoutMeterFare);
			((TextView) findViewById(R.id.textViewEnterMeterFare)).setTypeface(Data.latoRegular(this), Typeface.BOLD);

			linearLayoutMeterFareEditText = (LinearLayout) findViewById(R.id.linearLayoutMeterFareEditText);
			textViewMeterFareRupee = (TextView) findViewById(R.id.textViewMeterFareRupee);
			textViewMeterFareRupee.setTypeface(Data.latoRegular(this), Typeface.BOLD);
			textViewMeterFareRupee.setVisibility(View.GONE);
			editTextEnterMeterFare = (EditText) findViewById(R.id.editTextEnterMeterFare);
			editTextEnterMeterFare.setTypeface(Data.latoRegular(this), Typeface.BOLD);

			relativeLayoutEndRideLuggageCount = (RelativeLayout) findViewById(R.id.relativeLayoutEndRideLuggageCount);
			relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
			imageViewEndRideLuggageCountPlus = (ImageView) findViewById(R.id.imageViewEndRideLuggageCountPlus);
			imageViewEndRideLuggageCountMinus = (ImageView) findViewById(R.id.imageViewEndRideLuggageCountMinus);
			textViewEndRideLuggageCount = (TextView) findViewById(R.id.textViewEndRideLuggageCount);
			textViewEndRideLuggageCount.setTypeface(Data.latoRegular(this));

			relativeLayoutUseJugnooFare = (RelativeLayout) findViewById(R.id.relativeLayoutUseJugnooFare);
			((TextView) findViewById(R.id.textViewUseJugnooFare)).setTypeface(Data.latoRegular(this));
			relativeLayoutJugnooCalculatedFare = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCalculatedFare);
			textViewCalculatedDistance = (TextView) findViewById(R.id.textViewCalculatedDistance);
			textViewCalculatedDistance.setTypeface(Data.latoRegular(this));
			textViewCalculatedTime = (TextView) findViewById(R.id.textViewCalculatedTime);
			textViewCalculatedTime.setTypeface(Data.latoRegular(this));
			textViewCalculatedFare = (TextView) findViewById(R.id.textViewCalculatedFare);
			textViewCalculatedFare.setTypeface(Data.latoRegular(this));


			relativeLayoutEndRideCustomerAmount = (RelativeLayout) findViewById(R.id.relativeLayoutEndRideCustomerAmount);


			endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
			jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText);
			jugnooRideOverText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			takeFareText = (TextView) findViewById(R.id.takeFareText);
			takeFareText.setTypeface(Data.latoRegular(getApplicationContext()));

			reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn);
			reviewSubmitBtn.setTypeface(Data.latoRegular(getApplicationContext()));

			relativeLayoutRateCustomer = (RelativeLayout) findViewById(R.id.relativeLayoutRateCustomer);
			((TextView) findViewById(R.id.textViewRateYourCustomer)).setTypeface(Data.latoRegular(this));
			ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
			ratingBarFeedbackSide = (RatingBar) findViewById(R.id.ratingBarFeedbackSide);
			ratingBarFeedbackSide.setEnabled(false);
			reviewSkipBtn = (Button) findViewById(R.id.reviewSkipBtn);
			reviewSkipBtn.setTypeface(Data.latoRegular(this));


			relativeLayoutCoupon = (RelativeLayout) findViewById(R.id.relativeLayoutCoupon);
			textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle);
			textViewCouponTitle.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
			textViewCouponSubTitle = (TextView) findViewById(R.id.textViewCouponSubTitle);
			textViewCouponSubTitle.setTypeface(Data.museoSlab(getApplicationContext()));
			textViewCouponPayTakeText = (TextView) findViewById(R.id.textViewCouponPayTakeText);
			textViewCouponPayTakeText.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);
			textViewCouponDiscountedFare = (TextView) findViewById(R.id.textViewCouponDiscountedFare);
			textViewCouponDiscountedFare.setTypeface(Data.museoSlab(getApplicationContext()), Typeface.BOLD);

			relativeLayoutFatafatCustomerAmount = (RelativeLayout) findViewById(R.id.relativeLayoutFatafatCustomerAmount);
			linearLayoutFatafatBill = (LinearLayout) findViewById(R.id.linearLayoutFatafatBill);
			textViewFatafatBillAmountValue = (TextView) findViewById(R.id.textViewFatafatBillAmountValue);
			textViewFatafatBillAmountValue.setTypeface(Data.latoRegular(this));
			textViewFatafatBillDiscountValue = (TextView) findViewById(R.id.textViewFatafatBillDiscountValue);
			textViewFatafatBillDiscountValue.setTypeface(Data.latoRegular(this));
			textViewFatafatBillFinalAmountValue = (TextView) findViewById(R.id.textViewFatafatBillFinalAmountValue);
			textViewFatafatBillFinalAmountValue.setTypeface(Data.latoRegular(this));
			textViewFatafatBillJugnooCashValue = (TextView) findViewById(R.id.textViewFatafatBillJugnooCashValue);
			textViewFatafatBillJugnooCashValue.setTypeface(Data.latoRegular(this));
			textViewFatafatBillToPay = (TextView) findViewById(R.id.textViewFatafatBillToPay);
			textViewFatafatBillToPay.setTypeface(Data.latoRegular(this), Typeface.BOLD);

			((TextView) findViewById(R.id.textViewFatafatBillAmount)).setTypeface(Data.latoRegular(this));
			((TextView) findViewById(R.id.textViewFatafatBillDiscount)).setTypeface(Data.latoRegular(this));
			((TextView) findViewById(R.id.textViewFatafatBillFinalAmount)).setTypeface(Data.latoRegular(this));
			((TextView) findViewById(R.id.textViewFatafatBillJugnooCash)).setTypeface(Data.latoRegular(this));
			((TextView) findViewById(R.id.textViewFatafatBillTake)).setTypeface(Data.latoRegular(this));


			reviewMinFareText = (TextView) findViewById(R.id.reviewMinFareText);
			reviewMinFareText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewMinFareValue = (TextView) findViewById(R.id.reviewMinFareValue);
			reviewMinFareValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewFareAfterText = (TextView) findViewById(R.id.reviewFareAfterText);
			reviewFareAfterText.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewFareAfterValue = (TextView) findViewById(R.id.reviewFareAfterValue);
			reviewFareAfterValue.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewReviewConvenienceCharges = (TextView) findViewById(R.id.textViewReviewConvenienceCharges);
			textViewReviewConvenienceCharges.setTypeface(Data.latoRegular(this));
			textViewReviewConvenienceCharges.setVisibility(View.GONE);
			reviewFareInfoBtn = (Button) findViewById(R.id.reviewFareInfoBtn);
			reviewFareInfoInnerRl = (RelativeLayout) findViewById(R.id.reviewFareInfoInnerRl);

			scrollViewEndRide = (ScrollView) findViewById(R.id.scrollViewEndRide);
			linearLayoutEndRideMain = (LinearLayout) findViewById(R.id.linearLayoutEndRideMain);
			textViewScroll = (TextView) findViewById(R.id.textViewScroll);
			linearLayoutEndRideMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutEndRideMain, textViewScroll,
					new KeyboardLayoutListener.KeyBoardStateHandler() {
				@Override
				public void keyboardOpened() {

				}

				@Override
				public void keyBoardClosed() {

				}
			}));


			//Top bar events
			menuBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(menuLayout);
					FlurryEventLogger.event(MENU);
				}
			});


			checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					Toast.makeText(getApplicationContext(), "url = " + Data.SERVER_URL, Toast.LENGTH_SHORT).show();
					FlurryEventLogger.checkServerPressed(Data.userData.accessToken);

					return false;
				}
			});


			// menu events
			imageViewAutosOnToggle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.autosAvailable == 1) {
							changeJugnooON(BusinessType.AUTOS, 0, false);
						} else {
							changeJugnooON(BusinessType.AUTOS, 1, false);
						}
						FlurryEventLogger.event(JUGNOO_ON_OFF);
					}
				}
			});

			imageViewFatafatOnToggle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.fatafatAvailable == 1) {
							changeJugnooON(BusinessType.FATAFAT, 0, false);
						} else {
							changeJugnooON(BusinessType.FATAFAT, 1, false);
						}
						FlurryEventLogger.event(FATAFAT_ENABLE);
					}
				}
			});

			imageViewMealsOnToggle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.mealsAvailable == 1) {
							changeJugnooON(BusinessType.MEALS, 0, false);
						} else {
							changeJugnooON(BusinessType.MEALS, 1, false);
						}
						FlurryEventLogger.event(MEALS_ENABLE);
					}
				}
			});

			imageViewSharingOnToggle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.sharingAvailable == 1) {
							toggleSharingMode(BusinessType.AUTOS, 0, false);
						} else {
							toggleSharingMode(BusinessType.AUTOS, 1, false);
						}
						FlurryEventLogger.event(SHARING_ENABLE);
					}
				}
			});


			inviteFriendRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, ShareActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(INVITE_OPENED);
				}
			});

			notificationCenterRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, NotificationCenterActivity.class));
				}
			});

			RelativeLayoutNotificationCenter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, NotificationCenterActivity.class));
				}
			});


			driverImageRL.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverProfileActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(INVITE_OPENED);
				}
			});


			fareDetailsRl.setVisibility(View.GONE);
			fareDetailsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendToFareDetails();
					FlurryEventLogger.event(FARE_DETAILS_CHECKED);
				}
			});

			relativeLayoutSuperDrivers.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverLeaderboardActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(SUPER_DRIVERS_OPENED);
				}
			});


			callUsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Utils.openCallIntent(HomeActivity.this, Data.userData.driverSupportNumber);
//					startActivity(new Intent(HomeActivity.this, DownloadActivity.class));
					FlurryEventLogger.event(CALL_US);
				}
			});

			paytmRecharge.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverPatymRecharge.class));
				}
			});


			languagePrefrencesRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, LanguagePrefrencesActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});


			bookingsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverHistoryActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(RIDES_OPENED);
				}
			});

			relativeLayoutSharingRides.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, SharingRidesActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(SHARING_RIDES_OPENED);
				}
			});

			relativeLayoutSharingRides.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, SharingRidesActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(SHARING_RIDES_OPENED);
				}
			});

			logoutRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (((userMode == UserMode.DRIVER) && (driverScreenMode == DriverScreenMode.D_INITIAL))) {
						logoutPopup(HomeActivity.this);
					} else {
						DialogPopup.alertPopup(activity, "", "Ride in progress. You can logout only after the ride ends.");
					}
				}
			});

			SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
			String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
			if (link.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
				logoutRl.setVisibility(View.GONE);
			} else {
				logoutRl.setVisibility(View.VISIBLE);
			}


			menuLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});


			// driver initial layout events
			jugnooOffLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(menuLayout);
				}
			});


			// driver accept layout events
			driverRequestAcceptBackBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
					FlurryEventLogger.event(RIDE_CHECKED_AND_NO_ACTION);
				}
			});

			driverAcceptRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					acceptRequestFunc();
					GCMIntentService.cancelUploadPathAlarm(HomeActivity.this);
					FlurryEventLogger.event(RIDE_CHECKED_AND_ACCEPTED);
				}
			});


			driverCancelRequestBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rejectRequestFunc();
					FlurryEventLogger.event(RIDE_CHECKED_AND_CANCELLED);
				}
			});


			// driver start ride layout events
			driverPassengerCallRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String callPhoneNumber = "";

					if (Data.assignedCustomerInfo != null) {
						if (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType) {
							if (DriverScreenMode.D_ARRIVED == driverScreenMode || DriverScreenMode.D_START_RIDE == driverScreenMode) {
								callPhoneNumber = Data.assignedCustomerInfo.phoneNumber;
							} else if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
								if (((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo != null) {
									callPhoneNumber = ((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo.phoneNo;
								}
							}
						} else {
							callPhoneNumber = Data.assignedCustomerInfo.phoneNumber;
						}
					}

					if (!"".equalsIgnoreCase(callPhoneNumber)) {
						Utils.openCallIntent(HomeActivity.this, callPhoneNumber);
						if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
							FlurryEventLogger.event(CALLED_CUSTOMER);
						} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
							FlurryEventLogger.event(CALL_CUSTOMER_AFTER_ARRIVING);
						} else if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
							FlurryEventLogger.event(CUSTOMER_CALLED_WHEN_RIDE_IN_PROGRESS);
						}
					} else {
						Toast.makeText(HomeActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
					}
				}
			});


			driverStartRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (getBatteryPercentage() >= 10) {
						startRidePopup(HomeActivity.this);
						FlurryEventLogger.event(RIDE_STARTED);
					} else {
						DialogPopup.alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to start the ride. Plugin to a power source to continue.");
					}
				}
			});


			buttonMarkArrived.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (getBatteryPercentage() >= 10) {
						DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", "Have you arrived?", "Yes", "No",
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (myLocation != null) {
											LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
											double displacement = MapUtils.distance(driverAtPickupLatLng, Data.dCustLatLng);

											if (displacement <= DRIVER_START_RIDE_CHECK_METERS) {
												buildAlertMessageNoGps();
												GCMIntentService.clearNotifications(activity);
												driverMarkArriveRideAsync(activity, driverAtPickupLatLng);
												FlurryEventLogger.event(CONFIRMING_ARRIVE_YES);
											} else {
												DialogPopup.alertPopup(activity, "", "You must be present near the customer pickup location to mark ride arrived.");
											}
										} else {
											Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
										}
									}
								},
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										FlurryEventLogger.event(CONFIRMING_ARRIVE_NO);
									}
								}, false, false);
						FlurryEventLogger.event(ARRIVED_ON_THE_PICK_UP_LOCATION);
					} else {
						DialogPopup.alertPopup(HomeActivity.this, "", "Battery Level must be greater than 10% to to mark the ride arrived. Plugin to a power source to continue.");
					}
				}
			});


			driverCancelRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//				cancelRidePopup(HomeActivity.this);
					Intent intent = new Intent(HomeActivity.this, RideCancellationActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
						FlurryEventLogger.event(CANCELED_BEFORE_ARRIVING);
					} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
						FlurryEventLogger.event(RIDE_CANCELLED_AFTER_ARRIVING);
					}
				}
			});


			// driver in ride layout events
//		driverWaitRl.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(waitStart == 2){
//					startWait();
//				}
//				else if(waitStart == 1){
//					stopWait();
//				}
//				else if(waitStart == 0){
//					startWait();
//				}
//			}
//		});

			inrideFareInfoBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendToFareDetails();
				}
			});

			driverEndRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Data.assignedCustomerInfo != null) {
						if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType || BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType) {
							endRidePopup(HomeActivity.this, Data.assignedCustomerInfo.businessType);
						} else {
							//Meals case of end ride
							endRidePopup(HomeActivity.this, Data.assignedCustomerInfo.businessType);
						}
						FlurryEventLogger.event(RIDE_ENDED);
					}
				}
			});


			// End ride review layout events
			endRideReviewRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});



			reviewSubmitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (DriverScreenMode.D_BEFORE_END_OPTIONS == driverScreenMode) {
							if (Data.assignedCustomerInfo != null
									&& BusinessType.AUTOS == Data.assignedCustomerInfo.businessType
									&& 1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable) {

								String enteredMeterFare = editTextEnterMeterFare.getText().toString().trim();
								if ("".equalsIgnoreCase(enteredMeterFare)) {
									DialogPopup.alertPopupWithListener(HomeActivity.this, "", "Please enter some fare", new OnClickListener() {
										@Override
										public void onClick(View v) {
											new Handler().postDelayed(new Runnable() {
												@Override
												public void run() {
													linearLayoutMeterFareEditText.performClick();
												}
											}, 200);
										}
									});
								} else {
									if (AppStatus.getInstance(activity).isOnline(activity)) {
										String message = "The amount entered is " + getResources().getString(R.string.rupee) + " " + enteredMeterFare;
										if (1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).luggageChargesApplicable) {
											if (luggageCountAdded > 0) {
												message = message + "\n" + luggageCountAdded + " luggage items added";
											} else {
												message = message + "\n" + "No luggage added";
											}
										}

										DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", message, "OK", "Cancel",
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														endRideGPSCorrection(Data.assignedCustomerInfo.businessType);
													}
												},
												new OnClickListener() {
													@Override
													public void onClick(View v) {

													}
												}, false, false);
									} else {
										DialogPopup.alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
									}
								}
							} else if (Data.assignedCustomerInfo != null
									&& BusinessType.AUTOS == Data.assignedCustomerInfo.businessType
									&& 1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).luggageChargesApplicable) {

								if (AppStatus.getInstance(activity).isOnline(activity)) {
									String message = "";
									if (luggageCountAdded > 0) {
										message = luggageCountAdded + " luggage items added";
									} else {
										message = "No luggage added";
									}

									DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", message, "OK", "Cancel",
											new OnClickListener() {
												@Override
												public void onClick(View v) {
													endRideGPSCorrection(Data.assignedCustomerInfo.businessType);
												}
											},
											new OnClickListener() {
												@Override
												public void onClick(View v) {

												}
											}, false, false);
								} else {
									DialogPopup.alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
								}
							}
						} else if (DriverScreenMode.D_RIDE_END == driverScreenMode) {

							int rating = (int) ratingBarFeedback.getRating();
							rating = Math.abs(rating);
							if (0 == rating) {
								DialogPopup.alertPopup(HomeActivity.this, "", "We take your feedback seriously. Please give us a rating");
							} else {
								submitFeedbackToDriverAsync(HomeActivity.this, Data.dEngagementId, rating);
								MeteringService.clearNotifications(HomeActivity.this);
								driverScreenMode = DriverScreenMode.D_INITIAL;
								switchDriverScreen(driverScreenMode);
								FlurryEventLogger.event(OK_ON_FARE_SCREEN);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			reviewSkipBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (DriverScreenMode.D_RIDE_END == driverScreenMode) {
						MeteringService.clearNotifications(HomeActivity.this);
						driverScreenMode = DriverScreenMode.D_INITIAL;
						switchDriverScreen(driverScreenMode);
						FlurryEventLogger.event(OK_ON_FARE_SCREEN);
					}
				}
			});


			editTextEnterMeterFare.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s.length() > 0) {
						if (textViewMeterFareRupee.getVisibility() == View.GONE) {
							textViewMeterFareRupee.setVisibility(View.VISIBLE);
						}
					} else {
						if (textViewMeterFareRupee.getVisibility() == View.VISIBLE) {
							textViewMeterFareRupee.setVisibility(View.GONE);
						}
					}
					if (!getJugnooCalculatedFare().equalsIgnoreCase(s.toString())) {
						fareFetchedFromJugnoo = 0;
					}
				}
			});

			editTextEnterMeterFare.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					reviewSubmitBtn.performClick();
					return true;
				}
			});

			linearLayoutMeterFareEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editTextEnterMeterFare.requestFocus();
					editTextEnterMeterFare.setSelection(editTextEnterMeterFare.getText().length());
					Utils.showSoftKeyboard(HomeActivity.this, editTextEnterMeterFare);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							scrollViewEndRide.smoothScrollTo(0, editTextEnterMeterFare.getBottom());
						}
					}, 300);
				}
			});

			relativeLayoutUseJugnooFare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (relativeLayoutJugnooCalculatedFare.getVisibility() == View.GONE) {
						relativeLayoutJugnooCalculatedFare.setVisibility(View.VISIBLE);
						reviewFareInfoInnerRl.setVisibility(View.GONE);
						editTextEnterMeterFare.setText("" + getJugnooCalculatedFare());
						fareFetchedFromJugnoo = 1;
					}
				}
			});


			imageViewEndRideLuggageCountPlus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (luggageCountAdded < 5) {
						luggageCountAdded = luggageCountAdded + 1;
						textViewEndRideLuggageCount.setText("" + luggageCountAdded);
					}
				}
			});

			imageViewEndRideLuggageCountMinus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (luggageCountAdded > 0) {
						luggageCountAdded = luggageCountAdded - 1;
						if (luggageCountAdded == 0) {
							textViewEndRideLuggageCount.setText("NO LUGGAGE");
						} else {
							textViewEndRideLuggageCount.setText("" + luggageCountAdded);
						}
					}
				}
			});


			reviewFareInfoBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendToFareDetails();
				}
			});


			lastLocation = null;
			lastLocationTime = System.currentTimeMillis();

			if (Data.userData.remainigPenaltyPeriod > 0) {
				driverTimeOutPopup(HomeActivity.this, Data.userData.remainigPenaltyPeriod);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		try {
			String type = intent.getStringExtra("type");
			if (type.equalsIgnoreCase("accept")) {
				DriverRideRequest driverRideRequest = driverRequestListAdapter
						.driverRideRequests.get(driverRequestListAdapter.driverRideRequests.indexOf
								(new DriverRideRequest(intent.getExtras().getString("engagement_id"))));

				driverRideRequestsList.setVisibility(View.GONE);

				Data.dEngagementId = driverRideRequest.engagementId;
				Data.dCustomerId = driverRideRequest.customerId;
				Data.dCustLatLng = driverRideRequest.latLng;
				Data.openedDriverRideRequest = driverRideRequest;

				acceptRequestFunc();
				FlurryEventLogger.event(RIDE_ACCEPTED);

			} else if (type.equalsIgnoreCase("cancel")) {

				DriverRideRequest driverRideRequest = driverRequestListAdapter
						.driverRideRequests.get(driverRequestListAdapter.driverRideRequests.indexOf
								(new DriverRideRequest(intent.getExtras().getString("engagement_id"))));

				Data.dEngagementId = driverRideRequest.engagementId;
				Data.dCustomerId = driverRideRequest.customerId;
				Data.dCustLatLng = driverRideRequest.latLng;
				Data.openedDriverRideRequest = driverRideRequest;

				rejectRequestFunc();
				FlurryEventLogger.event(RIDE_CANCELLED);

			} else {

			}


		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onMapReady(GoogleMap gMap) {
		this.map = gMap;
		// map object initialized
		try {
			if (map != null) {
				map.getUiSettings().setZoomControlsEnabled(false);
				map.setMyLocationEnabled(true);
				map.getUiSettings().setTiltGesturesEnabled(false);
				map.getUiSettings().setMyLocationButtonEnabled(false);
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

				//30.75, 76.78

				if (0 == Data.latitude && 0 == Data.longitude) {
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
				} else {
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
				}

				//            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
				//                @Override
				//                public void onMyLocationChange(Location location) {
				//
				//                    Toast.makeText(HomeActivity.this, ""+location, Toast.LENGTH_SHORT).show();
				//
				//                }
				//            });


				map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

					@Override
					public void onMapClick(LatLng arg0) {
						Log.e("arg0", "=" + arg0);
					}
				});

				map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {

						if (arg0.getTitle().equalsIgnoreCase("pickup location")) {

							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Your Pickup Location", "");
							map.setInfoWindowAdapter(customIW);

							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("customer_current_location")) {

							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
							map.setInfoWindowAdapter(customIW);

							return true;
						} else if (arg0.getTitle().equalsIgnoreCase("start ride location")) {

							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Start Location", "");
							map.setInfoWindowAdapter(customIW);


							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("driver position")) {

							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, "Driver Location", "");
							map.setInfoWindowAdapter(customIW);

							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("station_marker")) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
							map.setInfoWindowAdapter(customIW);
							return false;
						} else {
							return true;
						}
					}
				});


				driverInitialMyLocationBtn.setOnClickListener(mapMyLocationClick);
				driverRequestAcceptMyLocationBtn.setOnClickListener(mapMyLocationClick);
				buttonDriverNavigation.setOnClickListener(startNavigation);
				driverEngagedMyLocationBtn.setOnClickListener(mapMyLocationClick);

			}


			try {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				Log.i("userMode", "=" + userMode);
				Log.i("driverScreenMode", "=" + driverScreenMode);

				if (userMode == null) {
					userMode = UserMode.DRIVER;
				}

				if (driverScreenMode == null) {
					driverScreenMode = DriverScreenMode.D_INITIAL;
				}


				switchUserScreen(userMode);

				switchDriverScreen(driverScreenMode);


				changeDriverOptionsUI();

				changeJugnooONUIAndInitService();


				Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Data.SERVER_URL);
				Database2.getInstance(HomeActivity.this).updatePushyToken("");

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (Data.userData.sharingEnabled == 1) {
					relativeLayoutSharingRides.setVisibility(View.VISIBLE);
				} else {
					relativeLayoutSharingRides.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				relativeLayoutSharingRides.setVisibility(View.GONE);
			}

			showManualPatchPushReceivedDialog();
		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		}

		try {
			if (getIntent().hasExtra("type")) {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						onNewIntent(getIntent());
					}
				}, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void acceptRequestFunc() {
		if (getBatteryPercentage() >= 20) {
			GCMIntentService.clearNotifications(HomeActivity.this);
			GCMIntentService.stopRing(true);
			driverAcceptRideAsync(HomeActivity.this);
		} else {
			driverRideRequestsList.setVisibility(View.VISIBLE);
			DialogPopup.alertPopup(HomeActivity.this, "", "Battery Level must be greater than 20% to accept the ride. Plugin to a power source to continue.");
		}
	}

	public void rejectRequestFunc() {
		GCMIntentService.clearNotifications(HomeActivity.this);
		GCMIntentService.stopRing(true);
		driverRejectRequestAsync(HomeActivity.this);
	}


	public void sendToFareDetails() {
		HelpParticularActivity.helpSection = HelpSection.FARE_DETAILS;
		startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
		FlurryEventLogger.fareDetailsOpened(Data.userData.accessToken);
	}


//	public void startWait(){
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				rideTimeChronometer.stop();
//				driverWaitRl.setBackgroundResource(R.drawable.red_btn_selector);
//				driverWaitText.setText(getResources().getString(R.string.stop_wait));
//				waitStart = 1;
//				distanceAfterWaitStarted = 0;
//				startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 1);
//
//			}
//		});
//	}
//
//
//
//	public void stopWait(){
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				rideTimeChronometer.start();
//				driverWaitRl.setBackgroundResource(R.drawable.blue_btn_selector);
//				driverWaitText.setText(getResources().getString(R.string.start_wait));
//				waitStart = 0;
//				startEndWaitAsync(HomeActivity.this, Data.dCustomerId, 0);
//
//			}
//		});
//	}


	public void changeJugnooON(BusinessType businessType, int mode, boolean enableSharing) {
		if (mode == 1) {
			if (myLocation != null) {
				switchJugnooOnThroughServer(businessType, 1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), enableSharing);
			} else {
				Toast.makeText(HomeActivity.this, "Waiting for location...", Toast.LENGTH_SHORT).show();
			}
		} else {
			if (Data.userData.sharingEnabled == 1 && Data.userData.sharingAvailable == 1) {
				toggleSharingMode(businessType, 0, true);
			} else {
				switchJugnooOnThroughServer(businessType, 0, new LatLng(0, 0), false);
			}
		}
	}


	public void toggleSharingMode(BusinessType businessType, int mode, boolean disableAutos) {
		if (mode == 1) {
			if (myLocation != null) {
				if (Data.userData.autosAvailable == 0) {
					changeJugnooON(businessType, 1, true);
				} else {
					toggleSharingModeAPI(businessType, 1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), false);
				}
			} else {
				Toast.makeText(HomeActivity.this, "Waiting for location...", Toast.LENGTH_SHORT).show();
			}
		} else {
			toggleSharingModeAPI(businessType, 0, new LatLng(0, 0), disableAutos);
		}
	}


	public void switchJugnooOnThroughServer(final BusinessType businessType, final int jugnooOnFlag, final LatLng latLng, final boolean enableSharing) {
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
					HashMap<String, String> params = new HashMap<String, String>();

					params.put("access_token", Data.userData.accessToken);
					params.put("latitude", "" + latLng.latitude);
					params.put("longitude", "" + latLng.longitude);
					params.put("flag", "" + jugnooOnFlag);
					params.put("business_id", "" + businessType.getOrdinal());

					Response response = RestClient.getApiServices().switchJugnooOnThroughServerRetro(params);
					String result = new String(((TypedByteArray) response.getBody()).getBytes());

					JSONObject jObj = new JSONObject(result);

					if (jObj.has("flag")) {
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							if (BusinessType.AUTOS == businessType) {
								Data.userData.autosAvailable = jugnooOnFlag;
							} else if (BusinessType.FATAFAT == businessType) {
								Data.userData.fatafatAvailable = jugnooOnFlag;
							} else if (BusinessType.MEALS == businessType) {
								Data.userData.mealsAvailable = jugnooOnFlag;
							}
							changeJugnooONUIAndInitService();
							if (jugnooOnFlag == 1) {
								AGPSRefresh.softRefreshGpsData(HomeActivity.this);
							}
						}
					}
					String message = JSONParser.getServerMessage(jObj);
					showDialogFromBackground(message);

				} catch (Exception e) {
					e.printStackTrace();
					showDialogFromBackground(Data.SERVER_ERROR_MSG);
				}
				dismissLoadingFromBackground();

				if (jugnooOnFlag == 1 && enableSharing && Data.userData.sharingEnabled == 1 && Data.userData.sharingAvailable == 0) {
					toggleSharingMode(businessType, 1, false);
				}
			}
		}).start();
	}


	public void toggleSharingModeAPI(final BusinessType businessType, final int mode, final LatLng latLng, final boolean disableAutos) {
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
					HashMap<String, String> params = new HashMap<String, String>();

					params.put("access_token", Data.userData.accessToken);
					params.put("latitude", "" + latLng.latitude);
					params.put("longitude", "" + latLng.longitude);
					params.put("flag", "" + mode);

					Response response = RestClient.getApiServices().toggleSharingMode(params);
					String result = new String(((TypedByteArray) response.getBody()).getBytes());

					JSONObject jObj = new JSONObject(result);

					if (jObj.has("flag")) {
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							Data.userData.sharingAvailable = mode;
							changeJugnooONUIAndInitService();
						}
					}
					String message = JSONParser.getServerMessage(jObj);
					showDialogFromBackground(message);

				} catch (Exception e) {
					e.printStackTrace();
					showDialogFromBackground(Data.SERVER_ERROR_MSG);
				}
				dismissLoadingFromBackground();
				if (mode == 0 && disableAutos && Data.userData.autosEnabled == 1 && Data.userData.autosAvailable == 1) {
					switchJugnooOnThroughServer(businessType, 0, new LatLng(0, 0), false);
				}
			}
		}).start();
	}


	public void showDialogFromBackground(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				DialogPopup.alertPopup(HomeActivity.this, "", message);
			}
		});
	}

	private void dismissLoadingFromBackground() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
			}
		});
	}


	public void changeDriverOptionsUI() {
		if (Data.userData != null) {
			if (1 == Data.userData.autosEnabled) {
				relativeLayoutAutosOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutAutosOn.setVisibility(View.GONE);
				Data.userData.autosAvailable = 0;
			}

			if (1 == Data.userData.mealsEnabled) {
				relativeLayoutMealsOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutMealsOn.setVisibility(View.GONE);
				Data.userData.mealsAvailable = 0;
			}

			if (1 == Data.userData.fatafatEnabled) {
				relativeLayoutFatafatOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutFatafatOn.setVisibility(View.GONE);
				Data.userData.fatafatAvailable = 0;
			}

			if (1 == Data.userData.sharingEnabled) {
				relativeLayoutSharingOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutSharingOn.setVisibility(View.GONE);
				Data.userData.sharingAvailable = 0;
			}
		}
	}


	public void changeJugnooONUIAndInitService() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				try {
					if(Data.userData != null){
						if(1 == Data.userData.autosAvailable){
							imageViewAutosOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						}
						else{
							imageViewAutosOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if(1 == Data.userData.mealsAvailable){
							imageViewMealsOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						}
						else{
							imageViewMealsOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if(1 == Data.userData.fatafatAvailable){
							imageViewFatafatOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						}
						else{
							imageViewFatafatOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if(1 == Data.userData.sharingAvailable){
							imageViewSharingOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						}
						else{
							imageViewSharingOnToggle.setImageResource(R.drawable.jugnoo_off_button);

						}

						if (!checkIfDriverOnline()) {
							if (isDriverStateFree()) {
								setDriverServiceRunOnOnlineBasis();
								jugnooOffLayout.setVisibility(View.VISIBLE);
								map.clear();
								stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

								GCMIntentService.clearNotifications(HomeActivity.this);
								GCMIntentService.stopRing(true);

								if (map != null) {
									map.clear();
								}
								dismissStationDataPopup();
								cancelStationPathUpdateTimer();
							}
						} else {
							if (isDriverStateFree()) {
								setDriverServiceRunOnOnlineBasis();
								jugnooOffLayout.setVisibility(View.GONE);
								fetchHeatMapData(HomeActivity.this);
								startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
								initializeStationDataProcedure();
							}
						}


						updateReceiveRequestsFlag();

						updateDriverRequestsAccAvailaviblity();

						if (Data.driverRideRequests.size() == 0) {
							GCMIntentService.clearNotifications(HomeActivity.this);
							GCMIntentService.stopRing(true);
						}

						showAllRideRequestsOnMap();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}


	private boolean checkIfDriverOnline() {
		if (0 == Data.userData.autosAvailable
				&& 0 == Data.userData.mealsAvailable
				&& 0 == Data.userData.fatafatAvailable
				&& 0 == Data.userData.sharingAvailable) {
			return false;
		} else {
			return true;
		}
	}

	private void setDriverServiceRunOnOnlineBasis() {
		if (checkIfDriverOnline()) {
			Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.YES);
		} else {
			Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
		}
	}


	public void updateDriverRequestsAccAvailaviblity() {
		try {
			if (Data.userData != null) {
				ArrayList<DriverRideRequest> tempDriverRideRequests = new ArrayList<DriverRideRequest>();
				tempDriverRideRequests.addAll(Data.driverRideRequests);
				for (int i = 0; i < tempDriverRideRequests.size(); i++) {
					if ((BusinessType.AUTOS == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.autosAvailable)) {
						Data.driverRideRequests.remove(i);
					} else if ((BusinessType.FATAFAT == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.fatafatAvailable)) {
						Data.driverRideRequests.remove(i);
					} else if ((BusinessType.MEALS == Data.driverRideRequests.get(i).businessType) && (1 != Data.userData.mealsAvailable)) {
						Data.driverRideRequests.remove(i);
					}
				}
				tempDriverRideRequests.clear();
				tempDriverRideRequests = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean isDriverStateFree() {
		return (UserMode.DRIVER == userMode
				&& DriverScreenMode.D_INITIAL == driverScreenMode);
	}

	public boolean isDriverEngaged() {
		return (UserMode.DRIVER == userMode
				&& (DriverScreenMode.D_ARRIVED == driverScreenMode
				|| DriverScreenMode.D_START_RIDE == driverScreenMode
				|| DriverScreenMode.D_IN_RIDE == driverScreenMode));
	}


	public void updateReceiveRequestsFlag() {
		if (Data.userData != null) {
			if (0 == Data.userData.autosAvailable && 0 == Data.userData.mealsAvailable && 0 == Data.userData.fatafatAvailable) {
				Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 0);
			} else {
				if (isDriverEngaged()) {
					Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 0);
				} else {
					Prefs.with(HomeActivity.this).save(SPLabels.RECEIVE_REQUESTS, 1);
				}
			}
		}
	}


	OnClickListener mapMyLocationClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (myLocation != null) {
				if (map.getCameraPosition().zoom < 12) {
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12));
				} else if (map.getCameraPosition().zoom < 15) {
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
				} else {
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
				}
			} else {
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
				reconnectLocationFetchers();
			}
		}
	};

	OnClickListener startNavigation = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (myLocation != null) {
				Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
				mapIntent.setPackage("com.google.android.apps.maps");
				startActivity(mapIntent);
				Intent intent = new Intent(HomeActivity.this, GeanieView.class);
				startService(intent);
			} else {
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
			}
		}
	};


	Handler reconnectionHandler = null;

	public void reconnectLocationFetchers() {
		if (reconnectionHandler == null) {
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


	public void setUserData() {
		try {
			userName.setText(Data.userData.userName);
			Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
			try {
				if (resumed) {
					Picasso.with(HomeActivity.this).load(Data.userData.userImage)
							.transform(new CircleTransform()).into(profileImg);
				} else {
					Picasso.with(HomeActivity.this).load(Data.userData.userImage)
							.skipMemoryCache()
							.transform(new CircleTransform()).into(profileImg);
				}
			} catch (Exception e) {
			}

			if ("-1".equalsIgnoreCase(Data.userData.deiValue)) {
				linearLayoutDEI.setVisibility(View.GONE);

				imageViewTitleBarDEI.setVisibility(View.GONE);
				textViewTitleBarDEI.setText("Jugnoo");
			} else {
				linearLayoutDEI.setVisibility(View.VISIBLE);
//				textViewDEI.setText(Data.userData.deiValue);
				textViewTitleBarDEI.setText(Data.userData.deiValue);
				imageViewTitleBarDEI.setVisibility(View.VISIBLE);
			}
//			textViewTitleBarOvalText.setText(Data.userData.driverOnlineHours);
			if(Data.userData.showDriverRating > 0 && Data.userData.showDriverRating < 6 ) {

				driverRatingRl.setVisibility(View.VISIBLE);
				ratingBarFeedbackSide.setRating((float) Data.userData.showDriverRating);
				ratingValue.setText("" + new DecimalFormat("#.#").format(Data.userData.showDriverRating));
			} else {
				driverRatingRl.setVisibility(View.GONE);
			}

			int unreadNotificationsCount = Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			if(unreadNotificationsCount > 0){
				textViewNotificationValue.setVisibility(View.VISIBLE);
				textViewNotificationValue.setText("" + unreadNotificationsCount);
			}
			else{
				textViewNotificationValue.setVisibility(View.GONE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (className.equals(service.service.getClassName())) {
				Log.e("service already running", "=" + service.service.getClassName());
				return true;
			}
		}
		return false;
	}


	public void switchUserScreen(final UserMode mode) {
		switch (mode) {
			case DRIVER:
				Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.YES);
				driverMainLayout.setVisibility(View.VISIBLE);
				break;

			default:
				Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.YES);
				driverMainLayout.setVisibility(View.VISIBLE);
		}
	}


	public void updateDriverServiceFast(String choice) {
		Database2.getInstance(HomeActivity.this).updateDriverServiceFast(choice);
	}


	public void switchDriverScreen(final DriverScreenMode mode) {
		if (userMode == UserMode.DRIVER) {

			initializeFusedLocationFetchers();

			if (mode == DriverScreenMode.D_RIDE_END) {
				if (Data.endRideData != null) {
					mapLayout.setVisibility(View.GONE);
					endRideReviewRl.setVisibility(View.VISIBLE);


					double totalDistanceInKm = Math.abs(totalDistance / 1000.0);
					String kmsStr = "";
					if (totalDistanceInKm > 1) {
						kmsStr = "kms";
					} else {
						kmsStr = "km";
					}

					reviewDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
					reviewWaitValue.setText(waitTime + " min");
					reviewRideTimeValue.setText(rideTime + " min");
					reviewFareValue.setText("Rs. " + Utils.getDecimalFormatForMoney().format(totalFare));

					setTextToFareInfoTextViews(reviewMinFareValue, reviewFareAfterValue, reviewFareAfterText, textViewReviewConvenienceCharges);

					jugnooRideOverText.setText("The Jugnoo ride is over.");
					takeFareText.setText("Please take the fare as shown above from the customer.");

					displayCouponApplied();


					reviewReachedDistanceRl.setVisibility(View.VISIBLE);
					linearLayoutMeterFare.setVisibility(View.GONE);
					relativeLayoutRateCustomer.setVisibility(View.VISIBLE);
					ratingBarFeedback.setVisibility(View.VISIBLE);
					reviewSkipBtn.setVisibility(View.VISIBLE);
					ratingBarFeedback.setRating(0);

					relativeLayoutEndRideCustomerAmount.setVisibility(View.VISIBLE);


					relativeLayoutUseJugnooFare.setVisibility(View.GONE);
					relativeLayoutJugnooCalculatedFare.setVisibility(View.GONE);
					try {
						if (Data.assignedCustomerInfo instanceof AutoCustomerInfo) {
							if (((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable == 1 && ((AutoCustomerInfo) Data.assignedCustomerInfo).getJugnooFareEnabled != 1) {
								reviewFareInfoInnerRl.setVisibility(View.GONE);
							} else {
								reviewFareInfoInnerRl.setVisibility(View.GONE);
							}
						} else {
							reviewFareInfoInnerRl.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						reviewFareInfoInnerRl.setVisibility(View.GONE);
					}
					try {
						if (Data.assignedCustomerInfo.businessType.getOrdinal() == BusinessType.AUTOS.getOrdinal()) {
							if (((AutoCustomerInfo) Data.assignedCustomerInfo).waitingChargesApplicable == 1) {
								reviewWaitTimeRl.setVisibility(View.VISIBLE);
								imageViewEndRideWaitSep.setVisibility(View.VISIBLE);
							} else {
								reviewWaitTimeRl.setVisibility(View.GONE);
								imageViewEndRideWaitSep.setVisibility(View.GONE);
							}
						} else {
							reviewWaitTimeRl.setVisibility(View.GONE);
							imageViewEndRideWaitSep.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						reviewWaitTimeRl.setVisibility(View.GONE);
						imageViewEndRideWaitSep.setVisibility(View.GONE);
					}

					relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);

					Database2.getInstance(this).deleteAllCurrentPathItems();
				} else {
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
				}
			} else if (mode == DriverScreenMode.D_BEFORE_END_OPTIONS) {
				mapLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.VISIBLE);

				editTextEnterMeterFare.setText("");

				setTextToFareInfoTextViews(reviewMinFareValue, reviewFareAfterValue, reviewFareAfterText, textViewReviewConvenienceCharges);

				reviewReachedDistanceRl.setVisibility(View.GONE);
				linearLayoutMeterFare.setVisibility(View.VISIBLE);
				relativeLayoutRateCustomer.setVisibility(View.GONE);
				ratingBarFeedback.setVisibility(View.GONE);
				reviewSkipBtn.setVisibility(View.GONE);

				relativeLayoutEndRideCustomerAmount.setVisibility(View.GONE);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reviewSubmitBtn.getLayoutParams();
				layoutParams.setMargins(0, 0, 0, (int) (270 * ASSL.Yscale()));
				reviewSubmitBtn.setLayoutParams(layoutParams);
				reviewSubmitBtn.setText("OK");

				try {
					if (Data.assignedCustomerInfo instanceof AutoCustomerInfo) {
						if (((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable == 1 && ((AutoCustomerInfo) Data.assignedCustomerInfo).getJugnooFareEnabled == 1) {
							relativeLayoutUseJugnooFare.setVisibility(View.VISIBLE);
						} else {
							relativeLayoutUseJugnooFare.setVisibility(View.GONE);
						}
					} else {
						relativeLayoutUseJugnooFare.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					relativeLayoutUseJugnooFare.setVisibility(View.GONE);
				}

				try {
					if (Data.assignedCustomerInfo instanceof AutoCustomerInfo) {
						if (((AutoCustomerInfo) Data.assignedCustomerInfo).luggageChargesApplicable == 1) {
							relativeLayoutEndRideLuggageCount.setVisibility(View.VISIBLE);
						} else {
							relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
						}
					} else {
						relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
				}

				luggageCountAdded = 0;
				textViewEndRideLuggageCount.setText("NO LUGGAGE");


				setCalculatedFareValuesAtEndRide();
				relativeLayoutJugnooCalculatedFare.setVisibility(View.GONE);
				reviewFareInfoInnerRl.setVisibility(View.GONE);

			} else {
				mapLayout.setVisibility(View.VISIBLE);
				endRideReviewRl.setVisibility(View.GONE);

			}


			switch (mode) {

				case D_INITIAL:
//					getDriverOnlineHours(HomeActivity.this);
					updateDriverServiceFast("no");

					textViewDriverInfo.setVisibility(View.GONE);

					driverInitialLayout.setVisibility(View.VISIBLE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);

					setDriverServiceRunOnOnlineBasis();
					if (checkIfDriverOnline()) {
						startService(new Intent(this, DriverLocationUpdateService.class));
					}

					cancelCustomerPathUpdateTimer();

					if (map != null) {
						map.clear();
						drawHeatMapData(heatMapResponseGlobal);
					}

					showAllRideRequestsOnMap();

					cancelCustomerPathUpdateTimer();
					cancelMapAnimateAndUpdateRideDataTimer();

					initializeStationDataProcedure();


					break;


				case D_REQUEST_ACCEPT:

					updateDriverServiceFast("no");

					setDriverServiceRunOnOnlineBasis();
					if (!isServiceRunning(HomeActivity.this, DriverLocationUpdateService.class.getName())) {
						startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					}

					if (map != null) {
						map.clear();
						customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.dCustLatLng));
					}

					if (Data.openedDriverRideRequest != null) {
						if (BusinessType.MEALS == Data.openedDriverRideRequest.businessType) {
							textViewBeforeAcceptRequestInfo.setVisibility(View.VISIBLE);
							textViewBeforeAcceptRequestInfo.setText("Ride Time: "
									+ ((MealRideRequest) Data.openedDriverRideRequest).rideTime);
						} else if (BusinessType.FATAFAT == Data.openedDriverRideRequest.businessType) {
							textViewBeforeAcceptRequestInfo.setVisibility(View.VISIBLE);
							textViewBeforeAcceptRequestInfo.setText("Cash Needed: Rs. "
									+ ((FatafatRideRequest) Data.openedDriverRideRequest).orderAmount);
						} else {
							textViewBeforeAcceptRequestInfo.setVisibility(View.GONE);
						}
					} else {
						textViewBeforeAcceptRequestInfo.setVisibility(View.GONE);
					}


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.VISIBLE);
					driverEngagedLayout.setVisibility(View.GONE);

					cancelCustomerPathUpdateTimer();
					cancelMapAnimateAndUpdateRideDataTimer();

					cancelStationPathUpdateTimer();

					break;


				case D_ARRIVED:

					updateDriverServiceFast("yes");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					if (map != null) {
						map.clear();
						customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.assignedCustomerInfo.requestlLatLng));
					}


					setAssignedCustomerInfoToViews(mode);


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerText.setVisibility(View.VISIBLE);
					if(Data.assignedCustomerInfo instanceof AutoCustomerInfo) {
						etaTimer(((AutoCustomerInfo) Data.assignedCustomerInfo).getEta());
					}
					driverStartRideMainRl.setVisibility(View.VISIBLE);
					driverInRideMainRl.setVisibility(View.GONE);

					driverStartRideBtn.setVisibility(View.GONE);
					buttonMarkArrived.setVisibility(View.VISIBLE);


					startCustomerPathUpdateTimer();
					cancelMapAnimateAndUpdateRideDataTimer();

					cancelStationPathUpdateTimer();


					break;

				case D_START_RIDE:

					updateDriverServiceFast("yes");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					if (map != null) {
						map.clear();
						customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(Data.assignedCustomerInfo.requestlLatLng));
					}


					setAssignedCustomerInfoToViews(mode);


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerText.setVisibility(View.GONE);
					timer.cancel();

					driverStartRideMainRl.setVisibility(View.VISIBLE);
					driverInRideMainRl.setVisibility(View.GONE);

					driverStartRideBtn.setVisibility(View.VISIBLE);
					buttonMarkArrived.setVisibility(View.GONE);


					startCustomerPathUpdateTimer();
					cancelMapAnimateAndUpdateRideDataTimer();

					cancelStationPathUpdateTimer();


					break;


				case D_IN_RIDE:

					updateDriverServiceFast("no");

					Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));


					rideTimeChronometer.setText(Utils.getChronoTimeFromMillis(HomeActivity.previousRideTime));
					rideTimeChronometer.eclipsedTime = (long) HomeActivity.previousRideTime;
					rideTimeChronometer.start();


					if (map != null) {
						map.clear();
						if (((Data.assignedCustomerInfo != null) && (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType))) {
							if (((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
								customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryLatLng));
							}
						}
					}

					updateDistanceFareTexts(totalDistance, rideTimeChronometer.eclipsedTime, HomeActivity.previousWaitTime);

					setAssignedCustomerInfoToViews(mode);

					setTextToFareInfoTextViews(inrideMinFareValue, inrideFareAfterValue, inrideFareAfterText, textViewInRideConvenienceCharges);


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerText.setVisibility(View.GONE);

					driverScheduledRideText.setVisibility(View.GONE);

					driverStartRideMainRl.setVisibility(View.GONE);
					driverInRideMainRl.setVisibility(View.VISIBLE);

					if (BusinessType.FATAFAT.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()) {
						startCustomerPathUpdateTimer();
						driverEndRideBtn.setText("Mark Delivered");
						inrideFareInfoRl.setVisibility(View.GONE);
						driverWaitRl.setVisibility(View.GONE);
						imageViewIRWaitSep.setVisibility(View.GONE);
					} else if (BusinessType.MEALS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()) {
						cancelCustomerPathUpdateTimer();
						driverEndRideBtn.setText("Mark Delivered");
						inrideFareInfoRl.setVisibility(View.GONE);
						driverWaitRl.setVisibility(View.GONE);
						imageViewIRWaitSep.setVisibility(View.GONE);
					} else if (BusinessType.AUTOS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()) {
						startCustomerPathUpdateTimer();
						driverEndRideBtn.setText("End Ride");

						try {
							if (Data.assignedCustomerInfo instanceof AutoCustomerInfo) {
								if (((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable == 1) {
									inrideFareInfoRl.setVisibility(View.GONE);
								} else {
									inrideFareInfoRl.setVisibility(View.GONE);
								}
							} else {
								inrideFareInfoRl.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							inrideFareInfoRl.setVisibility(View.GONE);
						}

						try {
							if (((AutoCustomerInfo) Data.assignedCustomerInfo).waitingChargesApplicable == 1) {
								driverWaitRl.setVisibility(View.VISIBLE);
								imageViewIRWaitSep.setVisibility(View.VISIBLE);
								driverWaitValue.setText(Utils.getChronoTimeFromMillis(HomeActivity.previousWaitTime));
							} else {
								driverWaitRl.setVisibility(View.GONE);
								imageViewIRWaitSep.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							e.printStackTrace();
							driverWaitRl.setVisibility(View.GONE);
							imageViewIRWaitSep.setVisibility(View.GONE);
						}

					}
					startMapAnimateAndUpdateRideDataTimer();
					cancelStationPathUpdateTimer();


					break;


				case D_BEFORE_END_OPTIONS:

					updateDriverServiceFast("no");

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					etaTimerText.setVisibility(View.GONE);

					cancelCustomerPathUpdateTimer();
					cancelMapAnimateAndUpdateRideDataTimer();
					cancelStationPathUpdateTimer();

					break;


				case D_RIDE_END:

					updateDriverServiceFast("no");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					etaTimerText.setVisibility(View.GONE);

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

			try {
				if (DriverScreenMode.D_ARRIVED == mode) {
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, mode.getOrdinal());

					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ACCESS_TOKEN, Data.userData.accessToken);
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ENGAGEMENT_ID, Data.dEngagementId);
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_CUSTOMER_ID, Data.dCustomerId);
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_REFERENCE_ID, "" + Data.assignedCustomerInfo.referenceId);
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_C_PICKUP_LATITUDE, "" + Data.assignedCustomerInfo.requestlLatLng.latitude);
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_C_PICKUP_LONGITUDE, "" + Data.assignedCustomerInfo.requestlLatLng.longitude);

					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ARRIVED_DISTANCE, "" + Data.userData.driverArrivalDistance);
				} else {
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, -1);

					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ACCESS_TOKEN, "");
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ENGAGEMENT_ID, "");
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_CUSTOMER_ID, "");
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_REFERENCE_ID, "");
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_C_PICKUP_LATITUDE, "");
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_C_PICKUP_LONGITUDE, "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			updateReceiveRequestsFlag();

			if (mode != DriverScreenMode.D_BEFORE_END_OPTIONS) {
				startMeteringService();
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (DriverScreenMode.D_INITIAL == mode) {
							Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.VULNERABLE);
						} else {
							Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.NOT_VULNERABLE);
						}
					} catch (Exception e) {
						Database2.getInstance(HomeActivity.this).updateDriverScreenMode(Database2.NOT_VULNERABLE);
						e.printStackTrace();
					} finally {
					}
				}
			}).start();

			MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, mode);
		}
	}


	public void setCalculatedFareValuesAtEndRide() {
		try {
			double totalDistanceInKm = Math.abs(totalDistance / 1000.0);
			String kmsStr = (totalDistanceInKm > 1) ? "kms" : "km";

			long rideTimeMillis = getElapsedRideTimeFromSPInMillis(HomeActivity.this, rideTimeChronometer.eclipsedTime);
			double rideTimeMinutes = Math.ceil(rideTimeMillis / 60000);
			String rideTime = decimalFormatNoDecimal.format(rideTimeMinutes);
			double totalFare = getTotalFare(totalDistance, rideTimeChronometer.eclipsedTime, HomeActivity.totalWaitTime);


			SpannableString distSS = new SpannableString(decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
			final StyleSpan distBSS = new StyleSpan(Typeface.BOLD);
			distSS.setSpan(distBSS, 0, distSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textViewCalculatedDistance.setText("");
			textViewCalculatedDistance.append("Distance: \n");
			textViewCalculatedDistance.append(distSS);

			SpannableString timeSS = new SpannableString(rideTime + " min");
			final StyleSpan timeBSS = new StyleSpan(Typeface.BOLD);
			timeSS.setSpan(timeBSS, 0, timeSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textViewCalculatedTime.setText("");
			textViewCalculatedTime.append("Time: \n");
			textViewCalculatedTime.append(timeSS);

			SpannableString fareSS = new SpannableString("Rs. " + Utils.getDecimalFormatForMoney().format(totalFare));
			final StyleSpan fareBSS = new StyleSpan(Typeface.BOLD);
			fareSS.setSpan(fareBSS, 0, fareSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textViewCalculatedFare.setText("");
			textViewCalculatedFare.append("Fare: \n");
			textViewCalculatedFare.append(fareSS);
		} catch (Exception e) {
			e.printStackTrace();
			textViewCalculatedDistance.setText("Sorry, we could not calculate your distance right now");
			textViewCalculatedTime.setText("");
			textViewCalculatedFare.setText("");
		}
	}


	private String getJugnooCalculatedFare() {
		try {
			double totalFare = getTotalFare(totalDistance, rideTimeChronometer.eclipsedTime, HomeActivity.totalWaitTime);
			return Utils.getDecimalFormatForMoney().format(totalFare);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


	public void startMeteringService() {

		lastGPSLocation = null;
		lastFusedLocation = null;
		distanceUpdateFromService = false;

		if (rideStartPositionMarker != null) {
			rideStartPositionMarker.remove();
			rideStartPositionMarker = null;
		}

		if (DriverScreenMode.D_ARRIVED == driverScreenMode
				|| DriverScreenMode.D_IN_RIDE == driverScreenMode) {
			String meteringState = Database2.getInstance(this).getMetringState();
			String meteringStateSp = Prefs.with(this).getString(SPLabels.METERING_STATE, Database2.OFF);

			if (!Database2.ON.equalsIgnoreCase(meteringState) && !Database2.ON.equalsIgnoreCase(meteringStateSp)) {
				GpsDistanceCalculator.saveTrackingToSP(this, 0);
			} else {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						displayOldPath();
					}
				}, 1000);
			}

			int rowsAffected = Database2.getInstance(this).updateMetringState(Database2.ON);
			Prefs.with(this).save(SPLabels.METERING_STATE, Database2.ON);
			if (rowsAffected > 0) {
				startService(new Intent(this, MeteringService.class));
			} else {
				Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
			}
		} else {
			int rowsAffected = Database2.getInstance(this).updateMetringState(Database2.OFF);
			Prefs.with(this).save(SPLabels.METERING_STATE, Database2.OFF);
			if (rowsAffected > 0) {
				stopService(new Intent(this, MeteringService.class));
			} else {
				Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
			}
//			Prefs.with(this).save(SPLabels.GPS_STATE, GpsState.ZERO_TWO.getOrdinal());
		}
	}


	public void displayCouponApplied() {
		try {
			if (Data.assignedCustomerInfo != null) {
				if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType) {
					reviewFareInfoInnerRl.setVisibility(View.GONE);
					AutoCustomerInfo autoCustomerInfo = (AutoCustomerInfo) Data.assignedCustomerInfo;
					if (autoCustomerInfo.couponInfo != null) {
						if (autoCustomerInfo.couponInfo.couponApplied) {
							if (BenefitType.CASHBACKS.getOrdinal() != autoCustomerInfo.couponInfo.benefitType) {
								endRideInfoRl.setVisibility(View.GONE);
								relativeLayoutCoupon.setVisibility(View.VISIBLE);
								relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);

								if (PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode) {                    // wallet
									textViewCouponDiscountedFare.setText("Rs. " + Utils.getDecimalFormatForMoney().format(Data.endRideData.toPay));
									textViewCouponTitle.setText(autoCustomerInfo.couponInfo.title + "\n& Wallet");
									Log.i("coupontitle", autoCustomerInfo.couponInfo.title);
									textViewCouponSubTitle.setVisibility(View.GONE);
								} else {                                                                            // no wallet
									textViewCouponDiscountedFare.setText("Rs. " + Utils.getDecimalFormatForMoney().format(Data.endRideData.toPay));
									textViewCouponTitle.setText(autoCustomerInfo.couponInfo.title);
									Log.i("coupontitle", autoCustomerInfo.couponInfo.title);
									textViewCouponSubTitle.setText(autoCustomerInfo.couponInfo.subtitle);
									textViewCouponSubTitle.setVisibility(View.VISIBLE);
								}

								textViewCouponPayTakeText.setText("Take");
							} else {
								throw new Exception();
							}
						} else {
							throw new Exception();
						}
					} else if (autoCustomerInfo.promoInfo != null) {
						if (autoCustomerInfo.promoInfo.promoApplied) {
							if (BenefitType.CASHBACKS.getOrdinal() != autoCustomerInfo.promoInfo.benefitType) {
								endRideInfoRl.setVisibility(View.GONE);
								relativeLayoutCoupon.setVisibility(View.VISIBLE);
								relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);


								if (PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode) {                    // wallet
									textViewCouponDiscountedFare.setText("Rs. " + Utils.getDecimalFormatForMoney().format(Data.endRideData.toPay));
									textViewCouponTitle.setText(autoCustomerInfo.promoInfo.title + "\n& Wallet");
									textViewCouponSubTitle.setVisibility(View.GONE);
								} else {                                                                            // no wallet
									textViewCouponDiscountedFare.setText("Rs. " + Utils.getDecimalFormatForMoney().format(Data.endRideData.toPay));
									textViewCouponTitle.setText(autoCustomerInfo.promoInfo.title);
									textViewCouponSubTitle.setVisibility(View.GONE);
								}
								textViewCouponPayTakeText.setText("Take");
							} else {
								throw new Exception();
							}
						} else {
							throw new Exception();
						}
					} else {
						throw new Exception();
					}
				} else if (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType) {
					reviewFareInfoInnerRl.setVisibility(View.GONE);
					FatafatOrderInfo fatafatOrderInfo = (FatafatOrderInfo) Data.assignedCustomerInfo;

					endRideInfoRl.setVisibility(View.GONE);
					relativeLayoutCoupon.setVisibility(View.GONE);
					relativeLayoutFatafatCustomerAmount.setVisibility(View.VISIBLE);

					double finalDiscountedPrice = fatafatOrderInfo.deliveryInfo.finalPrice - fatafatOrderInfo.deliveryInfo.discount;
					if (finalDiscountedPrice < 0) {
						finalDiscountedPrice = 0;
					}

					textViewFatafatBillAmountValue.setText("Rs. " + Utils.getDecimalFormatForMoney().format(fatafatOrderInfo.deliveryInfo.finalPrice));
					textViewFatafatBillDiscountValue.setText("Rs. " + Utils.getDecimalFormatForMoney().format(fatafatOrderInfo.deliveryInfo.discount));
					textViewFatafatBillFinalAmountValue.setText("Rs. " + Utils.getDecimalFormatForMoney().format(finalDiscountedPrice));
					textViewFatafatBillJugnooCashValue.setText("Rs. " + Utils.getDecimalFormatForMoney().format(fatafatOrderInfo.deliveryInfo.paidFromWallet));
					textViewFatafatBillToPay.setText("Rs. " + Utils.getDecimalFormatForMoney().format(fatafatOrderInfo.deliveryInfo.customerToPay));
				} else {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType) {
				if (PaymentMode.WALLET.getOrdinal() == Data.endRideData.paymentMode) {                                // wallet
					textViewCouponDiscountedFare.setText("Rs. " + Utils.getDecimalFormatForMoney().format(Data.endRideData.toPay));
					textViewCouponTitle.setText("Wallet");
					textViewCouponSubTitle.setVisibility(View.GONE);

					textViewCouponPayTakeText.setText("Take");

					endRideInfoRl.setVisibility(View.GONE);
					relativeLayoutCoupon.setVisibility(View.VISIBLE);
					relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
				} else {                                                                            // no wallet
					endRideInfoRl.setVisibility(View.VISIBLE);
					relativeLayoutCoupon.setVisibility(View.GONE);
					relativeLayoutFatafatCustomerAmount.setVisibility(View.GONE);
				}
			}
		}
	}


	private void updateFareFactorInEngagedState() {
		try {
			if (Data.fareStructure != null) {
				if (Data.fareStructure.fareFactor > 1 || Data.fareStructure.fareFactor < 1) {
					textViewInRideFareFactor.setVisibility(View.GONE);
//					textViewInRideFareFactor.setVisibility(View.VISIBLE);
//					textViewInRideFareFactor.setText("Rate: " + decimalFormat.format(Data.fareStructure.fareFactor) + "x");
				} else {
					textViewInRideFareFactor.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setAssignedCustomerInfoToViews(DriverScreenMode mode) {
		try {
			updateFareFactorInEngagedState();

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

					if (DriverScreenMode.D_ARRIVED == mode || DriverScreenMode.D_START_RIDE == mode) {
						textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
						buttonDriverNavigation.setVisibility(View.GONE);
						updateCustomerPickupAddress(Data.assignedCustomerInfo.requestlLatLng);
					} else {
						textViewCustomerPickupAddress.setVisibility(View.GONE);
						buttonDriverNavigation.setVisibility(View.GONE);
						if (((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng != null) {
							updateCustomerPickupAddress(((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng);
						}
					}

					textViewAfterAcceptRequestInfo.setVisibility(View.GONE);
					textViewAfterAcceptAmount.setVisibility(View.GONE);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType) {
				try {
					driverScheduledRideText.setVisibility(View.GONE);
					driverFreeRideIcon.setVisibility(View.GONE);
					textViewAfterAcceptRequestInfo.setVisibility(View.VISIBLE);
					textViewAfterAcceptAmount.setVisibility(View.VISIBLE);

					if (DriverScreenMode.D_ARRIVED == mode || DriverScreenMode.D_START_RIDE == mode) {
						textViewCustomerPickupAddress.setVisibility(View.GONE);
						driverPassengerName.setText(Data.assignedCustomerInfo.name);
						textViewAfterAcceptRequestInfo.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).address);
						textViewAfterAcceptAmount.setText("Money to pay: Rs. " + Utils.getDecimalFormatForMoney().format(((FatafatOrderInfo) Data.assignedCustomerInfo).orderAmount));
					} else if (DriverScreenMode.D_IN_RIDE == mode) {
						if (((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo != null && ((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
							driverPassengerName.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).customerInfo.name);
							textViewAfterAcceptRequestInfo.setText(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryAddress);
							textViewAfterAcceptAmount.setText("Money to take: Rs. " + Utils.getDecimalFormatForMoney().format(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.customerToPay));
							textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
							textViewCustomerPickupAddress.setText("Order ID: " + ((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.orderId);
						} else {
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

	String address = "";

	private void updateCustomerPickupAddress(final LatLng latLng) {
		if (DriverScreenMode.D_ARRIVED == driverScreenMode || DriverScreenMode.D_START_RIDE == driverScreenMode) {
			textViewCustomerPickupAddress.setText("Loading...");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					address = MapUtils.getGAPIAddress(latLng, language);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (DriverScreenMode.D_ARRIVED == driverScreenMode || DriverScreenMode.D_START_RIDE == driverScreenMode) {
								textViewCustomerPickupAddress.setText(address);
								if ("".equalsIgnoreCase(address)) {
									buttonDriverNavigation.setVisibility(View.GONE);
								} else {
									buttonDriverNavigation.setVisibility(View.VISIBLE);
								}
							} else {
								if ("".equalsIgnoreCase(address)) {
									buttonDriverNavigation.setVisibility(View.GONE);
								} else {
									buttonDriverNavigation.setVisibility(View.VISIBLE);
								}
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


	public void setTextToFareInfoTextViews(TextView minFareValue, TextView fareAfterValue, TextView fareAfterText, TextView textViewConvenienceCharges) {

		minFareValue.setText("Rs " + Utils.getDecimalFormatForMoney().format(Data.fareStructure.fixedFare) + " for "
				+ decimalFormat.format(Data.fareStructure.thresholdDistance) + " km");

		fareAfterValue.setText("Rs " + Utils.getDecimalFormatForMoney().format(Data.fareStructure.farePerKm) + " per km + Rs "
				+ decimalFormat.format(Data.fareStructure.farePerMin) + " per min");

		SpannableString sstr = new SpannableString("Fare");
		final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
		sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		fareAfterText.setText("");
		fareAfterText.append(sstr);
		fareAfterText.append(" (after " + decimalFormat.format(Data.fareStructure.thresholdDistance) + " km)");

		if (Data.fareStructure.getEffectiveConvenienceCharge() > 0) {
			textViewConvenienceCharges.setVisibility(View.VISIBLE);
			textViewConvenienceCharges.setText("Convenience charges for customer: "
					+ getResources().getString(R.string.rupee) + " " + decimalFormat.format(Data.fareStructure.getEffectiveConvenienceCharge()));
		} else {
			textViewConvenienceCharges.setVisibility(View.GONE);
		}
	}


	@Override
	public synchronized void onGPSLocationChanged(Location location) {
		if (Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0) {
			HomeActivity.myLocation = location;
			zoomToCurrentLocationAtFirstLocationFix(location);
		}
	}


	@Override
	public void refreshLocationFetchers(Context context) {

	}


	void buildAlertMessageNoGps() {
		if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (gpsDialogAlert != null && gpsDialogAlert.isShowing()) {
			} else {
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
		} else {
			if (gpsDialogAlert != null && gpsDialogAlert.isShowing()) {
				gpsDialogAlert.dismiss();
			}
		}
	}

	public Dialog timeDialogAlert;

	@SuppressWarnings("deprecation")
	public void buildTimeSettingsAlertDialog(final Activity activity) {
		try {
			int autoTime = android.provider.Settings.System.getInt(activity.getContentResolver(), android.provider.Settings.System.AUTO_TIME);
			if (autoTime == 0) {
				if (timeDialogAlert != null && timeDialogAlert.isShowing()) {
				} else {
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
			} else {
				if (timeDialogAlert != null && timeDialogAlert.isShowing()) {
					timeDialogAlert.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static boolean checkIfUserDataNull(Activity activity) {
		Log.e("checkIfUserDataNull", "Data.userData = " + Data.userData);
		if (Data.userData == null) {
			activity.startActivity(new Intent(activity, SplashNewActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		} else {
			return false;
		}
	}


	public void initializeGPSForegroundLocationFetcher() {
//		if(gpsForegroundLocationFetcher == null){
//			gpsForegroundLocationFetcher = new GPSForegroundLocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD);
//		}
	}

	public void connectGPSListener() {
		disconnectGPSListener();
		try {
			initializeGPSForegroundLocationFetcher();
//			gpsForegroundLocationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectGPSListener() {
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

		if (!checkIfUserDataNull(HomeActivity.this)) {
			setUserData();

			connectGPSListener();

			initializeFusedLocationFetchers();

			if (UserMode.DRIVER == userMode) {
				buildTimeSettingsAlertDialog(this);
			}

		}
		resumed = true;
		language = Locale.getDefault().getLanguage();
		long timediff = System.currentTimeMillis() - fetchHeatMapTime;
		if (timediff > Constants.HEAT_MAP_FETCH_DELAY) {
			fetchHeatMapData(HomeActivity.this);
			fetchHeatMapTime = System.currentTimeMillis();
		}
		stopService(new Intent(HomeActivity.this, GeanieView.class));


	}


	@Override
	protected void onPause() {

		GCMIntentService.clearNotifications(getApplicationContext());
		saveDataOnPause(false);

		try {
			if (userMode == UserMode.DRIVER) {
				if (driverScreenMode != DriverScreenMode.D_IN_RIDE) {
					disconnectGPSListener();
					destroyFusedLocationFetchers();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onPause();
	}


	@Override
	public void onBackPressed() {
		try {
			if (userMode == UserMode.DRIVER) {
				if (driverScreenMode == DriverScreenMode.D_IN_RIDE
						|| driverScreenMode == DriverScreenMode.D_START_RIDE
						|| driverScreenMode == DriverScreenMode.D_ARRIVED) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
				} else {
					ActivityCompat.finishAffinity(this);
				}
			} else {
				ActivityCompat.finishAffinity(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ActivityCompat.finishAffinity(this);
		}
	}


	@Override
	public void onDestroy() {
		try {
			saveDataOnPause(true);

			GCMIntentService.clearNotifications(HomeActivity.this);
			GCMIntentService.stopRing(true);

			MeteringService.clearNotifications(HomeActivity.this);

			disconnectGPSListener();

			destroyFusedLocationFetchers();

			ASSL.closeActivity(drawerLayout);

			appInterruptHandler = null;

			Database2.getInstance(this).close();

			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}


	public void saveDataOnPause(final boolean stopWait) {
		try {
			if (userMode == UserMode.DRIVER) {

				SharedPreferences pref = getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				Editor editor = pref.edit();

				if (driverScreenMode == DriverScreenMode.D_IN_RIDE) {

					if (stopWait) {
						if (rideTimeChronometer.isRunning) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										rideTimeChronometer.stop();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						}
					}


					editor.putString(Data.SP_TOTAL_DISTANCE, "" + totalDistance);


					editor.putString(Data.SP_LAST_LOCATION_TIME, "" + HomeActivity.this.lastLocationTime);

					if (HomeActivity.this.lastLocation != null) {
						if (Utils.compareDouble(HomeActivity.this.lastLocation.getLatitude(), 0) != 0 && Utils.compareDouble(HomeActivity.this.lastLocation.getLongitude(), 0) != 0) {
							editor.putString(Data.SP_LAST_LATITUDE, "" + HomeActivity.this.lastLocation.getLatitude());
							editor.putString(Data.SP_LAST_LONGITUDE, "" + HomeActivity.this.lastLocation.getLongitude());
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


	private double getTotalFare(double totalDistance, long elapsedTimeInMillis, long waitTimeInMillis) {
		double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

		if (BusinessType.AUTOS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()) {
			if (((AutoCustomerInfo) Data.assignedCustomerInfo).waitingChargesApplicable == 1) {
				elapsedTimeInMillis = elapsedTimeInMillis - waitTimeInMillis;
			} else {
				waitTimeInMillis = 0;
			}
		} else {
			waitTimeInMillis = 0;
		}

		long rideTimeSeconds = elapsedTimeInMillis / 1000;
		double rideTimeInMin = Math.ceil(rideTimeSeconds / 60);

		long waitTimeSeconds = waitTimeInMillis / 1000;
		double waitTimeInMin = Math.ceil(waitTimeSeconds / 60);

		return Data.fareStructure.calculateFare(totalDistanceInKm, rideTimeInMin, waitTimeInMin);
	}

	//totalDistance, rideTimeChronometer.eclipsedTime
	public synchronized void updateDistanceFareTexts(double distance, long elapsedTime, long waitTime) {
		try {
			double totalDistanceInKm = Math.abs(distance / 1000.0);

			driverIRDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " km");
			driverWaitValue.setText(Utils.getChronoTimeFromMillis(waitTime));

			if (Data.fareStructure != null) {
				driverIRFareValue.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(getTotalFare(distance,
						elapsedTime, waitTime)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public synchronized void displayOldPath() {

		try {
			ArrayList<CurrentPathItem> currentPathItemsArr = Database2.getInstance(HomeActivity.this).getCurrentPathItemsSaved();

			LatLng firstLatLng = null;

			PolylineOptions polylineOptions = new PolylineOptions();
			polylineOptions.width(ASSL.Xscale() * 5);
			polylineOptions.color(MAP_PATH_COLOR);
			polylineOptions.geodesic(true);

			for (CurrentPathItem currentPathItem : currentPathItemsArr) {
				if (1 != currentPathItem.googlePath) {
					polylineOptions.add(currentPathItem.sLatLng, currentPathItem.dLatLng);
					if (firstLatLng == null) {
						firstLatLng = currentPathItem.sLatLng;
					}
				}
			}


			if (Color.TRANSPARENT != MAP_PATH_COLOR) {
				map.addPolyline(polylineOptions);
			}


			if (firstLatLng == null) {
				firstLatLng = Data.startRidePreviousLatLng;
			}

			if (firstLatLng != null) {
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
		TextView textViewRequestAddress, textViewRequestDistance, textViewRequestTime,
				textViewOtherRequestDetails, textViewRequestFareFactor;
		Button buttonAcceptRide, buttonCancelRide;
//		ImageView imageViewRequestType;
		RelativeLayout relative;
		int id;
	}

	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRequest holder;

		ArrayList<DriverRideRequest> driverRideRequests;

		Handler handlerRefresh;
		Runnable runnableRefresh;

		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			driverRideRequests = new ArrayList<DriverRideRequest>();
			handlerRefresh = new Handler();
			runnableRefresh = new Runnable() {
				@Override
				public void run() {
					if (DriverScreenMode.D_INITIAL == driverScreenMode) {
						DriverRequestListAdapter.this.notifyDataSetChanged();
					}
					handlerRefresh.postDelayed(runnableRefresh, 1000);
				}
			};
			handlerRefresh.postDelayed(runnableRefresh, 1000);
		}

		@Override
		public int getCount() {
			return driverRideRequests.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public synchronized void setResults(ArrayList<DriverRideRequest> driverRideRequests) {
			if (this.driverRideRequests == null) {
				this.driverRideRequests = new ArrayList<DriverRideRequest>();
			}
			this.driverRideRequests.clear();
			this.driverRideRequests.addAll(driverRideRequests);
			this.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				holder = new ViewHolderDriverRequest();
				convertView = mInflater.inflate(R.layout.list_item_driver_request, null);

				holder.textViewRequestAddress = (TextView) convertView.findViewById(R.id.textViewRequestAddress);
				holder.textViewRequestAddress.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestDistance = (TextView) convertView.findViewById(R.id.textViewRequestDistance);
				holder.textViewRequestDistance.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestTime = (TextView) convertView.findViewById(R.id.textViewRequestTime);
				holder.textViewRequestTime.setTypeface(Data.latoRegular(getApplicationContext()));
//				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				holder.textViewOtherRequestDetails = (TextView) convertView.findViewById(R.id.textViewOtherRequestDetails);
				holder.textViewOtherRequestDetails.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestFareFactor = (TextView) convertView.findViewById(R.id.textViewRequestFareFactor);
				holder.textViewRequestFareFactor.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
				holder.buttonAcceptRide = (Button) convertView.findViewById(R.id.buttonAcceptRide);
				holder.buttonAcceptRide.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.buttonCancelRide = (Button) convertView.findViewById(R.id.buttonCancelRide);
				holder.buttonCancelRide.setTypeface(Data.latoRegular(getApplicationContext()));


				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);
				holder.buttonAcceptRide.setTag(holder);
				holder.buttonCancelRide.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRequest) convertView.getTag();
			}


			DriverRideRequest driverRideRequest = driverRideRequests.get(position);

			holder.id = position;

			holder.textViewRequestAddress.setText(driverRideRequest.address);


			long timeDiff = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), driverRideRequest.startTime);
			long timeDiffInSec = timeDiff / 1000;
			holder.textViewRequestTime.setText("" + timeDiffInSec + " sec left");

			if (myLocation != null) {
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				double distance = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), driverRideRequest.latLng);
				distance = distance * 1.5;
				if (distance >= 1000) {
					holder.textViewRequestDistance.setText("" + decimalFormatNoDecimal.format(distance / 1000) + " km away");
				} else {
					holder.textViewRequestDistance.setText("" + decimalFormatNoDecimal.format(distance) + " m away");
				}
			} else {
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}


			if(BusinessType.AUTOS == driverRideRequest.businessType){
//				holder.imageViewRequestType.setImageResource(R.drawable.request_autos);
				holder.textViewOtherRequestDetails.setVisibility(View.GONE);
			}
			else if(BusinessType.MEALS == driverRideRequest.businessType){
//				holder.imageViewRequestType.setImageResource(R.drawable.request_meals);
				holder.textViewOtherRequestDetails.setVisibility(View.VISIBLE);
				holder.textViewOtherRequestDetails.setText("Ride Time: "+((MealRideRequest)driverRideRequest).rideTime);
			}
			else if(BusinessType.FATAFAT == driverRideRequest.businessType){
//				holder.imageViewRequestType.setImageResource(R.drawable.request_fatafat);
				holder.textViewOtherRequestDetails.setVisibility(View.VISIBLE);
				holder.textViewOtherRequestDetails.setText("Cash Needed: Rs. " + ((FatafatRideRequest) driverRideRequest).orderAmount);
			}


			if (driverRideRequest.fareFactor > 1 || driverRideRequest.fareFactor < 1) {
				holder.textViewRequestFareFactor.setVisibility(View.VISIBLE);
				holder.textViewRequestFareFactor.setText("Rate: " + decimalFormat.format(driverRideRequest.fareFactor) + "x");
			} else {
				holder.textViewRequestFareFactor.setVisibility(View.GONE);
			}


			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						holder = (ViewHolderDriverRequest) v.getTag();

						DriverRideRequest driverRideRequest = driverRideRequests.get(holder.id);

						Data.dEngagementId = driverRideRequest.engagementId;
						Data.dCustomerId = driverRideRequest.customerId;
						Data.dCustLatLng = driverRideRequest.latLng;
						Data.openedDriverRideRequest = driverRideRequest;

						driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
						switchDriverScreen(driverScreenMode);

						map.animateCamera(CameraUpdateFactory.newLatLng(driverRideRequest.latLng), 1000, null);
						FlurryEventLogger.event(RIDE_CHECKED);
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

						DriverRideRequest driverRideRequest = driverRideRequests.get(holder.id);

						Data.dEngagementId = driverRideRequest.engagementId;
						Data.dCustomerId = driverRideRequest.customerId;
						Data.dCustLatLng = driverRideRequest.latLng;
						Data.openedDriverRideRequest = driverRideRequest;
						acceptRequestFunc();
						FlurryEventLogger.event(RIDE_ACCEPTED);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.buttonCancelRide.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						holder = (ViewHolderDriverRequest) v.getTag();

						DriverRideRequest driverRideRequest = driverRideRequests.get(holder.id);

						Data.dEngagementId = driverRideRequest.engagementId;
						Data.dCustomerId = driverRideRequest.customerId;
						Data.dCustLatLng = driverRideRequest.latLng;
						Data.openedDriverRideRequest = driverRideRequest;

						rejectRequestFunc();
						FlurryEventLogger.event(RIDE_CANCELLED);


					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}


	}


	public float getBatteryPercentage() {
		try {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = (level / (float) scale) * 100;

			// Are we charging / charged?
			int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_FULL;
			if (isCharging) {
				return 70;
			} else {
				return batteryPct;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 70;
		}
	}







//	public void getDriverOnlineHours(final Activity activity) {
//		try {
//			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//				progressBarDriverOnlineHours.setVisibility(View.VISIBLE);
//				textViewTitleBarOvalText.setVisibility(View.GONE);
//				RestClient.getApiServices().dailyOnlineHours(Data.userData.accessToken, new Callback<RegisterScreenResponse>() {
//					@Override
//					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
//						try {
//							progressBarDriverOnlineHours.setVisibility(View.GONE);
//							textViewTitleBarOvalText.setVisibility(View.VISIBLE);
//							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
//							JSONObject jObj;
//							jObj = new JSONObject(jsonString);
//							int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
//							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
//								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
//									String onlineHours = jObj.getString("driver_online_hours");
//									Log.i("Online hours",onlineHours);
//									if(Data.userData != null) {
//										Data.userData.driverOnlineHours = onlineHours;
//										setUserData();
//									}
//								}
//							}
//						} catch (Exception exception) {
//							exception.printStackTrace();
//						}
//					}
//
//					@Override
//					public void failure(RetrofitError error) {
//						progressBarDriverOnlineHours.setVisibility(View.GONE);
//						textViewTitleBarOvalText.setVisibility(View.VISIBLE);
//					}
//				});
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			progressBarDriverOnlineHours.setVisibility(View.GONE);
//			textViewTitleBarOvalText.setVisibility(View.VISIBLE);
//		}
//
//	}



//	Retrofit

	public void driverAcceptRideAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			initializeStartRideVariables();
			DialogPopup.showLoadingDialog(activity, "Loading...");

//			final RequestParams params = new RequestParams();

			if (myLocation != null) {
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("latitude", "" + Data.latitude);
			params.put("longitude", "" + Data.longitude);

			params.put("device_name", Utils.getDeviceName());
			params.put("imei", DeviceUniqueID.getUniqueId(this));
			params.put("app_version", "" + Utils.getAppVersion(this));

			if (Data.openedDriverRideRequest != null) {
				params.put("reference_id", "" + Data.openedDriverRideRequest.referenceId);
				Log.i("request", String.valueOf(params));
			}
			GCMIntentService.cancelUploadPathAlarm(HomeActivity.this);
			RestClient.getApiServices().driverAcceptRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {

					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);

						Log.e("accept_a_request jsonString", "=" + jsonString);
						if (!jObj.isNull("error")) {

							driverRideRequestsList.setVisibility(View.VISIBLE);
							int flag = jObj.getInt("flag");
							Log.e("accept_a_request flag", "=" + flag);
							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}

							DialogPopup.dismissLoadingDialog();

							reduceRideRequest(Data.dEngagementId);

						} else {

							int flag = ApiResponseFlags.RIDE_ACCEPTED.getOrdinal();

							if (jObj.has("flag")) {
								flag = jObj.getInt("flag");
							}

							if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
								if (jObj.has("fare_details")) {
									try {
										Data.fareStructure = JSONParser.parseFareObject(jObj.getJSONObject("fare_details"));
									} catch (Exception e) {
										Log.w("fareStructure", "e=" + e.toString());
									}
								}
								if (jObj.has("fare_factor")) {
									try {
										Data.fareStructure.fareFactor = jObj.getDouble("fare_factor");
									} catch (Exception e) {
										Log.w("fareFactor", "e=" + e.toString());
									}
								}
								if (jObj.has("luggage_charges")) {
									try {
										Data.fareStructure.luggageFare = jObj.getDouble("luggage_charges");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (jObj.has("convenience_charge")) {
									try {
										Data.fareStructure.convenienceCharge = jObj.getDouble("convenience_charge");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (jObj.has("convenience_charge_waiver")) {
									try {
										Data.fareStructure.convenienceChargeWaiver = jObj.getDouble("convenience_charge_waiver");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
//									"convenience_charge": 10,
//									"convenience_charge_waiver": 0,


								//"http://jugnoo-images.s3.amazonaws.com/user_profile/user.png";

								int referenceId = jObj.getInt("reference_id");
								int businessId = jObj.getInt("business_id");

								if (BusinessType.AUTOS.getOrdinal() == businessId) {


									double jugnooBalance = 0;


									JSONObject userData = jObj.getJSONObject("user_data");

									String userName = userData.getString("user_name");
									String userImage = userData.getString("user_image");
									String phoneNo = userData.getString("phone_no");
									String rating = "4";
									try {
										rating = userData.getString("user_rating");
									} catch (Exception e) {
									}
									if (userData.has("jugnoo_balance")) {
										jugnooBalance = userData.getDouble("jugnoo_balance");
										Log.i("jugnooblance", String.valueOf(jugnooBalance));
									}

									double pickupLatitude = jObj.getDouble("pickup_latitude");
									double pickupLongitude = jObj.getDouble("pickup_longitude");

									LatLng pickuplLatLng = new LatLng(pickupLatitude, pickupLongitude);

									int isScheduled = 0;
									String pickupTime = "";
									if (jObj.has("is_scheduled")) {
										isScheduled = jObj.getInt("is_scheduled");
										if (isScheduled == 1 && jObj.has("pickup_time")) {
											pickupTime = jObj.getString("pickup_time");
										}
									}

									int freeRide = 0;
									if (jObj.has("free_ride")) {
										freeRide = jObj.getInt("free_ride");
									}

									CouponInfo couponInfo = null;
									if (jObj.has("coupon")) {
										try {
											couponInfo = JSONParser.parseCouponInfo(jObj.getJSONObject("coupon"));
										} catch (Exception e) {
											Log.w("couponInfo", "e=" + e.toString());
										}
									}

									PromoInfo promoInfo = null;
									if (jObj.has("promotion")) {
										try {
											promoInfo = JSONParser.parsePromoInfo(jObj.getJSONObject("promotion"));
										} catch (Exception e) {
											Log.w("promoInfo", "e=" + e.toString());
										}
									}

									int meterFareApplicable = jObj.optInt("meter_fare_applicable", 0);
									int getJugnooFareEnabled = jObj.optInt("get_jugnoo_fare_enabled", 1);
									int luggageChargesApplicable = jObj.optInt("luggage_charges_applicable", 0);
									int waitingChargesApplicable = jObj.optInt("waiting_charges_applicable", 0);
									long eta = jObj.optLong("eta", 1000000);
									int cachedApiEnabled = jObj.optInt(KEY_CACHED_API_ENABLED, 0);

									Data.assignedCustomerInfo = new AutoCustomerInfo(Integer.parseInt(Data.dEngagementId),
											Integer.parseInt(Data.dCustomerId), referenceId,
											userName, phoneNo, pickuplLatLng, cachedApiEnabled,
											userImage, rating, pickupTime, freeRide, couponInfo, promoInfo, jugnooBalance,
											meterFareApplicable, getJugnooFareEnabled, luggageChargesApplicable, waitingChargesApplicable,eta);



									Data.driverRideRequests.clear();

									GCMIntentService.clearNotifications(getApplicationContext());

									initializeStartRideVariables();

									driverScreenMode = DriverScreenMode.D_ARRIVED;
									switchDriverScreen(driverScreenMode);

								} else if (BusinessType.MEALS.getOrdinal() == businessId) {

								} else if (BusinessType.FATAFAT.getOrdinal() == businessId) {

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

									initializeStartRideVariables();

									driverScreenMode = DriverScreenMode.D_ARRIVED;
									switchDriverScreen(driverScreenMode);

								} else {
									driverRideRequestsList.setVisibility(View.VISIBLE);
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								}


							} else {
								driverRideRequestsList.setVisibility(View.VISIBLE);
								try {
									Log.e("accept_a_request flag", "=" + flag);
									String logMessage = jObj.getString("log");
									DialogPopup.alertPopup(activity, "", "" + logMessage);
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											DialogPopup.dismissAlertPopup();
										}
									}, 3000);
									reduceRideRequest(Data.dEngagementId);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
						driverRideRequestsList.setVisibility(View.VISIBLE);
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					driverRideRequestsList.setVisibility(View.VISIBLE);
					DialogPopup.dismissLoadingDialog();
					callAndHandleStateRestoreAPI();
				}
			});


		} else {
			driverRideRequestsList.setVisibility(View.VISIBLE);
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}


	public void reduceRideRequest(String engagementId) {
		Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
	}


	public void driverRejectRequestAsync(final Activity activity) {

		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

//			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);

			if (Data.openedDriverRideRequest != null) {
				params.put("reference_id", "" + Data.openedDriverRideRequest.referenceId);
			}
			RestClient.getApiServices().driverRejectRequestRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {

							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {
							try {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
									String log = jObj.getString("log");
									DialogPopup.alertPopup(activity, "", "" + log);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}


							if (map != null) {
								map.clear();
								drawHeatMapData(heatMapResponseGlobal);
							}
							stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

							reduceRideRequest(Data.dEngagementId);

						}

						new DriverTimeoutCheck().timeoutBuffer(activity,false);
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

//					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
//					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}


	}


//Retrofit

	public void driverMarkArriveRideAsync(final Activity activity, final LatLng driverAtPickupLatLng) {

		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("pickup_latitude", "" + driverAtPickupLatLng.latitude);
			params.put("pickup_longitude", "" + driverAtPickupLatLng.longitude);
			params.put("dryrun_distance", "" + totalDistance);
			Log.i("dryrun_distance", String.valueOf(totalDistance));

			if (Data.assignedCustomerInfo != null) {
				params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);
			}
			RestClient.getApiServices().driverMarkArriveRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = ApiResponseFlags.ACTION_COMPLETE.getOrdinal();
						if (jObj.has("flag")) {
							flag = jObj.getInt("flag");
						}

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								DialogPopup.alertPopup(activity, "", error);
							} else if (ApiResponseFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
								String message = jObj.getString("message");
								callAndHandleStateRestoreAPI();
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

								Database2.getInstance(activity).insertRideData("0.0", "0.0", "" + System.currentTimeMillis());
								Log.writePathLogToFile(GpsDistanceCalculator.getEngagementIdFromSP(activity) + "m", "arrived sucessful");

								driverScreenMode = DriverScreenMode.D_START_RIDE;
								switchDriverScreen(driverScreenMode);
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					callAndHandleStateRestoreAPI();
				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	public void initializeStartRideVariables() {
		Utils.deleteCache(this);

		lastLocation = null;
		lastLocationTime = System.currentTimeMillis();

		if (DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal() == driverScreenMode.getOrdinal()
				|| DriverScreenMode.D_ARRIVED.getOrdinal() == driverScreenMode.getOrdinal()
				|| DriverScreenMode.D_START_RIDE.getOrdinal() == driverScreenMode.getOrdinal()
				|| DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode.getOrdinal()) {

		} else {
			Database2.getInstance(this).deleteRideData();
		}
		Database2.getInstance(this).deleteAllCurrentPathItems();
		Database.getInstance(this).deleteSavedPath();

		HomeActivity.previousWaitTime = 0;
		HomeActivity.previousRideTime = 0;
		HomeActivity.totalDistance = -1;

		clearSPData();

		MeteringService.gpsInstance(this).saveEngagementIdToSP(this, Data.dEngagementId);
		MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, driverScreenMode);
		MeteringService.gpsInstance(this).stop();
		Prefs.with(HomeActivity.this).save(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0);

		waitStart = 2;
	}


//	retrofit


	public void driverStartRideAsync(final Activity activity, final LatLng driverAtPickupLatLng) {
		initializeStartRideVariables();

		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", Data.dCustomerId);
			params.put("pickup_latitude", "" + driverAtPickupLatLng.latitude);
			params.put("pickup_longitude", "" + driverAtPickupLatLng.longitude);

			if (Data.assignedCustomerInfo != null) {
				params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);
			}

			Log.i("params", "=" + params);

			RestClient.getApiServices().driverStartRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {

							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {

							int flag = ApiResponseFlags.RIDE_STARTED.getOrdinal();

							if (jObj.has("flag")) {
								flag = jObj.getInt("flag");
							}


							if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {
								if ((Data.assignedCustomerInfo != null) && (BusinessType.FATAFAT.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal())) {

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

									((FatafatOrderInfo) Data.assignedCustomerInfo).setCustomerDeliveryInfo(customerInfo, deliveryInfo);
								} else if ((Data.assignedCustomerInfo != null) && (BusinessType.AUTOS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal())) {
									double dropLatitude = 0, dropLongitude = 0;
									try {
										if (jObj.has("op_drop_latitude") && jObj.has("op_drop_longitude")) {
											dropLatitude = jObj.getDouble("op_drop_latitude");
											dropLongitude = jObj.getDouble("op_drop_longitude");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if ((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)) {
										((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng = null;
									} else {
										((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng = new LatLng(dropLatitude, dropLongitude);
									}
								}
							}


							if (map != null) {
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
						new DriverTimeoutCheck().clearCount(activity);
						Prefs.with(HomeActivity.this).save(SPLabels.CUSTOMER_PHONE_NUMBER, Data.assignedCustomerInfo.phoneNumber);
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					callAndHandleStateRestoreAPI();
				}
			});

		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


//	Retro

	public void driverCancelRideAsync(final Activity activity) {

		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);

			if (Data.assignedCustomerInfo != null) {
				params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);
			}
			RestClient.getApiServices().driverCancelRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);


						if (!jObj.isNull("error")) {

							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {

							try {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
									String log = jObj.getString("log");
									DialogPopup.alertPopup(activity, "", "" + log);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (map != null) {
								map.clear();
							}
							stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
							reduceRideRequest(Data.dEngagementId);
						}
						new DriverTimeoutCheck().timeoutBuffer(activity,true);
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					callAndHandleStateRestoreAPI();

				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}


	}


	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude,
								   long rideTimeInMillis, long waitTimeInMillis,
								   int flagDistanceTravelled, final BusinessType businessType) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			if (BusinessType.AUTOS == businessType) {

				if (Data.assignedCustomerInfo != null) {
					if (BusinessType.AUTOS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal()) {
						if (((AutoCustomerInfo) Data.assignedCustomerInfo).waitingChargesApplicable != 1) {
							waitTimeInMillis = 0;
						}
					}
				}

				autoEndRideAPI(activity, lastAccurateLatLng, dropLatitude, dropLongitude,
						rideTimeInMillis, waitTimeInMillis, flagDistanceTravelled, businessType);
			} else if (BusinessType.FATAFAT == businessType) {
				fatafatEndRideAPI(activity, lastAccurateLatLng, dropLatitude, dropLongitude,
						rideTimeInMillis, waitTimeInMillis, flagDistanceTravelled, businessType);
			}
		} else {
			if (DriverScreenMode.D_BEFORE_END_OPTIONS != driverScreenMode) {
				driverScreenMode = DriverScreenMode.D_IN_RIDE;
				rideTimeChronometer.start();
			}
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	private long getElapsedRideTimeFromSPInMillis(Activity activity, long rideTimeInMillisChrono) {
		SharedPreferences pref = activity.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis()));
		long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
		if (timeDiffToAdd > 0) {
			rideTimeInMillisChrono = timeDiffToAdd;
		}
		return rideTimeInMillisChrono;
	}

	double enteredMeterFare = 0;

//	Retrofit

	public void autoEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, final double dropLatitude, final double dropLongitude,
							   long rideTimeInMillis, long waitTimeInMillis,
							   int flagDistanceTravelled, final BusinessType businessType) {
		DialogPopup.showLoadingDialog(activity, "Loading...");


		double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

		double Limit_endRideMinute = 360;
		double Average_endRideMinute = totalDistanceInKm * 2;

		rideTimeInMillis = getElapsedRideTimeFromSPInMillis(activity, rideTimeInMillis);
		double rideMinutes = Math.ceil(rideTimeInMillis / 60000);
		double waitMinutes = Math.ceil(waitTimeInMillis / 60000);
		if (rideMinutes < Limit_endRideMinute) {
			rideTime = decimalFormatNoDecimal.format(rideMinutes);
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		} else {
			rideMinutes = Average_endRideMinute;
			rideTimeInMillis = (long) (Average_endRideMinute * 60000.0);
			rideTime = String.valueOf(decimalFormatNoDecimal.format(Average_endRideMinute));
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		}
		final long eoRideTimeInMillis = rideTimeInMillis;
		final long eoWaitTimeInMillis = waitTimeInMillis;


		final double totalHaversineDistanceInKm = Math.abs(totalHaversineDistance / 1000.0);

		final HashMap<String, String> params = new HashMap<>();

		params.put("access_token", Data.userData.accessToken);
		params.put("engagement_id", Data.dEngagementId);
		params.put("customer_id", Data.dCustomerId);
		params.put("latitude", "" + dropLatitude);
		params.put("longitude", "" + dropLongitude);
		params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
		params.put("wait_time", waitTime);
		params.put("ride_time", rideTime);
		params.put("is_cached", "0");
		params.put("flag_distance_travelled", "" + flagDistanceTravelled);
		params.put("last_accurate_latitude", "" + lastAccurateLatLng.latitude);
		params.put("last_accurate_longitude", "" + lastAccurateLatLng.longitude);
		params.put("ride_distance_using_haversine", "" + decimalFormat.format(totalHaversineDistanceInKm));


		enteredMeterFare = 0;

		if (Data.assignedCustomerInfo != null) {
			params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);

			try {
				if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType
						&& 1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable) {
					enteredMeterFare = Double.parseDouble(editTextEnterMeterFare.getText().toString().trim());
					params.put("meter_fare", "" + enteredMeterFare);
					params.put("fare_fetched_from_jugnoo", "" + fareFetchedFromJugnoo);
				}
			} catch (Exception e) {
			}

			try {
				if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType
						&& 1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).luggageChargesApplicable) {
					params.put("luggage_count", "" + luggageCountAdded);
				}
			} catch (Exception e) {
			}
		}


		params.put("business_id", "" + businessType.getOrdinal());

		Log.i("end_ride params =", "=" + params);

		if (Data.assignedCustomerInfo.getCachedApiEnabled() == 1) {
			endRideOffline(activity, PendingCall.END_RIDE.getPath(), params, eoRideTimeInMillis, eoWaitTimeInMillis,
					(AutoCustomerInfo) Data.assignedCustomerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded);
		} else {
			RestClient.getApiServices().autoEndRideAPIRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
							driverScreenMode = DriverScreenMode.D_IN_RIDE;
							rideTimeChronometer.start();
						} else {

							try {
								totalFare = jObj.getDouble("fare");
							} catch (Exception e) {
								e.printStackTrace();
								totalFare = 0;
							}

							JSONParser.parseEndRideData(jObj, Data.dEngagementId, totalFare);
							applyCouponAndPromoOnSuccess();

							if (map != null) {
								map.clear();
								drawHeatMapData(heatMapResponseGlobal);
							}

							waitStart = 2;
							rideTimeChronometer.stop();


							clearSPData();

							driverScreenMode = DriverScreenMode.D_RIDE_END;
							switchDriverScreen(driverScreenMode);

							driverUploadPathDataFileAsync(activity, Data.dEngagementId, totalHaversineDistanceInKm);


							initializeStartRideVariables();

						}
					} catch (Exception exception) {
						exception.printStackTrace();
						driverScreenMode = DriverScreenMode.D_IN_RIDE;
						rideTimeChronometer.start();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					AGPSRefresh.hardRefreshGpsData(HomeActivity.this);
					DialogPopup.dismissLoadingDialog();

				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("error", "=" + error);
					endRideOffline(activity, PendingCall.END_RIDE.getPath(), params, eoRideTimeInMillis, eoWaitTimeInMillis,
							(AutoCustomerInfo) Data.assignedCustomerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded);
				}


			});
		}
	}

	private void applyCouponAndPromoOnSuccess() {
		if (Data.assignedCustomerInfo != null) {
			if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType) {
				if (((AutoCustomerInfo) Data.assignedCustomerInfo).couponInfo != null) {
					((AutoCustomerInfo) Data.assignedCustomerInfo).couponInfo.couponApplied = true;
				} else if (((AutoCustomerInfo) Data.assignedCustomerInfo).promoInfo != null) {
					((AutoCustomerInfo) Data.assignedCustomerInfo).promoInfo.promoApplied = true;
				}
			}
		}
	}


	private double calculateCouponDiscount(double totalFare, CouponInfo couponInfo) {
		double finalDiscount = 0;

		if (BenefitType.DISCOUNTS.getOrdinal() == couponInfo.benefitType) {        //coupon discount
			finalDiscount = ((totalFare * couponInfo.discountPrecent) / 100) < couponInfo.maximumDiscountValue ?
					Math.ceil(((totalFare * couponInfo.discountPrecent) / 100)) : couponInfo.maximumDiscountValue;

			Log.i("coupon case discount", "((totalFare * assignedCustomerInfo.couponInfo.discountPrecent) / 100) = "
					+ ((totalFare * couponInfo.discountPrecent) / 100));
			Log.i("coupon case discount", "assignedCustomerInfo.couponInfo.maximumDiscountValue = " + couponInfo.maximumDiscountValue);
		} else if (BenefitType.CAPPED_FARE.getOrdinal() == couponInfo.benefitType) {        // coupon capped fare
			Log.i("coupon case capped", "assignedCustomerInfo.couponInfo.cappedFare = " + couponInfo.cappedFare);
			Log.i("coupon case capped", "assignedCustomerInfo.couponInfo.cappedFareMaximum = " + couponInfo.cappedFareMaximum);
			if (totalFare < couponInfo.cappedFare) {        // fare less than capped fare
				finalDiscount = 0;
			} else {                                                                // fare greater than capped fare
				double maxDiscount = couponInfo.cappedFareMaximum - couponInfo.cappedFare;
				finalDiscount = totalFare - couponInfo.cappedFare;
				finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
				Log.i("coupon case capped", "maxDiscount = " + maxDiscount);
			}
		} else {
			finalDiscount = 0;
		}
		couponInfo.couponApplied = true;

		return finalDiscount;
	}


	private double calculatePromoDiscount(double totalFare, PromoInfo promoInfo) {
		double finalDiscount = 0;

		if (BenefitType.DISCOUNTS.getOrdinal() == promoInfo.benefitType) {        //promotion discount
			finalDiscount = ((totalFare * promoInfo.discountPercentage) / 100) < promoInfo.discountMaximum ?
					Math.ceil(((totalFare * promoInfo.discountPercentage) / 100)) : promoInfo.discountMaximum;

			Log.i("promo case discount", "((totalFare * assignedCustomerInfo.promoInfo.discountPercentage) / 100) = "
					+ ((totalFare * promoInfo.discountPercentage) / 100));
			Log.i("promo case discount", "assignedCustomerInfo.promoInfo.discountMaximum = " + promoInfo.discountMaximum);

		} else if (BenefitType.CAPPED_FARE.getOrdinal() == promoInfo.benefitType) {        // promotion capped fare
			Log.i("promo case capped", "assignedCustomerInfo.promoInfo.cappedFare = " + promoInfo.cappedFare);
			Log.i("promo case capped", "assignedCustomerInfo.promoInfo.cappedFareMaximum = " + promoInfo.cappedFareMaximum);
			if (totalFare < promoInfo.cappedFare) {        // fare less than capped fare
				finalDiscount = 0;
			} else {                                                                // fare greater than capped fare
				double maxDiscount = promoInfo.cappedFareMaximum - promoInfo.cappedFare;
				finalDiscount = totalFare - promoInfo.cappedFare;
				finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
				Log.i("promo case capped", "maxDiscount = " + maxDiscount);
			}
		} else {
			finalDiscount = 0;
		}
		promoInfo.promoApplied = true;

		return finalDiscount;
	}


	/**
	 * Calculating discounted fare and other values in case of offline end ride (no internet)
	 *
	 * @param activity
	 * @param url
	 * @param params
	 * @param rideTime
	 * @param waitTime
	 * @param assignedCustomerInfo
	 */
	//TODO end ride offline
	public void endRideOffline(Activity activity, String url, HashMap<String, String> params, long rideTimeInMillis, long waitTimeInMillis,
							   AutoCustomerInfo assignedCustomerInfo, final double dropLatitude, final double dropLongitude, double enteredMeterFare, int luggageCountAdded) {
		try {

			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay;
			int paymentMode = PaymentMode.CASH.getOrdinal();

			double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

			Log.e("offline =============", "============");
			Log.i("rideTime", "=" + rideTime);

			try {
				if (assignedCustomerInfo != null && 1 == assignedCustomerInfo.meterFareApplicable) {
					totalFare = enteredMeterFare;
				} else {
					totalFare = getTotalFare(totalDistance, rideTimeInMillis, waitTimeInMillis);
				}
			} catch (Exception e) {
				e.printStackTrace();
				totalFare = 0;
			}

			try {
				if (assignedCustomerInfo != null && 1 == assignedCustomerInfo.luggageChargesApplicable) {
					totalFare = totalFare + (luggageCountAdded * Data.fareStructure.luggageFare);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			try {
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.fareStructure = " + Data.fareStructure);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "rideTime = " + rideTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "waitTime = " + waitTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalDistanceInKm = " + totalDistanceInKm);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalFare = " + totalFare);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "assignedCustomerInfo = " + assignedCustomerInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}


			actualFare = totalFare;

			finalToPay = totalFare;

			Log.i("totalFare == endride offline ", "=" + totalFare);
			Log.i("endRideOffline assignedCustomerInfo.couponInfo=", "=" + assignedCustomerInfo.couponInfo);
			Log.i("endRideOffline assignedCustomerInfo.promoInfo=", "=" + assignedCustomerInfo.promoInfo);


			LatLng dropLatLng = new LatLng(dropLatitude, dropLongitude);

			if (assignedCustomerInfo.couponInfo != null) {        // coupon
				if (CouponType.DROP_BASED.getOrdinal() == assignedCustomerInfo.couponInfo.couponType) {
					double distanceFromDrop = MapUtils.distance(dropLatLng, assignedCustomerInfo.couponInfo.droplLatLng);
					if (distanceFromDrop <= assignedCustomerInfo.couponInfo.dropRadius) {                                     // drop condition satisfied
						finalDiscount = calculateCouponDiscount(totalFare, assignedCustomerInfo.couponInfo);
					} else {
						finalDiscount = 0;
					}
				} else {
					finalDiscount = calculateCouponDiscount(totalFare, assignedCustomerInfo.couponInfo);
				}
			} else if (assignedCustomerInfo.promoInfo != null) {        // promotion
				if (PromotionType.DROP_BASED.getOrdinal() == assignedCustomerInfo.promoInfo.promoType) {
					double distanceFromDrop = MapUtils.distance(dropLatLng, assignedCustomerInfo.promoInfo.droplLatLng);
					if (distanceFromDrop <= assignedCustomerInfo.promoInfo.dropRadius) {                                     // drop condition satisfied
						finalDiscount = calculatePromoDiscount(totalFare, assignedCustomerInfo.promoInfo);
					} else {
						finalDiscount = 0;
					}
				} else {
					finalDiscount = calculatePromoDiscount(totalFare, assignedCustomerInfo.promoInfo);
				}
			} else {
				finalDiscount = 0;
			}
			Log.i("finalDiscount == endride offline ", "=" + finalDiscount);

			if (totalFare > finalDiscount) {                                    // final toPay (totalFare - discount)
				finalToPay = totalFare - finalDiscount;
			} else {
				finalToPay = 0;
				finalDiscount = totalFare;
			}

			Log.i("finalDiscount == endride offline ", "=" + finalDiscount);
			Log.i("finalToPay == endride offline ", "=" + finalToPay);


			// wallet application (with split fare)
			if (assignedCustomerInfo.jugnooBalance > 0 && finalToPay > 0) {    // wallet

				if (assignedCustomerInfo.jugnooBalance >= finalToPay) {
					finalPaidUsingWallet = finalToPay;
				} else {
					finalPaidUsingWallet = assignedCustomerInfo.jugnooBalance;
				}

				finalToPay = finalToPay - finalPaidUsingWallet;

				paymentMode = PaymentMode.WALLET.getOrdinal();

				params.put("payment_mode", "" + PaymentMode.WALLET.getOrdinal());
				params.put("paid_using_wallet", "" + Utils.getDecimalFormatForMoney().format(finalPaidUsingWallet));
			} else {                                                                            // no wallet
				finalPaidUsingWallet = 0;

				paymentMode = PaymentMode.CASH.getOrdinal();

				params.put("payment_mode", "" + PaymentMode.CASH.getOrdinal());
				params.put("paid_using_wallet", "" + Utils.getDecimalFormatForMoney().format(finalPaidUsingWallet));
			}


			Data.endRideData = new EndRideData(Data.dEngagementId, actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode);

			try {
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.endRideData = " + Data.endRideData);
			} catch (Exception e) {
				e.printStackTrace();
			}


			if (map != null) {
				map.clear();
			}

			waitStart = 2;
			rideTimeChronometer.stop();


			clearSPData();

			driverScreenMode = DriverScreenMode.D_RIDE_END;
			switchDriverScreen(driverScreenMode);


			params.put("is_cached", "1");

			DialogPopup.dismissLoadingDialog();
			Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			try {
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "url = " + url + " params = " + params);
			} catch (Exception e) {
				e.printStackTrace();
			}

			driverUploadPathDataFileAsync(activity, Data.dEngagementId, totalHaversineDistance);

			initializeStartRideVariables();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * API for uploading ride path data csv string to server
	 *
	 * @param activity
	 * @param engagementId
	 */


//Retrofit
	public void driverUploadPathDataFileAsync(final Activity activity, String engagementId, double totalHaversineDistance) {
		String rideDataStr = Database2.getInstance(activity).getRideData();
		if (!"".equalsIgnoreCase(rideDataStr)) {
			totalHaversineDistance = totalHaversineDistance / 1000;
			rideDataStr = rideDataStr + "\n" + totalHaversineDistance;

			final HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", engagementId);
			params.put("ride_path_data", rideDataStr);

			Log.i(TAG, "driverUploadPathDataFileAsync params=" + params);

			RestClient.getApiServices().driverUploadPathDataFileRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					Log.e(TAG, "driverUploadPathDataFileRetro response=" + new String(((TypedByteArray) response.getBody()).getBytes()));
				}

				@Override
				public void failure(RetrofitError error) {
					Database2.getInstance(activity).insertPendingAPICall(activity, PendingCall.UPLOAD_RIDE_DATA.getPath(), params);
				}
			});
		}
	}


	/**
	 * API for Fatafat end ride (Mark Delivered)
	 *
	 * @param activity
	 * @param lastAccurateLatLng
	 * @param dropLatitude
	 * @param dropLongitude
	 * @param waitMinutes
	 * @param rideMinutes
	 * @param flagDistanceTravelled
	 * @param businessType
	 */

//	Retrofit
	public void fatafatEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude,
								  long rideTimeInMillis, long waitTimeInMillis,
								  int flagDistanceTravelled, final BusinessType businessType) {


		double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

		double Limit_endRideMinute = 360;
		double Average_endRideMinute = totalDistanceInKm * 2;

		rideTimeInMillis = getElapsedRideTimeFromSPInMillis(activity, rideTimeInMillis);
		double rideMinutes = Math.ceil(rideTimeInMillis / 60000);
		double waitMinutes = Math.ceil(waitTimeInMillis / 60000);
		if (rideMinutes < Limit_endRideMinute) {
			rideTime = decimalFormatNoDecimal.format(rideMinutes);
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		} else {
			rideMinutes = Average_endRideMinute;
			rideTimeInMillis = (long) (Average_endRideMinute * 60000.0);
			rideTime = String.valueOf(decimalFormatNoDecimal.format(Average_endRideMinute));
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		}
		final long eoRideTimeInMillis = rideTimeInMillis;
		final long eoWaitTimeInMillis = waitTimeInMillis;


		final HashMap<String, String> params = new HashMap<String, String>();

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
			params.put("business_id", "" + businessType.getOrdinal());
			params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);

			DialogPopup.showLoadingDialog(activity, "Loading...");


			RestClient.getApiServices().fatafatEndRideAPIRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag) {

								try {
									totalFare = jObj.getDouble("fare");
								} catch (Exception e) {
									e.printStackTrace();
									totalFare = 0;
								}

								JSONParser.parseEndRideData(jObj, Data.dEngagementId, totalFare);

								JSONObject jDeliveryInfo = jObj.getJSONObject("delivery_info");
								double finalPrice = jDeliveryInfo.getDouble("final_price");
								double discount = jDeliveryInfo.getDouble("discount");
								double paidFromWallet = jDeliveryInfo.getDouble("paid_from_wallet");
								double customerToPay = jDeliveryInfo.getDouble("customer_to_pay");

								((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.updatePrices(finalPrice, discount, paidFromWallet, customerToPay);

								if (map != null) {
									map.clear();
								}

								waitStart = 2;
								rideTimeChronometer.stop();


								clearSPData();

								driverScreenMode = DriverScreenMode.D_RIDE_END;
								switchDriverScreen(driverScreenMode);

								driverUploadPathDataFileAsync(activity, Data.dEngagementId, totalHaversineDistance);

								initializeStartRideVariables();

							} else {
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								rideTimeChronometer.start();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						} else {
							driverScreenMode = DriverScreenMode.D_IN_RIDE;
							rideTimeChronometer.start();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						driverScreenMode = DriverScreenMode.D_IN_RIDE;
						rideTimeChronometer.start();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					fatafatEndRideOffline(activity, PendingCall.MARK_DELIVERED.getPath(), params, eoRideTimeInMillis, eoWaitTimeInMillis,
							(FatafatOrderInfo) Data.assignedCustomerInfo);
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
			driverScreenMode = DriverScreenMode.D_IN_RIDE;
			rideTimeChronometer.start();
		}

	}


	//TODO fatafat offline mark delivered
	public void fatafatEndRideOffline(Activity activity, String url, HashMap<String, String> params, long rideTimeInMillis, long waitTimeInMillis,
									  FatafatOrderInfo fatafatOrderInfo) {
		try {

			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay;
			int paymentMode = PaymentMode.CASH.getOrdinal();

			double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

			Log.e("offline fatafatOrderInfo =============", "============");
			Log.i("rideTime", "=" + rideTime);

			try {
				totalFare = getTotalFare(totalDistance, rideTimeInMillis, waitTimeInMillis);
			} catch (Exception e) {
				e.printStackTrace();
				totalFare = 0;
			}


			try {
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "Data.fareStructure = " + Data.fareStructure);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "rideTime = " + rideTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "waitTime = " + waitTime);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalDistanceInKm = " + totalDistanceInKm);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "totalFare = " + totalFare);
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "fatafatOrderInfo = " + fatafatOrderInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}


			actualFare = totalFare;

			finalToPay = totalFare;

			Log.i("totalFare == endride offline ", "=" + totalFare);
			Log.i("endRideOffline assignedCustomerInfo.couponInfo=", "=" + fatafatOrderInfo);


			finalDiscount = 0;
			finalDiscount = totalFare;
			finalPaidUsingWallet = 0;


			Data.endRideData = new EndRideData(Data.dEngagementId, actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode);


			if (map != null) {
				map.clear();
			}

			waitStart = 2;
			rideTimeChronometer.stop();


			clearSPData();

			driverScreenMode = DriverScreenMode.D_RIDE_END;
			switchDriverScreen(driverScreenMode);


			params.put("is_cached", "1");

			DialogPopup.dismissLoadingDialog();
			Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			try {
				Log.writePathLogToFile(Data.dEngagementId + "endRide", "url = " + url + " params = " + params);
			} catch (Exception e) {
				e.printStackTrace();
			}

			driverUploadPathDataFileAsync(activity, Data.dEngagementId, totalHaversineDistance);

			initializeStartRideVariables();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void submitFeedbackToDriverAsync(final Activity activity, String engagementId, final int givenRating) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("given_rating", "" + givenRating);
				params.put("engagement_id", engagementId);
				params.put("customer_id", Data.dCustomerId);

				Log.i("params", "=" + params);

				RestClient.getApiServices().rateTheCustomer(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "rateTheCustomer response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Toast.makeText(activity, "Thank you for your valuable feedback", Toast.LENGTH_SHORT).show();

							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


//	Retrofit

	public void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");

			RestClient.getApiServices().logoutRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
							HomeActivity.logoutUser(activity);
						} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
							String errorMessage = jObj.getString("error");
							DialogPopup.alertPopup(activity, "", errorMessage);
						} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
							String message = jObj.getString("message");
							DialogPopup.alertPopup(activity, "", message);
						} else if (ApiResponseFlags.LOGOUT_FAILURE.getOrdinal() == flag) {
							String errorMessage = jObj.getString("error");
							DialogPopup.alertPopup(activity, "", errorMessage);
						} else if (ApiResponseFlags.LOGOUT_SUCCESSFUL.getOrdinal() == flag) {
							PicassoTools.clearCache(Picasso.with(activity));

							GCMIntentService.clearNotifications(activity);

							Data.clearDataOnLogout(activity);

							userMode = UserMode.DRIVER;
							driverScreenMode = DriverScreenMode.D_INITIAL;

							Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
							activity.stopService(new Intent(activity, DriverLocationUpdateService.class));

							loggedOut = true;
						} else {
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});
		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	public void updateInRideData() {
		try {
			long responseTime = System.currentTimeMillis();
			if (UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode) {
				if (myLocation != null) {
					double totalDistanceInKm = Math.abs(totalDistance / 1000.0);
					long rideTimeSeconds = rideTimeChronometer.eclipsedTime / 1000;
					double rideTimeMinutes = Math.ceil(rideTimeSeconds / 60);
					int lastLogId = Integer.parseInt((Prefs.with(HomeActivity.this).getString(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0)));
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("access_token", Data.userData.accessToken);
					params.put("engagement_id", Data.dEngagementId);
					params.put("current_latitude", "" + myLocation.getLatitude());
					params.put("current_longitude", "" + myLocation.getLongitude());
					params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
					params.put("ride_time", decimalFormatNoDecimal.format(rideTimeMinutes));
					params.put("wait_time", "0");
					params.put("last_log_id", "" + lastLogId);

					Log.i("update_in_ride_data nameValuePairs", "=" + nameValuePairs);

					Response response = RestClient.getApiServices().updateInRideDataRetro(params);
					if (response != null) {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
						lastLogId = jObj.optInt("last_log_id", 0);
						Prefs.with(HomeActivity.this).save(SPLabels.DISTANCE_RESET_LOG_ID, "" + lastLogId);
						if (ApiResponseFlags.DISTANCE_RESET.getOrdinal() == flag) {
							try {
								double distance = jObj.getDouble("total_distance") * 1000;
								MeteringService.gpsInstance(HomeActivity.this).updateDistanceInCaseOfReset(distance);
								FlurryEventLogger.logResponseTime(HomeActivity.this, System.currentTimeMillis() - responseTime, FlurryEventNames.UPDATE_IN_RIDE_DATA_RESPONSE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void fetchHeatMapData(final Activity activity) {
		try {
			if (DriverScreenMode.D_INITIAL == driverScreenMode && AppStatus.getInstance(activity).isOnline(activity)) {
				final long responseTime = System.currentTimeMillis();
				RestClient.getApiServices().getHeatMapAsync(Data.userData.accessToken, new Callback<HeatMapResponse>() {
					@Override
					public void success(HeatMapResponse heatMapResponse, Response response) {
						try {

							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i("heat", jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.optInt("flag", ApiResponseFlags.HEATMAP_DATA.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
								if (ApiResponseFlags.HEATMAP_DATA.getOrdinal() == flag) {
									heatMapResponseGlobal = heatMapResponse;
									drawHeatMapData(heatMapResponseGlobal);
									Log.i("Heat Map response", String.valueOf(heatMapResponse));
									Log.i("Heat Map response", String.valueOf(heatMapResponseGlobal));
									FlurryEventLogger.logResponseTime(HomeActivity.this, System.currentTimeMillis() - responseTime, FlurryEventNames.HEAT_MAP_RESPONSE);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}

					}

					@Override
					public void failure(RetrofitError error) {
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void drawHeatMapData(HeatMapResponse heatMapResponse) {
		try {
			if (DriverScreenMode.D_INITIAL == driverScreenMode) {
				map.clear();
				for (HeatMapResponse.Region region : heatMapResponse.getRegions()) {
					ArrayList<LatLng> arrLatLng = new ArrayList<>();
					List<HeatMapResponse.Region_> regionList = region.getRegion().get(0);
					for (HeatMapResponse.Region_ region_ : regionList) {
						arrLatLng.add(new LatLng(region_.getX(), region_.getY()));
					}
					addPolygon(arrLatLng, region.getDriverFareFactor(), region.getDriverFareFactorPriority(),
							region.getColor(), region.getStrokeColor());
				}
			}
		} catch (Exception e) {
		}
	}


	public void addPolygon(ArrayList<LatLng> arg, double fareFactor, int zIndex, String color, String strokeColor) {
		try {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			PolygonOptions polygonOptions = new PolygonOptions();
			polygonOptions.strokeColor(Color.parseColor(strokeColor))
					.strokeWidth((4))
					.fillColor(Color.parseColor(color));
			for (LatLng latLng : arg) {
				polygonOptions.add(latLng);
				builder.include(latLng);
			}
			polygonOptions.zIndex(100 / zIndex);
			LatLngBounds latLngBounds = builder.build();
			CustomMapMarkerCreator.addTextMarkerToMap(this, map,
					latLngBounds.getCenter(),
					decimalFormat.format(fareFactor), 2, 20);

			map.addPolygon(polygonOptions);
		} catch (Exception e) {
			e.printStackTrace();
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


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textHead.setText("Alert");
			textMessage.setText("Are you sure you want to logout?");

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					logoutAsync(activity);
					FlurryEventLogger.event(LOGOUT_FROM_APP);
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


	public void startCustomerPathUpdateTimer() {

		cancelCustomerPathUpdateTimer();

		try {
			timerCustomerPathUpdater = new Timer();

			timerTaskCustomerPathUpdater = new TimerTask() {

				@Override
				public void run() {
					if (myLocation != null) {
						if ((DriverScreenMode.D_ARRIVED == driverScreenMode || driverScreenMode == DriverScreenMode.D_START_RIDE) && (Data.assignedCustomerInfo != null)) {

							getCustomerPathAndDisplay(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), Data.assignedCustomerInfo.requestlLatLng);

						} else if ((DriverScreenMode.D_IN_RIDE == driverScreenMode) && (Data.assignedCustomerInfo != null) && (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType)) {

							if (((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng != null) {
								getCustomerPathAndDisplay(Data.assignedCustomerInfo.requestlLatLng, ((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng);
							}

						} else if (((Data.assignedCustomerInfo != null) && (driverScreenMode == DriverScreenMode.D_IN_RIDE) && (BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType))) {

							if (((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo != null) {
								getCustomerPathAndDisplay(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), ((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.deliveryLatLng);
							}

						}
					}
				}
			};
			timerCustomerPathUpdater.scheduleAtFixedRate(timerTaskCustomerPathUpdater, 1000, 15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelCustomerPathUpdateTimer() {
		try {
			if (timerTaskCustomerPathUpdater != null) {
				timerTaskCustomerPathUpdater.cancel();
				timerTaskCustomerPathUpdater = null;
			}

			if (timerCustomerPathUpdater != null) {
				timerCustomerPathUpdater.cancel();
				timerCustomerPathUpdater.purge();
				timerCustomerPathUpdater = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean toShowPathToCustomer() {
		boolean inRide = false;
		if ((DriverScreenMode.D_IN_RIDE == driverScreenMode)
				&& (Data.assignedCustomerInfo != null)
				&& (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType)) {
			if (((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng != null) {
				inRide = true;
			}
		}
		return ((DriverScreenMode.D_ARRIVED == driverScreenMode || driverScreenMode == DriverScreenMode.D_START_RIDE) || inRide ||
				(Data.assignedCustomerInfo != null && driverScreenMode == DriverScreenMode.D_IN_RIDE && BusinessType.FATAFAT == Data.assignedCustomerInfo.businessType));
	}

	public void getCustomerPathAndDisplay(final LatLng sourceLatLng, final LatLng customerLatLng) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) && sourceLatLng != null && customerLatLng != null) {

				Response response = RestClient.getGoogleApiServices().getDirections(sourceLatLng.latitude + "," + sourceLatLng.longitude,
						customerLatLng.latitude + "," + customerLatLng.longitude, false, "driving", false);
				String result = new String(((TypedByteArray) response.getBody()).getBytes());

				if (result != null) {
					final List<LatLng> list = MapUtils.getLatLngListFromPath(result);
					if (list.size() > 0) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								try {
									if (toShowPathToCustomer()) {
										if (customerLocationMarker != null) {
											customerLocationMarker.remove();
										}
										if (pathToCustomerPolyline != null) {
											pathToCustomerPolyline.remove();
										}

										customerLocationMarker = map.addMarker(getCustomerLocationMarkerOptions(customerLatLng));

										PolylineOptions polylineOptions = new PolylineOptions();
										polylineOptions.width(ASSL.Xscale() * 5).color(D_TO_C_MAP_PATH_COLOR).geodesic(true);
										for (int z = 0; z < list.size(); z++) {
											polylineOptions.add(list.get(z));
										}
										pathToCustomerPolyline = map.addPolyline(polylineOptions);

										if (myLocation != null) {
											LatLngBounds.Builder builder = new LatLngBounds.Builder();
											builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).include(customerLatLng);
											float minRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
											map.animateCamera(CameraUpdateFactory.newLatLngBounds(MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder), (int) (minRatio * 100)));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public MarkerOptions getCustomerLocationMarkerOptions(LatLng customerLatLng) {
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
//								if (myLocation != null && map != null) {
//									map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
//								}
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

	public void cancelMapAnimateAndUpdateRideDataTimer() {
		try {
			if (timerTaskMapAnimateAndUpdateRideData != null) {
				timerTaskMapAnimateAndUpdateRideData.cancel();
				timerTaskMapAnimateAndUpdateRideData = null;
			}

			if (timerMapAnimateAndUpdateRideData != null) {
				timerMapAnimateAndUpdateRideData.cancel();
				timerMapAnimateAndUpdateRideData.purge();
				timerMapAnimateAndUpdateRideData = null;
			}

		} catch (Exception e) {
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


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setText("Are you sure you want to start ride?");


			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (myLocation != null) {
						dialog.dismiss();
						LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
						double displacement = MapUtils.distance(driverAtPickupLatLng, Data.dCustLatLng);

						if (displacement <= DRIVER_START_RIDE_CHECK_METERS) {
							buildAlertMessageNoGps();

							GCMIntentService.clearNotifications(activity);

							driverStartRideAsync(activity, driverAtPickupLatLng);
							FlurryEventLogger.event(START_RIDE_CONFIRMED);
						} else {
							DialogPopup.alertPopup(activity, "", "You must be present near the customer pickup location to start ride.");
						}
					} else {
						Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
					}

				}

			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					FlurryEventLogger.event(START_RIDE_NOT_CONFIRMED);
				}

			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Dialog dialogEndRidePopup;

	void endRidePopup(final Activity activity, final BusinessType businessType) {
		try {
			if (dialogEndRidePopup == null || !dialogEndRidePopup.isShowing()) {
				dialogEndRidePopup = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialogEndRidePopup.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialogEndRidePopup.setContentView(R.layout.dialog_custom_two_buttons);

				FrameLayout frameLayout = (FrameLayout) dialogEndRidePopup.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);

				WindowManager.LayoutParams layoutParams = dialogEndRidePopup.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialogEndRidePopup.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialogEndRidePopup.setCancelable(false);
				dialogEndRidePopup.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialogEndRidePopup.findViewById(R.id.textHead);
				textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialogEndRidePopup.findViewById(R.id.textMessage);
				textMessage.setTypeface(Data.latoRegular(activity));


				if (BusinessType.AUTOS == businessType) {
					textMessage.setText("Are you sure you want to end ride?");
				} else if (BusinessType.FATAFAT == businessType) {
					textMessage.setText("Are you sure you want to mark this item delivered?\nPlease take Rs. "
							+ Utils.getDecimalFormatForMoney().format(((FatafatOrderInfo) Data.assignedCustomerInfo).deliveryInfo.customerToPay)
							+ " from customer.");
				}


				Button btnOk = (Button) dialogEndRidePopup.findViewById(R.id.btnOk);
				btnOk.setTypeface(Data.latoRegular(activity));
				Button btnCancel = (Button) dialogEndRidePopup.findViewById(R.id.btnCancel);
				btnCancel.setTypeface(Data.latoRegular(activity));

				btnOk.setOnClickListener(new View.OnClickListener() {
					@SuppressWarnings("unused")
					@Override
					public void onClick(View view) {
						if (AppStatus.getInstance(activity).isOnline(activity)) {
							if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
								if (Data.assignedCustomerInfo != null
										&& BusinessType.AUTOS == Data.assignedCustomerInfo.businessType
										&& (1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).meterFareApplicable
										|| 1 == ((AutoCustomerInfo) Data.assignedCustomerInfo).luggageChargesApplicable)) {
									driverScreenMode = DriverScreenMode.D_BEFORE_END_OPTIONS;
									switchDriverScreen(driverScreenMode);
									dialogEndRidePopup.dismiss();
								} else {
									boolean success = endRideGPSCorrection(businessType);
									if (success) {
										dialogEndRidePopup.dismiss();
									}
								}
								FlurryEventLogger.event(END_RIDE_CONFIRMED);
							}
						} else {
							DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
						}
					}
				});

				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialogEndRidePopup.dismiss();
						FlurryEventLogger.event(END_RIDE_NOT_CONFIRMED);
					}

				});

				dialogEndRidePopup.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean endRideGPSCorrection(BusinessType businessType) {
		try {
			if (distanceUpdateFromService) {
				Location locationToUse;
				boolean fusedLocationUsed = false;

				long currentTime = System.currentTimeMillis();
				long threeMinuteMillis = 3 * 60 * 1000;

				Log.e("lastGPSLocation on end ride", "=======" + lastGPSLocation);
				Log.e("lastFusedLocation on end ride", "=======" + lastFusedLocation);

				Log.writePathLogToFile(Data.dEngagementId + "m", "lastGPSLocation on end ride = " + lastGPSLocation);
				Log.writePathLogToFile(Data.dEngagementId + "m", "lastFusedLocation on end ride = " + lastFusedLocation);

				LatLng oldGPSLatLng = MeteringService.gpsInstance(HomeActivity.this).getSavedLatLngFromSP(HomeActivity.this);

				long lastloctime = GpsDistanceCalculator.lastLocationTime;

				Log.e("oldGPSLatLng on end ride", "=======" + oldGPSLatLng);
				Log.writePathLogToFile(Data.dEngagementId + "m", "oldGPSLatLng on end ride = " + oldGPSLatLng);

				if (lastGPSLocation != null && lastFusedLocation != null) {
					long gpsLocTimeDiff = currentTime - lastGPSLocation.getTime();
					long fusedLocTimeDiff = currentTime - lastFusedLocation.getTime();

					Log.e("gpsLocTimeDiff on end ride", "=======" + gpsLocTimeDiff);
					Log.e("fusedLocTimeDiff on end ride", "=======" + fusedLocTimeDiff);

					Log.writePathLogToFile(Data.dEngagementId + "m", "gpsLocTimeDiff=" + gpsLocTimeDiff + " and fusedLocTimeDiff=" + fusedLocTimeDiff);

					if (gpsLocTimeDiff <= threeMinuteMillis) {                                // gps location is fine
						locationToUse = lastGPSLocation;
						fusedLocationUsed = false;
					} else {
						if (fusedLocTimeDiff <= threeMinuteMillis) {                            //�fused to use
							locationToUse = lastFusedLocation;
							fusedLocationUsed = true;
						} else {
							locationToUse = lastGPSLocation;
							fusedLocationUsed = false;
						}
					}
				} else if (lastGPSLocation != null && lastFusedLocation == null) {
					locationToUse = lastGPSLocation;
					fusedLocationUsed = false;
				} else {
					locationToUse = myLocation;
					fusedLocationUsed = true;
					Log.e("locationToUse on end ride from myLocation", "=======" + locationToUse);
					Log.writePathLogToFile(Data.dEngagementId + "m", "locationToUse on end ride from myLocation=" + locationToUse);
				}


				if (locationToUse != null
						&& (Utils.compareDouble(oldGPSLatLng.latitude, 0.0) == 0)
						&& (Utils.compareDouble(oldGPSLatLng.longitude, 0.0) == 0)) {
					oldGPSLatLng = new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude());
				}
				Log.writePathLogToFile(Data.dEngagementId + "m", "oldGPSLatLng after on end ride = " + oldGPSLatLng);


				Log.e("locationToUse on end ride", "=======" + locationToUse);
				Log.e("fusedLocationUsed on end ride", "=======" + fusedLocationUsed);

				Log.writePathLogToFile(Data.dEngagementId + "m", "locationToUse on end ride=" + locationToUse);
				Log.writePathLogToFile(Data.dEngagementId + "m", "fusedLocationUsed on end ride=" + fusedLocationUsed);

				if (locationToUse != null) {

					GCMIntentService.clearNotifications(activity);

					waitStart = 0;

					if (BusinessType.AUTOS == businessType || BusinessType.FATAFAT == businessType) {

						rideTimeChronometer.stop();

						driverScreenMode = DriverScreenMode.D_RIDE_END;

						if (fusedLocationUsed) {
							calculateFusedLocationDistance(activity, oldGPSLatLng,
									new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude()),
									rideTimeChronometer.eclipsedTime, HomeActivity.totalWaitTime, businessType, lastloctime);
						} else {
							driverEndRideAsync(activity, oldGPSLatLng, locationToUse.getLatitude(), locationToUse.getLongitude(),
									rideTimeChronometer.eclipsedTime, HomeActivity.totalWaitTime, 0, businessType);
						}
					} else {

					}
					return true;
				} else {
					Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(activity, "Waiting for location...", Toast.LENGTH_SHORT).show();
			return false;
		}
	}


	int flagDistanceTravelled = -1;

	public void calculateFusedLocationDistance(final Activity activity, final LatLng source, final LatLng destination,
											   final long rideTimeInMillis, final long waitTimeInMillis,
											   final BusinessType businessType, final long lastloctime) {
		DialogPopup.showLoadingDialog(activity, "Loading...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					double lastTimeDiff = (System.currentTimeMillis() - lastloctime) / 1000; // in seconds
					double displacement = MapUtils.distance(source, destination);
					double endDisplacementSpeed = displacement / lastTimeDiff;
					double endDistanceSpeed = -1;

					Log.v("lasttime diff", "" + lastTimeDiff);
					Log.v("displacement", "" + displacement);
					Log.v("displacement speed", "" + endDisplacementSpeed);

					Response responseR = RestClient.getGoogleApiServices().getDistanceMatrix(source.latitude + "," + source.longitude,
							destination.latitude + "," + destination.longitude, "EN", false, false);
					String response = new String(((TypedByteArray) responseR.getBody()).getBytes());

					if (endDisplacementSpeed < 19) {
						try {
							double distanceOfPath = -1;
							JSONObject jsonObject = new JSONObject(response);
							String status = jsonObject.getString("status");
							if ("OK".equalsIgnoreCase(status)) {
								JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
								distanceOfPath = element0.getJSONObject("distance").getDouble("value");
								endDistanceSpeed = distanceOfPath / lastTimeDiff;
								Log.v("dist", "" + distanceOfPath);
								Log.v("displacement speed", "" + endDistanceSpeed);
							}
							Log.e("calculateFusedLocationDistance distanceOfPath ", "=" + distanceOfPath);
							if (distanceOfPath > 0.0001 && endDistanceSpeed < 14) {
								totalDistance = totalDistance + distanceOfPath;
								flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_DISTANCE.getOrdinal();
								Log.writePathLogToFile(Data.dEngagementId + "m", "GAPI distanceOfPath=" + distanceOfPath + " and totalDistance=" + totalDistance);
							} else {

								throw new Exception();
							}
						} catch (Exception e) {
							e.printStackTrace();
							totalDistance = totalDistance + displacement;
							flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_DISPLACEMENT.getOrdinal();
							Log.writePathLogToFile(Data.dEngagementId + "m", "GAPI excep displacement=" + displacement + " and totalDistance=" + totalDistance);

						}
					} else {
						double complimentaryDistance = 4.5 * lastTimeDiff;
						Log.v("distComp", "" + complimentaryDistance);
						totalDistance = totalDistance + complimentaryDistance;
						flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_COMP_DIST.getOrdinal();

					}
					Log.e("calculateFusedLocationDistance totalDistance ", "=" + totalDistance);

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
							driverEndRideAsync(activity, source, destination.latitude, destination.longitude,
									rideTimeInMillis, waitTimeInMillis, flagDistanceTravelled, businessType);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}


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


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setText("Are you sure you want to cancel ride?");


			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();

					GCMIntentService.clearNotifications(HomeActivity.this);
					driverCancelRideAsync(HomeActivity.this);
					if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
						FlurryEventLogger.event(CANCELLED_BEFORE_ARRIVING_CONFIRMED);
					} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
						FlurryEventLogger.event(CANCELLED_RIDE_CONFIRMED);
					}
				}


			});


			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
						FlurryEventLogger.event(CANCELLED_BEFORE_ARRIVING_NOT_CONFIRMED);
					} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
						FlurryEventLogger.event(CANCELLED_RIDE_NOT_CONFIRMED);
					}
				}

			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			if (loggedOut) {
				loggedOut = false;

				Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
				stopService(new Intent(this, DriverLocationUpdateService.class));

				Intent intent = new Intent(HomeActivity.this, SplashNewActivity.class);
				intent.putExtra("no_anim", "yes");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		}
		if (hasFocus) {
			buildAlertMessageNoGps();
		}
	}


	public void showAllRideRequestsOnMap() {

		try {
			if (userMode == UserMode.DRIVER) {

				Log.e("driverRideRequests", "=" + Data.driverRideRequests);

				map.clear();
				drawHeatMapData(heatMapResponseGlobal);

				if (Data.driverRideRequests.size() > 0) {

					driverRideRequestsList.setVisibility(View.VISIBLE);

					LatLng last = map.getCameraPosition().target;

					for (int i = 0; i < Data.driverRideRequests.size(); i++) {
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
							if (myLocation != null) {
								double distanceLhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), lhs.latLng);
								double distanceRhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), rhs.latLng);
								return (int) (distanceLhs - distanceRhs);
							}
							return 0;
						}
					});

					driverRequestListAdapter.setResults(Data.driverRideRequests);

					map.animateCamera(CameraUpdateFactory.newLatLng(last), 1000, null);

				} else {
					driverRideRequestsList.setVisibility(View.GONE);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onNewRideRequest() {
		if (userMode == UserMode.DRIVER && (driverScreenMode == DriverScreenMode.D_INITIAL || driverScreenMode == DriverScreenMode.D_RIDE_END)) {
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
	public void driverTimeoutDialogPopup(final long timeoutInterwal) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				driverTimeOutPopup(HomeActivity.this, timeoutInterwal);
			}
		});
	}

	@Override
	public void onCancelRideRequest(final String engagementId, final boolean acceptedByOtherDriver) {
		try {
			if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showAllRideRequestsOnMap();
						initializeStationDataProcedure();
					}
				});
			} else if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (engagementId.equalsIgnoreCase(Data.dEngagementId)) {
							DialogPopup.dismissLoadingDialog();
							dismissStationDataPopup();
							cancelStationPathUpdateTimer();
							driverScreenMode = DriverScreenMode.D_INITIAL;
							switchDriverScreen(driverScreenMode);
							if (acceptedByOtherDriver) {
								DialogPopup.alertPopup(HomeActivity.this, "", "This request has been accepted by other driver");
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										DialogPopup.dismissAlertPopup();
									}
								}, 3000);
							} else {
								DialogPopup.alertPopup(HomeActivity.this, "", "User has canceled the request");
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										DialogPopup.dismissAlertPopup();
									}
								}, 4000);
							}
						}
					}
				});
			} else if (userMode == UserMode.DRIVER && (driverScreenMode == DriverScreenMode.D_ARRIVED || driverScreenMode == DriverScreenMode.D_START_RIDE)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (engagementId.equalsIgnoreCase(Data.dEngagementId)) {
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
		if (userMode == UserMode.DRIVER) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (driverScreenMode == DriverScreenMode.D_REQUEST_ACCEPT) {
						if (engagementId.equalsIgnoreCase(Data.dEngagementId)) {
							driverScreenMode = DriverScreenMode.D_INITIAL;
							switchDriverScreen(driverScreenMode);
						}
					} else if (driverScreenMode == DriverScreenMode.D_INITIAL) {
						showAllRideRequestsOnMap();
					}
				}
			});
		}
	}


	@Override
	public void onManualDispatchPushReceived() {
		try {
			if (userMode == UserMode.DRIVER) {
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
		editor.putString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");
		editor.putString(Data.SP_LAST_LOCATION_TIME, "" + System.currentTimeMillis());

		editor.commit();

		Database.getInstance(this).deleteSavedPath();
	}


	@Override
	public void onChangeStatePushReceived() {
		try {
			callAndHandleStateRestoreAPI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	static class FBLogoutNoIntent extends AsyncTask<Void, Void, String> {

		Activity act;

		public FBLogoutNoIntent(Activity act) {
			this.act = act;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(Void... urls) {
			try {
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

	public static void logoutUser(final Activity cont) {
		try {

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

		} catch (Exception e) {
			e.printStackTrace();
		}

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
			if (highAccuracyLF == null) {
				highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
			}
			highAccuracyLF.connect();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void destroyFusedLocationFetchers() {
		destroyHighAccuracyFusedLocationFetcher();
	}

	public void destroyHighAccuracyFusedLocationFetcher() {
		try {
			if (highAccuracyLF != null) {
				highAccuracyLF.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public synchronized void onLocationChanged(Location location, int priority) {
//		if(((UserMode.PASSENGER == userMode) && (PassengerScreenMode.P_IN_RIDE != passengerScreenMode)) ||
//				((UserMode.DRIVER == userMode) && (DriverScreenMode.D_IN_RIDE != driverScreenMode) && (DriverScreenMode.D_START_RIDE != driverScreenMode))){
		if ((Utils.compareDouble(location.getLatitude(), 0.0) != 0) && (Utils.compareDouble(location.getLongitude(), 0.0) != 0)) {
			if (location.getAccuracy() <= HIGH_ACCURACY_ACCURACY_CHECK) {

				if (HomeActivity.myLocation == null) {
					HomeActivity.myLocation = location;
					zoomToCurrentLocationAtFirstLocationFix(location);
				} else {
					if (MapUtils.speed(HomeActivity.myLocation, location) <= GpsDistanceCalculator.MAX_SPEED_THRESHOLD) {
						HomeActivity.myLocation = location;
						zoomToCurrentLocationAtFirstLocationFix(location);
					}
				}
			} else {
				reconnectLocationFetchers();
			}
		}
//		}
	}


	public void zoomToCurrentLocationAtFirstLocationFix(Location location) {
		try {
			if (map != null) {
				if (!zoomedToMyLocation) {
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
				if (Data.userData != null) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
						}
					});
					int currentUserStatus = 0;
					if (UserMode.DRIVER == userMode) {
						currentUserStatus = 1;
					}
					if (currentUserStatus != 0) {
						String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);

						if (resp.contains(Constants.SERVER_TIMEOUT)) {
							String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);

							if (resp1.contains(Constants.SERVER_TIMEOUT)) {
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
					if (Data.userData != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
							}
						});
						int currentUserStatus = 0;
						if (UserMode.DRIVER == userMode) {
							currentUserStatus = 1;
						}
						if (currentUserStatus != 0) {
							manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);

							if (manualPatchPushStateRestoreResponse.contains(Constants.SERVER_TIMEOUT)) {
								manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);

								if (manualPatchPushStateRestoreResponse.contains(Constants.SERVER_TIMEOUT)) {
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

								if (!manualPatchPushStateRestoreResponse.contains(Constants.SERVER_TIMEOUT)) {
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


	public void showManualPatchPushReceivedDialog() {
		try {
			if (UserMode.DRIVER == userMode && (DriverScreenMode.D_ARRIVED == driverScreenMode || DriverScreenMode.D_START_RIDE == driverScreenMode)) {
				if (Data.assignedCustomerInfo != null) {
					String manualPatchPushReceived = Database2.getInstance(HomeActivity.this).getDriverManualPatchPushReceived();
					if (Database2.YES.equalsIgnoreCase(manualPatchPushReceived)) {
						DialogPopup.alertPopupWithListener(HomeActivity.this, "", "A pickup has been assigned to you. Please pick the customer.", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								GCMIntentService.stopRing(true);
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

			HashMap<String, String> params = new HashMap<>();

			params.put("access_token", Data.userData.accessToken);
			params.put("engagement_id", Data.dEngagementId);
			params.put("customer_id", "" + Data.assignedCustomerInfo.userId);

			Log.i("params", "=" + params);

			RestClient.getApiServices().acknowledgeManualEngagement(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					Log.v("Server response acknowledge_manual_engagement", "response = " + responseStr);

					try {
						JSONObject jObj = new JSONObject(responseStr);

						if (!jObj.isNull("error")) {
							int flag = jObj.getInt("flag");
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", errorMessage);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {
							if (jObj.has("flag")) {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
					manualPatchPushAckAPI(activity);
				}
			});
		}

	}


	public void initializeStationDataProcedure() {
		dismissStationDataPopup();
		cancelStationPathUpdateTimer();
		if (myLocation != null) {
			if (checkDriverFree()) {
				fetchStationDataAPI(HomeActivity.this);
			}
		}
	}

//	Retrofit

	public void fetchStationDataAPI(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", "" + myLocation.getLatitude());
			params.put("longitude", "" + myLocation.getLongitude());

			RestClient.getApiServices().fetchStationDataAPIRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
							HomeActivity.logoutUser(activity);
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
							String errorMessage = jObj.getString("error");
							DialogPopup.alertPopup(activity, "", errorMessage);
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
							String message = jObj.getString("message");
							DialogPopup.alertPopup(activity, "", message);
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						} else if (ApiResponseFlags.STATION_ASSIGNED.getOrdinal() == flag) {
							if (checkDriverFree()) {
								assignedStationData = new StationData(jObj.getString("station_id"), jObj.getDouble("latitude"), jObj.getDouble("longitude"),
										DateOperations.utcToLocal(jObj.getString("arrival_time")), jObj.getString("address"), jObj.getString("message"), jObj.getDouble("radius"));
								displayStationDataPopup(activity);
								startStationPathUpdateTimer();
								SoundMediaPlayer.startSound(activity, R.raw.ring_new, 3, false, false);
							} else {
								SoundMediaPlayer.stopSound();
								GCMIntentService.clearNotifications(activity);
							}
						} else if (ApiResponseFlags.NO_STATION_ASSIGNED.getOrdinal() == flag) {
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						} else if (ApiResponseFlags.NO_STATION_AVAILABLE.getOrdinal() == flag) {
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						} else {
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							SoundMediaPlayer.stopSound();
							GCMIntentService.clearNotifications(activity);
						}

					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});


		}
	}


	public boolean checkDriverFree() {
		try {
			checkIfUserDataNull(this);
			return (UserMode.DRIVER == userMode && DriverScreenMode.D_INITIAL == driverScreenMode && Data.userData.autosAvailable == 1 && Data.driverRideRequests.size() == 0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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


			TextView textHead = (TextView) stationDataDialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) stationDataDialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setText(assignedStationData.message);

			Button btnOk = (Button) stationDataDialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					stationDataDialog.dismiss();
					if (!assignedStationData.acknowledgeDone) {
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

	public void dismissStationDataPopup() {
		try {
			if (stationDataDialog != null) {
				stationDataDialog.dismiss();
				stationDataDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	Retro


	public void acknowledgeStationDataReadAPI(final Activity activity, final String stationId) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("station_id", stationId);

			RestClient.getApiServices().acknowledgeStationDataReadRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
							HomeActivity.logoutUser(activity);
						} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
							String errorMessage = jObj.getString("error");
							DialogPopup.alertPopup(activity, "", errorMessage);
						} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
							String message = jObj.getString("message");
							DialogPopup.alertPopup(activity, "", message);
						} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							if (assignedStationData != null) {
								assignedStationData.acknowledgeDone = true;
							}
						} else {
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}

					} catch (Exception exception) {
						exception.printStackTrace();
					}
					DialogPopup.dismissLoadingDialog();

				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
				}
			});


		}
	}


	int stationPathUpdaterRunCount;
	Timer timerStationPathUpdater;
	TimerTask timerTaskStationPathUpdater;

	public void startStationPathUpdateTimer() {
		cancelStationPathUpdateTimer();
		try {
			timerStationPathUpdater = new Timer();

			timerTaskStationPathUpdater = new TimerTask() {

				@Override
				public void run() {
					try {
						if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
							if (myLocation != null) {
								if (assignedStationData != null) {
									if (checkDriverFree()) {

										Calendar currentCalendar = Calendar.getInstance();
										if (currentCalendar.getTimeInMillis() > assignedStationData.timeCalendar.getTimeInMillis() && stationPathUpdaterRunCount == 1) { // start showing alerts
											stationPathUpdaterRunCount = 10;
										}

										Log.e("stationPathUpdaterRunCount", "=" + stationPathUpdaterRunCount);

										final LatLng source = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
										double distanceToReachToStation = MapUtils.distance(source, assignedStationData.latLng);
										Log.e("distanceToReachToStation", "=" + distanceToReachToStation);
										if (distanceToReachToStation > assignedStationData.toleranceDistance) {
											if (stationPathUpdaterRunCount % 10 == 0) {
												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														if (checkDriverFree()) {
															SoundMediaPlayer.startSound(HomeActivity.this, R.raw.ring_new, 3, false, false);
															displayStationDataPopup(HomeActivity.this);
														}
													}
												});
											}
											Log.e("currentCalendar", "=" + currentCalendar.getTime());
											Log.i("assignedStationData.timeCalendar", "=" + assignedStationData.timeCalendar.getTime());
											if (currentCalendar.getTimeInMillis() > assignedStationData.timeCalendar.getTimeInMillis()) { // start showing alerts
												stationPathUpdaterRunCount++;
											}

											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													if (checkDriverFree()) {
														textViewDriverInfo.setVisibility(View.VISIBLE);
														textViewDriverInfo.setText("Please reach " + assignedStationData.address + " by " + DateOperations.getTimeAMPM(assignedStationData.time));
													}
												}
											});


											Response response = RestClient.getGoogleApiServices().getDirections(source.latitude + "," + source.longitude,
													assignedStationData.latLng.latitude + "," + assignedStationData.latLng.longitude, false, "driving", false);
											String result = new String(((TypedByteArray) response.getBody()).getBytes());

											if (checkDriverFree()) {
												if (result != null) {

													final List<LatLng> list = MapUtils.getLatLngListFromPath(result);

													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															try {
																if (checkDriverFree()) {
																	if (assignedStationData != null) {
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


																		for (int z = 0; z < list.size() - 1; z++) {
																			LatLng src = list.get(z);
																			LatLng dest = list.get(z + 1);
																			map.addPolyline(new PolylineOptions()
																					.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
																					.width(ASSL.Xscale() * 5)
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

										} else {
											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													if (checkDriverFree()) {
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

	public void cancelStationPathUpdateTimer() {
		try {
			if (timerTaskStationPathUpdater != null) {
				timerTaskStationPathUpdater.cancel();
			}
			if (timerStationPathUpdater != null) {
				timerStationPathUpdater.cancel();
				timerStationPathUpdater.purge();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		if (Data.assignedCustomerInfo != null) {
			if (Data.assignedCustomerInfo.userId == userId) {
				if (BusinessType.AUTOS == Data.assignedCustomerInfo.businessType) {
					((AutoCustomerInfo) Data.assignedCustomerInfo).jugnooBalance = balance;
				}
			}
		}
	}


	@Override
	public void onDropLocationUpdated(final String engagementId, final LatLng dropLatLng) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (engagementId.equalsIgnoreCase(Data.dEngagementId)) {
					updateDropLatLngandPath(dropLatLng);
				}
			}
		});
	}


	private void updateDropLatLngandPath(LatLng dropLatLng) {
		if ((Data.assignedCustomerInfo != null) && (BusinessType.AUTOS.getOrdinal() == Data.assignedCustomerInfo.businessType.getOrdinal())) {
			((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng = dropLatLng;
			startCustomerPathUpdateTimer();
			if (((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng != null) {
				updateCustomerPickupAddress(((AutoCustomerInfo) Data.assignedCustomerInfo).dropLatLng);
			}
		}
	}


	@Override
	public void updateMeteringUI(final double distance, final long elapsedTime, final long waitTime,
								 final Location lastGPSLocation, final Location lastFusedLocation, final double totalHaversineDistance) {
		if (UserMode.DRIVER == userMode
				&& (DriverScreenMode.D_IN_RIDE == driverScreenMode
				|| DriverScreenMode.D_ARRIVED == driverScreenMode)) {
			totalDistance = distance;
			HomeActivity.totalHaversineDistance = totalHaversineDistance;
			HomeActivity.totalWaitTime = waitTime;
			HomeActivity.this.lastGPSLocation = lastGPSLocation;
			HomeActivity.this.lastFusedLocation = lastFusedLocation;
			HomeActivity.this.distanceUpdateFromService = true;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (UserMode.DRIVER == userMode
							&& DriverScreenMode.D_IN_RIDE == driverScreenMode) {
						updateDistanceFareTexts(distance, elapsedTime, waitTime);
						if (rideStartPositionMarker == null) {
							displayOldPath();
						}
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
				if (UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode) {
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
				if (UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode) {
					if (Color.TRANSPARENT != MAP_PATH_COLOR) {
						map.addPolyline(polylineOptions.width(ASSL.Xscale() * 5).color(MAP_PATH_COLOR).geodesic(true));
					}
				}
			}
		});
	}


	@Override
	public void addPathNew(final ArrayList<CurrentPathItem> currentPathItems) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode) {

					PolylineOptions polylineOptions = new PolylineOptions();
					polylineOptions.width(ASSL.Xscale() * 5);
					polylineOptions.color(MAP_PATH_COLOR);
					polylineOptions.geodesic(false);

					for (CurrentPathItem currentPathItem : currentPathItems) {
						if (1 != currentPathItem.googlePath) {
							polylineOptions.add(currentPathItem.sLatLng, currentPathItem.dLatLng);
						}
					}

					if (Color.TRANSPARENT != MAP_PATH_COLOR) {
						map.addPolyline(polylineOptions);
					}
				}
			}
		});

	}

	private void deleteGpsData() {
		/* Cold start */
		Bundle bundle = new Bundle();
		bundle.putBoolean("all", true);
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
		locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
		locationManager.sendExtraCommand("gps", "force_time_injection", bundle);
	}


	@Override
	public void handleCancelRideSuccess() {
		runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  if (map != null) {
								  map.clear();
							  }
							  stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

							  reduceRideRequest(Data.dEngagementId);
						  }
					  }
		);
	}

	@Override
	public void handleCancelRideFailure() {
		runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  callAndHandleStateRestoreAPI();
						  }
					  }
		);
	}


	@Override
	public void markArrivedInterrupt(final LatLng latLng) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				GCMIntentService.clearNotifications(activity);
				driverMarkArriveRideAsync(activity, latLng);
			}
		});
	}


	public void driverTimeOutPopup(final Activity activity, long timeoutInterwal) {

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_ride_request_timeout);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.8f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Data.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Data.latoRegular(activity));
			TextView timeOutText = (TextView) dialog.findViewById(R.id.timeOutText);
			timeOutText.setTypeface(Data.latoRegular(activity));
			final TextView timeOutValue = (TextView) dialog.findViewById(R.id.timeOutValue);
			timeOutValue.setTypeface(Data.latoRegular(activity));

			textMessage.setText(Data.userData.timeoutMessage);

			new CountDownTimer(timeoutInterwal, 1000) {

				public void onTick(long millisUntilFinished) {
					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
					timeOutValue.setText("" + String.format("%02d:%02d:%02d",
							TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
							TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
									TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
							TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
									TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
				}

				public void onFinish() {
					timeOutValue.setText("done!");
					Data.userData.autosAvailable = 0;
					changeJugnooONUIAndInitService();
					dialog.dismiss();
				}
			}.start();
			dialog.show();
			GCMIntentService.stopRing(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void etaTimer(long eta){
		timer = new CountDownTimer(eta, 1000) {

			public void onTick(long millisUntilFinished) {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				etaTimerText.setText("" + String.format("%02d:%02d",
						TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
								TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
						TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
								TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
			}

			public void onFinish() {
			}
		}.start();
	}

}