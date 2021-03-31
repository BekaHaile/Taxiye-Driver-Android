package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.fragment.app.Fragment;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.google.GAPIAddress;
import product.clicklabs.jugnoo.driver.google.GoogleJungleCaching;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import product.clicklabs.jugnoo.driver.utils.ProgressWheel;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class PlaceSearchListFragment extends Fragment implements FlurryEventNames,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Constants {

	private LinearLayout linearLayoutRoot;

	private EditText editTextSearch;
	private ProgressWheel progressBarSearch;
	private ImageView imageViewSearchCross, imageViewSearchGPSIcon;

	private LinearLayout linearLayoutAddFav;
	private RelativeLayout relativeLayoutAddHome, relativeLayoutAddWork;
	private TextView textViewAddHome, textViewAddWork;
	private ImageView imageViewSep, imageViewSep2;

	private LinearLayout linearLayoutScrollSearch;
	private NonScrollListView listViewSearch;
	private TextView textViewScrollSearch;

	private View rootView;
    private Activity activity;
	private PlacesClient mGoogleApiClient;
	private SearchListAdapter.SearchListActionsHandler searchListActionsHandler;
	private SearchListAdapter searchListAdapter;
	GoogleMap googleMap;
	private ImageView ivLocationMarker;
    private boolean setLocationOnMarker=false;
	private final int ADD_HOME = 2, ADD_WORK = 3;
	private RelativeLayout rlMarkerPin;
	private LinearLayout llSetLocationOnMap;
	private ScrollView scrollViewSearch;
	private Button submitButton;
	private Double lastLatFetched ;
	private Double lastLngFetched ;
	public PlaceSearchListFragment(){

	}

	public static PlaceSearchListFragment newInstance(){
		PlaceSearchListFragment fragment = new PlaceSearchListFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if(context instanceof SearchListAdapter.SearchListActionsHandler){
			this.searchListActionsHandler = (SearchListAdapter.SearchListActionsHandler) context;
		}
		if(context instanceof  HomeActivity){
			mGoogleApiClient = ((HomeActivity)context).mGoogleApiClient;
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place_search_list, container, false);


        activity = getActivity();
		if(activity!=null)
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);

		setMap();
		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.mavenRegular(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch);
		progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross);
		imageViewSearchCross.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		linearLayoutScrollSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutScrollSearch);
		textViewScrollSearch = (TextView) rootView.findViewById(R.id.textViewScrollSearch);
        rlMarkerPin=rootView.findViewById(R.id.rlMarkerPin);
		imageViewSearchGPSIcon = (ImageView) rootView.findViewById(R.id.imageViewSearchGPSIcon);
		ivLocationMarker=rootView.findViewById(R.id.ivLocationMarker);
		llSetLocationOnMap=rootView.findViewById(R.id.ll_set_location_on_map);
		scrollViewSearch=rootView.findViewById(R.id.scrollViewSearch);
		submitButton=rootView.findViewById(R.id.submitButton);

		editTextSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextSearch.requestFocus();
				Utils.showSoftKeyboard(activity, editTextSearch);
			}
		});
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String address=editTextSearch.getText().toString();
				if(!address.isEmpty()){
				SearchResultNew autoCompleteSearchResult = new SearchResultNew("",address,"", lastLatFetched, lastLngFetched);
				searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
				searchListActionsHandler.onPlaceSearchPre();
				searchListActionsHandler.onPlaceSearchPost(autoCompleteSearchResult);
				}
				else
					Utils.showToast(activity,getString(R.string.please_select_an_address));
			}
		});

		llSetLocationOnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				opensetLocView();
			}
		});

		ivLocationMarker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressBarSearch.setVisibility(View.VISIBLE);
				lastLatFetched=PlaceSearchListFragment.this.googleMap.getCameraPosition().target.latitude;
				lastLngFetched=PlaceSearchListFragment.this.googleMap.getCameraPosition().target.longitude;

				GoogleJungleCaching.INSTANCE.hitGeocode(PlaceSearchListFragment.this.googleMap.getCameraPosition().target, "driver_d",(googleGeocodeResponse, singleAddress)->{
					try {
						String address = null;
						if(googleGeocodeResponse != null){
							GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse);
							address = gapiAddress.getSearchableAddress();
						} else if(singleAddress != null){
							address = singleAddress;
						}
						else
							address=getString(R.string.unnamed);
						progressBarSearch.setVisibility(View.GONE);
						editTextSearch.setText(address);
//						new SearchResultNew("",address,"",lastLatFetched,lastLngFetched);
					}catch (Exception e){e.printStackTrace(); }

				});
			}
		});
		imageViewSearchCross.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextSearch.setText("");
			}
		});


		searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78),
				new SearchListAdapter.SearchListActionsHandler() {

					@Override
					public void onTextChange(String text) {
						try {
							if(text.length() > 0){
								imageViewSearchCross.setVisibility(View.VISIBLE);
//								hideSearchLayout();
							}
							else{
								imageViewSearchCross.setVisibility(View.GONE);
							}
							searchListActionsHandler.onTextChange(text);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onSearchPre();
					}

					@Override
					public void onSearchPost() {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onSearchPost();
					}

					@Override
					public void onPlaceClick(SearchResultNew autoCompleteSearchResult) {
						searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
					}

					@Override
					public void onPlaceSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onPlaceSearchPre();
					}

					@Override
					public void onPlaceSearchPost(SearchResultNew searchResult) {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onPlaceSearchPost(searchResult);
					}

					@Override
					public void onPlaceSearchError() {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onPlaceSearchError();
					}

					@Override
					public void onPlaceSaved() {
					}

				});


		listViewSearch.setAdapter(searchListAdapter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(editTextSearch.getText().length() == 0 || editTextSearch.getText().toString().equalsIgnoreCase(" ")) {
					editTextSearch.setText("");
					editTextSearch.setText(" ");
					editTextSearch.setText("");
				}
			}
		},500);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				editTextSearch.requestFocus();
				editTextSearch.setSelection(editTextSearch.getText().length());
				Utils.showSoftKeyboard(activity, editTextSearch);
			}
		});

		ImageView imageViewShadow = (ImageView) rootView.findViewById(R.id.imageViewShadow);
		if(activity instanceof HomeActivity){
			imageViewShadow.setVisibility(View.VISIBLE);
		} else {
			imageViewShadow.setVisibility(View.GONE);
		}


        return rootView;
	}


	private void hideSearchLayout(){
		linearLayoutAddFav.setVisibility(View.GONE);
		relativeLayoutAddHome.setVisibility(View.GONE);
		relativeLayoutAddWork.setVisibility(View.GONE);
		imageViewSep.setVisibility(View.GONE);
		imageViewSep2.setVisibility(View.GONE);
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
	}

	@Override
	public void onResume() {
		super.onResume();
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


    private void opensetLocView(){
	    setLocationOnMarker=true;
	    rlMarkerPin.setVisibility(View.VISIBLE);
		scrollViewSearch.setVisibility(View.GONE);
		llSetLocationOnMap.setVisibility(View.GONE);
		editTextSearch.setEnabled(false);
        searchListAdapter=null;
        editTextSearch.setHint(R.string.tap_marker);
        editTextSearch.setTag("dontchange");
    }

    private void hideSetLocView(){
		editTextSearch.setTag(null);
        setLocationOnMarker=false;
        rlMarkerPin.setVisibility(View.GONE);
        listViewSearch.setVisibility(View.VISIBLE);
		searchListAdapter = new SearchListAdapter(activity, editTextSearch, new LatLng(30.75, 76.78),
				new SearchListAdapter.SearchListActionsHandler() {

					@Override
					public void onTextChange(String text) {
						try {
							if(text.length() > 0){
								imageViewSearchCross.setVisibility(View.VISIBLE);
//								hideSearchLayout();
							}
							else{
								imageViewSearchCross.setVisibility(View.GONE);
							}
							searchListActionsHandler.onTextChange(text);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onSearchPre();
					}

					@Override
					public void onSearchPost() {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onSearchPost();
					}

					@Override
					public void onPlaceClick(SearchResultNew autoCompleteSearchResult) {
						searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
					}

					@Override
					public void onPlaceSearchPre() {
						progressBarSearch.setVisibility(View.VISIBLE);
						searchListActionsHandler.onPlaceSearchPre();
					}

					@Override
					public void onPlaceSearchPost(SearchResultNew searchResult) {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onPlaceSearchPost(searchResult);
					}

					@Override
					public void onPlaceSearchError() {
						progressBarSearch.setVisibility(View.GONE);
						searchListActionsHandler.onPlaceSearchError();
					}

					@Override
					public void onPlaceSaved() {
					}

				});


		listViewSearch.setAdapter(searchListAdapter);
    }

	private void setMap() {
		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(final GoogleMap googleMap) {
				PlaceSearchListFragment.this.googleMap = googleMap;
				if (googleMap != null) {
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//					enableMapMyLocation(googleMap, true);
					googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//					if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.)
//							== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//						googleMap.setMyLocationEnabled(true);
//					}
					if (0 == Data.latitude && 0 == Data.longitude) {
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.7500, 76.7800), 14));
					} else {
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Data.latitude, Data.longitude), 14));
					}
