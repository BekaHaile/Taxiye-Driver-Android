package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.GoogleRestApis;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;


/**
 * Base adapter for google autocomplete search and pressing on a search item fetches LatLng for that place.
 *
 * Created by socomo20 on 7/4/15.
 */
public class SearchListAdapter extends BaseAdapter {

    class ViewHolderSearchItem {
        TextView textViewSearchName, textViewSearchAddress;
        ImageView imageViewType, imageViewSep;
        RelativeLayout relative;
        int id;
    }

    Context context;
    LayoutInflater mInflater;
    ViewHolderSearchItem holder;
    EditText editTextForSearch;
    SearchListActionsHandler searchListActionsHandler;
    LatLng defaultSearchPivotLatLng;

    ArrayList<SearchResultNew> searchResultsForSearch;
    ArrayList<SearchResultNew> searchResults;

	private PlacesClient mGoogleApiClient;

	long delay = 700; // 1 seconds after user stops typing
	long last_text_edit = 0;
	private Handler handler;
	private class CustomRunnable implements Runnable {
		private String textToSearch;
		public CustomRunnable(String textToSearch){
			this.textToSearch = textToSearch;
		}
		public CustomRunnable setTextToSearch(String textToSearch){
			this.textToSearch = textToSearch;
			return this;
		}

		@Override
		public void run() {
			if (System.currentTimeMillis() > (last_text_edit + delay - 200)) {
				getSearchResults(textToSearch, defaultSearchPivotLatLng);
			}
		}
	}
	private CustomRunnable input_finish_checker = new CustomRunnable("");

