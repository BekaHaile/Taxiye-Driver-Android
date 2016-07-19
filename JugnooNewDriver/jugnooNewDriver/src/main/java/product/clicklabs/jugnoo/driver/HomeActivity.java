package product.clicklabs.jugnoo.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.driver.apis.ApiAcceptRide;
import product.clicklabs.jugnoo.driver.apis.ApiFetchDriverApps;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleDirectionWaypoints;
import product.clicklabs.jugnoo.driver.apis.ApiRejectRequest;
import product.clicklabs.jugnoo.driver.apis.ApiSendCallLogs;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.AppMode;
import product.clicklabs.jugnoo.driver.datastructure.BenefitType;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.CouponType;
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.CustomerRideData;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EndRideData;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.FlagRideStatus;
import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.PendingCall;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.PromotionType;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.home.BlockedAppsUninstallIntent;
import product.clicklabs.jugnoo.driver.home.CustomerSwitcher;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.AGPSRefresh;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomInfoWindow;
import product.clicklabs.jugnoo.driver.utils.CustomMapMarkerCreator;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.PausableChronometer;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("DefaultLocale")
public class HomeActivity extends BaseFragmentActivity implements AppInterruptHandler, LocationUpdate, GPSLocationUpdate,
		FlurryEventNames, OnMapReadyCallback, Constants, DisplayPushHandler {


	private final String TAG = HomeActivity.class.getSimpleName();

	DrawerLayout drawerLayout;                                                                        // views declaration


	//menu bar
	LinearLayout menuLayout;


	ImageView profileImg;
	TextView userName, ratingValue;
	LinearLayout linearLayoutDEI, driverImageRL, linearLayout_DEI;

	RelativeLayout relativeLayoutAutosOn, relativeLayoutSharingOn, relativeLayoutDeliveryOn;
	ImageView imageViewAutosOnToggle, imageViewSharingOnToggle, imageViewDeliveryOnToggle;

	RelativeLayout inviteFriendRl, driverRatingRl, notificationCenterRl;
	TextView inviteFriendText, notificationCenterText;

	RelativeLayout bookingsRl, RelativeLayoutNotificationCenter, etaTimerRLayout;
	TextView bookingsText, etaTimerText;

	RelativeLayout relativeLayoutSharingRides;

	RelativeLayout fareDetailsRl;
	TextView fareDetailsText, textViewDestination;
	RelativeLayout relativeLayoutSuperDrivers, relativeLayoutDestination;

	RelativeLayout callUsRl,termsConditionRl;
	TextView callUsText, termsConditionText;

	RelativeLayout paytmRechargeRl, paymentsRl;
	TextView paytmRechargeText, paymentsText;

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


	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;



	//Driver initial layout
	RelativeLayout driverInitialLayout;
	ListView driverRideRequestsList;
	Button driverInitialMyLocationBtn;
	TextView jugnooOffText;

	DriverRequestListAdapter driverRequestListAdapter;


	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn, buttonDriverNavigation;


	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;

	RelativeLayout perfectRidePassengerCallRl;
	LinearLayout perfectRidePassengerInfoRl, driverPassengerInfoRl;
	TextView driverPassengerCallText, driverPerfectRidePassengerName, textViewRideInstructions, textViewRideInstructionsInRide;
	Button driverEngagedMyLocationBtn;

	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	Button driverStartRideBtn, buttonMarkArrived;
	Button driverCancelRideBtn;


	//In ride layout
	LinearLayout driverInRideMainRl, linearLayoutRideValues;
	TextView driverIRDistanceText, driverIRDistanceValue, driverIRFareText, driverIRFareValue,
			driverRideTimeText, driverWaitText, driverWaitValue;
	PausableChronometer rideTimeChronometer;
	RelativeLayout driverWaitRl, driverIRFareRl;
	ImageView imageViewETASmily;
	Button driverEndRideBtn, buttonMakeDelivery;


	//Review layout
	RelativeLayout endRideReviewRl;

	LinearLayout reviewReachedDistanceRl;
	LinearLayout linearLayoutMeterFare;
	TextView textViewRateYourCustomer,
			reviewDistanceText, reviewDistanceValue, textViewSuperDrivers,
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

	LinearLayout endRideInfoRl;
	TextView jugnooRideOverText, takeFareText;

	Button reviewSubmitBtn;
	RelativeLayout relativeLayoutRateCustomer;
	RatingBar ratingBarFeedback, ratingBarFeedbackSide;
	Button reviewSkipBtn;

	ScrollView scrollViewEndRide;
	LinearLayout linearLayoutEndRideMain;
	TextView textViewScroll, textViewNotificationValue, textViewPerfectRideWating;

	RelativeLayout relativeLayoutDeliveryOver;
	TextView textViewDeliveryIsOver, textViewEndRideCustomerName;
	LinearLayout linearLayoutEndDelivery;
	TextView textViewOrdersDeliveredValue, textViewOrdersReturnedValue;


	CustomerSwitcher customerSwitcher;


	// data variables declaration
	public String language = "";
	private Handler checkwalletUpdateTimeoutHandler;
	private Runnable checkwalletUpdateTimeoutRunnable;


	DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));

	private CustomerRideData customerRideDataGlobal = new CustomerRideData();

	long fetchHeatMapTime = 0;

	double totalFare = 0;
	String waitTime = "", rideTime = "";
	EndRideData endRideData;


	public static Location myLocation;
	public Location lastGPSLocation, lastFusedLocation;
	public boolean distanceUpdateFromService = false;
	public boolean walletBalanceUpdatePopup = false;
	private boolean mapAnimatedToCustomerPath = false;

	static UserMode userMode;
	public static DriverScreenMode driverScreenMode;


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

	public static final long DRIVER_START_RIDE_CHECK_METERS = 600; //in meters

	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000; //in milliseconds

	public static final float HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
	public CountDownTimer timer = null;
	public Marker perfectRideMarker = null;


	public static final long MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT = 10 * 60000; //in milliseconds
	private final int MAP_ANIMATION_TIME = 300;
	private final double DISTANCE_UPPERBOUND = 300000;


	public ASSL assl;
	private String currentPreferredLang = "";

	private CustomerInfo openedCustomerInfo;
	private CustomerInfo getOpenedCustomerInfo(){
		return openedCustomerInfo;
	}
	private void setOpenedCustomerInfo(CustomerInfo customerInfo){
		openedCustomerInfo = customerInfo;
	}

	private RelativeLayout relativeLayoutContainer;

	private ArrayList<Marker> requestMarkers = new ArrayList<>();

	private final double FIX_ZOOM_DIAGONAL = 408;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_home);

			decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
			decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));


			HomeActivity.appInterruptHandler = HomeActivity.this;

			activity = this;

			loggedOut = false;
			zoomedToMyLocation = false;
			dontCallRefreshDriver = false;
			mapTouchedOnce = false;
			resumed = false;
			mapAnimatedToCustomerPath = false;

			appMode = AppMode.NORMAL;

			language = Locale.getDefault().getLanguage();


			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
			drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


			assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);

			//Swipe menu
			menuLayout = (LinearLayout) findViewById(R.id.menuLayout);


			profileImg = (ImageView) findViewById(R.id.profileImg);
			userName = (TextView) findViewById(R.id.userName);
			ratingValue = (TextView) findViewById(R.id.ratingValue);
			userName.setTypeface(Data.latoRegular(getApplicationContext()));
			ratingValue.setTypeface(Data.latoRegular(getApplicationContext()));

			linearLayoutDEI = (LinearLayout) findViewById(R.id.linearLayoutDEI);
			linearLayout_DEI = (LinearLayout) findViewById(R.id.linearLayout_DEI);

			relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
			relativeLayoutContainer.setVisibility(View.GONE);

			relativeLayoutAutosOn = (RelativeLayout) findViewById(R.id.relativeLayoutAutosOn);
			((TextView) findViewById(R.id.textViewAutosOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewAutosOnToggle = (ImageView) findViewById(R.id.imageViewAutosOnToggle);

			relativeLayoutSharingOn = (RelativeLayout) findViewById(R.id.relativeLayoutSharingOn);
			((TextView) findViewById(R.id.textViewSharingOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewSharingOnToggle = (ImageView) findViewById(R.id.imageViewSharingOnToggle);

			relativeLayoutDeliveryOn = (RelativeLayout) findViewById(R.id.relativeLayoutDeliveryOn);
			((TextView) findViewById(R.id.textViewDeliveryOn)).setTypeface(Data.latoRegular(getApplicationContext()));
			imageViewDeliveryOnToggle = (ImageView) findViewById(R.id.imageViewDeliveryOnToggle);


			inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
			driverRatingRl = (RelativeLayout) findViewById(R.id.driverRatingRl);
			inviteFriendText = (TextView) findViewById(R.id.inviteFriendText);
			inviteFriendText.setTypeface(Data.latoRegular(getApplicationContext()));
			inviteFriendText.setText(getStringText(R.string.invite_earn));

			notificationCenterRl = (RelativeLayout) findViewById(R.id.notificationCenterRl);
			notificationCenterText = (TextView) findViewById(R.id.notificationCenterText);
			notificationCenterText.setTypeface(Data.latoRegular(getApplicationContext()));
			notificationCenterText.setText(getResources().getString(R.string.Notifications));

			bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
			RelativeLayoutNotificationCenter = (RelativeLayout) findViewById(R.id.RelativeLayoutNotificationCenter);
			bookingsText = (TextView) findViewById(R.id.bookingsText);
			bookingsText.setTypeface(Data.latoRegular(getApplicationContext()));

			etaTimerRLayout = (RelativeLayout) findViewById(R.id.etaTimerRLayout);
			etaTimerText = (TextView) findViewById(R.id.ETATimerText);
			etaTimerText.setTypeface(Data.digitalRegular(getApplicationContext()));

			imageViewETASmily = (ImageView) findViewById(R.id.imageViewETASmily);

			relativeLayoutSharingRides = (RelativeLayout) findViewById(R.id.relativeLayoutSharingRides);
			((TextView) findViewById(R.id.textViewSharingRides)).setTypeface(Data.latoRegular(this));

			fareDetailsRl = (RelativeLayout) findViewById(R.id.fareDetailsRl);
			driverImageRL = (LinearLayout) findViewById(R.id.driverImageRL);
			fareDetailsText = (TextView) findViewById(R.id.fareDetailsText);
			fareDetailsText.setTypeface(Data.latoRegular(getApplicationContext()));

			relativeLayoutSuperDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutSuperDrivers);
			textViewSuperDrivers = (TextView) findViewById(R.id.textViewSuperDrivers);
			textViewSuperDrivers.setTypeface(Data.latoRegular(this));
			textViewSuperDrivers.setText(getStringText(R.string.super_driver));

			relativeLayoutDestination = (RelativeLayout) findViewById(R.id.relativeLayoutDestination);
			textViewDestination = (TextView) findViewById(R.id.textViewDestination);

			callUsRl = (RelativeLayout) findViewById(R.id.callUsRl);
			callUsText = (TextView) findViewById(R.id.callUsText);
			callUsText.setTypeface(Data.latoRegular(getApplicationContext()));
			callUsText.setText(getResources().getText(R.string.call_us));

			termsConditionRl = (RelativeLayout) findViewById(R.id.termsConditionRl);
			termsConditionText = (TextView) findViewById(R.id.termsConditionText);
			termsConditionText.setTypeface(Data.latoRegular(getApplicationContext()));

			paytmRechargeRl = (RelativeLayout) findViewById(R.id.paytmRechargeRl);
			paytmRechargeText = (TextView) findViewById(R.id.paytmRechargeText);
			paytmRechargeText.setTypeface(Data.latoRegular(getApplicationContext()));
			paytmRechargeText.setText(getStringText(R.string.paytm_recharge));

			paymentsRl = (RelativeLayout) findViewById(R.id.paymentRL);
			paymentsText = (TextView) findViewById(R.id.paymentText);
			paymentsText.setTypeface(Data.latoRegular(getApplicationContext()));
			paymentsText.setText(getResources().getString(R.string.Payments));

			languagePrefrencesRl = (RelativeLayout) findViewById(R.id.languagePrefrencesRl);
			languagePrefrencesText = (TextView) findViewById(R.id.languagePrefrencesText);
			languagePrefrencesText.setTypeface(Data.latoRegular(getApplicationContext()));

			logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
			logoutText = (TextView) findViewById(R.id.logoutText);
			logoutText.setTypeface(Data.latoRegular(getApplicationContext()));



			//Top RL
			topRl = (RelativeLayout) findViewById(R.id.topRl);
			menuBtn = (Button) findViewById(R.id.menuBtn);
			checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
			imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);
			textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
			textViewTitleBarDEI.setTypeface(Data.latoRegular(this));
			textViewNotificationValue = (TextView) findViewById(R.id.textViewNotificationValue);
			textViewNotificationValue.setTypeface(Data.latoRegular(this));
			textViewNotificationValue.setVisibility(View.GONE);


			menuBtn.setVisibility(View.VISIBLE);


			//Map Layout
			mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);



			//Driver initial layout
			driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
			driverRideRequestsList = (ListView) findViewById(R.id.driverRideRequestsList);
			driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
			jugnooOffText = (TextView) findViewById(R.id.jugnooOffText);
			jugnooOffText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			jugnooOffText.setVisibility(View.GONE);

			driverRideRequestsList.setVisibility(View.GONE);

			driverRequestListAdapter = new DriverRequestListAdapter();
			driverRideRequestsList.setAdapter(driverRequestListAdapter);


			// Driver Request Accept layout
			driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
			driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
			driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn);
			driverAcceptRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverAcceptRideBtn.setText(getStringText(R.string.accept_ride));
			driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn);
			driverCancelRequestBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
			buttonDriverNavigation = (Button) findViewById(R.id.buttonDriverNavigation);
			buttonDriverNavigation.setText(getStringText(R.string.click_for_direction));


			// Driver engaged layout
			driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
			perfectRidePassengerInfoRl = (LinearLayout) findViewById(R.id.perfectRidePassengerInfoRl);
			driverPassengerInfoRl = (LinearLayout) findViewById(R.id.driverPassengerInfoRl);


			driverPerfectRidePassengerName = (TextView) findViewById(R.id.driverPerfectRidePassengerName);
			driverPerfectRidePassengerName.setTypeface(Data.latoRegular(getApplicationContext()));
			textViewRideInstructions = (TextView) findViewById(R.id.textViewRideInstructions);
			textViewRideInstructions.setTypeface(Fonts.mavenRegular(this));
			textViewRideInstructionsInRide = (TextView) findViewById(R.id.textViewRideInstructionsInRide);
			textViewRideInstructionsInRide.setTypeface(Fonts.mavenRegular(this));

			perfectRidePassengerCallRl = (RelativeLayout) findViewById(R.id.perfectRidePassengerCallRl);
			driverPassengerCallText = (TextView) findViewById(R.id.textViewCall);
			driverPassengerCallText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverEngagedMyLocationBtn = (Button) findViewById(R.id.driverEngagedMyLocationBtn);

