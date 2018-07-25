package product.clicklabs.jugnoo.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.fugu.HippoConfig;
import com.fugu.HippoNotificationConfig;
import com.fugu.HippoTicketAttributes;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.json.JSONArray;
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

import product.clicklabs.jugnoo.driver.adapters.InfoTilesAdapter;
import product.clicklabs.jugnoo.driver.adapters.InfoTilesAdapterHandler;
import product.clicklabs.jugnoo.driver.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.driver.apis.ApiAcceptRide;
import product.clicklabs.jugnoo.driver.apis.ApiFetchDriverApps;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleDirectionWaypoints;
import product.clicklabs.jugnoo.driver.apis.ApiRejectRequest;
import product.clicklabs.jugnoo.driver.apis.ApiSendCallLogs;
import product.clicklabs.jugnoo.driver.apis.ApiSendRingCountData;
import product.clicklabs.jugnoo.driver.chat.ChatActivity;
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
import product.clicklabs.jugnoo.driver.datastructure.RingData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.dodo.MyViewPager;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.dodo.datastructure.EndDeliveryStatus;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfoTabs;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfosListInRideFragment;
import product.clicklabs.jugnoo.driver.fragments.AddSignatureFragment;
import product.clicklabs.jugnoo.driver.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.driver.home.BlockedAppsUninstallIntent;
import product.clicklabs.jugnoo.driver.home.CustomerSwitcher;
import product.clicklabs.jugnoo.driver.home.EngagementSP;
import product.clicklabs.jugnoo.driver.home.EnterTollDialog;
import product.clicklabs.jugnoo.driver.home.StartRideLocationUpdateService;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.selfAudit.SelfAuditActivity;
import product.clicklabs.jugnoo.driver.services.FetchDataUsageService;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeCardsActivity;
import product.clicklabs.jugnoo.driver.support.SupportMailActivity;
import product.clicklabs.jugnoo.driver.support.SupportOptionsActivity;
import product.clicklabs.jugnoo.driver.tutorial.AcceptResponse;
import product.clicklabs.jugnoo.driver.tutorial.Crouton;
import product.clicklabs.jugnoo.driver.tutorial.GenrateTourPush;
import product.clicklabs.jugnoo.driver.tutorial.TourResponseModel;
import product.clicklabs.jugnoo.driver.tutorial.UpdateTourStatusModel;
import product.clicklabs.jugnoo.driver.tutorial.UpdateTutStatusService;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.ui.LogoutCallback;
import product.clicklabs.jugnoo.driver.ui.ManualRideActivity;
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
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.GoogleRestApis;
import product.clicklabs.jugnoo.driver.utils.KeyboardLayoutListener;
import product.clicklabs.jugnoo.driver.utils.LocationInit;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.PausableChronometer;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import product.clicklabs.jugnoo.driver.widgets.PrefixedEditText;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.driver.Data.context;
import static product.clicklabs.jugnoo.driver.Data.getCurrentCustomerInfo;
import com.google.firebase.iid.FirebaseInstanceId;

@SuppressLint("DefaultLocale")
public class HomeActivity extends BaseFragmentActivity implements AppInterruptHandler, LocationUpdate, GPSLocationUpdate,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
		FlurryEventNames, SearchListAdapter.SearchListActionsHandler, OnMapReadyCallback, Constants, DisplayPushHandler, FirebaseEvents,
		ViewPager.OnPageChangeListener, View.OnClickListener {


	private final String TAG = HomeActivity.class.getSimpleName();

	DrawerLayout drawerLayout;                                                                        // views declaration


	//menu bar
	LinearLayout menuLayout;



	ImageView profileImg, seprator;
	TextView userName, ratingValue, textViewAutosOn,tvCredits;
	LinearLayout linearLayoutDEI, linearLayout_DEI;
	RelativeLayout driverImageRL;
	RelativeLayout relativeLayoutAutosOn, relativeLayoutSharingOn, relativeLayoutDeliveryOn;
	ImageView imageViewAutosOnToggle, imageViewSharingOnToggle, imageViewDeliveryOnToggle;

	RelativeLayout inviteFriendRl, driverRatingRl, notificationCenterRl,driverCreditsRl,manaulRequestRl,walletRl;
	TextView inviteFriendText, notificationCenterText;

	RelativeLayout bookingsRl, RelativeLayoutNotificationCenter, etaTimerRLayout;
	TextView bookingsText, etaTimerText;

	RelativeLayout relativeLayoutSharingRides;

	RelativeLayout fareDetailsRl;
	TextView fareDetailsText, textViewDestination;
	RelativeLayout relativeLayoutSuperDrivers, relativeLayoutDestination;

	RelativeLayout callUsRl, termsConditionRl, relativeLayoutRateCard, auditRL, earningsRL, homeRl,
			relativeLayoutSupport, relativeLayoutChatSupport,relativeLayoutPlans, rlSupportMain,
			rlSupportTicket, rlMailSupport;
	TextView callUsText, tvGetSupport, termsConditionText, textViewRateCard, auditText, earningsText, homeText;
	LinearLayout rlGetSupport;

	RelativeLayout paytmRechargeRl, paymentsRl;
	TextView paytmRechargeText, paymentsText;

	RelativeLayout languagePrefrencesRl;
	TextView languagePrefrencesText;

	RelativeLayout logoutRl, relativeLayoutItemHeader;
	TextView logoutText;
	HeatMapResponse heatMapResponseGlobal;

	SlidingUpPanelLayout slidingUpPanelLayout;


	//Top RL
	RelativeLayout topRl;
	Button menuBtn;
	Button checkServerBtn;
	ImageView imageViewTitleBarDEI, imageViewSliderView;
	TextView textViewTitleBarDEI;


	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;

	//slider menu

	RecyclerView recyclerViewInfo;
	InfoTilesAdapter infoTilesAdapter;
	LinearLayoutManager linearLayoutManagerScrollControl;


	//Driver initial layout
	RelativeLayout driverInitialLayout;
	ListView driverRideRequestsList;
	Button driverInitialMyLocationBtn, driverInformationBtn, buttonUploadOnInitial;
	TextView jugnooOffText, temptext, textViewDocText, textViewDocDayText;

	DriverRequestListAdapter driverRequestListAdapter;


	// Driver Request Accept layout
	RelativeLayout driverRequestAcceptLayout;
	Button driverRequestAcceptBackBtn, driverAcceptRideBtn, driverCancelRequestBtn, driverRequestAcceptMyLocationBtn, buttonDriverNavigation;


	// Driver Engaged layout
	RelativeLayout driverEngagedLayout;

	RelativeLayout perfectRidePassengerCallRl;
	LinearLayout perfectRidePassengerInfoRl, driverPassengerInfoRl, linearLayoutJugnooOff;
	TextView driverPassengerCallText, driverPerfectRidePassengerName, textViewRideInstructions;
	Button driverEngagedMyLocationBtn;
//	Button distanceReset2;

	//Start ride layout
	RelativeLayout driverStartRideMainRl;
	public Button driverStartRideBtn, buttonMarkArrived;
	public Button driverCancelRideBtn;
	public TextView tvDropAddressToggleView;
	public Button bDropAddressToggle;


	//In ride layout
	LinearLayout driverInRideMainRl, linearLayoutRideValues;
	TextView driverIRDistanceText, driverIRDistanceValue, driverIRFareText, driverIRFareValue,
			driverRideTimeText, driverWaitText, driverWaitValue;
	PausableChronometer rideTimeChronometer;
	RelativeLayout driverWaitRl, driverIRFareRl;
	ImageView imageViewETASmily;
	Button driverEndRideBtn, buttonMakeDelivery, changeButton;


	//Review layout
	RelativeLayout endRideReviewRl;

	LinearLayout reviewReachedDistanceRl;
	LinearLayout linearLayoutMeterFare;
	TextView textViewRateYourCustomer,
			reviewDistanceText, reviewDistanceValue, textViewSuperDrivers,
			reviewWaitText, reviewWaitValue, reviewRideTimeText, reviewRideTimeValue,
			reviewFareText, reviewFareValue;
	RelativeLayout reviewWaitTimeRl;

	LinearLayout linearLayoutMeterFareEditText;
	TextView textViewMeterFareRupee;
	EditText editTextEnterMeterFare;

	RelativeLayout relativeLayoutEndRideLuggageCount;
	ImageView imageViewEndRideLuggageCountPlus, imageViewEndRideLuggageCountMinus;
	TextView textViewEndRideLuggageCount;

	LinearLayout endRideInfoRl;
	TextView jugnooRideOverText, takeFareText, textViewDeliveryOn, tvChatCount;

	private TextView endRideCustomText;
	Button reviewSubmitBtn, btnHelp;
	TextView btnChatHead;
	RelativeLayout relativeLayoutRateCustomer, topRlOuter, rlChatDriver;
	RatingBar ratingBarFeedback, ratingBarFeedbackSide;
	Button reviewSkipBtn;

	ScrollView scrollViewEndRide;
	LinearLayout linearLayoutEndRideMain, linearLayoutRide;
	TextView textViewScroll, textViewNotificationValue, textViewPerfectRideWating;
	MyViewPager deliveryListHorizontal;
	RelativeLayout relativeLayoutDeliveryOver;
	TextView textViewDeliveryIsOver, textViewEndRideCustomerName;
	LinearLayout linearLayoutEndDelivery;

	TextView textViewOrdersDeliveredValue, textViewOrdersReturnedValue;

	RelativeLayout relativeLayoutLastRideEarning, linearLayoutSlidingBottom,
			relativeLayoutRefreshUSLBar, relativeLayoutEnterDestination, relativeLayoutBatteryLow;
	View viewRefreshUSLBar;
	ProgressBar progressBarUSL;
	TextView textViewDriverEarningOnScreen, textViewDriverEarningOnScreenDate, textViewDriverEarningOnScreenValue,
			 textViewRetryUSL, textViewEnterDestination;
	Shader textShader;
	double fixDeliveryDistance = -1;
	double fixedDeliveryWaitTime = -1;
	CustomerSwitcher customerSwitcher;
	DeliveryInfoTabs deliveryInfoTabs;


	// data variables declaration
	public String language = "";
	private Handler checkwalletUpdateTimeoutHandler;
	private Runnable checkwalletUpdateTimeoutRunnable;
	boolean sortCustomerState = true;


	DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));

	private CustomerRideData customerRideDataGlobal = new CustomerRideData();

	long fetchHeatMapTime = 0;
	long fetchAllAppTime = 0;

	double totalFare = 0;
	String waitTime = "", rideTime = "";
	EndRideData endRideData;

	public static MediaPlayer mediaPlayer;
	public static Vibrator vibrator;

	public static Location myLocation;
	public Location lastGPSLocation, lastFusedLocation;
	public boolean distanceUpdateFromService = false;
	public boolean walletBalanceUpdatePopup = false;
	private boolean mapAnimatedToCustomerPath = false;

	static UserMode userMode;
	public static DriverScreenMode driverScreenMode;


	public static AppInterruptHandler appInterruptHandler;

	public static Activity activity;

	boolean loggedOut = false,
			zoomedToMyLocation = false,
			mapTouchedOnce = false;
	boolean dontCallRefreshDriver = false, resumed = false;
	int fareFetchedFromJugnoo = 0;
	int luggageCountAdded = 0;
	int tileCount = 0;
	boolean playStartRideAlarm;
	boolean reCreateDeliveryMarkers =false;

	AlertDialog gpsDialogAlert;

	LocationFetcher highAccuracyLF;


	public static AppMode appMode;

	public static final int MAP_PATH_COLOR = Color.TRANSPARENT;

	public static final long DRIVER_START_RIDE_CHECK_METERS = 400; //in meters

	public static final long LOCATION_UPDATE_TIME_PERIOD = 10000; //in milliseconds

	public static final float HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
	public CountDownTimer timer = null;
	public Marker perfectRideMarker = null;
	public  Marker currentCustomerLocMarker = null;


	public static final long MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT = 10 * 60000; //in milliseconds
	private final int MAP_ANIMATION_TIME = 300;
	private final double DISTANCE_UPPERBOUND = 300000;
	public long refreshPolyLineDelay = 0;

	public ASSL assl;
	private String currentPreferredLang = "";

	private CustomerInfo openedCustomerInfo;
	private boolean rideCancelledByCustomer = false;
	private String cancelationMessage = "";
	private static  final  int REQUEST_CODE_TERMS_ACCEPT = 0x234;


	private CustomerInfo getOpenedCustomerInfo(){
		return openedCustomerInfo;
	}
	private void setOpenedCustomerInfo(CustomerInfo customerInfo){
		openedCustomerInfo = customerInfo;
	}

	private PlaceSearchListFragment placeSearchListFragment;

	public RelativeLayout relativeLayoutContainer;
	private ArrayList<Marker> requestMarkers = new ArrayList<>();
	ArrayList<Tile> infoTileResponses = new ArrayList<>();
	private final double FIX_ZOOM_DIAGONAL = 200;
	private GoogleApiClient mGoogleApiClient;

	DialogPopup endDelivery = new DialogPopup();


//	Driver tutorial views
	private RelativeLayout tourLayout;
	private ImageView tourCrossBtn ;
	private TextView tourTextView;
	private boolean isTourFlag, isTourBtnClicked, isJugnooOnTraining = false;
	private View customView;
	private GenrateTourPush gcmIntentService;
	private RelativeLayout relativeLayoutTour, relativeLayoutDocs;
	private TextView textViewTour, textViewDoc;
	private TextView croutonTourTextView;
	private ImageView crossTourImageView;
	public boolean deliveryInfolistFragVisibility = false;

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

			mGoogleApiClient = new GoogleApiClient
					.Builder(this)
					.addApi(Places.GEO_DATA_API)
					.addApi(Places.PLACE_DETECTION_API)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

			appMode = AppMode.NORMAL;

			language = Locale.getDefault().getLanguage();


			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
			drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


			assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);

			//Swipe menu
			menuLayout = (LinearLayout) findViewById(R.id.menuLayout);


			profileImg = (ImageView) findViewById(R.id.profileImg);
			userName = (TextView) findViewById(R.id.userName);
			tvCredits = (TextView) findViewById(R.id.tvCredits);
			ratingValue = (TextView) findViewById(R.id.ratingValue);
			userName.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			tvCredits.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			ratingValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			linearLayoutDEI = (LinearLayout) findViewById(R.id.linearLayoutDEI);
			linearLayout_DEI = (LinearLayout) findViewById(R.id.linearLayout_DEI);

			relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
			relativeLayoutContainer.setVisibility(View.GONE);

			relativeLayoutAutosOn = (RelativeLayout) findViewById(R.id.relativeLayoutAutosOn);
			textViewAutosOn = (TextView) findViewById(R.id.textViewAutosOn);
			textViewAutosOn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewAutosOn.setText(getString(R.string.jugnoo_on, getString(R.string.appname)));
			imageViewAutosOnToggle = (ImageView) findViewById(R.id.imageViewAutosOnToggle);

			relativeLayoutSharingOn = (RelativeLayout) findViewById(R.id.relativeLayoutSharingOn);
			((TextView) findViewById(R.id.textViewSharingOn)).setTypeface(Fonts.mavenRegular(getApplicationContext()));
			imageViewSharingOnToggle = (ImageView) findViewById(R.id.imageViewSharingOnToggle);

			temptext  = (TextView) findViewById(R.id.temptext);
			temptext.setTypeface(Fonts.mavenMedium(getApplicationContext()));
//			textShader=new LinearGradient(0, 0, 0, 20,
//					new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//					new float[]{0, 1}, Shader.TileMode.CLAMP);
//			temptext.getPaint().setShader(textShader);

			relativeLayoutDeliveryOn = (RelativeLayout) findViewById(R.id.relativeLayoutDeliveryOn);
			textViewDeliveryOn = (TextView) findViewById(R.id.textViewDeliveryOn);
			textViewDeliveryOn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			imageViewDeliveryOnToggle = (ImageView) findViewById(R.id.imageViewDeliveryOnToggle);


			inviteFriendRl = (RelativeLayout) findViewById(R.id.inviteFriendRl);
			driverCreditsRl = (RelativeLayout) findViewById(R.id.driverCreditsRl);
			manaulRequestRl = (RelativeLayout) findViewById(R.id.manaulRequestRl);
			driverRatingRl = (RelativeLayout) findViewById(R.id.driverRatingRl);
			walletRl = (RelativeLayout) findViewById(R.id.walletRl);
			inviteFriendText = (TextView) findViewById(R.id.inviteFriendText);
			inviteFriendText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			inviteFriendText.setText(getStringText(R.string.invite_earn));

			notificationCenterRl = (RelativeLayout) findViewById(R.id.notificationCenterRl);
			notificationCenterText = (TextView) findViewById(R.id.notificationCenterText);
			notificationCenterText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			notificationCenterText.setText(getResources().getString(R.string.Notifications));

			bookingsRl = (RelativeLayout) findViewById(R.id.bookingsRl);
			RelativeLayoutNotificationCenter = (RelativeLayout) findViewById(R.id.RelativeLayoutNotificationCenter);
			bookingsText = (TextView) findViewById(R.id.bookingsText);
			bookingsText.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			etaTimerRLayout = (RelativeLayout) findViewById(R.id.etaTimerRLayout);
			etaTimerText = (TextView) findViewById(R.id.ETATimerText);
			etaTimerText.setTypeface(Fonts.digitalRegular(getApplicationContext()));

			imageViewETASmily = (ImageView) findViewById(R.id.imageViewETASmily);

			relativeLayoutSharingRides = (RelativeLayout) findViewById(R.id.relativeLayoutSharingRides);
			((TextView) findViewById(R.id.textViewSharingRides)).setTypeface(Fonts.mavenRegular(this));

			fareDetailsRl = (RelativeLayout) findViewById(R.id.fareDetailsRl);
			driverImageRL = (RelativeLayout) findViewById(R.id.driverImageRL);
			fareDetailsText = (TextView) findViewById(R.id.fareDetailsText);
			fareDetailsText.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			relativeLayoutSuperDrivers = (RelativeLayout) findViewById(R.id.relativeLayoutSuperDrivers);
			textViewSuperDrivers = (TextView) findViewById(R.id.textViewSuperDrivers);
			textViewSuperDrivers.setTypeface(Fonts.mavenRegular(this));
			textViewSuperDrivers.setText(getStringText(R.string.super_driver));
			if(Prefs.with(this).getInt(SPLabels.VEHICLE_TYPE,0) == 2){
				textViewSuperDrivers.setText(getResources().getString(R.string.super_biker));
			}

			relativeLayoutDestination = (RelativeLayout) findViewById(R.id.relativeLayoutDestination);
			textViewDestination = (TextView) findViewById(R.id.textViewDestination);

			callUsRl = (RelativeLayout) findViewById(R.id.callUsRl);
			rlGetSupport = (LinearLayout) findViewById(R.id.rlGetSupport);
			callUsText = (TextView) findViewById(R.id.callUsText);
			callUsText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			tvGetSupport = (TextView) findViewById(R.id.tvGetSupport);
			tvGetSupport.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			callUsText.setText(getResources().getText(R.string.call_us));

			relativeLayoutSupport = (RelativeLayout) findViewById(R.id.relativeLayoutSupport);
			relativeLayoutChatSupport = (RelativeLayout) findViewById(R.id.relativeLayoutChatSupport);
			rlMailSupport = (RelativeLayout) findViewById(R.id.rlMailSupport);
			rlSupportMain = (RelativeLayout) findViewById(R.id.rlSupportMain);
			rlSupportTicket = (RelativeLayout) findViewById(R.id.rlSupportTicket);
			relativeLayoutPlans = (RelativeLayout) findViewById(R.id.relativeLayoutPlans);

			((TextView) findViewById(R.id.textViewPlans)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.textViewSupportMain)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.textViewChatSupport)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.textViewMailSupport)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.textViewSupport)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.textViewSupportTicket)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.tvDriverCredits)).setTypeface(Fonts.mavenRegular(this));
			((TextView) findViewById(R.id.tvManualRequest)).setTypeface(Fonts.mavenRegular(this));

			btnChatHead = (TextView) findViewById(R.id.btnChatHead);
			rlChatDriver = (RelativeLayout) findViewById(R.id.rlChatDriver);
			tvChatCount = (TextView) findViewById(R.id.tvChatCount);
//			btnChat1 = (Button) findViewById(R.id.btnChat1);
//			btnChat2 = (Button) findViewById(R.id.btnChat2);

			homeRl = (RelativeLayout) findViewById(R.id.homeRl);
			homeText = (TextView) findViewById(R.id.homeText);
			homeText.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			earningsRL = (RelativeLayout) findViewById(R.id.earningsRL);
			earningsText = (TextView) findViewById(R.id.earningsText);
			earningsText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			((TextView) findViewById(R.id.walletText)).setTypeface(Fonts.mavenRegular(getApplicationContext()));
			((ImageView) findViewById(R.id.imageViewWalletIcon)).setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);


			auditRL = (RelativeLayout) findViewById(R.id.auditRL);
			auditText = (TextView) findViewById(R.id.auditText);
			auditText.setTypeface(Fonts.mavenRegular(getApplicationContext()));


			relativeLayoutRateCard = (RelativeLayout) findViewById(R.id.relativeLayoutRateCard);
			textViewRateCard = (TextView) findViewById(R.id.textViewRateCard);
			textViewRateCard.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewRateCard.setText(getResources().getText(R.string.rate_card));


			termsConditionRl = (RelativeLayout) findViewById(R.id.termsConditionRl);
			termsConditionText = (TextView) findViewById(R.id.termsConditionText);
			termsConditionText.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			paytmRechargeRl = (RelativeLayout) findViewById(R.id.paytmRechargeRl);
			paytmRechargeText = (TextView) findViewById(R.id.paytmRechargeText);
			paytmRechargeText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			paytmRechargeText.setText(getStringText(R.string.paytm_recharge));

			paymentsRl = (RelativeLayout) findViewById(R.id.paymentRL);
			paymentsText = (TextView) findViewById(R.id.paymentText);
			paymentsText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			paymentsText.setText(getResources().getString(R.string.Invoices));

			languagePrefrencesRl = (RelativeLayout) findViewById(R.id.languagePrefrencesRl);
			languagePrefrencesText = (TextView) findViewById(R.id.languagePrefrencesText);
			languagePrefrencesText.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			logoutRl = (RelativeLayout) findViewById(R.id.logoutRl);
			logoutText = (TextView) findViewById(R.id.logoutText);
			logoutText.setTypeface(Fonts.mavenRegular(getApplicationContext()));



			//Top RL
			topRl = (RelativeLayout) findViewById(R.id.topRl);
			menuBtn = (Button) findViewById(R.id.menuBtn);
			checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
			imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);
			textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
			textViewTitleBarDEI.setTypeface(Fonts.mavenRegular(this));
			textViewNotificationValue = (TextView) findViewById(R.id.textViewNotificationValue);
			textViewNotificationValue.setTypeface(Fonts.mavenRegular(this));
			textViewNotificationValue.setVisibility(View.GONE);


			menuBtn.setVisibility(View.VISIBLE);


			// Tutorial Layout
			tourLayout = (RelativeLayout) findViewById(R.id.tour_layout);
			tourLayout.setVisibility(View.GONE);
			tourTextView = (TextView) findViewById(R.id.tour_textView);
			tourTextView.setTypeface(Fonts.mavenRegular(this));
			tourCrossBtn = (ImageView) findViewById(R.id.cross_tour);
			tourCrossBtn.setOnClickListener(this);

			relativeLayoutTour = (RelativeLayout) findViewById(R.id.relativeLayoutTour);
			textViewTour = (TextView) findViewById(R.id.textViewTour);
			textViewTour.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewTour.setText(getResources().getText(R.string.start_training));

			relativeLayoutDocs = (RelativeLayout) findViewById(R.id.relativeLayoutDocs);
			textViewDoc = (TextView) findViewById(R.id.textViewDoc);
			textViewDoc.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			relativeLayoutTour.setOnClickListener(this);
			// Inflate any custom view

			customView = getLayoutInflater().inflate(R.layout.dialog_tour, null); // Display the view just by calling "show"

			RelativeLayout layout = (RelativeLayout) customView.findViewById(R.id.tour_layout);
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			crossTourImageView = (ImageView) customView.findViewById(R.id.cross_tour);
			crossTourImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleTourView(false, "");
				}
			});
			croutonTourTextView = (TextView) customView.findViewById(R.id.tour_textView);
			croutonTourTextView.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			//Map Layout
			mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);



			//Driver initial layout
			driverInitialLayout = (RelativeLayout) findViewById(R.id.driverInitialLayout);
			driverRideRequestsList = (ListView) findViewById(R.id.driverRideRequestsList);
			driverInitialMyLocationBtn = (Button) findViewById(R.id.driverInitialMyLocationBtn);
			driverInformationBtn = (Button) findViewById(R.id.driverInformationBtn);
			jugnooOffText = (TextView) findViewById(R.id.jugnooOffText);
			jugnooOffText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			jugnooOffText.setText(getString(R.string.jugnoo_off, getString(R.string.appname)));
			jugnooOffText.setVisibility(View.GONE);

			buttonUploadOnInitial = (Button) findViewById(R.id.buttonUploadOnInitial);
			linearLayoutJugnooOff = (LinearLayout) findViewById(R.id.linearLayoutJugnooOff);
			textViewDocText = (TextView) findViewById(R.id.textViewDocText);
			textViewDocText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewDocText.setVisibility(View.GONE);

			textViewDocDayText = (TextView) findViewById(R.id.textViewDocDayText);
			textViewDocDayText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewDocDayText.setVisibility(View.GONE);

			driverRideRequestsList.setVisibility(View.GONE);

			driverRequestListAdapter = new DriverRequestListAdapter();
			driverRideRequestsList.setAdapter(driverRequestListAdapter);


			// Driver Request Accept layout
			driverRequestAcceptLayout = (RelativeLayout) findViewById(R.id.driverRequestAcceptLayout);
			driverRequestAcceptBackBtn = (Button) findViewById(R.id.driverRequestAcceptBackBtn);
			driverAcceptRideBtn = (Button) findViewById(R.id.driverAcceptRideBtn);
			driverAcceptRideBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverAcceptRideBtn.setText(getStringText(R.string.accept_ride));
			driverCancelRequestBtn = (Button) findViewById(R.id.driverCancelRequestBtn);
			driverCancelRequestBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverRequestAcceptMyLocationBtn = (Button) findViewById(R.id.driverRequestAcceptMyLocationBtn);
			buttonDriverNavigation = (Button) findViewById(R.id.buttonDriverNavigation);


			// Driver engaged layout
			driverEngagedLayout = (RelativeLayout) findViewById(R.id.driverEngagedLayout);
			perfectRidePassengerInfoRl = (LinearLayout) findViewById(R.id.perfectRidePassengerInfoRl);
			driverPassengerInfoRl = (LinearLayout) findViewById(R.id.driverPassengerInfoRl);


			driverPerfectRidePassengerName = (TextView) findViewById(R.id.driverPerfectRidePassengerName);
			driverPerfectRidePassengerName.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			textViewRideInstructions = (TextView) findViewById(R.id.textViewRideInstructions);
			textViewRideInstructions.setTypeface(Fonts.mavenRegular(this));
//			textViewRideInstructionsInRide = (TextView) findViewById(R.id.textViewRideInstructionsInRide);
//			textViewRideInstructionsInRide.setTypeface(Fonts.mavenRegular(this));

			perfectRidePassengerCallRl = (RelativeLayout) findViewById(R.id.perfectRidePassengerCallRl);
			driverPassengerCallText = (TextView) findViewById(R.id.textViewCall);
			driverPassengerCallText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverEngagedMyLocationBtn = (Button) findViewById(R.id.driverEngagedMyLocationBtn);

//			distanceReset2 = (Button) findViewById(R.id.distanceReset2);


			//Start ride layout
			driverStartRideMainRl = (RelativeLayout) findViewById(R.id.driverStartRideMainRl);
			driverStartRideBtn = (Button) findViewById(R.id.driverStartRideBtn);
			driverStartRideBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverStartRideBtn.setText(getStringText(R.string.start_ride));
			buttonMarkArrived = (Button) findViewById(R.id.buttonMarkArrived);
			buttonMarkArrived.setTypeface(Fonts.mavenRegular(this));
