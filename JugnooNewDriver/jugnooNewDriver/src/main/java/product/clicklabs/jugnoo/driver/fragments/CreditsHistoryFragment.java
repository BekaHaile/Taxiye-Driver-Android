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

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DriverCreditsHistoryAdapter;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class CreditsHistoryFragment extends Fragment {




    View rootView;

    private DriverCreditsListener driverCreditsListener;
    private ArrayList<DriverCreditResponse.CreditHistory> list ;
    private static final String ARGS_DRIVER_HISTORY_LIST = "driverHistoryList";

    public  static CreditsHistoryFragment newInstance( ArrayList<DriverCreditResponse.CreditHistory> list) {

        Bundle args = new Bundle();

        CreditsHistoryFragment fragment = new CreditsHistoryFragment();
        // TODO: 26/05/18 GSON
//        args.putParcelable(ARGS_DRIVER_HISTORY_LIST,list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null && getArguments().containsKey(ARGS_DRIVER_HISTORY_LIST)){
            list = getArguments().getParcelable(ARGS_DRIVER_HISTORY_LIST);
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
