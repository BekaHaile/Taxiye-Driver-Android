package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DriverCreditsHistoryAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class CreditsHistoryFragment extends Fragment {




    View rootView;

    private DriverCreditsListener driverCreditsListener;
    private ArrayList<DriverCreditResponse.CreditHistory> list ;
    private DriverCreditsHistoryAdapter driverCreditsHistoryAdapter;

    public static CreditsHistoryFragment newInstance() {

        Bundle args = new Bundle();
        Gson gson = new Gson();
        CreditsHistoryFragment fragment = new CreditsHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DriverCreditsListener){
            driverCreditsListener = (DriverCreditsListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_credit_history, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        driverCreditsHistoryAdapter = new DriverCreditsHistoryAdapter(getActivity(),list,recyclerView);
        recyclerView.setAdapter(driverCreditsHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        getCreditsApi();
        return rootView;



    }


    private void getCreditsApi() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            HomeUtil.putDefaultParams(params);
            DialogPopup.showLoadingDialog(getActivity(), getString(R.string.loading));
            RestClient.getApiServices().getCredits(params, new Callback<DriverCreditResponse>() {
                @Override
                public void success(DriverCreditResponse rateCardResponse, Response response) {
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj = new JSONObject(jsonString);
                        String message = JSONParser.getServerMessage(jObj);
                        int flag = jObj.getInt(Constants.KEY_FLAG);

                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                            list = rateCardResponse.getCreditHistoryList();
                            driverCreditsHistoryAdapter.notifyDataSetChanged();
                        } else {
                            DialogPopup.alertPopup(getActivity(), "", message);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(getActivity(), "", getString(R.string.server_error));
                    }
                    DialogPopup.dismissLoadingDialog();
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.i("error", String.valueOf(error));
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(getActivity(), "", getString(R.string.server_not_responding));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(getActivity(), "", getString(R.string.check_internet_message));
        }
    }
}
