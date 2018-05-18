package com.fugu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fugu.R;
import com.fugu.activity.FuguChatActivity;
import com.fugu.database.CommonData;
import com.fugu.model.ActionButtonModel;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class CustomActionButtonsAdapter extends RecyclerView.Adapter<CustomActionButtonsAdapter.ActionButtonViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<ActionButtonModel> mActionButtons;

    /**
     * Constructor
     *
     * @param context       calling context
     * @param actionButtons the action buttons
     */
    public CustomActionButtonsAdapter(Context context, ArrayList<ActionButtonModel> actionButtons) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mActionButtons = actionButtons;
    }

    @Override
    public ActionButtonViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View main = mLayoutInflater.inflate(R.layout.list_item_action_button, parent, false);
        return new ActionButtonViewHolder(main);
    }

    @Override
    public void onBindViewHolder(final ActionButtonViewHolder holder, final int position) {
        int pos = holder.getAdapterPosition();
        ActionButtonModel actionButton = mActionButtons.get(pos);
        holder.btnAction.setText(actionButton.getButtonText());
    }

    @Override
    public int getItemCount() {
        return mActionButtons.size();
    }

    /**
     * Action Button ViewHolder
     */
    class ActionButtonViewHolder extends RecyclerView.ViewHolder {

        Button btnAction;

        ActionButtonViewHolder(final View itemView) {
            super(itemView);
            btnAction = itemView.findViewById(R.id.btnAction);
            btnAction.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(mContext.getApplicationContext()));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    ((FuguChatActivity) mContext).onCustomActionClicked(mActionButtons.get(getAdapterPosition())
                            .getButtonAction());
                }
            });
        }
    }
}
