package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class EarnCreditsFragment extends BaseFragment {


    View rootView;
    private DriverCreditsListener driverCreditsListener;

    public static EarnCreditsFragment newInstance() {

        Bundle args = new Bundle();

        EarnCreditsFragment fragment = new EarnCreditsFragment();
        fragment.setArguments(args);
        return fragment;
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
        rootView = inflater.inflate(R.layout.fragment_earn_credits, container, false);
        Utils.setTypeface(getActivity(),rootView.findViewById(R.id.tv_refer_driver),rootView.findViewById(R.id.tv_refer_customer)
        ,rootView.findViewById(R.id.tv_advertise_us), rootView.findViewById(R.id.tv_get_credits));
        rootView.findViewById(R.id.tv_refer_driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cringe here
                if(driverCreditsListener!=null)driverCreditsListener.openDriverEarnScreen();

            }
        });rootView.findViewById(R.id.tv_refer_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverCreditsListener!=null)driverCreditsListener.openCustomerEarnScreen();
            }
        });rootView.findViewById(R.id.tv_advertise_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverCreditsListener!=null)driverCreditsListener.openAdvertiseScreen();
            }
        });
        rootView.findViewById(R.id.tv_get_credits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverCreditsListener!=null)driverCreditsListener.openGetCreditsInfoScreen();
            }
        });
        return rootView;



    }

    @Override
    public String getTitle() {
        return getString(R.string.title_earn_more_credits);
    }
}
