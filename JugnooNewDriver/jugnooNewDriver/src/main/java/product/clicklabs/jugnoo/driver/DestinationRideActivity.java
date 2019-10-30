package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.fragments.PlaceSearchListFragment;

public class DestinationRideActivity extends AppCompatActivity implements SearchListAdapter.SearchListActionsHandler {

    TextView title, tvSetDestRide;
    RecyclerView rvSavedDest;
    ImageView ivAddDestRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
    }

    private void initViews() {
        title = findViewById(R.id.title);
        rvSavedDest = findViewById(R.id.rvSavedDest);
        rvSavedDest.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvSavedDest.setAdapter(new SavedAddressAdapter(this,Data.userData.getSavedAddressList()));
        ivAddDestRide = findViewById(R.id.ivAddDestRide);
        tvSetDestRide = findViewById(R.id.tvSetDestRide);
    }

    private void setListeners() {
        ivAddDestRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.relativeLayoutContainer, PlaceSearchListFragment.newInstance(), PlaceSearchListFragment.class.getName())
                        .addToBackStack(PlaceSearchListFragment.class.getName())
                        .commit();
            }
        });
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
        Data.userData.getSavedAddressList().add(searchResult);
        rvSavedDest.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onPlaceSearchError() {

    }

    @Override
    public void onPlaceSaved() {

    }

    public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.Viewholder> {
        Activity activity;
        ArrayList<SearchResultNew> savedAddlist;

        SavedAddressAdapter(Activity activity, ArrayList<SearchResultNew> savedAddList) {
            this.activity = activity;
            this.savedAddlist = savedAddList;
        }

        @NonNull
        @Override
        public SavedAddressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
            return new Viewholder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SavedAddressAdapter.Viewholder holder, int position) {
        holder.textViewSearchName.setText(savedAddlist.get(position).getName());
        holder.textViewSearchAddress.setText(savedAddlist.get(position).getAddress());
        }

        @Override
        public int getItemCount() {
            return savedAddlist == null ? 0 : savedAddlist.size();
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            View root;
            ImageView imageViewType, ivDeleteAddress;
            TextView textViewSearchName, textViewSearchAddress;

            public Viewholder(View itemView) {
                super(itemView);
                root = itemView;
                imageViewType = root.findViewById(R.id.imageViewType);
                textViewSearchName = root.findViewById(R.id.textViewSearchName);
                textViewSearchAddress = root.findViewById(R.id.textViewSearchAddress);
                ivDeleteAddress = root.findViewById(R.id.ivDeleteAddress);

            }
        }
    }
}