//			distanceReset2 = (Button) findViewById(R.id.distanceReset2);


			//Start ride layout
			driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
			driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn);
			driverStartRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverStartRideBtn.setText(getStringText(R.string.start_ride));
			buttonMarkArrived = (Button) findViewById(R.id.buttonMarkArrived);
			buttonMarkArrived.setTypeface(Data.latoRegular(this));
			buttonMarkArrived.setText(getStringText(R.string.arrived));
			driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn);
			driverCancelRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverCancelRideBtn.setText(getStringText(R.string.cancel));


			//In ride layout
			driverInRideMainRl = (LinearLayout) findViewById(R.id.driverInRideMainRl);
			linearLayoutRideValues = (LinearLayout) findViewById(R.id.linearLayoutRideValues);

			driverIRDistanceText = (TextView) findViewById(R.id.driverIRDistanceText);
			driverIRDistanceText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverIRDistanceText.setText(R.string.distance);
			driverIRDistanceValue = (TextView) findViewById(R.id.driverIRDistanceValue);
			driverIRDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverIRFareText = (TextView) findViewById(R.id.driverIRFareText);
			driverIRFareText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverIRFareValue = (TextView) findViewById(R.id.driverIRFareValue);
			driverIRFareValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverRideTimeText = (TextView) findViewById(R.id.driverRideTimeText);
			driverRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverRideTimeText.setText(getStringText(R.string.ride_time));
			rideTimeChronometer = (PausableChronometer) findViewById(R.id.rideTimeChronometer);
			rideTimeChronometer.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);

			driverWaitRl = (RelativeLayout) findViewById(R.id.driverWaitRl);
			driverWaitText = (TextView) findViewById(R.id.driverWaitText);
			driverWaitText.setTypeface(Data.latoRegular(getApplicationContext()));
			driverWaitText.setText(getStringText(R.string.wait_time));

			driverWaitValue = (TextView) findViewById(R.id.driverWaitValue);
			driverWaitValue.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			driverWaitRl.setVisibility(View.GONE);

			driverIRFareRl = (RelativeLayout) findViewById(R.id.driverIRFareRl);
			driverIRFareRl.setVisibility(View.GONE);

			driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn);
			driverEndRideBtn.setTypeface(Data.latoRegular(getApplicationContext()));
			driverEndRideBtn.setText(R.string.end_ride);

			buttonMakeDelivery = (Button) findViewById(R.id.buttonMakeDelivery);
			buttonMakeDelivery.setTypeface(Fonts.mavenRegular(this));


			rideTimeChronometer.setText("00:00:00");
			driverWaitValue.setText("00:00:00");


			//Review Layout
			endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);

			reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText);
			reviewDistanceText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewDistanceText.setText(getStringText(R.string.distance));
			reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue);
			reviewDistanceValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewWaitText = (TextView) findViewById(R.id.reviewWaitText);
			reviewWaitText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewWaitText.setText(getStringText(R.string.wait_time));
			reviewWaitValue = (TextView) findViewById(R.id.reviewWaitValue);
			reviewWaitValue.setTypeface(Data.latoRegular(getApplicationContext()));
			reviewRideTimeText = (TextView) findViewById(R.id.reviewRideTimeText);
			reviewRideTimeText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			reviewRideTimeText.setText(getStringText(R.string.ride_time));
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

			textViewPerfectRideWating = (TextView) findViewById(R.id.textViewPerfectRideWating);
			textViewPerfectRideWating.setTypeface(Data.latoRegular(this));
			textViewPerfectRideWating.setText(getStringText(R.string.customer_waiting));



			endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
			jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText);
			jugnooRideOverText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
			takeFareText = (TextView) findViewById(R.id.takeFareText);
			takeFareText.setTypeface(Data.latoHeavy(getApplicationContext()));


			reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn);
			reviewSubmitBtn.setTypeface(Data.latoRegular(getApplicationContext()));

			relativeLayoutRateCustomer = (RelativeLayout) findViewById(R.id.relativeLayoutRateCustomer);
			textViewRateYourCustomer = (TextView) findViewById(R.id.textViewRateYourCustomer);
			textViewRateYourCustomer.setTypeface(Data.latoRegular(this));
			textViewRateYourCustomer.setText(getStringText(R.string.Rate_Your_Customer));
			ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
			ratingBarFeedbackSide = (RatingBar) findViewById(R.id.ratingBarFeedbackSide);
			reviewSkipBtn = (Button) findViewById(R.id.reviewSkipBtn);
			reviewSkipBtn.setTypeface(Data.latoRegular(this));

			scrollViewEndRide = (ScrollView) findViewById(R.id.scrollViewEndRide);
			linearLayoutEndRideMain = (LinearLayout) findViewById(R.id.linearLayoutEndRideMain);
			textViewScroll = (TextView) findViewById(R.id.textViewScroll);

			relativeLayoutDeliveryOver = (RelativeLayout) findViewById(R.id.relativeLayoutDeliveryOver);
			textViewDeliveryIsOver = (TextView)findViewById(R.id.textViewDeliveryIsOver);
			textViewDeliveryIsOver.setTypeface(Fonts.mavenRegular(this));
			textViewEndRideCustomerName = (TextView) findViewById(R.id.textViewEndRideCustomerName);
			textViewEndRideCustomerName.setTypeface(Fonts.mavenRegular(this));
			linearLayoutEndDelivery = (LinearLayout) findViewById(R.id.linearLayoutEndDelivery);
			((TextView)findViewById(R.id.textViewOrdersDelivered)).setTypeface(Fonts.mavenRegular(this));
			((TextView)findViewById(R.id.textViewOrdersReturned)).setTypeface(Fonts.mavenRegular(this));
			textViewOrdersDeliveredValue = (TextView) findViewById(R.id.textViewOrdersDeliveredValue);
			textViewOrdersDeliveredValue.setTypeface(Fonts.mavenRegular(this));
			textViewOrdersReturnedValue = (TextView) findViewById(R.id.textViewOrdersReturnedValue);
			textViewOrdersReturnedValue.setTypeface(Fonts.mavenRegular(this));
			relativeLayoutDeliveryOver.setVisibility(View.GONE);
			textViewEndRideCustomerName.setVisibility(View.GONE);
			linearLayoutEndDelivery.setVisibility(View.GONE);




			customerSwitcher = new CustomerSwitcher(this, drawerLayout);

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
					NudgeClient.trackEvent(HomeActivity.this, FlurryEventNames.NUDGE_MENU_CLICK, null);
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
							changeJugnooON(0, false, false);
						} else {
							changeJugnooON(1, false, false);
						}
						FlurryEventLogger.event(JUGNOO_ON_OFF);
					}
				}
			});

			imageViewSharingOnToggle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.sharingAvailable == 1) {
							toggleSharingMode(0, false, false);
						} else {
							toggleSharingMode(1, false, false);
						}
						FlurryEventLogger.event(SHARING_ENABLE);
					}
				}
			});

			imageViewDeliveryOnToggle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.getDeliveryAvailable() == 1) {
							changeJugnooON(0, false, true);
						} else {
							changeJugnooON(1, false, true);
						}
						FlurryEventLogger.event(JUGNOO_ON_OFF);
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
					NudgeClient.trackEvent(HomeActivity.this, FlurryEventNames.NUDGE_NOTIFICATION_CLICK, null);
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
					NudgeClient.trackEvent(HomeActivity.this, FlurryEventNames.NUDGE_SUPER_DRIVER_CLICK, null);
				}
			});

			relativeLayoutDestination.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, TriCitySupplyActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			callUsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Utils.openCallIntent(HomeActivity.this, Data.userData.driverSupportNumber);
					FlurryEventLogger.event(CALL_US);
				}
			});

			termsConditionRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, HelpActivity.class));
					FlurryEventLogger.event(TERMS_OF_USE);
				}
			});

			paytmRechargeRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, DriverPatymRecharge.class);
					intent.putExtra(KEY_ENGAGEMENT_ID, Data.getCurrentEngagementId());
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});


			languagePrefrencesRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, LanguagePrefrencesActivity.class));
					currentPreferredLang = Prefs.with(HomeActivity.this).getString(SPLabels.SELECTED_LANGUAGE, "");
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});


			paymentsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, PaymentActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(RIDES_OPENED);
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
					}
					else{
						DialogPopup.alertPopup(activity, "", getResources().getString(R.string.ride_in_progress_cant_logout));
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

			if(!currentPreferredLang.equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(SPLabels.SELECTED_LANGUAGE, ""))){
				currentPreferredLang = Prefs.with(HomeActivity.this).getString(SPLabels.SELECTED_LANGUAGE, "");
//				callAndHandleStateRestoreAPI();
			}


			menuLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});


			// driver initial layout events
			jugnooOffText.setOnClickListener(new OnClickListener() {

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
					acceptRequestFunc(getOpenedCustomerInfo());
					GCMIntentService.cancelUploadPathAlarm(HomeActivity.this);
					FlurryEventLogger.event(RIDE_CHECKED_AND_ACCEPTED);
				}
			});


			driverCancelRequestBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rejectRequestFuncCall(getOpenedCustomerInfo());
					FlurryEventLogger.event(RIDE_CHECKED_AND_CANCELLED);
				}
			});


			// driver start ride layout events
			perfectRidePassengerCallRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String callPhoneNumber = Prefs.with(activity).getString(SPLabels.PERFECT_CUSTOMER_CONT, "");

					if (!"".equalsIgnoreCase(callPhoneNumber)) {
						Utils.openCallIntent(HomeActivity.this, callPhoneNumber);
					} else {
						Toast.makeText(HomeActivity.this, getResources().getString(R.string.some_error_occured), Toast.LENGTH_SHORT).show();
					}
				}
			});



			driverStartRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Utils.getBatteryPercentage(HomeActivity.this) >= 10) {
						startRidePopup(HomeActivity.this, Data.getCurrentCustomerInfo());
						FlurryEventLogger.event(RIDE_STARTED);
					}
					else{
						DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.battery_level_start_text));
					}

				}
			});


			buttonMarkArrived.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (Utils.getBatteryPercentage(HomeActivity.this) >= 10) {
							DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", getResources().getString(R.string.have_arrived), getResources().getString(R.string.yes), getResources().getString(R.string.no),
									new OnClickListener() {
										@Override
										public void onClick(View v) {
											if (myLocation != null) {
												try {
													LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
													CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
													double displacement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getRequestlLatLng());

													if (displacement <= DRIVER_START_RIDE_CHECK_METERS) {
														buildAlertMessageNoGps();
														GCMIntentService.clearNotifications(activity);
														driverMarkArriveRideAsync(activity, driverAtPickupLatLng, customerInfo);
														FlurryEventLogger.event(CONFIRMING_ARRIVE_YES);
													} else {
														DialogPopup.alertPopup(activity, "", getResources().getString(R.string.present_near_customer_location));
													}
												} catch (Resources.NotFoundException e) {
													e.printStackTrace();
												}
											} else {
												Toast.makeText(activity, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
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
						}
						else{
							DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.battery_level_arrived_text));
						}
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}

				}
			});


			driverCancelRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, RideCancellationActivity.class);
					intent.putExtra(KEY_ENGAGEMENT_ID, Data.getCurrentEngagementId());
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
						FlurryEventLogger.event(CANCELED_BEFORE_ARRIVING);
					} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
						FlurryEventLogger.event(RIDE_CANCELLED_AFTER_ARRIVING);
					}
				}
			});


			driverEndRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					updateWalletBalance(Data.getCurrentCustomerInfo());
				}
			});

			buttonMakeDelivery.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					relativeLayoutContainer.setVisibility(View.VISIBLE);
					CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
					if(anyDeliveriesUnchecked(customerInfo)){
						getTransactionUtils().openDeliveryInfoListFragment(HomeActivity.this,
								getRelativeLayoutContainer(), customerInfo.getEngagementId(),
								DeliveryStatus.PENDING);
					} else{
						getTransactionUtils().openDeliveryInfoListFragment(HomeActivity.this,
								getRelativeLayoutContainer(), customerInfo.getEngagementId(),
								DeliveryStatus.COMPLETED);
					}
				}
			});


			// End ride review layout events
			endRideReviewRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			if(((System.currentTimeMillis() - Prefs.with(HomeActivity.this).getLong(Constants.FETCH_APP_TIME, 0))>Prefs.with(HomeActivity.this).getLong(Constants.FETCH_APP_API_FREQUENCY, 0))&&
					(Prefs.with(HomeActivity.this).getInt(Constants.FETCH_APP_API_ENABLED, 1)>0)) {

				apifetchAllDriverApps(Utils.fetchAllApps(HomeActivity.this));
				Log.i("allApps", String.valueOf(Utils.fetchAllApps(HomeActivity.this)));

			}

			reviewSubmitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						final CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
						if (DriverScreenMode.D_BEFORE_END_OPTIONS == driverScreenMode) {
							if (customerInfo != null
									&& 1 == customerInfo.meterFareApplicable) {

								String enteredMeterFare = editTextEnterMeterFare.getText().toString().trim();
								if ("".equalsIgnoreCase(enteredMeterFare)) {
									DialogPopup.alertPopupWithListener(HomeActivity.this, "", getResources().getString(R.string.enter_some_fare), new OnClickListener() {
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
										String message = getResources().getString(R.string.amount_entered) + getResources().getString(R.string.rupee) + " " + enteredMeterFare;
										if (1 == customerInfo.luggageChargesApplicable) {
											if (luggageCountAdded > 0) {
												message = message + "\n" + luggageCountAdded + getResources().getString(R.string.luggage_added);
											} else {
												message = message + "\n" + getResources().getString(R.string.no_luggage);
											}
										}

										DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", message, getResources().getString(R.string.ok), getResources().getString(R.string.cancel),
												new OnClickListener() {
													@Override
													public void onClick(View v) {
														endRideGPSCorrection(customerInfo);
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
							} else if (customerInfo != null
									&& 1 == customerInfo.luggageChargesApplicable) {

								if (AppStatus.getInstance(activity).isOnline(activity)) {
									String message = "";
									if (luggageCountAdded > 0) {
										message = luggageCountAdded + getResources().getString(R.string.luggage_added);
									} else {
										message = getResources().getString(R.string.no_luggage);
									}

									DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "", message, getResources().getString(R.string.ok), getResources().getString(R.string.cancel),
											new OnClickListener() {
												@Override
												public void onClick(View v) {
													endRideGPSCorrection(customerInfo);
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
						} else {
							int rating = (int) ratingBarFeedback.getRating();
							rating = Math.abs(rating);
							if (0 == rating) {
								DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.please_give_customer));
							} else {
								saveCustomerRideDataInSP(customerInfo);
								submitFeedbackToCustomerAsync(HomeActivity.this, customerInfo, rating);
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
					saveCustomerRideDataInSP(Data.getCurrentCustomerInfo());
					MeteringService.clearNotifications(HomeActivity.this);
					Data.removeCustomerInfo(Integer.parseInt(Data.getCurrentEngagementId()), EngagementStatus.ENDED.getOrdinal());
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
					FlurryEventLogger.event(OK_ON_FARE_SCREEN);
					perfectRideStateRestore();
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
					try {
						if (s.length() > 0) {
							if (textViewMeterFareRupee.getVisibility() == View.GONE) {
								textViewMeterFareRupee.setVisibility(View.VISIBLE);
							}
						} else {
							if (textViewMeterFareRupee.getVisibility() == View.VISIBLE) {
								textViewMeterFareRupee.setVisibility(View.GONE);
							}
						}
						CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
						String fare = Utils.getDecimalFormatForMoney().format(getTotalFare(customerInfo,
								customerInfo.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this),
								customerInfo.getElapsedRideTime(HomeActivity.this),
								customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(), HomeActivity.this)));
						if (!fare.equalsIgnoreCase(s.toString())) {
							fareFetchedFromJugnoo = 0;
						}
					} catch (Exception e) {
						e.printStackTrace();
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
							textViewEndRideLuggageCount.setText(getResources().getString(R.string.no_lugage));
						} else {
							textViewEndRideLuggageCount.setText("" + luggageCountAdded);
						}
					}
				}
			});


//			distanceReset2.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					MeteringService.gpsInstance(HomeActivity.this).distanceReset();
//				}
//			});


			try {
				if (Data.userData != null) {
					if (Data.userData.remainigPenaltyPeriod > 0) {
						driverTimeOutPopup(HomeActivity.this, Data.userData.remainigPenaltyPeriod);
					}
					if (1==Data.userData.paytmRechargeEnabled) {
						paytmRechargeRl.setVisibility(View.VISIBLE);
					} else {
						paytmRechargeRl.setVisibility(View.GONE);
					}

					if (1==Data.userData.destinationOptionEnable) {
						relativeLayoutDestination.setVisibility(View.VISIBLE);
					} else {
						relativeLayoutDestination.setVisibility(View.GONE);
					}


				} else {
					finish();
					startActivity(new Intent(this, SplashNewActivity.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try{
				JSONObject map = new JSONObject();
				map.put(KEY_LATITUDE, Data.latitude);
				map.put(KEY_LONGITUDE, Data.longitude);
				NudgeClient.trackEvent(this, NUDGE_APP_OPEN, map);
			} catch(Exception e){
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		try {
			String type = intent.getStringExtra("type");
			if (type.equalsIgnoreCase("accept")) {
				CustomerInfo customerInfo = driverRequestListAdapter
						.customerInfos.get(driverRequestListAdapter.customerInfos.indexOf
								(new CustomerInfo(Integer.parseInt(intent.getExtras().getString("engagement_id")))));
				acceptRequestFunc(customerInfo);
				FlurryEventLogger.event(RIDE_ACCEPTED);

			} else if (type.equalsIgnoreCase("cancel")) {

				CustomerInfo customerInfo = driverRequestListAdapter
						.customerInfos.get(driverRequestListAdapter.customerInfos.indexOf
								(new CustomerInfo(Integer.parseInt(intent.getExtras().getString("engagement_id")))));
				rejectRequestFuncCall(customerInfo);
				FlurryEventLogger.event(RIDE_CANCELLED);
			}
		} catch (Exception e) {
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

				map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {
						if (arg0.getTitle().equalsIgnoreCase((getResources().getString(R.string.pickup_location)))) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, getResources().getString(R.string.pickup_location), "");
							map.setInfoWindowAdapter(customIW);
							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("customer_current_location")) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, arg0.getSnippet(), "");
							map.setInfoWindowAdapter(customIW);
							return true;
						} else if (arg0.getTitle().equalsIgnoreCase("start ride location")) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, getResources().getString(R.string.start_loc), "");
							map.setInfoWindowAdapter(customIW);
							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("driver position")) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, getResources().getString(R.string.driver_loc), "");
							map.setInfoWindowAdapter(customIW);
							return false;
						} else if (arg0.getTitle().equalsIgnoreCase("next_pickup_marker")) {
							CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, getResources().getString(R.string.customer_2), "");
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

				if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode
						|| DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
					customerRideDataGlobal.setDistance(GpsDistanceCalculator.getTotalDistanceFromSP(this));
					customerRideDataGlobal.setWaitTime(GpsDistanceCalculator.getWaitTimeFromSP(this));
				}


				Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.YES);

				changeDriverOptionsUI();

				switchDriverScreen(driverScreenMode);


				changeJugnooONUIAndInitService();


				Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Data.SERVER_URL);

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
				}, 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void acceptRequestFunc(CustomerInfo customerInfo) {
		try {
			if (1 != customerInfo.getIsPooled()
					&& 1 != customerInfo.getIsDelivery()
					&& DriverScreenMode.D_IN_RIDE == driverScreenMode) {
				new ApiAcceptRide(this, new ApiAcceptRide.Callback() {
					@Override
					public void onSuccess(LatLng pickupLatLng, String customerName) {
						Data.nextPickupLatLng = pickupLatLng;
						Data.nextCustomerName = customerName;
						createPerfectRideMarker();
						Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal());
						driverPerfectRidePassengerName.setText(customerName);
						perfectRidePassengerInfoRl.setVisibility(View.VISIBLE);
						driverPassengerInfoRl.setVisibility(View.GONE);
						GCMIntentService.clearNotifications(getApplicationContext());
						driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
								EngagementStatus.REQUESTED.getOrdinal()));
					}

					@Override
					public void onFailure() {
						callAndHandleStateRestoreAPI();
					}
				}).acceptRide(Data.userData.accessToken,
						String.valueOf(customerInfo.userId),
						String.valueOf(customerInfo.engagementId),
						String.valueOf(customerInfo.referenceId),
						myLocation.getLatitude(),
						myLocation.getLongitude(), 1);
			}
			else {
				if (Utils.getBatteryPercentage(this) >= 20) {
					GCMIntentService.clearNotifications(HomeActivity.this);
					GCMIntentService.stopRing(true);
					driverAcceptRideAsync(HomeActivity.this, customerInfo);
				} else {
					DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.battery_level_text));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rejectRequestFuncCall(CustomerInfo customerInfo) {
		try {
			if (1 != customerInfo.getIsPooled()
					&& 1 != customerInfo.getIsDelivery()
					&& DriverScreenMode.D_IN_RIDE == driverScreenMode) {
				new ApiRejectRequest(this, new ApiRejectRequest.Callback() {
					@Override
					public void onSuccess(String engagementId) {
						Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
						GCMIntentService.clearNotifications(getApplicationContext());
						GCMIntentService.stopRing(true);
						try {
							if (perfectRideMarker != null) {
								perfectRideMarker.remove();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
								EngagementStatus.REQUESTED.getOrdinal()));
					}
				}).rejectRequestAsync(Data.userData.accessToken,
						String.valueOf(customerInfo.userId),
						String.valueOf(customerInfo.engagementId),
						String.valueOf(customerInfo.referenceId));
			} else {
				GCMIntentService.clearNotifications(HomeActivity.this);
				GCMIntentService.stopRing(true);
				driverRejectRequestAsync(HomeActivity.this, customerInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void apifetchAllDriverApps(JSONArray appList){
		try {
			new ApiFetchDriverApps(this, new ApiFetchDriverApps.Callback() {
                @Override
                public void onSuccess() {
					Prefs.with(HomeActivity.this).save(Constants.FETCH_APP_TIME, System.currentTimeMillis());
                }
            }).fetchDriverAppAsync(Data.userData.accessToken, appList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void sendToFareDetails() {
		HelpParticularActivity.helpSection = HelpSection.FARE_DETAILS;
		startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
		FlurryEventLogger.fareDetailsOpened(Data.userData.accessToken);
	}



	public void changeJugnooON(int mode, boolean enableSharing, boolean toggleDelivery) {
		if (mode == 1) {
			if (myLocation != null) {
				switchJugnooOnThroughServer(1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
						enableSharing, toggleDelivery);
			}
			else{
				Toast.makeText(HomeActivity.this, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
			}
		} else {
			if (Data.userData.sharingEnabled == 1 && Data.userData.sharingAvailable == 1) {
				toggleSharingMode(0, true, toggleDelivery);
			} else {
				switchJugnooOnThroughServer(0, new LatLng(0, 0), false, toggleDelivery);
			}
		}
	}


	public void toggleSharingMode(int mode, boolean disableAutos, boolean toggleDelivery) {
		if (mode == 1) {
			if (myLocation != null) {
				if (Data.userData.autosAvailable == 0) {
					changeJugnooON(1, true, toggleDelivery);
				} else {
					toggleSharingModeAPI(1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), false, toggleDelivery);
				}
			}
			else{
				Toast.makeText(HomeActivity.this, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
			}
		} else {
			toggleSharingModeAPI(0, new LatLng(0, 0), disableAutos, toggleDelivery);
		}
	}


	public void switchJugnooOnThroughServer(final int jugnooOnFlag, final LatLng latLng, final boolean enableSharing,
											final boolean toggleDelivery) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HashMap<String, String> params = new HashMap<String, String>();

					params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
					params.put("business_id", "1");
					if(toggleDelivery){
						params.put(KEY_DELIVERY_FLAG, "" + jugnooOnFlag);
						if(Data.userData.autosAvailable == 1 && jugnooOnFlag == 0 && myLocation != null) {
							params.put(KEY_LATITUDE, "" + myLocation.getLatitude());
							params.put(KEY_LONGITUDE, "" + myLocation.getLongitude());
						} else{
							params.put(KEY_LATITUDE, "" + latLng.latitude);
							params.put(KEY_LONGITUDE, "" + latLng.longitude);
						}
					} else {
						params.put(KEY_FLAG, "" + jugnooOnFlag);
						params.put(KEY_LATITUDE, "" + latLng.latitude);
						params.put(KEY_LONGITUDE, "" + latLng.longitude);
					}

					Response response = RestClient.getApiServices().switchJugnooOnThroughServerRetro(params);
					String result = new String(((TypedByteArray) response.getBody()).getBytes());

					JSONObject jObj = new JSONObject(result);

					if (jObj.has(KEY_FLAG)) {
						int flag = jObj.getInt(KEY_FLAG);
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							if(toggleDelivery){
								Data.userData.setDeliveryAvailable(jugnooOnFlag);
							} else {
								Data.userData.autosAvailable = jugnooOnFlag;
							}
							changeJugnooONUIAndInitService();
							if (jugnooOnFlag == 1) {
								AGPSRefresh.softRefreshGpsData(HomeActivity.this);
							}
							nudgeJugnooOnOff(latLng.latitude, latLng.longitude);
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
					toggleSharingMode(1, false, toggleDelivery);
				}
			}
		}).start();
	}

	private void nudgeJugnooOnOff(double latitude, double longitude){
		try{
			JSONObject map = new JSONObject();
			map.put(KEY_LATITUDE, latitude);
			map.put(KEY_LONGITUDE, longitude);
			if(Data.userData.autosAvailable == 1){
				NudgeClient.trackEvent(activity, NUDGE_JUGNOO_ON, map);
			} else{
				NudgeClient.trackEvent(activity, NUDGE_JUGNOO_OFF, map);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void toggleSharingModeAPI(final int mode, final LatLng latLng, final boolean disableAutos,
									 final boolean toggleDelivery) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
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
					switchJugnooOnThroughServer(0, new LatLng(0, 0), false, toggleDelivery);
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

			if (1 == Data.userData.sharingEnabled) {
				relativeLayoutSharingOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutSharingOn.setVisibility(View.GONE);
				Data.userData.sharingAvailable = 0;
			}

			if (1 == Data.userData.getDeliveryEnabled()) {
				relativeLayoutDeliveryOn.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutDeliveryOn.setVisibility(View.GONE);
				Data.userData.setDeliveryAvailable(0);
			}
		}
	}


	public void changeJugnooONUIAndInitService() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				try {
					if (Data.userData != null) {
						if (1 == Data.userData.autosAvailable) {
							imageViewAutosOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						} else {
							imageViewAutosOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if (1 == Data.userData.sharingAvailable) {
							imageViewSharingOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						} else {
							imageViewSharingOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if (1 == Data.userData.getDeliveryAvailable()) {
							imageViewDeliveryOnToggle.setImageResource(R.drawable.jugnoo_on_button);
						} else {
							imageViewDeliveryOnToggle.setImageResource(R.drawable.jugnoo_off_button);
						}

						if (!checkIfDriverOnline()) {
							if (DriverScreenMode.D_INITIAL == driverScreenMode) {
								setDriverServiceRunOnOnlineBasis();
								jugnooOffText.setVisibility(View.VISIBLE);
								stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

								GCMIntentService.clearNotifications(HomeActivity.this);
								GCMIntentService.stopRing(true);

								if (map != null) {
									map.clear();
								}
							}
						} else {
							if (DriverScreenMode.D_INITIAL == driverScreenMode) {
								Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
								setDriverServiceRunOnOnlineBasis();
								jugnooOffText.setVisibility(View.GONE);
								fetchHeatMapData(HomeActivity.this);
								startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
							}
						}


						updateReceiveRequestsFlag();

						updateDriverRequestsAccAvailaviblity();

						if (Data.getAssignedCustomerInfosListForStatus(
								EngagementStatus.REQUESTED.getOrdinal()).size() == 0) {
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
		try {
			if (0 == Data.userData.autosAvailable
					&& 0 == Data.userData.mealsAvailable
					&& 0 == Data.userData.fatafatAvailable
					&& 0 == Data.userData.sharingAvailable
					&& 0 == Data.userData.getDeliveryAvailable()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
				if(1 != Data.userData.autosAvailable){
					Data.clearAssignedCustomerInfosListForStatusWithDelivery(EngagementStatus.REQUESTED.getOrdinal(), 0);
				}
				if(1 != Data.userData.getDeliveryAvailable()){
					Data.clearAssignedCustomerInfosListForStatusWithDelivery(EngagementStatus.REQUESTED.getOrdinal(), 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void updateReceiveRequestsFlag() {
		if (Data.userData != null) {
			if (DriverScreenMode.D_INITIAL == driverScreenMode) {
				if (0 == Data.userData.autosAvailable
						&& 0 == Data.userData.mealsAvailable
						&& 0 == Data.userData.fatafatAvailable
						&& 0 == Data.userData.getDeliveryAvailable()) {
					Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_OFFLINE.getOrdinal());
				}
			}
		}
	}


	OnClickListener mapMyLocationClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (myLocation != null) {
				if (map.getCameraPosition().zoom < 12) {
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12), MAP_ANIMATION_TIME, null);
				} else if (map.getCameraPosition().zoom < 15) {
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15), MAP_ANIMATION_TIME, null);
				} else {
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), MAP_ANIMATION_TIME, null);
				}
			}
			else{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.waiting_for_location), Toast.LENGTH_LONG).show();
				reconnectLocationFetchers();
			}
		}
	};

	OnClickListener startNavigation = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				if (myLocation != null) {
					LatLng latLng = null;
					CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
					if(DriverScreenMode.D_IN_RIDE == driverScreenMode
							&& customerInfo.getIsDelivery() != 1
							&& customerInfo.getDropLatLng() != null){
						latLng = customerInfo.getDropLatLng();
					}
					else if(DriverScreenMode.D_ARRIVED == driverScreenMode
								|| DriverScreenMode.D_START_RIDE == driverScreenMode){
						latLng = customerInfo.getRequestlLatLng();
					}
					if(latLng != null) {
						Utils.openNavigationIntent(HomeActivity.this, latLng);
						Intent intent = new Intent(HomeActivity.this, GeanieView.class);
						startService(intent);
					}
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.waiting_for_location), Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};


	Handler reconnectionHandler = null;

	public void reconnectLocationFetchers() {
		if (reconnectionHandler == null) {
			destroyFusedLocationFetchers();
			reconnectionHandler = new Handler();
			reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
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
				textViewTitleBarDEI.setText(getResources().getString(R.string.jugnoo));
			} else {
				linearLayoutDEI.setVisibility(View.VISIBLE);
				textViewTitleBarDEI.setText(Data.userData.deiValue);
				imageViewTitleBarDEI.setVisibility(View.VISIBLE);
			}

			if (Data.userData.showDriverRating > 0 && Data.userData.showDriverRating < 6) {
				driverRatingRl.setVisibility(View.VISIBLE);
				ratingBarFeedbackSide.setRating((float) Data.userData.showDriverRating);
				ratingValue.setText("" + new DecimalFormat("#.#").format(Data.userData.showDriverRating));
			} else {
				driverRatingRl.setVisibility(View.GONE);
			}

			int unreadNotificationsCount = Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0);
			if (unreadNotificationsCount > 0) {
				textViewNotificationValue.setVisibility(View.VISIBLE);
				textViewNotificationValue.setText("" + unreadNotificationsCount);
			} else {
				textViewNotificationValue.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void updateDriverServiceFast(String choice) {
		Database2.getInstance(HomeActivity.this).updateDriverServiceFast(choice);
	}


	public void switchDriverScreen(DriverScreenMode mode) {
		if (userMode == UserMode.DRIVER) {

			driverScreenMode = Data.getCurrentState();
			mode = driverScreenMode;

			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();

			initializeFusedLocationFetchers();

			if (mode == DriverScreenMode.D_RIDE_END) {
				if (endRideData != null) {
					mapLayout.setVisibility(View.GONE);
					endRideReviewRl.setVisibility(View.VISIBLE);


					double totalDistanceInKm = Math.abs(customerInfo.getTotalDistance(customerRideDataGlobal
							.getDistance(HomeActivity.this), HomeActivity.this) / 1000.0);
					String kmsStr = "";
					if (totalDistanceInKm > 1) {
						kmsStr = getResources().getString(R.string.kms);
					} else {
						kmsStr = getResources().getString(R.string.km);
					}

					reviewDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
					reviewWaitValue.setText(waitTime + " "+ getResources().getString(R.string.min));
					reviewRideTimeValue.setText(rideTime + " "+ getResources().getString(R.string.min));
					reviewFareValue.setText(getResources().getString(R.string.rupees)+" " + Utils.getDecimalFormatForMoney().format(totalFare));


					if(customerInfo.getIsDelivery() == 1){
						jugnooRideOverText.setText(getResources().getString(R.string.total_fare));
						takeFareText.setText(getResources().getText(R.string.rupee) + " "
								+ Utils.getDecimalFormatForMoney().format(endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.VISIBLE);
						linearLayoutEndDelivery.setVisibility(View.VISIBLE);
						textViewEndRideCustomerName.setVisibility(View.GONE);
						textViewDeliveryIsOver.setText(getResources().getString(R.string.delivery_is_over));

						int totalDelivered = 0;
						int totalUndelivered = 0;
						for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
							if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
								totalDelivered++;
							} else{
								totalUndelivered++;
							}
						}
						textViewOrdersDeliveredValue.setText(String.valueOf(totalDelivered));
						textViewOrdersReturnedValue.setText(String.valueOf(totalUndelivered));
						textViewRateYourCustomer.setText(getResources().getString(R.string.rate_your_vendor));
					}
					else if(customerInfo.getIsPooled() == 1){
						jugnooRideOverText.setText(getResources().getString(R.string.total_fare));
						takeFareText.setText(getResources().getText(R.string.rupee) + " "
								+ Utils.getDecimalFormatForMoney().format(endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.VISIBLE);
						linearLayoutEndDelivery.setVisibility(View.GONE);
						textViewEndRideCustomerName.setVisibility(View.VISIBLE);
						textViewEndRideCustomerName.setText(customerInfo.getName());
						textViewDeliveryIsOver.setText(getResources().getString(R.string.reached_destination));

						textViewRateYourCustomer.setText(getResources().getString(R.string.Rate_Your_Customer));
					}
					else{
						jugnooRideOverText.setText(getResources().getString(R.string.jugnoo_ride_over));
						takeFareText.setText(getResources().getString(R.string.take_cash)+" "
								+getResources().getText(R.string.rupee)+" "
								+Utils.getDecimalFormatForMoney().format(endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.GONE);
						linearLayoutEndDelivery.setVisibility(View.GONE);
						textViewEndRideCustomerName.setVisibility(View.GONE);
						textViewRateYourCustomer.setText(getResources().getString(R.string.Rate_Your_Customer));
					}

					endRideInfoRl.setVisibility(View.VISIBLE);
					reviewFareValue.setText(getResources().getString(R.string.rupees) + " " + Utils.getDecimalFormatForMoney().format(endRideData.toPay));


					reviewReachedDistanceRl.setVisibility(View.VISIBLE);
					linearLayoutMeterFare.setVisibility(View.GONE);
					relativeLayoutRateCustomer.setVisibility(View.VISIBLE);
					ratingBarFeedback.setVisibility(View.VISIBLE);
					reviewSkipBtn.setVisibility(View.VISIBLE);
					ratingBarFeedback.setRating(0);

					try {
						if (customerInfo.getWaitingChargesApplicable() == 1) {
							reviewWaitTimeRl.setVisibility(View.VISIBLE);
							imageViewEndRideWaitSep.setVisibility(View.VISIBLE);
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

				} else {
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
				}
			} else if (mode == DriverScreenMode.D_BEFORE_END_OPTIONS) {
				mapLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.VISIBLE);

				editTextEnterMeterFare.setText("");

				endRideInfoRl.setVisibility(View.GONE);
				reviewReachedDistanceRl.setVisibility(View.GONE);
				linearLayoutMeterFare.setVisibility(View.VISIBLE);
				relativeLayoutRateCustomer.setVisibility(View.GONE);
				ratingBarFeedback.setVisibility(View.GONE);
				reviewSkipBtn.setVisibility(View.GONE);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reviewSubmitBtn.getLayoutParams();
				layoutParams.setMargins(0, 0, 0, (int) (270 * ASSL.Yscale()));
				reviewSubmitBtn.setLayoutParams(layoutParams);
				reviewSubmitBtn.setText(getResources().getString(R.string.ok));

				try {
					if (customerInfo.getLuggageChargesApplicable() == 1) {
						relativeLayoutEndRideLuggageCount.setVisibility(View.VISIBLE);
					} else {
						relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
				}

				luggageCountAdded = 0;
				textViewEndRideLuggageCount.setText(getResources().getString(R.string.no_lugage));

			} else {
				mapLayout.setVisibility(View.VISIBLE);
				endRideReviewRl.setVisibility(View.GONE);

			}

			try {
				heatMapHandler.removeCallbacks(heatMapRunnalble);
			} catch (Exception e) {
				e.printStackTrace();
			}

			switch (mode) {

				case D_INITIAL:
					updateDriverServiceFast("no");

					driverInitialLayout.setVisibility(View.VISIBLE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);

					setDriverServiceRunOnOnlineBasis();
					if (checkIfDriverOnline()) {
						startService(new Intent(this, DriverLocationUpdateService.class));
					}

					if (map != null) {
						map.clear();
						drawHeatMapData(heatMapResponseGlobal);
					}

					try {
						if (timer != null) {
							etaTimerText.setText(" ");
							timer.cancel();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						if (smilyHandler != null && smilyRunnalble != null) {
							smilyHandler.removeCallbacks(smilyRunnalble);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


					cancelMapAnimateAndUpdateRideDataTimer();
					try {


						if(Prefs.with(HomeActivity.this).getLong(SPLabels.HEAT_MAP_REFRESH_FREQUENCY, 0) >0) {
							heatMapHandler.postDelayed(heatMapRunnalble, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Data.nextPickupLatLng = null;
					Data.nextCustomerName = null;


					break;


				case D_REQUEST_ACCEPT:

					updateDriverServiceFast("no");

					setDriverServiceRunOnOnlineBasis();
					if (!Utils.isServiceRunning(HomeActivity.this, DriverLocationUpdateService.class)) {
						startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					}

					if (map != null) {
						map.clear();
					}

					if (getOpenedCustomerInfo() != null) {
						if(map != null){
							addCustomerPickupMarker(map, getOpenedCustomerInfo(), getOpenedCustomerInfo().getRequestlLatLng());
						}
					}


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.VISIBLE);
					driverEngagedLayout.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);


					cancelMapAnimateAndUpdateRideDataTimer();
					Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, false);

					break;


				case D_ARRIVED:

					updateDriverServiceFast("yes");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					if (map != null) {
						setAttachedCustomerMarkers();
					}

					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					driverStartRideMainRl.setVisibility(View.VISIBLE);
					driverInRideMainRl.setVisibility(View.GONE);

					driverStartRideBtn.setVisibility(View.GONE);
					buttonMarkArrived.setVisibility(View.VISIBLE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					Utils.setDrawableColor(buttonMarkArrived, customerInfo.getColor(),
							getResources().getColor(R.color.new_orange));

					setEtaTimerVisibility(customerInfo);
					cancelMapAnimateAndUpdateRideDataTimer();
					setTextViewRideInstructions();
					updateCustomers();

					break;

				case D_START_RIDE:

					updateDriverServiceFast("yes");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					if (map != null) {
						setAttachedCustomerMarkers();
					}


					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerRLayout.setVisibility(View.GONE);
					try {
						if (timer != null) {
							etaTimerText.setText(" ");
							timer.cancel();
							smilyHandler.removeCallbacks(smilyRunnalble);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					driverStartRideMainRl.setVisibility(View.VISIBLE);
					driverInRideMainRl.setVisibility(View.GONE);

					driverStartRideBtn.setVisibility(View.VISIBLE);
					buttonMarkArrived.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);
					if(customerInfo.getIsDelivery() == 1){
						driverStartRideBtn.setText(getResources().getString(R.string.start_delivery));
					} else{
						driverStartRideBtn.setText(getResources().getString(R.string.start_ride));
					}
					Utils.setDrawableColor(driverStartRideBtn, customerInfo.getColor(),
							getResources().getColor(R.color.new_orange));


					cancelMapAnimateAndUpdateRideDataTimer();
					setTextViewRideInstructions();
					updateCustomers();

					break;


				case D_IN_RIDE:

					updateDriverServiceFast("no");

					Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					rideTimeChronometer.eclipsedTime = customerInfo.getElapsedRideTime(HomeActivity.this);
					rideTimeChronometer.setText(Utils.getChronoTimeFromMillis(rideTimeChronometer.eclipsedTime));
					startRideChronometer(customerInfo);


					if (map != null) {
						setAttachedCustomerMarkers();
						addStartMarker();
					}

					long waitTime = customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(), HomeActivity.this);
					updateDistanceFareTexts(customerInfo, customerInfo.getTotalDistance(customerRideDataGlobal
									.getDistance(HomeActivity.this), HomeActivity.this),
							rideTimeChronometer.eclipsedTime,
							waitTime);

					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));


					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerRLayout.setVisibility(View.GONE);


					driverStartRideMainRl.setVisibility(View.GONE);
					driverInRideMainRl.setVisibility(View.VISIBLE);
					if(customerInfo.getIsDelivery() == 1){
						linearLayoutRideValues.setVisibility(View.GONE);
					} else{
						linearLayoutRideValues.setVisibility(View.VISIBLE);
					}

					driverEndRideBtn.setText(getResources().getString(R.string.end_ride));

					try {
						if (customerInfo.getWaitingChargesApplicable() == 1) {
							driverWaitRl.setVisibility(View.VISIBLE);
							driverWaitValue.setText(Utils.getChronoTimeFromMillis(waitTime));
						} else {
							driverWaitRl.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						driverWaitRl.setVisibility(View.GONE);
					}

					if(customerInfo.getIsDelivery() == 1){
						driverIRFareRl.setVisibility(View.GONE);
					} else{
						driverIRFareRl.setVisibility(View.GONE);
					}
					setMakeDeliveryButtonVisibility();
					setDeliveryMarkers();
					setTextViewRideInstructions();


					startMapAnimateAndUpdateRideDataTimer();

					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);
					createPerfectRideMarker();
					updateCustomers();


					break;


				case D_BEFORE_END_OPTIONS:

					updateDriverServiceFast("no");

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);

					cancelMapAnimateAndUpdateRideDataTimer();

					break;


				case D_RIDE_END:

					updateDriverServiceFast("no");

					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));
					startService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

					GCMIntentService.clearNotifications(getApplicationContext());
					GCMIntentService.stopRing(true);

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					cancelMapAnimateAndUpdateRideDataTimer();
					Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, false);

					break;


				default:
					driverInitialLayout.setVisibility(View.VISIBLE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);

					cancelMapAnimateAndUpdateRideDataTimer();

			}
			showAllRideRequestsOnMap();

			try {
				Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_ARRIVED_DISTANCE, "" + Data.userData.driverArrivalDistance);
				updateReceiveRequestsFlag();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mode != DriverScreenMode.D_BEFORE_END_OPTIONS) {
				startMeteringService();
			}

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
			MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, mode);
		}
	}



	public void startMeteringService() {

		lastGPSLocation = null;
		lastFusedLocation = null;
		distanceUpdateFromService = false;

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

			Database2.getInstance(this).updateMetringState(Database2.ON);
			Prefs.with(this).save(SPLabels.METERING_STATE, Database2.ON);
			startService(new Intent(this, MeteringService.class));

		} else {
			if(Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()) == null
					|| Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()).size() == 0) {
				Database2.getInstance(this).updateMetringState(Database2.OFF);
				Prefs.with(this).save(SPLabels.METERING_STATE, Database2.OFF);
				stopService(new Intent(this, MeteringService.class));
			}
		}
	}



	public void buttonDriverNavigationSetVisibility(int visibility){
		buttonDriverNavigation.setVisibility(visibility);
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
				builder.setMessage(getResources().getString(R.string.app_need_active_gps))
						.setCancelable(false)
						.setPositiveButton(getResources().getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
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
					builder.setMessage(getResources().getString(R.string.app_needs_network_time))
							.setCancelable(false)
							.setPositiveButton(getResources().getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
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


	private void setEtaTimerVisibility(CustomerInfo customerInfo){
		try {
			if (customerInfo.getIsDelivery() == 0
					&& customerInfo.getIsPooled() == 0
					&& Prefs.with(this).getLong(SPLabels.CURRENT_ETA, 0) - System.currentTimeMillis() > 0) {
				etaTimerRLayout.setVisibility(View.VISIBLE);
				if (Prefs.with(this).getLong(SPLabels.CURRENT_ETA, 0) > 0) {
					long eta = Prefs.with(this).getLong(SPLabels.CURRENT_ETA, 0) - System.currentTimeMillis();
					if (eta > 0) {
						etaTimer(eta);
					} else {
						if (Prefs.with(HomeActivity.this).getInt(SPLabels.ON_FINISH_CALLED, 0) == 0) {
							Prefs.with(HomeActivity.this).save(SPLabels.ETA_EXPIRE, System.currentTimeMillis());
						}
						changeSmilyTask();
						imageViewETASmily.setImageResource(R.drawable.happy_face);
					}
				}
			} else{
				etaTimerRLayout.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();

		HomeActivity.appInterruptHandler = HomeActivity.this;

		if (!checkIfUserDataNull()) {
			setUserData();

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

		EventsHolder.displayPushHandler = this;

		new BlockedAppsUninstallIntent().uninstallBlockedApps(this);


		if(!currentPreferredLang.equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(SPLabels.SELECTED_LANGUAGE, ""))){
			currentPreferredLang = Prefs.with(HomeActivity.this).getString(SPLabels.SELECTED_LANGUAGE, "");
			ActivityCompat.finishAffinity(this);
			sendToSplash();
		}
	}


	@Override
	protected void onPause() {

		GCMIntentService.clearNotifications(getApplicationContext());

		try {
			if (userMode == UserMode.DRIVER) {
				if (driverScreenMode != DriverScreenMode.D_IN_RIDE) {
					destroyFusedLocationFetchers();
				} else{

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onPause();
	}


	public void saveCustomerRideDataInSP(CustomerInfo customerInfo){
		try{
			JSONObject jObj = new JSONObject(Prefs.with(this).getString(SP_CUSTOMER_RIDE_DATAS_OBJECT, EMPTY_OBJECT));
			if(EngagementStatus.ENDED.getOrdinal() != customerInfo.getStatus()) {
				JSONObject jc = new JSONObject();
				jc.put(KEY_DISTANCE, customerRideDataGlobal.getDistance(HomeActivity.this));
				jc.put(KEY_HAVERSINE_DISTANCE, customerRideDataGlobal.getHaversineDistance());
				jc.put(KEY_RIDE_TIME, System.currentTimeMillis());
				jc.put(KEY_WAIT_TIME, customerRideDataGlobal.getWaitTime());
				jObj.put(String.valueOf(customerInfo.getEngagementId()), jc);
			} else{
				jObj.remove(String.valueOf(customerInfo.getEngagementId()));
			}
			Prefs.with(this).save(SP_CUSTOMER_RIDE_DATAS_OBJECT, jObj.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void onBackPressed() {
		try {
			if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				if(getSupportFragmentManager().getBackStackEntryCount() == 1){
					relativeLayoutContainer.setVisibility(View.GONE);
				}
				super.onBackPressed();
				setMakeDeliveryButtonVisibility();
			}
			else if (userMode == UserMode.DRIVER) {
				if (driverScreenMode == DriverScreenMode.D_IN_RIDE
						|| driverScreenMode == DriverScreenMode.D_START_RIDE
						|| driverScreenMode == DriverScreenMode.D_ARRIVED) {
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
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
			cancelMapAnimateAndUpdateRideDataTimer();

			GCMIntentService.clearNotifications(HomeActivity.this);
			GCMIntentService.stopRing(true);

			MeteringService.clearNotifications(HomeActivity.this);

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


	private double getTotalFare(CustomerInfo customerInfo, double totalDistance, long elapsedTimeInMillis, long waitTimeInMillis) {
		if(customerInfo.getIsPooled() == 1 && customerInfo.getPoolFare() != null){
			return customerInfo.getPoolFare().getFare();
		}
		double totalDistanceInKm = Math.abs(totalDistance / 1000.0d);

		double rideTimeInMin = Math.round(((double) elapsedTimeInMillis) / 60000.0d);
		double waitTimeInMin = Math.round(((double) waitTimeInMillis) / 60000.0d);

		if (customerInfo.getWaitingChargesApplicable() == 1) {
			rideTimeInMin = rideTimeInMin - waitTimeInMin;
		} else {
			waitTimeInMin = 0d;
		}

		return Data.fareStructure.calculateFare(totalDistanceInKm, rideTimeInMin, waitTimeInMin);
	}

	public synchronized void updateDistanceFareTexts(CustomerInfo customerInfo, double distance, long elapsedTime, long waitTime) {
		try {
			double totalDistanceInKm = Math.abs(distance / 1000.0);

			driverIRDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " "+getStringText(R.string.km));
			driverWaitValue.setText(Utils.getChronoTimeFromMillis(waitTime));

			if (Data.fareStructure != null) {
				driverIRFareValue.setText(getResources().getString(R.string.rupee) + " "
						+ Utils.getDecimalFormatForMoney().format(getTotalFare(customerInfo, distance,
						elapsedTime, waitTime)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public synchronized void displayOldPath() {

		try {
			if (Color.TRANSPARENT != MAP_PATH_COLOR) {
				ArrayList<CurrentPathItem> currentPathItemsArr = Database2.getInstance(HomeActivity.this).getCurrentPathItemsSaved();
				PolylineOptions polylineOptions = new PolylineOptions();
				polylineOptions.width(ASSL.Xscale() * 5);
				polylineOptions.color(MAP_PATH_COLOR);
				polylineOptions.geodesic(true);
				for (CurrentPathItem currentPathItem : currentPathItemsArr) {
					if (1 != currentPathItem.googlePath) {
						polylineOptions.add(currentPathItem.sLatLng, currentPathItem.dLatLng);
					}
				}
				map.addPolyline(polylineOptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	class ViewHolderDriverRequest {
		TextView textViewRequestName, textViewRequestAddress, textViewRequestDetails,
				textViewRequestTime, textViewRequestFareFactor, textViewDeliveryFare, textViewDeliveryApprox,
				textViewRequestDistance;
		Button buttonAcceptRide, buttonCancelRide;
		ImageView imageViewRequestType;
		LinearLayout relative, linearLayoutDeliveryFare;
		int id;
	}

	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRequest holder;

		ArrayList<CustomerInfo> customerInfos;

		Handler handlerRefresh;
		Runnable runnableRefresh;

		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			customerInfos = new ArrayList<>();
			handlerRefresh = new Handler();
			runnableRefresh = new Runnable() {
				@Override
				public void run() {
					DriverRequestListAdapter.this.notifyDataSetChanged();
					handlerRefresh.postDelayed(runnableRefresh, 1000);
				}
			};
			handlerRefresh.postDelayed(runnableRefresh, 1000);
		}

		@Override
		public int getCount() {
			return customerInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public synchronized void setResults(ArrayList<CustomerInfo> customerInfos) {
			if (this.customerInfos == null) {
				this.customerInfos = new ArrayList<>();
			}
			this.customerInfos.clear();
			this.customerInfos.addAll(customerInfos);
			this.notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if (DriverScreenMode.D_RIDE_END != HomeActivity.driverScreenMode
					&& DriverScreenMode.D_REQUEST_ACCEPT != HomeActivity.driverScreenMode
					&& customerInfos.size() > 0) {
				driverRideRequestsList.setVisibility(View.VISIBLE);
			} else {
				driverRideRequestsList.setVisibility(View.GONE);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {

				holder = new ViewHolderDriverRequest();
				convertView = mInflater.inflate(R.layout.list_item_driver_request, null);

				holder.textViewRequestName = (TextView) convertView.findViewById(R.id.textViewRequestName);
				holder.textViewRequestName.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestAddress = (TextView) convertView.findViewById(R.id.textViewRequestAddress);
				holder.textViewRequestAddress.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestDetails = (TextView) convertView.findViewById(R.id.textViewRequestDetails);
				holder.textViewRequestDetails.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestTime = (TextView) convertView.findViewById(R.id.textViewRequestTime);
				holder.textViewRequestTime.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestFareFactor = (TextView) convertView.findViewById(R.id.textViewRequestFareFactor);
				holder.textViewRequestFareFactor.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
				holder.textViewDeliveryFare = (TextView) convertView.findViewById(R.id.textViewDeliveryFare);
				holder.textViewDeliveryFare.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewDeliveryApprox = (TextView) convertView.findViewById(R.id.textViewDeliveryApprox);
				holder.textViewDeliveryApprox.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.textViewRequestDistance = (TextView) convertView.findViewById(R.id.textViewRequestDistance);
				holder.textViewRequestDistance.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.buttonAcceptRide = (Button) convertView.findViewById(R.id.buttonAcceptRide);
				holder.buttonAcceptRide.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.buttonCancelRide = (Button) convertView.findViewById(R.id.buttonCancelRide);
				holder.buttonCancelRide.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);


				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
				holder.linearLayoutDeliveryFare = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeliveryFare);

				holder.relative.setTag(holder);
				holder.buttonAcceptRide.setTag(holder);
				holder.buttonCancelRide.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRequest) convertView.getTag();
			}


			CustomerInfo customerInfo = customerInfos.get(position);

			holder.id = position;


			holder.textViewRequestAddress.setText(customerInfo.getAddress());
			long timeDiff = DateOperations.getTimeDifference(DateOperations.getCurrentTime(), customerInfo.getStartTime());
			long timeDiffInSec = timeDiff / 1000;
			holder.textViewRequestTime.setText(""+timeDiffInSec + " "+getResources().getString(R.string.sec_left));
			if (myLocation != null) {
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				double distance = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), customerInfo.getRequestlLatLng());
				distance = distance * 1.5;
				if (distance >= 1000) {
					holder.textViewRequestDistance.setText("" + decimalFormatNoDecimal.format(distance / 1000) + getResources().getString(R.string.km_away));
				} else {
					holder.textViewRequestDistance.setText("" + decimalFormatNoDecimal.format(distance) +" "+getResources().getString(R.string.m_away));
				}
			} else {
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}


			holder.textViewRequestName.setVisibility(View.GONE);
			holder.textViewRequestDetails.setVisibility(View.GONE);
			holder.linearLayoutDeliveryFare.setVisibility(View.GONE);
			if(customerInfo.getIsDelivery() == 1){
				if(customerInfo.getTotalDeliveries() > 0) {
					holder.textViewRequestDetails.setVisibility(View.VISIBLE);
					holder.textViewRequestDetails.setText(getResources().getString(R.string.delivery_numbers)
							+ " " + customerInfo.getTotalDeliveries());
				}
				holder.textViewRequestName.setVisibility(View.VISIBLE);
				holder.textViewRequestName.setText(customerInfo.getName());
				holder.linearLayoutDeliveryFare.setVisibility(View.VISIBLE);
				holder.textViewDeliveryFare.setText(getResources().getString(R.string.fare1)
						+" "+getResources().getString(R.string.rupee)
						+" "+customerInfo.getEstimatedFare());
			}


			if (customerInfo.getFareFactor() > 1 || customerInfo.getFareFactor() < 1) {
				holder.textViewRequestFareFactor.setVisibility(View.VISIBLE);
				holder.textViewRequestFareFactor.setText(getResources().getString(R.string.rate)+" " + decimalFormat.format(customerInfo.getFareFactor()) + "x");
			} else {
				holder.textViewRequestFareFactor.setVisibility(View.GONE);
			}

			if(customerInfo.getIsPooled() == 1){
				holder.imageViewRequestType.setImageResource(R.drawable.ic_pool_request);
			} else if(customerInfo.getIsDelivery() == 1){
				holder.imageViewRequestType.setImageResource(R.drawable.ic_delivery_request);
			} else{
				holder.imageViewRequestType.setImageResource(R.drawable.ic_auto_request);
			}


			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (DriverScreenMode.D_INITIAL == driverScreenMode) {
							holder = (ViewHolderDriverRequest) v.getTag();
							CustomerInfo customerInfo = customerInfos.get(holder.id);
							setOpenedCustomerInfo(customerInfo);
							driverScreenMode = DriverScreenMode.D_REQUEST_ACCEPT;
							switchDriverScreen(driverScreenMode);
							map.animateCamera(CameraUpdateFactory.newLatLng(customerInfo.getRequestlLatLng()), MAP_ANIMATION_TIME, null);
							FlurryEventLogger.event(RIDE_CHECKED);
						}
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
						CustomerInfo customerInfo1 = customerInfos.get(holder.id);
						acceptRequestFunc(customerInfo1);
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
						CustomerInfo customerInfo1 = customerInfos.get(holder.id);
						rejectRequestFuncCall(customerInfo1);
						FlurryEventLogger.event(RIDE_CANCELLED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}


	}



	public void driverAcceptRideAsync(final Activity activity, final CustomerInfo customerInfo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

			if (myLocation != null) {
				Data.latitude = myLocation.getLatitude();
				Data.longitude = myLocation.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();

			params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));

			if(Data.getAssignedCustomerInfosListForEngagedStatus() == null
					|| Data.getAssignedCustomerInfosListForEngagedStatus().size() == 0){
				Data.setCurrentEngagementId(String.valueOf(customerInfo.getEngagementId()));
			}
			params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));

			params.put(KEY_LATITUDE, "" + Data.latitude);
			params.put(KEY_LONGITUDE, "" + Data.longitude);

			params.put(KEY_DEVICE_NAME, Utils.getDeviceName());
			params.put(KEY_IMEI, DeviceUniqueID.getUniqueId(this));
			params.put(KEY_APP_VERSION, "" + Utils.getAppVersion(this));

			params.put(KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
			params.put(KEY_IS_POOLED, String.valueOf(customerInfo.getIsPooled()));
			params.put(KEY_IS_DELIVERY, String.valueOf(customerInfo.getIsDelivery()));

			Log.i("request", String.valueOf(params));

			GCMIntentService.cancelUploadPathAlarm(HomeActivity.this);
			RestClient.getApiServices().driverAcceptRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
					Prefs.with(activity).save(SPLabels.ACCEPT_RIDE_TIME, String.valueOf(System.currentTimeMillis()));
					acceptRideSucess(jsonString,
							String.valueOf(customerInfo.getEngagementId()),
							String.valueOf(customerInfo.getUserId()));
					GCMIntentService.stopRing(true);
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

	public void acceptRideSucess(String jsonString, String engagementId, String customerId) {
		try {
			JSONObject jObj = new JSONObject(jsonString);
			int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.RIDE_ACCEPTED.getOrdinal());
			if(!SplashNewActivity.checkIfTrivialAPIErrors(this, jObj, flag)){
				if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
					Data.fareStructure = JSONParser.parseFareObject(jObj);
					Data.fareStructure.fareFactor = jObj.optDouble(KEY_FARE_FACTOR, 1);
					Data.fareStructure.luggageFare = jObj.optDouble(KEY_LUGGAGE_CHARGES, 0d);
					Data.fareStructure.convenienceCharge = jObj.optDouble(KEY_CONVENIENCE_CHARGE, 0);
					Data.fareStructure.convenienceChargeWaiver = jObj.optDouble(KEY_CONVENIENCE_CHARGE_WAIVER, 0);
					int referenceId = jObj.optInt(KEY_REFERENCE_ID, 0);

					int isDelivery = jObj.optInt(KEY_IS_DELIVERY, 0);
					JSONObject userData = jObj.optJSONObject(KEY_USER_DATA);
					String userName = "", userImage = "", phoneNo = "", rating = "", address = "",
							vendorMessage = "";
					double jugnooBalance = 0, pickupLatitude = 0, pickupLongitude = 0, estimatedFare = 0;
					int totalDeliveries = 0;
					if(isDelivery == 1){
						userName = userData.optString(KEY_NAME, "");
						userImage = userData.optString(KEY_USER_IMAGE, "");
						phoneNo = userData.optString(KEY_PHONE, "");
						rating = userData.optString(KEY_USER_RATING, "4");
						jugnooBalance = userData.optDouble(KEY_JUGNOO_BALANCE, 0);
						pickupLatitude = userData.optDouble(KEY_LATITUDE, 0);
						pickupLongitude = userData.optDouble(KEY_LONGITUDE, 0);
						address = userData.optString(KEY_ADDRESS, "");
						totalDeliveries = userData.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
						estimatedFare = userData.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
						vendorMessage = userData.optString(Constants.KEY_VENDOR_MESSAGE, "");
					} else{
						userName = userData.optString(KEY_USER_NAME, "");
						userImage = userData.optString(KEY_USER_IMAGE, "");
						phoneNo = userData.optString(KEY_PHONE_NO, "");
						rating = userData.optString(KEY_USER_RATING, "4");
						jugnooBalance = userData.optDouble(KEY_JUGNOO_BALANCE, 0);
						pickupLatitude = jObj.optDouble(KEY_PICKUP_LATITUDE, 0);
						pickupLongitude = jObj.optDouble(KEY_PICKUP_LONGITUDE, 0);
						address = userData.optString(KEY_ADDRESS, "");
					}

					LatLng pickuplLatLng = new LatLng(pickupLatitude, pickupLongitude);
					CouponInfo couponInfo = JSONParser.parseCouponInfo(jObj);
					PromoInfo promoInfo = JSONParser.parsePromoInfo(jObj);

					int meterFareApplicable = jObj.optInt("meter_fare_applicable", 0);
					int getJugnooFareEnabled = jObj.optInt("get_jugnoo_fare_enabled", 1);
					int luggageChargesApplicable = jObj.optInt("luggage_charges_applicable", 0);
					int waitingChargesApplicable = jObj.optInt("waiting_charges_applicable", 0);
					Prefs.with(HomeActivity.this).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObj.optLong("eta", 0));
					int cachedApiEnabled = jObj.optInt(KEY_CACHED_API_ENABLED, 0);
					int isPooled = jObj.optInt(KEY_IS_POOLED, 0);

					Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal());

					GCMIntentService.clearNotifications(getApplicationContext());

					initializeStartRideVariables();

					CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
							Integer.parseInt(customerId), referenceId,
							userName, phoneNo, pickuplLatLng, cachedApiEnabled,
							userImage, rating, couponInfo, promoInfo, jugnooBalance,
							meterFareApplicable, getJugnooFareEnabled, luggageChargesApplicable,
							waitingChargesApplicable, EngagementStatus.ACCEPTED.getOrdinal(), isPooled,
							isDelivery, address, totalDeliveries, estimatedFare, vendorMessage);

					JSONParser.parsePoolFare(jObj, customerInfo);

					Data.addCustomerInfo(customerInfo);

					driverScreenMode = DriverScreenMode.D_ARRIVED;
					switchDriverScreen(driverScreenMode);

					driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
							EngagementStatus.REQUESTED.getOrdinal()));
				} else {
					try {
						String message = JSONParser.getServerMessage(jObj);
						DialogPopup.alertPopup(activity, "", "" + message);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								DialogPopup.dismissAlertPopup();
							}
						}, 3000);
						reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				DialogPopup.dismissLoadingDialog();

			} else{
				reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			DialogPopup.dismissLoadingDialog();
		}
		Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ");
		new ApiAcceptRide().perfectRideVariables(HomeActivity.this, "", "", "", 0, 0);
		Prefs.with(activity).save(SPLabels.PERFECT_CUSTOMER_CONT, "");
		DialogPopup.dismissLoadingDialog();
	}

	public void createPerfectRideMarker() {
		if (Data.nextPickupLatLng != null) {
			driverPerfectRidePassengerName.setText(Data.nextCustomerName);
			perfectRidePassengerInfoRl.setVisibility(View.VISIBLE);
			driverPassengerInfoRl.setVisibility(View.GONE);
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.title("next_pickup_marker");
			markerOptions.position(Data.nextPickupLatLng);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
					.createCustomMarkerBitmap(HomeActivity.this, assl, 50f, 69f, R.drawable.passenger)));
			perfectRideMarker = map.addMarker(markerOptions);
			CustomInfoWindow customIW = new CustomInfoWindow(HomeActivity.this, perfectRideMarker.getSnippet(), "");
			map.setInfoWindowAdapter(customIW);
		}
	}


	public void reduceRideRequest(String engagementId, int status) {
		Data.removeCustomerInfo(Integer.parseInt(engagementId), status);
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
	}


	public void driverRejectRequestAsync(final Activity activity, final CustomerInfo customerInfo) {

		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
			params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));

			params.put(KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
			params.put(KEY_IS_POOLED, String.valueOf(customerInfo.getIsPooled()));
			params.put(KEY_IS_DELIVERY, String.valueOf(customerInfo.getIsDelivery()));

			RestClient.getApiServices().driverRejectRequestRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());

						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.RIDE_ACCEPTED.getOrdinal());
						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
							if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
								String log = jObj.getString("log");
								DialogPopup.alertPopup(activity, "", "" + log);
							} else{
								if (map != null) {
									map.clear();
									drawHeatMapData(heatMapResponseGlobal);
								}
								stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

								reduceRideRequest(String.valueOf(customerInfo.getEngagementId()), EngagementStatus.REQUESTED.getOrdinal());
								nudgeRequestCancel(customerInfo);
								new DriverTimeoutCheck().timeoutBuffer(activity, 0);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}


	}

	private void nudgeRequestCancel(CustomerInfo customerInfo){
		try{
			JSONObject map = new JSONObject();
			map.put(Constants.KEY_ENGAGEMENT_ID, customerInfo.getEngagementId());
			map.put(Constants.KEY_CUSTOMER_ID, customerInfo.getUserId());
			NudgeClient.trackEvent(this, FlurryEventNames.NUDGE_REQUEST_CANCEL, map);
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void driverMarkArriveRideAsync(final Activity activity, final LatLng driverAtPickupLatLng,
										  final CustomerInfo customerInfo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			try {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<String, String>();

				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
				params.put(KEY_PICKUP_LATITUDE, "" + driverAtPickupLatLng.latitude);
				params.put(KEY_PICKUP_LONGITUDE, "" + driverAtPickupLatLng.longitude);
				params.put(KEY_DRYRUN_DISTANCE, "" + customerRideDataGlobal.getDistance(HomeActivity.this));
				params.put(KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));

				RestClient.getApiServices().driverMarkArriveRideRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

									Database2.getInstance(activity).insertRideData("0.0", "0.0", "" + System.currentTimeMillis(), customerInfo.getEngagementId());
									Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "arrived sucessful");

									driverScreenMode = DriverScreenMode.D_START_RIDE;
									Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);

									switchDriverScreen(driverScreenMode);
								} else {
									DialogPopup.alertPopup(activity, "", message);
									callAndHandleStateRestoreAPI();
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
			} catch (Exception e) {
				e.printStackTrace();
			}


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	public void initializeStartRideVariables() {
		if(Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()) == null
				|| Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()).size() == 0) {
			Utils.deleteCache(this);

			if (DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal() == driverScreenMode.getOrdinal()
					|| DriverScreenMode.D_ARRIVED.getOrdinal() == driverScreenMode.getOrdinal()
					|| DriverScreenMode.D_START_RIDE.getOrdinal() == driverScreenMode.getOrdinal()
					|| DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode.getOrdinal()) {

			} else {
				Database2.getInstance(this).deleteRideData();
			}
			Database2.getInstance(this).deleteAllCurrentPathItems();
			Database.getInstance(this).deleteSavedPath();

			customerRideDataGlobal.setWaitTime(0);
			customerRideDataGlobal.setStartRideTime(System.currentTimeMillis());
			customerRideDataGlobal.setDistance(-1);

			MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, driverScreenMode);
			MeteringService.gpsInstance(this).stop();
			Prefs.with(HomeActivity.this).save(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0);
		}
	}



	public void driverStartRideAsync(final Activity activity, final LatLng driverAtPickupLatLng,
									 final CustomerInfo customerInfo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

			HashMap<String, String> params = new HashMap<String, String>();

			params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
			params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
			params.put(KEY_PICKUP_LATITUDE, String.valueOf(driverAtPickupLatLng.latitude));
			params.put(KEY_PICKUP_LONGITUDE, String.valueOf(driverAtPickupLatLng.longitude));
			params.put(KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));

			Log.i("params", "=" + params);

			RestClient.getApiServices().driverStartRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.RIDE_STARTED.getOrdinal());
						String message = JSONParser.getServerMessage(jObj);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {
								double dropLatitude = 0, dropLongitude = 0;
								try {
									if (jObj.has(KEY_OP_DROP_LATITUDE) && jObj.has(KEY_OP_DROP_LONGITUDE)) {
										dropLatitude = jObj.getDouble(KEY_OP_DROP_LATITUDE);
										dropLongitude = jObj.getDouble(KEY_OP_DROP_LONGITUDE);
										Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_DISTANCE, jObj.optString(KEY_PR_DISTANCE, "1000"));
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if ((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)) {
									customerInfo.setDropLatLng(null);
								} else {
									customerInfo.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
								}

								if (customerInfo.getIsDelivery() == 1) {
									customerInfo.setDeliveryInfos(JSONParser.parseDeliveryInfos(jObj));
									Data.deliveryReturnOptionList = JSONParser.parseDeliveryReturnOptions(jObj);
								}

								try {
									new ApiSendCallLogs().sendCallLogs(HomeActivity.this, Data.userData.accessToken,
											String.valueOf(customerInfo.getEngagementId()), customerInfo.getPhoneNumber());
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (map != null) {
									map.clear();
								}

								initializeStartRideVariables();

								if(Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()) == null
										|| Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()).size() == 0) {
									Prefs.with(activity).save(Constants.SP_START_LATITUDE,
											String.valueOf(driverAtPickupLatLng.latitude));
									Prefs.with(activity).save(Constants.SP_START_LONGITUDE,
											String.valueOf(driverAtPickupLatLng.longitude));
								}

								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);
								saveCustomerRideDataInSP(customerInfo);

								switchDriverScreen(driverScreenMode);

								try {
									JSONObject map = new JSONObject();
									map.put(KEY_LATITUDE, driverAtPickupLatLng.latitude);
									map.put(KEY_LONGITUDE, driverAtPickupLatLng.longitude);
									map.put(KEY_ENGAGEMENT_ID, customerInfo.getEngagementId());
									map.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
									NudgeClient.trackEvent(activity, NUDGE_RIDE_START, map);
								} catch (Exception e) {
									e.printStackTrace();
								}
								new DriverTimeoutCheck().clearCount(activity);
								Prefs.with(HomeActivity.this).save(SPLabels.CUSTOMER_PHONE_NUMBER, customerInfo.getPhoneNumber());
							} else {
								DialogPopup.alertPopup(activity, "", message);
								callAndHandleStateRestoreAPI();
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


	public void startRideChronometer(CustomerInfo customerInfo){
		if(customerInfo.getIsPooled() != 1) {
			rideTimeChronometer.start();
		} else{
			rideTimeChronometer.stop();
		}
	}

	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude,
								   int flagDistanceTravelled, CustomerInfo customerInfo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			autoEndRideAPI(activity, lastAccurateLatLng, dropLatitude, dropLongitude,
					flagDistanceTravelled, customerInfo);
		} else {
			if (DriverScreenMode.D_BEFORE_END_OPTIONS != driverScreenMode) {
				driverScreenMode = DriverScreenMode.D_IN_RIDE;
				startRideChronometer(customerInfo);
			}
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	double enteredMeterFare = 0;

	//TODO End ride
	public void autoEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, final double dropLatitude, final double dropLongitude,
							   int flagDistanceTravelled, final CustomerInfo customerInfo) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

		double totalDistanceFromLogInMeter =Database2.getInstance(activity).getLastRideData(customerInfo.getEngagementId(), 1).accDistance;
		double totalDistanceFromLog = Math.abs(totalDistanceFromLogInMeter / 1000.0);
		double totalDistance = customerInfo
				.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
		double totalDistanceInKm = Math.abs(totalDistance / 1000.0);

		double Limit_endRideMinute = 360;
		double Average_endRideMinute = totalDistanceInKm * 2;

		long rideTimeInMillis = customerInfo.getElapsedRideTime(HomeActivity.this);

		long customerWaitTimeMillis = customerInfo
				.getTotalWaitTime(customerRideDataGlobal.getWaitTime(), HomeActivity.this);

		if (customerInfo != null && customerInfo.waitingChargesApplicable != 1) {
			customerWaitTimeMillis = 0;
		}

		double rideMinutes = Math.round(((double)rideTimeInMillis) / 60000.0d);
		double waitMinutes = Math.round(((double) customerWaitTimeMillis) / 60000.0d);

		if (rideMinutes < Limit_endRideMinute) {
			rideTime = decimalFormatNoDecimal.format(rideMinutes);
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		} else {
			rideMinutes = Average_endRideMinute;
			rideTimeInMillis = (long) (Average_endRideMinute * 60000.0);
			rideTime = String.valueOf(decimalFormatNoDecimal.format(Average_endRideMinute));
			waitTime = decimalFormatNoDecimal.format(waitMinutes);
		}

		double rideTimeSeconds = Math.ceil(((double) rideTimeInMillis) / 1000d);
		String rideTimeSecondsStr = decimalFormatNoDecimal.format(rideTimeSeconds);
		double waitTimeSeconds = Math.ceil(((double)customerWaitTimeMillis) / 1000d);
		String waitTimeSecondsStr = decimalFormatNoDecimal.format(waitTimeSeconds);

		final long eoRideTimeInMillis = rideTimeInMillis;
		final long eoWaitTimeInMillis = customerWaitTimeMillis;


		final double totalHaversineDistanceInKm = Math.abs(customerInfo
				.getTotalHaversineDistance(customerRideDataGlobal.getHaversineDistance(), HomeActivity.this) / 1000.0);

		final HashMap<String, String> params = new HashMap<>();

		params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
		params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
		params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
		params.put(KEY_LATITUDE, String.valueOf(dropLatitude));
		params.put(KEY_LONGITUDE, String.valueOf(dropLongitude));
		params.put(KEY_DISTANCE_TRAVELLED, decimalFormat.format(totalDistanceInKm));
		params.put(KEY_DISTANCE_TRAVELLED_LOG, decimalFormat.format(totalDistanceFromLog));

		params.put(KEY_WAIT_TIME, waitTime);
		params.put(KEY_RIDE_TIME, rideTime);
		params.put(KEY_RIDE_TIME_SECONDS, rideTimeSecondsStr);
		params.put(KEY_WAIT_TIME_SECONDS, waitTimeSecondsStr);
		params.put(KEY_IS_CACHED, "0");
		params.put("flag_distance_travelled", "" + flagDistanceTravelled);
		params.put("last_accurate_latitude", "" + lastAccurateLatLng.latitude);
		params.put("last_accurate_longitude", "" + lastAccurateLatLng.longitude);
		params.put("ride_distance_using_haversine", "" + decimalFormat.format(totalHaversineDistanceInKm));


		enteredMeterFare = 0;

		if (customerInfo != null) {
			params.put(KEY_REFERENCE_ID, String.valueOf(customerInfo.referenceId));

			try {
				if (1 == customerInfo.meterFareApplicable) {
					enteredMeterFare = Double.parseDouble(editTextEnterMeterFare.getText().toString().trim());
					params.put("meter_fare", "" + enteredMeterFare);
					params.put("fare_fetched_from_jugnoo", "" + fareFetchedFromJugnoo);
				}
			} catch (Exception e) {
			}

			try {
				if (1 == customerInfo.luggageChargesApplicable) {
					params.put("luggage_count", "" + luggageCountAdded);
				}
			} catch (Exception e) {
			}
		}


		params.put("business_id", "1");

		Log.i("end_ride params =", "=" + params);

		String url = PendingCall.END_RIDE.getPath();
		if(customerInfo.getIsDelivery() == 1){
			url = PendingCall.END_DELIVERY.getPath();
		}

		if (customerInfo.getCachedApiEnabled() == 1 && customerInfo.getIsDelivery() != 1) {
			endRideOffline(activity, url, params, eoRideTimeInMillis, eoWaitTimeInMillis,
					customerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded, totalDistanceFromLog);
		} else {
			if(customerInfo.getIsDelivery() == 1) {
				RestClient.getApiServices().endDelivery(params, new CallbackEndRide(customerInfo, totalHaversineDistanceInKm, dropLatitude, dropLongitude, params,
						eoRideTimeInMillis, eoWaitTimeInMillis, url, totalDistanceFromLog));
			} else{
				RestClient.getApiServices().autoEndRideAPIRetro(params, new CallbackEndRide(customerInfo, totalHaversineDistanceInKm, dropLatitude, dropLongitude, params,
						eoRideTimeInMillis, eoWaitTimeInMillis, url, totalDistanceFromLog));
			}
		}
	}

	private class CallbackEndRide<RegisterScreenResponse> implements Callback<RegisterScreenResponse>{
		private CustomerInfo customerInfo;
		private double totalHaversineDistanceInKm, dropLatitude, dropLongitude, totalDistanceFromLog;
		private HashMap<String, String> params;
		private long eoRideTimeInMillis, eoWaitTimeInMillis;
		private String url;

		public CallbackEndRide(CustomerInfo customerInfo, double totalHaversineDistanceInKm, double dropLatitude, double dropLongitude, HashMap<String, String> params,
							   long eoRideTimeInMillis, long eoWaitTimeInMillis, String url, double totalDistanceFromLog) {
			this.customerInfo = customerInfo;
			this.totalHaversineDistanceInKm = totalHaversineDistanceInKm;
			this.dropLatitude = dropLatitude;
			this.dropLongitude = dropLongitude;
			this.params = params;
			this.eoRideTimeInMillis = eoRideTimeInMillis;
			this.eoWaitTimeInMillis = eoWaitTimeInMillis;
			this.totalDistanceFromLog = totalDistanceFromLog;
			this.url = url;
		}

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
					startRideChronometer(customerInfo);
				} else {

					try {
						totalFare = jObj.getDouble("fare");
					} catch (Exception e) {
						e.printStackTrace();
						totalFare = 0;
					}

					endRideData = JSONParser.parseEndRideData(jObj, String.valueOf(customerInfo.getEngagementId()), totalFare);
					if (customerInfo != null) {
						if (customerInfo.couponInfo != null) {
							customerInfo.couponInfo.couponApplied = true;
						} else if (customerInfo.promoInfo != null) {
							customerInfo.promoInfo.promoApplied = true;
						}
					}

					if (map != null) {
						map.clear();
						drawHeatMapData(heatMapResponseGlobal);
					}

					rideTimeChronometer.stop();

					driverUploadPathDataFileAsync(activity, customerInfo.getEngagementId(), totalHaversineDistanceInKm);

					driverScreenMode = DriverScreenMode.D_RIDE_END;
					Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);
					switchDriverScreen(driverScreenMode);


					initializeStartRideVariables();
					nudgeRideEnd(customerInfo, dropLatitude, dropLongitude, params);

				}
			} catch (Exception exception) {
				exception.printStackTrace();
				driverScreenMode = DriverScreenMode.D_IN_RIDE;
				startRideChronometer(customerInfo);
				DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
			}
			AGPSRefresh.hardRefreshGpsData(HomeActivity.this);
			DialogPopup.dismissLoadingDialog();

		}

		@Override
		public void failure(RetrofitError error) {
			Log.e("error", "=" + error);
			if(customerInfo.getIsDelivery() != 1) {
				endRideOffline(activity, url, params, eoRideTimeInMillis, eoWaitTimeInMillis,
						customerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded, totalDistanceFromLog);
			} else{
				DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				if (DriverScreenMode.D_BEFORE_END_OPTIONS != driverScreenMode) {
					driverScreenMode = DriverScreenMode.D_IN_RIDE;
					startRideChronometer(customerInfo);
				}
				DialogPopup.dismissLoadingDialog();
			}
		}
	}

	private void nudgeRideEnd(CustomerInfo customerInfo, double dropLatitude, double dropLongitude, HashMap<String, String> params){
		try{
			JSONObject map = new JSONObject();
			map.put(KEY_LATITUDE, dropLatitude);
			map.put(KEY_LONGITUDE, dropLongitude);
			map.put(KEY_ENGAGEMENT_ID, customerInfo.getEngagementId());
			map.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.userId));
			map.put("params", params.toString());
			NudgeClient.trackEvent(activity, NUDGE_RIDE_END, map);
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	private double calculateCouponDiscount(double totalFare, CouponInfo couponInfo) {
		double finalDiscount = 0;

		if (BenefitType.DISCOUNTS.getOrdinal() == couponInfo.benefitType) {        //coupon discount
			finalDiscount = ((totalFare * couponInfo.discountPrecent) / 100) < couponInfo.maximumDiscountValue ?
					Math.ceil(((totalFare * couponInfo.discountPrecent) / 100)) : couponInfo.maximumDiscountValue;
		} else if (BenefitType.CAPPED_FARE.getOrdinal() == couponInfo.benefitType) {        // coupon capped fare
			if (totalFare < couponInfo.cappedFare) {        // fare less than capped fare
				finalDiscount = 0;
			} else {                                                                // fare greater than capped fare
				double maxDiscount = couponInfo.cappedFareMaximum - couponInfo.cappedFare;
				finalDiscount = totalFare - couponInfo.cappedFare;
				finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
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
		} else if (BenefitType.CAPPED_FARE.getOrdinal() == promoInfo.benefitType) {        // promotion capped fare
			if (totalFare < promoInfo.cappedFare) {        // fare less than capped fare
				finalDiscount = 0;
			} else {                                                                // fare greater than capped fare
				double maxDiscount = promoInfo.cappedFareMaximum - promoInfo.cappedFare;
				finalDiscount = totalFare - promoInfo.cappedFare;
				finalDiscount = finalDiscount > maxDiscount ? maxDiscount : finalDiscount;
			}
		} else {
			finalDiscount = 0;
		}
		promoInfo.promoApplied = true;

		return finalDiscount;
	}


	public void endRideOffline(Activity activity, String url, HashMap<String, String> params, long rideTimeInMillis, long waitTimeInMillis,
							   CustomerInfo customerInfo, final double dropLatitude, final double dropLongitude, double enteredMeterFare, int luggageCountAdded, double totalDistanceFromLog) {
		try {

			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, finalDistance;
			int paymentMode = PaymentMode.CASH.getOrdinal();

			double totalDistance = customerInfo.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
//			double totalDistanceFromLogFile = Database2.getInstance(activity).getLastRideData(customerInfo.getEngagementId(), 1).accDistance;
			double totalDistanceFromLogInMeters = totalDistanceFromLog * 1000;
			finalDistance = 0;
			if (totalDistance < totalDistanceFromLogInMeters) {
				long ridetimeInSec = rideTimeInMillis / 1000L;
				Double speed = totalDistanceFromLogInMeters / ridetimeInSec;
				if (speed <= 15) {
					finalDistance = totalDistanceFromLogInMeters;
					customerRideDataGlobal.setDistance(finalDistance);
				} else {
					finalDistance = totalDistance;
				}
			} else {
				finalDistance = totalDistance;
			}
			Log.e("offline =============", "============");
			Log.i("rideTime", "=" + rideTime);

			try {
				if (customerInfo != null && 1 == customerInfo.meterFareApplicable) {
					totalFare = enteredMeterFare;
				} else {
					totalFare = getTotalFare(customerInfo, finalDistance,
							rideTimeInMillis, waitTimeInMillis);
				}
			} catch (Exception e) {
				e.printStackTrace();
				totalFare = 0;
			}

			try {
				if (customerInfo != null && 1 == customerInfo.luggageChargesApplicable) {
					totalFare = totalFare + (luggageCountAdded * Data.fareStructure.luggageFare);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			try {
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "Data.fareStructure = " + Data.fareStructure);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "rideTime = " + rideTime);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "waitTime = " + waitTime);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "totalDistance = " + totalDistance);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "totalFare = " + totalFare);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "assignedCustomerInfo = " + customerInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}


			actualFare = totalFare;

			finalToPay = totalFare;

			Log.i("totalFare == endride offline ", "=" + totalFare);
			Log.i("endRideOffline assignedCustomerInfo.couponInfo=", "=" + customerInfo.couponInfo);
			Log.i("endRideOffline assignedCustomerInfo.promoInfo=", "=" + customerInfo.promoInfo);


			LatLng dropLatLng = new LatLng(dropLatitude, dropLongitude);

			if (customerInfo.couponInfo != null) {        // coupon
				if (CouponType.DROP_BASED.getOrdinal() == customerInfo.couponInfo.couponType) {
					double distanceFromDrop = MapUtils.distance(dropLatLng, customerInfo.couponInfo.droplLatLng);
					if (distanceFromDrop <= customerInfo.couponInfo.dropRadius) {                                     // drop condition satisfied
						finalDiscount = calculateCouponDiscount(totalFare, customerInfo.couponInfo);
					} else {
						finalDiscount = 0;
					}
				} else {
					finalDiscount = calculateCouponDiscount(totalFare, customerInfo.couponInfo);
				}
			} else if (customerInfo.promoInfo != null) {        // promotion
				if (PromotionType.DROP_BASED.getOrdinal() == customerInfo.promoInfo.promoType) {
					double distanceFromDrop = MapUtils.distance(dropLatLng, customerInfo.promoInfo.droplLatLng);
					if (distanceFromDrop <= customerInfo.promoInfo.dropRadius) {                                     // drop condition satisfied
						finalDiscount = calculatePromoDiscount(totalFare, customerInfo.promoInfo);
					} else {
						finalDiscount = 0;
					}
				} else {
					finalDiscount = calculatePromoDiscount(totalFare, customerInfo.promoInfo);
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
			Log.i("wallet Balance ", "=" + customerInfo.jugnooBalance);

			// wallet application (with split fare)
			if (customerInfo.jugnooBalance > 0 && finalToPay > 0) {    // wallet

				if (customerInfo.jugnooBalance >= finalToPay) {
					finalPaidUsingWallet = finalToPay;
				} else {
					finalPaidUsingWallet = customerInfo.jugnooBalance;
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


			endRideData = new EndRideData(String.valueOf(customerInfo.getEngagementId()), actualFare,
					finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode);

			try {
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "endRideData = " + endRideData);
			} catch (Exception e) {
				e.printStackTrace();
			}


			if (map != null) {
				map.clear();
			}

			rideTimeChronometer.stop();


			driverUploadPathDataFileAsync(activity, customerInfo.getEngagementId(),
					customerInfo.getTotalHaversineDistance(customerRideDataGlobal.getHaversineDistance(), HomeActivity.this));

			driverScreenMode = DriverScreenMode.D_RIDE_END;
			Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);
			switchDriverScreen(driverScreenMode);


			params.put("is_cached", "1");
			params.put("paid_in_cash", String.valueOf(finalToPay));

			DialogPopup.dismissLoadingDialog();
			Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			try {
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "url = " + url + " params = " + params);
			} catch (Exception e) {
				e.printStackTrace();
			}


			initializeStartRideVariables();
			nudgeRideEnd(customerInfo, dropLatitude, dropLongitude, params);

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
	public void driverUploadPathDataFileAsync(final Activity activity, int engagementId, double totalHaversineDistance) {
		try {
			String rideDataStr = Database2.getInstance(activity).getRideData(engagementId);
			if (!"".equalsIgnoreCase(rideDataStr)) {
				totalHaversineDistance = totalHaversineDistance / 1000;
				rideDataStr = rideDataStr + "\n" + totalHaversineDistance;

				rideDataStr = rideDataStr + "\n" + LocationFetcher.getSavedLatFromSP(activity) + "," + LocationFetcher.getSavedLngFromSP(activity);


				final HashMap<String, String> params = new HashMap<String, String>();

				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_ENGAGEMENT_ID, String.valueOf(engagementId));
				params.put(KEY_RIDE_PATH_DATA, rideDataStr);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void submitFeedbackToCustomerAsync(final Activity activity, final CustomerInfo customerInfo, final int givenRating) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<String, String>();

				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_GIVEN_RATING, String.valueOf(givenRating));
				params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				params.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));

				Log.i("params", "=" + params);

				Data.removeCustomerInfo(customerInfo.getEngagementId(), EngagementStatus.ENDED.getOrdinal());

				RestClient.getApiServices().rateTheCustomer(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "rateTheCustomer response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Toast.makeText(activity, getResources().getString(R.string.thanks_for_feedback), Toast.LENGTH_SHORT).show();

								try{
									JSONObject map = new JSONObject();
									map.put(KEY_GIVEN_RATING, givenRating);
									map.put(KEY_ENGAGEMENT_ID, customerInfo.getEngagementId());
									map.put(KEY_CUSTOMER_ID, String.valueOf(customerInfo.userId));
									NudgeClient.trackEvent(activity, NUDGE_RATING, map);
								} catch(Exception e){
									e.printStackTrace();
								}

							} else {
							}

						} catch (Exception exception) {
							exception.printStackTrace();
						}
						perfectRideStateRestore();
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						perfectRideStateRestore();
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

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.please_wait));
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


	public void updateInRideData(CustomerInfo customerInfo) {
		try {
			if(customerInfo.getIsPooled() != 1) {
				long responseTime = System.currentTimeMillis();
				if (UserMode.DRIVER == userMode && DriverScreenMode.D_IN_RIDE == driverScreenMode) {
					if (myLocation != null) {
						double totalDistanceInKm = Math.abs(customerRideDataGlobal.getDistance(HomeActivity.this) / 1000.0);
						long rideTimeSeconds = customerInfo.getElapsedRideTime(HomeActivity.this) / 1000;
						double rideTimeMinutes = Math.ceil(rideTimeSeconds / 60);
						int lastLogId = Integer.parseInt((Prefs.with(HomeActivity.this).getString(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0)));
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("access_token", Data.userData.accessToken);
						params.put("engagement_id", String.valueOf(customerInfo.getEngagementId()));
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void fetchHeatMapData(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
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
							Log.i("fetchHeatmapData", ">message="+message);
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

			textHead.setText(getResources().getString(R.string.alert));
			textMessage.setText(getResources().getString(R.string.logout_text));

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



	public Marker addCustomerPickupMarker(GoogleMap map, CustomerInfo customerInfo, LatLng latLng) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(String.valueOf(customerInfo.getEngagementId()));
		markerOptions.snippet("");
		markerOptions.position(latLng);
		if(customerInfo.getIsDelivery() == 1){
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
					.createCustomMarkerBitmap(HomeActivity.this, assl, 51f, 70f, R.drawable.ic_delivery_pickup_marker)));
		}
		else if(customerInfo.getIsPooled() == 1){
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.setBitmapColor(CustomMapMarkerCreator
							.createCustomMarkerBitmap(HomeActivity.this, assl, 30f, 72f, R.drawable.ic_pool_marker),
							customerInfo.getColor(), activity.getResources().getColor(R.color.new_orange))));
		}
		else{
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
					.createCustomMarkerBitmap(HomeActivity.this, assl, 50f, 69f, R.drawable.passenger)));
		}
		return map.addMarker(markerOptions);
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
//						TODO stopped this one updateInRideData(Data.getCurrentCustomerInfo());
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


	void startRidePopup(final Activity activity, final CustomerInfo customerInfo) {
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

			if(customerInfo.getIsDelivery()==1){
				textMessage.setText(getResources().getString(R.string.start_delivery_text));
			}else{
				textMessage.setText(getResources().getString(R.string.start_ride_text));
			}




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
						double displacement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getRequestlLatLng());

						if (customerInfo.getIsDelivery() == 1
								|| displacement <= DRIVER_START_RIDE_CHECK_METERS) {
							buildAlertMessageNoGps();

							GCMIntentService.clearNotifications(activity);

							driverStartRideAsync(activity, driverAtPickupLatLng, customerInfo);
							FlurryEventLogger.event(START_RIDE_CONFIRMED);
						}
						else{
							DialogPopup.alertPopup(activity, "", getResources().getString(R.string.present_near_customer_location_to_start));
						}
					}
					else{
						Toast.makeText(activity, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
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

	void endRidePopup(final Activity activity, final CustomerInfo customerInfo) {
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
				walletBalanceUpdatePopup = false;


				textMessage.setText(getResources().getString(R.string.end_ride_text));


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
								if (customerInfo != null
										&& (1 == customerInfo.meterFareApplicable
										|| 1 == customerInfo.luggageChargesApplicable)) {
									driverScreenMode = DriverScreenMode.D_BEFORE_END_OPTIONS;
									switchDriverScreen(driverScreenMode);
									dialogEndRidePopup.dismiss();
								} else {
									boolean success = endRideGPSCorrection(customerInfo);
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


	public boolean endRideGPSCorrection(CustomerInfo customerInfo) {
		try {
			if (distanceUpdateFromService) {
				Location locationToUse;
				boolean fusedLocationUsed = false;

				long currentTime = System.currentTimeMillis();
				long threeMinuteMillis = 3 * 60 * 1000;

				Log.e("lastGPSLocation on end ride", "=======" + lastGPSLocation);
				Log.e("lastFusedLocation on end ride", "=======" + lastFusedLocation);

				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "lastGPSLocation on end ride = " + lastGPSLocation);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "lastFusedLocation on end ride = " + lastFusedLocation);

				LatLng oldGPSLatLng = MeteringService.gpsInstance(HomeActivity.this).getSavedLatLngFromSP(HomeActivity.this);

				long lastloctime = GpsDistanceCalculator.lastLocationTime;

				Log.e("oldGPSLatLng on end ride", "=======" + oldGPSLatLng);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "oldGPSLatLng on end ride = " + oldGPSLatLng);

				if (lastGPSLocation != null && lastFusedLocation != null) {
					long gpsLocTimeDiff = currentTime - lastGPSLocation.getTime();
					long fusedLocTimeDiff = currentTime - lastFusedLocation.getTime();

					Log.e("gpsLocTimeDiff on end ride", "=======" + gpsLocTimeDiff);
					Log.e("fusedLocTimeDiff on end ride", "=======" + fusedLocTimeDiff);

					Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "gpsLocTimeDiff=" + gpsLocTimeDiff + " and fusedLocTimeDiff=" + fusedLocTimeDiff);

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
					Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "locationToUse on end ride from myLocation=" + locationToUse);
				}


				if (locationToUse != null
						&& (Utils.compareDouble(oldGPSLatLng.latitude, 0.0) == 0)
						&& (Utils.compareDouble(oldGPSLatLng.longitude, 0.0) == 0)) {
					oldGPSLatLng = new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude());
				}
				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "oldGPSLatLng after on end ride = " + oldGPSLatLng);


				Log.e("locationToUse on end ride", "=======" + locationToUse);
				Log.e("fusedLocationUsed on end ride", "=======" + fusedLocationUsed);

				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "locationToUse on end ride=" + locationToUse);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "fusedLocationUsed on end ride=" + fusedLocationUsed);

				if (locationToUse != null) {

					GCMIntentService.clearNotifications(activity);



					rideTimeChronometer.stop();

					driverScreenMode = DriverScreenMode.D_RIDE_END;

					if (fusedLocationUsed) {
						calculateFusedLocationDistance(activity, oldGPSLatLng,
								new LatLng(locationToUse.getLatitude(), locationToUse.getLongitude()),
								lastloctime, customerInfo);
					} else {
						driverEndRideAsync(activity, oldGPSLatLng, locationToUse.getLatitude(), locationToUse.getLongitude(),
								0, customerInfo);
					}
					return true;
				} else {
					Toast.makeText(activity, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
					return false;
				}
			} else {
				Toast.makeText(activity, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(activity, getResources().getString(R.string.waiting_for_location), Toast.LENGTH_SHORT).show();
			return false;
		}
	}


	int flagDistanceTravelled = -1;
	public void calculateFusedLocationDistance(final Activity activity, final LatLng source, final LatLng destination,
											   final long lastloctime, final CustomerInfo customerInfo) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
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
								customerRideDataGlobal.setDistance(customerRideDataGlobal.getDistance(HomeActivity.this)+distanceOfPath);
								flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_DISTANCE.getOrdinal();
								Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI distanceOfPath=" + distanceOfPath + " and totalDistance=" + customerRideDataGlobal.getDistance(HomeActivity.this));
							} else {

								throw new Exception();
							}
						} catch (Exception e) {
							e.printStackTrace();
							customerRideDataGlobal.setDistance(customerRideDataGlobal.getDistance(HomeActivity.this)+displacement);
							flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_DISPLACEMENT.getOrdinal();
							Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI excep displacement=" + displacement + " and totalDistance=" + customerRideDataGlobal.getDistance(HomeActivity.this));

						}
					} else {
						double complimentaryDistance = 4.5 * lastTimeDiff;
						Log.v("distComp", "" + complimentaryDistance);
						customerRideDataGlobal.setDistance(customerRideDataGlobal.getDistance(HomeActivity.this)+complimentaryDistance);
						flagDistanceTravelled = FlagRideStatus.END_RIDE_ADDED_COMP_DIST.getOrdinal();

					}
					Log.e("calculateFusedLocationDistance totalDistance ", "=" + customerRideDataGlobal.getDistance(HomeActivity.this));

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							DialogPopup.dismissLoadingDialog();
							driverEndRideAsync(activity, source, destination.latitude, destination.longitude,
									flagDistanceTravelled, customerInfo);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}).start();
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

				for(Marker marker : requestMarkers){
					marker.remove();
				}
				requestMarkers.clear();

				drawHeatMapData(heatMapResponseGlobal);
				ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForStatus(
						EngagementStatus.REQUESTED.getOrdinal());

				if (customerInfos.size() > 0) {

					for (int i = 0; i < customerInfos.size(); i++) {
						requestMarkers.add(addCustomerPickupMarker(map, customerInfos.get(i), customerInfos.get(i).requestlLatLng));
					}

					Collections.sort(customerInfos, new Comparator<CustomerInfo>() {

						@Override
						public int compare(CustomerInfo lhs, CustomerInfo rhs) {
							if (myLocation != null) {
								double distanceLhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), lhs.requestlLatLng);
								double distanceRhs = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), rhs.requestlLatLng);
								return (int) (distanceLhs - distanceRhs);
							}
							return 0;
						}
					});

				}
				driverRequestListAdapter.setResults(customerInfos);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onNewRideRequest(int perfectRide, int isPooled) {
		if (driverScreenMode == DriverScreenMode.D_INITIAL
				|| driverScreenMode == DriverScreenMode.D_RIDE_END
				|| perfectRide == 1
				|| isPooled == 1) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
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
	public void fetchHeatMapDataCall(Context context) {
		fetchHeatMapData(this);
	}

	@Override
	public void onCancelRideRequest(final String engagementId, final boolean acceptedByOtherDriver) {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showAllRideRequestsOnMap();
					if (driverScreenMode == DriverScreenMode.D_ARRIVED
							|| driverScreenMode == DriverScreenMode.D_START_RIDE) {
						if (engagementId.equalsIgnoreCase(Data.getCurrentEngagementId())) {
							driverScreenMode = DriverScreenMode.D_INITIAL;
							switchDriverScreen(driverScreenMode);
							DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.user_cancel_request));
						}
					} else if(driverScreenMode == DriverScreenMode.D_IN_RIDE){
						removePRMarkerAndRefreshList();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onRideRequestTimeout(final String engagementId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showAllRideRequestsOnMap();
				if (driverScreenMode == DriverScreenMode.D_IN_RIDE) {
					removePRMarkerAndRefreshList();
				}
			}
		});
	}

	private void removePRMarkerAndRefreshList(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
							EngagementStatus.REQUESTED.getOrdinal()));
					if (perfectRideMarker != null) {
						perfectRideMarker.remove();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	public void onManualDispatchPushReceived() {
		try {
			if (userMode == UserMode.DRIVER) {
				callStateRestoreAPIOnManualPatchPushReceived();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	public void onChangeStatePushReceived(final int flag, final String engagementId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
						try {
							new ApiSendCallLogs().sendCallLogs(HomeActivity.this, Data.userData.accessToken,
									engagementId, Data.getCustomerInfo(engagementId).getPhoneNumber());
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							Intent intent = new Intent(HomeActivity.this, RideCancellationActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							intent.putExtra(KEY_KILL_APP, 1);
							startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					callAndHandleStateRestoreAPI();
					if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
						perfectRidePassengerInfoRl.setVisibility(View.GONE);
						driverPassengerInfoRl.setVisibility(View.VISIBLE);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				removePRMarkerAndRefreshList();
			}
		});
	}


	public static void logoutUser(final Activity cont) {
		try {

			try {
				SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			} catch (Exception e) {
				Log.v("e", e.toString());
			}

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
			if (highAccuracyLF == null) {
				highAccuracyLF = new LocationFetcher(HomeActivity.this, LOCATION_UPDATE_TIME_PERIOD, 2);
			}
			highAccuracyLF.connect();
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
				customerSwitcher.updateDistanceOnLocationChanged();
			} else {
				reconnectLocationFetchers();
			}
		}
		if (location.getExtras() == null
				|| !location.getExtras().getBoolean("cached", false)) {
			if (driverScreenMode == DriverScreenMode.D_IN_RIDE
					&& Data.getCurrentCustomerInfo().dropLatLng != null
					&& !Prefs.with(HomeActivity.this).getBoolean(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, false)) {

				double currentDropDist = MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
						Data.getCurrentCustomerInfo().dropLatLng);

				if (currentDropDist < Double.parseDouble(Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_DISTANCE, "1000"))) {
					if(Data.getAssignedCustomerInfosListForEngagedStatus() != null
							&& Data.getAssignedCustomerInfosListForEngagedStatus().size() == 1) {
						perfectRideRequestRegion(currentDropDist);
					}
				}
			}
		}
	}


	public void zoomToCurrentLocationAtFirstLocationFix(Location location) {
		try {
			if (map != null) {
				if (!zoomedToMyLocation) {
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())), MAP_ANIMATION_TIME, null);
					zoomedToMyLocation = true;
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
							DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
						}
					});
					int currentUserStatus = 0;
					if (UserMode.DRIVER == userMode) {
						currentUserStatus = 1;
					}
					if (currentUserStatus != 0) {
						String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken);
						Log.i("currentUserStatus0", resp);
						if (resp.contains(Constants.SERVER_TIMEOUT)) {
							String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken);
							Log.i("currentUserStatus1", resp);
							if (resp1.contains(Constants.SERVER_TIMEOUT)) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										DialogPopup.alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);

										if (!Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").contains(" ")) {
											Data.setCurrentEngagementId(Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ENGAGEMENT_ID, " "));
											acceptRideSucess(Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " "),
													Data.getCurrentEngagementId(),
													Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_CUSTOMER_ID, " "));
										}
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
								DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
							}
						});
						int currentUserStatus = 0;
						if (UserMode.DRIVER == userMode) {
							currentUserStatus = 1;
						}
						if (currentUserStatus != 0) {
							manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken);

							if (manualPatchPushStateRestoreResponse.contains(Constants.SERVER_TIMEOUT)) {
								manualPatchPushStateRestoreResponse = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken);

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
				if (Data.getCurrentCustomerInfo() != null) {
					String manualPatchPushReceived = Database2.getInstance(HomeActivity.this).getDriverManualPatchPushReceived();
					if (Database2.YES.equalsIgnoreCase(manualPatchPushReceived)) {
						DialogPopup.alertPopupWithListener(HomeActivity.this, "", getResources().getString(R.string.customer_pickup_text), new View.OnClickListener() {
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

			params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(KEY_ENGAGEMENT_ID, Data.getCurrentEngagementId());
			params.put(KEY_CUSTOMER_ID, String.valueOf(Data.getCurrentCustomerInfo().getUserId()));

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
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							manualPatchPushAckAPI(activity);
						}
					}, 10000);
				}
			});
		}

	}




	@Override
	public void onCashAddedToWalletByCustomer(final int engagementId, final int userId, final double balance) {
		try {
			if (Data.getCustomerInfo(String.valueOf(engagementId)) != null) {
				Data.getCustomerInfo(String.valueOf(engagementId)).setJugnooBalance(balance);
			} else {
				for(CustomerInfo customerInfo : Data.getAssignedCustomerInfosListForEngagedStatus()){
					if(customerInfo.getUserId() == userId){
						customerInfo.setJugnooBalance(balance);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onDropLocationUpdated(final String engagementId, final LatLng dropLatLng) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateDropLatLngandPath(engagementId, dropLatLng);
			}
		});
	}


	private void updateDropLatLngandPath(String engagementId, LatLng dropLatLng) {
		try {
			Data.getCustomerInfo(engagementId).setDropLatLng(dropLatLng);
			customerSwitcher.setCustomerData(Integer.parseInt(engagementId));
			setAttachedCustomerMarkers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void updateMeteringUI(final double distance, final long elapsedTime, final long waitTime,
								 final Location lastGPSLocation, final Location lastFusedLocation, final double totalHaversineDistance) {
		if (UserMode.DRIVER == userMode
				&& (DriverScreenMode.D_IN_RIDE == driverScreenMode
				|| DriverScreenMode.D_ARRIVED == driverScreenMode)) {
			if(distance < DISTANCE_UPPERBOUND) {
				customerRideDataGlobal.setDistance(distance);
			} else{
				customerRideDataGlobal.setDistance(DISTANCE_UPPERBOUND);
			}
			customerRideDataGlobal.setHaversineDistance(totalHaversineDistance);
			customerRideDataGlobal.setWaitTime(waitTime);
			HomeActivity.this.lastGPSLocation = lastGPSLocation;
			HomeActivity.this.lastFusedLocation = lastFusedLocation;
			HomeActivity.this.distanceUpdateFromService = true;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (UserMode.DRIVER == userMode
							&& DriverScreenMode.D_IN_RIDE == driverScreenMode) {
						CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
						updateDistanceFareTexts(customerInfo, customerInfo.getTotalDistance(customerRideDataGlobal
										.getDistance(HomeActivity.this), HomeActivity.this),
								customerInfo.getElapsedRideTime(HomeActivity.this),
								customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(), HomeActivity.this));
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


	@Override
	public void handleCancelRideSuccess(final String engagementId) {
		runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  if (map != null) {
								  map.clear();
							  }
							  stopService(new Intent(HomeActivity.this, DriverLocationUpdateService.class));

							  reduceRideRequest(engagementId, EngagementStatus.ACCEPTED.getOrdinal());
							  reduceRideRequest(engagementId, EngagementStatus.ARRIVED.getOrdinal());
						  }
					  }
		);
	}

	@Override
	public void handleCancelRideFailure(final String message) {
		runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  DialogPopup.alertPopup(HomeActivity.this, "", message);
							  callAndHandleStateRestoreAPI();
						  }
					  }
		);
	}


	@Override
	public void markArrivedInterrupt(final LatLng latLng, final int engagementId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				GCMIntentService.clearNotifications(activity);
				driverMarkArriveRideAsync(activity, latLng, Data.getCustomerInfo(String.valueOf(engagementId)));
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
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


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
					timeOutValue.setText("" + String.format("%02d:%02d:%02d",
							TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
							TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
									TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
							TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
									TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
				}

				public void onFinish() {
					try {
						timeOutValue.setText("done!");
						Data.userData.autosAvailable = 0;
						changeJugnooONUIAndInitService();
						dialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			dialog.show();
			GCMIntentService.stopRing(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	Handler heatMapHandler = new Handler();

	Runnable heatMapRunnalble = new Runnable() {
		@Override
		public void run() {
			try {
				fetchHeatMapData(HomeActivity.this);
			} catch (Exception e) {

			}
			heatMapHandler.postDelayed(heatMapRunnalble, Prefs.with(HomeActivity.this).getLong(SPLabels.HEAT_MAP_REFRESH_FREQUENCY, 0));
		}
	};


	public void perfectRideStateRestore() {

		if (!Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
			boolean endNotDone = false;
			ArrayList<PendingAPICall> pendingAPICalls = Database2.getInstance(HomeActivity.this).getAllPendingAPICalls();
			for (PendingAPICall pendingAPICall : pendingAPICalls) {
				Log.e(TAG, "pendingApiCall=" + pendingAPICall);
				if (PendingCall.END_RIDE.getPath().equalsIgnoreCase(pendingAPICall.url)) {
					endNotDone = true;
					Log.i("pendingCallStatus", String.valueOf(endNotDone));
					break;
				}
			}

			if (endNotDone) {
				Data.setCurrentEngagementId(Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ENGAGEMENT_ID, " "));
				acceptRideSucess(Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " "),
						Data.getCurrentEngagementId(),
						Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_CUSTOMER_ID, " "));
			} else {
				callAndHandleStateRestoreAPI();
			}
		}
	}


	public void etaTimer(long eta) {
		imageViewETASmily.setImageResource(R.drawable.superhappy_face);
		Prefs.with(HomeActivity.this).save(SPLabels.ON_FINISH_CALLED, 0);
		timer = new CountDownTimer(eta, 1000) {
			public void onTick(long millisUntilFinished) {
				etaTimerText.setText("" + String.format("%02d:%02d",
						TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
								TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
						TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
								TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
			}

			public void onFinish() {
				etaTimerText.setText("00:00");
				Prefs.with(HomeActivity.this).save(SPLabels.ETA_EXPIRE, System.currentTimeMillis());
				Prefs.with(HomeActivity.this).save(SPLabels.ON_FINISH_CALLED, 1);
				imageViewETASmily.setImageResource(R.drawable.happy_face);
				changeSmilyTask();

			}
		}.start();
	}

	Handler smilyHandler = new Handler();
	Runnable smilyRunnalble = new Runnable() {
		@Override
		public void run() {
			try {
				if ((System.currentTimeMillis() - Prefs.with(HomeActivity.this).getLong(SPLabels.ETA_EXPIRE, 0)) > 180000) {
					imageViewETASmily.setImageResource(R.drawable.supersad_face);
					smilyHandler.removeCallbacks(smilyRunnalble);
				} else if ((System.currentTimeMillis() - Prefs.with(HomeActivity.this).getLong(SPLabels.ETA_EXPIRE, 0)) > 120000) {
					imageViewETASmily.setImageResource(R.drawable.sad_face);
				} else if ((System.currentTimeMillis() - Prefs.with(HomeActivity.this).getLong(SPLabels.ETA_EXPIRE, 0)) > 60000) {
					imageViewETASmily.setImageResource(R.drawable.netural_face);
				}
			} catch (Exception e) {

			}
			smilyHandler.postDelayed(smilyRunnalble, 30000);
		}
	};

	public void changeSmilyTask() {
		smilyHandler.removeCallbacks(smilyRunnalble);
		smilyHandler.postDelayed(smilyRunnalble, 1000);
	}

	public void perfectRideRequestRegion(double currentDropDist) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_DISTANCE, String.valueOf(currentDropDist));

				Log.i("params", "=" + params);

				RestClient.getApiServices().perfectRideRegionRequest(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i(TAG, "rateTheCustomer response = " + responseStr);
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt(KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, true);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						perfectRideStateRestore();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CallbackEndRideWalletUpdate callbackEndRideWalletUpdate;
	private CallbackEndRideWalletUpdate getCallbackUpdateWalletBalance(CustomerInfo customerInfo){
		if(callbackEndRideWalletUpdate == null){
			callbackEndRideWalletUpdate = new CallbackEndRideWalletUpdate(customerInfo);
		}
		callbackEndRideWalletUpdate.setCustomerInfo(customerInfo);
		return callbackEndRideWalletUpdate;
	}
	private class CallbackEndRideWalletUpdate implements Callback<RegisterScreenResponse>{

		private CustomerInfo customerInfo;
		public CallbackEndRideWalletUpdate(CustomerInfo customerInfo){
			this.customerInfo = customerInfo;
		}

		public void setCustomerInfo(CustomerInfo customerInfo) {
			this.customerInfo = customerInfo;
		}

		@Override
		public void success(RegisterScreenResponse registerScreenResponse, Response response) {
			String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
			Log.i(TAG, "callbackUpdateWalletBalance response = " + responseStr);
			try {
				if(Data.userData.walletUpdateTimeout > (System.currentTimeMillis()-walletUpdateCallTime)) {
					JSONObject jObj = new JSONObject(responseStr);
					int flag = jObj.getInt(KEY_FLAG);
					if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
						stopWalletUpdateTimeout();
						if (customerInfo != null && walletBalanceUpdatePopup) {
							if (jObj.getString(KEY_WALLET_BALANCE) != null) {
								double newBalance = Double.parseDouble(jObj.getString(KEY_WALLET_BALANCE));
								if (newBalance > -1) {
									customerInfo.setJugnooBalance(newBalance);
								}
							}
							endRidePopup(HomeActivity.this, customerInfo);
							FlurryEventLogger.event(RIDE_ENDED);
						}
						DialogPopup.dismissLoadingDialog();
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		@Override
		public void failure(RetrofitError error) {
			if (customerInfo != null && walletBalanceUpdatePopup) {
				stopWalletUpdateTimeout();
				endRidePopup(HomeActivity.this, customerInfo);
				DialogPopup.dismissLoadingDialog();
				FlurryEventLogger.event(RIDE_ENDED);
			}
		}
	}

	long walletUpdateCallTime;
	public void updateWalletBalance(CustomerInfo customerInfo) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				Log.i("params", "=" + params);

				walletUpdateCallTime = System.currentTimeMillis();
				DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
				RestClient.getApiServices().updateWalletBalance(params, getCallbackUpdateWalletBalance(customerInfo));
				walletBalanceUpdatePopup = true;
				startWalletUpdateTimeout(customerInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	private synchronized void startWalletUpdateTimeout(final CustomerInfo customerInfo){
		checkwalletUpdateTimeoutHandler = new Handler();
		checkwalletUpdateTimeoutRunnable = new Runnable() {
			@Override
			public void run() {
				getCallbackUpdateWalletBalance(customerInfo).failure(null);
			}
		};
		checkwalletUpdateTimeoutHandler.postDelayed(checkwalletUpdateTimeoutRunnable, Data.userData.walletUpdateTimeout);
	}

	public synchronized void stopWalletUpdateTimeout(){
		try{
			if(checkwalletUpdateTimeoutHandler != null && checkwalletUpdateTimeoutRunnable != null){
				checkwalletUpdateTimeoutHandler.removeCallbacks(checkwalletUpdateTimeoutRunnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			checkwalletUpdateTimeoutHandler = null;
			checkwalletUpdateTimeoutRunnable = null;
		}
	}


	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setUserData();
			}
		});
	}


	@Override
	public void updateCustomers() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				customerSwitcher.updateList();
			}
		});
	}

	private void setTextViewRideInstructions(){
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if(customerInfo.getIsDelivery() == 1) {
				textViewRideInstructions.setVisibility(View.GONE);
				textViewRideInstructionsInRide.setVisibility(View.GONE);
				if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
					textViewRideInstructions.setVisibility(View.VISIBLE);
					textViewRideInstructions.setText(getResources().getString(R.string.arrive_at_pickup_location));
				}
				else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
					textViewRideInstructions.setVisibility(View.VISIBLE);
					textViewRideInstructions.setText(getResources().getString(R.string.start_the_delivery));
				}
				else if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
					textViewRideInstructionsInRide.setVisibility(View.VISIBLE);
					for(int i=0; i<customerInfo.getDeliveryInfos().size(); i++){
						if(customerInfo.getDeliveryInfos().get(i).getStatus()
								== DeliveryStatus.PENDING.getOrdinal()){
							textViewRideInstructionsInRide.setText(getResources().getString(R.string.deliver_order_number,
									String.valueOf(i+1)));
							return;
						}
					}
					textViewRideInstructionsInRide.setText(getResources().getString(R.string.all_orders_have_been_delivered));
				}
			} else if(customerInfo.getIsPooled() != 1){
				textViewRideInstructions.setVisibility(View.GONE);
				textViewRideInstructionsInRide.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private boolean anyDeliveriesUnchecked(CustomerInfo customerInfo){
		boolean anyUnchecked = false;
		try {
			for(int i=0; i<customerInfo.getDeliveryInfos().size(); i++){
				if(customerInfo.getDeliveryInfos().get(i).getStatus() == DeliveryStatus.PENDING.getOrdinal()){
					anyUnchecked = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return anyUnchecked;
	}

	public void setMakeDeliveryButtonVisibility(){
		buttonMakeDelivery.setVisibility(View.GONE);
		try{
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if(customerInfo.getIsDelivery() == 1){
				boolean anyUnchecked = anyDeliveriesUnchecked(customerInfo);
				buttonMakeDelivery.setVisibility(View.VISIBLE);
				setTextViewRideInstructions();
				setMakeDeliveryButtonState(anyUnchecked);
				setEndRideButtonState(anyUnchecked);
			} else {
				throw new Exception();
			}
		} catch (Exception e){
			setEndRideButtonState(true);
		}
	}

	private void setEndRideButtonState(boolean isDefault){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) driverEndRideBtn.getLayoutParams();
		if(isDefault) {
			params.width = (int) (getResources().getDimension(R.dimen.button_width_big) * ASSL.Xscale());
			params.leftMargin = 0;
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			driverEndRideBtn.setLayoutParams(params);
			driverEndRideBtn.setBackgroundResource(R.drawable.menu_black_btn_selector);
		} else{
			params.width = (int) (getResources().getDimension(R.dimen.button_width_big_extra) * ASSL.Xscale());
			params.leftMargin = (int) (30f * ASSL.Xscale());
			params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
			driverEndRideBtn.setLayoutParams(params);
			driverEndRideBtn.setBackgroundResource(R.drawable.orange_btn_selector);
		}
	}

	private void setMakeDeliveryButtonState(boolean isDefault){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) buttonMakeDelivery.getLayoutParams();
		if(isDefault) {
			params.width = (int) (getResources().getDimension(R.dimen.button_width_big) * ASSL.Xscale());
			params.rightMargin = 0;
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			buttonMakeDelivery.setLayoutParams(params);
			buttonMakeDelivery.setText(getResources().getString(R.string.make_delivery));
			buttonMakeDelivery.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36f * ASSL.Xscale());
		} else{
			params.width = (int) (getResources().getDimension(R.dimen.button_width_small) * ASSL.Xscale());
			params.rightMargin = (int) (30f * ASSL.Xscale());
			params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			buttonMakeDelivery.setLayoutParams(params);
			buttonMakeDelivery.setText(getResources().getString(R.string.view_orders));
		}
	}

	public void setDeliveryMarkers(){
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if(customerInfo.getIsDelivery() == 1
					&& customerInfo.getDeliveryInfos() != null
					&& customerInfo.getDeliveryInfos().size() > 0){
				final String engagementId = String.valueOf(customerInfo.getEngagementId());
				ArrayList<LatLng> latLngs = new ArrayList<>();
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				HashMap<LatLng,Integer> counterMap = new HashMap<>();

				try {
					LatLng driverLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
					builder.include(driverLatLng);
					latLngs.add(driverLatLng);
				} catch (Exception e) {}

				latLngs.add(customerInfo.getRequestlLatLng());
				builder.include(customerInfo.getRequestlLatLng());

				for(int i=0; i<customerInfo.getDeliveryInfos().size(); i++){
					DeliveryInfo deliveryInfo = customerInfo.getDeliveryInfos().get(i);
					LatLng latLng = deliveryInfo.getLatLng();
					if(Utils.compareDouble(latLng.latitude, 0) != 0
							&& Utils.compareDouble(latLng.longitude, 0) != 0) {
						if (counterMap.containsKey(latLng)) {
							counterMap.put(latLng, counterMap.get(latLng) + 1);
						} else {
							counterMap.put(latLng, 1);
						}

						if (!latLngs.contains(latLng)) {
							latLngs.add(latLng);
							builder.include(latLng);
						} else {
							latLng = new LatLng(latLng.latitude, latLng.longitude + 0.0004d * (double) (counterMap.get(latLng)));
						}
						addDropPinMarker(map, latLng, String.valueOf(deliveryInfo.getIndex() + 1));
					}

				}

				map.animateCamera(CameraUpdateFactory.newLatLngBounds(MyApplication.getInstance()
								.getMapLatLngBoundsCreator().createBoundsWithMinDiagonal(builder, FIX_ZOOM_DIAGONAL),
						(int) (630f * ASSL.Xscale()), (int) (630f * ASSL.Xscale()),
						(int) (50f * ASSL.Xscale())), MAP_ANIMATION_TIME, null);

				if(latLngs.size() > 1) {
					new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.new_orange_path), map,
							new ApiGoogleDirectionWaypoints.Callback() {
								@Override
								public void onPre() {

								}

								@Override
								public boolean showPath() {
									return engagementId == Data.getCurrentEngagementId()
											&& driverScreenMode == DriverScreenMode.D_IN_RIDE;
								}

								@Override
								public void polylineAdded(Polyline polyline) {

								}
							}).execute();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private void addDropPinMarker(GoogleMap map, LatLng latLng, String text){
		final MarkerOptions markerOptions = new MarkerOptions()
				.position(latLng)
				.title("")
				.snippet("")
				.anchor(0.5f, 0.9f)
				.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.getTextBitmap(this, assl, text, 20)));
		map.addMarker(markerOptions);
	}

	public RelativeLayout getRelativeLayoutContainer(){
		return relativeLayoutContainer;
	}

	private TransactionUtils transactionUtils;
	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}


	public Location getMyLocation(){
		return myLocation;
	}



	public double getCurrentDeliveryDistance(CustomerInfo customerInfo){
		double distance = customerRideDataGlobal.getDistance(HomeActivity.this);
		for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
			if(deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
				distance = distance - deliveryInfo.getDistance();
			}
		}
		return distance;
	}

	public long getCurrentDeliveryWaitTime(CustomerInfo customerInfo){
		long waitTime = customerRideDataGlobal.getWaitTime();
		for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
			if(deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
				waitTime = waitTime - deliveryInfo.getWaitTime();
			}
		}
		return waitTime;
	}

	public long getCurrentDeliveryTime(CustomerInfo customerInfo){
		long deliveryTime = System.currentTimeMillis() - customerRideDataGlobal.getStartRideTime();
		for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
			if(deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
				deliveryTime = deliveryTime - deliveryInfo.getDeliveryTime();
			}
		}
		return deliveryTime;
	}


	private void setAttachedCustomerMarkers(){
		try {
			map.clear();
			ArrayList<LatLng> latLngs = new ArrayList<>();
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			HashMap<LatLng,Integer> counterMap = new HashMap<>();
			LatLng driverLatLng = null;
			try {
				if(myLocation != null){
					driverLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				} else if(Utils.compareDouble(Data.latitude, 0) != 0 && Utils.compareDouble(Data.longitude, 0) != 0){
					driverLatLng = new LatLng(Data.latitude, Data.longitude);
				}
				if(driverLatLng != null) {
					builder.include(driverLatLng);
					latLngs.add(driverLatLng);
				}
			} catch (Exception e) {}

			ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();
			final LatLng driverLatLngFinal = driverLatLng;
			Collections.sort(customerInfos, new Comparator<CustomerInfo>() {

				@Override
				public int compare(CustomerInfo lhs, CustomerInfo rhs) {
					try {
						LatLng lhsLatLng = null;
						if(lhs.getStatus() == EngagementStatus.STARTED.getOrdinal()
								&& lhs.getIsDelivery() != 1
								&& lhs.getDropLatLng() != null){
							lhsLatLng = lhs.getDropLatLng();
						} else if(lhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| lhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
							lhsLatLng = lhs.getRequestlLatLng();
						}
						LatLng rhsLatLng = null;
						if(rhs.getStatus() == EngagementStatus.STARTED.getOrdinal()
								&& rhs.getIsDelivery() != 1
								&& rhs.getDropLatLng() != null){
							rhsLatLng = rhs.getDropLatLng();
						} else if(rhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| rhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
							rhsLatLng = rhs.getRequestlLatLng();
						}

						if (driverLatLngFinal != null && lhsLatLng != null && rhsLatLng != null) {
							double distanceLhs = MapUtils.distance(driverLatLngFinal, lhsLatLng);
							double distanceRhs = MapUtils.distance(driverLatLngFinal, rhsLatLng);
							return (int) (distanceLhs - distanceRhs);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return 0;
				}
			});


			textViewRideInstructions.setVisibility(View.GONE);
			textViewRideInstructionsInRide.setVisibility(View.GONE);
			for(int i=0; i<customerInfos.size(); i++){
				CustomerInfo customerInfo = customerInfos.get(i);
				LatLng latLng = null;
				if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()
						&& customerInfo.getIsDelivery() != 1
						&& customerInfo.getDropLatLng() != null){
					latLng = customerInfo.getDropLatLng();
				} else if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
						|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
					latLng = customerInfo.getRequestlLatLng();
				}

				if(i == 0 && customerInfo.getIsPooled() == 1){
					String text = "";
					if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
						text = getResources().getString(R.string.please_drop_customer,
								customerInfo.getName());
					} else if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()){
						text = getResources().getString(R.string.please_reach_customer_location,
								customerInfo.getName());
					} else if(customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
						text = getResources().getString(R.string.please_start_customer_ride,
								customerInfo.getName());
					}

					if(Data.getCurrentState() == DriverScreenMode.D_IN_RIDE){
						textViewRideInstructionsInRide.setVisibility(View.VISIBLE);
						textViewRideInstructionsInRide.setText(text);
					} else {
						textViewRideInstructions.setVisibility(View.VISIBLE);
						textViewRideInstructions.setText(text);
					}
				}


				if(latLng != null
						&& Utils.compareDouble(latLng.latitude, 0) != 0
						&& Utils.compareDouble(latLng.longitude, 0) != 0) {
					if (counterMap.containsKey(latLng)) {
						counterMap.put(latLng, counterMap.get(latLng) + 1);
					} else {
						counterMap.put(latLng, 1);
					}
					if (!latLngs.contains(latLng)) {
						latLngs.add(latLng);
						builder.include(latLng);
					} else {
						latLng = new LatLng(latLng.latitude, latLng.longitude + 0.0004d * (double) (counterMap.get(latLng)));
					}
					if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()
							&& customerInfo.getIsDelivery() != 1
							&& customerInfo.getDropLatLng() != null){
						addDropPinMarker(map, latLng, customerInfos.size() > 1 ? String.valueOf(i + 1) : "");
					} else if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
							|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
						addCustomerPickupMarker(map, customerInfo, latLng);
					}
				}
			}

			map.animateCamera(CameraUpdateFactory.newLatLngBounds(MyApplication.getInstance()
							.getMapLatLngBoundsCreator().createBoundsWithMinDiagonal(builder, FIX_ZOOM_DIAGONAL),
					(int) (630f * ASSL.Xscale()), (int) (630f * ASSL.Xscale()),
					(int) (50f * ASSL.Xscale())), MAP_ANIMATION_TIME, null);

			if(latLngs.size() > 1) {
				new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.new_orange_path), map,
						new ApiGoogleDirectionWaypoints.Callback() {
							@Override
							public void onPre() {

							}

							@Override
							public boolean showPath() {
								return Data.getAssignedCustomerInfosListForEngagedStatus().size() > 0;
							}

							@Override
							public void polylineAdded(Polyline polyline) {

							}
						}).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addStartMarker(){
		try {
			double latitude = Double.parseDouble(Prefs.with(this).getString(Constants.SP_START_LATITUDE, "0"));
			double longitude = Double.parseDouble(Prefs.with(this).getString(Constants.SP_START_LONGITUDE, "0"));
			if(Utils.compareDouble(latitude, 0) != 0 && Utils.compareDouble(longitude, 0) != 0){
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.snippet("");
				markerOptions.title(getResources().getString(R.string.start_ride_loc));
				markerOptions.position(new LatLng(latitude, longitude));
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
				map.addMarker(markerOptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}