//			buttonMarkArrived.setText(getStringText(R.string.arrived));
			driverCancelRideBtn = (Button) findViewById(R.id.driverCancelRideBtn);
			driverCancelRideBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverCancelRideBtn.setText(getStringText(R.string.cancel));
			tvDropAddressToggleView = (TextView) findViewById(R.id.tvDropAddressToggleView);
			tvDropAddressToggleView.setTypeface(Fonts.mavenRegular(this));
			bDropAddressToggle = (Button) findViewById(R.id.bDropAddressToggle);
			bDropAddressToggle.setTypeface(Fonts.mavenRegular(this));


			//In ride layout
			driverInRideMainRl = (LinearLayout) findViewById(R.id.driverInRideMainRl);
			linearLayoutRideValues = (LinearLayout) findViewById(R.id.linearLayoutRideValues);

			driverIRDistanceText = (TextView) findViewById(R.id.driverIRDistanceText);
			driverIRDistanceText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverIRDistanceText.setText(R.string.distance);
			driverIRDistanceValue = (TextView) findViewById(R.id.driverIRDistanceValue);
			driverIRDistanceValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

			driverIRFareText = (TextView) findViewById(R.id.driverIRFareText);
			driverIRFareText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverIRFareValue = (TextView) findViewById(R.id.driverIRFareValue);
			driverIRFareValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

			driverRideTimeText = (TextView) findViewById(R.id.driverRideTimeText);
			driverRideTimeText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverRideTimeText.setText(getStringText(R.string.ride_time));
			rideTimeChronometer = (PausableChronometer) findViewById(R.id.rideTimeChronometer);
			rideTimeChronometer.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

			driverWaitRl = (RelativeLayout) findViewById(R.id.driverWaitRl);
			driverWaitText = (TextView) findViewById(R.id.driverWaitText);
			driverWaitText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverWaitText.setText(getStringText(R.string.wait_time));

			driverWaitValue = (TextView) findViewById(R.id.driverWaitValue);
			driverWaitValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			driverWaitRl.setVisibility(View.GONE);

			driverIRFareRl = (RelativeLayout) findViewById(R.id.driverIRFareRl);
			driverIRFareRl.setVisibility(View.GONE);

			driverEndRideBtn = (Button) findViewById(R.id.driverEndRideBtn);
			driverEndRideBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			driverEndRideBtn.setText(R.string.end_ride);

			buttonMakeDelivery = (Button) findViewById(R.id.buttonMakeDelivery);
			buttonMakeDelivery.setTypeface(Fonts.mavenRegular(this));

			changeButton = (Button) findViewById(R.id.changeButton);
			changeButton.setTypeface(Fonts.mavenRegular(this));

			rideTimeChronometer.setText("00:00:00");
			driverWaitValue.setText("00:00:00");


			//Review Layout
			endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);

			reviewDistanceText = (TextView) findViewById(R.id.reviewDistanceText);
			reviewDistanceText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			reviewDistanceText.setText(getStringText(R.string.distance));
			reviewDistanceValue = (TextView) findViewById(R.id.reviewDistanceValue);
			reviewDistanceValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			reviewWaitText = (TextView) findViewById(R.id.reviewWaitText);
			reviewWaitText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			reviewWaitText.setText(getStringText(R.string.wait_time));
			reviewWaitValue = (TextView) findViewById(R.id.reviewWaitValue);
			reviewWaitValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			reviewRideTimeText = (TextView) findViewById(R.id.reviewRideTimeText);
			reviewRideTimeText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			reviewRideTimeText.setText(getStringText(R.string.ride_time));
			reviewRideTimeValue = (TextView) findViewById(R.id.reviewRideTimeValue);
			reviewRideTimeValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));
			reviewFareText = (TextView) findViewById(R.id.reviewFareText);
			reviewFareText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			reviewFareValue = (TextView) findViewById(R.id.reviewFareValue);
			reviewFareValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			reviewWaitTimeRl = (RelativeLayout) findViewById(R.id.reviewWaitTimeRl);
			reviewWaitTimeRl.setVisibility(View.GONE);


			reviewReachedDistanceRl = (LinearLayout) findViewById(R.id.reviewReachedDistanceRl);

			linearLayoutMeterFare = (LinearLayout) findViewById(R.id.linearLayoutMeterFare);
			((TextView) findViewById(R.id.textViewEnterMeterFare)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

			linearLayoutMeterFareEditText = (LinearLayout) findViewById(R.id.linearLayoutMeterFareEditText);
			textViewMeterFareRupee = (TextView) findViewById(R.id.textViewMeterFareRupee);
			textViewMeterFareRupee.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
			textViewMeterFareRupee.setVisibility(View.GONE);
			editTextEnterMeterFare = (EditText) findViewById(R.id.editTextEnterMeterFare);
			editTextEnterMeterFare.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

			relativeLayoutEndRideLuggageCount = (RelativeLayout) findViewById(R.id.relativeLayoutEndRideLuggageCount);
			relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
			imageViewEndRideLuggageCountPlus = (ImageView) findViewById(R.id.imageViewEndRideLuggageCountPlus);
			imageViewEndRideLuggageCountMinus = (ImageView) findViewById(R.id.imageViewEndRideLuggageCountMinus);
			textViewEndRideLuggageCount = (TextView) findViewById(R.id.textViewEndRideLuggageCount);
			textViewEndRideLuggageCount.setTypeface(Fonts.mavenRegular(this));

			textViewPerfectRideWating = (TextView) findViewById(R.id.textViewPerfectRideWating);
			textViewPerfectRideWating.setTypeface(Fonts.mavenRegular(this));
			textViewPerfectRideWating.setText(getStringText(R.string.customer_waiting));


			endRideInfoRl = (LinearLayout) findViewById(R.id.endRideInfoRl);
			jugnooRideOverText = (TextView) findViewById(R.id.jugnooRideOverText);
			jugnooRideOverText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
			jugnooRideOverText.setText(getString(R.string.jugnoo_ride_over, getString(R.string.appname)));
			takeFareText = (TextView) findViewById(R.id.takeFareText);
			takeFareText.setTypeface(Fonts.mavenBold(getApplicationContext()));

			endRideCustomText = (TextView) findViewById(R.id.end_ride_custom_text);
			String endRideText = Prefs.with(HomeActivity.this).getString(Constants.END_RIDE_CUSTOM_TEXT, "");
			if(!TextUtils.isEmpty(endRideText)) {
				endRideCustomText.setText(endRideText);
				endRideCustomText.setVisibility(View.VISIBLE);
			} else {
				endRideCustomText.setVisibility(View.GONE);
			}
			endRideCustomText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getRidesAsync();
				}
			});

			reviewSubmitBtn = (Button) findViewById(R.id.reviewSubmitBtn);
			reviewSubmitBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));

			relativeLayoutRateCustomer = (RelativeLayout) findViewById(R.id.relativeLayoutRateCustomer);
			textViewRateYourCustomer = (TextView) findViewById(R.id.textViewRateYourCustomer);
			textViewRateYourCustomer.setTypeface(Fonts.mavenRegular(this));
			textViewRateYourCustomer.setText(getStringText(R.string.Rate_Your_Customer));
			ratingBarFeedback = (RatingBar) findViewById(R.id.ratingBarFeedback);
			ratingBarFeedbackSide = (RatingBar) findViewById(R.id.ratingBarFeedbackSide);
			reviewSkipBtn = (Button) findViewById(R.id.reviewSkipBtn);
			reviewSkipBtn.setTypeface(Fonts.mavenRegular(this));
			reviewSkipBtn.setVisibility(View.GONE);

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

			relativeLayoutLastRideEarning = (RelativeLayout) findViewById(R.id.relativeLayoutLastRideEarning);

			textViewDriverEarningOnScreen  = (TextView) findViewById(R.id.textViewDriverEarningOnScreen);
			textViewDriverEarningOnScreenDate = (TextView) findViewById(R.id.textViewDriverEarningOnScreenDate);
			textViewDriverEarningOnScreenValue = (TextView) findViewById(R.id.textViewDriverEarningOnScreenValue);
			textViewDriverEarningOnScreenValue.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
			imageViewSliderView = (ImageView)findViewById(R.id.imageViewSliderView);

			relativeLayoutRefreshUSLBar = (RelativeLayout) findViewById(R.id.relativeLayoutRefreshUSLBar);
			relativeLayoutBatteryLow = (RelativeLayout) findViewById(R.id.relativeLayoutBatteryLow);

			textViewRetryUSL = (TextView) findViewById(R.id.textViewRetryUSL);
			progressBarUSL = (ProgressBar) findViewById(R.id.progressBarUSL);

			relativeLayoutEnterDestination = (RelativeLayout) findViewById(R.id.relativeLayoutEnterDestination);
			textViewEnterDestination= (TextView) findViewById(R.id.textViewEnterDestination);
			textViewEnterDestination.setTypeface(Fonts.mavenRegular(this));

			linearLayoutSlidingBottom = (RelativeLayout) findViewById(R.id.linearLayoutSlidingBottom);
			slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.slidingLayout);
			recyclerViewInfo = (RecyclerView) findViewById(R.id.recyclerViewInfo);
			recyclerViewInfo.setHasFixedSize(true);
			linearLayoutManagerScrollControl = new LinearLayoutManager(this);
			linearLayoutManagerScrollControl.setOrientation(LinearLayoutManager.VERTICAL);
//			linearLayoutManagerScrollControl.setScrollEnabled(false);
			recyclerViewInfo.setLayoutManager(linearLayoutManagerScrollControl);
			recyclerViewInfo.setItemAnimator(new DefaultItemAnimator());
			linearLayoutSlidingBottom.setBackgroundColor(getResources().getColor(R.color.transparent));
			linearLayoutRide = (LinearLayout) findViewById(R.id.linearLayoutRide);
			deliveryListHorizontal = (MyViewPager) findViewById(R.id.deliveryListHorizontal);
			deliveryListHorizontal.addOnPageChangeListener(this);
			infoTileResponses = new ArrayList<>();
			infoTilesAdapter = new InfoTilesAdapter(this, infoTileResponses, adapterHandler);


			btnHelp = (Button) findViewById(R.id.btnHelp);

			recyclerViewInfo.setAdapter(infoTilesAdapter);
			slidingUpPanelLayout.setScrollableView(recyclerViewInfo);

			relativeLayoutItemHeader = (RelativeLayout) findViewById(R.id.relativeLayoutItemHeader);
			topRlOuter = (RelativeLayout) findViewById(R.id.topRlOuter);
			reCreateDeliveryMarkers = true;

			slidingUpPanelLayout.setPanelHeight((int) (140f * ASSL.Yscale()));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						if(DriverScreenMode.D_INITIAL == driverScreenMode && infoTileResponses.size() > 0 ) {
							slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 5000);
			slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
				@Override
				public void onPanelSlide(View panel, float slideOffset) {
				}

				@Override
				public void onPanelCollapsed(View panel) {
					imageViewSliderView.setImageResource(R.drawable.up_arrow_even_sahdow);
					driverInitialMyLocationBtn.setVisibility(View.VISIBLE);
//					recyclerViewInfo.smoothScrollToPosition(0);
					linearLayoutManagerScrollControl.scrollToPositionWithOffset(0, 1);
				}

				@Override
				public void onPanelExpanded(View panel) {
					infoTilesAdapter.notifyDataSetChanged();
					imageViewSliderView.setImageResource(R.drawable.down_arrow_even_sahdow);
					driverInitialMyLocationBtn.setVisibility(View.GONE);

				}

				@Override
				public void onPanelAnchored(View panel) {
				}

				@Override
				public void onPanelHidden(View panel) {
				}
			});


			customerSwitcher = new CustomerSwitcher(this, drawerLayout);
			deliveryInfoTabs = new DeliveryInfoTabs(this, drawerLayout);

			linearLayoutEndRideMain.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(linearLayoutEndRideMain, textViewScroll,
					new KeyboardLayoutListener.KeyBoardStateHandler() {
						@Override
						public void keyboardOpened() {

						}

						@Override
						public void keyBoardClosed() {

						}
					}));


			try {
			if(Prefs.with(HomeActivity.this).getInt(Constants.SET_DRIVER_TUTORIAL_STATUS, 0) == 1){
				relativeLayoutTour.setVisibility(View.GONE);
			} else {
				relativeLayoutTour.setVisibility(View.GONE);
			}

				if(Prefs.with(HomeActivity.this).getInt(SPLabels.SET_TRAINING_ID, 0) != 0
                        && !TextUtils.isEmpty(Prefs.with(HomeActivity.this).getString(SPLabels.SET_DRIVER_TOUR_STATUS, ""))) {
                    tourCompleteApi(Prefs.with(HomeActivity.this).getString(SPLabels.SET_DRIVER_TOUR_STATUS, ""),
                            String.valueOf(Prefs.with(HomeActivity.this).getInt(SPLabels.SET_TRAINING_ID, -1)));

					Prefs.with(activity).save(SPLabels.PREF_TRAINING_ACCESS_TOKEN, "");
					Prefs.with(activity).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
					Prefs.with(activity).save(SPLabels.PREF_TRAINING_ID, "");
                } else if(Prefs.with(HomeActivity.this).getInt(SPLabels.SET_TRAINING_ID, 0) != 0) {
                    tourCompleteApi("2", String.valueOf(Prefs.with(HomeActivity.this).getInt(SPLabels.SET_TRAINING_ID, -1)));
					Prefs.with(activity).save(SPLabels.PREF_TRAINING_ACCESS_TOKEN, "");
					Prefs.with(activity).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
					Prefs.with(activity).save(SPLabels.PREF_TRAINING_ID, "");
                }
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Top bar events
			menuBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.openDrawer(menuLayout);
					FlurryEventLogger.event(FlurryEventNames.MENU);
					firebaseJugnooDeliveryHomeEvent(FirebaseEvents.MENU);
					NudgeClient.trackEvent(HomeActivity.this, FlurryEventNames.NUDGE_MENU_CLICK, null);
					if (DriverScreenMode.D_INITIAL == driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.HOME_MENU);
					} else if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.HOME_IN_RIDE_MENU);
					}
				}
			});


			drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {

				}

				@Override
				public void onDrawerOpened(View drawerView) {
					setPannelVisibility(true);
				}

				@Override
				public void onDrawerClosed(View drawerView) {

				}

				@Override
				public void onDrawerStateChanged(int newState) {

				}
			});

			deliveryListHorizontal.setOnTouchListener(new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					try {
						CustomerInfo customerInfo = getCurrentCustomerInfo();
						if(customerInfo.getFalseDeliveries() ==1){
							return true;
						} else {
							return false;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			});


			imageViewSliderView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
						slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
						FlurryEventLogger.event(FlurryEventNames.HOME_SLIDEUP_BUTTON);
						firebaseJugnooDeliveryHomeEvent(SLIDE_UP_BUTTON);
					} else {
						slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
						FlurryEventLogger.event(FlurryEventNames.HOME_SLIDEDOWN_BUTTON);
						firebaseJugnooDeliveryHomeEvent(SLIDE_DOWN_BUTTON);
					}
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
			relativeLayoutAutosOn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.autosAvailable == 1) {
							changeJugnooON(0, false, false);
							resetSharedPrefs();
							MyApplication.getInstance().logEvent(HOME_SCREEN+"_"+JUGNOO+"_off", null);
						} else {
							changeJugnooON(1, false, false);
							MyApplication.getInstance().logEvent(HOME_SCREEN + "_" + JUGNOO + "_on", null);
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

			relativeLayoutDeliveryOn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userMode == UserMode.DRIVER && driverScreenMode == DriverScreenMode.D_INITIAL) {
						if (Data.userData.getDeliveryAvailable() == 1) {
							changeJugnooON(0, false, true);
							MyApplication.getInstance().logEvent(HOME_SCREEN + "_" + DELIVERY + "_on", null);

						} else {
							changeJugnooON(1, false, true);
							MyApplication.getInstance().logEvent(HOME_SCREEN + "_" + DELIVERY + "_off", null);
						}
						//handleTourView(false, "");
						FlurryEventLogger.event(DELIVERY_ON_OFF);
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

			walletRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, StripeCardsActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}
			});

			driverCreditsRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverCreditsActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}
			});
			manaulRequestRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawer(GravityCompat.START);
					startActivity(new Intent(HomeActivity.this, ManualRideActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

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
					if(DriverScreenMode.D_INITIAL == driverScreenMode){
						FlurryEventLogger.event(FlurryEventNames.HOME_NOTIFICATION);
						firebaseJugnooDeliveryHomeEvent(NOTIFICATION);
					} else if(DriverScreenMode.D_IN_RIDE == driverScreenMode){
						FlurryEventLogger.event(FlurryEventNames.HOME_IN_RIDE_NOTIFICATION);
					}
				}
			});


			driverImageRL.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverProfileActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
					FlurryEventLogger.event(HOME_ITEM_PROFILE);
					firebaseJugnooDeliveryHomeEvent(FirebaseEvents.MENU + "" + ITEM_PROFILE);
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

			homeRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawer(GravityCompat.START);
				}
			});

			callUsRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.makeCallIntent(HomeActivity.this, Data.userData.driverSupportNumber);

					FlurryEventLogger.event(CALL_US);

					drawerLayout.closeDrawer(GravityCompat.START);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});
			rlGetSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					HomeUtil.scheduleCallDriver(HomeActivity.this);
					drawerLayout.closeDrawer(GravityCompat.START);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			relativeLayoutSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, DriverTicketHistory.class);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});
			relativeLayoutChatSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					HippoConfig.getInstance().showConversations(HomeActivity.this, getString(R.string.chat));
				}
			});
			rlSupportMain.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, SupportOptionsActivity.class));
				}
			});
			rlMailSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, SupportMailActivity.class));
				}
			});
			rlSupportTicket.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						HippoTicketAttributes.Builder builder = new HippoTicketAttributes.Builder();
						if(Data.userData != null){
							builder.setFaqName(Data.userData.getHippoTicketFAQ());
						}

						ArrayList<String> tags = new ArrayList<>();
						tags.add(Constants.HIPPO_TAG_DRIVER_APP);
						builder.setTags(tags);

						HippoConfig.getInstance().showFAQSupport(builder.build());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			relativeLayoutPlans.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, JugnooSubscriptionActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});


			relativeLayoutDocs.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, DriverResourceActivity.class);
					startActivityForResult(intent, 14);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			btnChatHead.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);
						chatIntent.putExtra("engagement_id", Data.getCurrentEngagementId());
						chatIntent.putExtra("user_image", Data.getCurrentCustomerInfo().image);
						startActivity(chatIntent);
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});


			btnHelp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.openCallIntent(HomeActivity.this, Data.userData.driverSupportNumber);
					FlurryEventLogger.event(CALL_US);
					Log.i("completeRingData",Database2.getInstance(HomeActivity.this).getRingCompleteData());
				}
			});


			earningsRL.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, DriverEarningsNew.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			relativeLayoutRateCard.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, NewRateCardActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}
			});

			auditRL.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(HomeActivity.this, SelfAuditActivity.class));
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

//		Intent intent = new Intent(HomeActivity.this, DriverDocumentActivity.class);
//		startActivity(intent);

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

			SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
			String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);

			if (Prefs.with(this).getInt(Constants.KEY_LOGOUT, getResources().getInteger(R.integer.driver_logout_enabled)) == 1) {
				logoutRl.setVisibility(View.VISIBLE);
			} else {
				logoutRl.setVisibility(View.GONE);
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
					MyApplication.getInstance().logEvent(HOME_SCREEN + "_" + WHITE_SCREEN, null);
					drawerLayout.openDrawer(menuLayout);
				}
			});

			buttonUploadOnInitial.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, DriverDocumentActivity.class);
					intent.putExtra("access_token",Data.userData.accessToken);
					intent.putExtra("in_side", true);
					intent.putExtra("doc_required", 0);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
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


			relativeLayoutRefreshUSLBar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					textViewRetryUSL.setVisibility(View.GONE);
					progressBarUSL.setVisibility(View.VISIBLE);
					updateUSL();
				}
			});


			driverStartRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START + "_" + FirebaseEvents.YES, null);
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
						firebaseScreenEvent(FirebaseEvents.YES);
						if (Utils.getBatteryPercentage(HomeActivity.this) >= 10) {
							DialogPopup.alertPopupTrainingTwoButtonsWithListeners(HomeActivity.this, "", getResources().getString(R.string.have_arrived), getResources().getString(R.string.yes), getResources().getString(R.string.no),
									new OnClickListener() {
										@Override
										public void onClick(View v) {
											MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CONFIRM_YES, null);
											if (myLocation != null) {
												try {
													LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
													CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
													double displacement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getRequestlLatLng());
													double actualDispalcement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getCurrentLatLng());
													double arrivingDistance = Prefs.with(context).getInt(Constants.KEY_DRIVER_SHOW_ARRIVE_UI_DISTANCE, 600);

													if (displacement <= arrivingDistance || actualDispalcement <= arrivingDistance) {
														buildAlertMessageNoGps();
														if (isTourFlag) {
															driverScreenMode = DriverScreenMode.D_START_RIDE;
															Data.setCustomerState(String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()),
																	driverScreenMode);
															switchDriverScreen(driverScreenMode);
															handleTourView(isTourFlag, getString(R.string.tutorial_tap_to_start_ride));
														} else {
															GCMIntentService.clearNotifications(activity);
															driverMarkArriveRideAsync(activity, driverAtPickupLatLng, customerInfo);
															FlurryEventLogger.event(CONFIRMING_ARRIVE_YES);
														}
													} else {
//														if(isTourFlag) {
															DialogPopup.alertPopupWithListenerTopBar(HomeActivity.this, "", getResources().getString(R.string.present_near_customer_location),
																	new OnClickListener() {
																@Override
																public void onClick(View v) {
																	handleTourView(isTourFlag, getString(R.string.tutorial_tap_arrived_if_at_pickup));
																}
															}, new OnClickListener() {
																@Override
																public void onClick(View v) {
																	driverCancelRideBtn.performClick();
																}
															}, isTourFlag, getString(R.string.tutorial_driver_to_pickup_point));
//														} else {
//															DialogPopup.alertPopup(activity, "", getResources().getString(R.string.present_near_customer_location));
//														}
													}
												} catch (Exception e) {
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
											handleTourView(isTourFlag, getString(R.string.tutorial_tap_arrived_if_at_pickup));
											MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CONFIRM_NO, null);
											FlurryEventLogger.event(CONFIRMING_ARRIVE_NO);
										}
									}, false, false, isTourFlag, getString(R.string.tutorial_tap_yes),
									new OnClickListener() {
										@Override
										public void onClick(View v) {
//											handleTourView(false, "");
											tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 2);
										}
									});
							FlurryEventLogger.event(ARRIVED_ON_THE_PICK_UP_LOCATION);
							if (isTourFlag) {
								handleTourView(isTourFlag, getString(R.string.tutorial_tap_yes));
							}
						}
						else{
							DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.battery_level_arrived_text));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});


			driverCancelRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if(isTourFlag) {

							tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 2);
//							handleTourView(false, "");
						} else {
							Intent intent = new Intent(HomeActivity.this, RideCancellationActivity.class);
							intent.putExtra(KEY_ENGAGEMENT_ID, Data.getCurrentEngagementId());
							startActivityForResult(intent, 12);

							overridePendingTransition(R.anim.right_in, R.anim.right_out);
							if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
								MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ACCEPTED + "_" + FirebaseEvents.CANCEL, null);
								FlurryEventLogger.event(CANCELED_BEFORE_ARRIVING);
							} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
								MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CANCEL, null);
								FlurryEventLogger.event(RIDE_CANCELLED_AFTER_ARRIVING);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			relativeLayoutEnterDestination.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isTourFlag) {
						relativeLayoutContainer.setVisibility(View.VISIBLE);
						placeSearchListFragment = new PlaceSearchListFragment(HomeActivity.this, mGoogleApiClient);
						getSupportFragmentManager().beginTransaction()
								.add(R.id.relativeLayoutContainer, placeSearchListFragment, PlaceSearchListFragment.class.getName())
								.addToBackStack(PlaceSearchListFragment.class.getName())
								.commit();
					}
				}
			});


			driverEndRideBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_END_RIDE, null);
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

			changeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					relativeLayoutContainer.setVisibility(View.VISIBLE);
					CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
					try {

						getTransactionUtils().openSwitchCustomerFragment(HomeActivity.this, getRelativeLayoutContainer());

//						if(customerInfo.getIsPooled() ==1 || customerInfo.getIsDeliveryPool() ==1){
//							getTransactionUtils().openSwitchCustomerFragment(HomeActivity.this, getRelativeLayoutContainer());
//						} else if(customerInfo.getIsDelivery() ==1){
//							if(anyDeliveriesUnchecked(customerInfo)){
//								getTransactionUtils().openDeliveryInfoListFragment(HomeActivity.this,
//										getRelativeLayoutContainer(), customerInfo.getEngagementId(),
//										DeliveryStatus.PENDING);
//							} else{
//								getTransactionUtils().openDeliveryInfoListFragment(HomeActivity.this,
//										getRelativeLayoutContainer(), customerInfo.getEngagementId(),
//										DeliveryStatus.COMPLETED);
//							}
//						}
					} catch (Exception e) {
						e.printStackTrace();
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

			apiSendRingCountData();


			reviewSubmitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (isTourFlag) {
							tourCompleteApi("3", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
							handleTourView(false, "");
						}
//
							MyApplication.getInstance().logEvent(FirebaseEvents.RATING + "_" + FirebaseEvents.SUBMIT, null);
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
											String message = getResources().getString(R.string.amount_entered) + Utils.formatCurrencyValue(customerInfo.getCurrencyUnit() , enteredMeterFare);
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
									try {
										MyApplication.getInstance().logEvent(FirebaseEvents.RATING + "_" + FirebaseEvents.SKIP, null);
										saveCustomerRideDataInSP(Data.getCurrentCustomerInfo());
										MeteringService.clearNotifications(HomeActivity.this);
										Data.removeCustomerInfo(Integer.parseInt(Data.getCurrentEngagementId()), EngagementStatus.ENDED.getOrdinal());

										driverScreenMode = DriverScreenMode.D_INITIAL;
										switchDriverScreen(driverScreenMode);
										FlurryEventLogger.event(OK_ON_FARE_SCREEN);
										perfectRideStateRestore();
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									MyApplication.getInstance().logEvent(FirebaseEvents.RATING + "_" + rating, null);
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
					try {
						MyApplication.getInstance().logEvent(FirebaseEvents.RATING + "_" + FirebaseEvents.SKIP,null);
						saveCustomerRideDataInSP(Data.getCurrentCustomerInfo());
						MeteringService.clearNotifications(HomeActivity.this);
						Data.removeCustomerInfo(Integer.parseInt(Data.getCurrentEngagementId()), EngagementStatus.ENDED.getOrdinal());

						driverScreenMode = DriverScreenMode.D_INITIAL;
						switchDriverScreen(driverScreenMode);
						FlurryEventLogger.event(OK_ON_FARE_SCREEN);
						perfectRideStateRestore();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			driverInformationBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(HomeActivity.this, NotificationCenterActivity.class);
					intent.putExtra("trick_page", 1);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});

			relativeLayoutLastRideEarning.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					relativeLayoutLastRideEarning.setVisibility(View.GONE);
					Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
					intent.putExtra("trick_page", 1);
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
								customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(HomeActivity.this), HomeActivity.this),0));
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

			bDropAddressToggle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					tvDropAddressToggleView.setVisibility(tvDropAddressToggleView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
				}
			});

