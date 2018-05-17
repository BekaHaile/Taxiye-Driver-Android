package com.fugu.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fugu.FuguFontConfig;
import com.fugu.R;
import com.fugu.database.CommonData;
import com.fugu.model.DescriptionObject;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class CustomActionDescriptionAdapter extends RecyclerView.Adapter<CustomActionDescriptionAdapter.DescriptionViewHolder> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<DescriptionObject> mDescriptionList = new ArrayList<>();
    private Context mContext;

    /**
     * Constructor
     *
     * @param context     calling context
     * @param description the description list to show
     */
    public CustomActionDescriptionAdapter(Context context, ArrayList<DescriptionObject> description) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDescriptionList = description;
    }

    @Override
    public DescriptionViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View main = mLayoutInflater.inflate(R.layout.list_item_description, parent, false);
        return new DescriptionViewHolder(main);
    }

    @Override
    public void onBindViewHolder(final DescriptionViewHolder holder, final int position) {
        int pos = holder.getAdapterPosition();
        DescriptionObject descriptionObject = mDescriptionList.get(pos);
        holder.tvHeader.setText(descriptionObject.getHeader());
        holder.tvContent.setText(descriptionObject.getContent());
    }

    @Override
    public int getItemCount() {
        return mDescriptionList.size();
    }

    /**
     * Description view holder
     */
    class DescriptionViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader, tvContent;

        public DescriptionViewHolder(final View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvContent = itemView.findViewById(R.id.tvContent);
            FuguFontConfig fuguFontConfig = CommonData.getFontConfig();
            tvHeader.setTypeface(fuguFontConfig.getNormalTextTypeFace(mContext.getApplicationContext()));
            tvContent.setTypeface(fuguFontConfig.getNormalTextTypeFace(mContext.getApplicationContext())
                    , Typeface.BOLD);
        }

    }
}
