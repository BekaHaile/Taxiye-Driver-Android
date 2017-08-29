package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.databinding.LayoutItemShowMoreBinding;
import product.clicklabs.jugnoo.driver.databinding.LayoutTransactionItemBinding;

/**
 * Created by gurmail on 26/08/17.
 */

public class WalletTransAadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = WalletTransAadapter.class.getSimpleName();
    private final static int TYPE_ITEM_VIEW = 0;
    private final static int TYPE_FOOTER = 1;
    private ShowMore showMore;
    private Activity activity;

    public WalletTransAadapter(Activity activity, ShowMore showMore) {
        this.activity = activity;
        this.showMore = showMore;
    }

    public void updateList() {
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new WalletTransAadapter.footerView(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_show_more,
                    parent, false));
        } else {
            return new WalletTransAadapter.viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transaction_item,
                    parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof viewHolder) {
            ((viewHolder) holder).transactionItemBinding.textViewTransactionAmount.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        if(isViewTypeFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM_VIEW;
        }
    }

    private boolean isViewTypeFooter(int position) {
        if(position == 9)
            return true;
        else
            return false;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private LayoutTransactionItemBinding transactionItemBinding;
        public viewHolder(View itemView) {
            super(itemView);
            transactionItemBinding = DataBindingUtil.bind(itemView);
        }
    }

    public class footerView extends RecyclerView.ViewHolder {
        private LayoutItemShowMoreBinding itemShowMoreBinding;
        public footerView(View itemView) {
            super(itemView);
            itemShowMoreBinding = DataBindingUtil.bind(itemView);
            itemShowMoreBinding.relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMore.onClickMore();
                }
            });
        }
    }

    public interface ShowMore {
        public void onClickMore();
    }

}
