package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.net.PlacesClient;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
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

	private final int ADD_HOME = 2, ADD_WORK = 3;

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

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);


		editTextSearch = (EditText) rootView.findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Fonts.mavenRegular(activity));
		progressBarSearch = (ProgressWheel) rootView.findViewById(R.id.progressBarSearch);
		progressBarSearch.setVisibility(View.GONE);
		imageViewSearchCross = (ImageView) rootView.findViewById(R.id.imageViewSearchCross);
		imageViewSearchCross.setVisibility(View.GONE);
		listViewSearch = (NonScrollListView) rootView.findViewById(R.id.listViewSearch);
		linearLayoutScrollSearch = (LinearLayout) rootView.findViewById(R.id.linearLayoutScrollSearch);
		textViewScrollSearch = (TextView) rootView.findViewById(R.id.textViewScrollSearch);

		imageViewSearchGPSIcon = (ImageView) rootView.findViewById(R.id.imageViewSearchGPSIcon);

		editTextSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextSearch.requestFocus();
				Utils.showSoftKeyboard(activity, editTextSearch);
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
