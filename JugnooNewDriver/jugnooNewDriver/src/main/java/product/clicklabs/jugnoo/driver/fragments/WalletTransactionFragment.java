package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.WalletTransAadapter;
import product.clicklabs.jugnoo.driver.databinding.FragmentWalletTransactionBinding;
import product.clicklabs.jugnoo.driver.datastructure.WalletTransactionResponse;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
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
    private Activity walletActivity;
    private View rootView;
    private FragmentWalletTransactionBinding transactionFragment;
    private WalletTransAadapter transAadapter;
    private ArrayList<WalletTransactionResponse.Transactions> arrayList = new ArrayList<>();
    private boolean fromNewWallet;
    private static final String FROM_NEW_WALLET = "from_new_wallet";

    public static WalletTransactionFragment newInstance(boolean fromNewWallet){
        WalletTransactionFragment fragment = new WalletTransactionFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(FROM_NEW_WALLET, fromNewWallet);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        transactionFragment = DataBindingUtil.inflate(
                inflater, R.layout.fragment_wallet_transaction, container, false);
        rootView = transactionFragment.getRoot();
        if(getArguments() != null) {
            fromNewWallet = getArguments().getBoolean(FROM_NEW_WALLET, false);
        }
        walletActivity = getActivity();
        initView(transactionFragment);

        return rootView;
    }

    private void initView(FragmentWalletTransactionBinding transactionFragment) {
        ((TextView)rootView.findViewById(R.id.title)).setText(getString(R.string.wallet_transactions));
        rootView.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
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

        getTrnsList();
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
                HashMap<String, String> map = new HashMap<>();
                map.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
                map.put("start_from", ""+arrayList.size());
                map.put("device_type", ""+Data.DEVICE_TYPE);
                map.put("is_access_token_new", "1");
                map.put("app_version", ""+Data.appVersion);
                map.put("client_id", ""+Data.CLIENT_ID);
                map.put("login_type", ""+Data.LOGIN_TYPE);
                HomeUtil.putDefaultParams(map);

                Callback<WalletTransactionResponse> callback = new Callback<WalletTransactionResponse>() {
                    @Override
                    public void success(WalletTransactionResponse transactionResponse, Response response) {
                        try {
                            String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jObj;
                            jObj = new JSONObject(jsonString);
                            if (!jObj.isNull("error")) {
                                String errorMessage = jObj.getString("error");
                                if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                    HomeActivity.logoutUser(walletActivity, null);
                                }
                            } else {
                                arrayList.addAll(transactionResponse.getTransactions());
                                transAadapter.updateList(transactionResponse.getNumTxns(), arrayList);
                                if(arrayList.size() == 0) {
                                    transactionFragment.textViewNoItems.setVisibility(View.VISIBLE);
                                } else {
                                    transactionFragment.textViewNoItems.setVisibility(View.GONE);
                                }
                            }
                            DialogPopup.dismissLoadingDialog();
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
                };

                DialogPopup.showLoadingDialog(walletActivity, walletActivity.getResources().getString(R.string.loading));
                if(fromNewWallet){
                    RestClient.getApiServices().creditHistoryWallet(map, callback);
                } else {
                    RestClient.getApiServices().getUserTransaction(map, callback);
                }
            } else {
                DialogPopup.alertPopup(walletActivity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.dismissLoadingDialog();
        }
    }
}
