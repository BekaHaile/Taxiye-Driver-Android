package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
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

        if(Data.userData != null) {
            ((TextView) rootView.findViewById(R.id.tv_credits_value)).setText(
                    Utils.formatCurrencyValue(Data.userData.getCurrency(), Data.userData.creditsEarned == null ? 0.0 : Data.userData.creditsEarned));
            ((TextView) rootView.findViewById(R.id.tv_comission_value)).setText(
                    Utils.formatCurrencyValue(Data.userData.getCurrency(), Data.userData.commissionSaved == null ? 0.0 : Data.userData.commissionSaved));
            rootView.findViewById(R.id.tv_share_credits).setVisibility(Data.userData.getSendCreditsEnabled() == 0 ? View.GONE : View.VISIBLE);
        }
        return rootView;



    }

    public void update(){
        if(Data.userData != null) {
            ((TextView) rootView.findViewById(R.id.tv_credits_value)).setText(
                    Utils.formatCurrencyValue(Data.userData.getCurrency(), Data.userData.creditsEarned == null ? 0.0 : Data.userData.creditsEarned));
        }
    }
}
