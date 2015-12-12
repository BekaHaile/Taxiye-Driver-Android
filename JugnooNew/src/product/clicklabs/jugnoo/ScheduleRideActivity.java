package product.clicklabs.jugnoo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.datastructure.ScheduleOperationMode;
import product.clicklabs.jugnoo.datastructure.ScheduleScreenMode;
import product.clicklabs.jugnoo.datastructure.SearchResult;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.CustomDateTimePicker;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class ScheduleRideActivity extends FragmentActivity{
	
	
	RelativeLayout relative;
	
	Button backBtn, cancelBtn;
	
	GoogleMap map;
	
	RelativeLayout scheduleOptionsMainRl;
	TextView scheduleRideText;
	LinearLayout schedulePickupLocationLinear;
	TextView schedulePickupLocationText, schedulePickupLocationValue;
	LinearLayout scheduleDateTimeLinear;
	TextView scheduleDateTimeText, scheduleDateTimeValue;
	Button scheduleBtn;
	TextView textTerms;

	RelativeLayout scheduleSetPickupLocationRl;
	Button scheduleMyLocationBtn, pickThisLocationBtn;
	ImageView schedulePickupLocationCentrePin;
	
	LinearLayout searchListRl;
	ListView searchListView;
	EditText searchBarEditText;
	Button searchBtn;
	ProgressBar progressBarSearch;

	CustomDateTimePicker customDateTimePicker;
	public static Calendar selectedScheduleCalendar = Calendar.getInstance();
	public static LatLng selectedScheduleLatLng;
	
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	Location myLocation;
	boolean zoomedToMyLocation = false;
	
	ScheduleScreenMode scheduleScreenMode;
	
	ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
	
	ScheduleSearchListAdapter scheduleSearchListAdapter;
	
	public static FutureSchedule editableFutureSchedule;
	
	public static ScheduleOperationMode scheduleOperationMode = ScheduleOperationMode.INSERT;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_ride);
		
		zoomedToMyLocation = false;
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(ScheduleRideActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		cancelBtn = (Button) findViewById(R.id.cancelBtn); cancelBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		scheduleOptionsMainRl = (RelativeLayout) findViewById(R.id.scheduleOptionsMainRl);
		scheduleRideText = (TextView) findViewById(R.id.scheduleRideText); scheduleRideText.setTypeface(Data.latoRegular(getApplicationContext()));
		schedulePickupLocationLinear = (LinearLayout) findViewById(R.id.schedulePickupLocationLinear);
		schedulePickupLocationText = (TextView) findViewById(R.id.schedulePickupLocationText); schedulePickupLocationText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		schedulePickupLocationValue = (TextView) findViewById(R.id.schedulePickupLocationValue); schedulePickupLocationValue.setTypeface(Data.latoRegular(getApplicationContext()));
		scheduleDateTimeLinear = (LinearLayout) findViewById(R.id.scheduleDateTimeLinear);
		scheduleDateTimeText = (TextView) findViewById(R.id.scheduleDateTimeText); scheduleDateTimeText.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		scheduleDateTimeValue = (TextView) findViewById(R.id.scheduleDateTimeValue); scheduleDateTimeValue.setTypeface(Data.latoRegular(getApplicationContext()));
		scheduleBtn = (Button) findViewById(R.id.scheduleBtn); scheduleBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		textTerms = (TextView) findViewById(R.id.textTerms); textTerms.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		scheduleSetPickupLocationRl = (RelativeLayout) findViewById(R.id.scheduleSetPickupLocationRl);
		scheduleMyLocationBtn = (Button) findViewById(R.id.scheduleMyLocationBtn);
		pickThisLocationBtn = (Button) findViewById(R.id.pickThisLocationBtn); pickThisLocationBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		searchListRl = (LinearLayout) findViewById(R.id.searchListRl);
		searchListView = (ListView) findViewById(R.id.searchListView);
		searchBarEditText = (EditText) findViewById(R.id.searchBarEditText); searchBarEditText.setTypeface(Data.latoRegular(getApplicationContext()));
		searchBtn = (Button) findViewById(R.id.searchBtn); searchBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		progressBarSearch = (ProgressBar) findViewById(R.id.progressBarSearch);
		searchBtn.setVisibility(View.GONE);
		progressBarSearch.setVisibility(View.GONE);
		
		
		scheduleSearchListAdapter = new ScheduleSearchListAdapter();
		searchListView.setAdapter(scheduleSearchListAdapter);
		
		if(ScheduleOperationMode.MODIFY == scheduleOperationMode){
			scheduleRideText.setText("Modify Schedule");
			scheduleBtn.setText("RESCHEDULE");
		}
		else{
			scheduleRideText.setText("Schedule Ride");
			scheduleBtn.setText("SCHEDULE");
		}
		
		
		LatLng latLng;
		if(selectedScheduleLatLng == null){
			if(HomeActivity.myLocation != null){
				latLng = new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude());
			}
			else if(Data.latitude != 0 && Data.longitude != 0){
				latLng = new LatLng(Data.latitude, Data.longitude);
			}
			else{
				latLng = new LatLng(30.7500, 76.7800);
			}
			selectedScheduleLatLng = latLng;
		}
		else{
			latLng = selectedScheduleLatLng;
		}
		
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if(map != null){
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setMyLocationEnabled(true);
			map.getUiSettings().setTiltGesturesEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
			
			map.setOnMyLocationChangeListener(onMyLocationChangeListener);
			scheduleMyLocationBtn.setOnClickListener(mapMyLocationClick);
		}
		
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		// schedule layout
		scheduleOptionsMainRl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		schedulePickupLocationLinear.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						scheduleScreenMode = ScheduleScreenMode.PICK_LOCATION;
						switchScheduleScreen(scheduleScreenMode);
					}
				});

		scheduleDateTimeLinear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				customDateTimePicker.set24HourFormat(false);
				customDateTimePicker.setTimePickerIntervalInMinutes(5);
				customDateTimePicker.setDate(selectedScheduleCalendar);
				customDateTimePicker.showDialog();
			}
		});

		scheduleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedScheduleLatLng != null) {
					if(ScheduleOperationMode.MODIFY == scheduleOperationMode){
						if(editableFutureSchedule != null){
							modifyScheduleRideAsync(ScheduleRideActivity.this, editableFutureSchedule.pickupId, selectedScheduleCalendar, selectedScheduleLatLng);
						}
					}
					else{
						insertScheduleRideAsync(ScheduleRideActivity.this, selectedScheduleCalendar, selectedScheduleLatLng);
					}
				} else {
					Toast.makeText(ScheduleRideActivity.this, "Please wait while we get your pickup address", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		textTerms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpParticularActivity.helpSection = HelpSection.SCHEDULES_TNC;
				startActivity(new Intent(ScheduleRideActivity.this, HelpParticularActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});


		pickThisLocationBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (map != null) {
					getSchedulePickupLocationAddress(map.getCameraPosition().target);
					scheduleScreenMode = ScheduleScreenMode.INITIAL;
					switchScheduleScreen(scheduleScreenMode);
				}
			}
		});
		
		searchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String searchText = searchBarEditText.getText().toString().trim();
				if(searchText.length() > 0){
					scheduleScreenMode = ScheduleScreenMode.SEARCH;
					switchScheduleScreen(scheduleScreenMode);
					searchResults.clear();
					scheduleSearchListAdapter.notifyDataSetChanged();
					
					searchText = searchBarEditText.getText().toString().trim();
					try {
						searchText = URLEncoder.encode(searchText, "utf-8");
						getSearchResults(searchText);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						searchBarEditText.requestFocus();
						searchBarEditText.setError("Invalid search text");
					}
				}
				else{
					searchBarEditText.requestFocus();
					searchBarEditText.setError("Search cannot be empty");
				}
				
				
			}
		});
		
		
		searchBarEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					searchBarEditText.setError(null);
				}
			}
		});
		
		searchBarEditText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scheduleScreenMode = ScheduleScreenMode.SEARCH;
				switchScheduleScreen(scheduleScreenMode);
			}
		});
		
		searchBarEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						searchBtn.performClick();
						return false;

					case EditorInfo.IME_ACTION_NEXT:
						return false;

					default:
						return false;
				}
			}
		});
		
		searchBarEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0){
					if(searchBtn.getVisibility() == View.GONE){
						searchBtn.setVisibility(View.VISIBLE);
					}
				}
				else{
					if(searchBtn.getVisibility() == View.VISIBLE){
						searchBtn.setVisibility(View.GONE);
					}
				}
			}
		});

		customDateTimePicker = new CustomDateTimePicker(this,
				new CustomDateTimePicker.ICustomDateTimeListener() {

					@Override
					public void onSet(Dialog dialog, Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						
						Calendar currentCalendar = Calendar.getInstance();
						
						if(Data.userData != null){
							currentCalendar.add(Calendar.MINUTE, Data.userData.schedulingLimitMinutes);
						}
						
						Date currentOffsetDate = currentCalendar.getTime();
					    Date selectedDate = calendarSelected.getTime();
					    long diff = selectedDate.getTime() - currentOffsetDate.getTime();
						
					    if(diff > 0){
							selectedScheduleCalendar = calendarSelected;
							setScheduleDateTimeValue(selectedScheduleCalendar);
					    }
					    else{
					    	Toast.makeText(ScheduleRideActivity.this, "Schedule time must be after "+Data.userData.schedulingLimitMinutes+" minutes than current time", Toast.LENGTH_SHORT).show();
					    }
					}

					@Override
					public void onCancel() {

					}
				});

		
		
		scheduleScreenMode = ScheduleScreenMode.INITIAL;
		switchScheduleScreen(scheduleScreenMode);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(selectedScheduleCalendar == null){
			selectedScheduleCalendar = Calendar.getInstance();
		}
		
		if(selectedScheduleLatLng != null){
			getSchedulePickupLocationAddress(selectedScheduleLatLng);
		}
		
		setScheduleDateTimeValue(selectedScheduleCalendar);
		
	}
	
	
	public void switchScheduleScreen(ScheduleScreenMode mode){
		
		switch(mode){
		
		case INITIAL:
			scheduleOptionsMainRl.setVisibility(View.VISIBLE);
			scheduleSetPickupLocationRl.setVisibility(View.GONE);
			
			backBtn.setVisibility(View.VISIBLE);
			cancelBtn.setVisibility(View.GONE);
			break;
			
		case PICK_LOCATION:
			scheduleOptionsMainRl.setVisibility(View.GONE);
			scheduleSetPickupLocationRl.setVisibility(View.VISIBLE);
			searchListRl.setVisibility(View.GONE);

			backBtn.setVisibility(View.GONE);
			cancelBtn.setVisibility(View.VISIBLE);
			break;
			
		case SEARCH:
			scheduleOptionsMainRl.setVisibility(View.GONE);
			scheduleSetPickupLocationRl.setVisibility(View.VISIBLE);
			searchListRl.setVisibility(View.VISIBLE);

			backBtn.setVisibility(View.GONE);
			cancelBtn.setVisibility(View.VISIBLE);
			
			break;
			
		}
	}
	
	
	public void performBackPressed(){
		Utils.hideSoftKeyboard(ScheduleRideActivity.this, searchBarEditText);
		if(ScheduleScreenMode.SEARCH == scheduleScreenMode){
			scheduleScreenMode = ScheduleScreenMode.PICK_LOCATION;
			switchScheduleScreen(scheduleScreenMode);
		}
		else if(ScheduleScreenMode.PICK_LOCATION == scheduleScreenMode){
			scheduleScreenMode = ScheduleScreenMode.INITIAL;
			switchScheduleScreen(scheduleScreenMode);
		}
		else{
			backFinish();
		}
	}
	
	
	public void backFinish(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	OnMyLocationChangeListener onMyLocationChangeListener = new OnMyLocationChangeListener() {
		
		@Override
		public void onMyLocationChange(Location arg0) {
			if(!zoomedToMyLocation && selectedScheduleLatLng != null){
				map.animateCamera(CameraUpdateFactory.newLatLng(selectedScheduleLatLng));
			}
			myLocation = arg0;
			zoomedToMyLocation = true;
		}
	};
	
	
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
			}
		}
	};
	
	
	
	
	
	
	class ViewHolderScheduleSearch {
		TextView name;
		LinearLayout relative;
		int id;
	}

	class ScheduleSearchListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderScheduleSearch holder;

		public ScheduleSearchListAdapter() {
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
		public View getView(int position, View convertView, ViewGroup parent) {

			
			if (convertView == null) {
				
				holder = new ViewHolderScheduleSearch();
				convertView = mInflater.inflate(R.layout.list_item_search, null);
				
				holder.name = (TextView) convertView.findViewById(R.id.name); holder.name.setTypeface(Data.latoRegular(getApplicationContext()));
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderScheduleSearch) convertView.getTag();
			}
			
			
			holder.id = position;
			
			holder.name.setText(""+searchResults.get(position).name + "\n" + searchResults.get(position).address);
			
			holder.relative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderScheduleSearch) v.getTag();
					SearchResult searchResult = searchResults.get(holder.id);
					if(searchResult.latLng != null){
						searchBarEditText.setText(searchResult.address);
						scheduleScreenMode = ScheduleScreenMode.PICK_LOCATION;
						switchScheduleScreen(scheduleScreenMode);
						map.animateCamera(CameraUpdateFactory.newLatLng(searchResult.latLng), 1000, null);
					}
				}
			});
			return convertView;
		}
		
	}
	
	
	public void setScheduleDateTimeValue(final Calendar calendar){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String dayLongName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
				String monthShortName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				String amOrPm = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
				String hourLongName = (calendar.get(Calendar.HOUR) < 10)?"0"+calendar.get(Calendar.HOUR):""+calendar.get(Calendar.HOUR);
				if(calendar.get(Calendar.HOUR) == 0){
					hourLongName = "12";
				}
				String minuteLongName = (calendar.get(Calendar.MINUTE) < 10)?"0"+calendar.get(Calendar.MINUTE):""+calendar.get(Calendar.MINUTE);
            	scheduleDateTimeValue.setText(dayLongName + ", " + calendar.get(Calendar.DATE) + " " + monthShortName + " " + calendar.get(Calendar.YEAR) 
            			+ ", " + hourLongName + ":" + minuteLongName  + " " + amOrPm);
			}
		});
	}
	
	
	
	public void setSchedulePickupLocationAddress(final String address){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				schedulePickupLocationValue.setText(address);
			}
		});
	}
	
	Thread schedulePickupLocationAddressFetcherThread;
	public void getSchedulePickupLocationAddress(final LatLng schedulePickupLatLng){
		selectedScheduleLatLng = null;
		stopSchedulePickupLocationAddressFetcherThread();
		setSchedulePickupLocationAddress("Loading address...");
		try{
			schedulePickupLocationAddressFetcherThread = new Thread(new Runnable() {
				@Override
				public void run() {
					setSchedulePickupLocationAddress(MapUtils.getGAPIAddress(schedulePickupLatLng));
					selectedScheduleLatLng = schedulePickupLatLng;
				}
			});
			schedulePickupLocationAddressFetcherThread.start();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void stopSchedulePickupLocationAddressFetcherThread(){
		try{
			if(schedulePickupLocationAddressFetcherThread != null){
				schedulePickupLocationAddressFetcherThread.interrupt();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		schedulePickupLocationAddressFetcherThread = null;
	}
	
	
	
	
	
	
	public void setSearchResultsToList(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				searchBtn.setVisibility(View.VISIBLE);
				progressBarSearch.setVisibility(View.GONE);
				
				if(searchResults.size() == 0){
					searchResults.add(new SearchResult("No results found", "", null));
				}
				
				scheduleSearchListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void getSearchResults(final String searchText){
		searchBtn.setVisibility(View.GONE);
		progressBarSearch.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				searchResults.clear();
				searchResults.addAll(MapUtils.getSearchResultsFromGooglePlaces(searchText));
				setSearchResultsToList();
			}
		}).start();
	}
	
	
	
	
	
	/**
	 * ASync for inserting schedule ride event to server
	 */
	public void insertScheduleRideAsync(final Activity activity, Calendar selectedCalendar, LatLng selectedLatLng) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", ""+selectedLatLng.latitude);
			params.put("longitude", ""+selectedLatLng.longitude);
			params.put("pickup_time", ""+DateOperations.getTimeStampfromCalendar(selectedCalendar));

			Log.e("Server hit=", "=" + Data.SERVER_URL + "/insert_pickup_schedule");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("latitude", "="+selectedLatLng.latitude);
			Log.i("longitude", "="+selectedLatLng.longitude);
			Log.i("pickup_time", "="+DateOperations.getTimeStampfromCalendar(selectedCalendar));
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/insert_pickup_schedule", params,
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
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										new DialogPopup().alertPopupWithListener(activity, "", jObj.getString("message"), new View.OnClickListener() {
											
											@Override
											public void onClick(View v) {
												activity.finish();
												activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
											}
										});
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
	
	
	/**
	 * ASync for modifying schedule ride event to server
	 */
	public void modifyScheduleRideAsync(final Activity activity, String pickupId, Calendar selectedCalendar, LatLng selectedLatLng) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("pickup_id", ""+pickupId);
			params.put("latitude", ""+selectedLatLng.latitude);
			params.put("longitude", ""+selectedLatLng.longitude);
			params.put("pickup_time", ""+DateOperations.getTimeStampfromCalendar(selectedCalendar));

			Log.e("Server hit=", "=" + Data.SERVER_URL + "/insert_pickup_schedule");
			Log.i("access_token", "=" + Data.userData.accessToken);
			Log.i("pickup_id", "="+pickupId);
			Log.i("latitude", "="+selectedLatLng.latitude);
			Log.i("longitude", "="+selectedLatLng.longitude);
			Log.i("pickup_time", "="+DateOperations.getTimeStampfromCalendar(selectedCalendar));
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/modify_pickup_schedule", params,
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
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										new DialogPopup().alertPopupWithListener(activity, "", jObj.getString("message"), new View.OnClickListener() {
											
											@Override
											public void onClick(View v) {
												activity.finish();
												activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
											}
										});
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
