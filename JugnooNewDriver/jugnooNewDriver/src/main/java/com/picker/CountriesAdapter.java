package com.picker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public  class CountriesAdapter<T extends SearchDataModel> extends RecyclerView.Adapter<CountriesAdapter<T>.ViewHolder> {

    // region Variables
    private OnItemClickListener<T> listener;
    private List<T> countries;
    private Context context;
    private boolean showCheckBox;
    // endregion

    //region Constructor
      CountriesAdapter (Context context, boolean showCheckBox, List<T> countries, OnItemClickListener<T> listener) {
        this.context = context;
        this.countries = countries;
        this.showCheckBox = showCheckBox;
        this.listener = listener;

    }
    // endregion

    // region Adapter Methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final T country = countries.get(position);
        holder.countryNameText.setText(country.getLabel());
        holder.checkbox.setText(country.getLabel());
        int image = country.getImage(context);
        holder.checkbox.setVisibility(showCheckBox ? View.VISIBLE : View.GONE);
        holder.countryNameText.setVisibility(showCheckBox ? View.GONE : View.VISIBLE);
        holder.checkbox.setChecked(country.isSelected());

        if(country.showImage()){
            holder.countryFlagImageView.setVisibility(View.VISIBLE);
            if (image != -1) {
                holder.countryFlagImageView.setImageResource(image);
            }else{
                holder.countryFlagImageView.setImageResource(0);
            }
        }else{
            holder.countryFlagImageView.setVisibility(View.GONE);
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(country);
            }
        });
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onItemClicked(country);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countries==null ?0:countries.size();
    }
    // endregion

    // region ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView countryFlagImageView;
        private TextView countryNameText;
        private RelativeLayout rootView;
        private CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            countryFlagImageView = (ImageView) itemView.findViewById(R.id.country_flag);
            countryNameText = (TextView) itemView.findViewById(R.id.country_title);
            countryNameText.setTypeface(Fonts.mavenRegular(context));
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkbox.setTypeface(Fonts.mavenRegular(context));
        }
    }
    // endregion
}
