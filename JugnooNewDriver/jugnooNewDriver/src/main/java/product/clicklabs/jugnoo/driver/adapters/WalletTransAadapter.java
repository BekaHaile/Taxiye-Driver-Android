package product.clicklabs.jugnoo.driver.adapters;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.databinding.LayoutItemShowMoreBinding;
import product.clicklabs.jugnoo.driver.databinding.LayoutTransactionItemBinding;
import product.clicklabs.jugnoo.driver.datastructure.WalletTransactionResponse;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by gurmail on 26/08/17.
 */

public class WalletTransAadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = WalletTransAadapter.class.getSimpleName();
    private final static int TYPE_ITEM_VIEW = 0;
    private final static int TYPE_FOOTER = 1;
    private int totalTransactions = 0;
    private ShowMore showMore;
    private Activity activity;
    private ArrayList<WalletTransactionResponse.Transactions> transactionses = new ArrayList<>();

    public WalletTransAadapter(Activity activity, ShowMore showMore) {
        this.activity = activity;
        this.showMore = showMore;
    }

    public void updateList(int totalTransactions, ArrayList<WalletTransactionResponse.Transactions> transactionses) {
        this.transactionses = transactionses;
        this.totalTransactions = totalTransactions;
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

            viewHolder viewHolder = (WalletTransAadapter.viewHolder) holder;
            String amount = Utils.formatCurrencyValue(transactionses.get(position).getCurrencyUnit(),
                    transactionses.get(position).getAmount(), activity.getString(R.string.currency_fallback));
            viewHolder.transactionItemBinding.textViewTransactionAmount.setText(amount);
            viewHolder.transactionItemBinding.textViewTransactionDate.setText(transactionses.get(position).getTxnDate());
            viewHolder.transactionItemBinding.textViewTransactionTime.setText(transactionses.get(position).getTxnTime());
            viewHolder.transactionItemBinding.tvPaymentType.setText("#"+transactionses.get(position).getReferenceId()+" "+transactionses.get(position).getTxnText());

//            viewHolder.transactionItemBinding.tvPaymentType.setVisibility(TextUtils.isEmpty(transactionses.get(position).getTxnText().trim()) ? View.GONE : View.VISIBLE);
            if(activity.getResources().getInteger(R.integer.visibility_payment_type_in_wallet_trans) == activity.getResources().getInteger(R.integer.view_gone)) {
                viewHolder.transactionItemBinding.tvPaymentType.setText("#"+transactionses.get(position).getReferenceId());
            }

            viewHolder.transactionItemBinding.textViewTransactionType.setVisibility(View.VISIBLE);
            if(transactionses.get(position).getTxnType() == Data.TxnType.CREDITED) {
                viewHolder.transactionItemBinding.textViewTransactionType.setText(activity.getString(R.string.added));
                viewHolder.transactionItemBinding.textViewTransactionType.setTextColor(activity.getResources().getColor(R.color.case_added));
            } else if(transactionses.get(position).getTxnType() == Data.TxnType.DEBITED) {
                //Deducted
                viewHolder.transactionItemBinding.textViewTransactionType.setText(activity.getString(R.string.deducted));
                viewHolder.transactionItemBinding.textViewTransactionType.setTextColor(activity.getResources().getColor(R.color.case_deducted));
            }  else if(transactionses.get(position).getTxnType() == Data.TxnType.DEBT) {
                //Debt
                viewHolder.transactionItemBinding.textViewTransactionType.setText(activity.getString(R.string.debt));
                viewHolder.transactionItemBinding.textViewTransactionType.setTextColor(activity.getResources().getColor(R.color.case_deducted));
            } else {
                viewHolder.transactionItemBinding.textViewTransactionType.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public int getItemCount() {
        if(transactionses == null || transactionses.size() == 0){
            return 0;
        } else{
            if(totalTransactions > transactionses.size()){
                return transactionses.size() + 1;
            } else{
                return transactionses.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM_VIEW;
        }
    }

    private boolean isPositionFooter(int position) {
        return position == transactionses.size();
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
