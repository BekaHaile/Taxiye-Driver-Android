package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DriverCreditsAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class CreditsHomeFragment extends BaseFragment {

    View rootView;
    private ViewPager viewPager;
    private DriverCreditsAdapter driverCreditsAdapter;
    private PagerSlidingTabStrip tabs;
    private DriverCreditsListener driverCreditsListener;

    public static CreditsHomeFragment newInstance() {

        Bundle args = new Bundle();

        CreditsHomeFragment fragment = new CreditsHomeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_credits_home, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        driverCreditsAdapter = new DriverCreditsAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(driverCreditsAdapter);

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setIndicatorColor(getResources().getColor(R.color.new_orange));
        tabs.setTextColorResource(R.color.new_orange, R.color.menu_black);
        tabs.setTypeface(Fonts.mavenRegular(getActivity()), Typeface.NORMAL);
        tabs.setViewPager(viewPager);


        return rootView;
    }

    @Override
    public String getTitle() {
        return getString(R.string.title_credits_activity);
    }

}