//			distanceReset2.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					MeteringService.gpsInstance(HomeActivity.this).distanceResetForced();
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
					startActivity(new Intent(this, DriverSplashActivity.class));
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

            IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Constants.ACTION_UPDATE_RIDE_EARNING);
			intentFilter.addAction(Constants.UPDATE_MPESA_PRICE);
			HomeActivity.this.registerReceiver(broadcastReceiver, intentFilter);
			HomeActivity.this.registerReceiver(broadcastReceiverUSL, new IntentFilter(Constants.ACTION_REFRESH_USL));
			HomeActivity.this.registerReceiver(broadcastReceiverLowBattery, new IntentFilter(Constants.ALERT_BATTERY_LOW));
			HomeActivity.this.registerReceiver(broadcastReceiverIsCharging, new IntentFilter(Constants.ALERT_CHARGING));
			HomeActivity.this.registerReceiver(broadcastReceiverCancelEndDeliveryPopup, new IntentFilter(Constants.DISMISS_END_DELIVERY_POPUP));

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					if (Prefs.with(HomeActivity.this).getInt(SPLabels.SET_AUDIT_STATUS_POPUP, 0) == 1) {
						DialogPopup.alertPopupAuditWithListener(HomeActivity.this, "",
								Prefs.with(HomeActivity.this).getString(SPLabels.SET_AUDIT_POPUP_STRING, ""), new OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(HomeActivity.this, SelfAuditActivity.class);
										intent.putExtra("self_audit", "yes");
										startActivity(intent);
										finish();
										overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
								});
					}
					int avgEarning = Prefs.with(HomeActivity.this).getInt(AVERAGE_DRIVER_EARNING,0);
					if(avgEarning > 0){
						String averageDays = getResources().getString(R.string.average_days_text, String.valueOf(Prefs.with(HomeActivity.this).getInt(AVERAGE_EARNING_DAYS, 0)));
						String heading = getResources().getString(R.string.did_you_know);
						DialogPopup.driverEarningPopup(HomeActivity.this, heading, String.valueOf(avgEarning), averageDays,false, true);
					}

					int maxDriverEarning = Prefs.with(HomeActivity.this).getInt(DIFF_MAX_EARNING, 0);
					if(maxDriverEarning>0) {
						DialogPopup.alertPopup(HomeActivity.this,"", getResources().getString(R.string.cancel));
						String heading = getResources().getString(R.string.max_earning);
						DialogPopup.driverEarningPopup(HomeActivity.this, heading,"",
								getResources().getString(R.string.max_earning_ins,
										String.valueOf(Utils.formatCurrencyValue(Prefs.with(HomeActivity.this).getString(KEY_CURRENCY, "INR"),
												maxDriverEarning))),false, true);
					}
			}

			}, 300);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					if(Prefs.with(HomeActivity.this).getInt(SPLabels.DIGITAL_SIGNATURE_POPUP_STATUS,0) == 1){
						DialogPopup.alertPopupWithListener(HomeActivity.this, "",
								Prefs.with(HomeActivity.this).getString(SPLabels.SET_DIGITAL_SIGNATURE_POPUP_STRING,""), new OnClickListener() {
									@Override
									public void onClick(View v) {
										drawerLayout.closeDrawer(GravityCompat.START);
										relativeLayoutContainer.setVisibility(View.VISIBLE);
										getTransactionUtils().openAddSignatureFragment(HomeActivity.this, getRelativeLayoutContainer());
										overridePendingTransition(R.anim.right_in, R.anim.right_out);
//										overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
								});
					}

				}
			}, 300);




			if(Prefs.with(HomeActivity.this).getInt(Constants.SELF_AUDIT_BUTTON_STATUS,0) == 1){
				auditRL.setVisibility(View.VISIBLE);
			} else {
				auditRL.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.SHOW_SUPPORT_IN_MENU,0) == 1){
				relativeLayoutSupport.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutSupport.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.CHAT_SUPPORT,0) == 1){
				relativeLayoutChatSupport.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutChatSupport.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.SUPPORT_MAIN,0) == 1){
				rlSupportMain.setVisibility(View.VISIBLE);
			} else {
				rlSupportMain.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.TICKET_SUPPORT,0) == 1){
				rlSupportTicket.setVisibility(View.VISIBLE);
			} else {
				rlSupportTicket.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.MAIL_SUPPORT,0) == 1){
				rlMailSupport.setVisibility(View.VISIBLE);
			} else {
				rlMailSupport.setVisibility(View.GONE);
			}

			if( Prefs.with(HomeActivity.this).getInt(Constants.SHOW_PLANS_IN_MENU,0) == 1){
				relativeLayoutPlans.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutPlans.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.SHOW_RATE_CARD_IN_MENU,0) == 1){
				relativeLayoutRateCard.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutRateCard.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.SHOW_CALL_US_MENU,0) == 1){
				callUsRl.setVisibility(View.VISIBLE);
			} else {
				callUsRl.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.SHOW_IN_APP_CALL_US,0) == 1){
				rlGetSupport.setVisibility(View.VISIBLE);
			} else {
				rlGetSupport.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.LANGUAGE_PREFERENCE_IN_MENU, 1) == 1){
				languagePrefrencesRl.setVisibility(View.VISIBLE);
			} else {
				languagePrefrencesRl.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.INVITE_FRIENDS_IN_MENU, 1) == 1){
				inviteFriendRl.setVisibility(View.VISIBLE);
			} else {
				inviteFriendRl.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.DRIVER_RESOURCES_IN_MENU, 1) == 1){
				relativeLayoutDocs.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutDocs.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.SUPER_DRIVERS_IN_MENU, 1) == 1){
				relativeLayoutSuperDrivers.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutSuperDrivers.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.INVOICES_IN_MENU, 1) == 1){
				paymentsRl.setVisibility(View.VISIBLE);
			} else {
				paymentsRl.setVisibility(View.GONE);
			}
			if(Prefs.with(HomeActivity.this).getInt(Constants.EARNINGS_IN_MENU, 1) == 1){
				earningsRL.setVisibility(View.VISIBLE);
			} else {
				earningsRL.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.DRIVER_CREDITS, 1) == 1){
				driverCreditsRl.setVisibility(View.VISIBLE);
			} else {
				driverCreditsRl.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.SHOW_MANUAL_RIDE, 0) == 1){
				manaulRequestRl.setVisibility(View.VISIBLE);
			} else {
				manaulRequestRl.setVisibility(View.GONE);
			}

			if(Prefs.with(HomeActivity.this).getInt(Constants.WALLET, 0) == 1){
				walletRl.setVisibility(View.VISIBLE);
			} else {
				walletRl.setVisibility(View.GONE);
			}








		} catch (Exception e) {
			e.printStackTrace();
		}


		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(Prefs.with(HomeActivity.this).getBoolean(SPLabels.ACCEPT_RIDE_VIA_PUSH, false)){
					callAndHandleStateRestoreAPI();
					Prefs.with(HomeActivity.this).save(SPLabels.ACCEPT_RIDE_VIA_PUSH, false);
				}
			}
		}, 200);



		if(DriverScreenMode.D_INITIAL == driverScreenMode) {
			getInfoTilesAsync(HomeActivity.this);
		}

		try {
			HippoNotificationConfig.handleHippoPushNotification(HomeActivity.this, getIntent().getBundleExtra(Constants.FUGU_CHAT_BUNDLE));
		} catch (Exception e) {
			e.printStackTrace();
		}
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		Log.e("device_height",height+"");
		Log.e("device_width",width+"");
	}


	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		    if(intent.getAction().equalsIgnoreCase(Constants.UPDATE_MPESA_PRICE)){
		        endRideData.toPay=Double.parseDouble(intent.getStringExtra("to_pay"));
                takeFareText.setText(Utils.formatCurrencyValue(endRideData.getCurrency(),endRideData.toPay)); ;
            }
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showDriverEarning();
				}
			});
		}
	};

	BroadcastReceiver broadcastReceiverUSL = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showRefreshUSLBar();
				}
			});
		}
	};

	BroadcastReceiver broadcastReceiverLowBattery = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showLowBatteryAlert(true);
				}
			});
		}
	};

	BroadcastReceiver broadcastReceiverIsCharging = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int flag = intent.getIntExtra("type", -1);
					if(flag ==1){
						tvChatCount.setVisibility(View.VISIBLE);
						tvChatCount.setText(String.valueOf(Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 1)));
					} else {
						showLowBatteryAlert(false);
					}
				}
			});
		}
	};

	BroadcastReceiver broadcastReceiverCancelEndDeliveryPopup = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			HomeActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(endDelivery != null && callbackEndDelivery != null) {
							String response = intent.getStringExtra("response");
							callbackEndDelivery.success(response);
							endDelivery.dismissAlertPopup();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	};


	InfoTilesAdapterHandler adapterHandler = new InfoTilesAdapterHandler() {
		@Override
		public void okClicked(Tile infoTileResponse, int pos) {

			if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
			} else {

				openDeepIndexScreen(infoTileResponse, pos);
			}

		}
	};

	private void openDeepIndexScreen(Tile infoTileResponse, int pos) {
		if (infoTileResponse.getDeepIndex() == 1) {
			Intent intent = new Intent(HomeActivity.this, RideDetailsNewActivity.class);
			Gson gson = new Gson();
			intent.putExtra("extras", gson.toJson(infoTileResponse.getExtras(), Tile.Extras.class));
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_RIDE);
			firebaseJugnooDeliveryHomeEvent(ITEM_RIDE + "_" + pos);
		} else if (infoTileResponse.getDeepIndex() == 2) {
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = df.format(c.getTime());
			Intent intent = new Intent(HomeActivity.this, DailyRideDetailsActivity.class);
			intent.putExtra("date", formattedDate);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_DAILY + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_DAILY);
		} else if (infoTileResponse.getDeepIndex() == 3) {
			Intent intent = new Intent(HomeActivity.this, HighDemandAreaActivity.class);
			intent.putExtra("title", String.valueOf(infoTileResponse.getTitle()));
			intent.putExtra("extras", String.valueOf(infoTileResponse.getExtras().getRedirectUrl()));
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_WEB + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_WEB);
		} else if (infoTileResponse.getDeepIndex() == 4) {
			Intent intent = new Intent(HomeActivity.this, PaymentActivity.class);
			intent.putExtra("extras", String.valueOf(infoTileResponse.getExtras()));
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_INVOICE + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_INVOICE);
		} else if (infoTileResponse.getDeepIndex() == 5) {
			Intent intent = new Intent(HomeActivity.this, DriverEarningsNew.class);
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_EARNINGS + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_EARNINGS);
		} else if (infoTileResponse.getDeepIndex() == 6) {
			Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_INVITE + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_INVITE);
		} else if (infoTileResponse.getDeepIndex() == 7) {
			Intent intent = new Intent(HomeActivity.this, NotificationCenterActivity.class);
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_NOTIFICATION + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_NOTIFICATION);
		} else if (infoTileResponse.getDeepIndex() == 8) {
			Intent intent = new Intent(HomeActivity.this, NotificationCenterActivity.class);
			intent.putExtra("trick_page", 1);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_TIPS + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_TIPS);
		} else if (infoTileResponse.getDeepIndex() == 9) {
			Intent intent = new Intent(HomeActivity.this, DriverProfileActivity.class);
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_PROFILE + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_PROFILE);
		} else if (infoTileResponse.getDeepIndex() == 10) {
			Intent intent = new Intent(HomeActivity.this, HighDemandAreaActivity.class);
			intent.putExtra("title", String.valueOf(infoTileResponse.getTitle()));
			intent.putExtra("extras", String.valueOf(infoTileResponse.getExtras().getRedirectUrl()));
			HomeActivity.this.startActivity(intent);
			HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			firebaseJugnooDeliveryHomeEvent(ITEM_FULFILLMENT + "_" + pos);
			FlurryEventLogger.event(FlurryEventNames.HOME_ITEM_FULFILLMENT);
		} else if (infoTileResponse.getDeepIndex() == 11) {
			try {
				String finalUrl = String.valueOf(infoTileResponse.getExtras().getRedirectUrl())
						+ "?access_token=" + Database2.getInstance(HomeActivity.this).getDLDAccessToken();
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setPackage("com.android.chrome");
				startActivity(i);
			} catch (ActivityNotFoundException e) {
				// Chrome is not installed
				Intent intent1 = new Intent(HomeActivity.this, HighDemandAreaActivity.class);
				intent1.putExtra("title", String.valueOf(infoTileResponse.getTitle()));
				intent1.putExtra("extras", String.valueOf(infoTileResponse.getExtras().getRedirectUrl()));
				HomeActivity.this.startActivity(intent1);
				HomeActivity.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			} catch (Exception e) {

			}
		}
	}

	public void updateUSL(){
		new Thread(new Runnable() {
			@Override
			public void run() {

				if(myLocation != null) {
					Database2.getInstance(HomeActivity.this).updateDriverCurrentLocation(HomeActivity.this, myLocation);
				}
				new DriverLocationDispatcher().sendLocationToServer(HomeActivity.this);

				if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
					new DriverLocationDispatcher().sendLocationToServer(HomeActivity.this);
				}
			}
		}).start();
	}

	public void firebaseJugnooDeliveryHomeEvent(String event){
		if(Data.userData.autosAvailable == 0 && Data.userData.getDeliveryAvailable() == 0){

			MyApplication.getInstance().logEvent(HOME_SCREEN + "_0_0_" + event, null);

		} else if(Data.userData.autosAvailable == 1 && Data.userData.getDeliveryAvailable() == 0){

			MyApplication.getInstance().logEvent(HOME_SCREEN + "_1_0_" + event, null);

		} else if(Data.userData.autosAvailable == 0 && Data.userData.getDeliveryAvailable() == 1){

			MyApplication.getInstance().logEvent(HOME_SCREEN + "_0_1_" + event, null);

		} else if(Data.userData.autosAvailable == 1 && Data.userData.getDeliveryAvailable() == 1){

			MyApplication.getInstance().logEvent(HOME_SCREEN + "_1_1_" + event, null);

		}
	}

	public void updateInfoTileListData(String message, boolean errorOccurred) {
		try {
			if (errorOccurred) {
	//			DialogPopup.alertPopup(HomeActivity.this, "", message);
				infoTileResponses.clear();
				infoTilesAdapter.notifyDataSetChanged();
				linearLayoutSlidingBottom.setVisibility(View.GONE);
			} else {
				if(infoTileResponses.size() ==0){
					linearLayoutSlidingBottom.setVisibility(View.GONE);
				} else {

					try {
						LayoutParams params = linearLayoutSlidingBottom.getLayoutParams();

						if (tileCount > 0 && tileCount <= 1) {
							params.height = tileCount * (int) (310f * ASSL.Yscale());
						} else if (tileCount >= 2 && tileCount < 3) {
							params.height = tileCount * (int) (280f * ASSL.Yscale());
						} else if (tileCount >= 3 && tileCount < 4) {
							params.height = tileCount * (int) (272f * ASSL.Yscale());
						} else {
							params.height = (int) (980f * ASSL.Yscale());
						}
						linearLayoutSlidingBottom.setLayoutParams(params);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				infoTilesAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void showDriverEarning(){
		try {
			if (!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(Constants.DRIVER_RIDE_EARNING, ""))
					&& DriverScreenMode.D_INITIAL == HomeActivity.driverScreenMode) {

				relativeLayoutLastRideEarning.setVisibility(View.GONE);

				textViewDriverEarningOnScreenValue.setText(Utils.formatCurrencyValue(Prefs.with(HomeActivity.this).getString(Constants.DRIVER_RIDE_EARNING_CURRENCY, "INR") , Prefs.with(HomeActivity.this).getString(Constants.DRIVER_RIDE_EARNING, "")));

				textViewDriverEarningOnScreenDate.setText(Prefs.with(HomeActivity.this).getString(Constants.DRIVER_RIDE_DATE, ""));
			} else {
				relativeLayoutLastRideEarning.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void showRefreshUSLBar(){
		try {
			if (DriverScreenMode.D_INITIAL == HomeActivity.driverScreenMode
					&& Prefs.with(HomeActivity.this).getBoolean(SPLabels.GET_USL_STATUS, false)) {

				textViewRetryUSL.setVisibility(View.VISIBLE);
				progressBarUSL.setVisibility(View.GONE);
				if(relativeLayoutRefreshUSLBar.getVisibility() == View.GONE) {
					relativeLayoutRefreshUSLBar.setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_down);
					relativeLayoutRefreshUSLBar.startAnimation(animation);
				}
			} else {
				relativeLayoutRefreshUSLBar.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void showLowBatteryAlert(boolean visibility){
		try {
			if(visibility) {
				if (DriverScreenMode.D_INITIAL == HomeActivity.driverScreenMode
						&& Utils.isBatteryChargingNew(HomeActivity.this) == 0
						&& Double.parseDouble(Utils.getActualBatteryPer(HomeActivity.this)) < 20d) {
					if(relativeLayoutBatteryLow.getVisibility() == View.GONE) {
						relativeLayoutBatteryLow.setVisibility(View.VISIBLE);
						Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_down);
						relativeLayoutBatteryLow.startAnimation(animation);
					}
				} else {
					relativeLayoutBatteryLow.setVisibility(View.GONE);
				}
			} else {
				relativeLayoutBatteryLow.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public DeliveryInfoTabs getDeliveryInfoTabs() {
		return deliveryInfoTabs;
	}

	public void resetSharedPrefs(){
		if(Data.userData.autosAvailable == 0 && Data.userData.getDeliveryAvailable() == 0){
			Prefs.with(this).remove(SP_CUSTOMER_RIDE_DATAS_OBJECT);
			Prefs.with(this).remove(SPLabels.PERFECT_ACCEPT_RIDE_DATA);
			Prefs.with(this).remove(SPLabels.PERFECT_CUSTOMER_CONT);
			Prefs.with(this).remove(EngagementSP.SP_ENGAGEMENTS_ATTACHED);
			Database2.getInstance(this).deleteCustomerRideData();
			Database2.getInstance(this).deleteAllCurrentPathItems();
			Database2.getInstance(this).deleteRideData();
			Database2.getInstance(this).deleteDriverCurrentLocation();
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
				FlurryEventLogger.event(FlurryEventNames.RIDE_ACCEPTED);

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


				Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, FirebaseInstanceId.getInstance().getToken(), Data.SERVER_URL);

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


	public void acceptRequestFunc(final CustomerInfo customerInfo) {
		try {
			if (1 != customerInfo.getIsPooled()
					&& 1 != customerInfo.getIsDelivery()
					&& 1 != customerInfo.getIsDeliveryPool()
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
						driverPassengerInfoRl.setVisibility(View.VISIBLE);
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
					GCMIntentService.stopRing(true, activity);
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
						GCMIntentService.stopRing(true, getApplicationContext());
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
				GCMIntentService.stopRing(true, getApplicationContext());
				driverRejectRequestAsync(HomeActivity.this, customerInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setBidForEngagementAPI(final CustomerInfo customerInfo, final double bidValue) {

		if (AppStatus.getInstance(activity).isOnline(activity)) {

			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
			params.put(Constants.KEY_BID_VALUE, String.valueOf(bidValue));
			HomeUtil.putDefaultParams(params);
			DialogPopup.showLoadingDialog(this, getStringText(R.string.loading));

			RestClient.getApiServices().setBidForEngagement(params, new retrofit.Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
							HomeActivity.logoutUser(activity, null);
						} else if(flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
//							DialogPopup.alertPopup(activity, "", jObj.getString(Constants.KEY_MESSAGE));
							customerInfo.setBidPlaced(1);
							customerInfo.setBidValue(bidValue);
							driverRequestListAdapter.notifyDataSetChanged();
							GCMIntentService.clearNotifications(getApplicationContext());
							GCMIntentService.stopRing(false, HomeActivity.this);
						} else {
							DialogPopup.alertPopup(activity, "", JSONParser.getServerMessage(jObj));
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					DialogPopup.dismissLoadingDialog();
				}
			});
		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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

	public void apiSendRingCountData() {
		long ringFrequency = Prefs.with(HomeActivity.this).getLong(SPLabels.RING_COUNT_FREQUENCY, 0);
		if (ringFrequency > 0
				&& System.currentTimeMillis() - Prefs.with(HomeActivity.this).getLong(SPLabels.SEND_RING_COUNT_FREQUENCY, 0) > ringFrequency) {
			new ApiSendRingCountData(this, new ApiSendRingCountData.Callback() {
				@Override
				public void onSuccess() {
					Database2.getInstance(activity).deleteRingData();
					Prefs.with(activity).save(SPLabels.SEND_RING_COUNT_FREQUENCY, System.currentTimeMillis());
				}
			}).ringCountData();
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
		if(isTourBtnClicked) {
			isTourBtnClicked = false;
			isTourFlag = true;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
			}
		});
		if(isTourFlag){
			if(toggleDelivery){
				DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.turn_jugnooo_for_training, getString(R.string.appname)));
				dismissLoadingFromBackground();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (toggleDelivery) {
							Data.userData.setDeliveryAvailable(jugnooOnFlag);
						} else {
							Data.userData.autosAvailable = jugnooOnFlag;
						}
						changeJugnooONUIAndInitService();
						if (jugnooOnFlag == 1) {
							AGPSRefresh.softRefreshGpsData(HomeActivity.this);
							isJugnooOnTraining = true;
						} else {
							Intent intent1 = new Intent(HomeActivity.this, FetchDataUsageService.class);
							intent1.putExtra("task_id", "2");
							HomeActivity.this.startService(intent1);
							isJugnooOnTraining = false;
						}
						nudgeJugnooOnOff(latLng.latitude, latLng.longitude);
						resetSharedPrefs();
						if (jugnooOnFlag == 1) {
							showDialogFromBackgroundWithListener(getResources().getString(R.string.request_autos));
						}
						dismissLoadingFromBackground();
					}
				}).start();
			}
		} else {

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HashMap<String, String> params = new HashMap<String, String>();

						params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
						params.put("business_id", "1");
						HomeUtil.putDefaultParams(params);
						if (toggleDelivery) {
							params.put(KEY_DELIVERY_FLAG, "" + jugnooOnFlag);
							if (Data.userData.autosAvailable == 1 && jugnooOnFlag == 0 && myLocation != null) {
								params.put(KEY_LATITUDE, "" + myLocation.getLatitude());
								params.put(KEY_LONGITUDE, "" + myLocation.getLongitude());
							} else {
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
						final String message = JSONParser.getServerMessage(jObj);
						if (jObj.has(KEY_FLAG)) {
							int flag = jObj.getInt(KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if (toggleDelivery) {
									Data.userData.setDeliveryAvailable(jugnooOnFlag);
								} else {
									Data.userData.autosAvailable = jugnooOnFlag;
								}
								changeJugnooONUIAndInitService();
								if (jugnooOnFlag == 1) {
									AGPSRefresh.softRefreshGpsData(HomeActivity.this);
								} else {
									Intent intent1 = new Intent(HomeActivity.this, FetchDataUsageService.class);
									intent1.putExtra("task_id", "2");
									HomeActivity.this.startService(intent1);
								}
								nudgeJugnooOnOff(latLng.latitude, latLng.longitude);
								resetSharedPrefs();
								showDialogFromBackground(message);
							} else if(ApiResponseFlags.TNC_NOT_ACCEPTED.getOrdinal()==flag){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										DialogPopup.alertPopupWithListener(activity,"",message, new OnClickListener() {
											@Override
											public void onClick(View v) {
												DialogPopup.dismissLoadingDialog();
												Intent intent = new Intent(HomeActivity.this,HelpActivity.class);
												intent.putExtra(Constants.ASK_USER_CONFIRMATION,true);
												startActivityForResult(intent, REQUEST_CODE_TERMS_ACCEPT);
												overridePendingTransition(R.anim.right_in, R.anim.right_out);
											}
										});

									}
								});



							}else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
								Intent intent = new Intent(HomeActivity.this, DriverDocumentActivity.class);
								intent.putExtra("access_token", Data.userData.accessToken);
								intent.putExtra("in_side", true);
								intent.putExtra("doc_required", 0);
								startActivity(intent);
								overridePendingTransition(R.anim.right_in, R.anim.right_out);
							} else {
								showDialogFromBackground(message);
							}
						} else {
							showDialogFromBackground(message);
						}

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
					HomeUtil.putDefaultParams(params);

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

	public void showDialogFromPush(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				DialogPopup.alertPopup(HomeActivity.this, "", message);
			}
		});
	}

	public void showDialogFromBackgroundWithListener(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.dismissLoadingDialog();
				Crouton.cancelAllCroutons();
				DialogPopup.alertPopupWithListenerTopBar(HomeActivity.this, "", message, new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							if (isTourFlag && Data.userData.autosAvailable == 1) {
								isTourBtnClicked = false;
								//Crouton.cancelAllCroutons();
								handleTourView(isTourFlag, getString(R.string.tutorial_your_location) + "\n" + getString(R.string.tutorial_wait_for_customer));
								createTourNotification();
							} else {
								handleTourView(false, "");
							}
							drawerLayout.closeDrawer(GravityCompat.START);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						handleTourView(false, "");
					}
				}, isTourFlag, getString(R.string.tutorial_tap_ok));
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

			int menuOptionVisibility = Prefs.with(HomeActivity.this).getInt(SPLabels.MENU_OPTION_VISIBILITY, 0);
			if(menuOptionVisibility == 1){
				relativeLayoutDeliveryOn.setVisibility(View.GONE);
			} else if(menuOptionVisibility == 2){
				relativeLayoutAutosOn.setVisibility(View.GONE);
			} else if(menuOptionVisibility == 3){
				relativeLayoutDeliveryOn.setVisibility(View.VISIBLE);
				relativeLayoutAutosOn.setVisibility(View.VISIBLE);
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
							imageViewAutosOnToggle.setImageResource(R.drawable.toggle_on_v2);
							textViewAutosOn.setText(getString(R.string.jugnoo_on, getString(R.string.appname)));
							textViewAutosOn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

						} else {
							imageViewAutosOnToggle.setImageResource(R.drawable.toggle_off_v2);
							textViewAutosOn.setText(getString(R.string.jugnoo_off, getString(R.string.appname)));
							relativeLayoutLastRideEarning.setVisibility(View.GONE);
							textViewAutosOn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
						}

						if (1 == Data.userData.sharingAvailable) {
							imageViewSharingOnToggle.setImageResource(R.drawable.toggle_on_v2);
						} else {
							imageViewSharingOnToggle.setImageResource(R.drawable.toggle_off_v2);
						}

						if (1 == Data.userData.getDeliveryAvailable()) {
							imageViewDeliveryOnToggle.setImageResource(R.drawable.toggle_on_v2);
							textViewDeliveryOn.setText(getResources().getString(R.string.delivery_on));

						} else {
							imageViewDeliveryOnToggle.setImageResource(R.drawable.toggle_off_v2);
							textViewDeliveryOn.setText(getResources().getString(R.string.delivery_off));

						}

						if (!checkIfDriverOnline()) {
							if (DriverScreenMode.D_INITIAL == driverScreenMode) {
								setDriverServiceRunOnOnlineBasis();
								jugnooOffText.setVisibility(View.VISIBLE);
								linearLayoutJugnooOff.setVisibility(View.VISIBLE);
								if(!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_MESSAGE,""))){
									textViewDocText.setVisibility(View.VISIBLE);
									buttonUploadOnInitial.setVisibility(View.VISIBLE);
									textViewDocText.setText(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_MESSAGE,""));
									if(!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_DAYS_LEFT,""))){
										textViewDocDayText.setVisibility(View.VISIBLE);
										textViewDocDayText.setText(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_DAYS_LEFT,""));
									}
								} else {
									textViewDocText.setVisibility(View.GONE);
									textViewDocDayText.setVisibility(View.GONE);
									buttonUploadOnInitial.setVisibility(View.GONE);
								}

								Prefs.with(HomeActivity.this).save(Constants.IS_OFFLINE, 1);
								if(Prefs.with(HomeActivity.this).getInt(Constants.UPDATE_LOCATION_OFFLINE, 0) == 0) {
									stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
								} else {
									stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
									startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
								}
								GCMIntentService.clearNotifications(HomeActivity.this);
								GCMIntentService.stopRing(true, HomeActivity.this);

								if (map != null) {
									map.clear();
								}
							}
						} else {
							Prefs.with(HomeActivity.this).save(Constants.IS_OFFLINE, 0);
							if (DriverScreenMode.D_INITIAL == driverScreenMode) {
								Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
								setDriverServiceRunOnOnlineBasis();
								linearLayoutJugnooOff.setVisibility(View.GONE);
								jugnooOffText.setVisibility(View.GONE);
								fetchHeatMapData(HomeActivity.this);
								stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
								startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
							}
						}


						updateReceiveRequestsFlag();

						updateDriverRequestsAccAvailaviblity();

						if (Data.getAssignedCustomerInfosListForStatus(
								EngagementStatus.REQUESTED.getOrdinal()).size() == 0) {
							GCMIntentService.clearNotifications(HomeActivity.this);
							GCMIntentService.stopRing(true, HomeActivity.this);
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
		if (checkIfDriverOnline() || Prefs.with(HomeActivity.this).getInt(Constants.UPDATE_LOCATION_OFFLINE, 0) ==1) {
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
				firebaseScreenEvent(FirebaseEvents.LOCATION_BUTTON);
				boolean state = false;
				if(Data.getCurrentCustomerInfo() != null){
					if( Data.getCurrentCustomerInfo().getFalseDeliveries() ==1 && DriverScreenMode.D_IN_RIDE == driverScreenMode){
						state = true;
					}
				}

				if(DriverScreenMode.D_INITIAL == driverScreenMode || state) {
					if (map.getCameraPosition().zoom < 12) {
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12), MAP_ANIMATION_TIME, null);
					} else if (map.getCameraPosition().zoom < 15) {
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15), MAP_ANIMATION_TIME, null);
					} else {
						map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), MAP_ANIMATION_TIME, null);
					}
				} else if(DriverScreenMode.D_IN_RIDE == driverScreenMode) {
					inRideZoom();
				} else {
					arrivedOrStartStateZoom();
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
				firebaseScreenEvent(FirebaseEvents.NAVIGATE_BUTTON);
				if (myLocation != null) {
					LatLng latLng = null;
					CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
					if(DriverScreenMode.D_IN_RIDE == driverScreenMode
							&& customerInfo.getIsDelivery() != 1
							&& customerInfo.getDropLatLng() != null){
						latLng = customerInfo.getDropLatLng();
						FlurryEventLogger.event(FlurryEventNames.RIDE_STARTED_NAVIGATE_BUTTON);
					}
					else if(DriverScreenMode.D_ARRIVED == driverScreenMode
								|| DriverScreenMode.D_START_RIDE == driverScreenMode){
						latLng = customerInfo.getRequestlLatLng();
						FlurryEventLogger.event(FlurryEventNames.RIDE_ACCEPTED_NAVIGATE_BUTTON);
					} else if(DriverScreenMode.D_IN_RIDE == driverScreenMode
							&& customerInfo.getIsDelivery() == 1){
						int index = deliveryListHorizontal.getCurrentItem();
						latLng = customerInfo.getDeliveryInfos().get(index).getLatLng();
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
			if(Prefs.with(this).getInt(Constants.DRIVER_CREDITS, 0) == 1
					&& Data.userData.creditsEarned!=null){
				tvCredits.setVisibility(View.VISIBLE);
				tvCredits.setText(Utils.formatCurrencyValue(Data.userData.getCurrency(),Data.userData.creditsEarned) + " "
						+ getString(R.string.credits));
			}else{
				tvCredits.setVisibility(View.GONE);
			}
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
				textViewTitleBarDEI.setText(getResources().getString(R.string.appname));
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
			try {
				if(map != null &&
						(mode == DriverScreenMode.D_ARRIVED
						|| mode == DriverScreenMode.D_START_RIDE
						|| mode == DriverScreenMode.D_IN_RIDE)) {
					map.clear();
					ArrayList<CustomerInfo> customerInfosList;

					if(Data.getCurrentCustomerInfo().getIsDeliveryPool() == 1) {
						if(mode == DriverScreenMode.D_START_RIDE){
							sortCustomerState = false;
						}
						customerInfosList = setAttachedDeliveryPoolMarkers(sortCustomerState);
					} else {
						customerInfosList = setAttachedCustomerMarkers(sortCustomerState);
					}

					if (customerInfosList.size() > 0 && sortCustomerState ) {
						Data.setCurrentEngagementId(String.valueOf(customerInfosList.get(0).getEngagementId()));
					} else {
						sortCustomerState = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			driverScreenMode = Data.getCurrentState();
			mode = driverScreenMode;

			final CustomerInfo customerInfo = Data.getCurrentCustomerInfo();

			initializeFusedLocationFetchers();

			if (mode == DriverScreenMode.D_RIDE_END) {
				if (endRideData != null) {
					mapLayout.setVisibility(View.GONE);
					endRideReviewRl.setVisibility(View.VISIBLE);
					scrollViewEndRide.smoothScrollTo(0, 0);

					double totalDistanceInKm = Math.abs(customerInfo.getTotalDistance(customerRideDataGlobal
							.getDistance(HomeActivity.this), HomeActivity.this) * UserData.getDistanceUnitFactor(this));
					String kmsStr = "";

					try {
						if ( customerInfo.getIsDelivery()==1) {

							if(fixDeliveryDistance > -1) {
								totalDistanceInKm = fixDeliveryDistance;
							}

							if(fixedDeliveryWaitTime > -1){
								waitTime = String.valueOf(decimalFormatNoDecimal.format(fixedDeliveryWaitTime));
								reviewWaitTimeRl.setVisibility(View.VISIBLE);
							}
						}
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}

					kmsStr = Utils.getDistanceUnit(UserData.getDistanceUnit(this));

					reviewDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
					reviewWaitValue.setText(waitTime + " "+ getResources().getString(R.string.min));
					reviewRideTimeValue.setText(rideTime + " "+ getResources().getString(R.string.min));
					reviewFareValue.setText(Utils.formatCurrencyValue(endRideData.getCurrency(), totalFare));


					if(customerInfo.getIsDelivery() == 1){
						jugnooRideOverText.setText(getResources().getString(R.string.total_fare));
						takeFareText.setText(Utils.formatCurrencyValue(endRideData.getCurrency(),endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.VISIBLE);
						linearLayoutEndDelivery.setVisibility(View.VISIBLE);
						textViewEndRideCustomerName.setVisibility(View.GONE);
						textViewDeliveryIsOver.setText(getResources().getString(R.string.delivery_is_over));

						int totalDelivered = 0;
						int totalUndelivered = 0;
						for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
							if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
								totalDelivered++;
							} else if(deliveryInfo.getStatus() != DeliveryStatus.RETURN.getOrdinal()){
								totalUndelivered++;
							}
						}
						textViewOrdersDeliveredValue.setText(String.valueOf(totalDelivered));
						textViewOrdersReturnedValue.setText(String.valueOf(totalUndelivered));
						textViewRateYourCustomer.setText(getResources().getString(R.string.rate_your_vendor));
					}
					else if(customerInfo.getIsPooled() == 1){
						jugnooRideOverText.setText(getResources().getString(R.string.collect_cash));
						takeFareText.setText(Utils.formatCurrencyValue(endRideData.getCurrency(),endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.VISIBLE);
						linearLayoutEndDelivery.setVisibility(View.GONE);
						textViewEndRideCustomerName.setVisibility(View.VISIBLE);
						textViewEndRideCustomerName.setText(customerInfo.getName());
						textViewDeliveryIsOver.setText(getResources().getString(R.string.reached_destination));

						textViewRateYourCustomer.setText(getResources().getString(R.string.Rate_Your_Customer));
					}
					else{
						jugnooRideOverText.setText(getString(R.string.jugnoo_ride_over, getString(R.string.appname)));
						takeFareText.setText(getResources().getString(R.string.take_cash)+" "
								+Utils.formatCurrencyValue(endRideData.getCurrency(),endRideData.toPay));
						relativeLayoutDeliveryOver.setVisibility(View.GONE);
						linearLayoutEndDelivery.setVisibility(View.GONE);
						textViewEndRideCustomerName.setVisibility(View.GONE);
						textViewRateYourCustomer.setText(getResources().getString(R.string.Rate_Your_Customer));
					}

					endRideInfoRl.setVisibility(View.VISIBLE);
					reviewFareValue.setText(Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(), endRideData.toPay));


					reviewReachedDistanceRl.setVisibility(View.VISIBLE);
					linearLayoutMeterFare.setVisibility(View.GONE);
					relativeLayoutRateCustomer.setVisibility(View.VISIBLE);
					ratingBarFeedback.setVisibility(View.VISIBLE);
					reviewSkipBtn.setVisibility(View.GONE);
					ratingBarFeedback.setRating(0);

					try {
						if (customerInfo.getWaitingChargesApplicable() == 1) {
							reviewWaitTimeRl.setVisibility(View.VISIBLE);
						} else {
							reviewWaitTimeRl.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						reviewWaitTimeRl.setVisibility(View.GONE);
					}

					relativeLayoutEndRideLuggageCount.setVisibility(View.GONE);
					polylineOptionsCustomersPath = null;
					polylineOptionsDelivery = null;

				} else {
					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
				}
			} else if (mode == DriverScreenMode.D_BEFORE_END_OPTIONS) {
				mapLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.VISIBLE);
				scrollViewEndRide.smoothScrollTo(0, 0);
				editTextEnterMeterFare.setText("");

				endRideInfoRl.setVisibility(View.GONE);
				reviewReachedDistanceRl.setVisibility(View.GONE);
				linearLayoutMeterFare.setVisibility(View.VISIBLE);
				relativeLayoutRateCustomer.setVisibility(View.GONE);
				ratingBarFeedback.setVisibility(View.GONE);
				reviewSkipBtn.setVisibility(View.GONE);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reviewSubmitBtn.getLayoutParams();
				layoutParams.setMargins(0, 0, 0, (int) (270 * ASSL.Yscale()));
				layoutParams.setMarginStart(0);
				layoutParams.setMarginEnd(0);
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
			try {
				startRideAlarmHandler.removeCallbacks(startRideAlarmRunnalble);
				stopService(new Intent(HomeActivity.this, StartRideLocationUpdateService.class));
//				SoundMediaPlayer.stopSound();
			} catch (Exception e) {
				e.printStackTrace();
			}


			try {
				ArrayList<CustomerInfo> customerEngCount = Data.getAssignedCustomerInfosListForEngagedStatus();
				if (customerEngCount.size() > 1 ) {
					changeButton.setVisibility(View.VISIBLE);
				} else {
					changeButton.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			relativeLayoutLastRideEarning.setVisibility(View.GONE);
			relativeLayoutEnterDestination.setVisibility(View.GONE);
			topRlOuter.setVisibility(View.VISIBLE);
			customerSwitcher.setCallButton();

			try {
				showChatButton();
				if(customerInfo.getIsDelivery() == 1 || customerInfo.getIsDeliveryPool() == 1 ) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
//							if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
//								if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible() && deliveryInfolistFragVisibility){
//									deliveryInfolistFragVisibility =false;
//									onBackPressed();
//								}
//							}
						}
					}, 500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			switch (mode) {

				case D_INITIAL:
					updateDriverServiceFast("no");
					setPannelVisibility(true);
					getInfoTilesAsync(HomeActivity.this);

					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							setPannelVisibility(true);
						}
					}, 2000);

					driverInitialLayout.setVisibility(View.VISIBLE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					driverInformationBtn.setVisibility(View.GONE);
					Prefs.with(HomeActivity.this).save(KEY_CHAT_COUNT, 0);
					startMeteringService();
					setDriverServiceRunOnOnlineBasis();
					if (checkIfDriverOnline()) {
						linearLayoutJugnooOff.setVisibility(View.GONE);
						Prefs.with(HomeActivity.this).save(Constants.IS_OFFLINE, 0);
					}

					if(Prefs.with(HomeActivity.this).getInt(Constants.UPDATE_LOCATION_OFFLINE, 0) ==1 || checkIfDriverOnline()){
						startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					}

					try {
						polylineOptionsCustomersPath = null;
						polylineOptionsDelivery = null;
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (map != null) {
						map.clear();
						drawHeatMapData(heatMapResponseGlobal);
					}

					showDriverEarning();
					showRefreshUSLBar();
					showLowBatteryAlert(true);

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


					cancelTimerPathRerouting();
					Prefs.with(this).save(Constants.START_RIDE_ALARM_SERVICE_STATUS, false);
					try {


						if(Prefs.with(HomeActivity.this).getLong(SPLabels.HEAT_MAP_REFRESH_FREQUENCY, 0) >0) {
							heatMapHandler.postDelayed(heatMapRunnalble, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Data.nextPickupLatLng = null;
					Data.nextCustomerName = null;
					Prefs.with(HomeActivity.this).save(SPLabels.DELIVERY_IN_PROGRESS, -1);

					if (getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null) {
						if (getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible()) {
							deliveryInfolistFragVisibility = false;
//							onBackPressed();
							relativeLayoutContainer.setVisibility(View.GONE);
							getSupportFragmentManager().beginTransaction()
									.remove(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()))
									.commitAllowingStateLoss();
						}
					}
					break;


				case D_REQUEST_ACCEPT:

					updateDriverServiceFast("no");
					inRideZoom();
					setPannelVisibility(false);
					setDriverServiceRunOnOnlineBasis();
					if (!Utils.isServiceRunning(getApplicationContext(), DriverLocationUpdateService.class)) {
						startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					}

					if (map != null) {
						map.clear();
					}

					if (getOpenedCustomerInfo() != null) {
						if(map != null){
							addCustomerPickupMarker(map, getOpenedCustomerInfo(), getOpenedCustomerInfo().getRequestlLatLng());
						}
					}

					topRlOuter.setVisibility(View.GONE);
					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.VISIBLE);
					driverEngagedLayout.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);





					cancelTimerPathRerouting();
					Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, false);

//					if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
//						if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible()){
//							deliveryInfolistFragVisibility =false;
//							onBackPressed();
//						}
//					}

					break;


				case D_ARRIVED:

					tvDropAddressToggleView.setVisibility(View.GONE);
					bDropAddressToggle.setVisibility(View.GONE);
					updateDriverServiceFast("yes");
					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));

					if (map != null) {
						if(currentCustomerLocMarker != null){
							currentCustomerLocMarker.remove();
							currentCustomerLocMarker = null;
						}

						if(customerInfo.getIsPooled() != 1) {
							currentCustomerLocMarker = addCustomerCurrentLocationMarker(map, Data.getCurrentCustomerInfo());
						}

					}

					if(customerInfo.getIsDelivery() == 1 || customerInfo.getIsDeliveryPool() == 1 ) {
						setTextViewRideInstructions();
					} else {
						setCustomerInstruction(customerInfo);
					}

					topRlOuter.setVisibility(View.GONE);
					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					setPannelVisibility(false);
					driverStartRideMainRl.setVisibility(View.VISIBLE);
					driverInRideMainRl.setVisibility(View.GONE);
					driverStartRideBtn.setVisibility(View.GONE);
					buttonMarkArrived.setVisibility(View.VISIBLE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					Prefs.with(HomeActivity.this).save(Constants.KEY_PICKUP_LATITUDE_ALARM, String.valueOf(Data.getCurrentCustomerInfo().getRequestlLatLng().latitude));
					Prefs.with(HomeActivity.this).save(Constants.KEY_PICKUP_LONGITUDE_ALARM, String.valueOf(Data.getCurrentCustomerInfo().getRequestlLatLng().longitude));
					Prefs.with(HomeActivity.this).save(Constants.KEY_CURRENT_LATITUDE_ALARM, String.valueOf(Data.getCurrentCustomerInfo().getCurrentLatLng().latitude));
					Prefs.with(HomeActivity.this).save(Constants.KEY_CURRENT_LONGITUDE_ALARM, String.valueOf(Data.getCurrentCustomerInfo().getCurrentLatLng().longitude));


					try {
						ArrayList<CustomerInfo> customerEnfagementInfos1 = Data.getAssignedCustomerInfosListForEngagedStatus();

						if(customerInfo.getIsPooled() ==1){
							if(Database2.getInstance(HomeActivity.this).getPoolDiscountFlag(customerInfo.getEngagementId())!=1) {
								Database2.getInstance(HomeActivity.this).deletePoolDiscountFlag(customerInfo.getEngagementId());
								Database2.getInstance(HomeActivity.this).insertPoolDiscountFlag(customerInfo.getEngagementId(), 0);
							}
							if(customerEnfagementInfos1.size() >1){
								for (int i = 0; i < customerEnfagementInfos1.size(); i++) {
									Database2.getInstance(HomeActivity.this).updatePoolDiscountFlag(customerEnfagementInfos1.get(i).getEngagementId(), 1);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

//					Utils.setDrawableColor(buttonMarkArrived, customerInfo.getColor(),
//							getResources().getColor(R.color.themeColor));

					if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
						if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible()){
							deliveryInfolistFragVisibility =false;
//							onBackPressed();
							relativeLayoutContainer.setVisibility(View.GONE);
							getSupportFragmentManager().beginTransaction()
									.remove(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()))
									.commitAllowingStateLoss();
						}
					}

					setEtaTimerVisibility(customerInfo);
					startTimerPathRerouting();
					updateCustomers();


					break;

				case D_START_RIDE:

					tvDropAddressToggleView.setVisibility(View.GONE);
					bDropAddressToggle.setVisibility(View.GONE);
					updateDriverServiceFast("yes");
					inRideZoom();
					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					setPannelVisibility(false);
					if (map != null) {
						buttonDriverNavigationSetVisibility(View.GONE);
						if(polylineCustomersPath != null){
							polylineCustomersPath.remove();
						}
						polylineCustomersPath = null;
					}
					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));
					topRlOuter.setVisibility(View.GONE);
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
					if(currentCustomerLocMarker != null){
						currentCustomerLocMarker.remove();
						currentCustomerLocMarker = null;
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
//					Utils.setDrawableColor(driverStartRideBtn, customerInfo.getColor(),
//							getResources().getColor(R.color.themeColor));


					try {
						ArrayList<CustomerInfo> customerEnfagementInfos2 = Data.getAssignedCustomerInfosListForEngagedStatus();

						if(customerInfo.getIsPooled() ==1){
							if(Database2.getInstance(HomeActivity.this).getPoolDiscountFlag(customerInfo.getEngagementId())!=1) {
								Database2.getInstance(HomeActivity.this).deletePoolDiscountFlag(customerInfo.getEngagementId());
								Database2.getInstance(HomeActivity.this).insertPoolDiscountFlag(customerInfo.getEngagementId(), 0);
							}
							if(customerEnfagementInfos2.size() >1){
								for (int i = 0; i < customerEnfagementInfos2.size(); i++) {
									Database2.getInstance(HomeActivity.this).updatePoolDiscountFlag(customerEnfagementInfos2.get(i).getEngagementId(), 1);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					if(customerInfo.getIsDelivery() != 1 && customerInfo.getIsPooled() != 1) {
//						playStartRideAlarm = true;
//						startRideAlarmHandler.postDelayed(startRideAlarmRunnalble, 5000);
						boolean startRideAlarmStatus = Prefs.with(this).getBoolean(Constants.START_RIDE_ALARM_SERVICE_STATUS, false);
						if(!Utils.isServiceRunning(HomeActivity.this, StartRideLocationUpdateService.class) && !startRideAlarmStatus) {
							Prefs.with(this).save(Constants.FLAG_REACHED_PICKUP, false);
							Prefs.with(this).save(Constants.PLAY_START_RIDE_ALARM, true);
							Prefs.with(this).save(Constants.PLAY_START_RIDE_ALARM_FINALLY, false);
							Intent intent1 = new Intent(HomeActivity.this, StartRideLocationUpdateService.class);
							HomeActivity.this.startService(intent1);
							Prefs.with(this).save(Constants.START_RIDE_ALARM_SERVICE_STATUS, true);
						} else{
							stopService(new Intent(HomeActivity.this, StartRideLocationUpdateService.class));
							Intent intent1 = new Intent(HomeActivity.this, StartRideLocationUpdateService.class);
							HomeActivity.this.startService(intent1);
						}
					}

					startTimerPathRerouting();
					if (customerInfo.getIsDelivery() == 1 || customerInfo.getIsDeliveryPool() == 1) {
						setTextViewRideInstructions();
						try {
							DeliveryInfoInRideDetails deliveryInfoInRideDetails = new DeliveryInfoInRideDetails();

							if (customerInfo.getDeliveryInfos() != null) {
								deliveryInfoInRideDetails.getPickupData().setName(customerInfo.getName());
								deliveryInfoInRideDetails.getPickupData().setPhone(customerInfo.getPhoneNumber());
								deliveryInfoInRideDetails.getPickupData().setCashToCollect(Double.valueOf(customerInfo.getCashOnDelivery()));
								deliveryInfoInRideDetails.getPickupData().setLoadingStatus(customerInfo.getLoadingStatus());
								List<DeliveryInfoInRideDetails.DeliveryDatum> deliveryData = new ArrayList<>();
								for (int i = 0; i < customerInfo.getDeliveryInfos().size(); i++) {
									DeliveryInfoInRideDetails.DeliveryDatum deliveryDatum = new DeliveryInfoInRideDetails.DeliveryDatum();
									DeliveryInfo deliveryInfo = customerInfo.getDeliveryInfos().get(i);
									deliveryDatum.setName(deliveryInfo.getCustomerName());
									deliveryDatum.setAddress(deliveryInfo.getDeliveryAddress());
									deliveryData.add(i, deliveryDatum);
								}
								relativeLayoutContainer.setVisibility(View.VISIBLE);
								deliveryInfolistFragVisibility = true;
								deliveryInfoInRideDetails.setDeliveryData(deliveryData);
								customerInfo.setDeliveryInfoInRideDetails(deliveryInfoInRideDetails);
								deliveryInfoInRideDetails.setCurrencyUnit(customerInfo.getCurrencyUnit());
								getTransactionUtils().openDeliveryInfoInRideFragment(HomeActivity.this,
										getRelativeLayoutContainer(), deliveryInfoInRideDetails);
								overridePendingTransition(R.anim.right_in, R.anim.right_out);
							} else if (customerInfo.getDeliveryInfoInRideDetails() != null) {
								relativeLayoutContainer.setVisibility(View.VISIBLE);
								deliveryInfolistFragVisibility = true;
								customerInfo.getDeliveryInfoInRideDetails().setCurrencyUnit(customerInfo.getCurrencyUnit());
								getTransactionUtils().openDeliveryInfoInRideFragment(HomeActivity.this,
										getRelativeLayoutContainer(), customerInfo.getDeliveryInfoInRideDetails());
								overridePendingTransition(R.anim.right_in, R.anim.right_out);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						setCustomerInstruction(customerInfo);
					}
					updateCustomers();
					break;

				case D_IN_RIDE:

					tvDropAddressToggleView.setVisibility(View.GONE);
					bDropAddressToggle.setVisibility(View.GONE);
					updateDriverServiceFast("no");
					SoundMediaPlayer.stopSound();
					Database2.getInstance(HomeActivity.this).updateDriverServiceRun(Database2.NO);
					stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					stopService(new Intent(HomeActivity.this, StartRideLocationUpdateService.class));
					Prefs.with(this).save(Constants.START_RIDE_ALARM_SERVICE_STATUS, false);
					setPannelVisibility(false);
					rideTimeChronometer.eclipsedTime = customerInfo.getElapsedRideTime(HomeActivity.this);
					rideTimeChronometer.setText(Utils.getChronoTimeFromMillis(rideTimeChronometer.eclipsedTime));
					startRideChronometer(customerInfo);
					setNevigationButtonVisibiltyDelivery(0);
					if(customerInfo.getIsPooled() != 1 && customerInfo.getIsDelivery() != 1) {
						relativeLayoutEnterDestination.setVisibility(View.VISIBLE);
					}

					if(customerInfo.forceEndDelivery == 1){
						try {
							DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
							final CustomerInfo finalCustomerInfo = customerInfo;
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									DialogPopup.dismissLoadingDialog();
									endRideGPSCorrection(finalCustomerInfo);
									deliveryInfoTabs.notifyDatasetchange(true);
								}
							}, 5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (map != null) {
						if(customerInfo.getIsDelivery() == 1) {
							if(customerInfo.getIsDeliveryPool() != 1){
								double slatitude = Double.parseDouble(Prefs.with(this).getString(Constants.SP_START_LATITUDE, "0"));
								double slongitude = Double.parseDouble(Prefs.with(this).getString(Constants.SP_START_LONGITUDE, "0"));
								addDropPinMarker(map, new LatLng(slatitude, slongitude), "P", 2);
							}
						} else {
							if(customerInfo.getIsPooled() != 1) {
								addStartMarker();
							}
						}
					}

					long waitTime = customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(HomeActivity.this), HomeActivity.this);
					updateDistanceFareTexts(customerInfo, customerInfo.getTotalDistance(customerRideDataGlobal
									.getDistance(HomeActivity.this), HomeActivity.this),
							rideTimeChronometer.eclipsedTime,
							waitTime);

					textViewEnterDestination.setText("");
					customerSwitcher.setCustomerData(Integer.parseInt(Data.getCurrentEngagementId()));
					topRlOuter.setVisibility(View.GONE);
					if(customerInfo.getIsDelivery() == 1){
						linearLayoutRide.setVisibility(View.GONE);
						deliveryListHorizontal.setVisibility(View.GONE);
						deliveryInfoTabs.render(customerInfo.getEngagementId(), customerInfo.getDeliveryInfos(), customerInfo.getFalseDeliveries(), customerInfo.getOrderId());
						deliveryInfoTabs.notifyDatasetchange(true);
					} else {
						linearLayoutRide.setVisibility(View.VISIBLE);
						deliveryListHorizontal.setVisibility(View.GONE);
					}

					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.VISIBLE);
					etaTimerRLayout.setVisibility(View.GONE);

					driverStartRideMainRl.setVisibility(View.GONE);
					driverInRideMainRl.setVisibility(View.VISIBLE);

					if(customerInfo.getIsDelivery() == 1 && customerInfo.getDeliveryInfos().size() > 1){
						linearLayoutRideValues.setVisibility(View.GONE);
//						ArrayList<CustomerInfo> customerEngCount = Data.getAssignedCustomerInfosListForEngagedStatus();
//						if(customerInfo.getIsDeliveryPool() == 1
//								&& Data.getAssignedCustomerInfosListForEngagedStatus().size() < 2){
//							changeButton.setVisibility(View.GONE);
//						}
//						if (customerEngCount.size() > 1) {
//							changeButton.setVisibility(View.VISIBLE);
//						} else {
//							changeButton.setVisibility(View.GONE);
//						}

					} else{
						linearLayoutRideValues.setVisibility(View.VISIBLE);
					}

					driverEndRideBtn.setText(getResources().getString(R.string.end_ride));

					try {
						if (customerInfo.getWaitingChargesApplicable() == 1) {
							driverWaitRl.setVisibility(View.GONE);
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

					if(customerInfo.getIsPooled() == 1){
						relativeLayoutItemHeader.setVisibility(View.VISIBLE);
					} else {
						relativeLayoutItemHeader.setVisibility(View.GONE);
					}

					setMakeDeliveryButtonVisibility();
					if(customerInfo.getIsDeliveryPool() !=1) {
						setDeliveryMarkers();
					}
					if(customerInfo.getIsDelivery() == 1 || customerInfo.getIsDeliveryPool() == 1 ) {
						setTextViewRideInstructions();
						if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
							if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible()){
								deliveryInfolistFragVisibility =false;
//								onBackPressed();
								relativeLayoutContainer.setVisibility(View.GONE);
								getSupportFragmentManager().beginTransaction()
										.remove(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()))
										.commitAllowingStateLoss();
							}
						}

					} else {
						setCustomerInstruction(customerInfo);
					}

					startTimerPathRerouting();
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);
					createPerfectRideMarker();
					updateCustomers();
					break;


				case D_BEFORE_END_OPTIONS:

					updateDriverServiceFast("no");
					topRlOuter.setVisibility(View.GONE);
					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					setPannelVisibility(false);
					cancelTimerPathRerouting();
					break;

				case D_RIDE_END:

					updateDriverServiceFast("no");
					setDriverServiceRunOnOnlineBasis();
					stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
					setPannelVisibility(false);
					GCMIntentService.clearNotifications(getApplicationContext());
					GCMIntentService.stopRing(true, HomeActivity.this);
					topRlOuter.setVisibility(View.GONE);
					driverInitialLayout.setVisibility(View.GONE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);
					perfectRidePassengerInfoRl.setVisibility(View.GONE);
					driverPassengerInfoRl.setVisibility(View.VISIBLE);

					cancelTimerPathRerouting();
					Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_RIDE_REGION_REQUEST_STATUS, false);
					if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
						if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible()){
							deliveryInfolistFragVisibility =false;
//							onBackPressed();
							relativeLayoutContainer.setVisibility(View.GONE);
							getSupportFragmentManager().beginTransaction()
									.remove(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()))
									.commitAllowingStateLoss();
						}
					}

					if(isTourFlag) {
						handleTourView(true, getString(R.string.tutorial_tap_done_finish));
					}
					break;

				default:
					driverInitialLayout.setVisibility(View.VISIBLE);
					driverRequestAcceptLayout.setVisibility(View.GONE);
					driverEngagedLayout.setVisibility(View.GONE);

					cancelTimerPathRerouting();

			}
			if(DriverScreenMode.D_INITIAL != mode){
				relativeLayoutRefreshUSLBar.setVisibility(View.GONE);
				relativeLayoutBatteryLow.setVisibility(View.GONE);

				relativeLayoutLastRideEarning.setVisibility(View.GONE);
				driverInformationBtn.setVisibility(View.GONE);
			}

			if(DriverScreenMode.D_IN_RIDE == mode || DriverScreenMode.D_ARRIVED == mode || DriverScreenMode.D_START_RIDE == mode){
				setInRideZoom();
			} else {
				map.setPadding(0, 0, 0, 0);
				inRideMapZoomHandler.removeCallbacks(inRideMapZoomRunnable);
			}


			map.setPadding(0, 0, 0, 0);
			showAllRideRequestsOnMap();

			if(DriverScreenMode.D_INITIAL == mode ){
				slidingUpPanelLayout.setPanelHeight((int) (140f * ASSL.Yscale()));
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			} else {
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			}

			if(rideCancelledByCustomer){
				DialogPopup.dialogBannerNewWithCancelListener(HomeActivity.this, cancelationMessage, new OnClickListener() {
					@Override
					public void onClick(View v) {
						SoundMediaPlayer.stopSound();
					}
				}, 7000);
			}
			rideCancelledByCustomer = false;

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

			try {
				ArrayList<CustomerInfo> customerEnfagementInfos1 = Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal());
				if(customerEnfagementInfos1.size() > 0){
					MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, DriverScreenMode.D_IN_RIDE);
				} else{
					MeteringService.gpsInstance(this).saveDriverScreenModeMetering(this, mode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Handler startRideAlarmHandler = new Handler();
	boolean reachedDestination = false;
	boolean playStartRideAlarmFinal = false;
	Runnable startRideAlarmRunnalble = new Runnable() {
		@Override
		public void run() {
			try {
				LatLng driverONPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				if (reachedDestination && playStartRideAlarmFinal
						&& ((int)MapUtils.distance(driverONPickupLatLng, Data.getCurrentCustomerInfo().getRequestlLatLng()) >  Prefs.with(HomeActivity.this).getInt(SPLabels.START_RIDE_ALERT_RADIUS_FINAL, 400))) {
					DialogPopup.dialogNewBanner(HomeActivity.this, getResources().getString(R.string.start_ride_alert), 7000);
					SoundMediaPlayer.startSound(HomeActivity.this, R.raw.start_ride_accept_beep, 5, true);
					playStartRideAlarmFinal = false;
				}
				if (reachedDestination && playStartRideAlarm
						&& ((int)MapUtils.distance(driverONPickupLatLng, Data.getCurrentCustomerInfo().getRequestlLatLng()) >  Prefs.with(HomeActivity.this).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200))) {
					DialogPopup.dialogNewBanner(HomeActivity.this, getResources().getString(R.string.start_ride_alert), 7000);
					SoundMediaPlayer.startSound(HomeActivity.this, R.raw.start_ride_accept_beep, 5, true);
					playStartRideAlarm = false;
					playStartRideAlarmFinal = true;
				}
				if (MapUtils.distance(driverONPickupLatLng, Data.getCurrentCustomerInfo().getRequestlLatLng()) < Prefs.with(HomeActivity.this).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200)) {
					reachedDestination = true;
				}
			} catch (Exception e) {

			}
			startRideAlarmHandler.postDelayed(startRideAlarmRunnalble, 2000);
		}
	};


	private void showChatButton() {
		try {
			final CustomerInfo customerInfoCheck = Data.getCurrentCustomerInfo();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if ((driverScreenMode == DriverScreenMode.D_ARRIVED || driverScreenMode == DriverScreenMode.D_START_RIDE)
								&& Prefs.with(HomeActivity.this).getInt(SPLabels.CHAT_ENABLED, 0) == 1
								&& customerInfoCheck.getIsDelivery() != 1 && customerInfoCheck.getIsPooled() != 1) {
							rlChatDriver.setVisibility(View.VISIBLE);
							if (Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 0) > 0) {
								tvChatCount.setVisibility(View.VISIBLE);
								tvChatCount.setText(String.valueOf(Prefs.with(HomeActivity.this).getInt(KEY_CHAT_COUNT, 0)));
							} else {
								tvChatCount.setVisibility(View.GONE);
								Prefs.with(HomeActivity.this).save(KEY_CHAT_COUNT, 0);
							}
						} else {
							rlChatDriver.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
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
		Log.d(TAG, "button visibility = "+visibility);
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
				if(getResources().getInteger(R.integer.show_driver_timer)==getResources().getInteger(R.integer.view_visible)) {
					etaTimerRLayout.setVisibility(View.VISIBLE);
				}
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

	public void setPannelVisibility(boolean state){

		if(state && DriverScreenMode.D_INITIAL == driverScreenMode && infoTileResponses.size() > 0){
			slidingUpPanelLayout.setPanelHeight((int) (140f * ASSL.Yscale()));
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		}else{
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(Utils.isBatteryChargingNew(this) == 1 || Utils.getBatteryPercentage(this) > 20){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}


		if (!checkIfUserDataNull()) {
			setUserData();

			initializeFusedLocationFetchers();

			if (UserMode.DRIVER == userMode) {
				buildTimeSettingsAlertDialog(this);
			}

		}

		try {
			if(driverRequestListAdapter.customerInfos.size() > 0){
				setPannelVisibility(false);
			} else {
				setPannelVisibility(true);
			}

			if(!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_MESSAGE,""))){
				textViewDocText.setVisibility(View.VISIBLE);
				buttonUploadOnInitial.setVisibility(View.VISIBLE);
				textViewDocText.setText(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_MESSAGE,""));
				if(!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_DAYS_LEFT,""))){
					textViewDocDayText.setVisibility(View.VISIBLE);
					textViewDocDayText.setText(Prefs.with(HomeActivity.this).getString(UPLOAD_DOCUMENT_DAYS_LEFT,""));
				}
			} else {
				textViewDocText.setVisibility(View.GONE);
				textViewDocDayText.setVisibility(View.GONE);
				buttonUploadOnInitial.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		showChatButton();
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

		if(DriverScreenMode.D_INITIAL == driverScreenMode) {
			getInfoTilesAsync(HomeActivity.this);
		}
	}


	@Override
	protected void onPause() {

//		GCMIntentService.clearNotifications(getApplicationContext());

		try{
//			Intent intent = new Intent(HomeActivity.this, GeanieView.class);
//			startService(intent);
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
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	public void saveCustomerRideDataInSP(CustomerInfo customerInfo){
		try{
			JSONObject jObj = new JSONObject(Prefs.with(this).getString(SP_CUSTOMER_RIDE_DATAS_OBJECT, EMPTY_OBJECT));
			if(EngagementStatus.ENDED.getOrdinal() != customerInfo.getStatus()) {
				JSONObject jc = new JSONObject();
				jc.put(KEY_DISTANCE, customerRideDataGlobal.getDistance(HomeActivity.this));
				jc.put(KEY_HAVERSINE_DISTANCE, customerRideDataGlobal.getHaversineDistance());
				jc.put(KEY_RIDE_TIME, System.currentTimeMillis());
				jc.put(KEY_WAIT_TIME, customerRideDataGlobal.getWaitTime(HomeActivity.this));
				jObj.put(String.valueOf(customerInfo.getEngagementId()), jc);
			} else {
				jObj.remove(String.valueOf(customerInfo.getEngagementId()));
			}
			Prefs.with(this).save(SP_CUSTOMER_RIDE_DATAS_OBJECT, jObj.toString());
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private AddSignatureFragment getSignatureFragment() {
		return (AddSignatureFragment) getSupportFragmentManager().findFragmentByTag(AddSignatureFragment.class.getName());
	}

	@Override
	public void onBackPressed() {
		try {
			boolean visible = false;
			if(getSupportFragmentManager().findFragmentByTag(AddSignatureFragment.class.getName()) != null){
				if(getSupportFragmentManager().findFragmentByTag(AddSignatureFragment.class.getName()).isVisible()){
					visible = true;
				}
			}

			if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
				if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()).isVisible() &&
						deliveryInfolistFragVisibility){
					return;
				}
			}

			if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
					!visible) {
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
			if(isTourFlag && tourResponseModel != null) {
                Intent intent = new Intent(HomeActivity.this, UpdateTutStatusService.class);
                String status = "2";
                if(driverScreenMode == DriverScreenMode.D_RIDE_END) {
                    status = "3";
                }
                Prefs.with(activity).save(SPLabels.PREF_TRAINING_ACCESS_TOKEN, Data.userData.accessToken);
				Prefs.with(activity).save(SPLabels.SET_DRIVER_TOUR_STATUS, status);
				Prefs.with(activity).save(SPLabels.PREF_TRAINING_ID, String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
                startService(intent);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cancelTimerPathRerouting();

			GCMIntentService.clearNotifications(HomeActivity.this);
			GCMIntentService.stopRing(true, HomeActivity.this);

			MeteringService.clearNotifications(HomeActivity.this);

			destroyFusedLocationFetchers();

			ASSL.closeActivity(drawerLayout);


			Database2.getInstance(this).close();
			unregisterReceiver(broadcastReceiver);
			unregisterReceiver(broadcastReceiverUSL);
			unregisterReceiver(broadcastReceiverLowBattery);
			unregisterReceiver(broadcastReceiverIsCharging);
			unregisterReceiver(broadcastReceiverCancelEndDeliveryPopup);

			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onDestroy();
	}


	private double getTotalFare(CustomerInfo customerInfo, double totalDistance, long elapsedTimeInMillis, long waitTimeInMillis, int invalidPool) {
		if(customerInfo.getReverseBidFare() != null){
			return customerInfo.getReverseBidFare().getFare();
		}
		if(customerInfo.getIsPooled() == 1 && customerInfo.getPoolFare() != null && invalidPool==0){
			return customerInfo.getPoolFare().getFare(HomeActivity.this, customerInfo.getEngagementId());
		}

		double totalDistanceInKm = Math.abs(totalDistance * UserData.getDistanceUnitFactor(this));

		double rideTimeInMin = Math.round(((double) elapsedTimeInMillis) / 60000.0d);
		double waitTimeInMin = Math.round(((double) waitTimeInMillis) / 60000.0d);

		if (customerInfo.getWaitingChargesApplicable() == 1) {
			rideTimeInMin = rideTimeInMin - waitTimeInMin;
		} else {
			waitTimeInMin = 0d;
		}

		return Data.fareStructure.calculateFare(totalDistanceInKm, rideTimeInMin, waitTimeInMin,
				JSONParser.isTagEnabled(activity, Constants.KEY_SHOW_TOLL_CHARGE) ? customerInfo.getTollFare() : 0);
	}

	public synchronized void updateDistanceFareTexts(CustomerInfo customerInfo, double distance, long elapsedTime, long waitTime) {
		try {
			double totalDistanceInKm = Math.abs(distance * UserData.getDistanceUnitFactor(this));

			driverIRDistanceValue.setText("" + decimalFormat.format(totalDistanceInKm) + " "+Utils.getDistanceUnit(UserData.getDistanceUnit(this)));
			driverWaitValue.setText(Utils.getChronoTimeFromMillis(waitTime));

			if (Data.fareStructure != null) {
				driverIRFareValue.setText(Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(),getTotalFare(customerInfo, distance,
						elapsedTime, waitTime, 0)));
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
				polylineOptions.width(ASSL.Xscale() * 8);
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

	public void setBarAddress(String address){
//		relativeLayoutDestination.setVisibility(View.VISIBLE);
		textViewEnterDestination.setText(address);
	}

	@Override
	public void onTextChange(String text) {

	}

	@Override
	public void onSearchPre() {

	}

	@Override
	public void onSearchPost() {

	}

	@Override
	public void onPlaceClick(SearchResultNew autoCompleteSearchResult) {

	}

	@Override
	public void onPlaceSearchPre() {

	}

	@Override
	public void onPlaceSearchPost(SearchResultNew searchResult) {

//		textViewEnterDestination.setText(searchResult.getAddress());
//		onDropLocationUpdated(String.valueOf(Data.getCurrentEngagementId()),
//				searchResult.getLatLng(), searchResult.getAddress());
		updateDropLatLng(HomeActivity.this, searchResult.getAddress(), searchResult.getLatLng());

	}

	@Override
	public void onPlaceSearchError() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			if(getSupportFragmentManager().getBackStackEntryCount() == 1){
				relativeLayoutContainer.setVisibility(View.GONE);
				super.onBackPressed();
			}
		}
	}

	@Override
	public void onPlaceSaved() {

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




	class ViewHolderDriverRequest {
		TextView textViewRequestName, textViewRequestAddress, textViewRequestDetails,
				textViewRequestTime, textViewRequestFareFactor, textViewDeliveryFare,
				textViewRequestDistance, textViewEstimatedFareValue, textViewEstimatedFare, textViewEstimatedDist, textViewDropPoint,
				textViewDropPoint1, textViewDropPoint2, textViewDropPoint3, textViewDropPointCount;
		Button buttonAcceptRide, buttonCancelRide;
		ImageView imageViewRequestType, imageViewDeliveryList;
		LinearLayout relative, linearLayoutDeliveryParams;
		RelativeLayout relativeLayoutDropPoints, driverRideTimeRl, driverFareFactor, relativeLayoutDriverCOD, rlAcceptCancel;
		ProgressBar progressBarRequest;
		int id;
		LinearLayout linearLayoutRideValues, llPlaceBid;
		PrefixedEditText etPlaceBid;
		TextView tvPlaceBid;
		DriverRequestListAdapter.MyCustomEditTextListener myCustomEditTextListener;
		LinearLayout llMinus, llPlus;
		TextView tvDecrease, tvIncrease;
		TextView textViewEstimatedTripDistance;
	}

	public void firebaseScreenEvent(String event){
		if(DriverScreenMode.D_REQUEST_ACCEPT == HomeActivity.driverScreenMode){

		} else if(DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode){
			MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ACCEPTED+"_"+event, null);
		} else if(DriverScreenMode.D_START_RIDE == HomeActivity.driverScreenMode){
			MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED+"_"+event, null);
		} else if(DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode){
			MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START+"_"+event, null);
		}
	}


	class DriverRequestListAdapter extends BaseAdapter {
		LayoutInflater mInflater;

		ArrayList<CustomerInfo> customerInfos;
		ArrayList<String> bidValues;

		Handler handlerRefresh;
		Runnable runnableRefresh;
		float percent;

		public DriverRequestListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			customerInfos = new ArrayList<>();
			handlerRefresh = new Handler();
			runnableRefresh = new Runnable() {
				@Override
				public void run() {
					DriverRequestListAdapter.this.notifyDataSetChanged();
					if(driverRideRequestsList.getVisibility() == View.VISIBLE){
						handlerRefresh.postDelayed(runnableRefresh, 10000);
					} else{
						handlerRefresh.postDelayed(runnableRefresh, 10000);
					}
				}
			};
			handlerRefresh.postDelayed(runnableRefresh, 10000);
			bidValues = new ArrayList<>();
			percent = Prefs.with(HomeActivity.this).getFloat(Constants.BID_INCREMENT_PERCENT, 10f);
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
			bidValues = new ArrayList<>();
			for(int i=0; i<this.customerInfos.size(); i++){
				bidValues.add(Utils.getDecimalFormatForMoney().format(customerInfos.get(i).getInitialBidValue()));
			}
			if(this.customerInfos.size() == 0){
				Utils.hideSoftKeyboard(HomeActivity.this, textViewAutosOn);
			}
			this.notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if (DriverScreenMode.D_RIDE_END != HomeActivity.driverScreenMode
					&& DriverScreenMode.D_REQUEST_ACCEPT != HomeActivity.driverScreenMode
					&& customerInfos.size() > 0) {
				driverRideRequestsList.setVisibility(View.VISIBLE);
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
				relativeLayoutLastRideEarning.setVisibility(View.GONE);
				relativeLayoutRefreshUSLBar.setVisibility(View.GONE);
				relativeLayoutBatteryLow.setVisibility(View.GONE);
			} else {
				driverRideRequestsList.setVisibility(View.GONE);
				if(DriverScreenMode.D_INITIAL == driverScreenMode){
//					setPannelVisibility(true);
				}
				showDriverEarning();
				showRefreshUSLBar();
				showLowBatteryAlert(true);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderDriverRequest holder;
			if (convertView == null) {

				holder = new ViewHolderDriverRequest();
				convertView = mInflater.inflate(R.layout.list_item_driver_request_new, null);

				holder.textViewRequestName = (TextView) convertView.findViewById(R.id.textViewRequestName);
				holder.textViewRequestName.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
				holder.textViewRequestAddress = (TextView) convertView.findViewById(R.id.textViewRequestAddress);
				holder.textViewRequestAddress.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewEstimatedTripDistance = (TextView) convertView.findViewById(R.id.textViewEstimatedTripDistance);
				holder.textViewEstimatedTripDistance.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewRequestDetails = (TextView) convertView.findViewById(R.id.textViewRequestDetails);
				holder.textViewRequestDetails.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewEstimatedDist = (TextView) convertView.findViewById(R.id.textViewEstimatedDist);
				holder.textViewEstimatedDist.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewRequestTime = (TextView) convertView.findViewById(R.id.textViewRequestTime);
				holder.textViewRequestTime.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewRequestFareFactor = (TextView) convertView.findViewById(R.id.textViewRequestFareFactor);
				holder.textViewRequestFareFactor.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
				holder.textViewDeliveryFare = (TextView) convertView.findViewById(R.id.textViewDeliveryFare);
				holder.textViewDeliveryFare.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
//				holder.textViewDeliveryApprox = (TextView) convertView.findViewById(R.id.textViewDeliveryApprox);
//				holder.textViewDeliveryApprox.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewEstimatedFareValue = (TextView) convertView.findViewById(R.id.textViewEstimatedFareValue);
				holder.textViewEstimatedFareValue.setTypeface(Fonts.mavenRegular(getApplicationContext()));
//				holder.textViewEstimatedFare  = (TextView) convertView.findViewById(R.id.textViewEstimatedFare);
//				holder.textViewEstimatedFare.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewRequestDistance = (TextView) convertView.findViewById(R.id.textViewRequestDistance);
				holder.textViewRequestDistance.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
				holder.buttonAcceptRide = (Button) convertView.findViewById(R.id.buttonAcceptRide);
				holder.buttonAcceptRide.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.buttonCancelRide = (Button) convertView.findViewById(R.id.buttonCancelRide);
				holder.buttonCancelRide.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				holder.imageViewDeliveryList = (ImageView) convertView.findViewById(R.id.imageViewDeliveryList);
				holder.rlAcceptCancel = (RelativeLayout) convertView.findViewById(R.id.rlAcceptCancel);

				holder.linearLayoutRideValues = (LinearLayout) convertView.findViewById(R.id.linearLayoutRideValues);
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
//				holder.linearLayoutDeliveryFare = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeliveryFare);
				holder.linearLayoutDeliveryParams = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeliveryParams);
				holder.relativeLayoutDropPoints = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutDropPoints);
				holder.driverRideTimeRl = (RelativeLayout) convertView.findViewById(R.id.driverRideTimeRl);
				holder.driverFareFactor = (RelativeLayout) convertView.findViewById(R.id.driverFareFactor);
				holder.relative.setTag(holder);
				holder.buttonAcceptRide.setTag(holder);
				holder.buttonCancelRide.setTag(holder);

				holder.textViewDropPoint = (TextView) convertView.findViewById(R.id.textViewDropPoint);
				holder.textViewDropPoint.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
				holder.textViewDropPoint1 = (TextView) convertView.findViewById(R.id.textViewDropPoint1);
				holder.textViewDropPoint1.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewDropPoint2 = (TextView) convertView.findViewById(R.id.textViewDropPoint2);
				holder.textViewDropPoint2.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewDropPoint3 = (TextView) convertView.findViewById(R.id.textViewDropPoint3);
				holder.textViewDropPoint3.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.relativeLayoutDriverCOD = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutDriverCOD);
				holder.textViewDropPointCount = (TextView) convertView.findViewById(R.id.textViewDropPointCount);
				holder.textViewDropPointCount.setTypeface(Fonts.mavenRegular(getApplicationContext()));
				holder.textViewDropPointCount.setVisibility(View.GONE);

				holder.progressBarRequest = (ProgressBar) convertView.findViewById(R.id.progressBarRequest);

				holder.llPlaceBid = (LinearLayout) convertView.findViewById(R.id.llPlaceBid);
				holder.tvPlaceBid = (TextView) convertView.findViewById(R.id.tvPlaceBid);
				holder.etPlaceBid = (PrefixedEditText) convertView.findViewById(R.id.etPlaceBid);
				holder.etPlaceBid.setPrefixTextColor(ContextCompat.getColor(HomeActivity.this, R.color.text_color));
				holder.myCustomEditTextListener = new MyCustomEditTextListener();
				holder.etPlaceBid.addTextChangedListener(holder.myCustomEditTextListener);
				holder.llMinus = (LinearLayout) convertView.findViewById(R.id.llMinus);
				holder.llPlus = (LinearLayout) convertView.findViewById(R.id.llPlus);
				holder.llMinus.setTag(holder);
				holder.llPlus.setTag(holder);
				holder.tvDecrease = (TextView) convertView.findViewById(R.id.tvDecrease);
				holder.tvIncrease = (TextView) convertView.findViewById(R.id.tvIncrease);

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
			holder.textViewRequestTime.setText(""+timeDiffInSec);

			double distance=0;
			if(customerInfo.getDryDistance()>0){
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				distance = customerInfo.getDryDistance();
			} else if (myLocation != null) {
				holder.textViewRequestDistance.setVisibility(View.VISIBLE);
				 distance = MapUtils.distance(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), customerInfo.getRequestlLatLng());
				distance = distance * 1.5;
			} else {
				holder.textViewRequestDistance.setVisibility(View.GONE);
			}

			holder.textViewRequestDistance.setText("" + decimalFormat.format(distance * UserData.getDistanceUnitFactor(HomeActivity.this))
					+" "+ Utils.getDistanceUnit(UserData.getDistanceUnit(HomeActivity.this)));

//			holder.textViewDeliveryApprox.setVisibility(View.GONE);

			holder.textViewRequestName.setVisibility(View.GONE);
			holder.textViewRequestDetails.setVisibility(View.GONE);
//			holder.linearLayoutDeliveryFare.setVisibility(View.GONE);
			if(customerInfo.getIsDelivery() == 1 ){
				holder.linearLayoutDeliveryParams.setVisibility(View.VISIBLE);
				if(customerInfo.getTotalDeliveries() > 0) {
					holder.textViewRequestDetails.setVisibility(View.VISIBLE);
					holder.textViewRequestDetails.setText(getResources().getString(R.string.delivery_numbers)
							+ " " + customerInfo.getTotalDeliveries());
				}
				holder.textViewRequestName.setVisibility(View.VISIBLE);
				holder.textViewRequestName.setText(customerInfo.getName());
//				holder.linearLayoutDeliveryFare.setVisibility(View.VISIBLE);

				if(!customerInfo.getCashOnDelivery().equalsIgnoreCase("0")){
					holder.relativeLayoutDriverCOD.setVisibility(View.VISIBLE);
					holder.textViewDeliveryFare.setVisibility(View.VISIBLE);
//					holder.textViewDeliveryApprox.setVisibility(View.VISIBLE);
					holder.textViewDeliveryFare.setText(getResources().getString(R.string.COD)
							+": "+Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(),customerInfo.getCashOnDelivery()));
				} else {
					holder.relativeLayoutDriverCOD.setVisibility(View.GONE);
//					holder.textViewDeliveryApprox.setVisibility(View.GONE);
				}
			} else {
				holder.linearLayoutDeliveryParams.setVisibility(View.VISIBLE);
				holder.relativeLayoutDriverCOD.setVisibility(View.GONE);
			}

			if(!customerInfo.getEstimatedDriverFare().equalsIgnoreCase("")){
				holder.textViewEstimatedFareValue.setText(getResources().getString(R.string.estimated_fare)
						+": "+customerInfo.getEstimatedDriverFare());
				holder.textViewEstimatedFareValue.setVisibility(View.VISIBLE);
			} else {
//				holder.textViewEstimatedFare.setVisibility(View.GONE);
				holder.textViewEstimatedFareValue.setVisibility(View.GONE);
			}
			if(customerInfo.getEstimatedDist() != 0){
				holder.textViewEstimatedDist.setText(getResources().getString(R.string.estimated_dis)
						+": "+customerInfo.getEstimatedDist()+" "+Utils.getDistanceUnit(UserData.getDistanceUnit(HomeActivity.this)));
				holder.textViewEstimatedDist.setVisibility(View.VISIBLE);
			} else {
				holder.textViewEstimatedDist.setVisibility(View.GONE);
			}

			if (customerInfo.getFareFactor() > 1 || customerInfo.getFareFactor() < 1) {
				holder.driverFareFactor.setVisibility(View.VISIBLE);
				holder.textViewRequestFareFactor.setText(getResources().getString(R.string.rate)+" " + decimalFormat.format(customerInfo.getFareFactor()) + "x");
			} else {
				holder.driverFareFactor.setVisibility(View.GONE);
			}

			if(holder.relativeLayoutDriverCOD.getVisibility() == View.GONE
					&& holder.driverFareFactor.getVisibility() == View.GONE){
				holder.linearLayoutRideValues.setVisibility(View.GONE);
			} else {
				holder.linearLayoutRideValues.setVisibility(View.VISIBLE);
			}

			if(holder.textViewEstimatedFareValue.getVisibility() == View.GONE
				&& holder.textViewEstimatedDist.getVisibility() == View.GONE
				&& holder.textViewRequestDetails.getVisibility() == View.GONE){
				holder.linearLayoutDeliveryParams.setVisibility(View.GONE);
			} else {
				holder.linearLayoutDeliveryParams.setVisibility(View.VISIBLE);
			}

			int dropAddress = customerInfo.getDeliveryAddress().size();
			android.view.ViewGroup.LayoutParams layoutParams = holder.imageViewDeliveryList.getLayoutParams();

			if(dropAddress > 0){
				holder.relativeLayoutDropPoints.setVisibility(View.VISIBLE);
				if(dropAddress ==1){
					holder.imageViewDeliveryList.setBackgroundResource(R.drawable.dropoff_1);
					holder.textViewDropPoint1.setVisibility(View.VISIBLE);
					holder.textViewDropPoint2.setVisibility(View.GONE);
					holder.textViewDropPoint3.setVisibility(View.GONE);
					holder.textViewDropPointCount.setVisibility(View.GONE);
					holder.textViewDropPoint1.setText(customerInfo.getDeliveryAddress().get(0));
					layoutParams.height = (int) (76f * ASSL.Yscale());
				} else if(dropAddress ==2){
					holder.imageViewDeliveryList.setBackgroundResource(R.drawable.dropoff_2);
					holder.textViewDropPoint1.setVisibility(View.VISIBLE);
					holder.textViewDropPoint1.setText(customerInfo.getDeliveryAddress().get(0));
					holder.textViewDropPoint2.setVisibility(View.VISIBLE);
					holder.textViewDropPoint2.setText(customerInfo.getDeliveryAddress().get(1));
					holder.textViewDropPoint3.setVisibility(View.GONE);
					holder.textViewDropPointCount.setVisibility(View.GONE);
					layoutParams.height = (int) (166f * ASSL.Yscale());
				} else if(dropAddress ==3){
					holder.imageViewDeliveryList.setBackgroundResource(R.drawable.dropoff_3);
					holder.textViewDropPoint1.setVisibility(View.VISIBLE);
					holder.textViewDropPoint1.setText(customerInfo.getDeliveryAddress().get(0));
					holder.textViewDropPoint2.setVisibility(View.VISIBLE);
					holder.textViewDropPoint2.setText(customerInfo.getDeliveryAddress().get(1));
					holder.textViewDropPoint3.setVisibility(View.VISIBLE);
					holder.textViewDropPoint3.setText(customerInfo.getDeliveryAddress().get(2));
					holder.textViewDropPointCount.setVisibility(View.GONE);
					layoutParams.height = (int) (245f * ASSL.Yscale());
				} else if(dropAddress > 3){
					holder.imageViewDeliveryList.setBackgroundResource(R.drawable.dropoff_3);
					holder.textViewDropPoint1.setVisibility(View.VISIBLE);
					holder.textViewDropPoint1.setText(customerInfo.getDeliveryAddress().get(0));
					holder.textViewDropPoint2.setVisibility(View.VISIBLE);
					holder.textViewDropPoint2.setText(customerInfo.getDeliveryAddress().get(1));
					holder.textViewDropPoint3.setVisibility(View.VISIBLE);
					holder.textViewDropPoint3.setText(customerInfo.getDeliveryAddress().get(2));
					layoutParams.height = (int) (245f * ASSL.Yscale());
					int totalDropCount = dropAddress-3;
					holder.textViewDropPointCount.setVisibility(View.VISIBLE);
					holder.textViewDropPointCount.setText("+"+totalDropCount+" "+getResources().getString(R.string.more));
				}
				holder.imageViewDeliveryList.setLayoutParams(layoutParams);
			} else {
				holder.relativeLayoutDropPoints.setVisibility(View.GONE);
			}

			if(customerInfo.getIsPooled() == 1){
				holder.imageViewRequestType.setImageResource(R.drawable.ic_pool_request);
			} else if(customerInfo.getIsDelivery() == 1){
				holder.imageViewRequestType.setImageResource(R.drawable.ic_delivery_request);
			} else{
				int vehicleType = Prefs.with(HomeActivity.this).getInt(SPLabels.VEHICLE_TYPE,0);
				int vehicleTypeImage;
				switch (vehicleType){
					case 2:
						vehicleTypeImage = R.drawable.ic_ride_accept_bike;
						break;
					case 3:
						vehicleTypeImage = R.drawable.ic_ride_accept_taxi;
						break;
					case 6:
						vehicleTypeImage = R.drawable.ic_ride_accept_transport;
						break;
					default:
						vehicleTypeImage = R.drawable.ic_auto_request;
						break;

				}
				holder.imageViewRequestType.setImageResource(vehicleTypeImage);
			}


			holder.myCustomEditTextListener.updatePosition(position);
			if(customerInfo.isReverseBid()){
				holder.llPlaceBid.setVisibility(View.VISIBLE);
				holder.buttonAcceptRide.setText(R.string.bid);
				if(customerInfo.isBidPlaced()){
					holder.rlAcceptCancel.setVisibility(View.GONE);
					holder.tvPlaceBid.setText(R.string.bid_placed);
					holder.etPlaceBid.setEnabled(false);
					holder.etPlaceBid.setCompoundDrawables(null, null, null, null);
					holder.etPlaceBid.setText(Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(), customerInfo.getBidValue()));
					holder.llMinus.setVisibility(View.GONE);
					holder.llPlus.setVisibility(View.GONE);
				} else {
					holder.rlAcceptCancel.setVisibility(View.VISIBLE);
					holder.tvPlaceBid.setText(R.string.place_bid);
					holder.etPlaceBid.setPrefix(Utils.getCurrencySymbol(customerInfo.getCurrencyUnit()));
					holder.etPlaceBid.setEnabled(true);
					holder.llMinus.setVisibility(View.VISIBLE);
					holder.llPlus.setVisibility(View.VISIBLE);
					try {
						holder.etPlaceBid.setText(Utils.getDecimalFormatForMoney2Dec().format(Double.parseDouble(bidValues.get(position))));
					} catch (Exception e) {
						holder.etPlaceBid.setText("");
					}
				}
				holder.etPlaceBid.setSelection(holder.etPlaceBid.getText().length());
				holder.tvDecrease.setText(getString(R.string.reduce_by_format, Utils.getDecimalFormatNoDecimal().format((double)percent)+"%"));
				holder.tvIncrease.setText(getString(R.string.increase_by_format, Utils.getDecimalFormatNoDecimal().format((double)percent)+"%"));
			} else {
				holder.llPlaceBid.setVisibility(View.GONE);
				holder.buttonAcceptRide.setText(R.string.accept);
				holder.rlAcceptCancel.setVisibility(View.VISIBLE);
			}

			if(customerInfo.getEstimatedTripDistance() > 0.0){
				holder.textViewEstimatedTripDistance.setVisibility(View.VISIBLE);
				holder.textViewEstimatedTripDistance.setText(getString(R.string.estimated_distance_format,
						Utils.getDecimalFormatForMoney2Dec().format(customerInfo.getEstimatedTripDistance()
								*UserData.getDistanceUnitFactor(HomeActivity.this))));
			} else {
				holder.textViewEstimatedTripDistance.setVisibility(View.GONE);
			}


			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if (DriverScreenMode.D_INITIAL == driverScreenMode) {
							ViewHolderDriverRequest holder = (ViewHolderDriverRequest) v.getTag();
							CustomerInfo customerInfo = customerInfos.get(holder.id);
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
						ViewHolderDriverRequest holder = (ViewHolderDriverRequest) v.getTag();
						if(isTourFlag) {
							setTourOperation(2);
						} else {
							MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_RECEIVED+"_"+holder.id+"_"+FirebaseEvents.YES, null);
							CustomerInfo customerInfo1 = customerInfos.get(holder.id);
							if(customerInfo1.isReverseBid()){
								if(TextUtils.isEmpty(bidValues.get(holder.id))){
									Toast.makeText(HomeActivity.this, getString(R.string.please_enter_some_value), Toast.LENGTH_SHORT).show();
									return;
								}
								setBidForEngagementAPI(customerInfo1, Double.parseDouble(bidValues.get(holder.id)));
							} else {
								acceptRequestFunc(customerInfo1);
							}
							GCMIntentService.stopRing(true, HomeActivity.this);
							FlurryEventLogger.event(FlurryEventNames.RIDE_ACCEPTED);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.buttonCancelRide.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						ViewHolderDriverRequest holder = (ViewHolderDriverRequest) v.getTag();
						if(isTourFlag) {

							tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 1);
//							handleTourView(false, "");
						} else {
							MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_RECEIVED + "_" + holder.id + "_" + FirebaseEvents.NO, null);
							CustomerInfo customerInfo1 = customerInfos.get(holder.id);
							rejectRequestFuncCall(customerInfo1);
							FlurryEventLogger.event(RIDE_CANCELLED);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.llMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					modifyBidValue(v, false);
				}
			});
			holder.llPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					modifyBidValue(v, true);
				}
			});

			return convertView;
		}

		private void modifyBidValue(View v, boolean plus) {
			try {
				ViewHolderDriverRequest holder = (ViewHolderDriverRequest) v.getTag();
				CustomerInfo customerInfo1 = customerInfos.get(holder.id);
				double finalValue = Double.parseDouble(bidValues.get(holder.id));
				if(plus) {
					finalValue = finalValue + customerInfo1.getInitialBidValue() * (((double) percent) / 100d);
				} else {
					finalValue = finalValue - customerInfo1.getInitialBidValue() * (((double) percent) / 100d);
				}
				if(finalValue > 0.0) {
					bidValues.set(holder.id, String.valueOf(Utils.getDecimalFormatForMoney().format(finalValue)));
					notifyDataSetChanged();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private class MyCustomEditTextListener implements TextWatcher {
			private int position;

			public void updatePosition(int position) {
				this.position = position;
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
				bidValues.set(position, charSequence.toString());
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
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
			HomeUtil.putDefaultParams(params);

			if(customerInfo.getIsDeliveryPool() ==1){
				params.put(KEY_RIDE_TYPE,"4");
			} else if(customerInfo.getIsDelivery() ==1){
				params.put(KEY_RIDE_TYPE,"3");
			} else if(customerInfo.getIsPooled() ==1){
				params.put(KEY_RIDE_TYPE,"2");
			} else {
				params.put(KEY_RIDE_TYPE,"0");
			}

			Log.i("request", String.valueOf(params));

			GCMIntentService.cancelUploadPathAlarm(HomeActivity.this);
			RestClient.getApiServices().driverAcceptRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
					Prefs.with(activity).save(SPLabels.ACCEPT_RIDE_TIME, String.valueOf(System.currentTimeMillis()));
					Prefs.with(activity).save(Constants.DRIVER_RIDE_EARNING, "");
					Prefs.with(activity).save(Constants.DRIVER_RIDE_DATE, "");
					acceptRideSucess(jsonString,
							String.valueOf(customerInfo.getEngagementId()),
							String.valueOf(customerInfo.getUserId()));
					GCMIntentService.stopRing(true, activity);

					ArrayList<CustomerInfo> customerEnfagementInfos1 = Data.getAssignedCustomerInfosListForEngagedStatus();

					if (customerInfo.getIsPooled() == 1) {
						if (Database2.getInstance(HomeActivity.this).getPoolDiscountFlag(customerInfo.getEngagementId()) != 1) {
							Database2.getInstance(HomeActivity.this).deletePoolDiscountFlag(customerInfo.getEngagementId());
							Database2.getInstance(HomeActivity.this).insertPoolDiscountFlag(customerInfo.getEngagementId(), 0);
						}
						if (customerEnfagementInfos1.size() > 1) {
							for (int i = 0; i < customerEnfagementInfos1.size(); i++) {
								Database2.getInstance(HomeActivity.this).updatePoolDiscountFlag(customerEnfagementInfos1.get(i).getEngagementId(), 1);
							}
						}
					}


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
			if(!SplashNewActivity.checkIfTrivialAPIErrors(this, jObj, flag, null)){
				if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
					Data.fareStructure = JSONParser.parseFareObject(jObj);
					Data.fareStructure.fareFactor = jObj.optDouble(KEY_FARE_FACTOR, 1);
					Data.fareStructure.luggageFare = jObj.optDouble(KEY_LUGGAGE_CHARGES, 0d);
					Data.fareStructure.convenienceCharge = jObj.optDouble(KEY_CONVENIENCE_CHARGE, 0);
					Data.fareStructure.convenienceChargeWaiver = jObj.optDouble(KEY_CONVENIENCE_CHARGE_WAIVER, 0);
					int referenceId = jObj.optInt(KEY_REFERENCE_ID, 0);

					int isDelivery = jObj.optInt(KEY_IS_DELIVERY, 0);
					int isDeliveryPool = 0;
					if(jObj.optInt(KEY_RIDE_TYPE,0)==4){
						isDeliveryPool =1;
					}
					JSONObject userData = jObj.optJSONObject(KEY_USER_DATA);
					String userName = "", userImage = "", phoneNo = "", rating = "", address = "",
							vendorMessage = "", estimatedDriverFare ="";
					int ForceEndDelivery = 0, falseDeliveries = 0, loadingStatus=0;
					double jugnooBalance = 0, pickupLatitude = 0, pickupLongitude = 0, estimatedFare = 0, cashOnDelivery = 0,
							currrentLatitude=0, currrentLongitude=0;
					int totalDeliveries = 0, orderId =0 ;
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
						cashOnDelivery = userData.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0d);
						vendorMessage = userData.optString(Constants.KEY_VENDOR_MESSAGE, "");
						ForceEndDelivery = userData.optInt(Constants.KEY_END_DELIVERY_FORCED, 0);
						estimatedDriverFare = userData.optString(KEY_ESTIMATED_DRIVER_FARE, "");
						loadingStatus = userData.optInt(KEY_IS_LOADING, 0);
						falseDeliveries = userData.optInt("false_deliveries", 0);
						orderId = userData.optInt("order_id", 0);
					} else{
						userName = userData.optString(KEY_USER_NAME, "");
						userImage = userData.optString(KEY_USER_IMAGE, "");
						phoneNo = userData.optString(KEY_PHONE_NO, "");
						rating = userData.optString(KEY_USER_RATING, "4");
						jugnooBalance = userData.optDouble(KEY_JUGNOO_BALANCE, 0);
						pickupLatitude = jObj.optDouble(KEY_PICKUP_LATITUDE, 0);
						pickupLongitude = jObj.optDouble(KEY_PICKUP_LONGITUDE, 0);
						address = userData.getString(KEY_ADDRESS);
						Log.e("address", address);
						currrentLatitude = jObj.getDouble(Constants.KEY_CURRENT_LATITUDE);
						currrentLongitude = jObj.getDouble(Constants.KEY_CURRENT_LONGITUDE);
					}

					LatLng pickuplLatLng = new LatLng(pickupLatitude, pickupLongitude);
					LatLng currentLatLng = new LatLng(currrentLatitude, currrentLongitude);

					CouponInfo couponInfo = JSONParser.parseCouponInfo(jObj);
					PromoInfo promoInfo = JSONParser.parsePromoInfo(jObj);

					int meterFareApplicable = jObj.optInt("meter_fare_applicable", 0);
					int getJugnooFareEnabled = jObj.optInt("get_jugnoo_fare_enabled", 1);
					int luggageChargesApplicable = jObj.optInt("luggage_charges_applicable", 0);
					int waitingChargesApplicable = jObj.optInt("waiting_charges_applicable", 0);
					Prefs.with(HomeActivity.this).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObj.optLong("eta", 0));
					int cachedApiEnabled = jObj.optInt(KEY_CACHED_API_ENABLED, 0);
					Prefs.with(activity).save(SPLabels.CHAT_ENABLED,jObj.optInt("chat_enabled",0));
					int isPooled = jObj.optInt(KEY_IS_POOLED, 0);
					String currency = jObj.optString(Constants.KEY_CURRENCY);

					Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal());

					GCMIntentService.clearNotifications(getApplicationContext());

					initializeStartRideVariables();

					CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
							Integer.parseInt(customerId), referenceId, userName, phoneNo, pickuplLatLng, cachedApiEnabled,
							userImage, rating, couponInfo, promoInfo, jugnooBalance, meterFareApplicable, getJugnooFareEnabled,
							luggageChargesApplicable, waitingChargesApplicable, EngagementStatus.ACCEPTED.getOrdinal(), isPooled,
							isDelivery, isDeliveryPool, address, totalDeliveries, estimatedFare, vendorMessage, cashOnDelivery,
							currentLatLng, ForceEndDelivery, estimatedDriverFare, falseDeliveries, orderId, loadingStatus, currency);

					JSONParser.updateDropAddressLatlng(jObj, customerInfo);

					JSONParser.parsePoolOrReverseBidFare(jObj, customerInfo);

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
						reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal(),"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				DialogPopup.dismissLoadingDialog();

			} else{
				reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal(),"");
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
			driverPassengerInfoRl.setVisibility(View.VISIBLE);
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


	public void reduceRideRequest(String engagementId, int status, String message) {
		Data.removeCustomerInfo(Integer.parseInt(engagementId), status);
		driverScreenMode = DriverScreenMode.D_INITIAL;
		switchDriverScreen(driverScreenMode);
		if(!message.equalsIgnoreCase("")) {
			DialogPopup.alertPopup(HomeActivity.this, "", message);
		}
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
			HomeUtil.putDefaultParams(params);
			if(customerInfo.getIsDeliveryPool() ==1){
				params.put(KEY_RIDE_TYPE,"4");
			}

			RestClient.getApiServices().driverRejectRequestRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());

						JSONObject jObj = new JSONObject(jsonString);
						int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.RIDE_ACCEPTED.getOrdinal());
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
							if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
								String log = jObj.getString("log");
								DialogPopup.alertPopup(activity, "", "" + log);
							} else {
								if (map != null) {
									map.clear();
									drawHeatMapData(heatMapResponseGlobal);
								}
								stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));

								reduceRideRequest(String.valueOf(customerInfo.getEngagementId()), EngagementStatus.REQUESTED.getOrdinal(),"");
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
			reduceRideRequest(String.valueOf(customerInfo.getEngagementId()), EngagementStatus.REQUESTED.getOrdinal(),"");


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
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().driverMarkArriveRideRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.optInt(KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

									Database2.getInstance(activity).insertRideData("0.0", "0.0", "" + System.currentTimeMillis(), customerInfo.getEngagementId());
									Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "arrived sucessful");
									if(jObj.has("pickup_data")) {
										Gson gson = new Gson();
										DeliveryInfoInRideDetails deliveryInfoInRideDetails1 = new DeliveryInfoInRideDetails();
										DeliveryInfoInRideDetails.PickupData pickupData = gson.fromJson(jObj.getJSONObject("pickup_data").toString(), DeliveryInfoInRideDetails.PickupData.class);
										deliveryInfoInRideDetails1.setPickupData(pickupData);
										List<DeliveryInfoInRideDetails.DeliveryDatum> deliveryDatumList;
										deliveryDatumList= gson.fromJson(jObj.getJSONArray("delivery_data").toString(),
												new TypeToken<List<DeliveryInfoInRideDetails.DeliveryDatum>>(){}.getType());
										deliveryInfoInRideDetails1.setDeliveryData(deliveryDatumList);
//										relativeLayoutContainer.setVisibility(View.VISIBLE);

										customerInfo.setDeliveryInfoInRideDetails(deliveryInfoInRideDetails1);
//										deliveryInfolistFragVisibility =true;
//										getTransactionUtils().openDeliveryInfoInRideFragment(HomeActivity.this,
//												getRelativeLayoutContainer(), deliveryInfoInRideDetails1);
									}

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
				DialogPopup.dismissLoadingDialog();
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
			HomeUtil.putDefaultParams(params);

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
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
							if (ApiResponseFlags.RIDE_STARTED.getOrdinal() == flag) {

								Database2.getInstance(activity).insertCustomerRideData(customerInfo.getEngagementId(), System.currentTimeMillis());

								double dropLatitude = 0, dropLongitude = 0;
								try {
									if(deliveryInfolistFragVisibility){
										deliveryInfolistFragVisibility =false;
//										onBackPressed();
										relativeLayoutContainer.setVisibility(View.GONE);
										getSupportFragmentManager().beginTransaction()
												.remove(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()))
												.commitAllowingStateLoss();
									}
									if (jObj.has(KEY_OP_DROP_LATITUDE) && jObj.has(KEY_OP_DROP_LONGITUDE)) {
										dropLatitude = jObj.getDouble(KEY_OP_DROP_LATITUDE);
										dropLongitude = jObj.getDouble(KEY_OP_DROP_LONGITUDE);
										Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_DISTANCE, jObj.optString(KEY_PR_DISTANCE, "1000"));
									}
									if(jObj.has(KEY_DROP_ADDRESS)){
										customerInfo.setDropAddress(jObj.getString(KEY_DROP_ADDRESS));
									}

								} catch (Exception e) {
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

								initializeStartRideVariables();

								if (Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()) == null
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
//		if(customerInfo.getIsPooled() != 1) {
			rideTimeChronometer.start();
//		} else{
//			rideTimeChronometer.stop();
//		}
	}

	public void driverEndRideWithDistanceSafetyCheck(final Activity activity, final LatLng lastAccurateLatLng,
													 final double dropLatitude, final double dropLongitude,
													 final int flagDistanceTravelled, final CustomerInfo customerInfo) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
		new Thread(new Runnable() {

			@Override
			public void run() {
				final Pair<Double, CurrentPathItem> currentPathItemPair = Database2.getInstance(HomeActivity.this).getCurrentPathItemsAllComplete();
				if (currentPathItemPair != null
						&& (Math.abs(customerRideDataGlobal.getDistance(HomeActivity.this) - currentPathItemPair.first) > 500
						|| MapUtils.distance(currentPathItemPair.second.dLatLng, new LatLng(dropLatitude, dropLongitude)) > 500)) {
					double displacement = MapUtils.distance(currentPathItemPair.second.dLatLng, new LatLng(dropLatitude, dropLongitude));
					try {
						Response responseR = GoogleRestApis.getDirections(currentPathItemPair.second.dLatLng.latitude + "," + currentPathItemPair.second.dLatLng.longitude,
								dropLatitude + "," + dropLongitude, false, "driving", false);
						String response = new String(((TypedByteArray) responseR.getBody()).getBytes());
						JSONObject jsonObject = new JSONObject(response);
						String status = jsonObject.getString("status");
						if ("OK".equalsIgnoreCase(status)) {
							JSONObject leg0 = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
							double distanceOfPath = leg0.getJSONObject("distance").getDouble("value");
							if (Utils.compareDouble(distanceOfPath, (displacement * 1.6)) <= 0) {
								long rowId = Database2.getInstance(activity).insertCurrentPathItem(-1, currentPathItemPair.second.dLatLng.latitude, currentPathItemPair.second.dLatLng.longitude,
										dropLatitude, dropLongitude, 1, 1);
								String encodedString = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
								List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
								for (int z = 0; z < list.size() - 1; z++) {
									LatLng src = list.get(z);
									LatLng dest = list.get(z + 1);
									if(rowId != -1) {
										Database2.getInstance(activity).insertCurrentPathItem(rowId, src.latitude, src.longitude,
												dest.latitude, dest.longitude, 0, 0);
									}
								}
								if(rowId != -1) {
									Database2.getInstance(activity).updateCurrentPathItemSectionIncomplete(rowId, 0);
								}
								PathUploadReceiver.uploadInRidePath(activity, false);
								customerRideDataGlobal.setDistance(customerRideDataGlobal.getDistance(HomeActivity.this) + distanceOfPath);
								Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI 2 distanceOfPath=" + distanceOfPath + " and totalDistance=" + customerRideDataGlobal.getDistance(HomeActivity.this));
							} else {
								throw new Exception();
							}
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						e.printStackTrace();
						customerRideDataGlobal.setDistance(customerRideDataGlobal.getDistance(HomeActivity.this) + displacement);
						Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI 2 excep displacement=" + displacement + " and totalDistance=" + customerRideDataGlobal.getDistance(HomeActivity.this));
					}
				} else {
					if(currentPathItemPair != null) {
						Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI 2 else currentPathItemPair.second.dLatLng=" + currentPathItemPair.second.dLatLng + " and drop=" + new LatLng(dropLatitude, dropLongitude));
						Log.writePathLogToFile(customerInfo.getEngagementId() + "m", "GAPI 2 else currentPathItemPair.first=" + currentPathItemPair.first + " and global dist=" + customerRideDataGlobal.getDistance(HomeActivity.this));

					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						DialogPopup.dismissLoadingDialog();
						driverEndRideAsync1(activity, lastAccurateLatLng, dropLatitude, dropLongitude, flagDistanceTravelled, customerInfo);
					}
				});
			}
		}).start();
	}

	/**
	 * ASync for start ride in  driver mode from server
	 */
	public void driverEndRideAsync1(final Activity activity, LatLng lastAccurateLatLng, double dropLatitude, double dropLongitude,
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

	public void autoEndRideAPI(final Activity activity, LatLng lastAccurateLatLng, final double dropLatitude, final double dropLongitude,
							   int flagDistanceTravelled, final CustomerInfo customerInfo) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

		double totalDistanceFromLogInMeter = 0;

		double totalDistanceInKm = getTotalDistanceInKm(customerInfo);

		double Limit_endRideMinute = 360;
		double Average_endRideMinute = totalDistanceInKm * 2;

		long rideTimeInMillis = customerInfo.getElapsedRideTime(HomeActivity.this);
		long rideTimeInMillisFromDB = Database2.getInstance(activity).getCustomerElapsedRideTime(customerInfo.getEngagementId());
		long startTime = Database2.getInstance(activity).getCustomerStartRideTime(customerInfo.getEngagementId());
		try {
//			totalDistanceFromLogInMeter = Database2.getInstance(activity).getLastRideData(customerInfo.getEngagementId(), 1).accDistance;
			totalDistanceFromLogInMeter = Database2.getInstance(activity).checkRideData(customerInfo.getEngagementId(), startTime);
			Log.e("totalDistanceFromLogInMeter", String.valueOf(totalDistanceFromLogInMeter));
		} catch (Exception e) {
			e.printStackTrace();
		}
		double totalDistanceFromLog = Math.abs(totalDistanceFromLogInMeter / 1000.0);

		long customerWaitTimeMillis = customerInfo
				.getTotalWaitTime(customerRideDataGlobal.getWaitTime(HomeActivity.this), HomeActivity.this);

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
		double waitTimeSeconds = Math.ceil(((double) customerWaitTimeMillis) / 1000d);
		String waitTimeSecondsStr = decimalFormatNoDecimal.format(waitTimeSeconds);

		String rideTimeInSecFromDBStr = decimalFormatNoDecimal.format(Math.ceil(((double) rideTimeInMillisFromDB) / 1000d));

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
		params.put(KEY_RIDE_TIME_SECONDS_DB, rideTimeInSecFromDBStr);
		params.put(KEY_IS_CACHED, "0");
		if(JSONParser.isTagEnabled(activity, Constants.KEY_SHOW_TOLL_CHARGE)) {
			params.put(Constants.KEY_TOLL_CHARGE, String.valueOf(customerInfo.getTollFare()));
		}
		params.put("flag_distance_travelled", "" + flagDistanceTravelled);
		params.put("last_accurate_latitude", "" + lastAccurateLatLng.latitude);
		params.put("last_accurate_longitude", "" + lastAccurateLatLng.longitude);
		params.put("ride_distance_using_haversine", "" + decimalFormat.format(totalHaversineDistanceInKm));
		HomeUtil.putDefaultParams(params);

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
		try {
			if(customerInfo.getIsDelivery() == 1){
				url = PendingCall.END_DELIVERY.getPath();
			}
			double distance = customerInfo
					.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
			double totalFare = getTotalFare(customerInfo, distance,
					eoRideTimeInMillis, eoWaitTimeInMillis, getInvalidPool(customerInfo, dropLatitude, dropLongitude, 0));
			if (customerInfo.getCachedApiEnabled() == 1 && customerInfo.getIsDelivery() != 1 &&  (Data.userData.fareCachingLimit==null || totalFare<=Data.userData.fareCachingLimit)) {
				endRideOffline(activity, url, params, eoRideTimeInMillis, eoWaitTimeInMillis,
						customerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded,
						totalDistanceFromLogInMeter, rideTimeInMillisFromDB);
			} else {
				if(customerInfo.getIsDelivery() == 1) {
					if(customerInfo.getIsDeliveryPool() ==1){
						params.put(KEY_RIDE_TYPE, "4");
					} else {
						params.put(KEY_RIDE_TYPE, "3");
					}
					RestClient.getApiServices().endDelivery(params, getCallbackEndDelivery(customerInfo, totalHaversineDistanceInKm, dropLatitude, dropLongitude, params,
							eoRideTimeInMillis, eoWaitTimeInMillis, url, totalDistanceFromLogInMeter, rideTimeInMillisFromDB));
				} else{
					RestClient.getApiServices().autoEndRideAPIRetro(params, new CallbackEndRide(customerInfo, totalHaversineDistanceInKm, dropLatitude, dropLongitude, params,
							eoRideTimeInMillis, eoWaitTimeInMillis, url, totalDistanceFromLogInMeter, rideTimeInMillisFromDB));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getTotalDistanceInKm(CustomerInfo customerInfo) {
		double totalDistance = customerInfo
				.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
		return Math.abs(totalDistance / 1000.0);
	}

	private CallbackEndRide callbackEndDelivery;
	private CallbackEndRide getCallbackEndDelivery(CustomerInfo customerInfo, double totalHaversineDistanceInKm, double dropLatitude,
												   double dropLongitude, HashMap<String, String> params, long eoRideTimeInMillis,
												   long eoWaitTimeInMillis, String url, double totalDistanceFromLogInMeter, long rideTimeInMillisFromDB) {
		callbackEndDelivery = new CallbackEndRide(customerInfo, totalHaversineDistanceInKm, dropLatitude, dropLongitude,
				params, eoRideTimeInMillis, eoWaitTimeInMillis, url, totalDistanceFromLogInMeter, rideTimeInMillisFromDB);
		return callbackEndDelivery;
	}

	private class CallbackEndRide<RegisterScreenResponse> implements Callback<RegisterScreenResponse>{
		private CustomerInfo customerInfo;
		private double totalHaversineDistanceInKm, dropLatitude, dropLongitude, totalDistanceFromLogInMeter;
		private HashMap<String, String> params;
		private long eoRideTimeInMillis, eoWaitTimeInMillis;
		private String url;
		private long rideTimeInMillisFromDB;

		public CallbackEndRide(CustomerInfo customerInfo, double totalHaversineDistanceInKm, double dropLatitude, double dropLongitude, HashMap<String, String> params,
							   long eoRideTimeInMillis, long eoWaitTimeInMillis, String url, double totalDistanceFromLogInMeter,
							   long rideTimeInMillisFromDB) {
			this.customerInfo = customerInfo;
			this.totalHaversineDistanceInKm = totalHaversineDistanceInKm;
			this.dropLatitude = dropLatitude;
			this.dropLongitude = dropLongitude;
			this.params = params;
			this.eoRideTimeInMillis = eoRideTimeInMillis;
			this.eoWaitTimeInMillis = eoWaitTimeInMillis;
			this.totalDistanceFromLogInMeter = totalDistanceFromLogInMeter;
			this.url = url;
			this.rideTimeInMillisFromDB = rideTimeInMillisFromDB;
		}

		@Override
		public void success(RegisterScreenResponse registerScreenResponse, Response response) {
			try {
				success(new String(((TypedByteArray) response.getBody()).getBytes()));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		public void success(String jsonString) {
			try {
				JSONObject jObj;
				jObj = new JSONObject(jsonString);
				int flag = jObj.getInt("flag");
				if (!jObj.isNull("error")) {
					String errorMessage = jObj.getString("error");

					if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
						HomeActivity.logoutUser(activity, null);
					} else {
						DialogPopup.alertPopup(activity, "", errorMessage);
					}
					driverScreenMode = DriverScreenMode.D_IN_RIDE;
					startRideChronometer(customerInfo);
				} else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
					onEndRideFailure();
				} else {

					try {
						totalFare = jObj.getDouble("fare");
						if(customerInfo.getIsDelivery() ==1) {
							fixDeliveryDistance = jObj.getDouble("distance_travelled");
							fixedDeliveryWaitTime = jObj.getDouble("fix_wait_time");
						}
					} catch (Exception e) {
						e.printStackTrace();
						totalFare = 0;
						fixDeliveryDistance = -1;
						fixedDeliveryWaitTime = -1;
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
						drawHeatMapData(heatMapResponseGlobal);
					}

					rideTimeChronometer.stop();

					driverUploadPathDataFileAsync(activity, customerInfo.getEngagementId(), totalHaversineDistanceInKm);

					driverScreenMode = DriverScreenMode.D_RIDE_END;

					Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);
					switchDriverScreen(driverScreenMode);

					Database2.getInstance(activity).deleteCustomerRideDataForEngagement(customerInfo.getEngagementId());


					initializeStartRideVariables();
					nudgeRideEnd(customerInfo, dropLatitude, dropLongitude, params);

					try {
						if(customerInfo.getIsPooled() ==1){
							Database2.getInstance(HomeActivity.this).deletePoolDiscountFlag(customerInfo.getEngagementId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						Prefs.with(HomeActivity.this).save(Constants.DRIVER_RIDE_EARNING, jObj.optString("driver_ride_earning", ""));
						Prefs.with(HomeActivity.this).save(Constants.DRIVER_RIDE_EARNING_CURRENCY, jObj.optString(Constants.KEY_CURRENCY, "INR"));
						Prefs.with(HomeActivity.this).save(Constants.DRIVER_RIDE_DATE, jObj.optString("driver_ride_date", ""));

						if (!"".equalsIgnoreCase(Prefs.with(HomeActivity.this).getString(Constants.DRIVER_RIDE_EARNING, ""))) {
							Intent fetchDocIntent = new Intent(Constants.ACTION_UPDATE_RIDE_EARNING);
							HomeActivity.this.sendBroadcast(fetchDocIntent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}


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
			onEndRideFailure();
		}

		public void onEndRideFailure(){
			double distance = customerInfo
					.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
			double totalFare = getTotalFare(customerInfo, distance,
					eoRideTimeInMillis, eoWaitTimeInMillis, getInvalidPool(customerInfo, dropLatitude, dropLongitude, 0));

			if(customerInfo.getCachedApiEnabled() == 1 && customerInfo.getIsDelivery() != 1 && (Data.userData.fareCachingLimit==null || totalFare<=Data.userData.fareCachingLimit)) {
				endRideOffline(activity, url, params, eoRideTimeInMillis, eoWaitTimeInMillis,
						customerInfo, dropLatitude, dropLongitude, enteredMeterFare, luggageCountAdded,
						totalDistanceFromLogInMeter, rideTimeInMillisFromDB);
			} else{
//				endDelivery.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
//				endDelivery.alertPopup(activity, "", "Delivery will be ended shortly", true, false);

				endDelivery.alertPopupTwoButtonsWithListeners(activity, "", getString(R.string.connection_lost), "Retry", "Cancel", new OnClickListener() {
					@Override
					public void onClick(View v) {
						endRideGPSCorrection(customerInfo);

					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				},false,false);

				if (DriverScreenMode.D_BEFORE_END_OPTIONS != driverScreenMode) {
					driverScreenMode = DriverScreenMode.D_IN_RIDE;
					startRideChronometer(customerInfo);
				}
				DialogPopup.dismissLoadingDialog();

				Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
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
							   CustomerInfo customerInfo, final double dropLatitude, final double dropLongitude, double enteredMeterFare,
							   int luggageCountAdded, double totalDistanceFromLogInMeter, long rideTimeInMillisFromDB) {
		try {



			double actualFare, finalDiscount, finalPaidUsingWallet, finalToPay, finalDistance;
			int paymentMode = PaymentMode.CASH.getOrdinal();
			int invalidPool =0;

			invalidPool = getInvalidPool(customerInfo, dropLatitude, dropLongitude, invalidPool);


			double totalDistance = customerInfo.getTotalDistance(customerRideDataGlobal.getDistance(HomeActivity.this), HomeActivity.this);
			finalDistance = 0;

			long rideTimeFromLogDB = Database2.getInstance(activity).getFirstRideDataTime(customerInfo.getEngagementId());

			long MAX_RIDE_TIME_LIMIT = 360l * 60l * 1000l;
			long ONE_SEC = 1000;

			if (rideTimeInMillisFromDB > rideTimeInMillis
					&& rideTimeInMillisFromDB < MAX_RIDE_TIME_LIMIT) {
				rideTimeInMillis = rideTimeInMillisFromDB;
				rideTime = decimalFormatNoDecimal.format(Math.round(((double) rideTimeInMillis) / 60000.0d));
			}
			else if (rideTimeInMillisFromDB < ONE_SEC
					&& rideTimeFromLogDB > rideTimeInMillis
					&& rideTimeFromLogDB < MAX_RIDE_TIME_LIMIT) {
				rideTimeInMillis = rideTimeFromLogDB;
				rideTime = decimalFormatNoDecimal.format(Math.round(((double) rideTimeInMillis) / 60000.0d));
				params.put(KEY_RIDE_TIME_SECONDS_DB, decimalFormatNoDecimal.format(Math.ceil(((double) rideTimeFromLogDB) / 1000d)));
			}



			if (totalDistance < totalDistanceFromLogInMeter
					&& totalDistanceFromLogInMeter - totalDistance < 40d * 1000d) {
				long ridetimeInSec = rideTimeInMillis / 1000L;
				double speed = totalDistanceFromLogInMeter / ((double)ridetimeInSec);
				if (speed <= 15) {
					finalDistance = totalDistanceFromLogInMeter;
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
							rideTimeInMillis, waitTimeInMillis, invalidPool);
				}
			} catch (Exception e) {
				e.printStackTrace();
				totalFare = 0;
			}
			params.put("mandatory_fare_applicable", String.valueOf(Data.fareStructure.getMandatoryFareApplicable()));

			try {
				if (customerInfo != null && 1 == customerInfo.luggageChargesApplicable) {
					totalFare = totalFare + (luggageCountAdded * Data.fareStructure.luggageFare);
				}

				params.put("is_invalid_pool", String.valueOf(invalidPool));
				if(1 == Database2.getInstance(HomeActivity.this).getPoolDiscountFlag(customerInfo.getEngagementId())
						&& customerInfo.getPoolFare().getDiscountedFareEnabled() ==1 && invalidPool ==1){
					totalFare = totalFare - Math.round(customerInfo.getPoolFare().getDiscountPercentage() * totalFare);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			try {
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "Data.fareStructure = " + Data.fareStructure);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "rideTime = " + rideTime);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "waitTime = " + waitTime);
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "totalDistance = " + finalDistance);
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
					finalDiscount, finalPaidUsingWallet, finalToPay, paymentMode,customerInfo.getCurrencyUnit());

			try {
				Log.writePathLogToFile(customerInfo.getEngagementId() + "endRide", "endRideData = " + endRideData);
			} catch (Exception e) {
				e.printStackTrace();
			}


			rideTimeChronometer.stop();


			driverUploadPathDataFileAsync(activity, customerInfo.getEngagementId(),
					customerInfo.getTotalHaversineDistance(customerRideDataGlobal.getHaversineDistance(), HomeActivity.this));

			driverScreenMode = DriverScreenMode.D_RIDE_END;
			Data.setCustomerState(String.valueOf(customerInfo.getEngagementId()), driverScreenMode);
			switchDriverScreen(driverScreenMode);

			Database2.getInstance(activity).deleteCustomerRideDataForEngagement(customerInfo.getEngagementId());
			try {
				if(customerInfo.getIsPooled() == 1){
					Database2.getInstance(HomeActivity.this).deletePoolDiscountFlag(customerInfo.getEngagementId());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			params.put("is_cached", "1");
			params.put("paid_in_cash", String.valueOf(finalToPay));

			DialogPopup.dismissLoadingDialog();
			if(!isTourFlag) {
				Database2.getInstance(activity).insertPendingAPICall(activity, url, params);
			}
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

	private int getInvalidPool(CustomerInfo customerInfo, double dropLatitude, double dropLongitude, int invalidPool) {
		try {
            if(customerInfo.getPoolFare() != null && customerInfo.getIsPooled() ==1) {
                LatLng poolDropLatLng = customerInfo.dropLatLng;
                LatLng actualDropLatng = new LatLng(dropLatitude, dropLongitude);
                double poolDropDistanceDiff = MapUtils.distance(poolDropLatLng, actualDropLatng);
                if (poolDropDistanceDiff > customerInfo.getPoolFare().getPoolDropRadius()) {
                    invalidPool = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return invalidPool;
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
				HomeUtil.putDefaultParams(params);

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
				HomeUtil.putDefaultParams(params);

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
			HomeUtil.putDefaultParams(params);

			RestClient.getApiServices().logoutRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
							HomeActivity.logoutUser(activity, null);
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



	public void fetchHeatMapData(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				final long responseTime = System.currentTimeMillis();
				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				HomeUtil.putDefaultParams(params);
				RestClient.getApiServices().getHeatMapAsync(params, new Callback<HeatMapResponse>() {
					@Override
					public void success(HeatMapResponse heatMapResponse, Response response) {
						try {

							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i("heat", jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.optInt("flag", ApiResponseFlags.HEATMAP_DATA.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							Log.i("fetchHeatmapData", ">message=" + message);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
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
					if(region.getDriverFareFactor() != null){
						addPolygon(arrLatLng, region.getDriverFareFactor(), region.getDriverFareFactorPriority(),
								region.getColor(), region.getStrokeColor());
					} else {
						addPolygonWithoutMarker(arrLatLng, region.getDriverFareFactorPriority(),
								region.getColor(), region.getStrokeColor());
					}
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
			if(fareFactor > 0) {
				CustomMapMarkerCreator.addTextMarkerToMap(this, map,
						latLngBounds.getCenter(),
						decimalFormat.format(fareFactor), 2, 20);
			}

			map.addPolygon(polygonOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPolygonWithoutMarker(ArrayList<LatLng> arg, int zIndex, String color, String strokeColor) {
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

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));

			textHead.setText(getResources().getString(R.string.alert));
			textMessage.setText(getResources().getString(R.string.logout_text));

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));

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
		try {
			if(customerInfo.getIsDelivery() == 1){
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.createCustomMarkerBitmap(HomeActivity.this, assl, 51f, 70f, R.drawable.ic_delivery_pickup_marker)));
			}
			else if(customerInfo.getIsPooled() == 1){
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.setBitmapColor(CustomMapMarkerCreator
								.createCustomMarkerBitmap(HomeActivity.this, assl, 30f, 72f, R.drawable.ic_pool_marker),
								customerInfo.getColor(), activity.getResources().getColor(R.color.themeColor))));
			}
			else{
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.createCustomMarkerBitmap(HomeActivity.this, assl, 50f, 69f, R.drawable.passenger)));
			}
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
		return map.addMarker(markerOptions);
	}

	public Marker addCustomerCurrentLocationMarker(GoogleMap map, CustomerInfo customerInfo) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(String.valueOf(customerInfo.getEngagementId()));
		markerOptions.snippet("");
		markerOptions.position(customerInfo.currentLatLng);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
				.createCustomMarkerBitmap(HomeActivity.this, assl, 20f, 20f, R.drawable.red_dot_icon)));
		return map.addMarker(markerOptions);
	}


	Timer timerPathRerouting;
	TimerTask timerTaskPathRerouting;


	public void startTimerPathRerouting() {
		cancelTimerPathRerouting();
		try {
			timerPathRerouting = new Timer();

			timerTaskPathRerouting = new TimerTask() {

				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									if(driverScreenMode == DriverScreenMode.D_IN_RIDE
											&& Data.getCurrentCustomerInfo().getIsDelivery() == 1
											&& Data.getCurrentCustomerInfo().getIsDeliveryPool() != 1){
										setDeliveryMarkers();
									} else if(Data.getCurrentCustomerInfo().getIsDeliveryPool() == 1){
										setAttachedDeliveryPoolMarkers(false);
									}else {
										setAttachedCustomerMarkers(false);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			timerPathRerouting.scheduleAtFixedRate(timerTaskPathRerouting, 1000, 30000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cancelTimerPathRerouting() {
		try {
			if (timerTaskPathRerouting != null) {
				timerTaskPathRerouting.cancel();
				timerTaskPathRerouting = null;
			}

			if (timerPathRerouting != null) {
				timerPathRerouting.cancel();
				timerPathRerouting.purge();
				timerPathRerouting = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void startRidePopup(final Activity activity, final CustomerInfo customerInfo) {
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons_tour);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));

			RelativeLayout tour_layout = (RelativeLayout) dialog.findViewById(R.id.tour_layout);
			TextView tour_textView = (TextView) dialog.findViewById(R.id.tour_textView);
			tour_textView.setTypeface(Fonts.mavenRegular(activity));
			ImageView cross_tour = (ImageView) dialog.findViewById(R.id.cross_tour);


			if(customerInfo.getIsDelivery()==1){
				textMessage.setText(getResources().getString(R.string.start_delivery_text));
			}else{
				textMessage.setText(getResources().getString(R.string.start_ride_text));
				textMessage.setText(getResources().getString(R.string.start_ride_text, customerInfo.getName()));
			}

			if(isTourFlag){
				tour_layout.setVisibility(View.VISIBLE);
			} else {
				tour_layout.setVisibility(View.GONE);
			}
			tour_textView.setText(getResources().getString(R.string.tutorial_tap_ok));

			Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.mavenRegular(activity));
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			Button btnEnterToll = (Button) dialog.findViewById(R.id.btnEnterToll);
			btnEnterToll.setVisibility(View.GONE);
			TextView tvTollValue = (TextView) dialog.findViewById(R.id.tvTollValue);
			tvTollValue.setVisibility(View.GONE);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START + "_" + FirebaseEvents.CONFIRM_YES, null);
					if (myLocation != null) {
						dialog.dismiss();
						LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
						double displacement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getRequestlLatLng());
						double actualDisplacement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getCurrentLatLng());

						if (customerInfo.getIsDelivery() == 1
								|| displacement <= DRIVER_START_RIDE_CHECK_METERS
								|| actualDisplacement <= DRIVER_START_RIDE_CHECK_METERS) {
							buildAlertMessageNoGps();
							if(isTourFlag) {
								setTourOperation(3);
							} else {
								GCMIntentService.clearNotifications(activity);

								driverStartRideAsync(activity, driverAtPickupLatLng, customerInfo);
							}


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
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START+"_"+FirebaseEvents.CONFIRM_NO,null);
					dialog.dismiss();
					FlurryEventLogger.event(START_RIDE_NOT_CONFIRMED);
					if(isTourFlag) {
						handleTourView(isTourFlag, getString(R.string.tutorial_tap_to_start_ride));
					}
				}

			});

			cross_tour.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
//					handleTourView(false, "");
					dialog.dismiss();
					tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 2);
				}
			});

			dialog.show();
			if(isTourFlag) {
				handleTourView(isTourFlag, getString(R.string.tutorial_tap_ok));
			}
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
				dialogEndRidePopup.setContentView(R.layout.dialog_custom_two_buttons_tour);

				RelativeLayout frameLayout = (RelativeLayout) dialogEndRidePopup.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);

				WindowManager.LayoutParams layoutParams = dialogEndRidePopup.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialogEndRidePopup.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialogEndRidePopup.setCancelable(false);
				dialogEndRidePopup.setCanceledOnTouchOutside(false);


				TextView textHead = (TextView) dialogEndRidePopup.findViewById(R.id.textHead);
				textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialogEndRidePopup.findViewById(R.id.textMessage);
				textMessage.setTypeface(Fonts.mavenRegular(activity));
				walletBalanceUpdatePopup = false;

				RelativeLayout tour_layout = (RelativeLayout) dialogEndRidePopup.findViewById(R.id.tour_layout);
				TextView tour_textView = (TextView) dialogEndRidePopup.findViewById(R.id.tour_textView);
				tour_textView.setTypeface(Fonts.mavenRegular(activity));
				ImageView cross_tour = (ImageView) dialogEndRidePopup.findViewById(R.id.cross_tour);

				if(isTourFlag){
					tour_layout.setVisibility(View.VISIBLE);
				} else {
					tour_layout.setVisibility(View.GONE);
				}
				tour_textView.setText(getResources().getString(R.string.tutorial_tap_ok_endride));

				textMessage.setText(getResources().getString(R.string.end_ride_text));


				Button btnOk = (Button) dialogEndRidePopup.findViewById(R.id.btnOk);
				btnOk.setTypeface(Fonts.mavenRegular(activity));
				Button btnCancel = (Button) dialogEndRidePopup.findViewById(R.id.btnCancel);
				btnCancel.setTypeface(Fonts.mavenRegular(activity));
				final Button btnEnterToll = (Button) dialogEndRidePopup.findViewById(R.id.btnEnterToll);
				btnEnterToll.setVisibility(JSONParser.isTagEnabled(activity, Constants.KEY_SHOW_TOLL_CHARGE) ? View.VISIBLE : View.GONE);
				final TextView tvTollValue = (TextView) dialogEndRidePopup.findViewById(R.id.tvTollValue);
				tvTollValue.setVisibility(View.GONE);

				btnOk.setOnClickListener(new View.OnClickListener() {
					@SuppressWarnings("unused")
					@Override
					public void onClick(View view) {
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_END_RIDE+"_"+FirebaseEvents.CONFIRM_YES, null);
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
						customerInfo.setTollFare(0.0);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_END_RIDE+"_"+FirebaseEvents.CONFIRM_NO, null);
						dialogEndRidePopup.dismiss();
						FlurryEventLogger.event(END_RIDE_NOT_CONFIRMED);
						if(isTourFlag){
							handleTourView(isTourFlag, getString(R.string.tutorial_tap_end_ride));
						}
					}

				});

				View.OnClickListener clickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						new EnterTollDialog(HomeActivity.this).show(customerInfo.getTollFare(), new EnterTollDialog.Callback() {
							@Override
							public void tollEntered(double tollValue) {
								tvTollValue.setVisibility(View.VISIBLE);
								btnEnterToll.setVisibility(View.GONE);
								customerInfo.setTollFare(tollValue);
								tvTollValue.setText(getString(R.string.toll_value, Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(), tollValue)));
							}
						});
					}
				};

				btnEnterToll.setOnClickListener(clickListener);
				tvTollValue.setOnClickListener(clickListener);

				cross_tour.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
//						handleTourView(false, "");
					}
				});

				dialogEndRidePopup.show();
				if(isTourFlag) {
					handleTourView(true, getString(R.string.tutorial_tap_ok_endride));
				}
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
						driverEndRideWithDistanceSafetyCheck(activity, oldGPSLatLng, locationToUse.getLatitude(), locationToUse.getLongitude(),
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

					Response responseR = GoogleRestApis.getDistanceMatrix(source.latitude + "," + source.longitude,
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
							driverEndRideWithDistanceSafetyCheck(activity, source, destination.latitude, destination.longitude,
									flagDistanceTravelled, customerInfo);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					DialogPopup.dismissLoadingDialog();
					driverEndRideWithDistanceSafetyCheck(activity, source, destination.latitude, destination.longitude,
							flagDistanceTravelled, customerInfo);
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
				stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));

				Intent intent = new Intent(HomeActivity.this, DriverSplashActivity.class);
				intent.putExtra("no_anim", "yes");
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		}
		if (hasFocus) {
			LocationInit.showLocationAlertDialog(this);
//			buildAlertMessageNoGps();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);

			if(requestCode==REQUEST_CODE_TERMS_ACCEPT){
					if(resultCode==RESULT_OK){
						//relativeLayoutAutosOn.performClick();
					}

				return;
			}



			if(requestCode == 12){
				boolean state = data.getBooleanExtra("result", true);
				if(deliveryInfolistFragVisibility && state){
//					deliveryInfolistFragVisibility =false;
//					if(getSupportFragmentManager().findFragmentByTag(DeliveryInfosListInRideFragment.class.getName()) != null){
//						onBackPressed();
//					}
				}
			}

			if(requestCode == 14){
				boolean state = data.getBooleanExtra("result", true);
				if(state){
					relativeLayoutTour.performClick();
				}
			}

			if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
				if (0 == resultCode) {
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void showAllRideRequestsOnMap() {

		try {
			if (userMode == UserMode.DRIVER) {

//				if(map != null){
//					map.clear();
//				}

				for(Marker marker : requestMarkers){
					marker.remove();
				}
				requestMarkers.clear();
				boolean showHeatMap = true;
				ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForStatus(
						EngagementStatus.REQUESTED.getOrdinal());

				if (customerInfos.size() > 0) {

					for (int i = 0; i < customerInfos.size(); i++) {
						if(customerInfos.get(i).getIsDelivery() == 1){
							showHeatMap =false;
						}
					}

					if(showHeatMap){
						drawHeatMapData(heatMapResponseGlobal);
					}

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

				if(driverScreenMode == DriverScreenMode.D_INITIAL && myLocation != null && requestMarkers.size() == 1){
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
					builder.include(requestMarkers.get(0).getPosition());
					LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);
					final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
					int top = (int) (430f * ASSL.Yscale());
					map.setPadding(0, top, 0, 0);
					map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 300, null);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							map.setPadding(0, 0, 0, 0);
						}
					}, 1000);
				} else{
					map.setPadding(0, 0, 0, 0);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onNewRideRequest(int perfectRide, int isPooled, int isDelivery) {
		if (driverScreenMode == DriverScreenMode.D_INITIAL
				|| driverScreenMode == DriverScreenMode.D_RIDE_END
				|| perfectRide == 1
				|| isPooled == 1
				|| isDelivery == 1) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showAllRideRequestsOnMap();
					try {
						drawerLayout.closeDrawer(GravityCompat.START);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_RECEIVED + "_" + Data.getAssignedCustomerInfosListForStatus(
								EngagementStatus.REQUESTED.getOrdinal()).size(), null);
					} catch (Exception e) {
						e.printStackTrace();
					}

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
							setPannelVisibility(true);
							switchDriverScreen(driverScreenMode);
							DialogPopup.alertPopup(HomeActivity.this, "", getResources().getString(R.string.user_cancel_request));
						}
					} else if (driverScreenMode == DriverScreenMode.D_IN_RIDE) {
						setPannelVisibility(false);
						removePRMarkerAndRefreshList();
					} else if (driverScreenMode == DriverScreenMode.D_INITIAL) {
						setPannelVisibility(true);
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
				setPannelVisibility(true);
				if (driverScreenMode == DriverScreenMode.D_IN_RIDE) {
					setPannelVisibility(false);
					removePRMarkerAndRefreshList();
				}
				if(isTourFlag) {
					handleTourView(false, "");
					tourCompleteApi("2", engagementId);
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
	public void onChangeStatePushReceived(final int flag, final String engagementId, final String message, final int playRing) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
						try {
							new ApiSendCallLogs().sendCallLogs(HomeActivity.this, Data.userData.accessToken,
									engagementId, Data.getCustomerInfo(engagementId).getPhoneNumber());
							stopRing(true, HomeActivity.this);
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
					if (playRing == 1) {
						startRing(HomeActivity.this);
						DialogPopup.alertPopupWithListener(HomeActivity.this, "", getResources().getString(R.string.auto_accept_message), new OnClickListener() {
							@Override
							public void onClick(View v) {
								stopRing(true, HomeActivity.this);
							}
						});
					}

						callAndHandleStateRestoreAPI();
					if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
						setPannelVisibility(true);
						perfectRidePassengerInfoRl.setVisibility(View.GONE);
						driverPassengerInfoRl.setVisibility(View.VISIBLE);
						if(!"".equalsIgnoreCase(message)){
							cancelationMessage = message;
							rideCancelledByCustomer = true;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				removePRMarkerAndRefreshList();
			}
		});
	}

	public void startRing(Context context) {
		try {

			stopRing(true, context);
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			if (vibrator.hasVibrator()) {
				long[] pattern = {0, 1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900,
						1350, 3900};
				vibrator.vibrate(pattern, 1);
			}
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
				am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
			}else{
				mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
			}

			mediaPlayer.setLooping(true);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					try {
						vibrator.cancel();
						mediaPlayer.stop();
						mediaPlayer.reset();
						mediaPlayer.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CountDownTimer ringStopTimer;
	public void stopRing(boolean manual, Context context) {
		boolean stopRing;
		if (HomeActivity.appInterruptHandler != null) {
			if (Data.getAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal()) != null
					&& Data.getAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal()).size() > 0) {
				stopRing = false;
			} else {
				stopRing = true;
			}
		} else {
			stopRing = true;
		}
		if (manual) {
			stopRing = true;
		}
		if (stopRing) {
			try {
				if (vibrator != null) {
					vibrator.cancel();
				}
				if (mediaPlayer != null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.reset();
					mediaPlayer.release();
				}
				if (ringStopTimer != null) {
					ringStopTimer.cancel();
				}
				RingData ringData = Database2.getInstance(context).getRingData("1");
				long timeDiff = System.currentTimeMillis() - ringData.time;
				Database2.getInstance(context).updateRingData(ringData.engagement, String.valueOf(timeDiff));
			} catch (Exception e) {
			}
		}
	}



	public static void logoutUser(final Activity cont, final LogoutCallback callback) {
		try {

			try {
				SharedPreferences pref = cont.getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
					SharedPreferences pref = cont.getSharedPreferences("myPref", Context.MODE_PRIVATE);
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
								if(callback == null || callback.redirectToSplash()){
									Intent intent = new Intent(cont, DriverSplashActivity.class);
									intent.putExtra("no_anim", "yes");
									cont.startActivity(intent);
									cont.finish();
									cont.overridePendingTransition(
											R.anim.left_in,
											R.anim.left_out);
								}
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
		try {
			super.onStart();
			FlurryAgent.init(this, Data.FLURRY_KEY);
			FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
			FlurryAgent.onEvent("HomeActivity started");
			mGoogleApiClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		try {
			mGoogleApiClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
//							try {
//								if(Data.getCurrentCustomerInfo() != null){
//									if(Data.getCurrentCustomerInfo().getDeliveryInfos().size()>0){
//										setDeliveryPos(getDeliveryPos());
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
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
								GCMIntentService.stopRing(true, HomeActivity.this);
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
			HomeUtil.putDefaultParams(params);

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
								HomeActivity.logoutUser(activity, null);
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
	public void onDropLocationUpdated(final String engagementId, final LatLng dropLatLng, final String dropAddress) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				updateDropLatLngandPath(engagementId, dropLatLng, dropAddress);
			}
		});
	}


	private void updateDropLatLngandPath(String engagementId, LatLng dropLatLng, String dropAddress) {
		try {
			Data.getCustomerInfo(engagementId).setDropLatLng(dropLatLng);
			Data.getCustomerInfo(engagementId).setDropAddress(dropAddress);
			customerSwitcher.setCustomerData(Integer.parseInt(engagementId));
			setAttachedCustomerMarkers(true);
			setInRideZoom();
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
								customerInfo.getTotalWaitTime(customerRideDataGlobal.getWaitTime(HomeActivity.this), HomeActivity.this));
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
						map.addPolyline(polylineOptions.width(ASSL.Xscale() * 8).color(MAP_PATH_COLOR).geodesic(true));
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
					polylineOptions.width(ASSL.Xscale() * 8);
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
	public void handleCancelRideSuccess(final String engagementId, final String message) {
		runOnUiThread(new Runnable() {
						  @Override
						  public void run() {
							  if (map != null) {
								  map.clear();
							  }
							  stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));

							  reduceRideRequest(engagementId, EngagementStatus.ACCEPTED.getOrdinal(), message);
							  reduceRideRequest(engagementId, EngagementStatus.ARRIVED.getOrdinal(), message);
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
				if(!isTourFlag) {
					GCMIntentService.clearNotifications(activity);
					driverMarkArriveRideAsync(activity, latLng, Data.getCustomerInfo(String.valueOf(engagementId)));
				}
			}
		});
	}


	@Override
	public void notifyArrivedButton(final boolean arrived, final int engagementId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(Data.getCurrentEngagementId().equalsIgnoreCase(String.valueOf(engagementId))) {
						LatLng driverAtPickupLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
						CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
						double actualDispalcement = MapUtils.distance(driverAtPickupLatLng, customerInfo.getCurrentLatLng());
						double arrivingDistance = Prefs.with(context).getInt(Constants.KEY_DRIVER_SHOW_ARRIVE_UI_DISTANCE, 600);
						if (arrived || actualDispalcement < arrivingDistance) {
							buttonMarkArrived.setText(getResources().getString(R.string.arrived));
							buttonMarkArrived.setEnabled(true);
							buttonMarkArrived.setAlpha(1f);
							if(isTourFlag){
								handleTourView(isTourFlag, getString(R.string.tutorial_tap_arrived_if_at_pickup));
							}
						} else {
							buttonMarkArrived.setText(getResources().getString(R.string.arrivingdot));
							buttonMarkArrived.setEnabled(false);
							buttonMarkArrived.setAlpha(0.8f);
							if(isTourFlag){
								handleTourView(isTourFlag, getString(R.string.tutorial_driver_to_pickup_point));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void showStartRidePopup(){
		 runOnUiThread(new Runnable() {
			 @Override
			 public void run() {
				 DialogPopup.dialogNewBanner(HomeActivity.this, getResources().getString(R.string.start_ride_alert), 10000);
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
			textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.mavenRegular(activity));
			TextView timeOutText = (TextView) dialog.findViewById(R.id.timeOutText);
			timeOutText.setTypeface(Fonts.mavenRegular(activity));
			final TextView timeOutValue = (TextView) dialog.findViewById(R.id.timeOutValue);
			timeOutValue.setTypeface(Fonts.mavenRegular(activity));

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
			GCMIntentService.stopRing(true, activity);
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



	Handler inRideMapZoomHandler = new Handler();
	Runnable inRideMapZoomRunnable = new Runnable() {
		@Override
		public void run() {
			if (myLocation != null) {
				if(driverScreenMode == DriverScreenMode.D_IN_RIDE) {
					if (!Prefs.with(HomeActivity.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").contains(" ")) {
						inPerfectRideZoom();
					} else{
						inRideZoom();
					}

				} else if(driverScreenMode == DriverScreenMode.D_ARRIVED || driverScreenMode == DriverScreenMode.D_START_RIDE){
					arrivedOrStartStateZoom();
				}
				inRideMapZoomHandler.postDelayed(inRideMapZoomRunnable, 10000);
			}
		}
	};


	public void inRideZoom(){
		try {
			if(myLocation!=null) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();

				if(Data.getCurrentCustomerInfo().getIsDelivery() == 1){

					int j = getDeliveryPos();
					builder.include(markersDelivery.get(j).getPosition());

					if(polylineDelivery != null){
						for (LatLng latLng : polylineDelivery.getPoints()) {
							builder.include(latLng);
						}
					}

				} else {
					for (int i = 0; i < markersCustomers.size(); i++) {
						builder.include(markersCustomers.get(i).getPosition());
						break;
					}
					if(polylineCustomersPath != null){
						for (LatLng latLng : polylineCustomersPath.getPoints()) {
							builder.include(latLng);
						}
					}
				}
				builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);
				final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
				int top = (int) (220f * ASSL.Yscale());
				int bottom = (int) (364f * ASSL.Yscale());
				map.setPadding(0, top, 0, bottom);
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (80 * minScaleRatio)), 300, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void inPerfectRideZoom(){
		try {
			if(myLocation!=null) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();

				if(Data.getCurrentCustomerInfo().getIsDelivery() == 1){
					for (int i = 0; i < markersDelivery.size(); i++) {
						if(markersDelivery.get(i).getSnippet().equalsIgnoreCase("return")){
							builder = new LatLngBounds.Builder();
							builder.include(markersDelivery.get(i).getPosition());
						} else{
							builder.include(markersDelivery.get(i).getPosition());
						}
					}
				} else {
					for (int i = 0; i < markersCustomers.size(); i++) {
						builder.include(markersCustomers.get(i).getPosition());
						break;
					}
					if(polylineCustomersPath != null){
						for (LatLng latLng : polylineCustomersPath.getPoints()) {
							builder.include(latLng);
						}
					}
				}
				builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 100);
				final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
				int top = (int) (222f * ASSL.Yscale());
				int bottom = (int) (344f * ASSL.Yscale());
				map.setPadding(0, top, 0, bottom);
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (80 * minScaleRatio)), 300, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void arrivedOrStartStateZoom(){
		try {
			if(myLocation!=null) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				for (int i = 0; i < markersCustomers.size(); i++) {
					builder.include(markersCustomers.get(i).getPosition());
					break;
				}
				if(polylineCustomersPath != null){
					for (LatLng latLng : polylineCustomersPath.getPoints()) {
						builder.include(latLng);
					}
				}
				LatLngBounds bounds = MapLatLngBoundsCreator.createBoundsWithMinDiagonal(builder, 80);
				final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
				int top = (int) (169f * ASSL.Yscale());
				int bottom = (int) (400f * ASSL.Yscale());
				map.setPadding(0, top, 0, bottom);
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (80 * minScaleRatio)), 300, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  void setInRideZoom(){
		inRideMapZoomHandler.removeCallbacks(inRideMapZoomRunnable);
		inRideMapZoomHandler.postDelayed(inRideMapZoomRunnable, 1000);
	}
	public void perfectRideRequestRegion(double currentDropDist) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(KEY_DISTANCE, String.valueOf(currentDropDist));
				HomeUtil.putDefaultParams(params);

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
		try {
				String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
				Log.i(TAG, "callbackUpdateWalletBalance response = " + responseStr);
				if(Data.userData.walletUpdateTimeout > (System.currentTimeMillis()-walletUpdateCallTime)) {
					JSONObject jObj = new JSONObject(responseStr);
					int flag = jObj.getInt(KEY_FLAG);
					if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
						try {
							String pickupTime = jObj.optString(KEY_PICKUP_TIME, "");
							if(!"".equalsIgnoreCase(pickupTime)){
								long rideTimeInMillisFromDB = Database2.getInstance(activity).getCustomerElapsedRideTime(customerInfo.getEngagementId());
								long pickupTimeMillis = DateOperations.getMilliseconds(DateOperations.utcToLocalTZ(pickupTime));
								if(rideTimeInMillisFromDB <= 0){
									Database2.getInstance(HomeActivity.this).deleteCustomerRideDataForEngagement(customerInfo.getEngagementId());
									Database2.getInstance(HomeActivity.this).insertCustomerRideData(customerInfo.getEngagementId(),
											pickupTimeMillis);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

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
				HomeUtil.putDefaultParams(params);
				Log.i("params", "=" + params);

				walletUpdateCallTime = System.currentTimeMillis();
				DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
				RestClient.getApiServices().updateWalletBalance(params, getCallbackUpdateWalletBalance(customerInfo));
				walletBalanceUpdatePopup = true;
				startWalletUpdateTimeout(customerInfo);
			} else {
				DialogPopup.alertPopup(HomeActivity.this, "", Data.CHECK_INTERNET_MSG);
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
				try {
					customerSwitcher.updateList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setTextViewRideInstructions(){
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
				textViewRideInstructions.setVisibility(View.GONE);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textViewRideInstructions.getLayoutParams();
//				textViewRideInstructionsInRide.setVisibility(View.GONE);
				if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
					layoutParams.setMargins((int)(18f*ASSL.Xscale()), 0, 0, 0);
					layoutParams.setMarginStart((int)(18f*ASSL.Xscale()));
					layoutParams.setMarginEnd(0);
					textViewRideInstructions.setVisibility(View.VISIBLE);
					textViewRideInstructions.setText(getResources().getString(R.string.arrive_at_pickup_location));
				}
				else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
					layoutParams.setMargins((int)(18f*ASSL.Xscale()), 0, 0, 0);
					textViewRideInstructions.setVisibility(View.VISIBLE);
					textViewRideInstructions.setText(getResources().getString(R.string.start_the_delivery));
				}
				else if (DriverScreenMode.D_IN_RIDE == driverScreenMode) {
					layoutParams.setMargins((int)(18f*ASSL.Xscale()), 0, 0, (int)(5f*ASSL.Yscale()));
					layoutParams.setMarginStart((int)(18f*ASSL.Xscale()));
					layoutParams.setMarginEnd(0);
					textViewRideInstructions.setVisibility(View.VISIBLE);
					for(int i=0; i<customerInfo.getDeliveryInfos().size(); i++){
						if(customerInfo.getDeliveryInfos().get(i).getStatus()
								== DeliveryStatus.PENDING.getOrdinal()){
							textViewRideInstructions.setText(getResources().getString(R.string.delivery_route));
							return;
						} else if(customerInfo.getDeliveryInfos().get(i).getStatus()
								== DeliveryStatus.RETURN.getOrdinal()){
							textViewRideInstructions.setText(getResources().getString(R.string.delivery_route));
							return;
						}
					}
//					textViewRideInstructions.setText(getResources().getString(R.string.all_orders_have_been_delivered));
				}
				textViewRideInstructions.setLayoutParams(layoutParams);
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
		try {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) driverEndRideBtn.getLayoutParams();
			if(isDefault) {
				params.width = (int) (getResources().getDimension(R.dimen.button_width_big_new) * ASSL.Xscale());
				params.setMarginStart(0);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				driverEndRideBtn.setLayoutParams(params);
				driverEndRideBtn.setBackgroundResource(R.drawable.menu_black_btn_selector);
			} else{
				params.width = (int) (getResources().getDimension(R.dimen.button_width_big_extra_new) * ASSL.Xscale());
				params.setMarginStart((int) (30f * ASSL.Xscale()));
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				driverEndRideBtn.setLayoutParams(params);
				driverEndRideBtn.setBackgroundResource(R.drawable.orange_btn_selector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setMakeDeliveryButtonState(boolean isDefault){
		try {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) buttonMakeDelivery.getLayoutParams();
			if(isDefault) {
				params.width = (int) (getResources().getDimension(R.dimen.button_width_big_new) * ASSL.Xscale());
				params.setMarginEnd(0);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
					params.removeRule(RelativeLayout.ALIGN_PARENT_END);
				} else{
					params.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
				}
				buttonMakeDelivery.setLayoutParams(params);
				buttonMakeDelivery.setText(getResources().getString(R.string.make_delivery));
				buttonMakeDelivery.setTextSize(TypedValue.COMPLEX_UNIT_PX, 36f * ASSL.Xscale());
			} else{
				params.width = (int) (getResources().getDimension(R.dimen.button_width_small) * ASSL.Xscale());
				params.setMarginEnd((int) (30f * ASSL.Xscale()));
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
					params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
				} else{
					params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				}
				params.addRule(RelativeLayout.ALIGN_PARENT_END);
				buttonMakeDelivery.setLayoutParams(params);
				buttonMakeDelivery.setText(getResources().getString(R.string.view_orders));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDeliveryPos(){
		try {
			int index = deliveryListHorizontal.getCurrentItem();
			return index;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public void setDeliveryPos(int index) {
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			deliveryListHorizontal.setCurrentItem(index);
			int prev = Prefs.with(HomeActivity.this).getInt(SPLabels.DELIVERY_IN_PROGRESS, -1);
			if (prev > -1 && markersDelivery.size() > 0) {
				DeliveryInfo deliveryInfo = customerInfo.getDeliveryInfos().get(prev);
				Marker oldMarker = markersDelivery.get(prev);
				if (deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {
					oldMarker.setIcon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
							.getTextBitmap(this, assl, String.valueOf(prev + 1), 18, 1)));
				} else {
					oldMarker.setIcon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
							.getTextBitmap(this, assl, String.valueOf(prev + 1), 18, 3)));
				}
			}

			DeliveryInfo newDeliveryInfo = customerInfo.getDeliveryInfos().get(index);
			if (newDeliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal() && markersDelivery.size() > 0) {
				Prefs.with(HomeActivity.this).save(SPLabels.DELIVERY_IN_PROGRESS, index);
				Marker marker = markersDelivery.get(index);
				marker.setIcon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.getTextBitmap(this, assl, String.valueOf(index + 1), 18, 2)));
			} else {
				Prefs.with(HomeActivity.this).save(SPLabels.DELIVERY_IN_PROGRESS, index);
			}
			deliveryInfoTabs.notifyDatasetchange(false);
			if(polylineDelivery != null){
				polylineDelivery.remove();
				polylineDelivery = null;
			}
			inRideZoom();
			if(System.currentTimeMillis() > (refreshPolyLineDelay + 5000)) {
				refreshPolyLineDelay = System.currentTimeMillis();
				setDeliveryMarkers();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private ArrayList<Marker> markersDelivery = new ArrayList<>();
	private void clearDeliveryMarkers(){
		for(Marker marker : markersDelivery){
			marker.remove();
		}
		markersDelivery.clear();
	}

	private void addDeliveryMarker(Marker marker){
		markersDelivery.add(marker);
	}

	public void setDeliveryMarkers(){
		try {
			clearDeliveryMarkers();
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if(customerInfo.getIsDelivery() == 1
					&& customerInfo.getIsDeliveryPool() != 1
					&& customerInfo.getDeliveryInfos() != null
					&& customerInfo.getDeliveryInfos().size() > 0){
				final String engagementId = String.valueOf(customerInfo.getEngagementId());
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
				} catch (Exception e){

				}

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

						if(deliveryInfo.getStatus() == DeliveryStatus.RETURN.getOrdinal()) {
							latLngs.add(latLng);
							builder.include(latLng);
							addDeliveryMarker(addDropPinMarker(map, latLng, "R", 2));
						} else{

							if(-1 == Prefs.with(HomeActivity.this).getInt(SPLabels.DELIVERY_IN_PROGRESS, -1)) {
								if (!latLngs.contains(latLng)) {
									latLngs.add(latLng);
									builder.include(latLng);
								} else {
									latLng = new LatLng(latLng.latitude, latLng.longitude + 0.0004d * (double) (counterMap.get(latLng)));
								}
							} else {

								if(deliveryInfo.getIndex() == Prefs.with(HomeActivity.this).getInt(SPLabels.DELIVERY_IN_PROGRESS, -1)
										&& deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal()) {

									LatLng newLatLng = customerInfo.getDeliveryInfos().get(i).getLatLng();
										latLngs.add(newLatLng);
										builder.include(newLatLng);
								}

							}

							if(deliveryInfo.getStatus() == DeliveryStatus.PENDING.getOrdinal() && customerInfo.getFalseDeliveries() !=1){

								if (deliveryInfo.getIndex() == Prefs.with(HomeActivity.this).getInt(SPLabels.DELIVERY_IN_PROGRESS, 0)){
									addDeliveryMarker(addDropPinMarker(map, latLng, String.valueOf(deliveryInfo.getIndex() + 1), 2));
								} else {
									addDeliveryMarker(addDropPinMarker(map, latLng, String.valueOf(deliveryInfo.getIndex() + 1), 1));
								}
							}
							else if((deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal() ||
									deliveryInfo.getStatus() == DeliveryStatus.CANCELLED.getOrdinal())&& customerInfo.getFalseDeliveries() !=1){
								addDeliveryMarker(addDropPinMarker(map, latLng, String.valueOf(deliveryInfo.getIndex() + 1),3));
							}
						}
					}
				}

				try {
					if(reCreateDeliveryMarkers) {
						reCreateDeliveryMarkers = false;
						setDeliveryPos(getDeliveryPos());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


//				map.animateCamera(CameraUpdateFactory.newLatLngBounds(MyApplication.getInstance()
//								.getMapLatLngBoundsCreator().createBoundsWithMinDiagonal(builder, FIX_ZOOM_DIAGONAL),
//						(int) (630f * ASSL.Xscale()), (int) (630f * ASSL.Xscale()),
//						(int) (50f * ASSL.Xscale())), MAP_ANIMATION_TIME, null);

				if(latLngs.size() > 1) {
					new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.themeColorLight), false,
							new ApiGoogleDirectionWaypoints.Callback() {
								@Override
								public void onPre() {

								}

								@Override
								public boolean showPath() {
									return engagementId.equalsIgnoreCase(Data.getCurrentEngagementId())
											&& driverScreenMode == DriverScreenMode.D_IN_RIDE;
								}

								@Override
								public void polylineOptionGenerated(PolylineOptions polylineOptions) {
									polylineOptionsDelivery = polylineOptions;
								}

								@Override
								public void onFinish() {
									if(polylineOptionsDelivery != null){
										if(polylineDelivery != null){
											polylineDelivery.remove();
										}
										polylineDelivery = map.addPolyline(polylineOptionsDelivery);
										inRideZoom();
									}
								}
							}).execute();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private PolylineOptions polylineOptionsDelivery = null;
	private Polyline polylineDelivery = null;

	private Marker addDropPinMarker(GoogleMap map, LatLng latLng, String text, int imgType){
		final MarkerOptions markerOptions = new MarkerOptions()
				.position(latLng)
				.title("")
				.snippet("")
				.anchor(0.5f, 0.9f)
				.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.getTextBitmap(this, assl, text, 18, imgType)));
		return map.addMarker(markerOptions);
	}



	private Marker addReturnPinMarker(GoogleMap map, LatLng latLng){
		final MarkerOptions markerOptions = new MarkerOptions()
				.position(latLng)
				.title("")
				.snippet("return")
				.anchor(0.5f, 0.9f)
				.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator
						.createCustomMarkerBitmap(this, assl, 53f, 69f, R.drawable.blue_delivery_marker_burne)));
		return map.addMarker(markerOptions);
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
		double totalDistanceFromLogInMeter = 0;
		try {
			long startTime = Database2.getInstance(activity).getCustomerStartRideTime(customerInfo.getEngagementId());
//			totalDistanceFromLogInMeter = Database2.getInstance(activity).getLastRideData(customerInfo.getEngagementId(), 1).accDistance;
			totalDistanceFromLogInMeter = Database2.getInstance(activity).
					checkRideData(customerInfo.getEngagementId(), startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double distance = customerRideDataGlobal.getDistance(HomeActivity.this);
		if(distance - totalDistanceFromLogInMeter < 100){
			distance = totalDistanceFromLogInMeter;
		}
//		for(DeliveryInfo deliveryInfo : customerInfo.getDeliveryInfos()){
//			if(deliveryInfo.getStatus() != DeliveryStatus.PENDING.getOrdinal()){
//				distance = distance - deliveryInfo.getDistance();
//			}
//		}
		return distance;
	}

	public long getCurrentDeliveryWaitTime(CustomerInfo customerInfo){
		long waitTime = customerRideDataGlobal.getWaitTime(HomeActivity.this);
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



	private ArrayList<Marker> markersCustomers = new ArrayList<>();
	private void clearCustomerMarkers(){
		for(Marker marker : markersCustomers){
			marker.remove();
		}
		markersCustomers.clear();
	}

	private void addCustomerMarker(Marker marker){
		markersCustomers.add(marker);
	}


	public void setCustomerInstruction(CustomerInfo currentCustomer){
		String text = "";
		textViewRideInstructions.setVisibility(View.GONE);
		if (currentCustomer.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
			text = getResources().getString(R.string.please_drop_customer,
					currentCustomer.getName());
		} else if (currentCustomer.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()) {
			text = getResources().getString(R.string.please_reach_customer_location,
					currentCustomer.getName());
		} else if (currentCustomer.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
			text = getResources().getString(R.string.please_start_customer_ride,
					currentCustomer.getName());
		}
		textViewRideInstructions.setVisibility(View.VISIBLE);
		textViewRideInstructions.setText(text);
	}

	private ArrayList<CustomerInfo> setAttachedCustomerMarkers(final boolean sortList){
		ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();
		try {
			clearCustomerMarkers();
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


			final LatLng driverLatLngFinal = driverLatLng;
			Collections.sort(customerInfos, new Comparator<CustomerInfo>() {

				@Override
				public int compare(CustomerInfo lhs, CustomerInfo rhs) {
					try {
						LatLng lhsLatLng = null;
						if (lhs.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
							if(lhs.getIsDelivery() != 1  && lhs.getDropLatLng() != null) {
								lhsLatLng = lhs.getDropLatLng();
							}
						} else if (lhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| lhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
							lhsLatLng = lhs.getRequestlLatLng();
						}
						LatLng rhsLatLng = null;
						if (rhs.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
							if (rhs.getIsDelivery() != 1 && rhs.getDropLatLng() != null) {
								rhsLatLng = rhs.getDropLatLng();
							}
						} else if (rhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| rhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
							rhsLatLng = rhs.getRequestlLatLng();
						}

						if (!sortList && (lhs.getStatus() == rhs.getStatus())) {
							if (lhs.getEngagementId() == Integer.parseInt(Data.getCurrentEngagementId())) {
								return -1;
							} else if (rhs.getEngagementId() == Integer.parseInt(Data.getCurrentEngagementId())) {
								return 1;
							}
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

			for(int i=0; i<customerInfos.size(); i++){
				CustomerInfo customerInfo = customerInfos.get(i);
				LatLng latLng = null;

				if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
					if(customerInfo.getIsDelivery() != 1  && customerInfo.getDropLatLng() != null) {
						latLng = customerInfo.getDropLatLng();
					}
				} else if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
						|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
					latLng = customerInfo.getRequestlLatLng();
				}

				if(latLng != null
						&& Utils.compareDouble(latLng.latitude, 0) != 0
						&& Utils.compareDouble(latLng.longitude, 0) != 0) {
					if (counterMap.containsKey(latLng)) {
						counterMap.put(latLng, counterMap.get(latLng) + 1);
					} else {
						counterMap.put(latLng, 1);
					}
					if (customerInfo.getEngagementId() == Integer.parseInt(Data.getCurrentEngagementId())) {
						if (!latLngs.contains(latLng)) {
							latLngs.add(latLng);
							builder.include(latLng);
						} else {
							latLng = new LatLng(latLng.latitude, latLng.longitude + 0.0004d * (double) (counterMap.get(latLng)));
						}
						if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
							if (customerInfo.getIsDelivery() != 1 && customerInfo.getDropLatLng() != null) {
								addCustomerMarker(addDropPinMarker(map, latLng, customerInfos.size() > 1 ? "D" : "D", 2));
							}
						} else if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
							addCustomerMarker(addCustomerPickupMarker(map, customerInfo, latLng));
						}
					}
				}
			}

			if(latLngs.size() > 1) {
				new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.blue_polyline), false,
						new ApiGoogleDirectionWaypoints.Callback() {
							@Override
							public void onPre() {

							}

							@Override
							public boolean showPath() {
								return Data.getAssignedCustomerInfosListForEngagedStatus().size() > 0;
							}

							@Override
							public void polylineOptionGenerated(PolylineOptions polylineOptions) {
								polylineOptionsCustomersPath = polylineOptions;

							}

							@Override
							public void onFinish() {
								if(DriverScreenMode.D_START_RIDE != driverScreenMode
										&& polylineOptionsCustomersPath != null){
									if(polylineCustomersPath != null){
										polylineCustomersPath.remove();
									}
									polylineCustomersPath = map.addPolyline(polylineOptionsCustomersPath);
									arrivedOrStartStateZoom();
								}
							}
						}).execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerInfos;
	}

	private ArrayList<CustomerInfo> setAttachedDeliveryPoolMarkers(final boolean sortList) {
		ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();
		try {
			clearCustomerMarkers();
			ArrayList<LatLng> latLngs = new ArrayList<>();
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			HashMap<LatLng, Integer> counterMap = new HashMap<>();
			LatLng driverLatLng = null;
			try {
				if (myLocation != null) {
					driverLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				} else if (Utils.compareDouble(Data.latitude, 0) != 0 && Utils.compareDouble(Data.longitude, 0) != 0) {
					driverLatLng = new LatLng(Data.latitude, Data.longitude);
				}
				if (driverLatLng != null) {
					builder.include(driverLatLng);
					latLngs.add(driverLatLng);
				}
			} catch (Exception e) {
			}


			final LatLng driverLatLngFinal = driverLatLng;
			Collections.sort(customerInfos, new Comparator<CustomerInfo>() {

				@Override
				public int compare(CustomerInfo lhs, CustomerInfo rhs) {
					try {
						LatLng lhsLatLng = null;
						if (lhs.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
							if (lhs.getDeliveryInfos().size() > 1) {
								lhsLatLng = lhs.getDeliveryInfos().get(1).getLatLng();
							} else {
								lhsLatLng = lhs.getDeliveryInfos().get(0).getLatLng();
							}
						} else if (lhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| lhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
							lhsLatLng = lhs.getRequestlLatLng();
						}
						LatLng rhsLatLng = null;
						if (rhs.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
							if (rhs.getDeliveryInfos().size() > 1) {
								rhsLatLng = rhs.getDeliveryInfos().get(1).getLatLng();
							} else {
								rhsLatLng = rhs.getDeliveryInfos().get(0).getLatLng();
							}
						} else if (rhs.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
								|| rhs.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
							rhsLatLng = rhs.getRequestlLatLng();
						}

						if (!sortList && (lhs.getStatus() == rhs.getStatus())) {
							if (lhs.getEngagementId() == Integer.parseInt(Data.getCurrentEngagementId())) {
								return -1;
							} else if (rhs.getEngagementId() == Integer.parseInt(Data.getCurrentEngagementId())) {
								return 1;
							}
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

			for (int i = 0; i < customerInfos.size(); i++) {
				CustomerInfo customerInfo = customerInfos.get(i);
				LatLng latLng = null;

				if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
					if (customerInfo.getDeliveryInfos().size() > 1) {
						latLng = customerInfo.getDeliveryInfos().get(1).getLatLng();
					} else {
						latLng = customerInfo.getDeliveryInfos().get(0).getLatLng();
					}
				} else if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
						|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
					latLng = customerInfo.getRequestlLatLng();
				}

				if (latLng != null
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
					if (customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {

						if(customerInfo.engagementId == Integer.parseInt(Data.getCurrentEngagementId())){
							if (customerInfo.getDeliveryInfos().size() > 1) {
								addCustomerMarker(addDropPinMarker(map, latLng, "R", 2));
							} else {
								addCustomerMarker(addDropPinMarker(map, latLng, "D", 2));
							}
						} else {
							if (customerInfo.getDeliveryInfos().size() > 1) {
								addCustomerMarker(addDropPinMarker(map, latLng, "R", 1));
							} else {
								addCustomerMarker(addDropPinMarker(map, latLng, "D", 1));
							}
						}
					} else if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
							|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()) {
						addCustomerMarker(addCustomerPickupMarker(map, customerInfo, latLng));
					}
				}
			}

			if (latLngs.size() > 1) {
				new ApiGoogleDirectionWaypoints(latLngs, getResources().getColor(R.color.blue_polyline), false,
						new ApiGoogleDirectionWaypoints.Callback() {
							@Override
							public void onPre() {

							}

							@Override
							public boolean showPath() {
								return Data.getAssignedCustomerInfosListForEngagedStatus().size() > 0;
							}

							@Override
							public void polylineOptionGenerated(PolylineOptions polylineOptions) {
								polylineOptionsCustomersPath = polylineOptions;

							}

							@Override
							public void onFinish() {
								if (DriverScreenMode.D_START_RIDE != driverScreenMode
										&& polylineOptionsCustomersPath != null) {
									if (polylineCustomersPath != null) {
										polylineCustomersPath.remove();
									}
									polylineCustomersPath = map.addPolyline(polylineOptionsCustomersPath);
									arrivedOrStartStateZoom();
								}
							}
						}).execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customerInfos;
	}

	private PolylineOptions polylineOptionsCustomersPath = null;
	private Polyline polylineCustomersPath = null;

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


	private void getInfoTilesAsync(final Activity activity) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().getInfoTilesAsync(params, new Callback<InfoTileResponse>() {
				@Override
				public void success(InfoTileResponse infoTileResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.e("Shared rides jsonString", "=" + jsonString);
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity, null);
							} else {
								updateInfoTileListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							}
						} else {
							infoTileResponses.clear();
							tileCount = infoTileResponse.getTiles().size();
							infoTileResponses.addAll((ArrayList<Tile>) infoTileResponse.getTiles());
							updateInfoTileListData(getResources().getString(R.string.no_rides_currently), false);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						updateInfoTileListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					updateInfoTileListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private void updateDropLatLng(final Activity activity, final String address, final LatLng latLng) {

		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put("drop_address", address);
				params.put("drop_latitude", String.valueOf(latLng.latitude));
				params.put("drop_longitude", String.valueOf(latLng.longitude));
				params.put("engagement_id", Data.getCurrentEngagementId());
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().updateDropLatLng(params, new Callback<InfoTileResponse>() {
					@Override
					public void success(InfoTileResponse infoTileResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity, null);
								}
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag)  {
								onDropLocationUpdated(String.valueOf(Data.getCurrentEngagementId()),
										latLng, address);
								if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
									if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
										relativeLayoutContainer.setVisibility(View.GONE);
										getSupportFragmentManager().popBackStack(PlaceSearchListFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
									}
								}
							} else {
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopupWithListener(activity, "", message, new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
											if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
												relativeLayoutContainer.setVisibility(View.GONE);
												getSupportFragmentManager().popBackStack(PlaceSearchListFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
											}
										}
									}
								});
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

			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	public void setDeliveryState(JSONObject jsonObject, CustomerInfo customerInfo){

		try {
			if(jsonObject != null && customerInfo !=null) {
				int flag = jsonObject.optInt("status", 0);
				if (flag == EndDeliveryStatus.RETURN.getOrdinal()) {
					try {
						JSONParser.parseReturnDeliveryInfos(jsonObject, customerInfo);
						deliveryInfoTabs.notifyDatasetchange(true);


						if(customerInfo.getIsDeliveryPool()==1) {
							ArrayList<CustomerInfo> customerEnfagementInfos1 = Data.getAssignedCustomerInfosListForEngagedStatus();
							if (customerEnfagementInfos1.size() > 1) {
								if (customerInfo.engagementId == customerEnfagementInfos1.get(0).getEngagementId()) {
									Data.setCurrentEngagementId(String.valueOf(customerEnfagementInfos1.get(1).getEngagementId()));
								} else {
									Data.setCurrentEngagementId(String.valueOf(customerEnfagementInfos1.get(0).getEngagementId()));
								}
								changeCustomerState(false);
								driverScreenMode = DriverScreenMode.D_IN_RIDE;
								switchDriverScreen(driverScreenMode);
							}
							setAttachedDeliveryPoolMarkers(true);
						} else {
							setDeliveryMarkers();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (flag == EndDeliveryStatus.END.getOrdinal()) {
					endRideGPSCorrection(customerInfo);
					deliveryInfoTabs.notifyDatasetchange(true);
				} else {
					deliveryInfoTabs.notifyDatasetchange(true);
				}
			} else if(jsonObject == null && customerInfo !=null){
				endRideGPSCorrection(customerInfo);
				deliveryInfoTabs.notifyDatasetchange(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		setNevigationButtonVisibiltyDelivery(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	public void setNevigationButtonVisibiltyDelivery(int position){
		try {
			if(DriverScreenMode.D_IN_RIDE == driverScreenMode
					&& Data.getCurrentCustomerInfo().getIsDelivery() == 1){
				try {
					if( Data.getCurrentCustomerInfo().getDeliveryInfos().get(position).getStatus()
							== DeliveryStatus.PENDING.getOrdinal()
							|| Data.getCurrentCustomerInfo().getDeliveryInfos().get(position).getStatus()
							== DeliveryStatus.RETURN.getOrdinal()){
						buttonDriverNavigationSetVisibility(View.VISIBLE);
					} else {
						buttonDriverNavigationSetVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCustomerLocation(final double currrentLatitude, final double currrentLongitude) {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(map != null && currentCustomerLocMarker != null){
							LatLng currentLAtLng = new LatLng(currrentLatitude, currrentLongitude);
							currentCustomerLocMarker.setPosition(currentLAtLng);
							Prefs.with(HomeActivity.this).save(Constants.KEY_CURRENT_LATITUDE_ALARM, String.valueOf(currrentLatitude));
							Prefs.with(HomeActivity.this).save(Constants.KEY_CURRENT_LONGITUDE_ALARM, String.valueOf(currrentLongitude));
							Data.getCurrentCustomerInfo().setCurrentLatLng(currentLAtLng);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeCustomerState(boolean state){
		sortCustomerState = state;
	}


	// Tour operation start here.

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.cross_tour:
				try {
					if(tourResponseModel != null && tourResponseModel.responses.requestResponse.getEngagementId() != null) {
						if (driverScreenMode == DriverScreenMode.D_ARRIVED) {
							driverCancelRideBtn.performClick();
						} else if(driverScreenMode == DriverScreenMode.D_START_RIDE) {
							driverCancelRideBtn.performClick();
						} else if(driverScreenMode == DriverScreenMode.D_RIDE_END) {
							tourCompleteApi("3", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
						} else if(driverScreenMode == DriverScreenMode.D_INITIAL) {
							tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 1);
						} else {
							tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
						}
                    } else {
                        handleTourView(false, "");
                    }
				} catch (Exception e) {
					e.printStackTrace();
					handleTourView(false, "");
				}
				break;
			case R.id.relativeLayoutTour:
				if(!isTourFlag && Data.userData.autosEnabled == 1) {
					DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "",
							getResources().getString(R.string.training_confirmation_text),
							getResources().getString(R.string.ok), getResources().getString(R.string.cancel),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (Data.userData.autosAvailable == 1) {
										isTourFlag = true;
										drawerLayout.closeDrawer(GravityCompat.START);
										Crouton.cancelAllCroutons();
										handleTourView(isTourFlag, getString(R.string.tutorial_your_location) + "\n" + getString(R.string.tutorial_wait_for_customer));
										createTourNotification();
									} else {
										try {
											isTourBtnClicked = true;
											try {
												croutonTourTextView.setText(getString(R.string.tutorial_accept_ride, getString(R.string.appname)));
											} catch (Exception e) {

											}
											Crouton.cancelAllCroutons();
											Crouton.show(HomeActivity.this, customView);

										} catch (Exception e) {
											isTourBtnClicked = false;
										}
									}
								}
							},
							new OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}, false, false);
				}
				break;
		}
	}

	private void handleTourView(boolean flag, String tourText) {
		if(flag) {
			tourLayout.setVisibility(View.VISIBLE);
			tourTextView.setText(tourText);
		} else {
			try {
				if (driverScreenMode == DriverScreenMode.D_ARRIVED) {
                    //driverCancelRideBtn.performClick();
                } else if(driverScreenMode == DriverScreenMode.D_START_RIDE) {
                    //driverCancelRideBtn.performClick();
                } else if(driverScreenMode == DriverScreenMode.D_IN_RIDE) {
					if(endRideGPSCorrection(Data.getCurrentCustomerInfo())) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										tutorialChangeState();
									}
								}, 200);
							}
						});
					} else {
						return;
					}
                } else if(driverScreenMode == DriverScreenMode.D_RIDE_END) {
                    reviewSkipBtn.performLongClick();
                } else if(driverScreenMode == DriverScreenMode.D_INITIAL) {
					//setTourOperation(1);
				} else {
                    reviewSkipBtn.performLongClick();
                }
				Prefs.with(HomeActivity.this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("isTourFlag", String.valueOf(isTourFlag));
			if(isJugnooOnTraining) {
				changeJugnooON(0, false, false);
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isTourFlag = false;
					tourLayout.setVisibility(View.GONE);
					Crouton.cancelAllCroutons();
				}
			}, 500);
		}
	}

	private void tutorialChangeState() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					MyApplication.getInstance().logEvent(FirebaseEvents.RATING + "_" + FirebaseEvents.SKIP,null);
					saveCustomerRideDataInSP(Data.getCurrentCustomerInfo());
					MeteringService.clearNotifications(HomeActivity.this);
					Data.removeCustomerInfo(Integer.parseInt(Data.getCurrentEngagementId()), EngagementStatus.ENDED.getOrdinal());

					driverScreenMode = DriverScreenMode.D_INITIAL;
					switchDriverScreen(driverScreenMode);
					FlurryEventLogger.event(OK_ON_FARE_SCREEN);
					perfectRideStateRestore();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	TourResponseModel tourResponseModel;
	private void createTourNotification() {
		try {
			getTourDataFromServer(HomeActivity.this, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTourOperation(int status) {
		switch (status) {
			case 1:
				try {
					Data.removeCustomerInfo(tourResponseModel.responses.requestResponse.getEngagementId(), EngagementStatus.REQUESTED.getOrdinal());
					try {
                        if (perfectRideMarker != null) {
                            perfectRideMarker.remove();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
                            EngagementStatus.REQUESTED.getOrdinal()));
					if (gcmIntentService == null) {
                        gcmIntentService = new GenrateTourPush(HomeActivity.this);
                    }
					gcmIntentService.stopRing(true, HomeActivity.this);
					try {
                        if (map != null) {
                            map.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
					//isTourFlag = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:

				Gson gson = new Gson();
				String jsonString = gson.toJson(tourResponseModel.responses.acceptResponse, AcceptResponse.class);
				Prefs.with(activity).save(SPLabels.ACCEPT_RIDE_TIME, String.valueOf(System.currentTimeMillis()));
				Prefs.with(activity).save(Constants.DRIVER_RIDE_EARNING, "");
				Prefs.with(activity).save(Constants.DRIVER_RIDE_DATE, "");
				acceptRideSucess(jsonString,
						String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()),
						String.valueOf(tourResponseModel.responses.userData.userId));
				if (gcmIntentService == null) {
					gcmIntentService = new GenrateTourPush(HomeActivity.this);
				}
				gcmIntentService.stopRing(true, HomeActivity.this);

				handleTourView(isTourFlag, getString(R.string.tutorial_driver_to_pickup_point));

				break;
			case 3:
				double dropLatitude = 0, dropLongitude = 0;
				try {
					dropLatitude = tourResponseModel.responses.acceptResponse.opDropLatitude;
					dropLongitude = tourResponseModel.responses.acceptResponse.opDropLongitude;
					CustomerInfo newCustomerInfo = Data.getCurrentCustomerInfo();
					saveCustomerRideDataInSP(newCustomerInfo);
					newCustomerInfo.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
					newCustomerInfo.setDropAddress(tourResponseModel.responses.acceptResponse.dropAddress);
					Prefs.with(HomeActivity.this).save(SPLabels.PERFECT_DISTANCE, "1000");
				} catch (Exception e) {
					e.printStackTrace();
				}

				initializeStartRideVariables();
				driverScreenMode = DriverScreenMode.D_IN_RIDE;
				Data.setCustomerState(String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), driverScreenMode);
				switchDriverScreen(driverScreenMode);
				new DriverTimeoutCheck().clearCount(activity);
				Prefs.with(HomeActivity.this).save(SPLabels.CUSTOMER_PHONE_NUMBER, tourResponseModel.responses.acceptResponse.userData.phoneNo);
				handleTourView(isTourFlag, getString(R.string.tutorial_tap_end_ride));

				break;
			case 4:

				break;
		}
	}


	private void getTourDataFromServer(final Activity activity, final LatLng latLng) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put("latitude", String.valueOf(latLng.latitude));
				params.put("longitude", String.valueOf(latLng.longitude));
				params.put("ride_type", "0");
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().getTourData(params, new Callback<TourResponseModel>() {
					@Override
					public void success(TourResponseModel tourData, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								handleTourView(false, "");
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity, null);
								}
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag)  {
								if(isTourFlag) {
									tourResponseModel = tourData;
									try {
										new Handler().postDelayed(new Runnable() {
											@Override
											public void run() {
												if(isTourFlag && Data.userData.autosAvailable == 1) {
													if (gcmIntentService == null) {
														gcmIntentService = new GenrateTourPush(HomeActivity.this);
													}
													gcmIntentService.createDemoRequest(tourResponseModel);
													handleTourView(isTourFlag, getString(R.string.tutorial_customer_requesting_ride));
												} else {
													handleTourView(false, "");
												}
											}
										}, 5000);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									handleTourView(false, getString(R.string.tutorial_customer_requesting_ride));
								}
							} else {
								handleTourView(false, "");
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopupWithListener(activity, "", message, new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
											if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
												relativeLayoutContainer.setVisibility(View.GONE);
												getSupportFragmentManager().popBackStack(PlaceSearchListFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
											}
										}
									}
								});
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							handleTourView(false, "");
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						handleTourView(false, "");
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});

			} else {
				handleTourView(false, "");
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			handleTourView(false, "");
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	private void tourCompleteApi(String status, String trainingId) {
		tourCompleteApi(status, trainingId, 0);
	}

	/**
	 *
	 * @param status
	 * @param trainingId
	 * @param ridePushCancel 1 for cancel on push click, 2 for cancel on Arrived and start ride view
     */
	private void tourCompleteApi(final String status, final String trainingId, final int ridePushCancel) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(HomeActivity.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put("status", status);
				params.put("training_id", trainingId);
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().updateDriverStatus(params, new Callback<UpdateTourStatusModel>() {
					@Override
					public void success(UpdateTourStatusModel statusModel, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.e("Shared rides jsonString", "=" + jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = jObj.getString("message");
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity, null);
								}
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag)  {
								Log.e("isTourFlag1", String.valueOf(isTourFlag));
								if(ridePushCancel == 1) {
									setTourOperation(1);
								} else if(ridePushCancel == 2) {
									handleCancelRideSuccess(String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), "");
								}
								Log.e("isTourFlag2", String.valueOf(isTourFlag));
								handleTourView(false, "");
								tourResponseModel = null;
							} else {
								Prefs.with(activity).save(SPLabels.SET_DRIVER_TOUR_STATUS, status);
								handleTourView(false, "");
								DialogPopup.dismissLoadingDialog();
								DialogPopup.alertPopup(activity, "", message);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopupWithListener(activity, "", Data.TOUR_FAILED_MSG, new OnClickListener() {
							@Override
							public void onClick(View v) {
								tourCompleteApi(status, trainingId, ridePushCancel);
							}
						});
					}
				});

			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	public void cancelRideRemotely(String engagementId){
		try {
			if(isTourFlag) {

				tourCompleteApi("2", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()), 2);
//							handleTourView(false, "");
			} else {
				Intent intent = new Intent(HomeActivity.this, RideCancellationActivity.class);
				intent.putExtra(KEY_ENGAGEMENT_ID, engagementId);
				startActivityForResult(intent, 12);

				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				if (DriverScreenMode.D_ARRIVED == driverScreenMode) {
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ACCEPTED + "_" + FirebaseEvents.CANCEL, null);
					FlurryEventLogger.event(CANCELED_BEFORE_ARRIVING);
				} else if (DriverScreenMode.D_START_RIDE == driverScreenMode) {
					MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CANCEL, null);
					FlurryEventLogger.event(RIDE_CANCELLED_AFTER_ARRIVING);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getRidesAsync() {
		try {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", "0");
			HomeUtil.putDefaultParams(params);
			//params.put("engagement_date", "" + date);


			RestClient.getApiServices().getDailyRidesAsync(params, new Callback<DailyEarningResponse>() {
				@Override
				public void success(DailyEarningResponse dailyEarningResponse, Response response) {
					try {

							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							DialogPopup.dismissLoadingDialog();
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(HomeActivity.this, null);
								} else {
									Toast.makeText(HomeActivity.this, getString(R.string.error_occured_tap_to_retry), Toast.LENGTH_SHORT).show();
									//updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
								}

							} else {
								if(dailyEarningResponse != null && dailyEarningResponse.getTrips() != null &&
										dailyEarningResponse.getTrips().size()>0 && dailyEarningResponse.getTrips().get(0).getExtras() != null ) {
									Intent intent = new Intent(HomeActivity.this, RideDetailsNewActivity.class);
									Gson gson = new Gson();
									intent.putExtra("extras", gson.toJson(dailyEarningResponse.getTrips().get(0).getExtras(), Tile.Extras.class));
									startActivity(intent);
									overridePendingTransition(R.anim.right_in, R.anim.right_out);
								}
							}

					} catch (Exception e) {
						e.printStackTrace();
						try {
							Toast.makeText(HomeActivity.this, getString(R.string.error_occured_tap_to_retry), Toast.LENGTH_SHORT).show();
							//updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							DialogPopup.dismissLoadingDialog();
						} catch (Exception e1) {
							e1.printStackTrace();
							DialogPopup.dismissLoadingDialog();
						}
					}

				}

				@Override
				public void failure(RetrofitError error) {
					try {
						DialogPopup.dismissLoadingDialog();
						Toast.makeText(HomeActivity.this, getString(R.string.error_occured_tap_to_retry), Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						DialogPopup.dismissLoadingDialog();
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}