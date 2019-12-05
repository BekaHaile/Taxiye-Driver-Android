package product.clicklabs.jugnoo.driver.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import product.clicklabs.jugnoo.driver.R;

public class ImageWithTextAdapter extends RecyclerView.Adapter<ImageWithTextAdapter.ImageWithTextVH> {

    private List<String> imageList;
    private OnItemClickListener mOnItemClickListener;


    public ImageWithTextAdapter(final List<String> imageList, final OnItemClickListener onItemClickListener) {
        this.imageList = imageList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ImageWithTextVH onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_with_text, parent,false);
        return new ImageWithTextVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageWithTextVH holder, final int p) {

        int pos = holder.getAdapterPosition();

        if (pos >= 0 && pos < imageList.size()) {

            String image = imageList.get(pos);

            Picasso.with(holder.ivImage.getContext()).load(image)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    class ImageWithTextVH extends RecyclerView.ViewHolder {

        AppCompatTextView tvTitle;
        AppCompatImageView ivImage;

        ImageWithTextVH(final View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (imageList != null && imageList.size() > 0) {
                        int pos = getAdapterPosition();
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(imageList.get(pos), pos);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(final String image, final int pos);
    }
}
