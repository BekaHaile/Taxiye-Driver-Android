package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvError;
    private DriverCreditsListener driverCreditsListener;
    private ArrayList<DriverCreditResponse.CreditHistory> list = new ArrayList<>() ;
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
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        tvError.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCreditsApi();
            }
        });


        getCreditsApi();
        return rootView;



    }


    public void getCreditsApi() {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            HomeUtil.putDefaultParams(params);
            swipeRefreshLayout.setRefreshing(true);
            RestClient.getApiServices().creditHistory(params, new Callback<DriverCreditResponse>() {
                @Override
                public void success(DriverCreditResponse rateCardResponse, Response response) {
                    if(getActivity()==null)return;
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj = new JSONObject(jsonString);
                        String message = JSONParser.getServerMessage(jObj);
                        int flag = jObj.getInt(Constants.KEY_FLAG);

                        if (ApiResponseFlags.TRANSACTION_HISTORY.getOrdinal() == flag) {
                            list.clear();
                            list.addAll(rateCardResponse.getCreditHistoryList());
                            driverCreditsHistoryAdapter.notifyDataSetChanged();
                            tvError.setVisibility(driverCreditsHistoryAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                        } else {
                            DialogPopup.alertPopup(getActivity(), "", message);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(getActivity(), "", getString(R.string.server_error));
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
                @Override
                public void failure(RetrofitError error) {
                    if(getActivity()==null)return;

                    Log.i("error", String.valueOf(error));
                    DialogPopup.alertPopup(getActivity(), "", getString(R.string.server_not_responding));
                    swipeRefreshLayout.setRefreshing(false);
                    tvError.setVisibility(driverCreditsHistoryAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(getActivity(), "", getString(R.string.check_internet_message));
        }
    }
}
