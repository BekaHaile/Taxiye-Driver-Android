package product.clicklabs.jugnoo.driver.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.databinding.LayoutItemRechargeTypeBinding;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;

/**
 * Created by gurmail on 29/08/17.
 */

public class WalletTypeAdapter extends RecyclerView.Adapter<WalletTypeAdapter.ItemTypeHolder> {

    private OnItemClickListener onItemClickListener;
    private ArrayList<DriverEarningsResponse.RechargeOption> rechargeOptions = new ArrayList<>();

    public WalletTypeAdapter(ArrayList<DriverEarningsResponse.RechargeOption> rechargeOptions, OnItemClickListener onItemClickListener) {
        this.rechargeOptions = rechargeOptions;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public WalletTypeAdapter.ItemTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WalletTypeAdapter.ItemTypeHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_recharge_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemTypeHolder holder, int position) {
        holder.rechargeTypeBinding.tvRechargeType.setText(rechargeOptions.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return rechargeOptions.size();
    }

    public class ItemTypeHolder extends RecyclerView.ViewHolder {
        LayoutItemRechargeTypeBinding rechargeTypeBinding;
        public ItemTypeHolder(View itemView) {
            super(itemView);
            rechargeTypeBinding = DataBindingUtil.bind(itemView);
            rechargeTypeBinding.extrasView.setVisibility(View.GONE);
            rechargeTypeBinding.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onRechargeBtnClick();
                }
            });
            rechargeTypeBinding.tvRechargeType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("recharge_option".equalsIgnoreCase(rechargeOptions.get(getAdapterPosition()).getValue())) {
                        rechargeTypeBinding.extrasView.setVisibility(View.VISIBLE);
                    } else {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
            rechargeTypeBinding.amountEdtxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if(!TextUtils.isEmpty(s) && Integer.parseInt(s.toString()) > 0) {
                            rechargeTypeBinding.btnConfirm.setEnabled(true);
                        } else {
                            rechargeTypeBinding.btnConfirm.setEnabled(false);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        rechargeTypeBinding.btnConfirm.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onRechargeBtnClick();
        void onItemClick(int position);
    }

}