//					setupMapAndButtonMargins();
//					moveCameraToCurrent();


//
//					TouchableMapFragment mapFragment = ((TouchableMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap));
//					new MapStateListener(googleMap, mapFragment, activity) {
//
//						@Override
//						public void onMapTouched() {
//						}
//
//						@Override
//						public void onMapReleased() {
//						}
//
//						@Override
//						public void onMapUnsettled() {
//							mapSettledCanForward=false;
//							if(!showBouncingMarker()) {
//								setFetchedAddressToTextView("Loading...", true, true, true);
//							}
//							/*mapSettledCanForward = false;
//							searchResultNearPin = null;*/
//						}
//
//						@Override
//						public void moveMap() {
//							startAnimation();
//						}
//
//						@Override
//						public void onMapSettled() {
//							if(getContext() != null) {
//								if (!showBouncingMarker()) {
//									if (bottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//										fillAddressDetails(PlaceSearchListFragment.this.googleMap.getCameraPosition().target, false, false);
//									}
////								autoCompleteResultClicked = false;
//								}
//							}
//						}
//
//						@Override
//						public void onCameraPositionChanged(CameraPosition cameraPosition) {
//
//						}
//					};
//
//					if (PlaceSearchListFragment.this.searchMode == PlaceSearchMode.PICKUP_AND_DROP.getOrdinal()) {
//						fillAddressDetails(googleMap.getCameraPosition().target,true, false);
//
//					}

				}
			}
		});
	}

	public ProgressWheel getProgressBarSearch(){
		return progressBarSearch;
	}

	public enum PlaceSearchMode {
		PICKUP(1),
		DROP(2)
		;

		private int ordinal;
		PlaceSearchMode(int ordinal){
			this.ordinal = ordinal;
		}


		public int getOrdinal() {
			return ordinal;
		}

		public void setOrdinal(int ordinal) {
			this.ordinal = ordinal;
		}
	}

}
