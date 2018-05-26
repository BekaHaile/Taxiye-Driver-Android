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

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DriverCreditsHistoryAdapter;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class CreditsHistoryFragment extends Fragment {




    View rootView;

    private DriverCreditsListener driverCreditsListener;
    private ArrayList<DriverCreditResponse.CreditHistory> list ;
    private static final String ARGS_DRIVER_HISTORY_LIST = "driverHistoryList";

    public static CreditsHistoryFragment newInstance(ArrayList<DriverCreditResponse.CreditHistory> list) {

        Bundle args = new Bundle();
        Gson gson = new Gson();
        CreditsHistoryFragment fragment = new CreditsHistoryFragment();
        args.putString(ARGS_DRIVER_HISTORY_LIST,
                gson.toJsonTree(list, new TypeToken<List<DriverCreditResponse.CreditHistory>>() {}.getType())
                        .getAsJsonArray().toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null && getArguments().containsKey(ARGS_DRIVER_HISTORY_LIST)){
            Gson gson = new Gson();
            list = gson.fromJson(getArguments().getString(ARGS_DRIVER_HISTORY_LIST),
                    new TypeToken<List<DriverCreditResponse.CreditHistory>>() {}.getType());
        }
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
        DriverCreditsHistoryAdapter driverCreditsHistoryAdapter = new DriverCreditsHistoryAdapter(getActivity(),list,recyclerView);
        recyclerView.setAdapter(driverCreditsHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;



    }
}
