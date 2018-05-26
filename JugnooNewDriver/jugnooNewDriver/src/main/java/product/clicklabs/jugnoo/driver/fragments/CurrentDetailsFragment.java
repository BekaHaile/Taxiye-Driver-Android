package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class CurrentDetailsFragment extends Fragment {




    View rootView;

    private DriverCreditsListener driverCreditsListener;

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
        rootView = inflater.inflate(R.layout.fragment_current_details, container, false);
        rootView.findViewById(R.id.tv_earn_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverCreditsListener.openEarnCreditsScreen();

            }
        });
        rootView.findViewById(R.id.tv_share_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverCreditsListener.openShareCreditsScreen();

            }
        });
        Utils.setTypeface(getActivity(), rootView.findViewById(R.id.label_credits),
                rootView.findViewById(R.id.tv_credits_value),rootView.findViewById(R.id.label_comission),
                rootView.findViewById(R.id.tv_comission_value),rootView.findViewById(R.id.tv_earn_credits),
                rootView.findViewById(R.id.tv_share_credits));

        return rootView;



    }
}
