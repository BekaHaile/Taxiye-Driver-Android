package product.clicklabs.jugnoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.AddPaymentPath;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppMode;
import product.clicklabs.jugnoo.datastructure.AutoCompleteSearchResult;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.LatLngPair;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionApplyMode;
import product.clicklabs.jugnoo.datastructure.PromotionDialogEventHandler;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
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
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapStateListener;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.TouchableMapFragment;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

@SuppressLint("DefaultLocale")
public class HomeActivity extends FragmentActivity implements AppInterruptHandler, LocationUpdate {

	
	
	DrawerLayout drawerLayout;																		// views declaration
	
	
	
	//menu bar 
	LinearLayout menuLayout;
	
	
	
	LinearLayout linearLayoutProfile;
	ImageView imageViewProfile;
	TextView textViewUserName, textViewViewAccount;
	
	RelativeLayout relativeLayoutGetRide;
	TextView textViewGetRide;
	
	RelativeLayout relativeLayoutInvite;
	TextView textViewInvite;
	
	RelativeLayout relativeLayoutJugnooCash;
	TextView textViewJugnooCash, textViewJugnooCashValue;
	
	RelativeLayout relativeLayoutPromotions;
	TextView textViewPromotions, textViewPromotionsValue;
	
	RelativeLayout relativeLayoutFareDetails;
	TextView textViewFareDetails;
	
	RelativeLayout relativeLayoutSupport;
	TextView textViewSupport;
	
	RelativeLayout relativeLayoutAbout;
	TextView textViewAbout;
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	//Top RL
	RelativeLayout topRl;
	ImageView imageViewMenu, imageViewSearchCancel;
	TextView title;
	Button checkServerBtn, toggleDebugModeBtn;
	ImageView jugnooShopImageView;
	boolean mealsAnimating1 = false, fatafatAnimating1 = false;
	
	
	
	//Passenger main layout
	RelativeLayout passengerMainLayout;
	
	
	
	//Map layout
	RelativeLayout mapLayout;
	GoogleMap map;
	TouchableMapFragment mapFragment;
	
	
	//Initial layout
	RelativeLayout initialLayout;
	TextView textViewNearestDriverETA;
    RelativeLayout relativeLayoutInitialFareFactor;
    TextView textViewCurrentFareFactor;
	Button initialMyLocationBtn, changeLocalityBtn;

    ImageView imageViewRideLater, imageViewRideNow;
    RelativeLayout relativeLayoutJugnooAnim;
    ImageView imageViewMeals1, imageViewFatafat1, imageViewJugnoo1;

	RelativeLayout relativeLayoutInitialSearchBar;
	TextView textViewInitialSearch;
	ProgressBar progressBarInitialSearch;



    //Assigining layout
    RelativeLayout assigningLayout;
    TextView textViewFindingDriver;
    Button assigningMyLocationBtn, initialCancelRideBtn;






    //Search Layout
	LinearLayout linearLayoutSearch;
	EditText editTextSearch;
	ProgressBar progressBarSearch;
	ListView listViewSearch;
	SearchListAdapter searchListAdapter;
	
	
	
	
	
	
	
	//Request Final Layout
	RelativeLayout requestFinalLayout;
	RelativeLayout relativeLayoutInRideInfo;
	TextView textViewInRidePromoName, textViewInRideFareFactor;
	Button customerInRideMyLocationBtn;
	ImageView imageViewInRideDriver, imageViewInRideDriverCar;
	TextView textViewInRideDriverName, textViewInRideDriverCarNumber, textViewInRideState, textViewInRideLowJugnooCash;
	Button buttonCancelRide, buttonAddJugnooCash, buttonCallDriver;
	
	
	
	
	
	
	
	
	
	
	//Center Location Layout
	RelativeLayout centreLocationRl;
	ImageView centreLocationPin;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//End Ride layout
	RelativeLayout endRideReviewRl;
	ScrollView scrollViewEndRide;
	
	TextView textViewEndRideDriverName, textViewEndRideDriverCarNumber;
	TextView textViewEndRideStartLocationValue, textViewEndRideEndLocationValue, textViewEndRideStartTimeValue, textViewEndRideEndTimeValue;
	TextView textViewEndRideFareValue, textViewEndRidePromotionDiscountValue, textViewEndRideFinalFareValue, textViewEndRideJugnooCashValue, 
	textViewEndRideToBePaidValue, textViewEndRideBaseFareValue, textViewEndRideDistanceValue, textViewEndRideTimeValue, textViewEndRideAddJugnooCashInfo;
	Button buttonEndRideOk;
	
	
	
	
	
	
	
																								// data variables declaration
	
	
	
	
	
	
	
	
	Location lastLocation;
	
	ArrayList<AutoCompleteSearchResult> autoCompleteSearchResults = new ArrayList<AutoCompleteSearchResult>(); 
	
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#");
	
	static double totalDistance = -1;
	public static ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	
	
	static long previousWaitTime = 0, previousRideTime = 0;

	
	
	static Location myLocation;
	
	
	

	static UserMode userMode;
	static PassengerScreenMode passengerScreenMode;
	
	
	
	
	
	
	GetDistanceTimeAddress getDistanceTimeAddress;
	
	
	
	
	Marker pickupLocationMarker, driverLocationMarker, currentLocationMarker;
	
	static AppInterruptHandler appInterruptHandler;
	
	static Activity activity;
	
	boolean loggedOut = false, 
			zoomedToMyLocation = false, 
			mapTouchedOnce = false;
	boolean dontCallRefreshDriver = false;
	
	
	AlertDialog gpsDialogAlert;
	Dialog noDriversDialog;
	
	LocationFetcher lowPowerLF, highAccuracyLF;
	
	PromoCoupon promoCouponSelectedForRide;
	
	
	
	
	
	
	//TODO check final variables
	public static AppMode appMode;

	
	public static final long LOCATION_UPDATE_TIME_PERIOD = 6 * 10000; //in milliseconds
	
	
	public static final double MIN_BALANCE_ALERT_VALUE = 150; //in Rupees
	

	
	
	public static final float LOW_POWER_ACCURACY_CHECK = 2000, HIGH_ACCURACY_ACCURACY_CHECK = 200;  //in meters
	public static final float WAIT_FOR_ACCURACY_UPPER_BOUND = 2000, WAIT_FOR_ACCURACY_LOWER_BOUND = 200;  //in meters

	
	public static final double MAP_PAN_DISTANCE_CHECK = 50; // in meters
	
	public CheckForGPSAccuracyTimer checkForGPSAccuracyTimer;
	
	
	public boolean activityResumed = false;
	public static boolean rechargedOnce = false;
	
	public ASSL assl;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		HomeActivity.appInterruptHandler = HomeActivity.this;
		
		activity = this;
		
		activityResumed = false;
		rechargedOnce = false;
		
		loggedOut = false;
		zoomedToMyLocation = false;
		dontCallRefreshDriver = false;
		mapTouchedOnce = false;

		mealsAnimating1 = false;
		fatafatAnimating1 = false;

		appMode = AppMode.NORMAL;
		
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		
		
		assl = new ASSL(HomeActivity.this, drawerLayout, 1134, 720, false);
		
		


		
		//Swipe menu
		menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
		
		
		
		linearLayoutProfile = (LinearLayout) findViewById(R.id.linearLayoutProfile);
		imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
		textViewUserName = (TextView) findViewById(R.id.textViewUserName); textViewUserName.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		textViewViewAccount = (TextView) findViewById(R.id.textViewViewAccount); textViewViewAccount.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		
		relativeLayoutGetRide = (RelativeLayout) findViewById(R.id.relativeLayoutGetRide);
		textViewGetRide = (TextView) findViewById(R.id.textViewGetRide); textViewGetRide.setTypeface(Fonts.latoRegular(this));
		
		relativeLayoutInvite = (RelativeLayout) findViewById(R.id.relativeLayoutInvite);
		textViewInvite = (TextView) findViewById(R.id.textViewInvite); textViewInvite.setTypeface(Fonts.latoRegular(this));
		
