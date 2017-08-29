package product.clicklabs.jugnoo.driver.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverEarningsNew;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.WalletActivity;
import product.clicklabs.jugnoo.driver.adapters.WalletTransAadapter;
import product.clicklabs.jugnoo.driver.databinding.FragmentWalletBinding;
import product.clicklabs.jugnoo.driver.databinding.FragmentWalletTransactionBinding;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 26/08/17.
 */

public class WalletTransactionFragment extends DriverBaseFragment implements WalletTransAadapter.ShowMore {

    private static final String TAG = WalletTransactionFragment.class.getSimpleName();
    private WalletActivity walletActivity;
    private View rootView;
    private FragmentWalletTransactionBinding transactionFragment;
    private WalletTransAadapter transAadapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        transactionFragment = DataBindingUtil.inflate(
                inflater, R.layout.fragment_wallet_transaction, container, false);
        rootView = transactionFragment.getRoot();
        walletActivity = (WalletActivity) getActivity();
        initView(transactionFragment);

        return rootView;
    }

    private void initView(FragmentWalletTransactionBinding transactionFragment) {
        transactionFragment.include.textViewTitle.setText(getString(R.string.wallet_transactions).toUpperCase());
        transactionFragment.include.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletActivity.onBackPressed();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        transactionFragment.recyclerView.setLayoutManager(layoutManager);
        transAadapter = new WalletTransAadapter(walletActivity, this);
        transactionFragment.recyclerView.setAdapter(transAadapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected Object getTaskTag() {
        return null;
    }

    @Override
    public void onClickMore() {
        getTrnsList();
    }

    private void getTrnsList() {
        try {
            if (AppStatus.getInstance(walletActivity).isOnline(walletActivity)) {
                String invoiceId = "0";
                DialogPopup.showLoadingDialog(walletActivity, walletActivity.getResources().getString(R.string.loading));
                RestClient.getApiServices().earningNewDetails(Data.userData.accessToken, Data.LOGIN_TYPE, invoiceId, new Callback<DriverEarningsResponse>() {
                    @Override
                    public void success(DriverEarningsResponse driverEarningsResponse, Response response) {
                        try {
                            String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jObj;
                            jObj = new JSONObject(jsonString);
                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                    HomeActivity.logoutUser(walletActivity);
                                }
                            } else {
                                DialogPopup.dismissLoadingDialog();
                                // TODO: 28/08/17  After sucessfull handle data here.
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("error", String.valueOf(error));
                        DialogPopup.dismissLoadingDialog();
                    }
                });
            } else {
                DialogPopup.alertPopup(walletActivity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
        }
    }
}
