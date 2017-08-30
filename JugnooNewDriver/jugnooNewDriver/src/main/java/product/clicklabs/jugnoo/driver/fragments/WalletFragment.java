package product.clicklabs.jugnoo.driver.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.WalletActivity;
import product.clicklabs.jugnoo.driver.adapters.WalletTypeAdapter;
import product.clicklabs.jugnoo.driver.databinding.FragmentWalletBinding;
import product.clicklabs.jugnoo.driver.datastructure.RechargeType;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by gurmail on 24/08/17.
 */

public class WalletFragment extends DriverBaseFragment implements WalletTypeAdapter.OnItemClickListener {

    private WalletActivity walletActivity;
    private View rootView;
    private double balance = 0;
    private String jugnooAmount = "";

    private WalletTypeAdapter walletTypeAdapter;
    private ArrayList<DriverEarningsResponse.RechargeOption> rechargeOptionList = new ArrayList<>();
    private Type listType = new TypeToken<List<DriverEarningsResponse.RechargeOption>>() {
    }.getType();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            String data = getArguments().getString("data", "[]");
            balance = getArguments().getDouble("amount", 0);
            rechargeOptionList = new Gson().fromJson(data, listType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentWalletBinding fragmentWalletBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_wallet, container, false);
        rootView = fragmentWalletBinding.getRoot();
        walletActivity = (WalletActivity) getActivity();
        jugnooAmount = Utils.getDecimalFormatForMoney().format(balance);
        if(balance < 0) {
            fragmentWalletBinding.walletBalanceView.setText(getString(R.string.rupees_value_format_negtive, jugnooAmount) + getString(R.string.low_balance), TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) fragmentWalletBinding.walletBalanceView.getText();
            int index = getString(R.string.rupees_value_format, jugnooAmount).length();
            spannable.setSpan(new RelativeSizeSpan(1.6f), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fragmentWalletBinding.walletBalanceView.setTextColor(getResources().getColor(R.color.red_status));
        } else if(balance < Data.MINI_BALANCE) {
            fragmentWalletBinding.walletBalanceView.setText(getString(R.string.rupees_value_format, jugnooAmount) + getString(R.string.low_balance), TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) fragmentWalletBinding.walletBalanceView.getText();
            int index = getString(R.string.rupees_value_format, jugnooAmount).length();
            spannable.setSpan(new RelativeSizeSpan(1.6f), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fragmentWalletBinding.walletBalanceView.setTextColor(getResources().getColor(R.color.red_status));
        } else {
            fragmentWalletBinding.walletBalanceView.setText(getString(R.string.rupees_value_format, jugnooAmount));
        }
        fragmentWalletBinding.include.textViewTitle.setText(getString(R.string.wallet_balance).toUpperCase());
        fragmentWalletBinding.include.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletActivity.onBackPressed();
            }
        });

        walletTypeAdapter = new WalletTypeAdapter(rechargeOptionList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(walletActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentWalletBinding.recyclerView.setLayoutManager(layoutManager);
        fragmentWalletBinding.recyclerView.setAdapter(walletTypeAdapter);

        fragmentWalletBinding.relativeLayoutWalletTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(R.id.main_layout, new WalletTransactionFragment(), WalletTransactionFragment.class.getName(), true, true);
            }
        });

        if(rechargeOptionList == null || rechargeOptionList.size() == 0) {
            fragmentWalletBinding.howToRecharge.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected Object getTaskTag() {
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden) {
            updateBalance();
        }
    }

    private void updateBalance() {

    }

    @Override
    public void onRechargeBtnClick() {

    }

    @Override
    public void onItemClick(int position) {
        if(rechargeOptionList.get(position).getValue().equalsIgnoreCase(RechargeType.VISIT_OFFICE.toString())) {
            String address = "";
            int addressSize = rechargeOptionList.get(position).getAddresses().size();
            if(addressSize > 1) {
                for (int i = 0; i < addressSize; i++) {
                    if(i == 0)
                        address = rechargeOptionList.get(position).getAddresses().get(i).getAddress();
                    else
                        address =address +"\n"+ rechargeOptionList.get(position).getAddresses().get(i).getAddress();
                }
            } else if(addressSize == 1) {
                address = rechargeOptionList.get(position).getAddresses().get(0).getAddress();
            } else {
                address = "Visit Jugnoo office in your city";
            }
            DialogPopup.alertPopup(walletActivity, "", address);
        }
    }
}