		relativeLayoutJugnooCash = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCash);
		textViewJugnooCash = (TextView) findViewById(R.id.textViewJugnooCash); textViewJugnooCash.setTypeface(Fonts.latoRegular(this));
		textViewJugnooCashValue = (TextView) findViewById(R.id.textViewJugnooCashValue); textViewJugnooCashValue.setTypeface(Fonts.latoRegular(this));

		relativeLayoutPromotions = (RelativeLayout) findViewById(R.id.relativeLayoutPromotions);
		textViewPromotions = (TextView) findViewById(R.id.textViewPromotions); textViewPromotions.setTypeface(Fonts.latoRegular(this));
		textViewPromotionsValue = (TextView) findViewById(R.id.textViewPromotionsValue); textViewPromotionsValue.setTypeface(Fonts.latoRegular(this));

		relativeLayoutFareDetails = (RelativeLayout) findViewById(R.id.relativeLayoutFareDetails);
		textViewFareDetails = (TextView) findViewById(R.id.textViewFareDetails); textViewFareDetails.setTypeface(Fonts.latoRegular(this));

		relativeLayoutSupport = (RelativeLayout) findViewById(R.id.relativeLayoutSupport);
		textViewSupport = (TextView) findViewById(R.id.textViewSupport); textViewSupport.setTypeface(Fonts.latoRegular(this));

		relativeLayoutAbout = (RelativeLayout) findViewById(R.id.relativeLayoutAbout);
		textViewAbout = (TextView) findViewById(R.id.textViewAbout); textViewAbout.setTypeface(Fonts.latoRegular(this));
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Top RL
		topRl = (RelativeLayout) findViewById(R.id.topRl);
		imageViewMenu = (ImageView) findViewById(R.id.imageViewMenu);
		imageViewSearchCancel = (ImageView) findViewById(R.id.imageViewSearchCancel);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		checkServerBtn = (Button) findViewById(R.id.checkServerBtn);
		toggleDebugModeBtn = (Button) findViewById(R.id.toggleDebugModeBtn);
		jugnooShopImageView = (ImageView) findViewById(R.id.jugnooShopImageView);

		
		
		
		
		
		
		
		
		//Map Layout
		mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapFragment = ((TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		
		
		
		
		
		
		
		//Passenger main layout
		passengerMainLayout = (RelativeLayout) findViewById(R.id.passengerMainLayout);
		
		
		
		//Initial layout 
		initialLayout = (RelativeLayout) findViewById(R.id.initialLayout);
		textViewNearestDriverETA = (TextView) findViewById(R.id.textViewNearestDriverETA); textViewNearestDriverETA.setTypeface(Fonts.latoRegular(this));

        relativeLayoutInitialFareFactor = (RelativeLayout) findViewById(R.id.relativeLayoutInitialFareFactor);
        textViewCurrentFareFactor = (TextView) findViewById(R.id.textViewCurrentFareFactor); textViewCurrentFareFactor.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		((TextView) findViewById(R.id.textViewCurrentRatesInfo)).setTypeface(Fonts.latoRegular(this));

		initialMyLocationBtn = (Button) findViewById(R.id.initialMyLocationBtn);
        changeLocalityBtn = (Button) findViewById(R.id.changeLocalityBtn); changeLocalityBtn.setTypeface(Fonts.latoRegular(this));
        changeLocalityBtn.setVisibility(View.GONE);

        imageViewRideLater = (ImageView) findViewById(R.id.imageViewRideLater);
        imageViewRideNow = (ImageView) findViewById(R.id.imageViewRideNow);

        relativeLayoutJugnooAnim = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooAnim);
        imageViewMeals1 = (ImageView) findViewById(R.id.imageViewMeals1);
        imageViewFatafat1 = (ImageView) findViewById(R.id.imageViewFatafat1);
        imageViewJugnoo1 = (ImageView) findViewById(R.id.imageViewJugnoo1);


		relativeLayoutInitialSearchBar = (RelativeLayout) findViewById(R.id.relativeLayoutInitialSearchBar);
		textViewInitialSearch = (TextView) findViewById(R.id.textViewInitialSearch); textViewInitialSearch.setTypeface(Fonts.latoRegular(this));
		progressBarInitialSearch = (ProgressBar) findViewById(R.id.progressBarInitialSearch); progressBarInitialSearch.setVisibility(View.GONE);


        //Assigning layout
        assigningLayout = (RelativeLayout) findViewById(R.id.assigningLayout);
        textViewFindingDriver = (TextView) findViewById(R.id.textViewFindingDriver); textViewFindingDriver.setTypeface(Fonts.latoRegular(this));
        assigningMyLocationBtn = (Button) findViewById(R.id.assigningMyLocationBtn);
        initialCancelRideBtn = (Button) findViewById(R.id.initialCancelRideBtn); initialCancelRideBtn.setTypeface(Fonts.latoRegular(this));
		
		
		
		//Search Layout 
		linearLayoutSearch = (LinearLayout) findViewById(R.id.linearLayoutSearch);
		editTextSearch = (EditText) findViewById(R.id.editTextSearch); editTextSearch.setTypeface(Fonts.latoRegular(this));
		progressBarSearch = (ProgressBar) findViewById(R.id.progressBarSearch); progressBarSearch.setVisibility(View.GONE);
		listViewSearch = (ListView) findViewById(R.id.listViewSearch);
		searchListAdapter = new SearchListAdapter();
		listViewSearch.setAdapter(searchListAdapter);
		
		
		
		
		
		
		//Request Final Layout
		requestFinalLayout = (RelativeLayout) findViewById(R.id.requestFinalLayout);
		
		relativeLayoutInRideInfo = (RelativeLayout) findViewById(R.id.relativeLayoutInRideInfo);
		textViewInRidePromoName = (TextView) findViewById(R.id.textViewInRidePromoName); textViewInRidePromoName.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewInRideFareFactor = (TextView) findViewById(R.id.textViewInRideFareFactor); textViewInRideFareFactor.setTypeface(Fonts.latoRegular(this));
		
		customerInRideMyLocationBtn = (Button) findViewById(R.id.customerInRideMyLocationBtn);
		
		imageViewInRideDriver = (ImageView) findViewById(R.id.imageViewInRideDriver);
		imageViewInRideDriverCar = (ImageView) findViewById(R.id.imageViewInRideDriverCar);
		
		textViewInRideDriverName = (TextView) findViewById(R.id.textViewInRideDriverName); textViewInRideDriverName.setTypeface(Fonts.latoRegular(this));
		textViewInRideDriverCarNumber = (TextView) findViewById(R.id.textViewInRideDriverCarNumber); textViewInRideDriverCarNumber.setTypeface(Fonts.latoRegular(this));
		textViewInRideState = (TextView) findViewById(R.id.textViewInRideState); textViewInRideState.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewInRideLowJugnooCash = (TextView) findViewById(R.id.textViewInRideLowJugnooCash); textViewInRideLowJugnooCash.setTypeface(Fonts.latoRegular(this));
		textViewInRideLowJugnooCash.setVisibility(View.GONE);
		
		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide); buttonCancelRide.setTypeface(Fonts.latoRegular(this));
		buttonAddJugnooCash = (Button) findViewById(R.id.buttonAddJugnooCash); buttonAddJugnooCash.setTypeface(Fonts.latoRegular(this));
		buttonCallDriver = (Button) findViewById(R.id.buttonCallDriver); buttonCallDriver.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		
		
		
		
		
		

		
		
		//Center location layout
		centreLocationRl = (RelativeLayout) findViewById(R.id.centreLocationRl);
		centreLocationPin = (ImageView) findViewById(R.id.centreLocationPin);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Review Layout
		endRideReviewRl = (RelativeLayout) findViewById(R.id.endRideReviewRl);
		scrollViewEndRide = (ScrollView) findViewById(R.id.scrollViewEndRide);
		
		textViewEndRideDriverName = (TextView) findViewById(R.id.textViewEndRideDriverName); textViewEndRideDriverName.setTypeface(Fonts.latoRegular(this));
		textViewEndRideDriverCarNumber = (TextView) findViewById(R.id.textViewEndRideDriverCarNumber); textViewEndRideDriverCarNumber.setTypeface(Fonts.latoRegular(this));

		textViewEndRideStartLocationValue = (TextView) findViewById(R.id.textViewEndRideStartLocationValue); textViewEndRideStartLocationValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideEndLocationValue = (TextView) findViewById(R.id.textViewEndRideEndLocationValue); textViewEndRideEndLocationValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideStartTimeValue = (TextView) findViewById(R.id.textViewEndRideStartTimeValue); textViewEndRideStartTimeValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideEndTimeValue = (TextView) findViewById(R.id.textViewEndRideEndTimeValue); textViewEndRideEndTimeValue.setTypeface(Fonts.latoRegular(this));
		
		textViewEndRideFareValue = (TextView) findViewById(R.id.textViewEndRideFareValue); textViewEndRideFareValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRidePromotionDiscountValue = (TextView) findViewById(R.id.textViewEndRidePromotionDiscountValue); textViewEndRidePromotionDiscountValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideFinalFareValue = (TextView) findViewById(R.id.textViewEndRideFinalFareValue); textViewEndRideFinalFareValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideJugnooCashValue = (TextView) findViewById(R.id.textViewEndRideJugnooCashValue); textViewEndRideJugnooCashValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideToBePaidValue = (TextView) findViewById(R.id.textViewEndRideToBePaidValue); textViewEndRideToBePaidValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideBaseFareValue = (TextView) findViewById(R.id.textViewEndRideBaseFareValue); textViewEndRideBaseFareValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideDistanceValue = (TextView) findViewById(R.id.textViewEndRideDistanceValue); textViewEndRideDistanceValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideTimeValue = (TextView) findViewById(R.id.textViewEndRideTimeValue); textViewEndRideTimeValue.setTypeface(Fonts.latoRegular(this));
		textViewEndRideAddJugnooCashInfo = (TextView) findViewById(R.id.textViewEndRideAddJugnooCashInfo); textViewEndRideAddJugnooCashInfo.setTypeface(Fonts.latoRegular(this));
		
		buttonEndRideOk = (Button) findViewById(R.id.buttonEndRideOk); buttonEndRideOk.setTypeface(Fonts.latoRegular(this));
		
		
		((TextView)findViewById(R.id.textViewEndRideStartLocation)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideEndLocation)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideStartTime)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideEndTime)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideSummary)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideFare)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideFareRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRidePromotionDiscount)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRidePromotionDiscountRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideFinalFare)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideFinalFareRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideJugnooCash)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideJugnooCashRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideToBePaid)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideToBePaidRupee)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideBaseFare)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideDistance)).setTypeface(Fonts.latoRegular(this));
		((TextView)findViewById(R.id.textViewEndRideTime)).setTypeface(Fonts.latoRegular(this));















        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideAnims();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

		
		
		
		//Top bar events
		imageViewMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(menuLayout);
                hideAnims();
			}
		});


        checkServerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAnims();
            }
        });

        toggleDebugModeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAnims();
            }
        });

		checkServerBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(getApplicationContext(), "url = "+ Config.getServerUrl(), Toast.LENGTH_SHORT).show();
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
                hideAnims();
			}
		});

		imageViewSearchCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.hideSoftKeyboard(HomeActivity.this, editTextSearch);
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// menu events\
		
		linearLayoutProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AccountActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		relativeLayoutGetRide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(menuLayout);
			}
		});

		relativeLayoutInvite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, ShareActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.shareScreenOpened(Data.userData.accessToken);
			}
		});

		relativeLayoutJugnooCash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, WalletActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.walletScreenOpened(Data.userData.accessToken);
			}
		});

		relativeLayoutPromotions.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, PromotionsActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.couponsScreenOpened(Data.userData.accessToken);
			}
		});

		relativeLayoutFareDetails.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendToFareDetails();
			}
		});

		relativeLayoutSupport.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, SupportActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
			}
		});

		relativeLayoutAbout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.helpScreenOpened(Data.userData.accessToken);
			}
		});
		
		
		menuLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// Customer initial layout events
        imageViewRideNow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
                    hideAnims();
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						if (myLocation != null) {
							if(map != null){
								promoCouponSelectedForRide = null;
								LatLng scheduleLatLng = map.getCameraPosition().target;
								
								final PromotionDialog promotionDialog = new PromotionDialog(scheduleLatLng, PromotionApplyMode.BEFORE_RIDE);
								promotionDialog.fetchPromotionsAPI(HomeActivity.this, new PromotionDialogEventHandler() {
	
											@Override
											public void onOkPressed(PromoCoupon promoCoupon, int totalPromoCoupons) {
												promoCouponSelectedForRide = promoCoupon;
												callAnAutoPopup(HomeActivity.this, totalPromoCoupons);
											}
											
											@Override
											public void onOkOnlyPressed(PromotionDialog promotionDialog, PromoCoupon promoCoupon, String pickupId) {
											}
	
											@Override
											public void onCancelPressed() {
												promoCouponSelectedForRide = null;
											}
										});
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"Waiting for your location...",
									Toast.LENGTH_LONG).show();
						}
					} else {
						DialogPopup.alertPopup(HomeActivity.this, "",
								Data.CHECK_INTERNET_MSG);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


        imageViewRideLater.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                hideAnims();
				switchToScheduleScreen(HomeActivity.this);
			}
		});





        imageViewJugnoo1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!mealsAnimating1){
                    if(imageViewMeals1.getTag() != "shown"){
                        startShowAnimMeals1();
                    }
                    else{
                        startHideAnimMeals1();
                    }
                }
                if(!fatafatAnimating1){
                    if(imageViewFatafat1.getTag() != "shown"){
                        startShowAnimFatafat1();
                    }
                    else{
                        startHideAnimFatafat1();
                    }
                }
            }
        });

        imageViewMeals1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!mealsAnimating1){
                    try {
                        CustomAppLauncher.launchApp(HomeActivity.this, AccessTokenGenerator.MEALS_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        imageViewFatafat1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!fatafatAnimating1){
                    try {
                        CustomAppLauncher.launchApp(HomeActivity.this, AccessTokenGenerator.FATAFAT_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });







		
		relativeLayoutInitialSearchBar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextSearch.requestFocus();
				editTextSearch.setText(textViewInitialSearch.getText().toString());
				editTextSearch.setSelection(editTextSearch.getText().length());
				Utils.showSoftKeyboard(HomeActivity.this, editTextSearch);
				autoCompleteSearchResults.clear();
				searchListAdapter.notifyDataSetChanged();
				passengerScreenMode = PassengerScreenMode.P_SEARCH;
				switchPassengerScreen(passengerScreenMode);
			}
		});


        changeLocalityBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textViewInitialSearch.setText("");
                relativeLayoutInitialSearchBar.performClick();
                hideAnims();
            }
        });
		
		
		
		
		
		
		










        // Assigning layout events
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//Search Layout Events
		linearLayoutSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		editTextSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				autoCompleteSearchResults.clear();
				searchListAdapter.notifyDataSetChanged();
				if(s.length() > 0){
					if(map != null){
						getSearchResults(s.toString().trim(), map.getCameraPosition().target);
					}
				}
			}
		});
		
		editTextSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextSearch.requestFocus();
				Utils.showSoftKeyboard(HomeActivity.this, editTextSearch);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// customer request final layout events
		buttonCancelRide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, RideCancellationActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		buttonAddJugnooCash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WalletAddPaymentActivity.addPaymentPath = AddPaymentPath.FROM_IN_RIDE;
				startActivity(new Intent(HomeActivity.this, WalletAddPaymentActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		buttonCallDriver.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        Utils.openCallIntent(HomeActivity.this, Data.assignedDriverInfo.phoneNumber);
		        FlurryEventLogger.callDriverPressed(Data.userData.accessToken, Data.assignedDriverInfo.userId, 
		        		Data.assignedDriverInfo.phoneNumber);
			}
		});
		
		textViewInRideLowJugnooCash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonAddJugnooCash.performClick();
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// End ride review layout events
		endRideReviewRl.setOnClickListener(new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
						
			}
		});
				
				
		buttonEndRideOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GCMIntentService.clearNotifications(HomeActivity.this);
				if(userMode == UserMode.PASSENGER){
					Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
					intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.AFTER_RIDE.getOrdinal());
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
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
								final DriverInfo driverInfo = Data.driverInfos.get(Data.driverInfos.indexOf(new DriverInfo(driverId)));
								DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, "Driver Info", ""+driverInfo.toString(),
										"Call", "Cancel", new View.OnClickListener() {
											
											@Override
											public void onClick(View v) {
												Intent callIntent = new Intent(Intent.ACTION_VIEW);
										        callIntent.setData(Uri.parse("tel:"+driverInfo.phoneNumber));
										        startActivity(callIntent);
											}
										}, 
										new View.OnClickListener() {
											
											@Override
											public void onClick(View v) {
											}
										}, true, false);
								
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
                      hideAnims();
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
			
			
			
			initialMyLocationBtn.setOnClickListener(mapMyLocationClick);
            assigningMyLocationBtn.setOnClickListener(mapMyLocationClick);
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
			
			
			switchUserScreen();
			
			startUIAfterGettingUserStatus();
			

			Database2.getInstance(HomeActivity.this).insertDriverLocData(Data.userData.accessToken, Data.deviceToken, Config.getServerUrl());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Database2.getInstance(HomeActivity.this).close();
		
	}













    public long animDurationShow = 300;
    public long animDurationHide = 300;

    public void startShowAnimMeals1(){
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -140);
        translateAnimation.setDuration(animDurationShow);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDurationShow);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDurationShow);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.clearAnimation();
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setTag("shown");
                mealsAnimating1 = false;
            }
        });

        imageViewMeals1.clearAnimation();
        imageViewMeals1.startAnimation(animationSet);
    }


    public void startShowAnimFatafat1(){
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, ASSL.Yscale() * -280);
        translateAnimation.setDuration(animDurationShow);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(animDurationShow);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDurationShow);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.clearAnimation();
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setTag("shown");
                fatafatAnimating1 = false;
            }
        });

        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.startAnimation(animationSet);
    }




    public void startHideAnimMeals1(){
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 140);
        translateAnimation.setDuration(animDurationHide);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDurationHide);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDurationHide);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mealsAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 140)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewMeals1.clearAnimation();
                imageViewMeals1.setLayoutParams(layoutParams);
                imageViewMeals1.setTag("");
                imageViewMeals1.setVisibility(View.GONE);
                mealsAnimating1 = false;
            }
        });

        imageViewMeals1.clearAnimation();
        imageViewMeals1.startAnimation(animationSet);
    }


    public void startHideAnimFatafat1(){
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, 0,
            TranslateAnimation.ABSOLUTE, ASSL.Yscale() * 280);
        translateAnimation.setDuration(animDurationHide);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(false);

        Animation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(animDurationHide);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setFillAfter(false);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(animDurationHide);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                fatafatAnimating1 = true;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, 0, 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
                layoutParams.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewFatafat1.clearAnimation();
                imageViewFatafat1.setLayoutParams(layoutParams);
                imageViewFatafat1.setTag("");
                imageViewFatafat1.setVisibility(View.GONE);
                fatafatAnimating1 = false;
            }
        });

        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.startAnimation(animationSet);
    }

    public void clearAnims(){
        RelativeLayout.LayoutParams layoutParamsM = new RelativeLayout.LayoutParams(imageViewMeals1.getLayoutParams());
        layoutParamsM.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
        layoutParamsM.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewMeals1.clearAnimation();
        imageViewMeals1.setLayoutParams(layoutParamsM);
        imageViewMeals1.setTag("");
        imageViewMeals1.setVisibility(View.GONE);
        mealsAnimating1 = false;

        RelativeLayout.LayoutParams layoutParamsF = new RelativeLayout.LayoutParams(imageViewFatafat1.getLayoutParams());
        layoutParamsF.setMargins(0, ((int)(ASSL.Yscale() * 280)), 0, 0);
        layoutParamsF.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewFatafat1.clearAnimation();
        imageViewFatafat1.setLayoutParams(layoutParamsF);
        imageViewFatafat1.setTag("");
        imageViewFatafat1.setVisibility(View.GONE);
        fatafatAnimating1 = false;
    }

    public void hideAnims(){
        if(PassengerScreenMode.P_INITIAL == passengerScreenMode) {
            if (!mealsAnimating1) {
                if ("shown" == imageViewMeals1.getTag()) {
                    startHideAnimMeals1();
                }
            }
            if (!fatafatAnimating1) {
                if ("shown" == imageViewFatafat1.getTag()) {
                    startHideAnimFatafat1();
                }
            }
        }
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
	
	
	
	
	public void callMapTouchedRefreshDrivers(){
		try {
			if(Data.userData != null){
				if(UserMode.PASSENGER == userMode &&
						(PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode) &&
                    map != null &&
                    HomeActivity.this.hasWindowFocus()){
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
		} catch (Exception e) {
			e.printStackTrace();
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
				clearSPData();
				switchPassengerScreen(passengerScreenMode);
			}
			else{
				switchPassengerScreen(passengerScreenMode);
			}
			
		}
	}
	
	
	public void sendToFareDetails(){
		HelpParticularActivity.helpSection = HelpSection.FARE_DETAILS;
		startActivity(new Intent(HomeActivity.this, HelpParticularActivity.class));
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
		FlurryEventLogger.fareDetailsOpened(Data.userData.accessToken);
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
	
	
	
	
	
	
	
	
	
	
	
	
	OnClickListener mapMyLocationClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			textViewInitialSearch.setText("");
			if(myLocation != null){
				if(map.getCameraPosition().zoom < 12){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12));
				}
				else if(map.getCameraPosition().zoom < 15){
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
				}
				else{
					map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Waiting for your location...", Toast.LENGTH_LONG).show();
				reconnectLocationFetchers();
			}
            hideAnims();
		}
	};
	
	
	Handler reconnectionHandler = null;
	
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
			textViewUserName.setText(Data.userData.userName);
			
			textViewPromotionsValue.setText(""+Data.userData.numCouponsAvaliable);
			
			textViewJugnooCashValue.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDecimal.format(Data.userData.jugnooBalance));
			
			Data.userData.userImage = Data.userData.userImage.replace("http://graph.facebook", "https://graph.facebook");
			try{Picasso.with(HomeActivity.this).load(Data.userData.userImage).skipMemoryCache().transform(new CircleTransform()).into(imageViewProfile);}catch(Exception e){}
			
			updateLowJugnooCashBanner(passengerScreenMode);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	
	public void switchUserScreen(){
		
		if(getDistanceTimeAddress != null){
			getDistanceTimeAddress.cancel(true);
			getDistanceTimeAddress = null;
		}
		
		
		Database2.getInstance(HomeActivity.this).updateUserMode(Database2.UM_PASSENGER);
		
		passengerMainLayout.setVisibility(View.VISIBLE);
		
		relativeLayoutPromotions.setVisibility(View.VISIBLE);
		
		Database2.getInstance(HomeActivity.this).close();
		
	}
	
	
	
	
	
	
	
	public void switchPassengerScreen(PassengerScreenMode mode){
		if(userMode == UserMode.PASSENGER){
			initializeFusedLocationFetchers();

			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
		
			saveDataOnPause();
			
		if(mode == PassengerScreenMode.P_RIDE_END){
			if(Data.endRideData != null){
				
				scrollViewEndRide.scrollTo(0, 0);
				
				mapLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.VISIBLE);
				
				textViewEndRideDriverName.setText(Data.assignedDriverInfo.name);
				textViewEndRideDriverCarNumber.setText(Data.assignedDriverInfo.carNumber);
				
				textViewEndRideStartLocationValue.setText(Data.endRideData.pickupAddress);
				textViewEndRideEndLocationValue.setText(Data.endRideData.dropAddress);
				
				textViewEndRideStartTimeValue.setText(Data.endRideData.pickupTime);
				textViewEndRideEndTimeValue.setText(Data.endRideData.dropTime);
				
				textViewEndRideFareValue.setText(decimalFormatNoDecimal.format(Data.endRideData.fare));
				textViewEndRidePromotionDiscountValue.setText(decimalFormatNoDecimal.format(Data.endRideData.discount));
				textViewEndRideFinalFareValue.setText(decimalFormatNoDecimal.format(Math.abs(Data.endRideData.fare - Data.endRideData.discount)));
				textViewEndRideJugnooCashValue.setText(decimalFormatNoDecimal.format(Data.endRideData.paidUsingWallet));
				textViewEndRideToBePaidValue.setText(decimalFormatNoDecimal.format(Data.endRideData.toPay));
				
				textViewEndRideBaseFareValue.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDecimal.format(Data.endRideData.baseFare));
				
				if(!"".equalsIgnoreCase(Data.endRideData.banner)){
					textViewEndRideAddJugnooCashInfo.setText(Data.endRideData.banner);
				}
				
				textViewEndRideAddJugnooCashInfo.setVisibility(View.GONE);
				
				double totalDistanceInKm = Data.endRideData.distance;
				String kmsStr = "";
				if(totalDistanceInKm > 1){
					kmsStr = "kms";
				}
				else{
					kmsStr = "km";
				}
				textViewEndRideDistanceValue.setText(""+decimalFormat.format(totalDistanceInKm) + " " + kmsStr);
				
				textViewEndRideTimeValue.setText(decimalFormatNoDecimal.format(Data.endRideData.rideTime)+" min");
			}
			else{
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
			}
		}
		else{
			mapLayout.setVisibility(View.VISIBLE);
			endRideReviewRl.setVisibility(View.GONE);
		}
		
		
		
		enableJugnooShopUI();

		switch(mode){
		
			case P_INITIAL:

                clearAnims();

				try{pickupLocationMarker.remove();} catch(Exception e){}
				try{driverLocationMarker.remove();} catch(Exception e){}
				
				
		        GCMIntentService.clearNotifications(getApplicationContext());
				
				if(getDistanceTimeAddress != null){
					getDistanceTimeAddress.cancel(true);
					getDistanceTimeAddress = null;
				}
				
				
				initialLayout.setVisibility(View.VISIBLE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				if (Data.userData.canChangeLocation == 1) {
					centreLocationRl.setVisibility(View.VISIBLE);
					relativeLayoutInitialSearchBar.setVisibility(View.VISIBLE);
				} else {
					centreLocationRl.setVisibility(View.GONE);
					relativeLayoutInitialSearchBar.setVisibility(View.GONE);
				}
				
				textViewNearestDriverETA.setText("Finding nearby drivers...");

                imageViewRideNow.setVisibility(View.VISIBLE);
                imageViewRideLater.setVisibility(View.VISIBLE);
                relativeLayoutJugnooAnim.setVisibility(View.VISIBLE);
                changeLocalityBtn.setVisibility(View.GONE);
				
				setFareFactorToInitialState();

                cancelTimerRequestRide();


				Log.e("Data.latitude", "="+Data.latitude);
				Log.e("myLocation", "="+myLocation);
				
				if (Data.latitude != 0 && Data.longitude != 0) {
					showDriverMarkersAndPanMap(new LatLng(Data.latitude, Data.longitude));
				} else if (myLocation != null) {
					showDriverMarkersAndPanMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
				}
				

				imageViewSearchCancel.setVisibility(View.GONE);
				
				break;
				
				
			case P_SEARCH:

				initialLayout.setVisibility(View.GONE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.VISIBLE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);

				jugnooShopImageView.setVisibility(View.GONE);


				imageViewSearchCancel.setVisibility(View.VISIBLE);


				break;
				
				
			case P_ASSIGNING:
				
				initialLayout.setVisibility(View.GONE);
                assigningLayout.setVisibility(View.VISIBLE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);
				
				if(map != null){
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.title("pickup location");
					markerOptions.snippet("");
					markerOptions.position(Data.pickupLatLng);
					markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
					
					pickupLocationMarker = map.addMarker(markerOptions);
				}
				



				imageViewSearchCancel.setVisibility(View.GONE);
				
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
				
				
				initialLayout.setVisibility(View.GONE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				
				setAssignedDriverData(mode);
				
				buttonCancelRide.setVisibility(View.VISIBLE);
				buttonAddJugnooCash.setVisibility(View.GONE);
				
				textViewInRideLowJugnooCash.setVisibility(View.GONE);
				
				

				imageViewSearchCancel.setVisibility(View.GONE);
				
				break;
				
				
				
			case P_IN_RIDE:
				
				cancelTimerUpdateDrivers();
				
				cancelDriverLocationUpdateTimer();
				
				startMapAnimateAndUpdateRideDataTimer();
				
				if(map != null){
					map.clear();
					
					if(Data.pickupLatLng != null){
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.snippet("");
						markerOptions.title("start ride location");
						markerOptions.position(Data.pickupLatLng);
						markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
						map.addMarker(markerOptions);
					}
				}
				
				
				initialLayout.setVisibility(View.GONE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.VISIBLE);
				centreLocationRl.setVisibility(View.GONE);
				
				
				setAssignedDriverData(mode);
				
				buttonCancelRide.setVisibility(View.GONE);
				buttonAddJugnooCash.setVisibility(View.VISIBLE);
				
				updateLowJugnooCashBanner(mode);
				

				imageViewSearchCancel.setVisibility(View.GONE);
				
				break;
				
			case P_RIDE_END:
				
				cancelMapAnimateAndUpdateRideDataTimer();
				
				initialLayout.setVisibility(View.GONE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);

				imageViewSearchCancel.setVisibility(View.GONE);
				
				break;
				
				
			default:

				initialLayout.setVisibility(View.VISIBLE);
                assigningLayout.setVisibility(View.GONE);
				linearLayoutSearch.setVisibility(View.GONE);
				requestFinalLayout.setVisibility(View.GONE);
				endRideReviewRl.setVisibility(View.GONE);
				centreLocationRl.setVisibility(View.GONE);

				imageViewSearchCancel.setVisibility(View.GONE);
				
				
		}

            initiateTimersForStates(mode);
		
		}
		
	}



    private void initiateTimersForStates(PassengerScreenMode passengerScreenMode){
        try {
            switch (passengerScreenMode) {
                case P_INITIAL:
                    startTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_SEARCH:
                    break;

                case P_ASSIGNING:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_REQUEST_FINAL:
                    cancelTimerUpdateDrivers();
                    startDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_IN_RIDE:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    startMapAnimateAndUpdateRideDataTimer();
                    break;

                case P_RIDE_END:
                    cancelTimerUpdateDrivers();
                    cancelDriverLocationUpdateTimer();
                    cancelMapAnimateAndUpdateRideDataTimer();
                    break;

                default:

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void pauseAllTimers(){
        try {
            cancelTimerUpdateDrivers();
            cancelDriverLocationUpdateTimer();
            cancelMapAnimateAndUpdateRideDataTimer();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
	
	public void updateLowJugnooCashBanner(PassengerScreenMode mode){
		if(PassengerScreenMode.P_IN_RIDE == mode){
			if(Data.userData != null){
				if(HomeActivity.rechargedOnce){
					textViewInRideLowJugnooCash.setVisibility(View.GONE);
				}
				else if(Data.userData.jugnooBalance < MIN_BALANCE_ALERT_VALUE){
					textViewInRideLowJugnooCash.setVisibility(View.VISIBLE);
				}
				else{
					textViewInRideLowJugnooCash.setVisibility(View.GONE);
				}
			}
			else{
				textViewInRideLowJugnooCash.setVisibility(View.GONE);
			}
		}
		else{
			textViewInRideLowJugnooCash.setVisibility(View.GONE);
		}
	}
	
	
	public void setFareFactorToInitialState(){
		try {
			if((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)){
				if(Data.userData.fareFactor > 1 || Data.userData.fareFactor < 1){
                    relativeLayoutInitialFareFactor.setVisibility(View.VISIBLE);
					textViewCurrentFareFactor.setText(decimalFormat.format(Data.userData.fareFactor) +"x");
				}
				else{
                    relativeLayoutInitialFareFactor.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	
	
	public void setAssignedDriverData(PassengerScreenMode mode){
		textViewInRideDriverName.setText(Data.assignedDriverInfo.name);
		if(!"".equalsIgnoreCase(Data.assignedDriverInfo.carNumber)){
			textViewInRideDriverCarNumber.setText(Data.assignedDriverInfo.carNumber);
		}
		else{
			textViewInRideDriverCarNumber.setText("");
		}
		
		if(!"No Promo Code applied".equalsIgnoreCase(Data.assignedDriverInfo.promoName)){
			textViewInRidePromoName.setText(Data.assignedDriverInfo.promoName);
		}
		else{
			textViewInRidePromoName.setText("");
		}

		
		textViewInRideFareFactor.setVisibility(View.VISIBLE);
		if(Data.userData.fareFactor > 1){
			textViewInRideFareFactor.setText("Price: "+decimalFormat.format(Data.userData.fareFactor)+"x");
		}
		else if(Data.userData.fareFactor < 1){
			textViewInRideFareFactor.setText("Price: "+decimalFormat.format(Data.userData.fareFactor)+"x");
		}
		else{
			textViewInRideFareFactor.setText("");
		}
		
		if(textViewInRidePromoName.getText().length() == 0 && textViewInRideFareFactor.getText().length() == 0){
			relativeLayoutInRideInfo.setVisibility(View.GONE);
		}
		else{
			relativeLayoutInRideInfo.setVisibility(View.VISIBLE);
		}
		
		
		
		
		if(PassengerScreenMode.P_REQUEST_FINAL == mode){
			updateDriverETAText();
		}
		else{
			textViewInRideState.setText("Ride in progress");
		}
		
		
		Data.assignedDriverInfo.image = Data.assignedDriverInfo.image.replace("http://graph.facebook", "https://graph.facebook");
		try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.image).transform(new CircleTransform()).into(imageViewInRideDriver);}catch(Exception e){}
		
		
		Data.assignedDriverInfo.carImage = Data.assignedDriverInfo.carImage.replace("http://graph.facebook", "https://graph.facebook");
		try{Picasso.with(HomeActivity.this).load(Data.assignedDriverInfo.carImage).transform(new CircleTransform()).into(imageViewInRideDriverCar);}catch(Exception e){}
		
	}
	
	
	public void updateDriverETAText(){
		if(!"".equalsIgnoreCase(Data.assignedDriverInfo.eta)){
			try{
				double etaMin = Double.parseDouble(Data.assignedDriverInfo.eta);
				if(etaMin > 1){
					textViewInRideState.setText("Will arrive in "+Data.assignedDriverInfo.eta+" minutes");
				}
				else{
					textViewInRideState.setText("Will arrive in "+Data.assignedDriverInfo.eta+" minute");
				}
			} catch(Exception e){
				e.printStackTrace();
				textViewInRideState.setText("Will arrive in "+Data.assignedDriverInfo.eta+" minutes");
			}
		}
	}
	
	
	
	
	
	
	

	
	
	
	
	void buildAlertMessageNoGps() {
//		!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//		&&
		if(!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
			if(gpsDialogAlert != null && gpsDialogAlert.isShowing()){
		    }
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("The app needs Location Services to be enabled. Enable it from Settings.")
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
				
				try {
                    if(activityResumed) {
                        callAndHandleStateRestoreAPI(false);
                        initiateTimersForStates(passengerScreenMode);
                        if (userMode == UserMode.PASSENGER &&
                            (PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode)) {
                            if (map != null && myLocation != null) {
                                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())), 500, null);
                            }
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			    
			    initializeFusedLocationFetchers();

		}
		
		HomeActivity.checkForAccessTokenChange(this);
		
		activityResumed = true;
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
	
	
	
	
	public void saveDataOnPause(){
		new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
					if(userMode == UserMode.PASSENGER){
						
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
		
		destroyFusedLocationFetchers();
		
		GCMIntentService.clearNotifications(getApplicationContext());
		saveDataOnPause();
		
		try{
			if(userMode == UserMode.PASSENGER){
                pauseAllTimers();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		super.onPause();
		
	}
	
	
	
	   
	
	@Override
	public void onBackPressed() {
		try{
			if(PassengerScreenMode.P_SEARCH == passengerScreenMode){
				passengerScreenMode = PassengerScreenMode.P_INITIAL;
				switchPassengerScreen(passengerScreenMode);
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
        	saveDataOnPause();
        	
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
	
	
	
	

	
	
	
	public Thread autoCompleteThread;
    public boolean refreshingAutoComplete = false;
	
	public synchronized void getSearchResults(final String searchText, final LatLng latLng){
		try {
            if(!refreshingAutoComplete) {
                progressBarSearch.setVisibility(View.VISIBLE);

                if (autoCompleteThread != null) {
                    autoCompleteThread.interrupt();
                }

                autoCompleteThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshingAutoComplete = true;
                        autoCompleteSearchResults.clear();
                        autoCompleteSearchResults.addAll(MapUtils.getAutoCompleteSearchResultsFromGooglePlaces(searchText, latLng));
                        setSearchResultsToList();
                        refreshingAutoComplete = false;
                        autoCompleteThread = null;
                    }
                });
                autoCompleteThread.start();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void setSearchResultsToList(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressBarSearch.setVisibility(View.GONE);
				
				if(autoCompleteSearchResults.size() == 0){
					autoCompleteSearchResults.add(new AutoCompleteSearchResult("No results found", "", ""));
				}
				
				searchListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	
	public synchronized void getSearchResultFromPlaceId(final String placeId){
		progressBarInitialSearch.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				SearchResult searchResult = MapUtils.getSearchResultsFromPlaceIdGooglePlaces(placeId);
				setSearchResultToMapAndText(searchResult);
			}
		}).start();
	}
	
	public synchronized void setSearchResultToMapAndText(final SearchResult searchResult){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressBarInitialSearch.setVisibility(View.GONE);
				if(map != null && searchResult != null){
					textViewInitialSearch.setText(searchResult.name);
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResult.latLng, 14), 1000, null);
				}
			}
		});
	}
	
	
	
	class ViewHolderSearchItem {
		TextView textViewSearchName, textViewSearchAddress;
		LinearLayout relative;
		int id;
	}

	class SearchListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderSearchItem holder;

		public SearchListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return autoCompleteSearchResults.size();
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
				holder = new ViewHolderSearchItem();
				convertView = mInflater.inflate(R.layout.list_item_search_item, null);
				
				holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName); 
				holder.textViewSearchName.setTypeface(Fonts.latoRegular(HomeActivity.this));
				holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress); 
				holder.textViewSearchAddress.setTypeface(Fonts.latoRegular(HomeActivity.this));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderSearchItem) convertView.getTag();
			}
			

            try {
                holder.id = position;

                holder.textViewSearchName.setText(autoCompleteSearchResults.get(position).name);
                holder.textViewSearchAddress.setText(autoCompleteSearchResults.get(position).address);

                holder.relative.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        holder = (ViewHolderSearchItem) v.getTag();
                        Utils.hideSoftKeyboard(HomeActivity.this, editTextSearch);
                        AutoCompleteSearchResult autoCompleteSearchResult = autoCompleteSearchResults.get(holder.id);
                        if (!"".equalsIgnoreCase(autoCompleteSearchResult.placeId)) {
                            textViewInitialSearch.setText(autoCompleteSearchResult.name);
                            passengerScreenMode = PassengerScreenMode.P_INITIAL;
                            switchPassengerScreen(passengerScreenMode);
                            getSearchResultFromPlaceId(autoCompleteSearchResult.placeId);
                        }
                    }
                });
            } catch(Exception e){
                e.printStackTrace();
            }

			return convertView;
		}
		
		@Override
		public synchronized void notifyDataSetChanged() {
			if(autoCompleteSearchResults.size() > 1){
				if(autoCompleteSearchResults.contains(new AutoCompleteSearchResult("No results found", "", ""))){
					autoCompleteSearchResults.remove(autoCompleteSearchResults.indexOf(new AutoCompleteSearchResult("No results found", "", "")));
				}
			}
			super.notifyDataSetChanged();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class GetDistanceTimeAddress extends AsyncTask<Void, Void, String>{
	    String url;
	    
	    String distance, duration;
	    
	    LatLng destination;
	    
	    boolean driverAcceptPushRecieved;
	    String etaMinutes = "1", farAwayCity = "";
	    
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
				        
				        textViewNearestDriverETA.setText("Finding nearby drivers...");
				        dontCallRefreshDriver = false;
				        
					}
				});
		        etaMinutes = "1";
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
		    			
		    			Log.i("nameValuePairs in find_a_driver", "="+nameValuePairs);
		    			
		    			HttpRequester simpleJSONParser = new HttpRequester();
		    			String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/find_a_driver", nameValuePairs);
		    			Log.i("result in find_a_driver", "="+result);
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			
//		    			{
//		    			    "flag": 175,
//		    			    "drivers": [
//		    			        {
//		    			            "user_id": 209,
//		    			            "user_name": "Driver 2",
//		    			            "phone_no": "+919780298413",
//		    			            "latitude": 30.718963,
//		    			            "longitude": 76.810523,
//		    			            "rating": 5
//		    			        }
//		    			    ],
//		    			    "eta": 1
//		    			}
		    			
		    			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
		    			}
		    			else{
		    				try{
		    					JSONObject jObj = new JSONObject(result);
		    					new JSONParser().parseDriversToShow(jObj, "drivers");
		    					etaMinutes = jObj.getString("eta");
		    					Data.userData.fareFactor = jObj.getDouble("fare_factor");
		    					
		    					if(jObj.has("far_away_city")){
		    						farAwayCity = jObj.getString("far_away_city");
		    					}
		    				}
		    				catch(Exception e){
		    					e.printStackTrace();
		    				}
		    			}
		    		}
		    		
					
		    		if(!driverAcceptPushRecieved){
		    			return etaMinutes;
		    		}
		    		else if(driverAcceptPushRecieved){
                        if(Data.assignedDriverInfo != null) {
                            LatLng source = Data.assignedDriverInfo.latLng;

                            if (source == null) {
                                return "error";
                            }

                            this.url = MapUtils.makeDistanceMatrixURL(source, destination);
                            HttpRequester jParser = new HttpRequester();
                            String response = jParser.getJSONFromUrl(url);
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if ("OK".equalsIgnoreCase(status)) {
                                JSONObject element0 = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                                distance = element0.getJSONObject("distance").getString("text");
                                duration = element0.getJSONObject("duration").getString("text");
                                return "Distance: " + distance + "\n" + "Duration: " + duration;
                            }
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
                        try {
                            if (!driverAcceptPushRecieved) {
                                showDriverMarkersAndPanMap(destination);
                            }
                            dontCallRefreshDriver = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dontCallRefreshDriver = false;
                                }
                            }, 5000);

                            if (!"error".equalsIgnoreCase(result)) {
                                if (!driverAcceptPushRecieved) {
                                    if (Data.driverInfos.size() == 0) {
                                        textViewNearestDriverETA.setText("No drivers nearby");
                                    } else {
                                        if ("1".equalsIgnoreCase(etaMinutes)) {
                                            textViewNearestDriverETA.setText("Nearest driver is " + etaMinutes + " minute away");
                                        } else {
                                            textViewNearestDriverETA.setText("Nearest driver is " + etaMinutes + " minutes away");
                                        }
                                    }
                                }
                            } else {
                                if (!driverAcceptPushRecieved) {
                                    textViewNearestDriverETA.setText("Couldn't find drivers nearby.");
                                }
                            }

                            if (!"".equalsIgnoreCase(farAwayCity)) {
                                textViewNearestDriverETA.setText(farAwayCity);

                                imageViewRideNow.setVisibility(View.GONE);
                                imageViewRideLater.setVisibility(View.GONE);
                                relativeLayoutJugnooAnim.setVisibility(View.GONE);

                                changeLocalityBtn.setVisibility(View.VISIBLE);
                            } else {
                                imageViewRideNow.setVisibility(View.VISIBLE);
                                imageViewRideLater.setVisibility(View.VISIBLE);
                                relativeLayoutJugnooAnim.setVisibility(View.VISIBLE);

                                changeLocalityBtn.setVisibility(View.GONE);
                            }

                            setFareFactorToInitialState();

                            if (driverAcceptPushRecieved) {
                                try {
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

                                    editor.commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
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
		markerOptions.anchor(0.5f, 0.5f);
		map.addMarker(markerOptions);
	}
	
	public void addCurrentLocationAddressMarker(LatLng latLng){
		try {
			if (Data.userData.canChangeLocation == 0) {
				addUserCurrentLocationAddressMarker(latLng);
			}
		} catch (Exception e) {
		}
	}
	
	public void addUserCurrentLocationAddressMarker(LatLng latLng){
		try {
			if(currentLocationMarker != null){
				currentLocationMarker.remove();
			}
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.title("customer_current_location");
			markerOptions.snippet("");
			markerOptions.position(latLng);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(CustomMapMarkerCreator.createPinMarkerBitmap(HomeActivity.this, assl)));
			currentLocationMarker = map.addMarker(markerOptions);
		} catch (Exception e) {
		}
	}
	
	
	public void showDriverMarkersAndPanMap(final LatLng userLatLng){
		if(userMode == UserMode.PASSENGER && 
				((PassengerScreenMode.P_INITIAL == passengerScreenMode || PassengerScreenMode.P_SEARCH == passengerScreenMode) 
						|| PassengerScreenMode.P_ASSIGNING == passengerScreenMode)){
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
							
							double distance = MapUtils.distance(userLatLng, farthestLatLng);
							if(distance > 1000){
								boundsBuilder.include(new LatLng(userLatLng.latitude, farthestLatLng.longitude));
								boundsBuilder.include(new LatLng(farthestLatLng.latitude, userLatLng.longitude));
								boundsBuilder.include(new LatLng(userLatLng.latitude, ((2*userLatLng.longitude) - farthestLatLng.longitude)));
								boundsBuilder.include(new LatLng(((2*userLatLng.latitude) - farthestLatLng.latitude), userLatLng.longitude));
							}
							else{
								boundsBuilder.include(new LatLng((userLatLng.latitude - (0.01)), userLatLng.longitude));
								boundsBuilder.include(new LatLng((userLatLng.latitude + (0.01)), userLatLng.longitude));
							}
							
							boundsBuilder.include(userLatLng);
							
							try {
								final LatLngBounds bounds = boundsBuilder.build();
								final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										try {
											map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int)(160*minScaleRatio)), 1000, null);
											mapTouchedOnce = true;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, 1000);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else{
							boundsBuilder.include(new LatLng((userLatLng.latitude - (0.01)), userLatLng.longitude));
							boundsBuilder.include(new LatLng((userLatLng.latitude + (0.01)), userLatLng.longitude));
							boundsBuilder.include(userLatLng);
							
							try {
								final LatLngBounds bounds = boundsBuilder.build();
								final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										try {
											map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int)(160*minScaleRatio)), 1000, null);
											mapTouchedOnce = true;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, 1000);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}
		}
		if(userMode == UserMode.PASSENGER && (passengerScreenMode == PassengerScreenMode.P_ASSIGNING)){
			addUserCurrentLocationAddressMarker(userLatLng);
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
			client.post(Config.getServerUrl() + "/cancel_the_request", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
					
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							callAndHandleStateRestoreAPI(true);
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
								}
								else{
									customerUIBackToInitialAfterCancel();
									FlurryEventLogger.cancelRequestPressed(Data.userData.accessToken, Data.cSessionId);
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
	
	
	public void customerUIBackToInitialAfterCancel(){
		cancelTimerRequestRide();
		
		passengerScreenMode = PassengerScreenMode.P_INITIAL;
		switchPassengerScreen(passengerScreenMode);
		
		if(map != null && pickupLocationMarker != null){
			pickupLocationMarker.remove();
		}
			
		callMapTouchedRefreshDrivers();
	}
	
	
	
	
	
	
	
	
	
	
	public void initializeStartRideVariables(){
		
		lastLocation = null;
		
		HomeActivity.previousWaitTime = 0;
		HomeActivity.previousRideTime = 0;
		HomeActivity.totalDistance = -1;
		
		if(HomeActivity.deltaLatLngPairs == null){
			HomeActivity.deltaLatLngPairs = new ArrayList<LatLngPair>();
		}
		HomeActivity.deltaLatLngPairs.clear();
		
		clearRideSPData();
	}
	
	
	
	
	
	
	
	
	

	
	


	
	
	
	void noDriverAvailablePopup(final Activity activity, boolean zeroDriversNearby, String message){
		try {
			if(noDriversDialog != null && noDriversDialog.isShowing()){
				noDriversDialog.dismiss();
			}
			noDriversDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			noDriversDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			noDriversDialog.setContentView(R.layout.dialog_custom_one_button);

			FrameLayout frameLayout = (FrameLayout) noDriversDialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = noDriversDialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			noDriversDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			noDriversDialog.setCancelable(true);
			noDriversDialog.setCanceledOnTouchOutside(true);

			TextView textHead = (TextView) noDriversDialog.findViewById(R.id.textHead);
			textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
			textHead.setVisibility(View.GONE);
			
			TextView textMessage = (TextView) noDriversDialog.findViewById(R.id.textMessage);
			textMessage.setTypeface(Fonts.latoRegular(activity));

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
			}
			

			Button btnOk = (Button) noDriversDialog.findViewById(R.id.btnOk);
			btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					noDriversDialog.dismiss();
					noDriversDialog = null;
				}
			});
			
			frameLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					noDriversDialog.dismiss();
					noDriversDialog = null;
				}
			});
			
			noDriversDialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});

			noDriversDialog.show();
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
				client.post(Config.getServerUrl() + "/accept_app_rating_request", params,
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
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.alertPopup(activity, "", message);
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
	
	
	public void getRideSummaryAPI(final Activity activity, final String engagementId) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				RequestParams params = new RequestParams();
				params.put("access_token", Data.userData.accessToken);
				params.put("engagement_id", engagementId);
				AsyncHttpClient client = Data.getClient();
				client.post(Config.getServerUrl() + "/get_ride_summary", params,
						new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;
							@Override
							public void onFailure(Throwable arg3) {
								DialogPopup.dismissLoadingDialog();
								endRideRetryDialog(activity, engagementId, Data.SERVER_NOT_RESOPNDING_MSG);
							}

							@Override
							public void onSuccess(String response) {
								Log.i("Server response get_ride_summary", "response = " + response);
								DialogPopup.dismissLoadingDialog();
								try {
									jObj = new JSONObject(response);
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.RIDE_ENDED.getOrdinal() == flag){
											
//											{
//											    "pickup_address": "1050-1091, Madhya Marg, 28B, Sector 26 East, Chandigarh 160102, India",
//											    "drop_address": "1050-1091, Madhya Marg, 28B, Sector 26 East, Chandigarh 160102, India",
//											    "pickup_time": "05:51 PM",
//											    "drop_time": "05:51 PM",
//											    "fare": 26,
//											    "discount": 0,
//											    "paid_using_wallet": 0,
//											    "to_pay": 26,
//											    "distance": 0,
//											    "ride_time": 1,
//											    "flag": 115
//											}
											
											try{
												if(jObj.has("rate_app")){
													Data.customerRateAppFlag = jObj.getInt("rate_app");
												}
											} catch(Exception e){
												e.printStackTrace();
											}
											
											try{
												if(jObj.has("jugnoo_balance")){
													Data.userData.jugnooBalance = jObj.getDouble("jugnoo_balance");
												}
											} catch(Exception e){
												e.printStackTrace();
											}
											
											double baseFare = Data.fareStructure.fixedFare;
											if(jObj.has("base_fare")){
												baseFare = jObj.getDouble("base_fare");
											}
											
											String banner = "";
											if(jObj.has("banner")){
												banner = jObj.getString("banner");
											}
											
											Data.endRideData = new EndRideData(engagementId, 
													jObj.getString("pickup_address"), 
													jObj.getString("drop_address"), 
													jObj.getString("pickup_time"), 
													jObj.getString("drop_time"), 
													banner,
													jObj.getDouble("fare"), 
													jObj.getDouble("discount"), 
													jObj.getDouble("paid_using_wallet"), 
													jObj.getDouble("to_pay"), 
													jObj.getDouble("distance"), 
													jObj.getDouble("ride_time"),
													baseFare);
											
											lastLocation = null;
											clearSPData();
											map.clear();
											passengerScreenMode = PassengerScreenMode.P_RIDE_END;
											switchPassengerScreen(passengerScreenMode);
											
											setUserData();
											
										}
										else{
											endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
										}
									}
								}  catch (Exception exception) {
									exception.printStackTrace();
									endRideRetryDialog(activity, engagementId, Data.SERVER_ERROR_MSG);
								}
							}
						});
			}
			else{
				endRideRetryDialog(activity, engagementId, Data.CHECK_INTERNET_MSG);
			}
	}
	
	public void endRideRetryDialog(final Activity activity, final String engagementId, String errorMessage){
		DialogPopup.alertPopupWithListener(activity, "", errorMessage + "\nTap OK to retry", new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRideSummaryAPI(activity, engagementId);
			}
		});
	}
	
	
	
	
	
	
	public void applyPromotionToScheduleAPI(final Activity activity, final PromotionDialog promotionDialog, PromoCoupon promoCoupon, String pickupId) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("pickup_id", pickupId);
			
			if(promoCoupon != null){
				if(promoCoupon instanceof CouponInfo){
					params.put("coupon_to_apply", ""+promoCoupon.id);
					if(promoCoupon.id == 0){
						params.put("promo_to_apply", ""+promoCoupon.id);
					}
				}
				else if(promoCoupon instanceof PromotionInfo){
					params.put("promo_to_apply", ""+promoCoupon.id);
				}
			}

			Log.i("params add_promotion_to_schedule", "=" + params);
			
			AsyncHttpClient fetchPromotionClient = Data.getClient();
			fetchPromotionClient.post(Config.getServerUrl() + "/add_promotion_to_schedule", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;
					
						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
							DialogPopup.dismissLoadingDialog();
						}
						
						@Override
						public void onSuccess(String response) {
							Log.e("Server response show_available_promotions", "response = " + response);
							try {
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										
										promotionDialog.dismissAlert();
										
										String message = jObj.getString("message");
										DialogPopup.alertPopup(activity, "", message);
									}
									else if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else{
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	
	

	//Customer's timer
	Timer timerDriverLocationUpdater;
	TimerTask timerTaskDriverLocationUpdater;
	
	public void startDriverLocationUpdateTimer(){

        cancelDriverLocationUpdateTimer();
		
		try {
			timerDriverLocationUpdater = new Timer();
			
			timerTaskDriverLocationUpdater = new TimerTask() {
				
				@Override
				public void run() {
                    try{
					if (AppStatus.getInstance(HomeActivity.this).isOnline(HomeActivity.this)
                        && (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode)
                        && (Data.userData != null)
                        && (Data.assignedDriverInfo != null)) {

                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
                        nameValuePairs.add(new BasicNameValuePair("driver_id", Data.assignedDriverInfo.userId));


                        HttpRequester simpleJSONParser = new HttpRequester();
                        String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/get_driver_current_location", nameValuePairs);

                        Log.e("result of get_driver_current_location", "=" + result);
                        try {
                            JSONObject jObj = new JSONObject(result);

                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                    HomeActivity.logoutUser(HomeActivity.this);
                                }
                            } else {
//										{"flag":117,"latitude":30.718956,"longitude":76.810267,"eta":0}
                                int flag = jObj.getInt("flag");
                                if (ApiResponseFlags.DRIVER_LOCATION.getOrdinal() == flag) {
                                    final LatLng driverCurrentLatLng = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                                    String eta = "";
                                    if (jObj.has("eta")) {
                                        eta = jObj.getString("eta");
                                    }
                                    if (Data.assignedDriverInfo != null) {
                                        Data.assignedDriverInfo.latLng = driverCurrentLatLng;
                                        Data.assignedDriverInfo.eta = eta;
                                    }

                                    HomeActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                if (PassengerScreenMode.P_REQUEST_FINAL == passengerScreenMode) {
                                                    if (myLocation != null && map != null) {
                                                        if (HomeActivity.this.hasWindowFocus()) {
                                                            driverLocationMarker.setPosition(driverCurrentLatLng);
                                                            updateDriverETAText();
                                                            LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                                            double distance = MapUtils.distance(myLatLng, driverCurrentLatLng);
                                                            if (distance > 1000) {
                                                                final float minScaleRatio = Math.min(ASSL.Xscale(), ASSL.Yscale());
                                                                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                                                                boundsBuilder.include(myLatLng);
                                                                boundsBuilder.include(driverCurrentLatLng);
                                                                LatLngBounds bounds = boundsBuilder.build();
                                                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (160 * minScaleRatio)), 1000, null);
                                                            }
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
				}
			};
			
			
			timerDriverLocationUpdater.scheduleAtFixedRate(timerTaskDriverLocationUpdater, 10, 15000);
            Log.i("timerDriverLocationUpdater", "started");

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
            Log.i("timerDriverLocationUpdater", "canceled");
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
            Log.i("timerUpdateDrivers", "started");
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
            Log.i("timerUpdateDrivers", "canceled");
		} catch(Exception e){
			e.printStackTrace();
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
						if (myLocation != null &&
                            map != null &&
                            HomeActivity.this.hasWindowFocus()) {
                            HomeActivity.this.runOnUiThread(new Runnable() {

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
            Log.i("timerMapAnimateAndUpdateRideData", "started");
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
            Log.i("timerMapAnimateAndUpdateRideData", "canceled");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	void callAnAutoPopup(final Activity activity, int totalPromoCoupons) {
		try {
			
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			new ASSL(activity, (FrameLayout)dialog.findViewById(R.id.rv), 1134, 720, true);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.latoRegular(activity));
			
			textHead.setVisibility(View.VISIBLE);
			textHead.setText("Chalo Jugnoo Se");
			
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Fonts.latoRegular(activity));
			
			btnOk.setText("OK");
			btnCancel.setText("Cancel");
			
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
			
			Data.pickupLatLng = map.getCameraPosition().target;
			double distance = MapUtils.distance(Data.pickupLatLng, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
			if(distance > MAP_PAN_DISTANCE_CHECK){
				textMessage.setText("The pickup location you have set is different from your current location. Are you sure you want an auto at this pickup location?");
				dialog.show();
			}
			else{
				if(totalPromoCoupons == 0){
					textMessage.setText("Do you want an auto to pick you up?");
					dialog.show();
				}
				else{
					initiateRequestRide(true);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	void switchToScheduleScreen(final Activity activity){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.add(Calendar.MINUTE, (5 - (calendar.get(Calendar.MINUTE) % 5)));
		
		if(map != null){
			LatLng latLng = map.getCameraPosition().target;
			if(latLng != null){
				ScheduleRideDialog scheduleRideDialog = new ScheduleRideDialog(HomeActivity.this, calendar, latLng, new PromotionDialogEventHandler() {
					
					@Override
					public void onOkPressed(PromoCoupon promoCoupon, int totalPromoCoupons) {
					}
					
					@Override
					public void onOkOnlyPressed(PromotionDialog promotionDialog, PromoCoupon promoCoupon, String pickupId) {
						applyPromotionToScheduleAPI(activity, promotionDialog, promoCoupon, pickupId);
					}
					
					@Override
					public void onCancelPressed() {
					}
				});
				scheduleRideDialog.showDialog(HomeActivity.this);
			}
		}
		
	}
	
	
	
	
	
	
	
	
		
		
		/**
		 * Displays popup to rate the app
		 */
		public void rateAppPopup(final Activity activity) {
			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_custom_two_buttons);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, false);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.latoRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.latoRegular(activity));

				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
				
				textHead.setVisibility(View.VISIBLE);
				textHead.setText("Rate Us");
				textMessage.setText("Liked our services!!! Please rate us on Play Store");
				
				Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD); btnOk.setText("RATE NOW");
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Fonts.latoRegular(activity)); btnCancel.setText("LATER");
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						acceptAppRatingRequestAPI(activity);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=product.clicklabs.jugnoo"));
						activity.startActivity(intent);
					}
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});
				
				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
					
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
				dialog.setContentView(R.layout.dialog_edittext_confirm);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.latoRegular(activity), Typeface.BOLD);
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.latoRegular(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Fonts.latoRegular(activity));
				
				textHead.setText("Confirm Debug Password");
				textMessage.setText("Please enter password to continue.");
				
				textHead.setVisibility(View.GONE);
				textMessage.setVisibility(View.GONE);
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Fonts.latoRegular(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(Config.getDebugPassword().equalsIgnoreCase(code)){
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
				
				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
					
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
		
		void changeDebugModePopup(final Activity activity) {
			try {
				String message = "";
				if(appMode == AppMode.DEBUG){
					message = "App is in DEBUG mode.\nChange to:";
				}
				else{
					message = "App is in NORMAL mode.\nChange to:";
				}
				DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, "NORMAL", "DEBUG", 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								appMode = AppMode.NORMAL;
							}
						}, 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								appMode = AppMode.DEBUG;
							}
						}, true, false);
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
				
				closeCancelActivity();
				
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
							DialogPopup.alertPopup(HomeActivity.this, "", "Driver has canceled the ride.");
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
	
	
	
	@Override
	public void onCancelCompleted() {
		customerUIBackToInitialAfterCancel();
		FlurryEventLogger.cancelRequestPressed(Data.userData.accessToken, Data.cSessionId);
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
			
			String promoName = JSONParser.getPromoName(jObj);
			String eta = "";
			if(jObj.has("eta")){
				eta = jObj.getString("eta");
			}
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName, 
					driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta);
			
			
			double fareFactor = 1.0;
			if(jObj.has("fare_factor")){
				fareFactor = jObj.getDouble("fare_factor");
			}
			
			Data.userData.fareFactor = fareFactor;
			
			
			
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
			
			String promoName = JSONParser.getPromoName(jObj);
			
			String eta = "";
			if(jObj.has("eta")){
				eta = jObj.getString("eta");
			}
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, latitude, longitude, userName, 
					driverImage, driverCarImage, driverPhone, driverRating, carNumber, freeRide, promoName, eta);
			
			
			double fareFactor = 1.0;
			if(jObj.has("fare_factor")){
				fareFactor = jObj.getDouble("fare_factor");
			}
			
			Data.userData.fareFactor = fareFactor;
			
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

		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_C_TOTAL_FARE, "0");
		editor.putString(Data.SP_C_WAIT_TIME, "0");
		editor.putString(Data.SP_C_RIDE_TIME, "0");

		editor.commit();

		Database.getInstance(this).deleteSavedPath();
		Database.getInstance(this).close();

	}
	


	@Override
	public void customerEndRideInterrupt(final String engagementId) {
		try{
			if(userMode == UserMode.PASSENGER && passengerScreenMode == PassengerScreenMode.P_IN_RIDE){
				closeCancelActivity();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						getRideSummaryAPI(HomeActivity.this, engagementId);
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
			closeCancelActivity();
			callAndHandleStateRestoreAPI(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void closeCancelActivity(){
		try{
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					try {
						if(RideCancellationActivity.activityCloser != null){
							RideCancellationActivity.activityCloser.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void logoutUser(final Activity cont){
		try{

			SharedPreferences pref = cont.getSharedPreferences("myPref", 0);
			Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			Data.clearDataOnLogout(cont);
			
			PicassoTools.clearCache(Picasso.with(cont));

            if(cont.hasWindowFocus()) {
                cont.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        new FacebookLoginHelper().logoutFacebook();

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
            }
            else{
                Intent intent = new Intent(cont, SplashNewActivity.class);
                intent.putExtra("no_anim", "yes");
                cont.startActivity(intent);
                cont.finish();
                cont.overridePendingTransition(
                    R.anim.left_in,
                    R.anim.left_out);
            }
        
		} catch(Exception e){e.printStackTrace();}
		
	}
	
	
	
	
	
	
	
	
	
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Config.getFlurryKey());
		FlurryAgent.onStartSession(this, Config.getFlurryKey());
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
				else{
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
				
			}
		}
	}
	
	
	
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
								
								updateCancelButtonUI();
								
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
								
								if(promoCouponSelectedForRide != null){
									if(promoCouponSelectedForRide instanceof CouponInfo){
										nameValuePairs.add(new BasicNameValuePair("coupon_to_apply", ""+promoCouponSelectedForRide.id));
										if(promoCouponSelectedForRide.id == 0){
											nameValuePairs.add(new BasicNameValuePair("promo_to_apply", ""+promoCouponSelectedForRide.id));
										}
									}
									else if(promoCouponSelectedForRide instanceof PromotionInfo){
										nameValuePairs.add(new BasicNameValuePair("promo_to_apply", ""+promoCouponSelectedForRide.id));
									}
								}
								
								if("".equalsIgnoreCase(Data.cSessionId)){
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "0"));
									nameValuePairs.add(new BasicNameValuePair("fare_factor", ""+Data.userData.fareFactor));
									if(myLocation != null && myLocation.hasAccuracy()){
										nameValuePairs.add(new BasicNameValuePair("location_accuracy", ""+myLocation.getAccuracy()));
									}
								}
								else{
									nameValuePairs.add(new BasicNameValuePair("duplicate_flag", "1"));
								}
								
								Log.i("nameValuePairs of request_ride", "="+nameValuePairs);
								String response = new HttpRequester().getJSONFromUrlParams(Config.getServerUrl()+"/request_ride", nameValuePairs);
								
								Log.e("response of request_ride", "="+response);
								
								if(response.contains(HttpRequester.SERVER_TIMEOUT)){
									Log.e("timeout","=");
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
//											DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
																DialogPopup.alertPopup(HomeActivity.this, "", errorMessage);
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
								
								updateCancelButtonUI();
								
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
			
			FlurryEventLogger.requestRidePressed(Data.userData.accessToken, Data.pickupLatLng);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelTimerRequestRide(){
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
	
	
	public void updateCancelButtonUI(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if("".equalsIgnoreCase(Data.cSessionId)){
					initialCancelRideBtn.setBackgroundResource(R.drawable.button_yellow_pressed);
					initialCancelRideBtn.setTextColor(getResources().getColor(R.color.white_alpha));
				}
				else{
					initialCancelRideBtn.setBackgroundResource(R.drawable.button_yellow_selector);
					initialCancelRideBtn.setTextColor(getResources().getColor(R.drawable.text_color_white_alpha_selector));
				}
			}
		});
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

	
	@Override
	public void onAfterRideFeedbackSubmitted(final int givenRating) {
		runOnUiThread(new Runnable() {

            @Override
            public void run() {
                userMode = UserMode.PASSENGER;

                switchUserScreen();

                passengerScreenMode = PassengerScreenMode.P_INITIAL;
                switchPassengerScreen(passengerScreenMode);

                if (givenRating >= 4 && Data.customerRateAppFlag == 1) {
                    rateAppPopup(activity);
                }
            }
        });
		
	}
	
	
	@Override
	public void onJugnooCashAddedByDriver(final double jugnooBalance, final String message) {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(Data.userData != null){
						Data.userData.jugnooBalance = jugnooBalance;
						setUserData();
					}
					DialogPopup.alertPopupTwoButtonsWithListeners(HomeActivity.this, 
							"Jugnoo Cash added", 
							message, 
							"Check Balance", "Call Support", 
							new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									HomeActivity.this.startActivity(new Intent(HomeActivity.this, WalletActivity.class));
									overridePendingTransition(R.anim.right_in, R.anim.right_out);
								}
							}, 
							new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Utils.openCallIntent(HomeActivity.this, Config.getSupportNumber());
								}
							}, true, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	

	
	public void callAndHandleStateRestoreAPI(final boolean showDialogs) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(Data.userData != null){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									if(showDialogs){
										DialogPopup.showLoadingDialog(HomeActivity.this, "Loading...");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						int currentUserStatus = 2;
							String resp = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
							if(resp.contains(HttpRequester.SERVER_TIMEOUT)){
								String resp1 = new JSONParser().getUserStatus(HomeActivity.this, Data.userData.accessToken, currentUserStatus);
								if(resp1.contains(HttpRequester.SERVER_TIMEOUT)){
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											try {
												if(showDialogs){
													DialogPopup.alertPopup(HomeActivity.this, "", Data.SERVER_NOT_RESOPNDING_MSG);
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
								}
							}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									if(showDialogs){
										DialogPopup.dismissLoadingDialog();
									}
									startUIAfterGettingUserStatus();
								} catch (Exception e) {
									e.printStackTrace();
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