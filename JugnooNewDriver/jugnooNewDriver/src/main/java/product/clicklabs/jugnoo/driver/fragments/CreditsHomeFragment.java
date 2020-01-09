package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.DriverCreditsAdapter;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;

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
        tabs.setTextSize(28);
        tabs.setIndicatorColor(getResources().getColor(R.color.themeColor));
        tabs.setTextColorResource(R.color.themeColor, R.color.textColor);
        tabs.setTypeface(Fonts.mavenRegular(getActivity()), Typeface.NORMAL);
        tabs.setViewPager(viewPager);


        return rootView;
    }

    @Override
    public String getTitle() {
        return getString(R.string.title_credits_activity);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + 0);
            if (page != null) {
                ((CurrentDetailsFragment) page).update();
            }
        }
    }
}
