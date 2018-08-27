package com.picker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;


public class CountryPickerDialog<T extends SearchDataModel> extends DialogFragment implements OnItemClickListener<T> {

    // region Variables
    private CountryPickerDialogInteractionListener<T> dialogInteractionListener;
    private RecyclerView countriesRecyclerView;
    private CountriesAdapter<T> adapter;
    private List<T> searchResults;
    private OnCountryPickerListener<T> listener;
    private LinearLayout llNoData;
    public  static final String SEARCH_DIALOG_TITLE = "search_dialog_title";

    // endregion


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    // region Constructors
    public static CountryPickerDialog newInstance(String string) {
        CountryPickerDialog countryPickerDialog = new CountryPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_DIALOG_TITLE,string);
        countryPickerDialog.setArguments(bundle);
        return countryPickerDialog;
    }
    // endregion

    // region Lifecycle
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_picker,null);
        String title = null;
        if(getArguments()!=null && getArguments().containsKey(SEARCH_DIALOG_TITLE)){
            title =  getArguments().getString(SEARCH_DIALOG_TITLE,null);
        }else{
            title= getString(R.string.select);

        }
        ((TextView)view.findViewById(R.id.tv_title)).setText(title);
        EditText searchEditText = (EditText) view.findViewById(R.id.country_code_picker_search);
        countriesRecyclerView = (RecyclerView) view.findViewById(R.id.countries_recycler_view);
        llNoData = (LinearLayout) view.findViewById(R.id.llNoData);
        setupRecyclerView();
        if (!dialogInteractionListener.canSearch()) {
            searchEditText.setVisibility(View.GONE);
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable searchQuery) {
                search(searchQuery.toString());
            }
        });
        return view;
    }

    @Override
    public void onItemClicked(T country) {
        if (listener != null) {
            listener.onSelectCountry(country);
            dismiss();
        }
    }
    // endregion

    // region Setter Methods
    public void setCountryPickerListener(OnCountryPickerListener listener) {
        this.listener = listener;
    }

    public void setDialogInteractionListener(
            CountryPickerDialogInteractionListener dialogInteractionListener) {
        this.dialogInteractionListener = dialogInteractionListener;
    }
    // endregion

    // region Utility Methods
    private void search(String searchQuery) {
        searchResults.clear();
        for (T country : dialogInteractionListener.getAllCountries()) {
            if (country.getLabel().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                searchResults.add(country);
            }
        }
        dialogInteractionListener.sortCountries(searchResults);
        if(searchResults.size()>0) {
            llNoData.setVisibility(View.GONE);
            countriesRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            llNoData.setVisibility(View.VISIBLE);
            countriesRecyclerView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        searchResults = new ArrayList<>();
        searchResults.addAll(dialogInteractionListener.getAllCountries());
        adapter = new CountriesAdapter<>(getActivity(), searchResults, this);
        countriesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        countriesRecyclerView.setLayoutManager(layoutManager);
        countriesRecyclerView.setAdapter(adapter);
    }

    // endregion

    //region Interface
    public interface CountryPickerDialogInteractionListener<T extends SearchDataModel> {
        List<T> getAllCountries();

        void sortCountries(List<T> searchResults);

        boolean canSearch();
    }
    // endregion
}
