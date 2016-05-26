package product.clicklabs.jugnoo.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.AttachedCustomersAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Utils;


public class AttachedCustomersFragment extends Fragment implements FlurryEventNames, Constants {

	private final String TAG = AttachedCustomersFragment.class.getSimpleName();

	private LinearLayout linearLayoutRoot;
	private RecyclerView recyclerViewAttachedCustomers;

	private AttachedCustomersAdapter attachedCustomersAdapter;


	private View rootView;
    private HomeActivity activity;

	public AttachedCustomersFragment(){
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_attached_customers, container, false);

        activity = (HomeActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		recyclerViewAttachedCustomers = (RecyclerView) rootView.findViewById(R.id.recyclerViewAttachedCustomers);
		recyclerViewAttachedCustomers.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewAttachedCustomers.setItemAnimator(new DefaultItemAnimator());
		recyclerViewAttachedCustomers.setHasFixedSize(true);

		attachedCustomersAdapter = new AttachedCustomersAdapter(activity,
				new AttachedCustomersAdapter.Callback() {
					@Override
					public void onClick(int position, CustomerInfo customerInfo) {
						try {
							Data.dEngagementId = String.valueOf(customerInfo.getEngagementId());
							activity.switchDriverScreen(HomeActivity.driverScreenMode);
							activity.hideAttachedCustomersFragment();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCallUs(int position, CustomerInfo customerInfo) {
						try {
							Utils.openCallIntent(activity, customerInfo.getPhoneNumber());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		recyclerViewAttachedCustomers.setAdapter(attachedCustomersAdapter);


		return rootView;
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

}
