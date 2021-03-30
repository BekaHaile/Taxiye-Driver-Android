package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
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

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.google.GoogleJungleCaching;
import product.clicklabs.jugnoo.driver.google.PlaceDetailCallback;
import product.clicklabs.jugnoo.driver.google.PlacesCallback;
import product.clicklabs.jugnoo.driver.retrofit.model.PlaceDetailsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.Prediction;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
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
    String uuidVal = "";

	long delay = 1000; // 1 seconds after user stops typing
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
                             SearchListActionsHandler searchListActionsHandler)
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
                            handler.removeCallbacks(input_finish_checker);
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
            uuidVal = UUID.randomUUID().toString();
        }
        else{
            throw new IllegalStateException("context passed is not of Activity type");
        }
    }

    public void setResults(ArrayList<SearchResultNew> autoCompleteSearchResults) {
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
							if (autoCompleteSearchResult.getPlaceId() != null
									&& !"".equalsIgnoreCase(autoCompleteSearchResult.getPlaceId())
									&& MapUtils.distance(autoCompleteSearchResult.getLatLng(), new LatLng(0,0)) <= 10) {
								searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
								getSearchResultFromPlaceId(autoCompleteSearchResult.getName(),autoCompleteSearchResult.getAddress(), autoCompleteSearchResult.getPlaceId());
							} else{
								searchListActionsHandler.onPlaceClick(autoCompleteSearchResult);
								searchListActionsHandler.onPlaceSearchPre();
								searchListActionsHandler.onPlaceSearchPost(autoCompleteSearchResult);
							}

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
    public void notifyDataSetChanged() {
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

    private void getSearchResults(final String searchText, final LatLng latLng) {
        try {
			if (!refreshingAutoComplete) {
				refreshingAutoComplete = true;
				searchListActionsHandler.onSearchPre();

				String specifiedCountry = Prefs.with(context).getString(Constants.KEY_SPECIFIED_COUNTRY_PLACES_SEARCH, "");
                String components = !TextUtils.isEmpty(specifiedCountry)? "country:"+specifiedCountry:"";
                String radius = searchText.length() <= 3 ? "50" : (searchText.length() <= 5 ? "100": (searchText.length() <= 8 ? "1000" : "10000"));
                String location = latLng.latitude+","+latLng.longitude;

                GoogleJungleCaching.INSTANCE.getAutoCompletePredictions(searchText, uuidVal, components, location, radius, new PlacesCallback() {
                    @Override
                    public void onAutocompletePredictionsReceived(@NotNull List<Prediction> predictions) {
                        try {
                            searchResultsForSearch.clear();
							if(predictions != null) {
								for (Prediction autocompletePrediction : predictions) {
									String name = autocompletePrediction.getDescription().split(",")[0];
									searchResultsForSearch.add(new SearchResultNew(name,
											autocompletePrediction.getDescription(),
											autocompletePrediction.getPlaceId(),
											autocompletePrediction.getLat() != null ? autocompletePrediction.getLat() : 0,
											autocompletePrediction.getLng() != null ? autocompletePrediction.getLng() : 0));
								}
							}

                            setSearchResultsToList();
                            refreshingAutoComplete = false;

                            if (!editTextForSearch.getText().toString().trim().equalsIgnoreCase(searchText)) {
								handler.removeCallbacks(input_finish_checker);
								handler.postDelayed(input_finish_checker.setTextToSearch(editTextForSearch.getText().toString().trim()), delay);
                            }
//                            GoogleRestApis.INSTANCE.logGoogleRestAPIC("0", "0", GoogleRestApis.API_NAME_AUTOCOMPLETE);


                        } catch (Exception e) {
                            e.printStackTrace();
							refreshingAutoComplete = false;
                        }
                    }

                    @Override
                    public void onAutocompleteError() {
                        refreshingAutoComplete = false;
                    }
                });

			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	private void setSearchResultsToList() {
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





    private void getSearchResultFromPlaceId(final String placeName, final String placeAddress, final String placeId) {
        searchListActionsHandler.onPlaceSearchPre();
        Log.e("SearchListAdapter", "getPlaceById placeId=" + placeId);

        GoogleJungleCaching.INSTANCE.getPlaceById(placeId, placeAddress, defaultSearchPivotLatLng, uuidVal, new PlaceDetailCallback() {
            @Override
            public void onPlaceDetailReceived(@NotNull PlaceDetailsResponse placeDetailsResponse) {
                try {
                    SearchResultNew searchResult = new SearchResultNew(placeName, placeAddress, placeId,
                            placeDetailsResponse.getResults().get(0).getGeometry().getLocation().getLat(),
                            placeDetailsResponse.getResults().get(0).getGeometry().getLocation().getLng());
                    setSearchResult(searchResult);
                } catch (Exception e) {
                    e.printStackTrace();
					searchListActionsHandler.onPlaceSearchError();
                }
            }

            @Override
            public void onPlaceDetailError() {
				searchListActionsHandler.onPlaceSearchError();
            }
        });

        Log.v("after call back", "after call back");
    }

    private void setSearchResult(final SearchResultNew searchResult) {
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
