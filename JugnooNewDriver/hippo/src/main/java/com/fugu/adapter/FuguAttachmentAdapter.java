package com.fugu.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fugu.R;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguAttachmentModel;

import java.util.ArrayList;

/**
 * Created by bhavya on 12/08/17.
 */

public class FuguAttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant {
    private LayoutInflater inflater;
    private ArrayList<FuguAttachmentModel> attachmentList = new ArrayList<>();
    private Context context;
    private OnAttachListener mOnAttach;

    public FuguAttachmentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        attachmentList.add(new FuguAttachmentModel(R.drawable.ic_camera_icon,
                context.getResources().getString(R.string.fugu_camera), OPEN_CAMERA_ADD_IMAGE));
        attachmentList.add(new FuguAttachmentModel(R.drawable.ic_photo_icon,
                context.getResources().getString(R.string.fugu_gallery), OPEN_GALLERY_ADD_IMAGE));
//        attachmentList.add(new FuguAttachmentModel(R.drawable.ic_document_icon,
//                context.getResources().getString(R.string.fugu_pdf), SELECT_FILE));
    }

    public void setOnAttachListener(OnAttachListener OnAttachListener) {
        mOnAttach = OnAttachListener;
    }

    public interface OnAttachListener {
        public void onAttach(int action);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_dialog_attach, parent, false);
        AttachmentViewHolder holder = new AttachmentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AttachmentViewHolder attachmentViewHolder = (AttachmentViewHolder) holder;
        final FuguAttachmentModel currentAttachmentItem = attachmentList.get(position);

        attachmentViewHolder.tvAttachmentType.setText(currentAttachmentItem.getText());
        attachmentViewHolder.tvAttachmentType.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, currentAttachmentItem.getImageIcon()),
                null, null, null);

        if (position == attachmentList.size() - 1) {
            attachmentViewHolder.viewDivider.setVisibility(View.GONE);
        } else {
            attachmentViewHolder.viewDivider.setVisibility(View.VISIBLE);
        }

        attachmentViewHolder.tvAttachmentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAttach != null) {
                    mOnAttach.onAttach(currentAttachmentItem.getAction());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentList.size();
    }

    class AttachmentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttachmentType;
        private View viewDivider;

        public AttachmentViewHolder(View itemView) {
            super(itemView);
            tvAttachmentType = (TextView) itemView.findViewById(R.id.tvAttachmentType);
            tvAttachmentType.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(context.getApplicationContext()));
            viewDivider = itemView.findViewById(R.id.viewDivider);
        }
    }
}