    /**
     * Constructor for initializing search base adapter
     *
     * @param context
     * @param editTextForSearch edittext object whose text change will trigger autocomplete list
     * @param searchPivotLatLng LatLng for searching autocomplete results
     * @param searchListActionsHandler handler for custom actions
     * @throws IllegalStateException
     */
    public SearchListAdapter(final Context context, EditText editTextForSearch, LatLng searchPivotLatLng,
                             PlacesClient mGoogleApiClient, SearchListActionsHandler searchListActionsHandler)
            throws IllegalStateException {
        if(context instanceof Activity) {
			handler = new Handler();
            this.context = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.searchResultsForSearch = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.editTextForSearch = editTextForSearch;
            this.defaultSearchPivotLatLng = searchPivotLatLng;
            this.searchListActionsHandler = searchListActionsHandler;
			this.mGoogleApiClient = mGoogleApiClient;
            this.editTextForSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
					handler.removeCallbacks(input_finish_checker);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
					try {
						SearchListAdapter.this.searchListActionsHandler.onTextChange(s.toString());
						if (s.length() > 0) {
							last_text_edit = System.currentTimeMillis();
							handler.postDelayed(input_finish_checker.setTextToSearch(s.toString().trim()), delay);
						}
						else{
							searchResultsForSearch.clear();
							setResults(searchResultsForSearch);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            });

            this.editTextForSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    Utils.hideSoftKeyboard((Activity) context, SearchListAdapter.this.editTextForSearch);
                    return true;
                }
            });

        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    public synchronized void setResults(ArrayList<SearchResultNew> autoCompleteSearchResults) {
        this.searchResults.clear();
        this.searchResults.addAll(autoCompleteSearchResults);
        this.notifyDataSetChanged();
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
            holder = new ViewHolderSearchItem();
            convertView = mInflater.inflate(R.layout.list_item_search_item, null);

            holder.textViewSearchName = (TextView) convertView.findViewById(R.id.textViewSearchName);
            holder.textViewSearchName.setTypeface(Fonts.mavenRegular(context));
            holder.textViewSearchAddress = (TextView) convertView.findViewById(R.id.textViewSearchAddress);
            holder.textViewSearchAddress.setTypeface(Fonts.mavenRegular(context));
            holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
            holder.imageViewType = (ImageView)convertView.findViewById(R.id.imageViewType);
            holder.imageViewSep = (ImageView) convertView.findViewById(R.id.imageViewSep);

            holder.relative.setTag(holder);

            holder.relative.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110));
            ASSL.DoMagic(holder.relative);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSearchItem) convertView.getTag();
        }


        try {
            holder.id = position;

            holder.textViewSearchName.setText(searchResults.get(position).getName());
            holder.textViewSearchAddress.setText(searchResults.get(position).getAddress());

            if(searchResults.get(position).getType() == SearchResultNew.Type.HOME){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.ic_home);
            } else if(searchResults.get(position).getType() == SearchResultNew.Type.WORK){
                holder.imageViewType.setVisibility(View.VISIBLE);
                holder.imageViewType.setImageResource(R.drawable.ic_work);
            } else{
				holder.imageViewType.setVisibility(View.VISIBLE);
				if(searchResults.get(position).getType() == SearchResultNew.Type.LAST_SAVED) {
					holder.imageViewType.setImageResource(R.drawable.ic_recent_loc);
				} else{
					holder.imageViewType.setImageResource(R.drawable.ic_loc_other);
				}
            }

            if(searchResults.get(position).getAddress().equalsIgnoreCase("")){
                holder.textViewSearchAddress.setVisibility(View.GONE);
            }else {
                holder.textViewSearchAddress.setVisibility(View.VISIBLE);
            }

            if(position == getCount()-1){
                holder.imageViewSep.setVisibility(View.GONE);
            } else{
                holder.imageViewSep.setVisibility(View.VISIBLE);
            }

            holder.relative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
					try {
						holder = (ViewHolderSearchItem) v.getTag();
                        final SearchResultNew autoCompleteSearchResult = searchResults.get(holder.id);
                        if(!context.getResources().getString(R.string.no_results_found).equalsIgnoreCase(autoCompleteSearchResult.getName())
                                && !context.getResources().getString(R.string.check_internet_message).equalsIgnoreCase(autoCompleteSearchResult.getName())){
                            Utils.hideSoftKeyboard((Activity) context, editTextForSearch);
                            Log.e("SearchListAdapter", "on click="+autoCompleteSearchResult.getAddress());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (autoCompleteSearchResult.getPlaceId() != null
                                            && !"".equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())) {
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        getSearchResultFromPlaceId(autoCompleteSearchResult.getName(),autoCompleteSearchResult.getAddress(), autoCompleteSearchResult.getPlaceId());
                                    } else{
                                        searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
                                        searchListActionsHandler.onPlaceSearchPre();
                                        searchListActionsHandler.onPlaceSearchPost(autoCompleteSearchResult);
                                    }
                                }
                            }, 200);

                        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public synchronized void notifyDataSetChanged() {
        if (searchResults.size() > 1) {
            if (searchResults.contains(new SearchResultNew(context.getResources()
                    .getString(R.string.no_results_found), "", "", 0, 0))) {
                searchResults.remove(searchResults.indexOf(new SearchResultNew(context.getResources()
                        .getString(R.string.no_results_found), "", "", 0, 0)));
            }
        }

        super.notifyDataSetChanged();
    }

    private boolean refreshingAutoComplete = false;

    private synchronized void getSearchResults(final String searchText, final LatLng latLng) {
        try {
			if (!refreshingAutoComplete) {
				searchListActionsHandler.onSearchPre();

                // Create a RectangularBounds object.
                RectangularBounds bounds = RectangularBounds.newInstance(
                        latLng,
                        latLng);
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias( bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry(this.context.getResources().getBoolean(R.bool.specified_country_search_result_enabled)? "TT" : null)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(searchText)
                        .build();

                mGoogleApiClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    try {
                        refreshingAutoComplete = true;
                        searchResultsForSearch.clear();
                        for (AutocompletePrediction autocompletePrediction : response.getAutocompletePredictions()) {
                            String name = autocompletePrediction.getFullText(null).toString().split(",")[0];
                            searchResultsForSearch.add(new SearchResultNew(name,
                                    autocompletePrediction.getFullText(null).toString(),
                                    autocompletePrediction.getPlaceId(), 0, 0));
                        }

                        setSearchResultsToList();
                        refreshingAutoComplete = false;

                        if (!editTextForSearch.getText().toString().trim().equalsIgnoreCase(searchText)) {
                            recallSearch(editTextForSearch.getText().toString().trim());
                        }
                        GoogleRestApis.INSTANCE.logGoogleRestAPIC("0", "0", GoogleRestApis.API_NAME_AUTOCOMPLETE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        Log.e("Error", "Place not found: " + apiException.getStatusCode());
                    }
                });
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private synchronized void recallSearch(final String searchText){
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getSearchResults(searchText, defaultSearchPivotLatLng);
			}
		});
	}

	private synchronized void setSearchResultsToList() {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if ((searchResultsForSearch.size()) == 0 && (editTextForSearch.getText().toString().trim().length() > 0)) {
                    if(AppStatus.getInstance(context).isOnline(context)) {
                        searchResultsForSearch.add(new SearchResultNew(context.getResources()
                                .getString(R.string.no_results_found), "", "", 0, 0));
                    } else{
                        searchResultsForSearch.add(new SearchResultNew(context.getResources()
                                .getString(R.string.no_internet_tap_to_retry), "", "", 0, 0));
                    }
                }
                SearchListAdapter.this.setResults(searchResultsForSearch);
                searchListActionsHandler.onSearchPost();
            }
        });
    }





    private synchronized void getSearchResultFromPlaceId(final String placeName, final String placeAddress, final String placeId) {
        searchListActionsHandler.onPlaceSearchPre();
        Log.e("SearchListAdapter", "getPlaceById placeId=" + placeId);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
        mGoogleApiClient.fetchPlace(request)
                .addOnSuccessListener(fetchPlaceResponse -> {
                    try {
                        final Place myPlace = fetchPlaceResponse.getPlace();
                        final List<String> thirdPartyAttributions = fetchPlaceResponse.getPlace().getAttributions();
                        SearchResultNew searchResult = new SearchResultNew(placeName, placeAddress, placeId,
                                Objects.requireNonNull(myPlace.getLatLng()).latitude, myPlace.getLatLng().longitude);
                        if (thirdPartyAttributions != null && !thirdPartyAttributions.isEmpty()) {
                            searchResult.setThirdPartyAttributions(thirdPartyAttributions.get(0));
                        }
                        setSearchResult(searchResult);
                        if(myPlace.getLatLng() != null) {
                            GoogleRestApis.INSTANCE.logGoogleRestAPIC(String.valueOf(myPlace.getLatLng().latitude), String.valueOf(myPlace.getLatLng().longitude), GoogleRestApis.API_NAME_PLACES);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> {

                });
        Log.v("after call back", "after call back");
    }

    private synchronized void setSearchResult(final SearchResultNew searchResult) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(searchResult != null) {
                    searchListActionsHandler.onPlaceSearchPost(searchResult);
                }
                else{
                    DialogPopup.alertPopup((Activity) context, "", Data.CHECK_INTERNET_MSG);
                    searchListActionsHandler.onPlaceSearchError();
                }
            }
        });
    }



	public interface SearchListActionsHandler {
		void onTextChange(String text);
		void onSearchPre();
		void onSearchPost();
		void onPlaceClick(SearchResultNew autoCompleteSearchResult);
		void onPlaceSearchPre();
		void onPlaceSearchPost(SearchResultNew searchResult);
		void onPlaceSearchError();
        void onPlaceSaved();
	}



}
