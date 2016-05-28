package product.clicklabs.jugnoo.driver.home;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;

/**
 * Created by aneeshbansal on 28/05/16.
 */
public class CustomerSwitcher {

	private HomeActivity activity;

	private RecyclerView recyclerViewCustomersLinked;

	private CustomerInfoAdapter customerInfoAdapter;

	public CustomerSwitcher(HomeActivity activity, View rootView){
		this.activity = activity;
		init(rootView);
	}

	public void init(View rootView){
		recyclerViewCustomersLinked = (RecyclerView) rootView.findViewById(R.id.recyclerViewCustomersLinked);
		recyclerViewCustomersLinked.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewCustomersLinked.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCustomersLinked.setHasFixedSize(false);


		customerInfoAdapter = new CustomerInfoAdapter(activity, new CustomerInfoAdapter.Callback() {
			@Override
			public void onClick(int position, CustomerInfo customerInfo) {
				Data.setCurrentEngagementId(String.valueOf(customerInfo.getEngagementId()));
				activity.switchDriverScreen(HomeActivity.driverScreenMode);
			}

		});


		recyclerViewCustomersLinked.setAdapter(customerInfoAdapter);

	}


	public void updateList(){
		customerInfoAdapter.notifyList();
	}
}
